import java.util.Vector;

public class Sample implements ISortable
{
	// 0 float astat;
	// 1 float decel;
	// 2 float len;
	// 3 float lenmax;
	// 4 float score;
	// 5 float score compared to imposter ave;
	// 6 float score compared to imposter max;
	
	//
	// interaction table / independence table
	// for high interaction between features
	// reduce the additive value by some amount
	//
	
	static int sortBy;
	
	int driver, trip;
	double[] stats = new double[SampleSet.NFeatures];
	boolean valid;
	
	double totalProbability()
	{
		//double tot = 1;
		//for (int j = 0; j < SampleSet.NFeatures; ++j)
		//	tot *= Math.pow((1 - frequencyForFeature(j)), weights[j]);
		//return 1 - tot;
		return 0;
	}
	
	double frequencyForFeature(int n)
	{
		return 0; // probAtValue(n, stats[n]);
	}
	
	public String toString()
	{
		String s = "";
		for (int i = 0; i < SampleSet.NFeatures; ++i) s += "," + stats[i];
		return driver + "," + trip + "," + valid + s;
	}
	
	Sample(int d, int t, boolean v)
	{
		driver = d;
		trip = t;
		valid = v;
	}
	
	void updateValue(double r, int k)
	{
		stats[k] = r;
	}
	
	public int compareTo(ISortable b)
	{
		if (stats[sortBy] > ((Sample) b).stats[sortBy])
			return 1;
		else if (stats[sortBy] < ((Sample) b).stats[sortBy])
			return -1;
		else
			return 0;
	}
	
	static int Limit = 25;
	static double Far = 9E99;
	
	Vector<Pair> neighbors = null;
	double maxNeighbor = Far;
	
	Object lock = new Object();
	
	void addNeighbor(Sample sample, double distance)
	{
		//synchronized (lock)
		{
			if (distance >= maxNeighbor) return;
			
			if (neighbors == null)
			{
				neighbors = new Vector<Pair>();
				for (int i = 0; i < Limit; ++i)
					neighbors.add(new Pair(null, Far));
				maxNeighbor = Far;
			}
			
			for (int i = 0; i < Limit; ++i)
			{
				if (distance < neighbors.elementAt(i).distance)
				{
					// move i and beyond
					for (int j = Limit - 2; j >= i; --j)
						neighbors.setElementAt(neighbors.elementAt(j), j + 1);
					
					neighbors.setElementAt(new Pair(sample, distance), i);
	
					maxNeighbor = neighbors.elementAt(Limit - 1).distance;
					
					return;
				}
			}
			
			Utils.msg("cannot find place for neighbor " + distance + " " + maxNeighbor);
		}
	}
}
