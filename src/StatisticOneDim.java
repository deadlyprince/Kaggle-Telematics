
public class StatisticOneDim extends Thread
{
	static int PopulationSize = 500;
	static int TestLoops = 3;
	
	static int Dimension1 = 500;
	
	static float[] popTable;
	static float popTableTotal;
	
	float[] indTable;
	float indTableTotal;
		
	//------------------------------------

	synchronized static void createPopStats(StatisticOneDim as)
	{
		if (popTable != null) return;
		
		popTable = new float[Dimension1];
		
		for (int i = 0; i < PopulationSize; ++i)
		{
			Utils.msg(i);
			
			Driver x = new Driver(0, Main.randomDriverI());
			as.fillDriverStatistics(x);
			
			popTableTotal += as.indTableTotal;
			
			for (int j = 0; j < as.indTable.length; ++j)
				popTable[j] += as.indTable[j];
		}
		
		convertTables(popTableTotal, popTable);
	}
	
	private static void convertTables(float tt, float[] t)
	{
		float row = tt + 1;

		for (int k = 0; k < t.length; ++k)
		{
			if (t[k] < 0) Utils.msg("************ " + t[k]);
			if (t[k] == 0)
				t[k] = Main.NullNumber;
			else
				t[k] = (float) (-Math.log(t[k] / row));
			if (t[k] > 5) t[k] = 5;
		}
	}
	
	private void fillDriverStatisticsAndAdjust(Driver driver)
	{
		fillDriverStatistics(driver);
		
		convertTables(indTableTotal, indTable);

		for (int k = 0; k < indTable.length; ++k)
			if (indTable[k] == Main.NullNumber || popTable[k] == Main.NullNumber)
				indTable[k] = 0;
			else
				indTable[k] -= popTable[k];
	}

	private void fillDriverStatistics(Driver driver)
	{
		indTable = new float[Dimension1];
		indTableTotal = 0;
		
		//
		// for each entry count the number of occurences of possible values
		//
		//
		// then increment the array entry
		// and the total
		//
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			Trip trip = driver.trips[i];
			addTripStatistic(trip);
		}
	}
	
	int statisticForTrip(Trip trip)
	{
		return -1;
	}
	
	private void addTripStatistic(Trip trip)
	{
		int a = statisticForTrip(trip);
		indTable[a]++;
		indTableTotal++;
	}
	
	private float tripMatchedToDriver(Trip trip)
	{
		int a = statisticForTrip(trip);
		return indTable[a];
	}
	
	float[] tripScoresForDriver(Driver driver)
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
	
	//////////////////////////////////
	/*
	public void run(StatisticOneDim as) 
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
		
		//rank(e);
		
		for (int i = 0; i < e.length; ++i)
		{
			Utils.save(e[i].driver + "," + e[i].trip + "," + e[i].r + "," + value);
		}
	}
	
	void rankx(Element[] e)
	{
		Element.sortByR(e);
		for (int x = 0; x < e.length; ++x) 
			e[x].r = ((double) x) / e.length;
	}
	
	static void test(StatisticOneDim as)
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
			StatisticOneDim as = new StatisticOneDim();
			
			Driver driver = new Driver(0, Main.randomDriverI());
			as.fillDriverStatisticsAndAdjust(driver);
	
			// same driver
			float[] rs = as.tripScoresForDriver(driver);
			
			for (int i = 0; i < TestLoops; ++i)
			{
				// random driver
				driver = new Driver(0, Main.randomDriverI());
				float[] rr = as.tripScoresForDriver(driver);
				
				int c = 0, k = 0;
				for (int x = 0; x < rs.length; ++x)
					for (int y = 0; y < rr.length; ++y)
					{
						if (rs[x] < rr[y])
							c++;
						k++;
					}
				
				tot += ((c * 100) / k);
				a++;
			}
		}
		
		return (tot / a);
	}
	
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
}
