
import java.io.File;
import java.util.Vector;

import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.regression.*;

import weka.classifiers.*;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.*;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.*;

// https://dl.dropboxusercontent.com/u/32021591/Telematics.zip

public class Main
{
	static float MPH = 2.23694f;
	static float NullNumber = -99999f;
	static int ND = 4000;
	static int D1 = 201;
	static String HomeLinux = "/home/ec2-user/Telematics/";
	static String HomeMac = "/Users/matthewkuenzel/Desktop/Telematics/";
	static String HomeDir;
	static String DataDir = "drivers/";	
	static String OutputFile = "output.txt";
	static String SaveFile = "save.txt";
	static boolean DisplayOnly;
	static int nprocessors = Runtime.getRuntime().availableProcessors();
	static float Spacing = 6;
	static Vector<String> drivers;
	//static SampleSet sampleSet;
	static int SampleSize = 1094400;
	
	public static void main(String[] a)
	{
		if (true)
		{
			Utils.msg("start main() with " + nprocessors);
			neighbors();
			return;
		}
		
		//DisplayOnly = false;
		
		//Utils.regression();
		
		//MergeFeatures.merge();
		
		//DataManipulation.readMatch(2);
		//DataManipulation.readMatch(1);
		
		
		//Utils.save("accelerationstat");
		//astats();
		//decel();
		//len();
		
		//lenmax();
		
		//DataManipulation.readBaseResults();
		//DataManipulation.merge();
		
		
		//oneDriver();
		
		//Submissions.update();
		
		/*
		drivers = new Vector<String>();
		drivers.add("3018");
		drivers.add("3019");
		drivers.add("3020");
		drivers.add("3021");
		drivers.add("3017");
		*/
		
		//makeMaxAve();
		
		Utils.msg("complete");
	}
	
	static
	{
		String cd = System.getProperty("user.dir");
		
		if (cd.toLowerCase().indexOf("matthewkuenzel") > 0)
			HomeDir = HomeMac;
		else
			HomeDir = HomeLinux;
		
		DataDir = HomeDir + DataDir;
		OutputFile = HomeDir + OutputFile;
		SaveFile = HomeDir + SaveFile;
		
		makeDriversList(99999);
	}
	
	
	/*
	//if (false)
	//{
		//Display q = new Display(null);
		//q.d1 = new Driver(0, Utils.toInt(randomDriver()));
		//q.d2 = new Driver(0, Utils.toInt(randomDriver()));
		//q.d3 = new Driver(0, Utils.toInt(randomDriver()));
		//q.repaint();
		//return;
	//}
	
	if (false)
	{
		new DataManipulation().readBaseResults();
		Stats.test2();
		return;
	}
	
	if (false)
	{
		new DataManipulation().combine();
		return;
	}
	
	if (false)
	{
		//sub7 = new DataManipulation().readSubmission("submission7.txt");
		//sub7.special1();
		return;
	}

	//readLastResults();
	*/
	
	static void oneDriver()
	{
		// set singleDriver
		drivers = new Vector<String>();
		drivers.add
		("3017");
		//("1278");
		//("1279");
		//("2257");
		//("1635");
		// many nulls
		//("1477");
		Driver[] threads = new Driver[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new Driver(i);
		Driver dr = (Driver) allThreads(threads);
		// start up display
		// new Display1(dr);
	}
	
	static void allDrivers()
	{
		Driver[] threads = new Driver[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new Driver(i);
		allThreads(threads);
	}
	
	//---------------
	
	static void makeMaxAve()
	{
		DataManipulation.init();
		DataManipulation[] threads = new DataManipulation[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new DataManipulation();
		allThreads(threads);
	}
	
	static void decel()
	{
		DeccelStat[] threads = new DeccelStat[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new DeccelStat();
		allThreads(threads);
	}
	
	static void len()
	{
		LenStat[] threads = new LenStat[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new LenStat();
		allThreads(threads);
	}
	
	static void lenmax()
	{
		LenMaxSpeedStat[] threads = new LenMaxSpeedStat[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new LenMaxSpeedStat();
		allThreads(threads);
	}
	
	static void astats()
	{
		AccelerationStats[] threads = new AccelerationStats[nprocessors];
		for (int i = 0; i < nprocessors; ++i)
			threads[i] = new AccelerationStats();
		allThreads(threads);
	}
	
	//---------------
	
	static Object lock = new Object();
	static int nextDriver = 0;
	static int nextSample = 0;
	
	static String getNextDriver()
	{
		synchronized (lock)
		{
			if (nextDriver < drivers.size())
			{
				String r = drivers.elementAt(nextDriver);
				nextDriver++;
				return r;
			}
			else
				return null;
		}
	}
	
	static int getNextSamplex()
	{
		synchronized (lock)
		{
			if (nextSample < SampleSize)
			{
				int k = nextSample;
				nextSample += 100;
				return k;
			}
			else
				return -1;
		}
	}
	
	static void neighbors()
	{
		Neighbor.sampleSet = new SampleSet();
		Neighbor.sampleSet.normalizeAll();
		
		
		
		
		Neighbor[] threads = new Neighbor[nprocessors];
		
		int b = SampleSize / nprocessors;
		
		for (int i = 0; i < nprocessors; ++i)
		{
			int last = (i + 1) * b - 1;
			if (last > SampleSize - 1) last = SampleSize - 1;
			threads[i] = new Neighbor(i,i * b, last);
		}
		
		allThreads(threads);
		
		Element[] e = new Element[SampleSize / 2];
		int k = 0;
		
		// merge
		
		SampleSet sampleSet = Neighbor.sampleSet; // new SampleSet();
		
		for (int i = 0; i < nprocessors; ++i)
		{
			Neighbor n = threads[i];
			for (int j = 0; j < n.sampleSet.samples.size(); ++j)
			{
				if (n.sampleSet.samples.elementAt(j).neighbors != null)
					sampleSet.samples.elementAt(j).neighbors = n.sampleSet.samples.elementAt(j).neighbors;
			}
		}
		
		for (int i = 0; i < sampleSet.samples.size(); ++i)
		{
			Sample s = sampleSet.samples.elementAt(i);
			
			for (int j = 0; j < s.neighbors.size(); ++j)
			{
				Pair p = s.neighbors.elementAt(j);
				Sample t = p.sample;
				Utils.save(	s.driver + "," + s.trip + "," + s.valid + "," +
							t.driver + "," + t.trip + "," + t.valid + "," + 
							p.distance);
			}
		
			if (s.valid) e[k++] = new Element(s.driver, s.trip, score(s));
		}
		
		Element.sortByR(e);
		
		Utils.msg("driver_trip,prob");
		
		for (int i = 0; i < SampleSize / 2; ++i)
		{
			Element x = e[i];
			Utils.msg(x.driver + "_" + x.trip + "," + x.r);
		}
	}
	
	static double score(Sample s)
	{
		double totr = 0;
		double tota = 0;
		
		for (int j = 0; j < s.neighbors.size(); ++j)
		{
			Pair p = s.neighbors.elementAt(j);
			Sample t = p.sample;
			
			double r = p.distance;
			if (r < 0.00001) r = 0.00001;
			
			if (t.valid) 
				totr += 1 / r;
			else
				totr += -1 / r;
			
			tota += 1 / r;
		}
		
		return totr / tota;
	}
	
	static Thread allThreads(Thread[] threads)
	{
		for (int i = 0; i < nprocessors; ++i)
			threads[i].start();
		
		while(true)
		{
			Utils.pause(30000);
			boolean running = false;
			int c = 0;
			for (int i = 0; i < nprocessors; ++i)
				if (threads[i] != null)
					if (threads[i].isAlive())
					{
						c++;
						running = true;
					}
			Utils.msg(c + " threads running --------------------- ");
			if (!running)
				break;
		}
		
		return threads[0];
	}
	
	static void makeDriversList(int limit)
	{
		drivers = new Vector<String>();
		String[] list = new File(DataDir).list();
		
		for (int i = 0; i < list.length && i < limit; ++i)
			if (new File(DataDir + list[i]).isDirectory())
			{
				drivers.add(list[i]);
				//Utils.msg("------- " + list[i] + " " + drivers.size());
			}
	}
	
	//
	// ***** this doesnt work when there are few (no) drivers left to process
	//

	static String randomDriver()
	{
		return drivers.elementAt((int) (drivers.size() * Math.random()));
	}
	
	static int randomDriverI()
	{
		return Utils.toInt(drivers.elementAt((int) (drivers.size() * Math.random())));
	}	
}

//static boolean oneDriver()
//{
	/*
	drivers.add
	//("1634");
	//("1278");
	//("1279");
	("303");
	//("1635");
	// many nulls
	//("1477");
	
	Driver t = new Driver(0);
	
	t.start();
	while(true)
	{
		Utils.pause(1000);
		if (!t.isAlive())
			break;
	}
	
	if (true)
	{
		new Display(t);
	}
	*/
	/*
	else
	{
		int c2000 = 0;
		int c3000 = 0;
		int c0 = 0;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			if (t.bestScore(t.trips[i]) > 2000) c2000++;
			if (t.bestScore(t.trips[i]) > 3000) c3000++;
			if (t.bestScore(t.trips[i]) < 1) c0++;
			
		}
		Utils.msg("over 2000 = " + c2000 + " over 3000 = " + c3000 + " zero = " + c0);
		Utils.msg("******************** complete");
		//for (int i = 0; i < results.size(); ++i)
		//	Utils.msg(results.elementAt(i));
		
		//Result.printResults();
	}
	*/
	//return true;
//}



