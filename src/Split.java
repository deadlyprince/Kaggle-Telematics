
public class Split
{
	double infoGain = -999999;
	int index, field;
	double value;
	
	Split(double i, int f, int n, double v)
	{
		infoGain = i;
		
		field = f;
		index = n;
		value = v;
	}
	
	public String toString()
	{
		return "index=" + index + " value=" + value + " infoGain=" + infoGain;
	}
}