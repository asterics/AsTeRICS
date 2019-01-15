
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

package eu.asterics.component.actuator.crosshaircursorcontrol;

import java.awt.*;
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
    
    int width=0,height=0;
    int lineWidth=0;
    Robot rob;

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
    public GUI(final CrosshairCursorControlInstance owner, final Dimension dim, final int lineWidth) {
        super("CursorMovementPanel");
        this.width=width;    
        setUndecorated(true);
        setAlwaysOnTop(true);
        setBackground(new Color(0,0,0,0));
        setSize(dim);
        width=dim.width;
        height=dim.height;
        this.lineWidth=lineWidth;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setOpacity(0.5f);
        setVisible(true);
        
        try {
            rob = new Robot();
            rob.setAutoDelay(0);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        setLocation(0,0);
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

    void showCrosshair()  {
        setVisible(true);
    }

    void hideCrosshair()  {
        setVisible(false);
    }

    void setShape(float x, float y) {
        //Point location = MouseInfo.getPointerInfo().getLocation();
        //setLocation((int)locX-len, (int)locY-len);
        locX=x;
        locY=y;

        try {
            Robot r = new Robot();
            r.mouseMove((int)locX, (int) locY);
        } catch (AWTException e) {
            e.printStackTrace();
        }   

        //setLocation((int)locX-len, (int)locY-len);
        repaint();
    }
    
    @Override
    public void paint(Graphics g) 
    {
         super.paint(g);
         Graphics2D g2 = (Graphics2D) g;
         g2.setColor(Color.RED);
         g2.fillRect((int)locX-lineWidth/2, 0, lineWidth, (int)locY-lineWidth/2);
         g2.fillRect((int)locX-lineWidth/2, (int)locY+lineWidth/2, lineWidth, height-(int)locY);
         g2.fillRect(0, (int)locY-lineWidth/2, (int)locX-lineWidth/2, lineWidth);
         g2.fillRect((int)locX+lineWidth/2, (int)locY-lineWidth/2, width-(int)locX, lineWidth);
    }
}