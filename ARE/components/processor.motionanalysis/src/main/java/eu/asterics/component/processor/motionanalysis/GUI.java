
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

package eu.asterics.component.processor.motionanalysis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Implements the GUI for the Oscilloscope plugin
 * 
 * 
 * @author Armin Schmoldas [armin.schmoldas@technikum-wien.at] Date: Apr 21,
 *         2015 Time: 12:17:02 PM
 */
public class GUI extends JPanel {
    private JPanel osciPanel, paintPanel;
    private Dimension osciPanelSize;
    private GUI thisPanel;

    public final int MAX_SIZE = 3000;
    private final int DRAWINGMODE_AUTOUPDATE = 0;
    private int paintCount = 0;

    public double[] loadchnValues = new double[MAX_SIZE];
    public double[] drawchnValues = new double[MAX_SIZE];
    public double[] loadValues;

    public ArrayList<Double> allloadValues = new ArrayList<Double>();
    public ArrayList<Double> alldrawValues = new ArrayList<Double>();
    public ArrayList<double[]> roundloadValues = new ArrayList<double[]>();

    private int x, y, w, h;
    private int chnloadPos = 0;
    private int chndrawPos = 0;
    private int xSize = 300;
    private int loadcount = 0;

    private double repeat = 1;
    private double drawFactorX = 1;
    private double drawFactorY = 1;

    // public double chnMin = owner.propMin;
    // public double chnMax = Double.MIN_VALUE;

    private boolean save = false;
    private String valueToWrite = "";

    public BufferedWriter out = null;

    private final MotionAnalysisInstance owner;
    private DecimalFormat format = new DecimalFormat("#.##");

    /**
     * The class constructor, initializes the GUI
     * 
     * @param owner
     *            the Oscilloscope instance
     */
    public GUI(final MotionAnalysisInstance owner, final Dimension space) {
        super();
        this.owner = owner;

        this.setPreferredSize(new Dimension(space.width, space.height));
        design(space.width, space.height);

    }

    /**
     * The GUI consists of one panel with a drawing area for the bar graph.
     * 
     * @param width
     * @param height
     */
    private void design(int width, int height) {
        // Create Panels
        osciPanel = new JPanel();
        osciPanelSize = new Dimension(width, height);

        osciPanel.setMaximumSize(osciPanelSize);
        osciPanel.setPreferredSize(osciPanelSize);

        // title = new JLabel("Oscilloscope Display");
        // osciPanel.add(title);
        osciPanel.setVisible(true);

        // this.setBorder(new TitledBorder(owner.propCaption));

        this.setLayout(new BorderLayout());
        add(osciPanel, BorderLayout.PAGE_START);

        thisPanel = this;

        if (owner.propDrawingMode == DRAWINGMODE_AUTOUPDATE) {
            owner.propMin = Double.MAX_VALUE;
            owner.propMax = Double.MIN_VALUE;
        } else {
            owner.propMin = owner.propMin;
            owner.propMax = owner.propMax;
        }

        // It is essential to wait until the all panels are shown on
        // the screen before we call the class that generates the graphics.
        // This is because we need the final screen location for painting

        osciPanel.addHierarchyListener(new HierarchyListener() {

            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) != 0 && osciPanel.isShowing()) {

                    paintPanel = new PanelWithOsciGraph();
                    paintPanel.setMaximumSize(osciPanelSize);
                    paintPanel.setPreferredSize(osciPanelSize);
                    // rightPanel.removeAll();
                    osciPanel.add(paintPanel);
                    paintPanel.repaint(osciPanel.getX(), osciPanel.getY(), osciPanelSize.width, osciPanelSize.height);
                    osciPanel.repaint();
                    osciPanel.revalidate();
                    thisPanel.revalidate();
                }
            }
        });

    }

    /**
     * The drawing panel.
     */

    class PanelWithOsciGraph extends JPanel {

        Font actFont;

        public PanelWithOsciGraph() {
            setBorder(BorderFactory.createLineBorder(Color.black));
            setLayout(new FlowLayout(FlowLayout.LEFT));

            int fontSize = owner.propFontSize;
            actFont = new Font("Arial", 0, fontSize);
        }

        // Each time the position changes we reset the coordinates so that our
        // graphics are always painted inside the parent container.
        @Override
        public void repaint(int xi, int yi, int wi, int hi) {
            x = xi;
            y = yi;
            w = wi - 1;
            h = hi - 6;

            xSize = w;

            super.repaint();
        }

        /**
         * Draws all the graphics of the graph like the line in the middle and
         * sets the colors of it and the background color
         */

        @Override
        public synchronized void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            int new_y, old_y = 0;

            super.paintComponent(g);
            g2d.setFont(actFont);
            FontMetrics metrics = g.getFontMetrics(actFont);
            int txtHeight = metrics.getHeight();
            metrics.stringWidth(owner.propCaption);

            if (owner.propMax != owner.propMin) {
                drawFactorY = h / (owner.propMax - owner.propMin);
            }
            if (xSize > 0) {
                drawFactorX = w / xSize;
            }

            setBackground(getColorProperty(owner.propBackgroundColor));

            g.setColor(getColorProperty(owner.propGridColor));
            g.drawString(owner.propCaption, x + w - metrics.stringWidth(owner.propCaption), y + txtHeight);

            if (owner.propMax != Double.MIN_VALUE) {
                g.drawString("max:" + format.format(owner.propMax), 5, txtHeight);
                g.drawString("min:" + format.format(owner.propMin), 5, y + h - 2);
            }

            try {
                g.drawRect(x, y, w, h);
                g.drawLine(x, y + h / 2, x + w, y + h / 2);

                g.setColor(getColorProperty(owner.propLoadchannelColor));
                for (int i = 0; i < chnloadPos; i++) {
                    new_y = y + h - (int) ((loadchnValues[i] - owner.propMin) * drawFactorY);
                    if (i > 0) {
                        g.drawLine(x + (int) (i * drawFactorX), old_y, x + (int) ((i + 1) * drawFactorX), new_y);
                    }
                    old_y = new_y;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("exception in paint paintComponent, MotioinAnalysis");
            }
            try {
                g.setColor(getColorProperty(owner.propDrawchannelColor));
                for (int i = 0; i < chndrawPos; i++) {
                    new_y = y + h - (int) ((drawchnValues[i] - owner.propMin) * drawFactorY);
                    if (i > 0) {
                        g.drawLine(x + (int) (i * drawFactorX), old_y, x + (int) ((i + 1) * drawFactorX), new_y);
                    }
                    old_y = new_y;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("exception in paint paintComponent, MotioinAnalysis");
            }
        }
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

    /**
     * updates load channel values, draws if paint buffer full
     * 
     * @param actValue
     */
    synchronized void updateloadChn(double actValue) {
        if (owner.propMin > actValue) {
            owner.propMin = actValue;
        }
        if (owner.propMax < actValue) {
            owner.propMax = actValue;
        }

        loadchnValues[chnloadPos] = actValue;
        chnloadPos++;
        if (chnloadPos >= xSize) {
            chnloadPos = 0;
        }

        if (++paintCount > owner.propDisplayBuffer) {
            paintCount = 0;
            if (paintPanel != null) {
                paintPanel.setBackground(getColorProperty(owner.propBackgroundColor));
                paintPanel.repaint();
            }
        }
    }

    /**
     * updates draw channel values, draws if paint buffer full
     * 
     * @param actValue
     */
    synchronized void updatedrawChn(double actValue) {
        if (owner.propDrawingMode == DRAWINGMODE_AUTOUPDATE) {
            if (owner.propMin > actValue) {
                owner.propMin = actValue;
            }
            if (owner.propMax < actValue) {
                owner.propMax = actValue;
            }
        } else {
            if (save == true) {
                if (owner.propMin > actValue) {
                    owner.propMin = actValue;
                }
                if (owner.propMax < actValue) {
                    owner.propMax = actValue;
                }
            } else {
                if (owner.propMin > actValue) {
                    actValue = owner.propMin;
                }
                if (owner.propMax < actValue) {
                    actValue = owner.propMax;
                }
            }
        }

        drawchnValues[chndrawPos] = actValue;
        alldrawValues.add(actValue);
        if (save == false) {
            owner.percent();
        }
        valueToWrite = Double.toString(actValue);

        // if a save is true the actValue is saved to the file

        if (save == true) {
            try {
                out.write(valueToWrite + System.getProperty("line.separator"));
            } catch (IOException e) {
                AstericsErrorHandling.instance.getLogger().severe("Error writing file");
            }
        }

        chndrawPos++;

        // if a exercise is done and the list of load values is the same length
        // of the list of draw values
        // the exercises is stopped and the compare function is called

        if (save == false) {
            if (roundloadValues.get(loadcount - 1).length == chndrawPos
                    && roundloadValues.get(loadcount - 1).length != w - 1) {
                owner.pause();
                owner.startable = false;
                owner.compare();
            }
        }

        // if the draw channel comes to the end of the graph the position is set
        // to 0
        // and if it is not in save mode the next load values are loaded

        if (chndrawPos >= xSize) {
            chndrawPos = 0;
            if (save == false) {
                dynload();
                owner.resume();
            }
        }

        if (++paintCount > owner.propDisplayBuffer) {
            paintCount = 0;
            if (paintPanel != null) {
                paintPanel.setBackground(getColorProperty(owner.propBackgroundColor));
                paintPanel.repaint();
            }
        }
    }

    /**
     * clears the drawing area and sets drawing position to zero
     */
    synchronized void clearClick() {
        chnloadPos = 0;
        chndrawPos = 0;
        owner.propMin = 0;
        owner.propMax = 0;
        paintCount = 0;
        paintPanel.repaint();

    }

    /**
     * starts the save mode
     */
    synchronized void save() {
        save = true;
        startwrite();
    }

    /**
     * stops the save mode
     */
    synchronized void stopsave() {
        save = false;
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                AstericsErrorHandling.instance.reportInfo(owner, "Error closing file");
            }
        }
    }

    /**
     * loads a data from a directory
     */
    synchronized void load() {
        loadcount = 0;
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".csv", "csv");
        chooser.setFileFilter(filter);

        chooser.setCurrentDirectory(new File(owner.propFilepath));

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
            owner.savePath = chooser.getCurrentDirectory().getAbsolutePath().toString();
            owner.filepath = chooser.getSelectedFile().getAbsolutePath().toString();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(owner.filepath));
            String line;
            while ((line = br.readLine()) != null) {
                allloadValues.add(Double.valueOf(line));
            }
            br.close();
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportInfo(owner, "Error loading file");
        }

        repeat = allloadValues.size() / (w - 1);
        int round = (int) repeat;
        round++;

        // divides the data in arrays of the length of the graph

        for (int i = 0; i < round; i++) {

            if (allloadValues.size() < w) {
                loadValues = new double[allloadValues.size()];
                for (int j = 0; j < allloadValues.size(); j++) {
                    loadValues[j] = allloadValues.get(j);
                }
                roundloadValues.add(loadValues);
            } else {

                if (allloadValues.size() - i * (w - 1) < w - 1) {
                    loadValues = new double[allloadValues.size() - i * (w - 1)];
                    for (int j = 0; j < w - 1; j++) {
                        loadValues[j] = allloadValues.get(j + i * (w - 1));
                        if (j == allloadValues.size() - 1 - i * (w - 1)) {
                            break;
                        }
                    }
                } else {
                    loadValues = new double[w - 1];
                    for (int j = 0; j < w - 1; j++) {
                        loadValues[j] = allloadValues.get(j + i * (w - 1));
                    }
                }

                roundloadValues.add(loadValues);
            }
        }

        dynload();
    }

    /**
     * starts to write data in the chosen file
     */
    synchronized void startwrite() {
        if (out != null) {
            save = true;
            try {
                out.close();
            } catch (IOException e) {
                AstericsErrorHandling.instance.reportInfo(owner, "Error closing previous file");
                save = false;
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            save = true;
            File dir = new File(owner.propFilepath);
            dir.mkdir();
            out = new BufferedWriter(
                    new FileWriter(owner.propFilepath + owner.propFilename + "_" + sdf.format(cal.getTime()) + ".csv"));
        } catch (IOException e) {
            AstericsErrorHandling.instance.reportInfo(owner, "Error creating file");
            save = false;
        }
    }

    /**
     * changes a double backslash to 4 backslashes this is needed in java to
     * load a file
     */
    synchronized void checkPath() {
        if (owner.propFilepath.contains("/")) {
            return;
        }
        if (owner.propFilepath.contains("\\\\")) {
            return;
        }

        owner.propFilepath.replace("\\", "\\\\");
    }

    /**
     * loads the data of one length of the graph
     */
    synchronized void dynload() {
        owner.resume();
        if (loadcount == roundloadValues.size()) {
            owner.pause();
            owner.compare();
            return;
        }

        if (allloadValues.size() < w) {
            for (int j = 0; j < allloadValues.size(); j++) {
                updateloadChn(roundloadValues.get(loadcount)[j]);
            }
        } else {
            for (int j = 0; j < w - 1; j++) {
                if (loadcount == roundloadValues.size() - 1 && j == roundloadValues.get(loadcount).length - 1) {
                    break;
                }
                updateloadChn(roundloadValues.get(loadcount)[j]);
            }

        }
        owner.pause();
        loadcount++;
    }
}