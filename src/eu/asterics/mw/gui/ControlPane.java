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

/*import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;*/
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/*import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;*/











import org.osgi.framework.BundleContext;

import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.AsapiSupport;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.services.AREServices;


public class ControlPane extends Pane 
{

	
	private static final int CONTROLPANEL_WIDTH = 30;
		
	private BundleContext bundleContext;
	private Pane  jplPanel, iconPanel, mainPanel;
	private Pane mainFrame;
	
	private AsapiSupport as;
	private AstericsGUI astericsGUI;
	
	private Label startLabel;
	private Label pauseLabel;
	private Image startIcon;
	private Image startIcon_ro;
	private Image pauseIcon;
	private Image pauseIcon_ro; 
	private Image stopIcon;
	private Image stopIcon_ro;
	private Label stopLabel;
	private Image deployIcon;
	private Image deployIcon_ro;
	private Label deployLabel;
	private Label optionsLabel;
	private Image optionsIcon;
	private Image optionsIcon_ro;
	private Label exitLabel;
	private Image exitIcon;
	private Image exitIcon_ro;
	private Image statusIcon;
	private Label statusLabel;
	private ErrorLogPane errorLogPane;
	private boolean statusDialogActive=false;
	//private int controlPanelOrientation= BoxLayout.Y_AXIS;



	static String PAUSE_ICON_PATH = "/images/pause.png";
	static String PAUSE_ICON_PATH_RO = "/images/pause_ro.png";
	static String START_ICON_PATH = "/images/start.png";
	static String START_ICON_PATH_RO = "/images/start_ro.png";
	static String STOP_ICON_PATH = "/images/stop.png";
	static String STOP_ICON_PATH_RO= "/images/stop_ro.png";
	static String DEPLOY_ICON_PATH = "/images/deploy.png";
	static String DEPLOY_ICON_PATH_RO= "/images/deploy_ro.png";
	static String RESTART_ICON_PATH = "/images/restart.png";
	static String RESTART_ICON_PATH_RO = "/images/restart_ro.png";
	static String OPTIONS_ICON_PATH = "/images/options.png";
	static String OPTIONS_ICON_PATH_RO = "/images/options_ro.png";
	static String EXIT_ICON_PATH = "/images/exit.png";
	static String EXIT_ICON_PATH_RO = "/images/exit_ro.png";
	
	static String ERROR_ICON_PATH = "/images/are_error.png";
	static String RUNNING_ICON_PATH = "/images/are_running.png";
	static String UNKNOWN_ICON_PATH = "/images/are_unknown.png";
	static String NEUTRAL_ICON_PATH = "/images/are_neutral.png";

	
	URL deployIconPath =null;
	URL deployIconPath_ro =null;
	URL startIconPath =null;
	URL startIconPath_ro =null;
	URL pauseIconPath =null;
	URL pauseIconPath_ro =null;
	URL stopIconPath =null;
	URL stopIconPath_ro =null;
	URL optionsIconPath =null;
	URL optionsIconPath_ro =null;
	URL exitIconPath =null;
	URL exitIconPath_ro =null;

	URL unknownIconPath = null;
	URL errorIconPath = null;
	URL runningIconPath = null; 
	URL neutralIconPath = null;

	Image deployIconImg = null;
	Image deployIconImg_ro = null;
	Image startIconImg = null;
	Image startIconImg_ro = null;
	Image pauseIconImg = null;
	Image pauseIconImg_ro = null;
	Image stopIconImg = null;
	Image stopIconImg_ro = null;
	Image optionsIconImg = null;
	Image optionsIconImg_ro = null;
	Image exitIconImg = null;
	Image exitIconImg_ro = null;

	Image unknownIconImg = null;
	Image errorIconImg = null;
	Image runningIconImg = null; 
	Image neutralIconImg = null;
	Image actStatusImg = null;
	
	
	public ControlPane(BundleContext bc, Pane mainFrame, AsapiSupport as, 
			AstericsGUI gui ) 
	{
		super(new GridPane());
		this.as = as;
		this.bundleContext = bc;
		this.mainFrame = mainFrame;
		//int axis = BoxLayout.Y_AXIS;
		this.astericsGUI = gui;

		errorLogPane = new ErrorLogPane(); 

		//JComponent controlPanel = makeControlPanel("", axis);
		Pane controlPanel = makeControlPanel("", 0);
		mainPanel = new Pane();
		//mainPanel.setLayout(new BoxLayout(mainPanel, axis));
		//mainPanel.setPreferredSize(new Dimension (CONTROLPANEL_WIDTH,astericsGUI.screenSize.height));
		mainPanel.getChildren().add(controlPanel);
		getChildren().add(mainPanel);
	}

	

	protected Pane makeControlPanel(String text, int axis) 
	{
	
		jplPanel = new Pane();
		//jplPanel.setPreferredSize(new Dimension(100,300));
		//jplPanel.setLayout(new BoxLayout(jplPanel, axis));
		//jplPanel.setBorder(new TitledBorder(text));

		this.getChildren().add(jplPanel);

		deployIconPath = bundleContext.getBundle().getResource(DEPLOY_ICON_PATH);
		deployIconPath_ro = bundleContext.getBundle().getResource(DEPLOY_ICON_PATH_RO);
		startIconPath = bundleContext.getBundle().getResource(START_ICON_PATH);
		startIconPath_ro = bundleContext.getBundle().getResource(START_ICON_PATH_RO);
		pauseIconPath = bundleContext.getBundle().getResource(PAUSE_ICON_PATH);
		pauseIconPath_ro = bundleContext.getBundle().getResource(PAUSE_ICON_PATH_RO);
		stopIconPath = bundleContext.getBundle().getResource(STOP_ICON_PATH);
		stopIconPath_ro = bundleContext.getBundle().getResource(STOP_ICON_PATH_RO);
		optionsIconPath = bundleContext.getBundle().getResource(OPTIONS_ICON_PATH);
		optionsIconPath_ro = bundleContext.getBundle().getResource(OPTIONS_ICON_PATH_RO);
		exitIconPath = bundleContext.getBundle().getResource(EXIT_ICON_PATH);
		exitIconPath_ro = bundleContext.getBundle().getResource(EXIT_ICON_PATH_RO);
		
		unknownIconPath = bundleContext.getBundle().getResource(UNKNOWN_ICON_PATH);
		errorIconPath = bundleContext.getBundle().getResource(ERROR_ICON_PATH);
		runningIconPath = bundleContext.getBundle().getResource(RUNNING_ICON_PATH); 
		neutralIconPath = bundleContext.getBundle().getResource(NEUTRAL_ICON_PATH);


		iconPanel = new Pane();
		//iconPanel.setLayout(new BoxLayout(iconPanel, axis));

		try {
			deployIcon = new Image(deployIconPath.toString());
			deployIcon_ro = new Image(deployIconPath_ro.toString());
			deployLabel = new Label(deployIcon.toString());
			deployLabel.setTooltip(new Tooltip("Choose a new Model"));
			deployIconImg = new Image(deployIconPath.toString());
			deployIconImg_ro = new Image(deployIconPath_ro.toString());
			
			startIcon = new Image(startIconPath.toString());
			startIcon_ro = new Image(startIconPath_ro.toString());
			startLabel = new Label(startIcon.toString());
			startLabel.setTooltip(new Tooltip("Start Model"));
			startIconImg = new Image(startIconPath.toString());
			startIconImg_ro = new Image(startIconPath_ro.toString());

			pauseIcon = new Image(pauseIconPath.toString());
			pauseIcon_ro = new Image(pauseIconPath_ro.toString());
			pauseLabel = new Label(pauseIcon.toString());
			pauseLabel.setTooltip(new Tooltip("Pause Model"));
			pauseIconImg = new Image(pauseIconPath.toString());
			pauseIconImg_ro = new Image(pauseIconPath_ro.toString());
	
			stopIcon	= new Image(stopIconPath.toString());
			stopIcon_ro = new Image(stopIconPath_ro.toString());
			stopLabel = new Label(stopIcon.toString());
			stopLabel.setTooltip(new Tooltip("Stop model"));
			stopIconImg = new Image(stopIconPath.toString());
			stopIconImg_ro = new Image(stopIconPath_ro.toString());
	
			optionsIcon	= new Image(optionsIconPath.toString());
			optionsIcon_ro = new Image(optionsIconPath_ro.toString());
			optionsLabel = new Label(optionsIcon.toString());
			optionsLabel.setTooltip(new Tooltip("Display Model Help and Options"));
			optionsIconImg = new Image(optionsIconPath.toString());
			optionsIconImg_ro = new Image(optionsIconPath_ro.toString());
	
			exitIcon	= new Image(exitIconPath.toString());
			exitIcon_ro = new Image(exitIconPath_ro.toString());
			exitLabel = new Label(exitIcon.toString());
			exitLabel.setTooltip(new Tooltip("Exit ARE"));
			exitIconImg = new Image(exitIconPath.toString());
			exitIconImg_ro = new Image(exitIconPath_ro.toString());

			statusIcon = new Image(neutralIconPath.toString());
			statusLabel = new Label(statusIcon.toString());
			statusLabel.setTooltip(new Tooltip("Status / Display Error Messages"));
			runningIconImg = new Image(runningIconPath.toString());
			errorIconImg = new Image(errorIconPath.toString());
			unknownIconImg = new Image(unknownIconPath.toString()); 
			neutralIconImg = new Image(neutralIconPath.toString());
			
			actStatusImg=neutralIconImg;
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		deployLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				astericsGUI.fileChooser (as);
				try {
					as.runModel();
				} catch (AREAsapiException ex) {	}
				//mainFrame.validate();
				System.out.println ("Run/resume model OK!");
			}

		});
		
		deployLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				deployLabel.setGraphic(new ImageView(deployIcon_ro));
			}
					
		});

		deployLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				deployLabel.setGraphic(new ImageView(deployIcon));
			}
					
		});
		
		startLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				try {
					as.runModel();
				} catch (AREAsapiException ex) {

					//e.printStackTrace();
				}
				//mainFrame.validate();
				System.out.println ("Run/resume model OK!");
			}

		});
		
		startLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				startLabel.setGraphic(new ImageView(startIcon_ro));
			}
					
		});

		startLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				startLabel.setGraphic(new ImageView(startIcon));
			}
					
		});
		

		stopLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				try {
					as.stopModel();
				} catch (AREAsapiException ex) {

					//e.printStackTrace();
				}
			}

		});
		
		stopLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				stopLabel.setGraphic(new ImageView(stopIcon_ro));
			}
					
		});

		stopLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				stopLabel.setGraphic(new ImageView(stopIcon));
			}
					
		});

		pauseLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				try {
					as.pauseModel();
				} catch (AREAsapiException ex) {

					//e.printStackTrace();
				}
			}

		});
		
		pauseLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				pauseLabel.setGraphic(new ImageView(pauseIcon_ro));
			}
					
		});

		pauseLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				pauseLabel.setGraphic(new ImageView(pauseIcon));
			}
					
		});
		
		optionsLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				astericsGUI.optionsFrame.showFrame();
			}

		});
		
		optionsLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				optionsLabel.setGraphic(new ImageView(optionsIcon_ro));
			}
					
		});

		optionsLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				optionsLabel.setGraphic(new ImageView(optionsIcon));
			}
					
		});
		

		exitLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				DialogResponse n = Dialogs.showConfirmDialog(
                        null, "Are you sure to stop and close the ARE?", "",
                        "ARE Exit");

                if (n == DialogResponse.YES) {
    				//astericsGUI.closeAction(); //TODO wieder in GUI aktivieren
                } ;
			}

		});
		
		exitLabel.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				exitLabel.setGraphic(new ImageView(exitIcon_ro));
			}
					
		});

		exitLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent e){
				setCursor(Cursor.CLOSED_HAND);
				exitLabel.setGraphic(new ImageView(exitIcon));
			}
					
		});
		
		
		/*statusLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				//System.out.println ("Display Status!");
				if ( statusDialogActive==false)
				{
					final JDialog dialog = new JDialog(mainFrame, "ARE Status and Error Log");
					statusDialogActive=true;
					dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	                JButton closeButton = new JButton("Close");
	                closeButton.addActionListener(new ActionListener() {
	                    public void actionPerformed(ActionEvent e) {
	                        dialog.setVisible(false);
	                        dialog.dispose();
	                        statusDialogActive=false;
	                    }
	                });
	                JPanel closePanel = new JPanel();
	                closePanel.setLayout(new BoxLayout(closePanel,
	                                                   BoxLayout.LINE_AXIS));
	                closePanel.add(Box.createHorizontalGlue());
	                closePanel.add(closeButton);
	                closePanel.setBorder(BorderFactory.
	                    createEmptyBorder(0,0,5,5));
	
	                JPanel contentPane = new JPanel(new BorderLayout());
	
	                contentPane.add(errorLogPane, BorderLayout.CENTER);
	                errorLogPane.setVisible(true);
	                contentPane.add(closePanel, BorderLayout.PAGE_END);
	                contentPane.setOpaque(true);
	                dialog.setContentPane(contentPane);
	
	                //Show it.
	                dialog.setSize(new Dimension(550, 350));
	                dialog.setLocationRelativeTo(mainFrame);
	                dialog.setVisible(true);
				}
			}
			public void mouseEntered(MouseEvent e) {	
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			public void mouseExited(MouseEvent e) {			
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}	
		});*/
		
		iconPanel.getChildren().add(deployLabel);
		iconPanel.getChildren().add(startLabel);
		iconPanel.getChildren().add(pauseLabel);
		iconPanel.getChildren().add(stopLabel);
		iconPanel.getChildren().add(optionsLabel);
		iconPanel.getChildren().add(statusLabel);
		iconPanel.getChildren().add(exitLabel);

		jplPanel.getChildren().add(iconPanel);
	
		return jplPanel;
	}
	
	public void resizeLabels(int orientation)
	{
		/*int newSize=mainFrame.getHeight()/8;
		int maxSize=astericsGUI.screenSize.width/30;
		
		if (newSize>maxSize) newSize=maxSize;
		
		if (newSize>0)
		{
			if (newSize<30) newSize=30;
			if (newSize>64) newSize=64;
			
			deployIcon.setImage(deployIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			deployIcon_ro.setImage(deployIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			startIcon.setImage(startIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			startIcon_ro.setImage(startIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT));
			stopIcon.setImage(stopIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			stopIcon_ro.setImage(stopIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			pauseIcon.setImage(pauseIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			pauseIcon_ro.setImage(pauseIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT));
			optionsIcon.setImage(optionsIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			optionsIcon_ro.setImage(optionsIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			exitIcon.setImage(exitIconImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 
			exitIcon_ro.setImage(exitIconImg_ro.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT));
			statusIcon.setImage(actStatusImg.getScaledInstance(newSize,newSize,Image.SCALE_DEFAULT)); 

			if (orientation == BoxLayout.Y_AXIS)
				mainPanel.setPreferredSize(new Dimension(newSize, astericsGUI.screenSize.height));
			else
				mainPanel.setPreferredSize(new Dimension(astericsGUI.screenSize.height,newSize));

			iconPanel.revalidate();
		}*/
	}
	

	public void reAlign(int axis) 
	{
		
		/*mainPanel.setLayout(new BoxLayout(mainPanel,axis));
		jplPanel.setLayout(new BoxLayout(jplPanel,axis));
		// cpWrapperPanel.setLayout(new BoxLayout(cpWrapperPanel,axis));
		iconPanel.setLayout(new BoxLayout(iconPanel,axis));
		controlPanelOrientation=axis;
/*		
		if (axis==BoxLayout.Y_AXIS)
			setPreferredSize(new Dimension
					(VERTICAL_BAR_WIDTH,VERTICAL_BAR_HEIGHT));
		else
			setPreferredSize(new Dimension
					(HORIZONTAL_BAR_WIDTH,HORIZONTAL_BAR_HEIGHT));*//*
		mainPanel.revalidate();
		*/
	}
	

	public void setStatus (AREStatus s)
	{
		//System.out.println("setStatus:"+s.toString());
		switch (s){

		case RUNNING:
			actStatusImg = runningIconImg; 
			break;

		case ERROR: case FATAL_ERROR:
			actStatusImg = errorIconImg; 			
			break;
		case OK:  case DEPLOYED: 
			actStatusImg = unknownIconImg; 			
			break;
		default: case UNKNOWN:
			actStatusImg = neutralIconImg; 			
			break;
		}
		
		double bHeight=mainFrame.getHeight()/8;
		double bWidth=mainFrame.getWidth()/20;
		double max=0;
		if (bHeight>bWidth) max=bHeight; else max=bWidth;
		if (max>0)
		{
			if (max<25) max=25;
			if (max>64) max=64;
			bHeight=max;
			bWidth=max;
			ImageView actStatus = new ImageView(actStatusImg);
			actStatus.resize(bWidth, bHeight);
			statusIcon = actStatus.getImage(); 
		}
	}
}