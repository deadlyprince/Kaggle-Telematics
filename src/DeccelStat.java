
public class DeccelStat extends StatisticOneDim
{
	int statisticForTrip(Trip trip)
	{
		return (int) (trip.maxDeccel() * 10);
	}
}
