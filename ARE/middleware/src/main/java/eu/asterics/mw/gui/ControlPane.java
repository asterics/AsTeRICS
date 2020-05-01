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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.are.AREProperties;
import static eu.asterics.mw.are.AREProperties.*;
import eu.asterics.mw.utils.OSUtils;
import org.osgi.framework.BundleContext;

import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;

public class ControlPane extends JPanel {

    private static final int CONTROLPANEL_WIDTH = 30;
    private Logger logger = AstericsErrorHandling.instance.getLogger();

    private BundleContext bundleContext;
    private JPanel jplPanel, iconPanel, mainPanel;
    private JFrame mainFrame;

    private AsapiSupport as;
    private AstericsGUI astericsGUI;

    private JLabel startLabel;
    private JLabel pauseLabel;
    private ImageIcon startIcon;
    private ImageIcon startIcon_ro;
    private ImageIcon pauseIcon;
    private ImageIcon pauseIcon_ro;
    private ImageIcon stopIcon;
    private ImageIcon stopIcon_ro;
    private JLabel stopLabel;
    private ImageIcon deployIcon;
    private ImageIcon deployIcon_ro;
    private JLabel deployLabel;
    private JLabel optionsLabel;
    private ImageIcon optionsIcon;
    private ImageIcon optionsIcon_ro;
    private JLabel exitLabel;
    private ImageIcon exitIcon;
    private ImageIcon exitIcon_ro;
    private ControlPanelLabel globeLabel;
    private ControlPanelLabel pencilLabel;
    private ImageIcon statusIcon;
    private JLabel statusLabel;
    private ErrorLogPane errorLogPane;
    private boolean statusDialogActive = false;
    static String PAUSE_ICON_PATH = "/images/pause.png";
    static String PAUSE_ICON_PATH_RO = "/images/pause_ro.png";
    static String START_ICON_PATH = "/images/start.png";
    static String START_ICON_PATH_RO = "/images/start_ro.png";
    static String STOP_ICON_PATH = "/images/stop.png";
    static String STOP_ICON_PATH_RO = "/images/stop_ro.png";
    static String DEPLOY_ICON_PATH = "/images/deploy.png";
    static String DEPLOY_ICON_PATH_RO = "/images/deploy_ro.png";
    static String RESTART_ICON_PATH = "/images/restart.png";
    static String RESTART_ICON_PATH_RO = "/images/restart_ro.png";
    static String OPTIONS_ICON_PATH = "/images/options.png";
    static String OPTIONS_ICON_PATH_RO = "/images/options_ro.png";
    static String EXIT_ICON_PATH = "/images/exit.png";
    static String EXIT_ICON_PATH_RO = "/images/exit_ro.png";
    static String GLOBE_ICON_PATH = "/images/globe.png";
    static String GLOBE_ICON_PATH_RO = "/images/globe_ro.png";
    static String PENCIL_ICON_PATH = "/images/pencil.png";
    static String PENCIL_ICON_PATH_RO = "/images/pencil_ro.png";

    static String ERROR_ICON_PATH = "/images/are_error.png";
    static String RUNNING_ICON_PATH = "/images/are_running.png";
    static String UNKNOWN_ICON_PATH = "/images/are_unknown.png";
    static String NEUTRAL_ICON_PATH = "/images/are_neutral.png";

    URL deployIconPath = null;
    URL deployIconPath_ro = null;
    URL startIconPath = null;
    URL startIconPath_ro = null;
    URL pauseIconPath = null;
    URL pauseIconPath_ro = null;
    URL stopIconPath = null;
    URL stopIconPath_ro = null;
    URL optionsIconPath = null;
    URL optionsIconPath_ro = null;
    URL exitIconPath = null;
    URL exitIconPath_ro = null;

    URL unknownIconPath = null;
    URL errorIconPath = null;
    URL runningIconPath = null;
    URL neutralIconPath = null;

    BufferedImage deployIconImg = null;
    BufferedImage deployIconImg_ro = null;
    BufferedImage startIconImg = null;
    BufferedImage startIconImg_ro = null;
    BufferedImage pauseIconImg = null;
    BufferedImage pauseIconImg_ro = null;
    BufferedImage stopIconImg = null;
    BufferedImage stopIconImg_ro = null;
    BufferedImage optionsIconImg = null;
    BufferedImage optionsIconImg_ro = null;
    BufferedImage exitIconImg = null;
    BufferedImage exitIconImg_ro = null;

    BufferedImage unknownIconImg = null;
    BufferedImage errorIconImg = null;
    BufferedImage runningIconImg = null;
    BufferedImage neutralIconImg = null;
    BufferedImage actStatusImg = null;

    public ControlPane(BundleContext bc, JFrame mainFrame, AsapiSupport as, AstericsGUI gui) {
        super(new GridLayout(1, 1));
        this.as = as;
        this.bundleContext = bc;
        this.mainFrame = mainFrame;
        int axis = BoxLayout.Y_AXIS;
        this.astericsGUI = gui;

        errorLogPane = new ErrorLogPane();

        JComponent controlPanel = makeControlPanel("", axis);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, axis));
        mainPanel.setPreferredSize(new Dimension(CONTROLPANEL_WIDTH, astericsGUI.getScreenDimension().height));
        mainPanel.add(controlPanel);
        add(mainPanel);
    }

    protected JComponent makeControlPanel(String text, int axis) {

        jplPanel = new JPanel();
        // jplPanel.setPreferredSize(new Dimension(100,300));
        jplPanel.setLayout(new BoxLayout(jplPanel, axis));
        jplPanel.setBorder(new TitledBorder(text));

        this.add(jplPanel);

        deployIconPath = getFullURL(DEPLOY_ICON_PATH);
        deployIconPath_ro = getFullURL(DEPLOY_ICON_PATH_RO);
        startIconPath = getFullURL(START_ICON_PATH);
        startIconPath_ro = getFullURL(START_ICON_PATH_RO);
        pauseIconPath = getFullURL(PAUSE_ICON_PATH);
        pauseIconPath_ro = getFullURL(PAUSE_ICON_PATH_RO);
        stopIconPath = getFullURL(STOP_ICON_PATH);
        stopIconPath_ro = getFullURL(STOP_ICON_PATH_RO);
        optionsIconPath = getFullURL(OPTIONS_ICON_PATH);
        optionsIconPath_ro = getFullURL(OPTIONS_ICON_PATH_RO);
        exitIconPath = getFullURL(EXIT_ICON_PATH);
        exitIconPath_ro = getFullURL(EXIT_ICON_PATH_RO);

        unknownIconPath = getFullURL(UNKNOWN_ICON_PATH);
        errorIconPath = getFullURL(ERROR_ICON_PATH);
        runningIconPath = getFullURL(RUNNING_ICON_PATH);
        neutralIconPath = getFullURL(NEUTRAL_ICON_PATH);

        iconPanel = new JPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, axis));

        try {
            deployIcon = new ImageIcon(deployIconPath);
            deployIcon_ro = new ImageIcon(deployIconPath_ro);
            deployLabel = new JLabel(deployIcon);
            deployLabel.setToolTipText("Choose a new Model");
            deployIconImg = ImageIO.read(deployIconPath);
            deployIconImg_ro = ImageIO.read(deployIconPath_ro);

            startIcon = new ImageIcon(startIconPath);
            startIcon_ro = new ImageIcon(startIconPath_ro);
            startLabel = new JLabel(startIcon);
            startLabel.setToolTipText("Start Model");
            startIconImg = ImageIO.read(startIconPath);
            startIconImg_ro = ImageIO.read(startIconPath_ro);

            pauseIcon = new ImageIcon(pauseIconPath);
            pauseIcon_ro = new ImageIcon(pauseIconPath_ro);
            pauseLabel = new JLabel(pauseIcon);
            pauseLabel.setToolTipText("Pause Model");
            pauseIconImg = ImageIO.read(pauseIconPath);
            pauseIconImg_ro = ImageIO.read(pauseIconPath_ro);

            stopIcon = new ImageIcon(stopIconPath);
            stopIcon_ro = new ImageIcon(stopIconPath_ro);
            stopLabel = new JLabel(stopIcon);
            stopLabel.setToolTipText("Stop model");
            stopIconImg = ImageIO.read(stopIconPath);
            stopIconImg_ro = ImageIO.read(stopIconPath_ro);

            optionsIcon = new ImageIcon(optionsIconPath);
            optionsIcon_ro = new ImageIcon(optionsIconPath_ro);
            optionsLabel = new JLabel(optionsIcon);
            optionsLabel.setToolTipText("Display Model Help and Options");
            optionsIconImg = ImageIO.read(optionsIconPath);
            optionsIconImg_ro = ImageIO.read(optionsIconPath_ro);

            exitIcon = new ImageIcon(exitIconPath);
            exitIcon_ro = new ImageIcon(exitIconPath_ro);
            exitLabel = new JLabel(exitIcon);
            exitLabel.setToolTipText("Exit ARE");
            exitIconImg = ImageIO.read(exitIconPath);
            exitIconImg_ro = ImageIO.read(exitIconPath_ro);

            statusIcon = new ImageIcon(neutralIconPath);
            statusLabel = new JLabel(statusIcon);
            statusLabel.setToolTipText("Status / Display Error Messages");
            runningIconImg = ImageIO.read(runningIconPath);
            errorIconImg = ImageIO.read(errorIconPath);
            unknownIconImg = ImageIO.read(unknownIconPath);
            neutralIconImg = ImageIO.read(neutralIconPath);

            actStatusImg = neutralIconImg;

            pencilLabel = new ControlPanelLabel(getFullURL(PENCIL_ICON_PATH), getFullURL(PENCIL_ICON_PATH_RO), "Edit current model in WebACS") {
                @Override
                public void onMouseClick() {
                    AREServices.instance.editModel();
                }
            };

            globeLabel = new ControlPanelLabel(getFullURL(GLOBE_ICON_PATH), getFullURL(GLOBE_ICON_PATH_RO), "Open ARE Webserver Startpage") {
                @Override
                public void onMouseClick() {
                    String url = MessageFormat.format("http://localhost:{0}/", String.valueOf(AREServices.instance.getRESTPort()));
                    try {
                        OSUtils.openURL(url, OSUtils.OS_NAMES.ALL);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "error opening ARE Webserver Startpage.", e);
                    }
                }
            };

        } catch (IOException e) {
            e.printStackTrace();
        }

        deployLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                final String selectedModelFile = astericsGUI.fileChooser(as);
                // mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    try {
                        // If a new model was selected, deploy it.
                        if (selectedModelFile != null) {
                            as.deployFile(selectedModelFile);
                        }
                    } catch (AREAsapiException e) {
                        // do a catch only for deployFile here because runModel
                        // automatically shows error dialog.
                        // Don't show the reason, because sometimes it's saying
                        // nothing informativ. Better to simply log stacktrace,
                        // which should alrady be done on another location.
                        // String reason=e.getMessage()!=null ?
                        // "\n"+e.getMessage() : "";
                        AstericsErrorHandling.instance.reportError(null, "Could not deploy model!");
                        // Give up, if deployment fails.
                        return;
                    }
                    try {
                        // Also, if no model was selected or the deployment
                        // failed, restart old one.
                        AstericsErrorHandling.instance.getLogger().fine("Starting runModel of deployed model");
                        as.runModel();
                    } catch (AREAsapiException e) {
                    }

                } finally {
                    // Restore mouse cursor in any case
                    // mainFrame.setCursor(Cursor.getDefaultCursor());
                    // mainFrame.validate();
                    System.out.println("Run/resume model OK!");
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {

                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                deployLabel.setIcon(deployIcon_ro);
                // iconPanel.revalidate();

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                deployLabel.setIcon(deployIcon);
                // iconPanel.revalidate();
            }
        });

        startLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                try {
                    as.runModel();
                } catch (AREAsapiException e) {
                } finally {
                    // mainFrame.setCursor(Cursor.getDefaultCursor());
                    // mainFrame.validate();
                    System.out.println("Run/resume model OK!");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                startLabel.setIcon(startIcon_ro);
                // iconPanel.revalidate();

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                startLabel.setIcon(startIcon);
                // iconPanel.revalidate();
            }
        });

        stopLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    try {
                        as.stopModel();
                    } catch (AREAsapiException e) {
                    }
                } finally {
                    // mainFrame.setCursor(Cursor.getDefaultCursor());
                    // mainFrame.validate();
                    System.out.println("Stop model OK!");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                stopLabel.setIcon(stopIcon_ro);
                // iconPanel.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                stopLabel.setIcon(stopIcon);
                // iconPanel.revalidate();
            }
        });

        pauseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                try {
                    try {
                        as.pauseModel();
                    } catch (AREAsapiException e) {
                    }
                } finally {
                    // mainFrame.setCursor(Cursor.getDefaultCursor());
                    // mainFrame.validate();
                    System.out.println("Pause model OK!");
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                pauseLabel.setIcon(pauseIcon_ro);
                // iconPanel.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                pauseLabel.setIcon(pauseIcon);
                // iconPanel.revalidate();
            }
        });

        optionsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {

                astericsGUI.optionsFrame.showFrame();

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                optionsLabel.setIcon(optionsIcon_ro);
                iconPanel.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                optionsLabel.setIcon(optionsIcon);
                iconPanel.revalidate();
            }
        });

        exitLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                int n = JOptionPane.showConfirmDialog(null, "Are you sure to stop and close the ARE?", "ARE Exit", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    astericsGUI.closeAction();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                exitLabel.setIcon(exitIcon_ro);
                iconPanel.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                exitLabel.setIcon(exitIcon);
                iconPanel.revalidate();
            }
        });

        statusLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                // System.out.println ("Display Status!");
                if (statusDialogActive == false) {
                    final JDialog dialog = new JDialog((JFrame) null, "ARE Status and Error Log");
                    statusDialogActive = true;
                    dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    JButton closeButton = new JButton("Close");
                    closeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.setVisible(false);
                            dialog.dispose();
                            statusDialogActive = false;
                        }
                    });
                    JPanel closePanel = new JPanel();
                    closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
                    closePanel.add(Box.createHorizontalGlue());
                    closePanel.add(closeButton);
                    closePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));

                    JPanel contentPane = new JPanel(new BorderLayout());

                    contentPane.add(errorLogPane, BorderLayout.CENTER);
                    errorLogPane.setVisible(true);
                    contentPane.add(closePanel, BorderLayout.PAGE_END);
                    contentPane.setOpaque(true);
                    dialog.setContentPane(contentPane);

                    // Show it.
                    dialog.setSize(new Dimension(550, 350));
                    dialog.setLocationRelativeTo(mainFrame);
                    dialog.setVisible(true);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        iconPanel.add(deployLabel);
        iconPanel.add(pencilLabel);
        iconPanel.add(startLabel);
        iconPanel.add(pauseLabel);
        iconPanel.add(stopLabel);
        iconPanel.add(optionsLabel);
        iconPanel.add(globeLabel);
        iconPanel.add(statusLabel);
        iconPanel.add(exitLabel);

        jplPanel.add(iconPanel);

        return jplPanel;
    }

    public void resizeLabels(int orientation) {
        int newSize = mainFrame.getHeight() / 8;
        int maxSize = astericsGUI.getScreenDimension().width / 30;

        if (newSize > maxSize) {
            newSize = maxSize;
        }

        if (newSize > 0) {
            if (newSize < 30) {
                newSize = 30;
            }
            if (newSize > 64) {
                newSize = 64;
            }

            deployIcon.setImage(deployIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            deployIcon_ro.setImage(deployIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            startIcon.setImage(startIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            startIcon_ro.setImage(startIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            stopIcon.setImage(stopIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            stopIcon_ro.setImage(stopIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            pauseIcon.setImage(pauseIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            pauseIcon_ro.setImage(pauseIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            optionsIcon.setImage(optionsIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            optionsIcon_ro.setImage(optionsIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            exitIcon.setImage(exitIconImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            exitIcon_ro.setImage(exitIconImg_ro.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            statusIcon.setImage(actStatusImg.getScaledInstance(newSize, newSize, Image.SCALE_DEFAULT));
            pencilLabel.resizeImage(newSize);
            globeLabel.resizeImage(newSize);

            if (orientation == BoxLayout.Y_AXIS) {
                mainPanel.setPreferredSize(new Dimension(newSize, astericsGUI.getScreenDimension().height));
            } else {
                mainPanel.setPreferredSize(new Dimension(astericsGUI.getScreenDimension().height, newSize));
            }

            iconPanel.revalidate();
        }
    }

    public void reAlign(int axis) {

        mainPanel.setLayout(new BoxLayout(mainPanel, axis));
        jplPanel.setLayout(new BoxLayout(jplPanel, axis));
        // cpWrapperPanel.setLayout(new BoxLayout(cpWrapperPanel,axis));
        iconPanel.setLayout(new BoxLayout(iconPanel, axis));
        /*
         * if (axis==BoxLayout.Y_AXIS) setPreferredSize(new Dimension (VERTICAL_BAR_WIDTH,VERTICAL_BAR_HEIGHT)); else setPreferredSize(new Dimension
         * (HORIZONTAL_BAR_WIDTH,HORIZONTAL_BAR_HEIGHT));
         */
        mainPanel.revalidate();

    }

    public void setStatus(AREStatus s) {
        // System.out.println("setStatus:"+s.toString());
        switch (s) {

        case RUNNING:
            actStatusImg = runningIconImg;
            break;

        case ERROR:
        case FATAL_ERROR:
            actStatusImg = errorIconImg;
            break;
        case OK:
        case DEPLOYED:
            actStatusImg = unknownIconImg;
            break;
        default:
        case UNKNOWN:
            actStatusImg = neutralIconImg;
            break;
        }

        int bHeight = mainFrame.getHeight() / 8;
        int bWidth = mainFrame.getWidth() / 20;
        int max = 0;
        if (bHeight > bWidth) {
            max = bHeight;
        } else {
            max = bWidth;
        }
        if (max > 0) {
            if (max < 25) {
                max = 25;
            }
            if (max > 64) {
                max = 64;
            }
            bHeight = max;
            bWidth = max;
            statusIcon.setImage(actStatusImg.getScaledInstance(bWidth, bHeight, Image.SCALE_DEFAULT));
        }
        iconPanel.revalidate();
        iconPanel.repaint();
        mainFrame.getContentPane().repaint();
    }

    public void setStartKeyName(String key) {
        String fKey = getFKeyFromNativeKeyCode(key);
        String tooltipText = MessageFormat.format("Start Model [{0}]", fKey);
        startLabel.setToolTipText(tooltipText);
    }

    public void setPauseKeyName(String key) {
        String fKey = getFKeyFromNativeKeyCode(key);
        String tooltipText = MessageFormat.format("Pause Model [{0}]", fKey);
        pauseLabel.setToolTipText(tooltipText);

    }

    public void setStopKeyName(String key) {
        String fKey = getFKeyFromNativeKeyCode(key);
        String tooltipText = MessageFormat.format("Stop Model [{0}]", fKey);
        stopLabel.setToolTipText(tooltipText);
    }

    public void setEditKeyName(String key) {
        String fKey = getFKeyFromNativeKeyCode(key);
        String tooltipText = MessageFormat.format("Edit current model in WebACS [{0}]", fKey);
        pencilLabel.setToolTipText(tooltipText);
    }

    private String getFKeyFromNativeKeyCode(String nativeKeycode) {
        if (nativeKeycode != null && !"".equals("key")) {
            String[] elems = nativeKeycode.split("_");
            if (elems.length >= 2) {
                return elems[1];
            }
        }
        return "";
    }

    private URL getFullURL(String relativePath) {
        return bundleContext.getBundle().getResource(relativePath);
    }
}