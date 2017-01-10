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

package eu.asterics.mw.services;

/**
 * The interface to be implemented for components communicating with external
 * software via sockets. This will handle information on establishing and
 * terminating communication as well as the incoming packets from the third
 * party.
 * 
 * @author Christoph Weiss
 *
 */
public interface IRemoteConnectionListener {

    /**
     * Gets called after a remote client has connected to the server socket and
     * sender and receiver threads are set up
     */
    void connectionEstablished();

    /**
     * Gets called when data arrives from the remote client Although there
     * should be a certain buffering mechanism on the receiving side of the
     * socket, the handling of dataReceived should be executed in sufficiently
     * short time or otherwise executed in a different thread.
     * 
     * @param data
     *            data to be transferred
     */
    void dataReceived(byte[] data);

    /**
     * Gets called when the socket connection cannot read or write from the
     * socket anymore
     */
    void connectionLost();

    /**
     * Gets called after connection has been closed
     */
    void connectionClosed();

}
