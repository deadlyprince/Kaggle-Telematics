import java.util.Vector;


public class Element implements ISortable
{
	double r;
	String s;
	Object o;
	int driver, trip;
	boolean valid;
	double score1, score2;
	
	Element(int d, int t, double s1, double s2, boolean v)
	{
		driver = d;
		trip = t;
		score1 = s1;
		score2 = s2;
		valid = v;
	}
	
	Element(int dd, int tt, double rr, boolean v)
	{
		driver = dd;
		trip = tt;
		
		r = rr;
		valid = v;
	}
	
	Element(int dd, int tt, double rr)
	{
		driver = dd;
		trip = tt;
		r = rr;
	}
	
	Element(int dd, int tt, Object e)
	{
		driver = dd;
		trip = tt;
		o = e;
	}
	
	Element(float d, String e)
	{
		r = d;
		s = e;
	}
	
	Element(float d, Object e)
	{
		r = d;
		o = e;
	}
	
	public int compareTo(ISortable b)
	{
		if (sortType)
		{
			if (r > ((Element) b).r)
				return 1;
			else if (r < ((Element) b).r)
				return -1;
			else
				return 0;
		}
		else
		{
			if (trip > ((Element) b).trip)
				return 1;
			else if (trip < ((Element) b).trip)
				return -1;
			else
				return 0;
		}
	}
	
	public String toString()
	{
		return s + "-" + r;
	}
	
	static boolean sortType;
	
	static void sortByR(Element[] e)
	{
		sortType = true;
		Sorter.sort(e);
	}
	
	static void sortByTrip(Element[] e)
	{
		sortType = false;
		Sorter.sort(e);
	}
	
	static Vector sortByTrip(Vector e)
	{
		sortType = false;
		return Sorter.sort(e);
	}
}
