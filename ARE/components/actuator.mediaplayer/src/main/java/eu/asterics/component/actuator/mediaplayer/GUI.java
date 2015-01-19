
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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.mediaplayer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.TitledBorder;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsThreadPool;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.*;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;


/**
 *   Implements the Graphical User Interface for the
 *   Mediaplayer component  
 *  
 * @author Chris Veigl [ veigl@technikum-wien.at}
 *         Date: 30.7.2013
 */
public class GUI extends JPanel 
{
    
    private JPanel guiPanel;  
    private Dimension guiPanelSize; 
	private final MediaPlayerInstance owner;
	private String actMediaFile = "";
	private boolean playerCreated=false;
	
	private boolean positionOutputRunning=false;
	

    // The size does NOT need to match the mediaPlayer size - it's the size that
    // the media will be scaled to
    // Matching the native size will be faster of course

	
    /**
     * Image to render the video frame data.
     */

    private MediaPlayerFactory mediaPlayerFactory;

    private EmbeddedMediaPlayer mediaPlayer;

    private WindowsCanvas canvas;
	
	

    /**
     * The class constructor, initialises the GUI
     * @param owner    the Slider instance
     */
    public GUI(final MediaPlayerInstance owner, final Dimension space)
    {
        super();

    	System.out.println("In Constructor");

        this.owner=owner;
    	this.setPreferredSize(new Dimension (space.width, space.height));
    	
    	if (new File(owner.propPathToVLC).exists())
    		NativeLibrary.addSearchPath("libvlc", owner.propPathToVLC);
    	else if (new File("C:\\Program Files (x86)\\VideoLAN\\VLC").exists())
    		NativeLibrary.addSearchPath("libvlc", "C:\\Program Files (x86)\\VideoLAN\\VLC");
    	else if (new File("C:\\Program Files\\VideoLAN\\VLC").exists())
    		NativeLibrary.addSearchPath("libvlc", "C:\\Program Files\\VideoLAN\\VLC");
    	else 
    	{
			 int n = JOptionPane.showConfirmDialog(
                     null, "The VLC installation could not be found in C:\\Program Files\\VideoLan or C:\\Program Files (x86)\\VideoLan .. \n please install VLC player (32 bit version).",
                     "VLC native library not found !",
                     JOptionPane.CLOSED_OPTION);

    	}
		design (space.width, space.height);  	
		createPlayer();
    }

    
	/**
	 * set up the panel and its elements for the given size 
	 * @param width
	 * @param height
	 */
	private void design (int width, int height)
	{
    	System.out.println("In Design");

		//Create Panels
		guiPanel = new JPanel ();
		guiPanelSize = new Dimension (width, height);

		guiPanel.setMaximumSize(guiPanelSize);
		guiPanel.setPreferredSize(guiPanelSize);
		
		guiPanel.setVisible(owner.propDisplayGui);
/*		
		 JButton openFile = new JButton( "Open file to play" );
		 openFile.addActionListener( new ActionListener() 
		 {
			 public void actionPerformed( ActionEvent e )
			 {
				 openFile();
				 createPlayer();
			 }
		 });

		guiPanel.add (openFile,BorderLayout.PAGE_START);
	*/	
		
	     canvas = new WindowsCanvas();
	     canvas.setSize(width,height);
	     
  	    canvas.setVisible(owner.propDisplayGui);
			
	     guiPanel.add(canvas);
	     guiPanel.revalidate();
	     guiPanel.repaint();
		
		
	    this.setLayout(new BorderLayout());
        add (guiPanel,BorderLayout.PAGE_START);
	}
  
/*
	 private void openFile()
	 {
		 JFileChooser fileChooser = new JFileChooser();

		 fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
		 int result = fileChooser.showOpenDialog( this );

		 // user clicked Cancel button on dialog
		 if ( result == JFileChooser.CANCEL_OPTION )
			 file = null;
		 else
			 file = fileChooser.getSelectedFile();
	 }
*/
	 private void createPlayer()
	 {

	      //Creation a media player :
	        System.out.println("Creating Player");

	        mediaPlayerFactory = new MediaPlayerFactory();
	        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
	        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
	        
	        if (owner.propDisplayGui == true)
	          mediaPlayer.setVideoSurface(videoSurface);
	        playerCreated=true;
	        owner.playerActive=true;	        
	 }
	 
	 
	 public void play(String mediafile)
	 {
		 System.out.println("Play method called !!");
		 
		 positionOutputRunning=false;
		 if (!actMediaFile.equals(mediafile)) stop();
		 if (playerCreated==false) createPlayer();
		 if (playerCreated==true)
		 {
			 
		   	if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Ended"))  
		        	mediaPlayer.stop();

			 
			if (!actMediaFile.equals(mediafile))
	        {
		        //System.out.println("Trying to open and play file:"+ mediafile);
	        	mediaPlayer.playMedia(mediafile);
	        	actMediaFile=mediafile;
	        }
	        else
	        {
		        //System.out.println("Trying to play mediafile");
	        	mediaPlayer.play();

	        }
			  AstericsThreadPool.instance.execute(new Runnable() {
				  public void run()
				  {
					  positionOutputRunning=true;
					  
					  System.out.println("Started Position Report Thread");
					  while (positionOutputRunning)
					  {
	    				try
	    				{
							   Thread.sleep(250);
	    				}
	    				catch (InterruptedException e) {}
	    				if (positionOutputRunning==true)
	    					owner.opActPos.sendData(ConversionUtils.doubleToBytes(mediaPlayer.getPosition()*100));
					  }
					  System.out.println("Ended Position Report Thread ");
		    		}
		    	  }
		    	  );	
		 }
	 }

	 public void pause()
	 {
		 System.out.println("Pause");
		 if (playerCreated==true)
		 {
		        mediaPlayer.pause();
		 }

	 }

	 public void stop()
	 {
		  System.out.println("Stop");
		  positionOutputRunning=false;


		 if (playerCreated==true)
		 {
			 System.out.println("Sending Stop to player");

		        mediaPlayer.stop();
		 }

	 }

	 public void disposePlayer()
	 {
		 System.out.println("Dispose Player");
	       owner.playerActive=false;
		 if (mediaPlayer!=null)
		 {
		        mediaPlayer.stop();
		        mediaPlayer.release();
		 }
         playerCreated=false;
         actMediaFile="";
	 }
	 
	 public void setRate(double rate)
	 {
		 if (playerCreated==true)
		 {
		        mediaPlayer.setRate((float)(rate/100));
		 }

	 }
	 public void setPosition(double position)
	 {
		 System.out.println("Setposition");

		 if (playerCreated==true)
		 {
			   	//  System.out.println(mediaPlayer.getMediaPlayerState().toString());

			   	if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Paused"))  
			   	{	mediaPlayer.setPosition((float) position/100); }
			   	else
			   	if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Ended"))  
			   	{	mediaPlayer.stop(); mediaPlayer.play();}
			   	else
			   	if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Playing"))  
			   	{  mediaPlayer.pause(); }
		 }
	 }
}
