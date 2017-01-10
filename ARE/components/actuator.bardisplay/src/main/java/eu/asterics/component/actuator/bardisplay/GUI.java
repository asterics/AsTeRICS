
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

package eu.asterics.component.actuator.bardisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.text.DecimalFormat;

import javax.swing.JPanel;

/**
 * Implements the Graphical User Interface for the Bardisplay actuator plugin
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 7, 2011 Time:
 *         10:55:05 AM
 */
public class GUI extends JPanel {
    // private MyPanel paintPanel = new MyPanel();

    private JPanel barPanel, paintPanel;
    private Dimension barPanelSize;

    private double inputBuffer = 0;
    private double drawValue = 0;
    private int inputCount = 0;

    private double actMin;
    private double actMax;

    private GUI thisPanel;
    private double drawFactor = 1;
    private final BardisplayInstance owner;
    private DecimalFormat format = new DecimalFormat("#.##");

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the Bardisplay instance
     */
    public GUI(final BardisplayInstance owner, final Dimension space) {
        super();
        this.owner = owner;
        actMin = owner.propMin;
        actMax = owner.propMax;

        this.setPreferredSize(new Dimension(space.width, space.height));
        design(space.width, space.height);

    }

    /**
     * The GUI consists of one panel with a drawing area for the bar graph.
     * 
     * @param width
     * @param height
     */
    private void design(int width, int height) {
        // Create Panels
        barPanel = new JPanel();
        barPanelSize = new Dimension(width, height);

        barPanel.setMaximumSize(barPanelSize);
        barPanel.setPreferredSize(barPanelSize);

        // title = new JLabel("BarDisplay");
        // barPanel.add(title);
        barPanel.setVisible(true);

        // this.setBorder(new TitledBorder(owner.propCaption));

        this.setLayout(new BorderLayout());
        add(barPanel, BorderLayout.PAGE_START);

        // add(barCaption, BorderLayout.SOUTH);

        thisPanel = this;
        barPanel.addHierarchyListener(new HierarchyListener() {

            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) != 0 && barPanel.isShowing()) {

                    paintPanel = new PanelWithBarGraph();
                    paintPanel.setMaximumSize(barPanelSize);
                    paintPanel.setPreferredSize(barPanelSize);
                    // rightPanel.removeAll();
                    barPanel.add(paintPanel);
                    paintPanel.repaint(barPanel.getX(), barPanel.getY(), barPanelSize.width, barPanelSize.height);
                    barPanel.repaint();
                    barPanel.revalidate();
                    thisPanel.revalidate();
                }
            }
        });

    }

    /**
     * The drawing Panel
     */
    class PanelWithBarGraph extends JPanel {
        // private int X_MARGIN, Y_MARGIN, W_OFFSET, H_OFFSET;
        private int x, y, w, h;
        private int actheight;
        Font actFont;

        public PanelWithBarGraph() {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            // setBorder(BorderFactory.createLineBorder(Color.black));

            int fontSize = owner.propFontSize; // (w-x)/CHARS;
            actFont = new Font("Arial", 0, fontSize);
        }

        // Each time the position changes we reset the coordinates so that our
        // graphics are always painted inside the parent container.
        @Override
        public void repaint(int x, int y, int w, int h) {
            // X_MARGIN = (int) (0.05 * w);
            // Y_MARGIN = (int) (0.1 * w);
            // W_OFFSET = (int) (0.05 * w);
            // H_OFFSET = (int) (0.35 * h);

            this.x = x;// +X_MARGIN;
            this.y = y;// +Y_MARGIN;
            this.w = w - 1;// -W_OFFSET;
            this.h = h - 6;// -H_OFFSET;

            super.repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            // RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setFont(actFont);

            // get metrics from the graphics
            FontMetrics metrics = g.getFontMetrics(actFont);
            // get the height of a line of text in this font and render context
            int txtHeight = metrics.getHeight();
            // get the advance of my text in this font and render context

            drawFactor = 1 / (actMax - actMin) * h;
            actheight = (int) ((drawValue - actMin) * drawFactor);

            setBackground(getColorProperty(owner.propBackgroundColor));

            g.drawRect(x, y, w, h);

            g.setColor(getColorProperty(owner.propBarColor));
            g.fillRect(x + 1, (int) (y + h - 1 - actheight), w - 1, actheight);

            g.setColor(getColorProperty(owner.propGridColor));
            g.drawString(owner.propCaption, x + w - metrics.stringWidth(owner.propCaption), y + txtHeight);

            g.drawString("max:" + format.format(actMax), x + 1, y + txtHeight);
            g.drawString("min:" + format.format(actMin), x + 1, y + h - 2);

            if (owner.propIntegerDisplay) {
                // g.drawString(" "+(int)(drawValue+0.5),x+w/2,y+h-actheight);
                g.drawString(" " + (int) (drawValue + 0.5), x + w - metrics.stringWidth(" " + (int) (drawValue + 0.5)),
                        y + h - 2);
            } else {
                // g.drawString(format.format(drawValue),x+w/2,y+h-actheight);
                g.drawString(format.format(drawValue), x + w - metrics.stringWidth(format.format(drawValue)),
                        y + h - 2);
            }

            if (owner.propDisplayThreshold) {
                g.setColor(getColorProperty(owner.propGridColor));
                g.drawString(format.format(owner.propThreshold), x + 1,
                        (int) (y + h - (owner.propThreshold - actMin) * drawFactor));
                g.drawRect(x, (int) (y + h - (owner.propThreshold - actMin) * drawFactor), w - 1, 2);
            }

        }
    }

    /**
     * returns a color for a given color index
     * 
     * @param index
     *            the color index
     * @return the associated color
     */
    Color getColorProperty(int index) {
        switch (index) {
        case 0:
            return (Color.BLACK);
        case 1:
            return (Color.BLUE);
        case 2:
            return (Color.CYAN);
        case 3:
            return (Color.DARK_GRAY);
        case 4:
            return (Color.GRAY);
        case 5:
            return (Color.GREEN);
        case 6:
            return (Color.LIGHT_GRAY);
        case 7:
            return (Color.MAGENTA);
        case 8:
            return (Color.ORANGE);
        case 9:
            return (Color.PINK);
        case 10:
            return (Color.RED);
        case 11:
            return (Color.WHITE);
        case 12:
            return (Color.YELLOW);
        default:
            return (Color.BLUE);
        }
    }

    /**
     * paints the bar graph after a new input value has arrived
     */
    void updateInput(final double value) {
        inputBuffer += value;
        inputCount++;
        if (inputCount > owner.propDisplayBuffer) {
            drawValue = inputBuffer / inputCount;
            if (owner.propMode == owner.MODE_CLIPMINMAX) {
                if (drawValue > actMax) {
                    drawValue = actMax;
                }
                if (drawValue < actMin) {
                    drawValue = actMin;
                }
            } else if (owner.propMode == owner.MODE_AUTOMINMAX) {
                if (actMin > drawValue) {
                    actMin = drawValue;
                }
                if (actMax < drawValue) {
                    actMax = drawValue;
                }
            }
            inputCount = 0;
            inputBuffer = 0;
            thisPanel.repaint();

        }
    }

    /**
     * repaints the panel and resets min and max value
     */
    void clearClick() {
        inputCount = 0;
        inputBuffer = 0;
        actMin = owner.propMin;
        actMax = owner.propMax;
        thisPanel.repaint();
    }

}