package tetris;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Random;
import java.util.Vector;

import tetris.Shape.Tetrominoes;


//public class Board extends JPanel implements ActionListener 
public class Board extends Canvas3D implements ActionListener 
{
	

    public static int BoardWidth, BoardHeight, M, N;
    public static double S;
    
    int unitLength;
    
    int delay = 400;

    Timer timer;
    boolean isFallingFinished = false;
    public static boolean isStarted = false;
    boolean isPaused = false;
    int numLines = 0;
    int numLevel = 1;
    int numScore = 0;
    int curX = 0;
    int curY = 0;
    int curZ = 0;
    int boardTop;
    JLabel level;
    JLabel lines;
    JLabel score;
    JLabel status;
    NextZone next;
    Shape curPiece;
    public static Shape nextPiece;
    Tetrominoes[][][] board;
    
    boolean isInFallingShapeLast = false;
    boolean shiftToOriginFlag = false;
    
    int maxX, maxY, centerX, centerY;
    Obj3D obj = new Obj3D();
    Point2D imgCenter;
    
    int dx = 5, dy = 10, dz = -1;
    int[] colorCode;
    

    public Board(Tetris parent) 
    {
       setFocusable(true);  // to ensure this particular object Board has keyboard input
       curPiece = new Shape();
       nextPiece = new Shape();
       timer = new Timer(delay, this);
       timer.start(); 

       level =  parent.getLevel();
       lines = parent.getLines();
       score = parent.getScore();
       next = parent.getNext();
       status = parent.getStatus();
       
       board = new Tetrominoes[BoardWidth][BoardHeight][2];
       
       addMouseListener(new MouseAdapter()
       {
    	    	public void mouseEntered(MouseEvent e)
    	    	{
    	    		pause();
    	    	}
    	    	
    	    	
    	    	public void mouseExited(MouseEvent e)
    	    	{
    	    		pause();
    	    	}
       });
       
       addMouseMotionListener(new MouseAdapter()
       {
	     public void mouseMoved(MouseEvent e)
	    	{
	    		int x = e.getX();
	    	    int y = e.getY();
	    	    boolean isInFallingShapeCur = isInFallingShape(x, y); 
	    		
	    		if (isInFallingShapeCur && !isInFallingShapeLast)
	    		{
	    			int b = 1;
	    			Shape tempPiece = new Shape();
	    			Tetrominoes[] values = Tetrominoes.values(); 
	    			Random r = new Random();
	    	        Tetrominoes curShape = curPiece.getShape(),
	    	        		nextShape = nextPiece.getShape();
	    	        
	    	        if (curShape.compareTo(nextShape) == 0)
	    	        {
	    	        	while (TetrisGame.map.get(b) != curShape.ordinal())
	    	        		b++;
	    	        	
	    	        	do{
	    	        		int i = Math.abs(r.nextInt()) % (TetrisGame.map.size() - 1) + 1;
		    	        	if (i < b)
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i)]);
		    	        	else
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i + 1)]);
	    	        	} 
	    	        	while (!tryMove(tempPiece, curX, curY, curZ));
	    	        	
	    	        	curPiece.setShape(tempPiece.getShape());
	    	        	
	    	        	
	    	        }
	    	        else
	    	        {
	    	        	Tetrominoes min = curShape.compareTo(nextShape) < 0 ?
	    	        			curShape : nextShape;
	    	        	Tetrominoes max = curShape.compareTo(nextShape) > 0 ?
	    	        			curShape : nextShape;
	    	        	
	    	        	int b1, b2;
	    	        	
	    	        	while (TetrisGame.map.get(b) != min.ordinal())
	    	        		b++;
	    	            b1 = b++;
	    	        	
	    	        	while (TetrisGame.map.get(b) != max.ordinal())
	    	        		b++;
	    	        	b2 = b;
	    	        	
	    	        	do
	    	        	{
	    	        		int i = Math.abs(r.nextInt()) % (TetrisGame.map.size() - 2) + 1;
		    	        	if (i < b1)
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i)]);
		    	        	else if (b2 - b1 == 1)
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i + 2)]);	
		    	        	else if (i < b2 - 1)
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i + 1)]);
		    	        	else
		    	        		tempPiece.setShape(values[TetrisGame.map.get(i + 2)]);
	    	        	}
                        while (!tryMove(tempPiece, curX, curY, curZ));
	    	        	
	    	        	curPiece.setShape(tempPiece.getShape());
	    	        			
	    	        }
	    	        
	    	        repaint();
	    	        numScore -= numLevel * M;
	    	        score.setText(String.valueOf(numScore));
	    		}
	    		
	    		isInFallingShapeLast = isInFallingShapeCur;
	    	}
       });
       
       addKeyListener(new KeyAdapter()
       {
    	   public void keyPressed(KeyEvent e)
    	   {
    		   
    		   if (!isStarted || (curPiece.getShape() == 
    				   Tetrominoes.NoShape) || isPaused)
    			   return;
    		   
    		   int keycode = e.getKeyCode();
    		   
    		   if (keycode == 'd' || keycode == 'D')
    			   dropDown();   
    	   }
       });

       clearBoard();  
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
        
    }
    
   
    Tetrominoes shapeAt(int x, int y, int z) { return board[x][y][Math.abs(z)]; }
    


    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLines = 0;
        numScore = 0;
        numLevel = 1;
        clearBoard();
        
        nextPiece.setRandomShape();

        newPiece();
        timer.start();
    }

    private void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
        
        repaint();
    }
    
    
    public void paint(Graphics g)
    { 
        super.paint(g);
        
        obj.reset();
        
      //if (polyList == null) return;
        //if (nFaces == 0) return;
        
        Dimension dim = getSize();
        maxX = dim.width - 1; maxY = dim.height - 1;
        centerX = maxX/2; centerY = maxY/2;
        
        unitLength = Math.min(maxX/BoardWidth, maxY/BoardHeight);
        boardTop = maxY - BoardHeight * unitLength;
        
        drawBox(g, obj, dim);
        
        obj.reset();
        int count = 0;
        
        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i, count++) {
                int fallingx = curX + curPiece.x(i);
                int fallingy = curY - curPiece.y(i);
                addVertexAndFace(count, fallingx - dx, fallingy - dy, curZ - dz);
            }
        }
       
        for (int i = 0; i < BoardHeight; ++i)
            for (int j = 0; j < BoardWidth; ++j) 
            	for (int k = 0; k < 2; k++)
            	{
            		Tetrominoes shape = shapeAt(j, BoardHeight - i - 1, k);
            		if (shape != Tetrominoes.NoShape)
            		{
            			addVertexAndFace(count, j - dx, BoardHeight - i - 1 - dy, -k - dz);
            			count++;
            		}
                	
            	}
        
  
        	   wToScr(obj, dim);
        	   
        	   Vector polyList = obj.getPolyList();
               int nFaces = polyList.size();
               Point2D[] vScr = obj.getVScr();

               for (int j = 0; j < nFaces; j++)
               {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
               	  int[] nrs = pol.getNrs();
               	  
                  Point2D a = vScr[nrs[0]],
                          b = vScr[nrs[1]],
                          c = vScr[nrs[2]],
                          d = vScr[nrs[3]];
                  
                  //int cCode = colorCode[iTria];
                  //g.setColor(new Color(cCode, cCode, 0));
                  
                  int wx = 0, wy = 0, wz = 0;
                  Vector w = obj.getW();
                  
                  switch(nrs[0] % 8)
                  {
                  		case 0: wx = (int) ((Point3D)w.get(nrs[0])).x;
                  				wy = (int) ((Point3D)w.get(nrs[0])).y;
                  				wz = (int) ((Point3D)w.get(nrs[0])).z;
                  				break;
                  				
                  		case 1: wx = (int) ((Point3D)w.get(nrs[0])).x - 1;
          						wy = (int) ((Point3D)w.get(nrs[0])).y;
          						wz = (int) ((Point3D)w.get(nrs[0])).z;
          						break;
          						
                  		case 2: wx = (int) ((Point3D)w.get(nrs[0])).x - 1;
          						wy = (int) ((Point3D)w.get(nrs[0])).y;
          						wz = (int) ((Point3D)w.get(nrs[0])).z + 1;
          						break;
          						
                  		case 3: wx = (int) ((Point3D)w.get(nrs[0])).x;
          						wy = (int) ((Point3D)w.get(nrs[0])).y;
          						wz = (int) ((Point3D)w.get(nrs[0])).z + 1;
          						break;
          						
                  		case 4: wx = (int) ((Point3D)w.get(nrs[0])).x;
          						wy = (int) ((Point3D)w.get(nrs[0])).y - 1;
          						wz = (int) ((Point3D)w.get(nrs[0])).z;
          						break;
                  }
                  
                  Tetrominoes shape = shapeAt(wx + dx, wy + dy, wz + dz) != Tetrominoes.NoShape?
                		  							shapeAt(wx + dx, wy + dy, wz + dz) : curPiece.getShape();
                  
                  int[] x = {iX(a.x), iX(b.x), iX(c.x), iX(d.x)};
                  int[] y = {iY(a.y), iY(b.y), iY(c.y), iY(d.y)};
                  
                  if (colorCode[j] >= 0)
                	  drawSquare(g, x, y, shape, colorCode[j]);
               }
     
       
           
        if (isPaused)
        {
        	g.setColor(Color.blue);
        	g.drawRect(3 * unitLength, 9 * unitLength, 4 * unitLength,
        			(int) 2.2 * unitLength);
        	g.drawString("PAUSE", 4 * unitLength, 10 * unitLength);
        	g.setColor(Color.black);
        }
        
    }
    
    
    private void drawBox(Graphics g, Obj3D obj, Dimension dim)
    {
    	obj.addVertex(0, 0 - dx, 0 - dy, 0 - dz);
    	obj.addVertex(1, 10 - dx, 0 - dy, 0 - dz);
    	obj.addVertex(2, 10 - dx, 0 - dy, -2 - dz);
    	obj.addVertex(3, 0 - dx, 0 - dy, -2 - dz);
    	obj.addVertex(4, 0 - dx, 20 - dy, 0 - dz);
    	obj.addVertex(5, 10 - dx, 20 - dy, 0 - dz);
    	obj.addVertex(6, 10 - dx, 20 - dy, -2 - dz);
    	obj.addVertex(7, 0 - dx, 20 - dy, -2 - dz);
    	obj.addSquareFace(0);
    	
    	wToScr(obj, dim);
 	   
 	    Vector polyList = obj.getPolyList();
        int nFaces = polyList.size();
        Point2D[] vScr = obj.getVScr();

        for (int j = 0; j < nFaces; j++)
        {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
        	  int[] nrs = pol.getNrs();
        	  
           Point2D a = vScr[nrs[0]],
                   b = vScr[nrs[1]],
                   c = vScr[nrs[2]],
                   d = vScr[nrs[3]];
           
           
           
           int[] x = {iX(a.x), iX(b.x), iX(c.x), iX(d.x)};
           int[] y = {iY(a.y), iY(b.y), iY(c.y), iY(d.y)};
           
           g.drawPolygon(x, y, 4);
        }
    }
    
    private void wToScr(Obj3D obj, Dimension dim)
    {
    	Vector polyList = obj.getPolyList();
        int nFaces = polyList.size();
        
        // ze-axis towards eye, so ze-coordinates of
        // object points are all negative.
        // obj is a java object that contains all data:
        // - Vector w       (world coordinates)
        // - Array e        (eye coordinates)
        // - Array vScr     (screen coordinates)
        // - Vector polyList (Polygon3D objects)

        // Every Polygon3D value contains:
        // - Array 'nrs' for vertex numbers
        // - Values a, b, c, h for the plane ax+by+cz=h.
        // - Array t (with nrs.length-2 elements of type Tria)

        // Every Tria value consists of the three vertex
        // numbers iA, iB and iC.
        obj.eyeAndScreen(dim);
              // Computation of eye and screen coordinates.

        imgCenter = obj.getImgCenter();
        obj.planeCoeff();    // Compute a, b, c and h.
        
        
           Point3D[] e = obj.getE();
           Point2D[] vScr = obj.getVScr();
           float[] zFaces = new float[nFaces];
           colorCode = new int[nFaces];

           for (int j=0; j<nFaces; j++)
           {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
             // if (pol.getNrs().length < 3 || pol.getH() <= 0) continue;
           	  colorCode[j] = obj.colorCode(pol.getA(), pol.getB(), pol.getC());
           	  
              
              int[] nrs = pol.getNrs();
 
              float z0 = e[nrs[0]].z, z1 = e[nrs[1]].z,
                    z2 = e[nrs[2]].z, z3 = e[nrs[3]].z;
              zFaces[j] = z0 + z1 + z2 + z3;
         
           }

           sort(polyList, colorCode, zFaces, 0, nFaces - 1);
    }
    
    
    private void addVertexAndFace(int count, int x, int y, int z)
    {
    	obj.addVertex(count * 8, x, y, z);
    	obj.addVertex(count * 8 + 1, x + 1, y, z);
    	obj.addVertex(count * 8 + 2, x + 1, y, z - 1);
    	obj.addVertex(count * 8 + 3, x, y, z - 1);
    	
    	obj.addVertex(count * 8 + 4, x, y + 1, z);
    	obj.addVertex(count * 8 + 5, x + 1, y + 1, z);
    	obj.addVertex(count * 8 + 6, x + 1, y + 1, z - 1);
    	obj.addVertex(count * 8 + 7, x, y + 1, z - 1);
    	
    	obj.addSquareFace(count * 8);
    }
    
    
    private void dropDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1, curZ))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1, curZ))
            pieceDropped();
    }


    private void clearBoard()
    {
        for (int i = 0; i < BoardWidth; ++i)
        	for (int j = 0; j < BoardHeight; j++)
        		for (int k = 0; k < 2; k++)
        			board[i][j][k] = Tetrominoes.NoShape;
    }

    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[x][y][-curZ] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece()
    {
    	curPiece.setShape(nextPiece.getShape()); // should do deep copy!!!

        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();
        curZ = 0;
        
        nextPiece.setRandomShape();
        
        next.repaint();
        
        if (!tryMove(curPiece, curX, curY, curZ)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            status.setText("game over");
        }
        
        
    }
    

    private boolean tryMove(Shape newPiece, int newX, int newY, int newZ)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight || newZ > 0 || newZ < -1)
                return false;
            if (shapeAt(x, y, newZ) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        curZ = newZ;
        repaint();
        return true;
    }
    
    public void moveLeft()
    {
    	tryMove(curPiece, curX - 1, curY, curZ);
    }
    
    public void moveRight()
    {
    	tryMove(curPiece, curX + 1, curY, curZ);
    }
    
    public void rotateRight()
    {
    	tryMove(curPiece.rotateRight(), curX, curY, curZ);
    }
    
    public void rotateLeft()
    {
    	tryMove(curPiece.rotateLeft(), curX, curY, curZ);
    }

    public void moveIn()
    {
    	tryMove(curPiece, curX, curY, curZ - 1);
    }
    
    public void moveOut()
    {
    	tryMove(curPiece, curX, curY, curZ + 1);
    }
    
    
    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int k = 0; k < 2; k++)
        {
        	for (int i = BoardHeight - 1; i >= 0; --i) {
                boolean lineIsFull = true;

                for (int j = 0; j < BoardWidth; ++j) {
                    if (shapeAt(j, i, k) == Tetrominoes.NoShape) {
                        lineIsFull = false;
                        break;
                    }
                }

                if (lineIsFull) {
                    ++numFullLines;
                    for (int s = i; s < BoardHeight - 1; ++s) {
                        for (int j = 0; j < BoardWidth; ++j)
                             board[j][s][k] = shapeAt(j, s + 1, k);
                    }
                }
            }
        }
        

        if (numFullLines > 0) {
            numLines += numFullLines;
            numScore += numFullLines * numLevel * M;
            if (numLines - (numLevel - 1) * N >= N)
            {
            	numLevel++;
            	delay /= 1 + numLevel * S;
            	timer.setDelay(delay);
            	
            }
            lines.setText(String.valueOf(numLines));
            score.setText(String.valueOf(numScore));
            level.setText(String.valueOf(numLevel));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
     }

    private void drawSquare(Graphics g, int[] x, int[] y, Tetrominoes shape, int ccode)
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
        
        int red = color.getRed() * ccode / 255,
        	green = color.getGreen() * ccode / 255,
        	blue = color.getBlue() * ccode / 255;
        
        
        g.setColor(new Color(red, green, blue));
        g.fillPolygon(x, y, 4);
        
        g.setColor(Color.BLACK);
        g.drawPolygon(x, y, 4);
        
    }
    
    
    private boolean isInFallingShape(int x, int y)
    {
    	int pX, pY, actualX, actualY;
    	for (int i = 0; i < 4; ++i) 
    	{
            pX = curX + curPiece.x(i);
            pY = curY - curPiece.y(i);
            
            actualX = pX * unitLength;
            actualY = boardTop + (BoardHeight - pY - 1) * unitLength;
            
            if (x <= actualX + unitLength && x >= actualX
            		&& y <= actualY + unitLength && y >= actualY)
            	return true;
        }
    	
    	return false;
    }
    
    public Obj3D getObj(){return obj;}
    public void setObj(Obj3D obj){this.obj = obj;}
    
    int iX(float x){return Math.round(centerX + x );}
    int iY(float y){return Math.round(centerY - y );}
    
    void sort(Vector polyList, int[] colorCode, float[] zFaces, int l, int r)
    {  int i = l, j = r, wInt;
       float x = zFaces[(i + j)/2], w;
       Polygon3D pol;
       do
       {  while (zFaces[i] < x) i++;
          while (zFaces[j] > x) j--;
          if (i < j)
          {  w = zFaces[i]; zFaces[i] = zFaces[j]; zFaces[j] = w;
             pol = (Polygon3D)(polyList.elementAt(i)); 
             polyList.set(i, polyList.elementAt(j));
             polyList.set(j, pol);
             wInt = colorCode[i]; colorCode[i] = colorCode[j];
             colorCode[j] = wInt;
             i++; j--;
          } else
       if (i == j) {i++; j--;}
    } while (i <= j);
    if (l < j) sort(polyList, colorCode, zFaces, l, j);
    if (i < r) sort(polyList, colorCode, zFaces, i, r);
    }
   
}
