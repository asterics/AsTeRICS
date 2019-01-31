
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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.*;

/**
 * Implements the Graphical User Interface for the AngularCursorControl plugin
 * 
 * @author Chris, Date: 2019-01-20
 */
public class GUI extends JFrame {

    private int screenWidth = 0;
    private int screenHeight = 0;
    float x = 0.0f;
    int len = 0, width = 0;
    float angle = 0.0f;
    Robot rob;
    double remainX = 0;
    double remainY = 0;

    double locX = 0;
    double locY = 0;
    private boolean wrapAround = false;
    private boolean active = true;

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the owner class instance
     * @param dim
     *            the dimension of the screen
     */
    public GUI(final AngularCursorControlInstance owner, final Dimension dim, boolean wrapAround) {
        super("CursorMovementPanel");
        this.wrapAround = wrapAround;
        len = dim.height;
        width = dim.width;
        setUndecorated(true);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(new Dimension(len * 2 + width, len * 2 + width));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setOpacity(0.5f);
        setVisible(true);
        setShape(0.0f);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        try {
            rob = new Robot();
            rob.setAutoDelay(0);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        Point location = MouseInfo.getPointerInfo().getLocation();
        locX = location.x;
        locY = location.y;

    }

    void setActive(boolean active) {
        this.active = active;
    }

    void setOnTop() {
        setAlwaysOnTop(false);
        repaint();
        setAlwaysOnTop(true);
        repaint();

    }

    void setShape(float angle) {
        this.angle = angle;
        // Point location = MouseInfo.getPointerInfo().getLocation();
        // setLocation(location.x-len, location.y-len);
        setLocation((int) locX - len, (int) locY - len);
        repaint();

        /*
         * AffineTransform tx = new AffineTransform(); tx.translate(len,len); tx.rotate(angle); tx.translate(-len,-len); Rectangle shape = new
         * Rectangle(len-width/2, 0, width, len-10); Shape newShape = tx.createTransformedShape(shape); com.sun.awt.AWTUtilities .setWindowShape(this,
         * newShape);
         */
    }

    void moveCursor(double dx, double dy) {

        locX += dx;
        locY += dy;
        locX = normalizeValue(locX, 0, screenWidth, wrapAround);
        locY = normalizeValue(locY, 0, screenHeight, wrapAround);

        try {
            Robot r = new Robot();
            r.mouseMove((int) locX, (int) locY);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        setLocation((int) locX - len, (int) locY - len);
    }

    double getCursorX() {
        return locX;
    }

    double getCursorY() {
        return locY;
    }

    void setWrapAround(boolean wrapAround) {
        this.wrapAround = wrapAround;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(active ? Color.RED : Color.GRAY);

        int x1Points[] = { len, len + width, len - width, len };
        int y1Points[] = { 0, len - 10, len - 10, 0 };
        GeneralPath polygon = new GeneralPath(GeneralPath.WIND_EVEN_ODD, x1Points.length);
        polygon.moveTo(x1Points[0], y1Points[0]);

        for (int index = 1; index < x1Points.length; index++) {
            polygon.lineTo(x1Points[index], y1Points[index]);
        }
        ;

        polygon.closePath();

        AffineTransform tx = new AffineTransform();
        tx.translate(len, len);
        tx.rotate(angle);
        tx.translate(-len, -len);
        Shape newShape = tx.createTransformedShape(polygon);
        g2.fill(newShape);
    }

    /**
     * normalizes the given value to a given range.
     * 
     * @param value
     *            the value to normalize
     * @param minValue
     * @param maxValue
     * @param wrapAround
     *            if true, a value smaller than minValue results in maxValue and vice versa
     * @return
     */
    private double normalizeValue(double value, double minValue, double maxValue, boolean wrapAround) {
        if (value < minValue) {
            return wrapAround ? maxValue : minValue;
        } else if (value > maxValue) {
            return wrapAround ? minValue : maxValue;
        }
        return value;
    }
}