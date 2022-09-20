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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */
package eu.asterics.component.processor.oska;

import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;

/**
 * OskaOutputs encapsulates all outputs of the OSKA plug-in. This class only
 * works as a container and provides no additional functions.
 * 
 * @author Christoph Weiss [weissch@technikum-wien.at]
 */
class OskaOutputs {

    private static final int NUMBER_OF_EVENT_TRIGGERS = 10;

    IRuntimeOutputPort opAction = new DefaultRuntimeOutputPort();
    IRuntimeOutputPort opKeycodes = new DefaultRuntimeOutputPort();

    final IRuntimeEventTriggererPort[] etpEventOut = new DefaultRuntimeEventTriggererPort[NUMBER_OF_EVENT_TRIGGERS];

    public OskaOutputs() {
        for (int i = 0; i < etpEventOut.length; i++) {
            etpEventOut[i] = new DefaultRuntimeEventTriggererPort();
        }
    }
}
