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
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */


package eu.asterics.component.actuator.androidphonecontrol;

/**
 * 
 * This class keeps commands for the command queue.
 *  
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Jun 04, 2012
 *         Time: 13:24:48 AM
 */
public class CommandOrder
{
		/*
		public CommandOrder(ProtocolService.Command command)
		{
			this.command=command;
		}
		public CommandOrder(ProtocolService.Command command,String phoneID)
		{
			this.command=command;
			this.phoneID=phoneID;
		}*/
		
	   
  	/**
	 * The class constructor.
	 * @param command command type.
	 * @param phoneID the number of the remote phone
	 * @param message SMS message.
	 */
		public CommandOrder(ProtocolService.Command command,String phoneID,String message)
		{
			this.command=command;
			this.phoneID=phoneID;
			this.message=message;
		}
		
		/**
		 * Returns command type.
		 * @return command type
		 */
		public ProtocolService.Command getCommand()
		{
			return command;
		}
		
		/**
		 * Returns phone ID.
		 * @return phone ID
		 */
		public String getPhoneID()
		{
			return phoneID;
		}
		
		/**
		 * Returns message content.
		 * @return message content.
		 */
		public String getMessage()
		{
			return message;
		}
		
		private ProtocolService.Command command=ProtocolService.Command.None;
		private String phoneID="";
		private String message="";
}