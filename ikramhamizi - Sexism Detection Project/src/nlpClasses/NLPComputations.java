package nlpClasses;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import objectClasses.DecisionListObject;
import objectClasses.MutableInt;

public class NLPComputations {

	// I. unigram Log-likelihood estimator
	// P(w|class-1)/P(w|class-2) = freq(class-1, w)/freq(class-2, w) 
	public static ArrayList <DecisionListObject> unigramLogLiklihoodEstimator(Hashtable <String, Hashtable<String, MutableInt>> unigrams)
	{
		double logScore;	

		Set <String> FEATURES = unigrams.keySet(); // <key, value> Keys = FEATURES (n-grams features) 
		ArrayList <DecisionListObject> DECISION_LIST_ARR = new ArrayList<DecisionListObject>(); //Result

		for (String f : FEATURES) // O(n^2) (for each feature (f) in FEATURES (n-grams))
		{
			if(f == null || f.length() == 0)
				break;
			
			// Set (binary): Class(a) or Class(b) - each feature in the HashTable has 2 possible Classes: a, or b, or Both
			Object[] binaryClassNames = unigrams.get(f).keySet().toArray();  

			// count of occurrences of f in Class (b) 
			int count_f_in_a = unigrams.get(f).get(binaryClassNames[0]).getInt();  	 
			
			// (1) if f belongs only to one Class (A or B)
			if(binaryClassNames.length == 1) 
			{
				logScore = Math.abs(Math.log((count_f_in_a + 1)/1)); // Laplace smoothing (+1): in case count(f) == 0
				
				//ADD f to DecisionList (Class a or b)
				DECISION_LIST_ARR.add(new DecisionListObject(f, (String)binaryClassNames[0], logScore)); 
			}

			// (2) if f belongs to the two Classes (a and b)
			else if(binaryClassNames.length == 2) 
			{
				// take only results of the second Class (b) - count_f_in_a (Class a) is already calculated
				// number of occurrences in Class 2 (b)
				int count_f_in_b = unigrams.get(f).get(binaryClassNames[1]).getInt();	

				//logScore = Math.log((count_f_in_a)/(count_f_in_b)); // without Laplace smoothing
				logScore = Math.log((count_f_in_a + 1)/(count_f_in_b + 1)); // Laplace smoothing (+1): avoid 0 in denominator
				
				if(logScore < 0) // absolute value (negative)
				{
					logScore = Math.abs(logScore);
					logScore = rounding(logScore, 2);
					
					//ADD f to DecisionList (2nd Class b)
					DECISION_LIST_ARR.add(new DecisionListObject(f, (String) binaryClassNames[1], logScore));
				}
				else if(logScore > 0) // positive
				{
					logScore = rounding(logScore, 2);
					
					//ADD f to DecisionList (1st Class a)
					DECISION_LIST_ARR.add(new DecisionListObject(f, (String) binaryClassNames[0], logScore));
				}
			}
		}
		
		// sort DecisionList in Descending order over log-score
		Collections.sort(DECISION_LIST_ARR, Collections.reverseOrder()); 

		return DECISION_LIST_ARR;
	}

	// II. The probability of a tweet belonging to class(1) or class(2)
	// P(class-i|tweet) = Mult(log-likelihood scores)
	public static double tweetLogScoreEstimator (List<String> tweet_qw, RandomAccessFile stream, String class1_name, String class2_name) throws IOException
	{		
		double log_score = 0.0f;
		
		if(tweet_qw == null) // empty tweet
			return 0;
		
		// test cases for each feature in the feature_vector qw_tweet
		// return log-score of each f from DECISION_LIST.txt file
		for(String f: tweet_qw)
		{
			//1- set the file pointer at 0 position
			stream.seek(0L);
			boolean found = false;

			String DL_line = "";
			
			// while feature f is found in Decision
			while(!found && (DL_line = stream.readLine()) != null)
			{
				String[] DL_line_split = DL_line.split(" ");

				//System.out.println("DecisionList_line (DL_line)): " + DL_line_split[0]); //DEBUG
				
				//Decision List Object: "(0) feature (1) class (2) log_score"
				if(DL_line_split[0].equalsIgnoreCase(f))
				{
					found = true;
					try
					{
						//System.out.println(" Found: (" + f.word() + ") | Score: " + DL_line_split[2]); //DEBUG
						if(DL_line_split[1].equalsIgnoreCase(class2_name))
							log_score -= Double.parseDouble(DL_line_split[2]);
						
						else if(DL_line_split[1].equalsIgnoreCase(class1_name))
							log_score += Double.parseDouble(DL_line_split[2]);
					}
					catch(NumberFormatException e)
					{
						System.out.println("Number format exception");
					}
				}
			}
		}
		return log_score;
	}

	//Method Source: stackoverflow (user: manikanta)
	public static double rounding(double num, int scale)
	{
		int pow = 10;
		for (int i = 1; i < scale; i++)
			pow *= 10;
		
		double tmp = num * pow;
		
		return ( (double) ( (int) ((tmp - (int) tmp) >= 0.5f ? tmp + 1 : tmp) ) ) / pow;
	}
}