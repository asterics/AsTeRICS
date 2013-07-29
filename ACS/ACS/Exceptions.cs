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
 * Filename: Exceptions.cs
 * Class(es):
 *   Classname: LoadPropertiesException
 *   Description: Represents an exception, which occures when properties will be loaded from a model
 *   Classname: LoadInPortException
 *   Description: Represents an exception, which occures when the input ports will be loaded from a model
 *   Classname: LoadOutPortException
 *   Description: Represents an exception, which occures when the output ports will be loaded from a model
 * Author: Roland Ossmann
 * Date: 02.09.2011
 * Version: 0.3
 * Comments: Partial classes to extend the generated classes of deployment_model.cs.
 * --------------------------------------------------------------------------------
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Asterics.ACS {
    
    public class LoadPropertiesException : System.Exception {
        // The default constructor needs to be defined
        // explicitly now since it would be gone otherwise.

        public LoadPropertiesException() {
        }

        public LoadPropertiesException(string message)
            : base(message) {
        }
    }

    public class LoadPortsException : System.Exception {
        // The default constructor needs to be defined
        // explicitly now since it would be gone otherwise.

        public LoadPortsException() {
        }

        public LoadPortsException(string message)
            : base(message) {
        }
    }

    public class LoadInPortsException : System.Exception {
        // The default constructor needs to be defined
        // explicitly now since it would be gone otherwise.

        public LoadInPortsException() {
        }

        public LoadInPortsException(string message)
            : base(message) {
        }
    }

    public class LoadOutPortsException : System.Exception {
        // The default constructor needs to be defined
        // explicitly now since it would be gone otherwise.

        public LoadOutPortsException() {
        }

        public LoadOutPortsException(string message)
            : base(message) {
        }
    }

}
