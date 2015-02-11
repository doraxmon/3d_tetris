package tetris;

import java.awt.*;

import javax.swing.*;

import java.lang.Math;

import tetris.Shape.Tetrominoes;


public class NextZone extends JPanel
{
	
    final int Width = 6;
    final int Height = 4;
    
    int unitLength;

    public NextZone(Tetris parent) 
    {
    	
    }
    
    public void paint(Graphics g)
    { 
        super.paint(g);

        Dimension size = getSize();
        int maxX = size.width, maxY = size.height;
        unitLength = Math.min(maxX/Width, maxY/Height);
        int boardTop = maxY - Height * unitLength;
        
        g.drawRect(maxX / 2 - Width * unitLength / 2, boardTop, Width * unitLength, Height * unitLength - 1);

        if (Board.isStarted && (Board.nextPiece.getShape() != Tetrominoes.NoShape)) {
            for (int i = 0; i < 4; ++i) 
            {
            	int x = Board.nextPiece.x(i);
                int y = 2 - Board.nextPiece.y(i);
            	drawSquare(g, maxX / 2 + x * unitLength,
            			   boardTop + (Height - y - 1) * unitLength,
                           Board.nextPiece.getShape());
            }
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
