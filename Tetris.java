package tetris;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.*;
import java.awt.event.*;


public class Tetris extends JFrame implements ActionListener
{
	NextZone next;
	JLabel level;
	JLabel lines;
	JLabel score;
	JButton quit;
	JLabel status;
	JButton up;
	JButton down;
	public static double sizeParameter = 1.0;
	
	//int M, N, Height, Width;
	//double S;
	
	MenuItem eyeUp, eyeDown, eyeLeft, eyeRight, incrDist, decrDist;
	Menu mV;
	final Board board;
	
	
	
	//public Tetris(int M, int N, double S, int Height, int Width)
	public Tetris()
	{
		/*
		this.M = M;
		this.N = N;
		this.S = S;
		this.Height = Height;
		this.Width = Width;
		*/
		super("Tetris");
		
		MenuBar mBar = new MenuBar();
		  setMenuBar(mBar);
		  mV = new Menu("View");
		  mBar.add((Menu) mV);

		  eyeDown = new MenuItem("Viewpoint Down",
		     new MenuShortcut(KeyEvent.VK_DOWN));
		  eyeUp = new MenuItem("Viewpoint Up",
		     new MenuShortcut(KeyEvent.VK_UP));
		  eyeLeft = new MenuItem("Viewpoint to Left",
		     new MenuShortcut(KeyEvent.VK_LEFT));
		  eyeRight = new MenuItem("Viewpoint to Right",
		     new MenuShortcut(KeyEvent.VK_RIGHT));
		  incrDist = new MenuItem("Increase viewing distance",
		     new MenuShortcut(KeyEvent.VK_OPEN_BRACKET));
		  decrDist = new MenuItem("Decrease viewing distance",
		     new MenuShortcut(KeyEvent.VK_CLOSE_BRACKET));
		 
		  mV.add(eyeDown); mV.add(eyeUp);
		  mV.add(eyeLeft); mV.add(eyeRight);
		  mV.add(incrDist); mV.add(decrDist);
		  
		  eyeDown.addActionListener(this);
		  eyeUp.addActionListener(this);
		  eyeLeft.addActionListener(this);
		  eyeRight.addActionListener(this);
		  incrDist.addActionListener(this);
		  decrDist.addActionListener(this);
		
		
		next = new NextZone(this);
		
		level = new JLabel("1");
		lines = new JLabel("0");
		score = new JLabel("0");
		status = new JLabel("Press D to expedite the falling");
		
		quit = new JButton("QUIT");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		up = new JButton("\u2191");
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				board.moveIn();
				board.requestFocus(true);
			}
		});
		
		down = new JButton("\u2193");
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				board.moveOut();
				board.requestFocus(true);
			}
		});
		
		board = new Board(this);
		
		
		JPanel p3 = new JPanel();
		p3.setLayout(new GridLayout(3, 2, 5, 5));
		
		
		p3.add(new JLabel("							Level: "));
		p3.add(level);
		p3.add(new JLabel("							Lines: "));
		p3.add(lines);
		p3.add(new JLabel("							Score: "));
		p3.add(score);
		
		JPanel  pUp = new JPanel(new BorderLayout());
		pUp.add(next, BorderLayout.CENTER);
		pUp.add(new JPanel(), BorderLayout.NORTH);
		pUp.add(new JPanel(), BorderLayout.WEST);
		pUp.add(new JPanel(), BorderLayout.EAST);
		pUp.add(new JPanel(), BorderLayout.SOUTH);
		

		
		JPanel pDown = new JPanel(new GridLayout(3, 1, 10, 10));
		
		pDown.add(up);
		pDown.add(down);
		pDown.add(quit);
		
		
		JPanel p2 = new JPanel();
		p2.setLayout(new GridLayout(3, 1, 5, 5));
		
		p2.add(pUp);
		p2.add(p3);
		p2.add(pDown);
		
		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(board, BorderLayout.CENTER);
		p1.add(status, BorderLayout.NORTH);
		
		setLayout(new GridLayout(1, 2));
		
		add(p1);
		add(p2);
		
		
		board.start();
		
		p2.addMouseListener(new MouseAdapter() 
		{
			public void mousePressed(MouseEvent e)
	    	{
				
	    		
	    			if (e.getButton() == MouseEvent.BUTTON1)
	    				board.moveLeft();
	    			else if (e.getButton() == MouseEvent.BUTTON3)
	    				board.moveRight();
	    	
	    		
	    	}
		});
		
		p2.addMouseWheelListener(new MouseAdapter() 
		{
	    	public void mouseWheelMoved(MouseWheelEvent e)
	    	{
	    		
	    			int notches = e.getWheelRotation();
	    			if (notches < 0)
	    				board.rotateRight();
	    			else
	    				board.rotateLeft();
	    				
	    	}
		});
		
		setSize((int) (500 * sizeParameter), 
				(int) (250 * Board.BoardHeight / Board.BoardWidth * sizeParameter));
		setTitle("Tetris");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	public JLabel getLevel()
	{
		return level;
	}
	
	public JLabel getLines()
	{
		return lines;
	}
	
	public JLabel getScore()
	{
		return score;
	}
	
	public NextZone getNext()
	{
		return next;
	}
	
	public JLabel getStatus()
	{
		return status;
	}
	
	
	void vp(float dTheta, float dPhi, float dBeta, float fRho) // Viewpoint
	{  Obj3D obj = board.getObj();
	   if (obj == null  ||   !obj.vp(board, dTheta, dPhi, dBeta, fRho))
	      Toolkit.getDefaultToolkit().beep();
	}
	
	public void actionPerformed(ActionEvent ae)
	{  if (ae.getSource() instanceof MenuItem)
	{  MenuItem mi = (MenuItem)ae.getSource();
	
	if (mi == eyeDown) vp(0, .1F, 0, 1); else
		if (mi == eyeUp) vp(0, -.1F, 0, 1); else
		if (mi == eyeLeft) vp(0, 0, -.1F, 1); else
		if (mi == eyeRight) vp(0, 0, .1F, 1); else
		if (mi == incrDist) vp (0, 0, 0, 1.1F); else
		if (mi == decrDist) vp(0, 0, 0, .9F);

	/*
	if (mi == eyeDown) vp(0, .1F, 1); else
	if (mi == eyeUp) vp(0, -.1F, 1); else
	if (mi == eyeLeft) vp(-.1F, 0, 1); else
	if (mi == eyeRight) vp(.1F, 0, 1); else
	if (mi == incrDist) vp (0, 0, 1.1F); else
	if (mi == decrDist) vp(0, 0, .9F);
	*/

	}
	}
	
	/*
	public static void main(String[] args)
	{
		Tetris game = new Tetris();
		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}
    */
}
