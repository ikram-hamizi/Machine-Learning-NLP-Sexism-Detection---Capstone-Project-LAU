package jclasses;

import java.util.ArrayList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterDatasetHandler
{
	private static String CONSUMER_KEY = "MwuA3IpFkhyGDS8b0Oo5JGAJO",
			CONSUMER_KEY_SECRET = "XWzR7RysOHwvoKbXYerSSYz88Cicnd1PWdIPlhpKPqKLLmcwy5",
			TWITTER_TOKEN = "1012127827964153856-Jngi1UcI0ZzbuWKkx5Ghq2pSbIkkPA",
			TWITTER_TOKEN_SECRET = "C0Ck4Tep8CpW3ktLjt7xcCQr6U5nXAsvcyhcLCpwYA2Eo";

	//Grant Access to Tweets: Twitter Developer Account Configuration
	public static Twitter configurationBuilderOAuthInit()
	{
		
		System.out.println("Start OAuth");
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

		configurationBuilder.setDebugEnabled(true)
		.setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_KEY_SECRET)
		.setOAuthAccessToken(TWITTER_TOKEN)
		.setOAuthAccessTokenSecret(TWITTER_TOKEN_SECRET);	

		Configuration configuration = configurationBuilder.build();
		TwitterFactory factory = new TwitterFactory(configuration);

		final Twitter twitter = factory.getInstance();
		AccessToken accessToken = new AccessToken(TWITTER_TOKEN, TWITTER_TOKEN_SECRET);
		twitter.setOAuthAccessToken(accessToken);
		System.out.println("Finish OAuth");
		return twitter;
	}

	public static void createTweetDatasetHateSpeech()
	{
		//1. Configuration Builder and Twitter OAuth initializing
		Twitter twitter = configurationBuilderOAuthInit();
		
		java.util.List <String> hateSpeechIDs = FileHandler.tsvFileReaderTweetID("NAACL_SRW_2016.tsv");
		java.util.List <String> none = new ArrayList <String>();
  		java.util.List <String> sexism = new ArrayList <String>();
		java.util.List <String> racism = new ArrayList <String>();
		
		System.out.println("finish reading tweet ids");

		int i = 0;
		for(String line: hateSpeechIDs)
		{
			i++;

			String[] lineArr = line.split(",");
			String tweetID = lineArr[0];
			String classType = lineArr[1];
			
			final long startTime = System.nanoTime();

			String m = returnTweetID(tweetID, twitter);
			if(i == 1 || i == 2 || i == 10|| i == 3307 || i == 100 || i == 500 || i == 800 || i%1000 == 0)
				System.out.println("i = " + i + " | sizes: n = " + none.size() + " , s = " + sexism.size() + " , r = " + racism.size() + " | NULL count = " + count);
			if(m != "")
			{
				if(classType.equalsIgnoreCase("racism"))
					racism.add(m);
				else if(classType.equalsIgnoreCase("none"))
					none.add(m);
				//else if(classType.equalsIgnoreCase("sexism"))
					//sexism.add(m);
			}	

			final long duration = java.lang.System.nanoTime() - startTime;
		    if ((5500 - duration / 1000000) > 0) {
		        //System.out.println("Sleep for " + (6000 - duration / 1000000) + " miliseconds");
		        try {
					Thread.sleep((5500 - duration / 1000000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("FINAL NULL COUNT = " + count);
		
		//4. Store Tweets in TSV File
		FileHandler.tsvFileWriterTweets("none1NAACL.tsv", none);
		//FileHandler.tsvFileWriterTweets("sexismNAACL.tsv", sexism);
		//FileHandler.tsvFileWriterTweets("racismNAACL.tsv", racism);
	}
	public static void createTweetDataset()
	{	
		//1. Configuration Builder and Twitter OAuth initializing
		Twitter twitter = configurationBuilderOAuthInit();

		//2. Read Tweet IDs from tsv file --> Store in Array
		java.util.List <String> hostileIDs = FileHandler.tsvFileReaderTweetID("hostile_sexist_ID.tsv");
		java.util.List <String> benevolentIDs = FileHandler.tsvFileReaderTweetID("benevolent_sexist_ID.tsv");

		java.util.List <String> hostileTweets = new ArrayList <String>();
		java.util.List <String> benevolentTweets = new ArrayList <String>();

		System.out.println("finish reading tweet ids");

		//3. Store Tweets in Array

		int i = 0;
		for(String tweetID: hostileIDs)
		{
			i++;

			//final long startTime = System.nanoTime();

			String m = returnTweetID(tweetID, twitter);
			if(i == 1 || i == 3307 || i == 100 || i == 500 || i == 800 || i%1000 == 0)
				System.out.println("i = " + i + " | Current size = " + hostileTweets.size() + " | NULL count = " + count);
			if(m != "")
			{
				hostileTweets.add(m);
			}	

			/*final long duration = java.lang.System.nanoTime() - startTime;
		    if ((5500 - duration / 1000000) > 0) {
		        //System.out.println("Sleep for " + (6000 - duration / 1000000) + " miliseconds");
		        try {
					Thread.sleep((5500 - duration / 1000000));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		}

		for(String tweetID: benevolentIDs)
		{
			String m = returnTweetID(tweetID, twitter);
			if(m != "")
			{
				benevolentTweets.add(m);
			}
		}

			System.out.println("FINAL NULL COUNT = " + count);
			
			//4. Store Tweets in TSV File
			FileHandler.tsvFileWriterTweets("hostile_sexistCOPY.tsv", hostileTweets);
			FileHandler.tsvFileWriterTweets("benevolent_sexist.tsv", benevolentTweets);
		}
	
		public static int count = 0;
		public static String returnTweetID(String tweetID, Twitter twitter)
		{
			
			String tweet = "";
			try
			{
				Status status = twitter.showStatus(Long.parseLong(tweetID));
				if (status == null)
				{
					//
					System.out.println("null");
				}
				else
				{
					//1- Find tweet by ID
					//"@" + status.getUser().getScreenName()
					tweet = "" + status.getText() + "\n";

					//System.out.println("-" + tweet);	
				}
			}
			catch (TwitterException e)
			{
				//System.err.println(e);
				count++;
				//System.out.println("ID: " + tweetID);
				return "";
			}
			return tweet;
		}
	}