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

public class DisplayStats extends Canvas implements ActionListener
{
	int zoom = 1024;
	int current = 0;
	Frame frame;
	Canvas canvas;
	Button buttonn, buttonp, buttonyes, buttonno, buttonzi, buttonzo;
	Match[][] matchTable;
	Driver driver;
	Match match;
	int[] probs1;
	int[] probs2;
	Driver d1, d2, d3;
	SampleSet set;
	
	DisplayStats(SampleSet s)
	{
		set = s;
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
		if (e.getSource() == buttonn)
		{
			current++;
			
		}
		else if (e.getSource() == buttonp)
		{
			current--;
			
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
	
	public void paint(Graphics g)
	{
		int S = 300;
		
		int[] t1slots = new int[S];
		int[] t2slots = new int[S];
		
		int[] f1slots = new int[S];
		int[] f2slots = new int[S];
		
		int ix = 6;
		int iy = 6;
		
		//int maxt = -99999;
		//int maxf = -99999;
		
		for (int i = 0; i < set.samples.size(); ++i)
		{
			Sample s = set.samples.elementAt(i);
			
			int x = (int) (250 + (((s.stats[ix] - set.means[ix]) * 30) / set.sds[ix]));
			int y = (int) (200 + (((s.stats[iy] - set.means[iy]) * 20) / set.sds[iy]));
			
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			
			if (x >= S) x = S - 1;
			if (y >= S) y = S - 1;
			
			if (s.valid)
			{
				t1slots[x]++; 
				t2slots[y]++;
			}
			else
			{
				f1slots[x]++; 
				f2slots[y]++;
			}
		}
		
		/*
		for (int i = 0; i < S; ++i)
			for (int j = 0; j < S; ++j)
			{
				if (tslots[i][j] > maxt) maxt = tslots[i][j];
				if (fslots[i][j] > maxf) maxf = fslots[i][j];
			}
		*/
		
		int M = 10;
		int l;
		
		for (int i = 0; i < S; ++i)
		{
			g.setColor(Color.blue);
			l = (300 * t1slots[i]) / (t1slots[i] + f1slots[i] + 1);
			g.fillRect(10 + i * 3, 10, 2, l);
			
			g.setColor(Color.red);
			l = (300 * t2slots[i]) / (t2slots[i] + f2slots[i] + 1);
			g.fillRect(10 + i * 3, 320, 2, l);
		}
		
		
		/*
		for (int i = 0; i < S; ++i)
			for (int j = 0; j < S; ++j)
			{
				
				double rt = ((double) tslots[i][j]) / maxt;
				double rf = ((double) fslots[i][j]) / maxf;
				
				int r = (int) (255 * rf);
				int b = (int) (255 * rt);
				
				r = 255 - r * M; if (r > 255) r = 255; if (r < 0) r = 0;
				b = 255 - b * M; if (b > 255) b = 255; if (b < 0) b = 0;
				
				g.setColor(new Color(r, 255, b));
				g.fillRect(10 + 2 * i,  10 + 2 * j, 2, 2);
				
				Utils.msg(i + " " + j + " " + tslots[i][j] + " " + fslots[i][j] + " " + r + " " + b);
				
			}
		*/
	}
}
