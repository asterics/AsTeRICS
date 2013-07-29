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

package eu.asterics.component.sensor.buttongrid;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsThreadPool;

import java.awt.geom.Rectangle2D;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Implements the graphic interface for the GuiKeyboardInstance class.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 03, 2011
 *         Time: 9:49:00 AM
 */
public class GUI extends JPanel
{
  private final ButtonGridInstance owner;
  private JPanel panel;

  JButton buttons[];
  private int numberOfKeys=0;
  private Color colSav=null;
  private final double horizontalOffset=7;
  private final double verticalOffset=1;
  private final float fontSizeMax=150;
  private final float fontIncrementStep=0.5f;
  
  /**
   * The class constructor.
   * @param owner the GuiKeyboardInstance class instance
   * @param space plugin dimension
   */
  public GUI(final ButtonGridInstance owner,final Dimension space) 
  {
    this.owner = owner;
    	
    final JButton buttons[] = new JButton[owner.NUMBER_OF_KEYS];
    panel = new JPanel();
    
    setLayout(new BorderLayout());
    
    int labelHeight;
    
    if(owner.getCaption().length()>0)
    {
    	JLabel label = new JLabel(owner.getCaption(),0);
    	add(label, BorderLayout.NORTH);
    	labelHeight=(int)getPreferredSize().getHeight();
    }
    else
    {
    	labelHeight=0;
    }
   
 	
    for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
    {
      buttons[i]=new JButton();
      String caption = owner.getButtonCaption(i);
      buttons[i].setText(caption);
      if ("".equalsIgnoreCase(caption))
      {
        buttons[i].setEnabled(false);
        buttons[i].setVisible(false);
      }
      else
      {
    	numberOfKeys=numberOfKeys+1;
    	buttons[i].setEnabled(true);
        buttons[i].setVisible(true);
      }
        	
      final int y =i;
        	
      buttons[i].addActionListener(new ActionListener() 
      {
        public void actionPerformed(ActionEvent e) 
        {
          if (colSav==null) colSav=buttons[y].getBackground();
          buttons[y].setBackground(Color.RED);
          owner.etpKeyArray[y].raiseEvent();
          
		  AstericsThreadPool.instance.execute(new Runnable() {
			  public void run()
			  {
    				try
    				{
						   Thread.sleep(250);
					       buttons[y].setBackground(colSav);
    				}
    				catch (InterruptedException e) {}
	    		}
	    	  }
	    	  );
        } 
      });
    }
    
    if(numberOfKeys>0)
    {
    	
    	Dimension buttonDimension;
    	Dimension panelDimension;
    
    	if(owner.propHorizontalOrientation==true)
    	{
    		buttonDimension=new Dimension(space.width/numberOfKeys,((space.height-labelHeight)));
    
    		panelDimension=new Dimension(numberOfKeys*buttonDimension.width,buttonDimension.height);
    	}
    	else
    	{
    		buttonDimension=new Dimension(space.width,((space.height-labelHeight)/numberOfKeys));
        
    		panelDimension=new Dimension(space.width,numberOfKeys*buttonDimension.height);
    	}
    
    	panel.setMaximumSize(panelDimension);
    	panel.setPreferredSize(panelDimension);
    	panel.setMinimumSize(panelDimension);
    	panel.setVisible(true);
		
    	if(owner.propHorizontalOrientation==true)
    	{
    		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    	}
    	else
    	{
    		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	}
    
    
    	for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
    	{
    		buttons[i].setPreferredSize(buttonDimension);
    		buttons[i].setMinimumSize(buttonDimension);
    		buttons[i].setMaximumSize(buttonDimension);
    		//panel.add(buttons[i]);
    	}
    
   
    	float maxFontSize=fontSizeMax;
    	float maxFontSizeTable[]=new float[owner.NUMBER_OF_KEYS];
    	
    	Rectangle buttonRectangle=new Rectangle();
    	
    	for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
    	{
    		float fontSize=0;
    		boolean finish=false;
    		maxFontSizeTable[i]=0;
    		if(owner.getButtonCaption(i).length()>0)
    		{
    			do
    			{
    			
    				fontSize=fontSize+fontIncrementStep;
    				
    				buttons[i].setMargin(new Insets(2,2,2,2));
    				
    				Font font=buttons[i].getFont();
    				font=font.deriveFont(fontSize);
    				FontMetrics fontMetrics = buttons[i].getFontMetrics(font);
    				Rectangle2D tmpFontSize=fontMetrics.getStringBounds(owner.getButtonCaption(i), buttons[i].getGraphics());
    				
    				Insets insets = buttons[i].getMargin();
    				
    				double height=tmpFontSize.getHeight();
    				double width=tmpFontSize.getWidth();
    				double buttonHeightSpace=buttonDimension.getHeight()-(double)insets.bottom-(double)insets.top-verticalOffset;
    				double buttonWidthSpace=buttonDimension.getWidth()-(double)insets.left-(double)insets.right-horizontalOffset;
    				
    				
    				if((height>=buttonHeightSpace)||(width>=buttonWidthSpace))
    				{
    					finish=true;
    					maxFontSizeTable[i]=fontSize-1;
    				}
    				else
    				{
    	    	
    					if(fontSize>fontSizeMax)
    					{
    						finish=true;
    						maxFontSizeTable[i]=fontSize;
    					}
    				}
    	    	
    			}while(!finish);
    			
    		}
    	
    	}
    	
    	for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
		{
    		if((maxFontSizeTable[i]>0)&&(maxFontSizeTable[i]<maxFontSize))
    		{
    			maxFontSize=maxFontSizeTable[i];
    		}
		}
    	    	
    	for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
		{
    		Font font=buttons[i].getFont();
			font=font.deriveFont(maxFontSize);
			buttons[i].setFont(font);
		}
    	
    }
	
    for(int i=0;i<owner.NUMBER_OF_KEYS;i++)
    {
    	panel.add(buttons[i]);
    }
		
    add(panel, BorderLayout.CENTER);
    setBorder(BorderFactory.createLineBorder(Color.BLACK));
  }

}