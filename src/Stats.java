
public class Stats 
{
	static void test2()
	{
		Driver driver = new Driver(0, Utils.toInt(Main.randomDriver()));
		new Display(driver);
	}
	
	static void test1()
	{
		int N = 20;
		
		double[][] x = new double[(2 * N) * (Driver.lastTripIndex)][];
		double[] y = new double[(2 * N) * (Driver.lastTripIndex)];

		int c = 0;
		
		for (int r = 0; r < N; ++r)
		{
			x[c] = getPositiveSample();
			y[c] = 1;
			c++;
			Utils.msg(c);
		}
		
		for (int r = 0; r < N; ++r)
		{
			x[c] = getNegativeSample();
			y[c] = -1;
			c++;
			Utils.msg(c);
		}
		
		double[][] xx = new double[c][];
		double[] yy = new double[c];

		for (int i = 0; i < c; ++i)
		{
			xx[i] = x[i];
			yy[i] = y[i];
			String s = "";
			for (int j = 0; j < x[i].length; ++ j)
				s += x[i][j] + ",";
			Utils.msg(s + y[i]);
		}
		
		x = xx;
		y = yy;
		
		double[] v = Utils.regression(x, y);
		
		int n = 0;
		c = 0;
		
		for (int i = 0; i < N; ++i)
		{
			double[] s = getPositiveSample();
			c++;
			if (predict(s, v)) n++;
		}
		
		for (int i = 0; i < N; ++i)
		{
			double[] s = getNegativeSample();
			c++;
			if (!predict(s, v)) n++;
		}
		
		Utils.msg((n * 100) / c);
	}
	
	static boolean predict(double[] x, double[] params)
	{
		double t = 0;
		for (int k = 1; k < params.length; ++k)
			t += params[k] * x[k - 1];
		t += params[0];
		return (t > 0);
	}
	
	static double[] getPositiveSample()
	{
		while (true)
		{
			Driver driver = new Driver(0, Utils.toInt(Main.randomDriver()));
			int t = 1 + (int) (Driver.lastTripIndex * Math.random());
			
			if (driver.trips[t].len < 100) continue;
			return null;//Driver.statistic(driver.trips[t], driver);
		}
	}
	
	static double[] getNegativeSample()
	{
		while (true)
		{
			Driver outdriver = new Driver(0, Utils.toInt(Main.randomDriver()));
			Driver driver = new Driver(0, Utils.toInt(Main.randomDriver()));
			
			int t = 1 + (int) (Driver.lastTripIndex * Math.random());
			
			if (outdriver.trips[t].len < 100) continue;
			return null;//Driver.statistic(outdriver.trips[t], driver);
		}
	}
}
