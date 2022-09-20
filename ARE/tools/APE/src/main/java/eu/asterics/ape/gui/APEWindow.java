package eu.asterics.ape.gui;

import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
 * This class will be the starting point for an APE gui later. Currently not
 * used. Author: martin.deinhofer@technikum-wien.at Date: Oct 30, 2015 Time:
 * 14:30:00 PM
 */

public class APEWindow {

    private JFrame frame;
    private JTextField txtMyText;

    public static void createAndShow() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    APEWindow window = new APEWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public APEWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblBaseUrl = new JLabel("Base URL");

        txtMyText = new JTextField();
        txtMyText.setText("My text und sooosososdfjlsdjfklsdjfklasdjflsdj");
        txtMyText.setColumns(100);
        GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
        groupLayout
                .setHorizontalGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING,
                                groupLayout.createSequentialGroup().addContainerGap(194, Short.MAX_VALUE)
                                        .addComponent(lblBaseUrl, GroupLayout.PREFERRED_SIZE, 76,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(6).addComponent(txtMyText, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(72)));
        groupLayout
                .setVerticalGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createSequentialGroup().addGap(121)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                .addGroup(groupLayout.createSequentialGroup().addGap(3)
                                                        .addComponent(lblBaseUrl))
                                .addComponent(txtMyText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE)).addContainerGap(121, Short.MAX_VALUE)));
        frame.getContentPane().setLayout(groupLayout);
    }
}
