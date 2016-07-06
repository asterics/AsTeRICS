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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.filechooser.FileFilter;

import org.osgi.framework.BundleContext;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.ModelGUIInfo;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
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
	private static int DEFAULT_FONT_SIZE = 18;
	private static String DEFAULT_FONT_SIZE_PROPERTY="ARE.gui.font.size";
	public final static String ARE_VERSION="2.8";
	static int DEFAULT_SCREEN_X=0;
	static int DEFAULT_SCREEN_Y=0;
	static int DEFAULT_SCREEN_W=60;
	static int DEFAULT_SCREEN_H=250;

	static String ICON_PATH = "/images/icon.gif";
	static String TRAY_ICON_PATH = "/images/tray_icon.gif";
	static String ARE_OPTIONS = ".options";

	public AstericsDesktop desktop;

	private BundleContext bundleContext;
	private JFileChooser fc;
	private int openFrameCount=0;
	private int xOffset=20;
	private int yOffset=20;

	private boolean allowModification = true;

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
	
	public  AstericsGUI(BundleContext bundleContext) 
	{
		super();

		DEFAULT_FONT_SIZE=new Integer(AREProperties.instance.getProperty(DEFAULT_FONT_SIZE_PROPERTY, String.valueOf(DEFAULT_FONT_SIZE)));
		AstericsErrorHandling.instance.getLogger().info(DEFAULT_FONT_SIZE_PROPERTY+"="+DEFAULT_FONT_SIZE);		
		AREProperties.instance.setProperty(DEFAULT_FONT_SIZE_PROPERTY, Integer.toString(DEFAULT_FONT_SIZE));

		UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));

		Font defaultFont = (Font)UIManager.get("messageFont");
		UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));

		Font defaultColor = (Font)UIManager.get("messageForeground");
		UIManager.put("OptionPane.messageForeground", Color.black);
				

		UIManager.put("Button.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ToggleButton.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("RadioButton.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("CheckBox.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ColorChooser.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Label.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("List.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("MenuBar.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("MenuItem.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("RadioButtonMenuItem.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("CheckBoxMenuItem.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Menu.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("PopupMenu.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("OptionPane.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Panel.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ProgressBar.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ScrollPane.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Viewport.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TabbedPane.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Table.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TableHeader.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TextField.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("PasswordField.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TextArea.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TextPane.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("EditorPane.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("TitledBorder.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ToolBar.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("ToolTip.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		UIManager.put("Tree.font", new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
		
		
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
		
		Dimension defaultSize=new Dimension(DEFAULT_SCREEN_W, DEFAULT_SCREEN_H);
		pane.setPreferredSize(defaultSize);
		mainFrame.setPreferredSize(defaultSize);
		mainFrame.setSize(defaultSize);
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
				if (allowModification==true)
					controlPanel.setVisible(!controlPanel.isVisible());
			}
			else if (e.getButton()==MouseEvent.BUTTON1)
			{
				//System.out.println(" MOUSE CLICKED LEFT" + e.getClickCount() + " TIMES **");
				if (allowModification==true)
				{
					if (e.getClickCount() ==2) 
					{
						mainFrame.pack();
						mainFrame.setPreferredSize(mainFrame.getSize());
						
						if (mainFrame.isUndecorated() == false)
						{
							mainFrame.dispose();
							mainFrame.setUndecorated(true);
							mainFrame.pack();
							mainFrame.revalidate();
							mainFrame.setVisible(true);
						}
						else
						{
							mainFrame.dispose();
							mainFrame.setUndecorated(false);
							mainFrame.pack();
							mainFrame.revalidate();
							mainFrame.setVisible(true);
						}
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

	public Point getScreenDimension()
	{
		Point p = new Point();
        p.x = screenSize.width;
        p.y = screenSize.height;
        return(p);
    }

	public Point getAREWindowDimension()
	{
		Point p = new Point();
        p.x = mainFrame.getWidth();
        p.y = mainFrame.getHeight();
        return(p);
    }

	public Point getAREWindowLocation()
	{
		Point p = new Point();
        p.x = mainFrame.getLocationOnScreen().x;
        p.y = mainFrame.getLocationOnScreen().y;
        return(p);
    }
    // do we need invokeLater for these ?
	public void setAREWindowLocation(int x, int y)
	{
		mainFrame.setLocation(x, y);
    }
	
	public void setAREWindowState(int state)
	{
		mainFrame.setState(state);
    }
	public void setAREWindowToFront()
	{
		mainFrame.toFront();
		mainFrame.repaint();		
    }
	public void setFocusableWindowState(boolean state){
		mainFrame.setFocusableWindowState(state);
	}
	public void allowAREWindowModification(boolean state)
	{
		allowModification=state;
    }
	
	/**
	 * Unsets the the system tray set and displays the ARE gui.
	 */
	public void unsetSystemTray() {
		if(mainFrame!=null) {
			mainFrame.setState(JFrame.NORMAL);
			mainFrame.setVisible(true);
		}
		if (tray!=null)
			tray.remove(trayIcon);		
	}
	
	/**
	 * Makes the ARE gui invisible and adds it to the system try.
	 */
	public void setSystemTray() {


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
			
			//This is a workaround, otherwise the splash screen would not disappear, when the ARE starts directly to system tray.
			setAREWindowState(JFrame.NORMAL);
			mainFrame.setVisible(true);
			setAREWindowState(JFrame.ICONIFIED);
			mainFrame.setVisible(false);
		}
	}

	public JPanel getDesktop ()
	{
		return this.desktop;
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
				
				//this is a layout hack. it would be better to automatically pad depending on decorations and showcontrolpanel
				IRuntimeModel pendingModel=DeploymentManager.instance.getDeploymentPendingRuntimeModel();
				if (pendingModel!=null && pendingModel.getModelGuiInfo().isDecoration()) 
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
					//desktop.repaint();
				}
				else
				{
					if (panel!=null) {
					  panel.setVisible(false);
					  desktop.remove(panel);
					}
					//desktop.repaint();
					//desktop.validate();

				}
				mainFrame.pack();
				mainFrame.revalidate();
				mainFrame.repaint();
			}
		});

	}

	public void closeAction()
	{	//if (mainFrame.isShowing())
		//	setDesktopSize("both");
		//all gui related operations should be executed in EventDispatchThread, but when closing an application it does not matter
		AstericsErrorHandling.instance.reportDebugInfo(null, "Exiting ARE...");
		AREServices.instance.stopModel();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(false);		
		System.exit(0);
	}


	String fileChooser (final AsapiSupport as)
	{
		String selectedModelFile=null;
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
			final File file = fc.getSelectedFile();
			String fileName = file.getName();
			int mid= fileName.lastIndexOf(".");
			String extension=fileName.substring(mid+1,fileName.length());  

			if (extension != null) 
			{
				if (extension.equals("xml") ||
						extension.equals("acs") ) 
				{
					selectedModelFile=file.getAbsolutePath();
					/*
							try 
							{
								as.deployFile(file.getAbsolutePath());
								//as.runModel();
							} 
							catch (AREAsapiException e) 
							{
								AstericsErrorHandling.instance.reportError(null, e.getMessage());
							}
							*/
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
		return selectedModelFile;
	}
	
	
	private class ModelFilter  extends FileFilter {

		//Accept only .xml and .acs files
		public boolean accept(File f) {

			if (f.isDirectory()) {
				return true;
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
	

	private void applyChanges (ModelGUIInfo modelGUIInfo)
	{

		mainFrame.setVisible(false);
		AREProperties props = AREProperties.instance;
		//mad: this is experimental, and currently not activated. we could automatically add padding, but then we would have
		//to manually change all model files.
		int decorationPadding=0;
		int controlPanelPaddingW=0;
		int controlPanelPaddingH=0;

		
		if (!modelGUIInfo.isDecoration())
		{
			//decorationPadding=0;

			if (mainFrame.isUndecorated() == false)
			{
				mainFrame.dispose();
				mainFrame.setUndecorated(true);
				//mainFrame.pack();
			}
		}
		else
		{
			//decorationPadding=50;

			if (mainFrame.isUndecorated() == true) 
			{
				mainFrame.dispose();
				mainFrame.setUndecorated(false);
				//mainFrame.pack();
				
			}
		}
		
		if (modelGUIInfo.isShopControlPanel()) {
			//controlPanelPaddingW=100;
			//controlPanelPaddingH=0;
			this.controlPanel.setVisible(true);
		}
		else {
			//controlPanelPaddingW=0;
			//controlPanelPaddingH=0;
			this.controlPanel.setVisible(false);
		}
		
		//System.out.println("controlpanelpaddingW: "+controlPanelPaddingW+", controlpanelpaddingH: "+controlPanelPaddingH+", decorationPadding: "+decorationPadding);
		if (modelGUIInfo.isFullscreen())
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
					(int)(screenSize.width * modelGUIInfo.getDimension().width / 10000f)+controlPanelPaddingW+decorationPadding,
					(int)(screenSize.height * modelGUIInfo.getDimension().height / 10000f)+controlPanelPaddingH+decorationPadding); 

			position = new Point(
					(int)(screenSize.width * modelGUIInfo.getPosition().x / 10000f),
					(int)(screenSize.height * modelGUIInfo.getPosition().y / 10000f)); 
					
			mainFrame.setLocation(position);
			mainFrame.setPreferredSize(size);
			mainFrame.setSize(size.width, size.height);
		}
		
		
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
		
		mainFrame.setAlwaysOnTop(modelGUIInfo.isValwaysOnTop());

		if (modelGUIInfo.isToSysTray())
		{
			setSystemTray();
		}
		else 
		{
			unsetSystemTray();
		}
		mainFrame.pack();
		mainFrame.revalidate();
		mainFrame.repaint();
		
		if (!modelGUIInfo.isToSysTray()) {
			setAREWindowToFront();
		}
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
					applyChanges(info);					
				}
			});
		}
	}

	@Override
	public void preStartModel() {
	}

	@Override
	public void postStartModel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preStopModel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void postStopModel() {
		
	}


	@Override
	public void prePauseModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postPauseModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preResumeModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postResumeModel() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void preBundlesInstalled() {
		// TODO Auto-generated method stub
		
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

	public void setStartKeyName(String key) {
		controlPane.setStartKeyName(key);
	}

	public void setPauseKeyName(String key) {
		controlPane.setPauseKeyName(key);
	}

	public void setStopKeyName(String key) {
		controlPane.setStopKeyName(key);
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

