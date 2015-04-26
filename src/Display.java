import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Display extends Canvas implements ActionListener
{
	Trip[] trips;
	int zoom = 1024;
	int current = 1;
	Frame frame;
	Canvas canvas;
	Button buttonn, buttonp, buttonyes, buttonno, buttonzi, buttonzo;
	Match[][] matchTable;
	Driver driver;
	Match match;
	int[] probs1;
	int[] probs2;
	Driver d1, d2, d3;
	
	Display()
	{
		init();
	}
	
	Display(Driver mm)
	{
		driver = mm;
		
		if (driver != null)
		{
			trips = driver.trips;
			matchTable = driver.matchTable;
		}
		
		init();
	}
	
	void init()
	{
		int wd = 50000;
		int ht = 5000;
		
		frame = new Frame();
		frame.setSize(wd, ht);
		frame.addWindowListener(new WA());
		
		canvas = this;
		canvas.setSize(wd, ht);
		
		ScrollPane sp = new ScrollPane();
		frame.add("Center", sp);
		sp.add(canvas);	
		
		Panel p = new Panel();
		
		buttonn = buttonmaker("next", p, this);
		buttonp = buttonmaker("prev", p, this);
		buttonyes = buttonmaker("yes", p, this);
		buttonno = buttonmaker("no", p, this);
		buttonzi = buttonmaker("zoom in", p, this);
		buttonzo = buttonmaker("zoom out", p, this);
		
		frame.add("South", p);
		
		frame.setVisible(true);
		canvas.setVisible(true);
		
		canvas.invalidate();
		canvas.repaint();
	}
	
	Button buttonmaker(String l, Panel p, ActionListener a)
	{
		Button b = new Button(l);
		b.addActionListener(this);
		p.add(b);
		return b;
	}
	
	public void paint2(Graphics g)
	{
		g.setColor(Color.blue);
		for (int i = 0; i < Collector.n; ++i)
		{
			g.fillRect(100 + Collector.x[i], 100 + Collector.y[i], 3, 3);
		}
		
		g.setColor(Color.red);
		for (int i = 0; i < Collector.n; ++i)
		{
			g.fillRect(100 + i / 5, 100 + Collector.y[i] * Collector.x[i] / 300, 3, 3);
		}
	}
	
	void setMatch(Match m)
	{
		match = m;
		repaint();
	}
	
	public void paint(Graphics g)
	{
		int x, y;
		Trip t1;
		
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			t1 = driver.trips[i];
			x = t1.getx();
			y = t1.gety();
			g.setColor(Color.blue);
			g.fillRect(10 + 3 * x, 10 + 3 * y, 3, 3);
		}
	}
	
	public void paintxs(Graphics g)
	{
		if (d1 == null || d2 == null) return;
		int x, y;
		Trip t1;
		int c = 0;
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			t1 = d1.trips[i];
			x = t1.getx();
			y = t1.gety();
			g.setColor(Color.blue);
			{ g.fillRect(10 + x, 10 + y, 3, 3); c++; }
			t1 = d2.trips[i];
			x = t1.getx();
			y = t1.gety();
			g.setColor(Color.red);
			{ g.fillRect(10 + x, 10 + y, 3, 3); c++; }
			t1 = d3.trips[i];
			x = t1.getx();
			y = t1.gety();
			g.setColor(Color.red);
			{ g.fillRect(10 + x, 10 + y, 3, 3); c++; }
		}
		Utils.msg(c);
	}
	
	public void paintq(Graphics g)
	{
		if (false)
		{
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			g.setColor(Color.blue);
			g.fillRect(10 + (probs1[i]), 100, 1, 5);
			
			g.setColor(Color.red);
			g.fillRect(10 + (probs2[i]), 110, 1, 5);
			
			//Utils.msg((probs1[i]) + " " + (probs2[i]));
		}
		}
		else
		{
		/*
		for (int i = 1; i < Driver.lastTripIndex + 1; ++i)
		{
			int x = (int) (Main.sub7[driver.drivern][i] * 1000);
			int y = (int) (probs1[i] / 1.75f);
			
			g.setColor(Color.red);
			g.fillRect(10 + x, 10 + y, 3, 3);
		}
		*/
		}
	}
	
	public void paintgx(Graphics g)
	{
		/*
		int e = 8;
		int[][] table = driver.speedTable;
		for (int i = 0; i < table.length; ++i)
			for (int j = 0; j < table[i].length; ++j)
			{
				int x = 10 + i * e;
				int y = 10 + j * e;
				int k = table[i][j];
				//k /= 100;
				if (k > 255) k = 255;
				k = 255 - k;
				if (i % 10 == 0)
					g.setColor(new Color(k, k, 0));
				else
					g.setColor(new Color(k, k, k));
				g.fillRect(x, y, e, e);
			}
		*/
	}
	
	public void paintx(Graphics g)
	{
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
	
	class WA extends WindowAdapter
	{
		public void windowClosing(WindowEvent w)
		{
			System.exit(0);
		}

		public void windowClosed(WindowEvent w)
		{
			System.exit(0);
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		int M = 1000;
		boolean FILTER = false; 
		
		if (e.getSource() == buttonn)
		{
			current++;
			if (FILTER)
				while (current < 200)
				{
					Result res = Result.results[driver.drivern][trips[current].filenumber];
					if (res == null || res.score < M || trips[current].dataErr) break;
					current++;
				}
			if (current > 200) current = 200;
		}
		else if (e.getSource() == buttonp)
		{
			current--;
			if (FILTER)
				while (current > 1)
				{
					Result res = Result.results[driver.drivern][trips[current].filenumber];
					if (res == null || res.score < M || trips[current].dataErr) break;
					current--;
				}
			if (current < 1) current = 1;
		}
		else if (e.getSource() == buttonzi)
		{
			zoom /= 2;
			repaint();
		}
		else if (e.getSource() == buttonzo)
		{
			zoom *= 2;
			repaint();
		}
		else if (e.getSource() == buttonyes)
		{
			//if (match != null)
			//	Utils.msg(match.err + "," + match.decision + "," + match.f + "," + match.s0.entropy + "," + match.s1.entropy + "," + match.nsteps + "," + 1);
			
		}
		else if (e.getSource() == buttonno)
		{
			//if (match != null)
			//	Utils.msg(match.err + "," + match.decision + "," + match.f + "," + match.s0.entropy + "," + match.s1.entropy + "," + match.nsteps + "," + 0);
			
		}
		canvas.invalidate();
		canvas.repaint();	
	}
}
