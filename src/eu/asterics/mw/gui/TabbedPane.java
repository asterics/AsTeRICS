package eu.asterics.mw.gui;

/*import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;*/

import javafx.geometry.Dimension2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */


/**
 * @author Konstantinos Kakousis
 * This class generates a tabbed pane for the options panel.
 * 
 * Date: Oct 10, 2011
 */
public class TabbedPane extends Pane 
{
	private AstericsGUI parent;
	
	CheckBox iconifyBox;
	CheckBox defaultInfullScreen;
	CheckBox undecoratedBox;
	CheckBox onTopBox;
	CheckBox showSideBarBox;

	ColorPicker tcc;
	
	public TabbedPane(AstericsGUI parent) 
	{
		super(new GridPane());

		this.parent = parent;

		HBox panel1 = makeDescriptionPanel("Model Description");
		this.getChildren().add(panel1);
		//tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
	
		HBox panel3 = makeColorChooserPanel ("Background Color");
		this.getChildren().add(panel3);
		//tabbedPane.setMnemonicAt(1,KeyEvent.VK_2);

		//Add the tabbed pane to this panel.
		//add(tabbedPane);

		//The following line enables to use scrolling tabs.
		//tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	protected HBox makeDescriptionPanel(String text) 
	{
		HBox panel = new HBox();
		//panel.setBorder(BorderFactory.createTitledBorder(text));
		//panel.setLayoutFlag(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setPrefSize(460, 380);
			
		IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		/*
        JTextArea textArea = new JTextArea(15, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
       
        if (currentRuntimeModel !=null)
        {
	        textArea.append("\nShortDescription:\n----------------------\n");
	        textArea.append(currentRuntimeModel.getModelShortDescription());
	        textArea.append("\n\nModelDescription:\n-----------------------\n");
	        textArea.append(currentRuntimeModel.getModelDescription());
	        textArea.append("\n\nModelRequirements:\n--------------------------\n");
	        textArea.append(currentRuntimeModel.getModelRequirements());
        }
        Font font = textArea.getFont();
        textArea.setFont(textArea.getFont().deriveFont(font.PLAIN,14.0f));

 		textArea.setVisible(true);
 		*/

        HBox textPane = new HBox();
        //StyledDocument doc = textPane.getStyledDocument();
        //textPane.setEditable(false);
        //SimpleAttributeSet boldAttrib = new SimpleAttributeSet(); 
        //StyleConstants.setBold(boldAttrib, true);
        //JScrollPane scrollPane = new JScrollPane(textPane);
       
        if (currentRuntimeModel !=null)
        {
	        /*try {
				doc.insertString(doc.getLength(),"ShortDescription:\n",boldAttrib);
				if (currentRuntimeModel.getModelShortDescription()=="")
			        doc.insertString(doc.getLength(), "<not available>", null);
				else doc.insertString(doc.getLength(),currentRuntimeModel.getModelShortDescription() ,null);
				doc.insertString(doc.getLength(),"\n\nModelDescription:\n", boldAttrib);
				if (currentRuntimeModel.getModelDescription()=="")
			        doc.insertString(doc.getLength(), "<not available>", null);
				else doc.insertString(doc.getLength(), currentRuntimeModel.getModelDescription(),null);
				if (currentRuntimeModel.getModelRequirements()!="")
				{
			        doc.insertString(doc.getLength(), "\n\nModelRequirements:\n",boldAttrib);
			        doc.insertString(doc.getLength(), currentRuntimeModel.getModelRequirements(),null);
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
        }
        //Font font = textPane.getFont();
       // textPane.setFont(textPane.getFont().deriveFont(font.PLAIN,14.0f));

 		//textPane.setVisible(true);
 		//scrollPane.setVisible(true);
		panel.getChildren().add(textPane);

		return panel;
	}
	
	private HBox makeColorChooserPanel(String text) 
	{
		AREProperties props = AREProperties.instance;
		HBox panel = new HBox();
		//panel.setBorder(BorderFactory.createTitledBorder(text));
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		 
		/*JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.Y_AXIS));
		if (props.containsKey("background_color"))
			tcc = new JColorChooser(new Color(Integer
					.parseInt(props.getProperty("background_color"))));
		else	
			tcc = new JColorChooser(colorPanel.getForeground());
		colorPanel.add(tcc);
		panel.add(colorPanel);*/
		return panel;
	}
	
	void storeProperties()
	{
		AREProperties props = AREProperties.instance;
		props.setProperty("background_color", 
				Double.toString(tcc.getValue().getRed() + tcc.getValue().getBlue() + tcc.getValue().getGreen()));
		props.storeProperties();

		Color nc = new Color(tcc.getValue().getRed(),tcc.getValue().getBlue(),tcc.getValue().getGreen(),0);
		//parent.desktop.setBackground(nc);
		//parent.desktop.validate();
		
	}
}
