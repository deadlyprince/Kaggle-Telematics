import java.util.Vector;

public class Trip implements ISortable
{
	static float p2 = (float) (Math.PI * 2);
	
	float[] x;
	float[] y;
	int xlen;
	
	// calculated 
	
	float xmax, xmin, ymax, ymin;
	
	float[] spacedx;
	float[] spacedy;
	int len;
	
	int[] time;
	float[] speed;
	int filenumber;
	
	float[] shapes;
	float[] distances;
	float[] sentropy;
	float[] dentropy;
	Trip reverseTrip;
	boolean isReverse = false;
	float totEntropy = -1;
	float bestscore;
	boolean dataErr = false;
	int driver;
	boolean changed;
	
	float[] params = new float[NPARAMETERS];
	float nearestStatNeighborDistance;
	
	float totEntropy()
	{
		if (totEntropy < 0) totEntropy = tripEntropy(0, len - 1);
		return totEntropy;
	}
	
	float tripEntropy(int s, int e)
	{
		if (s < 0 || e < 0 || s >= len || e >= len) Utils.msg("> s e = " + s + " " + e + " len = " + len + " " + isReverse);
		float t = 0;
		for (int i = 1; i <= 5; ++i)
			t += i * nEntropy(i, s, e);
		return t;
	}
	
	float nEntropy(int n, int s, int e)
	{
		float t = 0;
		int c = 0;
		for (int i = s; i <= e - n; ++i)
		{
			t += sentropy[i + n * len] + dentropy[i + n * len];
			c++;
		}
		if (c > 0)
			return t;
		else
			return 0;
	}
	
	Trip()
	{
		
	}
	
	Trip(int i, int c)
	{
		bestscore = -99999f;
		driver = c;
	}
	
	Trip(int fn, String fileName, int c)
	{
		filenumber = fn;
		driver = c;
		
		input(fileName);
		calculate();
		
		//printSpacedPoints();
		
		reverseTrip = new Trip();
		reverseTrip.filenumber = filenumber;
		reverseTrip.isReverse = true;
		reverseTrip.driver = driver;
		reverseTrip.xlen = xlen;
		reverseTrip.x = new float[xlen];
		reverseTrip.y = new float[xlen];
		
		for (int i = 0; i < xlen; ++i)
		{
			reverseTrip.x[i] = x[xlen - 1 -i];
			reverseTrip.y[i] = y[xlen - 1 -i];
		}
		
		reverseTrip.calculate();
	}
	
	private void calculate()
	{
		//Utils.msg("Begin calculate");
		
		spacedPoints();
		
		//Utils.msg("Finish spaced points");
		
		xmax = Utils.max(spacedx);
		ymax = Utils.max(spacedy);
		
		xmin = Utils.min(spacedx);
		ymin = Utils.min(spacedy);
		
		shapes = new float[len * 10];
		distances = new float[len * 10];
		
		sentropy = new float[len * 10];
		dentropy = new float[len * 10];
		
		for (int i = 0; i < len; ++i)
		{
			for (int t = 2, k = 0; k < 10; ++k, t *= 2)
			{
				int j;
				
				j = i + t;
				
				if (j < len)
				{
					distances[i + k * len] = ds(i, j);
					
					float base = getHeading(i, (i + j) / 2); 
					float heading = getHeading(i , j);

					shapes[i + k * len] = Math.abs(Utils.hdiff(heading, base));
					
				}
			}
		}
		
		//Utils.msg("Finish calculate");
	}
	
	static int cds = 0;
	static float totds = 0;

	static int[] digits = new int[10];
	static int ndigits = 0;

	void spacedPoints()
	{	
		float totlen = 0;
		
		//float lastq = 0;
		
		for (int i = 1; i < x.length; ++i)
		{
			float q = d(i - 1, i);
			/*
			//Utils.msg(i + "===" + q + " " + Utils.dInt(q));
			
			if (q > 3)
			{
			int k = (int) ((8f / 9f)  * q);
			k = k % 10;
			//k = (int) (10 * Math.random());
			digits[k]++;
			ndigits++;
			}
			
			if (q > 1000)
			{
				//Utils.msg("**************** trip " + filenumber + " distance > 1000 at " + x[i - 1] + " " + y[i - 1] + " --> " + x[i] + " " + y[i]);
			}
			
			if (q > 50)
			{
				//Utils.msg(	"TOO FAST: speed is " + (q * 2.24f) + "MPH for trip " + 
				//			filenumber + " last speed was " + (lastq * 2.24f)); 
				//			//x[i - 1] + " " + y[i - 1] + " --> " + x[i] + " " + y[i]);
				//dataErr = true;
			}
			*/
			totlen += q;
			
			//lastq = q;
		}
		
		int n = (int) (totlen / Main.Spacing);
		len = n;
		
		spacedx = new float[n];
		spacedy = new float[n];
		
		time = new int[n];
		speed = new float[n];
		
		if (len <= 1)
		{
			if (false) Utils.msg("driver " + driver + " trip " + filenumber + " spaced data len = " + len + " raw data len = " + x.length);
			return;
		}
		
		float t = 0;
		
		spacedx[0] = x[0];
		spacedy[0] = y[0];
		
		int k = 1;
		
		float seek = Main.Spacing;
		
		for (int i = 1; i < x.length; ++i)
		{
			float q = d(i - 1, i);
			
			float lt = t;
			t += q;
		
			// while the next spaced point is between these two nodes 
			
			while (lt <= seek && t > seek)
			{
				//if (filenumber == 163) Utils.msg("seeking " + seek + " between " + lt + " and " + t);
				
				// the remaining distance to go in this segment
				
				float f = (seek - lt) / q;
				
				if (Float.isNaN(f) || Float.isInfinite(f))
					Utils.msg("*** " + f);
				
				float mx = (x[i] - x[i - 1]) * f;
				float my = (y[i] - y[i - 1]) * f;
				
				float tx = x[i - 1] + mx;
				float ty = y[i - 1] + my;
				
				//float totDist = lt + (float) Math.sqrt(mx * mx + my * my);
				
				float qx = spacedx[k - 1] - tx;
				float qy = spacedy[k - 1] - ty;
				
				float tr = (float) Math.sqrt(qx * qx + qy * qy);
				
				//if (filenumber == 152) Utils.msg(tr);
				
				if (tr > Main.Spacing - 0.01)
				{
					//if (filenumber == 163) Utils.msg("make point k=" + k);
					
					spacedx[k] = tx;
					spacedy[k] = ty;
					
					time[k] = i;
					speed[k] = q;
					
					seek += Main.Spacing;
					k++;
					if (k == len) 
						break;
				}
				else
				{
					
					//if (index == 19 || index == 134) 
					//	Utils.msg("trip " + index + " at " + k + " " + 
					//		isReverse + " q=" + q + " tr=" + tr + " tot=" + totDist);
					//if (filenumber == 163) Utils.msg("seek 1 = " + seek);
					seek += 0.01;
					//if (filenumber == 163) Utils.msg("seek 2 = " + seek);
				}
			}
			
			if (k == len) 
				break;
		}
		
		//if (n != k) Main.msg("n = " + n + " k = " + k);
	}
	
	float ds(int i , int j)
	{
		float qx = spacedx[i] - spacedx[j];
		float qy = spacedy[i] - spacedy[j];
		
		return (float) Math.sqrt(qx * qx + qy * qy);
	}
	
	float d(int i , int j)
	{
		float dx = x[i] - x[j];
		float dy = y[i] - y[j];
		
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	

	float getHeading(int f, int t)
	{
		return getHeading(spacedx[t] - spacedx[f], spacedy[t] - spacedy[f]);
	}
	
	float getHeading(float dx, float dy)
	{
		if (Math.abs(dx) < 0.001f)
		{
			if (dy > 0)
				return (p2 / 4);
			else
				return -(p2 / 4);
		}
		else
		{
			float a = (float) Math.atan(Math.abs(dy / dx));
			if (dy < 0)
			{
				if (dx < 0)
					return a + -(p2 / 2);
				else
					return -a;
			}
			else
			{
				if (dx < 0)
					return -a + (p2 / 2);
				else
					return a;
			}
		}
	}
	
	private void input(String n)
	{
		Vector<String[]> v = Utils.getData(n);
		
		xlen = v.size() - 1;
		
		x = new float[xlen];
		y = new float[xlen];
		
		for (int i = 1; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			x[i - 1] = convertNew(s[0]);
			y[i - 1] = convertNew(s[1]);
			
			//if (filenumber == 163) Utils.msg((i - 1) + " === " + x[i - 1] + " " + y[i - 1]);

			if (Math.abs(x[i - 1]) > 200000 || Math.abs(y[i - 1]) > 200000)
			{
				//Utils.msg("TOO LARGE driver " + driver + " trip " + filenumber + " point " + i + ": " + s[0] + " " + s[1]);
				x[i - 1] = 0;
				y[i - 1] = 0;
			}
			
			/*
			//s[0] = "" + ((int) (10 * Math.random()));
			//s[1] = "" + ((int) (10 * Math.random()));
			
			char c0 = s[0].charAt(s[0].length() - 1);
			char c1 = s[1].charAt(s[1].length() - 1);
			
			digits[(((int) c0) - ((int) '0'))]++;
			ndigits++;
			digits[(((int) c1) - ((int) '0'))]++;
			ndigits++;
			
			//Utils.msg(s[0] + " " + x[i - 1]);
			*/
		}
		
		/*
		// check data
		
		int end = -1;
		
		for (int i = 1; i < xlen; ++i)
		{
			float q = d(i - 1, i);

			if (q > 1000)
			{
				//Utils.msg("driver " + driver + " trip " + filenumber + " distance > 1000 at point " + i + " of " + xlen);
				end = i;
				dataErr = true;
				
				break;
			}
		}
		
		if (end < 0) 
			return;
		
		if (false)
		{
		xlen = end;
		
		float[] xn = new float[xlen];
		float[] yn = new float[xlen];
		
		for (int i = 0; i < xlen; ++i)
		{
			xn[i] = x[i];
			yn[i] = y[i];
		}
		
		x = xn;
		y = yn;
		}
		*/
	}
	
	float convertNew(String s1)
	{
		if (true) return Utils.toFloat(s1);
		
		String s = s1;
		
		if (s.charAt(s.length() - 2) != '.')
		{
			Utils.msg("***** &&& ***** invalid number: " + s + " driver = " + driver);
			return 0;
		}
		
		s = s.substring(0, s.length() - 2) + s.substring(s.length() - 1, s.length());
		
		int i = new Integer(s).intValue();

		float f = i + 100000;
		f = f / 10;
		
		//Utils.msg(s1 + " " + f);
		
		return f;
	}
	
	/*
	void printSpacedPoints()
	{
		for (int i = 1; i < len; ++i)
		{
			Utils.msg(	"trip " + filenumber + ", " + 
						(i - 1) + " to " + i + ": " + ds(i -1, i) + 
						" 2nd point: " + spacedx[i] + " " + spacedy[i]);
		}
	}
	*/
	
	public int compareTo(ISortable b)
	{
		if (bestscore > ((Trip) b).bestscore)
			return 1;
		else if (bestscore < ((Trip) b).bestscore)
			return -1;
		else
			return 0;
	}
	
	float maxSpeed()
	{
		float max = 0;
		for (int i = 0; i < len; ++i)
			if (speed[i] > max) max = speed[i];
		max = max * Main.MPH;
		if (max > 99) max = 99;
		return max;
	}
	
	float maxAccel()
	{
		int k = 2;
		float max = 0;
		for (int i = k; i < len; ++i) if ((speed[i] - speed[i - k]) > max) max = (speed[i] - speed[i - k]);
		if (max > 30) max = 30;
		return max;
	}
	
	float maxDeccel()
	{
		int k = 2;
		float max = 0;
		for (int i = k; i < len; ++i) if ((speed[i - k] - speed[i]) > max) max = (speed[i - k] - speed[i]);
		if (max > 30) max = 30;
		return max;
	}
	
	float timeFast()
	{
		int c = 0;
		for (int i = 0; i < len; ++i)
			if (speed[i] * Main.MPH > 60) c++;
		if (len == 0)
			return 0;
		return ((float) c) / len;
	}
	
	float timeStopped()
	{
		int c = 0;
		for (int i = 0; i < len; ++i)
			if (speed[i] < 2f) c++;
		return ((float) c) / (len + 1);
	}
	
	float timeMoving()
	{
		int c = 0;
		for (int i = 0; i < len; ++i)
			if (speed[i] > 3f) c++;
		return ((float) c) / (len + 1);
	}
	
	float variability()
	{
		float t = 0;
		for (int i = 1; i < len; ++i)
		{
			float q = Math.abs(speed[i - 1] - speed[i]);
			t += q;
		}
		return t / (len + 1);
	}
	
	float aveAccelTimeFor(int x, int y)
	{
		int lastx = -1;
		boolean before = true;
		float t = 0;
		int c = 0;
		for (int i = 1; i < len; ++i)
		{
			if (before)
			{
				if (speed[i] * Main.MPH > x && speed[i - 1] * Main.MPH < x)
				{
					lastx = i;
					before = false;
				}
			}
			else
			{
				if (speed[i] * Main.MPH > y && speed[i - 1] * Main.MPH < y)
				{
					if (lastx > 0)
					{
						int a = i - lastx;
						t += a;
						c++;
						before = true;
					}
				}
				if (speed[i] * Main.MPH < x)
				{
					before = false;
				}
			}
		}
		float r = t / (c + 1);
		//Utils.msg(x + " " + y + " : " + c + " " + r);
		return r;
	}
	
	int getx()
	{
		return (int) nearestStatNeighborDistance;
		//return len / 30;
	}
	
	int gety()
	{
		//return (int) (aveAccelTimeFor(20, 40) );
		//return (int) (xlen);
		return (int) (Result.results[driver][filenumber].score / 300);
	}
	
	static int NPARAMETERS = 4;
	
	float getParam(int p)
	{
		float r = getP(p);
		params[p] = r;
		//Utils.msg("return for " + p + " : " + r);
		return r;
	}
	
	private float getP(int p)
	{
		switch (p)
		{
			//case 0: return len;
			//case 1: return xlen;
			//case 0: return aveSpeed();
			case 1: return maxSpeed();
			case 2: return timeStopped();
			//case 3: return aveAccelTimeFor(1, 10);
			case 3: return aveAccelTimeFor(10, 20);
			//case 7: return maxAccel();
			//case 8: return maxDeccel();
			default: Utils.msg("Invalid p: " + p); return -1;
		}
	}
}
