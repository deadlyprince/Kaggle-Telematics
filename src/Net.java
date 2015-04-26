
import java.util.Vector;

public class Net 
{
	static double[] weights;
	static double learningRate = 0.01;
	static int max = 0;
	static Vector<Sample> tsamples = new Vector<Sample>();
	static Vector<Sample> fsamples = new Vector<Sample>();
	
	public static void main(String[] a)
	{
		readSampleFile("samples");
		weights = new double[SampleSet.NFeatures + 1];
		
		while (true)
		{
			run();
		}
	}
	
	static void run()
	{
		learningRate = 0.01;
		
		for(int i = 0; i < SampleSet.NFeatures + 1; ++i) 
			weights[i] = (Math.random() - 0.5) * 0.01;
		
		int n = 0;
		while (true)
		{
			if (n++ > 10 * 1000 * 1000) break;
			
			int i = Utils.r(tsamples.size());
			train(tsamples.elementAt(i), n);
			
			i = Utils.r(fsamples.size());
			train(fsamples.elementAt(i), n);
			
			learningRate *= 0.9999995;
		}
		
		Utils.msg("------------------------\n" + printw());
		
		int k = 0;
		
		for (n = 0; n < 1000000; ++n)
		{
			int i = Utils.r(tsamples.size());
			if (correct(tsamples.elementAt(i))) k++;
		}
		
		int ts = ((100 * k) / 1000000);
		
		k = 0;
		
		for (n = 0; n < 1000000; ++n)
		{
			int i = Utils.r(fsamples.size());
			if (correct(fsamples.elementAt(i))) k++;
		}
		
		int fs = ((100 * k) / 1000000);
		
		Utils.msg(learningRate);
		
		int ms = (ts + fs) / 2;
		
		Utils.msg("success rate = " + ms);
		
		if (ms > max)
		{
			max = ms;
			
			Main.SaveFile = Main.HomeDir + "save" + ms + "_" + Utils.r(1000000) + ".txt";
			if (Utils.save != null) Utils.save.close();
			Utils.save = null;
			
			Utils.msg(Main.SaveFile);
			Utils.save("driver_trip,prob");
			
			Element[] e = new Element[tsamples.size()];
			
			double[][] x = new double[tsamples.size()][];
			double[] y = new double[tsamples.size()];
			
			for (int i = 0; i < tsamples.size(); ++i)
			{
				Sample s = tsamples.elementAt(i);
				calculateScore(s);
				e[i] = new Element(s.driver, s.trip, score);
				
				x[i] = s.stats;
				y[i] = s.valid ? 1 : -1;
			}
			
			Utils.regression(x, y);
			
			
			
			Element.sortByR(e);
			
			for (int i = 0; i < e.length; ++i)
			{
				double r = ((double) i) / (e.length + 1);
				Utils.save(e[i].driver + "_" + e[i].trip + "," + r);
			}
		}
	}

	static void readSampleFile(String name)
	{
		Vector<String[]> v = Utils.getData(Main.HomeDir + name + ".txt");
		
		for (int i = 0; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int d = Utils.toInt(s[0]);
			int t = Utils.toInt(s[1]);
			
			boolean b = new Boolean(s[2]).booleanValue();
			
			Sample sample = new Sample(d, t, b);
			
			for (int j = 0; j < SampleSet.NFeatures; ++j)
			{
				//Utils.msg(d + " " + t + " " + x);
				double x = new Double(s[3 + j]).doubleValue();
				if (j == 4) x /= 1000;
				sample.updateValue(x, j);
			}
			
			if (b)
				tsamples.add(sample);
			else
				fsamples.add(sample);
		}
		
		for (int j = 0; j < SampleSet.NFeatures; ++j)
		{
			center(j);
		}
		
		Utils.msg(tsamples.size() + " true samples read");
	}
	
	static void center(int k)
	{
		float[] x = new float[tsamples.size() + fsamples.size()];
		int c = 0;
		for (int i = 0; i < tsamples.size(); ++i) x[c++] = (float) tsamples.elementAt(i).stats[k];
		for (int i = 0; i < fsamples.size(); ++i) x[c++] = (float) fsamples.elementAt(i).stats[k];
		double m = Utils.mean(x);
		double v = Utils.sd(x);
		for (int i = 0; i < tsamples.size(); ++i) tsamples.elementAt(i).stats[k] = (tsamples.elementAt(i).stats[k] - m) / v;
		for (int i = 0; i < fsamples.size(); ++i) fsamples.elementAt(i).stats[k] = (fsamples.elementAt(i).stats[k] - m) / v;
		c = 0;
		for (int i = 0; i < tsamples.size(); ++i) x[c++] = (float) tsamples.elementAt(i).stats[k];
		for (int i = 0; i < fsamples.size(); ++i) x[c++] = (float) fsamples.elementAt(i).stats[k];
		m = Utils.mean(x);
		v = Utils.sd(x);
		Utils.msg(m + " " + v);
	}
	
	static double score = 0;
	
	static void calculateScore(Sample sample)
	{
		score = 0;
		for (int i = 0; i <= SampleSet.NFeatures; ++i)
		{
			if (i < SampleSet.NFeatures)
				score += sample.stats[i] * weights[i];
			else
				score += weights[SampleSet.NFeatures];
		}
	}
	
	static boolean correct(Sample sample)
	{
		calculateScore(sample);
		
		double lscore = score > 0 ? 1 : -1;
		double target = (sample.valid ? 1 :-1);
		
		return lscore * target > 0;
	}
	
	static void train(Sample sample, int c)
	{
		calculateScore(sample);
		
		double lscore = score > 0 ? 1 : -1;
		double target = sample.valid ? 1 :-1;
		double error = lscore - target;

		if (Math.abs(error) > 0)
			for (int i = 0; i <= SampleSet.NFeatures; ++i)
			{
				weights[i] -= error * (i < SampleSet.NFeatures ? (sample.stats[i]) : 1) * learningRate;
				//weights[i] -= error * (i < SampleSet.NFeatures ? sample.stats[i] : 1) * learningRate;
			}
		
		
		//if (c % 1000 == 0) 
		//Utils.msg(c + " " + score + " " + target + " --- " + printw() + "   " + learningRate);
	}
	
	static double lg(double x)
	{
		return 1 - (2 / (1 + Math.exp(x)));
	}
	
	static String printw()
	{
		String s = "";
		for(int i = 0; i < SampleSet.NFeatures + 1; ++i) s += weights[i] + ", ";
		return s;
	}
}
