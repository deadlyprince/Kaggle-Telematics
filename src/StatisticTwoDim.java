
public class StatisticTwoDim extends Thread
{
	static int PopulationSize = 500;
	static int TestLoops = 3;
	
	static int Dimension1 = 100;
	static int Dimension2 = 100;
	
	static float[][] popTable;
	static float[] popTableRowTotals;
	
	float[][] indTable;
	float[] indTableRowTotals;
		
	//------------------------------------

	synchronized static void createPopStats(StatisticTwoDim as)
	{
		if (popTable != null) return;
		Utils.msg("STARTING CREATE POP STATS");
		
		popTable = new float[Dimension1][Dimension2];
		popTableRowTotals = new float[Dimension1];
		
		for (int i = 0; i < PopulationSize; ++i)
		{
			Utils.msg(i);
			
			Driver x = new Driver(0, Main.randomDriverI());
			as.fillDriverStatistics(x);
			
			for (int j = 0; j < as.indTable.length; ++j)
			{
				popTableRowTotals[j] += as.indTableRowTotals[j];
				for (int k = 0; k < as.indTable[j].length; ++k)
					popTable[j][k] += as.indTable[j][k];
			}
		}
		
		convertTables(popTableRowTotals, popTable);
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
		try
		{
			fillDriverStatistics(driver);
			
			convertTables(indTableRowTotals, indTable);
			
			// now compare
			
			for (int j = 0; j < indTable.length; ++j)
			{
				for (int k = 0; k < indTable[j].length; ++k)
					if (indTable[j][k] == Main.NullNumber || popTable[j][k] == Main.NullNumber)
						indTable[j][k] = 0;
					else
					{
						//Utils.msg(driver.drivern + " " + j + " " + k + ":" + 
						//		indTable[j][k] + " - " + 
						//		popTable[j][k] + " = " +
						//		(indTable[j][k] - popTable[j][k]));
						
						indTable[j][k] -= popTable[j][k];
					}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Utils.msg(e + " in FDSAA");
		}
	}

	private void fillDriverStatistics(Driver driver)
	{
		indTable = new float[Dimension1][Dimension2];
		indTableRowTotals = new float[Dimension1];
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			if (trip == null) 
			{
				Utils.msg("fds trip " + i + " is null in drive " + driver.drivern);
				continue;
			}
			addTripStatistic(trip);
		}
	}
	
	//int maxSpeed = (int) (trip.maxSpeed() / 5);
	
	IPair statisticForTrip(Trip trip)
	{
		return null;
	}
	
	private void addTripStatistic(Trip trip)
	{
		IPair p = statisticForTrip(trip);
		
		indTable[p.x][p.y]++;
		indTableRowTotals[p.x]++;
	}
	
	private float tripMatchedToDriver(Trip trip)
	{
		IPair p = statisticForTrip(trip);
		
		return indTable[p.x][p.y];
	}
	/*
	float[] tripScoresForDriverx(Driver driver)
	{
		float[] all = new float[Driver.lastTripIndex];
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			float r = tripMatchedToDriver(trip);
			all[i - 1] = r;
			//Utils.msg("trip " + i + " " + r);
		}
		return all;
	}
	*/
	
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
		try
		{
		Element[] e = new Element[Driver.lastTripIndex];
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			
			float r = 0;
			if (trip == null) 
			{
				Utils.msg("mast trip " + i + " is null in drive " + driver.drivern);
			}
			else
			{
				r = tripMatchedToDriver(trip);
			}
			
			e[i - 1] = new Element(driver.drivern, trip.filenumber, r);			
		}
	
		for (int i = 0; i < e.length; ++i)
		{
			Utils.save(e[i].driver + "," + e[i].trip + "," + e[i].r + "," + value);
		}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Utils.msg(e + " in MAST");
		}
	}
	
	void rankx(Element[] e)
	{
		Element.sortByR(e);
		for (int x = 0; x < e.length; ++x) 
			e[x].r = ((double) x) / e.length;
	}
	
	//////////////////////////////////
	/*
	public void run(StatisticTwoDim as) 
	{
		createPopStats(as);
		
		while (true)
		{
			String s = Main.getNextDriver();
			
			if (s == null) break;
			
			int n = Utils.toInt(s);
			
			Driver driver = new Driver(0, n);
			fillDriverStatisticsAndAdjust(driver);
			
			float[] rs = tripScoresForDriver(driver);
			
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
	static void test(StatisticTwoDim as)
	{
		createPopStats(as);
		
		for (int j = 1; j < 6; ++j)
		{	
			int r = oneTest();
			Utils.msg("result=" + r);
		}
	}
	
	private static int oneTest()
	{
		int tot = 0;
		int a = 0;
		for (int n = 0; n < TestLoops; ++n)
		{
			StatisticTwoDim as = new StatisticTwoDim();
			
			Driver driver = new Driver(0, Main.randomDriverI());
			as.fillDriverStatisticsAndAdjust(driver);
			
			// same driver
			float[] rs = as.tripScoresForDriver(driver);
			
			for (int i = 0; i < TestLoops; ++i)
			{
				// random driver
				driver = new Driver(0, Main.randomDriverI());
				float[] r = as.tripScoresForDriver(driver);
				
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
	
	public void run()
	{
		try
		{
			Utils.msg("start create population");
			createPopStats(this);
			Utils.msg("complete create population");
			
			while (true)
			{
				String s = Main.getNextDriver();
				if (s == null) break;
				
				//Utils.msg("analyze driver " + s);
				
				oneDriver(new Driver(0, Utils.toInt(s)));
			}
		}
		catch (Exception e)
		{
			Utils.msg("exception caught " + e + " === " + e.getMessage());
			e.printStackTrace();
		}
	}
}
