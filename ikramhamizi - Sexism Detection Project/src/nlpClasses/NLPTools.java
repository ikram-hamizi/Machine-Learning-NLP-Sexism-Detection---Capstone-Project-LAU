package nlpClasses;

import objectClasses.MutableInt;
import objectClasses.NgramTables;
import objectClasses.WindowTweet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class NLPTools {

	//I- NGRAM DECISION LIST
	//I. 1- Tokenizes tweet and adds the important words to a HashTable
	public static Hashtable <String, Hashtable <String, MutableInt>> createUnigramHashTable(ArrayList<WindowTweet> tweets_OriginalAndProcessed_class1, ArrayList<WindowTweet> tweets_OriginalAndProcessed_class2, String class1_name, String class2_name)
	{
		Hashtable <String, Hashtable <String, MutableInt>> unigrams = new Hashtable<String, Hashtable <String, MutableInt>>();
		
		// - Add tokens to hashtable unigrams (from pre-processed data)
		int class1_count = tweets_OriginalAndProcessed_class1.size();
		int total_tweets_count = class1_count + tweets_OriginalAndProcessed_class2.size();
		
		// Combine the two classes in one unigram hashtable
		for(int i = 0; i < total_tweets_count; i++)
		{
			WindowTweet window_tweet;
			String classname;
		
			if(i < class1_count)
			{
				window_tweet = tweets_OriginalAndProcessed_class1.get(i);
				classname = class1_name;
			}
			else 
			{
				window_tweet = tweets_OriginalAndProcessed_class2.get(i-class1_count);
				classname = class2_name;
			}
			
			if (window_tweet == null || window_tweet.getProcessed_tweet_qw() == null)
				break;
			
			if(window_tweet.getProcessed_tweet_qw().isEmpty())
				break;
			
			List<String> qw = window_tweet.getProcessed_tweet_qw();
			
			for (String token : qw)
			{
				MutableInt count = null;

				// if token exists in hashtable ngrams, get its count in the specified class 
				if (unigrams.get(token) != null)
					count = unigrams.get(token).get(classname);

				// if token does not exist, add it as a Hashtable <String, MutableInt>
				if (count == null)
				{
					Hashtable <String, MutableInt> ht2 = new Hashtable <String, MutableInt>();
					ht2.put(classname, new MutableInt());

					unigrams.put(token, ht2);
					count = unigrams.get(token).get(classname);
				}
				else if (count != null)
					count.increment();							
			}
		}

		System.out.println("size ngrams: " + unigrams.size() + " size keyset (FEATURES): " +unigrams.size()); //DEBUG
		return unigrams;
	}

	//%hash1; #n-gram  table - %hash2; #n-1gram table
	public static NgramTables ngramHashTable(int n, List<String> tweetList)
	{
		NgramTables ngramtables = new NgramTables();

		Hashtable <String, Hashtable <String, MutableInt>> hash1 = new Hashtable<String, Hashtable <String, MutableInt>>();
		Hashtable <String, MutableInt> hash2 = new Hashtable <String, MutableInt>();

		ArrayList<WindowTweet> tweets_OriginalAndProcessed = createProcessedFeatureVectorsList(tweetList, true);

		for(int k = 0; k < tweets_OriginalAndProcessed.size(); k++)
		{
			WindowTweet wt = tweets_OriginalAndProcessed.get(k); //Tweet and its Feauture vector

			//System.out.println("size: " + tweets_OriginalAndProcessed.size() + " | k: " + k ); //DEBUG

			if (wt == null || !wt.isInitialized())
				continue;

			Queue<String> array =  new LinkedList<String>(); 

			for (int i = 0; i < wt.getProcessed_tweet_qw().size(); i++)
			{
				String token = wt.getProcessed_tweet_qw().get(i); //Token[i] of the tweet (as Feature vector)
				String h = ""; //DEBUG

				if(array.size() < n - 1) 
				{	
					array.add(token);
				}

				if(array.size() == n - 1)
				{
					String history = String.join(" ", array);

					//NGRAM TABLE: Hash1
					MutableInt count1 = null;
					if (hash1.get(token) != null)
					{
						count1 = hash1.get(token).get(history);  //$hash1{$history}{$word}++;
					}

					if (count1 == null)
					{
						Hashtable <String, MutableInt> ht2 = new Hashtable <String, MutableInt>();
						ht2.put(history, new MutableInt());
						hash1.put(token, ht2);
						count1 = hash1.get(token).get(history);
					}

					else if (count1 != null)
					{
						count1.increment();							
					}

					//N-1GRAM TABLE: Hash2 - //$hash2{$history}++;
					MutableInt count2 = null;
					if (hash2.get(history) != null)
					{
						count2 = hash2.get(history);  
					}

					if (count2 == null)
					{
						hash2.put(history, new MutableInt());
						count2 = hash2.get(history);
					}

					else if (count2 != null)
					{
						count2.increment();							
					}
					h = history;
					array.add(token); //#Adds word to the rear of the array (add to queue)
					array.poll(); //Remove first word from history in array (pop from queue)
					System.out.println("History: " + h + " | word: " + token);
				}
			}
		}
		ngramtables.setNgram(hash1);
		ngramtables.setN_1gram(hash2);
		return ngramtables;
	}

	//HELPER FUNCTIONS (1):
	//I. 1-1. Tokenizer: returns tweet as a list of CoreLabels
	public static ArrayList<String> tweetToProcessedFeatureVector(String tweet, Properties props, boolean removeNonAnsi) //Espasia 
	{
		//System.out.println(tweet); //DEBUG
		ArrayList<String> qw = new ArrayList<String>();

		// text as CoreDocument (Stanford library)
		CoreDocument coredoc_tweet = new CoreDocument(tweet);

		// set up pipeline
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		// annotate CoreDocument (tweet)
		pipeline.annotate(coredoc_tweet);

		// access tokens - a token = CoreLabel
		List<CoreLabel> tweetTokens = new ArrayList<CoreLabel>();

		if(coredoc_tweet.sentences() == null)
		{
			//
		}
		else
		{
			//System.out.println("SIZE: " + coredoc_tweet.sentences().size()); //DEBUG

			int i = 0;
			if(coredoc_tweet.sentences().size() > 0)
			{
				while (i < coredoc_tweet.sentences().size() && coredoc_tweet.sentences().get(i) != null)
				{
					List<CoreLabel> sentenceTokens = coredoc_tweet.sentences().get(i).tokens();

					for(CoreLabel token: sentenceTokens)
						tweetTokens.add(token); 

					//System.out.println("\nsentence " + i + ": "+ coredoc_tweet.sentences().get(i));
					//System.out.println("tweet: " + tweet);
					//System.out.println("qw: " + String.join(" ", qw)); //DEBUG

					i++;
				}
				qw = dataPreProcessing(tweetTokens);
			}
		}
		return qw;
	}

	// 1- Lemmatize token - IF: 1) is not NER | *2) is not punctuation | *3) is verb, noun, adjective, or adverb */

	public static ArrayList<String> dataPreProcessing(List<CoreLabel> tweetTokens)
	{
		ArrayList<String> processedTweet = new ArrayList<String>();

		for(CoreLabel token: tweetTokens)
		{
			String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

			//ADD if (Noun, verb, adjective or adverb) - POS List "PENN Tree-bank"

			String posRegex = "(?)\\b(NN[A-Z]?|JJ[A-Z]?|RB[A-Z]?|VB[A-Z]?)\\b"; //"(?)" = CASE_INSENSITIVE

			//System.out.println("token: " + token); //DEBUG
			//System.out.println("pos: " + pos + " ner:" + token.ner() + " "); //DEBUG

			if((pos != null && Pattern.matches(posRegex, pos)) && (token.ner() == null || token.ner().equals("O"))) 
			{
				processedTweet.add(token.lemma());
			}
		}
		return processedTweet;
	}

	//Func: creates list of tweets represented as feature vectors (NLP-Processed)
	public static ArrayList<WindowTweet> createProcessedFeatureVectorsList(List<String> tweetList, boolean removeNonAnsi)
	{
		ArrayList<WindowTweet> tweets_OriginalAndProcessed = new ArrayList<WindowTweet>();

		//1- Set up pipeline properties (for: NLP TOKENIZATION + LEMMATIZATION)
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");

		//List<ArrayList<String>> tweets_to_feature_vectors = new ArrayList <ArrayList<String>>();

		//2- Tokenize each tweet from dataset
		for(String tweet: tweetList)
		{
			ArrayList<String> qw = NLPTools.tweetToProcessedFeatureVector(tweet, props, true);
			//tweets_to_feature_vectors.add((ArrayList<String>) tweetStringTokens);

			if(!qw.isEmpty()) 
			{
				WindowTweet wt = new WindowTweet (tweet, qw);
				tweets_OriginalAndProcessed.add(wt);
			}
		}
		System.out.println("Class - Number of Processed Elements: " + tweets_OriginalAndProcessed.size()); //DEBUG
		return tweets_OriginalAndProcessed;
	}

	//HELPER FUNCTIONS
	//1. Remove tags @user and RT
	public static String removeTagsAndRT(String str)
	{
		str = str.replaceAll("(^RT |^rt |#\\w+|@[A-Za-z0-9_]*:?)", "");
		return str;
	}
	
	//2. Remove tags and non-ANSI and Emojis
	public static String removeNonANSIandEmojis(String str)
	{
		str = str.replaceAll("[\\[\\](){}]\"'","");

		String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);

		Matcher matcher = pattern.matcher(str);
		String result = matcher.replaceAll("");
		return result;
	}

	//3. Remove URLS from tweet
	public static String removeUrl(String str)
	{
		String urlRegex = "((((https?|ftp|gopher|telnet|file|Unsure):((//)|(\\\\)))|(www\\.))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern p = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);

		int i = 0;
		while (m.find())
		{
			str = str.replaceAll(m.group(i).replaceAll("\\?", "").replaceAll("\\&", ""),"").trim(); //& and \: not recognizable by replaceAll()
			i++;
		}
		return str;
	}
}

/*
 	// Silence writes to System.err printstream 
	store reference
	PrintStream err = System.err;

	// make all writes to System.err printstream silent 
	System.setErr(new PrintStream(new OutputStream() {
		public void write(int b) {
		}
	}));

	//code
	System.setErr(err);  
 */