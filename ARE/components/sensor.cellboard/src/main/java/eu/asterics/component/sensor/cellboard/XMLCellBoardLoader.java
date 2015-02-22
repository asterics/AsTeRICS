package eu.asterics.component.sensor.cellboard;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eu.asterics.mw.services.AstericsErrorHandling;



public class XMLCellBoardLoader extends DefaultHandler 
{
	CellBoardInstance owner;
	
	private StringBuilder sb;
	private int buttons;
	private int size;
	int temprows=-1;
	int tempcols=-1;
	

	public XMLCellBoardLoader(int size,CellBoardInstance owner ) {
		sb = new StringBuilder();
		this.size = size;
		this.owner=owner;
		buttons = 0;
	}
	
	@Override
	  public void startElement( String namespaceURI, String localName,
	                            String qName, Attributes atts )
	  {
			if (qName.equalsIgnoreCase("keyboard")) {
				try {
					temprows = Integer.parseInt(atts.getValue("rows"));
					tempcols = Integer.parseInt(atts.getValue("columns"));
					
				} catch (Exception e) {
					System.out.println("Cellboard loader: rows/cols not found!");
				}

				if ((temprows>0) && (tempcols>0))  // load primary properties only if not saved from Cellboard's own editor !!
				{
					owner.propRows=temprows-1;
					owner.propColumns=tempcols-1;
					
					try 
					{
						if (atts.getIndex("fontsize") != -1) 
							owner.propFontSize = (float) (Integer.parseInt(atts.getValue("fontsize")));
					}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: fontsize not found"); }
					/*
	    			width = Integer.parseInt(atts.getValue("width"));
	    			height = Integer.parseInt(atts.getValue("height")); */
		    		try	 {owner.propScanType = Integer.parseInt(atts.getValue("scanning not found")); }
					catch (Exception e) {	System.out.println("Cellboard xml parser: scanning not found not found"); } 
		    		try {	owner.propTextColor = Integer.parseInt(atts.getValue("textColor not found")); }
					catch (Exception e) {	System.out.println("Cellboard xml parser error: textColor not found");  }
		    		try {	owner.propScanColor = Integer.parseInt(atts.getValue("scanColor not found"));}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: scanColor not found");  }
					try {	owner.propBackgroundColor = Integer.parseInt(atts.getValue("backgroundColor not found")); }
					catch (Exception e) {	System.out.println("Cellboard xml parser error: backgroundColor not found");  }
					try {	owner.propHoverTime = Integer.parseInt(atts.getValue("hoverTime"));}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: hoverTime not found"); }
					try {	    		
			    		if (atts.getValue("enableEdit").equalsIgnoreCase("true"))
			    			owner.propEnableEdit=true;
			    		else owner.propEnableEdit=false;
					}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: enableEdit not found");  }
					try {
			    		if (atts.getValue("enableClickSelection").equalsIgnoreCase("true"))
			    			owner.propEnableClickSelection=true;
			    		else owner.propEnableClickSelection=false;
					}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: enableClickSelection not found");  }
		    		
					try {	owner.propCaption=atts.getValue("caption");}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: caption not found");  }
					try {
						if (atts.getValue("displayGUI").equalsIgnoreCase("true"))
			    			owner.propDisplayGUI=true;
			    		else owner.propDisplayGUI=false;
					}
					catch (Exception e) {	System.out.println("Cellboard xml parser error: displayGUI not found");  }
				}

		    	owner.propCellCaptionArray = new String[size];
				owner.propCellTextArray = new String[size];
				owner.propCellImageArray = new String[size];
				owner.propCellSoundArray = new String[size];
				owner.propCellSoundPreviewArray = new String[size];
	    		for (int i = 0; i < size; i++) {
	    			owner.propCellCaptionArray[i] = "";
	    			owner.propCellTextArray[i] = "";
	    			owner.propCellImageArray[i] = "";
	    			owner.propCellSoundArray[i] = "";
	    			owner.propCellSoundPreviewArray[i] = "";
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
	  
}
