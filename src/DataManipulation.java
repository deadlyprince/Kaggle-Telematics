import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

public class DataManipulation extends Thread
{
	static float[][] runTable = null;
	
	static void merge()
	{
		//Element[] b = readBaseResults();
		//Element[] c = readAccStats();
		//mergeSave(c, b);
	}
	
	static void init()
	{
		//runTable = Submissions.readSubmission("ordered.txt", 100000);
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
				
				Utils.msg("begin " + name);

				compareMembersAndNonmembers(Utils.toInt(name));
				
				Utils.msg("finish " + name);
			}
		}
		catch (Exception e)
		{
			//Utils.msg("\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n***\n" + e);
			e.printStackTrace();
		}
	}

	void compareMembersAndNonmembers(int n)
	{
		Driver driver1 = new Driver(0, n);
		Driver driver2 = new Driver(0, Utils.toInt(Main.randomDriver()));
		
		for (int k = 0; k < 2; ++k)
		{
			Driver matcher;
			if (k == 0)
				matcher = driver1;
			else
				matcher = driver2;
			for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
			{
				Trip t = driver1.trips[i];
				if (runTable != null)
					if (runTable[driver1.drivern][t.filenumber] == Main.NullNumber)
						continue;
				FPair f = matcher.match(t);
				Utils.save(n + "," + t.filenumber + "," + f.x + "," + f.y + "," + (k == 0 ? "true" : "false"));
				Utils.msg(n + "," + t.filenumber + "," + f.x + "," + f.y + "," + (k == 0 ? "true" : "false"));
			}
		}
	}
	
	static Element[] readBaseResultsx()
	{
		if (!new File(Main.HomeDir + "base.txt").exists()) return null;
		Vector<String[]> v = Utils.getData(Main.HomeDir + "base.txt");
		
		Element[] elements = null;
		Element[] all = new Element[v.size()];
		
		int lastDriver = -1;
		int c = 0;
		int a = 0;
		
		for (int i = 0; i <= v.size(); ++i)
		{
			int driver = -1;
			int t0 = -1;
			float score = -1;
			
			if (i < v.size())
			{
				String[] s = v.elementAt(i);
				
				driver = Utils.toInt(s[0]);
				t0 = Utils.toInt(s[1]);
				score = Utils.toFloat(s[8]);
			}
			
			if (i == v.size() || driver != lastDriver)
			{
				if (lastDriver > 0)
				{
					// output these 200
					Utils.msg(c + " " + lastDriver + " " + driver);
					
					if (c != 200)
					{
						Element[] x = new Element[c];
						for (int j = 0; j < c; ++j)
							x[j] = elements[j];
						elements = x;
					}
					
					Element.sortByR(elements);
					
					for (int j = 0; j < elements.length; ++j)
					{
						Element e = elements[j];
						e.r = ((double) j) / elements.length;
						all[a++] = e;
					}
				}
				
				if (i < v.size())
				{
					lastDriver = driver;
					elements = new Element[200];
					c = 0;
				}
			}

			if (i < v.size())
			{
				//if (score == 0) score = 3000;
				elements[c++] = new Element(score, driver + "_" + t0);
			}
		}
		
		Utils.msg("------------- Read base.txt " + all.length);
		return all;
	}
	
	static Element[] readAccStats()
	{
		if (!new File(Main.HomeDir + "accelerationStatisticsRanked80823.txt").exists()) return null;
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + "accelerationStatisticsRanked80823.txt");
		Element[] elements = new Element[v.size()];
		
		for (int i = 0; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			float score = 1 - Utils.toFloat(s[1]);
			elements[i] = new Element(score, s[0]);
		}
		
		Element.sortByR(elements);
		for (int i = 0; i < elements.length; ++i)
		{
			Element e = elements[i];
			e.r = ((double) i) / elements.length;
		}
		
		Utils.msg("------------- Read acceleration statictics " + elements.length);
		return elements;
	}
	
	static void mergeSavexxx(Element[] acc, Element[] base)
	{
		//double accWeight = 0.2;

		//Element.sortByS(acc);
		//Element.sortByS(base);
		
		Utils.save("driver_trip,prob");
		
		for (int i = 0; i < acc.length; ++i)
		{
			if (!acc[i].s.equals(base[i].s)) Utils.msg(acc[i] + " ========== " + base[i]);
			
			//double r = (base[i].r + accWeight * acc[i].r) / (1 + accWeight);
			
			double r = (Math.pow(base[i].r, 0.25) * 1.5 + acc[i].r) / 2.5;
			
			
			
			
			Utils.save(acc[i].s + "," + r);	
		}
	}
	
	/*
	 
	static Hashtable readMatchx(int n)
	{
		Vector<String[]> v = Utils.getData(Main.HomeDir + "match" + n + ".txt");
		
		Hashtable h = new Hashtable();
		Vector<Element> set;
		
		for (int i = 0; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			int d = Utils.toInt(s[0]);
			int t = Utils.toInt(s[1]);
			float score1 = Utils.toFloat(s[2]);
			float score2 = Utils.toFloat(s[3]);
			Element e = new Element(d, t, s);
			Object x = h.get(d);
			if (x == null)
			{
				set = new Vector<Element>();
				h.put(d, set);
				//Utils.msg("new " + d);
			}
			else
			{
				set = (Vector<Element>) x;
				//Utils.msg("same " + d);
			}
			//if (i % 1000 == 0) Utils.msg(i);
			set.add(e);
		}
		
		for (int i = 0; i < Main.drivers.size(); ++i)
		{
			String s = (String) Main.drivers.elementAt(i);
			int d = Utils.toInt(s);
			//Utils.msg("search " + d);
			Object x = h.get(d);
			if (x == null)
			{
				Utils.msg("null get for " + d);
			}
		}
		
		return h;
		
	}
	
	*/
	
	
	/*
	void readBaseResults()
	{
		if (!new File(Main.HomeDir + "base.txt").exists()) return;
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + "base.txt");
		
		boolean start = false;
		for (int i = 0; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			if (start)
			{
				Result r = new Result(s);
				//Utils.msg(r.driver + " " + r.t0 + " " + r.score);
			}
			
			if (s[0].indexOf("driver_trip") >= 0) start = true;
		}
		
		//Utils.msg("------------- Read last results");
		
		
		//Result.printDeltaResults();
		*/
		/*
		if (true) return;
		
		//int k = 0;
		//int o1 = 0, o2 = 0;
		
		for (int i = 1; i < Result.ND; ++i)
		{
			if (Result.results[i][1] == null) continue;
	
			int k = 0;
			int o1 = 0, o2 = 0;
			for (int t = 1; t < Driver.lastTripIndex + 1; ++t)
			{
				//if (Result.results[i][t].newScaledScore == 0) 
				//	k++;
				//else if (Result.results[i][t].scaledScore < 0.4)
				//	o1++;
				//else if (Result.results[i][t].scaledScore < 0.5)
				//	o2++;
			}
			
			//Utils.msg("driver:" + Result.results[i][1].driver + "--" + k + " null, " + o1 + ", " + o2);
		}
		
		//Utils.msg("--" + k + " null, " + o1 + ", " + o2);
		*/
	//}
	/*
	void readSub6x()
	{
		if (!new File(Main.HomeDir + "submission6.txt").exists()) return;
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + "submission6.txt");
	
		int[] drivers = new int[4000];
		for (int i = 0; i < 4000; ++i) drivers[i] = -1;
		
		for (int i = 1; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int k = s[0].indexOf("_");
			int driver = Utils.toInt(s[0].substring(0, k));
			drivers[driver] = 1;
			//int trip = Utils.toInt(s[0].substring(k + 1, s[0].length()));
			//
			//float r = Utils.toFloat(s[1]);
			//Result.results[driver][trip].oldScaledScore = r;
		}
	}
	*/
	/*
	
	void buildDataSet()
	{
		Display display = null;
		while (true)
		{
			Match m = getNegativeAndPositiveExample();
			if (display == null)
				display = new Display(null);
			display.setMatch(m);
		}
	}
	
	Match getNegativeAndPositiveExample()
	{
		// pick a trip at random and test that its score is high
		Result r = null;
		int dr2 = -1, dr1 = -1, tr1 = -1;
		while (true)
		{
			dr1 = (int) (Result.ND * Math.random());
			if (Result.results[dr1][1] == null) continue;
			tr1 = (int) (1 + (Result.D1 - 1) * Math.random());
			
			if (Result.results[dr1][tr1].score > 5000 && Result.results[dr1][tr1].score < 7500)
			{
				r = Result.results[dr1][tr1];
				//Utils.msg("Test trip: " + r);
				break;
			}
		}
		
		dr2 = Utils.toInt(Main.randomDriver());
		
		//dr2 = dr1;
		
		Driver driver1 = new Driver(0, dr1);
		Driver driver2 = new Driver(0, dr2);
		
		// find a match
		
		driver2.stepLimit = -1;
		Match mn = driver2.match(driver1.trips[tr1]);
		Utils.msg(dr1 + "," + dr2 + "," + mn + ",false");
		
		driver1.stepLimit = mn.nsteps + 1;
		Match mp = driver1.match(driver1.trips[tr1]);
		Utils.msg(dr1 + "," + dr1 + "," + mp + ",true");
		
		return mp;
	}
	*/
	

	
	/*
	void special1()
	{
		if (!new File(Main.HomeDir + "submission7.txt").exists()) return;
		
		Vector<String[]> v = Utils.getData(Main.HomeDir + "submission7.txt");
		Vector<String[]> vs = Utils.getData(Main.HomeDir + "update.txt");
		
		Utils.save("driver_trip,prob");
		
		float[][] results = new float[Main.ND][Main.D1];
		
		for (int j = 0; j < vs.size(); ++j)
		{
			String[] saves = vs.elementAt(j);
			int savedriver = Utils.toInt(saves[0]);
			int savetrip = Utils.toInt(saves[1]);
			float s = Utils.toFloat(saves[2]);
			//if (s > 17500)
			results[savedriver][savetrip] = s;
			//Utils.msg(savedriver + " " + savetrip);
			//if (driver == savedriver && trip == savetrip)
			//{
			//	found = true;
			//	break;
			//}
		}
		
		for (int i = 1; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int k = s[0].indexOf("_");
			int driver = Utils.toInt(s[0].substring(0, k));
			int trip = Utils.toInt(s[0].substring(k + 1, s[0].length()));
			
			if (i % 100 == 0) Utils.msg(((float) i) / v.size());
			
			if (results[driver][trip] > 17500)
			{
				Utils.save(s[0] + ",0.5");
			}
			else
			{
				Utils.save(s[0] + "," + s[1]);
			}
		}
	}
	*/
	
	/*
	//Element[] elements = new Element[547200];
	
	void combine()
	{
		float[][] best = readSubmission("95076.txt", -1);
		Utils.save("driver_trip,prob");
		int c = 0;
		for (int i = 1; i < Main.ND; ++i)
		{
			if (best[i][1] == Main.NullNumber) continue;
			for (int j = 1; j < Main.D1; ++j)
			{
				float b = best[i][j];
				//if (b < 0) b = 0;
				//if (b > 0.99999f) b = 0.99999f;
				elements[c] = new Element(b, i + "_" + j);
				c++;
			}
		}
		
		
		
		//float[][] ns = readSubmission("newtry95076.txt");
		//Utils.save("driver_trip,prob");
		//int c = 0;
		//for (int i = 1; i < ND; ++i)
		//{
		//	if (best[i][1] == NullNumber) continue;
		//	for (int j = 1; j < D1; ++j)
		//	{
		//		float b = best[i][j];
		//		//if (b < 0) b = 0;
		//		//if (b > 0.99999f) b = 0.99999f;
		//		if (ns[i][j] != b) Utils.msg("old: " + b + " new: " + ns[i][j]);
		//		//elements[c] = new Element(b, i + "_" + j);
		//		//c++;
		//		//Utils.save(i + "_" + j + "," + b);
		//	}
		//}

		
		
		Sorter.sort(elements);
		for (int i = 0; i < 547200; ++i)
		{
			Element e = elements[i];
			double r = ((double) i) / 547200;
			Utils.save(e.s + "," + r);
		}
	}
	
	void combine1()
	{
		float[][] best = readSubmission("submission21.txt", -1);
		float[][] stat = readSubmission("statscores.txt", -1);
		
		int c = 0;
		
		for (int i = 1; i < Main.ND; ++i)
		{
			if (best[i][1] == Main.NullNumber) continue;
			for (int j = 1; j < Main.D1; ++j)
			{
				float b = best[i][j];
				float s = stat[i][j];
				float r = (7 * b - s) / 6;
				if (r < 0) r = 0;
				if (r >= 0.99999) r = 0.99999f;
				elements[c] = new Element(r, i + "_" + j);
				c++;
			}
		}
		
		//Sorter.sort(elements);
		
		Utils.save("driver_trip,prob");
		
		for (int i = 0; i < 547200; ++i)
		{
			Element e = elements[i];
			//double r = ((double) i) / 547200;
			Utils.save(e.s + "," + e.r);
		}
	}
	*/
}
