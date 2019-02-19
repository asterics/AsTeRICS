
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
import java.util.Arrays;

import javax.swing.*;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the Graphical User Interface for the CrosshairCursorControl plugin
 *
 * @author Chris, Date: 2019-01-20
 */
public class GUI {

    int width = 0, height = 0;
    int lineWidth = 0;

    int locX = 0;
    int locY = 0;

    private boolean highlightXAxis = false;
    private boolean highlightYAxis = true;
    private boolean currentHighlightXAxis = false;
    private boolean currentHighlightYAxis = false;
    private boolean highlightClick = false;
    private boolean active = true;

    private JFrame frameLeft;
    private JFrame frameRight;
    private JFrame frameUp;
    private JFrame frameDown;
    private java.util.List<JFrame> frames;
    private boolean taskbarOffset = false;
    // private JLabel myLabel;
    // add more GUI elements here

    /**
     * The class constructor, initialises the GUI
     *
     * @param owner
     *            the owner class instance
     * @param dim
     *            the dimension of the screen
     * @param lineWidth
     *            the width of horizontal and vertial crosshair lines
     */
    public GUI(final CrosshairCursorControlInstance owner, final Dimension dim, final int lineWidth, boolean taskbarOffset) {
        this.lineWidth = lineWidth;
        width = dim.width;
        height = dim.height;
        this.taskbarOffset = taskbarOffset;

        frameLeft = new JFrame("CrosshairLeft");
        frameRight = new JFrame("CrosshairRight");
        frameUp = new JFrame("CrosshairUp");
        frameDown = new JFrame("CrosshairDown");
        frames = Arrays.asList(frameLeft, frameRight, frameUp, frameDown);

        for (JFrame frame : frames) {
            frame.setUndecorated(true);
            frame.setAlwaysOnTop(true);
            frame.setBackground(new Color(0, 0, 0, 0)); // transparent !
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setOpacity(0.5f);
            frame.setVisible(true);
        }

        Point location = MouseInfo.getPointerInfo().getLocation();
        locX = location.x;
        locY = location.y;
        repaintInternal(true, 0);
    }

    void resetAxis() {
        highlightXAxis = false;
        highlightYAxis = false;
        if (currentHighlightXAxis || currentHighlightYAxis) {
            repaintInternal();
        }
    }

    void toggleAxis() {
        highlightXAxis = !highlightXAxis;
        highlightYAxis = !highlightYAxis;
        repaintInternal();
    }

    void setXAxisHighlight(boolean highlightXAxis) {
        this.highlightXAxis = highlightXAxis;
        if (currentHighlightXAxis != highlightXAxis) {
            repaintInternal();
        }
    }

    void setYAxisHighlight(boolean highlightYAxis) {
        this.highlightYAxis = highlightYAxis;
        if (currentHighlightYAxis != highlightYAxis) {
            repaintInternal();
        }
    }

    void doAxisClickHighlight() {
        highlightClick = true;
        repaintInternal();
    }

    void setCursor(int x, int y) {
        locX = x;
        locY = y;
        repaintInternal();
    }

    void setActive(boolean active) {
        this.active = active;
    }

    /**
     * resizes, moves and repaints all frames.
     * @param asynchronous if true SwingUtils.invokeLater() is used for repainting, otherwise SwingUtils.invokeAndWait()
     * @param sleepMs if > 0 the current thread is interrupted for the given amount of milliseconds
     */
    private void repaintInternal(boolean asynchronous, final int sleepMs) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (sleepMs > 0) {
                    sleepInternal(sleepMs);
                }
                doResizeAndPositining();
                setAllAlwaysOnTop(true);
                repaintAll();
            }
        };
        if (asynchronous) {
            SwingUtilities.invokeLater(r);
        } else {
            synchronized (this) {
                try {
                    SwingUtilities.invokeAndWait(r);
                } catch (Exception e) {
                    AstericsErrorHandling.instance.getLogger().warning("invokeAndWait error: " + e.getMessage());
                }
            }
        }
    }

    private void repaintInternal() {
        repaintInternal(false, 0);
    }

    private void sleepInternal(long ms) {
        try {
            Thread.currentThread().sleep(ms);
        } catch (InterruptedException e) {
            AstericsErrorHandling.instance.getLogger().warning("GUI sleep error: " + e.getMessage());
        }
    }

    void setAllVisible(boolean visible) {
        for (JFrame frame : frames) {
            frame.setVisible(visible);
        }
    }

    void disposeAll() {
        for (JFrame frame : frames) {
            frame.dispose();
        }
    }

    private void repaintAll() {
        for (JFrame frame : frames) {
            frame.repaint();
        }
    }

    private void setAllAlwaysOnTop(boolean alwaysOnTop) {
        for (JFrame frame : frames) {
            frame.setAlwaysOnTop(alwaysOnTop);
        }
    }

    private void doResizeAndPositining() {
        Color xAxisColor, yAxisColor;
        xAxisColor = Color.GRAY;
        yAxisColor = Color.GRAY;
        currentHighlightXAxis = false;
        currentHighlightYAxis = false;
        if (highlightXAxis && active) {
            xAxisColor = Color.RED;
            currentHighlightXAxis = true;
        }
        if (highlightYAxis && active) {
            yAxisColor = Color.RED;
            currentHighlightYAxis = true;
        }
        if (highlightClick) {
            xAxisColor = Color.GREEN;
            yAxisColor = Color.GREEN;
        }

        int usedWidth = width;
        int usedHeight = height;
        int offsetX = 0;
        int offsetY = 0;
        if (taskbarOffset) {
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(frameDown.getGraphicsConfiguration());
            offsetX = insets.left;
            offsetY = insets.top;
            usedWidth -= (insets.left + insets.right);
            usedHeight -= (insets.top + insets.bottom);
        }

        int space = Math.max(lineWidth / 2, 4);
        frameLeft.setSize(locX - space - offsetX, lineWidth);
        frameRight.setSize(usedWidth - locX - space, lineWidth);
        frameUp.setSize(lineWidth, locY - space - offsetY);
        frameDown.setSize(lineWidth, usedHeight - locY - space);

        frameLeft.setLocation(0, locY - lineWidth / 2);
        frameRight.setLocation(offsetX + locX + space, locY - lineWidth / 2);
        frameUp.setLocation(locX - lineWidth / 2, 0);
        frameDown.setLocation(locX - lineWidth / 2, offsetY + locY + space);

        frameLeft.setBackground(xAxisColor);
        frameRight.setBackground(xAxisColor);
        frameUp.setBackground(yAxisColor);
        frameDown.setBackground(yAxisColor);
        frameLeft.getContentPane().setBackground(xAxisColor);
        frameRight.getContentPane().setBackground(xAxisColor);
        frameUp.getContentPane().setBackground(yAxisColor);
        frameDown.getContentPane().setBackground(yAxisColor);

        if (highlightClick) {
            highlightClick = false;
            repaintInternal(true, 150);
        }
    }
}