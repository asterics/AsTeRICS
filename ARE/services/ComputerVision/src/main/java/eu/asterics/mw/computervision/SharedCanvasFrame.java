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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.computervision;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * Contains code to simplify and unify opening/closing and drawing on a
 * canvas/frame. It's planned to also support a shared usage of the canvas by
 * several plugins.
 * 
 * @author mad
 * 
 */
public class SharedCanvasFrame {
    public static SharedCanvasFrame instance = new SharedCanvasFrame();
    
    private Map<String, CanvasFrame> key2canvasFrame = new HashMap<String, CanvasFrame>();
    // CanvasFrame, FrameGrabber, and FrameRecorder use Frame objects to communicate image data.
    // We need a FrameConverter to interface with other APIs (Android, Java 2D, or OpenCV).
    private OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    
    public void createCanvasFrame(final String canvasKey, final String title, final double gammaOfGrabber,
            final Point pos, final Dimension d) {
        // must be invoked non-blocking because obviously the ctor of
        // CanvasFrame performs a SwingUtilities.invokeAndWait and
        // if we are called by an ARE-GUI action this would result in a
        // dead lock
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // CanvasFrame is a JFrame containing a Canvas
                // component, which is hardware accelerated.
                // It can also switch into full-screen mode when called
                // with a screenNumber.
                // We should also specify the relative monitor/camera
                // response for proper gamma correction.

                synchronized (key2canvasFrame) {
                    final CanvasFrame oldFrame = key2canvasFrame.get(canvasKey);
                    if (oldFrame != null) {
                        AstericsErrorHandling.instance.reportDebugInfo(null,
                                "Disposing old frame, then create new one.");
                        key2canvasFrame.remove(canvasKey);
                        oldFrame.dispose();
                    }

                    CanvasFrame frame = new CanvasFrame(title, CanvasFrame.getDefaultGamma() / gammaOfGrabber);
                    AstericsErrorHandling.instance.reportDebugInfo(null, "Setting to pos: " + pos + ", size: " + d);
                    frame.setLocation(pos);
                    // set default canvas size, if the plugin does not set it.
                    int w = d.width > 0 ? d.width : 96;
                    int h = d.height > 0 ? d.width : 96;
                    frame.setCanvasSize(w, h);

                    key2canvasFrame.put(canvasKey, frame);
                }
            }
        });
    }

    public CanvasFrame getCanvasFrame(String canvasKey) {
        synchronized (key2canvasFrame) {
            CanvasFrame frame = key2canvasFrame.get(canvasKey);
            return frame;
        }
    }

    public void showImage(String canvasKey, final IplImage image) {
        final CanvasFrame frame = getCanvasFrame(canvasKey);
        if (frame != null) {
            // invoke in Event Dispatch Thread, because of Swing
            // SwingUtilities.invokeLater(new Runnable() {
            // @Override
            // public void run() {
        	if(image!=null) {
        		frame.showImage(converter.convert(image));
        	}
            // }
            // });
        }
    }

    public void disposeFrame(final String canvasKey) {
        // Invoke the disposal in the event dispatch thread to resolve a
        // potential deadlock if the dispose is actually called from an ARE GUI
        // action.
        synchronized (key2canvasFrame) {
            final CanvasFrame frame = key2canvasFrame.get(canvasKey);
            if (frame != null) {
                key2canvasFrame.remove(canvasKey);

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.dispose();
                    }
                });
            }
        }
    }
}
