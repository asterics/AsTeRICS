

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
package eu.asterics.component.sensor.micgpi;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine;

/**
 *
 * @author David Thaller
 */
public class RecordDevice {

    public TargetDataLine getTargetDataLine(String name) {
        TargetDataLine line = null;
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
            try {
                if (info.getDescription().contains("Capture") == false || info.getName().startsWith(name) == false) {
                    continue;
                }
                AudioFormat format = new AudioFormat(8000,8,1,true,true);
                line = AudioSystem.getTargetDataLine(format,info);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(RecordDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return line;
    }
	
	public TargetDataLine getDefaultTargetDataLine() {
		TargetDataLine line = null;
		AudioFormat format = new AudioFormat(8000,8,1,true,true);
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException ex) {
			
		}
		return line;
	}
    
    public List<String> getCaptureDeviceNames() {
        ArrayList<String> devNames = new ArrayList<>();
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info : mixerInfos) {
                if (info.getDescription().contains("Capture") == true) {
                    devNames.add(info.getName());
                }
        }
        return devNames;
    }
}
