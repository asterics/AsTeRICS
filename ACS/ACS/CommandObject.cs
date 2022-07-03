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
 * Filename: CommandObject.cs
 * Class(es):
 *   Classname: CommandObject
 *   Description: CommandObject is used to store commands (for the undo and redo functionalities)
 * Author: Roland Ossmann
 * Date: 01.02.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections;
using System.Linq;
using System.Text;

namespace Asterics.ACS {
    
    /// <summary>
    /// CommandObject is used to store commands (for the undo and redo functionalities)
    /// </summary>
    class CommandObject {

        private string command;
        private ArrayList involvedObjects;
        private ArrayList parameter;

        public string Command {
            get {
                return command;
            }
            set {
                command = value;
            }
        }

        public object InvolvedObject1 {
            get {
                if (involvedObjects.Count == 1)
                    return involvedObjects[0];
                else return null;
            }
        }

        public ArrayList InvolvedObjects {
            get {
                return involvedObjects;
            }
            set {
                involvedObjects = value;
            }
        }

        public ArrayList Parameter {
            get {
                return parameter;
            }
            set {
                parameter = value;
            }
        }

        public CommandObject() {
            command = "";
            involvedObjects = new ArrayList();
            parameter = new ArrayList();
        }

        public CommandObject(string paramCommand, ArrayList paramObjects) {
            command = paramCommand;
            involvedObjects = paramObjects;
            parameter = new ArrayList();
        }

         public CommandObject(string paramCommand) {
            command = paramCommand;
            involvedObjects = new ArrayList();
            parameter = new ArrayList();
        }


        public CommandObject(string paramCommand, object paramObject) {
            command = paramCommand;
            involvedObjects = new ArrayList();
            involvedObjects.Add(paramObject);
            parameter = new ArrayList();
        }

        public CommandObject(string paramCommand, object[] paramObjects) {
            command = paramCommand;
            involvedObjects = new ArrayList();
            involvedObjects.AddRange(paramObjects);
            parameter = new ArrayList();
        }

    }

}
