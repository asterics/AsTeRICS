
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

package eu.asterics.component.sensor.eyex;

import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;

import eu.asterics.mw.services.AREServices;

/**
 * Implements a manual calibration GUI for EyeX Eyetracker
 *
 * @author Benjamin Klaus
 */
public class ManualCalibrationGUI extends JFrame {

    public Dimension screenDimension;
    int posMarkerX = 0, posMarkerY = 0;

    
    
    Marker posMarker = new Marker(3, Color.GRAY);
    Marker gazePointMarker = new Marker(100, Color.RED);

    long lastColorChange = 0;

    float gazePointMarkerColorH = 1f;
    Color gazePointMarkerColor = Color.getHSBColor(gazePointMarkerColorH, 1f, 1f);

    ManualCalibrationGUI instance = this;
    EyeXInstance owner;
    private KeyboardFocusManager keyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

    private KeyEventPostProcessor keyProcessor = new KeyEventPostProcessor() {
        @Override
        public boolean postProcessKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                instance.stop();
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                owner.addOffsetY(-10);
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                owner.addOffsetY(10);
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                owner.addOffsetX(-10);
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                owner.addOffsetX(10);
            }
            return false;
        }
    };

    /**
     * The class constructor, initialises the GUI
     *
     * @param owner
     *            the owner class instance
     */
    public ManualCalibrationGUI(final EyeXInstance owner) {
        this.owner = owner;
        screenDimension = AREServices.instance.getScreenDimension();
        int screenX = (int) screenDimension.getWidth();
        int screenY = (int) screenDimension.getHeight();

        keyboardManager.addKeyEventPostProcessor(keyProcessor);

        /*
         * JPanel main = new JPanel(); main.setLayout(null); main.setSize(screenDimension); main.setLocation(0, 0); main.setBackground(Color.DARK_GRAY);
         * main.add(posMarker); main.add(gazePointMarker); this.add(main);
         */

        Container pane = this.getContentPane();
        pane.setLayout(null);
        pane.setBackground(Color.BLACK);
        pane.add(posMarker);
        pane.add(gazePointMarker);
        gazePointMarker.setXY(screenX / 2, screenY / 2);

        this.setSize(screenDimension);
        this.setAlwaysOnTop(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setOpacity(1.0f);
        this.setLocation(0, 0);
        this.setVisible(true);
        this.repaint();
    }

    void setPositionMarker(int xVal, int yVal) {
        this.posMarkerX = xVal;
        this.posMarkerY = yVal;
        posMarker.setXY(xVal, yVal);

        if (System.currentTimeMillis() - lastColorChange > 50) {
            lastColorChange = System.currentTimeMillis();
            gazePointMarkerColorH += 0.01f;
            gazePointMarkerColor = Color.getHSBColor(gazePointMarkerColorH, 1f, 1f);
            gazePointMarker.setColor(gazePointMarkerColor);
        }

        this.repaint();
    }

    void stop() {
        this.dispose();
        keyboardManager.removeKeyEventPostProcessor(keyProcessor);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    class Marker extends JComponent {

        int radius;
        int x, y;
        Color color;

        public Marker(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            this.setBackground(new Color(0, 0, 0, 0)); // transparent !
        }

        public void setXY(int x, int y) {
            this.x = x - radius;
            this.y = y - radius;
            this.setBounds(this.x, this.y, 2 * radius, 2 * radius);
        }
        
        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(2 * radius, 2 * radius);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int x = (radius / 2);
            int y = (radius / 2);
            g.setColor(color);
            g.fillOval(x, y, radius, radius);
        }
    }
}