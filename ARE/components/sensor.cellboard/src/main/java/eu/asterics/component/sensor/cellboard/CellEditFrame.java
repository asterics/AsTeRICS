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

package eu.asterics.component.sensor.cellboard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.security.acl.Owner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.osgi.framework.BundleContext;

import eu.asterics.mw.services.AREServices;


/**
 * @author Chris Veigl
 * This class generates a JFrame for setting the Cell properties
 * 
 * Date: Jan 11, 2015
 */
public class CellEditFrame extends JDialog 
{
	private static final int CELLEDIT_FRAME_WIDTH = 450;
	private static final int CELLEDIT_FRAME_HEIGHT = 300;
	JDialog thisDialog;
	
	JPanel panel=null;
	JTextField captionField=null;
	JTextField actionField=null;
	JTextField imageField=null;
	JTextField wavField=null;
	JTextField fileNameField=null;
	
	GUICell parent;
	GUI gui;
	CellBoardInstance instance;
	
		   
	public CellEditFrame (final GUICell parent)
	{
		thisDialog=this;
		this.parent = parent;
		gui = parent.owner;
		instance=gui.owner;
		
		setTitle ("Cell Editor");
		setPreferredSize(new Dimension(CELLEDIT_FRAME_WIDTH, 
				CELLEDIT_FRAME_HEIGHT));
		//setLocation(100,100);
			
		panel = new JPanel(false);
        panel.setLayout( new FlowLayout());

		JLabel label1 = new JLabel("Cell Caption");
		label1.setPreferredSize(new Dimension(200, 20));
		panel.add(label1);
		
		captionField = new JTextField();
		captionField.setPreferredSize(new Dimension(200, 20));
		panel.add(captionField);

		JLabel label2 = new JLabel("Cell Action String");
		label2.setPreferredSize(new Dimension(200, 20));
		panel.add(label2);
		
		actionField = new JTextField();
		actionField.setPreferredSize(new Dimension(200, 20));
		panel.add(actionField);

		JLabel label3 = new JLabel("Path to Cell Image");
		label3.setPreferredSize(new Dimension(200, 20));
		panel.add(label3);
		
		imageField = new JTextField();
		imageField.setPreferredSize(new Dimension(200, 20));
		panel.add(imageField);
		
		JLabel label4 = new JLabel("Path to Soundfile");
		label4.setPreferredSize(new Dimension(200, 20));
		panel.add(label4);
		
		wavField = new JTextField();
		wavField.setPreferredSize(new Dimension(200, 20));
		panel.add(wavField);

		JLabel label5 = new JLabel("Save .xml File as:");
		label4.setPreferredSize(new Dimension(200, 20));
		panel.add(label5);
		
		
		fileNameField = new JTextField();
		fileNameField.setPreferredSize(new Dimension(350, 20));
		if (instance.propKeyboardFile!="")			
			fileNameField.setText("data/cellBoardKeyboards/"+instance.propKeyboardFile);
		else fileNameField.setText("data/cellBoardKeyboards/test.xml");
		panel.add(fileNameField);
		
		 JButton savebutton = new JButton("Save");
			savebutton.setPreferredSize(new Dimension(100, 25));
	        savebutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					parent.setCellCaption(captionField.getText());
					instance.setCellCaption(parent.getIndex(), captionField.getText());
					parent.setActionText(actionField.getText());
					instance.setCellText(parent.getIndex(), actionField.getText());
					instance.setImagePath(parent.getIndex(), imageField.getText());
					instance.setWavPath(parent.getIndex(), wavField.getText());
					
					gui.update(AREServices.instance.getAvailableSpace(instance), instance.propFontSize);					
					parent.repaint();
					instance.saveXmlFile(fileNameField.getText());
					thisDialog.dispose();					
				}
			});
		    panel.add(savebutton);
	        
		 JButton cancelbutton = new JButton("Cancel");
			cancelbutton.setPreferredSize(new Dimension(100, 25));
	        cancelbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					thisDialog.dispose();					
				}
			});
	
	    panel.add(cancelbutton);
	    
	    pack();
	    setLocationRelativeTo(null);
		add(panel, BorderLayout.CENTER);
	}
	
	public void showFrame()
	{
		captionField.setText(parent.getCellCaption());
		actionField.setText(parent.getCellText());
		imageField.setText(instance.getImagePath(parent.getIndex()));
		wavField.setText(instance.getWavPath(parent.getIndex()));
		pack();
		//this.setLocation(parent.getFrame().getLocation());
        //this.setLocationRelativeTo(parent.getFrame());
	    setVisible(true);
	}


}
