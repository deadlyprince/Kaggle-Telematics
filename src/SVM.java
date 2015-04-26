import java.util.ArrayList;
import java.util.Vector;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.functions.*;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.RandomCommittee;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class SVM 
{
	SampleSet sampleSet;
	Vector<Sample> trueset;
	
	SVM()
	{
		sampleSet = new SampleSet();
		
		int n = 100000;
		int s = 0;
		
		Instances train = buildTrainingSet(sampleSet, s, n);
		//s += n;
		//Instances test = buildTrainingSet(set, s, n);
		
		Instances test = buildFullSet(sampleSet);
		
		//for (int k = 0; k <= 8; ++k)
			
		classify(train, test, 0);
	}

	Instances buildTrainingSet(SampleSet set, int start, int n)
	{
		FastVector fvWekaAttributes = defineFeatures();
		 
		// Create an empty training set
		Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
		 
		// Set class index
		isTrainingSet.setClassIndex(7);

		for (int i = start; i < start + n; ++i)
		{
			Sample sample = set.samples.elementAt(Utils.r(set.samples.size()));
			Instance iExample = createInstance(fvWekaAttributes, sample); //new DenseInstance(4);
			isTrainingSet.add(iExample);
		}
		 
		return isTrainingSet;
	}
	
	Instances buildFullSet(SampleSet set)
	{
		FastVector fvWekaAttributes = defineFeatures();
		 
		// Create an empty training set
		Instances iset = new Instances("Rel", fvWekaAttributes, 10);
		 
		// Set class index
		iset.setClassIndex(7);

		trueset = new Vector<Sample>();
		
		for (int i = 0; i < set.samples.size(); ++i)
		{
			Sample sample = set.samples.elementAt(i);
			if (sample.valid)
			{
				Instance in = createInstance(fvWekaAttributes, sample);
				iset.add(in);
				trueset.add(sample);
			}
		}
		 
		return iset;
	}
	
	FastVector defineFeatures()
	{
		Attribute[] ats = new Attribute[7];
		
		for (int i = 0; i < 7; ++i)
			ats[i] = new Attribute("" + i);
		
		 // Declare the class attribute along with its values
		 FastVector fvClassVal = new FastVector(2);
		 
		 fvClassVal.addElement("true");
		 fvClassVal.addElement("false");
		 
		 Attribute ClassAttribute = new Attribute("theClass", fvClassVal);
		 
		 // Declare the feature vector
		 FastVector fvWekaAttributes = new FastVector(8);
		
		 for (int i = 0; i < 7; ++i)
			 fvWekaAttributes.addElement(ats[i]);
		 
		 fvWekaAttributes.addElement(ClassAttribute);
		 
		 return fvWekaAttributes;
	}
	
	Instance createInstance(FastVector fvWekaAttributes, Sample sp)
	{
		 Instance i = new DenseInstance(8);
		 
		 for (int k = 0; k < 7; ++k)
			 i.setValue((Attribute)fvWekaAttributes.elementAt(k), sp.stats[k]);
		 i.setValue((Attribute)fvWekaAttributes.elementAt(7), sp.valid ? "true" : "false");
		 
		 return i;
	}
	
	void classify(Instances isTrainingSet, Instances isTestingSet, int m)
	{
		Classifier cModel = null;
		 
		if (m == 0) cModel = new SMO(); 						//85%
		//if (m == 1) cModel = new SMOreg(); 					//doesnt work - binary
		//if (m == 2) cModel = new LinearRegression() ; 		//doesnt work - binary
		if (m == 3) cModel = new MultilayerPerceptron();		//89%
		if (m == 4) cModel = new SGD();							//87%
		if (m == 5) cModel = new RandomForest() ;				//99%
		if (m == 6) cModel = new RandomCommittee();				//
		if (m == 7) cModel = new AdaBoostM1();					//
		if (m == 8) cModel = new NaiveBayes();					//
		 
		if (cModel == null) return;	
		
		try	
		{
			 cModel.buildClassifier(isTrainingSet);
			
			 // Test the model
			 
			 Evaluation eTest = new Evaluation(isTestingSet);
			 eTest.evaluateModel(cModel, isTestingSet);
			 
			 String strSummary = eTest.toSummaryString();
			 
			 Utils.msg("================ using model " + m);
			 Utils.msg(strSummary);
			 
			 // Get the confusion matrix
			 double[][] cmMatrix = eTest.confusionMatrix();
			 
			 for (int i = 0; i < cmMatrix.length; ++i)
				 for (int j = 0; j < cmMatrix[i].length; ++j)
				 {
					 Utils.msg("confusion matrix for " + i + " " + j + " " + cmMatrix[i][j]);
				 }
			 
			 Utils.msg("driver_trip,prob");
			 
			 ArrayList<Prediction> p = eTest.predictions();
			 for (int i = 0; i < p.size(); ++i)
			 {
				 Prediction t = p.get(i);
				 
				 double r = 0.5;
				 boolean x = false;
				 if (t.predicted() == 0.0)
				 {
					 r = 0.5 + Math.random() / 3;
					 x = true;
				 }
				 else if (t.predicted() == 1.0)
				 {
					 r = Math.random() / 3; 
					 x = true;
				 }
				 
				 if (!x) Utils.msg("weight " + t.weight());
				 
				 //Utils.msg(i + " " + t.actual() + " " + t.predicted() + " " + t.weight() + " " + isTestingSet.instance(i));
				 Utils.msg(trueset.elementAt(i).driver + "_" + trueset.elementAt(i).trip + "," + r);
			 }
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	void tttxx()
	{
		FastVector fvWekaAttributes = defineFeatures();
		 
		// Create an empty training set
		 Instances isTrainingSet = new Instances("Rel", fvWekaAttributes, 10);
		 // Set class index
		 isTrainingSet.setClassIndex(3);
		 
		 
		// Create the instance
		 //Instance iExample = createInstance(fvWekaAttributes); //new DenseInstance(4);
		 //iExample.setValue((Attribute)fvWekaAttributes.elementAt(0), 1.0);
		 //iExample.setValue((Attribute)fvWekaAttributes.elementAt(1), 0.5);
		 //iExample.setValue((Attribute)fvWekaAttributes.elementAt(2), "gray");
		 //iExample.setValue((Attribute)fvWekaAttributes.elementAt(3), "positive");
		 
		 // add the instance
		 
		 
		 //for (int i = 0; i < 10; ++i)
		 //{
		 //Instance iExample = createInstance(fvWekaAttributes); //new DenseInstance(4);
		 //isTrainingSet.add(iExample);
		 //}
	}
}
