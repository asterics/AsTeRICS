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
 * Filename: App.xaml.cs
 * Class(es):
 *   Classname: App
 *   Description: Interaction logic for "App.xaml", the class being executed at the startup of the application
 * Author: Roland Ossmann
 * Date: 03.02.2011
 * Version: 0.3
 * Comments: 
 * --------------------------------------------------------------------------------
 */

using System;
using System.Linq;
using System.Windows;
using System.IO;

namespace Asterics.ACS {
    /// <summary>
    /// Interaction logic for "App.xaml", the class being executed at the startup of the application
    /// </summary>
    public partial class App : Application {
        // Global Exception handling. All unhandled exceptions will be handled by the following method
        private void Application_DispatcherUnhandledException(object sender, System.Windows.Threading.DispatcherUnhandledExceptionEventArgs e) {
            MessageBox.Show("An serious error has been occured. The programm will be terminated.\n\nDebugging Information:\n"+e.Exception.Message +"\n"+ e.Exception.ToString()); // .Message
        }

        protected override void OnStartup(StartupEventArgs e) {
                      
            // Loading the language settings from th ini-file. Needed to select langauge specific splash screen
            IniFile ini = null;
            String lang = "";
            if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
                lang = ini.IniReadValue("Options", "language");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
                lang = ini.IniReadValue("Options", "language");
            } 

            // laoding the splash-screen
            SplashScreen appSplash = null;
            if (lang == "de-AT") {
                appSplash = new SplashScreen("images/asterics_startup2_de.png");
            } else if (lang == "pl-PL") {
                appSplash = new SplashScreen("images/asterics_startup2_pl.png");
            } else if(lang == "es-ES") {
                appSplash = new SplashScreen("images/asterics_startup_es.png");
            } else {
                appSplash = new SplashScreen("images/asterics_startup2.png");
            }

            appSplash.Show(false);

            if (e.Args != null && e.Args.Count() > 0) {
                this.Properties["ArbitraryArgName"] = e.Args[0];
            }

             //SplashScreen Exception handling. Needed because of the Bug in .NET 3.5
            try {
                appSplash.Close(TimeSpan.FromSeconds(0.3));
            } catch {
                try {
                    appSplash.Close(TimeSpan.Zero);
                } catch { }
            }

            base.OnStartup(e);
        }


    }
}
