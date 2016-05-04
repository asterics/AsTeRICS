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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.osgi.framework.BundleContext;


/**
 * @author Konstantinos Kakousis
 * This class generates a JFrame for setting ARE options
 * 
 * Date: Oct 10, 2011
 */
public class OptionsFrame extends JDialog 
{
	private static final int OPTIONS_PRAME_WIDTH = 650;
	private static final int OPTIONS_PRAME_HEIGHT = 550;
	static String ICON_PATH = "/images/icon.gif";
	JDialog thisDialog;
	AstericsGUI parent;
	private TabbedPane tabbedPane;
	public OptionsFrame (AstericsGUI parent, JFrame mainFrame)
	{
		super(mainFrame);
		thisDialog=this;
		this.parent = parent;
		final URL iconPath = parent.getBundleContext().getBundle().
			getResource(ICON_PATH);
		setIconImage(Toolkit.getDefaultToolkit().getImage(iconPath));
		setTitle ("Model Help and Options");
		setPreferredSize(new Dimension(OPTIONS_PRAME_WIDTH, 
				OPTIONS_PRAME_HEIGHT));
		setLocation(100,100);
		tabbedPane = new TabbedPane(parent); 
		add(tabbedPane, BorderLayout.CENTER);
		
		
		 JPanel panel = new JPanel(false);
	        JButton savebutton = new JButton("OK");
	        savebutton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					
					tabbedPane.storeProperties();
					//parent.applyChanges ();
					thisDialog.dispose();
					
				}
			});
	        panel.add(savebutton);

		add(panel, BorderLayout.SOUTH);
	}
	
	public void showFrame()
	{
		remove(tabbedPane);                    // TBD: improve this !
		tabbedPane = new TabbedPane(parent);
		add(tabbedPane, BorderLayout.CENTER);
		pack();
		//this.setLocation(parent.getFrame().getLocation());
        this.setLocationRelativeTo(parent.getFrame());
	    setVisible(true);
	}
	public void hideFrame()
	{ 
	    setVisible(false);
	}

}
