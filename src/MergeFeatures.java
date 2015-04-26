import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


public class MergeFeatures 
{
	static int M = 1000000;
	
	static void merge()
	{
		Hashtable samples = new Hashtable();
		
		for (int i = 0; i < Main.drivers.size(); ++i)
		{
			String s = (String) Main.drivers.elementAt(i);
			int driver = Utils.toInt(s);
			for (int j = 1; j < Driver.lastTripIndex + 1; ++j)
			{
				put(samples, driver, j, true, new Sample(driver, j, true));
				put(samples, driver, j, false, new Sample(driver, j, false));
			}
		}
		
		Hashtable a = readFeatureFile("STATastats");
		Hashtable d = readFeatureFile("STATdecel");
		Hashtable l = readFeatureFile("STATlen");
		Hashtable lm = readFeatureFile("STATlenmax");
		
		addToSample(samples, a, 0);
		addToSample(samples, d, 1);
		addToSample(samples, l, 2);
		addToSample(samples, lm, 3);
		
		Hashtable m1 = readFeatureFile("match1");
		Hashtable m2 = readFeatureFile("match2");
		
		addToSample(samples, m1,  m2);
		
		if (true)
		{
			Iterator c = samples.values().iterator();
			while (c.hasNext())
			{
				Utils.msg(c.next());
			}
		}
	}
	
	static void put(Hashtable h, int driver, int trip, boolean valid, Object o)
	{
		int key = driver * M + trip;
		if (!valid) key = -key;
		h.put(key, o);
	}
	
	static Sample get(Hashtable h, int driver, int trip, boolean valid)
	{
		int key = driver * M + trip;
		if (!valid) key = -key;
		return (Sample) h.get(key);
	}
	
	static void addToSample(Hashtable samples, Hashtable h, int k)
	{
		for (int i = 0; i < Main.drivers.size(); ++i)
		{
			String s = (String) Main.drivers.elementAt(i);
			int driver = Utils.toInt(s);
			Vector<Element> v;
			
			v = (Vector<Element>) h.get(driver);
			
			if (v == null)
			{
				continue;
			}
			
			for (int j = 0; j < v.size(); ++j)
			{
				Element e = v.elementAt(j);
				if (e.driver != driver) Utils.msg("err - drivers not the same");
				Sample sample = get(samples, e.driver, e.trip, e.valid);
				if (sample == null)
					Utils.msg("sample is null " + e.driver + " " + e.trip + " " + e.valid);
				else
					sample.updateValue(e.score1, k);
			}
		}
	}
	
	static void addToSample(Hashtable samples, Hashtable m1, Hashtable m2)
	{
		for (int i = 0; i < Main.drivers.size(); ++i)
		{
			String s = (String) Main.drivers.elementAt(i);
			int driver = Utils.toInt(s);
			Vector<Element> v1, v2;
			
			v1 = (Vector<Element>) m1.get(driver);
			v2 = (Vector<Element>) m2.get(driver);
			
			if (v1 == null || v2 == null || v1.size() != v2.size())
			{
				Utils.msg("match table null " + v1 + " " + v2 + " for " + driver);
				continue;
			}
			
			v1 = Element.sortByTrip(v1);
			v2 = Element.sortByTrip(v2);
			
			for (int j = 0; j < v1.size() - 1; j += 2)
			{
				Element e1a = v1.elementAt(j);
				Element e1b = v1.elementAt(j + 1);
				
				if (e1a.driver != e1b.driver) 
					Utils.msg("err - drivers not the same ");
				if (e1a.trip != e1b.trip) 
					Utils.msg("err - trips not the same ");
				if (e1a.valid == e1b.valid) 
					Utils.msg("err - valid not the same ");
				
				Element e2a = v2.elementAt(j);
				Element e2b = v2.elementAt(j + 1);
				
				if (e2a.driver != e2b.driver) 
					Utils.msg("err - drivers not the same ");
				if (e2a.trip != e2b.trip) 
					Utils.msg("err - trips not the same ");
				if (e2a.valid == e2b.valid) 
					Utils.msg("err - valid not the same ");
				
				// now get the true and false in valriables
				
				Element e1t, e1f, e2t, e2f, et;
				
				if (e1a.valid)
				{
					e1t = e1a;
					e1f = e1b;
				}
				else
				{
					e1t = e1b;
					e1f = e1a;
				}
				
				if (e2a.valid)
				{
					e2t = e2a;
					e2f = e2b;
				}
				else
				{
					e2t = e2b;
					e2f = e2a;
				}
				
				if (Double.isInfinite(e1t.score1)) e1t.score1 = 0;
				if (Double.isInfinite(e2t.score1)) e2t.score1 = 0;
				if (Double.isInfinite(e1f.score1)) e1f.score1 = 0;
				if (Double.isInfinite(e2f.score1)) e2f.score1 = 0;
				
				if (Double.isInfinite(e1t.score2)) e1t.score2 = 0;
				if (Double.isInfinite(e2t.score2)) e2t.score2 = 0;
				if (Double.isInfinite(e1f.score2)) e1f.score2 = 0;
				if (Double.isInfinite(e2f.score2)) e2f.score2 = 0;
				
				if (Double.isNaN(e1t.score1)) e1t.score1 = 0;
				if (Double.isNaN(e2t.score1)) e2t.score1 = 0;
				if (Double.isNaN(e1f.score1)) e1f.score1 = 0;
				if (Double.isNaN(e2f.score1)) e2f.score1 = 0;
				
				if (Double.isNaN(e1t.score2)) e1t.score2 = 0;
				if (Double.isNaN(e2t.score2)) e2t.score2 = 0;
				if (Double.isNaN(e1f.score2)) e1f.score2 = 0;
				if (Double.isNaN(e2f.score2)) e2f.score2 = 0;
				
				if (	e1t.score1 != e2t.score1 || 
						e1t.score2 != e2t.score2 ||
						Double.isInfinite(e1t.score1) ||
						Double.isInfinite(e1t.score2) ||
						Double.isInfinite(e2t.score1) ||
						Double.isInfinite(e2t.score2) ||
						Double.isNaN(e1t.score1) ||
						Double.isNaN(e1t.score2) ||
						Double.isNaN(e2t.score1) ||
						Double.isNaN(e2t.score2) ||
						//e1t.score1 != e2t.score1 || 
						//e1t.score2 != e2t.score2 ||
						Double.isInfinite(e1f.score1) ||
						Double.isInfinite(e1f.score2) ||
						Double.isInfinite(e2f.score1) ||
						Double.isInfinite(e2f.score2) ||
						Double.isNaN(e1f.score1) ||
						Double.isNaN(e1f.score2) ||
						Double.isNaN(e2f.score1) ||
						Double.isNaN(e2f.score2) 
						)
					Utils.msg(	"score1 " + e1t.score1 + 
								" score2 " + e1t.score2 + 
								"score1 " + e2t.score1 + 
								" score2 " + e2t.score2 +
								"score1 " + e1f.score1 + 
								" score2 " + e1f.score2 + 
								"score1 " + e2f.score1 + 
								" score2 " + e2f.score2 +
								" for " + driver + " " + j);
				
				et = e1t;
				
				double aveImposterMax = (e1f.score1 + e2f.score1) / 2;
				double aveImposterAve = (e1f.score2 + e2f.score2) / 2;
				
				// update true
				Sample samplet = get(samples, et.driver, et.trip, true);
				if (samplet == null) Utils.msg("true sample is null " + et.driver + " " + et.trip);
				
				samplet.updateValue(et.score1, 4);
				samplet.updateValue(et.score1 / (1 + aveImposterAve), 5);
				samplet.updateValue(et.score1 / (1 + aveImposterMax), 6);
				
				// update false
				
				Sample samplef = get(samples, e1a.driver, e1a.trip, false);
				if (samplef == null) Utils.msg("false sample is null " + e1a.driver + " " + e1a.trip);
				
				samplef.updateValue(aveImposterMax, 4);
				samplef.updateValue(aveImposterMax / (1 + aveImposterAve), 5);
				
				double px = Math.max(e1f.score1, e2f.score1);
				double pn = Math.min(e1f.score1, e2f.score1);
				
				samplef.updateValue(px / (1 + pn), 6);
			}
		}
	}
	
	static Hashtable readFeatureFile(String name)
	{
		Vector<String[]> v = Utils.getData(Main.HomeDir + name + ".txt");
		
		Hashtable h = new Hashtable();
		Vector<Element> set;
		
		for (int i = 0; i < v.size(); ++i)
		{
			String[] s = v.elementAt(i);
			
			int d = Utils.toInt(s[0]);
			int t = Utils.toInt(s[1]);
			
			double score1 = 0;
			double score2 = 0;
			boolean tf = false;
			
			if (s.length == 4)
			{
				score1 = Utils.toFloat(s[2]);
				tf = new Boolean(s[3]).booleanValue();
			}
			else if (s.length == 5)
			{
				score1 = Utils.toFloat(s[2]);
				score2 = Utils.toFloat(s[3]);
				tf = new Boolean(s[4]).booleanValue();
			}
			else
			{
				Utils.msg("error " + s[0] + " " + s[1] + " " + s[2] + " " + s[3]);
			}
			
			Element e = new Element(d, t, score1, score2, tf);
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
			Object x = h.get(d);
			if (x == null)
			{
				Utils.msg("*** null get for " + d + " in " + name);
			}
			else if (((Vector<Element>) x).size() != 400)
			{
				Utils.msg("*** length not 400 for " + d + " in " + name + " size = " + ((Vector<Element>) x).size());	
			}
		}
		
		return h;
	}
}
