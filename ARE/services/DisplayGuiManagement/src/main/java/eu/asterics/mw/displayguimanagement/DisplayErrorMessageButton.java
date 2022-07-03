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

package eu.asterics.mw.displayguimanagement;

import java.util.LinkedList;
import java.util.List;

public class DisplayErrorMessageButton extends DisplayButton {

    private final int CHARACTERS_PER_LINE = 18;
    private final int NUMBER_OF_LINES = 4;

    boolean active = false;

    List<String> errorMessages = new LinkedList<String>();

    public DisplayErrorMessageButton(int x, int y, int w, int h) {
        super("", x, y, w, h);
    }

    void addErrorMessage(String message) {
        active = true;
        if (message.length() > CHARACTERS_PER_LINE) {
            int lines = 0;
            StringBuffer buf = new StringBuffer();
            buf.append("\37      ERROR       \36\n\r");
            try {
                while (lines < NUMBER_OF_LINES) {
                    buf.append(message.substring(CHARACTERS_PER_LINE * lines,

                            CHARACTERS_PER_LINE * lines + CHARACTERS_PER_LINE));
                    buf.append("\n\r");
                    lines++;
                }
            } catch (IndexOutOfBoundsException e) {
                buf.append(message.substring(CHARACTERS_PER_LINE * lines));
            }
            message = buf.toString();
        }
        // System.out.println("Message added:" + message);
        errorMessages.add(message);
        caption = errorMessages.get(0);
    }

    @Override
    public void press(int x, int y) {
        errorMessages.remove(0);
        if (errorMessages.isEmpty()) {
            DisplayGuiManager.instance.errorMessageActive = false;
            DisplayGuiManager.instance.displayMainMenu();
        } else {
            caption = errorMessages.get(0);
            draw();
        }
    }
}
