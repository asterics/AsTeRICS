

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.actuator.netconnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

//import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import eu.asterics.mw.services.AstericsErrorHandling;



/**
 * 
 * This class implements the communication protocol for the NetConnection plugin.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class ProtocolService {
	
	private final int headerSize=6;
	
	private static final byte firstByte=0x40;
	private static final byte secondByte=0x4e;
	
	private static final int eventMaxPort=10;
	private static final int integerMaxPort=5;
	private static final int doubleMaxPort=5;
	private static final int stringMaxPort=5;
	
	DataInputStream inputStream=null;
	DataOutputStream outputStream=null;
	private boolean ready=true;
	private int offset=0;
	private byte[] offsetBytes=new byte[headerSize];
		
	
	/**
	 * The class constructor.
	 * @param inputStream socket input stream
	 * @param outputStream socket output stream
	 */
	public ProtocolService(DataInputStream inputStream,DataOutputStream outputStream){
		
		this.inputStream=inputStream;
		this.outputStream=outputStream;
		
	}
	
	/**
	 * Checks if the packet header is available.
	 * @return true if header is available
	 */
	public boolean checkHeaderAvailable(){
		
		headerReceived=false;
		
		if(!ready){
			AstericsErrorHandling.instance.getLogger().fine("Not ready");
			return false;
		}
		
		int bytes=0;
		
		try{
			bytes=inputStream.available();
		}catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
		}
		
		if(bytes<headerSize){
			return false;
		}
		else{
			return true;
		}
		
	}
	
	/**
	 * Decodes the command from the packet header
	 * @param commandByte the command byte 
	 * @return command of the packet
	 */
	private Command getCommand(byte commandByte)
	{
		if((commandByte==0x41)||(commandByte==0x61))
		{
			return Command.Action;
		}
		
		if((commandByte==0x4E)||(commandByte==0x6E))
		{
			return Command.None;
		}
		
		if((commandByte==0x45)||(commandByte==0x65))
		{
			return Command.Event;
		}
		
		if((commandByte==0x49)||(commandByte==0x69))
		{
			return Command.Integer;
		}
		
		if((commandByte==0x44)||(commandByte==0x64))
		{
			return Command.Double;
		}
		
		if((commandByte==0x53)||(commandByte==0x73))
		{
			return Command.String;
		}
		
		return Command.None;
	}
	
	
	/**
	 * Decodes the port number from the packet header
	 * @param portByte the port byte 
	 * @return port number
	 */
	private int getPort(byte portByte)
	{
		int port=0;
		return port|portByte;
	}
	
	/**
	 * Decodes the size of the data
	 * @param first high bite of the size
	 * @param second low byte of the size
	 * @return data size
	 */
	private int getDataSize(byte first,byte second)
	{
		int dataSize=0;
		int secondByte=0;
		int firstByte=0;
		
		firstByte=0xFF & first;
		secondByte=0xFF & second;
		
		dataSize=dataSize|firstByte;
		dataSize=dataSize<<8;
		dataSize=dataSize|secondByte;
		
		
		return dataSize;
	}
	
	/**
	 * Checks if data of the command is available.
	 * @param size of the expected data
	 * @return true if data is ready
	 */
	public boolean checkDataAvailable(int size){
		try{
			int bytesAvailable=inputStream.available();
			if(bytesAvailable<size)
			{
				return false;
			}
			else
			{
				return true;
			}
		}catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
		}
		return false;
	}
	
	
	/**
	 * Gets data of the packet header.
	 * @return header data
	 */
	public HeaderInfo getHeader()
	{
		try{
			
			byte[] header = new byte[headerSize];
			
			for(int i=0;i<offset;i++)
			{
				header[i]=offsetBytes[i];
			}
			
			int readBytes = inputStream.read(header, offset, headerSize-offset);
			
			if(readBytes< headerSize-offset)
			{
				offset = readBytes+offset;
				for(int i=0;i<offset;i++)
				{
					offsetBytes[i]=header[i];
				}
				
				return new HeaderInfo(Command.None,0,0);
			}
			
			offset=0;
			
			if((header[0]==firstByte)&&(header[1]==secondByte))
			{
				Command command = getCommand(header[2]);
				int port =getPort(header[3]);
				int size=getDataSize(header[4],header[5]);
				
				headerReceived=true;
				
				return new HeaderInfo(command,port,size);
				
				//short dataSize=inputStream.readShort();
				/*
				if(command==Command.None)
				{
					//TODO 
				}
				
				if(checkCommandData(command)==false && dataSize>0)
				{
					//TODO
				}*/
				
				
			}
			else
			{
				boolean found=false;
				for(int i=1;i<headerSize-1;i++)
				{
					if((header[i]==firstByte)&&(header[i+1]==secondByte))
					{
						offset=headerSize-i;
						for(int j=0;j<offset;j++)
						{
							offsetBytes[j]=header[j+i];
						}
						
						found=true; 
						break;
					}
				}
				
				if(!found)
				{
					if(header[headerSize-1]==firstByte)
					{
						offset=1;
						offsetBytes[0]=firstByte;
					}
				}
				
				return new HeaderInfo(Command.None,0,0);
			}
			
		}catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
			//command=Command.None;
			return new HeaderInfo(Command.None,-1,-1);
		}
	}
	
	/**
	 * Prepares the event packet frame.
	 * @param port the event port
	 * @return Bytes array of the event packet
	 */
	static byte[] prepareEventFrame(int port)
	{
		
		if((port<0)||(port>eventMaxPort))
		{
			return null;
		}
		
		byte[] frame=new byte[6];
		frame[0]=firstByte;
		frame[1]=secondByte;
		frame[2]=0x45;
		frame[3]=(byte)port;
		frame[4]=0;
		frame[5]=0;
		
		return frame;
	}
	
	/**
	 * Prepares the action packet frame.
	 * @return Bytes array of the action packet
	 */
	static byte[] prepareActionFrame()
	{
			
		byte[] frame=new byte[6];
		frame[0]=firstByte;
		frame[1]=secondByte;
		frame[2]=0x41;
		frame[3]=0;
		frame[4]=0;
		frame[5]=0;
		
		return frame;
	}
	
	
	/**
	 * Prepares the integer packet frame.
	 * @param port the integer port
	 * @param value integer value
	 * @return Bytes array of the integer packet
	 */
	static byte[] prepareIntegerFrame(int port, int value)
	{
			
		if((port<0)||(port>integerMaxPort))
		{
			return null;
		}
		
		 
		
		byte[] frame=new byte[10];
		frame[0]=firstByte;
		frame[1]=secondByte;
		frame[2]=0x49;
		frame[3]=(byte)port;
		frame[4]=0;
		frame[5]=0x04;
		frame[6]=(byte)(value >>> 24);
		frame[7]=(byte)(value >>> 16);
		frame[8]=(byte)(value>>> 8);
		frame[9]= (byte) value;
		
		return frame;
	}
	
	/**
	 * Prepares the double packet frame.
	 * @param port the double port
	 * @param value double value
	 * @return Bytes array of the double packet
	 */
	static byte[] prepareDoubleFrame(int port, double value)
	{
			
		if((port<0)||(port>doubleMaxPort))
		{
			return null;
		}
		
		long doubleValue = Double.doubleToRawLongBits(value); 
		
		byte[] frame=new byte[14];
		frame[0]=firstByte;
		frame[1]=secondByte;
		frame[2]=0x44;
		frame[3]=(byte)port;
		frame[4]=0;
		frame[5]=0x08;
		frame[6]=(byte)(doubleValue >>> 56);
		frame[7]=(byte)(doubleValue >>> 48);
		frame[8]=(byte)(doubleValue >>> 40);
		frame[9]=(byte)(doubleValue >>> 32);
		frame[10]=(byte)(doubleValue >>> 24);
		frame[11]=(byte)(doubleValue >>> 16);
		frame[12]=(byte)(doubleValue>>> 8);
		frame[13]= (byte) doubleValue;
		
		return frame;
	}
	
	
	/**
	 * Prepares the string packet frame.
	 * @param port the string port
	 * @param text string value
	 * @return Bytes array of the string packet
	 */
	static byte[] prepareStringFrame(int port, String text)
	{
		
		try
		{
			if((port<0)||(port>stringMaxPort))
			{
				return null;
			}
			
		
			
			int stringLength=text.length();
			String stringValue="";
			
			if(stringLength>0x12c)
			{
				stringValue=text.substring(0, 0x12c);
			}
			else
			{
				stringValue=text;
			}
			
			byte[] byteArray=stringValue.getBytes("UTF-16BE");
			
			if(byteArray.length>0x258)
			{
				int toRemove=(byteArray.length-0x258)/2;
				stringValue=stringValue.substring(0,stringValue.length()-toRemove);
				byteArray=stringValue.getBytes("UTF-16BE");
				
			}
			
			int stringSize=byteArray.length;
			
			byte[] frame=new byte[stringSize+6];
			frame[0]=firstByte;
			frame[1]=secondByte;
			frame[2]=0x53;
			frame[3]=(byte)port;
			frame[4]=(byte)(stringSize>>> 8);
			frame[5]= (byte) stringSize;
			
			for(int i=0;i<stringSize;i++)
			{
				frame[6+i]=byteArray[i];
			}
			
			return frame;
		}catch(UnsupportedEncodingException e)
		{
			return null;
		}
		
		
	}
	
	/**
     * This method sends command through the network. 
     * @param command defines value type
     * @param port defines the port of the remote receiver.
     * @param doubleData double value
     * @param integerData integer value
     * @param stringData string value
     * @param send result
     */
	public SendError SendCommand(Command command,int port,double doubleData,int integerData,String stringData)
	{
		byte[] frame=null;
		switch (command)
		{
		case Action:
		{
			frame=prepareActionFrame();
			break;
		}
		case Event:
		{
			frame = prepareEventFrame(port);
			break;
		}
		case Integer:
		{
			frame=prepareIntegerFrame(port,integerData);
			break;
		}
		case Double:
		{
			frame=prepareDoubleFrame(port,doubleData);
			break;
		}
		case String:
		{
			frame=prepareStringFrame(port, stringData);
			break;
		}
		default:
		{
			AstericsErrorHandling.instance.getLogger().warning("Command not found");
			return SendError.CommandNotRecognized;
		}
		}
		
		if(frame==null)
		{
			AstericsErrorHandling.instance.getLogger().warning("Wrong data");
			return SendError.WrongData;
		}
		
		try {
			outputStream.write(frame);
			outputStream.flush();
			
		} catch (IOException e) {
		
			AstericsErrorHandling.instance.getLogger().warning("Send command error");
			return SendError.SendError;
		}
		
		return 	SendError.OK;
	}
	
	/**
	 * Gets package data.
	 * @param size size of the data
	 * @return data byte array
	 */
	public byte[] getData(int size)
	{
		if(size==0)
		{
			return null;
		}
		try
		{
			byte [] data= new byte[size];
			int readBytes = inputStream.read(data, 0, size);
			
			if(readBytes<size)
			{
				AstericsErrorHandling.instance.getLogger().warning("No all data downloaded");
			}
			
			return data;
		}
		catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
		}
		return null;
	}
	
	/**
	 * Decodes the integer value form the data.
	 * @param data data byte array
	 * @return the integer value
	 */
	public int getInteger(byte[] data)
	{
		if((data==null)||(data.length==0))
		{
			return 0;
		}
		
		int value=0;
		for(int i=0;i<data.length;i++)
		{
			value=value<<8;
			value=value|(data[i] & 0xff);
		}
		return value;
	}
	
	/**
	 * Decodes the double value from the data.
	 * @param data data byte array
	 * @return the double value
	 */
	public double getDouble(byte[] data)
	{
		
		if((data==null)||(data.length==0))
		{
			return 0;
		}
		
		long value=0;
		for(int i=0;i<data.length;i++)
		{
			value=value<<8;	
			value=value|(data[i] & 0xffL);
		}
		
		return Double.longBitsToDouble(value);
	}
	
	/**
	 * Decodes the string value from the data.
	 * @param data data byte array
	 * @return the string value
	 */
	public String getString(byte[] data)
	{
		
		if((data==null)||(data.length==0))
		{
			return "";
		}
		
		try
		{
			return new String(data, 0, data.length, "UTF-16BE");
		}
		catch(UnsupportedEncodingException ex)
		{
			return "";
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	private boolean headerReceived=false;
	
	/**
	 * Returns true if the header was received. Last getHeader call was successful.
	 * @return true if the header was received.
	 */
	public boolean getHeaderReceived()
	{
		return headerReceived;
	}
}

enum Command{
	None,Action,Event,Integer,Double,String 
}

enum SendError{
	OK,WrongData,SendError,CommandNotRecognized
}

/**
 * This class stores the header data
 */
class HeaderInfo
{
	private Command command=Command.None;
	private int dataSize=0;
	private int port=0;
	
	/**
	 * The class constructor
	 * @param command command type
	 * @param port the command port
	 * @param dataSize size of the command data
	 */
	HeaderInfo(Command command,int port, int dataSize){
		this.command=command;
		this.dataSize=dataSize;
		this.port=port;
		
		
	}
	
	/**
	 * Returns the command.
	 * @return the command
	 */
	Command getCommand(){
		return command;
	}
	
	/**
	 * Returns the data size.
	 * @return the data size
	 */
	int getDataSize(){
		return dataSize;
	}
	
	/**
	 * Returns the port.
	 * @return the port
	 */
	int getPort(){
		return port;
	}
	
	/**
	 * Sets the data size.
	 * @param dataSize data size
	 */
	void setDataSize(int dataSize)
	{
		this.dataSize=dataSize;
	}
	
	/**
	 * Sets the command.
	 * @param command command type.
	 */
	void setCommand(Command command)
	{
		this.command=command;
	}
	
	/**
	 * Sets the command port.
	 * @param port command port
	 */
	void setPort(int port){
		this.port=port;
	}
	
	
}