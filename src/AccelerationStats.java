
public class AccelerationStats extends Thread
{
	static int PopulationSize = 500;
	static int AccelerationTestLoops = 3;
	
	static int g = 1;
	static int MaxSpeed = 80;
	static int MaxIncrease = 15;
	static int ahead = 1;
	
	static float[][] popSpeedTable;
	static float[] popSpeedTableRowTotals;
	
	float[][] speedTable;
	float[] speedTableRowTotals;

	/*
	void createDrivingStatistics1()
	{
		
		createDrivingStatistics();
				
		speedStatsScores = new float[Driver.lastTripIndex + 1];
		
		float min = 99999;
		float max = -99999;
		
		//Utils.msg("start scores");
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			speedStatsScores[i] = (Driver.tripChangeSpeedProbMatchedToDriver(trips[i], this) / trips[i].xlen);
			trips[i].bestscore = speedStatsScores[i];
			
			if (speedStatsScores[i] < min) min = speedStatsScores[i];
			if (speedStatsScores[i] > max) max = speedStatsScores[i];
		}
		
		saveAllStats(this, max);
		
	}
	
	synchronized static void saveAllStatsxxx(Driver driver, float max)
	{
		Sorter.sort(driver.trips);
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			float x = driver.trips[i].bestscore / (max + 0.01f);
			x = 1 - x;
			Utils.save(driver.drivern + "_" + driver.trips[i].filenumber + "," + x);
		}
	}
	*/
		
	//------------------------------------

	synchronized static void createPopStats(AccelerationStats as)
	{
		if (popSpeedTable != null) return;
		
		popSpeedTable = new float[g * MaxSpeed][g * MaxIncrease * 2];;
		popSpeedTableRowTotals = new float[g * MaxSpeed];
		
		for (int i = 0; i < PopulationSize; ++i)
		{
			Utils.msg(i);
			
			Driver x = new Driver(0, Main.randomDriverI());
			as.fillDriverStatistics(x);
			
			for (int j = 0; j < as.speedTable.length; ++j)
			{
				popSpeedTableRowTotals[j] += as.speedTableRowTotals[j];
				
				for (int k = 0; k < as.speedTable[j].length; ++k)
					popSpeedTable[j][k] += as.speedTable[j][k];
			}
		}
		
		convertTables(popSpeedTableRowTotals, popSpeedTable);
	}
	
	private static void convertTables(float[] tt, float[][] t)
	{
		for (int j = 0; j < t.length; ++j)
		{
			float row = tt[j] + 1;
			
			//String s = j + " " ;
			for (int k = 0; k < t[j].length; ++k)
			{
				if (t[j][k] < 0) Utils.msg("************ " + t[j][k]);
				if (t[j][k] == 0)
					t[j][k] = Main.NullNumber;
				else
					t[j][k] = (float) (-Math.log(t[j][k] / row));
				if (t[j][k] > 5) t[j][k] = 5;
			}
			//Utils.msg(s);
		}
	}
	
	private void fillDriverStatisticsAndAdjust(Driver driver)
	{
		fillDriverStatistics(driver);
		
		convertTables(speedTableRowTotals, speedTable);
		
		// now compare
		
		for (int j = 0; j < speedTable.length; ++j)
		{
			for (int k = 0; k < speedTable[j].length; ++k)
				if (speedTable[j][k] == Main.NullNumber || popSpeedTable[j][k] == Main.NullNumber)
					speedTable[j][k] = 0;
				else
					speedTable[j][k] -= popSpeedTable[j][k];
			/*
			String s = j + " " ;
			for (int k = 0; k < speedTable[j].length; ++k)
				if (speedTable[j][k] == Main.NullNumber)
					s += "--- ";
				else
					s += ((int) (speedTable[j][k] * 1000)) + " ";
			Utils.msg(s);
			*/
		}
	}

	private void fillDriverStatistics(Driver driver)
	{
		speedTable = new float[g * MaxSpeed][g * MaxIncrease * 2];
		speedTableRowTotals = new float[g * MaxSpeed];
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			for (int t = ahead + 1; t < trip.x.length; ++t)
			{
				int k = t - ahead;
				float speed1 = 
						Main.MPH * 
						Utils.ds(trip.x[k - 1], trip.y[k - 1], trip.x[k], trip.y[k]);
				
				k = t;
				float speed2 = 
						Main.MPH * 
						Utils.ds(trip.x[k - 1], trip.y[k - 1], trip.x[k], trip.y[k]);
				
				if (speed1 >= MaxSpeed - 0.1f) speed1 = MaxSpeed - 0.1f;
				if (speed2 >= MaxSpeed - 0.1f) speed2 = MaxSpeed - 0.1f;

				int ix = (int) (speed1 * g);
				int dx = (int) (((speed2 - speed1) + MaxIncrease) * g);
				
				if (dx < 0)
				{
					//Utils.msg(dx);
					dx = 0;
				}
				if (dx >= g * MaxIncrease * 2)
				{
					//Utils.msg(dx);
					dx = g * MaxIncrease * 2 - 1;
				}
				
				speedTable[ix][dx]++;
				speedTableRowTotals[ix]++;
			}
		}
	}
	
	/*
	float[] allTripsByDriverx(Driver driver)
	{
		float[] all = new float[Driver.lastTripIndex];
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			float r = tripAccelerationMatchedToDriver(trip);
			all[i - 1] = r;
			//Utils.msg("trip " + i + " " + r);
		}
		return all;
	}
	*/
	
	private float tripMatchedToDriver(Trip trip)
	{
		float tot = 0;
		for (int t = ahead + 1; t < trip.x.length; ++t)
			tot += changeSpeedLogProbAt(trip, t);
		return tot;
	}
	
	private float changeSpeedLogProbAt(Trip trip, int t)
	{
		int k = t - ahead;
		
		if (k < 1)
			return 0;
		
		float speed1 = 
				Main.MPH * 
				Utils.ds(trip.x[k - 1], trip.y[k - 1], trip.x[k], trip.y[k]);
		
		k = t;
		float speed2 = 
				Main.MPH * 
				Utils.ds(trip.x[k - 1], trip.y[k - 1], trip.x[k], trip.y[k]);
		
		return changeSpeedLogProb(speed1, speed2);
	}
	
	private float changeSpeedLogProb(float speed1, float speed2)
	{
		if (speed1 >= MaxSpeed - 0.1f) speed1 = MaxSpeed - 0.1f;
		if (speed2 >= MaxSpeed - 0.1f) speed2 = MaxSpeed - 0.1f;

		int ix = (int) (speed1 * g);
		int dx = (int) (((speed2 - speed1) + MaxIncrease) * g);
		
		if (dx < 0) dx = 0;
		if (dx >= g * MaxIncrease * 2) dx = g * MaxIncrease * 2 - 1;

		return speedTable[ix][dx];
		
		//int cell = 0;//speedTable[ix][dx] + 1;
		//int row = 1;//speedTableRowTotals[ix] + 1;

		//float r = ((float) cell) / row;
		
		//if (speed1 > 15) return 0;
		
		//return (float) (-Math.log(r));
		
		//Utils.msg("row total = " + driver.speedTableRowTotals[ix] + " " + driver.drivern);
	}
	
	
	//////////////////////////////////
	
	public void run()
	{
		createPopStats(this);
		
		while (true)
		{
			String s = Main.getNextDriver();
			if (s == null) break;
			
			Utils.msg("analyze driver " + s);
			oneDriver(new Driver(0, Utils.toInt(s)));
		}
	}
	
	void oneDriver(Driver driver)
	{
		// same driver
		
		fillDriverStatisticsAndAdjust(driver);
		matchAndSaveTrips(driver, true);
		
		// random driver
		
		fillDriverStatisticsAndAdjust(new Driver(0, Main.randomDriverI()));
		matchAndSaveTrips(driver, false);
	}
	
	void matchAndSaveTrips(Driver driver, boolean value)
	{
		Element[] e = new Element[Driver.lastTripIndex];
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			float r = tripMatchedToDriver(trip);
			e[i - 1] = new Element(driver.drivern, trip.filenumber, r);			
		}
		
		for (int i = 0; i < e.length; ++i)
		{
			Utils.save(e[i].driver + "," + e[i].trip + "," + e[i].r + "," + value);
		}
	}
	
	/*
	public void run() //accelerationStats()
	{
		createPopStats();
		
		while (true)
		{
			String s = Main.getNextDriver();
			
			if (s == null) break;
			
			int n = Utils.toInt(s);
			
			//AccelerationStats as = new AccelerationStats();
			
			Driver driver = new Driver(0, n);
			createDriverStats(driver);
			
			float[] rs = allTripsByDriver(driver);
			
			// sort and reassign score
			
			Element[] e = new Element[rs.length];
			
			for (int x = 0; x < rs.length; ++x) 
				e[x] = new Element(rs[x], driver.trips[x + 1]);
			
			Element.sortByR(e);
			
			for (int x = 0; x < rs.length; ++x) 
				e[x].r = ((double) x) / e.length;
			
			// save
			
			for (int x = 0; x < rs.length; ++x)
			{
				String str = n + "_" + ((Trip) e[x].o).filenumber + "," + e[x].r;
				Utils.save(str);
				Utils.msg(str);
			}
		}
	}
	*/
	/*
	static void accelerationTest()
	{
		AccelerationStats.createPopStats();
		
	
		for (int j = 1; j < 6; ++j)
		{
			AccelerationStats.ahead = 1;
			//AccelerationStats.interval = j;
			
			int r = acceleration();
			Utils.msg("interval=" + j + " result=" + r);
			
			//r = acceleration();
			//Utils.msg("ahead=" + i + " interval=" + j + " result=" + r);
		}
	}
	
	private static int acceleration()
	{
		int tot = 0;
		int a = 0;
		for (int n = 0; n < AccelerationTestLoops; ++n)
		{
			AccelerationStats as = new AccelerationStats();
			Driver driver = new Driver(0, Main.randomDriverI());
			as.createDriverStats(driver);
			
			float[] rs;
			
			// same driver
			rs = as.allTripsByDriver(driver);
			
			for (int i = 0; i < AccelerationTestLoops; ++i)
			{
				// random driver
				driver = new Driver(0, Main.randomDriverI());
				float[] r = as.allTripsByDriver(driver);
				int c = 0, k = 0;
				for (int x = 0; x < rs.length; ++x)
					for (int y = 0; y < r.length; ++y)
					{
						if (rs[x] < r[y])
							c++;
						k++;
					}
				tot += ((c * 100) / k);
				a++;
			}
		}
		
		return (tot / a);
	}
	*/
}
