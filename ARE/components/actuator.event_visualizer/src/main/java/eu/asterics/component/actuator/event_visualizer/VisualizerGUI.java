
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

package eu.asterics.component.actuator.event_visualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The GUI for the Event Visualizer instance
 * 
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: 1/9/11 Time: 11:37
 *         AM
 */
public class VisualizerGUI extends JPanel {
    private JPanel visualizerPanel;
    private Dimension visualizerPanelSize;

    private final StringBuilder stringBuilder = new StringBuilder("");
    private final JTextArea textArea = new JTextArea(stringBuilder.toString());

    public static final Dimension PREFERRED_SIZE = new Dimension(320, 240);
    public JScrollPane scroll;

    public long startTime = 0;

    VisualizerGUI(final EventVisualizerInstance owner, final Dimension space) {
        super();
        startTime = System.currentTimeMillis();
        this.setPreferredSize(new Dimension(space.width, space.height));
        design(space.width, space.height);
    }

    private void design(int width, int height) {
        // Create Panels
        visualizerPanel = new JPanel();
        visualizerPanelSize = new Dimension(width, height);

        visualizerPanel.setMaximumSize(visualizerPanelSize);
        visualizerPanel.setPreferredSize(visualizerPanelSize);

        visualizerPanel.setVisible(true);

        setLayout(new BorderLayout());
        add(new JLabel("Events"), BorderLayout.NORTH);
        scroll = new JScrollPane(textArea);
        add(scroll);
    }

    public void addEvent(final String payload) {
        // stringBuilder.append(new Date()).append(":
        // ").append(payload).append("\n");
        // textArea.setText(stringBuilder.toString());

        long millis = System.currentTimeMillis() - startTime;
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d:%03d", hour, minute, second, millis % 1000);
        textArea.append(time + ": " + payload + "\n");
        scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
    }
}