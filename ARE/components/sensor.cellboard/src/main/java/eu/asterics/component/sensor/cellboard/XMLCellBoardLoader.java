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

package eu.asterics.component.sensor.cellboard;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLCellBoardLoader extends DefaultHandler {
    CellBoardInstance owner;

    private StringBuilder sb;
    private int buttons;
    private int size;
    int temprows = -1;
    int tempcols = -1;

    public XMLCellBoardLoader(int size, CellBoardInstance owner) {
        sb = new StringBuilder();
        this.size = size;
        this.owner = owner;
        buttons = 0;
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        if (qName.equalsIgnoreCase("keyboard")) {
            try {
                temprows = Integer.parseInt(atts.getValue("rows"));
                tempcols = Integer.parseInt(atts.getValue("columns"));

            } catch (Exception e) {
                System.out.println("Cellboard xml parser info: rows/cols not found!");
            }

            if (owner.propIgnoreKeyboardFileProperties == false) // load primary
                                                                 // cellboard
                                                                 // properties
                                                                 // only if not
                                                                 // ignored !!
            {
                if ((temprows > 0) && (tempcols > 0)) {
                    owner.propRows = temprows - 1;
                    owner.propColumns = tempcols - 1;
                }

                String temp;

                try {
                    owner.propFontSize = (float) (Integer.parseInt(atts.getValue("fontsize")));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: fontsize not found");
                }

                temp = atts.getValue("caption");
                if (temp != null) {
                    owner.propCaption = temp;
                } else {
                    System.out.println("Cellboard xml parser info: caption not found");
                }

                try {
                    owner.propScanMode = Integer.parseInt(atts.getValue("scanning"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: scanning not found not found");
                }
                try {
                    owner.propTextColor = Integer.parseInt(atts.getValue("textColor"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: textColor not found");
                }
                try {
                    owner.propScanColor = Integer.parseInt(atts.getValue("scanColor"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: scanColor not found");
                }
                try {
                    owner.propScanCycles = Integer.parseInt(atts.getValue("scanCycles"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: scanCycles not found");
                }
                try {
                    owner.propBackgroundColor = Integer.parseInt(atts.getValue("backgroundColor"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: backgroundColor not found");
                }
                try {
                    owner.propHoverTime = Integer.parseInt(atts.getValue("hoverTime"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: hoverTime not found");
                }
                try {
                    owner.propHoverIndicator = Integer.parseInt(atts.getValue("hoverIndicator"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: hoverIndicator not found");
                }
                try {
                    owner.propHoverFrameThickness = Integer.parseInt(atts.getValue("hoverFrameThickness"));
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: hoverFrameThickness not found");
                }

                temp = atts.getValue("commandSeparator");
                if (temp != null) {
                    owner.propCommandSeparator = temp;
                } else {
                    System.out.println("Cellboard xml parser info: commandSeparator not found");
                }

                try {
                    if (atts.getValue("enableEdit").equalsIgnoreCase("true")) {
                        owner.propEnableEdit = true;
                    } else {
                        owner.propEnableEdit = false;
                    }
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: enableEdit not found");
                }
                try {
                    if (atts.getValue("enableClickSelection").equalsIgnoreCase("true")) {
                        owner.propEnableClickSelection = true;
                    } else {
                        owner.propEnableClickSelection = false;
                    }
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: enableClickSelection not found");
                }
                try {
                    if (atts.getValue("ignoreKeyboardFileProperties").equalsIgnoreCase("true")) {
                        owner.propIgnoreKeyboardFileProperties = true;
                    } else {
                        owner.propIgnoreKeyboardFileProperties = false;
                    }
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: ignoreKeyboardFileProperties not found");
                }

                try {
                    if (atts.getValue("displayGUI").equalsIgnoreCase("true")) {
                        owner.propDisplayGUI = true;
                    } else {
                        owner.propDisplayGUI = false;
                    }
                } catch (Exception e) {
                    System.out.println("Cellboard xml parser info: displayGUI not found");
                }
            }

            owner.propCellCaptionArray = new String[size];
            owner.propCellTextArray = new String[size];
            owner.propCellImageArray = new String[size];
            owner.propCellSoundArray = new String[size];
            owner.propCellSoundPreviewArray = new String[size];
            owner.propCellSwitchGridArray = new String[size];
            for (int i = 0; i < size; i++) {
                owner.propCellCaptionArray[i] = "";
                owner.propCellTextArray[i] = "";
                owner.propCellImageArray[i] = "";
                owner.propCellSoundArray[i] = "";
                owner.propCellSoundPreviewArray[i] = "";
                owner.propCellSwitchGridArray[i] = "";
            }
        } else if (qName.equalsIgnoreCase("text")) {
            sb = new StringBuilder();
        } else if (qName.equalsIgnoreCase("action")) {
            sb = new StringBuilder();
        } else if (qName.equalsIgnoreCase("icon")) {
            sb = new StringBuilder();
        } else if (qName.equalsIgnoreCase("sound")) {
            sb = new StringBuilder();
        } else if (qName.equalsIgnoreCase("soundPreview")) {
            sb = new StringBuilder();
        } else if (qName.equalsIgnoreCase("switchGrid")) {
            sb = new StringBuilder();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        sb.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("button")) {
            buttons++;
        } else if (qName.equalsIgnoreCase("icon")) {
            String iconPath = sb.toString();
            if (iconPath.length() > 0) {
                owner.propCellImageArray[buttons] = iconPath;
            }
        } else if (qName.equalsIgnoreCase("text")) {
            String text = sb.toString();
            owner.propCellCaptionArray[buttons] = text;
        } else if (qName.equalsIgnoreCase("action")) {
            String action = sb.toString();
            owner.propCellTextArray[buttons] = action;
        } else if (qName.equalsIgnoreCase("sound")) {
            String sound = sb.toString();
            owner.propCellSoundArray[buttons] = sound;
        } else if (qName.equalsIgnoreCase("soundPreview")) {
            String soundPreview = sb.toString();
            owner.propCellSoundPreviewArray[buttons] = soundPreview;
        } else if (qName.equalsIgnoreCase("switchGrid")) {
            String switchGrid = sb.toString();
            owner.propCellSwitchGridArray[buttons] = switchGrid;
        }

    }

    @Override
    public void startDocument() {
        // System.out.println( "Document starts." );
    }

    @Override
    public void endDocument() {
        // System.out.println( "Document ends." );
    }

}
