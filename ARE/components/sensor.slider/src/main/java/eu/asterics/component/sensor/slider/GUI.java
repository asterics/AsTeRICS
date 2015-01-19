
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

package eu.asterics.component.sensor.slider;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AREServices;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;



/**
 *   Implements the Graphical User Interface for the
 *   slider sensor plugin
 *  
 * @author Chris Veigl [veigl@technikum-wien.at]
 *         Date: Oct 11, 2011
 *         Time: 04:28:05 PM
 */
public class GUI extends JPanel implements ChangeListener
{
    //private MyPanel paintPanel = new MyPanel();
    
    private JPanel sliderPanel,paintPanel; 
    private JSlider slider=null;
    private JLabel sliderLabel;
    private Dimension sliderPanelSize;
    private Dimension sliderSize;

    private final int X_OFFSET=35;
    private final int Y_OFFSET=20;
    
	private GUI thisPanel;
	private final SliderInstance owner;
	private boolean changingSliderByInputValue=false;


    /**
     * The class constructor, initialises the GUI
     * @param owner    the Slider instance
     */
    public GUI(final SliderInstance owner, final Dimension space)
    {
        super();
    	this.owner=owner;

		this.setPreferredSize(new Dimension (space.width, space.height));
		design (space.width, space.height);  	
    }

    
	/**
	 * The GUI consists of one panel with the slider element.
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
    	Font actFont=new Font ("Arial", 0, owner.propFontSize);
        
		//Create Panels
		sliderPanel = new JPanel (new BorderLayout());
		sliderPanelSize = new Dimension (width, height);
		sliderSize = new Dimension (width, height);


		sliderPanel.setMaximumSize(sliderPanelSize);
		sliderPanel.setPreferredSize(sliderPanelSize);
			
		TitledBorder b = BorderFactory.createTitledBorder(owner.propCaption);
		b.setTitleFont(actFont); 
		this.setBorder (b);
		
      
		//Slider
        if (owner.propAlignment==0)
        	slider = new JSlider(JSlider.HORIZONTAL, owner.propMin, owner.propMax, 
				owner.propDefault);
        else
        	slider = new JSlider(JSlider.VERTICAL, owner.propMin, owner.propMax, 
    				owner.propDefault);

		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setMajorTickSpacing(owner.propMajorTickSpacing);
		slider.setMinorTickSpacing(owner.propMinorTickSpacing);
		slider.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		slider.setMaximumSize(sliderSize);
		slider.setPreferredSize(sliderSize);
        slider.addChangeListener(this);
        slider.setFont(actFont);
		sliderPanel.add(slider,BorderLayout.CENTER);
		AREServices.instance.adjustFonts(sliderPanel, 24, 6, 0);
		sliderPanel.setVisible(true);

        
	    this.setLayout(new BorderLayout());
        add (sliderPanel,BorderLayout.CENTER);
	    
        thisPanel=this;
 
	}
  
    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
    	
    	if (changingSliderByInputValue==false)
    	{
         // if (!source.getValueIsAdjusting()) {
            owner.opValue.sendData(
            		ConversionUtils.intToBytes(source.getValue()));   
          //}
    	}
        changingSliderByInputValue=false;
		if (owner.propStoreValue == true)
			owner.storeRuntimeValue("sliderPosition",source.getValue());
    }

    public int getSliderValue() {
    	if (slider != null)
    		return(slider.getValue());
    	else return(0);
    }

    public void valueChanged(int value) {
    	if (slider != null)
    	{
    		changingSliderByInputValue=true;
    		slider.setValue(value);
    	}
    }

}
