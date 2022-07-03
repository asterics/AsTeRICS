
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

package eu.asterics.component.actuator.imagebox;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the image panel for the ImageBox plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Jan 17, 2012 Time: 12:31:41
 *         AM
 */

public class ImageBoxPanel extends JPanel {
    private GUI owner;

    BufferedImage image = null;
    BufferedImage scalledImage = null;

    int scaledImageWidth = -1;
    int scaledImageHeight = -1;

    /**
     * Paints the picture
     * 
     * @param g
     *            Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(getColorProperty(owner.getBackgroundColor()));

        if (image != null) {
            double panelWidth = this.getWidth();
            double panelHeight = this.getHeight();
            double pictureWidth = image.getWidth();
            double pictureHeight = image.getHeight();

            double ratioWidth = pictureWidth / panelWidth;
            double ratioHeight = pictureHeight / panelHeight;
            double ratio = 0;

            if (ratioWidth > ratioHeight) {
                ratio = ratioWidth;
            } else {
                ratio = ratioHeight;
            }

            double scaledWidth = pictureWidth / ratio;
            double scaledHeight = pictureHeight / ratio;
            double positionX = (panelWidth - scaledWidth) / 2;
            double positionY = (panelHeight - scaledHeight) / 2;

            // if(((int)scaledWidth!=scaledImageWidth)
            // ||((int)scaledHeight!=scaledImageHeight))
            // {
            scaleImage((int) scaledWidth, (int) scaledHeight);
            // }

            // g.drawImage(dimg, (int)positionX, (int)positionY,
            // (int)scaledWidth, (int)scaledHeight,
            // getColorProperty(owner.getBackgroundColor()),null);
            g.drawImage(scalledImage, (int) positionX, (int) positionY, getColorProperty(owner.getBackgroundColor()),
                    null);
        }
    }

    /**
     * Scale image.
     * 
     * @param width
     *            the width of the image
     * @param height
     *            the height of the image
     */
    void scaleImage(int width, int height) {
        scaledImageWidth = width;
        scaledImageHeight = height;

        if (scalledImage != null) {
            scalledImage.flush();
        }
        scalledImage = null;

        scalledImage = new BufferedImage((int) width, (int) height, image.getType());
        Graphics2D g = scalledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, scaledImageWidth, scaledImageHeight, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();
    }

    /**
     * Sets the picture path.
     * 
     * @param path
     *            path of the picture
     */
    void setPicturePath(String path) {
        if (path.length() == 0) {
            if (image != null) {
                image.flush();
            }
            image = null;
        } else {
            try {
                if (image != null) {
                    image.flush();
                }
                image = null;
                File imageFile = new File(path.trim());
                image = ImageIO.read(imageFile);
            } catch (Exception ex) {
                if (image != null) {
                    image.flush();
                }
                image = null;
                AstericsErrorHandling.instance.getLogger()
                        .warning("Can not open picture: " + path + " " + ex.getMessage());
            }
        }
    }

    /**
     * Sets the object owner
     * 
     * @param owner
     *            owner of the object
     */
    void setOwner(GUI owner) {
        this.owner = owner;
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

}