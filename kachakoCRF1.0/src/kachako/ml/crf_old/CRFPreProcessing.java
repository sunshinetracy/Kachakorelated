package kachako.ml.crf_old;


import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;



public class CRFPreProcessing extends Configured implements Tool {

  static class SequenceFileMapper
    extends Mapper<NullWritable, BytesWritable, Text, Text> {
    
    public void map(NullWritable key, BytesWritable value, Context context)
      throws IOException, InterruptedException {

      Configuration conf = context.getConfiguration();
      
     // String filename = conf.get("map.input.file");
     
      String[] sb= Text.decode(value.getBytes()).split("\n\n");
      
     
      for (int ii = 0; ii<sb.length;ii++)
      {
    	/*  StringTokenizer st = new StringTokenizer(sb[ii], "\n");
          String line= "";
          
         while(st.hasMoreTokens())
          {
        	  line = line+ st.nextToken()+"\n";
        	 // line = sb.substring(0,21);
        	  
        	  //System.out.println("/////////////");
        	  
          } 
      */
       System.out.println(sb[ii]+"\n");
         
      
      context.write(new Text("\\\\\\\\\\\\\\\\"),  new Text(sb[ii]));
      
       
    }
  }
  }

  static class IdentityReducer extends Reducer<Text, BytesWritable, Text, BytesWritable> {
    // Use default reduce() == org.apache.hadoop.mapred.lib.IdentityReducer.reduce()
  }

  public int run(String[] args) throws Exception {
    Configuration conf = getConf();
    if (conf == null) {
      return -1;
    }
    

    String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

    @SuppressWarnings("deprecation")
	Job job = new Job(conf, "CRFPreProcessing");

    job.setJarByClass(CRFPreProcessing.class);

    job.setMapperClass(SequenceFileMapper.class);
    //job.setReducerClass(IdentityReducer.class);

    job.setInputFormatClass(WholeFileInputFormat.class);
    job.setOutputFormatClass(SequenceFileOutputFormat.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
    FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

    return job.waitForCompletion(true) ? 0 : 1;
  }

  public static void main(String[] args) throws Exception {
    int exitCode = ToolRunner.run(new CRFPreProcessing(), args);
    System.exit(exitCode);
  }
  }