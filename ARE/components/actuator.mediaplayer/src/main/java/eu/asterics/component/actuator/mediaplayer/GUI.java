
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
 *         License: GPL v3.0
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.mediaplayer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.jna.NativeLibrary;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.services.AstericsThreadPool;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventAdapter;
import uk.co.caprica.vlcj.player.list.MediaListPlayerMode;
import uk.co.caprica.vlcj.runtime.windows.WindowsCanvas;

/**
 * Implements the Graphical User Interface for the Mediaplayer component
 * 
 * @author Chris Veigl [ veigl@technikum-wien.at} Date: 30.7.2013
 */
public class GUI extends JPanel {

    private JPanel guiPanel;
    private Dimension guiPanelSize;
    private final MediaPlayerInstance owner;
    private String actMediaFile = "";
    private boolean playerCreated = false;

    private boolean positionOutputRunning = false;

    // The size does NOT need to match the mediaPlayer size - it's the size that
    // the media will be scaled to
    // Matching the native size will be faster of course

    /**
     * Image to render the video frame data.
     */

    private MediaPlayerFactory mediaPlayerFactory;

    private EmbeddedMediaPlayer mediaPlayer;

    private WindowsCanvas canvas;

    private MediaListPlayer mediaListPlayer = null;

    /**
     * The class constructor, initialises the GUI
     * 
     * @param owner
     *            the Slider instance
     */
    public GUI(final MediaPlayerInstance owner, final Dimension space) {
        super();

        System.out.println("In Constructor");

        this.owner = owner;
        this.setPreferredSize(new Dimension(space.width, space.height));

        if (new File(owner.propPathToVLC).exists()) {
            NativeLibrary.addSearchPath("libvlc", owner.propPathToVLC);
        } else if (new File("C:\\Program Files (x86)\\VideoLAN\\VLC").exists()) {
            NativeLibrary.addSearchPath("libvlc", "C:\\Program Files (x86)\\VideoLAN\\VLC");
        } else if (new File("C:\\Program Files\\VideoLAN\\VLC").exists()) {
            NativeLibrary.addSearchPath("libvlc", "C:\\Program Files\\VideoLAN\\VLC");
        } else {
            JOptionPane.showConfirmDialog(null,
                    "The VLC installation could not be found in C:\\Program Files\\VideoLan or C:\\Program Files (x86)\\VideoLan .. \n please install VLC player (32 bit version).",
                    "VLC native library not found !", JOptionPane.CLOSED_OPTION);

        }
        design(space.width, space.height);
        createPlayer();
    }

    /**
     * set up the panel and its elements for the given size
     * 
     * @param width
     * @param height
     */
    private void design(int width, int height) {
        System.out.println("In Design");

        // Create Panels
        guiPanel = new JPanel();
        guiPanelSize = new Dimension(width, height);

        guiPanel.setMaximumSize(guiPanelSize);
        guiPanel.setPreferredSize(guiPanelSize);

        guiPanel.setVisible(owner.propDisplayGui);
        /*
         * JButton openFile = new JButton( "Open file to play" );
         * openFile.addActionListener( new ActionListener() { public void
         * actionPerformed( ActionEvent e ) { openFile(); createPlayer(); } });
         * 
         * guiPanel.add (openFile,BorderLayout.PAGE_START);
         */

        canvas = new WindowsCanvas();
        canvas.setSize(width, height);

        canvas.setVisible(owner.propDisplayGui);

        guiPanel.add(canvas);
        guiPanel.revalidate();
        guiPanel.repaint();

        this.setLayout(new BorderLayout());
        add(guiPanel, BorderLayout.PAGE_START);
    }

    /*
     * private void openFile() { JFileChooser fileChooser = new JFileChooser();
     * 
     * fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ); int result =
     * fileChooser.showOpenDialog( this );
     * 
     * // user clicked Cancel button on dialog if ( result ==
     * JFileChooser.CANCEL_OPTION ) file = null; else file =
     * fileChooser.getSelectedFile(); }
     */
    private void createPlayer() {

        // Creation a media player :
        System.out.println("Creating Player");

        mediaPlayerFactory = new MediaPlayerFactory();
        mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);

        if (owner.propDisplayGui == true) {
            mediaPlayer.setVideoSurface(videoSurface);
        }
        playerCreated = true;
        owner.playerActive = true;

        mediaListPlayer = mediaPlayerFactory.newMediaListPlayer();
        mediaListPlayer.addMediaListPlayerEventListener(new MediaListPlayerEventAdapter() {
            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, libvlc_media_t item, String itemMrl) {
                System.out.println("nextItem():" + itemMrl);
            }
        });

        mediaListPlayer.setMediaPlayer(mediaPlayer); // <--- Important,
                                                     // associate the media
                                                     // player with the media
                                                     // list player

    }

    public void playNext() {
        if (mediaListPlayer != null) {
            System.out.println("Play next medialist item !");
            mediaListPlayer.playNext();
        }
    }

    public void playPrevious() {
        if (mediaListPlayer != null) {
            System.out.println("Play previous medialist item !");
            mediaListPlayer.playPrevious();
        }
    }

    public void play(String mediafile) {
        System.out.println("Play method called: play " + mediafile);

        positionOutputRunning = false;
        if (!actMediaFile.equals(mediafile)) {
            stop();
        }
        if (playerCreated == false) {
            createPlayer();
        }
        if (playerCreated == true) {

            if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Ended")) {
                mediaPlayer.stop();
            }

            if (!actMediaFile.equals(mediafile)) {
                System.out.println("Trying to open and play file:" + mediafile);

                File pathName = new File(mediafile);
                String[] fileNames = pathName.list(); // lists all files in the
                                                      // directory
                if (fileNames == null) {
                    System.out.println("file " + mediafile + " is not a directory - trying to play file !");
                    /*
                     * MediaList mediaList = mediaPlayerFactory.newMediaList();
                     * String[] options = {};
                     * 
                     * mediaList.addMedia(mediafile, options);
                     * mediaListPlayer.setMediaList(mediaList);
                     * mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
                     * mediaListPlayer.play();
                     */
                    mediaPlayer.playMedia(mediafile);
                    actMediaFile = mediafile;
                } else {
                    System.out.println("directory " + mediafile + " found - creating media list !");

                    MediaList mediaList = mediaPlayerFactory.newMediaList();
                    String[] options = {};

                    for (int i = 0; i < fileNames.length; i++) {
                        File f = new File(pathName.getPath(), fileNames[i]); // getPath
                                                                             // converts
                                                                             // abstract
                                                                             // path
                                                                             // to
                                                                             // path
                                                                             // in
                                                                             // String,
                        // constructor creates new File object with fileName
                        // name
                        if (f.isDirectory()) {
                            System.out.println("skipping subfolder  " + fileNames[i]);
                            // nextDir.add(f.getPath());
                        } else {
                            System.out.println("adding file" + f.getPath());
                            mediaList.addMedia(f.getPath(), options);
                        }
                    }
                    mediaListPlayer.setMediaList(mediaList);
                    mediaListPlayer.setMode(MediaListPlayerMode.LOOP);
                    if (mediaList.size() > 0) {
                        mediaListPlayer.playItem(0);
                        // mediaListPlayer.play();
                    }
                }
            } else {
                System.out.println("Trying to play legacy mediafile");
                mediaPlayer.play();

            }
            AstericsThreadPool.instance.execute(new Runnable() {
                @Override
                public void run() {
                    positionOutputRunning = true;

                    System.out.println("Started Position Report Thread");
                    while (positionOutputRunning) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                        }
                        if (positionOutputRunning == true) {
                            owner.opActPos.sendData(ConversionUtils.doubleToBytes(mediaPlayer.getPosition() * 100));
                        }
                    }
                    System.out.println("Ended Position Report Thread ");
                }
            });
        }
    }

    public void pause() {
        System.out.println("Pause");
        if (playerCreated == true) {
            mediaPlayer.pause();
        }

    }

    public void stop() {
        System.out.println("Stop");
        positionOutputRunning = false;

        if (playerCreated == true) {
            System.out.println("Sending Stop to player");

            mediaPlayer.stop();
        }

    }

    public void disposePlayer() {
        System.out.println("Dispose Player");
        owner.playerActive = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        playerCreated = false;
        actMediaFile = "";
    }

    public void setRate(double rate) {
        if (playerCreated == true) {
            mediaPlayer.setRate((float) (rate / 100));
        }

    }

    public void setPosition(double position) {
        System.out.println("Setposition");

        if (playerCreated == true) {
            // System.out.println(mediaPlayer.getMediaPlayerState().toString());

            if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Paused")) {
                mediaPlayer.setPosition((float) position / 100);
            } else if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Ended")) {
                mediaPlayer.stop();
                mediaPlayer.play();
            } else if (mediaPlayer.getMediaPlayerState().toString().equals("libvlc_Playing")) {
                mediaPlayer.pause();
            }
        }
    }
}
