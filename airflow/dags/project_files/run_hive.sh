#!/bin/bash
INPUT_DIR3=$1
INPUT_DIR4=$2
OUTPUT_DIR6=$3

hadoop fs -rm -r -f "$OUTPUT_DIR6"
beeline -u jdbc:hive2://localhost:10000 -f "$(dirname "$0")/hive.hql" \
  --hiveconf input_dir3="$INPUT_DIR3" \
  --hiveconf input_dir4="$INPUT_DIR4" \
  --hiveconf output_dir6="$OUTPUT_DIR6"
