
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

package eu.asterics.component.sensor.editbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Implements the Graphical User Interface for the Edit Box plugin
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Dec 19, 2011 Time: 12:31:41
 *         AM
 */
public class GUI extends JPanel implements FocusListener {

    private JPanel guiPanel;
    private Dimension guiPanelSize;
    private JTextField textField;
    // private JLabel myLabel;
    // add more GUI elements here

    private final EditBoxInstance owner;

    // private final double verticalOffset=1;
    private final float fontSizeMax = 150;
    private final float fontIncrementStep = 0.5f;
    private final EditBoxInstance.OutputPort opOutput;

    private final int selectText = 1;
    private final int removeText = 2;

    /**
     * The class constructor, initializes the GUI
     * 
     * @param owner
     *            the Slider instance
     */
    public GUI(final EditBoxInstance owner, final Dimension space) {
        super();
        this.owner = owner;
        this.opOutput = owner.opOutput;
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

        textField = new JTextField(owner.getDefaultText());
        // textField.setMargin(new Insets(0,0,0,0));

        textField.addFocusListener(this);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opOutput.sendData(textField.getText());
                if (owner.getInsertAction() == selectText) {
                    textField.selectAll();
                } else {
                    if (owner.getInsertAction() == removeText) {
                        textField.setText("");
                    }
                }
            }
        });

        guiPanel.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), owner.getCaption()));

        Insets panelBorderInsets = guiPanel.getBorder().getBorderInsets(guiPanel);

        double labelWidth = width - panelBorderInsets.left - panelBorderInsets.right - 1;
        double labelHeight = height - panelBorderInsets.bottom - panelBorderInsets.top - 3;

        Dimension labelDimension = new Dimension((int) labelWidth, (int) labelHeight);
        textField.setPreferredSize(labelDimension);
        textField.setMinimumSize(labelDimension);
        textField.setMaximumSize(labelDimension);

        String testString = "THIS IS TEST STRING";

        float fontSize = 0;
        boolean finish = false;

        do {
            fontSize = fontSize + fontIncrementStep;

            Font font = textField.getFont();
            font = font.deriveFont(fontSize);
            FontMetrics fontMetrics = textField.getFontMetrics(font);
            Rectangle2D tmpFontSize = fontMetrics.getStringBounds(testString, textField.getGraphics());

            double fontHeight = tmpFontSize.getHeight();
            // double fontWidth=tmpFontSize.getWidth();

            if (fontHeight >= labelHeight) {
                finish = true;
                fontSize = fontSize - 1;
            } else {

                if (fontSize > fontSizeMax) {
                    finish = true;
                }
            }
        } while (!finish);

        textField.setOpaque(true);
        textField.setForeground(getColorProperty(owner.getTextColor()));
        textField.setBackground(getColorProperty(owner.getBackgroundColor()));

        Font font = textField.getFont();
        font = font.deriveFont(fontSize);
        textField.setFont(font);

        guiPanel.add(textField, BorderLayout.CENTER);

        guiPanel.setVisible(true);

        // this.setBorder(new TitledBorder(owner.propMyTitle));
        // myLabel = new JLabel (owner.propMyLabelCaption);
        // guiPanel.add(myLabel);

        this.setLayout(new BorderLayout());
        add(guiPanel, BorderLayout.PAGE_START);

    }

    @Override
    public void focusGained(FocusEvent e) {
        // System.out.println("Focus gained");
    }

    @Override
    public void focusLost(FocusEvent e) {
        {
            opOutput.sendData(textField.getText());
            if (owner.getInsertAction() == selectText) {
                textField.selectAll();
            } else {
                if (owner.getInsertAction() == removeText) {
                    textField.setText("");
                }
            }
        }
    }

    /**
     * Sets the Edit Box text
     * 
     * @param text
     *            new Edit Box text
     */
    void setText(String text) {
        if (textField != null) {
            textField.setText(text);

        }
    }

    /**
     * Sets the Edit Box text
     * 
     * @param text
     *            new Edit Box text
     */
    String getText() {
        if (textField != null) {
            return textField.getText();
        }
        return null;
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

}