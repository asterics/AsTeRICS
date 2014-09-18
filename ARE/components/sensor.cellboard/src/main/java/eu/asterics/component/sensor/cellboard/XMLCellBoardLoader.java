package eu.asterics.component.sensor.cellboard;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.asterics.mw.services.AstericsErrorHandling;



public class XMLCellBoardLoader extends DefaultHandler 
{
	
	private StringBuilder sb;
	private int size;
	private int rows;
	private int cols;
	private int fontSize;
	private int height;
	private int width;
	private int scanning;
	//final EventPort [] etpCellArray = new EventPort[NUMBER_OF_CELLS];
	private String [] propCellTextArray;
	private String [] propCellActionTextArray;
	private String [] propCellImageArray;
	private int buttons;
	
	
	public int getRows() {
		return rows-1;
	}

	public int getFontSize() {
		return fontSize;
	}
	
	public int getCols() {
		return cols-1;
	}

	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getScanning() {
		return scanning;
	}
	
	public String[] getPropCellTextArray() {
		return propCellTextArray;
	}

	public String[] getPropCellActionTextArray() {
		return propCellActionTextArray;
	}

	public String[] getPropCellImageArray() {
		return propCellImageArray;
	}

	public XMLCellBoardLoader(int size) {
		sb = new StringBuilder();
		this.size = size;
		buttons = 0;
	}
	
	@Override
	  public void startElement( String namespaceURI, String localName,
	                            String qName, Attributes atts )
	  {
			if (qName.equalsIgnoreCase("keyboard")) {
				try {
					rows = Integer.parseInt(atts.getValue("rows"));
					cols = Integer.parseInt(atts.getValue("columns"));
					if (atts.getIndex("fontsize") != -1) {
						fontSize = Integer.parseInt(atts.getValue("fontsize"));
					} else {
						fontSize = -1;
					}
				} catch (NumberFormatException ne) {
					rows = -1;
					cols = -1;
				}
	    		try {
	    			width = Integer.parseInt(atts.getValue("width"));
	    		} catch (NumberFormatException ne) {
	    			width = -1;
	    		}
	    		try {
	    			height = Integer.parseInt(atts.getValue("height"));
    			} catch (NumberFormatException ne) {
    				height = -1;
    			}
	    		try {
	    			scanning = Integer.parseInt(atts.getValue("scanning"));
    			} catch (NumberFormatException ne) {
    				scanning = 0;
    			}
	    		propCellTextArray = new String[size];
	    		propCellActionTextArray = new String[size];
	    		propCellImageArray = new String[size];
	    		for (int i = 0; i < size; i++) {
	    			propCellTextArray[i] = "";
	    			propCellActionTextArray[i] = "";
	    			propCellImageArray[i] = "";
	    		}
	    	} else if (qName.equalsIgnoreCase("text")) {
	    		sb = new StringBuilder();
	    	} else if (qName.equalsIgnoreCase("action")) {
	    		sb = new StringBuilder();
	    	} else if (qName.equalsIgnoreCase("icon")) {
	    		sb = new StringBuilder();
	    	}
	  }
	
	  @Override
	  public void characters( char[] ch, int start, int length )
	  {
	    sb.append(ch,start,length);
	  }
	
	  @Override
		public void endElement(String uri, String localName, String qName) throws SAXException 
		{
		  	if (qName.equalsIgnoreCase("button")) {
		  		buttons++;
		  	} else if (qName.equalsIgnoreCase("icon")) {
				String iconPath = sb.toString(); 
				if (iconPath.length() > 0) {
					propCellImageArray[buttons] = iconPath;
				}
			} else if (qName.equalsIgnoreCase("text")) {
				String text = sb.toString(); 
				propCellTextArray[buttons] = text;
			} else if (qName.equalsIgnoreCase("action")) {
				String action = sb.toString(); 
				propCellActionTextArray[buttons] = action;
			}

		}
	  
	  @Override
	  public void startDocument()
	  {
	    // System.out.println( "Document starts." );
	  }

	  @Override
	  public void endDocument()
	  {
	    // System.out.println( "Document ends." );
	  }
	  
	  public void print() {
		  for (int i = 0; i < propCellTextArray.length;i++) {
			  System.out.print(propCellTextArray[i]+"////");
			  System.out.print(propCellActionTextArray[i]+"/////");
			  System.out.println(propCellImageArray[i]);
		  }
	  }
}
