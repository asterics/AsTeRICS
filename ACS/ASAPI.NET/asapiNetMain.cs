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
 *         This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 * --------------------------------------------------------------------------------
 * AsTeRICS – EC Grant Agreement No. 247730
 * Assistive Technology Rapid Integration and Construction Set
 * --------------------------------------------------------------------------------
 * Filename: AsapiNetMain.cs
 * Class(es):
 *   Classname: AsapiNetMain
 *   Description: Class covering methods to open and close connection to the thrift server (ARE)
 * Author: Roland Ossmann
 * Date: 26.04.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using Thrift;
using Thrift.Transport;
using Thrift.Protocol;
using System.IO;

namespace Asterics.ASAPI {

    /// <summary>
    /// Class covering methods to open and close connection to the thrift server (ARE)
    /// </summary>
    public class AsapiNetMain {
        private TTransport transport;
        private TProtocol protocol;
        private AsapiServer.Client client;

        /// <summary>
        /// Establish a connection to the server (ARE)
        /// </summary>
        /// <param name="ipAddress">IP-Address of the ARE</param>
        /// <param name="port">Port to connect</param>
        /// <returns>A ASAPI-client object</returns>
        public AsapiServer.Client Connect(string ipAddress, int port, int timeOutParam) {
            int timeOut = 10000;

            if (timeOutParam == -1) {
                // read socket timeout from the ini-file                
                try {
                    IniFile ini = null;
                    if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                        ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
                    } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                        ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
                    }
                    if (ini != null) {
                        timeOut = int.Parse(ini.IniReadValue("ARE", "socket_timeout"));
                        if (timeOut < 100) {
                            timeOut = 1000;
                        }
                    }
                } catch (Exception) {
                }
            } else {
                timeOut = timeOutParam;
            }

            try {
                transport = new TSocket(ipAddress, port, timeOut); // set socket timeout to 10000ms
                protocol = new TBinaryProtocol(transport);
                client = new AsapiServer.Client(protocol);

                transport.Open();
                return client;

            } catch (Exception) { //catch (TApplicationException x) {
                //throw e;
                return null;
            }
        }

        /// <summary>
        /// Close th econnection to the server
        /// </summary>
        /// <param name="client"></param>
        public void Disconnect(AsapiServer.Client client) {
            try {
                transport.Close();
            }
            catch (TApplicationException x) { }
        }

        /// <summary>
        /// Return the version of the used ASAPI definition
        /// </summary>
        /// <returns>The version as string containing also the used thrift version</returns>
        public String getAsapiVersion() {
            return "ASAPI v. 1.1.5 @ thrift 0.8.0";
        }
    }
}
