
import java.util.Vector;

public class Driver extends Thread
{
	static int tripFiles = 200;
	static int lastTripIndex = 200;
	static int StepSize = 12;
	static int WalkInc = 12;
	static int m = 32;
	// strides are distances between points used in calculating score in walk()
	static int NumberOfStrides = 10; 
	
	static float[] athresh = new float[NumberOfStrides];
	static float[] dthresh = new float[NumberOfStrides];
	
	// reusable scratch area
	float[] set = new float[5000000];
	
	/////////////
	
	Trip[] trips;

	int[] ot = new int[NumberOfStrides];
	int[] or = new int[NumberOfStrides];
	float[] tdistances;
	float[] tshapes;
	float[] tsentropy;
	float[] tdentropy;
	float[] rdistances;
	float[] rshapes;
	
	// this should just keep the best result for each pair
	Match[][] matchTable;
	
	int myProcessor;
	int drivern;
	int stepLimit = -1;

	//float[] parametersMean = new float[Trip.NPARAMETERS];
	//float[] parametersSD = new float[Trip.NPARAMETERS];
	
	//float[] parametersMax = new float[Trip.NPARAMETERS];
	//float[] parametersMin = new float[Trip.NPARAMETERS];
	
	Driver(int n)
	{
		myProcessor = n;
	}
	
	Driver(int n, int dir)
	{
		myProcessor = n;
		drivern = dir;
		init();
	}
	
	static
	{
		float mc = m / 2;
		for (int i = 3; i < NumberOfStrides; ++i)
		{
			athresh[i] = 2f / mc;
			mc *= 2;
		}
		
		mc = 1f;
		for (int i = 3; i < NumberOfStrides; ++i)
		{
			dthresh[i] = Main.Spacing * mc;
			mc += 0.25f;
		}
	}
	
	public void run()
	{
		try
		{
			while (true)
			{
				String name = Main.getNextDriver();
			
				if (name == null)
					break;

				drivern = Utils.toInt(name);
				
				if (Main.DisplayOnly)
				{
					init();
				}
				else
				{
					init();
					process(null);
				}
				
				//Utils.msg("finish " + file + " on " + myProcessor);
			}
		}
		catch (Exception e)
		{
			//Utils.msg("\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n" + e);
			e.printStackTrace();
		}
	}
	
	void init()
	{
		buildTrips(Main.DataDir + drivern + "/");

		//boolean CreateDrivingStatistics = false;
		//boolean CalculateStats = false;
		//if (CalculateStats)
		//{
		//	calculateStats();
		//	rankByStats();
		//}
		
		matchTable = new Match[lastTripIndex + 1][lastTripIndex + 1];
		
		calculateEntropyContent(trips, set, true);
		calculateEntropyContent(trips, set, false);
	}
	
	void process(float[][] runTable)
	{
		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			Trip t = trips[i];
			
			if (runTable == null || (runTable[drivern][t.filenumber] != Main.NullNumber))
			{
				for (int j = i + 1; j < lastTripIndex + 1; ++j)
				{
					Trip r = trips[j];
					if (analyzeTripPair(t, r)) ;
					if (analyzeTripPair(t, r.reverseTrip)) ;
				}
				
				Utils.msg("Score driver " + drivern + " trip " + t.filenumber + " best score = " + bestScore(trips[i]));
			}
		}

		for (int i = 1; i < lastTripIndex + 1; ++i)
			trips[i].bestscore = bestScore(trips[i]);
		Sorter.sort(trips);
	}
	
	FPair match(Trip foreignTrip)
	{		
		for (int j = 1; j < lastTripIndex + 1; ++j)
		{
			Trip r = trips[j];
			
			if (r == foreignTrip) continue;
			
			if (analyzeTripPair(foreignTrip, r)) ;
			if (analyzeTripPair(foreignTrip, r.reverseTrip)) ;
		}
		
		float max = -99999;
		float tot = 0;
		
		int f = foreignTrip.filenumber;
		
		for (int j = 1; j < Driver.lastTripIndex + 1; ++j)
		{
			float s = 0;
			
			if (matchTable[f][trips[j].filenumber] != null)
				s = matchTable[f][trips[j].filenumber].score;
			
			if (s > max) max = s;
			tot += s;
		}
		
		tot /= Driver.lastTripIndex;
		
		return new FPair(max, tot);
	}	

	void buildTrips(String name)
	{
		trips = new Trip[lastTripIndex + 1];

		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			//Utils.msg("Build trip " + i);
			trips[i] = new Trip(i, name + i + ".csv", drivern);
		}
		
		trips[0] = new Trip(0, drivern);
	}
	
	boolean analyzeTripPair(Trip t, Trip r)
	{
		//Utils.msg(t.filenumber + " " + t.len + " " + r.index + " " + r.len);

		int minMatch = 32;
		
		rdistances = r.distances;
		rshapes = r.shapes;
		
		tdistances = t.distances;
		tshapes = t.shapes;
		
		tsentropy = t.sentropy;
		tdentropy = t.dentropy;
		
		for (int i = 3; i < NumberOfStrides; ++i)
		{
			ot[i] = i * t.len;
			or[i] = i * r.len;
		}
		
		int min = -r.len;
		int max = t.len;
		
		boolean found = false;
		
		int c = 0;
		
		for (int shift = min; shift < max; ++shift)
		{
			//
			// t
			// t 
			// t r
			// t r
			// t r 
			// t r 
			//   r
			//   r
			//
			
			//Utils.msg("* " + r.filenumber + " " + shift);
			
			int st = Math.max(0,  shift);
			int et = Math.min(t.len - 1, r.len + shift - 1);

			if (et - st + 1 < minMatch)
				continue;
			
			// 32 / 16 ?
			//int stepsize = ;
			for (int wstart = st; wstart <= et - minMatch; wstart += StepSize)
			{
				walk(wstart, et, shift, t, r);
			}
			
			
		}
		
		return found;
	}
	
	boolean walk(int start, int end, int shift, Trip t, Trip r)
	{	
		int inc = WalkInc;
		
		int segPosT, segPosR, lastT = 0;
	
		segPosT = start;
		segPosR = start - shift;
		
		segPosT += inc;
		segPosR += inc;
		
		int p = 0;
		
		//
		// try aggregating entropy, distance error, and shape error in
		// arrays by "jump" 8 / 16 / 32 etc
		//
		
		float totEntropy = 0;
		float totError = 0;
		
		int c = 0;
		int l = 0;
		
		dowalk:
			
		while (true)
		{
			
			p = segPosT - start;
			
			
			// test .............
			
			
			//if (stepLimit > 0)
			//	if (p > stepLimit)
			//		break;


			for (int k = 16, i = 3; i < 7; ++i, k *= 2)
			{
				if (p >= k)
				{
					int ti = segPosT - k + ot[i];
					int ri = segPosR - k + or[i];
					
					float dx = Math.abs(tdistances[ti] - rdistances[ri]);
					float ax = Math.abs(tshapes[ti] - rshapes[ri]);
				
					if (dx > dthresh[i] || ax > athresh[i])
						break dowalk;
					
					float x = tsentropy[ti] + tdentropy[ti];
					totEntropy += x * x;
					
					totError += dx + 100 * ax;
					c++;
				}
				else
					break;
			}
			
			if (segPosT + inc > end)
				break;
		
			
			lastT = segPosT;
			segPosT += inc;
			segPosR += inc;		
		}
		
		int len = lastT - start + 1;
		
		if (len > 16)
		{
			int t0 = t.filenumber;
			int t1 = r.filenumber;
			
			totError = (totError / (c + 1));
			float te = totEntropy * 100 / (totError + 1);
			float re = te;
			

			// boost for fraction used
			
			te *= 1 + 1f * ((((float) te) / t.totEntropy()));
			re *= 1 + 1f * ((((float) re) / r.totEntropy()));
		
		

			// closeness of starts and ends
			
			float nn = 0;
			
			for (int y = 0; y < 4; ++y)
			{
				if (y == 0) nn = start + start - shift;
				if (y == 1) nn = (t.len - lastT) + (r.len - (lastT - shift));
				if (y == 2) nn = start + (r.len - (lastT - shift));
				if (y == 3) nn = start - shift + (t.len - lastT);
				
				// total of 250 meters gives a boost
				nn = 250 - nn * Main.Spacing;
				
				if (nn > 0)
				{
					te *= (1 + (nn / 500));
					re *= (1 + (nn / 500));
				}
			}

		
			// see if the possible score is greater than best so far
			
			if (matchTable[t0][t1] == null || matchTable[t0][t1].score < te)
				matchTable[t0][t1] = new Match(t, r, start, start - shift, te, len, totError);

			
			
			if (matchTable[t1][t0] == null || matchTable[t1][t0].score < re) 
				matchTable[t1][t0] = new Match(r, t, start - shift, start, re, len, totError);

			
			return true;
		}
		
		return false;
	}

	Match bestMatch(Trip t)
	{
		if (matchTable == null) return null;
		
		float max = -999;
		Match r = null;
		
		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			if (matchTable[t.filenumber][i] != null && matchTable[t.filenumber][i].score > max)
			{
				max = matchTable[t.filenumber][i].score;
				r = matchTable[t.filenumber][i];
			}
		}
		
		return r;
	}
	
	float bestScore(Trip t)
	{
		Match m = bestMatch(t);
		if (m == null)
			return 0;
		else
			return m.score;
	}
	
	static void calculateEntropyContent(Trip[] trips, float[] set, boolean shape)
	{
		float[] input;
		float[] output;
		
		for (int t = 1, k = 0; k < 10; ++k, t *= 2)
		{
			int x = 0;
			
			// get every instance of shape or distance values
			for (int i = 1; i < lastTripIndex + 1; ++i)
			{
				Trip trip = trips[i];
				if (shape) input = trip.shapes; else input = trip.distances;
				
				for (int j = 0; j < trip.len - t; ++j)
					set[x++] = input[j + k * trip.len];
				
				trip = trip.reverseTrip;
				if (shape) input = trip.shapes; else input = trip.distances;
				
				for (int j = 0; j < trip.len - t; ++j)
					set[x++] = input[j + k * trip.len];
			}
			
			int S = 100;
			
			// get bounds
			float max = -99999999.0f;
			float min = 99999999.0f;

			for (int i = 0; i < x; ++i)
			{
				if (set[i] > max) 
					max = set[i];
				if (set[i] < min) 
					min = set[i];
			}

			int[] h = new int[S];
			
			// calculate frequency of each of 100 ranges of value
			for (int i = 0; i < x; ++i)
			{
				int ks = (int) (S * ((set[i] - min)/(max - min + 0.01f)));
				if (ks == S) ks--; 
				h[ks]++;
			}
			
			// calculate aggregate frequency of each range and above
			int tot = 0;
			for (int p = S - 1; p >= 0; --p)
			{
				tot += h[p];
				h[p] = tot;
			}
			
			float[] e = new float[S];
			
			// for each class, assign log(prob())
			for (int p = 0; p < S; ++p)
			{
				e[p] = - (float) Math.log(((float) h[p]) / x);
				if (e[p] < 
						1 // 0.001
						) e[p] = 0;
				
				//Utils.msg(p + " " + h[p] + " " + x + " " + e[p]);
			}
			
			// set log(prob()) for each shape or distance value
			for (int i = 1; i < lastTripIndex + 1; ++i)
			{
				Trip trip = trips[i];
				if (shape) input = trip.shapes; else input = trip.distances;
				if (shape) output = trip.sentropy; else output = trip.dentropy;
				
				for (int j = 0; j < trip.len - t; ++j)
				{
					int s = (int) ((S * (input[j + k * trip.len] - min))/(max - min + 0.01f));
					if (s == S) s--; 
					output[j + k * trip.len] = e[s];
				}
				
				trip = trip.reverseTrip;
				if (shape) input = trip.shapes; else input = trip.distances;
				if (shape) output = trip.sentropy; else output = trip.dentropy;
				
				for (int j = 0; j < trip.len - t; ++j)
				{
					int s = (int) ((S * (input[j + k * trip.len] - min))/(max - min + 0.01f));
					if (s == S) s--; 
					output[j + k * trip.len] = e[s];
				}
			}
		}
	}

	
	
	

	//void output()
	//{
		//for (int i = 1; i < lastTripIndex + 1; ++i)
		//	trips[i].bestscore = bestScore(trips[i]);
		//Sorter.sort(trips);
		
		/*
		for (int i = 1; i < lastTripIndex + 1; ++i)
			trips[i].bestscore = bestScore(trips[i]);
		
		new QSortAlgorithm().sort(trips);
		
		
		if (false)
		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			Match m = bestMatch(trips[i]);
			
			String s = "-- none yet";
			
			if (false) // write real results
			{	
				if (m != null)
					s = 	drivern + 
							"," + m.t0.filenumber +
							"," + m.t0.isReverse +
							"," + m.t1.filenumber + 
							"," + m.t1.isReverse +
							"," + m.i0 +
							"," + m.i1 + 
							"," + m.nsteps + 
							"," + m.score;
					else
						s = drivern + "," + trips[i].filenumber + ",false,0,false,0,0,0,0";
			}
			else
			{
				
			}
			
			//Main.results.add(s);
			
			Result res = Result.results[drivern][trips[i].filenumber];
			
			if (res != null && res.score > 6000)
				;
			else
				Result.update(drivern, trips[i].filenumber, (m == null ? 0 : m.score));
			
			//Utils.msg(s);
		}
		*/
	//}
	
	/*
	void calculateStats()
	{
		for (int k = 0; k < Trip.NPARAMETERS; ++k)
			calculateStats(k);
		statsCalculated = true;
	}
	
	void calculateStats(int p)
	{
		float[] z = new float[lastTripIndex];
		
		//--------------------------------------
		
		for (int i = 1; i < lastTripIndex + 1; ++i)
			z[i - 1] = trips[i].getParam(p);
		
		parametersMean[p] = Utils.mean(z);
		parametersSD[p] = Utils.sd(z);
		
		parametersMax[p] = Utils.max(z);
		parametersMin[p] = Utils.min(z);
		
		Utils.msg(" for " + p + " " + parametersMin[p] + " " + parametersMax[p]);
	}
	*/
	
	/*
	static double[] statistic(Trip trip, Driver driver)
	{
		if (!driver.statsCalculated)
			driver.calculateStats();

		double[] r = new double[Trip.NPARAMETERS];
		
		for (int k = 0; k < Trip.NPARAMETERS; ++k)
		{
			r[k] = (trip.getParam(k) - driver.parametersMean[k]) / driver.parametersSD[k];
		}
		
		return r;
		
		
		//return new double[] {
		//					cv((trip.timeFast() - driver.timeFastMean) / driver.timeFastSD),
		//					cv((trip.timeStopped() - driver.timeStoppedMean) / driver.timeStoppedSD),
		//					cv((trip.timeMoving() - driver.timeMovingMean) / driver.timeMovingSD),
		//					cv((trip.maxSpeed() - driver.maxSpeedMean) / driver.maxSpeedSD),
		//					cv((trip.len - driver.lenMean) / driver.lenSD),
		//					cv((trip.xlen - driver.xlenMean) / driver.xlenSD),
		//					cv((trip.aveSpeed() - driver.aveSpeedMean) / driver.aveSpeedSD),
		//				    };
		
	}
	*/
	/*
	static float cvx(float x)
	{
		return Math.abs(x);
		
		if (x > 0)
		{
			if (x < 2) return 0;
			return 1; // x - 1.5f;
		}
		else
		{
			if (x > -2) return 0;
			return 1; // -1.5f - x;
		}
		
	}
	
	void rankByStats()
	{
		// after all stats calculated

		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			Trip t1 = trips[i];
			t1.nearestStatNeighborDistance = 9999999999999f;
			
			for (int j = 1; j < lastTripIndex + 1; ++j)
			{
				if (i == j) continue;
				
				float r = statDistance(trips[i], trips[j]);
				if (r < t1.nearestStatNeighborDistance)
					t1.nearestStatNeighborDistance = r;
			}
			
			t1.bestscore = t1.nearestStatNeighborDistance;
		}
		
		// rank by nearest
		
		Sorter.sort(trips);
		
		for (int i = 1; i < lastTripIndex + 1; ++i)
		{
			Trip t1 = trips[i];
			t1.nearestStatNeighborDistance = i;
			//Utils.msg(i + " " + t1.filenumber + " " + t1.bestscore);
		}
	}
	
	float statDistance(Trip t0, Trip t1)
	{
		float tot = 0;
		
		for (int k = 0; k < Trip.NPARAMETERS; ++k)
		{
			float x = t0.params[k] - t1.params[k];
			x = x / (parametersMax[k] - parametersMin[k]);
			tot += x * x;
		}
		
		return tot;
	}
	*/
}
