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
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 *
 *
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * class to encapsulate a label with different button on hovering, abstract method for mouse move action must be implemented
 */
public abstract class ControlPanelLabel extends JLabel {

    /**
     * creates a new ControlPanelLabel
     *
     * @param iconPath path to the image that should be used for the normal icon
     * @param iconPathHover path to the image that should be used for the icon on mouse hover over the label
     * @param tooltipText tooltip text to show on hover
     * @throws IOException if images from given URL paths cannot be read
     */
    public ControlPanelLabel(URL iconPath, URL iconPathHover, String tooltipText) throws IOException {
        super();
        this.setToolTipText(tooltipText);
        BufferedImage iconImage = ImageIO.read(iconPath);
        BufferedImage iconImageHover = ImageIO.read(iconPathHover);
        final ImageIcon iconNormal = new ImageIcon(iconImage);
        final ImageIcon iconHover = new ImageIcon(iconImageHover);
        this.setIcon(iconNormal);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClick();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(iconHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(iconNormal);

            }
        });
    }

    /**
     * defines the action that is perfomed on a mouse click on this label
     */
    public abstract void onMouseClick();
}
