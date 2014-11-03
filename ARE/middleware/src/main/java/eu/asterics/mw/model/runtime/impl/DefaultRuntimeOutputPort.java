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
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;

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
	private final Map<EndPoint, String> portIdToConversion = 
			new LinkedHashMap<EndPoint,String>();

	public void sendData(final byte[] data)
	{
		//TODO: check if this should not be within the run method!!
		//Because the for loop is executed in another thread and the purpose of the synchronized statement is
		//to ensure that the iteration does not fail due to concurrent manipulation of the map. 
		synchronized (inputPortEndpoints)
		{

			AstericsModelExecutionThreadPool.instance.execute(new Runnable()
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
						//block data propagation if model and component is not up and running
						//Disabled for now, because has some strange side effects with camera			
						/*
						if(!DeploymentManager.instance.isComponentRunning(endPoint.targetComponentID)) {
							//logger.warning("Data could not be propagated, target component not running: targetComponentId: "+endPoint.targetComponentID);
							System.out.print("D");
							continue;							
						}
						*/
						
						String conversion = DefaultRuntimeOutputPort.this.portIdToConversion.get(endPoint);
						//logger.fine("targetCompId: "+endPoint.targetComponentID+", conversion: "+conversion+", data.length: "+data.length);
						//logger.fine("portIdToConversion: "+portIdToConversion.entrySet().toString());
						byte[] newData = data;
						if (conversion != null && !"".equals(conversion)) {
							newData = ConversionUtils.convertData(data, conversion);
						}
						
						if(newData==null) {
							logger.warning("Data for input port is null --> skip it, port: "+endPoint);
							continue;
						}
						if (runtimeInputPort.isBuffered()) {
								DeploymentManager.instance.bufferData(newData, endPoint.portID, endPoint.targetComponentID);
						}
						else {							
							//synchronize using the target component, because the component can be considered a black box, that must
							//ensure data integrity. The data propagation of (output to input ports) is also synchronized on the component object.							
							synchronized(targetComponent) {
								runtimeInputPort.receiveData(newData);
							}
						}
					}


				}
			});

		}
	}

	public void addInputPortEndpoint(final String targetComponentID, 
			final String portID,
			final IRuntimeInputPort inputPort,
			String conversion)
	{
		synchronized (inputPortEndpoints)
		{
			EndPoint ep=new EndPoint(targetComponentID, portID);
			inputPortEndpoints.put(ep, inputPort);
			if (conversion.compareTo("")!=0) {
				this.portIdToConversion.put(ep, conversion);
			}
		}
	}

	public void removeInputPortEndpoint(final String targetComponentID, final String portID)
	{
		synchronized (inputPortEndpoints)
		{
			inputPortEndpoints.remove(new EndPoint(targetComponentID, portID));
		}
	}


	class EndPoint  
	{
		private String uniqueId;

		private String targetComponentID ="";
		private String portID="";
		EndPoint (final String targetComponentID, final String portID)
		{
			this.targetComponentID=targetComponentID;
			this.portID=portID;
			this.uniqueId=getUniqueID(targetComponentID, portID);
		}
		
		public String getUniqueID(final String targetComponentID, final String portID)
		{
			return targetComponentID + ":" + portID;
		}		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((uniqueId == null) ? 0 : uniqueId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			EndPoint other = (EndPoint) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (uniqueId == null) {
				if (other.uniqueId != null)
					return false;
			} else if (!uniqueId.equals(other.uniqueId))
				return false;
			return true;
		}
				
		@Override
		public String toString() {
			return getUniqueID(targetComponentID, portID);
		}

		private DefaultRuntimeOutputPort getOuterType() {
			return DefaultRuntimeOutputPort.this;
		}		
	}
}