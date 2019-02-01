
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

package eu.asterics.component.actuator.tooltip;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.*;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.ResourceRegistry;

/**
 * Implements the Graphical User Interface for the Tooltip plugin
 *
 * @author Benjamin Klaus
 */
public class GUI extends JFrame {

    int width, height;
    BufferedImage image = null;
    int actTooltip = 0;
    int tooltipStartIndex = 0;
    boolean tooltipActive = false;
    long tooltipTime = 0;
    String tooltipFolder = "";
    String actImageFileName = "";
    float mouseX = 0;
    float mouseY = 0;

    private int screenWidth;
    private int screenHeight;

    /**
     * The class constructor, initialises the GUI
     *
     * @param owner
     *            the owner class instance
     */
    public GUI(final TooltipInstance owner) {
        super("TooltipPanel");

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        Dimension dim = new Dimension(width, height);

        screenWidth = (int) dim.getWidth();
        screenHeight = (int) dim.getHeight();

        setUndecorated(true);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0)); // transparent !
        setSize(dim);
        width = dim.width;
        height = dim.height;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setOpacity(0.5f);
        setVisible(true);
        setLocation(0, 0);
        repaintInternal();
    }

    String getTooltipFilename() {
        return actImageFileName;
    }

    boolean tooltipsActive() {
        return tooltipActive;
    }

    void activateTooltips(String tooltipFolder, int startIndex) {
        this.tooltipFolder = tooltipFolder + "/";
        this.tooltipStartIndex = startIndex;
        actTooltip = tooltipStartIndex;
        this.tooltipActive = true;
        loadImage(actTooltip);
    }

    void deactivateTooltips() {
        actTooltip = tooltipStartIndex;
        this.tooltipActive = false;
        resetImage();
        repaintInternal();
    }

    void navigateTooltips(float dx) {
        if (System.currentTimeMillis() - tooltipTime > 100) {
            if (dx > 0)
                actTooltip++;
            else {
                actTooltip--;
            }
            loadImage(actTooltip);
        }
    }

    void navigateNextTooltip() {
        navigateTooltips(1);
    }

    void navigatePreviousTooltip() {
        navigateTooltips(-1);
    }

    void setOnTop() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setAlwaysOnTop(false);
                repaint();
                setAlwaysOnTop(true);
                repaint();
            }
        });
    }

    void setMouseXY(float x, float y) {
        mouseX = (float) normalizeValue(x, 0, screenWidth);
        mouseY = (float) normalizeValue(y, 0, screenHeight);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;

        if (image != null) {
            int toolX, toolY;
            if ((int) mouseY < image.getHeight()) {
                toolY = (int) mouseY + 10;
            } else {
                toolY = (int) mouseY - image.getHeight() - 10;
            }
            if ((int) mouseX < image.getWidth()) {
                toolX = (int) mouseX + 10;
            } else {
                toolX = (int) mouseX - image.getWidth() - 10;
            }

            g.drawImage(image, toolX, toolY, null);
        }
    }

    private void loadImage(int nr) {
        String tmpFileName = tooltipFolder + actTooltip + ".png";
        try {
            URI myURI = ResourceRegistry.getInstance().getResource(tmpFileName, ResourceRegistry.RES_TYPE.DATA);
            File imageFile = new File(myURI);
            image = ImageIO.read(imageFile);
            actImageFileName = Integer.toString(nr);
            tooltipTime = System.currentTimeMillis();
        } catch (Exception ex) {
            deactivateTooltips();
            AstericsErrorHandling.instance.getLogger().fine(" *****  Can not open picture: " + ex.getMessage());
        }
        repaintInternal();
    }

    private void resetImage() {
        image = null;
        actImageFileName = "";
    }

    private void repaintInternal() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setAlwaysOnTop(false);
                repaint();
                setAlwaysOnTop(true);
                repaint();
            }
        });
    }

    private double normalizeValue(double value, double minValue, double maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        }
        return value;
    }
}