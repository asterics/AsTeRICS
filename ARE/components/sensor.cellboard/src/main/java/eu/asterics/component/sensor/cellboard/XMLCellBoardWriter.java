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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.helpers.DefaultHandler;

public class XMLCellBoardWriter extends DefaultHandler {
    CellBoardInstance owner;

    public XMLCellBoardWriter(CellBoardInstance owner) {
        this.owner = owner;

    }

    public void writeXML(String fileName) {
        if (fileName == "") {
            return;
        }
        if (!(fileName.contains(".xml"))) {
            fileName = fileName + ".xml";
        }
        String newline = System.getProperty("line.separator");

        System.out.println("writing xml file " + fileName);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(fileName));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        XMLStreamWriter out;
        try {
            out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, "utf-8"));

            out.writeStartDocument();
            out.writeCharacters(newline);
            out.writeStartElement("keyboard");

            // out.writeAttribute("rows", Integer.toString(0));
            // out.writeAttribute("columns", Integer.toString(0));
            // /*

            out.writeAttribute("caption", owner.getCaption());
            out.writeAttribute("rows", Integer.toString(owner.getRowCount()));
            out.writeAttribute("columns", Integer.toString(owner.getColumnCount()));
            out.writeAttribute("scanning", Integer.toString(owner.getScanMode()));
            out.writeAttribute("textColor", Integer.toString(owner.getTextColor()));
            out.writeAttribute("scanColor", Integer.toString(owner.getScanColor()));
            out.writeAttribute("scanCycles", Integer.toString(owner.getScanCycles()));
            out.writeAttribute("backgroundColor", Integer.toString(owner.getBackgroundColor()));
            out.writeAttribute("hoverTime", Integer.toString(owner.getHoverTime()));
            out.writeAttribute("hoverIndicator", Integer.toString(owner.getHoverIndicator()));
            out.writeAttribute("hoverFrameThickness", Integer.toString(owner.getHoverFrameThickness()));
            out.writeAttribute("commandSeparator", owner.propCommandSeparator);
            if (owner.getEnableEdit() == true) {
                out.writeAttribute("enableEdit", "true");
            } else {
                out.writeAttribute("enableEdit", "false");
            }
            if (owner.getEnableClickSelection() == true) {
                out.writeAttribute("enableClickSelection", "true");
            } else {
                out.writeAttribute("enableClickSelection", "false");
            }
            if (owner.propIgnoreKeyboardFileProperties == true) {
                out.writeAttribute("ignoreKeyboardFileProperties", "true");
            } else {
                out.writeAttribute("ignoreKeyboardFileProperties", "false");
            }
            if (owner.getDisplayGUI() == true) {
                out.writeAttribute("displayGUI", "true");
            } else {
                out.writeAttribute("displayGUI", "false");
                // */
            }

            for (int i = 0; i < owner.getColumnCount() * owner.getRowCount(); i++) {
                out.writeCharacters(newline);
                out.writeCharacters("\t");
                out.writeStartElement("button");

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("text");
                out.writeCharacters(owner.getCellCaption(i));
                out.writeEndElement();

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("icon");
                out.writeCharacters(owner.getImagePath(i));
                out.writeEndElement();

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("action");
                out.writeCharacters(owner.getCellText(i));
                out.writeEndElement();

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("sound");
                out.writeCharacters(owner.getSoundPath(i));
                out.writeEndElement();

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("soundPreview");
                out.writeCharacters(owner.getSoundPreviewPath(i));
                out.writeEndElement();

                out.writeCharacters(newline);
                out.writeCharacters("\t\t");
                out.writeStartElement("switchGrid");
                out.writeCharacters(owner.getSwitchGrid(i));
                out.writeEndElement();

                out.writeCharacters(newline);

                out.writeCharacters("\t");
                out.writeEndElement();
            }
            out.writeCharacters(newline);
            out.writeEndElement();
            out.writeEndDocument();

            out.close();

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}