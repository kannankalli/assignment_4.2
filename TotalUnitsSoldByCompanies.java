package com.bigdata.acadgild;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class TotalUnitsSoldByCompanies {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Configuration con = new Configuration();
		Job job = new Job(con);
		job.setJarByClass(TotalUnitsSoldByCompanies.class);
		
		job.setMapperClass(CompanyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setReducerClass(CompanyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

	}
	
	private static class CompanyMapper extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		private static final String NA = "NA"; 
		private static final IntWritable one = new IntWritable(1);
		
		public void map(LongWritable key, Text value, Context context ) throws IOException,InterruptedException
		{
			String[] values = value.toString().split("\\|");
			if ( !NA.equals(values[0]) &&  !NA.equals(values[1]))  {
				context.write(new Text(values[0]), one);
			}
		}
	}
	
	private static class CompanyReducer extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		private IntWritable total = new IntWritable(0);
		
		public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			Integer totalUnits = 0;
			for ( IntWritable value : values ) 
			{
				totalUnits+=value.get();
			}
			total.set(totalUnits);
			context.write(key,total);
		}
	}
	
	
}