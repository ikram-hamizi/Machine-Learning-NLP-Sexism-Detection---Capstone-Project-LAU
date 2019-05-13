package jclasses;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import nlpClasses.NLPComputations;
import nlpClasses.NLPTools;
import nlpClasses.NLPWekaClassification;
import objectClasses.DecisionListObject;
import objectClasses.MutableInt;
import objectClasses.WindowTweet;

public class FileHandler
{	
	//I. TSV reader/writer for: Tweet IDs
	/***** 1- Write *****/
	public static void tsvFileWriterTweets(String filename, List<String> output_list)
	{	
		System.out.println("SIZE OF OUTPUTLIST (WRITER TSV) IS: " + output_list.size());
		try
		{
			Files.write(Paths.get(filename), output_list, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/***** 2- Return (List) tweetIDs - Read Tweet IDs From File.tsv and store them in a List *****/
	public static List<String> tsvFileReaderTweetID(String filename)
	{
		//Dataset of Tweet ID is a list of numbers separated by '\n' and saved (.tsv).
		List<String[]> tweetIDTable = new ArrayList<String[]>(); 
		List<String> tweetIDList = new ArrayList<String>(); 

		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		TsvParser parser = new TsvParser(settings);
		tweetIDTable = parser.parseAll(new File(filename));

		System.out.println(String.format("%s: size of arr: %d",filename, tweetIDTable.size()));

		for(int i = 0; i<tweetIDTable.size(); i++)
			tweetIDList.add(tweetIDTable.get(i)[0]);

		return tweetIDList;
	}

	//II. TSV file reader/writer for: Tweets
	/***** 1- Return (List) tweets - Read Tweet From File.tsv and store them in a List *****/
	public static List<String> tsvToTweetArray(String filename) throws IOException
	{
		List<String> tweetList = new ArrayList<String>(); 

		RandomAccessFile stream = new RandomAccessFile(new File(filename), "r");
		String tweet;
		while((tweet = stream.readLine()) != null)
		{
			String tweet_utf8 = new String(tweet.getBytes("ISO-8859-1"), "UTF-8");
			String cleaned_tweet = NLPTools.removeNonANSIandEmojis(NLPTools.removeUrl(NLPTools.removeTagsAndRT(tweet_utf8)));
			//System.out.println(cleaned_tweet);
			tweetList.add(cleaned_tweet);
		}

		stream.close();
		System.out.println(String.format("%s: size of arr: %d",filename, tweetList.size())); //DEBUG

		return tweetList;
	}

	//III. CREATE TRAINING FILES
	//1- Print DecisionList from array to File DECISION_LIST
	public static void decisionListGeneratorArrtoFile(Hashtable <String, Hashtable <String, MutableInt>> ngrams, String DecisionList_FileName) throws IOException
	{
		ArrayList <DecisionListObject> DECISION_LIST_ARR = NLPComputations.unigramLogLiklihoodEstimator(ngrams);
		//System.out.println("size: " + DECISION_LIST_ARR.size()); //DEBUG

		//if(DecisionList_FileName != ) //CHECK IF IT ENDS WITH .txt
		//		System.exit(0);

		RandomAccessFile stream = new RandomAccessFile(DecisionList_FileName, "rw");
		FileChannel channel = stream.getChannel();

		for(DecisionListObject DLobj : DECISION_LIST_ARR)
		{
			writeToRandomAccessFile(DLobj.toString(), stream, channel, false);
		}
		stream.close();
		channel.close();
	}
	
	//V. WEKA DATASET
	public static void writeToRandomAccessFile(String str, RandomAccessFile stream, FileChannel channel, boolean append) throws IOException
	{
		//System.out.println("write to WEKA_DATASET.arff"); //DEBUG
		if(append)
			stream.seek(stream.length());

		byte[] stringBytes = str.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(stringBytes.length);

		buffer.put(stringBytes);
		buffer.flip();

		channel.write(buffer);
	}

	public static void writeRowToWekaDataSet(String arff_row, RandomAccessFile streamWriteARFF, FileChannel channelWriteARFF) throws IOException
	{
		writeToRandomAccessFile(arff_row, streamWriteARFF, channelWriteARFF, true);
	}

	public static void createTrainingDataFiles(String DecisionList_FileName, String WEKA_ARFF_FileName, String class1_FileName, String class2_FileName, String class1_name, String class2_name) throws IOException
	{
		/* I.(1)- PRELIMENARY STAGE*/
		// 1. Store tweets from files to List: Tweets are cleaned from URLs, Tags (@user), and non-ANSI codes.
		List<String> class1_raw = FileHandler.tsvToTweetArray(class1_FileName);
		List<String> class2_raw = FileHandler.tsvToTweetArray(class2_FileName);		

		// 1- Data Pre-Processing of tweets and creation of a List of Feature Vectors
		ArrayList<WindowTweet> tweets_OriginalAndProcessed_class1 = NLPTools.createProcessedFeatureVectorsList(class1_raw, true);
		ArrayList<WindowTweet> tweets_OriginalAndProcessed_class2 = NLPTools.createProcessedFeatureVectorsList(class2_raw, true);

		System.out.println("TRAIN: DONE (1) reading file (2) saving to list (3) data pre-processing - start UNIGRAMS"); //DEBUG

		// 2.  Creation of DECISION_LIST 
		// 1 -  Create (Hashtables): occurrences of (ngram, n=1) in a hash{word} 
		Hashtable <String, Hashtable <String, MutableInt>> unigrams = NLPTools.createUnigramHashTable(tweets_OriginalAndProcessed_class1, tweets_OriginalAndProcessed_class2, class1_name, class2_name);	
		System.out.println("--- DONE UNIGRAMs - start creating DecisionList ---");

		//	2 -  Create DECISION_LIST file
		decisionListGeneratorArrtoFile(unigrams, DecisionList_FileName);
		System.out.println("---DONE DECISION LIST---");

		/* I.(2)- SECOND STAGE: creating data-set for WEKA: WEKA_DATASET*/
		NLPWekaClassification.WekaCreateDataset(tweets_OriginalAndProcessed_class1, tweets_OriginalAndProcessed_class2, class1_name, class2_name, DecisionList_FileName, WEKA_ARFF_FileName);

		System.out.println("---DONE DATASET---");
	}

	public static void createTestingDataFiles(String DecisionList_FileName, String WEKA_ARFF_FileName, String class1_FileName, String class2_FileName, String class1_name, String class2_name) throws IOException
	{
		List<String> class1_raw = FileHandler.tsvToTweetArray(class1_FileName);
		List<String> class2_raw = FileHandler.tsvToTweetArray(class2_FileName);		

		// 1- Data Pre-Processing of tweets and creation of a List of Feature Vectors
		ArrayList<WindowTweet> tweets_OriginalAndProcessed_class1 = NLPTools.createProcessedFeatureVectorsList(class1_raw, true);
		ArrayList<WindowTweet> tweets_OriginalAndProcessed_class2 = NLPTools.createProcessedFeatureVectorsList(class2_raw, true);

		System.out.println(" TEST: DONE (1)reading file (2) saving to list (3) data pre-processing - start UNIGRAMS"); //DEBUG

		/* I.(2)- SECOND STAGE: creating data-set for WEKA: WEKA_DATASET*/
		NLPWekaClassification.WekaCreateDataset(tweets_OriginalAndProcessed_class1, tweets_OriginalAndProcessed_class2, class1_name, class2_name, DecisionList_FileName, WEKA_ARFF_FileName);

		System.out.println("---DONE DATASET---");

	}
}