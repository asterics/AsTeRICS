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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.proxy.remoteconsumer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class RemoteConsumerGUI extends JInternalFrame {
    private final JButton openButton = new JButton("Open Socket");
    private final JButton closeButton = new JButton("close Socket");
    private final JButton closeConnButton = new JButton("close Connection");

    public RemoteConsumerGUI(final RemoteConsumerInstance instance) {
        super("OSKA RemoteConsumer GUI");

        openButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // instance.connectRemoteComponent();
                openButton.setEnabled(false);
                closeButton.setEnabled(false);

            }
        });
        closeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // instance.disconnectRemoteComponent();
                closeButton.setEnabled(false);
                openButton.setEnabled(true);
            }

        });

        closeConnButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                instance.closeConnection();

            }

        });

        closeButton.setEnabled(false);
        final JPanel panel = new JPanel();
        final BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(boxLayout);
        panel.add(closeConnButton);
        add(panel);
        pack();
    }

    public void disableOpenSocket() {
        openButton.setEnabled(false);

    }

    public boolean isClosedSocketEnabled() {

        return closeButton.isEnabled();
    }

    public void enableClosedSocket(boolean b) {
        if (!openButton.isEnabled()) {
            closeButton.setEnabled(b);
        }
    }

}