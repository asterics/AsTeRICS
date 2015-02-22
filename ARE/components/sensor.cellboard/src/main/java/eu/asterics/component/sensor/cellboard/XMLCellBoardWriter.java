package eu.asterics.component.sensor.cellboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.xml.sax.helpers.DefaultHandler;


public class XMLCellBoardWriter extends DefaultHandler 
{
	CellBoardInstance owner;

	public XMLCellBoardWriter(CellBoardInstance owner )
	{
		this.owner = owner;
		
	}
	
	public void writeXML(String fileName)
	{
		if (fileName == "") return;
		if (!(fileName.contains(".xml"))) fileName=fileName+".xml";
		String newline=System.getProperty("line.separator");
		
		System.out.println("writing xml file "+fileName);
		OutputStream outputStream =null;
		try {
			outputStream = new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		XMLStreamWriter out;
		try {
			out = XMLOutputFactory.newInstance().createXMLStreamWriter(
			                new OutputStreamWriter(outputStream, "utf-8"));
						
			out.writeStartDocument();
			out.writeCharacters(newline);
			out.writeStartElement("keyboard");
			
			out.writeAttribute("rows", Integer.toString(0));
			out.writeAttribute("columns", Integer.toString(0));
			/*
			out.writeAttribute("rows", Integer.toString(owner.getRowCount()));
			out.writeAttribute("columns", Integer.toString(owner.getColumnCount()));
			out.writeAttribute("scanning", Integer.toString(owner.getScanType()));
			out.writeAttribute("textColor", Integer.toString(owner.getTextColor()));
			out.writeAttribute("scanColor", Integer.toString(owner.getScanColor()));
			out.writeAttribute("backgroundColor", Integer.toString(owner.getBackgroundColor()));
			out.writeAttribute("hoverTime", Integer.toString(owner.getHoverTime()));
			out.writeAttribute("caption", owner.getCaption());
			if (owner.getEnableEdit()==true)	
				out.writeAttribute("enableEdit", "true"); else out.writeAttribute("enableEdit", "false");
			if (owner.getEnableClickSelection()==true)	
				out.writeAttribute("enableClickSelection", "true"); else out.writeAttribute("enableClickSelection", "false");
			if (owner.getDisplayGUI()==true)	
				out.writeAttribute("displayGUI", "true"); else out.writeAttribute("displayGUI", "false");
			 */

			for (int i=0;i<owner.getColumnCount()*owner.getRowCount();i++)
			{
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

				out.writeCharacters(newline);
				out.writeCharacters("\t\t");
				out.writeStartElement("soundPreview");
				out.writeCharacters(owner.getSoundPreviewPath(i));
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