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

package eu.asterics.mw.are.asapi;

import java.util.logging.Logger;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import eu.asterics.mw.are.AREProperties;
import eu.asterics.mw.services.AstericsErrorHandling;

public class Activator implements Runnable {
    public static final String ASAPI_ACS_PORT_NUMBER_PROPKEY = "ASAPI.ACSPortNumber";
    public static int ASAPI_ACS_PORT_NUMBER_DEFAULT = 9090;

    /**
     * Read porperty value ASAPI.ACSPortNumber and return portnumber to use.
     * 
     * @return
     */
    private int getPortNumber() {
        String ASAPIACSPort = AREProperties.instance.getProperty(ASAPI_ACS_PORT_NUMBER_PROPKEY);

        try {
            return Integer.valueOf(ASAPIACSPort);
        } catch (NumberFormatException e) {
            return ASAPI_ACS_PORT_NUMBER_DEFAULT;
        }
    }

    TServer server = null;
    private Logger logger = null;

    public Activator() {
        try {
            logger = AstericsErrorHandling.instance.getLogger();
            AsapiServerHandler handler = new AsapiServerHandler();
            AsapiServer.Processor processor = new AsapiServer.Processor(handler);
            int portNr = getPortNumber();
            logger.info("Using ASAPI ACS port number: " + portNr);
            TServerTransport serverTransport = new TServerSocket(portNr); // socket
                                                                          // timeout
                                                                          // after
                                                                          // 3000ms
                                                                          // =>
                                                                          // TServerSocket(9090,
                                                                          // 3000)
            // simple server for thrift 0.5.0
            // server = new TSimpleServer(processor, serverTransport);
            // simple server for thrift 0.6.1
            // server = new TSimpleServer(new
            // TServer.Args(serverTransport).processor(processor));

            // multithreaded server for thrift 0.5.0
            // server = new TThreadPoolServer(processor, serverTransport);
            // multithreaded server for thrift >= 0.6.1
            server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            // TThreadPoolServer.Args serverArgs = new
            // TThreadPoolServer.Args(serverTransport);
            // serverArgs.maxWorkerThreads(4);
            // server = new
            // TThreadPoolServer(serverArgs.processor(processor).protocolFactory(new
            // TBinaryProtocol.Factory()));

        } catch (Exception e) {
            logger.warning(this.getClass().getName() + "." + "Activator: -> \n" + e.getMessage());
        }

    }

    @Override
    public void run() {
        server.serve();

    }

}