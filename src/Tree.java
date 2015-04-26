import java.util.Vector;

public class Tree 
{
	SampleSet sampleSet;
	Tree lower;
	Tree higher;
	Split theSplit;
	int level;
	int limit;
	double probability;
	int count;
	
	Tree(Vector<Sample> s, int l, int lt)
	{
		sampleSet = new SampleSet(s);
		level = l;
		limit = lt;
		
		//Utils.msg("tree created - sample count = " + sampleSet.samples.size() + " at level " + level);
	}
	
	void forceSplit(int fld, int index)
	{
		split(new Split(0, fld, index, sampleSet.samples.elementAt(index).stats[fld]));
	}
	
	void randomSplit()
	{
		int f = Utils.r(SampleSet.NFeatures);
		Split s = findBestSplit(f);
		
		if (s == null)
			leaf();
		else
			split(s);
	}
	
	void split(Split split)
	{
		int k = split.index;
		
		Vector<Sample> rs = new Vector<Sample>();
		Vector<Sample> ls = new Vector<Sample>();
		
		for (int i = 0; i < sampleSet.samples.size(); ++i)
		{
			if (i <= k) 
				rs.add(sampleSet.samples.elementAt(i));
			else
				ls.add(sampleSet.samples.elementAt(i));
		}
		
		lower = new Tree(rs, level + 1, limit);
		higher = new Tree(ls, level + 1, limit);
		
		if (level < limit)
		{
			lower.randomSplit();
			higher.randomSplit();
		}
		else
		{
			lower.leaf();
			higher.leaf();
		}
		
		theSplit = split;
		sampleSet = null;
	}
	
	void leaf()
	{
		count = sampleSet.samples.size();
		probability = ((double) (sampleSet.countValid())) / count;
		sampleSet = null;
	}
	
	Split findBestSplit(int fld)
	{
		sampleSet.sortBy(fld);
		
		Vector<Sample> s = sampleSet.samples;
		
		int n = s.size();
		int tt = sampleSet.countValid();
		
		double t = 0;
		Split bestSplit = null;
		
		//
		// splitting at i means split into
		// { ... i-1, i } and { i+1, i+2 ... }
		//
		
		for (int i = 0; i < n - 1; ++i)
		{
			if (s.elementAt(i).valid) t++;
			
			double r0 = t / (i + 1);
			double r1 = (tt - t) / (n - (i + 1));
			
			double e0 = term(r0) + term(1 - r0);
			double e1 = term(r1) + term(1 - r1);
			
			// calculate info gain
			
			double ig;
			
			ig = (((double) (i + 1)) / n) * e0;
			ig += (((double) (n - (i + 1))) / n) * e1;
			
			if (bestSplit == null || ig > bestSplit.infoGain)
				bestSplit = new Split(ig, fld, i, s.elementAt(i).stats[fld]);
			
			//if (fld == 4) Utils.msg(ig + " " + i + " " + s.elementAt(i).stats[fld] + " " + r0 + " " + r1);
		}
		
		//if (bestSplit == null)
		//	Utils.msg("best split is null, n = " + n);
		
		return bestSplit;
	}
	
	static double NoInfo = -Math.log(0.5);
	
	double votes(Sample sample)
	{
		double p = validProbability(sample);
		
		if (count < 2) count = 2;
		if (p == 0) p = 1.0 / count;
		if (p == 1) p = (count - 1.0) / count;
		
		double r;
		
		if (p > 0.5)
			r = -Math.log(1 - p) - NoInfo;
		else
			r = Math.log(p) + NoInfo;
		
		//Utils.msg(p + " --> " + r + " " + sample.valid);
		
		return r;
	}
	
	private double validProbability(Sample sample)
	{
		if (theSplit == null)
			return probability;
		else if (sample.stats[theSplit.field] <= theSplit.value)
			return lower.validProbability(sample);
		else
			return higher.validProbability(sample);
	}
	
	static double ln(double r)
	{
		if (r == 0) return 0;
		return Math.log(r);
	}
	
	static double term(double r)
	{
		return r * ln(r);
	}
}
