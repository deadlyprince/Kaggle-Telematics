
public class Neighbor extends Thread
{
	int p;
	int first, last;
	static SampleSet sampleSet = null;
	Sample[] mysamples;
	
	Neighbor(int i, int f, int l)
	{
		p = i;
		first = f;
		last = l;
		Utils.msg(i + " " + first + " " + last);
		mysamples = new Sample[sampleSet.samples.size()];
		for (int j = 0; j < sampleSet.samples.size(); ++j)
			mysamples[j] = sampleSet.samples.elementAt(j);
	}
	
	public void run()
	{	
		for (int i = first; i <= last; ++i)
		{
			//Utils.msg("get distances for sample " + i + " on " + p);
			doSample(mysamples[i], i);
		}
	}
	
	void doSample(Sample s1, int n)
	{
		for (int j = 0; j < mysamples.length; ++j)
		{
			if (n == j) continue;
			
			Sample s2 = mysamples[j];
			double s = distance(s1, s2);
			
			// set neighbor for s1
			s1.addNeighbor(s2, s);
			
			// set neighbor for s2
			//s2.addNeighbor(s1, s);
		}
	}
	
	static double distance(Sample s1, Sample s2)
	{
		double t = 0;
		for (int i = 0; i < SampleSet.NFeatures; ++i)
		{
			double r = s1.stats[i] - s2.stats[i];
			t += r * r;
		}
		return Math.sqrt(t);
	}
}
