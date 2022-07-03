package eu.asterics.mw.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.model.deployment.IRuntimeModel;

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

/**
 * @author Konstantinos Kakousis This class generates a tabbed pane for the
 *         options panel.
 * 
 *         Date: Oct 10, 2011
 */
public class TabbedPane extends JPanel {
    public static final String SHOW_ERROR_DIALOGS = "showErrorDialogs";
    public static final String BACKGROUND_COLOR = "background_color";
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(-11435361);

    private AstericsGUI parent;

    JCheckBox iconifyBox;
    JCheckBox defaultInfullScreen;
    JCheckBox undecoratedBox;
    JCheckBox onTopBox;
    JCheckBox showSideBarBox;
    JCheckBox showErrorGuiBox;

    JColorChooser tcc;

    public TabbedPane(AstericsGUI parent) {
        super(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new JTabbedPane();
        this.parent = parent;

        JComponent panel1 = makeDescriptionPanel("Model Description");
        tabbedPane.addTab("Model Description and Requirements", panel1);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = makeDialogPanel("Dialog Options");
        tabbedPane.addTab("Dialog Options", panel2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = makeColorChooserPanel("Background Color");
        tabbedPane.addTab("Background Color", panel3);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_3);

        // Add the tabbed pane to this panel.
        add(tabbedPane);

        // The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        storeDefaultProperties();
    }

    protected JComponent makeDescriptionPanel(String text) {
        JPanel panel = new JPanel(false);
        panel.setBorder(BorderFactory.createTitledBorder(text));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(460, 380));

        IRuntimeModel currentRuntimeModel = DeploymentManager.instance.getCurrentRuntimeModel();

        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        textPane.setEditable(false);
        SimpleAttributeSet boldAttrib = new SimpleAttributeSet();
        StyleConstants.setBold(boldAttrib, true);
        JScrollPane scrollPane = new JScrollPane(textPane);

        if (currentRuntimeModel != null) {
            try {
                doc.insertString(doc.getLength(), "ShortDescription:\n", boldAttrib);
                if (currentRuntimeModel.getModelShortDescription() == "") {
                    doc.insertString(doc.getLength(), "<not available>", null);
                } else {
                    doc.insertString(doc.getLength(), currentRuntimeModel.getModelShortDescription(), null);
                }
                doc.insertString(doc.getLength(), "\n\nModelDescription:\n", boldAttrib);
                if (currentRuntimeModel.getModelDescription() == "") {
                    doc.insertString(doc.getLength(), "<not available>", null);
                } else {
                    doc.insertString(doc.getLength(), currentRuntimeModel.getModelDescription(), null);
                }
                if (currentRuntimeModel.getModelRequirements() != "") {
                    doc.insertString(doc.getLength(), "\n\nModelRequirements:\n", boldAttrib);
                    doc.insertString(doc.getLength(), currentRuntimeModel.getModelRequirements(), null);
                }
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Font font = textPane.getFont();
        textPane.setFont(textPane.getFont().deriveFont(Font.PLAIN, 14.0f));

        textPane.setVisible(true);
        scrollPane.setVisible(true);
        panel.add(scrollPane);

        return panel;
    }

    protected JComponent makeDialogPanel(String text) {
        JPanel panel = new JPanel(false);
        showErrorGuiBox = new JCheckBox("Show Error GUI Windows");
        showErrorGuiBox.setSelected(true);

        panel.setBorder(BorderFactory.createTitledBorder(text));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(460, 380));

        AREProperties props = AREProperties.instance;

        if (props.containsKey(SHOW_ERROR_DIALOGS)) {
            if (Integer.parseInt(props.getProperty(SHOW_ERROR_DIALOGS)) == 1) {
                showErrorGuiBox.setSelected(true);
            } else {
                showErrorGuiBox.setSelected(false);
            }
        }

        panel.add(showErrorGuiBox);

        return panel;
    }

    private JComponent makeColorChooserPanel(String text) {
        AREProperties props = AREProperties.instance;
        JPanel panel = new JPanel(false);
        panel.setBorder(BorderFactory.createTitledBorder(text));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
        if (props.containsKey(BACKGROUND_COLOR)) {
            tcc = new JColorChooser(new Color(Integer.parseInt(props.getProperty(BACKGROUND_COLOR))));
        } else {
            tcc = new JColorChooser(DEFAULT_BACKGROUND_COLOR); // default
        }
        // background
        // color
        colorPanel.add(tcc);
        panel.add(colorPanel);
        return panel;
    }

    void storeDefaultProperties() {
        AREProperties props = AREProperties.instance;
       
        props.setDefaultPropertyValue(BACKGROUND_COLOR, Integer.toString(DEFAULT_BACKGROUND_COLOR.getRGB()), "The background colour of the ARE desktop. Please change the value in the ARE GUI.");
        props.setDefaultPropertyValue(SHOW_ERROR_DIALOGS, "1", "Flag to turn off showing the error dialog. 1=show errors, 0=don't show errors");
    }

    void storeProperties() {
        AREProperties props = AREProperties.instance;

        props.setProperty(BACKGROUND_COLOR, Integer.toString(tcc.getColor().getRGB()));

        if (showErrorGuiBox.isSelected()) {
            props.setProperty(SHOW_ERROR_DIALOGS, "1");
        } else {
            props.setProperty(SHOW_ERROR_DIALOGS, "0");
        }

        props.storeProperties();

        Color nc = new Color(tcc.getColor().getRGB());
        parent.desktop.setBackground(nc);
        parent.desktop.validate();

    }
}
