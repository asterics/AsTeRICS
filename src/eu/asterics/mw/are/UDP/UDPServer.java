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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */
package eu.asterics.mw.are.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class implements the UDPServer. The UDPServer is listening to an
 * UDP broadcast. If a broadcast with the right message will be received, the 
 * Server sends its name and IP-address back to the sender.
 * 
 * @author Roland Ossmann [ro@ki-i.at]
 *         Date: Sept 15, 2011
 *         Time: 11:08:01 AM
 *
 */
public class UDPServer {
	
	private static Logger logger = null;
	
	public UDPServer ()
	{
		logger = AstericsErrorHandling.instance.getLogger();
		
	}

	/**
	 * @param args
	 */
	public void start() {
		DatagramSocket udpServer = null;
		int listenerPort = 9091; // UPD Listener port
		int callbackPort = 9092; // UDP Message return port
		byte[] receiveUdp = new byte[32]; // Size of incoming datagrmm, formerly 256
		byte[] sendUdp = new byte[128];  //Size of outgoing datagramm, formerly 256

		try {
			udpServer = new DatagramSocket(listenerPort);
			logger.fine("UDPServer is ready to receive data...");
			
			while (true) {
				DatagramPacket receivedPacket = new DatagramPacket(receiveUdp,
						receiveUdp.length);
				udpServer.receive(receivedPacket);

				String receivedStr = new String(receivedPacket.getData());
				logger.fine("Received UPD data:" + receivedStr);
				
				// If the right string being received, the thread waits to give the sender enough time
				// to close the sending port socket and open the receiving port socket.
				if (receivedStr.contains("AsTeRICS Broadcast")) {
					Thread.sleep(200);
					InetAddress IPSender = receivedPacket.getAddress();

					String hostname = InetAddress.getLocalHost().getHostName();
					String ip = InetAddress.getLocalHost().getHostAddress();

					// sending the hostname and the host IP
					sendUdp = ("AsTeRICS Broadcast Ret, Hostname:" + hostname
							+ " IP:" + ip).getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendUdp,
							sendUdp.length, IPSender, callbackPort);
					udpServer.send(sendPacket);
					logger.fine("Sended UPD data to ACS:" + new String(sendUdp));
				}
			}
		} catch (SocketException e) {
			logger.warning(this.getClass().getName()+"." +
					"SocketException in UDPServer-> \n"+e.getMessage());			
		} catch (UnknownHostException e) {
			logger.warning(this.getClass().getName()+"." +
					"UnknownHostException in UDPServer-> \n"+e.getMessage());
		} catch (IOException e) {
			logger.warning(this.getClass().getName()+"." +
					"IOException in UDPServer-> \n"+e.getMessage());
		} catch (InterruptedException e) {
			logger.warning(this.getClass().getName()+"." +
					"InterruptedException in UDPServer-> \n"+e.getMessage());
		} finally {
			udpServer.close();
		}
	      

	}

}

