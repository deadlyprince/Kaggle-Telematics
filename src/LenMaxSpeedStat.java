
public class LenMaxSpeedStat extends StatisticTwoDim
{
	IPair statisticForTrip(Trip trip)
	{
		int len = trip.len / 100;
		if (len >= 100) len = 99;
		
		int maxSpeed = (int) (trip.maxSpeed() / 2);
		if (maxSpeed >= 100) maxSpeed = 99;
		
		return new IPair(len, maxSpeed);
	}
}
