
public class Collector 
{
	static int x[] = new int[5000];
	static int y[] = new int[5000];
	static int n = 0;
	
	static void collect(Match m, boolean valid)
	{
		//Utils.msg("collecting " + m + " as " + valid);
		
		int k = 6;
		
		if (m.nsteps >= 256)
		{
			int dx = (int) (50 * Math.abs(m.t0.distances[m.i0 + k * m.t0.len] - m.t1.distances[m.i1 + k * m.t1.len]));
	 		int da = (int) (10000 * Math.abs(m.t0.shapes[m.i0 + k * m.t0.len] - m.t1.shapes[m.i1 + k * m.t1.len]));
	 		
	 		//Utils.msg(dx + " " + da + " as " + valid);
 		
	 		if (n < 5000)
	 		{
	 			x[n] = dx;
	 			y[n] = da;
	 			n++;
	 		}
		}
	}
}
