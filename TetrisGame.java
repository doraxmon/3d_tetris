package tetris;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import tetris.Shape.Tetrominoes;

import java.awt.event.*;

public class TetrisGame extends JFrame
{
	Tetris game;
	
	public static Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	private JTextField jtfM = new JTextField("5");
	private JTextField jtfN = new JTextField("2");
	private JTextField jtfS = new JTextField("0.3");
	private JTextField jtfHeight = new JTextField("20");
	private JTextField jtfWidth = new JTextField("10");
	private JRadioButton jrbSmall = new JRadioButton("Small");
	private JRadioButton jrbLarge = new JRadioButton("Large"); 
	
	private JCheckBox[] jchkExtraShape = {new JCheckBox("Extra Shape 1"),
			new JCheckBox("Extra Shape 2"), new JCheckBox("Extra Shape 3"),
			new JCheckBox("Extra Shape 4"), new JCheckBox("Extra Shape 5"),
			new JCheckBox("Extra Shape 6"), new JCheckBox("Extra Shape 7"),
			new JCheckBox("Extra Shape 8")};
	
	private JButton jbtRunTetris = new JButton("Run Tetris");
	
	public TetrisGame() 
	{
		JPanel jpRadioButtons = new JPanel(new GridLayout(1, 2));
		jpRadioButtons.add(jrbSmall);
		jpRadioButtons.add(jrbLarge);
		
		ButtonGroup group = new ButtonGroup();
		group.add(jrbSmall);
		group.add(jrbLarge);
		jrbSmall.setSelected(true);
		
		Tetrominoes[] values = Tetrominoes.values();
		
		JPanel[] pExtraShape = new JPanel[8]; 
		for (int i = 0; i < 8; i++)
		{
			pExtraShape[i] = new JPanel(new BorderLayout());
			Shape piece = new Shape();
			piece.setShape(values[i + 8]);
			DrawExtraShape drawExtraShape = new DrawExtraShape(piece);
			pExtraShape[i].add(drawExtraShape, BorderLayout.CENTER);
			pExtraShape[i].add(jchkExtraShape[i], BorderLayout.EAST);
		}
		
		JPanel p1 = new JPanel(new GridLayout(11, 2, 0, 5));
		p1.add(new JLabel("Scoring Factor (range: 1-10)"));
		p1.add(jtfM);
		p1.add(new JLabel("Number of Rows for each level (range: 2-50)"));
		p1.add(jtfN);
		p1.add(new JLabel("Speed Factor (range: 0.1-1.0)"));
		p1.add(jtfS);
		p1.add(new JLabel("Height of Main Area (range: 20-40)"));
		p1.add(jtfHeight);
		p1.add(new JLabel("Width of Main Area (range: 10-20)"));
		p1.add(jtfWidth);
		p1.add(new JLabel("Size of the Square (small or large)"));
		p1.add(jpRadioButtons);
		p1.add(new JLabel("Pick Extra Shapes to add to the Game"));
		p1.add(new JLabel());
		for (int i = 0; i < 8; i++)
			p1.add(pExtraShape[i]);
		p1.setBorder(new TitledBorder("Choose the values of parameters for the game"));

		
		JPanel p2 = new JPanel();
		p2.add(jbtRunTetris, BorderLayout.EAST);
		
		add(p1, BorderLayout.CENTER);
		add(p2, BorderLayout.SOUTH);
		
		
		for (int i = 1; i <= 7; i++)
			map.put(i, i);
		
		
		jrbSmall.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				Tetris.sizeParameter = 1.0;
			}
		});
		
		jrbLarge.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				Tetris.sizeParameter = 1.3;
			}
		});
		
		
		jbtRunTetris.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				/*
				int M = Integer.parseInt(jtfM.getText());
				int N = Integer.parseInt(jtfN.getText());
				double S = Double.parseDouble(jtfS.getText());
				int Height = Integer.parseInt(jtfHeight.getText());
				int Width = Integer.parseInt(jtfWidth.getText());
				
				game = new Tetris(M, N, S, Height, Width);
				*/
				Board.M = Integer.parseInt(jtfM.getText());
				Board.N = Integer.parseInt(jtfN.getText());
				Board.S = Double.parseDouble(jtfS.getText());
				Board.BoardHeight = Integer.parseInt(jtfHeight.getText());
				Board.BoardWidth = Integer.parseInt(jtfWidth.getText());
				
				int count = 8;
				for (int i = 0; i < 8; i++)
					if (jchkExtraShape[i].isSelected())
						map.put(count++, i + 8);
					
				game = new Tetris();
				game.setLocationRelativeTo(null);
				game.setVisible(true);
			}
		});
	}
	
	
	class DrawExtraShape extends JPanel
	{
		final int Width = 5;
	    final int Height = 3;
	    Shape Piece;
	    
	    int unitLength;

	    public DrawExtraShape(Shape piece) 
	    {
	    	Piece = piece;
	    }
	    
	    public void paint(Graphics g)
	    { 
	        super.paint(g);

	        Dimension size = getSize();
	        int maxX = size.width, maxY = size.height;
	        unitLength = Math.min(maxX/Width, maxY/Height);
	        int boardTop = maxY - Height * unitLength;
	        
	        //g.drawRect(maxX / 2 - Width * unitLength / 2, boardTop, Width * unitLength, Height * unitLength - 1);

	        for (int i = 0; i < 4; ++i)
	            {
	            	int x = Piece.x(i);
	                int y = 1 + Piece.y(i);
	            	drawSquare(g, maxX / 2 + x * unitLength,
	            			   boardTop + (Height - y - 1) * unitLength,
	                           Piece.getShape());
	            }
	    }
	 

	    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)
	    {
	    	Color colors[] = { new Color(0, 0, 0), new Color(255, 0, 255), 
	                Color.yellow, new Color(31, 190, 214), 
	                Color.orange, Color.green, 
	                Color.red, Color.blue, new Color(0, 102, 102),
	                new Color(160, 160, 160), new Color(0, 204, 0),
	                new Color(255, 153, 153), new Color(204, 102, 0),
	                new Color(204, 255, 153), new Color(102, 102, 0),
	                new Color(0, 51, 102)
	        };


	        Color color = colors[shape.ordinal()];

	        g.setColor(color);
	        g.fillRect(x + 1, y + 1, unitLength - 2, unitLength - 2);

	        g.setColor(Color.black);
	        g.drawLine(x, y + unitLength - 1, x, y);
	        g.drawLine(x, y, x + unitLength - 1, y);
	        g.drawLine(x, y + unitLength - 1,
	                         x + unitLength - 1, y + unitLength - 1);
	        g.drawLine(x + unitLength - 1, y + unitLength - 1,
	                         x + unitLength - 1, y);

	    }
	}
	

	public static void main(String[] args)
	{
		TetrisGame frame = new TetrisGame();
		frame.setSize(600, 600);
		frame.setTitle("Set Up Parameters for Tetris");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
