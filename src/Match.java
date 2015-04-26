
public class Match
{
	Trip t0;
	Trip t1;
	int i0;
	int i1;
	int nsteps;
	float score;
	float err;
	
	Match(Trip t, Trip r, int s0, int s1, float s, int n, float e)	
	{
		//if (t.index == 55 && r.index == 75) Utils.msg("\n\nmatch " + t.index + " " + r.index + " " + s0 + " " + s1 + " " + s + " " + n);
		
		t0 = t;
		t1 = r;
		
		i0 = s0;
		i1 = s1;
		
		nsteps = n;
		score = s;
		err = e;
		
		//if (s > 8000) Collector.collect(this, true);
	}
	
	float maxSpeed(boolean first)
	{
		Trip t;
		if (first) t = t0; else t = t1;
		int ii;
		if (first) ii = i0; else ii = i1;
		
		if (ii + nsteps > t.speed.length) 
		{
			Utils.msg("OOB " + (ii + nsteps) + " " + t.len);
			return 0;
		}
		
		float max = -99;
		for (int i = ii; i < ii + nsteps; ++i)
		{
			if (t.speed[i] > max) max = t.speed[i];
		}
		
		return (int) (Main.MPH * max);
	}
	
	int time(boolean first)
	{
		if (first)
			return t0.time[i0 + nsteps - 1];
		else
			return t1.time[i1 + nsteps - 1];
	}
	
	public String toString()
	{
		return 	t0.filenumber + "," + 
				t0.isReverse + "," +
				t1.filenumber + "," + 
				t1.isReverse + "," + 
				i0 + "," + 
				i1 + "," + 
				nsteps + "," +
				score;
	}
}