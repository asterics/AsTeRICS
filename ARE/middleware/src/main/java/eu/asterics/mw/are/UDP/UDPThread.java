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

import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * This class starts the UDPServer in an separate thread. This thread has to be
 * started at the ARE startup
 * 
 * @author Roland Ossmann [ro@ki-i.at]
 *         Date: Sept 15, 2011
 *         Time: 11:08:01 AM
 *
 */

public class UDPThread implements Runnable{

	UDPServer udpServer=null;
	private Logger logger = null;
	public UDPThread (){
		try {
		  logger = AstericsErrorHandling.instance.getLogger();
	      udpServer = new UDPServer();
	     
		} catch (Exception e) {
			logger.warning(this.getClass().getName()+"." +
					"UDPThread: -> \n"+e.getMessage());
		}

	}

	public void run() {
		      udpServer.start();
	}


}