import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;


public class Display1 extends Display
{
	Display1()
	{
		super();
	}
	
	Display1(Driver mm)
	{
		super(mm);
	}
	
	public void paint(Graphics g)
	{
		g.setColor(Color.red);
		drawTrips(g, d2.trips, d2.matchTable, 10);
		
		g.setColor(Color.blue);
		drawTrips(g, d1.trips, d1.matchTable, 410);
	}
	
	void drawTrips(Graphics g, Trip[] trips, Match[][] matchTable, int start)
	{
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			float max = -99999;
			float tot = 0;
			
			for (int j = 1; j < Driver.lastTripIndex + 1; ++j)
			{
				float s = 0;
				if (matchTable[trips[i].filenumber][trips[j].filenumber] != null)
					s = matchTable[trips[i].filenumber][trips[j].filenumber].score;
				if (s > max) max = s;
				tot += s;
			}
			
			tot /= 200;
			
			int k = 5;
			int r = (int) ((max / tot) * 70);
			
			//int k = (int) (1 + Math.log(1 + trips[i].len));
			//g.fillRect(10 + (int) (max / 300),  10 + (int) (tot / 30),  k, k);
			
			g.fillRect(10 + start + i * 2, r, k, k);
			
			Utils.msg(i + " " + r);
		}
	}
	
	public void paintx(Graphics g)
	{
		/*
		if (match != null)
		{
			Match m = match;
			
			drawTrip(m.t0, g, m.i0, Color.blue, m.nsteps);
			describe(m.t0, g, 10, m.t0.filenumber);
			
			g.drawString("score = " + m.score, 20, 70);
			
			drawTrip(m.t1, g, m.i1, Color.black, m.nsteps);
			describe(m.t1, g, 30, m.t1.filenumber);

			return;
		}
		*/
		Match m = driver.bestMatch(trips[current]);
		
		//Result res = Result.results[driver.drivern][trips[current].filenumber];
		
		g.setColor(Color.blue);
		String ch = (trips[current].changed ? "CHANGED" : "NOTCHANGED");
		if (m != null)
		{
			g.drawString(ch + " " + trips[current].filenumber + " " + m.score, 20, 15);
		}
		else
		{
			g.drawString(ch + " result null for " + driver.drivern + " " + trips[current].filenumber, 20, 15);
		}
		
		if (m == null)
		{
			g.setColor(Color.blue);
			//g.drawString("no match ", 20, 20);
			drawTrip(trips[current], g, 0, Color.blue, 0);
			describe(trips[current], g, 10, current);
			return;
		}
		
		float max = m.score;
		
		drawTrip(m.t0, g, m.i0, Color.blue, m.nsteps);
		describe(m.t0, g, 10, m.t0.filenumber);
		
		drawTrip(m.t1, g, m.i1, Color.black, m.nsteps);
		describe(m.t1, g, 30, m.t1.filenumber);
		
		g.drawString("score = " + max + " max speeds = " + m.maxSpeed(true) + " " + m.maxSpeed(false), 20, 70);
		
		//float[] f = driver.calcError(m.i0, m.i0 + m.nsteps, m.i0 - m.i1, m.t0, m.t1);
		//for (int i = 0; i < 4; ++i)
		//	g.drawString(f[i * 2] + " " + f[i * 2 + 1], 200, 100 + i * 30);
		
		g.drawString("error=" + m.err, 200, 100);
	}

	void drawTrip(Trip trip, Graphics g, int z, Color c, int n)
	{	
		boolean RAW = false;
		int MZ = 1024;
		
		if (trip == null || trip.spacedx == null) return;
		
		if (RAW)
		{
			g.setColor(Color.blue);
			for (int i = 0; i < trip.x.length; ++i)
			{
				float fx = (trip.x[i] - trip.xmin);
				float fy = (trip.y[i] - trip.ymin);
					
				int x0 = 30 + (int) (fx * MZ / zoom);
				int y0 = 30 + (int) (fy * MZ / zoom);
		
				g.fillRect(x0 - 1, y0 - 1, 3, 3);
			}
		}
		if (true)
		{
			for (int i = 0; i < trip.spacedx.length; ++i)
			{
				float fx = (trip.spacedx[i] - trip.xmin);
				float fy = (trip.spacedy[i] - trip.ymin);
							
				int x0 = 30 + (int) (fx * MZ / zoom);
				int y0 = 30 + (int) (fy * MZ / zoom);
				
				int k = 2;
				
				if (i >= z && i <= z + n)
					g.setColor(Color.red);
				else
					g.setColor(c);

				if (i == 0)
					g.fillRect(x0, y0, 4 * k, 4 * k);
				else if (i % 5 == 0)
					g.fillRect(x0, y0, 2 * k, 2 * k);
				else
					g.fillRect(x0, y0, k, k);
			}
		}
		
		g.drawString((500 * zoom / MZ) + " meters to this point", 10, 500);
	}
	
	void describe(Trip t, Graphics g, int a, int xxx)
	{
		g.setColor(Color.blue);
		g.drawString(	" filenumber = " + t.filenumber + 
						" time = " + t.xlen + 
						" length = " + t.len, 20, 20 + a);
		
		//t.printSpacedPoints();
	}
	
	/*
	for (int j = 1; j < Driver.lastTripIndex + 1; ++j)
	{
		int x = 10 + i * 5;
		int y = 10 + j * 5;
		int s = 0;
		if (matchTable[trips[i].filenumber][trips[j].filenumber] != null)
			s = (int) (matchTable[trips[i].filenumber][trips[j].filenumber].score / 100);
		if (s > 255) s = 255;
		g.setColor(new Color(s, s, s));
		g.fillRect(x,  y,  5, 5);
	}
*/
}
