import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MapReduceApp {
    
    public static class EsportsMapper extends Mapper<LongWritable, Text, Text, Text> {
    private final Text outKey = new Text();
    private final Text outVal = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return;

        String[] fields = line.split(",", -1);
        if (fields.length < 8 || fields[0].equalsIgnoreCase("event_id")) return;

        String playerId = fields[1].trim();
        String heroesRaw = fields[3].trim().replace("\"", "");
        String killsStr = fields[4].trim();
        String deathsStr = fields[5].trim();
        String matchResult = fields[7].trim();

        if (playerId.isEmpty() || heroesRaw.isEmpty() || matchResult.isEmpty()) return;

        outVal.set(matchResult + "\t" + killsStr + "\t" + deathsStr);

        for (String hero : heroesRaw.split(";")) {
            String heroName = hero.trim();
            if (!heroName.isEmpty()) {
                outKey.set(playerId + "\t" + heroName);
                context.write(outKey, outVal);
            }
        }
    }
}

public static class EsportsCombiner extends Reducer<Text, Text, Text, Text> {
    private final Text outVal = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        long w = 0, d = 0, l = 0, dw = 0;

        for (Text val : values) {
            String[] p = val.toString().split("\t");
            if (p.length == 3) {
                try {
                    String res = p[0];
                    int kills = Integer.parseInt(p[1]);
                    int deaths = Integer.parseInt(p[2]);

                    if (res.equalsIgnoreCase("Win")) {
                        w++;
                        if (kills > deaths) dw++;
                    } else if (res.equalsIgnoreCase("Draw")) {
                        d++;
                    } else if (res.equalsIgnoreCase("Lose")) {
                        l++;
                    }
                } catch (Exception e) {}
            } else if (p.length == 4) {
                try {
                    w += Long.parseLong(p[0]);
                    d += Long.parseLong(p[1]);
                    l += Long.parseLong(p[2]);
                    dw += Long.parseLong(p[3]);
                } catch (Exception e) {}
            }
        }
        outVal.set(w + "\t" + d + "\t" + l + "\t" + dw);
        context.write(key, outVal);
    }
}

public static class EsportsReducer extends Reducer<Text, Text, Text, Text> {
    private final Text outVal = new Text();

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        long w = 0, d = 0, l = 0, dw = 0;

        for (Text val : values) {
            String[] p = val.toString().split("\t");
            if (p.length == 3) {
                try {
                    String res = p[0];
                    int kills = Integer.parseInt(p[1]);
                    int deaths = Integer.parseInt(p[2]);

                    if (res.equalsIgnoreCase("Win")) {
                        w++;
                        if (kills > deaths) dw++;
                    } else if (res.equalsIgnoreCase("Draw")) {
                        d++;
                    } else if (res.equalsIgnoreCase("Lose")) {
                        l++;
                    }
                } catch (Exception e) {}
            } else if (p.length == 4) {
                try {
                    w += Long.parseLong(p[0]);
                    d += Long.parseLong(p[1]);
                    l += Long.parseLong(p[2]);
                    dw += Long.parseLong(p[3]);
                } catch (Exception e) {}
            }
        }
        outVal.set(w + "\t" + d + "\t" + l + "\t" + dw);
        context.write(key, outVal);
    }
}

public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "EsportsHeroes_MapReduce");

    job.setJarByClass(MapReduceApp.class);
    job.setMapperClass(EsportsMapper.class);
    job.setCombinerClass(EsportsCombiner.class);
    job.setReducerClass(EsportsReducer.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(Text.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    job.setOutputFormatClass(TextOutputFormat.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
}

}