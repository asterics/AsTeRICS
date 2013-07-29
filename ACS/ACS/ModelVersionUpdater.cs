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
 * Filename: ModelVersionUpdater.cs
 * Class(es):
 *   Classname: ModelVersionUpdater
 *   Description: Check the error message of the XML-Validator and tries to
 *     update to the current model version. 
 * Author: Roland Ossmann
 * Date: 25.08.2011
 * Version: 0.1
 * Comments:
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;
using System.Windows;
using System.Collections;

namespace Asterics.ACS {

    /// <summary>
    /// Check the error message of the XML-Validator and tries to
    /// update to the current model version.
    /// </summary>
    class ModelVersionUpdater {

        /// <summary>
        /// Update the model and write the model back to the file
        /// </summary>
        /// <param name="xmlError">The error string from the XML-Validator</param>
        /// <param name="inputFile">The filename of the model</param>
        public static void ParseErrorUpdate(String xmlError, String inputFile) {
            if (xmlError.Contains("modelName")) {
                //if the modelName is missing, also the modelVersion will be missing

                XmlDocument myXmlDocument = new XmlDocument();
                myXmlDocument.Load(inputFile);
                //XmlNode node;
                //node = myXmlDocument.DocumentElement;

                XmlElement element = myXmlDocument.SelectSingleNode("model") as XmlElement;
                if (element != null) {
                    element.SetAttribute("modelName", MainWindow.GenerateModelName());
                    element.SetAttribute("version", model.VERSION);
                }

                myXmlDocument.Save(inputFile);
                MessageBox.Show(Properties.Resources.UpdateModelVersionInfo, Properties.Resources.UpdateModelVersionHeader, MessageBoxButton.OK, MessageBoxImage.Information);

                // if the model version is missing, it will be added here
            } else if (xmlError.Contains("version")) {
                XmlDocument myXmlDocument = new XmlDocument();
                myXmlDocument.Load(inputFile);

                XmlElement element = myXmlDocument.SelectSingleNode("model") as XmlElement;
                if (element != null) {
                    element.SetAttribute("version", model.VERSION);
                }

                myXmlDocument.Save(inputFile);
                MessageBox.Show(Properties.Resources.UpdateModelVersionInfo, Properties.Resources.UpdateModelVersionHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            }

        }

        /// <summary>
        /// Create an ARE GUI element, needed for older models, to update them to the current version
        /// </summary>
        /// <param name="mw">The MainWindow</param>
        /// <param name="deployModel">Deployment Motel, containing all components of the model</param>
        /// <param name="componentList">List with all available components (plug-ins)</param>
        public static void UpdateMissingGUI(MainWindow mw, model deployModel, Hashtable componentList) {
            foreach (componentType comp in deployModel.components) {
                Asterics.ACS2.componentTypesComponentType compType = (Asterics.ACS2.componentTypesComponentType)componentList[comp.type_id];
                if (comp.gui == null && compType != null && compType.gui != null) {
                    comp.gui = new guiType();
                    comp.gui.height = compType.gui.height;
                    comp.gui.width = compType.gui.width;
                    comp.gui.posX = "0";
                    comp.gui.posY = "0";
                    mw.AddGUIComponent(comp);
                    mw.ModelHasBeenEdited = true;
                }
            }
        }

        /// <summary>
        /// Create an ARE GUI element, needed for older models, to update them to the current version
        /// </summary>
        /// <param name="mw">The MainWindow</param>
        /// <param name="deployModel">Deployment Motel, containing all components of the model</param>
        public static void UpdateToCurrentVersion(MainWindow mw, model deployModel) {
            if (deployModel.version != model.VERSION) {
                if (deployModel.version == "20120301" || deployModel.version == "20120509" || deployModel.version == "20111104") {
                    // From version 20120301 to 20120509, only minor changes without any change in the older deployment files are made
                    // 20111104 should also work, needs further tests!!!

                    //Update GUI components resolution from, 1/100 to 1/10000
                    foreach (componentType comp in deployModel.components) {
                        if (comp.gui != null) {
                            comp.gui.height = String.Concat(comp.gui.height, "00");
                            comp.gui.width = String.Concat(comp.gui.width, "00");
                            comp.gui.posX = String.Concat(comp.gui.posX, "00");
                            comp.gui.posY = String.Concat(comp.gui.posY, "00");
                        }
                    }

                    // Add the AREGUIWindow
                    deployModel.modelGUI = new modelGUIType();
                    deployModel.modelGUI.AREGUIWindow = new guiType();

                    deployModel.modelGUI.AREGUIWindow.height = "5000";
                    deployModel.modelGUI.AREGUIWindow.width = "9000";
                    deployModel.modelGUI.AREGUIWindow.posX = "0";
                    deployModel.modelGUI.AREGUIWindow.posY = "0";
                    deployModel.modelGUI.AlwaysOnTop = false;
                    deployModel.modelGUI.Decoration = true;
                    deployModel.modelGUI.Fullscreen = false;
                    deployModel.modelGUI.ShopControlPanel = true;
                    deployModel.modelGUI.ToSystemTray = false;

                    MessageBox.Show(Properties.Resources.UpdateModelVersionGUIInfoFormat(deployModel.version,model.VERSION), Properties.Resources.UpdateModelVersionGUIHeader, MessageBoxButton.OK, MessageBoxImage.Information);

                    deployModel.version = model.VERSION;
                    mw.ModelHasBeenEdited = true;
                }
            }            
        }

    }
}
