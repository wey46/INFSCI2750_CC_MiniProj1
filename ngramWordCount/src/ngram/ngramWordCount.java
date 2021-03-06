package ngram;
import java.io.IOException;
//import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ngramWordCount {
	
	public static class ngMap extends
		Mapper<Object ,Text, Text, IntWritable> {
		
		private final static IntWritable one = new IntWritable(1);
		private int gram_len;
		private Text gram = new Text();
		
		protected void setup(Context context) throws IOException, InterruptedException {
			gram_len = context.getConfiguration().getInt("grams", 1); // get grams from config, default = 1 (single char)
		}
		
		public void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
					
			int i = 0;
			String line = value.toString();
			line = line.replaceAll("[\\W]", "");
			if(line.length() >= gram_len){
				while(i+gram_len <= line.length()){
					gram.set(line.substring(i, i+gram_len));
					context.write(gram, one);
					i++;
				}
			}	
		}
	}
	
	public static class ngReduce extends
		Reducer<Text, IntWritable, Text, IntWritable>{
		
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
			
			int sum = 0;
			for(IntWritable x : values){
				sum += x.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("grams", args[2]); // dynamicly pass variable to MapReduce
		
		Job job = Job.getInstance(conf, "ngramWordCount");		
		job.setJarByClass(ngramWordCount.class);
		job.setMapperClass(ngMap.class);
		job.setReducerClass(ngReduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setMapOutputKeyClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true)? 0 : 1);
	}
}
