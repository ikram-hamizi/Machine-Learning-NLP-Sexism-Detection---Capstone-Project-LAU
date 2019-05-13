package nlpClasses;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jclasses.FileHandler;
import objectClasses.WindowTweet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.stemmers.LovinsStemmer;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class NLPWekaClassification {

	public static double WekaAttribute1_DecisionListLogSumScore(String tweet, List<String> tweet_qw, RandomAccessFile stream) throws IOException
	{
		return NLPComputations.tweetLogScoreEstimator(tweet_qw, stream, "sexism", "none");
	}

	public static int WekaAttribute2_SentimentAnalysisScore(String tweet, List<String> tweet_qw, RandomAccessFile stream) throws IOException
	{
		return NLPSentiment.sentimentAnalysisTweet(tweet);
	}

	private static String cleanStringAttribute(String str)
	{
		return str.replace('\'', ' '); //Remove ( ' ) from tweets to avoid problems with the string attribue of weka
	}

	public static void WekaCreateDataset(ArrayList<WindowTweet> list_class1, ArrayList<WindowTweet> list_class2, String class1_name, String class2_name, String DecisionList_FileName, String WEKA_DATASET_FileName) throws IOException
	{
		RandomAccessFile streamReadDL = new RandomAccessFile(DecisionList_FileName, "rw");

		RandomAccessFile streamWriteARFF = new RandomAccessFile(WEKA_DATASET_FileName, "rw");
		FileChannel channelWriteARFF = streamWriteARFF.getChannel();

		for(int i=0; i<list_class1.size(); i++)
		{
			//Write row to ARFF file (Weka Dataset)
			WekaCreateRowInARFF(list_class1.get(i).getOriginal_tweet(), list_class1.get(i).getProcessed_tweet_qw(), class1_name, streamReadDL, streamWriteARFF, channelWriteARFF);

			if(i == 100 || i == 300 || i == 20 || i == 500)
				System.out.println(class1_name);
		}
		System.out.println("FINISH " + class1_name);

		for(int i=0; i<list_class2.size(); i++)
		{
			if(i == 0 || i == 1 || i == 3 || i == 300 || i == 100 || i == 600|| i == 2000 || i == list_class2.size()-300)
				System.out.println("STAAAAAAAAAAAAART " + class2_name);
			//Write row to ARFF file (Weka Dataset)
			WekaCreateRowInARFF(list_class2.get(i).getOriginal_tweet(), list_class2.get(i).getProcessed_tweet_qw(), class2_name, streamReadDL, streamWriteARFF, channelWriteARFF);
		}
		System.out.println("FINISHEU " + class2_name);

		streamReadDL.close();

		channelWriteARFF.close();
		streamWriteARFF.close();
	}

	public static void WekaCreateRowInARFF(String tweet, List<String> tweet_qw, String tweet_class, RandomAccessFile streamReadDL, RandomAccessFile streamWriteARFF, FileChannel channelWriteARFF) throws IOException
	{
		double attr1_logscore = WekaAttribute1_DecisionListLogSumScore(tweet, tweet_qw, streamReadDL);
		attr1_logscore = NLPComputations.rounding(attr1_logscore, 3);

		int attr2_tweet_Senti_Score = WekaAttribute2_SentimentAnalysisScore(tweet, tweet_qw, streamReadDL);

		String arff_row = "\n'" + cleanStringAttribute(String.join(" ", tweet_qw)) + "' , " + attr1_logscore + " , " + attr2_tweet_Senti_Score + " , " + tweet_class;
		//System.out.println("row: " + arff_row);
		FileHandler.writeRowToWekaDataSet(arff_row, streamWriteARFF, channelWriteARFF);
	}

	public static void runClassifier(Instances train_dataset, Instances test_dataset, Classifier classifier, int crossValidationSplit, boolean filteredClassifier) throws Exception
	{
		Evaluation e;
		if(filteredClassifier)
		{
			//TRAINING STEP
			//the filter
			StringToWordVector filter = new StringToWordVector();
			filter.setInputFormat(train_dataset);
			//filter.setIDFTransform(true);
			//filter.setUseStoplist(true);
			
			//LovinsStemmer stemmer = new LovinsStemmer();
			//filter.setStemmer(stemmer);
			filter.setLowerCaseTokens(true);
			filter.setOutputWordCounts(true);

			//Create the FilteredClassifier object
			FilteredClassifier fc = new FilteredClassifier();
			//specify filter
			fc.setFilter(filter);
			//specify base classifier
			fc.setClassifier(classifier);
			//Build the meta-classifier
			fc.buildClassifier(train_dataset);
			//System.out.println(classifier);

			
			//EVALUATIOn/TESTING STEP
			e = new Evaluation(train_dataset);
			e.evaluateModel(fc, test_dataset);
			//e.crossValidateModel(fc, dataset, crossValidationSplit, new Random(1));
		}

		else
		{
			//TRAINING STEP
			classifier.buildClassifier(train_dataset);

			e = new Evaluation(train_dataset);
			e.evaluateModel(classifier, test_dataset);
			//e.crossValidateModel(classifier, dataset, crossValidationSplit, new Random(1));
		}

		//Results
		System.out.println(e.toString());
		System.out.println(e.toClassDetailsString());
		System.out.println(e.areaUnderROC(1));
		System.out.println(e.toSummaryString("\nResults\n======\n", true));

		//Precision and recall
		System.out.println("fMeasure: " + e.fMeasure(1) + " | Precision: " + e.precision(1) + " | Recall: " + e.recall(1) + "\n");
		//System.out.println(nb.getCapabilities().toString());

		System.out.println(e.toMatrixString("\nConfusion Matrix\n=============="));

	}

	/*public static Instances[][] crossValidationDatasetSplit(Instances dataset, int numberOfFolds)
	{
		Instances[][] splits = new Instances[2][numberOfFolds];
		for(int i = 0; i < numberOfFolds; i++)
		{
			splits[0][1] = dataset.trainCV(numberOfFolds, 1);
			splits[1][i] = dataset.testCV(numberOfFolds, 1);
		}
		return splits;
	}*/

}
