

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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */
package eu.asterics.component.sensor.micgpi;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author david_t
 */
public class AudioGrabberThread extends Thread {

    private TargetDataLine line;

    public AudioGrabberThread(TargetDataLine line) {
        this.line = line;
    }
    
    private boolean buttonPressed = false;

    @Override
    public void run() {
        try {
            if (this.line == null) {
                return;
            }
            this.line.open();
            this.line.start();
            byte [] data = new byte[1];
            while (this.isInterrupted() == false) {
                int readBytes = line.read(data, 0, 1);
                if (readBytes == 0)
                    continue;
                if (data[0] > 30 && buttonPressed == true) {
                    System.out.println("Button released");
                    buttonPressed = false;
                }
                else if (data[0] < -30 && buttonPressed == false) {
                    System.out.println("Button pressed");
                    buttonPressed = true;
                }
            }
            this.line.stop();
            this.line.close();
        } catch (LineUnavailableException ex) {
            
        }
    }

}
