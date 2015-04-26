import java.util.Vector;


public class SampleSet 
{
	static int NFeatures = 7;
	static int Bins = 200;
	
	Vector<Sample> samples;
	int validSamples = -1;
	
	double[][] binBoundValues = new double[NFeatures][Bins];
	double[][] binCenterValues = new double[NFeatures][Bins];
	double[][] binFrequencies = new double[NFeatures][Bins];
	
	double[] means = new double[NFeatures];
	double[] sds = new double[NFeatures];
	
	double[] weights = {
		// score = 0.89106
		//2.024589423015035,2.52417029708879,2.2984159322263626,3.355844969454716,0.8632655086070448,1.1393471427568447,0.031882701181384185		
		// score = 0.90250
		1.7265611896037214,2.948673197709175,1.0215516281567756,2.75553143740765,0.22143389874228278,1.7688940823734476,0.5269022585118314
	};
	
	double[] best;
	double cwt;

	SampleSet(Vector<Sample> v)
	{	
		samples = new Vector<Sample>();
		
		for (int i = 0; i < v.size(); ++i)
			samples.add(v.elementAt(i));
	}
	
	private SampleSet(int i)
	{
		
	}

	SampleSet()
	{
		readSampleFile("samples");
	
		
		
		//buildMeanSd();
		
		
		
		/*
		boolean Submit = false;
		
		if (Submit)
		{
			submission();
			return;
		}
		*/
	}
	
	void testing()
	{
		best = new double[NFeatures];
		double max = -1E100;
		cwt = 0.4;
		
		while (true)
		{
			if (cwt >= 1) break;
			
			double r = generateTest();
			
			if (r > max)
			{
				max = r;
				String s = "";
				s += ("-------------------------- " + r + "   ");
				best = weights;
				for (int j = 0; j < NFeatures; ++j) s += weights[j] + ",";
				Utils.msg(s);
			}
			else
			{
				Utils.msg("= " + r + " < " + max);
			}
		}
	}
	
	void buildMeanSd()
	{
		for (int n = 0; n < NFeatures; ++n)
		{
			float[] x = new float[samples.size()];
			
			for (int i = 0; i < samples.size(); ++i)
			{
				Sample s = samples.elementAt(i);
				x[i] = (float) s.stats[n];
			}
			
			means[n] = Utils.mean(x);
			sds[n] = Utils.sd(x);
		}
		
		for (int n = 0; n < NFeatures; ++n)
		{
			Utils.msg("feature=" + n + " mean=" + means[n] + " sd=" + sds[n]);
		}
	}

	double generateTest()
	{
		cwt *= 1.00001;
		weights = new double[NFeatures];
		for (int i = 0; i < NFeatures; ++i)
		{
			weights[i] = best[i] * cwt + ((Math.random() - 0.1) * 5) * (1 - cwt);
		}
		return evaluate1();
	}

	void submission()
	{
		Main.SaveFile = Main.HomeDir + "save" + "_" + Utils.r(1000000) + ".txt";
		if (Utils.save != null) Utils.save.close();
		Utils.save = null;
		
		Utils.msg(Main.SaveFile);
		Utils.save("driver_trip,prob");
		
		Element[] e = new Element[samples.size() / 2];
		
		int k = 0;
		for (int i = 0; i < samples.size(); ++i)
		{
			Sample s = samples.elementAt(i);
			if (s.valid)
				e[k++] = new Element(s.driver, s.trip, s.totalProbability());
		}
		
		Element.sortByR(e);
		
		for (int i = 0; i < e.length; ++i)
		{
			double r = ((double) i) / (e.length + 1);
			Utils.save(e[i].driver + "_" + e[i].trip + "," + r);
		}
	}
	
	double evaluate0()
	{
		Element[] e = new Element[samples.size()];
		
		int k = 0;
		for (int i = 0; i < samples.size(); ++i)
		{
			Sample s = samples.elementAt(i);
			e[k++] = new Element(s.driver, s.trip, s.totalProbability(), s.valid);
		}
		
		Element.sortByR(e);
		
		double total = 0;
		int c = 0;
		for (int i = e.length - 1; i >= 0; --i)
		{
			if (!e[i].valid) 
			{
				total += i;
				c++;
			}
		}
		
		Utils.msg(total + " " + c);
		
		return total / c;
	}
	
	double evaluate1()
	{
		int N = 20000;
		
		Element[] e = new Element[N];
		
		int k = 0;
		for (int i = 0; i < N; ++i)
		{
			Sample s = samples.elementAt(Utils.r(samples.size()));
			e[k++] = new Element(s.driver, s.trip, s.totalProbability(), s.valid);
		}
		
		int c = 0;
		k = 0;
		for (int i = 0; i < N; ++i)
			for (int j = i + 1; j < N; ++j)
			{
				if (e[i].valid != e[j].valid)
				{
					if (e[i].valid && (e[i].r > e[j].r)) k++;
					if (e[j].valid && (e[j].r > e[i].r)) k++;
					c++;
				}
			}
		
		return ((double) k) / c;
	}
	
	void buildBin(int n)
	{
		int k = 0;
		int t = 0;
		int f = 0;
		
		double l = 0;
		
		for (int i = 0; i < samples.size(); ++i)
		{
			Sample s = samples.elementAt(i);
			
			if (i == 0) l = s.stats[n];
			
			if (s.valid) t++; else f++;
			
			if (i > ((k + 1) * samples.size()) / Bins || i == samples.size() - 1)
			{
				binBoundValues[n][k] = s.stats[n];
				binCenterValues[n][k] = (binBoundValues[n][k] + l) / 2;
				l = binBoundValues[n][k];
				
				binFrequencies[n][k] = ((double) t) / (t + f);
				
				k++;
				t = f = 0;
			}
		}
		
		Utils.msg("Bin ---");
		for (int i = 0; i < Bins; ++i)
			Utils.msg(n + " " + i + " ::: " + binBoundValues[n][i] + " " + binCenterValues[n][i] + " ===== " + binFrequencies[n][i]);
	}

	void readSampleFile(String name)
	{
		samples = new Vector<Sample>();
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + name + ".txt");
		
		for (int i = 0; i < v.size(); ++i)
		{
			//if (i % 10000 == 0) Utils.msg("parse " + i);
			
			String[] s = v.elementAt(i);
			
			int d = Utils.toInt(s[0]);
			int t = Utils.toInt(s[1]);
			
			boolean b = new Boolean(s[2]).booleanValue();
			
			Sample sample = new Sample(d, t, b);
			
			for (int j = 0; j < NFeatures; ++j)
			{
				// use only 4 features
				
				//if (j == 0 || j == 4)
				//if (j < 4)
				{
				
				
				
				//Utils.msg(d + " " + t + " " + x);
				double x = new Double(s[3 + j]).doubleValue();
				sample.updateValue(x, j);
				
				
				
				
				
				}
			}
		
			samples.add(sample);
		}
		
		Utils.msg(samples.size() + " samples read");
	}
	
	void buildBins()
	{
		for (int i = 0; i < NFeatures; ++i)
		{
			sortBy(i);
			buildBin(i);
		}
	}
	
	double probAtValue(int n, double v)
	{
		// given a statistic n of value v
		// what is the probability of true sample 
		
		for (int i = 1; i < binBoundValues[n].length; ++i)
		{
			if (v <= binBoundValues[n][i])
				return binFrequencies[n][i];
		}
		
		// error
		
		Utils.msg("cannot find for " + n + " " + v);
		
		return 0;
	}
	
	int countValid()
	{
		if (validSamples < 0)
		{
			validSamples = 0;
			for (int i = 0; i < samples.size(); ++i) if (samples.elementAt(i).valid) validSamples++;
		}
		
		return validSamples;
	}
	
	void sortBy(int n)
	{
		Sample.sortBy = n;
		samples = Sorter.sort(samples);
	}
	
	Vector<Sample> subset(double r)
	{
		Vector<Sample> n = new Vector<Sample>();
		
		for (int i = 0; i < samples.size(); ++i)
		{
			if (Math.random() < r)
				n.add(samples.elementAt(i));
		}
		
		return n;
	}
	
	static void combine()
	{
		Utils.msg("starting SampleSet");
		
		SampleSet set = new SampleSet();
		set.buildBins();
		
		Vector<float[][]> r = new Vector<float[][]>();
		
		for (int i = 0; i < 6; ++i)
			r.add(getResultsForStat(set, i));
		
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
				results[i][j] = Main.NullNumber;
		
		float[] weight = { 0, 0, 0, 4, 5, 0 };
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j)
			{
				float[][] t = r.elementAt(0);
				if (t[i][j] == Main.NullNumber) continue;
				
				results[i][j] = 0;
				
				for (int k = 0; k < 6; ++k)
				{
					t = r.elementAt(k);
					results[i][j] += t[i][j] * weight[k];
				}
			}
		
		Submissions.driverCorrection(results);
	}
	
	static float[][] getResultsForStat(SampleSet set, int n)
	{
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
				results[i][j] = Main.NullNumber;
		
		for (int i = 0; i < set.samples.size(); ++i)
		{
			Sample sample = set.samples.elementAt(i);
			if (sample.valid)
			{
				//float r = (float) sample.stats[n]; // (float) set.probAtValue(n, sample.stats[n]);
				
				if (n == 4 || n == 5)
					results[sample.driver][sample.trip] = (float) sample.stats[n];
				else
					results[sample.driver][sample.trip] = (float) -sample.stats[n];
				
				//Utils.msg(sample.stats[4] + " --> " + r);
				//e[k++] = new Element(sample.driver, sample.trip, set.probAtValue(4, sample.stats[4]));
			}
		}
		
		Submissions.driverCorrectionOnly(results);
		
		return results;
	}
	
	void normalizeAll()
	{
		buildMeanSd();
		
		for (int i = 0; i < NFeatures; ++i)
		{
			sortBy(i);
			normalize(i);
		}
	}
	
	static int Big = 999111000;
	
	void normalize(int n)
	{
		int rLimit = Big;
		int lLimit = -Big;
		int M = 3;
		
		for (int i = 0; i < samples.size(); ++i)
		{
			Sample sample = samples.elementAt(i);
			
			if (sample.stats[n] < means[n] - M * sds[n])
				if (i > lLimit)
					lLimit = i;
			
			if (sample.stats[n] > means[n] + M * sds[n])
				if (i < rLimit)
					rLimit = i;
		}
		
		double min, max;
		
		if (lLimit == -Big)
			min = samples.elementAt(0).stats[n];
		else
			min = samples.elementAt(lLimit).stats[n];
		
		if (rLimit == Big)
			max = samples.elementAt(Main.SampleSize - 1).stats[n];
		else
			max = samples.elementAt(rLimit).stats[n];
		
		Utils.msg("min = " + min + ", max = " + max);
		Utils.msg("left limit = " + lLimit + ", right limit = " + rLimit);
		
		double del = max - min;
		
		for (int i = 0; i < samples.size(); ++i)
		{
			Sample sample = samples.elementAt(i);
			double r = (sample.stats[n] - min) / del;
			
			if (r < 0) r = 0;
			if (r > 1) r = 1;
			
			sample.stats[n] = r;
		}
	}
}
