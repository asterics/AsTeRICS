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

package eu.asterics.mw.displayguimanagement;

public class DisplaySlider extends DisplayCanvas {

	double sliderValue = 0;
	double sliderStepSize = 10;
	
	boolean boundariesEnabled = false;
	double lowerBoundary = 0;
	double upperBoundary = 0;
	
	DisplayLabel lblValue;
	
	byte [] plusIconData = new byte [8*16];
	byte [] plusInvertedIconData = new byte [8*16];
	byte [] minusIconData = new byte [8*16];
	byte [] minusInvertedIconData = new byte [8*16];
	private IGuiChangeListener guiChangeListener;

	
	
	public DisplaySlider(int x, int y, int w, int h) {
		super(x, y, w, h);
		
		initIconData();
		
		DisplayButton btnUp   = new DisplayButton("up", w-16, 0, 16, 16);
		btnUp.canvasName = "slider_btnUp";
		btnUp.setIcon(plusIconData);
		btnUp.setIconLocation(0, 0, 16, 16);
		btnUp.enableText(false);
		btnUp.addEventListener(new IDisplayEventListener()
		{

			@Override
			public void action() 
			{
				DisplayGuiManager.debugMessage("Slider Btn Up event listener");
				double value = sliderValue + sliderStepSize;
				if (boundariesEnabled)
				{
					if (value > upperBoundary)
						value = upperBoundary;
				}
				setValue(value);
				draw();
				
				if (guiChangeListener != null)
				{
					guiChangeListener.guiChange(sliderValue);
				}
			}
		}
		);
		DisplayButton btnDown = new DisplayButton("dn", 0, 0, 16, 16);
		btnDown.canvasName = "slider_btnDown";
		btnDown.setIcon(minusIconData);
		btnDown.setIconLocation(0, 0, 16, 16);
		btnDown.enableText(false);
		btnDown.addEventListener(new IDisplayEventListener()
		{

			@Override
			public void action() 
			{
				DisplayGuiManager.debugMessage("Slider Btn down event listener");
				double value = sliderValue - sliderStepSize;
				if (boundariesEnabled)
				{
					if (value < lowerBoundary)
						value = lowerBoundary;
				}
				setValue(value);
				draw();

				if (guiChangeListener != null)
				{
					guiChangeListener.guiChange(sliderValue);
				}
			}
		}
		);
		lblValue = new DisplayLabel("" + sliderValue, 32, 0, w-64, 16);
		lblValue.canvasName = "slider_lbl";
		addChild(btnDown);
		addChild(lblValue);
		addChild(btnUp); 
	}
	
	private void initIconData() 
	{
		/*
		for (int i = 0; i < 8*16; i++)
		{
			plusIconData[i] = (byte) 0xff;
			plusInvertedIconData[i] = (byte) 0xff;
			minusIconData[i] = (byte) 0xff;
			minusInvertedIconData[i] = (byte) 0xff;
		}
		*/
		// a plus sign
		for (int y = 0; y < 16; y++)
		{
			for (int x = 0; x < 8; x++)
			{
				if (x >=3 && x < 5 && y >=2 && y < 14)
				{
					plusIconData[y*8 + x] = 0;
					plusInvertedIconData[y*8 + x] = (byte) 0xee;
				}
				else if (x >=1 && x < 7 && y >=6 && y < 10)
				{
					plusIconData[y*8 + x] = 0;
					plusInvertedIconData[y*8 + x] = (byte) 0xee;
				}
				else
				{
					plusIconData[y*8 + x] = (byte) 0xff;
					plusInvertedIconData[y*8 + x] = (byte) 0;
				}
			}
		}

		// a minus sign
		for (int y = 0; y < 16; y++)
		{
			for (int x = 0; x < 8; x++)
			{
				if (x >=1 && x < 7 && y >=6 && y < 10)
				{
					minusIconData[y*8 + x] = 0;
					minusInvertedIconData[y*8 + x] = (byte) 0xee;
				}
				else
				{
					minusIconData[y*8 + x] = (byte) 0xff;
					minusInvertedIconData[y*8 + x] = (byte) 0;
				}
			}
		}
	}
	
	void addGuiChangeListener(IGuiChangeListener listener)
	{
		this.guiChangeListener = listener;
	}

	void setValue(double value)
	{
		sliderValue = value;
		lblValue.caption = "" + sliderValue;
	}
	
	void setStepSize(double step)
	{
		this.sliderStepSize = step;
	}
	
	void setBoundaries(double lower, double upper)
	{
		this.upperBoundary = upper;
		this.lowerBoundary = lower;
		this.boundariesEnabled = true;
	}
	
	void disableBoundaries()
	{
		this.boundariesEnabled = false;
	}
}
