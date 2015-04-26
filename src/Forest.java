import java.util.Vector;

public class Forest 
{
	int Levels;
	int NumberOfTrees;
	
	double SubsetSize;
	double TreeSuccessThreshold;
	
	int TreeTestSetSize = 3000;
	int ForestTestSetSize = 30000;
	
	Vector<Tree> trees;
	SampleSet all;
	
	Forest()
	{
		all = new SampleSet();
	}
	
	void buildNew()
	{
		trees = new Vector<Tree>();
		
		int n = 0;
		while(n < NumberOfTrees)
		{
			if (makeTree()) n++;
			//Utils.msg(n + " trees made");
		}
		
		testForest();
	}
	
	boolean makeTree()
	{
		Tree tree = new Tree(all.subset(SubsetSize), 0, Levels);
		tree.randomSplit();
		
		double r = testTree(tree);
		
		if (r > TreeSuccessThreshold)
		{
			trees.add(tree);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	double testForest()
	{
		double t = 0;

		for (int i = 0; i < ForestTestSetSize; ++i)
		{
			Sample s = all.samples.elementAt(Utils.r(all.samples.size()));
			if (decision(s) == s.valid) t++;
		}
		
		double r = t / ForestTestSetSize;
		Utils.msg("ntrees = " + NumberOfTrees + " levels = " + Levels + " forest success rate = " + r);
		return r;
	}
	
	boolean decision(Sample s)
	{
		return (test(s) > 0);
	}
	
	double test(Sample s)
	{
		double t = 0;
		double tot = 0;
		
		for (int i = 0; i < trees.size(); ++i)
		{
			double votes = trees.elementAt(i).votes(s);
			t += votes;
			tot += Math.abs(votes);
		}
		
		double r = t / tot;
		return r;
	}

	double testTree(Tree tree)
	{
		double t = 0;
		
		for (int i = 0; i < TreeTestSetSize; ++i)
		{
			Sample s = all.samples.elementAt(Utils.r(all.samples.size()));
			if ((tree.votes(s) > 0) == s.valid) t++;
		}
		
		double r = t / TreeTestSetSize;
		//Utils.msg("tree success rate = " + r);
		return r;
	}
	
	void submission()
	{
		Main.SaveFile = Main.HomeDir + "save" + "_" + Utils.r(1000000) + ".txt";
		if (Utils.save != null) Utils.save.close();
		Utils.save = null;
		
		Utils.msg(Main.SaveFile);
		Utils.save("driver_trip,prob");
		
		Element[] e = new Element[all.samples.size() / 2];
		
		int k = 0;
		for (int i = 0; i < all.samples.size(); ++i)
		{
			Sample s = all.samples.elementAt(i);
			if (s.valid)
				e[k++] = new Element(s.driver, s.trip, test(s));
		}
		
		Element.sortByR(e);
		
		for (int i = 0; i < e.length; ++i)
		{
			double r = ((double) i) / (e.length + 1);
			Utils.save(e[i].driver + "_" + e[i].trip + "," + r);
		}
	}
	
	public static void main(String[] a)
	{
		Forest f = new Forest();
		//f.TreeSuccessThreshold = 0.85;
		f.SubsetSize = 0.03;
		f.Levels = 3;
		f.NumberOfTrees = 200;
		f.buildNew();
		f.submission();
	}
}
