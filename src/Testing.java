
public class Testing 
{
	static int SortNumber;
	
	class Thing implements ISortable
	{
		int id;
		boolean actual;
		float[] probs = new float[3];
		
		Thing(int x, boolean a, float p, int k)
		{
			id = x;
			actual = a;
			probs[k] = p;
		}
		
		public int compareTo(ISortable b)
		{
			float t, o;
			t = probs[SortNumber];
			o = ((Thing) b).probs[SortNumber];
			
			if (t > o)
				return 1;
			else if (t < o)
				return -1;
			else
				return 0;
		}
	}
	
	public static void main(String[] a)
	{
		new Testing();
	}
	
	Testing()
	{
		// 1.5
		for (float r = 1f; r < 2f; r += 0.05f)
		{
			Utils.msg(r + " " + test10(r));
		}
	}
	
	float test10(float r)
	{
		float t = 0;
		for (int i = 0; i < 10; ++i)
			t += testOne(r);
		return t / 10;
	}
	
	float testOne(float r)
	{
		Thing[] t1 = makeSet();
		
		//score(t1, 0.345f, 0);
		
		score(t1, 0.388f, 0);
		score(t1, 0.388f, 1);
		
		//Utils.msg(test(t1, 0));
		//Utils.msg(test(t1, 1));
		
		// prob[1] + 1.5 * prob[0] ^ 0.25 for 80% and 90%
		
		for (int i = 0; i < 10000; ++i)
		{
			t1[i].probs[2] = (float) Math.pow(t1[i].probs[1], 1) + (float) Math.pow(t1[i].probs[0], 1);
		}
		
		return test(t1, 2);
	}
	
	void score(Thing[] things, float r, int k)
	{
		for (int i = 0; i < 10000; ++i)
		{
			float p;
			if (things[i].actual)
				p = (float) ((1 - r) - Math.random() / 2);
			else
				p = (float) (r + Math.random() / 2);
			things[i].probs[k] = p;
		}
	}
	
	Thing[] makeSet()
	{
		Thing[] things = new Thing[10000];
	
		for (int i = 0; i < 10000; ++i)
		{
			boolean a = (Math.random() > 0.5);
			things[i] = new Thing(i, a, 0, 0);
		}
		
		return things;
	}
	
	float test(Thing[] things, int k)
	{
		SortNumber = k;
		
		Sorter.sort(things);
		
		int ct = 0;
		int n = 0;
		
		for (int i = 0; i < 10000; ++i)
			for (int j = i + 1; j < 10000; ++j)
			{
				if (things[i].actual != things[j].actual)
				{
					if (things[i].actual) ct++;
					n++;
				}
			}
		
		return ((((float) ct) * 100) / n);
	}
}









/*

for (int i = 0; i < 10000; ++i)
			for (int j = 0; j < 10000; ++j)
			{
				if (table[i][j] == 0)
				{
					float k;
					if (i > j)
						k = 0.80f;
					else
						k = 0.20f;
					if (Math.random() < k)
					{
						int z = 0;
						if (things[i].actual && !things[j].actual) z = 1;
						if (!things[i].actual && things[j].actual) z = -1;
						if (z != 0)
						{
							table[i][j] = z;
							table[j][i] = -z;
						}
					}
				}
			}
		
		for (int i = 0; i < 10000; ++i)
		{
			int c = 0;
			for (int j = 0; j < 10000; ++j)
			{
				if (table[i][j] == 1) c++;
			}
			things[i].prob1 = ((float) c) / 10000;
		}
*/
