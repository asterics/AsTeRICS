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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.mw.cimcommunication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.*;
import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * A CIM protocol packet wrapped into a Java class. This representation of one
 * packet holds all the necessary information to retrieve data from the packet.
 * The class also provides the means to read the packet from the incoming byte
 * stream and to write the packet to the output stream. It handles the 
 * endianness changes in the protocols if necessary.
 * 
 * @author Christoph Weiss [christoph.weiss@technikum-wien.at]
 *         Date: Nov 3, 2010
 *         Time: 02:22:08 PM
 */
public class CIMProtocolPacket {
	
	
	public final static byte MODE_CRC_ON  = 0x01;
	public final static byte MODE_CRC_OFF = 0x00;
	
	final static byte STATUS_CRC_MODE_MASK  				= 0x01;
	final static byte STATUS_ERROR_LOST_PACKET_MASK  	= 0x02;
	final static byte STATUS_ERROR_CRC_MASK  			= 0x04;
	final static byte STATUS_ERROR_INVALID_FEATURE_MASK  = 0x08;
	final static byte STATUS_ERROR_INVALID_VERSION_MASK  = 0x10;
	final static byte STATUS_ERROR_CIM_NOT_READY_MASK  	= 0x20;
	final static byte STATUS_ERROR_MASK  =  (byte) 0xbe;
    final static byte STATUS_ERROR_OTHER_ERROR_MASK  	=  (byte) 0x80;
	
	
	// request/reply codes, should be 
	public final static byte COMMAND_REQUEST_FEATURE_LIST    = 0x00;
	public final static byte COMMAND_REPLY_FEATURE_LIST      = 0x01;
	public final static byte COMMAND_REQUEST_WRITE_FEATURE   = 0x10;
	public final static byte COMMAND_REPLY_WRITE_FEATURE     = 0x10;
	public final static byte COMMAND_REQUEST_READ_FEATURE    = 0x11;
	public final static byte COMMAND_REPLY_READ_FEATURE      = 0x11;
	
	public final static byte COMMAND_EVENT_REPLY 			 = 0x20;
	
	public final static short COMMAND_REQUEST_RESET_CIM       = (short) 0x80;
	public final static short COMMAND_REPLY_RESET_CIM         = (short) 0x80;
	public final static short COMMAND_REQUEST_START_CIM       = (short) 0x81;
	public final static short COMMAND_REPLY_START_CIM         = (short) 0x81;
	public final static short COMMAND_REQUEST_STOP_CIM        = (short) 0x82;
	public final static short COMMAND_REPLY_STOP_CIM          = (short) 0x82;

	public static final short FEATURE_UNIQUE_SERIAL_NUMBER = 0;

	public static final int ERRORSTATE_NO_ERROR = 0;
	public static final int ERRORSTATE_RECOVERABLE = 1;
	public static final int ERRORSTATE_NOT_READY = 2;
	public static final int ERRORSTATE_DROP_PACKET = 3;
	
	boolean crcOn;  

	short areCimID;
	byte serialNumber;
	short featureAddress;
	short requestReplyCode;
	byte [] data;
	int crc;

	enum ParseState
	{
		areCimId,dataSize,serialNumber,featureAddress,reqReplyCode,data,crc 
	}

	ParseState parseState = ParseState.areCimId;
	byte [] parseBuffer = new byte[4];
	int parseIdxCtr = 0;

	private static Logger logger = AstericsErrorHandling.instance.getLogger();
	
	/**
	 * Constructor that creates a CIMProtocolPacket from an input stream. 
	 * This constructor is used to read in packets from the serial port 
	 * controller. 
	 * 
	 * @param in
	 * @param crcOn
	 * @throws CIMProtocolException
	 */
	public CIMProtocolPacket(InputStream in, boolean crcOn) throws CIMProtocolException
	{
		LEDataInputStream din = new LEDataInputStream(in);
		int datasize;
		
		this.crcOn = crcOn;
		
		/* assumes that the packet has been identified using 
		checkForPacketBoundary and the next byte will be the LSB of 
		software version */
        try 
        {
        	areCimID = din.readShort();
        	datasize         = din.readShort();
        	serialNumber     = din.readByte();
        	featureAddress   = din.readShort();
        	requestReplyCode = din.readShort();
        	
        	if (datasize > 0)
        	{
        		byte data_read = 0;
        		data = new byte[datasize];
        		
        		while (data_read < datasize)
        		{
        			data_read += din.readByte();
        		}
        	}
        	
        	if (crcOn)
        		crc = din.readInt();
        	
        	din.close();
        	in.close();
        }
        catch (IOException e)
        {  	
        	throw new CIMProtocolException();
        }
	}
	
	/**
	 * 
	 * @param buffer
	 * @param crcOn
	 * @throws CIMProtocolException
	 */
	public CIMProtocolPacket(byte [] buffer, boolean crcOn) throws CIMProtocolException
	{
		int datasize;		
		ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
		LEDataInputStream din = new LEDataInputStream(bin);
		
		try
		{
			boolean packetFound = false;
			while (!packetFound)
			{
				byte id = din.readByte();
				if (id == -1)
					throw new CIMProtocolException();

				if (id == '@')
				{
					id = din.readByte();
					if (id == -1)
						throw new CIMProtocolException();

					if (id == 'T')
						packetFound = true;
				}
				else
				{
					System.out.print((char) id);
				}
			}
			
			areCimID = din.readShort();
			datasize         = din.readShort();
			serialNumber     = din.readByte();
			featureAddress   = din.readShort();
			requestReplyCode = din.readShort();
    	
	    	if (datasize > 0)
	    	{
	    		byte data_read = 0;
	    		data = new byte[datasize];
	    		
	    		while (data_read < datasize)
	    		{
	    			int d = din.readByte();
	    			if (d == -1)
	    				throw new CIMProtocolException();
	    				data[data_read++] = (byte) d;
	    		}
	    	}
	    	
	    	if (crcOn)
	    		crc = din.readInt();
	    	
	    	din.close();
	    	bin.close();
		}
		catch (IOException e)
		{
			throw new CIMProtocolException();
		}
		
	}
	
	/**
	 * Parses a short from the first two elements of a byte array in little 
	 * endian byte order 
	 * @param data the byte array holding the value
	 * @return the parsed value as a short
	 */
	short leParseShort(byte [] data)
	{
		return (short)  ( ((data[1] << 8) & 0xff00) | ((data[0]) & 0xff) );
	}
	
	/**
	 * Parses a long from the first four  elements of a byte array in little 
	 * endian byte order 
	 * @param data the byte array holding the value
	 * @return the parsed value as a long
	 */
	long leParseLong(byte [] data)
	{
		return (long) ( ((data[3] << 24) & 0xff000000) | 
				        ((data[2] << 16) & 0x00ff0000) | 
				        ((data[1] <<  8) & 0x0000ff00) | 
				         (data[0]        & 0x000000ff) );
	}
	
	/**
	 * Parses the next byte of CIM protocol packet. This uses an internal state
	 * machine which checks for the packet identifier and then interprets the
	 * following bytes according to the project.	
	 * @param b the next byte in the packet/stream
	 * @return true if the packet has been parsed completely, false otherwise
	 */
	public boolean parsePacket(byte b)
	{
		boolean ret = false;
		switch (parseState)
		{
		case areCimId:
			parseBuffer[parseIdxCtr++] = b;
			if (parseIdxCtr == 2)
			{
				parseIdxCtr = 0;
				this.areCimID = leParseShort(parseBuffer);
				parseState = ParseState.dataSize;
			}
			break;
		case dataSize:
			parseBuffer[parseIdxCtr++] = b;
			if (parseIdxCtr == 2)
			{
				parseIdxCtr = 0;
				if (leParseShort(parseBuffer) > 0 )
				{
					data  = new byte[leParseShort(parseBuffer)];
				}
				else
				{
					data = null;
				}
				parseState = ParseState.serialNumber;
			}
			break;
		case serialNumber:
			this.serialNumber = b;
			parseState = ParseState.featureAddress;
			break;
		case featureAddress:
			parseBuffer[parseIdxCtr++] = b;
			if (parseIdxCtr == 2)
			{
				parseIdxCtr = 0;
				this.featureAddress = leParseShort(parseBuffer);
				parseState = ParseState.reqReplyCode;
			}
			break;
		case reqReplyCode:
			parseBuffer[parseIdxCtr++] = b;
			if (parseIdxCtr == 2)
			{
				parseIdxCtr = 0;
				requestReplyCode = leParseShort(parseBuffer);
				crcOn = (((requestReplyCode >> 8) & STATUS_CRC_MODE_MASK) == STATUS_CRC_MODE_MASK) ? true : false;
				if (data != null)
				{
					parseState = ParseState.data;
				}
				else if (crcOn)
				{
					parseState = ParseState.crc;
				}
				else
				{
					ret = true;
				}
			}
			break;
		case data:
			data[parseIdxCtr++] = b;
			if (parseIdxCtr == data.length)
			{
				parseIdxCtr = 0;
				if (crcOn)
				{
					parseState = ParseState.crc;
				}
				else
				{
					ret  = true;
				}
			}
			break;
		case crc:
			parseBuffer[parseIdxCtr++] = b;
			if (parseIdxCtr == 4)
			{
				//TODO check CRC
				requestReplyCode = leParseShort(parseBuffer);
				ret = true;
			}			
		}
		return ret;
	}
	
	/**
	 * Translates the request/reply code of a packet to a string representation
	 * @return string about content of field
	 */
	private String requestReplyCodeToString()
	{
		int errorWord = (requestReplyCode >> 8) & 0xff;
		StringBuffer buf = new StringBuffer();
		
		buf.append("error flags: ");
		if ((errorWord & STATUS_ERROR_LOST_PACKET_MASK) != 0)
			buf.append("lost packet, ");
		if ((errorWord & STATUS_ERROR_CRC_MASK) != 0)
			buf.append("crc mismatch, ");
		if ((errorWord & STATUS_ERROR_INVALID_FEATURE_MASK) != 0)
			buf.append("invalid feature request, ");
		if ((errorWord & STATUS_ERROR_INVALID_VERSION_MASK) != 0)
			buf.append("invalid version on, ");
		if ((errorWord & STATUS_ERROR_CIM_NOT_READY_MASK) != 0)
			buf.append("CIM not ready on, ");
		if ((errorWord & STATUS_ERROR_OTHER_ERROR_MASK) != 0)
			buf.append("other error, ");
		buf.append("; request/reply code: ").append(String.format("0x%x ", requestReplyCode & 0xff));
		
		return buf.toString();
	}
	
	/**
	 * Creates a string representation of the CIM protocol packet
	 * @return the string representation
	 */
	public String toString()
	{
		//TODO finish this
		StringBuffer buf = new StringBuffer();
		buf.append("AreCimID: ")
			.append(String.format("0x%x",this.areCimID))
			.append(", serialNumber: ")
			.append(this.serialNumber)
			.append(", featureAddress: ")
			.append(String.format("0x%x", this.featureAddress))
			.append(",")
			.append(requestReplyCodeToString())
			.append(", crc:")
			.append(this.crcOn ? "on" : "off" );

		if (data != null)
		{
		    buf.append(", data lg: ")
		    	.append(data != null ? data.length : 0)
		    	.append(", data: ");
			for (int i = 0; i < data.length; i++)
			{
				buf.append((int) data[i]);
				buf.append(", ");
			}
		}
		return buf.toString();
	}
	
	/**
	 * Serializes the packet to a byte stream which can be sent via the serial
	 * communication interface 
	 * @return the packet as a byte array
	 */
	public byte [] toBytes()
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		LEDataOutputStream dos = new LEDataOutputStream(bos);
		try {
			dos.writeByte('@');
			dos.writeByte('T');
			
			dos.writeShort(areCimID);
			dos.writeShort(data == null ? 0 : data.length);
			dos.writeByte(serialNumber);
			dos.writeShort(featureAddress);
			dos.writeShort(requestReplyCode);
			
			if (data != null)
				dos.write(data);
			
			if (crcOn)
				dos.writeLong(crc);
			
			dos.flush();
			dos.close();
			bos.close();
			
			return bos.toByteArray();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Looks for the packet identifier "@T" within an input stream
	 * @param input bye stream
	 * @return true if the packet bound has been found, false otherwise
	 */
	public static boolean checkForPacketBoundary(InputStream in)
	{
        int data;

        try 
        {
	        if ( ( data = in.read()) > -1 && ((byte) data == '@' )) {
	            if ( ( data = in.read()) > -1 && ((byte) data == 'T' )) {
	            	return true;
	            }
	        }
	        System.out.print((char) data);
        }
        catch (IOException e)
        {  	
        }
		return false;
	}
	
	/**
	 * Setter method
	 * @param crcOn
	 */
	public void useCrc(boolean crcOn)
	{
		this.crcOn = crcOn;
	}

	/**
	 * Getter method 
	 */
	public short getAreCimID() {
		return areCimID;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setAreCimID(short areCimID) {
		this.areCimID = areCimID;
	}

	/**
	 * Getter method 
	 */
	public byte getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setSerialNumber(byte serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Getter method 
	 */
	public short getFeatureAddress() {
		return featureAddress;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setFeatureAddress(short featureAddress) {
		this.featureAddress = featureAddress;
	}

	/**
	 * Getter method 
	 */
	public short getRequestReplyCode() {
		return requestReplyCode;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setRequestReplyCode(short requestReplyCode) {
		this.requestReplyCode = requestReplyCode;
	}

	/**
	 * Getter method 
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	public int getCrc() {
		return crc;
	}

	/**
	 * Setter method
	 * @param crcOn
	 */
	public void setCrc(int crc) {
		this.crc = crc;
	}

	public CIMProtocolPacket ()
	{
	}
	
	/**
	 * Checks the CRC of a packet, currently unimplemented
	 * @return
	 */
	boolean checkCrc()
	{
		//TODO implement CRC check
		return true;
	}

	/**
	 * Performs sanity checks on the instance of the packet and informs the 
	 * caller about the errors
	 * @return 0 if no error occured, an error code according to the error codes
	 * declared in CIMProtocolPacket.
	 */
	public int receivedWithoutErrors() {

		int ret = ERRORSTATE_NO_ERROR;
		int errorWord = (requestReplyCode >> 8) & 0xff;
		if ((errorWord & STATUS_ERROR_MASK) != 0)
		{
			if (!checkCrc())
			{
				logger.warning(this.getClass().getName()+"." +
						"receivedWithoutErrors:" +
						" Packet did not pass CRC check: " + this.toString());
				ret = ERRORSTATE_DROP_PACKET;
			}
			
			if ((errorWord & STATUS_ERROR_LOST_PACKET_MASK) != 0)
			{
				if ((requestReplyCode & 0xff) != 0x80)
				{
					logger.warning(this.getClass().getName()+"." +
						"receivedWithoutErrors: Faulty packet received: " +
						"lost packet error: " + this.toString());
					ret = ERRORSTATE_RECOVERABLE;
				}
			}
			if ((errorWord & STATUS_ERROR_CRC_MASK) != 0)
			{
				logger.warning(this.getClass().getName()+"." +
						"receivedWithoutErrors: Faulty packet received: " +
						"crc mismatch on sent packet: " + this.toString());
				ret = ERRORSTATE_DROP_PACKET;
			}
			if ((errorWord & STATUS_ERROR_INVALID_FEATURE_MASK) != 0)
			{
				if ((requestReplyCode & 0xff) != 0x80)
				{
					logger.warning(this.getClass().getName()+"." +
							"receivedWithoutErrors: Faulty packet received: " +
							"invalid feature request on: " + this.toString());
					ret = ERRORSTATE_RECOVERABLE;
				}
			}
			if ((errorWord & STATUS_ERROR_INVALID_VERSION_MASK) != 0)
			{
				logger.warning(this.getClass().getName()+"." +
						"receivedWithoutErrors: Faulty packet received: " +
						"invalid version on: " + this.toString());
				ret = ERRORSTATE_DROP_PACKET;
			}
			if ((errorWord & STATUS_ERROR_CIM_NOT_READY_MASK) != 0)
			{
				logger.warning(this.getClass().getName()+"." +
						"receivedWithoutErrors: Faulty packet received: " +
						"CIM not ready on: " + this.toString());
				ret = ERRORSTATE_NOT_READY;
			}
		}
//		logger.fine(this.getClass().getName()+".receivedWithoutErrors: " +
//				"Packet received: " + this.toString());
		return ret;
	}

}
