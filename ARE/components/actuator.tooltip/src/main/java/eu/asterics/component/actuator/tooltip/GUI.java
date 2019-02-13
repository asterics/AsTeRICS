
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
import java.security.acl.Owner;

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
    BufferedImage image = null;
    int actTooltip = -1;
    boolean tooltipActive = false;
    String actImageFileName = "";
    TooltipInstance owner = null;

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

        this.owner = owner;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        screenWidth = gd.getDisplayMode().getWidth();
        screenHeight = gd.getDisplayMode().getHeight();

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0)); // transparent !
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setOpacity(0.5f);
    }

    /**
     * Return currently active Tooltip filename.
     * 
     * @return
     */
    String getTooltipFilename() {
        return actImageFileName;
    }

    /**
     * Tell if Tooltip is currently active (visible)
     * 
     * @return
     */
    boolean tooltipsActive() {
        return tooltipActive;
    }

    /**
     * Activates the current Tooltip image. Sends an etpTooltipActivated afterwards.
     */
    void activateTooltips() {
        if (actTooltip < 0) {
            actTooltip = owner.propTooltipStartIndex;
        }
        loadImage(actTooltip);
        owner.etpTooltipActivated.raiseEvent();
        this.tooltipActive = true;
    }

    /**
     * Deactivates the current Tooltip image. Sends an etpTooltipDeactivated afterwards.
     */
    void deactivateTooltips() {
        resetImage();
        actTooltip = owner.propTooltipStartIndex;
        owner.etpTooltipDeactivated.raiseEvent();
        showTooltip(false);
        this.tooltipActive = false;
    }

    /**
     * Selects the next index of Tooltip images. If the @see {@link #TOOLTIP_INDEX_MAX} is reached, starts with {@link #TOOLTIP_INDEX_MIN}.
     */
    void navigateNextTooltip() {
        actTooltip++;
        loadImage(actTooltip);
    }

    /**
     * Selects the previous index of Tooltip images. If the @see {@link #TOOLTIP_INDEX_MIN} is reached, starts with {@link #TOOLTIP_INDEX_MAX}. *
     */
    void navigatePreviousTooltip() {
        actTooltip--;
        loadImage(actTooltip);
    }

    /**
     * Sets the location where the Tooltip should be shown.
     * 
     * @param x
     * @param y
     */
    void setMouseXY(float x, float y) {

    }

    /**
     * Paints the Tooltip image at the defined location or at the mouse cursor location, if not set.
     */
    @Override
    public void paint(Graphics g) {
        if (image == null) {
            return;
        }

        int toolX = 0, toolY = 0;
        int mouseX = (int) owner.x;
        int mouseY = (int) owner.y;
        //if the mouse coordinates are set to -1, capture them using MouseInfo
        if (mouseX < 0 || mouseY < 0) {
            mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
            mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
        }
        //ensures mouse coordinates within the screen bounding box
        mouseX = sanitizeValue(mouseX, 0, screenWidth);
        mouseY = sanitizeValue(mouseY, 0, screenHeight);

        //Calculates the optimal position for the frame depending on screen corner and image size
        if (mouseY < image.getHeight()) {
            toolY = mouseY + 10;
        } else {
            toolY = mouseY - image.getHeight() - 10;
        }
        if (mouseX < image.getWidth()) {
            toolX = mouseX + 10;
        } else {
            toolX = mouseX - image.getWidth() - 10;
        }

        setLocation(toolX, toolY);
        setSize(image.getWidth(), image.getHeight());
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g.drawImage(image, 0, 0, null);
    }

    /**
     * Loads the image with the given nr and shows the Tooltip.
     * If .png file with the given number does not exist, tootip mode is deactivated
     * @param nr
     */
    private void loadImage(int nr) {
        String tmpFileName = owner.propTooltipFolder + "/" + nr + ".png";
        try {
            image = ImageIO.read(ResourceRegistry.getInstance().getResourceInputStream(tmpFileName, ResourceRegistry.RES_TYPE.DATA));
            actImageFileName = Integer.toString(nr);
            showTooltip(true);
        } catch (Exception ex) {
            deactivateTooltips();
            resetImage();
            AstericsErrorHandling.instance.getLogger().fine(" *****  Can not open picture: " + ex.getMessage());
        }
    }

    /**
     * Resets the current image to null.
     */
    private void resetImage() {
        image = null;
        actImageFileName = "";
    }

    /**
     * Repaints the Tooltip at the current position.
     */
    void repaintTooltip() {
        showTooltip(true);
    }

    /**
     * Toggles visibility of the Tooltip.
     * 
     * @param showTooltip
     */
    private void showTooltip(final boolean showTooltip) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (showTooltip) {
                    setVisible(true);
                    setAlwaysOnTop(true);
                    repaint();
                } else {
                    setAlwaysOnTop(false);
                    setVisible(false);
                    repaint();
                }
            }
        });
    }

    /**
     * Checks screen boundary boxes and ensures that the given values are within that boundary.
     * 
     * @param value
     * @param minValue
     * @param maxValue
     * @return
     */
    private int sanitizeValue(int value, int minValue, int maxValue) {
        if (value < minValue) {
            return minValue;
        } else if (value > maxValue) {
            return maxValue;
        }
        return value;
    }
}