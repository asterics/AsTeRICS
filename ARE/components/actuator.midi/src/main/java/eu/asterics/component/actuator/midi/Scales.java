package eu.asterics.component.actuator.midi;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Vector;

import eu.asterics.mw.services.AstericsErrorHandling;

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

/**
 * Scales.java
 * Purpose of this module:
 * deliveres the scales for the class MidiInstance
 * 
 * @Author Dominik Koller [dominik.koller@gmx.at]
 */


public class Scales 
{
	final static boolean debugOutput = false;

	int[] noteNumberArray;
	int size;
	
	public Scales()
	{
		 noteNumberArray= new int[256];
		 size=0;
	}
	
	/**RETURNS AN ARRAY OF THE MIDI NUMBERS CORRESPONDING TO THE SELECTED SCALE
    * @param index 	index is the value corresponding to the selected scale
    * @return		returns an array with the MIDI numbers corresponding to the scale
    */  
	public boolean loadScale (String scaleName)
	{
        FileInputStream fIn=null;
        byte[] fileInput= new byte[1128];
		
		int readBytes,len;
         
		if (!(scaleName.endsWith(".sc"))) scaleName+=".sc";
        try{
            fIn= new FileInputStream("data/actuator.midi/"+scaleName);

            //System.out.println(fIn.available()+ " bytes are available.");
            readBytes=fIn.read(fileInput);
            //System.out.println(readBytes+ " bytes have been read.");
            fIn.close();
            len=fromByteArray(fileInput,0);
            if (debugOutput) System.out.println("loading "+len+ " tones from midi tonescale "+ scaleName+".");
            
            for (int i=0;i<len;i++)
            {
            	noteNumberArray[i]=fromByteArray(fileInput,104+i*4);
                // System.out.println("Note "+noteNumberArray[i]+ " read.");
            }
            noteNumberArray[len]=0;
            size=len;
        }
        catch(IOException e){
        	
        	AstericsErrorHandling.instance.getLogger().fine("Could not load tonescale file data/actuator/midi/"+scaleName+".sc");
        	noteNumberArray[0]=0;
        	size=0;
        	return(false);
        	
        }
        return(true);
	
	}
	
	int fromByteArray(byte[] bytes,int index) {
	     return bytes[index+3] << 24 | (bytes[index+2] & 0xFF) << 16 | (bytes[index+1] & 0xFF) << 8 | (bytes[index+0] & 0xFF);
	}
	     
}
