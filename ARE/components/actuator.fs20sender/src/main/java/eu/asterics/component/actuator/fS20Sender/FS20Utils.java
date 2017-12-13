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

package eu.asterics.component.actuator.fS20Sender;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.utils.OSUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FS20Utils {

    private static Logger logger = AstericsErrorHandling.instance.getLogger();

    private static final String REGISTRY_PATH_DEVICE_PARAMS = "Device Parameters";
    private static final String REGISTRY_PATH_FS20 = "SYSTEM\\CurrentControlSet\\Enum\\USB\\VID_18EF&PID_E015";
    private static final String REGISTRY_KEY_POWERMANAGEMENT = "EnhancedPowerManagementEnabled";
    static final String FILENAME_REGPATCH = "regpatchfs20.vbs";
    static final String FILENAME_REGPATCH2 = "regpatchfs20.cmd";

    public static byte Off = 0x00;
    public static byte OnStep1 = 0x01;
    public static byte OnStep2 = 0x02;
    public static byte OnStep3 = 0x03;
    public static byte OnStep4 = 0x04;
    public static byte OnStep5 = 0x05;
    public static byte OnStep6 = 0x06;
    public static byte OnStep7 = 0x07;
    public static byte OnStep8 = 0x08;
    public static byte OnStep9 = 0x09;
    public static byte OnStep10 = 0x0A;
    public static byte OnStep11 = 0x0B;
    public static byte OnStep12 = 0x0C;
    public static byte OnStep13 = 0x0D;
    public static byte OnStep14 = 0x0E;
    public static byte OnStep15 = 0x0F;
    public static byte OnStep16 = 0x10;
    public static byte OnOld = 0x11;
    public static byte Toggle = 0x12;
    public static byte DimUp = 0x13;
    public static byte DimDown = 0x14;
    public static byte DimUpDown = 0x15;
    public static byte TimeSet = 0x16;
    public static byte Learn = 0x17;
    public static byte OffForTimeOld = 0x18;
    public static byte OnForTimeOff = 0x19;
    public static byte OnOldForTimeOff = 0x1A;
    public static byte Reset = 0x1B;
    public static byte OnForTimeOld = 0x1E;
    public static byte OnOldForTimeOld = 0x1F;

    public static byte FS20StringToByte(String value) {
        byte temp = 0x00;
        int tempValue = Integer.parseInt(value);
        temp += (byte) ((tempValue % 10) - 1);
        tempValue = (tempValue - (tempValue % 10)) / 10;
        temp += (byte) (((tempValue % 10) - 1) * 4);
        tempValue = (tempValue - (tempValue % 10)) / 10;
        temp += (byte) (((tempValue % 10) - 1) * 16);
        tempValue = (tempValue - (tempValue % 10)) / 10;
        temp += (byte) (((tempValue % 10) - 1) * 64);
        return temp;
    }

    public static String TimeToString(int time) {
        String timeAsString = "";
        int highnibble = time;
        int lownibble = highnibble % 16;
        highnibble = (highnibble - lownibble) / 16;
        double tempTime = Math.pow(2.0, highnibble) * lownibble * 0.25;
        double seconds = tempTime % 60;
        double minutes = (tempTime - seconds) / 60;
        minutes = minutes % 60;
        double hours = (tempTime - (minutes * 60) - seconds) / 3600;

        if (hours != 0) {
            timeAsString = hours + "h ";
        }

        if (minutes != 0 || hours != 0) {
            timeAsString += minutes + "m ";
        }

        timeAsString += seconds + "s";

        return timeAsString;
    }

    public static byte GetTimeFromSeconds(double time) {
        double timeConstant = 0.25;
        int counter = 0;
        int value = 0;
        do {
            if ((value = (int) (time / timeConstant)) <= 15) {
                return (byte) (counter * 16 + value);
            }

            timeConstant = timeConstant * 2;
            counter++;
        } while (timeConstant < 2048);

        return 0x00; // Zeit nicht gefunden
    }

    public static byte[] houseCodeToHex(int houseCode) {
        int value = 0x00;
        int tmp;
        int u = 1;
        for (int i = 1; i < 100000000; i *= 10) {
            tmp = ((((int) (houseCode / i)) % 10) - 1) * u;
            u = u * 4;
            value += tmp;
        }

        byte[] hc = new byte[2];
        hc[0] = (byte) ((value >> 8) & 255);
        hc[1] = (byte) (value & 255);
        return hc;
    }

    public static byte addressToHex(int address) {
        int value = 0x00;
        int tmp;
        int u = 1;
        for (int i = 1; i < 10000; i *= 10) {
            tmp = ((((int) (address / i)) % 10) - 1) * u;
            u = u * 4;
            value += tmp;
        }

        return (byte) value;
    }

    public static boolean isWindowsOS() {
    	//Use already existing utility function for detecting os
        return OSUtils.isWindows();
    }

    public static boolean patchRegistryDisablePowerSaveMode() {
        boolean patched = false;
        try {
            if (Advapi32Util.registryKeyExists(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20)) {
                String[] subkeys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, REGISTRY_PATH_FS20);
                if (!allSubkeysPatched(subkeys)) {
                    String pathPatchFile = copyBatchFromJar(FILENAME_REGPATCH);
                    copyBatchFromJar(FILENAME_REGPATCH2);
                    ProcessBuilder builder = new ProcessBuilder("cscript", "/E:JScript", "/nologo", pathPatchFile,
                            getArgString(subkeys));
                    builder.directory(new File(getHomePath()));
                    Process process = builder.start();
                    patched = process.waitFor() == 0;
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "error patching registry.", e);
        }
        return patched;
    }

    private static String getArgString(String[] strings) {
        String args = "";
        for (String key : strings) {
            args += " \"" + key + "\"";
        }
        return args.trim();
    }

    /**
     * copies a file with given name from java package to a temp-file. the temp file is deleted on exit of the program
     *
     * @param batchFilename
     * @return the path of the temp-file
     * @throws IOException
     */
    static String copyBatchFromJar(String batchFilename) throws IOException {
    	//This is a small hack because we want to get the system or user temp dir but don't want to get a unique tempfilename.
    	//Create a temp file
    	File tempFile=File.createTempFile(batchFilename,"");
    	tempFile.deleteOnExit();
    	//use parent directory to know actual temp dir
    	File tempDir=tempFile.getParentFile();
    	//create a file with the name we want.
    	File batchFile=new File(tempDir,batchFilename);
    	//delete both temp files on exit of the program.
    	batchFile.deleteOnExit();
    	logger.fine("Extracting FS20 batch file to "+batchFile);
        ClassLoader loader = PCSDevice.class.getClassLoader();
        try(InputStream resource = loader.getResourceAsStream(batchFilename);) {
        	Files.copy(resource, batchFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return batchFile.getAbsolutePath();
    }

    private static String getHomePath() {
        return System.getProperty("user.dir");
    }

    private static boolean allSubkeysPatched(String[] subkeys) {
        for (String subkey : subkeys) {
            int value = 0;
            String regPath = null;
            try {
                regPath = REGISTRY_PATH_FS20 + "\\" + subkey + "\\" + REGISTRY_PATH_DEVICE_PARAMS;
                value = Advapi32Util.registryGetIntValue(WinReg.HKEY_LOCAL_MACHINE, regPath, REGISTRY_KEY_POWERMANAGEMENT);
            } catch (Exception e) {
                logger.log(Level.INFO, MessageFormat.format("could not get registry value {0} from path {1}", REGISTRY_KEY_POWERMANAGEMENT, regPath));
                continue;
            }
            if (value == 1) {
                return false;
            }
        }
        return true;
    }

}
