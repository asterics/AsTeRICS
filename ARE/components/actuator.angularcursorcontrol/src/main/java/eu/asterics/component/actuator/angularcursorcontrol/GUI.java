
/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.angularcursorcontrol;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Implements the Graphical User Interface for the <pluginname> plugin
 * 
 * @author <your name> [<your email>] Date: Time:
 */
public class GUI extends JFrame {
    
    float x=0.0f;
    int len=0, width=0;
    float angle = 0.0f;
    Robot rob;
    double remainX = 0;
    double remainY = 0;

    double locX = 0;
    double locY = 0;
    // private JLabel myLabel;
    // add more GUI elements here

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the owner class instance
     */
    public GUI(final AngularCursorControlInstance owner, final Dimension dim) {
        super("CursorMovementPanel");
        len=dim.height;
        width=dim.width;    
        setUndecorated(true);
        setAlwaysOnTop(true);
        setBackground(new Color(0,0,0,0));
        setSize(new Dimension(len*2+width,len*2+width));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setOpacity(0.5f);
        setVisible(true);
        setShape(0.0f);
        
        try {
            rob = new Robot();
            rob.setAutoDelay(0);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        Point location = MouseInfo.getPointerInfo().getLocation();
        locX=location.x;
        locY=location.y;
        
    }

    void setOnTop()  {
        System.out.println("set on top!!"); 
        setAlwaysOnTop(false);
        repaint();
        setAlwaysOnTop(true);
        repaint();

    }
    void setShape(float angle) {
        this.angle=angle;
        //Point location = MouseInfo.getPointerInfo().getLocation();
        //setLocation(location.x-len, location.y-len);
        setLocation((int)locX-len, (int)locY-len);
        repaint();

        /*
        AffineTransform tx = new AffineTransform();
        tx.translate(len,len);
        tx.rotate(angle);
        tx.translate(-len,-len);
        Rectangle shape = new Rectangle(len-width/2, 0, width, len-10);
        Shape newShape = tx.createTransformedShape(shape);        
        com.sun.awt.AWTUtilities
        .setWindowShape(this, newShape);
         */
    }

    void moveCursor(double dx, double dy) {

        locX+=dx;
        locY+=dy;

        try {
            Robot r = new Robot();
            r.mouseMove((int)locX, (int) locY);
        } catch (AWTException e) {
            e.printStackTrace();
        }   

        setLocation((int)locX-len, (int)locY-len);
    }
    
    @Override
    public void paint(Graphics g) 
    {
         super.paint(g);
         Graphics2D g2 = (Graphics2D) g;
         g2.setColor(Color.RED);
         
         int x1Points[] = {len, len+width, len-width, len};
         int y1Points[] = {0, len-10, len-10, 0};
         GeneralPath polygon = 
                 new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                                 x1Points.length);
         polygon.moveTo(x1Points[0], y1Points[0]);

         for (int index = 1; index < x1Points.length; index++) {
                 polygon.lineTo(x1Points[index], y1Points[index]);
         };

         polygon.closePath();
         
         AffineTransform tx = new AffineTransform();
         tx.translate(len,len);
         tx.rotate(angle);
         tx.translate(-len,-len);
         Shape newShape = tx.createTransformedShape(polygon);         
         g2.fill(newShape);              
    }
}