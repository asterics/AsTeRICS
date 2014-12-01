package eu.asterics.mw.are;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.asterics.mw.are.AREEvent;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.gui.AstericsGUI;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IBindingEdge;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IEventEdge;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.AREGUIElement;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.IAREEventListener;


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


/**
 * The deployment manager is responsible for managing the deployment of models 
 * (deploy/undeploy a model, start/stop a model etc.). It also keeps track of 
 * component instances at runtime.
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 12:59:28 PM
 */
public class DeploymentManager
{
	private Logger logger = null;
	private ComponentRepository componentRepository = ComponentRepository.instance;
	public static final DeploymentManager instance = new DeploymentManager();
	private AstericsGUI gui=null;

	//TODO REPLACE WIDTH A FUNCTION THAT RETRIEVES X, Y, W, H FROM MODEL
	static int X=10;
	static int Y=15;
	static int W = 580;
	static int H = 700;
	static int OFFSET = 5;

	private DeploymentManager()
	{
		super();
		logger = AstericsErrorHandling.instance.getLogger();
	}

	private AREStatus areStatus;

	public void setStatus (AREStatus status)
	{
		areStatus = status; 
		gui.setStatus(status);
	}

	public AREStatus getStatus()
	{
		return areStatus;
	}




	private IRuntimeModel currentRuntimeModel = null;

	void start(final BundleContext bundleContext)
	{
		// this.bundleContext = bundleContext;
		// todo load default deployment descriptor from file into
		// currentRuntimeModel
	}

	void stop()
	{
		// todo save currentRuntimeModel deployment descriptor to file
	}

	private Map<String, IRuntimeComponentInstance> runtimeComponentInstances
	= new LinkedHashMap<String, IRuntimeComponentInstance>();
	//stores started components, is used to find started components 
	private Map<String,AREStatus> runtimeComponentInstancesStatus = new HashMap<String, AREStatus>(); 
	
	private Map<String, Set<IRuntimeComponentInstance>> 
	componentTypeIdToRuntimeComponentInstances
	= new LinkedHashMap<String, Set<IRuntimeComponentInstance>>();
	private BundleManager bundleManager;
	private Map<IRuntimeComponentInstance,String > runtimeInstanceToComponentTypeID = new LinkedHashMap<IRuntimeComponentInstance, String>();
	private volatile boolean modelStartupFinished=false;
	private volatile boolean modelLifecycleTaskPending=false;
	
	private Map<String, Stack<LinkedHashMap<String, byte[]>>> bufferedPortsMap
	= new LinkedHashMap<String, Stack<LinkedHashMap<String,byte[]>>>();

	private void init()
	{
		// unget components
		runtimeComponentInstances.clear();
		componentTypeIdToRuntimeComponentInstances.clear();
		runtimeComponentInstancesStatus.clear();		
		modelStartupFinished=false;
		modelLifecycleTaskPending=false;

		// todo unget components
	}


	/**
	 * After the deployment model has been retrieved from the ACS and 
	 * successfully parsed a IRuntimeModel object is created and passed to this 
	 * method for deploying the model.
	 * @param runtimeModel the object generated after parsing a deployment model
	 * @throws DeploymentException
	 */
	public void deployModel(final IRuntimeModel runtimeModel)
			throws DeploymentException
			{
		
		modelStartupFinished=false;
		if (runtimeModel == null)
		{
			logger.severe(this.getClass().getName()+
					": install-> Illegal null argument");
			throw new RuntimeException("Illegal null argument");
		}

		notifyAREEventListeners (AREEvent.PRE_DEPLOY_EVENT);


		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();

		// handle instantiation of new component instances and assign property
		// values as needed

		String conversion="";
		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			final String componentTypeID =
					componentInstance.getComponentTypeID();


			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				logger.severe("Could not find component " +
						"type: " + componentTypeID);
				throw new DeploymentException("Could not find component " +
						"type: " + componentTypeID);
			}

			final String canonicalName = componentType.getCanonicalName();

			IRuntimeComponentInstance runtimeComponentInstance=null;

			try{
				runtimeComponentInstance =
						componentRepository.getInstance(canonicalName);

			}
			catch (NullPointerException e){
				//logger.severe(canonicalName+ " prevents the model from starting: "+e.getMessage());
				e.printStackTrace();
				AstericsErrorHandling.instance.reportError(runtimeComponentInstance, "Could not deploy component ["+runtimeComponentInstance+"]: \n"+e.getMessage());
				return;
			}

			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{
					final Object propertyValue = 
							componentInstance.getPropertyValue(propertyName);
					if (propertyName != null)
					{
						synchronized(runtimeComponentInstance) {
							runtimeComponentInstance.
							setRuntimePropertyValue(propertyName, propertyValue);
						}
					}
				}
			}

			runtimeComponentInstances.put(componentInstance.getInstanceID(),
					runtimeComponentInstance);
			runtimeInstanceToComponentTypeID.put(runtimeComponentInstance, 
					componentInstance.getComponentTypeID());

			Set<IRuntimeComponentInstance> set =
					componentTypeIdToRuntimeComponentInstances.
					get(componentInstance.getComponentTypeID());
			if (set == null)
			{
				set = new LinkedHashSet<IRuntimeComponentInstance>();
			}
			set.add(runtimeComponentInstance);

			componentTypeIdToRuntimeComponentInstances.
			put(componentInstance.getComponentTypeID(), set);
			
			//set component status
			runtimeComponentInstancesStatus.put(componentInstance.getInstanceID(), AREStatus.DEPLOYED);
			
		}

		// handle channel formation
		final Set<IChannel> channels = runtimeModel.getChannels();

		for (final IChannel channel : channels)
		{
			final IBindingEdge sourceBindingEdge = channel.getSource();
			final IBindingEdge targetBindingEdge = channel.getTarget();

			final String sourceComponentInstanceID = sourceBindingEdge.
					getComponentInstanceID();
			final String sourceOutputPortID = sourceBindingEdge.getPortID();

			final String targetComponentInstanceID = targetBindingEdge.
					getComponentInstanceID();
			final String targetInputPortID = targetBindingEdge.getPortID();

			

			final IRuntimeComponentInstance sourceComponentInstance
			= runtimeComponentInstances.get(sourceComponentInstanceID);

			final IRuntimeOutputPort sourceRuntimeOutputPort
			= sourceComponentInstance.getOutputPort(sourceOutputPortID);

			final IRuntimeComponentInstance targetComponentInstance
			= runtimeComponentInstances.get(targetComponentInstanceID);

			final IRuntimeInputPort targetRuntimeInputPort
			= targetComponentInstance.getInputPort(targetInputPortID);

			// form the binding
			//The targetRuntimeInputPort is the same for averager and targetComponentInstance are the same

			// 1. find out the data types of the source and target ports
			// 2. if not the same and compatible, then
			// -- select appropriate wrapper and use that instead
			final IRuntimeInputPort wrapper;

			final DataType sourceDataType = runtimeModel.
					getPortDataType(sourceComponentInstanceID, sourceOutputPortID);
			final DataType targetDataType = runtimeModel.
					getPortDataType(targetComponentInstanceID, targetInputPortID);
			
			conversion = "";			
			if(sourceDataType != targetDataType)
			{
				conversion=ConversionUtils.getDataTypeConversionString(sourceDataType, targetDataType);
			}


			wrapper = targetRuntimeInputPort;

			sourceRuntimeOutputPort.addInputPortEndpoint(
					targetComponentInstanceID, 
					targetInputPortID, 
					wrapper,
					conversion);
			
			runtimeModel.getComponentInstance(targetComponentInstanceID).
				setWrapper (targetInputPortID, wrapper);

			//            sourceRuntimeOutputPort.addInputPortEndpoint(targetComponentInstanceID, targetInputPortID, targetRuntimeInputPort);
		}

		// handle event channels
		final Set<IEventChannel> eventChannels = runtimeModel.getEventChannels();
		for (final IEventChannel eventChannel : eventChannels)
		{
			final String eventChannelID = eventChannel.getChannelID();
			final IEventEdge [] eventSources = eventChannel.getSources();
			final IEventEdge [] eventTargets = eventChannel.getTargets();

			final Set<EventListenerDetails> targetEventListenerPorts
			= new LinkedHashSet<EventListenerDetails>();

			for(final IEventEdge targetEventEdge : eventTargets)
			{
				final String targetComponentInstanceID = 
						targetEventEdge.getComponentInstanceID();
				final String targetEventPortID = 
						targetEventEdge.getEventPortID();

				final IRuntimeComponentInstance targetComponentInstance
				= runtimeComponentInstances.
				get(targetComponentInstanceID);

				final IRuntimeEventListenerPort eventListenerPort
				= targetComponentInstance.
				getEventListenerPort(targetEventPortID);
				targetEventListenerPorts.add(
						new EventListenerDetails(targetComponentInstanceID, 
								targetEventPortID, 
								eventListenerPort));
			}

			for(final IEventEdge sourceEventEdge : eventSources)
			{
				final String sourceComponentInstanceID = 
						sourceEventEdge.getComponentInstanceID();
				final String sourceEventPortID = sourceEventEdge.
						getEventPortID();
				final IRuntimeComponentInstance sourceComponentInstance
				= runtimeComponentInstances.
				get(sourceComponentInstanceID);
				final IRuntimeEventTriggererPort eventTriggererPort
				= sourceComponentInstance.
				getEventTriggererPort(sourceEventPortID);

				eventTriggererPort.setEventChannelID(eventChannelID);

				for(final EventListenerDetails eventListenerDetails : 
					targetEventListenerPorts)
				{
					eventTriggererPort.addEventListener(
							eventListenerDetails.componentID, 
							eventListenerDetails.portID,
							eventListenerDetails.runtimeEventListenerPort);
				}
			}
		}

		this.currentRuntimeModel = runtimeModel;

		notifyAREEventListeners (AREEvent.POST_DEPLOY_EVENT);
	}


	/**
	 * This method undeploys the model and clears all component instances at runtime.
	 * 
	 */
	public void undeployModel()
	{		
		modelStartupFinished=false;
		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();

		final Set<IChannel> channels = runtimeModel.getChannels();
		final Set <IEventChannel> eventChannels = 
				runtimeModel.getEventChannels();

		//Disconnect channels
		for (IChannel channel : channels)
		{
			final IBindingEdge sourceBindingEdge = channel.getSource();
			final IBindingEdge targetBindingEdge = channel.getTarget();
			final String sourceComponentInstanceID = sourceBindingEdge.
					getComponentInstanceID();
			final String sourceOutputPortID = sourceBindingEdge.getPortID();
			final String targetComponentInstanceID = targetBindingEdge.
					getComponentInstanceID();
			final String targetInputPortID = targetBindingEdge.getPortID();
			final IRuntimeComponentInstance sourceComponentInstance
			= runtimeComponentInstances.get(sourceComponentInstanceID);

			if (sourceComponentInstance!=null)
			{
				final IRuntimeOutputPort sourceRuntimeOutputPort
				= sourceComponentInstance.getOutputPort(sourceOutputPortID);			

				sourceRuntimeOutputPort.
				removeInputPortEndpoint(targetComponentInstanceID, 
						targetInputPortID);
			}

		}
		for (IEventChannel eventChannel : eventChannels)
		{
			final String eventChannelID = eventChannel.getChannelID();
			final IEventEdge [] eventSources = eventChannel.getSources();
			final IEventEdge [] eventTargets = eventChannel.getTargets();

			final Set<EventListenerDetails> targetEventListenerPorts
			= new LinkedHashSet<EventListenerDetails>();

			for(final IEventEdge targetEventEdge : eventTargets)
			{
				final String targetComponentInstanceID = 
						targetEventEdge.getComponentInstanceID();
				final String targetEventPortID = 
						targetEventEdge.getEventPortID();

				final IRuntimeComponentInstance targetComponentInstance
				= runtimeComponentInstances.
				get(targetComponentInstanceID);

				if (targetComponentInstance!=null)
				{
					final IRuntimeEventListenerPort eventListenerPort
					= targetComponentInstance.
					getEventListenerPort(targetEventPortID);
					targetEventListenerPorts.add(
							new EventListenerDetails(targetComponentInstanceID, 
									targetEventPortID, 
									eventListenerPort));
				}
			}
			//disconnect event channels
			for(final IEventEdge sourceEventEdge : eventSources)
			{
				final String sourceComponentInstanceID = 
						sourceEventEdge.getComponentInstanceID();
				final String sourceEventPortID = sourceEventEdge.
						getEventPortID();

				final IRuntimeComponentInstance sourceComponentInstance
				= runtimeComponentInstances.
				get(sourceComponentInstanceID);
				if (sourceComponentInstance!=null)
				{
					final IRuntimeEventTriggererPort eventTriggererPort
					= sourceComponentInstance.
					getEventTriggererPort(sourceEventPortID);
					eventTriggererPort.setEventChannelID(eventChannelID);


					for(final EventListenerDetails eventListenerDetails : 
						targetEventListenerPorts)
					{
						eventTriggererPort.
						removeEventListener(eventListenerDetails.componentID,
								eventListenerDetails.portID);
					}
				}
			}
		}

		runtimeComponentInstances.values().clear();
		runtimeComponentInstancesStatus.clear();
		System.gc();

	}


	/**
	 * This method removes the specified component from the runtime environment.
	 * @param componentID the component to be removed
	 * @throws BundleException, BundleManagementException
	 */
	public void removeComponent(String componentID) throws BundleException,
	BundleManagementException
	{
		IRuntimeComponentInstance ci = runtimeComponentInstances.get(componentID);
		if (ci == null)
			return;
//Removed synchronized again due to issue #59							
//		synchronized(ci) {
			ci.stop();
//		}

		String cType = getCurrentRuntimeModel().
				getComponentInstance(componentID).
				getComponentTypeID();

		runtimeComponentInstances.remove(ci);
		runtimeComponentInstancesStatus.remove(componentID);
		if (cType == null)
			return;
		Set<IRuntimeComponentInstance> set =
				componentTypeIdToRuntimeComponentInstances.
				get(cType);

		if (set != null)
		{
			set.remove(ci);

			if (set.size() == 0)
			{
				componentTypeIdToRuntimeComponentInstances.remove(set);

				//No instances of this type stop the Bundle possible
				synchronized(ci) {
					BundleManager.
					stopBundleComponent(componentRepository.getComponentType(cType));
				}
			}
		}
	}


	// ---------------------- Model lifecycle support ----------------------- //

	/**
	 * This method runs the deployed model.
	 * 
	 */

	public void runModel()
	{
		try{
			modelLifecycleTaskPending=true;
			notifyAREEventListeners (AREEvent.PRE_START_EVENT);
			for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
			{
				try 
				{
					String compRefName=componentInstance.getClass().getSimpleName();
					logger.fine("Trying to start component instance: "+compRefName);
					String id = runtimeInstanceToComponentTypeID.get(componentInstance);
					
					String s = getComponentInstanceIDFromComponentInstance(componentInstance);
					synchronized (componentInstance) {
						bundleManager.getBundleFromId(id).start();
						componentInstance.start();
						runtimeComponentInstancesStatus.put(s, AREStatus.RUNNING);
					}
	
	
					IRuntimeModel runtimeModel = getCurrentRuntimeModel();
					
					IComponentInstance ci = runtimeModel.getComponentInstance(s);
	
					Set<IInputPort> bufferedPorts = ci.getBufferedInputPorts();
					Iterator<IInputPort> itr = bufferedPorts.iterator();
					while(itr.hasNext())
					{			
						IInputPort port = itr.next();
						IRuntimeInputPort runtimePort = componentInstance.getInputPort(port.getPortType());
						
						boolean isConnected=false;
						Set<IChannel> channels = runtimeModel.getChannels();
						Iterator<IChannel> itc = channels.iterator();
						while (itc.hasNext())
						{
							IChannel channel =  itc.next();
							
							if ((channel.getTargetInputPortID().equals(port.getPortType()))  &&
									(channel.getTargetComponentInstanceID().equals(s)))
							{
								isConnected=true;
							}
						}
						
						if (!isConnected)
							logger.severe("The synchronized port "+ s + " (" +port.getPortType() + ") is not connected to a data source - this blocks the component operation !");
							
						runtimePort.startBuffering((AbstractRuntimeComponentInstance) componentInstance, port.getPortType());
						
						IRuntimeInputPort wrapper = ci.getWrapper(port.getPortType());
						if (wrapper!=null)
							wrapper.startBuffering((AbstractRuntimeComponentInstance) componentInstance, port.getPortType());
					}
					logger.fine("Started component instance: "+compRefName);
					
				}
				catch (Throwable t) 
				{
					//custom title, error icon
					t.printStackTrace();
					AstericsErrorHandling.instance.reportError(componentInstance, "Could not run component ["+componentInstance+"]: \n"+t.getMessage());
				}
			}
		}finally {
			modelStartupFinished=true;
			logger.fine("Setting modelLifecycleTaskPending=false");
			modelLifecycleTaskPending=false;
		}
	}

	/**
	 * This method pauses the model that is currently at runtime.
	 * 
	 */
	public void pauseModel()
	{
		try {
			modelLifecycleTaskPending=true;
			modelStartupFinished=false;
			for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
			{
				String compRefName=componentInstance.getClass().getSimpleName();
				logger.fine("Trying to pause component instance: "+compRefName);
				synchronized(componentInstance) {
					componentInstance.pause();
					String componentInstanceId=getComponentInstanceIDFromComponentInstance(componentInstance);
					runtimeComponentInstancesStatus.put(componentInstanceId, AREStatus.PAUSED);
				}
				logger.fine("Paused component instance: "+compRefName);
			}
		}finally {
			logger.fine("Setting modelLifecycleTaskPending=false");
			modelLifecycleTaskPending=false;			
		}
	}		

	/**
	 * This method resumes the paused model.
	 * 
	 */
	public void resumeModel()
	{
		try{
			modelLifecycleTaskPending=true;
			for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
			{
				String compRefName=componentInstance.getClass().getSimpleName();
				logger.fine("Trying to resume component instance: "+compRefName);
				synchronized(componentInstance) {
					componentInstance.resume();
					String componentInstanceId=getComponentInstanceIDFromComponentInstance(componentInstance);
					runtimeComponentInstancesStatus.put(componentInstanceId, AREStatus.RUNNING);				
				}
				logger.fine("Resumed component instance: "+compRefName);
			}
		}finally{
			modelStartupFinished=true;
			logger.fine("Setting modelLifecycleTaskPending=false");
			modelLifecycleTaskPending=false;
		}
	}

	/**
	 * This method stops the model that is currently at runtime
	 * 
	 */
	public void stopModel()
	{
		try{
			modelLifecycleTaskPending=true;
			modelStartupFinished=false;
			for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
			{
				try 
				{
					String compRefName=componentInstance.getClass().getSimpleName();
					logger.fine("Trying to stop component instance: "+compRefName);
					String id = runtimeInstanceToComponentTypeID.get(componentInstance);
	
//Removed synchronized again due to issue #59					
//					synchronized(componentInstance) {
						bundleManager.getBundleFromId(id).stop();
						componentInstance.stop();
						String componentInstanceId=getComponentInstanceIDFromComponentInstance(componentInstance);
						//There is no state for STOP, so use DEPLOYED? or better OK?
						runtimeComponentInstancesStatus.put(componentInstanceId, AREStatus.DEPLOYED);
//					}
					
					logger.fine("Stopped component instance: "+compRefName);
				}
				catch (Throwable t) 
				{
					//custom title, error icon
					t.printStackTrace();
					AstericsErrorHandling.instance.reportError(componentInstance, "Could not stop component ["+componentInstance+"]: \n"+t.getMessage());
				}
	
	
			}
			notifyAREEventListeners (AREEvent.POST_STOP_EVENT);
		}finally{
			logger.fine("Setting modelLifecycleTaskPending=false");
			modelLifecycleTaskPending=false;
		}
		System.gc();
	}

	// ------------------- End of model lifecycle support ------------------- //
	
	/**
	 * Resets the flags modelLifecycleTaskPending and others to a clean state.
	 * This should be used after e.g. an error occurred by the caller of a lifecycle task (e.g. AREServices.runModel) after an execution timeout
	 */
	public void reseToCleanState() {
		logger.fine("Setting modelLifecycleTaskPending=false");
		modelStartupFinished=false;
		modelLifecycleTaskPending=false;
	}
	
	/**
	 * Checks if the DeploymentManager is currently performing a lifecycle task (start, pause, resume, stop) of a model 
	 * @return
	 */
	public boolean isModelLifecycleTaskPending() {
		return modelLifecycleTaskPending;
	}
	
	/**
	 * Checks if the DeploymentManager has finished starting a model and the model is up and running.
	 * @return
	 */
	public boolean isModelStartupFinished() {
		return modelStartupFinished;
	}
	
	/**
	 * Checks if a model is running and the component with the given Id is started.
	 * @NOTE: currently disabled
	 * @param componentInstanceId
	 * @return true: Returns true if the component is running and ready
	 */
	public boolean isComponentRunning(String componentInstanceId) {		
		if(!modelLifecycleTaskPending && modelStartupFinished) {			
			AREStatus compStatus=runtimeComponentInstancesStatus.get(componentInstanceId);
			if(compStatus!=null) {
				//logger.fine("Component <"+componentInstanceId+">, state: "+compStatus);
				if(compStatus.equals(AREStatus.RUNNING)) {
					return true;
				}
			}
		}
		//logger.fine("Component <"+componentInstanceId+"> not running");
		return false;		 
	}

	/**
	 * This method returns the model that is currently at runtime
	 * 
	 */
	public IRuntimeModel getCurrentRuntimeModel()
	{
		return currentRuntimeModel;
	}

	private class EventListenerDetails
	{
		private final String componentID;
		private final String portID;
		private final IRuntimeEventListenerPort runtimeEventListenerPort;

		private EventListenerDetails(final String componentID, final String portID,
				final IRuntimeEventListenerPort runtimeEventListenerPort)
		{
			this.componentID = componentID;
			this.portID = portID;
			this.runtimeEventListenerPort = runtimeEventListenerPort;
		}

		@Override
		public String toString()
		{
			return "EventListenerDetails(" + componentID + ":" + portID + "/" + runtimeEventListenerPort + ")";
		}
	}


	/**
	 * This method returns the value of the property specified by the key in the component 
	 * specified by the componentID.
	 * @param componentID the component whose property is to be returned
	 * @param key the key of the property whose value is to be returned
	 */
	public String getComponentProperty(String componentID, String key) {

		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();
		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();

		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			if (!componentInstance.getInstanceID().equals(componentID))
				continue;

			final String componentTypeID =componentInstance.getComponentTypeID();

			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				return null;
			}
			//	final String canonicalName = componentType.getCanonicalName();
			final IRuntimeComponentInstance runtimeComponentInstance =
					runtimeComponentInstances.get(componentID);
			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{


					if (propertyName.equals(key))
					{
						Object value = runtimeComponentInstance.
								getRuntimePropertyValue(key);

						return String.valueOf(value);

					}
				}
			}
		}
		return null;

	}


	/**
	 * This method sets the value of the property specified by the key in the component 
	 * specified by the componentID as specified by the value.
	 * @param componentID the component whose property is to be set
	 * @param key the key of the property whose value is to be set
	 * @param value the new value
	 */
	public void setComponentProperty(String componentID, String key,
			String value) {

		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();
		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();
		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			if (!componentInstance.getInstanceID().equals(componentID))
				continue;

			final String componentTypeID =
					componentInstance.getComponentTypeID();

			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				return;
			}
			//final String canonicalName = componentType.getCanonicalName();
			final IRuntimeComponentInstance runtimeComponentInstance =
					runtimeComponentInstances.get(componentID);
			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{

					if (propertyName.equals(key))
					{
						synchronized(runtimeComponentInstance) {
							runtimeComponentInstance.setRuntimePropertyValue(key, value);
						}
					}
				}
			}
		}
	}

	/**
	 * This method returns the component instance id of the specified component 
	 * if it exists or empty string otherwise.
	 * @param component the component whose instance is to be returned
	 */
	public String getComponentInstanceIDFromComponentInstance 
	(IRuntimeComponentInstance component) 
	{

		if (component==null) return "";
		Set<String> keys = runtimeComponentInstances.keySet();		
		for (IRuntimeComponentInstance c : 
			runtimeComponentInstances.values())
		{
			if (c.equals(component))
			{

				for (String k : keys)
				{
					if (runtimeComponentInstances.get(k).equals(component))
						return k;
				}	
			}
		}
		return "";
	}

	private void notifyAREEventListeners(AREEvent event) 
	{
		ArrayList<IAREEventListener> listeners = 
				AREServices.instance.getAREEventListners();
		
		for (IAREEventListener listener : listeners)
		{
			switch (event)
			{
			case PRE_DEPLOY_EVENT:
				listener.preDeployModel(); break;
			case POST_DEPLOY_EVENT:
				listener.postDeployModel(); break;
			case PRE_START_EVENT:
				listener.preStartModel(); break;
			case POST_STOP_EVENT:
				listener.postStopModel(); break;
			default:
				break;
			}
		}
		

/*		
		if (methodName.equals("preDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preDeployModel();
			}
		}
		else if (methodName.equals("postDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postDeployModel();
			}

		}
		else if (methodName.equals("preStartModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preStartModel();
			}

		}
		else if (methodName.equals("postStopModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postStopModel();
			}

		}
*/		

	}

	public void setGui(AstericsGUI gui) {

		this.gui = gui;

	}

	public void displayPanel(JPanel panel, 
			IRuntimeComponentInstance componentInstance, boolean display) 
	{

		Set<IComponentInstance> componentInstances = 
				getCurrentRuntimeModel().getComponentInstances();

		AREGUIElement ele;
		String id = getComponentInstanceIDFromComponentInstance(componentInstance);
		for (IComponentInstance instance : componentInstances)
		{
			if (instance.getInstanceID().equals(id))
			{
				ele = instance.getAREGUIElement();
				if (ele != null)
				{
					gui.displayPanel(panel, ele.posX, ele.posY, ele.width, 
							ele.height, display);
				}
			}
		}
	}
	
	/**
	 * @deprecated
	 * @param internalFrame
	 * @param display
	 */
	public void displayFrame (JInternalFrame internalFrame, boolean display)
	{
		gui.displayFrame(internalFrame, display);
	}

	public Dimension getAvailableSpace (IRuntimeComponentInstance componentInstance)
	{

		String componentInstanceID = 
				getComponentInstanceIDFromComponentInstance (componentInstance);

		IRuntimeModel model = getCurrentRuntimeModel();
		IComponentInstance component = 
				model.getComponentInstance(componentInstanceID);
		AREGUIElement el = component.getAREGUIElement();
		if (el!=null)
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) (screenSize.width*el.width/10000f);
			int height = (int) (screenSize.height*el.height/10000f);
			return new Dimension( width , height );
		}
		else
			return new Dimension (0, 0);


	}

	public Point getComponentPosition (IRuntimeComponentInstance componentInstance)
	{

		String componentInstanceID = 
				getComponentInstanceIDFromComponentInstance (componentInstance);

		IRuntimeModel model = getCurrentRuntimeModel();
		IComponentInstance component = 
				model.getComponentInstance(componentInstanceID);
		AREGUIElement el = component.getAREGUIElement();

		if (el!=null)
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			JPanel desktop = gui.getDesktop();
			//int x = ((screenSize.width*el.posX/100) + desktop.getLocationOnScreen().x);
			//int y = ((screenSize.height*el.posY/100) + desktop.getLocationOnScreen().y);
			int x = (int) ((screenSize.width*el.posX/10000f));
			int y = (int) ((screenSize.height*el.posY/10000f));

			return new Point( x , y );
		}
		else
			return new Point (0, 0);


	}

	public void setBundleManager(BundleManager bundleManager) {

		this.bundleManager = bundleManager;

	}


	public Collection<IRuntimeComponentInstance> getComponentRuntimeInstances ()
	{
		return runtimeComponentInstances.values();
	}

	/**
	 * Sync
	 * @param data
	 * @param portID
	 * @param targetComponentID
	 */
	public void bufferData(byte[] data, String portID, String targetComponentID)  
	{
		//		System.out.println ("Buffering..");
		//		System.out.println ("Data: "+data);
		//		System.out.println ("PortID: "+portID);
		//		System.out.println ("targetComponentID: "+targetComponentID);


		IComponentInstance ci = getCurrentRuntimeModel().
				getComponentInstance(targetComponentID);
		Set<IInputPort> bufferedPorts = ci.getBufferedInputPorts();
		Iterator<IInputPort> itr = bufferedPorts.iterator();

	
		if (!bufferedPortsMap.isEmpty() && 
				bufferedPortsMap.containsKey(targetComponentID)) //already buffering
		{

		

			Stack<LinkedHashMap<String, byte[]>> stack = bufferedPortsMap.get(targetComponentID);
			synchronized (stack)
			{
				if (stack.isEmpty()){

					
					
					LinkedHashMap<String, byte[]> row = new LinkedHashMap<String, byte[]>();
					synchronized (row)
					{
						row.put(portID, data);
						stack.push(row);
						
						
						
						if (row.size()==bufferedPorts.size()) //A row has been completed
						{
					
							
							IRuntimeComponentInstance rci = this.runtimeComponentInstances.get(targetComponentID);
							
							//synchronize using the target component, because the component can be considered a black box, that must
							//ensure data integrity. The data propagation of (output to input ports) is also synchronized on the component object.	
							if(rci != null) {
								synchronized(rci) {
									rci.syncedValuesReceived(row);
								}
							} else {
								logger.warning("Data could not be propagated, target component not found: targetComponentId: "+targetComponentID);
							}

							stack.pop();
							if (stack.isEmpty()) bufferedPortsMap.remove(stack);
						}
					}
				}
				else {

			
					
					LinkedHashMap<String, byte[]> row = stack.peek();
					synchronized (row)
					{
						//if (row.size()<=100) 
						row.put(portID, data);
						if (row.size()==bufferedPorts.size()) //A row has been completed
						{
							IRuntimeComponentInstance rci = this.runtimeComponentInstances.get(targetComponentID);
							
							//synchronize using the target component, because the component can be considered a black box, that must
							//ensure data integrity. The data propagation of (output to input ports) is also synchronized on the component object.	
							if(rci != null) {
								synchronized(rci) {
									rci.syncedValuesReceived(row);
								}
							} else {
								logger.warning("Data could not be propagated, target component not found: targetComponentId: "+targetComponentID);
							}
							
							stack.pop();
						}
					}
				}
			}
		}
		else //start buffering
		{
			Stack<LinkedHashMap<String, byte[]>> stack = new  Stack<LinkedHashMap<String, byte[]>>();
			synchronized (stack)
			{
				bufferedPortsMap.put(targetComponentID, stack);
				LinkedHashMap<String, byte[]> row = new LinkedHashMap<String, byte[]>();
				row.put(portID, data);
				stack.push(row);
				//	System.out.println("Row.zise="+row.size()+" bufferedPorts.size() "+bufferedPorts.size() );
				if (row.size()==bufferedPorts.size()) //A row has been completed
				{
				
					
					IRuntimeComponentInstance rci = runtimeComponentInstances.get(targetComponentID);
					
					//synchronize using the target component, because the component can be considered a black box, that must
					//ensure data integrity. The data propagation of (output to input ports) is also synchronized on the component object.			
					if(rci != null) {
						synchronized(rci) {
							rci.syncedValuesReceived(row);
						}
					} else {
						logger.warning("Data could not be propagated, target component not found: targetComponentId: "+targetComponentID);
					}
					
					stack.pop();
					//if (stack.isEmpty()) bufferedPortsMap.remove(stack);
				}
			}
		}
	}
}
