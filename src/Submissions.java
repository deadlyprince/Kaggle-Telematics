import java.io.File;
import java.util.Vector;


public class Submissions 
{
	static void init()
	{
		//float[][] ordered = Submissions.readSubmission("ordered.txt", 100000);
	}
	
	static void update()
	{
		float[][] ordered = Submissions.readSubmission("ordered.txt", -1);
		
		float[][] t1 = readMaxAve("up1.txt", true, true);
		float[][] t2 = readMaxAve("up2.txt", true, true);
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
			{
				if (t1[i][j] != t2[i][j])
				{
					t1[i][j] = t2[i][j] = Main.NullNumber;
					//Utils.msg(i + " " +  j + " " + t1[i][j] + " " + t2[i][j]);
				}
			}
		
		float[][] i1 = readMaxAve("up1.txt", false, true);
		float[][] i2 = readMaxAve("up2.txt", false, true);
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
			{
				if (i1[i][j] == Main.NullNumber || i2[i][j] == Main.NullNumber)
					if (i1[i][j] != Main.NullNumber || i2[i][j] != Main.NullNumber)
					{
						//Utils.msg(i + " " +  j + " " + i1[i][j] + " " + i2[i][j]);
						i1[i][j] = i2[i][j] = Main.NullNumber;
					}
			}
		
		int c = 0;
		float max = -99999;
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
			{
				if (i1[i][j] != Main.NullNumber && t1[i][j] != Main.NullNumber)
				{
					float imp = Math.max(i1[i][j], i2[i][j]); //float imp = (i1[i][j] + i2[i][j]) / 2;
					
					if (imp > 1)
					{
						float r = t1[i][j] / imp;
						if (r > max)
							max = r;
					}
					
					c++;
				}
			}
		
		float t = 100000f / 547200f;
		
		//Utils.msg(c);
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
			{
				if (i1[i][j] != Main.NullNumber && t1[i][j] != Main.NullNumber)
				{	
					float imp = Math.max(i1[i][j], i2[i][j]); //(i1[i][j] + i2[i][j]) / 2;
					
					if (imp > 1)
					{
						float r = t1[i][j] / imp;
						ordered[i][j] = (r / max) * t;
					}
				}
			}
		
		rewriteSubmission(ordered);
		
		Utils.msg("completed");
	}
	
	static float[][] readSubmission(String name)
	{
		return readSubmission(name, -1);
	}
	
	static float[][] readSubmission(String name, int recordsToRead)
	{
		Vector<String[]> v = Utils.getData(Main.HomeDir + name);
		
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
				results[i][j] = Main.NullNumber;
		
		for (int i = 1; i < v.size(); ++i)
		{
			if (recordsToRead > 0)
				if (i > recordsToRead)
					break;
			
			String[] s = v.elementAt(i);
			
			int k = s[0].indexOf("_");
			int driver = Utils.toInt(s[0].substring(0, k));
			int trip = Utils.toInt(s[0].substring(k + 1, s[0].length()));
			
			results[driver][trip] = Utils.toFloat(s[1]);
			
			//Utils.msg(i + " " + driver + " " + trip);
		}
		
		return results;
	}
	
	static void rewriteSubmission(float[][] r)
	{		
		Utils.save("driver_trip,prob");
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j)
				if (r[i][j] != Main.NullNumber)
					Utils.save(i + "_" + j + "," + r[i][j]);
	}
	
	static float[][] readMaxAve(String name, boolean mem, boolean max)
	{
		if (!new File(Main.HomeDir + name).exists()) return null;
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + name);
		
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
				results[i][j] = Main.NullNumber;
		
		int c = 0;
		
		for (int i = 1; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int driver = Utils.toInt(s[0]);
			int trip = Utils.toInt(s[1]);
			boolean member = Boolean.parseBoolean(s[4]);
			
			if (mem == member)
			{
				if (max)
					results[driver][trip] = Utils.toFloat(s[2]);
				else
					results[driver][trip] = Utils.toFloat(s[3]);
				c++;
			}
		}
		
		Utils.msg("records=" + c);
			
		/*	
			float m = Utils.toFloat(s[2]);
			float a = Utils.toFloat(s[3]);
			
			if (a > 1)
				r[i] = m / a;
			else
				r[i] = 0;
			
			if (r[i] > max)
				max = r[i];

			//Utils.msg(driver + " " + trip + " " + r[i] + " " + max);
		}
		
		float t = 100000f / 547200f;
		
		for (int i = 1; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int driver = Utils.toInt(s[0]);
			int trip = Utils.toInt(s[1]);
			
			float z = t * r[i] / max;
			
			results[driver][trip] = z;
			
			//Utils.msg(driver + " " + trip + " " + z);
		}
		*/
		
		return results;
	}
	
	public static void main(String[] str)
	{
		float[][] r = readSubmission("95366.txt");
		int k;
		
		//double top = 0.55;
		
		SampleSet s = new SampleSet();
		
		s.sortBy(4);
		k = 0;
		
		for (int i = 0; i < s.samples.size(); ++i)
		{
			Sample sample = s.samples.elementAt(i);
			if (sample.valid)
			{
				if (k >= 547200 * (1 - 0.60))
				{
					r[sample.driver][sample.trip] = 1; 
				}
				
				k++;
			}
		}
		
		s.sortBy(5);
		k = 0;
		
		for (int i = 0; i < s.samples.size(); ++i)
		{
			Sample sample = s.samples.elementAt(i);
			if (sample.valid)
			{
				if (k >= 547200 * (1 - 0.40))
				{
					r[sample.driver][sample.trip] = 1; 
				}
				
				k++;
			}
		}
		
		driverCorrectionOnly(r);
		
		/*
		s.sortBy(4);
		k = 0;
		
		for (int i = 0; i < s.samples.size(); ++i)
		{
			Sample sample = s.samples.elementAt(i);
			if (sample.valid)
			{
				if (k < 27360)
				{
					if (r[sample.driver][sample.trip] > 0.05)
					{
						Utils.msg(k + " " + sample.stats[4] + " " + r[sample.driver][sample.trip]);
						r[sample.driver][sample.trip] = (r[sample.driver][sample.trip] + 0.05f) / 2;
					}
				}
				else
					break;
				
				k++;
			}
		}
		*/
		
		rewriteSubmission(r);
		
		Utils.msg("complete");
	}
	
	void junk()
	{
		Utils.msg("starting Submissions");
		
		float[][] r = readSubmission("95366.txt");
		float[][] r2 = readSubmission("222.txt");
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j)
				if (r[i][j] != Main.NullNumber)
				{
					r[i][j] = r[i][j] - 0.5f * r2[i][j];
					r[i][j] = (r[i][j] + 0.5f) / 1.5f;
				}
		
		
		rewriteSubmission(r);
		Utils.msg("complete");
		
		//
		// did 4 and 5
		//
		
		//float[][] r = samplesToArray(5);
		//driverCorrection(r);
		
		
		/*
		float[][] r = readSubmission("95076.txt");
		
		for (int i = 0; i < Main.ND; ++i)
			if (r[i][1] != Main.NullNumber)
			{
				Utils.msg(i);
				Element[] e = new Element[200];
				
				for (int j = 1; j < Main.D1; ++j) 
					e[j - 1] = new Element(i, j, r[i][j]);
				
				Element.sortByR(e);
				
				for (int j = 1; j < Main.D1; ++j)
					r[e[j - 1].driver][e[j - 1].trip] = ((float) j) / 200;
			}
		
		rewriteSubmission(r);
		*/
	}
	
	static float[][] samplesToArray(int stat)
	{
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int i = 0; i < Main.ND; ++i) 
			for (int j = 0; j < Main.D1; ++j) 
				results[i][j] = Main.NullNumber;
		
		SampleSet set = new SampleSet();
		
		for (int i = 0; i < set.samples.size(); ++i)
		{
			Sample s = set.samples.elementAt(i);
			if (s.valid)
			{
				results[s.driver][s.trip] = (float) s.stats[stat]; 
			}
		}
		
		return results;
	}
	
	static void driverCorrection(float[][] r)
	{
		for (int i = 0; i < Main.ND; ++i)
			if (r[i][1] != Main.NullNumber)
			{
				Utils.msg(i);
				Element[] e = new Element[200];
				
				for (int j = 1; j < Main.D1; ++j) 
					e[j - 1] = new Element(i, j, r[i][j]);
				
				Element.sortByR(e);
				
				for (int j = 1; j < Main.D1; ++j)
					r[e[j - 1].driver][e[j - 1].trip] = ((float) j) / 200;
			}
		
		rewriteSubmission(r);
	}
	
	static void driverCorrectionOnly(float[][] r)
	{
		for (int i = 0; i < Main.ND; ++i)
			if (r[i][1] != Main.NullNumber)
			{
				Utils.msg(i);
				Element[] e = new Element[200];
				
				for (int j = 1; j < Main.D1; ++j) 
					e[j - 1] = new Element(i, j, r[i][j]);
				
				Element.sortByR(e);
				
				for (int j = 1; j < Main.D1; ++j)
					r[e[j - 1].driver][e[j - 1].trip] = ((float) j) / 200;
			}
	}
}
