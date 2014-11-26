package eu.asterics.mw.gui;

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


import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
//import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.osgi.framework.BundleContext;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.deployment.impl.ModelGUIInfo;
import eu.asterics.mw.services.AREServices;
//import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.IAREEventListener;


/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * 		   Konstantinos Kakousis [kakousis@cs.ucy.ac.cy]
 * 		   Chris Veigl [veigl@technikum-wien.at]
 *         Date: Aug 20, 2010
 *         Time: 2:14:37 PM
 */
public class AstericsGUI implements IAREEventListener
{
	public final static String ARE_VERSION="2.5";
	static int DEFAULT_SCREEN_X=0;
	static int DEFAULT_SCREEN_Y=0;
	static int DEFAULT_SCREEN_W=0;
	static int DEFAULT_SCREEN_H=0;

	static String ICON_PATH = "/images/icon.gif";
	static String TRAY_ICON_PATH = "/images/tray_icon.gif";
	static String ARE_OPTIONS = ".options";

	public AstericsDesktop desktop;

	private BundleContext bundleContext;
	private JFileChooser fc;
	private int openFrameCount=0;
	private int xOffset=20;
	private int yOffset=20;

	JButton restartButton;
	JPanel centerPanel,
	controlPanel, jplPanel, copyrightPanel, modelWrapperPanel, cpWrapperPanel;
	private Container pane;

	private final AsapiSupport as; 
	Dimension screenSize ;
	private JFrame mainFrame;
	OptionsFrame optionsFrame;
	// private AboutFrame aboutFrame;

	private Dimension size;
	private Point position, initialClick;
	private ControlPane controlPane;
	private TrayIcon trayIcon=null;
	private int controlPanelOrientation= BoxLayout.Y_AXIS;

	SystemTray tray=null;
	
	ModelGUIInfo modelGuiInfo = new ModelGUIInfo(	DEFAULT_SCREEN_X, 
													DEFAULT_SCREEN_Y,
													DEFAULT_SCREEN_W,
													DEFAULT_SCREEN_H);

	public  AstericsGUI(BundleContext bundleContext) 
	{
		super();

		// logger = AstericsErrorHandling.instance.getLogger();
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		AREServices.instance.registerAREEventListener(this);
		
		this.bundleContext = bundleContext;
		final URL iconPath = bundleContext.getBundle().getResource(ICON_PATH);
		
		as = new AsapiSupport();
		String hostname="", ip="";

		try {
			hostname = InetAddress.getLocalHost().getHostName();
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		mainFrame = new JFrame();
		mainFrame.setVisible(false); 
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconPath));
		String versionString="AsTeRICS Runtime Environment "+ARE_VERSION+"   Host: "+hostname+"  IP:"+ip;
		System.out.println(versionString);
		mainFrame.setTitle(versionString);
		mainFrame.addComponentListener(new ResizeListener());
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				if (mainFrame.isShowing())
				closeAction();
			}
			// public void windowStateChanged(java.awt.event.WindowEvent e) {
			// if (mainFrame.isShowing())
			//	setDesktopSize("both");
			//}
		});

		pane = mainFrame.getContentPane();

		//Create and set up the content pane.
		desktop = new AstericsDesktop(this);
		desktop.setOpaque(true); //content panes must be opaque
		desktop.addMouseListener(new DesktopListener());
		desktop.addMouseMotionListener(new DesktopListener());
		pane.add(desktop, BorderLayout.CENTER);

		optionsFrame = new OptionsFrame (this, mainFrame);
		
		controlPane = new ControlPane(this.bundleContext, mainFrame, as, this);
		controlPane.setStatus(AREStatus.UNKNOWN);

		controlPanel = new JPanel ();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(controlPane);
		pane.add(controlPanel, BorderLayout.LINE_END);	

		// aboutFrame = new AboutFrame (this, mainFrame);
		
	}

	private class ResizeListener extends ComponentAdapter {

		  @Override
		  public void componentResized(ComponentEvent evt) {
			  
				int bHeight=mainFrame.getHeight();
				int bWidth=mainFrame.getWidth();
				int actOrientation;
				
				if ((bHeight<250) && (bWidth>bHeight))  { 
					actOrientation=BoxLayout.X_AXIS;
					if (controlPanelOrientation!=actOrientation)
					{
						pane.remove(controlPanel);
						pane.add(controlPanel, BorderLayout.PAGE_END);	
						controlPane.reAlign(actOrientation);
					}
				}
				else { 
					actOrientation=BoxLayout.Y_AXIS; 
					if (controlPanelOrientation!=actOrientation)
					{
						pane.remove(controlPanel);
						pane.add(controlPanel, BorderLayout.LINE_END);	
						controlPane.reAlign(actOrientation);
					}
				}
				controlPanelOrientation=actOrientation;
				
				controlPane.resizeLabels(actOrientation);
		  }
	}

	private class DesktopListener implements MouseListener, MouseMotionListener {

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if (e.getButton()==MouseEvent.BUTTON3) 
			{
				//System.out.println(" MOUSE CLICKED RIGHT " + e.getClickCount() + " TIMES **");
				controlPanel.setVisible(!controlPanel.isVisible());
			}
			else if (e.getButton()==MouseEvent.BUTTON1)
			{
				//System.out.println(" MOUSE CLICKED LEFT" + e.getClickCount() + " TIMES **");
				if (e.getClickCount() ==2) 
				{
					mainFrame.setPreferredSize(mainFrame.getSize());
					
					if (mainFrame.isUndecorated() == false)
					{
						mainFrame.dispose();
						mainFrame.setUndecorated(true);
						mainFrame.pack();
						mainFrame.setVisible(true);
					}
					else
					{
						mainFrame.dispose();
						mainFrame.setUndecorated(false);
						mainFrame.pack();
						mainFrame.setVisible(true);
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {
			initialClick = e.getPoint();
            mainFrame.getComponentAt(initialClick);
		}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
		
		@Override
		public void mouseMoved(MouseEvent e) { }
		
	    @Override
	    public void mouseDragged(MouseEvent e) {
	
	        // get location of Window
	        int thisX = mainFrame.getLocationOnScreen().x;
	        int thisY = mainFrame.getLocationOnScreen().y;
	          
	        // Determine how much the mouse moved since the initial click
	        int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
	        int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);
	
	        // Move window to this position
	        int X = thisX + xMoved;
	        int Y = thisY + yMoved;
	        mainFrame.setLocation(X, Y);
	    }
	}
		
		

	private void setSystemTray() {


		if (SystemTray.isSupported()) {

			if (tray!=null && tray.getTrayIcons().length>0) 
				return;

			// get the SystemTray instance
			tray = SystemTray.getSystemTray();
			// load an image
			final URL iconPath = bundleContext.
					getBundle().getResource(TRAY_ICON_PATH);
			Image image = Toolkit.getDefaultToolkit().getImage(iconPath);

			mainFrame.setIconImage(image);

			// create a action listener to listen for default action executed on the tray icon
			ActionListener quitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					closeAction();
				}
			};

			ActionListener openListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(!mainFrame.isVisible()) {
						mainFrame.setVisible(true);

						mainFrame.setState(JFrame.NORMAL);
					}
				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();

			MenuItem openItem = new MenuItem("Show");
			openItem.addActionListener(openListener);
			popup.add(openItem);

			MenuItem defaultItem = new MenuItem("Exit");
			defaultItem.addActionListener(quitListener);
			popup.add(defaultItem);



			trayIcon = new TrayIcon(image, "AsTeRICS Runtime Envrironment", popup);
			// set the TrayIcon properties
			trayIcon.addActionListener(openListener);
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
		} 
	}

	public JPanel getDesktop ()
	{
		return this.desktop;
	}

	public void displayFrame (final JInternalFrame frame, final boolean display)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (display)
				{
					openFrameCount++;
					frame.setResizable(true);
					frame.setClosable(true);
					frame.setMaximizable(true);
					frame.setIconifiable(true);
					frame.setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
					frame.setVisible(true);
					desktop.validate();
					desktop.add(frame);

					frame.moveToFront();
					desktop.repaint();
				}
				else
				{
					desktop.remove(frame);
					frame.setVisible(false);
					frame.dispose();
					desktop.repaint();
				}
			}
		});

	}

	public void displayPanel (final JPanel panel, final int posX, final int posY, 
			final int width, final int height, 
			final boolean display)
	{
		//x, y, w and h are given in % we need to convert them to absolute
		//values based on the screen dimension
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				
				AREProperties props = AREProperties.instance;
				
				int realX=screenSize.width;
				int realY=screenSize.height;
				
				int nposX = (int) (realX*posX/10000f);
				int nposY = (int) (realY*posY/10000f);
				int nwidth = (int) (realX*width/10000f);
				int nheight = (int) (realY*height/10000f);
				if (props.checkProperty("undecorated", "0")) 
				{
					nposY -= 41;
				}
				//System.out.println("\nDisplay Panel:");
				//System.out.println("  Screensize ="+screenSize.width + ", " + screenSize.height);
				//System.out.println("  Pos ="+posX + ", " + posY + ", " + width + ", " + height);
				//System.out.println("  nPos ="+nposX + ", " + nposY + ", " + nwidth + ", " + nheight);
				//System.out.println("  set to:"+(nposX-position.x)+ ", " + (nposY-position.y));

				if (display)
				{
					desktop.validate();
					if (panel!=null) desktop.addPanel(panel, nposX - position.x , nposY - position.y, nwidth, nheight);
					desktop.repaint();
				}
				else
				{
					if (panel!=null) {
					  panel.setVisible(false);
					  desktop.remove(panel);
					}
					desktop.repaint();
					desktop.validate();

				}
			}
		});

	}

	public void closeAction()
	{	//if (mainFrame.isShowing())
		//	setDesktopSize("both");
		//all gui related operations should be executed in EventDispatchThread, but when closing an application it does not matter
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(false);
		AREServices.instance.stopModel();
		System.exit(0);
	}


	void fileChooser (AsapiSupport as)
	{
		//Should only be invoked by a gui action (mouse click) and hence no check for EventDispatchThread necessary.
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		//Add a custom file filter and disable the default
		//(Accept All) file filter.
		fc.addChoosableFileFilter(new ModelFilter());
		fc.setAcceptAllFileFilterUsed(true);
		fc.setCurrentDirectory(new java.io.File("./models"));

		//Show it.
		int returnVal = fc.showDialog(mainFrame, "Open model...");

		//Process the results.
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			String fileName = file.getName();
			int mid= fileName.lastIndexOf(".");
			String extension=fileName.substring(mid+1,fileName.length());  

			if (extension != null) 
			{
				if (extension.equals("xml") ||
						extension.equals("acs") ) 
				{
					try 
					{
						as.deployFile(file.getAbsolutePath());
						//as.runModel();
					} 
					catch (AREAsapiException e) 
					{
						//e.printStackTrace();
					}
				} 
				else 
				{
					JOptionPane.showMessageDialog(mainFrame,
							"The selected file is not a valid AsTeRICS model.",
							"Invalid file",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}
		else if (returnVal == JFileChooser.CANCEL_OPTION) {;}


		//Reset the file chooser for the next time it's shown.
		fc.setSelectedFile(null);
	}
	
	
	private class ModelFilter  extends FileFilter {

		//Accept only .xml and .acs files
		public boolean accept(File f) {

			if (f.isDirectory()) {
				return false;
			}

			String fileName = f.getName();
			int mid= fileName.lastIndexOf(".");
			String extension=fileName.substring(mid+1,fileName.length());  

			if (extension != null) {
				if (extension.equals("xml") ||
						extension.equals("acs") ) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		//The description of this filter
		public String getDescription() {
			return "AsTeRICS models";
		}
	}

	private void setVisible(String name, boolean b)
	{
		Component[] components = pane.getComponents();

		for (Component c : components)
		{
			if ( c!=null && (c instanceof JPanel) && (c.getName()!=null))
			{
				if (c.getName().equals(name))
				{
					c.setVisible(b);
					return;
				}
			}
		}
	}
	

	private void applyChanges ()
	{
		
		mainFrame.setVisible(false);
		AREProperties props = AREProperties.instance;
		
		if (props.checkProperty("undecorated", "1"))
		{
			if (mainFrame.isUndecorated() == false)
			{
				mainFrame.dispose();
				mainFrame.setUndecorated(true);
				//mainFrame.pack();
			}
		}
		else
		{
			if (mainFrame.isUndecorated() == true) 
			{
				mainFrame.dispose();
				mainFrame.setUndecorated(false);
				//mainFrame.pack();
				
			}
		}
		if (props.checkProperty("fullscreen", "1"))
		{
			mainFrame.setPreferredSize(
					new Dimension(screenSize.width+10, 
							screenSize.height+10));
			mainFrame.setLocation(new Point(-2,-2));  
			mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			size = new Dimension((int)(screenSize.width +10),(int)(screenSize.height +10)); 
			position = new Point((int)(-2),(int)(-2)); 
		}
		else
		{	
			size = new Dimension(
					(int)(screenSize.width * modelGuiInfo.getDimension().width / 10000f),
					(int)(screenSize.height * modelGuiInfo.getDimension().height / 10000f)); 

			position = new Point(
					(int)(screenSize.width * modelGuiInfo.getPosition().x / 10000f),
					(int)(screenSize.height * modelGuiInfo.getPosition().y / 10000f)); 
					
			mainFrame.setLocation(position);
			mainFrame.setPreferredSize(size);
		}
		
		if (props.checkProperty("show_side_bar", "1"))
			this.controlPanel.setVisible(true);
		else
			this.controlPanel.setVisible(false);
		
		if (props.containsKey("background_color"))
		{
			int ncint = Integer.parseInt(props.getProperty("background_color"));
			Color nc = new Color(ncint);
			desktop.setBackground(nc);
			desktop.validate();
		}
		else
		{
			Color nc = new Color(-11435361);  // default background color lightblue
			desktop.setBackground(nc);
			desktop.validate();
		}	
		
		mainFrame.setAlwaysOnTop(props.getProperty("always_on_top").equals("1"));

		if (props.checkProperty("iconify", "1"))
		{
			setSystemTray();
		}
		else 
		{
			mainFrame.setVisible(true);
			mainFrame.setState(JFrame.NORMAL);

			if (tray!=null)
				tray.remove(trayIcon);
		}
		mainFrame.pack();
		mainFrame.revalidate();		
	}
	
	
	public void setStatus (final AREStatus s)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				controlPane.setStatus (s);	
			}
		});
	}
	
	
	public JFrame getFrame() {
		return this.mainFrame;
	}
	
	public void setModelGuiInfo(ModelGUIInfo modelGuiInfo) {
		this.modelGuiInfo = modelGuiInfo;
	}

	@Override
	public void preDeployModel() {
	}

	@Override
	public void postDeployModel() 
	{
		final ModelGUIInfo info = DeploymentManager.instance
			.getCurrentRuntimeModel().getModelGuiInfo();
		
		if (info != null)
		{
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					modelGuiInfo = info;
					modelGuiInfo.updateProperties();			
					applyChanges();					
				}
			});
		}
	}

	@Override
	public void preStartModel() {
	}

	@Override
	public void postStopModel() {
		
	}

	@Override
	public void postBundlesInstalled() {
		
	}

	@Override
	public void onAreError(String msg) {
		
	}
	
	public BundleContext getBundleContext ()
	{
		return this.bundleContext;
	}


}



/*
mainFrame.addHierarchyListener(new HierarchyListener() {

	@Override
	public void hierarchyChanged(HierarchyEvent e) 
	{
	
		if ((HierarchyEvent.SHOWING_CHANGED & e.getChangeFlags()) !=0 
				&& mainFrame.isShowing()) 
		{
			setPostDisplayableOptions();
		}
	}
});
*/



/*
mainFrame.addComponentListener(new ComponentAdapter() {


	@Override
	public void componentResized(ComponentEvent e) {
		if (mainFrame.isShowing())
		{
			setDesktopSize("both");
		}
	}
	public void componentMoved(ComponentEvent e) {

		if (mainFrame.isShowing())
		{
			setDesktopSize("both");
		}
	}
});

mainFrame.addWindowStateListener(new WindowStateListener() {
	public void windowStateChanged(WindowEvent e) {
		if (e.getNewState() == JFrame.ICONIFIED) {
			if (areOptions.get("iconify")!=null)
			{
				if(areOptions.get("iconify").equals("1"))
				{
					setSystemTray();
					mainFrame.setVisible(false);
				}
			}
		}
	}
});
	*/		



/*
public void setDesktopSize(String mode) 
{
	BufferedWriter out = null;

	try 
	{
		out = new BufferedWriter(new FileWriter(WINDOW_PROPERTIES));
		if (mode.equals("both"))
		{
			out.write(mainFrame.getSize().width+","+mainFrame.getSize().height);
			out.newLine();
			int x = mainFrame.getLocationOnScreen().x;
			if (x<0) x=0;
			int y = mainFrame.getLocationOnScreen().y;
			if (y<0) y=0;
			out.write(x+","+y);
		}
		out.close();
	}
	catch (IOException ioe)
	{
		logger.warning(this.getClass().getName()+"." +
				"setDesktopSize: IO Exception while writting window properties." 
				+"Details:"+ioe.getMessage());
	}
}
*/

