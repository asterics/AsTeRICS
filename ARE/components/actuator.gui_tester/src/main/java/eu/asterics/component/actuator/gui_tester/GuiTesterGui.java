package eu.asterics.component.actuator.gui_tester;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import eu.asterics.mw.services.AREServices;

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */


/**
 * @author Konstantinos Kakousis
 * This class generates a JPanel to be displayed on the AsTeRICS Desktop.
 * It can be used as a prototype from developers interested in creating plugins
 * with gui elements. This plugin includes several Swing components as well as
 * Graphics. Everything is defined in terms of available space which is passed 
 * as a constructor argument.
 * 
 * Date: Sep 16, 2011
 */

public class GuiTesterGui extends JPanel
{


	private JPanel leftPanel, topPanel, centerPanel, bottomPanel, 
	internalCenterPanel, rightPanel, nameLabelPanel;
	private JLabel title, nameLabel, notesLabel, checkBoxesLabel, sliderLabel;
	private JTextField nameField;
	private Dimension topPanelSize, bottomPanelSize, leftPanelSize,
	rightPanelSize, centerPanelSize;
	private JSlider slider;
	private JTextArea notesTextArea;
	private JCheckBox fhtwBox, ucyBox, kiiBox;
	private JButton submitButton;

	private int MAX_FONT_SIZE=16;
	private int MIN_FONT_SIZE=8;
	private int OFFSET=30;

	private GuiTesterGui thisPanel;
	/**
	 * 
	 * @param space The available space as it can be retrieved from the ARE:
	 * <code>AREServices.instance.getAvailableSpace(IRuntimeComponentInstance)
	 * </code>
	 */
	public GuiTesterGui(final Dimension space)
	{
		super();
		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);
	}
	
	/**
	 * The GUI consists of a big Panel that contains multiple other Swing 
	 * elements displayed in a BorderLayout style.
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
		//Create Panels
		createPanels();
		//Set Panel Sizes
		setPanelSizes (width, height);

		//Set Panel elements
		setTopPanel ();
		setLeftPanel ();
		setCenterPanel ();
		setBottomPanel ();

		this.setBorder(new TitledBorder("Main Panel"));

		//Place everything to the main panel
		this.setLayout(new BorderLayout());
		this.add(topPanel, BorderLayout.PAGE_START);
		this.add(leftPanel, BorderLayout.LINE_START);
		this.add(centerPanel, BorderLayout.CENTER);
		this.add(rightPanel, BorderLayout.LINE_END);
		this.add(bottomPanel, BorderLayout.PAGE_END);

		//Adjust font according to available space
		AREServices.instance.adjustFonts (this, MAX_FONT_SIZE, MIN_FONT_SIZE,
				OFFSET);
		

		this.setVisible(true);
		thisPanel = this;
		
		//The right panel contains some java graphics.
		//It is essential to wait until the right (or East) panel is shown on 
		//the screen before we call the class that generates the graphics.
		//This is because when using graphics the screen location is very often
		//needed!
		rightPanel.addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) 
			{
				if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) !=0 
						&& rightPanel.isShowing()) 
				{
					
					JPanel newPanel = new PanelWithGraphics();
					newPanel.setMaximumSize(rightPanelSize);
					newPanel.setPreferredSize(rightPanelSize);
					rightPanel.removeAll();
					rightPanel.add(newPanel);	
					newPanel.repaint(rightPanel.getX(), 
							rightPanel.getY(), 
							rightPanelSize.width, rightPanelSize.height);
					rightPanel.repaint();
					rightPanel.revalidate();
					thisPanel.revalidate();
				}
			}
		});
	}


	/**
	 * Creates main panels
	 */
	private void createPanels() 
	{
		topPanel = new JPanel ();
		centerPanel = new JPanel ();
		leftPanel = new JPanel ();
		rightPanel = new JPanel ();
		bottomPanel = new JPanel ();

	}
	
	/**
	 * All panel sizes are defined in terms of the available space!
	 * This is essential to keep the GUI elements adjustable to changing 
	 * available space on the Desktop. It is important to define preferred sizes
	 * for JPanels since the can affect the font resizing procedure which the 
	 * ARE services provide using <code>AREServices.instance.adjustFonts</code>
	 * 
	 * @param width
	 * @param height
	 */
	private void setPanelSizes(int width, int height) 
	{
		topPanelSize = new Dimension (width, height/8);
		bottomPanelSize = new Dimension (width, height/8);
		leftPanelSize = new Dimension (width/3, 3*height/4);
		rightPanelSize = new Dimension (width/3, 3*height/4);
		centerPanelSize = new Dimension (width/3, 3*height/4);

		topPanel.setMaximumSize(topPanelSize);
		topPanel.setPreferredSize(topPanelSize);
		leftPanel.setMaximumSize(leftPanelSize);
		leftPanel.setPreferredSize(leftPanelSize);
		centerPanel.setMaximumSize(centerPanelSize);
		centerPanel.setPreferredSize(centerPanelSize);
		rightPanel.setMaximumSize(rightPanelSize);
		rightPanel.setPreferredSize(rightPanelSize);
		bottomPanel.setMaximumSize(bottomPanelSize);
		bottomPanel.setPreferredSize(bottomPanelSize);

	}
	/**
	 * Top (or North) panel contains only a title.
	 */
	private void setTopPanel() 
	{
		//Title
		title = new JLabel("The AsTeRICS GUI Tester Plugin!");
		topPanel.add(title);
		topPanel.setVisible(true);
	}
	/**
	 * Left (or West) panel contains several Swing elements in an html form 
	 * style. It is used to demonstrate how Swing components can be created in
	 * a way adjustable to changing sizes. 
	 */
	private void setLeftPanel()
	{

		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

		//Full name
		nameLabelPanel = new JPanel ();
		nameLabelPanel.setLayout(new BoxLayout(nameLabelPanel, 
				BoxLayout.Y_AXIS));
		nameLabelPanel.setPreferredSize(new Dimension (leftPanelSize.width, 
				(int) (leftPanelSize.height*0.08)) );
		nameLabel = new JLabel("Full name:");
		nameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);		
		nameLabelPanel.add(nameLabel);
		nameLabelPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);	
		
		
		nameField = new JTextField();
		nameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		//Slider
		slider = new JSlider(JSlider.HORIZONTAL, 0, leftPanelSize.width, 
				leftPanelSize.width/6);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		
		JPanel sliderLabelPanel = new JPanel ();
		sliderLabelPanel.setLayout(new BoxLayout(sliderLabelPanel, 
				BoxLayout.Y_AXIS));
		sliderLabelPanel.setPreferredSize(new Dimension (leftPanelSize.width, 
				(int) (leftPanelSize.height*0.08)) );
		sliderLabel = new JLabel ("How much do you like AsteRICS?");
		sliderLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		sliderLabelPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		sliderLabelPanel.add(sliderLabel);
		
		//Notes text area
		JPanel notesLabelPanel = new JPanel ();
		notesLabelPanel.setPreferredSize(new Dimension (leftPanelSize.width, 
				(int) (leftPanelSize.height*0.08)) );
		notesLabelPanel.setLayout(new BoxLayout(notesLabelPanel, 
				BoxLayout.Y_AXIS));
		notesLabel = new JLabel("Your notes:");
		notesLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		notesLabelPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		notesLabelPanel.add(notesLabel);
		
		notesTextArea = new JTextArea("Text");
		notesTextArea.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		//Add everything to the left panel
		leftPanel.add(nameLabelPanel);
		leftPanel.add(nameField);
		leftPanel.add(sliderLabelPanel);	
		leftPanel.add(slider);	
		leftPanel.add(notesLabelPanel);
		leftPanel.add(notesTextArea);
		leftPanel.setVisible(true);
	}
	
	/**
	 * The Center panel contains a simple Checkbox group.
	 */
	private void setCenterPanel() 
	{
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		centerPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		internalCenterPanel = new JPanel ();
		internalCenterPanel.setLayout(new BoxLayout(internalCenterPanel, 
				BoxLayout.Y_AXIS));
		internalCenterPanel.setBorder(BorderFactory.
				createEmptyBorder(0, 10, 0, 10) );


		JPanel checkBoxesLabelPanel = new JPanel();
		checkBoxesLabelPanel.setLayout(new BoxLayout(checkBoxesLabelPanel, 
				BoxLayout.Y_AXIS));
		checkBoxesLabelPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		checkBoxesLabel = new JLabel ("Participants");
		checkBoxesLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		checkBoxesLabelPanel.add(checkBoxesLabel);
		checkBoxesLabelPanel.setPreferredSize(new Dimension 
				(centerPanelSize.width, 
				(int) (centerPanelSize.height*0.08)) );
		//Create the check boxes.
		fhtwBox = new JCheckBox("FHTW");
		fhtwBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		fhtwBox.setMnemonic(KeyEvent.VK_C);
		fhtwBox.setSelected(true);

		ucyBox = new JCheckBox("UCY");
		ucyBox.setMnemonic(KeyEvent.VK_G);
		ucyBox.setSelected(false);
		ucyBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

		kiiBox = new JCheckBox("KI-I");
		kiiBox.setMnemonic(KeyEvent.VK_H);
		kiiBox.setSelected(false);
		kiiBox.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);


		internalCenterPanel.add(fhtwBox);
		internalCenterPanel.add(ucyBox);
		internalCenterPanel.add(kiiBox);

		centerPanel.add(checkBoxesLabelPanel);
		centerPanel.add(internalCenterPanel);
		centerPanel.setVisible(true);

	}

	/**
	 * The Bottom (or South) panel contains a regular java button.
	 */
	private void setBottomPanel() 
	{

		submitButton = new JButton ("Submit");
		bottomPanel.add(submitButton);	
		bottomPanel.setVisible(true);
	}

}