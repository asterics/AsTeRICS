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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Konstantinos Kakousis This class generates a JFrame for setting ARE
 *         options
 * 
 *         Date: Oct 10, 2011
 */
public class AboutFrame extends JDialog {
    static String ICON_PATH = "/images/icon.gif";
    JDialog thisDialog;
    AstericsGUI parent;

    public AboutFrame(AstericsGUI parent, JFrame mainFrame) {
        super(mainFrame);
        thisDialog = this;
        this.parent = parent;
        final URL iconPath = parent.getBundleContext().getBundle().getResource(ICON_PATH);
        setIconImage(Toolkit.getDefaultToolkit().getImage(iconPath));
        setTitle("About AsTeRICS");
        // setPreferredSize(new
        // Dimension(OPTIONS_PRAME_WIDTH,OPTIONS_PRAME_HEIGHT));
        this.setLocationRelativeTo(mainFrame);
        try {
            BufferedImage myPicture;

            final URL astericsImage = parent.getBundleContext().getBundle().getResource("/images/asterics.png");

            myPicture = ImageIO.read(astericsImage);
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            JLabel version = new JLabel("AsTeRICS ARE Version 2.2");
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(picLabel);
            panel.add(version);
            add(panel, BorderLayout.CENTER);
            add(makeButtonsPanel(), BorderLayout.SOUTH);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void showFrame() {
        pack();
        this.setLocation(parent.getFrame().getLocation());
        setVisible(true);
    }

    public void hideFrame() {
        setVisible(false);
    }

    private JComponent makeButtonsPanel() {
        JPanel panel = new JPanel(false);

        JButton cancelBbutton = new JButton("Close");
        cancelBbutton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                thisDialog.dispose();

            }
        });

        panel.add(cancelBbutton);
        return panel;

    }

}
