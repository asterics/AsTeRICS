
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

package eu.asterics.component.sensor.textfieldreader;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Implements the GUI for the textfieldreader plugin, which displays one edit
 * field to take keyboard input
 * 
 * @author Chris Veigl [veigl@technikum-wien.at] Date: Mar 2, 2011 Time:
 *         19:35:00 PM
 */
public class GUI extends JFrame implements KeyListener

{
    protected JTextField textField;
    protected JTextArea textArea;
    private final static String newline = "\n";

    private final TextfieldreaderInstance owner;

    /**
     * The class constructor, creates the GUI
     */
    public GUI(final TextfieldreaderInstance owner) {
        super("Txt-Reader");
        this.owner = owner;
        // super(new GridBagLayout());

        setLayout(new BorderLayout());
        textField = new JTextField(20);
        // textField.addActionListener(this);
        textField.addKeyListener(this);

        add(textField, BorderLayout.NORTH);
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(scrollPane, BorderLayout.SOUTH);

        /*
         * 
         * //Add Components to this panel. GridBagConstraints c = new
         * GridBagConstraints(); c.gridwidth = GridBagConstraints.REMAINDER;
         * 
         * c.fill = GridBagConstraints.HORIZONTAL; add(textField, c);
         * 
         * c.fill = GridBagConstraints.BOTH; c.weightx = 1.0; c.weighty = 1.0;
         * add(scrollPane, c);
         */

        setSize(250, 150);
        setLocation(10, 400);
        toFront();
        requestFocus();
        repaint();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                textField.requestFocusInWindow();
            }
        });
    }

    /**
     * called if key typed
     */
    @Override
    public void keyTyped(KeyEvent e) { // System.out.println("Key Typed");
    }

    /**
     * called if key pressed, forwards key to processing method
     */
    @Override
    public void keyPressed(KeyEvent e) {
        // System.out.println("Key "+ e.getKeyText(e.getKeyCode()));
        // System.out.println("Code: "+ e.getKeyCode());
        owner.processKeyboardInput(e.getKeyChar(), e.getKeyCode());
        // System.out.println("Key Pressed");
    }

    /**
     * called if key released
     */
    @Override
    public void keyReleased(KeyEvent e) { // System.out.println("Key Released");
    }

    /**
     * appends recognized command to textArea
     */
    public void logCommand(String str) {
        textArea.append(str + newline);
        textField.setText(str.substring(0, str.length() - 1));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

}
