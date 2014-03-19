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
 * Filename: AutodetectARE.cs
 * Class(es):
 *   Classname: AutodetectARE
 *   Description: Sending out an UDP braodcast and parse the return string delivered 
 *   in the callback UPD packet
 * Author: Roland Ossmann
 * Date: 11.08.2011
 * Version: 0.1
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Net.NetworkInformation;

namespace Asterics.ACS {

    /// <summary>
    /// Sending out an UDP braodcast and parse the return string delivered 
    /// in the callback UPD packet
    /// </summary>
    class AutodetectARE {

        private int receivePort; // = 9092;
        private int sendPort; // = 9091;
        private String hostname;
        private String ipAdd;
        private StorageDialog storageDialog;

        /// <summary>
        /// The hostname of the receiver
        /// </summary>
        public String Hostname {
            get { return hostname; }
            set { hostname = value; }
        }

        /// <summary>
        /// The Ip address of the receiver
        /// </summary>
        public String IpAdd {
            get { return ipAdd; }
            set { ipAdd = value; }
        }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="sendPort">The UDP port, the broadcast will be send</param>
        /// <param name="receivePort">The UDP port, the callback message will be received</param>
        public AutodetectARE(int sendPort, int receivePort) {
            this.receivePort = receivePort;
            this.sendPort = sendPort;

            hostname = "";
            ipAdd = "";
        }

        /// <summary>
        /// Send out the broadcast and parse the return string
        /// </summary>
        /// <returns>True if an ARE has been detected, otherwise false</returns>
        public bool Detect(MainWindow window) {

            Dictionary<string, string> foundedAREs = new Dictionary<string, string>();

            foreach (NetworkInterface nic in NetworkInterface.GetAllNetworkInterfaces()) {
                IPInterfaceProperties ipProps = nic.GetIPProperties();
                // check if localAddr is in ipProps.UnicastAddresses
                if (nic.OperationalStatus == OperationalStatus.Up && nic.NetworkInterfaceType != NetworkInterfaceType.Loopback) {




                    System.Text.ASCIIEncoding enc = new System.Text.ASCIIEncoding();
                    byte[] byteString = enc.GetBytes("AsTeRICS Broadcast");
                    UdpClient udpSend = null;

                    try {
                        udpSend = new UdpClient(receivePort, AddressFamily.InterNetwork);


                        udpSend.MulticastLoopback = true;
                        udpSend.EnableBroadcast = true;
                        foreach (MulticastIPAddressInformation ipInfo in ipProps.MulticastAddresses) {
                            Console.WriteLine(ipInfo.Address);
                            if (ipInfo.Address.AddressFamily == AddressFamily.InterNetwork) {
                                udpSend.JoinMulticastGroup(ipInfo.Address);
                                break;
                            }
                        }

                        IPEndPoint groupEp = new IPEndPoint(IPAddress.Broadcast, sendPort);
                        udpSend.Connect(groupEp);

                        udpSend.Send(byteString, byteString.Length);
                        Thread.Sleep(50);  // Sleeps are needed to give the system the time to close the ports.
                        // Otherwise confilicts because of still open ports
                        udpSend.Close();
                        Thread.Sleep(50);

                        IPEndPoint recvEp = new IPEndPoint(IPAddress.Any, 0);
                        UdpClient udpResponse = new UdpClient(receivePort);
                        //Loop to give the ARE some time to respond
                        for (int i = 0; i <= 10; i++) {
                            if (udpResponse.Available > 0) {
                                IPEndPoint remoteEndPoint = new IPEndPoint(IPAddress.Any, receivePort);

                                Byte[] recvBytes = udpResponse.Receive(ref recvEp);
                                //Console.WriteLine("Rxed " + recvBytes.Length + " bytes, " + enc.GetString(recvBytes));
                                String retString = enc.GetString(recvBytes);
                                if (retString.Contains("AsTeRICS Broadcast Ret")) {
                                    hostname = retString.Substring(33, retString.IndexOf(" IP:") - 33); // Parsing the hostname out f the return string
                                    ipAdd = retString.Substring(retString.IndexOf(" IP:") + 4, retString.Length - retString.IndexOf(" IP:") - 4);
                                    if (!foundedAREs.ContainsKey(hostname)) {
                                        foundedAREs.Add(hostname, ipAdd);
                                    }
                                }

                                //break;
                            }
                            else {
                                //Console.WriteLine("nothing received");
                                Thread.Sleep(400); // wait before the next attemt to read data from the port
                            }
                        }
                        Thread.Sleep(100);
                        udpResponse.Close();
                    }
                    catch (Exception ex) {
                        Console.WriteLine(ex.Message);
                        Console.WriteLine(ex.StackTrace);
                        //throw ex;
                    }
                }
            }

            // if more than one ARE is detected, a selection dialog will be opend
            if (foundedAREs.Count > 1) {

                storageDialog = new StorageDialog();

                foreach (string s in foundedAREs.Keys) {
                    storageDialog.filenameListbox.Items.Add(s + " (" + foundedAREs[s] + ")");
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.MultipleAREsDialogTitle;
                storageDialog.filenameTextbox.Text = foundedAREs.Keys.First();

                storageDialog.listLabel.Content = Properties.Resources.MultipleAREsDialogMsg;
                storageDialog.modelNameLabel.Content = Properties.Resources.MultipleAREsDialogSelected;
                storageDialog.cancelButton.Visibility = Visibility.Hidden;
                storageDialog.filenameTextbox.IsEnabled = false;

                storageDialog.Owner = window;
                storageDialog.ShowDialog();

                if (storageDialog.filenameTextbox.Text != null && storageDialog.filenameTextbox.Text != "") {
                    hostname = storageDialog.filenameTextbox.Text;
                    ipAdd = foundedAREs[hostname];
                }

            }
            if (ipAdd.Equals("")) {
                return false;
            }
            else {
                return true;
            }

        }

        private void filenameListbox_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            string hostString = ((ListBox)sender).SelectedItem.ToString();
            storageDialog.filenameTextbox.Text = hostString.Substring(0, hostString.LastIndexOf(" ("));
        }
    }
}

