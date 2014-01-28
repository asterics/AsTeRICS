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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;

import eu.asterics.mw.are.AREProperties;


/**
 * 
 * @author weissch
 *
 */
public class AstericsDesktop extends JPanel implements ActionListener,
MouseMotionListener 
{
	private Dimension screenSize, sizeBefore;
	private boolean onFullScreen;
	private int DEFAULT_WIDTH=200;
	private int DEFAULT_HEIGHT=200;
	AstericsGUI parentFrame;
//	HashMap areOptions;
	public AstericsDesktop (AstericsGUI frame)
	{	
		super(null);

		AREProperties props = AREProperties.instance;
		
		parentFrame=frame;
		if (props.checkProperty("fullscreen", "1"))
			onFullScreen=true;
		else
			onFullScreen=false;

		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(screenSize.width, 
				screenSize.height));
		//setBorder(BorderFactory.createTitledBorder(
		//"Desktop"));
		addMouseMotionListener(this);
		//sizeBefore = new Dimension (DEFAULT_WIDTH, DEFAULT_HEIGHT);

		//this.getInputMap().put(KeyStroke.getKeyStroke("F11"), "fullscreen");
		//this.getActionMap().put("fullscreen", setFullscreen());

	}

	public Action setFullscreen() {
		Action fullScreenAction = new FullScreenAction ();
		return fullScreenAction;
	}


	public class FullScreenAction extends AbstractAction 
	{
		public FullScreenAction() 
		{
			super();
		}
		public void actionPerformed(ActionEvent e) 
		{

//			if (!onFullScreen)
//			{
//				sizeBefore = parentFrame.getSize();
//				parentFrame.dispose();
//				parentFrame.setUndecorated(true); 
//				parentFrame.pack();
//				parentFrame.setVisible(true);
//				((AstericsGUI) parentFrame).setVisible("lineStartPanel", false);
//				parentFrame.setExtendedState(Frame.MAXIMIZED_BOTH);  
//
//				onFullScreen=true;
//			}
//			else
//			{
//				parentFrame.dispose();
//				parentFrame.setUndecorated(false); 
//				parentFrame.pack();
//				parentFrame.setVisible(true);
//				if (sizeBefore!=null)
//					parentFrame.setSize(sizeBefore);
//				else
//					parentFrame.setExtendedState(Frame.NORMAL); 
//				((AstericsGUI) parentFrame).setVisible("lineStartPanel", true);
//
//				onFullScreen=false;
//			}
		}
	}

	public void addPanel (JPanel panel, int x, int y, int width, 
			int height)
	{
		panel.setBounds(x, y, width, height);
		add(panel);
		validate();
	}
	
	public void setFullScreenMode (boolean b)
	{
		this.onFullScreen=b;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

}
