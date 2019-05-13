package nlpClasses;
import java.util.ArrayList;

import uk.ac.wlv.sentistrength.*;

public class NLPSentiment {

	public static java.util.List<Integer> sentimentAnalysisTweetList(java.util.List<String> tweetsArr)
	{
		java.util.List<Integer> tweetsSenti = new ArrayList<Integer>();

		//Initialization	
		SentiStrength sentiStrength = new SentiStrength(); 

		//Java manual (SentiStrength)- Option: trinary (report positive-negative-neutral classification)
		String ssthInitialisation[] = {"sentidata", "C:/SentStrength_Data/", "trinary"};
		sentiStrength.initialise(ssthInitialisation); 

		//Classification
		for(int i = 0; i<tweetsArr.size(); i++)
		{
			String[] result_SentiStrengthTrinary = sentiStrength.computeSentimentScores(tweetsArr.get(i)).split(" ");
			String senti_score = result_SentiStrengthTrinary[result_SentiStrengthTrinary.length-1];
			
			try
			{
				tweetsSenti.add(Integer.parseInt(senti_score));
				//System.out.println("Tweet: " + (tweetsArr.get(i)) + "\n Sentiment: " + senti_score + "\n"); //DEBUG
			}
			catch (NumberFormatException e)
			{
				System.out.println("SENTI SCORE (LIST) NumberFormatException: " + senti_score );
			}
		}

		return tweetsSenti;
	}
	public static int sentimentAnalysisTweet(String tweet)
	{
		//Initialization	
		SentiStrength sentiStrength = new SentiStrength(); 

		//Java manual (SentiStrength)- Option: trinary (report positive-negative-neutral classification)
		String ssthInitialisation[] = {"sentidata", "C:/SentStrength_Data/", "trinary"};
		sentiStrength.initialise(ssthInitialisation); 

		//Classification

		String[] result_SentiStrengthTrinary = sentiStrength.computeSentimentScores(tweet).split(" ");
		String senti_score = result_SentiStrengthTrinary[result_SentiStrengthTrinary.length-1];
		
		int senti_score_int = 0;
		
		try
		{
			senti_score_int = Integer.parseInt(senti_score);
		}
		catch (NumberFormatException e)
		{
			System.out.println("SENTI SCORE (tweet) NumberFormatException: " + senti_score );
		}
		
		return senti_score_int;
	}
}
