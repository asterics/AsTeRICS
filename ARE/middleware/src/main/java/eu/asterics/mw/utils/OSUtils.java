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

package eu.asterics.mw.utils;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.services.AstericsErrorHandling;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to find OS ARE is running on.
 * 
 * @author Martin Deinhofer [deinhofe@technikum-wien.at] Date: May 28, 2015
 */
public class OSUtils {
    private static Logger logger = AstericsErrorHandling.instance.getLogger();
    public static final String LINUX = "linux";
    public static final String MACOSX = "macosx";
    public static final String WINDOWS = "windows";
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static final String ARE_OPEN_URL_CMD_KEY_PREFIX = "ARE.openURL.cmd.";

    static {
        AREProperties.instance.setDefaultPropertyValue(ARE_OPEN_URL_CMD_KEY_PREFIX + LINUX, "sensible-browser", "Default Linux command to start a browser.");
        AREProperties.instance.setDefaultPropertyValue(ARE_OPEN_URL_CMD_KEY_PREFIX + WINDOWS, "explorer", "Default Windows command to start a browser.");
        AREProperties.instance.setDefaultPropertyValue(ARE_OPEN_URL_CMD_KEY_PREFIX + MACOSX, "open", "Default Mac OSX command to start a browser.");
    }

    public static enum OS_NAMES {
        ALL, WINDOWS, LINUX, MACOSX;
    }

    /**
     * Is the ARE running on any windows?
     * 
     * @return true: yes
     */
    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    /**
     * Is the ARE running on an Mac OS X?
     * 
     * @return true: yes
     */
    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    /**
     * Is the ARE runningn on Linux or Unix?
     * 
     * @return true: yes
     */
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
    }

    /**
     * This is a synonym of {@link #isUnix()};
     * 
     * @return
     */
    public static boolean isLinux() {
        return isUnix();
    }

    /**
     * Returns the operating system name according to AsTeRICS convention.
     * 
     * @return
     */
    public static String getOsName() {
        if (isWindows()) {
            return WINDOWS;
        } else if (isMac()) {
            return MACOSX;
        } else if (isUnix()) {
            return LINUX;
        }
        return "unknown";
    }

    /**
     * Starts the given application using its applicationPath, arguments and workingDirectory but only if the current platform equals to executeOnPlatform or if
     * executeOnPlatform equals to {@link OS_NAMES#ALL}.
     * 
     * @param applicationPath
     * @param arguments
     * @param workingDirectory
     * @param executeOnPlatform
     * @return : The Process oject of the started application.
     * @throws IOException
     */
    public static Process startApplication(String applicationPath, String arguments, String workingDirectory, OS_NAMES executeOnPlatform) throws IOException {
        if (applicationPath == null) {
            return null;
        }
        applicationPath = applicationPath.trim();
        if ("".equals(applicationPath)) {
            return null;
        }
        // quote command if it is with spaces
        if(applicationPath.contains(" ")) {
			applicationPath = "\"" + applicationPath + "\"";
        }
        // File applicationPathFile = ResourceRegistry.resolveRelativeFilePath(ResourceRegistry.getInstance().getAREBaseURI(), applicationPath);
        // applicationPath=FilenameUtils.separatorsToSystem("\""+applicationPath+"\"");
        // return startApplication("\"" + applicationPathFile.getPath() + "\"" + " " + arguments, workingDirectory, executeOnPlatform);
        return startApplication(applicationPath + " " + arguments, workingDirectory, executeOnPlatform);
    }

    /**
     * Starts the given application using applicationPathAndArguments and the given workingDirectory but only if the current platform equals to
     * executeOnPlatform or if executeOnPlatform equals to {@link OS_NAMES#ALL}.
     * 
     * @param applicationPathAndArguments
     * @param workingDirectory
     * @param executeOnPlatform
     * @return
     * @throws IOException
     */
    public static Process startApplication(String applicationPathAndArguments, String workingDirectory, OS_NAMES executeOnPlatform) throws IOException {
        if (!isCurrentOS(executeOnPlatform) || applicationPathAndArguments == null || "".equals(applicationPathAndArguments.trim())) {
            return null;
        }
        applicationPathAndArguments = applicationPathAndArguments.trim();

        try {
            List<String> command = new ArrayList<>();

            int cmdEndIndex = 0;
            // if cmd starts with quotes, extract the cmd with quotes.
            if (applicationPathAndArguments.indexOf("\"") == 0) {
                cmdEndIndex = applicationPathAndArguments.substring(1).indexOf('"');
                if (cmdEndIndex > -1) {
                    cmdEndIndex = cmdEndIndex + 2;
                }
            } else {
                // a cmd without quotes is split by a space or can be without arguments.
                cmdEndIndex = applicationPathAndArguments.indexOf(" ");
            }
            //if the cmdEndIndex < 0, we did not find a space or endQuote, so treat the whole string as command.
            if (cmdEndIndex < 0) {
                logger.warning("Only command, no arguments: " + applicationPathAndArguments);
                cmdEndIndex=applicationPathAndArguments.length();
            }
            // cmdEndIndex++;
            String cmdString = applicationPathAndArguments.substring(0, cmdEndIndex);
            // The command can be an absolute path, so ensure that it has proper system separators (\ or \\ or /)
            command.add(FilenameUtils.separatorsToSystem(cmdString));

            String arguments = "";
            try {
                arguments = applicationPathAndArguments.substring(cmdEndIndex + 1);
            } catch (IndexOutOfBoundsException e) {
                logger.fine("No cmd arguments found.");
            }

            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(arguments);
            while (m.find()) {
                String token = m.group(1);
                command.add(token);
                logger.fine("adding argument: " + token);
            }

            return startApplication(command, workingDirectory);
        } catch (Exception e) {
            throw new RuntimeException("Could not construct command string: " + e.getMessage());
        }
    }

    /**
     * Starts the given application using commandAndArgumentList and the given workingDirectory
     *
     * @param commandAndArgumentList list of strings where first element is the command to execute and the remaining ones
     *                               are arguments to pass to the command
     * @param workingDirectory
     * @return
     * @throws IOException
     */
    private static Process startApplication(List<String> commandAndArgumentList, String workingDirectory) throws IOException {
        try {
            if (commandAndArgumentList.isEmpty()) {
                logger.warning("Could not find command string in: " + commandAndArgumentList);
                return null;
            }

            ProcessBuilder builder = new ProcessBuilder(commandAndArgumentList);
            if (workingDirectory != null && !"".equals(workingDirectory)) {
                workingDirectory = FilenameUtils.separatorsToSystem(workingDirectory);
                // File workingDirFile=ResourceRegistry.resolveRelativeFilePath(ResourceRegistry.getInstance().getAREBaseURI(),workingDirectory);
                File workingDirFile = new File(workingDirectory);
                logger.fine("Setting workingDirectory to: " + workingDirFile);
                builder.directory(workingDirFile);
            }

            logger.fine("Finally constructed command: " + commandAndArgumentList);
            return builder.start();
        } catch (Exception e) {
            throw new RuntimeException("Could not construct command string: " + e.getMessage());
        }
    }

    /**
     * returns true if the given osName matches the current operating system or is OS_NAMES.ALL
     * @param osName
     * @return
     */
    private static boolean isCurrentOS(OS_NAMES osName) {
        return osName != null && getOsName().equalsIgnoreCase(osName.toString()) || osName != null && osName.equals(OS_NAMES.ALL);
    }

    /**
     * Opens the given URL with the pre-configured browser start command for the current platform but only if the current platform equals to executeOnPlatform
     * or if executeOnPlatform equals to {@link OS_NAMES#ALL}.
     * 
     * @param urlToOpen
     * @param executeOnPlatform
     * @return
     * @throws IOException
     */
    public static Process openURL(String urlToOpen, OS_NAMES executeOnPlatform) throws IOException {
        if (!isCurrentOS(executeOnPlatform)) {
            return null;
        }
        String browserStartCmd = AREProperties.instance.getProperty(ARE_OPEN_URL_CMD_KEY_PREFIX + getOsName());

        urlToOpen = urlToOpen.trim();
        urlToOpen = urlToOpen.replaceAll("^\"|\"$", ""); // remove quotes
        if(!urlToOpen.startsWith("http")) {
            urlToOpen = "http://" + urlToOpen;
        }
        if(isWindows()) {
            urlToOpen="\"" + urlToOpen + "\"";
        }

        List<String> commandList = Arrays.asList(browserStartCmd.trim(), urlToOpen);
        return startApplication(commandList, null);
    }

}
