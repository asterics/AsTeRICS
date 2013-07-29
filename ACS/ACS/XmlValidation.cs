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
 * Filename: XmlValidation.cs
 * Class(es):
 *   Classname: XmlValidation
 *   Description: Validates a Xml-File against a schema definition
 * Author: Roland Ossmann
 * Date: 03.09.2010
 * Version: 0.2
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System;
using System.Xml;
using System.Xml.Schema;

namespace Asterics.ACS {
    
    /// <summary>
    /// Validates a Xml-File against a schema definition
    /// </summary>
    class XmlValidation {

        private String errorString = "";

        public XmlValidation() {
        }

        public String validateXml(String infile, String schema) {
            XmlReaderSettings xmlReaderSettings = new XmlReaderSettings();
            //xmlReaderSettings.ProhibitDtd = false;
            xmlReaderSettings.Schemas.Add("", schema);
            xmlReaderSettings.ValidationType = ValidationType.Schema;
            xmlReaderSettings.ValidationEventHandler += new ValidationEventHandler(schemaValidationEventHandler);

            XmlReader xmlReader = XmlReader.Create(infile, xmlReaderSettings);

            try {
                while (xmlReader.Read()) {
                }
                xmlReader.Close();
            } catch (Exception e) {
                errorString = e.Message;
            }

            return errorString;
        }

        private void schemaValidationEventHandler(object sender, ValidationEventArgs e) {
            if (e.Severity == XmlSeverityType.Warning) {
                Console.Write("WARNING: ");
                Console.WriteLine(e.Message);
                errorString = e.Message;
            } else if (e.Severity == XmlSeverityType.Error) {
                Console.Write("ERROR: ");
                Console.WriteLine(e.Message);
                errorString = e.Message;
            }
        }
    }
}
