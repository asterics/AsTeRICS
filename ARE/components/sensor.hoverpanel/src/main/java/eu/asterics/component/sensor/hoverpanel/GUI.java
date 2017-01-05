
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

package eu.asterics.component.sensor.hoverpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Implements the Graphical User Interface for the <pluginname> plugin
 * 
 * @author <your name> [<your email>] Date: Time:
 */
public class GUI extends JFrame {

    // private JLabel myLabel;
    // add more GUI elements here

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the owner class instance
     */
    public GUI(final HoverPanelInstance owner, final Point location, final Dimension space) {
        super("HoverPanel");
        Font actFont = new Font("Arial", 0, owner.propFontSize);

        setLayout(new GridBagLayout());
        // setDefaultLookAndFeelDecorated(false);
        setUndecorated(true);
        setAlwaysOnTop(true);
        this.getContentPane().setBackground(getColorProperty(owner.propBackgroundColor));
        setSize(space);
        setLocation(location.x, location.y);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // .DO_NOTHING_ON_CLOSE

        JLabel captionLabel = new JLabel(owner.propCaption);
        captionLabel.setFont(actFont);
        captionLabel.setForeground(getColorProperty(owner.propTextColor));
        add(captionLabel);

        setOpacity(((float) owner.propOpacity) / 100.0f);
        setVisible(true);
    }

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