
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

package eu.asterics.component.sensor.cellboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * Implements the cell panel for the cell board plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Jan 17, 2012 Time: 12:31:41
 *         AM
 */

public class GUICell extends JPanel implements Accessible {
    private AccessibleContext accessibleContext = null;
    public final GUI owner;
    private GUICell guiCell;
    private CellBoardInstance instance;

    final int HOVERMODE_FRAME_ONLY = 0;
    final int HOVERMODE_FRAME_GROWING = 1;
    final int HOVERMODE_BACKGROUND_FADE = 2;

    private int hoverSleep = 30;
    private int selectFeedbackSleep = 200;
    private boolean hoverExit = false;
    private boolean hoverFinish = false;
    private boolean cellhovering = false;
    private boolean blockSendEvent = true;

    private int index = -1;
    private int row = -1;
    private int column = -1;
    private float fontSize = -1;

    BufferedImage image = null;
    BufferedImage scalledImage = null;

    int scaledImageWidth = -1;
    int scaledImageHeight = -1;

    String text = "";
    String actionText = "";
    String picturePath = "";
    String soundPath = "";
    String soundPreviewPath = "";
    String switchGrid = "";

    private final float fontSizeMax = 150;
    private final float fontIncrementStep = 0.5f;

    private final int defaultFrameWidth = 1;

    public CellEditFrame editFrame = null;

    boolean scanActive = false;

    /**
     * The class constructor.
     * 
     * @param owner
     *            the GUI instance
     * @param eventPort
     *            the event port
     * @param cellPort
     *            the cell number output port
     * @param cellTextPort
     *            the cell text output port
     */
    public GUICell(final GUI owner) {
        super();
        guiCell = this;
        this.owner = owner;
        instance = owner.owner;

        blockSendEvent = true;
        hoverFinish = false;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {

                if ((instance.propEnableEdit) && (SwingUtilities.isRightMouseButton(me))) {
                    // System.out.println("Cell "+index+ " was right-clicked
                    // !");
                    if (editFrame != null) {
                        editFrame.closeDialog();
                    }

                    editFrame = new CellEditFrame(guiCell);
                    editFrame.showFrame();
                } else {
                    if ((instance.propEnableClickSelection) || (blockSendEvent == false)) {
                        if (instance.propScanMode == owner.SCANMODE_HOVER) {
                            hoverFinish = true;
                        } else {
                            AstericsThreadPool.instance.execute(selectFeedback);
                            owner.performCellSelection(row, column);
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverSelection) {
                    hoverExit = false;
                    cellhovering = true;
                    owner.performActCellUpdate(row, column);
                    repaintNow(0);
                    AstericsThreadPool.instance.execute(hoverTimer);
                } else {
                    if (!blockSendEvent) {
                        cellhovering = true;
                        owner.performActCellUpdate(row, column);
                        repaintNow();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverExit = true;
                cellhovering = false;
                if (!blockSendEvent) {
                    repaintNow();
                }
            }
        });
    }

    public void repaintNow() {
        repaintNow(1);
    }

    public void repaintNow(final double hoverPercent) {
        Runnable performRepaint = new Runnable() {

            @Override
            public void run() {

                Color scanBorderColor;
                final int scanFrameWidth = instance.propHoverFrameThickness;
                int dRed = getColorProperty(owner.getScanColor()).getRed()
                        - getColorProperty(owner.getBackgroundColor()).getRed();
                int dGreen = getColorProperty(owner.getScanColor()).getGreen()
                        - getColorProperty(owner.getBackgroundColor()).getGreen();
                int dBlue = getColorProperty(owner.getScanColor()).getBlue()
                        - getColorProperty(owner.getBackgroundColor()).getBlue();

                if (scanActive) {
                    scanBorderColor = new Color(255 - getColorProperty(owner.getScanColor()).getRed(),
                            255 - getColorProperty(owner.getScanColor()).getGreen(),
                            255 - getColorProperty(owner.getScanColor()).getBlue());
                    setBorder(BorderFactory.createLineBorder(scanBorderColor, scanFrameWidth));
                } else {
                    if (cellhovering) {
                        double framePercent = hoverPercent;
                        if ((!hoverSelection) || (owner.getHoverIndicator() == HOVERMODE_FRAME_ONLY)) {
                            scanBorderColor = getColorProperty(owner.getScanColor());
                        } else {
                            scanBorderColor = new Color(
                                    (int) (getColorProperty(owner.getBackgroundColor()).getRed()
                                            + (double) dRed * framePercent),
                                    (int) (getColorProperty(owner.getBackgroundColor()).getGreen()
                                            + (double) dGreen * framePercent),
                                    (int) (getColorProperty(owner.getBackgroundColor()).getBlue()
                                            + (double) dBlue * framePercent));
                        }

                        if (hoverSelection) {
                            switch (owner.getHoverIndicator()) {
                            case HOVERMODE_FRAME_ONLY:
                                setBorder(BorderFactory.createLineBorder(scanBorderColor, (int) (scanFrameWidth)));
                                break;
                            case HOVERMODE_FRAME_GROWING:
                                setBorder(BorderFactory.createLineBorder(scanBorderColor,
                                        (int) (scanFrameWidth + scanFrameWidth * hoverPercent)));
                                break;
                            case HOVERMODE_BACKGROUND_FADE:
                                setBackground(scanBorderColor);
                                break;
                            }
                        } else {
                            setBorder(BorderFactory.createLineBorder(scanBorderColor, (int) (scanFrameWidth)));
                        }

                    } else {
                        setBorder(BorderFactory.createLineBorder(getColorProperty(owner.getTextColor()),
                                defaultFrameWidth));
                    }
                }

                GUICell.this.repaint();
                // GUICell.this.revalidate();
            }
        };

        if (SwingUtilities.isEventDispatchThread()) {
            performRepaint.run();
        } else {
            // try {
            SwingUtilities.invokeLater(performRepaint);
            // } catch (InvocationTargetException | InterruptedException e) {
            // }
        }
    }

    /**
     * Paints the picture
     * 
     * @param g
     *            Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scanActive) {
            setBackground(getColorProperty(owner.getScanColor()));
        } else {
            if (!cellhovering) {
                setBackground(getColorProperty(owner.getBackgroundColor()));
            }
        }

        if (image != null) {

            double pictureWidth = image.getWidth();
            double pictureHeight = image.getHeight();

            double panelWidth;
            double panelHeight;

            if (text.length() > 0) {
                panelWidth = this.getWidth();
                panelHeight = (2 * this.getHeight()) / 3;
            } else {
                panelWidth = this.getWidth();
                panelHeight = this.getHeight();
                ;
            }

            pictureWidth = image.getWidth();
            pictureHeight = image.getHeight();

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

            /*
             * if(((int)scaledWidth!=scaledImageWidth)
             * ||((int)scaledHeight!=scaledImageHeight)) {
             * scaleImage((int)scaledWidth,(int)scaledHeight); }
             */
            g.drawImage(image, (int) positionX, (int) positionY, (int) scaledWidth, (int) scaledHeight, null);
        }

        if ((text.length() > 0) && (fontSize > 0)) {
            Font font = this.getFont();
            font = font.deriveFont(fontSize);
            FontMetrics fontMetrics = this.getFontMetrics(font);
            Rectangle2D tmpFontSize = fontMetrics.getStringBounds(text, this.getGraphics());
            // System.out.println(text + " --> " + fontSize + " pt\n");
            double panelWidth = this.getWidth();
            double panelHeight = this.getHeight();

            double dbHeight = tmpFontSize.getHeight();
            double dbWidth = tmpFontSize.getWidth();

            double positionX = ((double) panelWidth - dbWidth) / 2;
            double positionY;

            if (image != null) {
                double textHeight = (double) panelHeight / 3.0;

                positionY = ((double) textHeight - dbHeight) / 2.0 + (2.0 * (double) panelHeight) / 3.0;
            } else {
                positionY = ((double) panelHeight - dbHeight) / 2.0;
            }

            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            int ascent = fm.getMaxAscent();
            ((Graphics2D)g).setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setColor(getColorProperty(owner.getTextColor()));
            g.drawString(text, (int) positionX, (int) positionY + ascent);

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

        scalledImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scalledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (scanActive) {
            g.drawImage(image, 0, 0, scaledImageWidth, scaledImageHeight, 0, 0, image.getWidth(), image.getHeight(),
                    Color.BLUE, null);
        } else {
            g.drawImage(image, 0, 0, scaledImageWidth, scaledImageHeight, 0, 0, image.getWidth(), image.getHeight(),
                    Color.GREEN, null);
        }
        g.dispose();
    }

    /**
     * Sets the picture path.
     * 
     * @param path
     *            path of the picture
     */
    void setPicturePath(String path) {
        if (index < 0) {
            return;
        }

        if (path.length() == 0) {
            if (image != null) {
                image.flush();
            }
            image = null;
            picturePath = "";
        } else {
            try {
                if (image != null) {
                    image.flush();
                }
                image = null;
                File imageFile = new File(path.trim());
                picturePath = path.trim();

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

    String getPicturePath() {
        return picturePath;
    }

    void setSoundPath(String soundPath) {
        this.soundPath = soundPath;
    }

    String getSoundPath() {
        return soundPath;
    }

    String getSwitchGrid() {
        return switchGrid;
    }

    void setSoundPreviewPath(String soundPreviewPath) {
        this.soundPreviewPath = soundPreviewPath;
    }

    void setSwitchGrid(String switchGrid) {
        this.switchGrid = switchGrid;
    }

    String getSoundPreviewPath() {
        return soundPreviewPath;
    }

    /**
     * Sets the cell index.
     * 
     * @param index
     *            the cell4 index
     */
    void setIndex(int index) {
        this.index = index;
    }

    /**
     * Returns the cell index
     * 
     * @return the cell index
     */
    int getIndex() {
        return index;
    }

    /**
     * Returns the cell ID.
     * 
     * @return cell ID
     */
    public int getCellID() {
        return index + 1;
    }

    /**
     * Sets or removes the scanning frame.
     * 
     * @param scanActive
     *            if true, the scanning frame is activated.
     */
    public void setScanActive(boolean scanActive) {
        this.scanActive = scanActive;
    }

    /**
     * Sets the cell text.
     * 
     * @param text
     *            text of the cell
     */
    public void setCellCaption(String text) {
        this.text = text;
        // Set tooltip text to provide descriptive information for Java Access
        // Bridge --> A screen reader can read the text.
        this.setToolTipText("Cell " + (column + 1) + "/" + (row + 1) + " " + text);
    }

    /**
     * Returns the cell text.
     * 
     * @return cell text
     */
    public String getCellCaption() {
        return text;
    }

    /**
     * Sets the cell action text.
     * 
     * @param text
     *            action text of the cell
     */
    public void setActionText(String text) {
        this.actionText = text;
    }

    /**
     * Returns the cell action text.
     * 
     * @return cell action text
     */
    public String getCellText() {
        return actionText;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Sets the font size.
     * 
     * @param fontSize
     *            font size
     */
    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Block the cell actions during scanning
     * 
     * @param block
     *            if true, cell action are blocked.
     */
    public void setEventBlock(boolean block) {
        blockSendEvent = block;
    }

    private int hoverTime = 1000;
    private boolean hoverSelection = false;

    /**
     * Sets hover time.
     * 
     * @param hoverTime
     *            hover time.
     */
    public void setHoverTime(int hoverTime) {
        this.hoverTime = hoverTime;
    }

    /**
     * Sets hover selection.
     * 
     * @param hoverSelection
     *            hover selection.
     */
    public void setHoverSelection(boolean hoverSelection) {
        this.hoverSelection = hoverSelection;
    }

    /**
     * Returns the maximum size of the font, which can be used to display the
     * text.
     * 
     * @return size of the font
     */
    public float getMaxFont() {
        if (text.length() < 1) {
            return -1;
        }

        float fontSize = 0;
        boolean finish = false;

        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
        // System.out.println("Panelwidth is " + panelWidth);
        // System.out.println("Panelheight is " + panelHeight);
        if (image != null) {
            // System.out.println("There is an image!");
            panelHeight = panelHeight / 3;
            // System.out.println("New PanelHeight is "+panelHeight);
        }

        do {
            fontSize = fontSize + fontIncrementStep;
            Font font = this.getFont();
            font = font.deriveFont(fontSize);
            FontMetrics fontMetrics = this.getFontMetrics(font);
            Rectangle2D tmpFontSize = fontMetrics.getStringBounds(text, this.getGraphics());

            double height = tmpFontSize.getHeight();
            double width = tmpFontSize.getWidth();

            if ((height >= panelHeight) || (width >= panelWidth)) {
                finish = true;
                fontSize = fontSize - 1;
            } else {

                if (fontSize > fontSizeMax) {
                    finish = true;
                }
            }

        } while (!finish);
        // System.out.println(text + " optimal length is " + fontSize);
        return fontSize;
    }

    /**
     * Returns the maximum size of the font, which can be used to display the
     * text.
     * 
     * @return size of the font
     */
    public float getMaxFont(int width, int height) {
        if (text.length() < 1) {
            return -1;
        }

        float fontSize = 0;
        boolean finish = false;

        double panelWidth = width;
        double panelHeight = height;
        if (image != null) {
            panelHeight = panelHeight / 3;
        }

        do {
            fontSize = fontSize + fontIncrementStep;
            Font font = this.getFont();
            font = font.deriveFont(fontSize);
            FontMetrics fontMetrics = this.getFontMetrics(font);
            Rectangle2D tmpFontSize = fontMetrics.getStringBounds(text, this.getGraphics());

            double theight = tmpFontSize.getHeight();
            double twidth = tmpFontSize.getWidth();

            if ((theight >= panelHeight) || (twidth >= panelWidth)) {
                finish = true;
                fontSize = fontSize - 1;
            } else {

                if (fontSize > fontSizeMax) {
                    finish = true;
                }
            }

        } while (!finish);
        return fontSize;
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
     * Prepares to close.
     */
    void close() {
        hoverExit = true;
        if (editFrame != null) {
            editFrame.closeDialog();
        }
    }

    /**
     * Timer user in the hover selection mode.
     */
    private final Runnable hoverTimer = new Runnable() {

        /**
         * Thread function.
         */
        @Override
        public void run() {
            boolean finish = false;
            int currentTime = 0;

            while (!finish) {
                try {
                    Thread.sleep(hoverSleep);
                } catch (InterruptedException e) {
                }

                if (hoverFinish) {
                    currentTime = hoverTime;
                } else {
                    currentTime += hoverSleep;
                }

                if (hoverExit) {
                    finish = true;
                    cellhovering = false;
                    repaintNow();
                } else {
                    if (currentTime >= hoverTime) {
                        owner.performCellSelection(row, column);
                        repaintNow();
                        try {
                            Thread.sleep(selectFeedbackSleep);
                        } catch (InterruptedException e) {
                        }
                        hoverFinish = false;
                        cellhovering = false;
                        repaintNow();
                        finish = true;
                    } else {
                        repaintNow(currentTime / (double) hoverTime);
                    }
                }
            }
        }
    };

    private final Runnable selectFeedback = new Runnable() {

        @Override
        public void run() {
            // boolean temp=scanActive;
            scanActive = true;
            GUICell.this.setBackground(getColorProperty(owner.getScanColor()));
            GUICell.this.repaint();

            try {
                Thread.sleep(selectFeedbackSleep);
            } catch (InterruptedException e) {
            }

            setScanActive(false);
            GUICell.this.setBackground(getColorProperty(owner.getBackgroundColor()));
            GUICell.this.repaint();
            GUICell.this.revalidate();

        }

    };

    /*
     * Start of support for Accessiblity API.
     */

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JPanel#getAccessibleContext()
     */
    @Override
    public AccessibleContext getAccessibleContext() {
        // We override the default accessible context
        if (accessibleContext == null) {
            // and return a custom AccessibleGUICell object.
            accessibleContext = new AccessibleGUICell();
        }
        return accessibleContext;
    }

    /**
     * The class extends AccessibleJPanel and overrides only the
     * AccessibleAction method to return our customized object.
     */
    class AccessibleGUICell extends AccessibleJPanel {

        /*
         * (non-Javadoc)
         * 
         * @see javax.accessibility.AccessibleContext#getAccessibleAction()
         */
        @Override
        public AccessibleAction getAccessibleAction() {
            return new AccessibleActionGUICell();
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JPanel.AccessibleJPanel#getAccessibleRole()
         */
        @Override
        public AccessibleRole getAccessibleRole() {
            // TODO Auto-generated method stub
            return AccessibleRole.PUSH_BUTTON;
        }

    }

    /**
     * The class provides AccessibleActions for the GUICell.
     * 
     * @author mad
     *
     */
    class AccessibleActionGUICell implements AccessibleAction {
        @Override
        public int getAccessibleActionCount() {
            // System.out.println("action count: "+1);
            // TODO Auto-generated method stub
            return 1;
        }

        @Override
        public String getAccessibleActionDescription(int i) {
            // System.out.println("accessible action descr: "+i);
            // TODO Auto-generated method stub
            if (i == 0) {
                return AccessibleAction.CLICK;
            }
            return "No Action";
        }

        @Override
        public boolean doAccessibleAction(int i) {
            // System.out.println("Do accessible action: "+i);
            if (i == 0) {
                owner.performCellSelection(row, column);
                return true;
            }
            return false;
        }
    }

}