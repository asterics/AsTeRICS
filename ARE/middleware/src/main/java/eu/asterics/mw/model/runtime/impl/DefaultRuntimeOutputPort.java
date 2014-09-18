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

package eu.asterics.mw.model.runtime.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsSendingThreadPool;
import eu.asterics.mw.services.AstericsThreadPool;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 3:20:36 PM
 */
public class DefaultRuntimeOutputPort implements IRuntimeOutputPort
{
	private static final Logger logger=AstericsErrorHandling.instance.getLogger();
	//Sync
	private final Map<EndPoint, IRuntimeInputPort> inputPortEndpoints = 
			new LinkedHashMap<EndPoint,IRuntimeInputPort>();
	private final Map<String, String> portIdToConversion = 
			new LinkedHashMap<String,String>();

	public void sendData(final byte[] data)
	{
		synchronized (inputPortEndpoints)
		{

			AstericsSendingThreadPool.instance.execute(new Runnable()
			{
				public void run()
				{
					for (Entry<EndPoint, IRuntimeInputPort> entry : inputPortEndpoints.entrySet()) {
						final IRuntimeInputPort runtimeInputPort = entry.getValue();
						final EndPoint endPoint = entry.getKey();
						
						IComponentInstance targetComponent=DeploymentManager.instance.getCurrentRuntimeModel().getComponentInstance(endPoint.targetComponentID);
						if(targetComponent == null) {
							logger.warning("Data could not be propagated, target component not found: targetComponentId: "+endPoint.targetComponentID);
							continue;
						}
						
						if (runtimeInputPort.isBuffered()) {
							
							String conversion = DefaultRuntimeOutputPort.this.portIdToConversion.get(endPoint.portID);
							byte[] newData = null;
							if (conversion != null) {
								newData = DefaultRuntimeOutputPort.this.convertData(data, conversion);
							}
															
							if (newData!=null)
								DeploymentManager.instance.bufferData(newData, endPoint.portID, endPoint.targetComponentID);
							else
								DeploymentManager.instance.bufferData(data, endPoint.portID, endPoint.targetComponentID);													
						}
						else {							
							//System.out.println("PORT "+runtimeInputPort+" is NOT BUFFERRED"+runtimeInputPort);

							//synchronize using the target component, because the component can be considered a black box, that must
							//ensure data integrity. The data propagation of (output to input ports) is also synchronized on the component object.							
							synchronized(targetComponent) {
								runtimeInputPort.receiveData(data);
							}
						}
					}


				}
			});

		}
	}


	protected byte[] convertData(byte[] data, String conversion) {
		switch (conversion) {
		case "integerToDouble":
			return ConversionUtils.doubleToBytes(ConversionUtils.intFromBytes(data));
		case "byteToDouble":
			return ConversionUtils.doubleToBytes(data[0]);
		case "byteToInteger":
			return ConversionUtils.intToBytes( data[0]);
		case "charToDouble":
			return ConversionUtils.doubleToBytes(new String(data).charAt(0));
		case "charToInteger":
			return ConversionUtils.intToBytes(new String(data).charAt(0));
		case "doubleToInteger":
			return ConversionUtils.intToBytes((int)ConversionUtils.doubleFromBytes(data));
		case "integerToLong":
			return ConversionUtils.longToBytes(ConversionUtils.intFromBytes(data));
		default:
			break;
		}
		return null;
	}


	public void addInputPortEndpoint(final String targetComponentID, 
			final String portID,
			final IRuntimeInputPort inputPort,
			String conversion)
	{
		synchronized (inputPortEndpoints)
		{
			inputPortEndpoints.put(new EndPoint(targetComponentID, portID), inputPort);
			if (conversion.compareTo("")!=0) {
				this.portIdToConversion.put(portID, conversion);
			}
		}
	}

	public void removeInputPortEndpoint(final String targetComponentID, final String portID)
	{
		synchronized (inputPortEndpoints)
		{
			inputPortEndpoints.remove(getUniqueID(targetComponentID, portID));
		}
	}

	private String getUniqueID(final String targetComponentID, final String portID)
	{
		return targetComponentID + ":" + portID;
	}

	class EndPoint  
	{
		String targetComponentID ="";
		String portID="";
		EndPoint (final String targetComponentID, final String portID)
		{
			this.targetComponentID=targetComponentID;
			this.portID=portID;
		}

	}

	

}