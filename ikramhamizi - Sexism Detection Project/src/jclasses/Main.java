/*
 * @author Ikram Hamizi (ikram.hamizi@lau.edu - ikramhamizi@gmail.com)
 * */
package jclasses;

import weka.classifiers.functions.SMO;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.IOException;
import nlpClasses.NLPWekaClassification;

public class Main {
	
	// 1- Commented section in main - DONE :D
	// 3- Split train and key and gold : DONE
	//-----------------------------------------------------------
	// 2- NaiveBayesMultinomial (not accepting negative numbers)
	// 7- Check if filename is appropriate (arff, txt, tsv)
	
	// 4- Poster
	// 5- PPT
	// 6- Paper
	
	private static String DecisionList_FileName = "DECISION_LIST.txt",

			TRAIN_class1_FileName = "TRAIN_sexism.tsv",
			TRAIN_class2_FileName = "TRAIN_noneNAACL.tsv",

			TEST_class1_FileName = "TEST_sexism.tsv",
			TEST_class2_FileName = "TEST_noneNAACL.tsv",

			class1_name = "sexism",
			class2_name = "none",

			TRAIN_WEKA_DATASET = "TRAIN_WEKA_DATASET.arff",
			TEST_WEKA_DATASET = "TEST_WEKA_DATASET.arff";

	public static void main(String[] args) 
	{
		try
		{
			//I. CREATING TRAINING DATA FILES (DecisionList and 
			//FileHandler.createTrainingDataFiles(DecisionList_FileName, TRAIN_WEKA_DATASET, TRAIN_class1_FileName, TRAIN_class2_FileName, class1_name, class2_name);

			//II. CREATING TESTING DATA FILES (DecisionList and Test Set for Weka)
			//FileHandler.createTestingDataFiles(DecisionList_FileName, TEST_WEKA_DATASET, TEST_class1_FileName, TEST_class2_FileName, class1_name, class2_name);

			//III. RUNNING WEKA
			DataSource train_source = new DataSource(TRAIN_WEKA_DATASET);
			Instances train_dataset = train_source.getDataSet();
			train_dataset.setClass(train_dataset.attribute("class-attr"));
			System.out.println("\"-WEKA (TRAIN) DATASET-\"\n" + train_dataset.toSummaryString() + "----------------\nWEKA\n--------------------");
			
			DataSource test_source = new DataSource(TEST_WEKA_DATASET);
			Instances test_dataset = test_source.getDataSet();
			//dataset.setClassIndex(dataset.numAttributes() - 1); //
			//dataset.setClassIndex(0);
			test_dataset.setClass(test_dataset.attribute("class-attr"));

			System.out.println("\"-WEKA (TEST) DATASET-\"\n" + test_dataset.toSummaryString() + "----------------\nWEKA\n--------------------");

			ZeroR zeror = new ZeroR(); //BaseLine (Chance)
			System.out.println("\n--------------ZeroR:");
			NLPWekaClassification.runClassifier(train_dataset, test_dataset, zeror, 10, false);

			//NaiveBayesMultinomial nb = new NaiveBayesMultinomial(); //Naive Bayes
			//System.out.println("\n--------------MultinomialNaiveBayes:");
			//NLPWekaDataSet.runClassifier(dataset, nb, 10, true, false);

			J48 tree = new J48(); //TREE
			System.out.println("\n--------------J48:");
			NLPWekaClassification.runClassifier(train_dataset, test_dataset, tree, 10, true);

			SMO svm = new SMO(); //SVM
			System.out.println("\n--------------SVM:");
			NLPWekaClassification.runClassifier(train_dataset, test_dataset, svm, 10, true);
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}