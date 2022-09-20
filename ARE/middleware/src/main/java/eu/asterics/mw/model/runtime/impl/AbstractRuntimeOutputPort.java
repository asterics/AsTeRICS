package eu.asterics.mw.model.runtime.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AstericsModelExecutionThreadPool;
import eu.asterics.mw.services.RuntimeDataEvent;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy] Date: Aug 20, 2010 Time:
 *         3:20:36 PM
 */
public abstract class AbstractRuntimeOutputPort implements IRuntimeOutputPort {
    private static final Logger logger = AstericsErrorHandling.instance.getLogger();
    // Sync
    private final Map<EndPoint, IRuntimeInputPort> inputPortEndpoints = new LinkedHashMap<EndPoint, IRuntimeInputPort>();
    private final Map<EndPoint, String> portIdToConversion = new LinkedHashMap<EndPoint, String>();

    @Override
    public void sendData(final byte[] data) {

        AstericsModelExecutionThreadPool.instance.execute(new Runnable() {
            @Override
            public void run() {
                // MULTI-THREADED: Remove comments if you want to reenable
                // multi-threaded execution approach.
                // syncing on inputPortEndpoints ensures that a deployed model
                // is not changed during data propagation.
                // logger.fine("Synchronizing on inputPortEndpoints
                // ["+inputPortEndpoints+"], curThread:
                // "+Thread.currentThread().getName());
                // synchronized (inputPortEndpoints)
                {

                    for (Entry<EndPoint, IRuntimeInputPort> entry : inputPortEndpoints.entrySet()) {
                        final IRuntimeInputPort runtimeInputPort = entry.getValue();
                        final EndPoint endPoint = entry.getKey();

                        IComponentInstance targetComponent = DeploymentManager.instance.getCurrentRuntimeModel()
                                .getComponentInstance(endPoint.targetComponentID);
                        if (targetComponent == null) {
                            logger.warning(
                                    "Data could not be propagated, target component not found: targetComponentId: "
                                            + endPoint.targetComponentID);
                            continue;
                        }
                        // block data propagation if model and component is not
                        // up and running
                        // Disabled for now, because has some strange side
                        // effects with camera
                        /*
                         * if(!DeploymentManager.instance.isComponentRunning(
                         * endPoint.targetComponentID)) { //logger.warning(
                         * "Data could not be propagated, target component not running: targetComponentId: "
                         * +endPoint.targetComponentID); System.out.print("D");
                         * continue; }
                         */

                        String conversion = AbstractRuntimeOutputPort.this.portIdToConversion.get(endPoint);
                        // logger.fine("targetCompId:
                        // "+endPoint.targetComponentID+", conversion:
                        // "+conversion+", data.length: "+data.length);
                        // logger.fine("portIdToConversion:
                        // "+portIdToConversion.entrySet().toString());
                        byte[] newData = data;
                        if (conversion != null && !"".equals(conversion)) {
                            newData = ConversionUtils.convertData(data, conversion);
                        }

                        if (newData == null) {
                            logger.warning("Data for input port is null --> skip it, port: " + endPoint);
                            continue;
                        }
                        if (runtimeInputPort.isBuffered()) {
                            DeploymentManager.instance.bufferData(newData, endPoint.portID, endPoint.targetComponentID);
                        } else {
                            // MULTI-THREADED: Remove comments if you want to
                            // reenable multi-threaded execution approach.
                            // We have to synchronize using the target
                            // component, because the component can be
                            // considered a black box, that must
                            // ensure data integrity. The data propagation,
                            // event notification, start, (stop), set Property
                            // should all synchronize on targetComponent.
                            // logger.fine("Synchronizing on targetComponentId:
                            // "+endPoint.targetComponentID);
                            // synchronized(targetComponent)
                            {
                                runtimeInputPort.receiveData(newData);

                                try {
                                    // Retrieve the channel id
                                    String currentChannelId = "";
                                    Set<IChannel> channels = DeploymentManager.instance.getCurrentRuntimeModel()
                                            .getChannels();
                                    for (IChannel channel : channels) {
                                        if (channel.getTargetComponentInstanceID().equals(endPoint.targetComponentID)
                                                && channel.getTargetInputPortID().equals(endPoint.portID)) {
                                            currentChannelId = channel.getChannelID();
                                            break;
                                        }
                                    }

                                    // Convert the data to the actual type and
                                    // save them as sctring characters
                                    DataType portDataType = DeploymentManager.instance.getCurrentRuntimeModel()
                                            .getPortDataType(endPoint.targetComponentID, endPoint.portID);
                                    String listenerData = "";
                                    switch (portDataType) {
                                    case BOOLEAN:
                                        listenerData = ConversionUtils.booleanFromBytes(newData) + "";
                                        break;
                                    case BYTE:
                                        listenerData = newData + "";
                                        break;
                                    case CHAR:
                                        listenerData = ConversionUtils.charFromBytes(newData) + "";
                                        break;
                                    case INTEGER:
                                        listenerData = ConversionUtils.intFromBytes(newData) + "";
                                        break;
                                    case DOUBLE:
                                        listenerData = ConversionUtils.doubleFromBytes(newData) + "";
                                        break;
                                    case STRING:
                                        listenerData = ConversionUtils.stringFromBytes(newData) + "";
                                        break;
                                    case UNKNOWN:
                                        listenerData = "";
                                        break;
                                    }

                                    // notify the subscribers
                                    if (!currentChannelId.isEmpty()) {
                                        RuntimeDataEvent event = RuntimeDataEvent
                                                .newDataChannelTransmission(currentChannelId, listenerData);
                                        AREServices.instance.notifyRuntimeDataListeners(event);
                                    }
                                } catch (Exception ex) {
                                    // error while trying to transmit channel
                                    // data to a remote subscriber
                                }

                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    public void addInputPortEndpoint(final String targetComponentID, final String portID,
            final IRuntimeInputPort inputPort, String conversion) {
        // MULTI-THREADED: Remove comments if you want to reenable
        // multi-threaded execution approach.
        // syncing on inputPortEndpoints ensures that a deployed model is not
        // changed during data propagation.
        // synchronized (inputPortEndpoints)
        {
            EndPoint ep = new EndPoint(targetComponentID, portID);
            inputPortEndpoints.put(ep, inputPort);
            if (conversion.compareTo("") != 0) {
                this.portIdToConversion.put(ep, conversion);
            }
        }
    }

    @Override
    public void removeInputPortEndpoint(final String targetComponentID, final String portID) {
        // MULTI-THREADED: Remove comments if you want to reenable
        // multi-threaded execution approach.
        // syncing on inputPortEndpoints ensures that a deployed model is not
        // changed during data propagation.
        // synchronized (inputPortEndpoints)
        {
            inputPortEndpoints.remove(new EndPoint(targetComponentID, portID));
        }
    }

    class EndPoint {
        private String uniqueId;

        private String targetComponentID = "";
        private String portID = "";

        EndPoint(final String targetComponentID, final String portID) {
            this.targetComponentID = targetComponentID;
            this.portID = portID;
            this.uniqueId = getUniqueID(targetComponentID, portID);
        }

        public String getUniqueID(final String targetComponentID, final String portID) {
            return targetComponentID + ":" + portID;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            EndPoint other = (EndPoint) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (uniqueId == null) {
                if (other.uniqueId != null) {
                    return false;
                }
            } else if (!uniqueId.equals(other.uniqueId)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return getUniqueID(targetComponentID, portID);
        }

        private AbstractRuntimeOutputPort getOuterType() {
            return AbstractRuntimeOutputPort.this;
        }
    }
}