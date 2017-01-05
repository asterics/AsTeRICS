package eu.asterics.component.actuator.gui_tester;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
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
 *    This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

/**
 * @author Konstantinos Kakousis This class is part of the gui_tester plugin and
 *         demonstrates how Java Graphics can be created and diplayed on the
 *         AsTeRICS Desktop.
 * 
 *         Date: Sep 16, 2011
 */
public class PanelWithGraphics extends JPanel {
    private int X_MARGIN, Y_MARGIN, W_OFFSET, H_OFFSET, CHARS;
    private int x, y, w, h;

    public PanelWithGraphics() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        // setBorder(BorderFactory.createTitledBorder("Graphics"));
    }

    // Each time the position changes we reset the coordinates so that our
    // graphics are always painted inside the parent container.
    @Override
    public void repaint(int x, int y, int w, int h) {
        X_MARGIN = (int) (0.05 * w);
        Y_MARGIN = (int) (0.1 * w);
        W_OFFSET = (int) (0.1 * w);
        H_OFFSET = (int) (0.17 * h);
        this.x = x + X_MARGIN;
        this.y = y + Y_MARGIN;
        this.w = w - W_OFFSET;
        this.h = h - H_OFFSET;
        super.repaint();
    }

    /**
     * Overrides the paintComponent method of JPanel. It is called every time a
     * repaint is called. For this example we paint a rectangle as a frame, a
     * string as title and random circles
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // set color to black
        g.setColor(Color.BLACK);

        // draw a rectangle
        g.drawRect(x, y, w, h);

        for (int i = 0; i < 20; i++) {
            // set random color
            g.setColor(
                    new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));

            g.fillOval(randomNumber(x, w + x - X_MARGIN, 1), randomNumber(y, h + y - Y_MARGIN, 1), W_OFFSET, W_OFFSET);
        }
        // set color to black
        g.setColor(Color.RED);

        // draw a string: the font size must be defined in terms of the
        // characters we wish to paint and the available space.
        CHARS = 25;
        int fontSize = y - x / CHARS;
        g2d.setFont(new Font("Arial", 0, fontSize));
        g.drawString("AsTeRICS Painting", x, y);
    }

    /**
     * Helper method that generates random numbers in the given range
     * 
     * @param min
     * @param max
     * @param offset
     * @return
     */
    private int randomNumber(int min, int max, int offset) {
        int num = min + (int) (Math.random() * ((max - min) + offset));

        return num;
    }

}
