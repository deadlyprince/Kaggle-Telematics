import java.util.Vector;


class Result
{
	static int ND = 4000;
	static int D1 = 201;
	static Result[][] results = new Result[ND][D1];
	int t0, t1, s0, s1, len, driver;
	boolean r0, r1;
	boolean updated;
	float score;
	private float newScaledScore;
	float oldScaledScore;
	
	public String toString()
	{
			return driver + 
			"," + t0 +
			"," + r0 +
			"," + t1 + 
			"," + r1 +
			"," + s0 +
			"," + s1 + 
			"," + len + 
			"," + score +
			"," + newScaledScore;
	}
	
	Result()
	{
		
	}
	
	synchronized static void update(int d, int t, float s)
	{
		if (results[d][t] == null) results[d][t] = new Result();
		results[d][t].score = s;
		results[d][t].updated = true;
	}
	
	Result(String[] s)
	{
		driver = Utils.toInt(s[0]);
		t0 = Utils.toInt(s[1]);
		r0 = Utils.toBoolean(s[2]);
		t1 = Utils.toInt(s[3]);
		if (t1 > 0)
		{
			r1 = Utils.toBoolean(s[4]);
			s0 = Utils.toInt(s[5]);
			s1 = Utils.toInt(s[6]);
			len = Utils.toInt(s[7]);
			score = Utils.toFloat(s[8]);
		}
		results[driver][t0] = this;
	}
	
	private static void printResults()
	{
		for (int i = 1; i < ND; ++i)
		{
			if (results[i][1] == null) continue;
			scaleScores(i);
			pdriver(i);
		}
	}
	
	synchronized static void printResult(int i)
	{
		scaleScores(i);
		pdriver(i);
	}
	
	private static void printDeltaResults()
	{
		for (int i = 1; i < ND; ++i)
		{
			if (results[i][1] == null) continue;
			scaleScores(i);
			pdeltadriver(i);
		}
	}
	
	private static void pdriver(int n)
	{
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			Utils.save(n + "_" + i + "," + results[n][i].newScaledScore);
	}
	
	private static void pdeltadriver(int n)
	{
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (results[n][i].newScaledScore != results[n][i].oldScaledScore)
				Utils.save(n + "_" + i + "," + results[n][i].newScaledScore + " --- " + results[n][i].oldScaledScore);
	}
	
	private static void scaleScores(int n)
	{
		//for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		//	if (results[n][i].score < 6000)
		//		results[n][i].updated = true;
					
					
					
					
					
					
		float max;
		
		max = -9999999;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (results[n][i].updated)
				if (results[n][i].score > max)
					max = results[n][i].score;
		max++;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (results[n][i].updated)
			{
				float s = 0.5f * results[n][i].score / max;
				if (s > 0.5) s = 0.5f;
				int fi = (int) (s * 100000);
				results[n][i].newScaledScore = fi / 100000f;
				//Utils.msg(n + "_" + i + "," + s);
			}
		
		max = -9999999;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (!results[n][i].updated)
				if (results[n][i].score > max)
					max = results[n][i].score;
		max++;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (!results[n][i].updated)
			{
				float s = 0.5f + (0.5f * results[n][i].score / max);
				if (s >= 0.999f) s = 0.999f;
				int fi = (int) (s * 100000);
				results[n][i].newScaledScore = fi / 100000f;
				//Utils.msg(n + "_" + i + "," + s);
			}
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			if (results[n][i].score == 0.0)
				results[n][i].newScaledScore = 0.1f;
	}
}