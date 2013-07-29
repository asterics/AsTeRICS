package eu.asterics.mw.tcp;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
public class smtpClient {
    public static void main(String[] args) {

        Socket smtpSocket = null;  
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            smtpSocket = new Socket("localhost", 4546);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new DataInputStream(smtpSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:" +
            		" hostname");
        }
    if (smtpSocket != null && is != null) {
            try {
     
            	byte[] buffer = new byte [256];
            	byte[] emptyBuffer = "#*idle*#".getBytes();
            	byte[] nonEmptyBuffer = "dummy data".getBytes();
            	
                while (is.read(buffer)!=-1) {
                
                	System.out.println("Remote client received: "+ByteBuffer.
                			wrap(buffer).getInt());
					if(ByteBuffer.wrap(buffer).getInt()==-1)
					{	
						os.write(nonEmptyBuffer);
					}
					else
					{	
						os.write(emptyBuffer);
					}
						
                 }
               
                os.close();
                is.close();
            	smtpSocket.close();   
                
                
        
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }
    
    
}