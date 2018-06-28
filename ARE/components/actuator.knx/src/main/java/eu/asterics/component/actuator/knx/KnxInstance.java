
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

package eu.asterics.component.actuator.knx;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeInputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import tuwien.auto.calimero.DetachEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.Priority;
import tuwien.auto.calimero.datapoint.CommandDP;
import tuwien.auto.calimero.dptxlator.DPT;
import tuwien.auto.calimero.dptxlator.DPTXlator;
import tuwien.auto.calimero.dptxlator.TranslatorTypes;
import tuwien.auto.calimero.dptxlator.TranslatorTypes.MainType;
import tuwien.auto.calimero.exception.KNXAckTimeoutException;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;
import tuwien.auto.calimero.process.ProcessEvent;
import tuwien.auto.calimero.process.ProcessListener;

/* 
 * This module provides read and write access to a KNX home automation installation
 * 
 * Currently, it is necessary to have a KNX to IP gateway, USB gateways don't work
 * The plugin uses the calimero library (https://github.com/calimero-project/calimero-core)
 * 
 * Interfacing KNX devices is possible via different ways:
 * -) using the action string
 * -) using input ports (write only)
 * -) using output ports (read only)
 * -) using event trigger ports (read only)
 * -) using event listener ports (write only)
 * 
 * For all methods, except the action string, you need to define the
 * DPT (datapoint type), a group adress and its value.
 * All possible DPTs are fetched from the calimero library. Due
 * to the dynamic property feature, the list only appears if the ACS is
 * connected to the ARE and the model is uploaded (it is not necessary to start it)
 *  
 * @author Benjamin Aigner <aignerb@technikum-wien.at
 *         Date: 2015-2016
 *         Time: 
 */
public class KnxInstance extends AbstractRuntimeComponentInstance {
    // Usage of an output port e.g.:
    // opMyOutPort.sendData(ConversionUtils.intToBytes(10));
    private KNXNetworkLinkIP netLinkIp = null;
    private ProcessCommunicator pc = null;
    // Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

    /** PROPERTIES **/
    private boolean propNAT = true;
    private String propLocalIP = new String();
    private String propKnxNetIP = new String();

    // Output to KNX of given values to the given group addresses
    // Output is triggered via event input ports
    private String propGroupAddress1 = new String();
    private String propDataValue1 = new String();
    private String propGroupAddress2 = new String();
    private String propDataValue2 = new String();
    private String propGroupAddress3 = new String();
    private String propDataValue3 = new String();
    private String propGroupAddress4 = new String();
    private String propDataValue4 = new String();
    private String propGroupAddress5 = new String();
    private String propDataValue5 = new String();
    private String propGroupAddress6 = new String();
    private String propDataValue6 = new String();
    private String propDPTEvent1 = new String();
    private String propDPTEvent2 = new String();
    private String propDPTEvent3 = new String();
    private String propDPTEvent4 = new String();
    private String propDPTEvent5 = new String();
    private String propDPTEvent6 = new String();

    // Output to KNX of the slider values (input ports) to the given group
    // addresses
    private String propGroupAddressSlider1 = new String();
    private String propGroupAddressSlider2 = new String();
    private String propGroupAddressSlider3 = new String();
    private String propGroupAddressSlider4 = new String();
    private String propGroupAddressSlider5 = new String();
    private String propGroupAddressSlider6 = new String();
    private String propDPTSlider1 = new String();
    private String propDPTSlider2 = new String();
    private String propDPTSlider3 = new String();
    private String propDPTSlider4 = new String();
    private String propDPTSlider5 = new String();
    private String propDPTSlider6 = new String();

    // Group addresses for the event triggers (ignoring data & DPT)
    private String propGroupAddressTrigger1 = new String();
    private String propGroupAddressTrigger2 = new String();
    private String propGroupAddressTrigger3 = new String();
    private String propGroupAddressTrigger4 = new String();
    private String propGroupAddressTrigger5 = new String();
    private String propGroupAddressTrigger6 = new String();

    // Group addresses for the output ports
    private String propGroupAddressOutput1 = new String();
    private String propGroupAddressOutput2 = new String();
    private String propGroupAddressOutput3 = new String();
    private String propGroupAddressOutput4 = new String();
    private String propGroupAddressOutput5 = new String();
    private String propGroupAddressOutput6 = new String();
    private String propDPTOutput1 = new String();
    private String propDPTOutput2 = new String();
    private String propDPTOutput3 = new String();
    private String propDPTOutput4 = new String();
    private String propDPTOutput5 = new String();
    private String propDPTOutput6 = new String();

    /** PORTS: INPUT, OUTPUT, EVENT IN, EVENT OUT **/

    // Event Listener Ports
    private final String ELP_SEND1 = "send1";
    private final String ELP_SEND2 = "send2";
    private final String ELP_SEND3 = "send3";
    private final String ELP_SEND4 = "send4";
    private final String ELP_SEND5 = "send5";
    private final String ELP_SEND6 = "send6";
    private final String ELP_READ1 = "read1";
    private final String ELP_READ2 = "read2";
    private final String ELP_READ3 = "read3";
    private final String ELP_READ4 = "read4";
    private final String ELP_READ5 = "read5";
    private final String ELP_READ6 = "read6";

    // Event trigger ports (output events)
    final IRuntimeEventTriggererPort runtimeEventTriggererPort1 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort runtimeEventTriggererPort2 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort runtimeEventTriggererPort3 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort runtimeEventTriggererPort4 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort runtimeEventTriggererPort5 = new DefaultRuntimeEventTriggererPort();
    final IRuntimeEventTriggererPort runtimeEventTriggererPort6 = new DefaultRuntimeEventTriggererPort();

    // 6 output ports for KNX data received by AsTeRICS
    private IRuntimeOutputPort opData1 = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opData2 = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opData3 = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opData4 = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opData5 = new DefaultRuntimeOutputPort();
    private IRuntimeOutputPort opData6 = new DefaultRuntimeOutputPort();

    private double in1 = 0, in2 = 0, in3 = 0, in4 = 0, in5 = 0, in6 = 0;

    // KNX Datatypes 
	private List<String> KNX_IDS_INTEGER = Arrays.asList("5.010", "5.001", "5.004", "5.005", "5.006", "7.001","7.002", "7.003","7.004", "7.005", "7.006", "7.007","7.010", "7.011","7.012","7.013","9.001","9.002","9.003", "9.006","9.007","9.008", "9.010", "9.011","9.020","9.021","9.022","9.023","9.024","9.025","12.001","13.001","13.002","13.010","13.011", "13.012", "13.014", "13.015","13.100", "18.001");
	private List<String> KNX_IDS_DOUBLE = Arrays.asList("9.026", "9.027", "9.028", "9.029", "14.000", "14.001", "14.002", "14.003", "14.004","14.005",  "14.006", "14.007", "14.008", "14.009", "14.010","14.011", "14.012","14.013", "14.014", "14.015", "14.016","14.017","14.018","14.019", "14.020","14.021", "14.022","14.023","14.024", "14.025","14.026","14.027","14.028","14.029","14.030", "14.031","14.032", "14.033","14.034","14.035","14.036","14.037","14.038","14.039","14.040","14.041","14.042", "14.043", "14.044","14.045","14.046","14.047","14.048","14.049","14.050","14.051","14.052", "14.053","14.054","14.055","14.056","14.057","14.058", "14.059","14.060","14.061","14.062","14.063","14.064","14.065","14.066","14.067","14.068","14.069","14.070","14.071","14.072","14.073","14.074","14.075","14.076","14.077","14.078","14.079");


    
    /**
     * The class constructor.
     */
    public KnxInstance() {

    }

    /**
     * returns an Input Port.
     * 
     * @param portID
     *            the name of the port
     * @return the input port or null if not found
     */
    @Override
    public IRuntimeInputPort getInputPort(String portID) {
        if ("actionString".equalsIgnoreCase(portID)) {
            return ipCommand;
        }
        if ("slider1".equalsIgnoreCase(portID)) {
            return ipSlider1;
        }
        if ("slider2".equalsIgnoreCase(portID)) {
            return ipSlider2;
        }
        if ("slider3".equalsIgnoreCase(portID)) {
            return ipSlider3;
        }
        if ("slider4".equalsIgnoreCase(portID)) {
            return ipSlider4;
        }
        if ("slider5".equalsIgnoreCase(portID)) {
            return ipSlider5;
        }
        if ("slider6".equalsIgnoreCase(portID)) {
            return ipSlider6;
        }

        return null;
    }

    /**
     * returns an Output Port.
     * 
     * @param portID
     *            the name of the port
     * @return the output port or null if not found
     */
    @Override
    public IRuntimeOutputPort getOutputPort(String portID) {
        if ("data1".equalsIgnoreCase(portID)) {
            return opData1;
        }
        if ("data2".equalsIgnoreCase(portID)) {
            return opData2;
        }
        if ("data3".equalsIgnoreCase(portID)) {
            return opData3;
        }
        if ("data4".equalsIgnoreCase(portID)) {
            return opData4;
        }
        if ("data5".equalsIgnoreCase(portID)) {
            return opData5;
        }
        if ("data6".equalsIgnoreCase(portID)) {
            return opData6;
        }
        return null;
    }

    /**
     * returns an Event Listener Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventListener port or null if not found
     */
    @Override
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID) {
        if (ELP_SEND1.equalsIgnoreCase(eventPortID)) {
            return elpSend1;
        }
        if (ELP_SEND2.equalsIgnoreCase(eventPortID)) {
            return elpSend2;
        }
        if (ELP_SEND3.equalsIgnoreCase(eventPortID)) {
            return elpSend3;
        }
        if (ELP_SEND4.equalsIgnoreCase(eventPortID)) {
            return elpSend4;
        }
        if (ELP_SEND5.equalsIgnoreCase(eventPortID)) {
            return elpSend5;
        }
        if (ELP_SEND6.equalsIgnoreCase(eventPortID)) {
            return elpSend6;
        }
        if (ELP_READ1.equalsIgnoreCase(eventPortID)) {
            return elpRead1;
        }
        if (ELP_READ2.equalsIgnoreCase(eventPortID)) {
            return elpRead2;
        }
        if (ELP_READ3.equalsIgnoreCase(eventPortID)) {
            return elpRead3;
        }
        if (ELP_READ4.equalsIgnoreCase(eventPortID)) {
            return elpRead4;
        }
        if (ELP_READ5.equalsIgnoreCase(eventPortID)) {
            return elpRead5;
        }
        if (ELP_READ6.equalsIgnoreCase(eventPortID)) {
            return elpRead6;
        }

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * 
     * @param eventPortID
     *            the name of the port
     * @return the EventTriggerer port or null if not found
     */

    @Override
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID) {
        if ("event_out_1".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort1;
        } else if ("event_out_2".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort2;
        } else if ("event_out_3".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort3;
        } else if ("event_out_4".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort4;
        } else if ("event_out_5".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort5;
        } else if ("event_out_6".equalsIgnoreCase(eventPortID)) {
            return runtimeEventTriggererPort6;
        }

        return null;
    }

    /**
     * Returns all possible data point types (DPT) for KNX communication, shown
     * in the different lists for each receiving/transmitting part
     */
    @Override
    public List<String> getRuntimePropertyList(String key) {
        // build up the list of all supported datapoint types (given by the
        // calimero library)
        List<String> res = new ArrayList<String>();
        Map<Integer, MainType> mainTypes = TranslatorTypes.getAllMainTypes();
        for (MainType elem : mainTypes.values()) {
            try {
                for (DPT elemSub : (Collection<DPT>) elem.getSubTypes().values()) {
                    res.add(elemSub.getDescription() + "[" + elemSub.getLowerValue() + "," + elemSub.getUpperValue()
                            + "]" + " (" + elemSub.getID() + ")");
                }
            } catch (KNXException e) {
                e.printStackTrace();
            }
        }
        // return the list, if the correct property is given as key
        // return null otherwise
        switch (key) {
        case "DPTEvent1":
        case "DPTEvent2":
        case "DPTEvent3":
        case "DPTEvent4":
        case "DPTEvent5":
        case "DPTEvent6":
        case "DPTSlider1":
        case "DPTSlider2":
        case "DPTSlider3":
        case "DPTSlider4":
        case "DPTSlider5":
        case "DPTSlider6":
        case "DPTOutput1":
        case "DPTOutput2":
        case "DPTOutput3":
        case "DPTOutput4":
        case "DPTOutput5":
        case "DPTOutput6":
            return res;
        default:
            return null;
        }
    }

    /**
     * returns the value of the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @return the property value or null if not found
     */
    @Override
    public Object getRuntimePropertyValue(String propertyName) {
        if ("localIP".equalsIgnoreCase(propertyName)) {
            return propLocalIP;
        } else if ("KNXNetIP".equalsIgnoreCase(propertyName)) {
            return propKnxNetIP;
        } else if ("NAT".equalsIgnoreCase(propertyName)) {
            return propNAT;
        } else if ("groupAddress1".equalsIgnoreCase(propertyName)) {
            return propGroupAddress1;
        } else if ("dataValue1".equalsIgnoreCase(propertyName)) {
            return propDataValue1;
        } else if ("DPTEvent1".equalsIgnoreCase(propertyName)) {
            return propDPTEvent1;
        } else if ("groupAddress2".equalsIgnoreCase(propertyName)) {
            return propGroupAddress2;
        } else if ("dataValue2".equalsIgnoreCase(propertyName)) {
            return propDataValue2;
        } else if ("DPTEvent2".equalsIgnoreCase(propertyName)) {
            return propDPTEvent2;
        } else if ("groupAddress3".equalsIgnoreCase(propertyName)) {
            return propGroupAddress3;
        } else if ("dataValue3".equalsIgnoreCase(propertyName)) {
            return propDataValue3;
        } else if ("DPTEvent3".equalsIgnoreCase(propertyName)) {
            return propDPTEvent3;
        } else if ("groupAddress4".equalsIgnoreCase(propertyName)) {
            return propGroupAddress4;
        } else if ("dataValue4".equalsIgnoreCase(propertyName)) {
            return propDataValue4;
        } else if ("DPTEvent4".equalsIgnoreCase(propertyName)) {
            return propDPTEvent4;
        } else if ("groupAddress5".equalsIgnoreCase(propertyName)) {
            return propGroupAddress5;
        } else if ("dataValue5".equalsIgnoreCase(propertyName)) {
            return propDataValue5;
        } else if ("DPTEvent5".equalsIgnoreCase(propertyName)) {
            return propDPTEvent5;
        } else if ("groupAddress6".equalsIgnoreCase(propertyName)) {
            return propGroupAddress6;
        } else if ("dataValue6".equalsIgnoreCase(propertyName)) {
            return propDataValue6;
        } else if ("DPTEvent6".equalsIgnoreCase(propertyName)) {
            return propDPTEvent6;
        } else if ("groupAddressTrigger1".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger1;
        } else if ("groupAddressTrigger2".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger2;
        } else if ("groupAddressTrigger3".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger3;
        } else if ("groupAddressTrigger4".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger4;
        } else if ("groupAddressTrigger5".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger5;
        } else if ("groupAddressTrigger6".equalsIgnoreCase(propertyName)) {
            return propGroupAddressTrigger6;
        } else if ("groupAddressSlider1".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider1;
        } else if ("DPTSlider1".equalsIgnoreCase(propertyName)) {
            return propDPTSlider1;
        } else if ("groupAddressSlider2".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider2;
        } else if ("DPTSlider2".equalsIgnoreCase(propertyName)) {
            return propDPTSlider2;
        } else if ("groupAddressSlider3".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider3;
        } else if ("DPTSlider3".equalsIgnoreCase(propertyName)) {
            return propDPTSlider3;
        } else if ("groupAddressSlider4".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider4;
        } else if ("DPTSlider4".equalsIgnoreCase(propertyName)) {
            return propDPTSlider4;
        } else if ("groupAddressSlider5".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider5;
        } else if ("DPTSlider5".equalsIgnoreCase(propertyName)) {
            return propDPTSlider5;
        } else if ("groupAddressSlider6".equalsIgnoreCase(propertyName)) {
            return propGroupAddressSlider6;
        } else if ("DPTSlider6".equalsIgnoreCase(propertyName)) {
            return propDPTSlider6;
        } else if ("groupAddressOutput1".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput1;
        } else if ("DPTOutput1".equalsIgnoreCase(propertyName)) {
            return propDPTOutput1;
        } else if ("groupAddressOutput2".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput2;
        } else if ("DPTOutput2".equalsIgnoreCase(propertyName)) {
            return propDPTOutput2;
        } else if ("groupAddressOutput3".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput3;
        } else if ("DPTOutput3".equalsIgnoreCase(propertyName)) {
            return propDPTOutput3;
        } else if ("groupAddressOutput4".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput4;
        } else if ("DPTOutput4".equalsIgnoreCase(propertyName)) {
            return propDPTOutput4;
        } else if ("groupAddressOutput5".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput5;
        } else if ("DPTOutput5".equalsIgnoreCase(propertyName)) {
            return propDPTOutput5;
        } else if ("groupAddressOutput6".equalsIgnoreCase(propertyName)) {
            return propGroupAddressOutput6;
        } else if ("DPTOutput6".equalsIgnoreCase(propertyName)) {
            return propDPTOutput6;
        }
        return null;
    }

    /**
     * sets a new value for the given property.
     * 
     * @param propertyName
     *            the name of the property
     * @param newValue
     *            the desired property value or null if not found
     */
    @Override
    public Object setRuntimePropertyValue(String propertyName, Object newValue) {
        Object oldValue = null;
        if ("localIP".equalsIgnoreCase(propertyName)) {
            oldValue = propLocalIP;
            propLocalIP = newValue.toString();
        } else if ("KNXNetIP".equalsIgnoreCase(propertyName)) {
            oldValue = propKnxNetIP;
            propKnxNetIP = newValue.toString();
        } else if ("NAT".equalsIgnoreCase(propertyName)) {
            oldValue = propNAT;
            if ("true".equalsIgnoreCase((String) newValue)) {
                propNAT = true;
            } else if ("false".equalsIgnoreCase((String) newValue)) {
                propNAT = false;
            }
        } else if ("groupAddress1".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress1;
            propGroupAddress1 = newValue.toString();
        } else if ("dataValue1".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue1;
            propDataValue1 = newValue.toString();
        } else if ("DPTEvent1".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent1;
            propDPTEvent1 = newValue.toString();
        } else if ("groupAddress2".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress2;
            propGroupAddress2 = newValue.toString();
        } else if ("dataValue2".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue2;
            propDataValue2 = newValue.toString();
        } else if ("DPTEvent2".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent2;
            propDPTEvent2 = newValue.toString();
        } else if ("groupAddress3".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress3;
            propGroupAddress3 = newValue.toString();
        } else if ("dataValue3".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue3;
            propDataValue3 = newValue.toString();
        } else if ("DPTEvent3".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent3;
            propDPTEvent3 = newValue.toString();
        } else if ("groupAddress4".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress4;
            propGroupAddress4 = newValue.toString();
        } else if ("dataValue4".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue4;
            propDataValue4 = newValue.toString();
        } else if ("DPTEvent4".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent4;
            propDPTEvent4 = newValue.toString();
        } else if ("groupAddress5".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress5;
            propGroupAddress5 = newValue.toString();
        } else if ("dataValue5".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue5;
            propDataValue5 = newValue.toString();
        } else if ("DPTEvent5".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent5;
            propDPTEvent5 = newValue.toString();
        } else if ("groupAddress6".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddress6;
            propGroupAddress6 = newValue.toString();
        } else if ("dataValue6".equalsIgnoreCase(propertyName)) {
            oldValue = propDataValue6;
            propDataValue6 = newValue.toString();
        } else if ("DPTEvent6".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTEvent6;
            propDPTEvent6 = newValue.toString();
        } else if ("groupAddressSlider1".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider1;
            propGroupAddressSlider1 = newValue.toString();
        } else if ("DPTSlider1".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider1;
            propDPTSlider1 = newValue.toString();
        } else if ("groupAddressSlider2".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider2;
            propGroupAddressSlider2 = newValue.toString();
        } else if ("DPTSlider2".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider2;
            propDPTSlider2 = newValue.toString();
        } else if ("groupAddressSlider3".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider3;
            propGroupAddressSlider3 = newValue.toString();
        } else if ("DPTSlider3".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider3;
            propDPTSlider3 = newValue.toString();
        } else if ("groupAddressSlider4".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider4;
            propGroupAddressSlider4 = newValue.toString();
        } else if ("DPTSlider4".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider4;
            propDPTSlider4 = newValue.toString();
        } else if ("groupAddressSlider5".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider5;
            propGroupAddressSlider5 = newValue.toString();
        } else if ("DPTSlider5".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider5;
            propDPTSlider5 = newValue.toString();
        } else if ("groupAddressSlider6".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressSlider6;
            propGroupAddressSlider6 = newValue.toString();
        } else if ("DPTSlider6".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTSlider6;
            propDPTSlider6 = newValue.toString();
        } else if ("groupAddressTrigger1".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger1;
            propGroupAddressTrigger1 = newValue.toString();
        } else if ("groupAddressTrigger2".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger2;
            propGroupAddressTrigger2 = newValue.toString();
        } else if ("groupAddressTrigger3".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger3;
            propGroupAddressTrigger3 = newValue.toString();
        } else if ("groupAddressTrigger4".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger4;
            propGroupAddressTrigger4 = newValue.toString();
        } else if ("groupAddressTrigger5".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger5;
            propGroupAddressTrigger5 = newValue.toString();
        } else if ("groupAddressTrigger6".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressTrigger6;
            propGroupAddressTrigger6 = newValue.toString();
        } else if ("groupAddressOutput1".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput1;
            propGroupAddressOutput1 = newValue.toString();
        } else if ("DPTOutput1".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput1;
            propDPTOutput1 = newValue.toString();
        } else if ("groupAddressOutput2".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput2;
            propGroupAddressOutput2 = newValue.toString();
        } else if ("DPTOutput2".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput2;
            propDPTOutput2 = newValue.toString();
        } else if ("groupAddressOutput3".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput3;
            propGroupAddressOutput3 = newValue.toString();
        } else if ("DPTOutput3".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput3;
            propDPTOutput3 = newValue.toString();
        } else if ("groupAddressOutput4".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput4;
            propGroupAddressOutput4 = newValue.toString();
        } else if ("DPTOutput4".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput4;
            propDPTOutput4 = newValue.toString();
        } else if ("groupAddressOutput5".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput5;
            propGroupAddressOutput5 = newValue.toString();
        } else if ("DPTOutput5".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput5;
            propDPTOutput5 = newValue.toString();
        } else if ("groupAddressOutput6".equalsIgnoreCase(propertyName)) {
            oldValue = propGroupAddressOutput6;
            propGroupAddressOutput6 = newValue.toString();
        } else if ("DPTOutput6".equalsIgnoreCase(propertyName)) {
            oldValue = propDPTOutput6;
            propDPTOutput6 = newValue.toString();
        }

        return oldValue;
    }

    /**
     * Input Ports for receiving values.
     */
    private final IRuntimeInputPort ipCommand = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            String text = ConversionUtils.stringFromBytes(data);
            AstericsErrorHandling.instance.getLogger().info("KNX received: " + text);

            if (text.startsWith("@KNX:")) {
                try {
                    StringTokenizer st = new StringTokenizer(text.substring(5), " ,#");
                    sendKNX(st.nextToken(), st.nextToken(), st.nextToken());
                } catch (Exception e) {
                    AstericsErrorHandling.instance.getLogger().severe(e.toString());
                }
            }
        }
    };
    private final IRuntimeInputPort ipSlider1 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in1 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider1);
            String DPTval = convertKNXValue(in1,DPTid);
            sendKNX(propGroupAddressSlider1, DPTid, DPTval);
        } 
    };
    private final IRuntimeInputPort ipSlider2 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in2 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider2);
            String DPTval = convertKNXValue(in2,DPTid);
            sendKNX(propGroupAddressSlider2, DPTid, DPTval);
        }
    };
    private final IRuntimeInputPort ipSlider3 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in3 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider3);
            String DPTval = convertKNXValue(in3,DPTid);
            sendKNX(propGroupAddressSlider3, DPTid, DPTval);
        }
    };
    private final IRuntimeInputPort ipSlider4 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in4 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider4);
            String DPTval = convertKNXValue(in4,DPTid);
            sendKNX(propGroupAddressSlider4, DPTid, DPTval);
        }
    };
    private final IRuntimeInputPort ipSlider5 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in5 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider5);
            String DPTval = convertKNXValue(in5,DPTid);
            sendKNX(propGroupAddressSlider5, DPTid, DPTval);
        }
    };
    private final IRuntimeInputPort ipSlider6 = new DefaultRuntimeInputPort() {
        @Override
        public void receiveData(byte[] data) {
            in6 = ConversionUtils.doubleFromBytes(data);
            String DPTid = propertyToDPTid(propDPTSlider6);
            String DPTval = convertKNXValue(in6,DPTid);
            sendKNX(propGroupAddressSlider6, DPTid, DPTval);
        }
    };

    /**
     * Event Listerner Ports.
     */
    final IRuntimeEventListenerPort elpSend1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress1, propertyToDPTid(propDPTEvent1), propDataValue1);
        }
    };
    final IRuntimeEventListenerPort elpSend2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress2, propertyToDPTid(propDPTEvent2), propDataValue2);
        }
    };
    final IRuntimeEventListenerPort elpSend3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress3, propertyToDPTid(propDPTEvent3), propDataValue3);
        }
    };
    final IRuntimeEventListenerPort elpSend4 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress4, propertyToDPTid(propDPTEvent4), propDataValue4);
        }
    };
    final IRuntimeEventListenerPort elpSend5 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress5, propertyToDPTid(propDPTEvent5), propDataValue5);
        }
    };
    final IRuntimeEventListenerPort elpSend6 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            sendKNX(propGroupAddress6, propertyToDPTid(propDPTEvent6), propDataValue6);
        }
    };

    final IRuntimeEventListenerPort elpRead1 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput1), "read1");
                dp.setDPT(0, propertyToDPTid(propDPTOutput1));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData1.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };
    final IRuntimeEventListenerPort elpRead2 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput2), "read2");
                dp.setDPT(0, propertyToDPTid(propDPTOutput2));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData2.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };
    final IRuntimeEventListenerPort elpRead3 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput3), "read3");
                dp.setDPT(0, propertyToDPTid(propDPTOutput3));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData3.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };
    final IRuntimeEventListenerPort elpRead4 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput4), "read4");
                dp.setDPT(0, propertyToDPTid(propDPTOutput4));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData4.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };
    final IRuntimeEventListenerPort elpRead5 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput5), "read5");
                dp.setDPT(0, propertyToDPTid(propDPTOutput5));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData5.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };
    final IRuntimeEventListenerPort elpRead6 = new IRuntimeEventListenerPort() {
        @Override
        public void receiveEvent(final String data) {
            try {
                // there is no read for groupaddress/DPT, building a new
                // CommandDP
                CommandDP dp = new CommandDP(new GroupAddress(propGroupAddressOutput6), "read6");
                dp.setDPT(0, propertyToDPTid(propDPTOutput6));
                // Read the data, wait for the response and send it to the
                // output port
                if (pc != null) {
                    opData6.sendData(pc.read(dp).getBytes());
                } else {
                    AstericsErrorHandling.instance.getLogger().info("KNX == null, not connected");
                }
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        }
    };

    /**
     * Convert a property string to DPT id
     * 
     * @param s
     *            property string
     * @return DPT id
     */
    private String propertyToDPTid(String s) {
        String[] res = s.split(Pattern.quote("("));
        String ret = res[res.length - 1];
        return ret.substring(0, ret.length() - 1);
    }

    /**
     * called when model is started.
     */
    @Override
    public void start() {
        openKNXconnection();
        super.start();
    }

    /**
     * called when model is paused.
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * called when model is resumed.
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * called when model is stopped.
     */
    @Override
    public void stop() {
        closeKNXconnection();
        super.stop();
    }

    /**
     * opens a connection to a KNX/IP router via the Calimero library.
     */
    private void openKNXconnection() {
        try {
            AstericsErrorHandling.instance.getLogger().info("Try to open KNX connection");
            netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNELING,
                    new InetSocketAddress(InetAddress.getByName(propLocalIP), 0),
                    new InetSocketAddress(InetAddress.getByName(propKnxNetIP), KNXnetIPConnection.DEFAULT_PORT),
                    propNAT, new TPSettings(false));
            pc = new ProcessCommunicatorImpl(netLinkIp);
            AstericsErrorHandling.instance.getLogger().info("KNX connection opened");

            pc.addProcessListener(new ProcessListener() {

                @Override
                public void detached(DetachEvent e) {
                    System.out.println(e.getSource().toString() + "-DETACH");
                }

                @Override
                public void groupWrite(ProcessEvent e) {
                    // if something happens on the given groupadresses, raise an
                    // event
                    if (e.getDestination().toString().equals(propGroupAddressTrigger1)) {
                        runtimeEventTriggererPort1.raiseEvent();
                    }
                    if (e.getDestination().toString().equals(propGroupAddressTrigger2)) {
                        runtimeEventTriggererPort2.raiseEvent();
                    }
                    if (e.getDestination().toString().equals(propGroupAddressTrigger3)) {
                        runtimeEventTriggererPort3.raiseEvent();
                    }
                    if (e.getDestination().toString().equals(propGroupAddressTrigger4)) {
                        runtimeEventTriggererPort4.raiseEvent();
                    }
                    if (e.getDestination().toString().equals(propGroupAddressTrigger5)) {
                        runtimeEventTriggererPort5.raiseEvent();
                    }
                    if (e.getDestination().toString().equals(propGroupAddressTrigger6)) {
                        runtimeEventTriggererPort6.raiseEvent();
                    }

                    // check for any output port data (corresponding to the
                    // property: propGroupAddressOutputx)
                    try {
                        if (e.getDestination().toString().equals(propGroupAddressOutput1)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput1));
                            trans.setData(e.getASDU());
                            opData1.sendData(trans.getValue().getBytes());
                        }
                        if (e.getDestination().toString().equals(propGroupAddressOutput2)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput2));
                            trans.setData(e.getASDU());
                            opData2.sendData(trans.getValue().getBytes());
                        }
                        if (e.getDestination().toString().equals(propGroupAddressOutput3)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput3));
                            trans.setData(e.getASDU());
                            opData3.sendData(trans.getValue().getBytes());
                        }
                        if (e.getDestination().toString().equals(propGroupAddressOutput4)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput4));
                            trans.setData(e.getASDU());
                            opData4.sendData(trans.getValue().getBytes());
                        }
                        if (e.getDestination().toString().equals(propGroupAddressOutput5)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput5));
                            trans.setData(e.getASDU());
                            opData5.sendData(trans.getValue().getBytes());
                        }
                        if (e.getDestination().toString().equals(propGroupAddressOutput6)) {
                            DPTXlator trans = TranslatorTypes.createTranslator(0, propertyToDPTid(propDPTOutput6));
                            trans.setData(e.getASDU());
                            opData6.sendData(trans.getValue().getBytes());
                        }
                    } catch (KNXException e1) {
                        AstericsErrorHandling.instance.getLogger().severe(e1.toString());
                    }
                }

            });
        } catch (Exception e) {
            netLinkIp = null;
            AstericsErrorHandling.instance.getLogger().severe(e.toString());
        }
    }

    /**
     * closes a connection to a KNX/IP router.
     */
    private void closeKNXconnection() {
        if (pc != null) {
            pc.detach();
        }
        if (netLinkIp != null) {
            netLinkIp.close();
            AstericsErrorHandling.instance.getLogger().info("KNX connection closed");
        }
    }

    /**
     * sends a group/type/value command to a KNX/IP router via the Calimero
     * library.
     */
    private void sendKNX(String groupaddress, String type, String value) {
        if (netLinkIp != null) {
            try {
                DPTXlator trans = TranslatorTypes.createTranslator(0, type);
                trans.setValue(value);
                pc.write(new GroupAddress(groupaddress), Priority.NORMAL, trans);

                AstericsErrorHandling.instance.getLogger()
                        .info("sent value: " + value + " type: " + type + " to " + groupaddress);
            } catch (KNXAckTimeoutException kae) {
                AstericsErrorHandling.instance.getLogger().severe("KNXAckTimeoutException, reconnecting!");
                this.closeKNXconnection();
                this.openKNXconnection();
            } catch (KNXLinkClosedException kle) {
                AstericsErrorHandling.instance.getLogger().severe("KNXLinkClosedException, reconnecting!");
                this.closeKNXconnection();
                this.openKNXconnection();
            } catch (Exception e) {
                AstericsErrorHandling.instance.getLogger().severe(e.toString());
            }
        } else {
            AstericsErrorHandling.instance.getLogger().severe("KNX connection not open");
        }
    }
    
    /**
     * Checks if the DPT type needs the data formatted as integer or double id
     * 
     * @param DPTid	DPT id as String
     *           
     * @return DPT 	true when DPT type should be formatted as int
     * 				false when DPT type should be formatted as double
     */
	private boolean DPTDataTypeIsInt(String DPTid) {
	    return KNX_IDS_INTEGER.contains(DPTid);
	}

	
    /**
     * Converts the value into a string
     * 
     * @param DPTid	DPT id as String
     *        
     * @param value	value of the slider
     *           
     * @return  formatted String
     */
	private String convertKNXValue(double value, String DPTid) {
		if(DPTDataTypeIsInt(DPTid)){
			return Integer.toString((int) value);
		}else{
			return Double.toString(value);
		}
	}
}