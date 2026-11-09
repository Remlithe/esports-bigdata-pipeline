USE default;

DROP TABLE IF EXISTS esports_mr_results;
DROP TABLE IF EXISTS players_raw;
DROP TABLE IF EXISTS final_output_json;

CREATE EXTERNAL TABLE esports_mr_results (
    player_id STRING,
    hero_name STRING,
    wins BIGINT,
    draws BIGINT,
    losses BIGINT,
    dominant_wins BIGINT
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t'
STORED AS TEXTFILE
LOCATION '${hiveconf:input_dir3}';

CREATE EXTERNAL TABLE players_raw (
    player_id STRING,
    player_name STRING,
    country STRING,
    team STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION '${hiveconf:input_dir4}'
TBLPROPERTIES ("skip.header.line.count"="1");

CREATE EXTERNAL TABLE final_output_json (
    json_line STRING
)
STORED AS TEXTFILE
LOCATION '${hiveconf:output_dir6}';

INSERT OVERWRITE TABLE final_output_json
SELECT CONCAT(
    '{"player_name":"', p.player_name,
    '","team":"', p.team,
    '","hero_name":"', r.hero_name,
    '","wins":', r.wins,
    ',"draws":', r.draws,
    ',"losses":', r.losses,
    ',"dominant_wins":', r.dominant_wins, '}'
)
FROM esports_mr_results r
JOIN players_raw p ON r.player_id = p.player_id;

DROP TABLE IF EXISTS esports_mr_results;
DROP TABLE IF EXISTS players_raw;
DROP TABLE IF EXISTS final_output_json;
