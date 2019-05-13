package jclasses;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import nlpClasses.NLPComputations;
import nlpClasses.NLPTools;
import objectClasses.NgramTables;

public class Main2 {

	public static void main(String[] args)   
	{
		/*
		RandomAccessFile streamReadDL;
		java.util.List<String> benevolentTweets;
		java.util.List<String> hostileTweets;
		java.util.List<String> hateSpeechTweets;
		try
		{
			//I. TRAINING STEP

			// I.(1)- PRELIMENARY STAGE
		// 1. Store tweets from files to List: Tweets are cleaned from URLs, Tags (@user), and non-ANSI codes.
		//benevolentTweets = FileHandler.tsvToTweetArray("TRAIN_benevolent_sexist.tsv");
		//hostileTweets = FileHandler.tsvToTweetArray("TRAIN_hostile_sexist.tsv");
		//TwitterDatasetHandler.createTweetDatasetHateSpeech();
		if(false)
			hateSpeechTweets = FileHandler.tsvToTweetArray("hatespeech.tsv");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		 */
		//1- Set up pipeline properties (for: NLP TOKENIZATION + LEMMATIZATION)
		/*Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		NLPTools.tweetToProcessedFeatureVector("github has been great for that. love them.", props, true);
		*/
		String s = "y' girl be nun cause y' all be hoe round";
		s = s.replace('\'', ' ');
		System.out.println(s);
		
		Object m = "hello";
		String k = (String) m;
		System.out.println(m);
	}
}