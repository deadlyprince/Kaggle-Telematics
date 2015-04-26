
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.Vector;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class Utils 
{
	static PrintStream out = null;
	static PrintStream save = null;
	
	static public String[] splitFields(String s, String sep, boolean inQuotes)
	{
		String[] field = new String[100000];
		
		int n = 0;
		int start = 0;
		
		while (true)
		{
			int k0 = s.indexOf(sep, start);
			
			if (k0 < 0)
				field[n] = s.substring(start).trim();
			else
				field[n] = s.substring(start, k0).trim();
			
			n++;
			start = k0 + 1;
			
			if (k0 < 0)
				break;
		}
				
		String[] f = new String[n];
		
		for (int i = 0; i < n; ++i)
			f[i] = field[i];
		
		return f;
	}
	
	static public Vector<String[]> getData(String file)
	{
		Vector<String[]> r = new Vector<String[]>();
		
		try
		{
			LineNumberReader in = new LineNumberReader(new FileReader(file));

			while (true)
			{
				String s = in.readLine();				
				if (s == null)
					break;
	
				String[] fields = splitFields(s, ",", false);
				r.addElement(fields);
			}
		
			in.close();
		}
		
		catch (Exception e)
		{
			System.out.println(e.toString()); e.printStackTrace();	
		}
		
		return r;
	}
	
	static int toInt(String a)
	{
		try
		{
			return new Integer(a).intValue();
		}
		catch (Exception e)
		{
			msg("int exp: " + a);
			return 0;
		}
	}
	
	static float toFloat(String a)
	{
		try
		{
			return new Float(a).floatValue();
		}
		catch (Exception e)
		{
			msg("float exp: " + a);
			return 0;
		}
	}
	
	static boolean toBoolean(String a)
	{
		try
		{
			return new Boolean(a).booleanValue();
		}
		catch (Exception e)
		{
			msg("boolean exp: " + a);
			return false;
		}
	}
	
	static int ave(int[] x, int s, int l)
	{
		int ave = 0;
		for (int c = s; c < s + l; ++c)
		{
			ave += x[c];
		}
		ave /= l;
		return ave;
	}
		static float[] makeFloat(int[] x)
	{
		float[] f = new float[x.length];
		for (int i = 0; i < x.length; ++i) f[i] = x[i];
		return f;
	}
	
	static float kurtosis(int[] x)
	{
		float m = 0;
		for (int i = 0; i < x.length; ++i)
			m += x[i];
		m /= x.length;
		
		float t2 = 0;
		float t4 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] - m;
			t2 += t * t;
			t4 += t2 * t2;
		}
		t2 /= x.length;
		t4 /= x.length;
		return t4 / t2 / t2;
	}
	
	static float sd(int[] x)
	{
		float m = 0;
		for (int i = 0; i < x.length; ++i)
			m += x[i];
		m /= x.length;
		
		float t2 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] - m;
			t2 += t * t;
		}
		t2 /= x.length;
		
		return (float) Math.sqrt(t2);
	}
	
	static float sd(float[] x)
	{
		float m = 0;
		for (int i = 0; i < x.length; ++i)
			m += x[i];
		m /= x.length;
		
		float t2 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] - m;
			t2 += t * t;
		}
		t2 /= x.length;
		
		return (float) Math.sqrt(t2);
	}
	static float mean(float[] x)
	{
		float m = 0;
		for (int i = 0; i < x.length; ++i)
			m += x[i];
		m /= x.length;
		return m;
	}
	
	static float mean(int[] x)
	{
		float m = 0;
		for (int i = 0; i < x.length; ++i)
			m += x[i];
		m /= x.length;
		return m;
	}
	
	static float variance(float[] x)
	{
		float m = mean(x);	
		float t2 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] - m;
			t2 += t * t;
		}
		t2 /= x.length;
		return t2;
	}
	
	static float variance(int[] x)
	{
		float m = mean(x);	
		float t2 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] - m;
			t2 += t * t;
		}
		t2 /= x.length;
		return t2;
	}
	
	static float hcenter(int[] x)
	{
		float m = 0;
		float c = 0;
		for (int i = 0; i < x.length; ++i)
		{
			m += x[i] * i;
			c += x[i];
		}
		return m / c;
	}
	
	static float hvar(int[] x)
	{
		float m = Utils.hcenter(x);
		float t2 = 0;
		for (int i = 0; i < x.length; ++i)
		{
			float t = x[i] * (i - m);
			t2 += t * t;
		}
		t2 /= x.length;
		return t2;
	}
	
	static float min(int[] a)
	{
		float min = 999999999;
		for (int i = 0; i < a.length; ++i)
			if (a[i] < min)
				min = a[i];
		return min;
	}
	
	static float min(float[] a)
	{
		float min = 999999999;
		for (int i = 0; i < a.length; ++i)
			if (a[i] < min)
				min = a[i];
		return min;
	}
	
	static float max(float[] a)
	{
		float max = -999999999;
		for (int i = 0; i < a.length; ++i)
			if (a[i] > max)
				max = a[i];
		return max;
	}
	
	static int max(int[] a)
	{
		int max = -999999999;
		for (int i = 0; i < a.length; ++i)
			if (a[i] > max)
				max = a[i];
		return max;
	}
	
	static int nonzero(int[] x)
	{
		int c = 0;
		for (int i = 0; i < x.length; ++i)
		{
			if (x[i] > 0) c++;
		}
		return c;
	}
	
	static float entropy(int[] a)
	{
		double t = 0;
		for (int i = 0; i < a.length; ++i) t += a[i];
		double e = 0;
		for (int i = 0; i < a.length; ++i)
		{
			if (a[i] == 0) continue;
			double p = (a[i] / t);
			e += p * Math.log(p);
		}
		return (float) -e;
	}
	/*
	static float cp(float[] kp, float[] kn)
	{
		// normalized ...
		float dpp = len(kp);
		float dnn = len(kn);
		return (distance2(kp, kn) / dpp) / dnn;
	}
	*/
	static float d2(float[] kp, float[] kn)
	{
		float tot = 0;
		for (int t = 0; t < kp.length; ++t)
		{
			float z = kp[t] - kn[t];
			tot += z * z;
		}
		return (float) Math.sqrt(tot);
	}
	
	static float len(float[] kp)
	{
		float tot = 0;
		for (int t = 0; t < kp.length; ++t)
		{
			float z = kp[t];
			tot += z * z;
		}
		return (float) Math.sqrt(tot);
	}
	
	static void pause(int n)
    {
        try
        {
            Thread.sleep(n);
        }
        catch (Exception e)
        {
            
        }
    }

	static synchronized void save(String a)
	{
		try 
		{
			if (save == null)
				save = new PrintStream(Main.SaveFile);

			if (save != null)
				save.println(a);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	static synchronized void msg(Object a)
	{
		System.out.println(a);
		
		try 
		{
			if (out == null)
			{
				out = new PrintStream(Main.OutputFile);	
			}
			if (out != null)
				out.println(a);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	static float ds(float x0, float y0, float x1, float y1)
	{
		float dx = x0 - x1;
		float dy = y0 - y1;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	static float hdiff(float a, float b)
	{
		float q = a - b;
		
		if (q <= (-Trip.p2 / 2)) return q + Trip.p2;
		if (q >= (Trip.p2 / 2)) return q - Trip.p2;
		return q;
	}
	

	public static int[] histogram(float[] r, int n, int len)
	{
		float max = -99999999.0f;
		float min = 99999999.0f;

		for (int i = 0; i < len; ++i)
		{
			if (r[i] > max) 
				max = r[i];
			if (r[i] < min) 
				min = r[i];
		}

		min -= 0.000001f;
		max += 0.000001f;

		int[] h = new int[n];
		
		for (int i = 0; i < len; ++i)
		{
			int k = (int) ((n * (r[i] - min))/(max - min));
			if (k == n) Utils.msg("XXX histogram " + min + " " + max + " " + r[i]);
			h[k]++;
		}
		
		return h;
	}
	
	static float dInt(float x)
	{
		int i = (int) x;
		if (x < 0)
			i -= 1;
		float f = x - i;
		if (f > 0.5)
			f = 1 - f;
		return f;
	}
	
	static void regression()
	{
		double[] y = { 1, 1, 1, 0, 0, 0 };
		double[][] x = {
				
				{5, -2, 1},
				{3, -8, 2},
				{3, -9, 2},
				
				{8, -10, 4},
				{8, -12, 3},
				{8, -10, 3}
		};
		Utils.regression(x, y);
	}
	
	static double[] regression(double[][] x, double[] y)
	{
		OLSMultipleLinearRegression r = new OLSMultipleLinearRegression();
		
		r.newSampleData(y, x);
		double[] v = r.estimateRegressionParameters();
		
		Utils.msg("============");
		
		for (int i = 0; i < v.length; ++i)
			Utils.msg("B[" + i + "] = " + v[i]);
		
		Utils.msg("============");
		
		float rr = (float) r.calculateRSquared();
		Utils.msg("r2=" + rr);
		
		return v;
	}
	
	static public int r(int n)
	{
		return (int) (Math.random() * n);
	}
}
