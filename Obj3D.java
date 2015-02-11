package tetris;

import java.awt.*;
import java.util.*;
import javax.swing.*;


class Obj3D
{  private float d = 737.48395F, rho = 38.814945F, /*theta= 1.57F - 4.7F, phi= 5.75F,*/ rhoMin, rhoMax, //beta = 0F,
	  theta = -3.1299996F, phi = 5.6500006F, beta = -0.6999996F,
      xMin, xMax, yMin, yMax, zMin, zMax, v11, v12, v13, v21,
      v22, v23, v32, v33, v43, xe, ye, ze, objSize;
   private Point2D imgCenter;
   private double sunZ = 1/Math.sqrt (3), sunY = sunZ, sunX = -sunZ;
     public double inprodMin = 1e30, inprodMax = -1e30, inprodRange;
   private Vector w = new Vector();         // World coordinates
   private Point3D[] e;                     // Eye coordinates
   private Point2D[] vScr;                  // Screen coordinates
   private Vector polyList = new Vector();  // Polygon3D objects 
   
   public void reset()
   {
	   w = new Vector();
	   polyList = new Vector();
   }
  

   Vector getW() {return w;}
   Vector getPolyList(){return polyList;}
   Point3D[] getE(){return e;}
   Point2D[] getVScr(){return vScr;}
   Point2D getImgCenter(){return imgCenter;}
   float getRho(){return rho;}
   float getD(){return d;}

   

   public void addVertex(int i, float x, float y, float z)
   {  if (x < xMin) xMin = x; if (x > xMax) xMax = x;
      if (y < yMin) yMin = y; if (y > yMax) yMax = y;
      if (z < zMin) zMin = z; if (z > zMax) zMax = z;
      if (i >= w.size()) w.setSize(i + 1);
      w.setElementAt(new Point3D(x, y, z), i);
   }
   
   public void addSquareFace(int i)
   {
	   Vector vnrs = new Vector(Arrays.asList(i, i + 1, i + 5, i + 4));
	   polyList.addElement(new Polygon3D(vnrs));
	   
	   vnrs = new Vector(Arrays.asList(i + 1, i + 2, i + 6, i + 5));
	   polyList.addElement(new Polygon3D(vnrs));

	   vnrs = new Vector(Arrays.asList(i + 2, i + 3, i + 7, i + 6));
	   polyList.addElement(new Polygon3D(vnrs));

	   vnrs = new Vector(Arrays.asList(i + 3, i + 0, i + 4, i + 7));
	   polyList.addElement(new Polygon3D(vnrs));

	   vnrs = new Vector(Arrays.asList(i + 4, i + 5, i + 6, i + 7));
	   polyList.addElement(new Polygon3D(vnrs));

	   vnrs = new Vector(Arrays.asList(i + 0, i + 3, i + 2, i + 1));
	   polyList.addElement(new Polygon3D(vnrs));
       
   }

   public void shiftToOrigin()
   {  float xwC = 0.5F * (xMin + xMax),
            ywC = 0.5F * (yMin + yMax),
            zwC = 0.5F * (zMin + zMax);
      int n = w.size();
     
      // for (int i=1; i<n; i++) 
      for (int i=0; i<n; i++)
            if (w.elementAt(i) != null)
            {  ((Point3D)w.elementAt(i)).x -= xwC;
               ((Point3D)w.elementAt(i)).y -= ywC;
               ((Point3D)w.elementAt(i)).z -= zwC;
            }
         float dx = xMax - xMin, dy = yMax - yMin, dz = zMax - zMin;
         rhoMin = 0.6F * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
         rhoMax = 1000 * rhoMin;
         rho = 3 * rhoMin;
         d = 19 * rho;
      }

      private void initPersp()
      {  float costh = (float)Math.cos(theta),
               sinth = (float)Math.sin(theta),
               cosph = (float)Math.cos(phi),
               sinph = (float)Math.sin(phi);
      /*
         v11 = -sinth; v12 = -cosph * costh; v13 = sinph * costh;
         v21 = costh;  v22 = -cosph * sinth; v23 = sinph * sinth;
                       v32 = sinph;          v33 = cosph;
                                             v43 = -rho;
      */
      
      v11 = -costh; v12 = -cosph * sinth; v13 = sinph * sinth;
      v21 = sinth;  v22 = -cosph * costh; v23 = sinph * costh;
                    v32 = sinph;          v33 = cosph;
                                          v43 = -rho;
      
      
      
      
      }

      float eyeAndScreen(Dimension dim)
         // Called in paint method of Canvas class
      {  initPersp();
         int n = w.size();
         e = new Point3D[n];
         vScr = new Point2D[n];
         float xScrMin=1e30F, xScrMax=-1e30F,
               yScrMin=1e30F, yScrMax=-1e30F;
         //for (int i=1; i<n; i++)
         for (int i=0; i<n; i++)
         {  Point3D P = (Point3D)(w.elementAt(i));
            if (P == null)
         {  e[i] = null; vScr[i] = null;
         }
         else
         { 
        	float
        	xw = P.x * (float)Math.cos(-beta) + P.z * (float)Math.sin(-beta),
        	yw = P.y,
        	zw = -P.x * (float)Math.sin(-beta) + P.z * (float)Math.cos(-beta);
        	 
        	float x = v11 * xw + v21 * yw;
            float y = v12 * xw + v22 * yw + v32 * zw;
            float z = v13 * xw + v23 * yw + v33 * zw + v43;
            
            Point3D Pe = e[i] = new Point3D(x, y, z);
            
            float xScr = -Pe.x/Pe.z, yScr = -Pe.y/Pe.z;
            vScr[i] = new Point2D(xScr, yScr);
            if (xScr < xScrMin) xScrMin = xScr; 
            if (xScr > xScrMax) xScrMax = xScr;
            if (yScr < yScrMin) yScrMin = yScr;
            if (yScr > yScrMax) yScrMax = yScr;
         }
      }
    	  
      float rangeX = xScrMax - xScrMin, rangeY = yScrMax - yScrMin;
     // d = 0.95F * Math.min(dim.width/rangeX, dim.height/rangeY);
      imgCenter = new Point2D(d * (xScrMin + xScrMax)/2,
                              d * (yScrMin + yScrMax)/2);
      //for (int i=1; i<n; i++)
      for (int i=0; i<n; i++)
      {  if (vScr[i] != null){vScr[i].x *= d; vScr[i].y *= d;}
      }
      
      
      
      return d * Math.max(rangeX, rangeY);
      // Maximum screen-coordinate range used in CvHLines for HP-GL
   }

   void planeCoeff()
   {  int nFaces = polyList.size();

      for (int j=0; j<nFaces; j++)
      {  Polygon3D pol = (Polygon3D)(polyList.elementAt(j));
         int[] nrs = pol.getNrs();
         if (nrs.length < 3) continue;
         int iA = Math.abs(nrs[0]), // Possibly negative
             iB = Math.abs(nrs[1]), // for HLines.
             iC = Math.abs(nrs[2]);
                  
         Point3D A = e[iA], B = e[iB], C = e[iC];
         
         double
            u1 = B.x - A.x, u2 = B.y - A.y, u3 = B.z - A.z,
            v1 = C.x - A.x, v2 = C.y - A.y, v3 = C.z - A.z,
            a = u2 * v3 - u3 * v2,
            b = u3 * v1 - u1 * v3,
            c = u1 * v2 - u2 * v1,
            len = Math.sqrt(a * a + b * b + c * c), h;
            a /= len; b /= len; c /= len;
            h = a * A.x + b * A.y + c * A.z;
         pol.setAbch(a, b, c, h);
         Point2D A1 = vScr[iA], B1 = vScr[iB], C1 = vScr[iC];
         u1 = B1.x - A1.x; u2 = B1.y - A1.y;
         v1 = C1.x - A1.x; v2 = C1.y - A1.y;
         if (u1 * v2 - u2 * v1 <= 0) continue; // backface
         double inprod = a * sunX + b * sunY + c * sunZ;
         if (inprod < inprodMin) inprodMin = inprod; 
         if (inprod > inprodMax) inprodMax = inprod;
       }
       inprodRange = inprodMax - inprodMin;
     }
   
   
   boolean vp(Canvas3D cv, float dTheta, float dPhi, float dBeta, float fRho)
   {  theta += dTheta;
      phi += dPhi;
      rho *= fRho;
      beta += dBeta;
      
     // float rhoNew = fRho * rho;
     // if (rhoNew >= rhoMin && rhoNew <= rhoMax)
     //    rho = rhoNew;
     // else
     //    return false;
      
      cv.repaint();
     
      return true;
  }
  
     
    int colorCode(double a, double b, double c)
    {  double inprod = a * sunX + b * sunY + c * sunZ;
    
       return (int)Math.round(
          ((inprod - inprodMin)/inprodRange) * 255);
    }
}
