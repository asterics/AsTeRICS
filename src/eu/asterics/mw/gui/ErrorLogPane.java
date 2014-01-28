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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

package eu.asterics.mw.gui;


import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.DeploymentManager;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;

public class ErrorLogPane extends JPanel  {
	protected static  JTextArea textArea;
	protected static String newline = "\n";


	public ErrorLogPane() {
		super(new BorderLayout());


		textArea = new JTextArea(10, 80);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);

		//Lay out the main panel.
		setPreferredSize(new Dimension(650, 400));
		add(scrollPane, BorderLayout.CENTER);

		textArea.setVisible(true);
		this.setVisible(false);
	}


	public static void appendLog(String error) {

			textArea.append(new Timestamp(System.currentTimeMillis())+": "+error+"\n");
			textArea.setCaretPosition(textArea.getDocument().getLength());

	}
	
}
