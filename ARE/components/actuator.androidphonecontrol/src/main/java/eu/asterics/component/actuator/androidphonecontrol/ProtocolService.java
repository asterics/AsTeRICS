

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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.actuator.androidphonecontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import eu.asterics.mw.services.AstericsErrorHandling;



/**
 * 
 * This calls implements the communication protocol for connection between AsTeRICS and the phone.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class ProtocolService {
	private final byte firstByte = 0x40;
	private final byte secondByte=0x54;
	private final int headerSize=6;
	
	public enum Command{
		None, Call,Accept, Drop, SendSMS, Start, Result, NewSMS, CallState
	}
	
	private Socket socket=null;
	DataInputStream inputStream=null;
	DataOutputStream outputStream=null;
	private boolean ready=true;
	private int offset=0;
	private byte[] offsetBytes=new byte[headerSize];
	
	/**
	 * The class constructor.
	 * @param socket the socket for connection
	 */
	public ProtocolService(Socket socket){
		ready=true;
		this.socket=socket;
		try{
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
			ready=false;
		}
	}
	
	/**
	 * Checks if the packet header is available.
	 * @return true if header is available
	 */
	public boolean checkHeaderAvailable(){
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
	 * Checks if the command data is available.
	 * @return true if data is available
	 */
	private boolean checkCommandData(Command command){
		switch(command){
		case Call:
			return true;
		case Accept:
		case Drop:
			return false;
		case SendSMS:
			return true;
		case Start:
			return false;
		case Result:
		case NewSMS: 
		case CallState:
			return true;
		}
		
		return false;
	}
	
	/**
	 * Decodes command from the header data.
	 * @param byte1 the first byte of data
	 * @param byte2 the second byte of data
	 * @return decoded command
	 */
	private Command decodeCommand(byte byte1, byte byte2){
		if((byte1==0x43)&&(byte2==0x6c)){
			return Command.Call;
		}
		
		if((byte1==0x41)&&(byte2==0x63)){
			return Command.Accept;
		}
		
		if((byte1==0x44)&&(byte2==0x43)){
			return Command.Drop;
		}
		
		if((byte1==0x53)&&(byte2==0x4d)){
			return Command.SendSMS;
		}
		
		if((byte1==0x53)&&(byte2==0x74)){
			return Command.Start;
		}
		
		if((byte1==0x52)&&(byte2==0x74)){
			return Command.Result;
		}
		
		if((byte1==0x4e)&&(byte2==0x4d)){
			return Command.NewSMS;
		}
		
		if((byte1==0x43)&&(byte2==0x53)){
			return Command.CallState;
		}
		
		return Command.None;
		
			
	}
	
	/**
	 * Decodes phone ID from command data.
	 * @param data data contains phone ID
	 * @return phone ID
	 */
	public String decodeCallNumber (byte[] data)
	{
		//String -> bytes: string.getBytes("UTF-16BE")
		//bytes -> String: new String(bytes, 0, len, "UTF-16BE")
		try
		{
			return new String(data,0,data.length,"UTF-16BE");
		}
		catch(NullPointerException e)
		{
			AstericsErrorHandling.instance.getLogger().warning("NullPointerException: "+e.getMessage());
			return null;
		}
		catch(IndexOutOfBoundsException e1)
		{
			AstericsErrorHandling.instance.getLogger().warning("IndexOutOfBoundsException: "+e1.getMessage());
			return null;
		}
		catch(UnsupportedEncodingException e2)
		{
			AstericsErrorHandling.instance.getLogger().warning("UnsupportedEncodingException: "+e2.getMessage());
			return null;
		}
	}
	
	/**
	 * Decodes SMS receiver from command data.
	 * @param data data contains SMS receiver
	 * @return SMS receiver
	 */
	public String decodeSMSNumber(byte[] data)
	{
		int offset = data[0];
		
		if(offset+1>=data.length)
		{
			AstericsErrorHandling.instance.getLogger().warning("package error");
			return null;
		}
		
		try
		{
			return new String(data,1,offset,"UTF-16BE");
		}
		catch(NullPointerException e)
		{
			AstericsErrorHandling.instance.getLogger().warning("NullPointerException: "+e.getMessage());
			return null;
		}
		catch(IndexOutOfBoundsException e1)
		{
			AstericsErrorHandling.instance.getLogger().warning("IndexOutOfBoundsException: "+e1.getMessage());
			return null;
		}
		catch(UnsupportedEncodingException e2)
		{
			AstericsErrorHandling.instance.getLogger().warning("UnsupportedEncodingException: "+e2.getMessage());
			return null;
		}
	}
	
	/**
	 * Decodes SMS content from command data.
	 * @param data data contains SMS content
	 * @return SMS content
	 */
	public String decodeSMS(byte[] data)
	{
		int offset = data[0];
		
		if(offset+1>=data.length)
		{
			AstericsErrorHandling.instance.getLogger().warning("package error");
			return null;
		}
		
		try
		{
			return new String(data,offset+1,data.length-offset-1,"UTF-16BE");
		}
		catch(NullPointerException e)
		{
			AstericsErrorHandling.instance.getLogger().warning("NullPointerException: "+e.getMessage());
			return null;
		}
		catch(IndexOutOfBoundsException e1)
		{
			AstericsErrorHandling.instance.getLogger().warning("ndexOutOfBoundsException: "+e1.getMessage());
			return null;
		}
		catch(UnsupportedEncodingException e2)
		{
			AstericsErrorHandling.instance.getLogger().warning("UnsupportedEncodingException: "+e2.getMessage());
			return null;
		}
	}
	
	/**
	 * Decodes phone state from command data.
	 * @param data data contains phone state
	 * @return phone state
	 */
	public byte decodeState(byte [] data)
	{
		return data[0];
	}
	
	/**
	 * Decodes phone ID from phone state command data
	 * @param data data contains phone ID
	 * @return phone ID
	 */
	public String decodeStatePhone(byte[] data)
	{
		if(data.length<1)
		{
			return null; 
		}
		
		try
		{
			return new String(data,1,data.length-1,"UTF-16BE");
		}
		catch(NullPointerException e)
		{
			AstericsErrorHandling.instance.getLogger().warning("NullPointerException: "+e.getMessage());
			return null;
		}
		catch(IndexOutOfBoundsException e1)
		{
			AstericsErrorHandling.instance.getLogger().warning("IndexOutOfBoundsException: "+e1.getMessage());
			return null;
		}
		catch(UnsupportedEncodingException e2)
		{
			AstericsErrorHandling.instance.getLogger().warning("UnsupportedEncodingException: "+e2.getMessage());
			return null;
		}
	}
	
	/**
	 * Decodes command result command data.
	 * @param data data contains command result
	 * @return command result
	 */
	public short decodeResult(byte[] data)
	{
		try {
			return inputStream.readShort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			AstericsErrorHandling.instance.getLogger().warning("ResultDecode error ");
			return 0;
		}
	}
	
	/**
	 * Prepares header of the command packet
	 * @param command packet command
	 * @return header in the byte array
	 */
	private byte[]  prepareHeader(Command command)
	{
		byte [] header=new byte[4];
		
		header[0]=firstByte;
		header[1]=secondByte;
		
		switch(command)
		{
		case Call:
			header[2]=0x43;
			header[3]=0x6c;
			break;
		case Accept:
			header[2]=0x41;
			header[3]=0x63;
			break;
		case Drop:
			header[2]=0x44;
			header[3]=0x43;
			break;
		case SendSMS:
			header[2]=0x53;
			header[3]=0x4d;
			break;
		case Start:
			header[2]=0x53;
			header[3]=0x74;
			break;
		case Result:
			header[2]=0x52;
			header[3]=0x74;
			break;
		case NewSMS:
			header[2]=0x4e;
			header[3]=0x4d;
			break;
		case CallState:
			header[2]=0x43;
			header[3]=0x53;
			break;
		default:
			return null;
		}
		
		return header;
	}
	
	/**
	 * Sends the call package.
	 * @param phoneNumber phone number
	 * @return operation result
	 */
	public boolean sendCallPackage(String phoneNumber)
	{
		byte header[] = prepareHeader(Command.Call);
		byte  phone[]=null;
		try {
			phone = phoneNumber.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			return false;
		}
		
		try {
			outputStream.write(header);
			short phoneSize=(short)phone.length;
			outputStream.writeShort(phoneSize);
			outputStream.write(phone);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the accept package.
	 * @return operation result
	 */
	public boolean sendAcceptPackage()
	{
		byte header[] = prepareHeader(Command.Accept);
		
		try {
			outputStream.write(header);
			short DataSize=0;
			outputStream.writeShort(DataSize);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the drop package.
	 * @return operation result
	 */
	public boolean sendDropPackage()
	{
		byte header[] = prepareHeader(Command.Drop);
		
		try {
			outputStream.write(header);
			short DataSize=0;
			outputStream.writeShort(DataSize);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the send SMS package.
	 * @param phoneNumber receiver phone number
	 * @param messageContent SMS content
	 * @return operation result
	 */
	public boolean sendSendSMSPackage(String phoneNumber, String messageContent)
	{
		byte header[] = prepareHeader(Command.SendSMS);
		
		byte  phone[]=null;
		byte message[]=null;
		try {
			phone = phoneNumber.getBytes("UTF-16BE");
			message=messageContent.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			return false;
		}
		
		try {
			outputStream.write(header);
			short DataSize=(short)(phone.length+message.length+1);
			outputStream.writeShort(DataSize);
			byte[]phoneSize= new byte[1];
			phoneSize[0]=(byte)phone.length;
			outputStream.write(phoneSize);
			outputStream.write(phone);
			outputStream.write(message);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the start package.
	 * @return operation result
	 */
	public boolean sendStartPackage()
	{
		byte header[] = prepareHeader(Command.Start);
		
		try {
			outputStream.write(header);
			short DataSize=0;
			outputStream.writeShort(DataSize);
			outputStream.flush();
			
		} catch (IOException e) {
		
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the result package.
	 * @param result result
	 * @return operation result
	 */
	public boolean sendResultPackage(int result)
	{
		byte header[] = prepareHeader(Command.Start);
		
		try {
			outputStream.write(header);
			short DataSize=2;
			outputStream.writeShort(DataSize);
			short resultData = (short)result;
			outputStream.writeShort(resultData);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the new SMS ready package.
	 * @param phoneNumber sender phone number
	 * @param messageContent SMS content
	 * @return operation result
	 */
	public boolean sendNewSMSPackage(String phoneNumber, String messageContent)
	{
		byte header[] = prepareHeader(Command.NewSMS);
		
		byte  phone[]=null;
		byte message[]=null;
		try {
			phone = phoneNumber.getBytes("UTF-16BE");
			message=messageContent.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			return false;
		}
		
		try {
			outputStream.write(header);
			short DataSize=(short)(phone.length+message.length+1);
			outputStream.writeShort(DataSize);
			byte[]phoneSize= new byte[1];
			phoneSize[0]=(byte)phone.length;
			outputStream.write(phoneSize);
			outputStream.write(phone);
			outputStream.write(message);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	/**
	 * Sends the phone state package.
	 * @param state state of the phone
	 * @param phoneNumber number of the remote phone
	 * @return operation result
	 */
	public boolean sendCallStatePackage(byte state, String phoneNumber)
	{
		byte header[] = prepareHeader(Command.CallState);
		byte  phone[]=null;
		try {
			phone = phoneNumber.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			return false;
		}
		
		try {
			outputStream.write(header);
			short dataSize=(short)(phone.length+1);
			outputStream.writeShort(dataSize);
			byte[] callState=new byte[1];
			callState[0]=state;
			outputStream.write(callState);
			outputStream.write(phone);
			outputStream.flush();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		
		return true;
		
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
	 * This class contains the header data
	 */
	public class HeaderData
	{
		private Command command=Command.None;
		private int size=0;
		public HeaderData(Command command,int size)
		{
			this.command=command;
			this.size=size;
		}
		
		public int getSize()
		{
			return size;
		}
		
		public Command getCommand()
		{
			return command;
		}
		
	};
	
	/**
	 * Gets and decode the header.
	 */
	public HeaderData decodeHeader()
	{
		Command command;
		int headerBegin=4;  //local;
		try
		{
			byte [] header= new byte[headerBegin];
			
			for(int i=0;i<offset;i++)
			{
				header[i]=offsetBytes[i];
			}
			
			int readBytes = inputStream.read(header, offset, headerBegin-offset);
			
			if(readBytes< headerBegin-offset)
			{
				offset = readBytes+offset;
				for(int i=0;i<offset;i++)
				{
					offsetBytes[i]=header[i];
				}
				command=Command.None;
				return new HeaderData(command,0);
			}
			
			
			offset=0;
			
			if((header[0]==firstByte)&&(header[1]==secondByte))
			{
				command = decodeCommand(header[2],header[3]);
				
				short dataSize=inputStream.readShort();
				
				if(command==Command.None)
				{
					//TODO 
				}
				
				if(checkCommandData(command)==false && dataSize>0)
				{
					//TODO
				}
				
				
				return new HeaderData(command,dataSize);
			}
			else
			{
				boolean found=false;
				for(int i=1;i<headerBegin-1;i++)
				{
					if((header[i]==firstByte)&&(header[i+1]==secondByte))
					{
						offset=headerBegin-i;
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
					if(header[headerBegin-1]==firstByte)
					{
						offset=1;
						offsetBytes[0]=firstByte;
					}
				}
				
				command=Command.None;
				return new HeaderData(command,0);
			}
			
			
		}
		catch(IOException e){
			AstericsErrorHandling.instance.getLogger().warning("Stream error: "+e.getMessage());
			command=Command.None;
			return new HeaderData(command,-1);
		}
	}
	
	
}