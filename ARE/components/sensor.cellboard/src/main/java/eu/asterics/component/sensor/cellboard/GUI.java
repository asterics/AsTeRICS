
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

package eu.asterics.component.sensor.cellboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Implements the Graphical User Interface for the Cell Board plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Feb 06, 2012 Time: 11:21:43
 *         AM
 */
public class GUI extends JPanel {

    private JPanel guiPanel;
    private Dimension guiPanelSize;
    private GUICell cells[][];
    private JPanel gridPanel;
    // private JLabel myLabel;
    // add more GUI elements here

    final int SCANMODE_NONE = 0;
    final int SCANMODE_ROW_COL = 1;
    final int SCANMODE_COL_ROW = 2;
    final int SCANMODE_DIRECTED = 3;
    final int SCANMODE_HOVER = 4;

    private Lock lock = new ReentrantLock();
    public CellBoardInstance owner;
    int rows;
    int columns;

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the CellBoard instance
     * @param space
     *            the size of the component
     */
    public GUI(final CellBoardInstance owner, final Dimension space) {
        super();
        this.owner = owner;
        this.setPreferredSize(new Dimension(space.width, space.height));
        design(space.width, space.height);
    }

    /**
     * set up the panel and its elements for the given size
     * 
     * @param width
     * @param height
     */
    private void design(int width, int height) {
        // Create Panels
        guiPanel = new JPanel();
        guiPanelSize = new Dimension(width, height);

        guiPanel.setMaximumSize(guiPanelSize);
        guiPanel.setPreferredSize(guiPanelSize);

        guiPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), owner.getCaption()));

        gridPanel = new JPanel();

        columns = owner.getColumnCount();
        rows = owner.getRowCount();

        cells = new GUICell[rows][columns];
        Insets panelBorderInsets = guiPanel.getBorder().getBorderInsets(guiPanel);
        int gridPanelWidth = width - panelBorderInsets.left - panelBorderInsets.right;
        int gridPanelHeight = height - panelBorderInsets.top - panelBorderInsets.bottom - 5;
        Dimension gridPanelSize = new Dimension(gridPanelWidth, gridPanelHeight);
        gridPanel.setMaximumSize(gridPanelSize);
        gridPanel.setPreferredSize(gridPanelSize);

        GridLayout gridLayout = new GridLayout(rows, columns);
        gridPanel.setLayout(gridLayout);

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j] = new GUICell(this);
                cells[i][j].setVisible(true);
                cells[i][j].setIndex(index);
                cells[i][j].setRow(i);
                cells[i][j].setColumn(j);
                cells[i][j].setCellCaption(owner.getCellCaption(index));
                cells[i][j].setActionText(owner.getCellText(index));
                cells[i][j].setPicturePath(owner.getImagePath(index));
                cells[i][j].setSoundPath(owner.getSoundPath(index));
                cells[i][j].setSoundPreviewPath(owner.getSoundPreviewPath(index));
                cells[i][j].setSwitchGrid(owner.getSwitchGrid(index));
                cells[i][j].setScanActive(false);
                cells[i][j].setHoverTime(owner.getHoverTime());
                index++;
                gridPanel.add(cells[i][j]);
            }
        }

        gridPanel.setVisible(true);
        guiPanel.add(gridPanel, BorderLayout.CENTER);

        guiPanel.setVisible(true);

        // this.setBorder(new TitledBorder(owner.propMyTitle));
        // myLabel = new JLabel (owner.propMyLabelCaption);
        // guiPanel.add(myLabel);

        // this.setLayout(new BorderLayout());
        this.setLayout(new GridLayout());
        add(guiPanel);

    }

    public void repaintCells() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        cells[i][j].repaintNow();
                    }
                }
            }
        });
    }

    public void update(final Dimension space, final float pfontSize) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GUI.this.remove(guiPanel);
                GUI.this.rows = owner.getRowCount();
                GUI.this.columns = owner.getColumnCount();
                design(space.width, space.height);
                if (pfontSize == -1) {
                    defineTextFontSize(space);
                } else {
                    defineTextFontSize(pfontSize);
                }
                clearScanState();
                setScanning();
                repaintCells();
                gridPanel.repaint();
                gridPanel.invalidate();
            }
        });
    }

    /**
     * Search for the optimal text size of cells.
     */
    public void defineTextFontSize(final float pfontSize) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                float fontSize = -1;

                if (pfontSize < 0) {
                    for (int i = 0; i < rows; i++) {
                        for (int j = 0; j < columns; j++) {
                            float size = cells[i][j].getMaxFont();
                            if (size > 0) {
                                if (fontSize < 0) {
                                    fontSize = size;
                                } else {
                                    if (size < fontSize) {
                                        fontSize = size;
                                    }

                                }
                            }
                        }
                    }
                } else {
                    fontSize = pfontSize;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        cells[i][j].setFontSize(fontSize);
                    }
                }
            }
        });
    }

    /**
     * Search for the optimal text size of cells.
     */
    private void defineTextFontSize(Dimension space) {
        float fontSize = -1;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                float size = cells[i][j].getMaxFont((int) (space.width / columns), (int) (space.height / rows));
                if (size > 0) {
                    if (fontSize < 0) {
                        fontSize = size;
                    } else {
                        if (size < fontSize) {
                            fontSize = size;
                        }

                    }
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].setFontSize(fontSize);
            }
        }

    }

    /**
     * Returns the background color.
     * 
     * @return color of the background
     */
    int getBackgroundColor() {
        return owner.getBackgroundColor();
    }

    /**
     * Returns the color of the text.
     * 
     * @return color of the text
     */
    int getTextColor() {
        return owner.getTextColor();
    }

    int getHoverIndicator() {
        return owner.getHoverIndicator();
    }

    /**
     * Returns the background color for the active cell during scanning.
     * 
     * @return color of the active cell background
     */
    int getScanColor() {
        return owner.getScanColor();
    }

    private int level = 0;
    private int scanRow = 0;
    private int scanColumn = 0;
    private int scanMode = 0;
    private int repeatCount = 0;
    // private int maxRepeatCount=3; //owner.propScanCycles;

    public enum ScanSelectionDirection {
        up, down, left, right
    }

    /**
     * Removes scanning frames from all cells.
     */
    private void clearScanState() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                cells[i][j].setScanActive(false);
            }
        }
        // normally needed repaintCells but is called only from within update
        // method.
    }

    /**
     * Initializes the scanning.
     */
    public void setScanning() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                scanMode = owner.getScanMode();

                clearScanState();

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        switch (scanMode) {
                        case SCANMODE_NONE:
                            cells[i][j].setEventBlock(false);
                            cells[i][j].setHoverSelection(false);
                            break;

                        case SCANMODE_HOVER:
                            cells[i][j].setEventBlock(true);
                            cells[i][j].setHoverSelection(true);
                            break;
                        default:
                            cells[i][j].setEventBlock(true);
                            cells[i][j].setHoverSelection(false);
                            break;
                        }
                    }
                }

                if (scanMode == SCANMODE_DIRECTED) {
                    cells[0][0].setScanActive(true);
                    scanRow = 0;
                    scanColumn = 0;
                } else {
                    if ((scanMode == SCANMODE_ROW_COL) || (scanMode == SCANMODE_COL_ROW)) {
                        if ((rows == 1) || (columns == 1)) {
                            cells[0][0].setScanActive(true);
                            scanRow = 0;
                            scanColumn = 0;
                            level = 1;
                        } else {
                            if (scanMode == SCANMODE_ROW_COL) {
                                for (int j = 0; j < columns; j++) {
                                    cells[0][j].setScanActive(true);
                                }
                            } else {
                                for (int i = 0; i < rows; i++) {
                                    cells[i][0].setScanActive(true);
                                }
                            }

                            level = 0;
                            scanRow = 0;
                            scanColumn = 0;
                            repeatCount = 0;
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the cell row in the directed mode.
     * 
     * @param cellRow
     *            the row of the cell
     */
    public void setSelectionRow(final int cellRow) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (scanMode != SCANMODE_DIRECTED) {
                    return;
                }
                int row = cellRow - 1;
                if (row < 0) {
                    row = 0;
                }
                if (row >= rows) {
                    row = rows - 1;
                }

                cells[scanRow][scanColumn].setScanActive(false);
                scanRow = row;
                cells[scanRow][scanColumn].setScanActive(true);
                // AstericsErrorHandling.instance.getLogger().fine("Setting
                // scanRow "+scanRow+", scanColumn: "+scanColumn);
                performActCellUpdate(scanRow, scanColumn);
                repaintCells();
            }
        });
    }

    /**
     * Sets the cell column in the directed mode.
     * 
     * @param cellColumn
     *            the column of the cell
     */
    public void setSelectionColumn(final int cellColumn) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (scanMode != SCANMODE_DIRECTED) {
                    return;
                }
                int column = cellColumn - 1;
                if (column < 0) {
                    column = 0;
                }
                if (column >= columns) {
                    column = columns - 1;
                }

                cells[scanRow][scanColumn].setScanActive(false);
                scanColumn = column;
                cells[scanRow][scanColumn].setScanActive(true);
                // AstericsErrorHandling.instance.getLogger().fine("Setting
                // scanRow "+scanRow+", scanColumn: "+scanColumn);
                performActCellUpdate(scanRow, scanColumn);
                repaintCells();
            }
        });
    }

    /**
     * Sets the cell column in the directed mode.
     * 
     * @param cellColumn
     *            the column of the cell
     */
    public void setSelectionNumber(final int cellIndex) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (scanMode != SCANMODE_DIRECTED) {
                    return;
                }
                boolean found = false;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (cells[i][j].getIndex() == cellIndex - 1) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanRow = i;
                            scanColumn = j;
                            cells[scanRow][scanColumn].setScanActive(true);
                            performActCellUpdate(scanRow, scanColumn);
                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }
                repaintCells();
            }
        });
    }

    /**
     * Changes the active cell in the directed mode.
     */
    public void scanSelectionMove(final ScanSelectionDirection direction) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (scanMode == SCANMODE_DIRECTED) {
                    switch (direction) {
                    case up: {
                        if (scanRow - 1 >= 0) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanRow = scanRow - 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        } else {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanRow = rows - 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        }
                        performActCellUpdate(scanRow, scanColumn);
                        break;
                    }
                    case down: {
                        if (scanRow + 1 < rows) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanRow = scanRow + 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        } else {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanRow = 0;
                            cells[scanRow][scanColumn].setScanActive(true);
                        }
                        performActCellUpdate(scanRow, scanColumn);
                        break;
                    }
                    case left: {
                        if (scanColumn - 1 >= 0) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanColumn = scanColumn - 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        } else {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanColumn = columns - 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        }
                        performActCellUpdate(scanRow, scanColumn);
                        break;
                    }
                    case right: {
                        if (scanColumn + 1 < columns) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanColumn = scanColumn + 1;
                            cells[scanRow][scanColumn].setScanActive(true);
                        } else {
                            cells[scanRow][scanColumn].setScanActive(false);
                            scanColumn = 0;
                            cells[scanRow][scanColumn].setScanActive(true);
                        }
                        performActCellUpdate(scanRow, scanColumn);
                        break;
                    }

                    }

                    GUI.this.revalidate();
                    GUI.this.repaint();

                }
                repaintCells();
            }
        });
    }

    public void unSelectAll() {
        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                if (cells[i][j].scanActive) {
                    cells[i][j].setScanActive(false);
                    cells[i][j].repaintNow();
                }
            }
        }
    }

    /**
     * Moves the scanning frame.
     */
    public void scanMove() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final int maxRepeatCount = owner.propScanCycles;

                if ((scanMode == SCANMODE_ROW_COL) || (scanMode == SCANMODE_COL_ROW)) {
                    if ((rows > 1) && (columns > 1)) {
                        if (level == 0) {
                            if (scanMode == SCANMODE_ROW_COL) {
                                for (int j = 0; j < columns; j++) {
                                    cells[scanRow][j].setScanActive(false);
                                }
                                if (scanRow + 1 < rows) {
                                    scanRow = scanRow + 1;
                                } else {
                                    scanRow = 0;
                                }

                                for (int j = 0; j < columns; j++) {
                                    cells[scanRow][j].setScanActive(true);
                                }
                                owner.getScanRowOutputPort().sendData(ConversionUtils.intToBytes(scanRow + 1));
                            } else {
                                for (int i = 0; i < rows; i++) {
                                    cells[i][scanColumn].setScanActive(false);
                                }

                                if (scanColumn + 1 < columns) {
                                    scanColumn = scanColumn + 1;
                                } else {
                                    scanColumn = 0;
                                }

                                for (int i = 0; i < rows; i++) {
                                    cells[i][scanColumn].setScanActive(true);
                                }
                                owner.getScanColumnOutputPort().sendData(ConversionUtils.intToBytes(scanColumn + 1));
                            }
                        } else if (level == 1) {
                            if (scanMode == SCANMODE_ROW_COL) {
                                cells[scanRow][scanColumn].setScanActive(false);
                                if (scanColumn + 1 < columns) {
                                    scanColumn = scanColumn + 1;
                                } else {
                                    scanColumn = 0;
                                    repeatCount = repeatCount + 1;
                                }
                                cells[scanRow][scanColumn].setScanActive(true);
                                owner.getScanColumnOutputPort().sendData(ConversionUtils.intToBytes(scanColumn + 1));

                                if (repeatCount < maxRepeatCount) {
                                    performActCellUpdate(scanRow, scanColumn);
                                }

                            } else {
                                cells[scanRow][scanColumn].setScanActive(false);
                                if (scanRow + 1 < rows) {
                                    scanRow = scanRow + 1;
                                } else {
                                    scanRow = 0;
                                    repeatCount = repeatCount + 1;
                                }
                                cells[scanRow][scanColumn].setScanActive(true);
                                owner.getScanRowOutputPort().sendData(ConversionUtils.intToBytes(scanRow + 1));

                                if (repeatCount < maxRepeatCount) {
                                    performActCellUpdate(scanRow, scanColumn);
                                }

                            }

                            if (repeatCount >= maxRepeatCount) {
                                cells[scanRow][scanColumn].setScanActive(false);
                                level = 0;
                                repeatCount = 0;
                                scanRow = 0;
                                scanColumn = 0;
                                if (scanMode == SCANMODE_ROW_COL) {
                                    for (int j = 0; j < columns; j++) {
                                        cells[scanRow][j].setScanActive(true);
                                    }
                                } else {
                                    for (int i = 0; i < rows; i++) {
                                        cells[i][scanColumn].setScanActive(true);
                                    }
                                }

                            }
                        }
                    } else {
                        cells[scanRow][scanColumn].setScanActive(false);

                        if (rows == 1) {
                            if (scanColumn + 1 < columns) {
                                scanColumn = scanColumn + 1;
                            } else {
                                scanColumn = 0;
                            }
                        } else {
                            if (scanRow + 1 < rows) {
                                scanRow = scanRow + 1;
                            } else {
                                scanRow = 0;
                            }
                        }

                        cells[scanRow][scanColumn].setScanActive(true);
                        performActCellUpdate(scanRow, scanColumn);

                    }
                }
                repaintCells();
            }
        });
    }

    /**
     * Performs the selecting scanning action.
     */
    public void scanSelect() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if ((scanMode == SCANMODE_ROW_COL) || (scanMode == SCANMODE_COL_ROW)) {
                    if ((rows > 1) && (columns > 1)) {
                        if (level == 0) {
                            level = 1;
                            if (scanMode == SCANMODE_ROW_COL) {
                                for (int j = 0; j < columns; j++) {
                                    cells[scanRow][j].setScanActive(false);
                                }

                                scanColumn = 0;

                                cells[scanRow][scanColumn].setScanActive(true);
                                performActCellUpdate(scanRow, scanColumn);
                                owner.getScanColumnOutputPort().sendData(ConversionUtils.intToBytes(1));

                            } else {
                                for (int i = 0; i < rows; i++) {
                                    cells[i][scanColumn].setScanActive(false);
                                }

                                scanRow = 0;
                                cells[scanRow][scanColumn].setScanActive(true);
                                performActCellUpdate(scanRow, scanColumn);
                                owner.getScanRowOutputPort().sendData(ConversionUtils.intToBytes(1));

                            }
                        } else if (level == 1) {
                            cells[scanRow][scanColumn].setScanActive(false);
                            performCellSelection(scanRow, scanColumn);
                            level = 0;
                            repeatCount = 0;
                            scanRow = 0;
                            scanColumn = 0;
                            if (scanMode == SCANMODE_ROW_COL) {
                                for (int j = 0; j < columns; j++) {
                                    cells[scanRow][j].setScanActive(true);
                                }
                                owner.getScanRowOutputPort().sendData(ConversionUtils.intToBytes(1));

                            } else {
                                for (int i = 0; i < rows; i++) {
                                    cells[i][scanColumn].setScanActive(true);
                                }
                                owner.getScanColumnOutputPort().sendData(ConversionUtils.intToBytes(1));

                            }
                        }
                    } else {
                        cells[scanRow][scanColumn].setScanActive(false);
                        performCellSelection(scanRow, scanColumn);
                        level = 1;
                        scanRow = 0;
                        scanColumn = 0;
                        cells[scanRow][scanColumn].setScanActive(true);
                    }
                } else {
                    if (scanMode == SCANMODE_DIRECTED) {
                        performCellSelection(scanRow, scanColumn);
                    }
                    if (scanMode == SCANMODE_HOVER) {
                        performCellSelection(actHoverRow, actHoverColumn);
                    }
                }
                repaintCells();
            }
        });
    }

    /**
     * Performs the cell action.
     */
    public void performCellSelection(int row, int column) {
        int index = cells[row][column].getIndex();

        System.out.println("Cell selected ");

        owner.getEventPort(index).raiseEvent();
        owner.getCellClickedEventPort().raiseEvent();

        owner.getSelectedCellOutputPort().sendData(ConversionUtils.intToBytes(index + 1));
        owner.getSelectedCellCaptionOutputPort()
                .sendData(ConversionUtils.stringToBytes(cells[row][column].getCellCaption()));

        if (owner.propCommandSeparator.length() == 0) {
            owner.getSelectedCellTextOutputPort()
                    .sendData(ConversionUtils.stringToBytes(cells[row][column].getCellText()));
        } else // split commands into multiple outputs !
        {
            String cmd = cells[row][column].getCellText();
            StringTokenizer st = new StringTokenizer(cmd, owner.propCommandSeparator);
            while (st.hasMoreElements()) {
                String act = (String) st.nextElement();
                System.out.println("send next cmd:" + act);
                owner.getSelectedCellTextOutputPort().sendData(ConversionUtils.stringToBytes(act));
            }
        }

        final int r = row;
        final int c = column;

        if (cells[row][column].getSoundPath().length() > 0) {
            System.out.println("Trying to play sound " + cells[row][column].getSoundPath());
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    playWavFile(cells[r][c].getSoundPath());
                }
            });
        }

        if (cells[row][column].getSwitchGrid().length() > 0) {
            if (!(cells[row][column].getSwitchGrid().equals("back"))) {
                System.out.println("Trying to switch to grid:" + cells[row][column].getSwitchGrid());
                System.out.println("Storing to stack (" + owner.currentGridLevel + "):" + owner.propKeyboardFile);
                System.out.println("Switching to:" + cells[row][column].getSwitchGrid());

                owner.backGridStack[owner.currentGridLevel] = owner.propKeyboardFile;
                owner.currentGridLevel++;
                owner.xmlFile = cells[row][column].getSwitchGrid();
            } else {
                if (owner.currentGridLevel > 0) {
                    owner.currentGridLevel--;
                    System.out.println("Going back to Grid:" + owner.backGridStack[owner.currentGridLevel]);
                    owner.xmlFile = owner.backGridStack[owner.currentGridLevel];
                }
            }

            prepareToClose();
            owner.loadXmlFile();
        }

    }

    int actHoverRow = -1;
    int actHoverColumn = -1;

    public void performActCellUpdate(int row, int column) {
        owner.getActCellOutputPort().sendData(ConversionUtils.intToBytes(cells[row][column].getCellID()));
        owner.getActCellCaptionOutputPort()
                .sendData(ConversionUtils.stringToBytes(cells[row][column].getCellCaption()));
        owner.getActCellTextOutputPort().sendData(ConversionUtils.stringToBytes(cells[row][column].getCellText()));

        final int r = row;
        final int c = column;

        actHoverRow = row;
        actHoverColumn = column;

        if (cells[row][column].getSoundPreviewPath().length() > 0) {
            System.out.println("Trying to play preview sound " + cells[row][column].getSoundPreviewPath());
            AstericsThreadPool.instance.execute(new Runnable() {

                @Override
                public void run() {
                    playWavFile(cells[r][c].getSoundPreviewPath());
                }
            });
        }
    }

    /**
     * Prepares the cells to close.
     */
    void prepareToClose() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j] != null) {
                    cells[i][j].close();
                }
            }
        }
    }

    private final int WAVE_BUFFER_SIZE = 524288; // 128Kb

    public void playWavFile(String filename) {
        File soundFile = new File(filename);
        if (!soundFile.exists()) {
            return;
        }

        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        try {
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[WAVE_BUFFER_SIZE];

        try {
            while (nBytesRead != -1) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    auline.write(abData, 0, nBytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            auline.drain();
            auline.close();
        }

    }

}