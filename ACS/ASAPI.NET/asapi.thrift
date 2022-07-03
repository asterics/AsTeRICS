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
 * Filename: asapi.thrift
 * Description: Definition file for the remote methods for the thrift communication
 * Author: Stefan Parker / Roland Ossmann
 * Date: 10.05.2012
 * Version: 1.1.4
 * Comments: Compiled with the thrift-0.8.0 compiler
 * --------------------------------------------------------------------------------
 */

namespace csharp Asterics.ASAPI
namespace java eu.asterics.mw.are.asapi
namespace cpp Asterics.ASAPI

exception AsapiException { // not sure if this is how we want the exception to be - if no, please indicate how we want it
  1: i32 number,
  2: string description
}

struct StatusObject {
  1: string status,
  2: string involvedComponentID,
  3: string errorMsg
}

service AsapiServer {
// as thrift does not support arrays, "list" has been used instead - it translates to "List<string>" in C# and Java an to "std::vector<std::string> &" in C++
	list<string> GetAvailableComponentTypes(),
	string GetModel(),
	void DeployModel(1:string modelInXml) throws (1:AsapiException asapiEx),
	void DeployFile(1:string filename) throws (1:AsapiException asapiEx),
	void DeployModelWithFile(1:string filename, 2:string modelInXml) throws (1:AsapiException asapiEx),
	void NewModel(),
	void RunModel() throws (1:AsapiException asapiEx),
	void PauseModel() throws (1:AsapiException asapiEx),
	void StopModel() throws (1:AsapiException asapiEx),
	list<string> GetComponents(),
	list<string> GetChannels(1:string componentID),
	void InsertComponent(1:string componentID, 2:string componentType) throws (1:AsapiException asapiEx),
	void RemoveComponent(1:string componentID) throws (1:AsapiException asapiEx),
	list<string> GetAllPorts(1:string componentID) throws (1:AsapiException asapiEx),
	list<string> GetInputPorts(1:string componentID) throws (1:AsapiException asapiEx),
	list<string> GetOutputPorts(1:string componentID) throws (1:AsapiException asapiEx),
	void InsertChannel (1:string channelID, 2:string sourceComponentID, 3:string sourcePortID, 4:string targetComponentID, 5:string targetPortID) throws (1:AsapiException asapiEx),
	void RemoveChannel (1:string channelID) throws (1:AsapiException asapiEx),
	list<string> GetComponentPropertyKeys(1:string componentID),
	string GetComponentProperty(1:string componentID, 2:string key),
	string SetComponentProperty(1:string componentID, 2:string key, 3:string value),
	list<string> GetPortPropertyKeys(1:string componentID, 2:string portID),
	string GetPortProperty(1:string componentID, 2:string portID, 3:string key),
	string SetPortProperty(1:string componentID, 2:string portID, 3:string key, 4:string value),
	list<string> GetChannelPropertyKeys(1:string channelID),
	string GetChannelProperty(1:string channelID, 2:string key),
	string SetChannelProperty(1:string channelID, 2:string key, 3:string value),
	string RegisterRemoteConsumer(1:string sourceComponentID, 2:string sourceOutputPortID) throws (1:AsapiException asapiEx),
	void UnregisterRemoteConsumer(1:string remoteConsumerID) throws (1:AsapiException asapiEx),
	string RegisterRemoteProducer (1:string targetComponentID, 2:string targetInputPortID) throws (1:AsapiException asapiEx),
	void UnregisterRemoteProducer (1:string remoteProducerID) throws (1:AsapiException asapiEx),
// the following "binary"	translates to "byte[]" in Java and C#, to "std::string&" in C++:
	binary PollData(1:string courceComponentID, 2: string sourceOutputPortID) throws (1:AsapiException asapiEx),
	void SendData(1:string targetComponentID, 2:string targetInputPortID, 3:binary data) throws (1:AsapiException asapiEx),
	string RegisterLogListener(),
	void UnregisterLogListener(1:string logListenerID),
// updates for ASAPI1.1 (and ASAPI1.1.2: adding AsapiException in this section)
	void storeModel(1:string modelInXML, 2:string filename) throws (1:AsapiException asapiEx),
	bool deleteModelFile(1:string filename) throws (1:AsapiException asapiEx),
	list<string> listAllStoredModels() throws (1:AsapiException asapiEx),
	string getModelFromFile(1:string filename) throws (1:AsapiException asapiEx),
	string getLogFile(),
// update for ASAPI 1.1.1
	list<StatusObject> QueryStatus(1:bool fullList),
// update for ASAPI 1.1.3
	list<string> getRuntimePropertyList(1:string componentID, 2:string key) throws (1:AsapiException asapiEx),
// update for ASAPI 1.1.4
	list<string> getBundleDescriptors() throws (1:AsapiException asapiEx),
// update for ASAPI 1.1.5
	i32 Ping() throws (1:AsapiException asapiEx)



// don't forget: update version in header and in AsapiNetMain.cs !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
}
