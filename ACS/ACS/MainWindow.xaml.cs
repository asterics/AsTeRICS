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
 * Filename: MainWindow.xaml.cs
 * Class(es):
 *   Classname: MainWindow
 *   Description: Functions and eventhandlers for the main window
 * Author: Roland Ossmann
 * Date: 03.09.2010
 * Version: 04.
 * Comment: partial class of MainWindow, other parts of this class in file
 *   MainWindow.xaml and MainWindowGUIEditor.xaml.cs
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Windows.Threading;
using System.Xml.Serialization;
using Asterics.ASAPI;
using AvalonDock;
using Microsoft.VisualBasic.Logging;
using Microsoft.Windows.Controls.Ribbon;

namespace Asterics.ACS {
    /// <summary>
    /// The Main Window of the ACS, including functions and event handlers
    /// </summary>
    public partial class MainWindow {

        #region Global variables

        public const String ACS_VERSION="2.5";
        private int mouseMoveComponentX;
        private int mouseMoveComponentY;

        private int offsetX = 0;
        private int offsetY = 0;

        private int copyOffsetMulti = 1;
        private int copyXOffset = 150;
        private int copyYOffset = 150;
        //private Hashtable model = new Hashtable();

        private String copyDummyName = "copydummyftwsurrockslolcatftwnyannyannyan";
        private String pasteDummyName = "";
        

        private bool hiddenChannels = false;
        private bool connectedChannelLastClick = false;

        private Hashtable componentList = new Hashtable();
        private System.Collections.Generic.Dictionary<string, componentType> deploymentComponentList = new System.Collections.Generic.Dictionary<string, componentType>();
        private System.Collections.Generic.Dictionary<string, channel> deploymentChannelList = new System.Collections.Generic.Dictionary<string, channel>();

        private ContextMenu componentContextMenu;
        private ContextMenu channelContextMenu;
        private ContextMenu eventChannelContextMenu;
        private MenuItem componentContextMenuItemDelete;
        private MenuItem componentContextMenuItemAddChannel;
        private MenuItem componentContextMenuItemConnectChannel;
        private MenuItem componentContextMenuItemDropChannel;
        private MenuItem componentContextMenuItemAddEventChannel;
        private MenuItem componentContextMenuItemConnectEventChannel;
        private MenuItem componentContextMenuItemDropEventChannel;
        private MenuItem componentContextMenuItemSolveConflict;

        private IniFile ini;

        private String saveFile = null;

        private model deploymentModel;
        private model copyModel;
        private ArrayList copyGroupEventChannels = new ArrayList();

        private AstericsLinkedList<componentType> selectedComponentList = new AstericsLinkedList<componentType>();
        //Modelcomponent which has keyboard focus  
        private componentType focusedComponent;

        private bool moveTracking = false;
        private componentType componentToMove;
        private channel channelToConnect;

        private channel focusedChannel;
        private AstericsLinkedList<channel> selectedChannelList = new AstericsLinkedList<channel>();

        private eventChannelLine eventChannelToConnect;
        private eventChannelLine focusedEventChannel;
        private AstericsLinkedList<eventChannelLine> selectedEventChannelList = new AstericsLinkedList<eventChannelLine>();

        private ArrayList eventChannelLinesList;
        private ArrayList eventChannelList;

        private Rectangle selectionRectangle;
        private Point selRectStartPoint;
        private const int selRectZIndex = -1000;

        private String backupIdForPropertyEditor;
        //private PropertyWindow pw; // needed, if the properties are shown in a seperate window

        // AsapiNet provides the connect/disconnect functions
        // Ater an estabilished connection, AsapiServer.Client handles the function calls
        private AsapiNetMain asapiNet;
        private AsapiServer.Client asapiClient;
        private AsapiServer.Client asapiStatusClient;
        // host and port of connection to ARE
        private int AREPort = 0;
        private String AREHostIP = "";
        private String AREHost = "";

        private AstericsStack<CommandObject> redoStack;
        private AstericsStack<CommandObject> undoStack;

        private FileLogTraceListener traceListener;
        private TraceSource traceSource;
        private SourceSwitch sourceSwitch;

        //AvalonDock variables
        private DockableContent dockableComponentProperties;
        private ScrollViewer propertyDockScrollViewer;
        private TreeView dockableInportsList;
        private TreeView dockableOutportsList;
        private DockableContent dockableInportsTab;
        private DockableContent dockableOutportsTab;
        private DockableContent dockableEventsTab;
        private DockableContent dockableEventListenerTab;
        private DockableContent dockableEventTriggerTab;

        private Grid dockEventGrid;
        private ScrollViewer eventTriggerDockScrollViewer;
        private ScrollViewer eventListenerDockScrollViewer;
        private DocumentContent scrollCanvars;

        // Visibility of dialogues
        private bool showNamingDialogOnComponentInsert;
        private bool showHostPortDialogOnConnect;
        private bool showEventChannelConnectMessage;
        public bool showAREConnectedMessage;
        public bool showOverrideModelQuestion;
        public bool showOverrideLocalModelQuestion;
        public bool showOverrideAtConnectionQuestion;
        public bool showOverrideAndRunAtConnectionQuestion;
        public bool showOverrideComponentCollectionQuestion;

        private AREStatus areStatus;
        private Rectangle inactiveCanvasRectangle; // The inactive rectangle over the canvas
        //private ComboBox componentsComboBox;

        // indicating the active property editor. this is needed, calling a refresh() after
        // an invalid input. the refresh is needed to restore the original, valid value
        private WPG.PropertyGrid activePropertyGrid;

        // ARE Storage variable
        private StorageDialog storageDialog;

        // storing all ARE-status objects
        List<StatusObject> statusList;

        // Thread to read the status of the ARE
        DispatcherTimer statusTimer = null;
        Timer focusTimer = null;
        // flag, if model has been edited
        private bool modelHasBeenEdited = false;        

        // Constants
        private static int MAXNUMBEROFRECENTFILES = 12;
        private const int TOOLTIP_SHOW_DURATION = 10000;
        
        private String activeBundle = "default";

        #endregion

        #region Construction / Initialization

        /// <summary>
        /// Constructor
        /// </summary>
        public MainWindow() {            

            // make an ACS folder in the AppData folder
            // uncomment for the final version
            // Also, change useAppDateFolder in asterics.ini !!!
            //if ((!Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\")) ||
            //    (!Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\")) ||
            //    (!Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\groups\\"))) {
            //    Directory.CreateDirectory(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\groups\\");
            //}
            //if (!Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\")) {
            //    Directory.CreateDirectory(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\");
            //}
          
            // uncomment for local use
            if (!Directory.Exists(AppDomain.CurrentDomain.BaseDirectory + "\\groups\\")) {
                Directory.CreateDirectory(AppDomain.CurrentDomain.BaseDirectory + "\\groups\\");
            }
            if (!Directory.Exists(AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\")) {
                Directory.CreateDirectory(AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\");
            }
            

            // loading the asterics.ini file, containing some basic settings
            // uncomment for the final version
            /*if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData)+"\\AsTeRICS\\ACS\\asterics.ini")) {
                ini = new IniFile(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\asterics.ini");
            } else */ if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            } else {
                MessageBox.Show(Properties.Resources.IniFileNotFoundText, Properties.Resources.IniFileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Application.Current.Shutdown();
            }

            // Setting the localisation info, if no value is set, the system value will be used.
            // More info about the used localisation technique can be found at:
            // http://www.codeproject.com/KB/WPF/WPFUsingLocbaml.aspx
            // Localisation should be done before initializing the components
            ACS.Properties.Resources.Culture = new System.Globalization.CultureInfo(ini.IniReadValue("Options", "language"));
            //ACS.Properties.Resources.Culture = new System.Globalization.CultureInfo("de-AT");
            // Read local language settings:
            //ACS.Properties.Resources.Culture = System.Threading.Thread.CurrentThread.CurrentCulture;
            InitializeComponent();
            
            Title = "AsTeRICS Configuration Suite " + ACS_VERSION;
            // Remove the original default trace listener and add a new one (for logging exceptions)
            traceListener = new FileLogTraceListener();
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {                
                traceListener.BaseFileName = Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\ACS";
            } else {
                traceListener.BaseFileName = "ACS";
            }
            traceListener.TraceOutputOptions = TraceOptions.DateTime | TraceOptions.ProcessId | TraceOptions.Callstack;
            traceListener.LogFileCreationSchedule = LogFileCreationScheduleOption.None;
            traceListener.CustomLocation = ".";
            traceListener.AutoFlush = true;
            traceListener.Append = false;
            traceListener.Delimiter = "\r\n";
            traceListener.Filter = new EventTypeFilter(SourceLevels.Error);
            traceSource = new TraceSource("mySource");
            sourceSwitch = new SourceSwitch("SourceSwitch", "Verbose");
            traceSource.Switch = sourceSwitch;
            traceSource.Listeners.Add(traceListener);

            this.Closing += MainWindow_Closing;

            // Adding mouse listeners to the drawing canvas
            canvas.MouseLeftButtonDown += OnLeftDown;
            canvas.MouseLeftButtonUp += OnLeftUp;
            canvas.MouseMove += OnMouseMove;

            // Context menu for the components
            componentContextMenu = new ContextMenu();

            // Channel related context menu entries
            componentContextMenuItemAddChannel = new MenuItem();
            componentContextMenuItemAddChannel.Header = Properties.Resources.ComponentContextMenuAddChannel;
            componentContextMenu.Items.Add(componentContextMenuItemAddChannel);
            componentContextMenuItemConnectChannel = new MenuItem();
            componentContextMenuItemConnectChannel.Header = Properties.Resources.ComponentContextMenuConnectChannel;
            // set inactive
            componentContextMenuItemAddChannel.IsEnabled = false; // making channels with ontext menu only when context menu is called by keyboard
            componentContextMenuItemConnectChannel.IsEnabled = false;
            componentContextMenu.Items.Add(componentContextMenuItemConnectChannel);
            componentContextMenuItemDropChannel = new MenuItem();
            componentContextMenuItemDropChannel.Header = Properties.Resources.ComponentContextMenuDropChannel;
            componentContextMenuItemDropChannel.IsEnabled = false;
            componentContextMenu.Items.Add(componentContextMenuItemDropChannel);
            componentContextMenuItemDropChannel.Click += ComponentContextMenuItemDrop_Click;

            componentContextMenu.Items.Add(new Separator());

            // Event Channel related context menu entries
            componentContextMenuItemAddEventChannel = new MenuItem();
            componentContextMenuItemAddEventChannel.Header = Properties.Resources.ComponentContextMenuAddEventChannel;
            componentContextMenuItemAddEventChannel.Click += ComponentContextMenuItemAddEvent_Click;
            componentContextMenu.Items.Add(componentContextMenuItemAddEventChannel);
            componentContextMenuItemConnectEventChannel = new MenuItem();
            componentContextMenuItemConnectEventChannel.Header = Properties.Resources.ComponentContextMenuConnectEventChannel;
            componentContextMenuItemConnectEventChannel.Click += ComponentContextMenuItemConnectEvent_Click;
            // set inactive
            componentContextMenuItemConnectEventChannel.IsEnabled = false;
            componentContextMenuItemAddEventChannel.IsEnabled = false; // making event channels with ontext menu only when context menu is called by keyboard
            componentContextMenu.Items.Add(componentContextMenuItemConnectEventChannel);
            componentContextMenuItemDropEventChannel = new MenuItem();
            componentContextMenuItemDropEventChannel.Header = Properties.Resources.ComponentContextMenuDropEventChannel;
            componentContextMenuItemDropEventChannel.IsEnabled = false;
            componentContextMenu.Items.Add(componentContextMenuItemDropEventChannel);
            componentContextMenuItemDropEventChannel.Click += ComponentContextMenuItemDropEvent_Click;

            componentContextMenu.Items.Add(new Separator());

            MenuItem componentContextMenuItemMove = new MenuItem();
            componentContextMenuItemMove.Header = Properties.Resources.ComponentContextMenuMoveComponent;
            componentContextMenuItemMove.Click += ComponentContextMenuItemMove_Click;
            componentContextMenu.Items.Add(componentContextMenuItemMove);

            componentContextMenuItemDelete = new MenuItem();
            componentContextMenuItemDelete.Header = Properties.Resources.ComponentContextMenuDeleteComponent;
            componentContextMenuItemDelete.Click += ComponentContextItemDelete_Click;
            componentContextMenu.Items.Add(componentContextMenuItemDelete);

            MenuItem componentContextItemProperties = new MenuItem();
            componentContextItemProperties.Header = Properties.Resources.ComponentContextMenuComponentProperties;
            componentContextItemProperties.Click += ComponentContextItemProperties_Click;
            componentContextMenu.Items.Add(componentContextItemProperties);

            MenuItem componentContextItemStatus = new MenuItem();
            componentContextItemStatus.Header = Properties.Resources.ComponentContextMenuComponentStatus;
            componentContextItemStatus.Click += ComponentContextItemStatus_Click;
            componentContextMenu.Items.Add(componentContextItemStatus);

            componentContextMenuItemSolveConflict = new MenuItem();
            componentContextMenuItemSolveConflict.Header = Properties.Resources.ComponentContextMenuRemoveConflict;
            componentContextMenuItemSolveConflict.Click += ComponentContextItemSolveConflict_Click;
            componentContextMenuItemSolveConflict.IsEnabled = false;
            componentContextMenu.Items.Add(componentContextMenuItemSolveConflict);
            
            componentContextMenu.Opened += ComponentContextMenu_Opened;

            // Context menu for the channels
            channelContextMenu = new ContextMenu();
            MenuItem channelContextMenuItemDelete = new MenuItem();
            channelContextMenuItemDelete.Header = Properties.Resources.ChannelContextMenuDeleteChannel;
            channelContextMenuItemDelete.Click += ChannelContextItemDelete_Click;
            channelContextMenu.Items.Add(channelContextMenuItemDelete);

            channelContextMenu.Opened += ChannelContextMenu_Opened;

            // Context menu for the event channels
            eventChannelContextMenu = new ContextMenu();

            MenuItem eventChannelContextMenuItemDelete = new MenuItem();
            eventChannelContextMenuItemDelete.Header = Properties.Resources.EventChannelContextMenuDeleteChannel;
            eventChannelContextMenuItemDelete.Click += EventChannelContextItemDelete_Click;
            eventChannelContextMenu.Items.Add(eventChannelContextMenuItemDelete);

            MenuItem eventChannelContextMenuItemEvents = new MenuItem();
            eventChannelContextMenuItemEvents.Header = Properties.Resources.EventChannelContextMenuSetEvents;
            eventChannelContextMenuItemEvents.Click += EventChannelContextItemSetEvents_Click;
            eventChannelContextMenu.Items.Add(eventChannelContextMenuItemEvents);

            eventChannelContextMenu.Opened += EventChannelContextMenu_Opened;

            // load all components to the ribbon menu
            LoadBundle(null);

            // initialize the model for drawing a new schema
            deploymentModel = new ACS.model();
            deploymentModel.channels = new channel[1];
            deploymentModel.eventChannels = new eventChannel[0];
            deploymentModel.components = new componentType[1];
            eventChannelLinesList = new ArrayList();
            eventChannelList = new ArrayList();
            NewAREGUIWindow();
            DateTime dt = DateTime.UtcNow;
            deploymentModel.modelName = dt.ToShortDateString() + "_" + dt.Hour + dt.Minute;

            // Set the keyboard navigation
            KeyboardNavigation.SetTabIndex(canvas, 1);
            KeyboardNavigation.SetTabNavigation(canvas, KeyboardNavigationMode.Cycle);

            asapiNet = new AsapiNetMain();

            redoStack = new AstericsStack<CommandObject>();
            undoStack = new AstericsStack<CommandObject>();
            undoStack.PropertyChanged += undoStack_PropertyChanged;
            redoStack.PropertyChanged += redoStack_PropertyChanged;
            autoCompleteTextBox.itemSelected += searchItemSelected;
            // Creating the AvalonDock (split window and properties on the right hand side)
            BuildDockingLayout();

            // init dummy element
            pasteDummyName += (char) 86;
            pasteDummyName += (char)101;
            pasteDummyName += (char)105;
            pasteDummyName += (char)103;
            pasteDummyName += (char)108;
            pasteDummyName += (char)49;
            pasteDummyName += (char)50;
            pasteDummyName += (char)51;
            pasteDummyName += (char)53;
            pasteDummyName += (char)56;
            // read options from ini-file
            String tmp = ini.IniReadValue("Options", "showNamingDialogOnComponentInsert");
            if (tmp.Equals("true")) {
                showNamingDialogOnComponentInsert = true;
            }
            else {
                showNamingDialogOnComponentInsert = false;
            }
            tmp = ini.IniReadValue("Options", "showHostPortDialogOnConnect");
            if (tmp.Equals("true")) {
                showHostPortDialogOnConnect = true;
            }
            else {
                showHostPortDialogOnConnect = false;
            }
            tmp = ini.IniReadValue("Options", "showEventChannelConnectMessage");
            if (tmp.Equals("true")) {
                showEventChannelConnectMessage = true;
            }
            else {
                showEventChannelConnectMessage = false;
            }
            tmp = ini.IniReadValue("Options", "showAREConnectedMessage");
            if (tmp.Equals("true")) {
                showAREConnectedMessage = true;
            }
            else {
                showAREConnectedMessage = false;
            }
            tmp = ini.IniReadValue("Options", "showOverrideModelQuestion");
            if (tmp.Equals("true")) {
                showOverrideModelQuestion = true;
            }
            else {
                showOverrideModelQuestion = false;
            }
            tmp = ini.IniReadValue("Options", "showOverrideLocalModelQuestion");
            if (tmp.Equals("true")) {
                showOverrideLocalModelQuestion = true;
            }
            else {
                showOverrideLocalModelQuestion = false;
            }
            tmp = ini.IniReadValue("Options", "showOverrideLocalWhenConnected");
            if (tmp.Equals("true")) {
                showOverrideAtConnectionQuestion = true;
            }
            else {
                showOverrideAtConnectionQuestion = false;
            }
            tmp = ini.IniReadValue("Options", "showOverrideAndRunLocalWhenConnected");
            if (tmp.Equals("true")) {
                showOverrideAndRunAtConnectionQuestion = true;
            }
            else {
                showOverrideAndRunAtConnectionQuestion = false;
            }
            tmp = ini.IniReadValue("Options", "showOverrideComponentCollectionQuestion");
            if (tmp.Equals("true")) {
                showOverrideComponentCollectionQuestion = true;
            }
            else {
                showOverrideComponentCollectionQuestion = false;
            }           

            // Eventhandler needed to read start parameter (start file after double-click)
            this.Loaded += new RoutedEventHandler(MainWindow_Loaded);

            areStatus = new AREStatus(); //AREStatus.Disconnected;
            areStatus.PropertyChanged += AreStatusChanged;
            statusBar.Text = Properties.Resources.AREStatusDisconnected;

            statusList = new List<StatusObject>();

            //showPortsRibbonButton.IsChecked = true;
            //showEventsRibbonButton.IsChecked = true;
            this.KeyDown += Global_KeyDown;
            canvas.MouseWheel += Zoom_MouseWheele;
            canvas.Focusable = true;
            canvas.Focus();

            //Adding event handlers to the selected Elemts lists.
            selectedComponentList.PropertyChanged += LinkedList_SizeChanged;
            selectedChannelList.PropertyChanged += LinkedList_SizeChanged;
            selectedEventChannelList.PropertyChanged += LinkedList_SizeChanged;
            // make sure a file for recently opened documents exists
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {                
                if (!File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt")) {
                    File.Create(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt");
                }
            } else {
                if (!File.Exists(AppDomain.CurrentDomain.BaseDirectory + "recent.txt")) {
                    File.Create(AppDomain.CurrentDomain.BaseDirectory + "recent.txt");
                }
            }
        }


        private void LinkedList_SizeChanged(object sender, PropertyChangedEventArgs e) {
            if ((selectedEventChannelList.Count == 0) && (selectedComponentList.Count == 0) && (selectedChannelList.Count == 0)) {
                deleteElementRibbonButton.IsEnabled = false;
                cutRibbonButton.IsEnabled = false;
                copyRibbonButton.IsEnabled = false;
                groupButton.IsEnabled = false;
                ungroupButton.IsEnabled = false;
                saveGroupButton.IsEnabled = false;
            } else {
                deleteElementRibbonButton.IsEnabled = true;
                cutRibbonButton.IsEnabled = true;
                copyRibbonButton.IsEnabled = true;
                groupButton.IsEnabled = true;
                ungroupButton.IsEnabled = true;
                saveGroupButton.IsEnabled = true;
            }
        }

        #endregion // Construction / Initialization

        #region Menu functionalities

        /// <summary>
        /// Establish a connection to the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Connect_Click(object sender, RoutedEventArgs e) {
            ConnectAREDialog connectDialog;
            bool doConnect = true;

            if (ini.IniReadValue("ARE", "enable_autodetection").Equals("true")) {
                // autodection of ARE
                int receivePort = int.Parse(ini.IniReadValue("ARE", "autodetect_receive_port"));
                int sendPort = int.Parse(ini.IniReadValue("ARE", "autodetect_send_port"));
                AutodetectARE autodet = new AutodetectARE(sendPort, receivePort);
                try {
                    autodet.Detect(this);
                }
                catch (Exception ex) {
                    //MessageBox.Show(Properties.Resources.ConnectAREErrorDialogText, Properties.Resources.ConnectAREErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    MessageBox.Show(Properties.Resources.AREAutodetectionException, Properties.Resources.ConnectAREErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message + "\n" + ex.StackTrace);
                    doConnect = false;
                }
                if (autodet.IpAdd.Equals("")) { // no ARE found
                    MessageBox.Show(Properties.Resources.AREAutodetectionNoARE, Properties.Resources.ConnectAREErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    doConnect = false;
                }
                else {
                    AREHostIP = autodet.IpAdd;
                    Int32.TryParse(ini.IniReadValue("ARE", "default_port"), out AREPort);
                    AREHost = autodet.Hostname;
                }
            }
            else { // manual connection with host IP

                // show connection dialog if set in options
                if (showHostPortDialogOnConnect) {
                    string nameErrorStr = "";
                    do {
                        connectDialog = new ConnectAREDialog();
                        connectDialog.Owner = this;
                        connectDialog.hostNameBox.Text = ini.IniReadValue("ARE", "default_host");
                        connectDialog.portNumberBox.Text = ini.IniReadValue("ARE", "default_port");
                        if (nameErrorStr != "") {
                            connectDialog.errorField.Text = nameErrorStr;
                        }
                        connectDialog.ShowDialog();

                        if (connectDialog.hostNameBox.Text.Length == 0) {
                            nameErrorStr = Properties.Resources.ConnectAREDialogHostEmpty;
                        }
                        else if (connectDialog.portNumberBox.Text.Length == 0) {
                            nameErrorStr = Properties.Resources.ConnectAREDialogPortEmpty;
                        }
                        else if (!Int32.TryParse(connectDialog.portNumberBox.Text, out AREPort)) {
                            nameErrorStr = Properties.Resources.ConnectAREDialogInvalidNumber;
                        }
                        else {
                            nameErrorStr = "";
                            AREHostIP = connectDialog.hostNameBox.Text;
                            AREHost = AREHostIP;
                        }
                        if (connectDialog.DialogResult == false) {
                            nameErrorStr = "";
                            doConnect = false;
                        }
                        showHostPortDialogOnConnect = (bool)connectDialog.showCheckbox.IsChecked;
                    } while (nameErrorStr != "");
                    if (showHostPortDialogOnConnect) {
                        ini.IniWriteValue("Options", "showHostPortDialogOnConnect", "true");
                    }
                    else {
                        ini.IniWriteValue("Options", "showHostPortDialogOnConnect", "false");
                    }
                }
                else {
                    AREHostIP = ini.IniReadValue("ARE", "default_host");
                    AREHost = AREHostIP;
                    if ((AREHostIP.Length == 0) || !Int32.TryParse(ini.IniReadValue("ARE", "default_port"), out AREPort)) {
                        doConnect = false;
                    }
                }
            }
            if (doConnect) {
                asapiClient = asapiNet.Connect(AREHostIP, AREPort, -1);
                if (asapiClient != null) {
                    if (showAREConnectedMessage) {
                        CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.AREConnectedDialogFormat(AREHostIP), Properties.Resources.AREConnectedDialogHeader,
                            CustomMessageBox.messageType.Info, CustomMessageBox.resultType.OK);
                        messageBox.Owner = this;
                        messageBox.showCheckbox.IsChecked = showAREConnectedMessage;
                        messageBox.ShowDialog();

                        showAREConnectedMessage = (bool)messageBox.showCheckbox.IsChecked;
                        if (showAREConnectedMessage) {
                            ini.IniWriteValue("Options", "showAREConnectedMessage", "true");
                        }
                        else {
                            ini.IniWriteValue("Options", "showAREConnectedMessage", "false");
                        }
                    }

                    areStatus.Status = AREStatus.ConnectionStatus.Connected;

                   // check, if a deployed model is ready on ARE
                   if (showOverrideAtConnectionQuestion)
                   {
                            //if (MessageBox.Show(Properties.Resources.AREStatusMessageDeployed, Properties.Resources.AREStatusMessageHeader, MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes) {
                            CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.AREStatusMessageDeployed, Properties.Resources.AREStatusMessageHeader, CustomMessageBox.messageType.Info, CustomMessageBox.resultType.YesNo);
                            messageBox.Owner = this;
                            messageBox.showCheckbox.IsChecked = showOverrideAtConnectionQuestion;
                            messageBox.ShowDialog();

                            bool dialogResult = (bool)messageBox.DialogResult;
                            if (dialogResult)
                            {
                                DownloadAndCheckModel();
                            }
                            showOverrideAtConnectionQuestion = (bool)messageBox.showCheckbox.IsChecked;
                            if (showOverrideAtConnectionQuestion)
                            {
                                ini.IniWriteValue("Options", "showOverrideLocalWhenConnected", "true");
                            }
                            else
                            {
                                ini.IniWriteValue("Options", "showOverrideLocalWhenConnected", "false");
                                if (dialogResult)
                                    ini.IniWriteValue("Options", "downloadModelOnConnect", "true");
                                else
                                    ini.IniWriteValue("Options", "downloadModelOnConnect", "false");
                            }
                        }
                        else
                        {
                            if (ini.IniReadValue("Options", "downloadModelOnConnect") == "true")
                            {
                                DownloadAndCheckModel();
                            }
                    }
                }
                else {
                    MessageBox.Show(Properties.Resources.ConnectAREErrorDialogText, Properties.Resources.ConnectAREErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, Properties.Resources.ConnectAREErrorDialogText);
                }
            }
        }


        /// <summary>
        /// Close the connection to the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Disconnect_Click(object sender, RoutedEventArgs e) {
            try {                
                asapiNet.Disconnect(asapiClient);
                AREHostIP = "";
                AREPort = 0;
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.AREDisconnectErrorDialog, Properties.Resources.AREDisconnectErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }
            asapiClient = null;
            statusList.Clear();
            areStatus.Status = AREStatus.ConnectionStatus.Disconnected;
        }

        private void MidiToneCreatorRibbonButton_Click(object sender, RoutedEventArgs e) {
            if (File.Exists(ini.IniReadValue("Options", "pathToMidiCreationTool"))) {
                System.Diagnostics.Process.Start(ini.IniReadValue("Options", "pathToMidiCreationTool"));
            }
            else {
                MessageBox.Show(Properties.Resources.ExternalToolFileNotFound, Properties.Resources.FileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }


        /// <summary>
        /// Upload a deployment schema from ACS to ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void UploadSchema_Click(object sender, RoutedEventArgs e) {
            bool isError = false;
            bool doOverride = false;

            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }
            if (deploymentComponentList.Count == 0) {
                MessageBox.Show(Properties.Resources.EmptyModel, Properties.Resources.EmptyModelHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            } else {

                String mustBeConnectedError = MustBeConnectedChecker();
                if (mustBeConnectedError != "") {
                    MessageBox.Show(mustBeConnectedError, Properties.Resources.MustBeConnectedCheckerHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                } else {
                    if (showOverrideModelQuestion) {
                        CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.OverrideAREDialog, Properties.Resources.OverrideDialogHeader, CustomMessageBox.messageType.Warning, CustomMessageBox.resultType.YesNo);
                        messageBox.Owner = this;
                        messageBox.showCheckbox.IsChecked = showOverrideModelQuestion;
                        messageBox.ShowDialog();

                        showOverrideModelQuestion = (bool)messageBox.showCheckbox.IsChecked;
                        if (showOverrideModelQuestion) {
                            ini.IniWriteValue("Options", "showOverrideModelQuestion", "true");
                        } else {
                            ini.IniWriteValue("Options", "showOverrideModelQuestion", "false");
                        }
                        doOverride = (bool)messageBox.DialogResult;
                    } else {
                        doOverride = true;
                    }

                    if (doOverride) {

                        // Validation: the schema can not be sent to ARE, if the components are not available there. 
                        // Just the name of the components will be checked, not the data/version

                        // uncomment, when asapiClient.GetAvailableComponentTypes() is implemented
                        List<String> availableComponents;

                        model deploymentModelWithoutGroups = RemoveGroupingElementsInDeployment(deploymentModel);
                        try {

                            availableComponents = asapiClient.GetAvailableComponentTypes();
                            foreach (componentType comp in deploymentModelWithoutGroups.components) {
                                if (!availableComponents.Contains(comp.type_id)) {
                                    MessageBox.Show(Properties.Resources.SynchronisationComponentNotAvailableErrorFormat(comp.type_id), Properties.Resources.SynchronisationErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                    isError = true;
                                }
                            }
                        } catch (Exception ex) {
                            MessageBox.Show(Properties.Resources.SynchronisationAvailableComponentsError, Properties.Resources.SynchronisationErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                            isError = true;
                            CheckASAPIConnection();
                        }

                        if (!isError) {
                            try {
                                if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 0)) {
                                    deploymentModel.eventChannels = null;
                                } else if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 1) && (deploymentModel.eventChannels[0] == null)) {
                                    deploymentModel.eventChannels = null;
                                }

                                // validation of the model before sending it to the ARE
                                // model should be valid, this is a double-check
                                XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
                                // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
                                FileStream strVal = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
                                x.Serialize(strVal, deploymentModelWithoutGroups);
                                strVal.Close();

                                // check, if model is valid against the deployment_model schema
                                String xmlError;
                                XmlValidation xv = new XmlValidation();
                                xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

                                // if valid, xml-file will be written
                                if (xmlError.Equals("")) {
                                    x = new XmlSerializer(deploymentModel.GetType());
                                    StringWriter str = new StringWriter();
                                    x.Serialize(str, deploymentModelWithoutGroups);
                                    asapiClient.DeployModel(str.ToString());

                                    areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                                } else {
                                    MessageBox.Show(Properties.Resources.XmlValidErrorText, Properties.Resources.XmlValidErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                    traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                                }
                            } catch (Exception ex) {
                                MessageBox.Show(Properties.Resources.SynchronisationUploadError, Properties.Resources.SynchronisationUploadErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                                CheckASAPIConnection();
                            }
                        }
                    }
                }
            }
        }

        private model RemoveGroupingElementsInDeployment(model modelToClean) {

            model retModel = CopyModel(modelToClean);

            LinkedList<componentType> retModelComponentList = new LinkedList<componentType>();
            LinkedList<channel> retModelChannelList = new LinkedList<channel>();
            LinkedList<eventChannel> retModelEventChannelList = new LinkedList<eventChannel>();

            foreach (componentType component in retModel.components) {
                retModelComponentList.AddLast(component);
            }

            if (retModel.channels != null) {
                foreach (channel ch in retModel.channels) {
                    retModelChannelList.AddLast(ch);
                }
            }

            if (retModel.eventChannels != null) {
                foreach (eventChannel ec in retModel.eventChannels) {
                    retModelEventChannelList.AddLast(ec);
                }
            }

            foreach (componentType component in retModel.components) {
                if (component.ComponentType == ACS2.componentTypeDataTypes.group) {
                    if (retModel.channels != null) {
                        foreach (channel ch in retModel.channels) {
                            if ((ch.source.component.id == component.id) || (ch.target.component.id == component.id)) {
                                retModelChannelList.Remove(ch);
                            }
                        }
                    }


                    if (retModel.eventChannels != null) {
                        foreach (eventChannel ec in retModel.eventChannels) {
                            if ((ec.sources.source.component.id == component.id) || (ec.targets.target.component.id == component.id)) {
                                retModelEventChannelList.Remove(ec);
                            }
                        }
                    }
                    retModelComponentList.Remove(component);

                }
            }
            if (retModelChannelList.Count == 0) {
                retModel.channels = null;
            }
            else {
                retModel.channels = retModelChannelList.ToArray();
            }
            if (retModelEventChannelList.Count == 0) {
                retModel.eventChannels = null;
            }
            else {
                retModel.eventChannels = retModelEventChannelList.ToArray();
            }
            retModel.components = retModelComponentList.ToArray();

            return retModel;
        }

        /// <summary>
        /// Download and install the bundle from the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DownloadBundles_Click(object sender, RoutedEventArgs e) {

            Boolean doOverride = true;
            if (showOverrideComponentCollectionQuestion) {
                CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.BundleDownloadFromAREQuestion, Properties.Resources.BundleDownloadFromAREQuestionHeader, CustomMessageBox.messageType.Question, CustomMessageBox.resultType.YesNo);
                messageBox.Owner = this;
                messageBox.showCheckbox.IsChecked = showOverrideComponentCollectionQuestion;
                messageBox.ShowDialog();

                showOverrideComponentCollectionQuestion = (bool)messageBox.showCheckbox.IsChecked;
                if (showOverrideComponentCollectionQuestion) {
                    ini.IniWriteValue("Options", "showOverrideComponentCollectionQuestion", "true");
                } else {
                    ini.IniWriteValue("Options", "showOverrideComponentCollectionQuestion", "false");
                }
                doOverride = (bool)messageBox.DialogResult;
            }

            if (doOverride) {

                bool cont = true;
                if (modelHasBeenEdited) {
                    SaveQuestionDialog saveQuestion = new SaveQuestionDialog();
                    saveQuestion.Owner = this;
                    saveQuestion.ShowDialog();

                    // Process message box results
                    switch (saveQuestion.Result) {
                        case SaveQuestionDialog.save:
                            if (SaveLocalCommand(false) == true) {
                                CleanACS();
                                modelHasBeenEdited = false;
                            }
                            break;
                        case SaveQuestionDialog.dontSave:
                            CleanACS();
                            modelHasBeenEdited = false;
                            break;
                        case SaveQuestionDialog.cancel:
                            cont = false;
                            break;
                    }
                } else {
                    CleanACS();
                }
                if (cont) {

                    SetSaveFile(null);
                    deploymentModel.modelName = GenerateModelName();

                    try {
                        List<String> allBundles = asapiClient.getBundleDescriptors();
                        StreamWriter tempfile = new StreamWriter(System.IO.Path.GetTempPath() + "tempBundle.xml", false, System.Text.Encoding.ASCII);
                        tempfile.Write("<?xml version=\"1.0\"?>");
                        tempfile.Write("<componentTypes xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
                        String tempStr;
                        foreach (string str in allBundles) {
                            tempStr = str.Remove(0, str.IndexOf("<componentType ") - 1);
                            tempStr = tempStr.Remove(tempStr.IndexOf("</componentTypes>"), 17);
                            tempfile.WriteLine(tempStr);
                            //Console.WriteLine(str);               
                        }

                        tempfile.Write("</componentTypes>");

                        tempfile.Close();
                        componentList.Clear();
                        LoadBundle(System.IO.Path.GetTempPath() + "tempBundle.xml");  // ini.IniReadValue("model", "tempfile"));
                        activeBundle = AREHost;
                    } catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.DownloadBundleError, Properties.Resources.DownloadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                        CheckASAPIConnection();
                    }
                }
            }
        }

        /// <summary>
        /// Downlaod a deployment schema from the ARE to the ACS
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DownloadSchema_Click(object sender, RoutedEventArgs e) {

            bool doOverride = true;
            if ((undoStack.Count > 0 || redoStack.Count > 0) && (showOverrideLocalModelQuestion)) {
                CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.OverrideACSDialog, Properties.Resources.OverrideDialogHeader, CustomMessageBox.messageType.Warning, CustomMessageBox.resultType.YesNo);
                messageBox.Owner = this;
                messageBox.showCheckbox.IsChecked = showOverrideLocalModelQuestion;
                messageBox.ShowDialog();

                showOverrideLocalModelQuestion = (bool)messageBox.showCheckbox.IsChecked;
                if (showOverrideLocalModelQuestion) {
                    ini.IniWriteValue("Options", "showOverrideLocalModelQuestion", "true");
                }
                else {
                    ini.IniWriteValue("Options", "showOverrideLocalModelQuestion", "false");
                }
                doOverride = (bool)messageBox.DialogResult;
            }
            if (doOverride) {
                try {
                    String xmlModel = asapiClient.GetModel();
                    if (xmlModel != null && xmlModel != "") {

                        XmlSerializer ser2 = new XmlSerializer(typeof(model));
                        StringReader sr2 = new StringReader(xmlModel);
                        deploymentModel = (model)ser2.Deserialize(sr2);
                        
                        sr2.Close();

                        // Validate, if downlaoded schema is valid
                        // Should be valid, is double-check
                        XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
                        // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
                        FileStream str = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
                        x.Serialize(str, deploymentModel);
                        str.Close();

                        // check, if model is valid against the deployment_model schema
                        String xmlError;
                        XmlValidation xv = new XmlValidation();
                        xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

                        // if valid, xml-file will be written
                        if (xmlError.Equals("")) {
                            
                            ResetPropertyDock();
                            ModelVersionUpdater.UpdateMissingGUI(this, deploymentModel, componentList);
                            ModelVersionUpdater.UpdateToCurrentVersion(this, deploymentModel);
                            LoadComponentsCommand();
                            areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                        }
                        else {
                            MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                        }
                    }
                    else {
                        CleanACS();
                    }
                }
                catch (Exception ex) {
                    MessageBox.Show(Properties.Resources.SynchronisationDownloadError, Properties.Resources.SynchronisationDownloadErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    CleanACS();
                    CheckASAPIConnection();
                }
                SetSaveFile(null);
            }
        }


        /// <summary>
        /// Name dialog to edit the name of the model
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EditModelNameRibbonButton_Click(object sender, RoutedEventArgs e) {
            NameDialog nd = new NameDialog();
            nd.showCheckbox.Visibility = Visibility.Collapsed;
            nd.Title = Properties.Resources.EditModelNameWindow;
            nd.nameFieldLabel.Content = Properties.Resources.EditModelNameLabel;
            nd.Owner = this;
            nd.componentNameBox.Text = deploymentModel.modelName;
            nd.ShowDialog();

            if (nd.DialogResult == true) {
                deploymentModel.modelName = nd.componentNameBox.Text;
                modelHasBeenEdited = true;
                UpdateToolTips();
            }
        }

        /// <summary>
        /// Dialog to show and edit the model description
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EditModelDescriptionRibbonButton_Click(object sender, RoutedEventArgs e) {
            ModelDescriptionDialog mdd = new ModelDescriptionDialog();
            mdd.Owner = this;
            if (deploymentModel.modelDescription != null) {
                mdd.shortDescriptionText.Text = deploymentModel.modelDescription.shortDescription;
                mdd.requirementsText.Text = deploymentModel.modelDescription.requirements;
                mdd.logDecriptionText.Text = deploymentModel.modelDescription.description;
            }
            mdd.ShowDialog();
            if (mdd.DialogResult == true) {
                if (deploymentModel.modelDescription == null) {
                    deploymentModel.modelDescription = new modelDescriptionType();
                }
                deploymentModel.modelDescription.shortDescription = mdd.shortDescriptionText.Text;
                deploymentModel.modelDescription.requirements = mdd.requirementsText.Text;
                deploymentModel.modelDescription.description = mdd.logDecriptionText.Text;
                modelHasBeenEdited = true;
                UpdateToolTips();
            }
        }

        /// <summary>
        /// Executes a cut-command on all selected components
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Cut_Click(object sender, RoutedEventArgs e) {
            CopySelectedCommand();
            DeleteSelectedComponents();
            pasteRibbonButton.IsEnabled = true;
        }

        /// <summary>
        /// Copies all selected components to a temporary list
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Copy_Click(object sender, RoutedEventArgs e) {
            CopySelectedCommand();
            pasteRibbonButton.IsEnabled = true;
        }

        /// <summary>
        /// Pastes all elements of a temporary copy list to the model
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Paste_Click(object sender, RoutedEventArgs e) {
            model tmpModel = CopyModel(copyModel);
            PasteCopiedModel(copyModel, false,true);
            copyModel = tmpModel;
        }

        /// <summary>
        /// Creates a new schema: Clean the canvas (with CleanACS()) and reset the deployment
        /// model in the background. Open save dialog, if requested
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void NewSchema_Click(object sender, RoutedEventArgs e) {
            if (modelHasBeenEdited) {
                SaveQuestionDialog saveQuestion = new SaveQuestionDialog();
                saveQuestion.Owner = this;
                saveQuestion.ShowDialog();

                // Process message box results
                switch (saveQuestion.Result) {
                    case SaveQuestionDialog.save:
                        if (SaveLocalCommand(false) == true) {
                            CleanACS();
                            modelHasBeenEdited = false;
                        }
                        break;
                    case SaveQuestionDialog.dontSave:
                        CleanACS();
                        modelHasBeenEdited = false;
                        break;
                    case SaveQuestionDialog.cancel:
                        break;
                }
            }
            else {
                CleanACS();
            }
            SetSaveFile(null);
            deploymentModel.modelName = GenerateModelName();

            if (areStatus.Status == AREStatus.ConnectionStatus.Synchronised) {
                areStatus.Status = AREStatus.ConnectionStatus.Connected;
            }
            else if ((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause)) {
                areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                areStatus.Status = AREStatus.ConnectionStatus.Connected;
            }
        }


        /// <summary>
        /// Save local: firstly check, if all ports that must be connected are connected.
        /// Then call the SaveLocalCommand(bool saveAs)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveLocal_Click(object sender, RoutedEventArgs e) {
            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            String mustBeConnectedError = MustBeConnectedChecker();
            if (mustBeConnectedError != "") {
                MessageBox.Show(mustBeConnectedError, Properties.Resources.MustBeConnectedCheckerHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
            else {
                // if no file for saving is defined yet, show the saveas-dialog
                if (saveFile != null) {
                    SaveLocalCommand(false);
                }
                else {
                    SaveLocalCommand(true);
                }
            }
        }

        /// <summary>
        /// Save local as: firstly check, if all ports that must be connected are connected.
        /// Then call the SaveLocalCommand(bool saveAs)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void SaveLocalAs_Click(object sender, RoutedEventArgs e) {
            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            String mustBeConnectedError = MustBeConnectedChecker();
            if (mustBeConnectedError != "") {
                MessageBox.Show(mustBeConnectedError, Properties.Resources.MustBeConnectedCheckerHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
            else {
                SaveLocalCommand(true);
            }
        }

        /// <summary>
        /// Load a local deployment model to the drawing board. Firstly check, if the actual model
        /// needs to be saved.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OpenLocal_Click(object sender, RoutedEventArgs e) {
            CheckIfSavedAndOpenCommand(null);
        }


        /// <summary>
        /// Storing the active model of the canvas into the ARE storage
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void StoreModelOnARE_Click(object sender, RoutedEventArgs e) {
            List<string> storedModels = null;
            // load the list of all stored models
            try {
                storedModels = asapiClient.listAllStoredModels();
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.LoadStoredModelsListError, Properties.Resources.LoadStoredModelsListErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            // Show the list of all stored models
            if (storedModels != null) {
                storageDialog = new StorageDialog();

                foreach (string s in storedModels) {
                    if (s.EndsWith(".acs")) {
                        storageDialog.filenameListbox.Items.Add(s);
                    }
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.StoreModelButton;
                if (saveFile != null) {
                    storageDialog.filenameTextbox.Text = saveFile.Substring(saveFile.LastIndexOf('\\') + 1);
                }
                else {
                    storageDialog.filenameTextbox.Text = "NewModel.acs";
                }
                storageDialog.Owner = this;
                storageDialog.ShowDialog();
                string filename = storageDialog.filenameTextbox.Text;
                if ( filename != null && filename != "") {
                    try {
                        if (filename.ToLower().EndsWith(".acs") == false)
                            filename += ".acs";
                        asapiClient.storeModel(ConvertDeploymentModelToValidString(), filename);
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.StoreModelOnAREError, Properties.Resources.StoreModelOnAREErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            }
        }

        /// <summary>
        /// Storing the model on the canvas on the ARE storage and set it as auto start (will be started automatically
        /// when the ARE will be started)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AutostartModel_Click(object sender, RoutedEventArgs e) {
            try {
                asapiClient.storeModel(ConvertDeploymentModelToValidString(), "autostart.acs");
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.StoreModelOnAREError, Properties.Resources.StoreModelOnAREErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }
        }

        /// <summary>
        /// Delete a model on the ARE storage
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void DeleteModelOnStorage_Click(object sender, RoutedEventArgs e) {
            List<string> storedModels = null;
            try {
                storedModels = asapiClient.listAllStoredModels();
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.LoadStoredModelsListError, Properties.Resources.LoadStoredModelsListErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            if (storedModels != null) {
                storageDialog = new StorageDialog();

                foreach (string s in storedModels) {
                    if (s.EndsWith(".acs")) {
                        storageDialog.filenameListbox.Items.Add(s);
                    }
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.DeleteStoredModelButton;
                storageDialog.Owner = this;
                storageDialog.filenameTextbox.IsEnabled = false;
                storageDialog.ShowDialog();

                if (storageDialog.filenameTextbox.Text != null && storageDialog.filenameTextbox.Text != "") {
                    try {
                        asapiClient.deleteModelFile(storageDialog.filenameTextbox.Text);
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.DeleteModelOnAREError, Properties.Resources.DeleteModelOnAREErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            }
        }

        /// <summary>
        /// Loading a model from the storage of the ARE to the ACS (to the canvas)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void LoadModelFromStorage_Click(object sender, RoutedEventArgs e) {
            List<string> storedModels = null;
            try {
                storedModels = asapiClient.listAllStoredModels();
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.LoadStoredModelsListError, Properties.Resources.LoadStoredModelsListErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            if (storedModels != null) {
                storageDialog = new StorageDialog();

                foreach (string s in storedModels) {
                    if (s.EndsWith(".acs")) {
                        storageDialog.filenameListbox.Items.Add(s);
                    }
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.LoadModelFromStorageButton;
                storageDialog.Owner = this;
                storageDialog.filenameTextbox.IsEnabled = false;
                storageDialog.ShowDialog();

                if (storageDialog.filenameTextbox.Text != null && storageDialog.filenameTextbox.Text != "") {
                    try {
                        string storedModel = asapiClient.getModelFromFile(storageDialog.filenameTextbox.Text);

                        if (storedModel != null && storedModel != "") {

                            XmlSerializer ser2 = new XmlSerializer(typeof(model));
                            StringReader sr2 = new StringReader(storedModel);
                            deploymentModel = (model)ser2.Deserialize(sr2);
                            sr2.Close();

                            // Validate, if downlaoded schema is valid
                            // Should be valid, is double-check
                            XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
                            // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
                            FileStream str = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
                            x.Serialize(str, deploymentModel);
                            str.Close();

                            // check, if model is valid against the deployment_model schema
                            String xmlError;
                            XmlValidation xv = new XmlValidation();
                            xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

                            // if valid, xml-file will be written
                            if (xmlError.Equals("")) {
                                LoadComponentsCommand();
                                SetSaveFile(storageDialog.filenameTextbox.Text);
                            }
                            else {
                                deploymentModel = null;
                                MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                            }

                        }
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.LoadStoredModelError, Properties.Resources.LoadStoredModelErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            }
        }

        /// <summary>
        /// Activating (setting to the run modus) a stored model of the ARE storage and downloading it to the ACS
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ActivateStoredModel_Click(object sender, RoutedEventArgs e) {
            List<string> storedModels = null;
            try {
                storedModels = asapiClient.listAllStoredModels();
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.LoadStoredModelsListError, Properties.Resources.LoadStoredModelsListErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            if (storedModels != null) {
                storageDialog = new StorageDialog();

                foreach (string s in storedModels) {
                    if (s.EndsWith(".acs")) {
                        storageDialog.filenameListbox.Items.Add(s);
                    }
                }
                storageDialog.filenameListbox.SelectionChanged += filenameListbox_SelectionChanged;
                storageDialog.Title = Properties.Resources.ActivateStoredModelButton;
                storageDialog.Owner = this;
                storageDialog.filenameTextbox.IsEnabled = false;
                storageDialog.ShowDialog();

                if (storageDialog.filenameTextbox.Text != null && storageDialog.filenameTextbox.Text != "") {
                    try {
                        string storedModel = asapiClient.getModelFromFile(storageDialog.filenameTextbox.Text);
                        if (storedModel != null && storedModel != "") {

                            XmlSerializer ser2 = new XmlSerializer(typeof(model));
                            StringReader sr2 = new StringReader(storedModel);
                            deploymentModel = (model)ser2.Deserialize(sr2);
                            sr2.Close();

                            // Validate, if downlaoded schema is valid
                            // Should be valid, is double-check
                            XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
                            // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
                            FileStream str = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
                            x.Serialize(str, deploymentModel);
                            str.Close();

                            // check, if model is valid against the deployment_model schema
                            String xmlError;
                            XmlValidation xv = new XmlValidation();
                            xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

                            // if valid, xml-file will be written
                            if (xmlError.Equals("")) {
                                LoadComponentsCommand();
                                SetSaveFile(storageDialog.filenameTextbox.Text);
                                asapiClient.DeployFile(storageDialog.filenameTextbox.Text);
                                areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                                asapiClient.RunModel();
                                areStatus.Status = AREStatus.ConnectionStatus.Running;
                            }
                            else {
                                deploymentModel = null;
                                MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                            }

                        }
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.ActivateStoredModelError, Properties.Resources.ActivateStoredModelErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            }
        }

        /// <summary>
        /// Close Window with X-symbol on top right corner of the window. Just calls CloseCommand(object eventObject)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void MainWindow_Closing(object sender, CancelEventArgs e) {
            CloseCommand(e);
        }

        /// <summary>
        /// Close Window from menu, just calls CloseCommand(object eventObject)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void CloseApplication_Click(object sender, RoutedEventArgs e) {
            CloseCommand(e);
        }

        /// <summary>
        /// Open the Options dialog (OptionsDialog)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Options_Click(object sender, RoutedEventArgs e) {
            OptionsDialog optionsDialog;
            optionsDialog = new OptionsDialog(this);
            optionsDialog.Owner = this;
            optionsDialog.ShowDialog();
        }

        /// <summary>
        /// Open the About dialog
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void About_Click(object sender, RoutedEventArgs e) {
            About aboutDialog = new About(asapiNet.getAsapiVersion());
            aboutDialog.Owner = this;
            aboutDialog.ShowDialog();
        }

        // Dummy function for unimplemented menu entries
        private void OnIgnore(object sender, RoutedEventArgs e) {
            Console.WriteLine("OnIgnore from sender: " + e.Source.ToString());
        }

        /// <summary>
        /// Restore or better load the layout, will be called when the application starts
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void RestoreLayout(object sender, RoutedEventArgs e) {
            try {
                if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                    if (File.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\" + ini.IniReadValue("Layout", "layout_file"))) {
                        dockManager.RestoreLayout(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\" + ini.IniReadValue("Layout", "layout_file"));
                    }
                } else {
                    if (File.Exists(ini.IniReadValue("Layout", "layout_file"))) {
                        dockManager.RestoreLayout(ini.IniReadValue("Layout", "layout_file"));
                    } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("Layout", "layout_file"))) {
                        dockManager.RestoreLayout(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("Layout", "layout_file"));
                    }
                }
                dockManager.ActiveContent = scrollCanvars;
            } catch (Exception ex) {
                MessageBox.Show(Properties.Resources.LayoutRestoreError, Properties.Resources.LayoutRestoreError, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }
        }

        /// <summary>
        /// Load the default layout (the AvalonDock layout)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        public void RestoreDefaultLayout(object sender, RoutedEventArgs e) {
            if (File.Exists(ini.IniReadValue("Layout", "default_layout_file"))) {
                dockManager.RestoreLayout(ini.IniReadValue("Layout", "default_layout_file"));
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("Layout", "default_layout_file"))) {
                dockManager.RestoreLayout(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("Layout", "default_layout_file"));
            }            
        }

        /// <summary>
        /// Creating (drawing) a new, presaved group out of a menu selection (RibbonDropDown)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AddGroupFromRibbonMenu(object sender, RoutedEventArgs e) {
            MenuItem mi = (MenuItem)e.Source;
            // check if the file is valid against the deployment_schema
            try {
                String xmlError;
                XmlValidation xv = new XmlValidation();
                xmlError = xv.validateXml((string)mi.CommandParameter, ini.IniReadValue("model", "deployment_schema"));

                if (xmlError.Equals("")) {
                    XmlSerializer ser2 = new XmlSerializer(typeof(model));
                    StreamReader sr2 = new StreamReader((string)mi.CommandParameter);
                    model groupToPaste = (model)ser2.Deserialize(sr2);
                    sr2.Close();

                    foreach (componentType ct in groupToPaste.components) {
                        bool found = false;
                        if (((Asterics.ACS2.componentTypesComponentType)componentList[ct.type_id]).singleton) {
                            foreach (componentType ct1 in deploymentComponentList.Values) {
                                if (ct1.type_id == ct.type_id) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                MessageBox.Show(Properties.Resources.SingletonErrorHeaderFormat(ct.type_id), Properties.Resources.SingletonErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Warning);
                                return;
                            }
                        }
                    }

                    if (groupToPaste.groups == null) {
                        throw new Exception("group element missing");
                    }

                    // adapt names in the component, so they can be added without renaming in the PasteCopiedModel method
                    // change id
                    Dictionary<string, string> changedComponents = new Dictionary<string, string>();
                    foreach (componentType modelComp in groupToPaste.components) {
                        bool namevalid = false;
                        int i = 1;
                        while (namevalid == false) {
                            string modelID = modelComp.id + "." + i;
                            bool foundInPasteGroup = false;
                            foreach (componentType ct in groupToPaste.components) {
                                if (ct.id == modelID) {
                                    foundInPasteGroup = true;
                                    break;
                                }
                            }
                            if (!deploymentComponentList.ContainsKey(modelID) && !foundInPasteGroup) {
                                if (groupToPaste.channels != null) {
                                    foreach (channel c in groupToPaste.channels) {
                                        if (c.source.component.id == modelComp.id) {
                                            c.source.component.id = modelID;
                                        }
                                        if (c.target.component.id == modelComp.id) {
                                            c.target.component.id = modelID;
                                        }
                                    }
                                }
                                if (groupToPaste.eventChannels != null) {
                                    foreach (eventChannel ec in groupToPaste.eventChannels) {
                                        if (ec.sources.source.component.id == modelComp.id)
                                            ec.sources.source.component.id = modelID;
                                        if (ec.targets.target.component.id == modelComp.id)
                                            ec.targets.target.component.id = modelID;
                                    }
                                }
                                changedComponents.Add(modelComp.id, modelID);
                                modelComp.id = modelID;
                                namevalid = true;
                            } else
                                i++;
                        }
                    }
                    // adapting the alias to the new group port names
                    if (groupToPaste.groups[0].portAlias != null) {
                        foreach (portAlias alias in groupToPaste.groups[0].portAlias) {
                            foreach (string oldCompName in changedComponents.Keys) {
                                if (alias.portId.Contains(oldCompName)) {
                                    alias.portId = alias.portId.Replace(oldCompName, changedComponents[oldCompName]);
                                    break;
                                }
                            }
                        }
                    }

                    //group[] backupGroups = groupToPaste.groups;
                    //groupToPaste.groups = null;

                    // add dummy element
                    AddDummyToModel(pasteDummyName);
                    
                    PasteCopiedModel(groupToPaste, true,false);

                    ClearSelectedComponentList();
                    foreach (string compId in changedComponents.Values) {
                        AddSelectedComponent(deploymentComponentList[compId]);
                    }

                    // generate ID for new group
                    int counter = 0;
                    string compName = "";
                    do {
                        counter++;
                        compName = groupToPaste.groups[0].id + "." + counter;
                        compName = TrimComponentName(compName);
                    } while (deploymentComponentList.ContainsKey(compName));


                    string newGroupID = DoGrouping(compName, false, true);
                    
                    RemoveDummyFromModel(pasteDummyName);

                    if (groupsList.ContainsKey(newGroupID)) {
                        CommandObject co = new CommandObject();
                        co.Command = "Delete";
                        co.InvolvedObjects.Add(groupsList[newGroupID]);
                        undoStack.Push(co);
                        redoStack.Clear();
                    }
                    componentType groupToUpdate = deploymentComponentList[compName];
                    // set the alias
                    if (groupToPaste.groups[0].portAlias != null) {
                        foreach (portAlias alias in groupToPaste.groups[0].portAlias) {
                            foreach (object port in groupToUpdate.ports) {
                                if ((port is inputPortType) && (((inputPortType)port).portTypeID == alias.portId)) {
                                    ((inputPortType)port).PortAliasForGroups = alias.portAlias1;
                                    ((inputPortType)port).PortLabel.Text = alias.portAlias1;
                                    break;
                                } else if ((port is outputPortType) && (((outputPortType)port).portTypeID == alias.portId)) {
                                    ((outputPortType)port).PortAliasForGroups = alias.portAlias1;
                                    ((outputPortType)port).PortLabel.Text = alias.portAlias1;
                                    break;
                                }
                            }
                        }
                    }
                    groupToUpdate.description = groupToPaste.groups[0].description;

                    
                    // store the group in the model

                    /*
                    group[] tempGroups = deploymentModel.groups;

                    if (tempGroups == null) {
                        tempGroups = new group[1];
                    } else {
                        Array.Resize(ref tempGroups, deploymentModel.groups.Count() + 1);
                    }
                    deploymentModel.groups = tempGroups;

                    //groupToPaste.groups[0].id = compName;
                    //deploymentModel.groups[deploymentModel.groups.Count() - 1] = groupToPaste.groups[0];
                    
                    group groupForDeployment = new group();
                    groupForDeployment.id = compName;
                    groupForDeployment.componentId = new string[changedComponents.Count];
                    int i2 = 0;
                    foreach (string key in changedComponents.Values) {
                        groupForDeployment.componentId[i2] = key;
                        i2++;
                    }
                    groupForDeployment.portAlias = groupToPaste.groups[0].portAlias;
                    deploymentModel.groups[deploymentModel.groups.Count() - 1] = groupForDeployment;
*/

                } else {
                    throw new Exception("group element missing");
                }
            } catch (Exception ex) {
                MessageBox.Show(Properties.Resources.GroupingErrorReadingGroupFile, Properties.Resources.GroupingErrorReadingGroupHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

        }
        
        /// <summary>
        /// Creating (drawing) a new component out of a menu selection (RibbonDropDown)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void AddComponentFromRibbonMenu(object sender, RoutedEventArgs e) {
            MenuItem mi = (MenuItem)e.Source;
            AddComponent((string)mi.CommandParameter, false,true,true);

        }


        private void AddComponent(string typeId, bool forceTypeIDAsName, bool addToUndoStack, bool allowNamingDialog) {
            // generate an unique id and show this id in the naming dialogue or using this id when using automatic placement
            int counter = 0;
            String suggestID = null;
            Asterics.ACS2.componentTypesComponentType componentToAdd = (Asterics.ACS2.componentTypesComponentType)componentList[typeId];

            // check, if component is singleton
            bool canBeAdded = true;
            if (componentList.Contains(typeId) == false)
            {
                return;
            }
            if (((Asterics.ACS2.componentTypesComponentType)componentList[typeId]).singleton) {
                List<string> componentTypeList = new List<string>();
                foreach (componentType comp in deploymentComponentList.Values) {
                    componentTypeList.Add(comp.type_id);
                }
                canBeAdded = !componentTypeList.Contains(componentToAdd.id);
            }

            if (canBeAdded) {
                if (forceTypeIDAsName == false) {
                    do {
                        counter++;
                        suggestID = componentToAdd.id + "." + counter;
                        suggestID = TrimComponentName(suggestID);
                    } while (deploymentComponentList.ContainsKey(suggestID));
                }
                else
                    suggestID = typeId;
                String newComponentId = null;

                // Show the naming dialog box only when set to do so in the options dialog
                if (showNamingDialogOnComponentInsert && allowNamingDialog) {
                    newComponentId = SetNameForComponentOnCanvas(suggestID);
                }
                else {
                    newComponentId = suggestID;
                }
                componentType selectedComponent;
                if (newComponentId != "") {
                    selectedComponent = componentType.CopyFromBundleModel(componentToAdd, newComponentId);

                    // adding the property changed listener to component properties
                    foreach (propertyType p in selectedComponent.PropertyArrayList) {
                        p.PropertyChanged += ComponentPropertyChanged;
                        p.PropertyChangeError += ComponentPropertyChangeError;
                    }
                    // adding the property changed listener to port properties
                    foreach (object port in selectedComponent.PortsList.Values) {
                        if (port is inputPortType) {
                            ((inputPortType)port).PropertyChanged += InputPortIntPropertyChanged;
                            foreach (propertyType p in ((inputPortType)port).PropertyArrayList) {
                                p.PropertyChanged += InPortPropertyChanged;
                                p.PropertyChangeError += ComponentPropertyChangeError;
                            }
                        }
                        else {
                            // update the alias for group ports via property changed listener
                            ((outputPortType)port).PropertyChanged += OutputPortIntPropertyChanged;
                            foreach (propertyType p in ((outputPortType)port).PropertyArrayList) {
                                p.PropertyChanged += OutPortPropertyChanged;
                                p.PropertyChangeError += ComponentPropertyChangeError;
                            }
                        }
                    }

                    int[] pos = ProperComponentCoordinates(40, 40);
                    int positionX = pos[0];
                    if (positionX < 0)
                        positionX = 0;
                    else if (positionX + selectedComponent.ComponentCanvas.Width > canvas.RenderSize.Width)
                        positionX = (int)(canvas.RenderSize.Width - selectedComponent.ComponentCanvas.Width);
                    int positionY = pos[1];
                    if (positionY < 0)
                        positionY = 0;
                    else if (positionY + selectedComponent.ComponentCanvas.Height > canvas.RenderSize.Height)
                        positionY = (int)(canvas.RenderSize.Height - selectedComponent.ComponentCanvas.Height);
                    selectedComponent.layout.posX = Convert.ToString(positionX);
                    selectedComponent.layout.posY = Convert.ToString(positionY);
                    focusedComponent = selectedComponent;

                    this.ClearAndAddSelectedComponent(selectedComponent);

                    AddComponent(selectedComponent);
                    if (addToUndoStack) {
                        CommandObject co = new CommandObject("Delete", selectedComponent);
                        undoStack.Push(co);
                        redoStack.Clear();
                    }

                    // Check, if component has GUI-elements and if, setting it on the GUI-editor
                    // check, if component has a gui component, and load the gui component

                    if (componentToAdd.gui != null) {
                        selectedComponent.gui = new guiType();
                        selectedComponent.gui.height = componentToAdd.gui.height;
                        selectedComponent.gui.width = componentToAdd.gui.width;
                        selectedComponent.gui.posX = "0";
                        selectedComponent.gui.posY = "0";
                        if (componentToAdd.gui.IsExternalGUIElementSpecified && componentToAdd.gui.IsExternalGUIElement) {
                            selectedComponent.gui.IsExternalGUIElement = true;
                        } else {
                            selectedComponent.gui.IsExternalGUIElement = false;
                        }
                        AddGUIComponent(selectedComponent);
                    }
                    //Keyboard.Focus(selectedComponent.ComponentCanvas);
                }
            }
            else {
                MessageBox.Show(Properties.Resources.SingletonErrorHeaderFormat(typeId), Properties.Resources.SingletonErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Warning);
            }
            UpdateToolTips();
        }

        /// <summary>
        /// Implementation of the Undo-functionality 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Undo_Click(object sender, RoutedEventArgs e) {
            modelHasBeenEdited = true;
            newChannelRibbonButton.IsChecked = false;
            ArrayList groupIds = new ArrayList();
            if (undoStack.Count > 0) {
                CommandObject co = undoStack.Pop();
                switch (co.Command) {
                    case "moveComponent":
                        for (int i = 0; i < co.InvolvedObjects.Count; i++) {
                            componentType mc = (componentType)co.InvolvedObjects[i];
                            int posX = (int)co.Parameter[i * 2];
                            int posY = (int)co.Parameter[(i * 2) + 1];
                            co.Parameter[i * 2] = int.Parse(mc.layout.posX);
                            co.Parameter[(i * 2) + 1] = int.Parse(mc.layout.posY);
                            MoveComponent(mc, posX, posY);
                        }
                        redoStack.Push(co);
                        break;
                    case "Add":
                        Console.WriteLine("UNDO ADD");
                        co.Command = "Delete";
                        ArrayList groupComps = new ArrayList();
                        ArrayList events = new ArrayList();
                        ArrayList channels = new ArrayList();
                        foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                componentType co1 = (componentType)o;
                                if (!(co1.ComponentType == ACS2.componentTypeDataTypes.group)) {
                                    Console.WriteLine("Added " + co1.id);
                                    AddComponent(co1);
                                    AddSelectedComponent(co1);
                                    if (co1.ComponentCanvas.Visibility != System.Windows.Visibility.Visible)
                                        co1.ComponentCanvas.Visibility = System.Windows.Visibility.Visible;

                                }
                            }
                            else if (o is channel) {
                                Console.WriteLine("Added channel");
                                channel c = (channel)o;
                                if (c.GroupOriginalSource == null && c.GroupOriginalTarget == null) {
                                    AddChannel(c);
                                    AddSelectedChannel(c);
                                    if (c.Line.Visibility != System.Windows.Visibility.Visible)
                                        c.Line.Visibility = System.Windows.Visibility.Visible;
                                }
                                else
                                    channels.Add(c);
                            }
                            else if (o is eventChannelLine) {
                                eventChannelLine ec = (eventChannelLine)o;
                                if (!ec.HasGroupSource && !ec.HasGroupTarget) {
                                    AddEventChannelCommand(ec, false);
                                    AddSelectedEventChannel(ec);
                                    canvas.Children.Add(ec.Line);
                                    if (ec.Line.Visibility != System.Windows.Visibility.Visible)
                                        ec.Line.Visibility = System.Windows.Visibility.Visible;
                                }
                                else
                                    events.Add(ec);
                            } else if (o is groupComponent) {
                                groupComponent gc = (groupComponent)o;
                                groupComps.Add(gc);
                            }
                        }

                        foreach (object param in co.Parameter) {
                            if (param is eventChannel) {
                                eventChannel ec = (eventChannel)param;
                                if (!eventChannelList.Contains(ec) && ec.GroupOriginalTarget == null && ec.GroupOriginalSource == null)
                                    eventChannelList.Add(ec);
                            }
                        }
                        foreach (groupComponent gc in groupComps) {
                            ClearSelectedChannelList();
                            ClearSelectedEventChannelList();
                            ClearSelectedComponentList();
                            foreach (componentType ct in gc.AddedComponentList) {
                                AddSelectedComponent(ct);
                            }
                            groupIds.Add(DoGrouping(gc.ID, false, false));
                            
                        }
                        updateRedoUndoStacks(groupIds);
                        foreach (channel c in channels) {
                            AddChannel(c);
                            AddSelectedChannel(c);
                        }
                        foreach (eventChannelLine ech in events) {
                            if (!eventChannelLineExists(ech.ListenerComponentId, ech.TriggerComponentId)) {
                                AddEventChannelCommand(ech, false);
                                AddSelectedEventChannel(ech);
                                canvas.Children.Add(ech.Line);
                            }
                        }

                        // Add eventchannels of the command to the eventChannelList
                        foreach (object param in co.Parameter) {
                            if (param is eventChannel) {
                                eventChannel ec = (eventChannel)param;
                                if (!eventChannelList.Contains(ec))
                                    eventChannelList.Add(ec);
                            }
                        }
                        co.InvolvedObjects.Reverse();
                        redoStack.Push(co);
                        DeleteDanglingEventChannelLines();
                        DeleteDanglingEventChannels();
                        DeleteDanglingChannels();
                        DeleteDanglineLines();
                        break;
                    case "Delete":
                        Console.WriteLine("UNDO Delete");
                        foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                Console.WriteLine("componentType");
                                componentType c = (componentType)o;
                                DeleteComponent(c);
                            }
                            else if (o is channel) {
                                Console.WriteLine("channel");
                                DeleteChannel((channel)o);
                            }
                            else if (o is eventChannelLine) {
                                Console.WriteLine("eventChannel");
                                foreach (eventChannel eventCh in eventChannelList) {
                                    if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) &&
                                        (eventCh.targets.target.component.id == ((eventChannelLine)o).ListenerComponentId)) {
                                        if (!co.Parameter.Contains(eventCh))
                                            co.Parameter.Add(eventCh);
                                    }
                                }
                                DeleteEventChannelCommand((eventChannelLine)o);
                            }
                            else if (o is groupComponent) {
                                groupComponent gc = (groupComponent)o;
                                foreach (componentType ct in gc.AddedComponentList) {
                                    DeleteComponent(ct);
                                }
                                DeleteComponent(deploymentComponentList[gc.ID]);
                            }
                        }
                        co.InvolvedObjects.Reverse();
                        co.Command = "Add";
                        redoStack.Push(co);
                        DeleteDanglingChannels();
                        DeleteDanglingEventChannelLines();
                        DeleteDanglingEventChannels();
                        DeleteDanglineLines();
                        break;
                    case "Ungroup":
                       ArrayList elems = new ArrayList();
                       co.Parameter.Clear();
                        foreach (componentType ct in co.InvolvedObjects) {
                            groupComponent gc = null;
                            if (groupsList.ContainsKey(ct.id)) {
                                gc = groupsList[ct.id];
                                elems.Add(gc.AddedComponentList);
                                co.Parameter.Add(ct.id);
                            }

                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                            AddSelectedComponent(ct);
                            DoUngrouping(false);
                        }
                        
                        co.Command = "Group";
                        co.InvolvedObjects.Clear();
                        co.InvolvedObjects.AddRange(elems);
                        redoStack.Push(co);
                        ArrayList removeCanvas = new ArrayList();
                        foreach (object o in canvas.Children) {
                            if (o is Canvas) {
                                Canvas c = (Canvas)o;
                                bool found = false;
                                foreach (componentType ct in deploymentComponentList.Values) {
                                    if (ct.ComponentCanvas.Equals(c)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found)
                                    removeCanvas.Add(c);
                            }
                        }

                        foreach (Canvas c in removeCanvas)
                            canvas.Children.Remove(c);
                        break;
                    case "Group":
                         ArrayList groups = co.InvolvedObjects;
                        ArrayList groupIds1 = new ArrayList();
                        int k = 0;
                        foreach (object list in groups) {
                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                            LinkedList<componentType> elems1 = (LinkedList<componentType>)list;
                            foreach (componentType ct in elems1) {
                                AddSelectedComponent(ct);
                            }
                            string groupName = DoGrouping(null, false, true);
                            string newname = (string)co.Parameter[k++];
                            componentType tmpct = deploymentComponentList[groupName];
                            tmpct.id = newname;
                            groupIds1.Add(newname);
                        }
                        
                        co.Command = "Ungroup";
                        co.InvolvedObjects.Clear();
                        foreach (string groupId in groupIds1) {
                            if (deploymentComponentList.ContainsKey(groupId)) {
                                co.InvolvedObjects.Add(deploymentComponentList[groupId]);
                            }
                        }
                        co.Parameter.Clear();
                        redoStack.Push(co);
                        updateRedoUndoStacks(groupIds1);
                        break;
                }
            }
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }
            UpdateToolTips();
        }

        
        /// <summary>
        /// If an object gets grouped by undo or redo, a new object with the same name of the old
        /// group will be created. Therefore all previous commandobjects which contain the old group have to be updated
        /// to operate on the new object
        /// </summary>
        /// <param name="ct">component which should get updated</param>
        private void updateRedoUndoStacks(ArrayList groupIds) {
            ArrayList commands = new ArrayList();
            commands.AddRange(undoStack);
            commands.AddRange(redoStack);
            foreach (CommandObject co in commands) {
                switch (co.Command) {
                    case "moveComponent":
                        foreach (string groupId in groupIds) {
                            componentType ctToRemove = null;
                            int index = 0;
                            foreach (componentType ct in co.InvolvedObjects) {
                                if (ct.id.Equals(groupId)) {
                                    ctToRemove = ct;
                                    break;
                                }
                                index++;
                            }
                            if (ctToRemove != null) {
                                co.InvolvedObjects.Remove(ctToRemove);
                                co.InvolvedObjects.Insert(index,deploymentComponentList[groupId]);
                                
                            }
                        }
                        break;
                }
            }
            UpdateToolTips();
        }

        /// <summary>
        /// Implementation of the Redo-functionality
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Redo_Click(object sender, RoutedEventArgs e) {
            modelHasBeenEdited = true;
            newChannelRibbonButton.IsChecked = false;
            if (redoStack.Count > 0) {
                CommandObject co = redoStack.Pop();
                switch (co.Command) {
                    case "moveComponent":
                        for (int i = 0; i < co.InvolvedObjects.Count; i++) {
                            componentType mc = (componentType)co.InvolvedObjects[i];
                            int posX = (int)co.Parameter[i * 2];
                            int posY = (int)co.Parameter[(i * 2) + 1];
                            co.Parameter[i * 2] = int.Parse(mc.layout.posX);
                            co.Parameter[(i * 2) + 1] = int.Parse(mc.layout.posY);
                            MoveComponent(mc, posX, posY);
                        }
                        undoStack.Push(co);
                        break;
                    case "Add":
                        Console.WriteLine("REDO ADD");
                        co.Command = "Delete";
                        ArrayList groupComps = new ArrayList();
                        ArrayList events = new ArrayList();
                        ArrayList channels = new ArrayList();
                        foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                componentType co1 = (componentType)o;
                                if (!(co1.ComponentType == ACS2.componentTypeDataTypes.group)) {
                                    Console.WriteLine("Added " + co1.id);
                                    AddComponent(co1);
                                    AddSelectedComponent(co1);
                                    if (co1.ComponentCanvas.Visibility != System.Windows.Visibility.Visible)
                                        co1.ComponentCanvas.Visibility = System.Windows.Visibility.Visible;

                                }
                            }
                            else if (o is channel) {
                                Console.WriteLine("Added channel");
                                channel c = (channel)o;
                                if (c.GroupOriginalSource == null && c.GroupOriginalTarget == null) {
                                    AddChannel(c);
                                    AddSelectedChannel(c);
                                    if (c.Line.Visibility != System.Windows.Visibility.Visible)
                                        c.Line.Visibility = System.Windows.Visibility.Visible;
                                }
                                else
                                    channels.Add(c);
                            }
                            else if (o is eventChannelLine) {
                                eventChannelLine ec = (eventChannelLine)o;
                                if (!ec.HasGroupSource && !ec.HasGroupTarget) {
                                    AddEventChannelCommand(ec, false);
                                    AddSelectedEventChannel(ec);
                                    canvas.Children.Add(ec.Line);
                                    if (ec.Line.Visibility != System.Windows.Visibility.Visible)
                                        ec.Line.Visibility = System.Windows.Visibility.Visible;
                                }
                                else
                                    events.Add(ec);
                            } else if (o is groupComponent) {
                                groupComponent gc = (groupComponent)o;
                                groupComps.Add(gc);
                            }
                        }

                        foreach (object param in co.Parameter) {
                            if (param is eventChannel) {
                                eventChannel ec = (eventChannel)param;
                                if (!eventChannelList.Contains(ec) && ec.GroupOriginalTarget == null && ec.GroupOriginalSource == null)
                                    eventChannelList.Add(ec);
                            }
                        }
                        foreach (groupComponent gc in groupComps) {
                            ClearSelectedChannelList();
                            ClearSelectedEventChannelList();
                            ClearSelectedComponentList();
                            foreach (componentType ct in gc.AddedComponentList) {
                                AddSelectedComponent(ct);
                            }
                            DoGrouping(gc.ID, false, false);
                        }
                        foreach (channel c in channels) {
                            AddChannel(c);
                            AddSelectedChannel(c);
                        }
                        foreach (eventChannelLine ech in events) {
                            if (!eventChannelLineExists(ech.ListenerComponentId, ech.TriggerComponentId)) {
                                AddEventChannelCommand(ech, false);
                                AddSelectedEventChannel(ech);
                                canvas.Children.Add(ech.Line);
                            }
                        }

                        // Add eventchannels of the command to the eventChannelList
                        foreach (object param in co.Parameter) {
                            if (param is eventChannel) {
                                eventChannel ec = (eventChannel)param;
                                if (!eventChannelList.Contains(ec))
                                    eventChannelList.Add(ec);
                            }
                        }
                        co.InvolvedObjects.Reverse();
                        undoStack.Push(co);
                        DeleteDanglingEventChannelLines();
                        DeleteDanglingEventChannels();
                        DeleteDanglingChannels();
                        DeleteDanglineLines();
                        break;
                        /*co.Command = "Delete";
                        foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                AddComponent((componentType)o);
                                AddSelectedComponent((componentType)o);
                            }
                            else if (o is channel) {
                                AddChannel((channel)o);
                                AddSelectedChannel((channel)o);
                            }
                            else if (o is eventChannelLine) {
                                AddEventChannelCommand((eventChannelLine)o);
                                AddSelectedEventChannel((eventChannelLine)o);
                                canvas.Children.Add(((eventChannelLine)o).Line);
                            }
                        }
                        // Add eventchannels of the command to the eventChannelList
                        foreach (object param in co.Parameter) {
                            if (param is eventChannel) {
                                if (!eventChannelList.Contains((eventChannel)param))
                                    eventChannelList.Add((eventChannel)param);

                            }
                        }
                        co.InvolvedObjects.Reverse();
                        undoStack.Push(co);
                        break;*/
                    case "Delete":
                        // reverse the order of the elements cause to have the following insert order
                        // 1: Components; 2: channels and eventchannels 
                        /*foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                DeleteComponent((componentType)o);
                            }
                            else if (o is channel) {
                                DeleteChannel((channel)o);
                            }
                            else if (o is eventChannelLine) {
                                foreach (eventChannel eventCh in eventChannelList) {
                                    if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) &&
                                        (eventCh.targets.target.component.id == ((eventChannelLine)o).ListenerComponentId)) {
                                        co.Parameter.Add(eventCh);
                                    }
                                }
                                DeleteEventChannelCommand((eventChannelLine)o);
                            }
                        }
                        co.InvolvedObjects.Reverse();
                        co.Command = "Add";
                        undoStack.Push(co);
                        break;*/
                        Console.WriteLine("UNDO Delete");
                        foreach (object o in co.InvolvedObjects) {
                            if (o is componentType) {
                                Console.WriteLine("componentType");
                                componentType c = (componentType)o;
                                DeleteComponent(c);
                            }
                            else if (o is channel) {
                                Console.WriteLine("channel");
                                DeleteChannel((channel)o);
                            }
                            else if (o is eventChannelLine) {
                                Console.WriteLine("eventChannel");
                                foreach (eventChannel eventCh in eventChannelList) {
                                    if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) &&
                                        (eventCh.targets.target.component.id == ((eventChannelLine)o).ListenerComponentId)) {
                                        if (!co.Parameter.Contains(eventCh))
                                            co.Parameter.Add(eventCh);
                                    }
                                }
                                DeleteEventChannelCommand((eventChannelLine)o);
                            }
                            else if (o is groupComponent) {
                                groupComponent gc = (groupComponent)o;
                                foreach (componentType ct in gc.AddedComponentList) {
                                    DeleteComponent(ct);
                                }
                                DeleteComponent(deploymentComponentList[gc.ID]);
                            }
                        }
                        co.InvolvedObjects.Reverse();
                        co.Command = "Add";
                        undoStack.Push(co);
                        DeleteDanglingChannels();
                        DeleteDanglingEventChannelLines();
                        DeleteDanglingEventChannels();
                        DeleteDanglineLines();
                        break;
                    case "Ungroup":
                        ArrayList elems = new ArrayList();
                        co.Parameter.Clear();
                        foreach (componentType ct in co.InvolvedObjects) {
                            groupComponent gc = null;
                            if (groupsList.ContainsKey(ct.id)) {
                                gc = groupsList[ct.id];
                                elems.Add(gc.AddedComponentList);
                                // add name of the group to the parameter to make it possible to 
                                // regroup with the correct id
                                co.Parameter.Add(ct.id);
                            }

                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                            AddSelectedComponent(ct);
                            DoUngrouping(false);
                        }
                        
                        co.Command = "Group";
                        co.InvolvedObjects.Clear();
                        co.InvolvedObjects.AddRange(elems);
                        
                        undoStack.Push(co);
                        ArrayList removeCanvas = new ArrayList();
                        foreach (object o in canvas.Children) {
                            if (o is Canvas) {
                                Canvas c = (Canvas)o;
                                bool found = false;
                                foreach (componentType ct in deploymentComponentList.Values) {
                                    if (ct.ComponentCanvas.Equals(c)) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found)
                                    removeCanvas.Add(c);
                            }
                        }

                        foreach (Canvas c in removeCanvas)
                            canvas.Children.Remove(c);
                        break;
                    case "Group":
                        ArrayList groups = co.InvolvedObjects;
                        ArrayList groupIds = new ArrayList();
                        int k = 0;
                        foreach (object list in groups) {
                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                            LinkedList<componentType> elems1 = (LinkedList<componentType>)list;
                            foreach (componentType ct in elems1) {
                                AddSelectedComponent(ct);
                            }
                            string newName = (string)co.Parameter[k++];
                            string oldName = DoGrouping(null, false, true);
                            componentType tmpct = deploymentComponentList[oldName];
                            tmpct.id = newName;
                            groupIds.Add(newName);
                        }
                        
                        co.Command = "Ungroup";
                        co.InvolvedObjects.Clear();
                        foreach (string groupId in groupIds) {
                            if (deploymentComponentList.ContainsKey(groupId)) {
                                co.InvolvedObjects.Add(deploymentComponentList[groupId]);
                            }
                        }
                        co.Parameter.Clear();
                        undoStack.Push(co);
                        updateRedoUndoStacks(groupIds);
                        break;
                }
            }
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }
            UpdateToolTips();
        }

        // Delete a component, called by the ribbon menu
        /// <summary>
        /// Delete a component, called by the ribbon menu. Calls DeleteComponentFromMenu(modelComponent deleteComponent) to 
        /// delete the component
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        //private void DeleteComponentRibbonButton_Click(object sender, RoutedEventArgs e) {
        //    newChannelRibbonButton.IsChecked = false;
        //    foreach (modelComponent mc in selectedComponentList)
        //        DeleteComponentFromMenu(mc);
        //    this.ClearSelectedComponentList();
        //}


        private void DeleteSelectionRibbonButton_Click(object sender, RoutedEventArgs e) {
            DeleteSelectedComponents();
        }

        /// <summary>
        /// Delete a channel, called by the ribbon menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        //private void DeleteChannelRibbonButton_Click(object sender, RoutedEventArgs e) {
        //    newChannelRibbonButton.IsChecked = false;
        //    if (focusedChannel != null) {
        //        CommandObject co = new CommandObject("Add", focusedChannel);
        //        undoStack.Push(co);
        //        redoStack.Clear();
        //        DeleteChannel(focusedChannel);
        //        focusedChannel = null;
        //        //deleteChannelRibbonButton.IsEnabled = false;
        //    }
        //}

        /// <summary>
        /// Delete an event channel, called by the ribbon menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        //private void DeleteEventChannelRibbonButton_Click(object sender, RoutedEventArgs e) {
        //    newEventChannelRibbonButton.IsChecked = false;
        //    if (focusedEventChannel != null) {
        //        //if (MessageBox.Show(Properties.Resources.DeleteEventChannelConfirmTextFormat(focusedEventChannel.TriggerComponentId,focusedEventChannel.ListernerComponentId), 
        //        //        Properties.Resources.DeleteEventChannelConfirmHeader, MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes) {
        //        // uncomment if-condition to activate "delete eventchannel?" question
        //        if (true) {
        //            /*CommandObject co = new CommandObject("addEventChannel", focusedEventChannel);
        //            foreach (eventChannel eventCh in eventChannelList) {
        //                if ((eventCh.sources.source.component.id == ((eventChannelLine)co.InvolvedObject).TriggerComponentId) && (eventCh.targets.target.component.id == ((eventChannelLine)co.InvolvedObject).ListernerComponentId)) {
        //                    co.Parameter.Add(eventCh);
        //                }
        //            }
        //            undoStack.Push(co);
        //            redoStack.Clear();
        //            DeleteEventChannelCommand(focusedEventChannel);*/
        //            focusedEventChannel = null;
        //        }
        //    }
        //    foreach (eventChannelLine ech in selectedEventChannelList) {
        //        CommandObject co = new CommandObject("Add", ech);
        //        foreach (eventChannel eventCh in eventChannelList) {
        //            foreach (object o in co.InvolvedObjects) {
        //                if (!(o is eventChannelLine))
        //                    continue;
        //                if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) && (eventCh.targets.target.component.id == ((eventChannelLine)o).ListernerComponentId)) {
        //                    co.Parameter.Add(eventCh);
        //                }
        //            }
        //        }
        //        undoStack.Push(co);
        //        redoStack.Clear();
        //        DeleteEventChannelCommand(ech);
        //    } 
        //}

        /// <summary>
        /// Move a Component on the canvas. As long as the ribbon button is checked, the 
        /// component can be moved with the arrow keys
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void moveComponentRibbonButton_Checked(object sender, RoutedEventArgs e) {
            newChannelRibbonButton.IsChecked = false;
            if (Keyboard.FocusedElement is Canvas) {
                Canvas canvasWithFocus = (Canvas)Keyboard.FocusedElement;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.ComponentCanvas == canvasWithFocus) {
                        componentToMove = tempComponent;
                        break;
                    }
                }
                //componentToMove = null;
                //CommandObject co = CreateMoveCommandObject();
                //undoStack.Push(co);
                //redoStack.Clear();
            }
        }

        /// <summary>
        /// Stop the moving capability of a component
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void moveComponentRibbonButton_Unchecked(object sender, RoutedEventArgs e) {
            componentToMove = null;
        }

        /// <summary>
        /// Opens the Help-window
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Help_Click(object sender, RoutedEventArgs e) {
            if (sender is RibbonButton) {
                System.Windows.Forms.Help.ShowHelp(null, @"ACS_Help.chm", System.Windows.Forms.HelpNavigator.TableOfContents);
            }
            else {
                if (focusedComponent != null) {
                    System.Windows.Forms.Help.ShowHelp(null, @"ACS_Help.chm", System.Windows.Forms.HelpNavigator.KeywordIndex, focusedComponent.type_id);
                }
                else {
                    if (selectedComponentList.Count == 0)
                    {
                        System.Windows.Forms.Help.ShowHelp(null, @"ACS_Help.chm", System.Windows.Forms.HelpNavigator.TableOfContents);
                    }
                    else
                    {
                        System.Windows.Forms.Help.ShowHelp(null, @"ACS_Help.chm", System.Windows.Forms.HelpNavigator.KeywordIndex, selectedComponentList.First.Value.type_id);
                    }
                }
            }
        }

        /// <summary>
        /// Start the model at the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void StartModel_Click(object sender, RoutedEventArgs e) {
            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            try {

                // check, if a model is loaded and remove error marker
                if ((deploymentModel != null) && (deploymentModel.components != null) && (deploymentModel.components.Count() > 0)) {
                    foreach (componentType mc in deploymentModel.components) {
                        if ((mc != null) && ((mc.ComponentCanvas.Background == Brushes.Red) || (mc.ComponentCanvas.Background == Brushes.Orange))) {
                            mc.ComponentCanvas.Background = null;
                        }
                    }
                }

                asapiClient.RunModel();
                
                //Thread t = new Thread(asapiClient.RunModel);
                //t.Start();
                //t.Join(3000); // 
                //if (t.IsAlive) { // 
                //    t.Abort();
                //    t.Join();
                //}
                areStatus.Status = AREStatus.ConnectionStatus.Running;
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.StartModelErrorDialog, Properties.Resources.StartModelErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                CheckASAPIConnection();
            }
        }

        /// <summary>
        /// Pause the model at the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void PauseModel_Click(object sender, RoutedEventArgs e) {
            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            try {
                areStatus.Status = AREStatus.ConnectionStatus.Pause;
                asapiClient.PauseModel();            
                //Thread t = new Thread(asapiClient.PauseModel);
                //t.Start();
                //t.Join(3000); // 
                //if (t.IsAlive) { // 
                //    t.Abort();
                //    t.Join();
                //}
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.PauseModelErrorDialog, Properties.Resources.PauseModelErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                CheckASAPIConnection();
            }
        }

        /// <summary>
        /// Stop the model at the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void StopModel_Click(object sender, RoutedEventArgs e) {
            // If a property has been edited and the focus has not been set to another element, the property will not be set. 
            // Clicking ribbon elments did not remove focus from property editor, so the property will
            // not be set. Causes problems, saving, uplaoding, ... the model
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            try {
                asapiClient.StopModel();
                
                //Thread t = new Thread(asapiClient.StopModel);
                //t.Start();
                //t.Join(3000); // 
                //if (t.IsAlive) { // 
                //    t.Abort();
                //    t.Join();
                //}
                areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.StopModelErrorDialog, Properties.Resources.StopModelErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                CheckASAPIConnection();
            }
        }

        /// <summary>
        /// Called, when a RibbonTab will be changed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Ribbon_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            newChannelRibbonButton.IsChecked = false;
            newEventChannelRibbonButton.IsChecked = false;
            //showEventsRibbonButton.IsChecked = false;
        }

        /// <summary>
        /// Hide the ports and the channel between the ports on the canvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void showPortsRibbonButton_Unchecked(object sender, RoutedEventArgs e) {

            // set visibility to "hidden" in the deploymentmodel
            foreach (componentType mc in deploymentModel.components) {
                if (mc != null && mc.ports != null) {
                    foreach (object o in mc.ports) {
                        if (o is outputPortType) {
                            ((outputPortType)o).PortRectangle.Visibility = Visibility.Collapsed;
                            ((outputPortType)o).PortLabel.Visibility = Visibility.Collapsed;
                        }
                        else if (o is inputPortType) {
                            ((inputPortType)o).PortRectangle.Visibility = Visibility.Collapsed;
                            ((inputPortType)o).PortLabel.Visibility = Visibility.Collapsed;
                        }
                    }
                }
            }
            // set visibility to "hidden" in the deploymentChannelList
            foreach (channel moc in deploymentChannelList.Values) {
                if (moc != null) {
                    moc.Line.Visibility = Visibility.Collapsed;
                }
            }
            // set the ribbon buttons inactive
            newChannelRibbonButton.IsChecked = false;
            newChannelRibbonButton.IsEnabled = false;
            //deleteChannelRibbonButton.IsEnabled = false;
            if (channelToConnect != null) {
                canvas.Children.Remove(channelToConnect.Line);
                channelToConnect = null;
            }
        }

        /// <summary>
        /// Show the ports and the channel between the ports on the canvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void showPortsRibbonButton_Checked(object sender, RoutedEventArgs e) {
            newChannelRibbonButton.IsChecked = false;
            // set visibility to "visible" in the deploymentmodel
            foreach (componentType mc in deploymentModel.components) {
                if (mc != null && mc.ports != null) {
                    foreach (object o in mc.ports) {
                        if (o is outputPortType) {
                            if (((outputPortType)o).PortRectangle.Visibility == System.Windows.Visibility.Collapsed) {
                                ((outputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                                ((outputPortType)o).PortLabel.Visibility = Visibility.Visible;
                            }
                        }
                        else if (o is inputPortType) {
                            if (((inputPortType)o).PortRectangle.Visibility == System.Windows.Visibility.Collapsed) {
                                ((inputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                                ((inputPortType)o).PortLabel.Visibility = Visibility.Visible;
                            }
                        }
                    }
                }
            }
            // set visibility to "visible" in the deploymentChannelList
            foreach (channel moc in deploymentChannelList.Values) {
                if (moc != null && moc.Line.Visibility == System.Windows.Visibility.Collapsed) {
                    moc.Line.Visibility = Visibility.Visible;
                }
            }
            // set the ribbon buttons active
            newChannelRibbonButton.IsEnabled = true;
            //deleteChannelRibbonButton.IsEnabled = false;
        }

        /// <summary>
        /// Hide the eventchannels between the components on the canvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void showEventsRibbonButton_Unchecked(object sender, RoutedEventArgs e) {
            newChannelRibbonButton.IsChecked = false;
            // set visibility to "hidden" in the deploymentmodel
            foreach (UIElement elem in canvas.Children) {
                if (elem is Canvas) {
                    Canvas cElem = (Canvas)elem;
                    foreach (UIElement elem2 in cElem.Children) {
                        if (elem2 is Canvas) {
                            Canvas cElem2 = (Canvas)elem2;
                            if (cElem2.Name.Equals("EventListenerPort") || cElem2.Name.Equals("EventTriggerPort")) {
                                cElem2.Visibility = Visibility.Hidden;
                            }
                        }
                    }
                }
                else if (elem is Line) {
                    Line tempLine = (Line)elem;
                    if (tempLine.Name == "EventChannelLine") {
                        tempLine.Visibility = Visibility.Hidden;
                    }
                }
            }
            // set the ribbon buttons inactive
            newEventChannelRibbonButton.IsChecked = false;
            newEventChannelRibbonButton.IsEnabled = false;
            //deleteEventChannelRibbonButton.IsEnabled = false;
            if (eventChannelToConnect != null) {
                canvas.Children.Remove(eventChannelToConnect.Line);
                eventChannelToConnect = null;
            }
        }

        /// <summary>
        /// Show the eventchannels between the components on the canvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void showEventsRibbonButton_Checked(object sender, RoutedEventArgs e) {

            // set visibility to "visible" in the deploymentmodel
            foreach (UIElement elem in canvas.Children) {
                if (elem is Canvas) {
                    Canvas cElem = (Canvas)elem;
                    foreach (UIElement elem2 in cElem.Children) {
                        if (elem2 is Canvas) {
                            Canvas cElem2 = (Canvas)elem2;
                            if (cElem2.Name.Equals("EventListenerPort") || cElem2.Name.Equals("EventTriggerPort")) {
                                cElem2.Visibility = Visibility.Visible;
                            }
                        }
                    }
                }
                else if (elem is Line) {
                    Line tempLine = (Line)elem;
                    if (tempLine.Name == "EventChannelLine") {
                        tempLine.Visibility = Visibility.Visible;
                    }
                }
            }
            // set the ribbon buttons active
            newEventChannelRibbonButton.IsEnabled = true;
            //deleteEventChannelRibbonButton.IsEnabled = true;
        }

        /// <summary>
        /// Called, when the new channel button will be checked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void newChannelRibbonButton_Checked(object sender, RoutedEventArgs e) {
            if (newEventChannelRibbonButton.IsChecked == true) {
                newEventChannelRibbonButton.IsChecked = false;
            }
        }

        /// <summary>
        /// Called, when the new eventchannel button will be checked
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void newEventChannelRibbonButton_Checked(object sender, RoutedEventArgs e) {
            if (newChannelRibbonButton.IsChecked == true) {
                newChannelRibbonButton.IsChecked = false;
            }
        }

        /// <summary>
        /// Request the status of the ARE (and the plugins, running on the ARE)
        /// Display the status in a window
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void GetAREStatusRibbonButton_Click(object sender, RoutedEventArgs e) {

            try {
                List<StatusObject> newStatus = asapiClient.QueryStatus(false);
                if (newStatus.Count > 0) {
                    foreach (StatusObject so in newStatus) {
                        statusList.Add(so);
                    }
                }

                StatusWindow sw = new StatusWindow();
                string[] statusRow;
                foreach (StatusObject so in statusList) {
                    statusRow = new string[3];
                    statusRow[0] = so.Status;
                    statusRow[2] = so.ErrorMsg;
                    if (so.InvolvedComponentID == "") {
                        statusRow[1] = "ARE";
                    }
                    else {
                        statusRow[1] = so.InvolvedComponentID;
                    }
                    sw.StatusDataGrid.Rows.Add(statusRow);
                }

                sw.Owner = this;
                sw.ShowDialog();

            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.GetAREStatusErrorDialog, Properties.Resources.GetAREStatusErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }
        }


        /// <summary>
        /// Request the log file from the ARE and displaying it in a window
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ShowLogFileRibbonButton_Click(object sender, RoutedEventArgs e) {
            String content = "";
            try {
                content = asapiClient.getLogFile();
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.ShowLogFileErrorDialog, Properties.Resources.ShowLogFileErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            LogfileWindow lFW = new LogfileWindow();
            lFW.textBlock.Text = content;
            lFW.Owner = this;
            lFW.ShowDialog();
        }

        /// <summary>
        /// Opening the Bundle Management dialog
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void configureBundlesRibbonButton_Click(object sender, RoutedEventArgs e) {
            BundleManager bm = new BundleManager();
            //lFW.textBlock.Text = content;
            bm.activeBundleTextBox.Text = activeBundle;
                      
            string fName;
            if (File.Exists(ini.IniReadValue("model", "bundle_model_startup"))) {
                fName = ini.IniReadValue("model", "bundle_model_startup");
            } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_model"))) {
                fName = AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_model");
            } else {
                fName = ini.IniReadValue("model", "bundle_model");
            }        
            bm.autostartBundleTextBox.Text = System.IO.Path.GetFileNameWithoutExtension(fName);

            string[] filesInBundlesFolder = null;
            try {

                if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                    if (Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\")) {
                        filesInBundlesFolder = Directory.GetFiles(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\componentcollections\\", "*.abd");
                    }
                } else {
                    if (Directory.Exists(AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\")) {
                        filesInBundlesFolder = Directory.GetFiles(AppDomain.CurrentDomain.BaseDirectory + "\\componentcollections\\", "*.abd");
                    }
                }
                if (filesInBundlesFolder != null) {
                    foreach (string str in filesInBundlesFolder) {
                        bm.bundlesListbox.Items.Add(System.IO.Path.GetFileNameWithoutExtension(str));
                    }
                }
            } catch (Exception ex) {
                MessageBox.Show(Properties.Resources.BundlesErrorReadingFiles, Properties.Resources.BundlesErrorReadingFiles, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }

            bm.MainIni = ini;
            bm.Owner = this;
            bm.ShowDialog();
            if (bm.SelectedBundleID >= 0) {
                componentList.Clear();
                CleanACS();
                LoadBundle(filesInBundlesFolder[bm.SelectedBundleID]);
            } else if (bm.SelectedBundleID == -2) {
                componentList.Clear();
                LoadBundle(null);
                CleanACS();
                MessageBox.Show(Properties.Resources.ReadBundleText, Properties.Resources.ReadBundleHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            }
        }


        /// <summary>
        /// Opening the external tool to activace plugins inside the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void CreationWizardRibbonButton_Click(object sender, RoutedEventArgs e) {
            if (File.Exists(ini.IniReadValue("Options", "pathToPluginCreationTool"))) {
                System.Diagnostics.Process.Start(ini.IniReadValue("Options", "pathToPluginCreationTool"));
            }
            else {
                MessageBox.Show(Properties.Resources.ExternalToolFileNotFound, Properties.Resources.FileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        /// <summary>
        /// Opening the external tool to create a new plugin
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ActivationWizardRibbonButton_Click(object sender, RoutedEventArgs e) {
            if (File.Exists(ini.IniReadValue("Options", "pathToPluginActivationTool"))) {
                System.Diagnostics.Process.Start(ini.IniReadValue("Options", "pathToPluginActivationTool"));
            }
            else {
                MessageBox.Show(Properties.Resources.ExternalToolFileNotFound, Properties.Resources.FileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        /// <summary>
        /// Opening the external tool to create a language translation file, so that the properties of a components are also translated
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void LanguageFileCreationRibbonButton_Click(object sender, RoutedEventArgs e) {
            if (File.Exists(ini.IniReadValue("Options", "pathToLanguageFileCreationTool"))) {
                System.Diagnostics.Process.Start(ini.IniReadValue("Options", "pathToLanguageFileCreationTool"));
            }
            else {
                MessageBox.Show(Properties.Resources.ExternalToolFileNotFound, Properties.Resources.FileNotFoundHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        /// <summary>
        /// Funkction is called, when the drop-down of the main file menu is opened. The function reads the recently-opened-files file and generates items to be shown in the menu.
        /// </summary>
        private void fileMenu_DropDownOpened(object sender, EventArgs e) {
            string[] recent;
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                recent = File.ReadAllLines(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt");
            } else {
                recent = File.ReadAllLines(AppDomain.CurrentDomain.BaseDirectory + "recent.txt");
            }
            RecentFiles rf = (RecentFiles)this.Resources["MostRecentFiles"];
            rf.Clear();
            foreach (string s in recent) {
                RibbonGalleryItem recentFileItem = new RibbonGalleryItem();
                String str = s;
                recentFileItem.Content = str.Substring(str.LastIndexOf('\\') + 1);
                recentFileItem.Tag = str;
                recentFileItem.ToolTip = str;
                recentFileItem.PreviewMouseLeftButtonDown += new MouseButtonEventHandler(recentFileItem_MouseDown);
                recentFileItem.PreviewKeyDown += new KeyEventHandler(recentFileItem_KeyDown);
                rf.AddItem(recentFileItem);
            }
        }


        /// <summary>
        /// Prints the components of the canvas to an A4 page
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Print_Click(object sender, RoutedEventArgs e) {
            // The "printVisual" function only prints the viaual components. So, the scrollViewer must be set to 0,0
            // and the zoom factor must be set to a value that all components are on the screen
            scrollViewer.ScrollToLeftEnd();
            scrollViewer.ScrollToTop();

            // find the x and Y values of the components, being far away from the start point
            double farX = 0;
            double farY = 0;
            foreach (componentType cT in deploymentComponentList.Values) {
                if (cT.ComponentCanvas.Visibility == Visibility.Visible) {
                    if (Canvas.GetLeft(cT.ComponentCanvas) > farX) {
                        farX = Canvas.GetLeft(cT.ComponentCanvas);
                    }
                    if (Canvas.GetTop(cT.ComponentCanvas) > farY) {
                        farY = Canvas.GetTop(cT.ComponentCanvas);
                    }
                }
            }
            farX += LayoutConstants.COMPONENTCANVASWIDTH + 15;
            farY += LayoutConstants.COMPONENTCANVASHEIGHT + 15;

            // calculating the zoom-factor for the canvas, so that all components are on the printed page
            double zoomX = 1;
            double zoomY = 1;
            //if (farX > dialog.PrintableAreaWidth) {
            //    zoomX = dialog.PrintableAreaWidth / farX;
            //}
            //if (farY > dialog.PrintableAreaHeight) {
            //    zoomY = dialog.PrintableAreaHeight / farY;
            //}
            // values of an A4 page: 793*1122
            if (farX > 793) {
                zoomX = 793 / farX;
            }
            if (farY > 1122) {
                zoomY = 1122 / farY;
            }
            Transform backupScale = canvas.LayoutTransform;

            if (zoomX < zoomY) {
                canvas.LayoutTransform = new ScaleTransform(zoomX, zoomX);
            } else {
                canvas.LayoutTransform = new ScaleTransform(zoomY, zoomY);
            }
            PrintDialog dialog = new PrintDialog();

            if (dialog.ShowDialog() == true) {
                dialog.PrintVisual(canvas, "ACS Print");
            }
            canvas.LayoutTransform = backupScale;
        }


        #endregion // Menu functionalities

        #region Focus listeners

        /// <summary>
        /// A component got the focus. Several functions are triggerd in that case:
        /// 1. Dashed line around the component canvas
        /// 2. Activate several buttons in the ribbon menu (move, delete, properties)
        /// 3. Set the property editors in the property-dock (by calling SetPropertyDock(modelComponent tempComponent))
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void ComponentCanvas_GotKeyboardFocus(object sender, KeyboardEventArgs e) {
            Canvas focusCanvas = (Canvas)sender;
            bool abort = false;
            foreach (UIElement uie in focusCanvas.Children) {
                if ((uie is Rectangle) && (((Rectangle)uie).Name.Equals("keyboardFocusRectangle"))) {
                    abort = true;
                    break;
                }
            }
            if (!abort) {
                double canvasX = Canvas.GetLeft(focusCanvas);
                double canvasY = Canvas.GetTop(focusCanvas);
                //focusCanvas.Background = new SolidColorBrush(Colors.Red);

                Rectangle cr = new Rectangle();
                cr.Stroke = new SolidColorBrush(Colors.Blue);
                cr.StrokeThickness = 2;
                /*DoubleCollection dashes = new DoubleCollection();
                dashes.Add(1.0000001);
                dashes.Add(2.0000001);
                cr.StrokeDashArray = dashes;*/
                cr.Width = focusCanvas.Width;
                cr.Height = focusCanvas.Height;
                focusCanvas.Children.Add(cr);
                Canvas.SetTop(cr, 0);
                Canvas.SetLeft(cr, 0);
                cr.Name = "keyboardFocusRectangle";
                cr.RadiusX = 4;
                cr.RadiusY = 4;
            }
            Canvas.SetZIndex(focusCanvas, Canvas.GetZIndex(focusCanvas) + 3000);

            moveComponentRibbonButton.IsEnabled = true;
            //deleteComponentRibbonButton.IsEnabled = true;
            componentPropertiesRibbonButton.IsEnabled = true;

            componentType tempComponent = null;
            foreach (componentType tempComponent2 in deploymentComponentList.Values) {
                if (tempComponent2.ComponentCanvas == (Canvas)sender) {
                    tempComponent = tempComponent2;
                    break;
                }
            }
            focusedComponent = tempComponent;
            SetPropertyDock(tempComponent);
        }

        // Function, when a component lost the focus
        /// <summary>
        /// A component lost the focus. Two functions are triggerd in that case:
        /// 1. Remove dashed line around the component canvas
        /// 2. Deactivate several buttons in the ribbon menu (move, delete, properties)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void ComponentCanvas_LostKeyboardFocus(object sender, KeyboardEventArgs e) {
            Canvas focusCanvas = (Canvas)sender;
            Canvas.SetZIndex(focusCanvas, Canvas.GetZIndex(focusCanvas) - 3000);
            focusedComponent = null;
            foreach (UIElement uie in focusCanvas.Children) {
                if ((uie is Rectangle) && (((Rectangle)uie).Name.Equals("keyboardFocusRectangle"))) {
                    focusCanvas.Children.Remove((Rectangle)uie);
                    moveComponentRibbonButton.IsEnabled = false;
                    //deleteComponentRibbonButton.IsEnabled = false;
                    componentPropertiesRibbonButton.IsEnabled = false;
                    moveComponentRibbonButton.IsChecked = false;
                    break;
                }
            }
        }

        /// <summary>
        /// A channel got the focus, content in property dock will be reseted by ResetPropertyDock()
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void Channel_GotKeyboardFocus(object sender, RoutedEventArgs args) {
            DoubleCollection dashes = new DoubleCollection();
            dashes.Add(1);
            dashes.Add(1);
            ((Line)sender).StrokeDashArray = dashes;
            //((Line)sender).Stroke = new SolidColorBrush(Colors.Red);
            //deleteChannelRibbonButton.IsEnabled = true;

            foreach (channel tempChannel in deploymentChannelList.Values) {
                if (tempChannel.Line == (Line)sender) {
                    focusedChannel = tempChannel;
                    break;
                }
            }

            ResetPropertyDock();
        }

        /// <summary>
        /// A channel lost the focus
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void Channel_LostKeyboardFocus(object sender, RoutedEventArgs args) {
            ((Line)sender).StrokeDashArray = null;
            focusedChannel = null;
            //deleteChannelRibbonButton.IsEnabled = false;
        }

        /// <summary>
        /// An eventchannel got the focus, content in property dock will be set by SetEventPropertyDock(modelComponent sourceComponent, modelComponent targetComponent)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void EventChannel_GotKeyboardFocus(object sender, RoutedEventArgs args) {
            DoubleCollection dashes = new DoubleCollection();
            dashes.Add(1);
            dashes.Add(1);
            ((Line)sender).StrokeDashArray = dashes;
            //deleteEventChannelRibbonButton.IsEnabled = true;
            ResetPropertyDock();
            foreach (eventChannelLine tempChannel in eventChannelLinesList) {
                if ((Line)sender == tempChannel.Line) {
                    focusedEventChannel = tempChannel;
                    break;
                }
            }
            SetEventPropertyDock(deploymentComponentList[focusedEventChannel.TriggerComponentId], deploymentComponentList[focusedEventChannel.ListenerComponentId]);
        }

        /// <summary>
        /// An eventchannel lost the focus
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void EventChannel_LostKeyboardFocus(object sender, RoutedEventArgs args) {
            ((Line)sender).StrokeDashArray = null;
            //focusedEventChannel = null;
            //deleteEventChannelRibbonButton.IsEnabled = false;
        }

        /// <summary>
        /// A property editor got the focus. The active property editor will be stored, needed for input validation
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void PropertyEditor_GotKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e) {
            activePropertyGrid = (WPG.PropertyGrid)sender;
        }

        /// <summary>
        /// Selection changed listener for the ARE Storage list box
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void filenameListbox_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            storageDialog.filenameTextbox.Text = ((ListBox)sender).SelectedItem.ToString();
        }

        #endregion // Focus listener

        #region Context Menu Functionalities

        /// <summary>
        /// Function to be called, when the context menu of a component will be opened
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenu_Opened(object sender, RoutedEventArgs e) {
            // Check, if context menu was called from a rectangel
            if (((ContextMenu)e.Source).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)e.Source).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        if (tempComponent.HasVersionConflict) {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemSolveConflict)]).IsEnabled = true;
                        }
                        else {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemSolveConflict)]).IsEnabled = false;
                        }

                        // Enable and disable the Add/connect event channel in the context menu. Only, if a component has event triggers and listeners,
                        // the menu entries should be enabled
                        if ((tempComponent.EventTriggerList.Count > 0) && (eventChannelToConnect == null) && (newEventChannelRibbonButton.IsEnabled)) // was editeventribbongroup
                        {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemAddEventChannel)]).IsEnabled = true;
                        }
                        else {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemAddEventChannel)]).IsEnabled = false;
                        }
                        if ((tempComponent.EventListenerList.Count > 0) && (eventChannelToConnect != null) && (newEventChannelRibbonButton.IsEnabled))  // was editeventribbongroup
                        {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemConnectEventChannel)]).IsEnabled = true;
                        }
                        else {
                            ((MenuItem)r.ContextMenu.Items[r.ContextMenu.Items.IndexOf(componentContextMenuItemConnectEventChannel)]).IsEnabled = false;
                        }

                        // check, if ACS is not in running mode
                        if (componentsRibbonGroup.IsEnabled) {
                            componentContextMenuItemDelete.IsEnabled = true;
                        }
                        else {
                            componentContextMenuItemDelete.IsEnabled = false;
                        }

                        
                        break;
                    }
                }
                // Check, if context menu was called from a grid (the name field at the top of a component)
            }
            else if (((ContextMenu)e.Source).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)e.Source).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.TopGrid == g) {
                        if (tempComponent.HasVersionConflict) {
                            ((MenuItem)g.ContextMenu.Items[g.ContextMenu.Items.IndexOf(componentContextMenuItemSolveConflict)]).IsEnabled = true;
                        }
                        else {
                            ((MenuItem)g.ContextMenu.Items[g.ContextMenu.Items.IndexOf(componentContextMenuItemSolveConflict)]).IsEnabled = false;
                        }
                        break;
                    }
                }
            }
        }

        /// <summary>
        /// The 'move' functionality, called from the component context menu
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuItemMove_Click(object sender, RoutedEventArgs e) {
            // Check, if context menu was called from a rectangel
            if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        componentToMove = tempComponent;
                        //CommandObject co = CreateMoveCommandObject();
                        //undoStack.Push(co);
                        //redoStack.Clear();
                        break;
                    }
                }
                // Check, if context menu was called from a grid (the name field at the top of a component)
            }
            else if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.TopGrid == g) {
                        componentToMove = tempComponent;
                        //CommandObject co = CreateMoveCommandObject();
                        //undoStack.Push(co);
                        //redoStack.Clear();
                        break;
                    }
                }
            }

            moveComponentRibbonButton.IsChecked = true;
        }

        /// <summary>
        /// Connect channel from the component context menu - needed to make the ACS fully keyboard accessible
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuConnectChannel_Click(object sender, RoutedEventArgs e) {
            componentType componentWithFocus = null;

            // Check, if context menu was called from a rectangel
            if (((ContextMenu)((MenuItem)((MenuItem)e.Source).Parent).Parent).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)((MenuItem)((MenuItem)e.Source).Parent).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        componentWithFocus = tempComponent;
                        break;
                    }
                }
                // Check, if context menu was called from a grid (the name field at the top of a component)
            }
            else if (((ContextMenu)((MenuItem)((MenuItem)e.Source).Parent).Parent).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)((MenuItem)((MenuItem)e.Source).Parent).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.TopGrid == g) {
                        componentWithFocus = tempComponent;
                        break;
                    }
                }
            }

            // if no channel is prepared to be connected, a new channel will be prepared
            if (channelToConnect == null) {
                channelToConnect = new channel();
                channelToConnect.id = NewIdForChannel();
                channelToConnect.source.component.id = componentWithFocus.id;
                channelToConnect.source.port.id = ((MenuItem)e.Source).Header.ToString();
                Rectangle portRectangle = ((outputPortType)componentWithFocus.PortsList[channelToConnect.source.port.id]).PortRectangle;
                channelToConnect.Line.X1 = Canvas.GetLeft(portRectangle) + portRectangle.ActualWidth + Canvas.GetLeft(componentWithFocus.ComponentCanvas);
                channelToConnect.Line.Y1 = Canvas.GetTop(portRectangle) + portRectangle.ActualHeight - 5 + Canvas.GetTop(componentWithFocus.ComponentCanvas);

                newChannelRibbonButton.IsChecked = true;
                componentContextMenuItemAddChannel.IsEnabled = false;
                componentContextMenuItemConnectChannel.IsEnabled = true;
                componentContextMenuItemDropChannel.IsEnabled = true;
                // if a channel is prepared, it will tried to connect it
            }
            else {
                outputPortType outPort = (outputPortType)((componentType)deploymentComponentList[channelToConnect.source.component.id]).PortsList[channelToConnect.source.port.id];
                channelToConnect.target.component.id = componentWithFocus.id;
                channelToConnect.target.port.id = ((MenuItem)e.Source).Header.ToString();
                // check, if the datatypes of the port fits to each other
                if (CheckInteroperabilityOfPorts(outPort.PortDataType, ((inputPortType)componentWithFocus.PortsList[channelToConnect.target.port.id]).PortDataType)) {

                    Rectangle portRectangle = ((inputPortType)componentWithFocus.PortsList[channelToConnect.target.port.id]).PortRectangle;
                    channelToConnect.Line.X2 = Canvas.GetLeft(portRectangle) + Canvas.GetLeft(componentWithFocus.ComponentCanvas);
                    channelToConnect.Line.Y2 = Canvas.GetTop(portRectangle) + portRectangle.ActualHeight - 5 + Canvas.GetTop(componentWithFocus.ComponentCanvas);

                    AddChannel(channelToConnect);
                    Canvas.SetZIndex(channelToConnect.Line, Canvas.GetZIndex(channelToConnect.Line) + 1000);
                    CommandObject co = new CommandObject("Delete", channelToConnect);
                    undoStack.Push(co);
                    redoStack.Clear();
                    channelToConnect = null;
                    newChannelRibbonButton.IsChecked = false;
                    componentContextMenuItemAddChannel.IsEnabled = true;
                    componentContextMenuItemConnectChannel.IsEnabled = false;
                    componentContextMenuItemDropChannel.IsEnabled = false;
                }
                else {
                    MessageBox.Show(Properties.Resources.PortConnectingDatyTypeErrorFormat(outPort.PortDataType, ((inputPortType)componentWithFocus.PortsList[channelToConnect.target.port.id]).PortDataType),
                        Properties.Resources.PortConnectingDatyTypeErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    channelToConnect.target.component.id = null;
                    channelToConnect.target.port.id = null;
                }
            }
        }

        /// <summary>
        /// Drop a channel from the component context menu. This function be used, if a channel was created, but should not be connected to the selected input port
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuItemDrop_Click(object sender, RoutedEventArgs e) {
            channelToConnect = null;
            newChannelRibbonButton.IsChecked = false;

            componentContextMenuItemAddChannel.IsEnabled = true;
            componentContextMenuItemConnectChannel.IsEnabled = false;
            componentContextMenuItemDropChannel.IsEnabled = false;
        }

        /// <summary>
        /// Prepare an event channel from the component context menu and connect it to the event trigger port
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuItemAddEvent_Click(object sender, RoutedEventArgs e) {
            Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
            componentType componentWithFocus = null;
            foreach (componentType tempComponent in deploymentComponentList.Values) {
                if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                    componentWithFocus = tempComponent;
                    break;
                }
            }
            eventChannelToConnect = new eventChannelLine();
            eventChannelToConnect.TriggerComponentId = componentWithFocus.id;
            eventChannelToConnect.Line.X1 = Canvas.GetLeft(componentWithFocus.ComponentCanvas) + +LayoutConstants.EVENTOUTPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
            eventChannelToConnect.Line.Y1 = Canvas.GetTop(componentWithFocus.ComponentCanvas) + LayoutConstants.EVENTOUTPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;

            componentContextMenuItemAddEventChannel.IsEnabled = false;
            componentContextMenuItemConnectEventChannel.IsEnabled = true;
            componentContextMenuItemDropEventChannel.IsEnabled = true;
        }

        /// <summary>
        /// Connect an event channel to the event listener port. Called from the component context menu. 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuItemConnectEvent_Click(object sender, RoutedEventArgs e) {
            Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
            componentType componentWithFocus = null;
            foreach (componentType tempComponent in deploymentComponentList.Values) {
                if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                    componentWithFocus = tempComponent;
                    break;
                }
            }
            
            eventChannelToConnect.ListenerComponentId = componentWithFocus.id;
            eventChannelToConnect.Line.X2 = Canvas.GetLeft(componentWithFocus.ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
            eventChannelToConnect.Line.Y2 = Canvas.GetTop(componentWithFocus.ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
            CommandObject co = new CommandObject("Delete", eventChannelToConnect);
            undoStack.Push(co);
            redoStack.Clear();
            if (AddEventChannelCommand(eventChannelToConnect, true))
                canvas.Children.Add(eventChannelToConnect.Line);
            eventChannelToConnect = null;
            ////dockManager.ActiveContent = dockableEventsTab;

            componentContextMenuItemAddEventChannel.IsEnabled = true;
            componentContextMenuItemConnectEventChannel.IsEnabled = false;
            componentContextMenuItemDropEventChannel.IsEnabled = false;
        }

        /// <summary>
        /// Drop an event channel from the component context menu. This will be used, if an event channel was created, but should not be connected to an event listner port
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextMenuItemDropEvent_Click(object sender, RoutedEventArgs e) {
            eventChannelToConnect = null;
            newEventChannelRibbonButton.IsChecked = false;

            componentContextMenuItemAddEventChannel.IsEnabled = true;
            componentContextMenuItemConnectEventChannel.IsEnabled = false;
            componentContextMenuItemDropEventChannel.IsEnabled = false;
        }

        /// <summary>
        /// Delete a component, called from component context menu. The component will be deleted by DeleteComponentFromMenu(modelComponent deleteComponent)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextItemDelete_Click(object sender, RoutedEventArgs e) {
            if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        //DeleteComponentFromMenu(tempComponent);
                        DeleteSelectedComponents();
                        break;
                    }
                }
            }
            else if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                    if (tempComponent.TopGrid == g) {
                        DeleteSelectedComponents();
                        //DeleteComponentFromMenu(tempComponent);
                        break;
                    }
                }
            }
        }

        /// <summary>
        /// Properties from compoent context menu: transfer the focus to the property dock (to dockableComponentProperties)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextItemProperties_Click(object sender, RoutedEventArgs e) {
            newChannelRibbonButton.IsChecked = false;
            Keyboard.Focus(dockableComponentProperties);
        }

        /// <summary>
        /// Show the status of the selected component
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextItemStatus_Click(object sender, RoutedEventArgs e) {
            string callerId = "";
            if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        callerId = tempComponent.id;
                        break;
                    }
                }
            }
            else if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.TopGrid == g) {
                        callerId = tempComponent.id;
                        break;
                    }
                }
            }

            // if the ACS is connected to the ARE, th status of the component will be requested
            // otherwise, the status from earlier requests will be shown
            try {
                if ((areStatus.Status == AREStatus.ConnectionStatus.Connected) || (areStatus.Status == AREStatus.ConnectionStatus.Synchronised)) {
                    List<StatusObject> newStatus = asapiClient.QueryStatus(false);
                    if (newStatus.Count > 0) {
                        foreach (StatusObject so in newStatus) {
                            statusList.Add(so);
                        }
                    }
                }

                StatusWindow sw = new StatusWindow();

                string[] statusRow;
                foreach (StatusObject so in statusList) {
                    if (so.InvolvedComponentID == callerId) {
                        statusRow = new string[3];
                        statusRow[0] = so.Status;
                        statusRow[2] = so.ErrorMsg;
                        statusRow[1] = so.InvolvedComponentID;

                        sw.StatusDataGrid.Rows.Add(statusRow);
                        //break;
                    } else if (deploymentComponentList[callerId].ComponentType == ACS2.componentTypeDataTypes.group) {
                        foreach (componentType componentInGroup in groupsList[callerId].AddedComponentList) {
                            foreach (StatusObject soInGroup in statusList) {
                                if (soInGroup.InvolvedComponentID == componentInGroup.id) {
                                    statusRow = new string[3];
                                    statusRow[0] = soInGroup.Status;
                                    statusRow[2] = soInGroup.ErrorMsg;
                                    statusRow[1] = soInGroup.InvolvedComponentID;

                                    sw.StatusDataGrid.Rows.Add(statusRow);
                                }
                            }
                        }
                    }
                }

                sw.InvolvedComponent = deploymentComponentList[callerId];
                sw.label1.Content = Properties.Resources.StatusWindowLabelComponentFormat(callerId);
                sw.Owner = this;
                sw.ShowDialog();

            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.GetAREStatusErrorDialog, Properties.Resources.GetAREStatusErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }
        }

        /// <summary>
        /// Solve version conflict called from component context menu. This function is only available, if the component has a version conflict
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentContextItemSolveConflict_Click(object sender, RoutedEventArgs e) {
            if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Rectangle) {
                Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.MainRectangle == r) || (tempComponent.TopRectangle == r)) {
                        tempComponent.ComponentCanvas.Background = null;
                        tempComponent.HasVersionConflict = false;
                        break;
                    }
                }
            }
            else if (((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget is Grid) {
                Grid g = (Grid)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if (tempComponent.TopGrid == g) {
                        tempComponent.ComponentCanvas.Background = null;
                        tempComponent.HasVersionConflict = false;
                        break;
                    }
                }
            }
        }


        // the property editor for the plugin will be shown
        // the property editor is an external essembly, a dll
        // OLD METHOD, SHOWING AN EXTRA PORPERTY WINDOW !!!!!!!!!!!!!!!!!!!!!!
        //private void ComponentContextItemProperties_Click(object sender, RoutedEventArgs e)
        //{
        //    Rectangle r = (Rectangle)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;
        //    modelComponent tempComponent = null;
        //    foreach (modelComponent tempComponent2 in deploymentComponentList.Values) {
        //        if (tempComponent2.MainRectangle == r) {
        //            tempComponent = tempComponent2;
        //            break;
        //        }
        //    }

        //    backupIdForPropertyEditor = tempComponent.id;

        //    pw = new PropertyWindow();
        //    //pw.propEditor.SelectedObject = tempComponent;
        //    //int rowCounter = 1;

        //    /*
        //    foreach (propertyType property in tempComponent.PropertyArrayList) {
        //        PropertyEditorLibrary.PropertyEditor pe = new PropertyEditorLibrary.PropertyEditor();
        //        //WPG.PropertyGrid pe = new WPG.PropertyGrid();
        //        pe.ShowTabs = false;
        //        //pe.Margin = new Thickness(8);
        //        pe.Width = 350;
        //        pe.SelectedObject = property;
        //        //pe.Instance = property;

        //        pw.gridPanelComponent.RowDefinitions.Add(new RowDefinition());
        //        pw.gridPanelComponent.Children.Add(pe);

        //        Grid.SetRow(pe, rowCounter);
        //        rowCounter++;
        //    }*/
        //    //PropertyEditorLibrary.PropertyEditor pe = new PropertyEditorLibrary.PropertyEditor();
        //    ////WPG.PropertyGrid pe = new WPG.PropertyGrid();
        //    //pe.ShowTabs = false;
        //    ////pe.Margin = new Thickness(8);
        //    //pe.Width = 350;
        //    //pe.SelectedObjects = tempComponent.properties;
        //    ////pe.Instance = property;

        //    WPG.PropertyGrid pe = new WPG.PropertyGrid();
        //    pe.Instance = tempComponent;
        //    pe.Margin = new Thickness(8);
        //    pe.DisplayName = tempComponent.id;
        //    pe.ShowPreview = false;

        //    pw.gridPanelComponent.RowDefinitions.Add(new RowDefinition());
        //    pw.gridPanelComponent.Children.Add(pe);
        //    Grid.SetRow(pe, 0);

        //    ListBox listBoxInport = new ListBox();
        //    ListBox listBoxOutport = new ListBox();

        //    // A test with a collection of inputPorts
        //    //System.Collections.ObjectModel.ObservableCollection<inputPortType> oc = new System.Collections.ObjectModel.ObservableCollection<inputPortType>();            

        //    foreach (object o in tempComponent.ports.PortsList.Values) {
        //        ListBoxItem lbi = new ListBoxItem();
        //        if (o is inputPortType) {
        //            lbi.Content = ((inputPortType)o).portTypeID;
        //            listBoxInport.Items.Add(lbi);
        //            //oc.Add((inputPortType)o);
        //        } else {
        //            lbi.Content = ((outputPortType)o).portTypeID;
        //            listBoxOutport.Items.Add(lbi);
        //        }
        //    }
        //    if (listBoxInport.Items.Count == 0) {
        //        pw.inportTab.Visibility = Visibility.Collapsed;
        //    } else {
        //        pw.gridPanelInport.Children.Add(listBoxInport);
        //        Grid.SetRow(listBoxInport, 0);
        //    }
        //    if (listBoxOutport.Items.Count == 0) {
        //        pw.outportTab.Visibility = Visibility.Collapsed;
        //    } else {
        //        pw.gridPanelOutport.Children.Add(listBoxOutport);
        //        Grid.SetRow(listBoxOutport, 0);
        //    }
        //    listBoxInport.SelectionChanged += ListBoxInport_SelectionChanged;
        //    listBoxOutport.SelectionChanged += ListBoxOutport_SelectionChanged;

        //    //WPG.PropertyGrid peInport = new WPG.PropertyGrid();
        //    //peInport.Instance = oc;
        //    //pw.gridPanelInport.Children.Add(peInport);
        //    //Grid.SetRow(peInport, 1);

        //    //pw.ShowInTaskbar = false;
        //    pw.Owner = this;
        //    pw.ShowInTaskbar = false;
        //    //pw.Language = System.Windows.Markup.XmlLanguage.GetLanguage("en-US");  //System.Globalization.CultureInfo("en-GB");
        //    pw.ShowDialog();
        //}


        // Old Code , needed if the properties are shown in a seperate window, see above
        //
        //void ListBoxInport_SelectionChanged(object sender, SelectionChangedEventArgs e) {
        //    if (pw.gridPanelInport.RowDefinitions.Count > 1) {
        //        pw.gridPanelInport.RowDefinitions.RemoveAt(1);
        //    }
        //    if (pw.gridPanelInport.Children.Count > 1) {
        //        pw.gridPanelInport.Children.RemoveAt(1);
        //    }
        //    ListBoxItem lbi = ((sender as ListBox).SelectedItem as ListBoxItem);
        //    modelComponent selectedComponent = deploymentComponentList[backupIdForPropertyEditor];
        //    inputPortType port = null;
        //    foreach (object o in selectedComponent.ports.PortsList.Values) {
        //        if ((o is inputPortType) && (((inputPortType)o).portTypeID == lbi.Content.ToString())) {
        //            port = (inputPortType)o;
        //            break;
        //        }
        //    }
        //    WPG.PropertyGrid pe = new WPG.PropertyGrid();
        //    pe.Instance = port;
        //    pe.Margin = new Thickness(8);
        //    pe.DisplayName = port.portTypeID;
        //    pe.ShowPreview = false;
        //    RowDefinition rd = new RowDefinition();
        //    rd.Height = new GridLength(480);
        //    pw.gridPanelInport.RowDefinitions.Add(rd);
        //    pw.gridPanelInport.Children.Add(pe);
        //    Grid.SetRow(pe, 1);
        //}

        // Old Code , needed if the properties are shown in a seperate window, see above
        //
        //void ListBoxOutport_SelectionChanged(object sender, SelectionChangedEventArgs e) {
        //    if (pw.gridPanelOutport.RowDefinitions.Count > 1) {
        //        pw.gridPanelOutport.RowDefinitions.RemoveAt(1);
        //    }
        //    if (pw.gridPanelOutport.Children.Count > 1) {
        //        pw.gridPanelOutport.Children.RemoveAt(1);
        //    }
        //    ListBoxItem lbi = ((sender as ListBox).SelectedItem as ListBoxItem);
        //    modelComponent selectedComponent = deploymentComponentList[backupIdForPropertyEditor];
        //    outputPortType port = null;
        //    foreach (object o in selectedComponent.ports.PortsList.Values) {
        //        if ((o is outputPortType) && (((outputPortType)o).portTypeID == lbi.Content.ToString())) {
        //            port = (outputPortType)o;
        //            break;
        //        }
        //    }

        //    WPG.PropertyGrid pe = new WPG.PropertyGrid();
        //    pe.Instance = port;
        //    pe.Margin = new Thickness(8);
        //    pe.Name = port.portTypeID;
        //    RowDefinition rd = new RowDefinition();
        //    rd.Height = new GridLength(480);
        //    pw.gridPanelOutport.RowDefinitions.Add(rd);
        //    pw.gridPanelOutport.Children.Add(pe);
        //    Grid.SetRow(pe, 1);
        //}

        /// <summary>
        /// Adapting the context menu of a channel, when it will be opened. Deactivating the
        /// menu entries, if the ACS is in running mode
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ChannelContextMenu_Opened(object sender, RoutedEventArgs e) {
            if (((ContextMenu)sender).Placement == System.Windows.Controls.Primitives.PlacementMode.MousePoint) {
                ((ContextMenu)sender).HorizontalOffset = 0;
                ((ContextMenu)sender).VerticalOffset = 0;
            }

            //Deactivated all Menu entries, if ACS is in running mode 
            foreach (MenuItem mi in ((ContextMenu)sender).Items) {
                if (newChannelRibbonButton.IsEnabled) // was editchannelribbongroup
                {
                    mi.IsEnabled = true;
                }
                else {
                    mi.IsEnabled = false;
                }
            }
        }

        /// <summary>
        /// Delete a channel from the component context menu. Delete will done by DeleteChannel(channel deleteChannel)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ChannelContextItemDelete_Click(object sender, RoutedEventArgs e) {
            // find selected line
            //Line l = (Line)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;

            //foreach (channel tempChannel in deploymentChannelList.Values) {
            //    if (l == tempChannel.Line) {
            //        CommandObject co = new CommandObject("Add", tempChannel);
            //        undoStack.Push(co);
            //        redoStack.Clear();
            //        DeleteChannel(tempChannel);
            //        break;
            //    }               
            //}
            DeleteSelectedComponents();
        }

        /// <summary>
        /// Adapting the context menu of an event channel, when it will be opened. Deactivating the
        /// menu entries, if the ACS is in running mode
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EventChannelContextMenu_Opened(object sender, RoutedEventArgs e) {
            if (((ContextMenu)sender).Placement == System.Windows.Controls.Primitives.PlacementMode.MousePoint) {
                ((ContextMenu)sender).HorizontalOffset = 0;
                ((ContextMenu)sender).VerticalOffset = 0;
            }

            //Deactivated all Menu entries, if ACS is in running mode 
            foreach (MenuItem mi in ((ContextMenu)sender).Items) {
                if (newEventChannelRibbonButton.IsEnabled) // was editeventribbongroup
                {
                    mi.IsEnabled = true;
                }
                else {
                    mi.IsEnabled = false;
                }
            }
        }

        /// <summary>
        /// Delete an eventchannel from the component context menu. Delete will done by DeleteEventChannelCommand(eventChannelLine eventChannelToDelete)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EventChannelContextItemDelete_Click(object sender, RoutedEventArgs e) {
            // find selected line
            //Line l = (Line)((ContextMenu)((MenuItem)e.Source).Parent).PlacementTarget;

            //foreach (eventChannelLine tempChannel in eventChannelLinesList) {
            //    if (l == tempChannel.Line) {
            //        focusedEventChannel = tempChannel;
            //        break;
            //    }
            //}
            //if (focusedEventChannel != null) {
            //    //if (MessageBox.Show(Properties.Resources.DeleteEventChannelConfirmTextFormat(focusedEventChannel.TriggerComponentId, focusedEventChannel.ListernerComponentId),
            //    //        Properties.Resources.DeleteEventChannelConfirmHeader, MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes) {
            //    if (true) {
            //        /*CommandObject co = new CommandObject("addEventChannel", focusedEventChannel);
            //        foreach (eventChannel eventCh in eventChannelList) {
            //            if ((eventCh.sources.source.component.id == ((eventChannelLine)co.InvolvedObject).TriggerComponentId) && (eventCh.targets.target.component.id == 
            //                ((eventChannelLine)co.InvolvedObject).ListernerComponentId)) {
            //                co.Parameter.Add(eventCh);
            //            }
            //        }
            //        undoStack.Push(co);
            //        DeleteEventChannelCommand(focusedEventChannel);*/
            //        focusedEventChannel = null;
            //    }
            //}
            //foreach (eventChannelLine ech in selectedEventChannelList) {
            //    CommandObject co = new CommandObject("Add", ech);
            //    foreach (eventChannel eventCh in eventChannelList) {
            //        foreach ( object o in co.InvolvedObjects) {
            //            if (!(o is eventChannelLine))
            //                continue;
            //            if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) && (eventCh.targets.target.component.id == ((eventChannelLine)o).ListernerComponentId)) {
            //                co.Parameter.Add(eventCh);
            //            }
            //        }
            //    }
            //    undoStack.Push(co);
            //    redoStack.Clear();
            //    DeleteEventChannelCommand(ech);
            //}

            DeleteSelectedComponents();
        }

        /// <summary>
        /// Component context menu, set the events. Therefore, the focus will be moved to the dockableEventsTab
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EventChannelContextItemSetEvents_Click(object sender, RoutedEventArgs e) {
            dockManager.ActiveContent = dockableEventsTab;
        }

        #endregion // Context Menu functionalities

        #region Mouse listeners


        /// <summary>
        /// Function called, when the left mouse button will be released. If the release will be done over a channel or an event channel, the channel will get the focus
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnLeftUp(object sender, MouseEventArgs args) {
            // reset mouseMove Coordinates
            mouseMoveComponentX = -1;
            mouseMoveComponentY = -1;
            // when on the LeftDown Event a channel docked to a component all Selected Items should get deselected 
            if (connectedChannelLastClick == true) {
                connectedChannelLastClick = false;
                ClearSelectedChannelList();
                ClearSelectedComponentList();
                ClearSelectedEventChannelList();
                return;
            }

            /////  *** begin patch for easier selection of channels and ports

            foreach (eventChannelLine tempLine in eventChannelLinesList)
            {
                if (tempLine.Line.StrokeThickness == SELECTED_LINE_THICKNESS)
                {
                    focusedEventChannel = tempLine;

                    double v = scrollViewer.VerticalOffset;
                    double h = scrollViewer.HorizontalOffset;

                    Keyboard.Focus(focusedEventChannel.Line);

                    scrollViewer.ScrollToVerticalOffset(v);
                    scrollViewer.ScrollToHorizontalOffset(h);

                    if (!selectedEventChannelList.Contains(tempLine))
                    {
                        if ((Keyboard.Modifiers & ModifierKeys.Control) == 0)
                        {
                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                        }
                        AddSelectedEventChannel(tempLine);
                    }
                    else
                    {
                        if ((Keyboard.Modifiers & ModifierKeys.Control) > 0)
                        {
                            ClearColorOfSelectedEventChannels();
                            selectedEventChannelList.Remove(tempLine);
                            UpdateSelectedEventChannels();
                        }
                    }
                    break ;
                }
            }

            foreach (channel tempChannel in deploymentChannelList.Values)
            {
                if (tempChannel.Line.StrokeThickness == SELECTED_LINE_THICKNESS)
                {
                    focusedChannel = tempChannel;
                    double v=scrollViewer.VerticalOffset;
                    double h = scrollViewer.HorizontalOffset;

                    Keyboard.Focus(focusedChannel.Line);

                    scrollViewer.ScrollToVerticalOffset(v);
                    scrollViewer.ScrollToHorizontalOffset(h);

                    if (!selectedChannelList.Contains(tempChannel))
                    {
                        if ((Keyboard.Modifiers & ModifierKeys.Control) == 0)
                        {
                            ClearSelectedChannelList();
                            ClearSelectedComponentList();
                            ClearSelectedEventChannelList();
                        }
                        this.AddSelectedChannel(tempChannel);
                        Console.WriteLine(tempChannel.id + ": " + tempChannel.source.component.id + " --> " + tempChannel.target.component.id);

                    }
                    else
                    {
                        if ((Keyboard.Modifiers & ModifierKeys.Control) > 0)
                        {
                            ClearColorOfSelectedChannels();
                            selectedChannelList.Remove(tempChannel);
                            UpdateSelectedChannels();
                        }
                    }
                    break;
                }
            }
            /////  *** end patch for easier selection of channels and ports


            // left up on an event channel. set event channel as focused event channel
            if (args.Source is Line) {
                if (((Line)args.Source).Name == "EventChannelLine") {
                    foreach (eventChannelLine tempLine in eventChannelLinesList) {
                        if (tempLine.Line == (Line)args.Source) {
                            focusedEventChannel = tempLine;
                            Keyboard.Focus(focusedEventChannel.Line);
                            if (!selectedEventChannelList.Contains(tempLine)) {
                                if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                    ClearSelectedChannelList();
                                    ClearSelectedComponentList();
                                    ClearSelectedEventChannelList();
                                }
                                AddSelectedEventChannel(tempLine);
                            }
                            else {
                                if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                    ClearColorOfSelectedEventChannels();
                                    selectedEventChannelList.Remove(tempLine);
                                    UpdateSelectedEventChannels();
                                }
                            }
                            break;
                        }
                    }
                }
                else { // left up on a channel. set channel as focused channel
                    foreach (channel tempChannel in deploymentChannelList.Values) {
                        if (tempChannel.Line == (Line)args.Source) {
                            focusedChannel = tempChannel;
                            Keyboard.Focus(focusedChannel.Line);
                            if (!selectedChannelList.Contains(tempChannel)) {
                                if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                    ClearSelectedChannelList();
                                    ClearSelectedComponentList();
                                    ClearSelectedEventChannelList();
                                }
                                this.AddSelectedChannel(tempChannel);
                                Console.WriteLine(tempChannel.id + ": " + tempChannel.source.component.id + " --> " + tempChannel.target.component.id);

                            }
                            else {
                                if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                    ClearColorOfSelectedChannels();
                                    selectedChannelList.Remove(tempChannel);
                                    UpdateSelectedChannels();
                                }
                            }
                            break;
                        }
                    }
                }
            }
            else if (args.Source is Rectangle) {
                //modelComponent tempComponent = null;
                moveTracking = false;
                foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                    if ((tempComponent.MainRectangle == (Rectangle)args.Source) || (tempComponent.TopRectangle == (Rectangle)args.Source)) {
                        focusedComponent = tempComponent;
                        Keyboard.Focus(focusedComponent.ComponentCanvas);
                        if (!selectedComponentList.Contains(tempComponent)) {
                            if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                ClearSelectedComponentList();
                                ClearSelectedChannelList();
                                ClearSelectedEventChannelList();
                            }
                            AddSelectedComponent(tempComponent);
                        }
                        else {
                            if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                ClearBorderOfSelectedComponents();
                                selectedComponentList.Remove(tempComponent);
                                UpdateSelectedComponents();
                            }
                        }
                        break;
                    }
                    else {
                        if (newChannelRibbonButton.IsEnabled) // was editchannelribbongroup
                        {
                            foreach (object o in tempComponent.PortsList.Values) {
                                if (o is outputPortType) {
                                    if (((outputPortType)o).PortRectangle == (Rectangle)args.Source) {
                                        //focusedComponent = tempComponent;
                                        //Keyboard.Focus(focusedComponent.ComponentCanvas);
                                        newChannelRibbonButton.IsChecked = true;
                                        break;
                                    }
                                }
                                else if (o is inputPortType) {
                                    if (((inputPortType)o).PortRectangle == (Rectangle)args.Source) {
                                        if (!selectedComponentList.Contains(tempComponent)) {
                                            if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                                ClearSelectedComponentList();
                                                ClearSelectedChannelList();
                                                ClearSelectedEventChannelList();
                                            }
                                            AddSelectedComponent(tempComponent);
                                        }
                                        else {
                                            if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                                ClearBorderOfSelectedComponents();
                                                selectedComponentList.Remove(tempComponent);
                                                UpdateSelectedComponents();
                                            }
                                        }
                                        focusedComponent = tempComponent;
                                        Keyboard.Focus(tempComponent.ComponentCanvas);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (args.Source is TextBlock) {
                foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                    if (tempComponent.Label == (TextBlock)args.Source) {
                        focusedComponent = tempComponent;
                        Keyboard.Focus(focusedComponent.ComponentCanvas);
                        if (!selectedComponentList.Contains(tempComponent)) {
                            if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                ClearSelectedComponentList();
                                ClearSelectedChannelList();
                                ClearSelectedEventChannelList();
                            }
                            AddSelectedComponent(tempComponent);
                        }
                        else {
                            if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                ClearBorderOfSelectedComponents();
                                selectedComponentList.Remove(tempComponent);
                                UpdateSelectedComponents();
                            }
                        }
                        break;
                    }
                    else {
                        foreach (object o in tempComponent.PortsList.Values) {
                            if (o is inputPortType) {
                                if (((inputPortType)o).PortLabel == (TextBlock)args.Source) {
                                    focusedComponent = tempComponent;
                                    Keyboard.Focus(focusedComponent.ComponentCanvas);
                                    if (!selectedComponentList.Contains(tempComponent)) {
                                        if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                            ClearSelectedComponentList();
                                            ClearSelectedChannelList();
                                            ClearSelectedEventChannelList();
                                        }
                                        AddSelectedComponent(tempComponent);
                                    }
                                    else {
                                        if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                            ClearBorderOfSelectedComponents();
                                            selectedComponentList.Remove(tempComponent);
                                            UpdateSelectedComponents();
                                        }
                                    }
                                    break;
                                }
                            }
                            else if (o is outputPortType) {
                                if (((outputPortType)o).PortLabel == (TextBlock)args.Source) {
                                    focusedComponent = tempComponent;
                                    Keyboard.Focus(focusedComponent.ComponentCanvas);
                                    if (!selectedComponentList.Contains(tempComponent)) {
                                        if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                            ClearSelectedComponentList();
                                            ClearSelectedChannelList();
                                            ClearSelectedEventChannelList();
                                        }
                                        AddSelectedComponent(tempComponent);
                                    }
                                    else {
                                        if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                            ClearBorderOfSelectedComponents();
                                            selectedComponentList.Remove(tempComponent);
                                            UpdateSelectedComponents();
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            else if (args.Source is Polygon) {
                if (newEventChannelRibbonButton.IsEnabled) // was editeventribbongroup
                {
                    if (!((Polygon)args.Source).Name.Equals("EventTriggerPortPolygon")) {
                        foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                            Canvas tempCanvas = (Canvas)((Polygon)args.Source).Parent;
                            if (tempComponent.ComponentCanvas == tempCanvas.Parent) {
                                if (!selectedComponentList.Contains(tempComponent)) {
                                    if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                        ClearSelectedComponentList();
                                        ClearSelectedChannelList();
                                        ClearSelectedEventChannelList();
                                    }
                                    AddSelectedComponent(tempComponent);
                                    focusedComponent = tempComponent;
                                    Keyboard.Focus(focusedComponent.ComponentCanvas);
                                }
                                else {
                                    if ((Keyboard.Modifiers & ModifierKeys.Control) > 0) {
                                        ClearBorderOfSelectedComponents();
                                        selectedComponentList.Remove(tempComponent);
                                        UpdateSelectedComponents();
                                    }
                                    else {
                                        focusedComponent = tempComponent;
                                        Keyboard.Focus(tempComponent.ComponentCanvas);
                                    }
                                }
                                break;
                            }

                        }
                    }
                }
            }
            if (selectionRectangle != null) {
                //select all objects of the canvas which collide with this rectangle
                this.selectObjectsFromRectangle(selectionRectangle);
                canvas.Children.Remove(selectionRectangle);
                selectionRectangle = null;
            }
            if (componentToMove != null) {
                focusedComponent = componentToMove;
                Keyboard.Focus(focusedComponent.ComponentCanvas);
                Canvas.SetZIndex(focusedComponent.ComponentCanvas, Canvas.GetZIndex(focusedComponent.ComponentCanvas) - 3000);
                componentToMove = null;
            }
            offsetX = 0;
            offsetY = 0;
        }

        /// <summary>
        /// Function called, if the left mouse button is pressed down on the canvas. This can cause several actions: move the component, draw a channel between ports or 
        /// draw an event channel between two event ports
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnLeftDown(object sender, MouseEventArgs args) {
            if (hiddenChannels) {
                SetAllChannelsVisible();
                hiddenChannels = false;
            }
            // four possibe actions:
            // select a component to move
            // connect the start point of a channel to an out-port
            // connect the end point of a channel to an in-port
            // fix a component after movement
            Point clickPoint = args.GetPosition(canvas);
            mouseMoveComponentX = (int)clickPoint.X;
            mouseMoveComponentY = (int)clickPoint.Y;

            connectedChannelLastClick = false;
            // sets a component to a new position
            // Line uncomment, to allow the drawing of channels just by clicking on the output port or the event trigger port
            //if ((newChannelRibbonButton.IsChecked == false) && (newEventChannelRibbonButton.IsChecked == false)) {
            if (args.Source is Canvas && ((Canvas)args.Source) == canvas && ModifierKeys.Control != Keyboard.Modifiers) {
                // whenever the user leaves the canvas with the mouse pointer, and stops holding the mousebutton, the mouseup
                // event will not get fired in this class. In this case the rectangle should not get modified
                // and work as before the user moved the mouse cursor outside the canvas.
                if (selectionRectangle == null) {
                    ClearSelectedComponentList();
                    ClearSelectedChannelList();
                    ClearSelectedEventChannelList();
                    ResetPropertyDock();
                    selectionRectangle = new Rectangle();
                    Canvas.SetZIndex(selectionRectangle, selRectZIndex);
                    selectionRectangle.Fill = new SolidColorBrush(Color.FromArgb(15, 0, 0, 255));
                    DoubleCollection dashes = new DoubleCollection();
                    dashes.Add(1.0000001);
                    dashes.Add(2.0000001);
                    selectionRectangle.StrokeDashArray = dashes;
                    selectionRectangle.Stroke = new SolidColorBrush(Colors.Black);
                    selectionRectangle.Width = 1;
                    selectionRectangle.Height = 1;
                    selRectStartPoint = args.GetPosition(canvas);
                    Canvas.SetLeft(selectionRectangle, selRectStartPoint.X);
                    Canvas.SetTop(selectionRectangle, selRectStartPoint.Y);
                    canvas.Children.Add(selectionRectangle);
                }
            }
            else if (args.Source is Rectangle) {
                //modelComponent tempComponent = null;
                if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                    foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                        if ((tempComponent.MainRectangle == (Rectangle)args.Source) || (tempComponent.TopRectangle == (Rectangle)args.Source)) {
                            focusedComponent = tempComponent;
                            Keyboard.Focus(focusedComponent.ComponentCanvas);
                            if (!selectedComponentList.Contains(tempComponent)) {
                                ClearSelectedComponentList();
                                ClearSelectedChannelList();
                                ClearSelectedEventChannelList();
                                this.AddSelectedComponent(tempComponent);
                                moveComponentRibbonButton.IsChecked = true;
                                moveTracking = false;
                            }
                            break;
                        }
                        else {
                            if (newChannelRibbonButton.IsEnabled) // was editchannelribbongroup
                            {
                                foreach (object o in tempComponent.PortsList.Values) {
                                    if (o is outputPortType) {
                                        if (((outputPortType)o).PortRectangle == (Rectangle)args.Source) {
                                            //focusedComponent = tempComponent;
                                            //Keyboard.Focus(focusedComponent.ComponentCanvas);
                                            newChannelRibbonButton.IsChecked = true;
                                            //disable mouse move
                                            mouseMoveComponentX = -1;
                                            mouseMoveComponentY = -1;
                                            ClearSelectedChannelList();
                                            ClearSelectedComponentList();
                                            ClearSelectedEventChannelList();
                                            break;
                                        }
                                    }
                                    else if (o is inputPortType) {
                                        if (((inputPortType)o).PortRectangle == (Rectangle)args.Source) {
                                            //disable mouse move
                                            if (!selectedComponentList.Contains(tempComponent) && channelToConnect == null) {
                                                ClearSelectedComponentList();
                                                ClearSelectedChannelList();
                                                ClearSelectedEventChannelList();
                                                this.AddSelectedComponent(tempComponent);
                                                moveComponentRibbonButton.IsChecked = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (args.Source is TextBlock) {
                if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                    foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                        if (tempComponent.Label == (TextBlock)args.Source) {
                            focusedComponent = tempComponent;
                            Keyboard.Focus(focusedComponent.ComponentCanvas);
                            if (!selectedComponentList.Contains(tempComponent)) {
                                ClearSelectedComponentList();
                                ClearSelectedChannelList();
                                ClearSelectedEventChannelList();
                                this.AddSelectedComponent(tempComponent);
                                moveComponentRibbonButton.IsChecked = true;
                            }
                            break;
                        }
                        else {
                            foreach (object o in tempComponent.PortsList.Values) {
                                if (o is inputPortType) {
                                    if (((inputPortType)o).PortLabel == (TextBlock)args.Source) {
                                        focusedComponent = tempComponent;
                                        Keyboard.Focus(focusedComponent.ComponentCanvas);
                                        if (!selectedComponentList.Contains(tempComponent)) {
                                            ClearSelectedComponentList();
                                            ClearSelectedChannelList();
                                            ClearSelectedEventChannelList();
                                            this.AddSelectedComponent(tempComponent);
                                            moveComponentRibbonButton.IsChecked = true;
                                        }
                                        break;
                                    }
                                }
                                else if (o is outputPortType) {
                                    if (((outputPortType)o).PortLabel == (TextBlock)args.Source) {
                                        focusedComponent = tempComponent;
                                        Keyboard.Focus(focusedComponent.ComponentCanvas);
                                        if (!selectedComponentList.Contains(tempComponent)) {
                                            ClearSelectedComponentList();
                                            ClearSelectedChannelList();
                                            ClearSelectedEventChannelList();
                                            this.AddSelectedComponent(tempComponent);
                                            moveComponentRibbonButton.IsChecked = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else if (args.Source is Polygon) {
                if (newEventChannelRibbonButton.IsEnabled) // was editeventribbongroup
                {
                    if (((Polygon)args.Source).Name == "EventTriggerPortPolygon") {
                        newEventChannelRibbonButton.IsChecked = true;
                        ClearSelectedEventChannelList();
                        ClearSelectedComponentList();
                        ClearSelectedChannelList();
                    }
                    else {
                        if (newEventChannelRibbonButton.IsChecked == false) { // mouse down on an event channel input, not in connect mode
                            if ((Keyboard.Modifiers & ModifierKeys.Control) == 0) {
                                foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                                    Canvas tempCanvas = (Canvas)((Polygon)args.Source).Parent;
                                    if (tempComponent.ComponentCanvas == tempCanvas.Parent) {
                                        focusedComponent = tempComponent;
                                        Keyboard.Focus(focusedComponent.ComponentCanvas);
                                        if (!selectedComponentList.Contains(tempComponent)) {
                                            ClearSelectedComponentList();
                                            ClearSelectedChannelList();
                                            ClearSelectedEventChannelList();
                                            this.AddSelectedComponent(tempComponent);
                                            moveComponentRibbonButton.IsChecked = true;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (focusedComponent != null) {
                Canvas.SetZIndex(focusedComponent.ComponentCanvas, Canvas.GetZIndex(focusedComponent.ComponentCanvas) + 3000);
            }

            if (componentToMove == null && focusedComponent != null) {
                if (args.Source is Rectangle) {
                    if ((focusedComponent.MainRectangle == (Rectangle)args.Source) || (focusedComponent.TopRectangle == (Rectangle)args.Source)) {
                        componentToMove = focusedComponent;
                        offsetX = (int)args.GetPosition(canvas).X - (int)Canvas.GetLeft(focusedComponent.ComponentCanvas);
                        offsetY = (int)args.GetPosition(canvas).Y - (int)Canvas.GetTop(focusedComponent.ComponentCanvas);
                        
                    }
                }
                else if (args.Source is TextBlock) {
                    if ((focusedComponent.ComponentCanvas == ((TextBlock)args.Source).Parent) || (focusedComponent.Label) == (TextBlock)args.Source) {
                        componentToMove = focusedComponent;
                        offsetX = (int)args.GetPosition(canvas).X - (int)Canvas.GetLeft(focusedComponent.ComponentCanvas);
                        offsetY = (int)args.GetPosition(canvas).Y - (int)Canvas.GetTop(focusedComponent.ComponentCanvas);
                    }
                }
                //MoveComponent(componentToMove, (int)args.GetPosition(canvas).X, (int)args.GetPosition(canvas).Y);
                //componentToMove = null;
            }
            //} 
            if ((newChannelRibbonButton.IsChecked == true) && (newChannelRibbonButton.IsEnabled)) // was editchannelribbongroup
            {
                // outport has been found, component channel has been created, now searching for inport and make all connections/settings

                if (channelToConnect != null) {
                    if (args.Source is Rectangle) {
                        Rectangle r = (Rectangle)args.Source;
                        Canvas tempCanvas = (Canvas)r.Parent;
                        foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                            if (tempComponent.ComponentCanvas == tempCanvas) {
                                bool portfound = false;
                                if (tempComponent.ports != null) {
                                    foreach (object o in tempComponent.ports) {
                                        if (o is inputPortType) {
                                            inputPortType inPort = (inputPortType)o;
                                            if ((inPort.PortRectangle == r) && (inPort.ChannelId == "")) {
                                                outputPortType outPort = (outputPortType)((componentType)deploymentComponentList[channelToConnect.source.component.id]).PortsList[channelToConnect.source.port.id];
                                                //outputPortType outPort = (outputPortType)((modelComponent)deploymentComponentList[channelToConnect.source.component.id]).ports.PortsList[]
                                                if (CheckInteroperabilityOfPorts(outPort.PortDataType, inPort.PortDataType)) {
                                                    channelToConnect.target.component.id = tempComponent.id;
                                                    channelToConnect.target.port.id = inPort.portTypeID;
                                                    channelToConnect.Line.X2 = Canvas.GetLeft(r) + Canvas.GetLeft(tempCanvas);
                                                    channelToConnect.Line.Y2 = Canvas.GetTop(r) + r.ActualHeight - r.ActualHeight / 2 + Canvas.GetTop(tempCanvas);
                                                    AddChannel(channelToConnect);
                                                    portfound = true;
                                                    Canvas.SetZIndex(channelToConnect.Line, Canvas.GetZIndex(channelToConnect.Line) + 1000);
                                                    CommandObject co = new CommandObject("Delete", channelToConnect);
                                                    undoStack.Push(co);
                                                    redoStack.Clear();
                                                    channelToConnect = null;
                                                    newChannelRibbonButton.IsChecked = false;
                                                    connectedChannelLastClick = true;
                                                    ChangeChannelVisibility(outPort.PortDataType, false, true, false);
                                                    break;
                                                }
                                                else {
                                                    MessageBox.Show(Properties.Resources.PortConnectingDatyTypeErrorFormat(outPort.PortDataType, inPort.PortDataType),
                                                        Properties.Resources.PortConnectingDatyTypeErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (portfound == false) {
                                    // click on a component while trying to connect a channel
                                    ClearSelectedChannelList();
                                    ClearSelectedComponentList();
                                    ClearSelectedEventChannelList();
                                    AddSelectedComponent(tempComponent);
                                    canvas.Children.Remove(channelToConnect.Line);
                                    channelToConnect = null;
                                    newChannelRibbonButton.IsChecked = false;
                                    //TODO: restore visibility of ports
                                }
                                break;
                            }
                        }
                        // click on the empty canvas, componentChannel will be deleted instead of connected
                    }
                    else {
                        canvas.Children.Remove(channelToConnect.Line);
                        channelToConnect = null;
                        newChannelRibbonButton.IsChecked = false;
                    }
                }

                 // searching an out-port for a new channel
                else if (args.Source is Rectangle) {
                    Rectangle r = (Rectangle)args.Source;

                    Canvas tempCanvas = (Canvas)r.Parent;
                    foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                        if (tempComponent.ComponentCanvas == tempCanvas) {
                            foreach (object o in tempComponent.ports) {
                                if (o is outputPortType) {
                                    outputPortType outPort = (outputPortType)o;
                                    if (outPort.PortRectangle == r) {
                                        channelToConnect = new channel();
                                        channelToConnect.id = NewIdForChannel();
                                        channelToConnect.source.component.id = tempComponent.id;
                                        channelToConnect.source.port.id = outPort.portTypeID;
                                        //outPort.ChannelId = channelToConnect.id;
                                        channelToConnect.Line.X1 = Canvas.GetLeft(r) + r.ActualWidth + Canvas.GetLeft(tempCanvas);
                                        channelToConnect.Line.Y1 = Canvas.GetTop(r) + r.ActualHeight - r.ActualHeight / 2 + Canvas.GetTop(tempCanvas); // -5

                                        channelToConnect.Line.X2 = args.GetPosition(canvas).X;
                                        channelToConnect.Line.Y2 = args.GetPosition(canvas).Y;
                                        canvas.Children.Add(channelToConnect.Line);
                                        ChangeChannelVisibility(outPort.PortDataType, true, false, true);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
            else if ((newChannelRibbonButton.IsChecked == false) && (channelToConnect != null)) {
                // click on the empty canvas, componentChannel will be deleted instead of connected
                canvas.Children.Remove(channelToConnect.Line);
                channelToConnect = null;
                newChannelRibbonButton.IsChecked = false;
            }

            if ((newEventChannelRibbonButton.IsChecked == true) && (newEventChannelRibbonButton.IsEnabled)) // was editeventribbongroup
            {
                if (args.Source is Polygon) {
                    if (eventChannelToConnect == null) {
                        Polygon p = (Polygon)args.Source;
                        Canvas tempCanvas = (Canvas)p.Parent;
                        if (tempCanvas.Name == "EventTriggerPort") {
                            eventChannelToConnect = new eventChannelLine();
                            foreach (componentType tempComponent in deploymentComponentList.Values) {
                                if (tempComponent.ComponentCanvas == (Canvas)tempCanvas.Parent) {
                                    eventChannelToConnect.TriggerComponentId = tempComponent.id;
                                    break;
                                }
                            }
                            eventChannelToConnect.Line.X1 = Canvas.GetLeft((Canvas)tempCanvas.Parent) + Canvas.GetLeft(tempCanvas) + tempCanvas.Width / 2 + 2;
                            eventChannelToConnect.Line.Y1 = Canvas.GetTop((Canvas)tempCanvas.Parent) + Canvas.GetTop(tempCanvas) + tempCanvas.Height;

                            eventChannelToConnect.Line.X2 = args.GetPosition(canvas).X;
                            eventChannelToConnect.Line.Y2 = args.GetPosition(canvas).Y;
                            canvas.Children.Add(eventChannelToConnect.Line);
                        }

                    }
                    else {
                        // connect
                        Polygon p = (Polygon)args.Source;
                        Canvas tempCanvas = (Canvas)p.Parent;
                        if (tempCanvas.Name == "EventListenerPort") {
                            foreach (componentType tempComponent in deploymentComponentList.Values) {
                                if (tempComponent.ComponentCanvas == (Canvas)tempCanvas.Parent) {
                                    eventChannelToConnect.ListenerComponentId = tempComponent.id;
                                    break;
                                }
                            }
                            eventChannelToConnect.Line.X2 = Canvas.GetLeft((Canvas)tempCanvas.Parent) + Canvas.GetLeft(tempCanvas) + tempCanvas.ActualWidth / 2 + 2;
                            eventChannelToConnect.Line.Y2 = Canvas.GetTop((Canvas)tempCanvas.Parent) + Canvas.GetTop(tempCanvas) + tempCanvas.Height;
                            connectedChannelLastClick = true;
                            if (AddEventChannelCommand(eventChannelToConnect, true) == false)
                            {
                                canvas.Children.Remove(eventChannelToConnect.Line);
                            } else
                            {
                                CommandObject co = new CommandObject("Delete", eventChannelToConnect);
                                undoStack.Push(co);
                                redoStack.Clear();
                            }
                            eventChannelToConnect = null;
                            newEventChannelRibbonButton.IsChecked = false;
                            ////dockManager.ActiveContent = dockableEventsTab;
                        }
                    }
                }
                else { // drop the eventChannelLine
                    if (eventChannelToConnect != null) {
                        canvas.Children.Remove(eventChannelToConnect.Line);
                        eventChannelToConnect = null;
                        newEventChannelRibbonButton.IsChecked = false;
                    }
                }
            }
            else if ((newEventChannelRibbonButton.IsChecked == false) && (eventChannelToConnect != null)) {
                // click on the empty canvas, eventChannel will be deleted instead of connected
                canvas.Children.Remove(eventChannelToConnect.Line);
                eventChannelToConnect = null;
                newEventChannelRibbonButton.IsChecked = false;
            }
        }


        /////  *** begin patch for easier selection of channels and ports

        //Compute the dot product AB . AC
        private double DotProduct(double[] pointA, double[] pointB, double[] pointC)
        {
            double[] AB = new double[2];
            double[] BC = new double[2];
            AB[0] = pointB[0] - pointA[0];
            AB[1] = pointB[1] - pointA[1];
            BC[0] = pointC[0] - pointB[0];
            BC[1] = pointC[1] - pointB[1];
            double dot = AB[0] * BC[0] + AB[1] * BC[1];

            return dot;
        }

        //Compute the cross product AB x AC
        private double CrossProduct(double[] pointA, double[] pointB, double[] pointC)
        {
            double[] AB = new double[2];
            double[] AC = new double[2];
            AB[0] = pointB[0] - pointA[0];
            AB[1] = pointB[1] - pointA[1];
            AC[0] = pointC[0] - pointA[0];
            AC[1] = pointC[1] - pointA[1];
            double cross = AB[0] * AC[1] - AB[1] * AC[0];

            return cross;
        }

        //Compute the distance from A to B
        double Distance(double[] pointA, double[] pointB)
        {
            double d1 = pointA[0] - pointB[0];
            double d2 = pointA[1] - pointB[1];

            return Math.Sqrt(d1 * d1 + d2 * d2);
        }

        //Compute the distance from AB to C
        //if isSegment is true, AB is a segment, not a line.
        double LineToPointDistance2D(double[] pointA, double[] pointB, double[] pointC,
            bool isSegment)
        {
            double dist = CrossProduct(pointA, pointB, pointC) / Distance(pointA, pointB);
            if (isSegment)
            {
                double dot1 = DotProduct(pointA, pointB, pointC);
                if (dot1 > 0)
                    return Distance(pointB, pointC);

                double dot2 = DotProduct(pointB, pointA, pointC);
                if (dot2 > 0)
                    return Distance(pointA, pointC);
            }
            return Math.Abs(dist);
        }

        Polygon selectedEventPort = null;
        const int SELECTED_PORT_THICKNESS = 4;
        const int SELECTED_EVENTPORT_THICKNESS = 3;
        const int SELECTED_LINE_THICKNESS = 4;
        const int UNSELECTED_LINE_THICKNESS = 2;
        const int LINE_SELECTION_DISTANCE = 20;
      
        /////  *** end patch for easier selection of channels and ports
        
        /// <summary>
        /// Mouse move on the canvas. If a channel or an eventchannel has been created, the line wil be drawn to the mouse pointer
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>

        private void OnMouseMove(object sender, MouseEventArgs args) {

            /////  *** begin patch for easier selection of channels and ports

            Line foundLine = null;
            double minDist = 100000, actDist=0;
            foreach (object o in canvas.Children)
            {
                if (o is Line)
                {
                    Line lin = (Line)o;
                    lin.StrokeThickness = UNSELECTED_LINE_THICKNESS;  // reset prior selections ! 

                    double[] pointA={lin.X1,lin.Y1};
                    double[] pointB={lin.X2,lin.Y2};
                    double[] pointC={args.GetPosition(canvas).X,args.GetPosition(canvas).Y};

                    actDist = LineToPointDistance2D(pointA, pointB, pointC, true);
                    if ((actDist < LINE_SELECTION_DISTANCE) && (actDist < minDist))
                    {
                        minDist=actDist;
                        foundLine = lin;
                    }
                }
            }

            if (selectedEventPort != null)
            {
                selectedEventPort.StrokeThickness = 1;  // reset prior selections
                selectedEventPort = null;
            }

            if (args.Source is Polygon)
            {
                Polygon p = (Polygon)args.Source;
                if ((p.IsMouseOver) && (channelToConnect == null))
                {
                    p.StrokeThickness = SELECTED_EVENTPORT_THICKNESS;
                    selectedEventPort = p;
                    foundLine = null;
                }
            }

            foreach (componentType tempComponent in deploymentComponentList.Values.ToList())
            {

                foreach (object o in tempComponent.PortsList.Values)
                {
                    if (o is outputPortType)
                    {
                        if ((((outputPortType)o).PortRectangle.IsMouseOver) && (channelToConnect == null))
                        {
                            ((outputPortType)o).PortRectangle.StrokeThickness = SELECTED_PORT_THICKNESS;
                            foundLine = null;
                        }
                        else ((outputPortType)o).PortRectangle.StrokeThickness = 1;
                    }
                    else if (o is inputPortType)
                    {
                        if ((((inputPortType)o).PortRectangle.IsMouseOver) && (channelToConnect != null))
                        {
                            ((inputPortType)o).PortRectangle.StrokeThickness = SELECTED_PORT_THICKNESS;
                            foundLine = null;
                        }
                        else ((inputPortType)o).PortRectangle.StrokeThickness = 1;
                    }
                }
            }

            if (channelToConnect != null) foundLine = channelToConnect.Line;
            if (eventChannelToConnect != null) foundLine = eventChannelToConnect.Line;

            if (foundLine != null)
            { foundLine.StrokeThickness = SELECTED_LINE_THICKNESS; }

            /////  *** end patch for easier selection of channels and ports


            // if a 'movePlugin' is defined, the plugin will be moved
            //if (componentToMove != null && Mouse.LeftButton == MouseButtonState.Pressed)  
            //MoveComponent(selectedComponentList.First(), (int)args.GetPosition(canvas).X, (int)args.GetPosition(canvas).Y);
            if (selectionRectangle == null && Mouse.LeftButton == MouseButtonState.Pressed && (ModifierKeys.Control & Keyboard.Modifiers) == 0 && mouseMoveComponentX >= 0 && mouseMoveComponentY >= 0) {
                Point actPos = args.GetPosition(canvas);
                MoveSelectedComponents((int)(mouseMoveComponentX - actPos.X), (int)(mouseMoveComponentY - actPos.Y));
                mouseMoveComponentX = (int)actPos.X;
                mouseMoveComponentY = (int)actPos.Y;
            }
            // if a new channel is to be connected (already connected to an out-port, searching for an in-port),
            // the line will be drown to the mouse pointer
            else if (channelToConnect != null) {
                if (channelToConnect.Line.X2 > channelToConnect.Line.X1) {
                    channelToConnect.Line.X2 = args.GetPosition(canvas).X - 1;
                }
                else {
                    channelToConnect.Line.X2 = args.GetPosition(canvas).X + 1;
                }
                if (channelToConnect.Line.Y2 > channelToConnect.Line.Y1) {
                    channelToConnect.Line.Y2 = args.GetPosition(canvas).Y - 1;
                }
                else {
                    channelToConnect.Line.Y2 = args.GetPosition(canvas).Y + 1;
                }
            }
            else if (eventChannelToConnect != null) {
                if (eventChannelToConnect.Line.X2 > eventChannelToConnect.Line.X1) {
                    eventChannelToConnect.Line.X2 = args.GetPosition(canvas).X - 1;
                }
                else {
                    eventChannelToConnect.Line.X2 = args.GetPosition(canvas).X + 1;
                }
                if (eventChannelToConnect.Line.Y2 > eventChannelToConnect.Line.Y1) {
                    eventChannelToConnect.Line.Y2 = args.GetPosition(canvas).Y - 1;
                }
                else {
                    eventChannelToConnect.Line.Y2 = args.GetPosition(canvas).Y + 1;
                }
            }
            //check if the user is trying to select multiple items with a rectangle
            if (selectionRectangle != null && canvas.Children.Contains(selectionRectangle)) {
                // calculate the position for the selection Rectangle
                Point mousePoint = args.GetPosition(canvas);
                if (mousePoint.X < selRectStartPoint.X) { // new point is on the leftside of the start point
                    Canvas.SetLeft(selectionRectangle, mousePoint.X);
                }
                else
                    Canvas.SetLeft(selectionRectangle, selRectStartPoint.X);
                if (mousePoint.Y < selRectStartPoint.Y)
                    Canvas.SetTop(selectionRectangle, mousePoint.Y);
                else
                    Canvas.SetTop(selectionRectangle, selRectStartPoint.Y);
                selectionRectangle.Width = Math.Abs(mousePoint.X - selRectStartPoint.X);
                selectionRectangle.Height = Math.Abs(mousePoint.Y - selRectStartPoint.Y);
            }
        }

        /// <summary>
        /// Changing the Zoom-Slider value when the mouse wheele is used and Crtl is pressed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void Zoom_MouseWheele(object sender, MouseWheelEventArgs e) {
            if (e.Delta != 0 && Keyboard.Modifiers == ModifierKeys.Control) {
                if (e.Delta > 0) {
                    zoomSlider.Value += 0.1;
                } else {
                    zoomSlider.Value -= 0.1;
                }
                e.Handled = true;
            }
        }

        /// <summary>
        /// Function is called, when a mouse-button is pressed on an item in the Recently Opened Files
        /// </summary>
        void recentFileItem_MouseDown(object sender, MouseEventArgs e) {
            RibbonGalleryItem rf = (RibbonGalleryItem)sender;
            if (File.Exists((String)rf.Tag)) {
                CheckIfSavedAndOpenCommand((String)rf.Tag);
            }
            else {
                RemoveFromRecentList((String)rf.Tag);
                MessageBox.Show(Properties.Resources.RecentFileNotFound, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
            e.Handled = true;
        }

        #endregion // Mouse listeners

        #region Internal functions

        // Read the Application arguments - used after a .acs file has been double clicked to start ACS
        /// <summary>
        /// Function called, when the MainWindow will be loaded. Used to read the application arguments (filename) and load the file by using LoadComponentsCommand()
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void MainWindow_Loaded(object sender, RoutedEventArgs e) {
            if (Application.Current.Properties["ArbitraryArgName"] != null) {
                string fname = Application.Current.Properties["ArbitraryArgName"].ToString();
                // check if the file is valid against the deployment_schema
                String xmlError;
                XmlValidation xv = new XmlValidation();
                string dsFile = ini.IniReadValue("model", "deployment_schema");
                if (!File.Exists(dsFile)) {
                    dsFile = AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "deployment_schema");
                }
                xmlError = xv.validateXml(fname, dsFile);
                if (xmlError.Equals("")) {
                    XmlSerializer ser2 = new XmlSerializer(typeof(model));
                    StreamReader sr2 = new StreamReader(fname);
                    deploymentModel = (model)ser2.Deserialize(sr2);
                    sr2.Close();

                    ResetPropertyDock();
                    ModelVersionUpdater.UpdateMissingGUI(this, deploymentModel, componentList);
                    ModelVersionUpdater.UpdateToCurrentVersion(this, deploymentModel);
                    LoadComponentsCommand();

                    // set the saveFile in order to still know the filename when trying to save the schema again
                    SetSaveFile(fname);
                    AddToRecentList(fname);
                }
                else {
                    MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, "xmlError Msg:" + xmlError);
                }
            }
            TimerCallback tcbFocus = this.DoFocusTimer;
            focusTimer = new Timer(tcbFocus, dockableComponentProperties, 300, Timeout.Infinite);
        }


        /// <summary>
        /// Set the filename and also update the title of the application (showing the filename)
        /// </summary>
        /// <param name="filename">The new filename to set. Can also be null</param>
        private void SetSaveFile(string filename) {
            saveFile = filename;
            if (saveFile != null) {
                // showing the name of the model in the title of the window
                this.Title = System.IO.Path.GetFileNameWithoutExtension(saveFile) + " - " + Properties.Resources.MainWindowTitle;
            }
            else {
                this.Title = Properties.Resources.MainWindowTitle;
            }
        }

        /// <summary>
        /// Generating the name for the model. This name will be the default model name
        /// </summary>
        /// <returns>the model name</returns>
        public static string GenerateModelName() {
            DateTime dt = DateTime.UtcNow;
            return dt.ToShortDateString() + "_" + dt.Hour + dt.Minute;
        }

        /// <summary>
        /// Check, if the connection is valid, or disconnect, if the connection is terminated
        /// </summary>
        /// <returns>True, if the connection is open</returns>
        private bool CheckASAPIConnection() {
            if ((asapiClient != null) && (!asapiClient.OutputProtocol.Transport.IsOpen)) {
                MessageBox.Show(Properties.Resources.CheckASAPIConnectionText, Properties.Resources.CheckASAPIConnectionHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Disconnect_Click(null, null);
                return false;
            }
            else {
                int ret = 0;
                try {
                    ret = asapiClient.Ping();
                } catch (Exception ae) {
                    traceSource.TraceEvent(TraceEventType.Error, 3, ae.Message);
                } finally {
                    MessageBox.Show(Properties.Resources.CheckASAPIConnectionText, Properties.Resources.CheckASAPIConnectionHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    Disconnect_Click(null, null);
                }

                if (ret == 1) {
                    return true;
                }
                MessageBox.Show(Properties.Resources.CheckASAPIConnectionText, Properties.Resources.CheckASAPIConnectionHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                Disconnect_Click(null, null);
                return false;
            }
        }

        /// <summary>
        /// Reset the ACS. All lists, arrays, canvas, propertydock, etc. will be cleared
        /// </summary>
        private void CleanACS() {
            // reset component list, channel list and canvas
            deploymentComponentList.Clear();
            deploymentChannelList.Clear();
            eventChannelList.Clear();
            eventChannelLinesList.Clear();
            canvas.Children.Clear();
            groupsList.Clear();
            // initialize the model for drawing a new schema
            deploymentModel = new model();
            deploymentModel.channels = new channel[1];
            deploymentModel.eventChannels = new eventChannel[1];
            deploymentModel.components = new componentType[1];
            // clear the undo/redo stack
            undoStack.Clear();
            redoStack.Clear();
            ResetPropertyDock();
            statusList.Clear();
            // clean the GUI editor
            NewAREGUIWindow();
            CleanGUICanvas();            
            if (areStatus.Status == AREStatus.ConnectionStatus.Synchronised) {
                areStatus.Status = AREStatus.ConnectionStatus.Connected;
            }
        }

        /// <summary>
        /// Init the AREGUIWindow. Also the default values of the ARE GUI are defined here
        /// </summary>
        private void NewAREGUIWindow() {
            deploymentModel.modelGUI = new modelGUIType();
            deploymentModel.modelGUI.AREGUIWindow = new guiType();
            
            deploymentModel.modelGUI.AREGUIWindow.height = "5000";
            deploymentModel.modelGUI.AREGUIWindow.width = "9000";
            deploymentModel.modelGUI.AREGUIWindow.posX = "0";
            deploymentModel.modelGUI.AREGUIWindow.posY = "0";
            deploymentModel.modelGUI.AlwaysOnTop = false;
            deploymentModel.modelGUI.Decoration = true;
            deploymentModel.modelGUI.Fullscreen = false;
            deploymentModel.modelGUI.ShopControlPanel = true;
            deploymentModel.modelGUI.ToSystemTray = false;

        }

        /// <summary>
        /// Load the bundle-model (all available components) to the ribbon menu
        /// </summary>
        private void LoadBundle(String pathToBundleFile) {
            // check, if model is valid against the deployment_model schema
            String xmlError;
            string fName;
            try {
                if (pathToBundleFile == null) {
                    if (File.Exists(ini.IniReadValue("model", "bundle_model_startup"))) {
                        fName = ini.IniReadValue("model", "bundle_model_startup");
                    } else if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_model"))) {
                        fName = AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_model");
                    } else {
                        fName = ini.IniReadValue("model", "bundle_model");
                    }
                } else {
                    fName = pathToBundleFile;
                }
                activeBundle = System.IO.Path.GetFileNameWithoutExtension(fName);
                if (activeBundle == "bundle")
                    activeBundle = "default";

                XmlValidation xv = new XmlValidation();
                //xmlError = xv.validateXml(fName, ini.IniReadValue("model", "bundle_schema"));
                if (File.Exists(ini.IniReadValue("model", "bundle_schema"))) {
                    xmlError = xv.validateXml(fName, ini.IniReadValue("model", "bundle_schema"));
                } else {
                    xmlError = xv.validateXml(fName, AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_schema"));
                }
            } catch (Exception ex) {
                MessageBox.Show(Properties.Resources.ReadBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error); // called twice to be shown once. Some kind of initialisation problem
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                xmlError = "Error opening bundle file";
                fName = "";
            }

            // if valid, xml-file will be written
            if (xmlError.Equals("")) {
                try {
                    XmlSerializer ser = new XmlSerializer(typeof(Asterics.ACS2.componentTypes));
                    //string fName;
                    //if (File.Exists(ini.IniReadValue("model", "bundle_model"))) {
                    //    fName = ini.IniReadValue("model", "bundle_model");
                    //}
                    //else {
                    //    fName = AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "bundle_model");
                    //}
                    StreamReader sr = new StreamReader(fName);
                    Asterics.ACS2.componentTypes allComponents = (Asterics.ACS2.componentTypes)ser.Deserialize(sr);
                    sr.Close();
                    autoCompleteTextBox.ClearItems();
                    foreach (object o in allComponents.componentType) {
                        if (o is Asterics.ACS2.componentTypesComponentType) {
                            Asterics.ACS2.componentTypesComponentType comp = (Asterics.ACS2.componentTypesComponentType)o;
                            comp.InitGraphPorts(comp.id);
                            componentList.Add(comp.id, comp);
                            AddSearchSuggestion(comp.id,comp.description);
                        }
                    }
                    foreach (Asterics.ACS2.componentTypesComponentType component in componentList.Values) {
                        // making each element in the plugin selection focusable
                        component.ComponentCanvas.Focusable = true;
                    }

                    // setting the ribbon 'components', adding the components to the
                    // four categories
                    actuatorDropDown.Items.Clear();
                    processorDropDown.Items.Clear();
                    sensorDropDown.Items.Clear();
                    specialDropDown.Items.Clear();
                    groupDropDown.Items.Clear();
                    Dictionary<string, RibbonSplitMenuItem> actuatorSubmenus = new Dictionary<string, RibbonSplitMenuItem>();
                    Dictionary<string, RibbonSplitMenuItem> processorSubmenus = new Dictionary<string, RibbonSplitMenuItem>();
                    Dictionary<string, RibbonSplitMenuItem> sensorSubmenus = new Dictionary<string, RibbonSplitMenuItem>();
                    Dictionary<string, RibbonSplitMenuItem> specialSubmenus = new Dictionary<string, RibbonSplitMenuItem>();
                    foreach (RibbonSplitMenuItem rsmi in actuatorSubmenus.Values) {
                        if (rsmi != null)
                            rsmi.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
                    }
                    foreach (Asterics.ACS2.componentTypesComponentType component in componentList.Values) {
                        RibbonApplicationSplitMenuItem i = new RibbonApplicationSplitMenuItem();
                        string header = component.id;
                        header = TrimComponentName(header);
                        i.Header = header;
                        i.Click += AddComponentFromRibbonMenu;
                        i.CommandParameter = component.id;
                        i.ToolTipTitle = component.description;
                        RibbonSplitMenuItem rmi = new RibbonSplitMenuItem();
                        rmi.StaysOpenOnClick = true;
                        rmi.Height = 37;
                        rmi.Header = component.type.subtype;

                        switch (component.type.Value) {
                            case Asterics.ACS2.componentTypeDataTypes.actuator:
                                if (component.type.subtype == null || component.type.subtype.Equals(""))
                                    actuatorDropDown.Items.Add(i);
                                else {
                                    if (actuatorSubmenus.ContainsKey(component.type.subtype) == false) {
                                        rmi.Items.Add(i);
                                        actuatorDropDown.Items.Add(rmi);
                                        actuatorSubmenus.Add(component.type.subtype, rmi);
                                    }
                                    else {
                                        rmi = actuatorSubmenus[component.type.subtype];
                                        rmi.Items.Add(i);
                                    }
                                }
                                break;
                            case Asterics.ACS2.componentTypeDataTypes.processor:
                                if (component.type.subtype == null || component.type.subtype.Equals(""))
                                    processorDropDown.Items.Add(i);
                                else {
                                    if (processorSubmenus.ContainsKey(component.type.subtype) == false) {
                                        rmi.Items.Add(i);
                                        processorDropDown.Items.Add(rmi);
                                        processorSubmenus.Add(component.type.subtype, rmi);
                                    }
                                    else {
                                        rmi = processorSubmenus[component.type.subtype];
                                        rmi.Items.Add(i);
                                    }
                                }
                                break;
                            case Asterics.ACS2.componentTypeDataTypes.sensor:
                                if (component.type.subtype == null || component.type.subtype.Equals(""))
                                    sensorDropDown.Items.Add(i);
                                else {
                                    if (sensorSubmenus.ContainsKey(component.type.subtype) == false) {
                                        rmi.Items.Add(i);
                                        sensorDropDown.Items.Add(rmi);
                                        sensorSubmenus.Add(component.type.subtype, rmi);
                                    }
                                    else {
                                        rmi = sensorSubmenus[component.type.subtype];
                                        rmi.Items.Add(i);
                                    }
                                }
                                break;
                            case Asterics.ACS2.componentTypeDataTypes.special:
                                if (component.type.subtype == null || component.type.subtype.Equals(""))
                                    specialDropDown.Items.Add(i);
                                else {
                                    if (specialSubmenus.ContainsKey(component.type.subtype) == false) {
                                        rmi.Items.Add(i);
                                        specialDropDown.Items.Add(rmi);
                                        specialSubmenus.Add(component.type.subtype, rmi);
                                    }
                                    else {
                                        rmi = specialSubmenus[component.type.subtype];
                                        rmi.Items.Add(i);
                                    }
                                }
                                break;
                        }
                    }
                    // Sorting the lists alphabetically
                    sensorDropDown.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
                    actuatorDropDown.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
                    processorDropDown.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
                    specialDropDown.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
                    // sort Submenus
                    sortComponentSubmenu(actuatorSubmenus.Values.ToArray());
                    sortComponentSubmenu(sensorSubmenus.Values.ToArray());
                    sortComponentSubmenu(processorSubmenus.Values.ToArray());
                    sortComponentSubmenu(specialSubmenus.Values.ToArray());
                    //move others at the end of the submenus
                    moveOthersMenuItemBack(sensorDropDown);
                    moveOthersMenuItemBack(processorDropDown);
                    moveOthersMenuItemBack(actuatorDropDown);
                    moveOthersMenuItemBack(specialDropDown);
                    if (pathToBundleFile != null) {
                        MessageBox.Show(Properties.Resources.ReadBundleText, Properties.Resources.ReadBundleHeader, MessageBoxButton.OK, MessageBoxImage.Information);
                    }
                } catch (Exception e) {
                    actuatorDropDown.Items.Clear();
                    processorDropDown.Items.Clear();
                    sensorDropDown.Items.Clear();
                    specialDropDown.Items.Clear();
                    groupDropDown.Items.Clear();
                    componentList.Clear();
                    if (pathToBundleFile == null) {                        
                        MessageBox.Show(Properties.Resources.ReadBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        MessageBox.Show(Properties.Resources.ReadBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error); // called twice to be shown once. Some kind of initialisation problem
                        traceSource.TraceEvent(TraceEventType.Error, 3, e.Message);
                        //Application.Current.Shutdown();
                        //Environment.Exit(0);
                    } else {
                        MessageBox.Show(Properties.Resources.ReadDownloadedBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        componentList.Clear();
                        LoadBundle(null);
                    }
                }

                // loading the presaved groups
                string errorStr = "unknown";
                try {                    
                    string[] filesInGroupsFolder = null;
                    if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                        if (Directory.Exists(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData)+"\\AsTeRICS\\ACS\\groups\\")) {
                            filesInGroupsFolder = Directory.GetFiles(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData)+"\\AsTeRICS\\ACS\\groups\\", "*.agr");
                        }
                    } else {
                        if (Directory.Exists(AppDomain.CurrentDomain.BaseDirectory + "\\groups\\")) {
                            filesInGroupsFolder = Directory.GetFiles(AppDomain.CurrentDomain.BaseDirectory + "\\groups\\", "*.agr");
                        }
                    }
                    if (filesInGroupsFolder != null) {
                        foreach (string filename in filesInGroupsFolder) {
                            errorStr = filename;
                            RibbonApplicationSplitMenuItem i = new RibbonApplicationSplitMenuItem();
                            string header = filename.Substring(filename.LastIndexOf('\\') + 1);
                            i.Header = header.Substring(0, header.LastIndexOf('.'));
                            i.Click += AddGroupFromRibbonMenu;
                            i.CommandParameter = filename;
                            groupDropDown.Items.Add(i);
                            
                        }
                    }
                } catch (Exception e) {
                    MessageBox.Show(Properties.Resources.GroupingErrorReadingGroupFormat(errorStr), Properties.Resources.GroupingErrorReadingGroupHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, e.Message);
                }

            } else {
                actuatorDropDown.Items.Clear();
                processorDropDown.Items.Clear();
                sensorDropDown.Items.Clear();
                specialDropDown.Items.Clear();
                groupDropDown.Items.Clear();
                componentList.Clear();
                if (pathToBundleFile == null) {
                    MessageBox.Show(Properties.Resources.ReadBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    MessageBox.Show(Properties.Resources.ReadBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error); // called twice to be shown once. Some kind of initialisation problem
                    traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                    //Application.Current.Shutdown();
                    //Environment.Exit(0);
                } else {
                    MessageBox.Show(Properties.Resources.ReadDownloadedBundleErrorText, Properties.Resources.ReadBundleErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    componentList.Clear();
                    LoadBundle(null);
                }

            }
        }

        private void AddSearchSuggestion(string name, string description) {
            string tmpName = name.Replace("asterics.", "");
            if (tmpName == "Averager")
            {
                int k = 17;
                k++;
            }
            AutoCompleteEntry entry = new AutoCompleteEntry(tmpName,name);
            entry.ToolTip = description;
            List<string> keywords = new List<string>();
            for (int i = 0; i < tmpName.Length; i++)
            {
                keywords.Add(tmpName.Substring(i));
            }
            entry.KeywordStrings = (string[])keywords.ToArray();
            autoCompleteTextBox.AddItem(entry);
        }

        private void searchItemSelected()
        {
            foreach (AutoCompleteEntry entry in autoCompleteTextBox.Items) 
            {
                if (entry.DisplayName.Replace("asterics.", "") == autoCompleteTextBox.Text)
                {
                    AddComponent(entry.Key, false, true, false);
                }
            }
            autoCompleteTextBox.Text = "";
        }

        private void showStuff(Object sender, RoutedEventArgs e)
        {
            sensorDropDown.IsDropDownOpen = true;
        }
        /// <summary>
        /// Download a model from ARE to the canvas and check, if the model is valid
        /// </summary>
        private void DownloadAndCheckModel() {
            try {
                String xmlModel = asapiClient.GetModel();
                if (xmlModel != null && xmlModel != "") {

                    XmlSerializer ser2 = new XmlSerializer(typeof(model));
                    StringReader sr2 = new StringReader(xmlModel);
                    deploymentModel = (model)ser2.Deserialize(sr2);
                    sr2.Close();


                    // Validate, if downlaoded schema is valid
                    // Should be valid, is double-check
                    XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
                    // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
                    FileStream str = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
                    x.Serialize(str, deploymentModel);
                    str.Close();

                    // check, if model is valid against the deployment_model schema
                    String xmlError;
                    XmlValidation xv = new XmlValidation();
                    xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

                    // if valid, xml-file will be written
                    if (xmlError.Equals("")) {
                        ResetPropertyDock();
                        LoadComponentsCommand();

                        areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                    }
                    else {
                        MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                    }
                }
                else {
                    CleanACS();
                }
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.SynchronisationDownloadError, Properties.Resources.SynchronisationDownloadErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                CleanACS();
            }
            SetSaveFile(null);
        }

        /// <summary>
        /// Converting the active model to a string (and check validity of the model)
        /// </summary>
        /// <returns>The model as string</returns>
        private string ConvertDeploymentModelToValidString() {
            if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 0)) {
                deploymentModel.eventChannels = null;
            }
            else if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 1) && (deploymentModel.eventChannels[0] == null)) {
                deploymentModel.eventChannels = null;
            }

            // validation of the model before sending it to the ARE
            // model should be valid, this is a double-check
            XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
            // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
            FileStream strVal = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
            x.Serialize(strVal, deploymentModel);
            strVal.Close();

            // check, if model is valid against the deployment_model schema
            String xmlError;
            XmlValidation xv = new XmlValidation();
            xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));

            // if valid, xml-file will be written
            if (xmlError.Equals("")) {
                x = new XmlSerializer(deploymentModel.GetType());
                StringWriter str = new StringWriter();
                x.Serialize(str, RemoveGroupingElementsInDeployment(deploymentModel));
                return str.ToString();
            }
            else {
                MessageBox.Show(Properties.Resources.XmlValidErrorText, Properties.Resources.XmlValidErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
            }
            return "";
        }


        /// <summary>
        /// Reset the property dock: Clear all elements inside the property dock
        /// </summary>
        private void ResetPropertyDock() {
            dockableComponentProperties.ContainerPane.SelectedItem = dockableComponentProperties.ContainerPane.Items[0];
            propertyDockScrollViewer.Content = null;
            dockEventGrid.RowDefinitions.Clear();
            dockEventGrid.Children.Clear();
            dockableInportsTab.Visibility = Visibility.Collapsed;
            dockableOutportsTab.Visibility = Visibility.Collapsed;
            dockableEventsTab.Visibility = Visibility.Collapsed;
            dockableEventListenerTab.Visibility = Visibility.Collapsed;
            dockableEventTriggerTab.Visibility = Visibility.Collapsed;
            //dockManager.ActiveDocument = dockableComponentProperties;
        }


        /// <summary>
        /// Creating an unique (within the model) string id for the channels
        /// </summary>
        /// <returns>Unique string id</returns>
        private string NewIdForChannel() {
            int count = deploymentChannelList.Count;
            string retString = "";
            do {
                retString = "binding." + count.ToString();
                count++;
            } while (deploymentChannelList.ContainsKey(retString));
            return retString;
        }

        /// <summary>
        /// Creating an unique (within the model) string id for the channel, being connected to a grouping element
        /// </summary>
        /// <returns>Unique string id</returns>
        private string NewIdForGroupChannel() {
            int count = deploymentChannelList.Count;
            string retString = "";
            do {
                retString = "groupbinding." + count.ToString();
                count++;
            } while (deploymentChannelList.ContainsKey(retString));
            return retString;
        }

        /// <summary>
        /// Initialise the proprety dock (dockManager of AvalonDock)
        /// </summary>
        void BuildDockingLayout() {
            dockManager.Content = null;

            // The name properties are needed to save and restore the layout
            dockManager.Name = "dockManagerName";

            BitmapImage propertiesIcon = new BitmapImage();
            propertiesIcon.BeginInit();
            propertiesIcon.UriSource = new Uri(@"/images/Properties_Icon.png", UriKind.RelativeOrAbsolute);
            propertiesIcon.EndInit();

            BitmapImage eventsIcon = new BitmapImage();
            eventsIcon.BeginInit();
            eventsIcon.UriSource = new Uri(@"/images/event.png", UriKind.RelativeOrAbsolute);
            eventsIcon.EndInit();

            dockableComponentProperties = new DockableContent() {
                Title = Properties.Resources.DockProperties,
                Content = null,
                Name = "dockComponentName",
                Icon = propertiesIcon
            };

            propertyDockScrollViewer = new ScrollViewer();
            propertyDockScrollViewer.HorizontalScrollBarVisibility = ScrollBarVisibility.Auto;
            propertyDockScrollViewer.VerticalScrollBarVisibility = ScrollBarVisibility.Auto;

            StackPanel sp = new StackPanel();
            dockableComponentProperties.Content = sp;
            sp.Children.Add(propertyDockScrollViewer);
            dockableComponentProperties.Show(dockManager, AnchorStyle.Right);
            dockableComponentProperties.IsCloseable = false;

            scrollCanvars = new DocumentContent() {
                Title = Properties.Resources.DockMainWindow,
                Content = scrollViewer,
                Name = "dockDeploymentWindowName"
            };
            scrollCanvars.IsCloseable = false;
            scrollCanvars.Show(dockManager);

            // creating the tab for the gui designer
            InitGUITab();


            //TreeView dockable content
            dockableInportsList = new TreeView();
            BrushConverter bc = new BrushConverter();
            dockableInportsList.Background = (Brush)bc.ConvertFrom("#FFFFFFFF");
            dockableInportsList.IsTabStop = true;
            //dockableInportsList.Background = (Brush)bc.ConvertFrom("#FFE9ECFA");
            dockableOutportsList = new TreeView();
            dockableOutportsList.Background = (Brush)bc.ConvertFrom("#FFFFFFFF");
            //dockableOutportsList.Background = (Brush)bc.ConvertFrom("#FFE9ECFA");

            dockableInportsTab = new DockableContent() {
                Title = Properties.Resources.DockInPorts,
                Content = dockableInportsList,
                Visibility = Visibility.Collapsed,
                Name = "inputPortsName",
                Icon = propertiesIcon,
                IsCloseable = false
            };
            dockableOutportsTab = new DockableContent() {
                Title = Properties.Resources.DockOutPorts,
                Content = dockableOutportsList,
                Visibility = Visibility.Collapsed,
                Name = "outputPortsName",
                Icon = propertiesIcon,
                IsCloseable = false
            };


            // EventListenerGrid
            eventListenerDockScrollViewer = new ScrollViewer();
            eventListenerDockScrollViewer.HorizontalScrollBarVisibility = ScrollBarVisibility.Auto;
            eventListenerDockScrollViewer.VerticalScrollBarVisibility = ScrollBarVisibility.Auto;
            dockableEventListenerTab = new DockableContent() {
                Title = Properties.Resources.PropertyDockEventListenerTab,
                Content = eventListenerDockScrollViewer,
                Visibility = Visibility.Collapsed,
                Name = "eventListenerName",
                Icon = eventsIcon,
                IsCloseable = false
            };
            eventListenerDockScrollViewer.SizeChanged += dockableEventListenerTriggerTab_SizeChanged;

            // EventTriggerGrid
            eventTriggerDockScrollViewer = new ScrollViewer();
            eventTriggerDockScrollViewer.HorizontalScrollBarVisibility = ScrollBarVisibility.Auto;
            eventTriggerDockScrollViewer.VerticalScrollBarVisibility = ScrollBarVisibility.Auto;
            dockableEventTriggerTab = new DockableContent() {
                Title = Properties.Resources.PropertyDockEventTriggerTab,
                Content = eventTriggerDockScrollViewer,
                Visibility = Visibility.Collapsed,
                Name = "eventTriggerName",
                Icon = eventsIcon,
                IsCloseable = false
            };
            eventTriggerDockScrollViewer.SizeChanged += dockableEventListenerTriggerTab_SizeChanged;


            // EventGrid
            dockEventGrid = new Grid();
            dockEventGrid.MinWidth = 200;
            dockEventGrid.Background = new SolidColorBrush(Colors.White);
            //dockEventGrid.Height = 100;
            ColumnDefinition evtChnlDescriptionColDef = new ColumnDefinition();
            ColumnDefinition labelColDef = new ColumnDefinition();
            ColumnDefinition comboColDef = new ColumnDefinition();
            dockEventGrid.ColumnDefinitions.Add(labelColDef);
            dockEventGrid.ColumnDefinitions.Add(comboColDef);
            dockEventGrid.ColumnDefinitions.Add(evtChnlDescriptionColDef);
            dockEventGrid.HorizontalAlignment = HorizontalAlignment.Left;
            dockEventGrid.VerticalAlignment = VerticalAlignment.Top;
            dockEventGrid.Margin = new Thickness(4, 4, 4, 4);
            ScrollViewer eventDockScrollViewer = new ScrollViewer();
            eventDockScrollViewer.HorizontalScrollBarVisibility = ScrollBarVisibility.Auto;
            eventDockScrollViewer.VerticalScrollBarVisibility = ScrollBarVisibility.Auto;
            eventDockScrollViewer.Content = dockEventGrid;

            dockableEventsTab = new DockableContent() {
                Title = Properties.Resources.DockEvents,
                Content = eventDockScrollViewer,
                Visibility = Visibility.Collapsed,
                Name = "events",
                Icon = eventsIcon,
                IsCloseable = false
            };
            eventDockScrollViewer.SizeChanged += dockableEventListenerTriggerTab_SizeChanged;

            dockableComponentProperties.ContainerPane.Items.Add(dockableInportsTab);
            dockableComponentProperties.ContainerPane.Items.Add(dockableOutportsTab);
            dockableComponentProperties.ContainerPane.Items.Add(dockableEventsTab);
            dockableComponentProperties.ContainerPane.Items.Add(dockableEventListenerTab);
            dockableComponentProperties.ContainerPane.Items.Add(dockableEventTriggerTab);

            sp.SizeChanged += StackPanel_SizeChanged;

            // Component "Toolbox" on left side
            // under development, do NOT delete
            //DockableContent dockableComponentToolbox = new DockableContent() {
            //    Title = "Components", Content = null, Name = "dockComponentToolbox"
            //};
            //dockableComponentToolbox.Show(dockManager, AnchorStyle.Left);
            //TreeView dockableComponentList = new TreeView();

            //dockableComponentList.Items.Add(new TreeViewItem() {
            //    Header = "Sensors", Background = new SolidColorBrush(Colors.Beige)//, ItemContainerStyle
            //});
            //dockableComponentList.Items.Add(new TreeViewItem() {
            //    Header = "Processors"
            //});
            //dockableComponentList.Items.Add(new TreeViewItem() {
            //    Header = "Actuators"
            //});
            //dockableComponentList.Items.Add(new TreeViewItem() {
            //    Header = "Special"
            //});
            //dockableComponentToolbox.Content = dockableComponentList;

            //foreach (Asterics.ACS2.componentTypesComponentType component in componentList.Values) {
            //    switch (component.type) {
            //        case Asterics.ACS2.componentType.actuator:
            //            ((TreeViewItem)dockableComponentList.Items[2]).Items.Add(new TreeViewItem {
            //                Header = component.id, Margin = new Thickness(-20, 0, 0, 0)
            //            });
            //            break;
            //        case Asterics.ACS2.componentType.processor:
            //            ((TreeViewItem)dockableComponentList.Items[1]).Items.Add(new TreeViewItem {
            //                Header = component.id, Margin = new Thickness(-20, 0, 0, 0)
            //            });
            //            break;
            //        case Asterics.ACS2.componentType.sensor:
            //            ((TreeViewItem)dockableComponentList.Items[0]).Items.Add(new TreeViewItem {
            //                Header = component.id, Margin = new Thickness(-20, 0, 0, 0)
            //            });
            //            break;
            //        case Asterics.ACS2.componentType.special:
            //            ((TreeViewItem)dockableComponentList.Items[3]).Items.Add(new TreeViewItem {
            //                Header = component.id, Margin = new Thickness(-20, 0, 0, 0)
            //            });
            //            break;
            //    }
            //}
            // End toolbox
        }


        /// <summary>
        /// Set the property editors and the tabs in the property dock
        /// </summary>
        /// <param name="tempComponent">The component, which will be displayed</param>
        private void SetPropertyDock(componentType tempComponent) {
            if (tempComponent != null) {
                backupIdForPropertyEditor = tempComponent.id;
                WPG.PropertyGrid pe = new WPG.PropertyGrid();
                pe.Instance = tempComponent;
                pe.DisplayName = tempComponent.id;
                pe.ShowPreview = false;

                if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                    pe.Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
                }
                if (dockableComponentProperties.ContainerPane.ActualHeight > 48) {
                    pe.Height = dockableComponentProperties.ContainerPane.ActualHeight - 48;
                }
                propertyDockScrollViewer.Content = pe;

                pe.GotKeyboardFocus += new KeyboardFocusChangedEventHandler(PropertyEditor_GotKeyboardFocus);
                KeyboardNavigation.SetTabNavigation(dockableInportsList, KeyboardNavigationMode.Continue);
                // Keyboard navigation within the list has to be done with the arrow-keys. A better solution might be here:
                // http://social.msdn.microsoft.com/Forums/en/wpf/thread/daca7b71-2893-4564-8379-097aabcdd553


                dockableInportsList.Items.Clear();
                dockableOutportsList.Items.Clear();
                

                foreach (object o in tempComponent.PortsList.Values) {
                    WPG.PropertyGrid pe2 = new WPG.PropertyGrid();
                    pe2.Instance = o;
                    pe2.ShowPreview = false;
                    pe2.IsTabStop = true;
                    //KeyboardNavigation.SetTabNavigation(pe2, KeyboardNavigationMode.Continue);
                    pe2.Margin = new Thickness(-35, 0, 0, 0);
                    if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                        pe2.Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
                    }
                    //pe2.BorderBrush = new SolidColorBrush(Colors.Black);
                    //pe2.BorderThickness = new Thickness(1, 1, 1, 1);

                    if (o is inputPortType) {
                        dockableInportsList.Items.Add(new TreeViewItem() {
                            Header = ((inputPortType)o).portTypeID , IsTabStop = true, IsExpanded = true
                        });
                        pe2.DisplayName = ((inputPortType)o).portTypeID;
                        ((TreeViewItem)dockableInportsList.Items[dockableInportsList.Items.Count - 1]).Items.Add(pe2);
                        pe2.TabIndex = dockableInportsList.Items.Count + 58;
                    }
                    else {
                        dockableOutportsList.Items.Add(new TreeViewItem() {
                            Header = ((outputPortType)o).portTypeID, IsTabStop = true, IsExpanded = true
                        });
                        pe2.DisplayName = ((outputPortType)o).portTypeID;
                        ((TreeViewItem)dockableOutportsList.Items[dockableOutportsList.Items.Count - 1]).Items.Add(pe2);
                    }
                }
                dockableComponentProperties.Visibility = Visibility.Visible;
                // Tab 'Input Ports' just available, if the component has input ports
                if (dockableInportsList.Items.Count == 0) {
                    dockableInportsTab.Visibility = Visibility.Collapsed;
                }
                else {
                    dockableInportsTab.Visibility = Visibility.Visible;
                }
                // Tab 'Output Ports' just available, if the component has output ports
                if (dockableOutportsList.Items.Count == 0) {
                    dockableOutportsTab.Visibility = Visibility.Collapsed;
                }
                else {
                    dockableOutportsTab.Visibility = Visibility.Visible;
                }

                // Tab 'Event Listeners' just available, if the component has event listeners
                if (tempComponent.EventListenerList.Count == 0) {
                    dockableEventListenerTab.Visibility = Visibility.Collapsed;
                }
                else {
                    eventListenerDockScrollViewer.Content = SetListnerTriggerList(true, tempComponent);
                    dockableEventListenerTab.Visibility = Visibility.Visible;
                }
                // Tab 'Event Triggers' just available, if the component has event triggers
                if (tempComponent.EventTriggerList.Count == 0) {
                    dockableEventTriggerTab.Visibility = Visibility.Collapsed;
                }
                else {
                    eventTriggerDockScrollViewer.Content = SetListnerTriggerList(false, tempComponent);
                    dockableEventTriggerTab.Visibility = Visibility.Visible;
                }

                dockableEventsTab.Visibility = Visibility.Collapsed;
                dockManager.ActiveDocument = dockableComponentProperties;
            }
        }

        /// <summary>
        /// Sets up a grid with a list of event listeners or triggers, belonging to one component
        /// </summary>
        /// <param name="isListener">True, is list is of event listeners, otherwise event triggers</param>
        /// <param name="modelComp">The component, of thich the events should be listed</param>
        /// <returns></returns>
        private Grid SetListnerTriggerList(bool isListener, componentType modelComp) {
            Grid eventGrid = new Grid();

            eventGrid.MinWidth = 200;
            eventGrid.Background = new SolidColorBrush(Colors.White);
            ColumnDefinition labelListenerColDef = new ColumnDefinition();
            ColumnDefinition comboListenerColDef = new ColumnDefinition();
            eventGrid.ColumnDefinitions.Add(labelListenerColDef);
            eventGrid.ColumnDefinitions.Add(comboListenerColDef);
            eventGrid.HorizontalAlignment = HorizontalAlignment.Left;
            eventGrid.VerticalAlignment = VerticalAlignment.Top;
            eventGrid.Margin = new Thickness(4, 4, 4, 4);

            GridLengthConverter glc = new GridLengthConverter();
            if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                eventGrid.Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
            }

            // Setting the heading for the events Tab
            BrushConverter bc = new BrushConverter();
            eventGrid.RowDefinitions.Add(new RowDefinition() {
                Height = (GridLength)glc.ConvertFromString("28")
            });
            TextBox headingListener = new TextBox() {
                Margin = new Thickness(0, 0, 0, 0),
                FontWeight = FontWeights.Bold,
                FontSize = 12,
                FontFamily = new FontFamily("Segoe UI"),
                IsReadOnly = true,
                Background = (Brush)bc.ConvertFrom("#FFE9ECFA")
            };
            if (isListener) {
                headingListener.Text = Properties.Resources.PropertyDockEventListenerHeader;
            }
            else {
                headingListener.Text = Properties.Resources.PropertyDockEventTriggerHeader;
            }

            TextBox headingTrigger = new TextBox() {
                Text = Properties.Resources.PropertyDockEventDescriptionHeader,
                Margin = new Thickness(0, 0, 0, 0),
                FontWeight = FontWeights.Bold,
                FontSize = 12,
                FontFamily = new FontFamily("Segoe UI"),
                IsReadOnly = true,
                Background = (Brush)bc.ConvertFrom("#FFE9ECFA")
            };
            Grid.SetRow(headingListener, 0);
            Grid.SetColumn(headingListener, 0);
            Grid.SetRow(headingTrigger, 0);
            Grid.SetColumn(headingTrigger, 1);
            eventGrid.Children.Add(headingTrigger);
            eventGrid.Children.Add(headingListener);
            Border headline = new Border();
            Grid.SetColumn(headline, 0);
            Grid.SetRow(headline, 0);
            Grid.SetColumnSpan(headline, 2);
            headline.BorderBrush = (Brush)bc.ConvertFrom("#FFE9ECFA");
            //headline.BorderBrush = Brushes.Black;
            headline.BorderThickness = new Thickness(1);
            eventGrid.Children.Add(headline);
            ArrayList events;
            if (isListener) {
                events = modelComp.EventListenerList;
            }
            else {
                events = modelComp.EventTriggerList;
            }
            int rowCounter = 1;
            foreach (object o in events) {
                eventGrid.RowDefinitions.Add(new RowDefinition() {
                    Height = (GridLength)glc.ConvertFromString("22")
                });
                TextBox elpName = new TextBox() {
                    Margin = new Thickness(0, 0, 0, 0),
                    FontSize = 12,
                    FontFamily = new FontFamily("Segoe UI"),
                    IsReadOnlyCaretVisible = true
                };
                TextBox elpDesc = new TextBox() {
                    Margin = new Thickness(0, 0, 0, 0),
                    FontSize = 12,
                    FontFamily = new FontFamily("Segoe UI"),
                    IsReadOnlyCaretVisible = true
                };
                if (isListener) {
                    elpName.Text = ((EventListenerPort)o).EventListenerId;
                    elpDesc.Text = ((EventListenerPort)o).EventDescription;
                }
                else {
                    elpName.Text = ((EventTriggerPort)o).EventTriggerId;
                    elpDesc.Text = ((EventTriggerPort)o).EventDescription;
                }
                Grid.SetRow(elpName, rowCounter);
                Grid.SetColumn(elpName, 0);
                Grid.SetRow(elpDesc, rowCounter);
                Grid.SetColumn(elpDesc, 1);
                GridSplitter mySimpleGridSplitter = new GridSplitter();
                mySimpleGridSplitter.Background = Brushes.DarkGray;
                mySimpleGridSplitter.HorizontalAlignment = HorizontalAlignment.Right;
                mySimpleGridSplitter.VerticalAlignment = VerticalAlignment.Stretch;
                mySimpleGridSplitter.Width = 1;
                Grid.SetColumn(mySimpleGridSplitter, 0);
                Grid.SetRow(mySimpleGridSplitter, rowCounter);
                eventGrid.Children.Add(elpName);
                eventGrid.Children.Add(elpDesc);
                eventGrid.Children.Add(mySimpleGridSplitter);

                Border top = new Border();
                Grid.SetColumn(top, 0);
                Grid.SetRow(top, rowCounter);
                Grid.SetColumnSpan(top, 2);
                top.BorderBrush = Brushes.DarkGray;
                top.BorderThickness = new Thickness(1);
                eventGrid.Children.Add(top);

                rowCounter++;
            }
            return eventGrid;
        }


        /// <summary>
        /// Set the event tab (and the event lists) in the property dock
        /// </summary>
        /// <param name="sourceComponent">The component with the event triggers</param>
        /// <param name="targetComponent">The component with the event listeners</param>
        private void SetEventPropertyDock(componentType sourceComponent, componentType targetComponent) {
            ArrayList eventTriggers = sourceComponent.EventTriggerList;
            ArrayList eventListeners = targetComponent.EventListenerList;

            // workaround: when dockable event tab is active and will be selected again, the tab is
            // "some kind of" unselected
            dockableComponentProperties.Visibility = Visibility.Visible;
            dockManager.ActiveDocument = dockableComponentProperties;

            // Tab for Events
            dockableComponentProperties.Visibility = Visibility.Collapsed;
            dockableInportsTab.Visibility = Visibility.Collapsed;
            dockableOutportsTab.Visibility = Visibility.Collapsed;
            dockableEventListenerTab.Visibility = Visibility.Collapsed;
            dockableEventTriggerTab.Visibility = Visibility.Collapsed;
            if (eventTriggers.Count > 0 || eventListeners.Count > 0) {
                dockableEventsTab.Visibility = Visibility.Visible;
                // ActiveContent moves focus to dockManager, ActiveDocument just set the selected tab
                dockManager.ActiveDocument = dockableEventsTab;
            }
            else {
                dockableEventsTab.Visibility = Visibility.Collapsed;
            }
            GridLengthConverter glc = new GridLengthConverter();
            dockEventGrid.Children.Clear();
            dockEventGrid.RowDefinitions.Clear();
            dockEventGrid.Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;

            // Setting the heading for the events Tab
            BrushConverter bc = new BrushConverter();
            dockEventGrid.RowDefinitions.Add(new RowDefinition() {
                Height = (GridLength)glc.ConvertFromString("28")
            });

            TextBox headingDescription = new TextBox()
            {
                Text = "Description",
                Margin = new Thickness(0, 0, 0, 0),
                FontWeight = FontWeights.Bold,
                FontSize = 12,
                FontFamily = new FontFamily("Segoe UI"),
                IsReadOnly = true,
                Background = (Brush)bc.ConvertFrom("#FFE9ECFA")
            };


            TextBox headingListener = new TextBox() {
                Text = targetComponent.id,
                Margin = new Thickness(0, 0, 0, 0),
                FontWeight = FontWeights.Bold,
                FontSize = 12,
                FontFamily = new FontFamily("Segoe UI"),
                IsReadOnly = true,
                Background = (Brush)bc.ConvertFrom("#FFE9ECFA")
            };
            componentType comp = findComponentType(targetComponent.id);
            if (comp != null)
            {
                headingListener.ToolTip = constructComponentTypeToolTip(comp, true);
            }

            TextBox headingTrigger = new TextBox() {
                Text = sourceComponent.id,
                Margin = new Thickness(0, 0, 0, 0),
                FontWeight = FontWeights.Bold,
                FontSize = 12,
                FontFamily = new FontFamily("Segoe UI"),
                IsReadOnly = true,
                Background = (Brush)bc.ConvertFrom("#FFE9ECFA")
            };
            comp = findComponentType(sourceComponent.id);
            if (comp != null)
            {
                headingTrigger.ToolTip = constructComponentTypeToolTip(comp, true);
            }


            Grid.SetRow(headingListener, 0);
            Grid.SetColumn(headingListener, 0);
            Grid.SetRow(headingTrigger, 0);
            Grid.SetColumn(headingTrigger, 1);
            Grid.SetRow(headingDescription, 0);
            Grid.SetColumn(headingDescription, 2);

            dockEventGrid.Children.Add(headingTrigger);
            dockEventGrid.Children.Add(headingListener);
            dockEventGrid.Children.Add(headingDescription);

            Border headline = new Border();
            Grid.SetColumn(headline, 0);
            Grid.SetRow(headline, 0);
            Grid.SetColumnSpan(headline, 3);
            headline.BorderBrush = (Brush)bc.ConvertFrom("#FFE9ECFA");
            //headline.BorderBrush = Brushes.Black;
            headline.BorderThickness = new Thickness(1);
            dockEventGrid.Children.Add(headline);

            int eventListenerIndex = 0;
            int rowCounter = 1;
            ArrayList settedEvents = (ArrayList)eventChannelList.Clone();
            while (eventListenerIndex < eventListeners.Count) {
                EventListenerPort eventListener = (EventListenerPort)eventListeners[eventListenerIndex];
                eventListenerIndex++;
                dockEventGrid.RowDefinitions.Add(new RowDefinition() {
                    Height = (GridLength)glc.ConvertFromString("22")
                });

                TextBox l = new TextBox() {
                    Text = eventListener.EventListenerId,
                    Margin = new Thickness(0, 0, 0, 0),
                    FontSize = 12,
                    FontFamily = new FontFamily("Segoe UI"),
                    IsReadOnly = true,
                    ToolTip = eventListener.EventDescription
                };
                ComboBox eventCombobox = new ComboBox();
                eventCombobox.Name = "eventCombobox" + (eventListenerIndex - 1);//+ rowCounter;

                TextBox evtChnlDescription = new EvtChannelDescriptionTextBox
                {
                    eventListenerTextBox = l,
                    eventTriggerComboBox = eventCombobox,
                    
                };
                evtChnlDescription.LostFocus += EvtChnlDescription_LostFocus;
                // original code, without tooltip
                // eventCombobox.Items.Add("---");

                // new code for tooltips
                // several changes in EventCombobox_SelectionChanged also needed
                eventCombobox.Items.Add(new ComboBoxItem() { Content = "---" });

                foreach (EventTriggerPort eventTrigger in eventTriggers) {
                    // Uncomment, to enable tooltips for the combobox-elements.
                    ComboBoxItem cbi = new ComboBoxItem();
                    cbi.Content = eventTrigger.EventTriggerId;
                    cbi.ToolTip = eventTrigger.EventDescription;
                    eventCombobox.Items.Add(cbi);

                    // original code, without tooltip
                    // eventCombobox.Items.Add(eventTrigger.EventTriggerId);
                }

                // original code, without tooltip
                //eventCombobox.SelectedItem = "---";

                // new code for tooltips
                eventCombobox.SelectedItem = eventCombobox.Items[0];

                eventCombobox.KeyDown += EventCombobox_KeyDown;
                eventChannel eventToRemove = null;

                // load events and connect them
                foreach (eventChannel eventCh in settedEvents) {
                    if ((eventCh.sources.source.component.id == sourceComponent.id) && (eventCh.targets.target.component.id == targetComponent.id)
                            && (eventCh.targets.target.eventPort.id == eventListener.EventListenerId)) {

                        String sourceComponentId = eventCh.sources.source.component.id;
                        String sourceEventId = eventCh.sources.source.eventPort.id;
                        String targetComponentId = eventCh.targets.target.component.id;
                        String targetEventId = eventCh.targets.target.eventPort.id;
                        if (eventCh.GroupOriginalSource != null)
                        {
                            sourceComponentId = eventCh.GroupOriginalSource.component.id;
                            sourceEventId = eventCh.GroupOriginalSource.eventPort.id;
                        }

                        if (eventCh.GroupOriginalTarget!=null)
                        {
                            targetComponentId = eventCh.GroupOriginalTarget.component.id;
                            targetEventId = eventCh.GroupOriginalTarget.eventPort.id;
                        }

                        eventChannel originalEventChannel = findEventChannel(sourceComponentId, sourceEventId, targetComponentId, targetEventId);
                        if (originalEventChannel != null)
                        {
                            evtChnlDescription.Text = originalEventChannel.description;
                        }
    
                        // new code for tooltips
                        foreach (ComboBoxItem cbi in eventCombobox.Items) {
                            if ((string)cbi.Content == eventCh.sources.source.eventPort.id) {
                                eventCombobox.SelectedItem = cbi;
                                eventCombobox.ToolTip = cbi.ToolTip;
                                break;
                            }
                        }

                        // original code, without tooltip
                        // eventCombobox.SelectedItem = eventCh.sources.source.eventPort.id;

                        eventListenerIndex--;
                        eventToRemove = eventCh;
                        break;
                    }
                }
                if (eventToRemove != null) {
                    settedEvents.Remove(eventToRemove);
                    eventToRemove = null;
                }

                eventCombobox.SelectionChanged += EventCombobox_SelectionChanged;

                Grid.SetRow(l, rowCounter);
                Grid.SetColumn(l, 0);
                Grid.SetRow(eventCombobox, rowCounter);
                Grid.SetColumn(eventCombobox, 1);
                Grid.SetRow(evtChnlDescription, rowCounter);
                Grid.SetColumn(evtChnlDescription, 2);

                GridSplitter mySimpleGridSplitter = new GridSplitter();
                mySimpleGridSplitter.Background = Brushes.DarkGray;
                mySimpleGridSplitter.HorizontalAlignment = HorizontalAlignment.Right;
                mySimpleGridSplitter.VerticalAlignment = VerticalAlignment.Stretch;
                mySimpleGridSplitter.Width = 1;
                Grid.SetColumn(mySimpleGridSplitter, 0);
                Grid.SetRow(mySimpleGridSplitter, rowCounter);
                dockEventGrid.Children.Add(evtChnlDescription);
                dockEventGrid.Children.Add(l);
                dockEventGrid.Children.Add(eventCombobox);
                if (!newEventChannelRibbonButton.IsEnabled) // was editeventribbongroup
                {
                    eventCombobox.IsEnabled = false;
                }
                dockEventGrid.Children.Add(mySimpleGridSplitter);

                Border top = new Border();
                Grid.SetColumn(top, 0);
                Grid.SetRow(top, rowCounter);
                Grid.SetColumnSpan(top, 2);
                top.BorderBrush = Brushes.DarkGray;
                top.BorderThickness = new Thickness(1);
                dockEventGrid.Children.Add(top);

                rowCounter++;
            }

        }

        /// <summary>
        /// Deleting a component, will be called from all delete component points
        /// </summary>
        /// <param name="deleteComponent">The component to be deleted</param>
        //private void DeleteComponentFromMenu(modelComponent deleteComponent) {
        //    CommandObject co;
        //    // delete the eventChannels and therefore the events
        //    eventChannelLine eCL;
        //    for (int index = eventChannelLinesList.Count - 1; index >= 0; index--) {
        //        eCL = (eventChannelLine)eventChannelLinesList[index];
        //        if ((eCL.TriggerComponentId == deleteComponent.id) || (eCL.ListernerComponentId == deleteComponent.id)) {
        //            focusedEventChannel = eCL;
        //            // Ask a question, if component and therefore all events should be deleted. Causes some problems, if no is selected
        //            // if (MessageBox.Show(Properties.Resources.DeleteEventChannelConfirmTextFormat(focusedEventChannel.TriggerComponentId, focusedEventChannel.ListernerComponentId),
        //            //    Properties.Resources.DeleteEventChannelConfirmHeader, MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes) {
        //                co = new CommandObject("Add", focusedEventChannel);
        //                foreach (eventChannel eventCh in eventChannelList) {
        //                    foreach (object o in co.InvolvedObjects) {
        //                        if (!(o is eventChannelLine))
        //                            continue;
        //                        if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) && 
        //                            (eventCh.targets.target.component.id == ((eventChannelLine)o).ListernerComponentId)) {
        //                            co.Parameter.Add(eventCh);
        //                        }
        //                    }
        //                }
        //                undoStack.Push(co);                        
        //                DeleteEventChannelCommand(focusedEventChannel);
        //                focusedEventChannel = null;
        //            //}
        //        }
        //    }
        //    focusedEventChannel = null;

        //    foreach (Object o in deleteComponent.ports.PortsList.Values) {
        //        if (o is inputPortType) {
        //            inputPortType pIn = (inputPortType)o;
        //            if (pIn.ChannelId != "") {
        //                channel tempChannel = deploymentChannelList[pIn.ChannelId];
        //                DeleteChannel(tempChannel);
        //                co = new CommandObject("Add", tempChannel);
        //                undoStack.Push(co);
        //            }
        //        } else if (o is outputPortType) {
        //            outputPortType pOut = (outputPortType)o;
        //            while (pOut.ChannelIds.Count > 0) {
        //                channel tempChannel = deploymentChannelList[pOut.ChannelIds[0].ToString()];
        //                DeleteChannel(tempChannel);
        //                co = new CommandObject("Add", tempChannel);
        //                undoStack.Push(co);
        //            }
        //        }
        //    }
        //    if (canvas.Children.Count > 0) {
        //        Keyboard.Focus(canvas.Children[0]);
        //    } else {
        //        Keyboard.Focus(canvas);
        //    }
        //    DeleteComponent(deleteComponent);
        //    co = new CommandObject("Add", deleteComponent);
        //    undoStack.Push(co);
        //    ResetPropertyDock();
        //}

        /// <summary>
        /// Opens a dialog box asking for a new component id. Returning the new id
        /// </summary>
        /// <param name="suggestID">Id, suggested by the system</param>
        /// <returns>Id, returned from user input</returns>
        private String SetNameForComponentOnCanvas(String suggestID) {
            // opening dialog box for the name of the component
            string nameErrorStr = "";
            NameDialog nameDiag;
            // the dialog appears as long as
            // 1. the name is empty
            // 2. the name is already been used
            do {
                nameDiag = new NameDialog();
                nameDiag.Owner = this;
                nameDiag.componentNameBox.Text = suggestID;
                if (nameErrorStr != "") {
                    nameDiag.errorField.Text = nameErrorStr;
                }
                nameDiag.ShowDialog();
                if (nameDiag.componentNameBox.Text.Length == 0) {
                    nameErrorStr = Properties.Resources.NameDialogTextEmpty;
                }
                else if (deploymentComponentList.ContainsKey(nameDiag.componentNameBox.Text)) {
                    nameErrorStr = Properties.Resources.NameDialogTextUsed;
                }
                else {
                    nameErrorStr = "";
                }
                if (nameDiag.DialogResult == false) {
                    nameErrorStr = "";
                }
            } while (nameErrorStr != "");

            if (nameDiag.DialogResult == true) {
                showNamingDialogOnComponentInsert = (bool)nameDiag.showCheckbox.IsChecked;
                if (showNamingDialogOnComponentInsert) {
                    ini.IniWriteValue("Options", "showNamingDialogOnComponentInsert", "true");
                }
                else {
                    ini.IniWriteValue("Options", "showNamingDialogOnComponentInsert", "false");
                }
                return nameDiag.componentNameBox.Text;
            }
            else {
                return "";
            }
        }

        /// <summary>
        /// Check, if all input ports, who must be connected are connected.
        /// Return string contains detailed information about the unconnected ports
        /// </summary>
        /// <returns>String, containing port id and component id of unconnected ports</returns>
        private String MustBeConnectedChecker() {
            String retString = "";
            foreach (componentType component in deploymentComponentList.Values) {
                foreach (object port in component.PortsList.Values) {
                    if (port is inputPortType) {
                        inputPortType inPort = (inputPortType)port;
                        if (inPort.MustBeConnected == true && inPort.ChannelId == "") {
                            retString += Properties.Resources.MustBeConnectedCheckerStringFormat(component.id, inPort.portTypeID) + "\n";
                        }
                    }
                }
            }
            return retString;
        }

        /// <summary>
        /// Check, if the datatype of the input port and the output port fits to each other
        /// Beside connecting the same datatypes, the following casts are allowed:
        /// byte to integer, byte to double
        /// char to integer, char to double
        /// integer to double
        /// double to integer
        /// </summary>
        /// <param name="outPortType"></param>
        /// <param name="inPortType"></param>
        /// <returns>true, if ports can be connected to each other</returns>
        private bool CheckInteroperabilityOfPorts(ACS2.dataType outPortType, ACS2.dataType inPortType)
        {
            if (outPortType == inPortType)
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.@byte && 
                (inPortType == ACS2.dataType.integer ||
                inPortType == ACS2.dataType.@double ||
                inPortType == ACS2.dataType.@string||
                inPortType== ACS2.dataType.boolean ||
                inPortType == ACS2.dataType.@char))
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.@char && 
                (inPortType == ACS2.dataType.integer ||
                inPortType == ACS2.dataType.@double ||
                inPortType == ACS2.dataType.@string ||
                inPortType == ACS2.dataType.boolean ||
                inPortType == ACS2.dataType.@byte
                ))
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.integer && 
                (inPortType == ACS2.dataType.@double ||
                inPortType == ACS2.dataType.@string ||
                inPortType == ACS2.dataType.boolean ||
                inPortType == ACS2.dataType.@byte ||
                inPortType == ACS2.dataType.@char
                ))
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.@double && 
                (inPortType == ACS2.dataType.integer ||
                inPortType == ACS2.dataType.@string ||
                inPortType == ACS2.dataType.boolean ||
                inPortType == ACS2.dataType.@byte ||
                inPortType == ACS2.dataType.@char

                ))
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.boolean
                    &&
                    inPortType == ACS2.dataType.@byte ||
                    inPortType == ACS2.dataType.integer ||
                    inPortType == ACS2.dataType.@double ||
                    inPortType == ACS2.dataType.@string ||                
                    inPortType == ACS2.dataType.@char
                )
            {
                return true;
            }
            else if (outPortType == ACS2.dataType.@string &&
                    (inPortType == ACS2.dataType.@byte ||
                    inPortType == ACS2.dataType.integer ||
                    inPortType == ACS2.dataType.@double ||
                    inPortType == ACS2.dataType.@char ||
                    inPortType == ACS2.dataType.boolean))
            {
                return true;
            }

            return false;
        }


        private bool CheckPropertyDatatype(String value, ACS2.dataType propertyType) {
            switch (propertyType) {
                case ACS2.dataType.boolean:
                    bool retBVal;
                    return Boolean.TryParse(value, out retBVal);
                case ACS2.dataType.@byte:
                    byte retByVal;
                    return Byte.TryParse(value, out retByVal);
                case ACS2.dataType.@char:
                    char retCVal;
                    return Char.TryParse(value, out retCVal);
                case ACS2.dataType.@double:
                    double retDVal;
                    return Double.TryParse(value, System.Globalization.NumberStyles.Number, System.Globalization.CultureInfo.InvariantCulture, out retDVal);
                case ACS2.dataType.integer:
                    Int32 retIVal;
                    return Int32.TryParse(value, out retIVal);
                case ACS2.dataType.@string:
                    return true;
                default:
                    return false;
            }
        }


        /// <summary>
        /// Set the position of a new component, avoiding two components at the exact same position
        /// </summary>
        /// <param name="x"></param>
        /// <param name="y"></param>
        /// <returns>int[] containing the new coordinates</returns>
        private int[] ProperComponentCoordinates(int x, int y) {
            int[] retVal = new int[2];
            foreach (componentType dc in deploymentComponentList.Values) {
                if ((Int32.Parse(dc.layout.posX) == x) && (Int32.Parse(dc.layout.posY) == y)) {
                    retVal = ProperComponentCoordinates(x + 40, y + 40);
                    return retVal;
                }
            }
            retVal[0] = x;
            retVal[1] = y;
            return retVal;
        }

        /// <summary>
        /// Set colors on the canvas and in the componentlist to the ones selected in the options dialog
        /// </summary>
        public void UpdateColors() {
            BrushConverter bc = new BrushConverter();

            // read the colors from ini-file
            String headerColor = ini.IniReadValue("Layout", "headercolor");
            if (headerColor.Equals("")) headerColor = ACS.LayoutConstants.TOPRECTANGLECOLOR;
            String groupColor = ini.IniReadValue("Layout", "groupcolor");
            if (groupColor.Equals("")) groupColor = ACS.LayoutConstants.GROUPRECTANGLECOLOR;
            String bodyColor = ini.IniReadValue("Layout", "bodycolor");
            if (bodyColor.Equals("")) bodyColor = ACS.LayoutConstants.MAINRECTANGLECOLOR;
            String inPortColor = ini.IniReadValue("Layout", "inportcolor");
            if (inPortColor.Equals("")) inPortColor = ACS.LayoutConstants.INPORTRECTANGLECOLOR;
            String outPortColor = ini.IniReadValue("Layout", "outportcolor");
            if (outPortColor.Equals("")) outPortColor = ACS.LayoutConstants.OUTPORTRECTANGLECOLOR;
            String eventInPortColor = ini.IniReadValue("Layout", "eventinportcolor");
            if (eventInPortColor.Equals("")) eventInPortColor = ACS.LayoutConstants.EVENTINPORTCOLOR;
            String eventOutPortColor = ini.IniReadValue("Layout", "eventoutportcolor");
            if (eventOutPortColor.Equals("")) eventOutPortColor = ACS.LayoutConstants.EVENTOUTPORTCOLOR;

            // update colors in the deploymentmodel
            foreach (componentType mc in deploymentModel.components) {
                if (mc != null) {
                    if (mc.ComponentType == ACS2.componentTypeDataTypes.group) {
                        mc.TopRectangle.Fill = (Brush)bc.ConvertFrom(groupColor);
                    } else {
                        mc.TopRectangle.Fill = (Brush)bc.ConvertFrom(headerColor);
                    }
                    mc.MainRectangle.Fill = (Brush)bc.ConvertFrom(bodyColor);
                    if (mc.ports != null) {
                        foreach (object o in mc.ports) {
                            if (o is outputPortType) {
                                outputPortType outPort = (outputPortType)o;
                                outPort.PortRectangle.Fill = (Brush)bc.ConvertFrom(outPortColor);
                            }
                            else if (o is inputPortType) {
                                inputPortType inPort = (inputPortType)o;
                                inPort.PortRectangle.Fill = (Brush)bc.ConvertFrom(inPortColor);
                            }
                        }
                    }
                    if (mc.EventTriggerPolygon != null) {
                        mc.EventTriggerPolygon.EventPortPolygon.Fill = (Brush)bc.ConvertFrom(eventOutPortColor);
                    }
                    if (mc.EventListenerPolygon != null) {
                        mc.EventListenerPolygon.EventPortPolygon.Fill = (Brush)bc.ConvertFrom(eventInPortColor);
                    }
                }
            }

            // update colors in the componentList
            foreach (Asterics.ACS2.componentTypesComponentType ct in componentList.Values) {
                ct.MainRectangle.Fill = (Brush)bc.ConvertFrom(bodyColor);
                if (ct.ports != null) {
                    foreach (object o in ct.ports) {
                        if (o is Asterics.ACS2.inputPortType) {
                            ((Asterics.ACS2.inputPortType)o).PortRectangle.Fill = (Brush)bc.ConvertFrom(inPortColor);
                        }
                        else if (o is Asterics.ACS2.outputPortType) {
                            ((Asterics.ACS2.outputPortType)o).PortRectangle.Fill = (Brush)bc.ConvertFrom(outPortColor);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Enables the editable functions of the canvas and hides the components combobox
        /// </summary>
        private void EnableCanvas() {
            //canvas.IsEnabled = true;
            //canvas.Children.Remove(inactiveCanvasRectangle);
            canvas.Background = Brushes.White;
            //componentsComboBox.Visibility = Visibility.Collapsed;
            editRibbonGroup.IsEnabled = true;
            editComponentRibbonGroup.IsEnabled = true;
            if (undoButton.IsEnabled) {
                UndoQuickAccess.IsEnabled = true;
            }
            newChannelRibbonButton.IsEnabled = true; // was editchannelribbongroup
            componentsRibbonGroup.IsEnabled = true;
            newEventChannelRibbonButton.IsEnabled = true; // was editeventribbongroup
        }

        /// <summary>
        /// Disables the edit functions of the canvas, place a gray rectange over the canvas and
        /// fills the component combobox with the components and deactivates the component buttons.
        /// Used, when the model is running
        /// </summary>
        private void DisableCanvas() {
            if (canvas.IsEnabled) {
                //canvas.IsEnabled = false;
                inactiveCanvasRectangle = new Rectangle();
                inactiveCanvasRectangle.Width = canvas.Width;
                inactiveCanvasRectangle.Height = canvas.Height;
                //canvas.Children.Add(inactiveCanvasRectangle);
                Canvas.SetZIndex(inactiveCanvasRectangle, Canvas.GetZIndex(inactiveCanvasRectangle) + 5000);
                Canvas.SetLeft(canvas, 0);
                Canvas.SetTop(canvas, 0);
                BrushConverter bc = new BrushConverter();
                inactiveCanvasRectangle.Fill = (Brush)bc.ConvertFrom("#88e2e2e2");
                canvas.Background = (Brush)bc.ConvertFrom("#88e2e2e2");
                //componentsComboBox.Items.Clear();
                //foreach (modelComponent comp in deploymentModel.components) {
                //    ComboBoxItem cbi = new ComboBoxItem();
                //    cbi.Content = comp.id;
                //    if ((focusedComponent != null) && (focusedComponent.id == comp.id)) {
                //        cbi.IsSelected = true;
                //    }
                //    componentsComboBox.Items.Add(cbi);
                //}
                //if (componentsComboBox.SelectedItem == null) {
                //    componentsComboBox.SelectedItem = componentsComboBox.Items[0];
                //}
                //componentsComboBox.Visibility = Visibility.Visible;
                editComponentRibbonGroup.IsEnabled = false;
                UndoQuickAccess.IsEnabled = false;
                newChannelRibbonButton.IsEnabled = false; // was editchannelribbongroup
                componentsRibbonGroup.IsEnabled = false;
                editRibbonGroup.IsEnabled = false;
                newEventChannelRibbonButton.IsEnabled = false; // was editeventribbongroup
            }
        }

        /// <summary>
        /// Build the context menu of a component, if the context menu will be opened by keyboard.
        /// Channels and eventChannels can be established with the context menu
        /// </summary>
        private void BuildKeyboardContextMenu() {
            Canvas canvasWithFocus = Keyboard.FocusedElement as Canvas;
            componentType componentWithFocus = null;
            foreach (componentType tempComponent in deploymentComponentList.Values) {
                if (tempComponent.ComponentCanvas == canvasWithFocus) {
                    componentWithFocus = tempComponent;
                    break;
                }
            }

            componentWithFocus.MainRectangle.ContextMenu.PlacementTarget = componentWithFocus.MainRectangle;
            componentWithFocus.MainRectangle.ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Bottom;

            // check, if ACS is not in running mode
            if (newChannelRibbonButton.IsEnabled) // was editchannelribbongroup
            {
                // if no channel is available, the list of output ports will be generated for the context menu
                if (channelToConnect == null) {
                    componentContextMenuItemAddChannel.Items.Clear();
                    int index = 0;
                    foreach (Object o in componentWithFocus.PortsList.Values) {
                        if (o is outputPortType) {
                            MenuItem i = new MenuItem();
                            i.Header = ((outputPortType)o).portTypeID;
                            componentContextMenuItemAddChannel.Items.Add(i);
                            i.Click += ComponentContextMenuConnectChannel_Click;
                        }
                        index++;
                    }
                    componentContextMenuItemAddChannel.IsEnabled = true;
                    componentContextMenuItemConnectChannel.IsEnabled = false;
                    componentContextMenuItemDropChannel.IsEnabled = false;
                }
                else {
                    // if a channel is already connected to an out-port, the list of in-ports
                    // will be created for the context menu
                    componentContextMenuItemConnectChannel.Items.Clear();
                    int index = 0;
                    foreach (Object o in componentWithFocus.PortsList.Values) {
                        if (o is inputPortType) {
                            MenuItem i = new MenuItem();
                            if (((inputPortType)o).ChannelId == "") {
                                i.Header = ((inputPortType)o).portTypeID;
                                componentContextMenuItemConnectChannel.Items.Add(i);
                                i.Click += ComponentContextMenuConnectChannel_Click;
                            }
                        }
                        index++;
                    }
                    componentContextMenuItemAddChannel.IsEnabled = false;
                    componentContextMenuItemConnectChannel.IsEnabled = true;
                    componentContextMenuItemDropChannel.IsEnabled = true;
                }
            }
            else {
                componentContextMenuItemAddChannel.IsEnabled = false;
                componentContextMenuItemConnectChannel.IsEnabled = false;
            }
            componentWithFocus.MainRectangle.ContextMenu.IsOpen = true;
        }


        /// <summary>
        /// Starts a new theard, requesting the status of the ARE in a defined period of time
        /// </summary>
        private void StartStatusPolling() {
            if (ini.IniReadValue("ARE", "enable_status_polling").Equals("true")) {
                if (statusTimer == null) {
                    try {
                        asapiStatusClient = asapiNet.Connect(AREHostIP, AREPort, 300);
                        int interval = 2000;
                        int.TryParse(ini.IniReadValue("ARE", "status_polling_frequency"), out interval);
                        if (interval < 500)
                        { // 500ms is the minimum for the polling frequency
                            interval = 500;
                        }
                        statusTimer = new DispatcherTimer(DispatcherPriority.SystemIdle);
                        statusTimer.Interval = TimeSpan.FromMilliseconds(interval);
                        statusTimer.Tick += new EventHandler(CheckStatus);
                        statusTimer.IsEnabled = true;
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.StatusPollingThreadErrorDialog, Properties.Resources.StatusPollingThreadErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    }
                }
            }
        }


        /// <summary>
        /// Stop the status polling thread
        /// </summary>
        private void StopStatusPolling() {
            /*try {
                if (statusTimer != null) {
                    statusTimer.Dispose();
                    Thread.Sleep(100); // needed to stop the timer process before closing the connection
                    //Console.WriteLine("timer stopped");
                    statusTimer = null;
                }
                if (asapiStatusClient != null) {
                    asapiNet.Disconnect(asapiStatusClient);
                    //Console.WriteLine("connection discinnected");
                    asapiStatusClient = null;
                }
            }
            catch (Exception ex) {
                MessageBox.Show(Properties.Resources.StatusPollingThreadErrorDialog, Properties.Resources.StatusPollingThreadErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
            }*/
            if (statusTimer != null)
                statusTimer.IsEnabled = false;
            statusTimer = null;
        }


        /// <summary>
        /// Function, called by a periodic timer to request the status of the ARE and the plug-ins
        /// If an error in a plug-in occurs, the background of this plug-in will be coloured red
        /// If the status of the ARE changes the gui will be updated
        /// </summary>
        /// <param name="statusObject">Empty Object, not used</param>
        public void CheckStatus(Object statusObject, EventArgs e)
        {
            List<StatusObject> newStatus = new List<StatusObject>();
            if (asapiStatusClient != null && asapiStatusClient.InputProtocol.Transport.IsOpen)
            {
                try
                {
                    newStatus = asapiStatusClient.QueryStatus(false);
                }
                catch (IOException ie)
                {
                    MessageBox.Show(Properties.Resources.StatusPollingThreadErrorDialog, Properties.Resources.StatusPollingThreadErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    StopStatusPolling();
                    areStatus.Status = AREStatus.ConnectionStatus.Disconnected;
                    return;
                }
            }
            string tmpAreStatus = "none";
            foreach (StatusObject so in newStatus)
            {
                if (so.InvolvedComponentID == "")
                    tmpAreStatus = so.Status;
                else if (so.Status == "error") {
                    Canvas errorCompCanvas = null;
                    if (deploymentComponentList.ContainsKey(so.InvolvedComponentID))
                    {
                        errorCompCanvas = deploymentComponentList[so.InvolvedComponentID].ComponentCanvas; //.Background = Brushes.Red;
                        Canvas groupErrorCanvas = null;
                        if (GetParentGroup(deploymentComponentList[so.InvolvedComponentID]) != null)
                        {
                            groupErrorCanvas = deploymentComponentList[GetParentGroup(deploymentComponentList[so.InvolvedComponentID]).ID].ComponentCanvas;
                        }
                        if (errorCompCanvas != null)
                        {
                            errorCompCanvas.Dispatcher.BeginInvoke(DispatcherPriority.Normal,
                            (Action)(() =>
                            {
                                errorCompCanvas.Background = Brushes.Red;
                                if (groupErrorCanvas != null)
                                {
                                    groupErrorCanvas.Background = Brushes.Red;
                                }
                            }));
                        }
                    }
                }
                else if (so.Status == "ok")
                { // remove error state
                    Canvas errorCompCanvas = null;
                    if (deploymentComponentList.ContainsKey(so.InvolvedComponentID))
                    {
                        errorCompCanvas = deploymentComponentList[so.InvolvedComponentID].ComponentCanvas;
                        Canvas groupErrorCanvas = null;
                        if (errorCompCanvas != null)
                        {
                            errorCompCanvas.Dispatcher.BeginInvoke(DispatcherPriority.Normal,
                            (Action)(() =>
                            {
                                if (errorCompCanvas.Background == Brushes.Red)
                                {
                                    groupComponent group = GetParentGroup(deploymentComponentList[so.InvolvedComponentID]);
                                    if (group != null && deploymentComponentList.ContainsKey(group.ID))
                                    {
                                        groupErrorCanvas = deploymentComponentList[group.ID].ComponentCanvas;
                                    }
                                }
                                errorCompCanvas.Background = null;
                                if (groupErrorCanvas != null)
                                {
                                    groupErrorCanvas.Background = null;
                                }
                            }));
                        }
                    }
                }

            }
            switch (tmpAreStatus)
            {
                case "running":
                    areStatus.Status = AREStatus.ConnectionStatus.Running;
                    break;
                case "ok":
                case "deployed":
                    areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                    break;
                case "paused":
                    areStatus.Status = AREStatus.ConnectionStatus.Pause;
                    break;
                default:
                    Console.WriteLine("Unknown Status: " + tmpAreStatus);
                    break;
            }
        }

        /// <summary>
        /// Return the componentType object with the given id.
        /// </summary>
        /// <param name="componentTypeId"></param>
        /// <returns></returns>
        private componentType findComponentType(String componentTypeId) {
            foreach (componentType comp in deploymentComponentList.Values)
            {
                if (comp.id.Equals(componentTypeId))
                {
                    return comp;
                }
            }
            return null;
        }
        /// <summary>
        /// Find a channel with the given id.
        /// </summary>
        /// <param name="channelId"></param>
        /// <returns></returns>
        private channel findChannel(String channelId)
        {
            foreach (channel chn in deploymentChannelList.Values)
            {
                if (chn.id.Equals(channelId))
                {
                    return chn;
                }
            }
            return null;
        }

        /// <summary>
        /// Updates the ToolTip texts of the canvas elements.
        /// </summary>
        private void UpdateToolTips()
        {
            UpdateEventChannelToolTips();
            UpdateComponentTypeToolTips();
        }

        /// <summary>
        /// Updates the tooltips for the event channels.
        /// </summary>
        private void UpdateEventChannelToolTips()
        {
            //update tooltips for event channels
            foreach (eventChannelLine evchnlLine in eventChannelLinesList)
            {
                List<eventChannel> evtChannels = findEventChannels(evchnlLine.TriggerComponentId, evchnlLine.ListenerComponentId);
                String toolTipString = "";

                int ctr = 0;
                foreach (eventChannel chn in evtChannels)
                {
                    String sourceComponentId = chn.sources.source.component.id;
                    String sourceEventId = chn.sources.source.eventPort.id;
                    String targetComponentId = chn.targets.target.component.id;
                    String targetEventId = chn.targets.target.eventPort.id;
                    String description = chn.description;

                    //Unfortunately we always have to do extra work because of Groups                    
                    if (chn.GroupOriginalSource != null)
                    {
                        sourceComponentId = chn.GroupOriginalSource.component.id;
                        sourceEventId = chn.GroupOriginalSource.eventPort.id;
                    }
                    if (chn.GroupOriginalTarget != null)
                    {
                        targetComponentId = chn.GroupOriginalTarget.component.id;
                        targetEventId = chn.GroupOriginalTarget.eventPort.id;
                    }
                    eventChannel origChn = findEventChannel(sourceComponentId, sourceEventId, targetComponentId, targetEventId);
                    if (origChn != null)
                    {
                        description = origChn.description;
                    }

                    if (ctr > 0) toolTipString += "\n";
                    toolTipString += sourceComponentId + "." + sourceEventId + "->" +
                         targetComponentId + "." + targetEventId;
                    if (description != null && !description.Equals(""))
                    {
                        toolTipString += " (" + description + ")";
                    }
                    ctr++;
                }
                ToolTipService.SetShowDuration(evchnlLine.Line, TOOLTIP_SHOW_DURATION);
                evchnlLine.Line.ToolTip = toolTipString;
            }
        }

        /// <summary>
        /// Construct tooltip string for the given componentType.
        /// </summary>
        /// <param name="comp"></param>
        /// <param name="withProperties">true: Property values are included.</param>
        /// <returns></returns>
        private String constructComponentTypeToolTip(componentType comp, Boolean withProperties)
        {
            String toolTip="Type: " + comp.type_id + "\n" + comp.description;
            if (withProperties)
            {
                int ctr = 0;
                foreach (propertyType prop in comp.properties)
                {
                    if(ctr++==0) toolTip += "\n\nProperties:";
                    toolTip += "\n" + prop.name + "=" + prop.value;
                }
            }
            return toolTip;

        }

        /// <summary>
        /// Update the tooltips of all componentType instances.
        /// </summary>
        private void UpdateComponentTypeToolTips()
        {
            //update tooltips for components
            foreach (componentType comp in deploymentComponentList.Values)
            {
                if (comp.ComponentCanvas != null)
                {
                    comp.ComponentCanvas.ToolTip = constructComponentTypeToolTip(comp, false);
                    UpdateEventPortsToolTips(comp);
                    UpdatePortsToolTips(comp);
                }
                if (comp.gui != null && comp.gui.GuiElementCanvas != null)
                {
                    comp.gui.GuiElementCanvas.ToolTip = "Type: " + comp.type_id + "\n" + comp.description;
                }
            }
        }

        /// <summary>
        /// Update the tooltips of the ports of a given componentType instance.
        /// </summary>
        /// <param name="comp"></param>
        private void UpdatePortsToolTips(componentType comp)
        {
            if (comp.PortsList != null)
            {
                foreach (object port in comp.PortsList.Values)
                {
                    if (port is outputPortType)
                    {
                        outputPortType p = (outputPortType)port;

                        String portName = (p.PortAliasForGroups != null && !p.PortAliasForGroups.Equals("")) ? p.PortAliasForGroups : p.PortLabel.Text;
                        if (p.PortRectangle != null)
                        {
                            p.PortRectangle.ToolTip = portName + " (" + p.PortDataType + "): " + p.Description;
                            
                            if (p.ChannelIds != null)
                            {
                                foreach (String channelId in p.ChannelIds)
                                {
                                    channel chn = findChannel(channelId);
                                    if (chn != null)
                                    {
                                        if (chn.GroupOriginalTarget != null)
                                        {
                                            chn = findChannel(bindingEdgeGetRealId(chn.target, chn.GroupOriginalTarget));
                                        }
                                        if (chn != null)
                                        {
                                            p.PortRectangle.ToolTip += "\n-> " + bindingEdgeGetRealId(chn.target, chn.GroupOriginalTarget);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (port is inputPortType)
                    {
                        inputPortType p = (inputPortType)port;
                        
                        String portName = (p.PortAliasForGroups != null && !p.PortAliasForGroups.Equals("")) ? p.PortAliasForGroups : p.PortLabel.Text;
                        if (p.PortRectangle != null)
                        {
                            p.PortRectangle.ToolTip = portName + " (" + p.PortDataType + "): " + p.Description +  " --> synchronized: ";
                            if (p.sync)
                            {
                                p.PortRectangle.ToolTip += "true";
                            }
                            else
                            {
                                p.PortRectangle.ToolTip += "false";
                            }
                            if (p.ChannelId != null)
                            {
                                channel chn = findChannel(p.ChannelId);
                                if (chn != null)
                                {
                                    if (chn.GroupOriginalSource != null)
                                    {
                                        chn = findChannel(bindingEdgeGetRealId(chn.source, chn.GroupOriginalSource));
                                    }
                                    if (chn != null)
                                    {
                                        p.PortRectangle.ToolTip += "\n" + bindingEdgeGetRealId(chn.source, chn.GroupOriginalSource)+" ->";
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }

        /// <summary>
        /// Returns the composed Id of componentId and portId. If groupBindingEdge != null the respective values are taken from the groupBindingEdge.
        /// </summary>
        /// <param name="normalBindingEdge"></param>
        /// <param name="groupBindingEdge"></param>
        /// <returns></returns>
        private String bindingEdgeGetRealId(bindingEdge normalBindingEdge, bindingEdge groupBindingEdge)
        {
            String componentId = normalBindingEdge.component.id;
            String portId = normalBindingEdge.port.id;

            if (groupBindingEdge != null)
            {
                componentId = groupBindingEdge.component.id;
                portId = groupBindingEdge.port.id;
            }
            return componentId + "." + portId;
        }

        /// <summary>
        /// Updates the tooltips for the event ports.
        /// </summary>
        /// <param name="comp"></param>
        private void UpdateEventPortsToolTips(componentType comp)
        {
            if (comp.EventListenerPolygon != null && comp.EventListenerPolygon.InputEventPortCanvas != null)
            {
                comp.EventListenerPolygon.InputEventPortCanvas.ToolTip = "";
                int ctr = 0;
                foreach (EventListenerPort evtListenerPort in comp.EventListenerList)
                {
                    if (ctr++ > 0) comp.EventListenerPolygon.InputEventPortCanvas.ToolTip += "\n";
                    comp.EventListenerPolygon.InputEventPortCanvas.ToolTip += evtListenerPort.EventListenerId + ": " + evtListenerPort.EventDescription;
                }

            }
            if (comp.EventTriggerPolygon != null && comp.EventTriggerPolygon.OutputEventPortCanvas != null)
            {
                comp.EventTriggerPolygon.OutputEventPortCanvas.ToolTip = "";
                int ctr = 0;
                foreach (EventTriggerPort evtTriggerPort in comp.EventTriggerList)
                {
                    if (ctr++ > 0) comp.EventTriggerPolygon.OutputEventPortCanvas.ToolTip += "\n";
                    comp.EventTriggerPolygon.OutputEventPortCanvas.ToolTip += evtTriggerPort.EventTriggerId + ": " + evtTriggerPort.EventDescription;
                }
            }

        }
        /// <summary>
        /// Timer so set the focus on the first element of a canvas. This is needed to set the focus after
        /// tabs have been switched, otherwise, the elements are not visible and therefore, the focus
        /// will not be set.
        /// </summary>
        /// <param name="focusObject">The canvas, which should get the focus</param>
        public void DoFocusTimer(Object focusObject) {
            try {
                if (focusObject is Canvas) {
                    Canvas focusTimerCanvas = (Canvas)focusObject;
                    focusTimerCanvas.Dispatcher.BeginInvoke(DispatcherPriority.Normal,
                        (Action)(() => {
                            if (focusTimerCanvas.Children.Count > 0) {
                                Keyboard.Focus(focusTimerCanvas.Children[0]);
                            } else {
                                Keyboard.Focus(focusTimerCanvas);
                            }
                        }));
                } else if (focusObject is DockableContent) {
                    DockableContent sv = (DockableContent)focusObject;
                    sv.Dispatcher.BeginInvoke(DispatcherPriority.Normal,
                        (Action)(() => {
                           Keyboard.Focus(sv);
                        }));
                }
            } catch (Exception) {
                //MessageBox.Show("Exception in Focus timer", Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        /// <summary>
        /// Adds a new line to the list of recently opened files or moves it to the first position, if it already was in the list. Called when a files is opened or when a files is saved for the first time.
        /// </summary>
        private void AddToRecentList(String newLine) {
            // get array of recent items from file
            string[] actLines;
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                actLines = File.ReadAllLines(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt");
            } else {
                actLines = File.ReadAllLines(AppDomain.CurrentDomain.BaseDirectory + "recent.txt");
            }            
            // check if new item is already in the array
            int oldIndex = Array.IndexOf(actLines, newLine);
            if (oldIndex > -1) {
                // take the element out of the array, as it will be re-inserted on the first position
                Array.Copy(actLines, 0, actLines, 1, oldIndex);
            }
            else {
                // shift array-contents to the right
                if (actLines.Length < MAXNUMBEROFRECENTFILES) {
                    Array.Resize(ref actLines, actLines.Length + 1);
                    Array.Copy(actLines, 0, actLines, 1, actLines.Length - 1);
                }
                else {
                    Array.Copy(actLines, 0, actLines, 1, MAXNUMBEROFRECENTFILES - 1);
                }
            }
            // insert most recently opened file at the beginning of the array
            actLines[0] = newLine;
            // write new array to file
            
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                File.WriteAllLines(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt", actLines);
            } else {
                File.WriteAllLines(AppDomain.CurrentDomain.BaseDirectory + "recent.txt", actLines);
            }  
        }

        /// <summary>
        /// Remove one element from the receent file list
        /// </summary>
        /// <param name="removeLine">File to remove</param>
        private void RemoveFromRecentList(String removeLine) {
            // get array of recent items from file
            string[] actLines;
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                actLines = File.ReadAllLines(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt");
            } else {
                actLines = File.ReadAllLines(AppDomain.CurrentDomain.BaseDirectory + "recent.txt");
            }  
            int delIndex = Array.IndexOf(actLines, removeLine);
            string[] newLines = new string[actLines.GetLength(0) - 1];
            Array.Copy(actLines, newLines, delIndex);
            Array.Copy(actLines, delIndex + 1, newLines, delIndex, actLines.GetLength(0) - delIndex - 1);
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                File.WriteAllLines(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\recent.txt", newLines);
            } else {
                File.WriteAllLines(AppDomain.CurrentDomain.BaseDirectory + "recent.txt", newLines);
            }  
        }

        /// <summary>
        /// Inserts a Rectangle to every component which is selected
        /// </summary>
        private void UpdateSelectedComponents() {
            foreach (componentType mc in selectedComponentList) {
                if (mc == null)
                    continue;
                Canvas selectCanvas = mc.ComponentCanvas;
                bool abort = false;
                foreach (UIElement uie in selectCanvas.Children) {
                    if ((uie is Rectangle) && (((Rectangle)uie).Name.Equals("selectRectangle"))) {
                        abort = true;
                        break;
                    }
                }
                if (abort)
                    continue;
                double canvasX = Canvas.GetLeft(selectCanvas);
                double canvasY = Canvas.GetTop(selectCanvas);
                //focusCanvas.Background = new SolidColorBrush(Colors.Red);
                Rectangle cr = new Rectangle();
                cr.Stroke = new SolidColorBrush(Colors.Black);
                cr.Fill = new SolidColorBrush(Color.FromArgb(40, 0, 0, 255));
                cr.Width = selectCanvas.Width;
                cr.Height = selectCanvas.Height;
                selectCanvas.Children.Insert(0, cr);
                Canvas.SetTop(cr, 0);
                Canvas.SetLeft(cr, 0);
                cr.Name = "selectRectangle";
                cr.RadiusX = 4;
                cr.RadiusY = 4;
                Canvas.SetZIndex(selectCanvas, Canvas.GetZIndex(selectCanvas) + 3000);
            }
        }


        /// <summary>
        /// Deletes the selection Rectangle from every component which is selected
        /// </summary>
        private void ClearBorderOfSelectedComponents() {
            foreach (componentType mc in selectedComponentList) {
                Canvas selectCanvas = mc.ComponentCanvas;
                Canvas.SetZIndex(selectCanvas, Canvas.GetZIndex(selectCanvas) - 3000);
                //Canvas.SetZIndex(selectCanvas, 999999);
                foreach (UIElement uie in selectCanvas.Children) {
                    if ((uie is Rectangle) && (((Rectangle)uie).Name.Equals("selectRectangle"))) {
                        selectCanvas.Children.Remove((Rectangle)uie);
                        break;
                    }
                }
            }
        }

        /// <summary>
        /// Deselects all components which are selected and only selects the given component
        /// </summary>
        /// <param name="mc">Component to select</param>
        private void ClearAndAddSelectedComponent(componentType mc) {
            ClearSelectedComponentList();
            if (!selectedComponentList.Contains(mc))
                selectedComponentList.AddLast(mc);
            UpdateSelectedComponents();
        }

        /// <summary>
        /// Additionally select the given component
        /// </summary>
        /// <param name="mc">Component to select</param>
        private void AddSelectedComponent(componentType mc) {
            if (!selectedComponentList.Contains(mc))
                selectedComponentList.AddLast(mc);
            if (mc.ComponentType == ACS2.componentTypeDataTypes.group) {
                groupComponent gc = null;
                if (groupsList.ContainsKey(mc.id)) {
                    gc = groupsList[mc.id];
                    foreach (componentType ct in gc.AddedComponentList) {
                        AddSelectedComponent(ct);
                    }
                    foreach (channel c in gc.AddedChannelsList) {
                        AddSelectedChannel(c);
                    }
                    foreach (eventChannelLine ec in gc.AddedEventChannelsList) {
                        AddSelectedEventChannel(ec);
                    }
                }
            }
            UpdateSelectedComponents();
        }

        /// <summary>
        /// Deselects all components which are selected
        /// </summary>
        private void ClearSelectedComponentList() {
            ClearBorderOfSelectedComponents();
            selectedComponentList.Clear();
        }

        /// <summary>
        /// Sets the Color of all selected channels to red
        /// </summary>

        private void UpdateSelectedChannels() {
            foreach (channel ch in selectedChannelList) {
                if (ch == null)
                    continue;
                DoubleCollection dashes = new DoubleCollection();
                ch.Line.Stroke = new SolidColorBrush(Colors.Green);
            }
        }

        /// <summary>
        /// Sets the Color of all selected channels back to black
        /// </summary>

        private void ClearColorOfSelectedChannels() {
            BrushConverter bc = new BrushConverter();
            foreach (channel ch in selectedChannelList) {
                if (ch == null)
                    continue;
                //ch.Line.Stroke = new SolidColorBrush(Colors.Black);
                ch.Line.Stroke = (Brush)bc.ConvertFrom("#AA000000");
            }
        }

        /// <summary>
        /// Deselects all Channels and selects the given channel
        /// </summary>
        /// <param name="ch">Channel to select</param>
        private void ClearAndAddSelectedChannel(channel ch) {
            ClearSelectedChannelList();
            if (!selectedChannelList.Contains(ch))
                selectedChannelList.AddLast(ch);
            UpdateSelectedChannels();
        }

        /// <summary>
        /// Additional select the given channel
        /// </summary>
        /// <param name="ch">Channel to select</param>
        private void AddSelectedChannel(channel ch) {
            if (!selectedChannelList.Contains(ch)) {
                selectedChannelList.AddLast(ch);

                if (ch.GroupOriginalSource != null) {
                    foreach (channel c in deploymentChannelList.Values) {
                        if (c.source.component.id.Equals(ch.GroupOriginalSource.component.id) &&
                            c.source.port.id.Equals(ch.GroupOriginalSource.port.id) &&
                            c.target.component.id.Equals(ch.target.component.id) &&
                            c.target.port.id.Equals(ch.target.port.id)) {
                            AddSelectedChannel(c);
                        }
                    }
                }

                if (ch.GroupOriginalTarget != null) {
                    foreach (channel c in deploymentChannelList.Values) {
                        if (c.source.component.id.Equals(ch.source.component.id) &&
                            c.source.port.id.Equals(ch.source.port.id) &&
                            c.target.component.id.Equals(ch.GroupOriginalTarget.component.id) &&
                            c.target.port.id.Equals(ch.GroupOriginalTarget.port.id)) {
                            AddSelectedChannel(c);
                        }
                    }
                }

            }
            UpdateSelectedChannels();
        }

        /// <summary>
        /// Deselect all Channels
        /// </summary>
        private void ClearSelectedChannelList() {
            ClearColorOfSelectedChannels();
            selectedChannelList.Clear();
        }

        // <summary>
        /// Set the line color of all selected Eventchannels to BlueViolet
        /// </summary>
        private void UpdateSelectedEventChannels() {
            foreach (eventChannelLine ech in selectedEventChannelList) {
                if (ech == null)
                    continue;
                ech.Line.Stroke = new SolidColorBrush(Colors.BlueViolet);
            }
        }

        /// <summary>
        /// Restores the original color of all selected Eventchannels. 
        /// </summary>
        private void ClearColorOfSelectedEventChannels() {
            foreach (eventChannelLine ech in selectedEventChannelList) {
                if (ech == null)
                    continue;
                BrushConverter bc = new BrushConverter();
                ech.Line.Stroke = (Brush)bc.ConvertFrom("#99d41919");
            }
        }

        /// <summary>
        /// Deselect all EventChannels and select the given one
        /// </summary>
        /// <param name="ech">EventChannel to select</param>
        private void ClearAndAddSelectedEventChannel(eventChannelLine ech) {
            ClearSelectedEventChannelList();
            if (!selectedEventChannelList.Contains(ech))
                selectedEventChannelList.AddLast(ech);
            UpdateSelectedEventChannels();
        }

        /// <summary>
        /// Additionally select the given Eventchannel
        /// </summary>
        /// <param name="ech">EventChannel to select</param>
        private void AddSelectedEventChannel(eventChannelLine ech) {
            if (!selectedEventChannelList.Contains(ech)) {
                selectedEventChannelList.AddLast(ech);
                if (!ech.HasGroupSource && !ech.HasGroupTarget) {
                    UpdateSelectedEventChannels();
                    return;
                }
                if (ech.HasGroupSource) {
                    groupComponent gc = groupsList[ech.TriggerComponentId];
                    foreach (componentType ct in gc.AddedComponentList) {
                        foreach (eventChannelLine ecl in eventChannelLinesList) {
                            if (ecl.TriggerComponentId.Equals(ct.id) && ecl.ListenerComponentId.Equals(ech.ListenerComponentId)) {
                                AddSelectedEventChannel(ecl);
                            }
                        }
                    }
                }
                if (ech.HasGroupTarget) {
                    groupComponent gc = groupsList[ech.ListenerComponentId];
                    foreach (componentType ct in gc.AddedComponentList) {
                        foreach (eventChannelLine ecl in eventChannelLinesList) {
                            if (ecl.ListenerComponentId.Equals(ct.id) && ecl.TriggerComponentId.Equals(ech.TriggerComponentId)) {
                                AddSelectedEventChannel(ecl);
                            }
                        }
                    }
                }
                if (ech.HasGroupTarget && ech.HasGroupSource) {
                    groupComponent target = groupsList[ech.ListenerComponentId];
                    groupComponent source = groupsList[ech.TriggerComponentId];
                    foreach (componentType ct in source.AddedComponentList) {
                        foreach (eventChannelLine ecl in eventChannelLinesList) {
                            if (ecl.TriggerComponentId.Equals(ct.id) && ecl.ListenerComponentId.Equals(target.ID)) {
                                AddSelectedEventChannel(ecl);
                            }
                        }
                    }

                    foreach (componentType ct in target.AddedComponentList) {
                        foreach (eventChannelLine ecl in eventChannelLinesList) {
                            if (ecl.ListenerComponentId.Equals(ct.id) && ecl.TriggerComponentId.Equals(source.ID)) {
                                AddSelectedEventChannel(ecl);
                            }
                        }
                    }
                }
            }
            UpdateSelectedEventChannels();
        }

        /// <summary>
        /// Deselect all EventChannels
        /// </summary>
        private void ClearSelectedEventChannelList() {
            ClearColorOfSelectedEventChannels();
            selectedEventChannelList.Clear();
        }


        /// <summary>
        /// Searches and selects all objects (components, channels, eventchannels) which collide with the given rectangle
        /// </summary>
        /// <param name="selectionRectangle"></param>
        private void selectObjectsFromRectangle(Rectangle selectionRectangle) {
            if (selectionRectangle.Width > 5 || selectionRectangle.Height > 5) {
                selectComponentsFromRectangle(selectionRectangle);
                selectChannelsFromRectangle(selectionRectangle);
                selectEventChannelsFromRectangle(selectionRectangle);
                SetKeyboardFocus();
            }
        }
        /// <summary>
        /// Sets the keyboard focus automatically to an object on the canvas using the following order:
        /// 1. modelcomponent
        /// 2. channel
        /// 3. eventchannel
        /// </summary>
        private void SetKeyboardFocus() {
            if (selectedComponentList.Count > 0)
                Keyboard.Focus(selectedComponentList.First().ComponentCanvas);
            else if (selectedChannelList.Count > 0)
                Keyboard.Focus(selectedChannelList.First().Line);
            else if (selectedEventChannelList.Count > 0)
                Keyboard.Focus(selectedEventChannelList.First().Line);
        }

        /// <summary>
        /// Selects all channels which collide with the given selection rectangle
        /// </summary>
        /// <param name="selRect"></param>
        private void selectChannelsFromRectangle(Rectangle selRect) {
            int sleft = (int)Canvas.GetLeft(selRect);
            int stop = (int)Canvas.GetTop(selRect);
            int sright = (int)(selRect.Width + sleft);
            int sbottom = (int)(selRect.Height + stop);
            foreach (channel ch in deploymentChannelList.Values) {
                if (ch.Line.Visibility != System.Windows.Visibility.Visible)
                    continue;
                if (LineRectangleCollide(ch.Line, selRect))
                    AddSelectedChannel(ch);
            }
        }


        /// <summary>
        /// Selects all eventchannels which collide with the given selection rectangle
        /// </summary>
        /// <param name="selRect"></param>
        private void selectEventChannelsFromRectangle(Rectangle selRect) {
            int sleft = (int)Canvas.GetLeft(selRect);
            int stop = (int)Canvas.GetTop(selRect);
            int sright = (int)(selRect.Width + sleft);
            int sbottom = (int)(selRect.Height + stop);
            foreach (eventChannelLine ech in eventChannelLinesList) {
                if (ech.Line.Visibility != System.Windows.Visibility.Visible)
                    continue;
                if (LineRectangleCollide(ech.Line, selRect))
                    AddSelectedEventChannel(ech);
            }
        }


        /// <summary>
        /// Select all components on the canvas
        /// </summary>
        private void SelectAll() {
            ClearSelectedComponentList();
            foreach (componentType mc in deploymentComponentList.Values) {
                selectedComponentList.AddLast(mc);
            }
            UpdateSelectedComponents();
            ClearSelectedChannelList();
            foreach (channel c in deploymentChannelList.Values) {
                selectedChannelList.AddLast(c);
            }
            UpdateSelectedChannels();
            ClearSelectedEventChannelList();
            LinkedList<eventChannelLine> selEventChannels = new LinkedList<eventChannelLine>();
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                foreach (eventChannel ech in deploymentModel.eventChannels) {
                    if (ecl.ListenerComponentId == ech.targets.target.component.id ||
                        ecl.TriggerComponentId == ech.sources.source.component.id) {
                        selEventChannels.AddLast(ecl);
                        break;
                    }
                }
            }
            foreach (eventChannelLine ecl in selEventChannels)
                selectedEventChannelList.AddLast(ecl);
            UpdateSelectedEventChannels();
        }


        /// <summary>
        /// sorts the submenu Items of the Menuitems alphabetically
        /// </summary>
        /// <param name="rsmis">Menuitem to sort</param>
        private void sortComponentSubmenu(RibbonSplitMenuItem[] rsmis) {
            foreach (RibbonSplitMenuItem rsmi in rsmis) {
                if (rsmi != null)
                    rsmi.Items.SortDescriptions.Add(new SortDescription("Header", ListSortDirection.Ascending));
            }
        }


        private void moveOthersMenuItemBack(RibbonMenuButton button) {
            RibbonMenuItem tmprami = null;
            foreach (RibbonMenuItem rami in button.Items) {
                if (rami.Header.Equals("Others")) {
                    tmprami = rami;
                    break;
                }
            }
            if (tmprami != null) {
                button.Items.Remove(tmprami);
                button.Items.Insert(button.Items.Count, tmprami);
            }
        }


        /// <summary>
        /// Check if the given line and the given rectangle intersect
        /// </summary>
        /// <param name="line"></param>
        /// <param name="selRect"></param>
        bool LineRectangleCollide(Line line, Rectangle selRect) {
            double a = (line.Y2 - line.Y1) / (line.X2 - line.X1);
            double b = (line.Y1 - a * line.X1);
            // left top Point of the rect
            double x = Canvas.GetLeft(selRect);
            double y = Canvas.GetTop(selRect);
            double d1 = a * x + b - y;
            // right top Point
            x = x + selRect.Width;
            double d2 = a * x + b - y;
            // left bottom Point
            x = Canvas.GetLeft(selRect);
            y = y + selRect.Height;
            double d3 = a * x + b - y;
            //right bottom Point
            x = x + selRect.Width;
            double d4 = a * x + b - y;

            if (d1 < 0 && d2 < 0 && d3 < 0 && d4 < 0)
                return false;
            if (d1 > 0 && d2 > 0 && d3 > 0 && d4 > 0)
                return false;

            int lleft = Math.Min((int)line.X1, (int)line.X2);
            int ltop = Math.Min((int)line.Y1, (int)line.Y2);
            int lright = Math.Max((int)line.X1, (int)line.X2);
            int lbottom = Math.Max((int)line.Y1, (int)line.Y2);

            int sleft = (int)Canvas.GetLeft(selRect);
            int stop = (int)Canvas.GetTop(selRect);
            int sright = (int)(selRect.Width + sleft);
            int sbottom = (int)(selRect.Height + stop);
            if (!(lleft > sright || sleft > lright || ltop > sbottom || stop > lbottom)) {// intersection found
                return true;
            }
            return false;
        }

        /// <summary>
        /// Select all modelComponents which collide with the given selection rectangle
        /// </summary>
        /// <param name="selRect"></param>
        private void selectComponentsFromRectangle(Rectangle selRect) {
            int sleft = (int)Canvas.GetLeft(selRect);
            int stop = (int)Canvas.GetTop(selRect);
            int sright = (int)(selRect.Width + sleft);
            int sbottom = (int)(selRect.Height + stop);
            foreach (componentType tempComponent in deploymentComponentList.Values.ToList()) {
                if (tempComponent.ComponentCanvas.Visibility != System.Windows.Visibility.Visible)
                    continue;
                int left = (int)Canvas.GetLeft(tempComponent.ComponentCanvas);
                int top = (int)Canvas.GetTop(tempComponent.ComponentCanvas);
                int right = (int)(tempComponent.ComponentCanvas.Width + left);
                int bottom = (int)(tempComponent.ComponentCanvas.Height + top);
                if (!(left > sright || sleft > right || top > sbottom || stop > bottom)) {// intersection found
                    AddSelectedComponent(tempComponent);
                }
            }
        }

        /// <summary>
        /// Returns the number of selected objects (channels, eventchannels and components)
        /// </summary>
        /// <returns>Integer, containing the number of selected objects (channels, eventchannels and components)<returns>
        private int GetSelectedObjectsCount() {
            int components = selectedComponentList.Count;
            int channels = selectedChannelList.Count;
            int eventChannels = selectedEventChannelList.Count;
            return components + channels + eventChannels;
        }

        /// <summary>
        /// Creates a CommandObject containing the coordinates of all
        /// selected components
        /// </summary>
        private CommandObject CreateMoveCommandObject() {
            CommandObject co = new CommandObject("moveComponent", selectedComponentList.ToArray());
            for (int i = 0; i < selectedComponentList.Count; i++) {
                componentType mc = selectedComponentList.ElementAt(i);
                co.Parameter.Add(int.Parse(mc.layout.posX));
                co.Parameter.Add(int.Parse(mc.layout.posY));
            }
            return co;
        }

        /// <summary>
        /// Deletes all selected components, channels and eventchannels
        /// </summary>
        private void DeleteSelectedComponents() {
            if (selectedComponentList.Count == 0 &&
                selectedChannelList.Count == 0 &&
                selectedEventChannelList.Count == 0)
                return;
            CommandObject co = new CommandObject("Add");

            foreach (componentType mc in selectedComponentList) {
                if (mc.ComponentType != ACS2.componentTypeDataTypes.group)
                    co.InvolvedObjects.Add(mc);
                else {
                    if (groupsList.ContainsKey(mc.id))
                        co.InvolvedObjects.Add(groupsList[mc.id]);
                }
            }
            // delete all selected channels
            foreach (channel ch in selectedChannelList) {
                DeleteChannel(ch);
                co.InvolvedObjects.Add(ch);
            }

            // delete all selected eventchannels
            if (focusedEventChannel != null) {

                focusedEventChannel = null;
                ResetPropertyDock();
            }
            foreach (eventChannelLine ech in selectedEventChannelList) {
                co.InvolvedObjects.Add(ech);
                foreach (eventChannel eventCh in eventChannelList) {
                    foreach (object o in co.InvolvedObjects) {
                        if (!(o is eventChannelLine))
                            continue;
                        if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) && (eventCh.targets.target.component.id == ((eventChannelLine)o).ListenerComponentId)) {
                            co.Parameter.Add(eventCh);
                        }
                    }
                }
                DeleteEventChannelCommand(ech);
            }

            foreach (componentType mc in selectedComponentList) {
                // delete all eventchannels from the mc
                // delete the eventChannels and therefore the events
                eventChannelLine eCL;
                for (int index = eventChannelLinesList.Count - 1; index >= 0; index--) {
                    eCL = (eventChannelLine)eventChannelLinesList[index];
                    if ((eCL.TriggerComponentId == mc.id) || (eCL.ListenerComponentId == mc.id)) {
                        focusedEventChannel = eCL;
                        // Ask a question, if component and therefore all events should be deleted. Causes some problems, if no is selected
                        // if (MessageBox.Show(Properties.Resources.DeleteEventChannelConfirmTextFormat(focusedEventChannel.TriggerComponentId, focusedEventChannel.ListernerComponentId),
                        //    Properties.Resources.DeleteEventChannelConfirmHeader, MessageBoxButton.YesNo, MessageBoxImage.Question) == MessageBoxResult.Yes) {
                        co.InvolvedObjects.Add(focusedEventChannel);
                        foreach (eventChannel eventCh in eventChannelList) {
                            foreach (object o in co.InvolvedObjects) {
                                if (!(o is eventChannelLine))
                                    continue;
                                if ((eventCh.sources.source.component.id == ((eventChannelLine)o).TriggerComponentId) &&
                                    (eventCh.targets.target.component.id == ((eventChannelLine)o).ListenerComponentId)) {
                                    co.Parameter.Add(eventCh);
                                }
                            }
                        }
                        DeleteEventChannelCommand(focusedEventChannel);
                        focusedEventChannel = null;
                    }
                }
                focusedEventChannel = null;

                // delete all channels from the mc
                if (mc.ports != null && mc.PortsList != null && mc.PortsList.Values != null) {
                    foreach (Object o in mc.PortsList.Values) {
                        if (o is inputPortType) {
                            inputPortType pIn = (inputPortType)o;
                            if (pIn.ChannelId != "") {
                                channel tempChannel = deploymentChannelList[pIn.ChannelId];
                                DeleteChannel(tempChannel);
                                co.InvolvedObjects.Add(tempChannel);
                            }
                        }
                        else if (o is outputPortType) {
                            outputPortType pOut = (outputPortType)o;
                            while (pOut.ChannelIds.Count > 0) {
                                channel tempChannel = deploymentChannelList[pOut.ChannelIds[0].ToString()];
                                DeleteChannel(tempChannel);
                                co.InvolvedObjects.Add(tempChannel);
                            }
                        }
                    }
                }
                ArrayList channelToDelete = new ArrayList();
                foreach (channel c in deploymentChannelList.Values) {
                    if (c.target.component.id.Equals(mc.id))
                        channelToDelete.Add(c);
                }
                foreach (channel c in channelToDelete) {
                    DeleteChannel(c);
                }


                if (canvas.Children.Count > 0) {
                    Keyboard.Focus(canvas.Children[0]);
                }
                else {
                    Keyboard.Focus(canvas);
                }
                if (focusedComponent == mc)
                    focusedComponent = null;
                DeleteComponent(mc);
            }
            undoStack.Push(co);
            redoStack.Clear();
            DeleteDanglineLines();
            ClearSelectedChannelList();
            ClearSelectedComponentList();
            ClearSelectedEventChannelList();
            ResetPropertyDock();
        }

        /// <summary>
        /// Set all channels with a different datatype as dataTypeto visible or unvisible according to the given arguments
        /// </summary>
        /// <param name="dataType">Specifies the datatype which should not be affected by the operation.</param>
        /// <param name="inputports">If set to true inputports with a datatype different to dataType will get hidden/visible. If set to false inputports will not get changed.</param>
        /// <param name="outputports">If set to true outputports with a datatype different to dataType will get hidden/visible. If set to false outputports will not get changed.</param>
        /// <param name="hide">If true all selected channels get invisible. If set to false all selected channels get visible.</param>
        private void ChangeChannelVisibility(Asterics.ACS2.dataType dataType, bool inputports, bool outputports, bool hide) {
            if (hide == true)
                hiddenChannels = true;
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.boolean))
                ChangeVisibleChannels(ACS2.dataType.boolean, outputports, inputports, hide);
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.integer))
                ChangeVisibleChannels(ACS2.dataType.integer, outputports, inputports, hide);
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.@byte))
                ChangeVisibleChannels(ACS2.dataType.@byte, outputports, inputports, hide);
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.@char))
                ChangeVisibleChannels(ACS2.dataType.@char, outputports, inputports, hide);
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.@double))
                ChangeVisibleChannels(ACS2.dataType.@double, outputports, inputports, hide);
            if (!CheckInteroperabilityOfPorts(dataType, ACS2.dataType.@string))
                ChangeVisibleChannels(ACS2.dataType.@string, outputports, inputports, hide);
        }

        /// <summary>
        /// Set all inputports and outputports visible
        /// </summary>
        private void SetAllChannelsVisible() {
            ChangeVisibleChannels(ACS2.dataType.boolean, true, true, false);
            ChangeVisibleChannels(ACS2.dataType.integer, true, true, false);
            ChangeVisibleChannels(ACS2.dataType.@byte, true, true, false);
            ChangeVisibleChannels(ACS2.dataType.@char, true, true, false);
            ChangeVisibleChannels(ACS2.dataType.@double, true, true, false);
            ChangeVisibleChannels(ACS2.dataType.@string, true, true, false);
        }

        /// <summary>
        /// This method changes the visibility of all channels with the specified datatype according to the given arguments.
        /// </summary>
        /// <param name="dataType">Datatype of the channels which should get changed</param>
        /// <param name="outputports">True if outputports should be affected, false otherwise.</param>
        /// <param name="inputports">True if inputports should be affected, false otherwise</param>
        /// <param name="hidden">True if the channels should become invisible, true if they should become visible</param>
        private void ChangeVisibleChannels(Asterics.ACS2.dataType dataType, bool outputports, bool inputports, bool hidden) {
            // set visibility to "hidden" in the deploymentmodel
            foreach (componentType mc in deploymentModel.components) {
                if (mc != null && mc.ports != null && mc.ports != null) {
                    foreach (object o in mc.ports) {
                        if (outputports && o is outputPortType && ((outputPortType)o).PortDataType == dataType && ((outputPortType)o).ChannelIds.Count == 0) {
                            if (hidden) {
                                ((outputPortType)o).PortRectangle.Visibility = Visibility.Hidden;
                                ((outputPortType)o).PortLabel.Visibility = Visibility.Hidden;
                            }
                            else {
                                ((outputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                                ((outputPortType)o).PortLabel.Visibility = Visibility.Visible;
                            }
                        }
                        else if (inputports && o is inputPortType && ((inputPortType)o).PortDataType == dataType && ((inputPortType)o).ChannelId.Equals("")) {
                            if (hidden) {
                                ((inputPortType)o).PortRectangle.Visibility = Visibility.Hidden;
                                ((inputPortType)o).PortLabel.Visibility = Visibility.Hidden;
                            }
                            else {
                                ((inputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                                ((inputPortType)o).PortLabel.Visibility = Visibility.Visible;
                            }
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Set all Channels visible which have the givent dataType
        /// </summary>
        /// <param name="dataType"></param>
        private void ShowChannels(Asterics.ACS2.dataType dataType) {
            // set visibility to "hidden" in the deploymentmodel
            foreach (componentType mc in deploymentModel.components) {
                if (mc != null && mc.ports != null) {
                    foreach (object o in mc.ports) {
                        if (o is outputPortType && ((outputPortType)o).PortDataType == dataType) {
                            ((outputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                            ((outputPortType)o).PortLabel.Visibility = Visibility.Visible;
                        }
                        else if (o is inputPortType && ((inputPortType)o).PortDataType == dataType) {
                            ((inputPortType)o).PortRectangle.Visibility = Visibility.Visible;
                            ((inputPortType)o).PortLabel.Visibility = Visibility.Visible;
                        }
                    }
                }
            }
        }


        /// <summary>
        /// Trims the name of the models.
        /// E.g.: asterics.filewriter becomes filewriter
        /// </summary>
        /// <returns></returns>
        private string TrimComponentName(string name) {
            if (name.StartsWith("asterics.")) {
                name = name.Substring(9);
            }
            return name;
        }

        #endregion // Internal functions

        #region Keyboard listners


        /// <summary>
        /// Key listner: trigger combo boxes in event tab opens with the enter-key
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void EventCombobox_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Enter) {
                ComboBox cb = (ComboBox)sender;
                cb.IsDropDownOpen = true;
            }
        }

        /// <summary>
        /// Key up event on a component. Needed to show the context menu with
        /// the Apps-key (aka context-key, application-key, ...). Not working within Key down event!
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Component_KeyUp(object sender, KeyEventArgs e) {
            if (e.Key == Key.Apps) {
                e.Handled = true;
                BuildKeyboardContextMenu();
            }
        }

        /// <summary>
        /// Key event on a component. The following actions are possible: open context menu,
        /// move component, end move operation, tab to next element, delete component
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Component_KeyDown(object sender, KeyEventArgs e) {

            if (e.Key == Key.Space) { // || e.Key == Key.Apps)
                e.Handled = true;
                if (Keyboard.Modifiers == ModifierKeys.Control) {
                    if (selectedComponentList.Contains(focusedComponent)) {
                        ClearBorderOfSelectedComponents();
                        selectedComponentList.Remove(focusedComponent);
                        UpdateSelectedComponents();
                    }
                    else
                        AddSelectedComponent(focusedComponent);

                }
                else {
                    if (!selectedComponentList.Contains(focusedComponent)) {
                        ClearSelectedComponentList();
                        ClearSelectedChannelList();
                        ClearSelectedEventChannelList();
                        AddSelectedComponent(focusedComponent);
                    }
                }
            }
            else if (e.Key == Key.Up) {
                if (componentToMove != null) {
                    e.Handled = true;
                    //MoveComponent(componentToMove, (int)Canvas.GetLeft(componentToMove.ComponentCanvas) + offsetX, (int)Canvas.GetTop(componentToMove.ComponentCanvas) - 2 + offsetY );
                    foreach (componentType mc in selectedComponentList)
                        MoveComponent(mc, (int)Canvas.GetLeft(mc.ComponentCanvas) + offsetX, (int)Canvas.GetTop(mc.ComponentCanvas) - 2 + offsetY);
                }
            }
            else if (e.Key == Key.Down) {
                if (componentToMove != null) {
                    e.Handled = true;
                    //MoveComponent(componentToMove, (int)Canvas.GetLeft(componentToMove.ComponentCanvas) + offsetX, (int)Canvas.GetTop(componentToMove.ComponentCanvas) + 2 + offsetY);
                    foreach (componentType mc in selectedComponentList)
                        MoveComponent(mc, (int)Canvas.GetLeft(mc.ComponentCanvas) + offsetX, (int)Canvas.GetTop(mc.ComponentCanvas) + 2 + offsetY);
                }
            }
            else if (e.Key == Key.Left) {
                if (componentToMove != null) {
                    e.Handled = true;
                    //MoveComponent(componentToMove, (int)Canvas.GetLeft(componentToMove.ComponentCanvas) - 2 + offsetX, (int)Canvas.GetTop(componentToMove.ComponentCanvas) + offsetY);
                    foreach (componentType mc in selectedComponentList)
                        MoveComponent(mc, (int)Canvas.GetLeft(mc.ComponentCanvas) - 2 + offsetX, (int)Canvas.GetTop(mc.ComponentCanvas) + offsetY);

                }
            }
            else if (e.Key == Key.Right) {
                if (componentToMove != null) {
                    e.Handled = true;
                    //MoveComponent(componentToMove, (int)Canvas.GetLeft(componentToMove.ComponentCanvas) + 2 + offsetX, (int)Canvas.GetTop(componentToMove.ComponentCanvas) + offsetY);
                    foreach (componentType mc in selectedComponentList)
                        MoveComponent(mc, (int)Canvas.GetLeft(mc.ComponentCanvas) + 2 + offsetX, (int)Canvas.GetTop(mc.ComponentCanvas) + offsetY);
                }
            }
            else if (e.Key == Key.Enter) {
                if (componentToMove != null) {
                    componentToMove = null;
                    moveComponentRibbonButton.IsChecked = false;
                }
            }
            else if (e.Key == Key.Tab) {
                if (componentToMove != null) {
                    componentToMove = null;
                }
                //} else if (e.Key == Key.Delete) {
                //    e.Handled = true;
                //    DeleteSelectedComponents();
                //} else if (e.Key == Key.C) {
                //    if (ModifierKeys.Control == e.KeyboardDevice.Modifiers)
                //        CopySelectedCommand();
                //} else if (e.Key == Key.V) {
                //    if (ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                //        model tmpModel = CopyModel(copyModel);
                //        PasteCopiedModel();
                //        copyModel = tmpModel;
                //        e.Handled = true;
                //    }
                //} else if (e.Key == Key.A && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                //    SelectAll();
            }

        }

        /// <summary>
        /// Key down on a focused channel, can open context menu or delete channel
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Channel_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Space) {
                e.Handled = true;
                if (Keyboard.Modifiers == ModifierKeys.Control) {
                    if (selectedChannelList.Contains(focusedChannel)) {
                        ClearColorOfSelectedChannels();
                        selectedChannelList.Remove(focusedChannel);
                        UpdateSelectedChannels();
                    }
                    else
                        AddSelectedChannel(focusedChannel);
                }
                else {
                    if (!selectedChannelList.Contains(focusedChannel)) {
                        ClearSelectedComponentList();
                        ClearSelectedChannelList();
                        ClearSelectedEventChannelList();
                        AddSelectedChannel(focusedChannel);
                    }
                }
                /*((Line)sender).ContextMenu.PlacementTarget = (Line)sender;
                ((Line)sender).ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Relative;

                ((Line)sender).ContextMenu.HorizontalOffset = ((Line)sender).X1;
                ((Line)sender).ContextMenu.VerticalOffset = ((Line)sender).Y1;

                ((Line)sender).ContextMenu.IsOpen = true;*/
            }
            //else if (e.Key == Key.Delete) {
            //    e.Handled = true;
            //    DeleteSelectedComponents();
            //} else if (e.Key == Key.A && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
            //    SelectAll();
            //} else if (e.Key == Key.C && ModifierKeys.Control == e.KeyboardDevice.Modifiers ) {
            //    CopySelectedCommand();
            //    e.Handled = true;
            //} else if (e.Key == Key.V) {
            //    e.Handled = true;
            //    if (ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
            //        model tmpModel = CopyModel(copyModel);
            //        PasteCopiedModel();
            //        copyModel = tmpModel;
            //    }
            //}
        }

        /// <summary>
        /// Key down on a focused eventchannel, can open context menu or delete eventchannel
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EventChannel_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Space) {
                e.Handled = true;
                if (Keyboard.Modifiers == ModifierKeys.Control) {
                    if (selectedEventChannelList.Contains(focusedEventChannel)) {
                        ClearColorOfSelectedEventChannels();
                        selectedEventChannelList.Remove(focusedEventChannel);
                        UpdateSelectedEventChannels();
                    }
                    else
                        AddSelectedEventChannel(focusedEventChannel);
                }
                else {
                    if (!selectedEventChannelList.Contains(focusedEventChannel)) {
                        ClearSelectedComponentList();
                        ClearSelectedChannelList();
                        ClearSelectedEventChannelList();
                        AddSelectedEventChannel(focusedEventChannel);
                    }
                }
                if (GetSelectedObjectsCount() > 1)
                    ResetPropertyDock();
                /*((Line)sender).ContextMenu.PlacementTarget = (Line)sender;
                ((Line)sender).ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Relative;

                ((Line)sender).ContextMenu.HorizontalOffset = ((Line)sender).X1;
                ((Line)sender).ContextMenu.VerticalOffset = ((Line)sender).Y1;

                ((Line)sender).ContextMenu.IsOpen = true;*/
                //} else if (e.Key == Key.Delete) {
                //    e.Handled = true;
                //    DeleteSelectedComponents();
                //} else if (e.Key == Key.A && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                //    SelectAll();
                //} else if (e.Key == Key.C && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                //    CopySelectedCommand();
                //    e.Handled = true;
                //} else if (e.Key == Key.V) {
                //    e.Handled = true;
                //    if (ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                //        model tmpModel = CopyModel(copyModel);
                //        PasteCopiedModel();
                //        copyModel = tmpModel;
                //    }
            }
        }

        /// <summary>
        /// Global keyboard listener within the application. Used to handle 'Esc'-key, F-keys, Zoom and Shortcuts for avalon-windows
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Global_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Escape) {
                if (channelToConnect != null) {
                    canvas.Children.Remove(channelToConnect.Line);
                    channelToConnect = null;
                }
                if (eventChannelToConnect != null) {
                    canvas.Children.Remove(eventChannelToConnect.Line);
                    eventChannelToConnect = null;
                }
            }

            if ((Keyboard.Modifiers == ModifierKeys.Control) && (e.Key == Key.P)) {
                Keyboard.Focus(dockableComponentProperties);
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && (e.Key == Key.F))
            {
                ribbonComponentsTab.IsSelected = true;
                Keyboard.Focus(ribbonComponentsTab);
                autoCompleteTextBox.focusTextbox();
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && (e.Key == Key.E)) {
                Keyboard.Focus(dockableEventsTab);
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && (e.Key == Key.D)) {
                Keyboard.Focus(scrollCanvars);

                // Timer is setting the focus to the first element. Otherwise, the system is still changing the tabs
                // and the focus will not be set, as the elements are not visible
                TimerCallback tcbFocus = this.DoFocusTimer;                
                focusTimer = new Timer(tcbFocus, canvas, 300, Timeout.Infinite);
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && (e.Key == Key.G)) {
                Keyboard.Focus(GUIEditorCanvas);

                
                // Timer is setting the focus to the first element. Otherwise, the system is still changing the tabs
                // and the focus will not be set, as the elements are not visible
                TimerCallback tcbFocus = this.DoFocusTimer; 
                focusTimer = new Timer(tcbFocus, guiCanvas, 300, Timeout.Infinite);                                
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && ((e.Key == Key.Add) || (e.Key == Key.OemPlus))) {
                if (zoomSlider.Value < zoomSlider.Maximum) {
                    zoomSlider.Value += zoomSlider.SmallChange;
                }
            }
            else if ((Keyboard.Modifiers == ModifierKeys.Control) && ((e.Key == Key.Subtract) || (e.Key == Key.OemMinus))) {
                if (zoomSlider.Value > zoomSlider.Minimum) {
                    zoomSlider.Value -= zoomSlider.SmallChange;
                }
            }
            else if (e.Key == Key.F1) {
                Help_Click(sender, null);
            }
            else if (e.Key == Key.F5) {
                if (runModelButton.IsEnabled) {
                    StartModel_Click(null, null);
                }
            }
            else if (e.Key == Key.F6) {
                if (pauseModelButton.IsEnabled) {
                    PauseModel_Click(null, null);
                }
            }
            else if (e.Key == Key.F7) {
                if (stopModelButton.IsEnabled) {
                    StopModel_Click(null, null);
                }
            }
            else if (e.Key == Key.A && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                SelectAll();
            }
            else if (e.Key == Key.C && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                if (!((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause))) {
                    CopySelectedCommand();
                    e.Handled = true;
                    pasteRibbonButton.IsEnabled = true;
                }
            }
            else if (e.Key == Key.V && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                if (!((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause))) {
                    model tmpModel = CopyModel(copyModel);
                    PasteCopiedModel(copyModel, false,true);
                    copyModel = tmpModel;
                }
            }
            else if (e.Key == Key.X && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                if (!((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause))) {
                    CopySelectedCommand();
                    DeleteSelectedComponents();
                    pasteRibbonButton.IsEnabled = true;
                }
            } else if (e.Key == Key.Delete) {
                if (!((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause))) {
                    DeleteSelectedComponents();
                }
            } else if (e.Key == Key.Z && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                if (undoButton.IsEnabled) {
                    Undo_Click(sender, e);
                }
            } else if (e.Key == Key.Y && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                if (redoButton.IsEnabled) {
                    Redo_Click(sender, e);
                }
            } else if (e.Key == Key.S && ModifierKeys.Control == e.KeyboardDevice.Modifiers) {
                // If a property has been edited and the focus has not been set to another element, the property will not be set. 
                // Clicking ribbon elments did not remove focus from property editor, so the property will
                // not be set. Causes problems, saving, uplaoding, ... the model
                if (canvas.Children.Count > 0) {
                    Keyboard.Focus(canvas.Children[0]);
                } else {
                    Keyboard.Focus(canvas);
                }
                String mustBeConnectedError = MustBeConnectedChecker();
                if (mustBeConnectedError != "") {
                    MessageBox.Show(mustBeConnectedError, Properties.Resources.MustBeConnectedCheckerHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                } else {
                    // if no file for saving is defined yet, show the saveas-dialog
                    if (saveFile != null) {
                        SaveLocalCommand(false);
                    } else {
                        SaveLocalCommand(true);
                    }
                }
            }
        }

        /// <summary>
        /// Function is called, when a key is pressed on an item in the Recently Opened Files
        /// </summary>
        void recentFileItem_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Enter) {
                RibbonGalleryItem rf = (RibbonGalleryItem)sender;
                if (File.Exists((String)rf.Tag)) {
                    CheckIfSavedAndOpenCommand((String)rf.Tag);
                }
                else {
                    RemoveFromRecentList((String)rf.Tag);
                    MessageBox.Show(Properties.Resources.RecentFileNotFound, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
        }

        #endregion // Keyboard listeners

        #region Property Listeners


        private void OutputPortIntPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            outputPortType  groupPort = (outputPortType)sender;
            if ((deploymentModel.groups != null)  && (deploymentComponentList[groupPort.ComponentId].ComponentType == ACS2.componentTypeDataTypes.group)) {
                foreach (group groupToUpdate in deploymentModel.groups) {
                    if (groupToUpdate.id == groupPort.ComponentId) {
                        List<portAlias> aliasList = new List<portAlias>();
                        if (groupToUpdate.portAlias != null) {
                            aliasList = groupToUpdate.portAlias.ToList();
                        }
                        if (groupPort.PortAliasForGroups == "" || groupPort.PortAliasForGroups == " ") {
                            groupPort.PortLabel.Text = groupPort.portTypeID;
                        } else {
                            groupPort.PortLabel.Text = groupPort.PortAliasForGroups;
                        }
                        // check, if an alias for this port already exists
                        portAlias foundAlias = null;
                        foreach (portAlias alias in aliasList) {
                            if (alias.portId == groupPort.portTypeID) {
                                alias.portAlias1 = groupPort.PortAliasForGroups;
                                foundAlias = alias;
                                break;
                            }
                        }
                        if ((foundAlias != null) && ((groupPort.PortAliasForGroups == "") || (groupPort.PortAliasForGroups == " "))) {
                            aliasList.Remove(foundAlias);
                        } else if (foundAlias == null) {
                            portAlias newAlias = new portAlias();
                            newAlias.portAlias1 = groupPort.PortAliasForGroups;
                            newAlias.portId = groupPort.portTypeID;
                            aliasList.Add(newAlias);
                        }
                        groupToUpdate.portAlias = aliasList.ToArray();

                    }
                }
            } else {
                MessageBox.Show("When the model will be saved, only alias for ports of groups will be stored", Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            }
        }

        private void InputPortIntPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            inputPortType groupPort = (inputPortType)sender;            
            if (e.PropertyName == "sync")
            {
                if (deploymentComponentList.ContainsKey(groupPort.ComponentId)) {
                    UpdatePortsToolTips(deploymentComponentList[groupPort.ComponentId]);
                }
                return;
            }
            if ((deploymentModel.groups != null)  && (deploymentComponentList[groupPort.ComponentId].ComponentType == ACS2.componentTypeDataTypes.group)) {
                foreach (group groupToUpdate in deploymentModel.groups) {
                    if (groupToUpdate.id == groupPort.ComponentId) {
                        List<portAlias> aliasList = new List<portAlias>();
                        if (groupToUpdate.portAlias != null) {
                            aliasList = groupToUpdate.portAlias.ToList();
                        }
                        if (groupPort.PortAliasForGroups == "" || groupPort.PortAliasForGroups == " ") {
                            groupPort.PortLabel.Text = groupPort.portTypeID;
                        } else {
                            groupPort.PortLabel.Text = groupPort.PortAliasForGroups;
                        }                      
                        // check, if an alias for this port already exists
                        portAlias foundAlias = null;
                        foreach (portAlias alias in aliasList) {
                            if (alias.portId == groupPort.portTypeID) {
                                alias.portAlias1 = groupPort.PortAliasForGroups;
                                foundAlias = alias;
                                break;
                            }
                        }
                        if ((foundAlias != null) && ((groupPort.PortAliasForGroups == "") || (groupPort.PortAliasForGroups == " "))) {
                            aliasList.Remove(foundAlias);
                        } else if (foundAlias == null) {
                            portAlias newAlias = new portAlias();
                            newAlias.portAlias1 = groupPort.PortAliasForGroups;
                            newAlias.portId = groupPort.portTypeID;
                            aliasList.Add(newAlias);
                        }
                        groupToUpdate.portAlias = aliasList.ToArray();
                      
                    }
                }
            } else {
                MessageBox.Show("When the model will be saved, only alias for ports of groups will be stored", Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            }
        }

        /// <summary>
        /// Fired, if an internal property in the component (at the moment, only the Id possible) will be changed. Sets the new name of the component
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentIntPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            componentType tempComponent = (componentType)sender;
            if (e.PropertyName == "Component Name" && !deploymentComponentList.Keys.Contains(tempComponent.id) && tempComponent.id != "" && tempComponent.id != backupIdForPropertyEditor) {
                string backupName = tempComponent.Label.Text;
                tempComponent.Label.Text = tempComponent.id;
                if (tempComponent.gui != null && tempComponent.gui.GuiElementCanvas != null) {
                    foreach (object o in tempComponent.gui.GuiElementCanvas.Children) {
                        if (o is TextBlock) {
                            TextBlock tb = (TextBlock)o;
                            tb.Text = tempComponent.id;
                        }
                    }
                }
                deploymentComponentList.Remove(backupIdForPropertyEditor);
                deploymentComponentList.Add(tempComponent.id, tempComponent);
                // all connected channels also have to be updated

                if (deploymentModel.groups != null) {
                    foreach (group g in deploymentModel.groups) {
                        if (g.id == backupIdForPropertyEditor)
                            g.id = tempComponent.id;
                    }
                }
                foreach (Object o in tempComponent.PortsList.Values) {
                    if (o is inputPortType) {
                        inputPortType pIn = (inputPortType)o;
                        if (pIn.ChannelId != "") {
                            deploymentChannelList[pIn.ChannelId].target.component.id = tempComponent.id;
                        }
                        if (pIn.ComponentId.Equals(backupIdForPropertyEditor))
                            pIn.ComponentId = tempComponent.id;
                    }
                    else if (o is outputPortType) {
                        outputPortType pOut = (outputPortType)o;
                        if (pOut.ChannelIds.Count > 0) {
                            foreach (string s in pOut.ChannelIds) {
                                deploymentChannelList[s].source.component.id = tempComponent.id;
                            }
                        }
                        if (pOut.ComponentId.Equals(backupIdForPropertyEditor))
                            pOut.ComponentId = tempComponent.id;
                    }
                }

                // all connected eventChannels have to be updated
                foreach (eventChannel ec in eventChannelList) {
                    if (ec.sources.source.component.id == backupIdForPropertyEditor) {
                        ec.sources.source.component.id = tempComponent.id;
                    }
                    if (ec.targets.target.component.id == backupIdForPropertyEditor) {
                        ec.targets.target.component.id = tempComponent.id;
                    }
                }

                // all connected eventChannel lines have to be updated
                if (tempComponent.EventListenerList.Count > 0) {
                    foreach (eventChannelLine line in eventChannelLinesList) {
                        if (line.ListenerComponentId == backupIdForPropertyEditor) {
                            line.ListenerComponentId = tempComponent.id;
                        }
                    }
                }
                if (tempComponent.EventTriggerList.Count > 0) {
                    foreach (eventChannelLine line in eventChannelLinesList) {
                        if (line.TriggerComponentId == backupIdForPropertyEditor) {
                            line.TriggerComponentId = tempComponent.id;
                        }
                    }
                }


                if (groupsList.ContainsKey(backupIdForPropertyEditor)) {
                    groupComponent gc = groupsList[backupIdForPropertyEditor];
                    groupsList.Remove(backupIdForPropertyEditor);
                    gc.ID = tempComponent.id;
                    groupsList.Add(tempComponent.id, gc);
                }

                // if the ACS is synchronised with ARE, this has to be cut
                if ((areStatus.Status == AREStatus.ConnectionStatus.Pause) || (areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Synchronised)) {
                    areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                    areStatus.Status = AREStatus.ConnectionStatus.Connected;
                }
                modelHasBeenEdited = true;
                // If the name is already in use, it will not be changed
            } else if ((e.PropertyName == "Component Name") && (tempComponent.id != backupIdForPropertyEditor)) {
                MessageBox.Show(Properties.Resources.ChangingComponentIdFormat(tempComponent.id), Properties.Resources.ChangingComponentIdHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                tempComponent.id = backupIdForPropertyEditor;
            } else if (e.PropertyName == "Component Description" && tempComponent.ComponentType == ACS2.componentTypeDataTypes.group) {
                foreach (group gr in deploymentModel.groups) {
                    if (gr.id == tempComponent.id) {
                        gr.description = tempComponent.description;
                        break;
                    }
                }
            }
            UpdateToolTips();
        }

        /// <summary>
        /// Fired, when an input port property was changed. Will be commited to the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void InPortPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            if ((asapiClient != null) && (areStatus.Status == AREStatus.ConnectionStatus.Running ||
                areStatus.Status == AREStatus.ConnectionStatus.Pause || areStatus.Status == AREStatus.ConnectionStatus.Synchronised)) {
                string portId = "empty";
                foreach (object o in focusedComponent.PortsList.Values) {
                    if (o is inputPortType) {
                        if (((inputPortType)o).PropertyArrayList.Contains((propertyType)sender)) {
                            portId = ((inputPortType)o).portTypeID;
                            break;
                        }
                    }
                }
                try {
                    // TODO: uncomment, when implemented on ARE
                    //asapiClient.SetPortProperty(focusedComponent.id, portId, ((propertyType)sender).name, ((propertyType)sender).value);
                }
                catch (Exception ex) {
                    MessageBox.Show(Properties.Resources.SetPropertyErrorDialog, Properties.Resources.SetPropertyErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                }

            }
            modelHasBeenEdited = true;
            UpdateToolTips();
        }

        /// <summary>
        /// Fired, when an output port property was changed. Will be commited to the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void OutPortPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            if ((asapiClient != null) && (areStatus.Status == AREStatus.ConnectionStatus.Running ||
                areStatus.Status == AREStatus.ConnectionStatus.Pause || areStatus.Status == AREStatus.ConnectionStatus.Synchronised)) {
                string portId = "empty";
                foreach (object o in focusedComponent.PortsList.Values) {
                    if (o is outputPortType) {
                        if (((outputPortType)o).PropertyArrayList.Contains((propertyType)sender)) {
                            portId = ((outputPortType)o).portTypeID;
                            break;
                        }
                    }
                }
                try {
                    // TODO: uncomment, when implemented on ARE
                    //asapiClient.SetPortProperty(focusedComponent.id, portId, ((propertyType)sender).name, ((propertyType)sender).value);
                }
                catch (Exception ex) {
                    MessageBox.Show(Properties.Resources.SetPropertyErrorDialog, Properties.Resources.SetPropertyErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                }
            }
            modelHasBeenEdited = true;
            UpdateToolTips();
        }

        /// <summary>
        /// Fired, if a property of a component will be changed. Will be commited to the ARE
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentPropertyChanged(Object sender, PropertyChangedEventArgs e) {
            if ((asapiClient != null) && (areStatus.Status == AREStatus.ConnectionStatus.Running ||
                areStatus.Status == AREStatus.ConnectionStatus.Pause || areStatus.Status == AREStatus.ConnectionStatus.Synchronised)) {
                    try {
                        String retVal = asapiClient.SetComponentProperty(backupIdForPropertyEditor, ((propertyType)sender).name, ((propertyType)sender).value);
                    // } catch (Thrift.Transport.TTransportException tEx) {
                    //    try {
                          
                    //        MessageBox.Show(Properties.Resources.SetPropertyErrorDialog, Properties.Resources.SetPropertyErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    //        traceSource.TraceEvent(TraceEventType.Error, 3, tEx.Message);
                    //    } catch (Exception innerEx) { }
                    } catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.SetPropertyErrorDialog, Properties.Resources.SetPropertyErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                    } 
            }
            modelHasBeenEdited = true;
            UpdateToolTips();
        }

        /// <summary>
        /// Fired, if an unvalid value (e.g. char in an integer field) will be entered in the property editor. Refresh the active property
        /// editor
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void ComponentPropertyChangeError(Object sender, PropertyChangedEventArgs e) {
            MessageBox.Show(Properties.Resources.PropertyValidationErrorFormat(((propertyType)sender).DataType.ToString()), Properties.Resources.PropertyValidationErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
            activePropertyGrid.Refresh();
        }

        /// <summary>
        /// Check, if the undoStack has been changed, and if yes, activare or deactivate the undo-Button
        /// If ARE and ACS are synchrinised, the status will be changed (only exception: a component move)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void undoStack_PropertyChanged(object sender, PropertyChangedEventArgs e) {
            if (e.PropertyName.Equals("Pop")) {
                if (undoStack.Count == 1) {
                    undoButton.IsEnabled = false;
                    UndoQuickAccess.IsEnabled = false;
                }
                else {
                    undoButton.IsEnabled = true;
                    UndoQuickAccess.IsEnabled = true;
                }
            }
            else {
                if (undoStack.Count == 0) {
                    undoButton.IsEnabled = false;
                    UndoQuickAccess.IsEnabled = false;
                }
                else {
                    undoButton.IsEnabled = true;
                    UndoQuickAccess.IsEnabled = true;
                }
            }
            if (areStatus.Status == AREStatus.ConnectionStatus.Synchronised) {
                AstericsStack<CommandObject> oSender = (AstericsStack<CommandObject>)sender;
                if ((oSender.Count > 0) && (!oSender.First().Command.Equals("moveComponent"))) {
                    areStatus.Status = AREStatus.ConnectionStatus.Connected;
                }
            }
        }

        /// <summary>
        /// Check, if the redoStack has been changed, and if yes, activare or deactivate the undo-Button
        /// If ARE and ACS are synchrinised, the status will be changed (only exception: a component move)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void redoStack_PropertyChanged(object sender, PropertyChangedEventArgs e) {
            if (e.PropertyName.Equals("Pop")) {
                if (redoStack.Count == 1) {
                    redoButton.IsEnabled = false;
                }
                else {
                    redoButton.IsEnabled = true;
                }
            }
            else {
                if (redoStack.Count == 0) {
                    redoButton.IsEnabled = false;
                }
                else {
                    redoButton.IsEnabled = true;
                }
            }
            if (areStatus.Status == AREStatus.ConnectionStatus.Synchronised) {
                AstericsStack<CommandObject> oSender = (AstericsStack<CommandObject>)sender;
                if ((oSender.Count > 0) && (!oSender.First().Command.Equals("moveComponent"))) {
                    areStatus.Status = AREStatus.ConnectionStatus.Connected;
                }
            }
        }

        /// <summary>
        /// Listener, when the AREStatus has been changed. Setting the status of
        /// several buttons and enables or disables the canvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void AreStatusChanged(object sender, PropertyChangedEventArgs e) {
            switch (areStatus.Status) {
                case AREStatus.ConnectionStatus.Disconnected:
                    runModelButton.IsEnabled = false;
                    pauseModelButton.IsEnabled = false;
                    stopModelButton.IsEnabled = false;
                    connectAREButton.IsEnabled = true;
                    disconnectAREButton.IsEnabled = false;
                    uplaodSchemaButton.IsEnabled = false;
                    downlaodSchemaButton.IsEnabled = false;
                    downloadBundlesButton.IsEnabled = false;
                    storeModelButton.IsEnabled = false;
                    loadModelFromStorageButton.IsEnabled = false;
                    activateStoredModelButton.IsEnabled = false;
                    deleteStoredModelButton.IsEnabled = false;
                    autorunModelButton.IsEnabled = false;
                    statusRibbonButton.IsEnabled = false;
                    showLogRibbonButton.IsEnabled = false;
                    statusBar.Text = Properties.Resources.AREStatusDisconnected;
                    EnableCanvas();
                    StopStatusPolling();
                    dispatcherHelperRibbonButton.IsEnabled = false; 
                    break;
                case AREStatus.ConnectionStatus.Connected:
                    runModelButton.IsEnabled = false;
                    pauseModelButton.IsEnabled = false;
                    stopModelButton.IsEnabled = false;
                    connectAREButton.IsEnabled = false;
                    disconnectAREButton.IsEnabled = true;
                    uplaodSchemaButton.IsEnabled = true;
                    downlaodSchemaButton.IsEnabled = true;
                    downloadBundlesButton.IsEnabled = true;
                    storeModelButton.IsEnabled = true;
                    loadModelFromStorageButton.IsEnabled = true;
                    activateStoredModelButton.IsEnabled = true;
                    deleteStoredModelButton.IsEnabled = true;
                    autorunModelButton.IsEnabled = true;
                    statusRibbonButton.IsEnabled = true;
                    showLogRibbonButton.IsEnabled = true;
                    dispatcherHelperRibbonButton.IsEnabled = true; 
                    statusBar.Text = Properties.Resources.AREStatusConnected;
                    StopStatusPolling();
                    break;
                case AREStatus.ConnectionStatus.Synchronised:
                    runModelButton.IsEnabled = true;
                    pauseModelButton.IsEnabled = false;
                    stopModelButton.IsEnabled = false;
                    connectAREButton.IsEnabled = false;
                    disconnectAREButton.IsEnabled = true;
                    // TODO: uncomment the following lines, just in comment as
                    // long as .SetProperty and .SetPortProperty is not implemented on ARE
                    //uplaodSchemaButton.IsEnabled = false;
                    downlaodSchemaButton.IsEnabled = false;
                    downloadBundlesButton.IsEnabled = false;
                    statusBar.Text = Properties.Resources.AREStatusSynchronised;
                    EnableCanvas();
                    StartStatusPolling();
                    GetAllDynamicProperties();
                    break;
                case AREStatus.ConnectionStatus.Running:
                    runModelButton.IsEnabled = false;
                    stopModelButton.IsEnabled = true;
                    pauseModelButton.IsEnabled = true;
                    statusBar.Text = Properties.Resources.AREStatusRunning;
                    DisableCanvas();
                    StartStatusPolling();
                    break;
                case AREStatus.ConnectionStatus.Pause:
                    runModelButton.IsEnabled = true;
                    pauseModelButton.IsEnabled = false;
                    statusBar.Text = Properties.Resources.AREStatusPause;
                    break;
            }
        }

        /// <summary>
        /// Is called when the event channel description field lost focus --> the corresponding event channel object is updated.
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EvtChnlDescription_LostFocus(Object sender, RoutedEventArgs e)
        {
            EvtChannelDescriptionTextBox evtChnlDescTextBox = (EvtChannelDescriptionTextBox)e.Source;
            ComboBox triggerBox = (ComboBox)evtChnlDescTextBox.eventTriggerComboBox;
            TextBox listenerBox = (TextBox)evtChnlDescTextBox.eventListenerTextBox;
            if (!triggerBox.SelectedValue.Equals("---"))
            {
                String sourceComponentId=focusedEventChannel.TriggerComponentId;
                String sourceEventId=triggerBox.Text;
                String targetComponentId=focusedEventChannel.ListenerComponentId;
                String targetEventId=listenerBox.Text;

                if (focusedEventChannel.HasGroupSource)
                {
                    componentType source = GetComponentTypeFromEventString(sourceEventId);
                    if (source != null)
                    {
                        sourceComponentId = source.id;
                        sourceEventId = sourceEventId.Substring(source.id.Length + 1);
                    }
                }

                if (focusedEventChannel.HasGroupTarget)
                {
                    componentType target = GetComponentTypeFromEventString(targetEventId);
                    if (target != null)
                    {
                        targetComponentId = target.id;
                        targetEventId = targetEventId.Substring(target.id.Length + 1);
                    }
                }

                eventChannel updateEventChannel = findEventChannel(sourceComponentId, sourceEventId, targetComponentId, targetEventId);
                if (updateEventChannel != null)
                {
                    updateEventChannel.description = constructEvtChannelDescription(updateEventChannel.sources.source.eventPort.id, updateEventChannel.targets.target.eventPort.id, evtChnlDescTextBox);
                }
            }
            UpdateToolTips();
        }

        /// <summary>
        /// Finds the eventChannel that is connected between the given ids.
        /// </summary>
        /// <param name="sourceComponentId"></param>
        /// <param name="sourceEventId"></param>
        /// <param name="targetComponentId"></param>
        /// <param name="targetEventId"></param>
        /// <returns></returns>
        eventChannel findEventChannel(String sourceComponentId, String sourceEventId, String targetComponentId, String targetEventId)
        {
            foreach (eventChannel updateEvent in eventChannelList)
            {
                if ((updateEvent.sources.source.component.id == sourceComponentId) &&
                    (updateEvent.sources.source.eventPort.id == sourceEventId) &&
                    (updateEvent.targets.target.component.id == targetComponentId) &&
                    (updateEvent.targets.target.eventPort.id == targetEventId))
                {
                    return updateEvent;
                }
            }
            return null;
        }

        /// <summary>
        /// Returns a list of event channels with the given sourceComponentId and targetComponentId.
        /// </summary>
        /// <param name="sourceComponentId"></param>
        /// <param name="targetComponentId"></param>
        /// <returns></returns>
        List<eventChannel> findEventChannels(String sourceComponentId, String targetComponentId)
        {
            List<eventChannel> list=new List<eventChannel>();
            foreach (eventChannel updateEvent in eventChannelList)
            {
                if ((updateEvent.sources.source.component.id == sourceComponentId) &&
                    (updateEvent.targets.target.component.id == targetComponentId))
                {
                    list.Add(updateEvent);
                }
            }
            return list;
        }


        /// <summary>
        /// Listener for the componentComboBox in the property dock. This compobox is available, when the
        /// ACS is in 'run'-status
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        //private void ComponentsComboBox_SelectionChanged(object sender, EventArgs e) {
        //    ComboBoxItem cbi = (ComboBoxItem)componentsComboBox.SelectedItem;
        //    if (cbi != null) {
        //        SetPropertyDock(deploymentComponentList[cbi.Content.ToString()]);
        //        focusedComponent = deploymentComponentList[cbi.Content.ToString()];
        //    }
        //}

        /// <summary>
        /// Listener for the componentComboBox in the event dock. Will set events delete or add lines in the event dock
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void EventCombobox_SelectionChanged(object sender, SelectionChangedEventArgs e) {
            ComboBox cb = (ComboBox)sender;
            // original code, without tooltips
            // String selection = cb.SelectedItem.ToString();

            // new code, with tooltips
            String selection = ((ComboBoxItem)cb.SelectedItem).Content.ToString();
            cb.ToolTip = ((ComboBoxItem)cb.SelectedItem).ToolTip;

            int key = int.Parse(cb.Name.Replace("eventCombobox", ""));

            if (areStatus.Status.Equals(AREStatus.ConnectionStatus.Synchronised)) {
                areStatus.Status = AREStatus.ConnectionStatus.Connected;
            }
            modelHasBeenEdited = true;
            EvtChannelDescriptionTextBox correspondingEvtChannelDescpription = findEventChannelDescpriptionTextBox(cb);

            // Add a new line into the grid:
            if (!selection.Equals("---")) {
                bool duplicated = false;
                // check, if a triggerable event is once or several times in the list
                // only, if the successor is different to the selected event, a new line should be added
                foreach (UIElement element in dockEventGrid.Children) {
                    if ((Grid.GetRow(element) == Grid.GetRow(cb) + 1) && (element is ComboBox)) {
                        if (cb.Name == ((ComboBox)element).Name) {
                            duplicated = true;
                            break;
                        }
                    }
                }

                if (!duplicated) { 
                    TextBox l = new TextBox() {
                        Text = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId,
                        Margin = new Thickness(0, 0, 0, 0),
                        FontSize = 12,
                        FontFamily = new FontFamily("Segoe UI"),
                        IsReadOnly = true,
                        ToolTip = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventDescription
                    };
                    dockEventGrid.RowDefinitions.Insert(Grid.GetRow(cb) + 1, new RowDefinition() {
                        // Height = (GridLength)glc.ConvertFromString("22")
                    });
                    foreach (UIElement element in dockEventGrid.Children) {
                        if (Grid.GetRow(element) > Grid.GetRow(cb)) {
                            Grid.SetRow(element, Grid.GetRow(element) + 1);
                        }
                    }
                    ComboBox eventCombobox = new ComboBox();
                    eventCombobox.Name = "eventCombobox" + key;

                    eventCombobox.Items.Add(new ComboBoxItem() {
                        Content = "---"
                    });
                    TextBox evtChnlDescription = new EvtChannelDescriptionTextBox
                        {
                            eventListenerTextBox=l,
                            eventTriggerComboBox=eventCombobox,
                        };
                    evtChnlDescription.LostFocus += EvtChnlDescription_LostFocus;

                    foreach (EventTriggerPort eventTrigger in deploymentComponentList[focusedEventChannel.TriggerComponentId].EventTriggerList) {
                        // Uncomment, to enable tooltips for the combobox-elements.
                        ComboBoxItem cbi = new ComboBoxItem();
                        cbi.Content = eventTrigger.EventTriggerId;
                        cbi.ToolTip = eventTrigger.EventDescription;
                        eventCombobox.Items.Add(cbi);

                    }

                    eventCombobox.SelectedItem = eventCombobox.Items[0];

                    eventCombobox.SelectionChanged += EventCombobox_SelectionChanged;

                    Grid.SetRow(l, Grid.GetRow(cb) + 1);
                    Grid.SetColumn(l, 0);
                    Grid.SetRow(eventCombobox, Grid.GetRow(cb) + 1);
                    Grid.SetColumn(eventCombobox, 1);
                    Grid.SetRow(evtChnlDescription, Grid.GetRow(cb) + 1);
                    Grid.SetColumn(evtChnlDescription, 2);
                    GridSplitter mySimpleGridSplitter = new GridSplitter();
                    mySimpleGridSplitter.Background = Brushes.DarkGray;
                    mySimpleGridSplitter.HorizontalAlignment = HorizontalAlignment.Right;
                    mySimpleGridSplitter.VerticalAlignment = VerticalAlignment.Stretch;
                    mySimpleGridSplitter.Width = 1;
                    Grid.SetColumn(mySimpleGridSplitter, 0);
                    Grid.SetRow(mySimpleGridSplitter, Grid.GetRow(cb) + 1);
                    dockEventGrid.Children.Add(l);
                    dockEventGrid.Children.Add(eventCombobox);
                    dockEventGrid.Children.Add(evtChnlDescription);
                    dockEventGrid.Children.Add(mySimpleGridSplitter);

                    Border top = new Border();
                    Grid.SetColumn(top, 0);
                    Grid.SetRow(top, Grid.GetRow(cb) + 1);
                    Grid.SetColumnSpan(top, 2);
                    top.BorderBrush = Brushes.DarkGray;
                    top.BorderThickness = new Thickness(1);
                    dockEventGrid.Children.Add(top);
                }


                if (((ComboBoxItem)e.RemovedItems[e.RemovedItems.Count - 1]).Content.Equals("---")) {
                    // write data to deployment model
                    eventChannel addEventChannel = new eventChannel();

                    addEventChannel.sources.source.component.id = focusedEventChannel.TriggerComponentId;
                    addEventChannel.sources.source.eventPort.id = selection;
                    addEventChannel.targets.target.component.id = focusedEventChannel.ListenerComponentId;
                    addEventChannel.targets.target.eventPort.id = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId;
                    addEventChannel.id = addEventChannel.sources.source.eventPort.id + "_" + addEventChannel.targets.target.eventPort.id;

                    addEventChannel.description = constructEvtChannelDescription(addEventChannel.sources.source.eventPort.id, addEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                    if (EventChannelHasGroupSource(addEventChannel)) {
                        eventEdge ee = new eventEdge();
                        componentType source = GetComponentTypeFromEventString(selection);
                        ee.component.id = source.id;
                        ee.eventPort.id = selection.Substring(source.id.Length + 1);
                        addEventChannel.GroupOriginalSource = ee;

                        // new for bugfixing 29.01.2013
                        focusedEventChannel.HasGroupSource = true;
                    }
                    if (EventChannelHasGroupTarget(addEventChannel)) {
                        eventEdge ee = new eventEdge();
                        string targetEventString = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId;
                        componentType target = GetComponentTypeFromEventString(targetEventString);
                        ee.component.id = target.id;
                        ee.eventPort.id = targetEventString.Substring(target.id.Length + 1);
                        addEventChannel.GroupOriginalTarget = ee;

                        // new for bugfixing 29.01.2013
                        focusedEventChannel.HasGroupTarget = true;
                    }

                    eventChannelList.Add(addEventChannel);
                    deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));

                    // check if the eventchannel was added to a group
                    // if this is the case the eventchannel has to be added also to the original source and target

                    if (focusedEventChannel.HasGroupTarget && focusedEventChannel.HasGroupSource) {
                        EventListenerPort elp = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]);
                        componentType target = GetComponentTypeFromEventString(elp.EventListenerId);
                        componentType source = GetComponentTypeFromEventString(selection);

                        eventChannel baseEventChannel = new eventChannel();
                        baseEventChannel.sources.source.component.id = source.id;
                        baseEventChannel.sources.source.eventPort.id = selection.Substring(source.id.Length + 1);
                        baseEventChannel.targets.target.component.id = target.id;
                        baseEventChannel.targets.target.eventPort.id = elp.EventListenerId.Substring(target.id.Length + 1);
                        baseEventChannel.id = baseEventChannel.sources.source.eventPort.id + "_" + baseEventChannel.targets.target.eventPort.id;
                        baseEventChannel.description = constructEvtChannelDescription(baseEventChannel.sources.source.eventPort.id, baseEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                        eventChannelList.Add(baseEventChannel);


                        baseEventChannel = new eventChannel();
                        baseEventChannel.sources.source.component.id = focusedEventChannel.TriggerComponentId;
                        baseEventChannel.sources.source.eventPort.id = selection;
                        baseEventChannel.targets.target.component.id = target.id;
                        baseEventChannel.targets.target.eventPort.id = elp.EventListenerId.Substring(target.id.Length + 1);
                        baseEventChannel.id = baseEventChannel.sources.source.eventPort.id + "_" + baseEventChannel.targets.target.eventPort.id;
                        baseEventChannel.description = constructEvtChannelDescription(baseEventChannel.sources.source.eventPort.id, baseEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                        eventChannelList.Add(baseEventChannel);

                        baseEventChannel = new eventChannel();
                        baseEventChannel.sources.source.component.id = source.id;
                        baseEventChannel.sources.source.eventPort.id = selection.Substring(source.id.Length + 1);
                        baseEventChannel.targets.target.component.id = focusedEventChannel.ListenerComponentId;
                        baseEventChannel.targets.target.eventPort.id = elp.EventListenerId;
                        baseEventChannel.id = baseEventChannel.sources.source.eventPort.id + "_" + baseEventChannel.targets.target.eventPort.id;
                        baseEventChannel.description = constructEvtChannelDescription(baseEventChannel.sources.source.eventPort.id, baseEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                        eventChannelList.Add(baseEventChannel);

                        deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
                    }
                    else if (focusedEventChannel.HasGroupTarget && !focusedEventChannel.HasGroupSource) {
                        EventListenerPort elp = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]);
                        componentType target = GetComponentTypeFromEventString(elp.EventListenerId);


                        eventChannel baseEventChannel = new eventChannel();
                        baseEventChannel.sources.source.component.id = focusedEventChannel.TriggerComponentId;
                        baseEventChannel.sources.source.eventPort.id = selection;
                        baseEventChannel.targets.target.component.id = target.id;
                        baseEventChannel.targets.target.eventPort.id = elp.EventListenerId.Substring(target.id.Length + 1);
                        baseEventChannel.id = baseEventChannel.sources.source.eventPort.id + "_" + baseEventChannel.targets.target.eventPort.id;
                        baseEventChannel.description = constructEvtChannelDescription(baseEventChannel.sources.source.eventPort.id, baseEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                        eventChannelList.Add(baseEventChannel);
                        deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
                    }
                    else if (focusedEventChannel.HasGroupSource && !focusedEventChannel.HasGroupTarget) {
                        componentType source = GetComponentTypeFromEventString(selection);
                        EventListenerPort elp = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]);

                        eventChannel baseEventChannel = new eventChannel();

                        baseEventChannel.sources.source.component.id = source.id;
                        baseEventChannel.sources.source.eventPort.id = selection.Substring(source.id.Length + 1);
                        baseEventChannel.targets.target.component.id = focusedEventChannel.ListenerComponentId;
                        baseEventChannel.targets.target.eventPort.id = elp.EventListenerId;
                        baseEventChannel.id = baseEventChannel.sources.source.eventPort.id + "_" + baseEventChannel.targets.target.eventPort.id;
                        baseEventChannel.description = constructEvtChannelDescription(baseEventChannel.sources.source.eventPort.id, baseEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                        eventChannelList.Add(baseEventChannel);
                        deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
                    }
                }
                else {
                    // update data of an existing event
                    eventChannel updateEventChannel = new eventChannel();
                    updateEventChannel.sources.source.component.id = focusedEventChannel.TriggerComponentId;
                    
                    updateEventChannel.sources.source.eventPort.id = (string)((ComboBoxItem)e.RemovedItems[e.RemovedItems.Count - 1]).Content;

                    updateEventChannel.targets.target.component.id = focusedEventChannel.ListenerComponentId;
                    updateEventChannel.targets.target.eventPort.id = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId;
                    foreach (eventChannel updateEvent in eventChannelList) {
                        if ((updateEvent.sources.source.component.id == updateEventChannel.sources.source.component.id) &&
                            (updateEvent.sources.source.eventPort.id == updateEventChannel.sources.source.eventPort.id) &&
                            (updateEvent.targets.target.component.id == updateEventChannel.targets.target.component.id) &&
                            (updateEvent.targets.target.eventPort.id == updateEventChannel.targets.target.eventPort.id)) {
                            updateEvent.sources.source.eventPort.id = selection;
                            updateEventChannel.description = constructEvtChannelDescription(updateEventChannel.sources.source.eventPort.id, updateEventChannel.targets.target.eventPort.id, correspondingEvtChannelDescpription);

                            //deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
                            break;
                        }
                    }
                }

            }
            else {
                // remove a line
                ArrayList elements = new ArrayList();
                foreach (UIElement element in dockEventGrid.Children) {
                    if (element is TextBox && !(element is EvtChannelDescriptionTextBox)) {
                        if (!elements.Contains(((TextBox)element).Text)) {
                            elements.Add(((TextBox)element).Text);
                        }
                        else if ((((TextBox)element).Text == ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId) &&
                          (Grid.GetRow(element) == Grid.GetRow(cb) + 1)) {
                            // remove one row
                            
                            dockEventGrid.Children.Remove(element);
                            dockEventGrid.Children.Remove(cb);
                            correspondingEvtChannelDescpription.eventTriggerComboBox = null;
                            correspondingEvtChannelDescpription.eventListenerTextBox = null;
                            dockEventGrid.Children.Remove(correspondingEvtChannelDescpription);


                            // move the other elements one line up
                            foreach (UIElement element2 in dockEventGrid.Children) {
                                if (Grid.GetRow(element2) > Grid.GetRow(cb)) {
                                    Grid.SetRow(element2, Grid.GetRow(element2) - 1);
                                }
                            }

                            dockEventGrid.RowDefinitions.RemoveAt(dockEventGrid.RowDefinitions.Count - 1);
                            break;
                        }
                    }
                }
                // remove event from deployment model
                eventChannel deleteEventChannel = new eventChannel();
                deleteEventChannel.sources.source.component.id = focusedEventChannel.TriggerComponentId;

                deleteEventChannel.sources.source.eventPort.id = (string)((ComboBoxItem)e.RemovedItems[e.RemovedItems.Count - 1]).Content;
                deleteEventChannel.targets.target.component.id = focusedEventChannel.ListenerComponentId;
                deleteEventChannel.targets.target.eventPort.id = ((EventListenerPort)deploymentComponentList[focusedEventChannel.ListenerComponentId].EventListenerList[key]).EventListenerId;
                ArrayList evChannelsToDelete = new ArrayList();

                foreach (eventChannel delEvent in eventChannelList) {
                    string source = deleteEventChannel.sources.source.component.id;
                    string sourceEvent = deleteEventChannel.sources.source.eventPort.id;
                    string target = deleteEventChannel.targets.target.component.id;
                    string targetEvent = deleteEventChannel.targets.target.eventPort.id;

                    eventChannel targetChannel = null;

                    // delete the deselected eventChannel
                    if (delEvent.sources.source.component.id.Equals(source) &&
                        delEvent.sources.source.eventPort.id.Equals(sourceEvent) &&
                        delEvent.targets.target.component.id.Equals(target) &&
                        delEvent.targets.target.eventPort.id.Equals(targetEvent))
                        targetChannel = delEvent;
                    if (targetChannel == null)
                        continue;

                    evChannelsToDelete.Add(targetChannel);

                    string source1 = null;
                    string sourceEvent1 = null;
                    string target1 = null;
                    string targetEvent1 = null;

                    if (targetChannel.GroupOriginalSource != null) {
                        source1 = targetChannel.GroupOriginalSource.component.id;
                        sourceEvent1 = targetChannel.GroupOriginalSource.eventPort.id;
                    }
                    if (targetChannel.GroupOriginalTarget != null) {
                        target1 = targetChannel.GroupOriginalTarget.component.id;
                        targetEvent1 = targetChannel.GroupOriginalTarget.eventPort.id;
                    }

                    if (source1 != null && sourceEvent1 != null) {
                        // Delete eventchannels with sourc1 && source1event && target && targetEvent
                        foreach (eventChannel delEvent1 in eventChannelList) {
                            if (delEvent1.sources.source.component.id.Equals(source1) &&
                            delEvent1.sources.source.eventPort.id.Equals(sourceEvent1) &&
                            delEvent1.targets.target.component.id.Equals(target) &&
                            delEvent1.targets.target.eventPort.id.Equals(targetEvent))
                                evChannelsToDelete.Add(delEvent1);
                        }
                    }
                    if (target1 != null && targetEvent1 != null) {
                        // Delete eventchannels with target1 && targetEvent1 && source && sourceEvent
                        foreach (eventChannel delEvent1 in eventChannelList) {
                            if (delEvent1.sources.source.component.id.Equals(source) &&
                            delEvent1.sources.source.eventPort.id.Equals(sourceEvent) &&
                            delEvent1.targets.target.component.id.Equals(target1) &&
                            delEvent1.targets.target.eventPort.id.Equals(targetEvent1))
                                evChannelsToDelete.Add(delEvent1);
                        }
                    }
                    if (source1 != null && sourceEvent1 != null && target1 != null && targetEvent1 != null) {
                        // delete eventchannels with source1 && sourceEvent1 && target1 && targetEvent1
                        foreach (eventChannel delEvent1 in eventChannelList) {
                            if (delEvent1.sources.source.component.id.Equals(source1) &&
                            delEvent1.sources.source.eventPort.id.Equals(sourceEvent1) &&
                            delEvent1.targets.target.component.id.Equals(target1) &&
                            delEvent1.targets.target.eventPort.id.Equals(targetEvent1))
                                evChannelsToDelete.Add(delEvent1);
                        }
                    }
                }
                foreach (eventChannel ec in evChannelsToDelete)
                    eventChannelList.Remove(ec);
                deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
            }
            UpdateToolTips();
        }

        String constructEvtChannelDescription(String sourceId, String targetId, EvtChannelDescriptionTextBox evtChnlDescriptionTxtBox)
        {
            //String desc = sourceId + "->" + targetId;
            String desc = "";
            if (evtChnlDescriptionTxtBox.Text != null)
            {
                desc = evtChnlDescriptionTxtBox.Text;
            }
            return desc;
        }
        EvtChannelDescriptionTextBox findEventChannelDescpriptionTextBox(UIElement eventTriggercomboBox)
        {
            foreach (UIElement element in dockEventGrid.Children)
            {
                if (element is EvtChannelDescriptionTextBox && ((EvtChannelDescriptionTextBox)element).eventTriggerComboBox == eventTriggercomboBox)
                {
                    return ((EvtChannelDescriptionTextBox)element);
                }
            }

            return null;
        }

        /// <summary>
        /// Resize the size of the Event Listener and Trigger list in the property dock
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void dockableEventListenerTriggerTab_SizeChanged(object sender, SizeChangedEventArgs e) {
            if (((ScrollViewer)sender).Content != null && dockableComponentProperties.ContainerPane != null) {
                if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                    ((Grid)((ScrollViewer)sender).Content).Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
                }
            }
        }


        /// <summary>
        /// Resetting the size of the stack panel, adapting it to the size of the scollViewer
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void StackPanel_SizeChanged(object sender, SizeChangedEventArgs e) {
            if ((propertyDockScrollViewer.Content != null) && (propertyDockScrollViewer.Content is WPG.PropertyGrid)) {
                if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                    ((WPG.PropertyGrid)propertyDockScrollViewer.Content).Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
                }
                if (dockableComponentProperties.ContainerPane.ActualHeight > 48) {
                    ((WPG.PropertyGrid)propertyDockScrollViewer.Content).Height = dockableComponentProperties.ContainerPane.ActualHeight - 48;
                }
            }
        }


        #endregion // property Changed listener

        #region Commands

        /// <summary>
        /// Check if current model needs saving and then open model
        /// </summary>
        private void CheckIfSavedAndOpenCommand(String inputFile) {
            if (modelHasBeenEdited) {
                SaveQuestionDialog saveQuestion = new SaveQuestionDialog();
                saveQuestion.Owner = this;
                saveQuestion.ShowDialog();

                // Process message box results
                switch (saveQuestion.Result) {
                    case SaveQuestionDialog.save:
                        if (SaveLocalCommand(false) == true) {
                            OpenLocalCommand(inputFile);
                            modelHasBeenEdited = false;
                        }
                        break;
                    case SaveQuestionDialog.dontSave:
                        OpenLocalCommand(inputFile);
                        modelHasBeenEdited = false;
                        break;
                    case SaveQuestionDialog.cancel:
                        break;
                }
            }
            else {
                OpenLocalCommand(inputFile);
                modelHasBeenEdited = false;
            }
        }

        /// <summary>
        /// Load a deployment model to the drawing board
        /// </summary>
        private void OpenLocalCommand(String inputFile) {
            if (inputFile == null) {
                System.Windows.Forms.OpenFileDialog openLocalXML = new System.Windows.Forms.OpenFileDialog();

                //openLocalXML.InitialDirectory = "c:\\temp\\" ;
                openLocalXML.Filter = "AsTeRICS-Files (*.acs)|*.acs|All files (*.*)|*.*";
                openLocalXML.FilterIndex = 1;
                openLocalXML.RestoreDirectory = true;
                if (openLocalXML.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                    inputFile = openLocalXML.FileName;
                }
            }

            if (inputFile != null) {
                // check if the file is valid against the deployment_schema
                String xmlError;
                XmlValidation xv = new XmlValidation();
                // old, working code: xmlError = xv.validateXml(inputFile, ini.IniReadValue("model", "deployment_schema"));

                if (!File.Exists(ini.IniReadValue("model", "deployment_schema"))) {
                    xmlError = xv.validateXml(inputFile, AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "deployment_schema"));
                } else {
                    xmlError = xv.validateXml(inputFile, ini.IniReadValue("model", "deployment_schema"));
                }

                if (xmlError.Equals("")) {
                    XmlSerializer ser2 = new XmlSerializer(typeof(model));
                    StreamReader sr2 = new StreamReader(inputFile);
                    deploymentModel = (model)ser2.Deserialize(sr2);
                    sr2.Close();

                    ResetPropertyDock();
                    ModelVersionUpdater.UpdateMissingGUI(this, deploymentModel, componentList);
                    ModelVersionUpdater.UpdateToCurrentVersion(this, deploymentModel);
                    LoadComponentsCommand();

                    // set the saveFile in order to still know the filename when trying to save the schema again
                    SetSaveFile(inputFile);

                    if (areStatus.Status == AREStatus.ConnectionStatus.Synchronised) {
                        areStatus.Status = AREStatus.ConnectionStatus.Connected;
                    }
                    else if ((areStatus.Status == AREStatus.ConnectionStatus.Running) || (areStatus.Status == AREStatus.ConnectionStatus.Pause)) {
                        areStatus.Status = AREStatus.ConnectionStatus.Synchronised;
                        areStatus.Status = AREStatus.ConnectionStatus.Connected;
                    }
                }
                else {
                    MessageBox.Show(Properties.Resources.ReadXmlErrorText, Properties.Resources.ReadXmlErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
                    SetSaveFile(null);
                    MessageBoxResult result = MessageBox.Show(Properties.Resources.UpdateModelVersionQuestion, Properties.Resources.UpdateModelVersionHeader, MessageBoxButton.YesNo, MessageBoxImage.Question);
                    if (result.Equals(MessageBoxResult.Yes)) {
                        ModelVersionUpdater.ParseErrorUpdate(xmlError, inputFile);
                    }
                }
                AddToRecentList(inputFile);
            }
        }


        /// <summary>
        /// Copy alle Components of the copyModel to the actual model
        /// </summary>
        private void PasteCopiedModel(model modelToPaste, bool namesAreValid, bool addToUndoStack) {
            CommandObject co = new CommandObject("Delete");
            if (modelToPaste == null)
                return;
            // loading the components
            /*
             *  Create a list of lists which contain all elements which should be within a group 
             *  after the paste method finishes
             */
            ArrayList groupComps = new ArrayList();
            ArrayList groups = new ArrayList();
            ArrayList groupNames = new ArrayList();
            foreach (componentType ct in modelToPaste.components) {
                if (ct.ComponentType != ACS2.componentTypeDataTypes.group)
                    continue;
                groups.Add(ct);
                groupComponent gc = groupsList[ct.id];
                ArrayList groupElems = new ArrayList();
                foreach (componentType child in gc.AddedComponentList) {
                    foreach (componentType child1 in modelToPaste.components) {
                        if (child1.id.Equals(child.id)) {
                            groupElems.Add(child1);
                        }
                    }
                }
                if (groupElems.Count > 0) {
                    groupComps.Add(groupElems);
                    bool namevalid = namesAreValid;
                    int i = 1;
                    while (namevalid == false) {
                        string modelID = ct.id + "." + i;
                        if (deploymentComponentList.ContainsKey(modelID) == false) {
                            namevalid = true;
                            groupNames.Add(modelID);
                        }
                        else
                            i++;
                    }
                }
            }
            
            // Rename components and update all channels
            Dictionary<string, string> changedComponents = new Dictionary<string, string>();
            foreach (object o in modelToPaste.components) {
                componentType modelComp = (componentType)o;
                if (modelComp.ComponentType == ACS2.componentTypeDataTypes.group)
                    continue;
                bool componentFound = false;
                foreach (componentType c in deploymentModel.components)
                {
                    if (c == null)
                    {
                        continue;
                    }
                    if (c.type_id == modelComp.type_id)
                    {
                        componentFound = true;
                        break;
                    }
                }
                if (componentFound && ((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id]).singleton) {
                    MessageBox.Show(Properties.Resources.SingletonErrorHeaderFormat(modelComp.type_id), Properties.Resources.SingletonErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Warning);
                    List<channel> channelsToDelete = new List<channel>();
                    if (modelToPaste.channels != null)
                    {
                        foreach (channel c in modelToPaste.channels)
                        {
                            if (c.source.component.id == modelComp.id)
                            {
                                channelsToDelete.Add(c);
                            }
                            else if (c.target.component.id == modelComp.id)
                            {
                                channelsToDelete.Add(c);
                            }
                        }
                    }
                    // Delete all channels connected to the singleton
                    modelToPaste.channels = modelToPaste.channels.Where(val => channelsToDelete.Contains(val) == false).ToArray();
                    List<eventChannel> evChannelsToDelete = new List<eventChannel>();
                    if (modelToPaste.eventChannels != null)
                    {
                        foreach (eventChannel ec in modelToPaste.eventChannels)
                        {
                            if (ec.sources.source.component.id == modelComp.id)
                            {
                                evChannelsToDelete.Add(ec);
                            }
                            else if (ec.targets.target.component.id == modelComp.id)
                            {
                                evChannelsToDelete.Add(ec);
                            }
                        }
                    }
                    // Delete all eventchannels connected to the singleton
                    modelToPaste.eventChannels = modelToPaste.eventChannels.Where(val => evChannelsToDelete.Contains(val) == false).ToArray();
                    continue;
                }
                // change id
                bool namevalid = namesAreValid;
                int i = 1;
                if (deploymentComponentList.ContainsKey(modelComp.id))
                {
                    while (namevalid == false) 
                    {
                        string modelID = modelComp.id + "." + i;
                        bool inPasteModel = false;
                        foreach (componentType ct in modelToPaste.components)
                        {
                            if (ct.id == modelID)
                            {
                                inPasteModel = true;
                                break;
                            }
                        }
                        if (deploymentComponentList.ContainsKey(modelID) == false && inPasteModel == false)
                        {
                            if (modelToPaste.channels != null) {
                                foreach (channel c in modelToPaste.channels) {
                                    if (c.source.component.id == modelComp.id)
                                        c.source.component.id = modelID;
                                    if (c.target.component.id == modelComp.id)
                                        c.target.component.id = modelID;
                                }
                            }
                            if (modelToPaste.eventChannels != null) {
                                foreach (eventChannel ec in modelToPaste.eventChannels) {
                                    if (ec.sources.source.component.id == modelComp.id)
                                        ec.sources.source.component.id = modelID;
                                    if (ec.targets.target.component.id == modelComp.id)
                                        ec.targets.target.component.id = modelID;
                                }
                            }
                            changedComponents.Add(modelComp.id,modelID);
                            modelComp.id = modelID;
                            namevalid = true;
                        }
                        else
                            i++;
                    }
                }
                // check, if bundle is available
                if (componentList.ContainsKey(modelComp.type_id)) {

                    // init the ArrayLists containing the ports. Used for easier and faster access
                    modelComp.InitArrayLists();

                    foreach (propertyType p in modelComp.properties) {
                        modelComp.PropertyArrayList.Add(p);
                    }

                    // copy the property datatype and description from bundle_description
                    // also copy the port datatypes form the bundle_description
                    // works only, if bundle and deployment fits to each other

                    try {
                        // search for bundle component
                        Asterics.ACS2.componentTypesComponentType bundleComponent = (Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id];

                        // copy the ComponentType of the component
                        modelComp.ComponentType = bundleComponent.type.Value;

                        if (modelComp.properties != null) {
                            foreach (propertyType deploymentProperty in modelComp.properties) {
                                int index = 0;
                                // searching for the right component property
                                while (deploymentProperty.name != bundleComponent.properties[index].name) {
                                    index++;
                                }
                                // copy the properties of the component
                                deploymentProperty.DataType = bundleComponent.properties[index].type;

                                // check, if the property is a dynamic property
                                if (bundleComponent.properties[index].getStringList) {
                                    deploymentProperty.GetStringList = true;
                                } else {
                                    deploymentProperty.GetStringList = false;
                                }

                                // check the value fitting to the datatype
                                if (!CheckPropertyDatatype(deploymentProperty.value, deploymentProperty.DataType)) {
                                    throw new LoadPropertiesException();
                                }

                                deploymentProperty.Description = bundleComponent.properties[index].description;
                                if (bundleComponent.properties[index].combobox != null) {
                                    deploymentProperty.ComboBoxStrings = bundleComponent.properties[index].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                }
                                deploymentProperty.PropertyChanged += ComponentPropertyChanged;
                                deploymentProperty.PropertyChangeError += ComponentPropertyChangeError;
                            }
                            // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                            if ((bundleComponent.properties == null) && (modelComp.properties.Length != 0)) {
                                throw new LoadPropertiesException();
                            }
                            else if ((bundleComponent.properties != null) && (modelComp.properties.Length != bundleComponent.properties.Length)) {
                                throw new LoadPropertiesException();
                            }
                        }

                        foreach (object portObj in modelComp.PortsList.Values) {
                            // searching for the inPorts
                            if (portObj is inputPortType) {
                                int index = 0;
                                inputPortType deploymentInPort = (inputPortType)portObj;
                                ArrayList helperListInPort = new ArrayList(); // a list with all InPorts of a component for the bundel_description
                                foreach (object bundleInPort in bundleComponent.ports) {
                                    if (bundleInPort is ACS2.inputPortType) {
                                        helperListInPort.Add(bundleInPort);
                                    }
                                }
                                while (deploymentInPort.portTypeID != ((ACS2.inputPortType)helperListInPort[index]).id) {
                                    index++; // searching for the right input port
                                }
                                // copy the dataType of the port
                                deploymentInPort.PortDataType = ((Asterics.ACS2.inputPortType)helperListInPort[index]).dataType;
                                deploymentInPort.MustBeConnected = ((Asterics.ACS2.inputPortType)helperListInPort[index]).mustBeConnected;
                                deploymentInPort.Description = ((Asterics.ACS2.inputPortType)helperListInPort[index]).description;
                                deploymentInPort.ComponentId = ((Asterics.ACS2.inputPortType)helperListInPort[index]).ComponentId;
                                deploymentInPort.ComponentTypeId = ((Asterics.ACS2.inputPortType)helperListInPort[index]).id;
                                
                                // update the alias for group ports via property changed listener
                                deploymentInPort.PropertyChanged += InputPortIntPropertyChanged;

                                ACS2.propertyType[] sourceProperties = ((Asterics.ACS2.inputPortType)helperListInPort[index]).properties;
                                if ((sourceProperties != null) && (sourceProperties.Length > 0)) {
                                    //if (deploymentInPort.PropertyArrayList.Count > 0) {
                                    foreach (propertyType deploymentProperty in deploymentInPort.properties) {
                                        int inPortIndex = 0;
                                        while (deploymentProperty.name != sourceProperties[inPortIndex].name) {
                                            inPortIndex++;
                                        }
                                        // copy the properties of the inPort
                                        deploymentProperty.DataType = sourceProperties[inPortIndex].type;
                                        deploymentProperty.Description = sourceProperties[inPortIndex].description;
                                        if (sourceProperties[inPortIndex].combobox != null) {
                                            deploymentProperty.ComboBoxStrings = sourceProperties[inPortIndex].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                        }
                                        deploymentProperty.PropertyChanged += InPortPropertyChanged;
                                        deploymentProperty.PropertyChangeError += ComponentPropertyChangeError;
                                    }
                                    // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                                    if (deploymentInPort.properties.Length != sourceProperties.Length) {
                                        throw new Exception();
                                    }
                                }
                                deploymentInPort.properties = (propertyType[])deploymentInPort.PropertyArrayList.ToArray(typeof(propertyType));
                            }
                            else {
                                // comparing all outPports
                                int index = 0;
                                outputPortType outPort = (outputPortType)portObj;
                                ArrayList helperListOutPort = new ArrayList();
                                foreach (object origOutPort in bundleComponent.ports) {
                                    if (origOutPort is ACS2.outputPortType) {
                                        helperListOutPort.Add(origOutPort);
                                    }
                                }
                                while (outPort.portTypeID != ((Asterics.ACS2.outputPortType)helperListOutPort[index]).id) {
                                    index++;
                                }
                                // copy the dataType of the port
                                outPort.PortDataType = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).dataType;
                                outPort.Description = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).description;
                                outPort.ComponentId = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).ComponentId;
                                outPort.ComponentTypeId = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).id;

                                // update the alias for group ports via property changed listener
                                outPort.PropertyChanged += OutputPortIntPropertyChanged;

                                ACS2.propertyType[] sourceProperties = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).properties;
                                if ((sourceProperties != null) && (sourceProperties.Length > 0)) {
                                    //if (outPort.PropertyArrayList.Count > 0) {
                                    foreach (propertyType compProperty in outPort.properties) {
                                        int outPortIndex = 0;
                                        while (compProperty.name != sourceProperties[outPortIndex].name) {
                                            outPortIndex++;
                                        }
                                        // copy the properties of the outPort
                                        compProperty.DataType = sourceProperties[outPortIndex].type;
                                        compProperty.Description = sourceProperties[outPortIndex].description;
                                        if (sourceProperties[outPortIndex].combobox != null) {
                                            compProperty.ComboBoxStrings = sourceProperties[outPortIndex].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                        }
                                        compProperty.PropertyChanged += OutPortPropertyChanged;
                                        compProperty.PropertyChangeError += ComponentPropertyChangeError;
                                    }
                                    // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                                    if (outPort.properties.Length != sourceProperties.Length) {
                                        throw new Exception();
                                    }
                                }
                                outPort.properties = (propertyType[])outPort.PropertyArrayList.ToArray(typeof(propertyType));
                            }
                        }
                    }
                    catch (Exception ex) {
                        MessageBox.Show(Properties.Resources.CopyPropertiesErrorTextFormat(modelComp.id), Properties.Resources.CopyPropertiesErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);

                        //versionconflict
                        int posX;
                        int posY;
                        // set coordinates for the component in case there are not already set
                        if ((modelComp.layout.posX == null) || (modelComp.layout.posX == "") || (modelComp.layout.posY == null) || (modelComp.layout.posY == "")) {
                            posX = 40;
                            posY = 40;
                        }
                        else {
                            posX = Int32.Parse(modelComp.layout.posX);
                            posY = Int32.Parse(modelComp.layout.posY);
                        }

                        // backup component to load properties
                        componentType backupComp = modelComp;

                        modelComp = componentType.CopyFromBundleModel((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id], modelComp.id);
                        modelComp.layout.posX = Convert.ToString(posX);
                        modelComp.layout.posY = Convert.ToString(posY);
                        // HasVersionConflict indicates a version conflict between the component in a stored model and
                        // the component in the bundle descriptor
                        modelComp.HasVersionConflict = true;
                        modelComp.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                        //break;

                        // new code, copy property values from invalid (version conflict) component
                        foreach (propertyType deploymentProperty in modelComp.properties) {
                            foreach (propertyType backupProperty in backupComp.properties) {
                                if (deploymentProperty.name == backupProperty.name) {
                                    if (CheckPropertyDatatype(backupProperty.value, deploymentProperty.DataType)) {
                                        deploymentProperty.value = backupProperty.value; // try-parse is missing
                                    }
                                    break;
                                }
                            }
                        }

                    } // end of exception

                    // set coordinates for the component in case there are not already set
                    if ((modelComp.layout.posX == null) || (modelComp.layout.posX == "") || (modelComp.layout.posY == null) || (modelComp.layout.posY == "")) {
                        int[] pos = ProperComponentCoordinates(40, 40);
                        modelComp.layout.posX = Convert.ToString(pos[0]);
                        modelComp.layout.posY = Convert.ToString(pos[1]);
                    }

                    // Searching for the event triggers and event listeners of a component
                    Asterics.ACS2.componentTypesComponentType bundleComponentEvents = (Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id];
                    // If component has version conflict, the events are already set by 'CopyFromBundleModel'
                    if ((bundleComponentEvents.events != null) && (bundleComponentEvents.events != null) && !modelComp.HasVersionConflict) {
                        foreach (object eventO in bundleComponentEvents.events) {
                            if (eventO is ACS2.eventsTypeEventListenerPortType) {
                                ACS2.eventsTypeEventListenerPortType compEl = (ACS2.eventsTypeEventListenerPortType)eventO;
                                EventListenerPort el = new EventListenerPort();
                                el.EventListenerId = compEl.id;
                                el.ComponentId = modelComp.id;
                                el.EventDescription = compEl.description;
                                modelComp.EventListenerList.Add(el);
                            }
                            else if (eventO is ACS2.eventsTypeEventTriggererPortType) {
                                ACS2.eventsTypeEventTriggererPortType compEl = (ACS2.eventsTypeEventTriggererPortType)eventO;
                                EventTriggerPort el = new EventTriggerPort();
                                el.EventTriggerId = compEl.id;
                                el.ComponentId = modelComp.id;
                                el.EventDescription = compEl.description;
                                modelComp.EventTriggerList.Add(el);
                            }
                        }
                    }


                    // if the component has no version conflict, it will be pasted on the layout, otherwise, it is already on the 
                    // canvas (done by 'CopyFromBundleModel')
                    if (!modelComp.HasVersionConflict) {
                        modelComp.InitGraphLayout(modelComp.id);
                    }
                    else {
                        //deploymentModel.components = deploymentComponentList.Values.ToArray();
                    }
                    canvas.Children.Add(modelComp.ComponentCanvas);
                    KeyboardNavigation.SetTabIndex(modelComp.ComponentCanvas, canvas.Children.Count + 1);

                    modelComp.Label.Text = modelComp.id;
                    double positionX = (Int32.Parse(modelComp.layout.posX) + copyXOffset * copyOffsetMulti);
                    if (positionX + modelComp.ComponentCanvas.Width > canvas.RenderSize.Width)
                        positionX = canvas.RenderSize.Width - modelComp.ComponentCanvas.Width;
                    double positionY = (Int32.Parse(modelComp.layout.posY) + copyYOffset * copyOffsetMulti);
                    if (positionY + modelComp.ComponentCanvas.Height > canvas.RenderSize.Height)
                        positionY = canvas.RenderSize.Height - modelComp.ComponentCanvas.Height;
                    modelComp.layout.posX = positionX.ToString();
                    modelComp.layout.posY = positionY.ToString();
                    deploymentComponentList.Add(modelComp.id, modelComp);
                    //componentList.Add(modelComp.id, modelComp);
                    // adding context menu
                    modelComp.MainRectangle.ContextMenu = componentContextMenu;
                    // adding keyboard focus listener
                    modelComp.ComponentCanvas.KeyDown += Component_KeyDown;
                    modelComp.ComponentCanvas.KeyUp += Component_KeyUp;
                    modelComp.ComponentCanvas.Focusable = true;
                    modelComp.ComponentCanvas.GotKeyboardFocus += ComponentCanvas_GotKeyboardFocus;
                    modelComp.ComponentCanvas.LostKeyboardFocus += ComponentCanvas_LostKeyboardFocus;
                    
                    // adding property changed listener
                    modelComp.PropertyChanged += ComponentIntPropertyChanged;
                    modelComp.TopGrid.ContextMenu = componentContextMenu;
                    modelComp.TopRectangle.ContextMenu = componentContextMenu;
                    // set position of component on the canvas
                    Canvas.SetLeft(modelComp.ComponentCanvas, Int32.Parse(modelComp.layout.posX));
                    Canvas.SetTop(modelComp.ComponentCanvas, Int32.Parse(modelComp.layout.posY));

                    // Adapt the size of MainRectangle, if more then MAINRECTANGLENUMBEROFPORTS are in a component
                    int numInPorts = 0;
                    int numOutPorts = 0;
                    foreach (object objPort in modelComp.PortsList.Values) {
                        if (objPort is inputPortType) {
                            numInPorts++;
                        }
                        else {
                            numOutPorts++;
                        }
                    }
                    if (numOutPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                        modelComp.MainRectangle.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE);
                        modelComp.ComponentCanvas.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE);
                    }
                    else if (numInPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                        modelComp.MainRectangle.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
                        modelComp.ComponentCanvas.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
                    }
                    // Adapt the position of event trigger and event listener port, if the component has more input/output ports
                    if (modelComp.EventListenerList.Count > 0) {
                        Canvas.SetTop(modelComp.EventListenerPolygon.InputEventPortCanvas, modelComp.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
                    }
                    if (modelComp.EventTriggerList.Count > 0) {
                        Canvas.SetTop(modelComp.EventTriggerPolygon.OutputEventPortCanvas, modelComp.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
                    }

                }
                else {
                    MessageBox.Show(Properties.Resources.LoadComponentNotFoundFormat(modelComp.type_id), Properties.Resources.CopyPropertiesErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                }

                // check, if component has a gui component, and load the gui component
                if (modelComp.gui != null) {
                    AddGUIComponent(modelComp);
                }
            }
            deploymentModel.components = deploymentComponentList.Values.ToArray();


            // loading the channels
            if (modelToPaste.channels != null) {
                foreach (object o in modelToPaste.channels) {
                    channel modChannel = (channel)o;
                    // check versionconflict: add only channels to components without a conflict
                    if ((deploymentComponentList.ContainsKey(modChannel.source.component.id)) && (deploymentComponentList.ContainsKey(modChannel.target.component.id))) {

                        // one of the channels component has a conflict. Check, if port is available and datatype fits together
                        componentType tempCompOut = (componentType)deploymentComponentList[modChannel.source.component.id];
                        componentType tempCompIn = (componentType)deploymentComponentList[modChannel.target.component.id];
                        // try, if the ports are still available
                        
                        if ((tempCompOut.PortsList.Contains(modChannel.source.port.id)) && (tempCompIn.PortsList.Contains(modChannel.target.port.id))) {
                            // check the datatypes of the ports, for the case, that they have been changed
                            //if (CheckInteroperabilityOfPorts(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortDataType, ((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortDataType)) {                                
                                modChannel.id = NewIdForChannel();
                                AddChannel(modChannel);
                                modChannel.Line.Y1 = Canvas.GetTop(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortRectangle) + Canvas.GetTop(tempCompOut.ComponentCanvas) + 5;
                                modChannel.Line.X1 = Canvas.GetLeft(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortRectangle) + Canvas.GetLeft(tempCompOut.ComponentCanvas) + 20;

                                modChannel.Line.Y2 = Canvas.GetTop(((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortRectangle) + Canvas.GetTop(tempCompIn.ComponentCanvas) + 5;
                                modChannel.Line.X2 = Canvas.GetLeft(((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortRectangle) + Canvas.GetLeft(tempCompIn.ComponentCanvas);
                                Canvas.SetZIndex(modChannel.Line, Canvas.GetZIndex(modChannel.Line) + 1000);
                            //}
                            //else {
                            //    // if no event listener Port can be found, the component has a version conflict
                            //    MessageBox.Show(Properties.Resources.CopyChannelsErrorTextFormat(tempCompOut.id, tempCompIn.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            //    tempCompIn.ComponentCanvas.Background = new SolidColorBrush(Colors.Red);
                            //    tempCompIn.HasVersionConflict = true;
                            //    tempCompOut.ComponentCanvas.Background = new SolidColorBrush(Colors.Red);
                            //    tempCompOut.HasVersionConflict = true;
                            //}
                        }
                        else {
                            if (!tempCompOut.PortsList.Contains(modChannel.source.port.id)) {
                                MessageBox.Show(Properties.Resources.CopyChannelErrorNotFoundFormat(tempCompOut.id, tempCompIn.id, modChannel.source.port.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                tempCompOut.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompOut.HasVersionConflict = true;
                            }
                            else {
                                MessageBox.Show(Properties.Resources.CopyChannelErrorNotFoundFormat(tempCompOut.id, tempCompIn.id, modChannel.source.port.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                tempCompIn.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompIn.HasVersionConflict = true;
                            }
                        }

                    }
                }
            }
            // Loading the events and drawing the lines between the event ports
            if (modelToPaste.eventChannels != null) {
                bool foundLine = false;
                foreach (object o in modelToPaste.eventChannels) {
                    eventChannel evChannel = (eventChannel)o;
                    bool foundTriggerPort = false;
                    bool foundListenerPort = false;
                    try {
                        foreach (EventTriggerPort checkEvent in ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).EventTriggerList) {
                            if (checkEvent.ComponentId == evChannel.sources.source.component.id && checkEvent.EventTriggerId == evChannel.sources.source.eventPort.id) {
                                foundTriggerPort = true;
                                break;
                            }
                        }
                        if (foundTriggerPort) {
                            foreach (EventListenerPort checkEvent in ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).EventListenerList) {
                                if (checkEvent.ComponentId == evChannel.targets.target.component.id && checkEvent.EventListenerId == evChannel.targets.target.eventPort.id) {
                                    foundListenerPort = true;
                                    break;
                                }
                            }
                            if (foundListenerPort) {
                                foreach (eventChannelLine channelLine in eventChannelLinesList) {
                                    if ((evChannel.sources.source.component.id == channelLine.TriggerComponentId) && (evChannel.targets.target.component.id == channelLine.ListenerComponentId)) {
                                        foundLine = true;
                                        break;
                                    }
                                }
                                if (!foundLine) {
                                    eventChannelLine eCL = new eventChannelLine();

                                    eCL.Line.X1 = Canvas.GetLeft(((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) + LayoutConstants.EVENTOUTPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                                    //eCL.Line.Y1 = Canvas.GetTop(((modelComponent)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) + LayoutConstants.EVENTOUTPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                                    eCL.Line.Y1 = Canvas.GetTop(((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) +
                                        ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).MainRectangle.Height + LayoutConstants.EVENTPORTHEIGHT + LayoutConstants.MAINRECTANGLEOFFSETY - 7;
                                    eCL.Line.X2 = Canvas.GetLeft(((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                                    //eCL.Line.Y2 = Canvas.GetTop(((modelComponent)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                                    eCL.Line.Y2 = Canvas.GetTop(((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) +
                                        ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).MainRectangle.Height + LayoutConstants.EVENTPORTHEIGHT + LayoutConstants.MAINRECTANGLEOFFSETY - 7;

                                    eCL.Line.Focusable = true;
                                    eCL.ListenerComponentId = evChannel.targets.target.component.id;
                                    eCL.TriggerComponentId = evChannel.sources.source.component.id;
                                    eCL.Line.GotKeyboardFocus += EventChannel_GotKeyboardFocus;
                                    eCL.Line.LostKeyboardFocus += EventChannel_LostKeyboardFocus;
                                    eCL.Line.KeyDown += EventChannel_KeyDown;
                                    eCL.Line.ContextMenu = eventChannelContextMenu;
                                    eventChannelLinesList.Add(eCL);
                                    canvas.Children.Add(eCL.Line);
                                    KeyboardNavigation.SetTabIndex(eCL.Line, canvas.Children.Count + 1);
                                    Canvas.SetZIndex(eCL.Line, Canvas.GetZIndex(eCL.Line) + 2000);
                                }
                                eventChannelList.Add(o);
                                foundLine = false;
                            }
                            else {
                                // if no event listener Port can be found, the component has a version conflict
                                MessageBox.Show(Properties.Resources.CopyEventsErrorTextFormat(evChannel.targets.target.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).HasVersionConflict = true;
                            }
                        }
                        else {
                            // if no event trigger Port can be found, the component has a version conflict
                            MessageBox.Show(Properties.Resources.CopyEventsErrorTextFormat(evChannel.sources.source.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                            ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).HasVersionConflict = true;
                        }
                    }
                    catch (Exception) {
                        MessageBox.Show(Properties.Resources.CopyEventsExceptionTextFormat(evChannel.sources.source.component.id, evChannel.sources.source.eventPort.id,
                            evChannel.targets.target.component.id, evChannel.targets.target.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    }
                }
                deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
            }

            // focus the first element
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }
            copyOffsetMulti++;
            ClearSelectedChannelList();
            if (modelToPaste.channels != null) {
                foreach (channel c in modelToPaste.channels) {
                    selectedChannelList.AddLast(c);
                    co.InvolvedObjects.Add(c);
                }
            }
            UpdateSelectedChannels();
            ClearSelectedEventChannelList();
            LinkedList<eventChannelLine> selEventChannels = new LinkedList<eventChannelLine>();
            foreach (eventChannelLine ecl in eventChannelLinesList) {
                if (modelToPaste.eventChannels != null) {
                    foreach (eventChannel ech in modelToPaste.eventChannels) {
                        if (ecl.ListenerComponentId == ech.targets.target.component.id ||
                            ecl.TriggerComponentId == ech.sources.source.component.id) {
                            selEventChannels.AddLast(ecl);
                            break;
                        }
                    }
                }
            }
            foreach (eventChannelLine ecl in selEventChannels) {
                selectedEventChannelList.AddLast(ecl);
                co.InvolvedObjects.Add(ecl);
            }
            UpdateSelectedEventChannels();
            // select all inserted components
            ClearSelectedComponentList();
            foreach (componentType mc in modelToPaste.components) {
                selectedComponentList.AddLast(mc);
                co.InvolvedObjects.Add(mc);
            }
            UpdateSelectedComponents();
            if (addToUndoStack) {
                undoStack.Push(co);
                redoStack.Clear();
            }
            SetKeyboardFocus();
            int groupIndex = 0;
            AddDummyToModel(copyDummyName);
            foreach (ArrayList group in groupComps) {
                ClearSelectedChannelList();
                ClearSelectedComponentList();
                ClearSelectedEventChannelList();
                foreach (componentType ct in group) {
                    AddSelectedComponent(ct);
                }
                componentType groupComponent = (componentType) groups[groupIndex];
                // add dummy channels to retain all ports of the copied group element
                foreach (object o in groupComponent.ports) {
                    if (o is inputPortType) {
                        inputPortType inPort = (inputPortType) o;
                        
                        string targetComponent = inPort.refs.componentID;
                        componentType newTarget = null;
                        foreach (componentType ct in group) {
                            if (ct.id.StartsWith(targetComponent)) {
                                if (newTarget == null)
                                    newTarget = ct;
                                else {
                                    if (newTarget.id.Length < ct.id.Length)
                                        newTarget = ct;
                                }
                            }
                        }
                        if (newTarget == null)
                            continue;

                        // check if an channel already exists for this inputporttype
                        bool duplicate = false;
                        foreach (channel c in deploymentChannelList.Values) {
                            if (c.target.component.id.Equals(newTarget.id) &&
                                c.target.port.id.Equals(inPort.portTypeID.Substring(targetComponent.Length + 1))) {
                                    duplicate = true;
                            }
                        }
                        if (duplicate)
                            continue;

                        // add dummy channel
                        channel groupChannel = new channel();

                        groupChannel.id = NewIdForChannel();
                        groupChannel.target.component.id = newTarget.id;
                        groupChannel.target.port.id = inPort.refs.portID;
                        groupChannel.source.component.id = copyDummyName;
                        groupChannel.source.port.id = "out";
                        if (!ChannelExists(groupChannel))
                            AddChannel(groupChannel);
                    } else if (o is outputPortType) {
                        outputPortType outPort = (outputPortType) o;
                        string sourceComponent = outPort.refs.componentID;
                        componentType newSource = null;
                        foreach (componentType ct in group) {
                            if (ct.id.StartsWith(sourceComponent)) {
                                if (newSource == null)
                                    newSource = ct;
                                else {
                                    if (newSource.id.Length < ct.id.Length)
                                        newSource = ct;
                                }
                            }
                        }
                        if (newSource == null)
                            continue;

                        // add dummy channel
                        channel groupChannel = new channel();

                        groupChannel.id = NewIdForChannel();
                        groupChannel.source.component.id = newSource.id;
                        groupChannel.source.port.id = outPort.refs.portID;
                        groupChannel.target.component.id = copyDummyName;
                        groupChannel.target.port.id = "in";
                        if (!ChannelExists(groupChannel))
                            AddChannel(groupChannel);
                    }
                    
                }
                // add dummy eventchannels to retain all eventchannel ports of the copied group
                
                foreach (ArrayList echList in copyGroupEventChannels) {
                    foreach (eventChannel ech in echList) {
                        componentType source = null;
                        componentType target = null;
                        foreach (componentType ct in group) {
                            if (ct.id.StartsWith(ech.sources.source.component.id)) {
                                if (source == null)
                                    source = ct;
                                else if (ct.id.Length > source.id.Length)
                                    source = ct;
                            }
                            if (ct.id.StartsWith(ech.targets.target.component.id)) {
                                if (target == null)
                                    target = ct;
                                else if (ct.id.Length > target.id.Length)
                                    target = ct;
                            }
                        }
                        if (source != null)
                            ech.sources.source.component.id = source.id;
                        if (target != null) {
                            ech.targets.target.component.id = target.id;
                            ech.id = target.id + "_" + ech.sources.source.eventPort.id + "_" + ech.targets.target.eventPort.id;
                        }
                        if (source != null || target != null)
                            eventChannelList.Add(ech);
                    }
                }


                LinkedList<portAlias> newPortAlias = new LinkedList<portAlias>();
                group originalGroup = null;
                foreach (object o in groups) {
                   // groups.Add(ct);
                    groupComponent gc = groupsList[((componentType)o).id];
                    // adapting the alias to the new group port names
                    
                    foreach (group gr in deploymentModel.groups) {
                        if (gr.id == ((componentType)o).id) {
                            originalGroup = gr;
                            break;
                        }
                    }


                    if (originalGroup != null && originalGroup.portAlias != null) {
                        foreach (portAlias alias in originalGroup.portAlias) {
                            foreach (string oldCompName in changedComponents.Keys) {
                                if (alias.portId.Contains(oldCompName)) {
                                    portAlias pAlias = new portAlias();
                                    pAlias.portId = alias.portId.Replace(oldCompName, changedComponents[oldCompName]);
                                    pAlias.portAlias1 = alias.portAlias1;
                                    newPortAlias.AddLast(pAlias);
                                    break;
                                }
                            }
                        }
                    }
                }


                DoGrouping((string) groupNames[groupIndex], false, true);
                group groupToUpdate = null;
                foreach (group gr in deploymentModel.groups) {
                    if (gr.id == (string)groupNames[groupIndex]) {
                        gr.portAlias = newPortAlias.ToArray();
                        groupToUpdate = gr;
                        break;
                    }
                }
                if (groupToUpdate.portAlias != null) {
                    foreach (portAlias alias in groupToUpdate.portAlias) {
                        componentType groupComponentToUpdate = deploymentComponentList[groupToUpdate.id];
                        groupComponentToUpdate.description = originalGroup.description;
                        foreach (object port in groupComponentToUpdate.ports) {
                            if ((port is inputPortType) && (((inputPortType)port).portTypeID == alias.portId)) {
                                ((inputPortType)port).PortAliasForGroups = alias.portAlias1;
                                ((inputPortType)port).PortLabel.Text = alias.portAlias1;
                                break;
                            } else if ((port is outputPortType) && (((outputPortType)port).portTypeID == alias.portId)) {
                                ((outputPortType)port).PortAliasForGroups = alias.portAlias1;
                                ((outputPortType)port).PortLabel.Text = alias.portAlias1;
                                break;
                            }
                        }
                    }
                }
                groupToUpdate.description = originalGroup.description;
                groupIndex++;
            }
            RemoveDummyFromModel(copyDummyName);
            DeleteDanglingChannels();
            DeleteDanglingEventChannels();

            // select the copied elements after paste
            selectedComponentList.Clear();
            foreach (componentType mc in modelToPaste.components) {
                selectedComponentList.AddLast(mc);
            }
            UpdateSelectedComponents();
            UpdateToolTips();
        }

        private void LoadComponentsCommand() {
            // reset component list, channel list and canvas
            deploymentComponentList.Clear();
            canvas.Children.Clear();
            deploymentChannelList.Clear();
            eventChannelLinesList.Clear();
            eventChannelList.Clear();
            groupsList.Clear();
            CleanGUICanvas();
            //copyModel = null;

            // loading the components
            foreach (object o in deploymentModel.components) {
                componentType modelComp = (componentType)o;
                if (deploymentComponentList.ContainsKey(modelComp.id))
                    continue;
                // check, if bundle is available
                if (componentList.ContainsKey(modelComp.type_id)) {

                    // init the ArrayLists containing the ports. Used for easier and faster access
                    modelComp.InitArrayLists();

                    foreach (propertyType p in modelComp.properties) {
                        modelComp.PropertyArrayList.Add(p);
                    }

                    // copy the property datatype and description from bundle_description
                    // also copy the port datatypes form the bundle_description
                    // works only, if bundle and deployment fits to each other

                    try {
                        // search for bundle component
                        Asterics.ACS2.componentTypesComponentType bundleComponent = (Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id];

                        // copy the ComponentType of the component
                        modelComp.ComponentType = bundleComponent.type.Value;

                        if (modelComp.properties != null) {
                            foreach (propertyType deploymentProperty in modelComp.properties) {
                                int index = 0;
                                // searching for the right component property
                                while (deploymentProperty.name != bundleComponent.properties[index].name) {
                                    index++;
                                }
                                // copy the properties of the component
                                deploymentProperty.DataType = bundleComponent.properties[index].type;

                                // check, if the property is a dynamic property
                                if (bundleComponent.properties[index].getStringList) {
                                    deploymentProperty.GetStringList = true;
                                }
                                else {
                                    deploymentProperty.GetStringList = false;
                                }

                                // check the value fitting to the datatype
                                if (!CheckPropertyDatatype(deploymentProperty.value, deploymentProperty.DataType)) {
                                    throw new LoadPropertiesException();
                                }

                                deploymentProperty.Description = bundleComponent.properties[index].description;
                                if (bundleComponent.properties[index].combobox != null) {
                                    deploymentProperty.ComboBoxStrings = bundleComponent.properties[index].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                }
                                deploymentProperty.PropertyChanged += ComponentPropertyChanged;
                                deploymentProperty.PropertyChangeError += ComponentPropertyChangeError;
                            }
                            // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                            if ((bundleComponent.properties == null) && (modelComp.properties.Length != 0)) {
                                throw new LoadPropertiesException();
                            }
                            else if ((bundleComponent.properties != null) && (modelComp.properties.Length != bundleComponent.properties.Length)) {
                                throw new LoadPropertiesException();
                            }
                        }

                        foreach (object portObj in modelComp.PortsList.Values) {
                            // searching for the inPorts
                            if (portObj is inputPortType) {
                                int index = 0;
                                inputPortType deploymentInPort = (inputPortType)portObj;
                                ArrayList helperListInPort = new ArrayList(); // a list with all InPorts of a component for the bundel_description
                                foreach (object bundleInPort in bundleComponent.ports) {
                                    if (bundleInPort is ACS2.inputPortType) {
                                        helperListInPort.Add(bundleInPort);
                                    }
                                }
                                while (deploymentInPort.portTypeID != ((ACS2.inputPortType)helperListInPort[index]).id) {
                                    index++; // searching for the right input port
                                }
                                // copy the dataType of the port
                                deploymentInPort.PortDataType = ((Asterics.ACS2.inputPortType)helperListInPort[index]).dataType;
                                deploymentInPort.MustBeConnected = ((Asterics.ACS2.inputPortType)helperListInPort[index]).mustBeConnected;
                                deploymentInPort.Description = ((Asterics.ACS2.inputPortType)helperListInPort[index]).description;
                                deploymentInPort.ComponentId = ((Asterics.ACS2.inputPortType)helperListInPort[index]).ComponentId;
                                deploymentInPort.ComponentTypeId = ((Asterics.ACS2.inputPortType)helperListInPort[index]).id;


                                // update the alias for group ports via property changed listener
                                deploymentInPort.PropertyChanged += InputPortIntPropertyChanged;

                                ACS2.propertyType[] sourceProperties = ((Asterics.ACS2.inputPortType)helperListInPort[index]).properties;
                                if ((sourceProperties != null) && (sourceProperties.Length > 0)) {
                                    //if (deploymentInPort.PropertyArrayList.Count > 0) {
                                    foreach (propertyType deploymentProperty in deploymentInPort.properties) {
                                        int inPortIndex = 0;
                                        while (deploymentProperty.name != sourceProperties[inPortIndex].name) {
                                            inPortIndex++;
                                        }
                                        // copy the properties of the inPort
                                        deploymentProperty.DataType = sourceProperties[inPortIndex].type;
                                        deploymentProperty.Description = sourceProperties[inPortIndex].description;
                                        if (sourceProperties[inPortIndex].combobox != null) {
                                            deploymentProperty.ComboBoxStrings = sourceProperties[inPortIndex].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                        }
                                        deploymentProperty.PropertyChanged += InPortPropertyChanged;
                                        deploymentProperty.PropertyChangeError += ComponentPropertyChangeError;
                                    }
                                    // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                                    if (deploymentInPort.properties.Length != sourceProperties.Length) {
                                        throw new LoadPropertiesException();
                                    }
                                }
                                deploymentInPort.properties = (propertyType[])deploymentInPort.PropertyArrayList.ToArray(typeof(propertyType));
                            }
                            else {
                                // comparing all outPports
                                int index = 0;
                                outputPortType outPort = (outputPortType)portObj;
                                ArrayList helperListOutPort = new ArrayList();
                                foreach (object origOutPort in bundleComponent.ports) {
                                    if (origOutPort is ACS2.outputPortType) {
                                        helperListOutPort.Add(origOutPort);
                                    }
                                }
                                while (outPort.portTypeID != ((Asterics.ACS2.outputPortType)helperListOutPort[index]).id) {
                                    index++;
                                }
                                // copy the dataType of the port
                                outPort.PortDataType = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).dataType;
                                outPort.Description = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).description;
                                outPort.ComponentId = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).ComponentId;
                                outPort.ComponentTypeId = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).id;

                                // update the alias for group ports via property changed listener
                                outPort.PropertyChanged += OutputPortIntPropertyChanged;

                                ACS2.propertyType[] sourceProperties = ((Asterics.ACS2.outputPortType)helperListOutPort[index]).properties;
                                if ((sourceProperties != null) && (sourceProperties.Length > 0)) {
                                    //if (outPort.PropertyArrayList.Count > 0) {
                                    foreach (propertyType compProperty in outPort.properties) {
                                        int outPortIndex = 0;
                                        while (compProperty.name != sourceProperties[outPortIndex].name) {
                                            outPortIndex++;
                                        }
                                        // copy the properties of the outPort
                                        compProperty.DataType = sourceProperties[outPortIndex].type;
                                        compProperty.Description = sourceProperties[outPortIndex].description;
                                        if (sourceProperties[outPortIndex].combobox != null) {
                                            compProperty.ComboBoxStrings = sourceProperties[outPortIndex].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                        }
                                        compProperty.PropertyChanged += OutPortPropertyChanged;
                                        compProperty.PropertyChangeError += ComponentPropertyChangeError;
                                    }
                                    // check the amount of properties. Cause an exception, if the bundle has more properties then the deployment
                                    if (outPort.properties.Length != sourceProperties.Length) {
                                        throw new LoadPropertiesException();
                                    }
                                }
                                outPort.properties = (propertyType[])outPort.PropertyArrayList.ToArray(typeof(propertyType));
                            }
                        }
                        // check if the amount of ports is equal to the amount of ports in the bundle
                        foreach (object origPort in bundleComponent.ports) {
                            if (origPort is ACS2.outputPortType) {
                                if (!modelComp.PortsList.Contains(((ACS2.outputPortType)origPort).id)) {
                                    outputPortType outPort = new outputPortType();
                                    outPort.PortDataType = ((Asterics.ACS2.outputPortType)origPort).dataType;
                                    outPort.Description = ((Asterics.ACS2.outputPortType)origPort).description;
                                    outPort.ComponentId = ((Asterics.ACS2.outputPortType)origPort).ComponentId;
                                    outPort.ComponentTypeId = ((Asterics.ACS2.outputPortType)origPort).id;
                                    outPort.portTypeID = ((Asterics.ACS2.outputPortType)origPort).id;
                                    modelComp.PortsList.Add(outPort.portTypeID, outPort);
                                    modelComp.ports = new object[modelComp.PortsList.Count];
                                    modelComp.PortsList.Values.CopyTo(modelComp.ports, 0);
                                    throw new LoadPortsException(outPort.portTypeID);
                                }
                            } else if (origPort is ACS2.inputPortType) {
                                if (!modelComp.PortsList.Contains(((ACS2.inputPortType)origPort).id)) {
                                    inputPortType inPort = new inputPortType();
                                    inPort.PortDataType = ((Asterics.ACS2.inputPortType)origPort).dataType;
                                    inPort.MustBeConnected = ((Asterics.ACS2.inputPortType)origPort).mustBeConnected;
                                    inPort.Description = ((Asterics.ACS2.inputPortType)origPort).description;
                                    inPort.ComponentId = ((Asterics.ACS2.inputPortType)origPort).ComponentId;
                                    inPort.ComponentTypeId = ((Asterics.ACS2.inputPortType)origPort).id;
                                    inPort.portTypeID = ((Asterics.ACS2.inputPortType)origPort).id;
                                    modelComp.PortsList.Add(inPort.portTypeID, inPort);
                                    modelComp.ports = new object[modelComp.PortsList.Count];
                                    modelComp.PortsList.Values.CopyTo(modelComp.ports, 0);
                                    throw new LoadPortsException(inPort.portTypeID);
                                }
                            }
                        }

                    } 
                    catch (LoadPortsException lPortEx) {
                        // HasVersionConflict indicates a version conflict between the component in a stored model and
                        // the component in the bundle descriptor
                        //modelComp.HasVersionConflict = true;
                        modelComp.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                        MessageBox.Show(Properties.Resources.CopyPortsErrorTextFormat(lPortEx.Message, modelComp.id), Properties.Resources.CopyPortsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, lPortEx.Message);

                    } catch (Exception lPropEx) { //LoadPropertiesException
                        MessageBox.Show(Properties.Resources.CopyPropertiesErrorTextFormat(modelComp.id), Properties.Resources.CopyPropertiesErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                        traceSource.TraceEvent(TraceEventType.Error, 3, lPropEx.Message);

                        //versionconflict
                        int posX;
                        int posY;
                        // set coordinates for the component in case there are not already set
                        if ((modelComp.layout.posX == null) || (modelComp.layout.posX == "") || (modelComp.layout.posY == null) || (modelComp.layout.posY == "")) {
                            posX = 40;
                            posY = 40;
                        }
                        else {
                            posX = Int32.Parse(modelComp.layout.posX);
                            posY = Int32.Parse(modelComp.layout.posY);
                        }

                        // backup component to load properties
                        componentType backupComp = modelComp;

                        modelComp = componentType.CopyFromBundleModel((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id], modelComp.id);
                        modelComp.layout.posX = Convert.ToString(posX);
                        modelComp.layout.posY = Convert.ToString(posY);

                        //reload the GUIElemens form the backup
                        if (backupComp.gui != null) {
                            modelComp.gui = backupComp.gui;
                        }
                        // HasVersionConflict indicates a version conflict between the component in a stored model and
                        // the component in the bundle descriptor
                        modelComp.HasVersionConflict = true;
                        modelComp.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                        //break;

                        // new code, copy property values from invalid (version conflict) component
                        foreach (propertyType deploymentProperty in modelComp.properties) {
                            foreach (propertyType backupProperty in backupComp.properties) {
                                if (deploymentProperty.name == backupProperty.name) {
                                    if (CheckPropertyDatatype(backupProperty.value, deploymentProperty.DataType)) {
                                        deploymentProperty.value = backupProperty.value; // try-parse is missing
                                    }
                                    break;
                                }
                            }
                        }

                    } // end of LoadPropertiesException
                

                    // set coordinates for the component in case there are not already set
                    if ((modelComp.layout.posX == null) || (modelComp.layout.posX == "") || (modelComp.layout.posY == null) || (modelComp.layout.posY == "")) {
                        int[] pos = ProperComponentCoordinates(40, 40);
                        modelComp.layout.posX = Convert.ToString(pos[0]);
                        modelComp.layout.posY = Convert.ToString(pos[1]);
                    }

                    // Searching for the event triggers and event listeners of a component
                    Asterics.ACS2.componentTypesComponentType bundleComponentEvents = (Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id];
                    // If component has version conflict, the events are already set by 'CopyFromBundleModel'
                    if ((bundleComponentEvents.events != null) && !modelComp.HasVersionConflict) {
                        foreach (object eventO in bundleComponentEvents.events) {
                            if (eventO is ACS2.eventsTypeEventListenerPortType) {
                                ACS2.eventsTypeEventListenerPortType compEl = (ACS2.eventsTypeEventListenerPortType)eventO;
                                EventListenerPort el = new EventListenerPort();
                                el.EventListenerId = compEl.id;
                                el.ComponentId = modelComp.id;
                                el.EventDescription = compEl.description;
                                modelComp.EventListenerList.Add(el);
                            }
                            else if (eventO is ACS2.eventsTypeEventTriggererPortType) {
                                ACS2.eventsTypeEventTriggererPortType compEl = (ACS2.eventsTypeEventTriggererPortType)eventO;
                                EventTriggerPort el = new EventTriggerPort();
                                el.EventTriggerId = compEl.id;
                                el.ComponentId = modelComp.id;
                                el.EventDescription = compEl.description;
                                modelComp.EventTriggerList.Add(el);
                            }
                        }
                    }


                    // if the component has no version conflict, it will be pasted on the layout, otherwise, it is already on the 
                    // canvas (done by 'CopyFromBundleModel')
                    if (!modelComp.HasVersionConflict) {
                        // setting the mainRectangle and the portRectangles on the canvas
                        modelComp.InitGraphLayout(modelComp.id);
                    }
                    else {
                        //deploymentModel.components = deploymentComponentList.Values.ToArray();
                    }

                    // check, if the component has a confilct marker, but the VersionConflict is not set. This happens,
                    // if the version conflict occurs bacause of new ports
                    if (!modelComp.HasVersionConflict && modelComp.ComponentCanvas.Background != null) {
                        modelComp.HasVersionConflict = true;
                    }

                    canvas.Children.Add(modelComp.ComponentCanvas);
                    KeyboardNavigation.SetTabIndex(modelComp.ComponentCanvas, canvas.Children.Count + 1);
                    Console.WriteLine(modelComp.id);
                    deploymentComponentList.Add(modelComp.id, modelComp);
                    // adding context menu
                    modelComp.MainRectangle.ContextMenu = componentContextMenu;
                    // adding keyboard focus listener
                    modelComp.ComponentCanvas.KeyDown += Component_KeyDown;
                    modelComp.ComponentCanvas.KeyUp += Component_KeyUp;
                    modelComp.ComponentCanvas.Focusable = true;
                    modelComp.ComponentCanvas.GotKeyboardFocus += ComponentCanvas_GotKeyboardFocus;
                    modelComp.ComponentCanvas.LostKeyboardFocus += ComponentCanvas_LostKeyboardFocus;

                    // adding property changed listener
                    modelComp.PropertyChanged += ComponentIntPropertyChanged;
                    modelComp.TopGrid.ContextMenu = componentContextMenu;
                    modelComp.TopRectangle.ContextMenu = componentContextMenu;
                    // set position of component on the canvas
                    Canvas.SetLeft(modelComp.ComponentCanvas, Int32.Parse(modelComp.layout.posX));
                    Canvas.SetTop(modelComp.ComponentCanvas, Int32.Parse(modelComp.layout.posY));

                    // Adapt the size of MainRectangle, if more then MAINRECTANGLENUMBEROFPORTS are in a component
                    int numInPorts = 0;
                    int numOutPorts = 0;
                    foreach (object objPort in modelComp.PortsList.Values) {
                        if (objPort is inputPortType) {
                            numInPorts++;
                        }
                        else {
                            numOutPorts++;
                        }
                    }
                    if (numOutPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                        modelComp.MainRectangle.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE);
                        modelComp.ComponentCanvas.Height += (numOutPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.OUTPORTDISTANCE);
                    }
                    else if (numInPorts > ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) {
                        modelComp.MainRectangle.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
                        modelComp.ComponentCanvas.Height += (numInPorts - ACS.LayoutConstants.MAINRECTANGLENUMBEROFPORTS) * (ACS.LayoutConstants.INPORTDISTANCE);
                    }
                    // Adapt the position of event trigger and event listener port, if the component has more input/output ports
                    if (modelComp.EventListenerList.Count > 0) {
                        Canvas.SetTop(modelComp.EventListenerPolygon.InputEventPortCanvas, modelComp.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
                    }
                    if (modelComp.EventTriggerList.Count > 0) {
                        Canvas.SetTop(modelComp.EventTriggerPolygon.OutputEventPortCanvas, modelComp.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10);
                    }
                }
                else {
                    MessageBox.Show(Properties.Resources.LoadComponentNotFoundFormat(modelComp.type_id), Properties.Resources.CopyPropertiesErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    // removing the channels
                    if (deploymentModel.channels != null) {
                        List<channel> tempChannelList = deploymentModel.channels.ToList();
                        foreach (object oChannel in deploymentModel.channels) {
                            channel modChannel = (channel)oChannel;
                            if ((modChannel.source.component.id == modelComp.id) || (modChannel.target.component.id == modelComp.id)) {
                                tempChannelList.Remove(modChannel);
                            }
                        }
                        deploymentModel.channels = tempChannelList.ToArray();
                    }
                    // removing the eventchannels
                    if (deploymentModel.eventChannels != null) {
                        List<eventChannel> tempEventChannelList = deploymentModel.eventChannels.ToList();
                        foreach (object oEventChannel in deploymentModel.eventChannels) {
                            eventChannel modEventChannel = (eventChannel)oEventChannel;
                            if ((modEventChannel.sources.source.component.id == modelComp.id) || (modEventChannel.targets.target.component.id == modelComp.id)) {
                                tempEventChannelList.Remove(modEventChannel);
                            }
                        }
                        deploymentModel.eventChannels = tempEventChannelList.ToArray();
                    }
                }

                // check, if component has a gui component, and load the gui component
                if (modelComp.gui != null && ((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id]) != null) {
                    if (((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id]).gui.IsExternalGUIElementSpecified && ((Asterics.ACS2.componentTypesComponentType)componentList[modelComp.type_id]).gui.IsExternalGUIElement) {
                        modelComp.gui.IsExternalGUIElement = true;
                    } else {
                        modelComp.gui.IsExternalGUIElement = false;
                    }
                    AddGUIComponent(modelComp);
                }
            }
            deploymentModel.components = deploymentComponentList.Values.ToArray();


            // loading the channels
            if (deploymentModel.channels != null) {
                foreach (object o in deploymentModel.channels) {
                    channel modChannel = (channel)o;
                    // check versionconflict: add only channels to components without a conflict
                    if ((deploymentComponentList.ContainsKey(modChannel.source.component.id)) && (deploymentComponentList.ContainsKey(modChannel.target.component.id))) {

                        // one of the channels component has a conflict. Check, if port is available and datatype fits together
                        componentType tempCompOut = (componentType)deploymentComponentList[modChannel.source.component.id];
                        componentType tempCompIn = (componentType)deploymentComponentList[modChannel.target.component.id];
                        // try, if the ports are still available
                        if ((tempCompOut.PortsList.Contains(modChannel.source.port.id)) && (tempCompIn.PortsList.Contains(modChannel.target.port.id))) {
                            // check the datatypes of the ports, for the case, that they have been changed
                            if (CheckInteroperabilityOfPorts(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortDataType, ((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortDataType)) {
                                AddChannel(modChannel);
                                modChannel.Line.Y1 = Canvas.GetTop(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortRectangle) + Canvas.GetTop(tempCompOut.ComponentCanvas) + 5;
                                modChannel.Line.X1 = Canvas.GetLeft(((outputPortType)tempCompOut.PortsList[modChannel.source.port.id]).PortRectangle) + Canvas.GetLeft(tempCompOut.ComponentCanvas) + 20;

                                modChannel.Line.Y2 = Canvas.GetTop(((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortRectangle) + Canvas.GetTop(tempCompIn.ComponentCanvas) + 5;
                                modChannel.Line.X2 = Canvas.GetLeft(((inputPortType)tempCompIn.PortsList[modChannel.target.port.id]).PortRectangle) + Canvas.GetLeft(tempCompIn.ComponentCanvas);
                                Canvas.SetZIndex(modChannel.Line, Canvas.GetZIndex(modChannel.Line) + 1000);
                            }
                            else {
                                // if no event listener Port can be found, the component has a version conflict
                                MessageBox.Show(Properties.Resources.CopyChannelsErrorTextFormat(tempCompOut.id, tempCompIn.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                tempCompIn.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompIn.HasVersionConflict = true;
                                tempCompOut.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompOut.HasVersionConflict = true;
                            }
                        }
                        else {
                            if (!tempCompOut.PortsList.Contains(modChannel.source.port.id)) {
                                MessageBox.Show(Properties.Resources.CopyChannelErrorNotFoundFormat(tempCompOut.id, tempCompIn.id, modChannel.source.port.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                tempCompOut.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompOut.HasVersionConflict = true;
                            }
                            else {
                                MessageBox.Show(Properties.Resources.CopyChannelErrorNotFoundFormat(tempCompOut.id, tempCompIn.id, modChannel.source.port.id), Properties.Resources.CopyChannelsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                tempCompIn.ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                tempCompIn.HasVersionConflict = true;
                            }
                        }

                    }
                }
            }

            // Loading the events and drawing the lines between the event ports
            if (deploymentModel.eventChannels != null) {
                bool foundLine = false;
                foreach (object o in deploymentModel.eventChannels) {
                    eventChannel evChannel = (eventChannel)o;
                    //if (!(((modelComponent)deploymentComponentList[evChannel.sources.source[0].component.id]).HasVersionConflict) && !(((modelComponent)deploymentComponentList[evChannel.targets.target[0].component.id]).HasVersionConflict)) {
                    bool foundTriggerPort = false;
                    bool foundListenerPort = false;
                    try {
                        foreach (EventTriggerPort checkEvent in ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).EventTriggerList) {
                            if (checkEvent.ComponentId == evChannel.sources.source.component.id && checkEvent.EventTriggerId == evChannel.sources.source.eventPort.id) {
                                foundTriggerPort = true;
                                break;
                            }
                        }
                        if (foundTriggerPort) {
                            foreach (EventListenerPort checkEvent in ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).EventListenerList) {
                                if (checkEvent.ComponentId == evChannel.targets.target.component.id && checkEvent.EventListenerId == evChannel.targets.target.eventPort.id) {
                                    foundListenerPort = true;
                                    break;
                                }
                            }
                            if (foundListenerPort) {
                                foreach (eventChannelLine channelLine in eventChannelLinesList) {
                                    if ((evChannel.sources.source.component.id == channelLine.TriggerComponentId) && (evChannel.targets.target.component.id == channelLine.ListenerComponentId)) {
                                        foundLine = true;
                                        break;
                                    }
                                }
                                if (!foundLine) {
                                    eventChannelLine eCL = new eventChannelLine();

                                    eCL.Line.X1 = Canvas.GetLeft(((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) + LayoutConstants.EVENTOUTPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                                    //eCL.Line.Y1 = Canvas.GetTop(((modelComponent)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) + LayoutConstants.EVENTOUTPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                                    eCL.Line.Y1 = Canvas.GetTop(((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas) +
                                        ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).MainRectangle.Height + LayoutConstants.EVENTPORTHEIGHT + LayoutConstants.MAINRECTANGLEOFFSETY - 7;
                                    eCL.Line.X2 = Canvas.GetLeft(((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                                    //eCL.Line.Y2 = Canvas.GetTop(((modelComponent)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) + LayoutConstants.EVENTINPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                                    eCL.Line.Y2 = Canvas.GetTop(((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas) +
                                        ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).MainRectangle.Height + LayoutConstants.EVENTPORTHEIGHT + LayoutConstants.MAINRECTANGLEOFFSETY - 7;

                                    eCL.Line.Focusable = true;
                                    eCL.ListenerComponentId = evChannel.targets.target.component.id;
                                    eCL.TriggerComponentId = evChannel.sources.source.component.id;
                                    eCL.Line.GotKeyboardFocus += EventChannel_GotKeyboardFocus;
                                    eCL.Line.LostKeyboardFocus += EventChannel_LostKeyboardFocus;
                                    eCL.Line.KeyDown += EventChannel_KeyDown;
                                    eCL.Line.ContextMenu = eventChannelContextMenu;
                                    eventChannelLinesList.Add(eCL);
                                    canvas.Children.Add(eCL.Line);
                                    KeyboardNavigation.SetTabIndex(eCL.Line, canvas.Children.Count + 1);
                                    Canvas.SetZIndex(eCL.Line, Canvas.GetZIndex(eCL.Line) + 2000);
                                }
                                eventChannelList.Add(o);
                                foundLine = false;
                            }
                            else {
                                // if no event listener Port can be found, the component has a version conflict
                                MessageBox.Show(Properties.Resources.CopyEventsErrorTextFormat(evChannel.targets.target.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                                ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                                ((componentType)deploymentComponentList[evChannel.targets.target.component.id]).HasVersionConflict = true;
                            }
                        }
                        else {
                            // if no event trigger Port can be found, the component has a version conflict
                            MessageBox.Show(Properties.Resources.CopyEventsErrorTextFormat(evChannel.sources.source.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).ComponentCanvas.Background = new SolidColorBrush(Colors.Orange);
                            ((componentType)deploymentComponentList[evChannel.sources.source.component.id]).HasVersionConflict = true;
                        }
                    }
                    catch (Exception) {
                        MessageBox.Show(Properties.Resources.CopyEventsExceptionTextFormat(evChannel.sources.source.component.id, evChannel.sources.source.eventPort.id,
                            evChannel.targets.target.component.id, evChannel.targets.target.component.id), Properties.Resources.CopyEventsErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    }
                }
                deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
            }


            ClearSelectedChannelList();
            ClearSelectedEventChannelList();
            ClearSelectedComponentList();
            // loading the groups
            if (deploymentModel.groups != null) {
                foreach (group makeGroup in deploymentModel.groups) {
                    foreach (string id in makeGroup.componentId) {
                        AddSelectedComponent(deploymentComponentList[id]);
                    }
                    string groupName = DoGrouping(null, false, false);
                    componentType ct = deploymentComponentList[groupName];
                    backupIdForPropertyEditor = groupName;
                    ct.id = makeGroup.id;
                    // set the alias
                    if (makeGroup.portAlias != null) {
                        foreach (portAlias alias in makeGroup.portAlias) {
                            componentType groupToUpdate = deploymentComponentList[makeGroup.id];
                            foreach (object port in groupToUpdate.ports) {
                                if ((port is inputPortType) && (((inputPortType)port).portTypeID == alias.portId)) {
                                    ((inputPortType)port).PortAliasForGroups = alias.portAlias1;
                                    ((inputPortType)port).PortLabel.Text = alias.portAlias1;
                                    break;
                                }
                                else if ((port is outputPortType) && (((outputPortType)port).portTypeID == alias.portId)) {
                                    ((outputPortType)port).PortAliasForGroups = alias.portAlias1;
                                    ((outputPortType)port).PortLabel.Text = alias.portAlias1;
                                    break;
                                }
                            }
                        }
                    }
                    if (makeGroup.description != null) {
                        deploymentComponentList[makeGroup.id].description = makeGroup.description;
                    }
                    ClearSelectedChannelList();
                    ClearSelectedEventChannelList();
                    ClearSelectedComponentList();
                }
            }


            // clear the undo/redo stack
            undoStack.Clear();
            redoStack.Clear();

            // focus the first element
            if (canvas.Children.Count > 0) {
                Keyboard.Focus(canvas.Children[0]);
            }
            else {
                Keyboard.Focus(canvas);
            }

            UpdateToolTips();
        }


        /// <summary>
        /// Copy Model
        /// </summary>
        private model CopyModel(model m) {
            if (m == null)
                return null;
            XmlSerializer x = new XmlSerializer(m.GetType());
            MemoryStream ms = new MemoryStream();
            x.Serialize(ms, m);
            ms.Seek(0, SeekOrigin.Begin);
            StreamReader sr = new StreamReader(ms);
            model copyModel = (model)x.Deserialize(ms);
            sr.Close();
            ms.Close();

            // copy attributes, not being copies because of [XMLIgnoreAttirubte]
            foreach (componentType component in copyModel.components) {
                if (componentList.ContainsKey(component.type_id))
                    component.ComponentType = ((Asterics.ACS2.componentTypesComponentType)componentList[component.type_id]).type.Value;
            }
            return copyModel;
        }


        /// <summary>
        /// Copy selected Items into the copyModel
        /// </summary>
        private void CopySelectedCommand() {
            copyOffsetMulti = 1;
            copyModel = new model();
            copyModel.modelName = "copy";
            // insert all selected components to the model
            LinkedList<componentType> t = new LinkedList<componentType>();
            for (int i = 0; i < selectedComponentList.Count; i++) {
                componentType ct = selectedComponentList.ElementAt(i);
                t.AddLast(ct);
            }
            copyModel.components = t.ToArray();
            //get all selected channels where the source and target components
            //are selected
            LinkedList<channel> copyChannels = new LinkedList<channel>();
            foreach (channel c in selectedChannelList) {
                bool sourceFound, targetFound;
                sourceFound = targetFound = false;
                foreach (componentType mc in copyModel.components) {
                    if (mc.ComponentType == ACS2.componentTypeDataTypes.group)
                        continue;
                    if (mc.id == c.source.component.id)
                        sourceFound = true;
                    if (mc.id == c.target.component.id)
                        targetFound = true;
                    if (sourceFound && targetFound) {
                        break;
                    }
                }
                if (sourceFound && targetFound)
                    copyChannels.AddLast(c);
            }
            copyModel.channels = new channel[copyChannels.Count];
            for (int i = 0; i < copyChannels.Count; i++)
                copyModel.channels[i] = copyChannels.ElementAt(i);

            // get all selected Eventchannels
            LinkedList<eventChannel> selEventChannels = new LinkedList<eventChannel>();
            foreach (eventChannelLine ecl in selectedEventChannelList) {
                //bool eventfound = false;
                foreach (eventChannel ech in eventChannelList) {
                    if (ecl.ListenerComponentId == ech.targets.target.component.id &&
                        ecl.TriggerComponentId == ech.sources.source.component.id) {
                        selEventChannels.AddLast(ech);
                        
                    }
                }
            }
            LinkedList<eventChannel> copyEventChannels = new LinkedList<eventChannel>();
            // get all selected eventchannels with a selected source and target
            foreach (eventChannel ech in selEventChannels) {
                bool sourceFound, targetFound;
                sourceFound = targetFound = false;
                foreach (componentType mc in copyModel.components) {
                    if (mc.id == ech.sources.source.component.id) {
                        if (mc.ComponentType != ACS2.componentTypeDataTypes.group)
                            sourceFound = true;
                    }
                    else if (mc.id == ech.targets.target.component.id) {
                        if (mc.ComponentType != ACS2.componentTypeDataTypes.group)
                            targetFound = true;
                    }
                    if (sourceFound && targetFound)
                        break;
                }
                if (sourceFound && targetFound)
                    copyEventChannels.AddLast(ech);
            }

            /*
             * Check all groups if they have docked EventListeners and EventTriggers
             * and add dummy lines to the model to retain these eventports in the pasted
             * model
             *  
             */
            copyGroupEventChannels = new ArrayList();
            foreach (componentType ct in copyModel.components) {
                if (ct.ComponentType != ACS2.componentTypeDataTypes.group)
                    continue;
                ArrayList echannels = new ArrayList();
                foreach (EventListenerPort elp in ct.EventListenerList) {
                    componentType target = null;
                    foreach (componentType tmpct in copyModel.components) {
                        if (elp.EventListenerId.StartsWith(tmpct.id)) {
                            if (target == null) {
                                target = tmpct;
                            } else if (tmpct.id.Length > target.id.Length)
                                target = tmpct;
                        }
                    }
                    if (target == null)
                        continue;
                    eventChannel tmpEC = new eventChannel();
                    tmpEC.sources.source.component.id = copyDummyName;
                    tmpEC.sources.source.eventPort.id = "eventtrigger";
                    tmpEC.targets.target.component.id = target.id;
                    tmpEC.targets.target.eventPort.id = elp.EventListenerId.Substring(target.id.Length+1);
                    tmpEC.id = target.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                    tmpEC.GroupOriginalSource = null;
                    tmpEC.GroupOriginalTarget = null;

                    bool found = false;
                    foreach (eventChannel ech in copyEventChannels) {
                        if (ech.sources.source.component.id.Equals(copyDummyName) &&
                            ech.targets.target.component.id.Equals(target.id)) {
                                found = true;
                                break;
                        }
                    }
                    if (!found)
                        echannels.Add(tmpEC);
                }


                foreach (EventTriggerPort etp in ct.EventTriggerList) {
                    componentType source = null;
                    foreach (componentType tmpct in copyModel.components) {
                        if (etp.EventTriggerId.StartsWith(tmpct.id)) {
                            if (source == null) {
                                source = tmpct;
                            }
                            else if (tmpct.id.Length > source.id.Length)
                                source = tmpct;
                        }
                    }
                    if (source == null)
                        continue;
                    eventChannel tmpEC = new eventChannel();
                    tmpEC.sources.source.component.id = source.id;
                    tmpEC.sources.source.eventPort.id = etp.EventTriggerId.Substring(source.id.Length + 1);
                    tmpEC.targets.target.component.id = copyDummyName;
                    tmpEC.targets.target.eventPort.id = "eventlistener"; 
                    tmpEC.id = source.id + "_" + tmpEC.sources.source.eventPort.id + "_" + tmpEC.targets.target.eventPort.id;
                    tmpEC.GroupOriginalSource = null;
                    tmpEC.GroupOriginalTarget = null;

                    bool found = false;
                    foreach (eventChannel ech in copyEventChannels) {
                        if (ech.sources.source.component.id.Equals(copyDummyName) &&
                            ech.targets.target.component.id.Equals(source.id)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        echannels.Add(tmpEC);
                }
                
                copyGroupEventChannels.Add(echannels);
            }



            copyModel.eventChannels = copyEventChannels.ToArray();
            copyModel = CopyModel(copyModel);
        }


        /// <summary>
        /// Save the deployment model (actual drawing) as xml-file to the local HDD
        /// </summary>
        /// <param name="saveAs">If true, a file name dialog will be opened</param>
        /// <returns>True, if file was saved successfully</returns>
        private bool SaveLocalCommand(bool saveAs) {

            if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 0)) {
                deploymentModel.eventChannels = null;
            }
            else if ((deploymentModel.eventChannels != null) && (deploymentModel.eventChannels.Length == 1) && (deploymentModel.eventChannels[0] == null)) {
                deploymentModel.eventChannels = null;
            }
            XmlSerializer x = new XmlSerializer(deploymentModel.GetType());
            // firstly, write the data to a tempfile and use this temp file, checking valitity against schema
            FileStream str = new FileStream(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), FileMode.Create);
            x.Serialize(str, RemoveGroupingElementsInDeployment(deploymentModel));
            str.Close();

            // check, if model is valid against the deployment_model schema
            String xmlError;
            XmlValidation xv = new XmlValidation();
            // old, working code: xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));


            if (!File.Exists(ini.IniReadValue("model", "deployment_schema"))) {
                xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("model", "deployment_schema"));
            } else {
                xmlError = xv.validateXml(System.IO.Path.GetTempPath() + ini.IniReadValue("model", "tempfile"), ini.IniReadValue("model", "deployment_schema"));
            }


            // if valid, xml-file will be written
            if (xmlError.Equals("")) {
                try {
                    if (saveAs || saveFile == null) {
                        System.Windows.Forms.SaveFileDialog saveLocalXML = new System.Windows.Forms.SaveFileDialog();

                        //saveLocalXML.InitialDirectory = "c:\\temp\\";
                        saveLocalXML.Filter = "AsTeRICS-Files (*.acs)|*.acs|All files (*.*)|*.*";
                        saveLocalXML.FilterIndex = 1;
                        saveLocalXML.RestoreDirectory = true;

                        SetSaveFile(null);
                        if (saveLocalXML.ShowDialog() == System.Windows.Forms.DialogResult.OK) {
                            SetSaveFile(saveLocalXML.FileName);

                            str = new FileStream(saveFile, FileMode.Create);
                            //x.Serialize(str, deploymentModel);
                            x.Serialize(str, RemoveGroupingElementsInDeployment(deploymentModel));
                            str.Close();
                            modelHasBeenEdited = false;
                            AddToRecentList(saveFile);
                            return true;
                        }
                    }
                    else {
                        if (ini.IniReadValue("Options", "createBackupFile").Equals("true")) {
                            File.Copy(saveFile, saveFile + ".backup", true);
                        }

                        str = new FileStream(saveFile, FileMode.Create);
                        x.Serialize(str, RemoveGroupingElementsInDeployment(deploymentModel));
                        str.Close();
                        modelHasBeenEdited = false;
                        return true;
                    }
                }
                catch (Exception fileEx) {
                    MessageBox.Show(Properties.Resources.SaveDialogErrorMessage, Properties.Resources.SaveDialogErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                    traceSource.TraceEvent(TraceEventType.Error, 3, fileEx.Message + "\n" + fileEx.StackTrace);
                }
            }
            else {
                MessageBox.Show(Properties.Resources.XmlValidErrorText, Properties.Resources.XmlValidErrorHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                traceSource.TraceEvent(TraceEventType.Error, 3, xmlError);
            }
            return false;
        }

        /// <summary>
        /// Close Application. Before layout settings will be saved and the user
        /// will be asked to save the data model
        /// </summary>
        /// <param name="eventObject">Object of type CancelEventArgs or RoutedEventArgs</param>
        private void CloseCommand(object eventObject) {
            if (dockableComponentProperties.ContainerPane.Items.Count > 0) {
                dockableComponentProperties.ContainerPane.SelectedItem = dockableComponentProperties.ContainerPane.Items[0];
            }
            if (ini.IniReadValue("Options", "useAppDataFolder").Equals("true")) {
                dockManager.SaveLayout(Environment.GetFolderPath(System.Environment.SpecialFolder.ApplicationData) + "\\AsTeRICS\\ACS\\" + ini.IniReadValue("Layout", "layout_file"));
            } else {
                if (File.Exists(ini.IniReadValue("Layout", "layout_file"))) {
                    dockManager.SaveLayout(ini.IniReadValue("Layout", "layout_file"));
                } else {
                    dockManager.SaveLayout(AppDomain.CurrentDomain.BaseDirectory + ini.IniReadValue("Layout", "layout_file"));
                }
            }

            if (modelHasBeenEdited) {
                SaveQuestionDialog saveQuestion = new SaveQuestionDialog();
                saveQuestion.Owner = this;
                saveQuestion.ShowDialog();

                // Process message box results
                switch (saveQuestion.Result) {
                    case SaveQuestionDialog.save:
                        bool shouldSaveAs = false;
                        if (saveFile == null) shouldSaveAs = true;
                        if (SaveLocalCommand(shouldSaveAs) == true) {
                            StopStatusPolling();
                            if (!(eventObject is CancelEventArgs)) {
                                Application.Current.Shutdown();
                            }
                        }
                        else {
                            if (eventObject is CancelEventArgs) {
                                ((CancelEventArgs)eventObject).Cancel = true;
                            }
                        }
                        break;
                    case SaveQuestionDialog.dontSave:
                        StopStatusPolling();
                        if (!(eventObject is CancelEventArgs)) {
                            Application.Current.Shutdown();
                        }
                        break;
                    case SaveQuestionDialog.cancel:
                        if (eventObject is CancelEventArgs) {
                            ((CancelEventArgs)eventObject).Cancel = true;
                        }
                        break;
                }
            }
            else {
                StopStatusPolling();
                if (!(eventObject is CancelEventArgs)) {
                    Application.Current.Shutdown();
                }
            }
        }



        /// <summary>
        /// Move all selected components on the canvas relative to the given offset
        /// </summary>
        /// <param name="xOffset"></param>
        /// <param name="yOffset"></param>
        private void MoveSelectedComponents(int xOffset, int yOffset) {
            bool moveX = true;
            bool moveY = true;
            if (moveTracking == false) {
                moveTracking = true;
                CommandObject co = CreateMoveCommandObject();
                undoStack.Push(co);
                redoStack.Clear();
            }
            foreach (componentType tempComponent in selectedComponentList) {
                int xPos = (int)Canvas.GetLeft(tempComponent.ComponentCanvas);
                int yPos = (int)Canvas.GetTop(tempComponent.ComponentCanvas);
                int newxPos = xPos - xOffset;
                int newyPos = yPos - yOffset;
                if (tempComponent.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                    if (newxPos < 0)
                        moveX = false;
                    else if (newxPos + tempComponent.ComponentCanvas.Width > canvas.RenderSize.Width)
                        moveX = false;
                    if (newyPos < 0)
                        moveY = false;
                    else if (newyPos + tempComponent.ComponentCanvas.Height > canvas.RenderSize.Height)
                        moveY = false;
                }
            }
            foreach (componentType tempComponent in selectedComponentList) {
                int xPos = (int)Canvas.GetLeft(tempComponent.ComponentCanvas);
                int yPos = (int)Canvas.GetTop(tempComponent.ComponentCanvas);
                int newxPos = xPos;
                int newyPos = yPos;
                if (moveX)
                    newxPos = newxPos - xOffset;
                if (moveY)
                    newyPos = newyPos - yOffset;
                if (tempComponent.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                    if (newxPos < 0)
                        newxPos = 0;
                    else if (newxPos + tempComponent.ComponentCanvas.Width > canvas.RenderSize.Width)
                        newxPos = (int)(canvas.RenderSize.Width - tempComponent.ComponentCanvas.Width);
                    if (newyPos < 0)
                        newyPos = 0;
                    else if (newyPos + tempComponent.ComponentCanvas.Height > canvas.RenderSize.Height)
                        newyPos = (int)(canvas.RenderSize.Height - tempComponent.ComponentCanvas.Height);
                } else {
                    newxPos =xPos - xOffset; 
                    newyPos = yPos - yOffset;
                }
                Canvas.SetLeft(tempComponent.ComponentCanvas, newxPos);
                Canvas.SetTop(tempComponent.ComponentCanvas, newyPos);
                tempComponent.layout.posX = newxPos.ToString();
                tempComponent.layout.posY = newyPos.ToString();

                int counterIn = 0;
                int counterOut = 0;
                // all connected channels also have to move
               
                foreach (Object o in tempComponent.PortsList.Values) {
                    if (o is inputPortType) {
                        inputPortType pIn = (inputPortType)o;
                        if ((pIn.ChannelId != "") && (deploymentChannelList.ContainsKey(pIn.ChannelId))) {
                            deploymentChannelList[pIn.ChannelId].Line.X2 = newxPos + LayoutConstants.INPORTRECTANGLEOFFSETX;
                            deploymentChannelList[pIn.ChannelId].Line.Y2 = newyPos + (LayoutConstants.INPORTRECTANGLEOFFSETY + LayoutConstants.INPORTRECTANGLEHEIGHT / 2) +
                                counterIn * LayoutConstants.INPORTDISTANCE;
                        } 
                        if ((pIn.GroupChannelId != "") && (deploymentChannelList.ContainsKey(pIn.GroupChannelId))) {
                            deploymentChannelList[pIn.GroupChannelId].Line.X2 = newxPos + LayoutConstants.INPORTRECTANGLEOFFSETX;
                            deploymentChannelList[pIn.GroupChannelId].Line.Y2 = newyPos + (LayoutConstants.INPORTRECTANGLEOFFSETY + LayoutConstants.INPORTRECTANGLEHEIGHT / 2) +
                                counterIn * LayoutConstants.INPORTDISTANCE;
                        }
                        counterIn++;
                    }
                    else if (o is outputPortType) {
                        outputPortType pOut = (outputPortType)o;
                        if (pOut.ChannelIds.Count > 0) {
                            foreach (string s in pOut.ChannelIds) {
                                deploymentChannelList[s].Line.X1 = newxPos + (LayoutConstants.MAINRECTANGLEOFFSETX + LayoutConstants.MAINRECTANGLEWIDTH + LayoutConstants.OUTPORTRECTANGLEWIDTH / 2);
                                deploymentChannelList[s].Line.Y1 = newyPos + (LayoutConstants.OUTPORTRECTANGLEOFFSETY + LayoutConstants.OUTPORTRECTANGLEHEIGHT / 2) +
                                    counterOut * LayoutConstants.OUTPORTDISTANCE;
                            }
                        }
                        counterOut++;
                    }
                }
                // move the event channels
                if (tempComponent.EventListenerList.Count > 0) {
                    foreach (eventChannelLine line in eventChannelLinesList) {
                        if (line.ListenerComponentId == tempComponent.id) {
                            line.Line.X2 = newxPos + LayoutConstants.EVENTINPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                            //line.Line.Y2 = yPos - offsetY + LayoutConstants.EVENTINPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                            line.Line.Y2 = newyPos + tempComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10 + LayoutConstants.EVENTPORTHEIGHT + 3;
                        }
                    }
                }
                if (tempComponent.EventTriggerList.Count > 0) {
                    foreach (eventChannelLine line in eventChannelLinesList) {
                        if (line.TriggerComponentId == tempComponent.id) {
                            line.Line.X1 = newxPos + LayoutConstants.EVENTOUTPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                            //line.Line.Y1 = yPos - offsetY + LayoutConstants.EVENTOUTPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                            line.Line.Y1 = newyPos + tempComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10 + LayoutConstants.EVENTPORTHEIGHT + 3;
                        }
                    }
                }
            }
            modelHasBeenEdited = true;
        }


        /// <summary>
        /// Move a component on the canvas
        /// </summary>
        /// <param name="moveComponent">The component to move</param>
        /// <param name="xPos"></param>
        /// <param name="yPos"></param>
        private void MoveComponent(componentType moveComponent, int xPos, int yPos) {
            int leftVal = xPos - offsetX;
            int topVal = yPos - offsetY;
            Size csize = canvas.RenderSize;
            if (moveComponent.ComponentCanvas.Visibility == System.Windows.Visibility.Visible) {
                if (leftVal + moveComponent.ComponentCanvas.Width > csize.Width)
                    leftVal = (int)(csize.Width - moveComponent.ComponentCanvas.Width);
                else if (leftVal < 0)
                    leftVal = 0;
                if (topVal + moveComponent.ComponentCanvas.Height > csize.Height)
                    topVal = (int)(csize.Height - moveComponent.ComponentCanvas.Height);
                else if (topVal < 0)
                    topVal = 0;
            } else {
                leftVal = xPos - offsetX;
                topVal = yPos - offsetY;
            }
            Canvas.SetLeft(moveComponent.ComponentCanvas, leftVal);
            Canvas.SetTop(moveComponent.ComponentCanvas, topVal);

            moveComponent.layout.posX = (leftVal).ToString();
            moveComponent.layout.posY = (topVal).ToString();
            int counterIn = 0;
            int counterOut = 0;

            // also move all channels which are virtually connected over groups
            foreach (channel c in deploymentChannelList.Values) {
                if (c.target.component.id.Equals(moveComponent.id)) {
                    c.Line.X2 = xPos + LayoutConstants.INPORTRECTANGLEOFFSETX - offsetX;
                    c.Line.Y2 = yPos + (LayoutConstants.INPORTRECTANGLEOFFSETY + LayoutConstants.INPORTRECTANGLEHEIGHT / 2) +
                            counterIn * LayoutConstants.INPORTDISTANCE - offsetY;
                }
            }
            
            // all connected channels also have to move
            foreach (Object o in moveComponent.PortsList.Values) {
                if (o is inputPortType) {
                    inputPortType pIn = (inputPortType)o;
                    if (pIn.ChannelId != "") {
                        deploymentChannelList[pIn.ChannelId].Line.X2 = xPos + LayoutConstants.INPORTRECTANGLEOFFSETX - offsetX;
                        deploymentChannelList[pIn.ChannelId].Line.Y2 = yPos + (LayoutConstants.INPORTRECTANGLEOFFSETY + LayoutConstants.INPORTRECTANGLEHEIGHT / 2) +
                            counterIn * LayoutConstants.INPORTDISTANCE - offsetY;
                    }
                    counterIn++;
                }
                else if (o is outputPortType) {
                    outputPortType pOut = (outputPortType)o;
                    if (pOut.ChannelIds.Count > 0) {
                        foreach (string s in pOut.ChannelIds) {
                            deploymentChannelList[s].Line.X1 = xPos + (LayoutConstants.MAINRECTANGLEOFFSETX + LayoutConstants.MAINRECTANGLEWIDTH + LayoutConstants.OUTPORTRECTANGLEWIDTH / 2) - offsetX;
                            deploymentChannelList[s].Line.Y1 = yPos + (LayoutConstants.OUTPORTRECTANGLEOFFSETY + LayoutConstants.OUTPORTRECTANGLEHEIGHT / 2) +
                                counterOut * LayoutConstants.OUTPORTDISTANCE - offsetY;
                        }
                    }
                    counterOut++;
                }
            }
            // move the event channels
            if (moveComponent.EventListenerList.Count > 0) {
                foreach (eventChannelLine line in eventChannelLinesList) {
                    if (line.ListenerComponentId == moveComponent.id) {
                        line.Line.X2 = xPos - offsetX + LayoutConstants.EVENTINPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                        //line.Line.Y2 = yPos - offsetY + LayoutConstants.EVENTINPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                        line.Line.Y2 = yPos - offsetY + moveComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10 + LayoutConstants.EVENTPORTHEIGHT + 3;
                    }
                }
            }
            if (moveComponent.EventTriggerList.Count > 0) {
                foreach (eventChannelLine line in eventChannelLinesList) {
                    if (line.TriggerComponentId == moveComponent.id) {
                        line.Line.X1 = xPos - offsetX + LayoutConstants.EVENTOUTPORTCANVASOFFSETX + LayoutConstants.EVENTPORTWIDTH / 2 + 5;
                        //line.Line.Y1 = yPos - offsetY + LayoutConstants.EVENTOUTPORTCANVASOFFSETY + LayoutConstants.EVENTPORTHEIGHT + 3;
                        line.Line.Y1 = yPos - offsetY + moveComponent.MainRectangle.Height + ACS.LayoutConstants.MAINRECTANGLEOFFSETY - 10 + LayoutConstants.EVENTPORTHEIGHT + 3;
                    }
                }
            }
            modelHasBeenEdited = true;
        }

        /// <summary>
        /// Add a component to the canvas and the data model.
        /// </summary>
        /// <param name="newComponent"></param>
        public void AddComponent(componentType newComponent) {
            canvas.Children.Add(newComponent.ComponentCanvas);

            Canvas.SetLeft(newComponent.ComponentCanvas, Int32.Parse(newComponent.layout.posX));
            Canvas.SetTop(newComponent.ComponentCanvas, Int32.Parse(newComponent.layout.posY));

            // adding the context menu
            newComponent.MainRectangle.ContextMenu = componentContextMenu;
            newComponent.TopGrid.ContextMenu = componentContextMenu;
            newComponent.TopRectangle.ContextMenu = componentContextMenu;
            // adding keyboard focus listener
            newComponent.ComponentCanvas.KeyDown += Component_KeyDown;
            newComponent.ComponentCanvas.KeyUp += Component_KeyUp;
            newComponent.ComponentCanvas.Focusable = true;

            // adding property changed listener
            newComponent.PropertyChanged += ComponentIntPropertyChanged;
            newComponent.ComponentCanvas.GotKeyboardFocus += ComponentCanvas_GotKeyboardFocus;
            newComponent.ComponentCanvas.LostKeyboardFocus += ComponentCanvas_LostKeyboardFocus;
            // add the new component to the ArrayList (which will be used for faster internal access and manipulation)
            deploymentComponentList.Add(newComponent.id, newComponent);
            // adding the new component to the components-array (required for the schema consistency)                
            deploymentModel.components = deploymentComponentList.Values.ToArray();
            //FocusManager.SetIsFocusScope(newComponent.ComponentCanvas, true);
            Keyboard.Focus(newComponent.ComponentCanvas);
            KeyboardNavigation.SetTabIndex(newComponent.ComponentCanvas, canvas.Children.Count + 1);
            ClearSelectedEventChannelList();
            ClearSelectedComponentList();
            ClearSelectedChannelList();
            AddSelectedComponent(newComponent);
            modelHasBeenEdited = true;
            if (newComponent.gui != null) {
                if (((Asterics.ACS2.componentTypesComponentType)componentList[newComponent.type_id]).gui.IsExternalGUIElementSpecified && ((Asterics.ACS2.componentTypesComponentType)componentList[newComponent.type_id]).gui.IsExternalGUIElement) {
                    newComponent.gui.IsExternalGUIElement = true;
                } else {
                    newComponent.gui.IsExternalGUIElement = false;
                }
                AddGUIComponent(newComponent);
            }
            UpdateToolTips();
        }

        /// <summary>
        /// Delete a component from the canvas and the data model
        /// </summary>
        /// <param name="deleteComponent"></param>
        private void DeleteComponent(componentType deleteComponent) {
            // delete the component
            
            canvas.Children.Remove(deleteComponent.ComponentCanvas);
            if (deleteComponent.gui != null) {
                RemoveGUIComponent(deleteComponent);
            }
            if (deleteComponent.ComponentType == ACS2.componentTypeDataTypes.group) {
                LinkedList<group> deploymentGroups = new LinkedList<group>();
                foreach (group g in deploymentModel.groups) {
                    if (g.id.Equals(deleteComponent.id) == false) {
                        deploymentGroups.AddLast(g);
                    }
                }
                deploymentModel.groups = deploymentGroups.ToArray();
                groupsList.Remove(deleteComponent.id);
            }
            deploymentComponentList.Remove(deleteComponent.id);
            // set the array with the new amount of components
            deploymentModel.components = deploymentComponentList.Values.ToArray();
            // set the array with the new amount of channels, in the case, any channels have been deleted
            deploymentModel.channels = deploymentChannelList.Values.ToArray();
            deleteComponent.ComponentCanvas.KeyDown -= Component_KeyDown;
            deleteComponent.ComponentCanvas.KeyUp -= Component_KeyUp;

            deleteComponent.PropertyChanged -= ComponentIntPropertyChanged;
            deleteComponent.ComponentCanvas.GotKeyboardFocus -= ComponentCanvas_GotKeyboardFocus;
            deleteComponent.ComponentCanvas.LostKeyboardFocus -= ComponentCanvas_LostKeyboardFocus;

            modelHasBeenEdited = true;
            UpdateToolTips();
        }

        /// <summary>
        /// Add a new channel to the canvas and the data model
        /// </summary>
        /// <param name="newChannel"></param>
        private void AddChannel(channel newChannel) {
            if (deploymentChannelList.ContainsKey(newChannel.id))
                return;
            deploymentChannelList.Add(newChannel.id, newChannel);
            if (!canvas.Children.Contains(newChannel.Line)) {
                canvas.Children.Add(newChannel.Line);
                newChannel.Line.StrokeDashArray = null;
                KeyboardNavigation.SetTabIndex(newChannel.Line, canvas.Children.Count + 1);
            }
            componentType tempComponent = deploymentComponentList[newChannel.source.component.id];
            ((outputPortType)tempComponent.PortsList[newChannel.source.port.id]).ChannelIds.Add(newChannel.id);
            tempComponent = deploymentComponentList[newChannel.target.component.id];

            // check, if there is already a channel connected to the port. If so, it must be a channel of a group, being connected to the input port
            object o = tempComponent.PortsList[newChannel.target.port.id];
            if (o != null) {
                if (((inputPortType)o).ChannelId == "") {
                    ((inputPortType)tempComponent.PortsList[newChannel.target.port.id]).ChannelId = newChannel.id;
                }
                else {
                    ((inputPortType)tempComponent.PortsList[newChannel.target.port.id]).GroupChannelId = newChannel.id;
                }
            }

            // Set GroupOriginal Source
            string sourceCompId = newChannel.source.component.id;
            if (deploymentComponentList.ContainsKey(sourceCompId)) {
                if (groupsList.ContainsKey(deploymentComponentList[sourceCompId].id)) {
                    int maxLength = 0;
                    componentType tmpct = null;
                    foreach (componentType ct in deploymentComponentList.Values) {
                        if (newChannel.source.port.id.StartsWith(ct.id)) {
                            if (ct.id.Length > maxLength) {
                                tmpct = ct;
                                maxLength = ct.id.Length;
                            }
                        }
                    }
                    if (tmpct != null) {
                        bindingEdge be = new bindingEdge();
                        be.component = new bindingEdgeComponentType();
                        be.component.id = tmpct.id;
                        be.port = new bindingEdgePortType();
                        be.port.id = newChannel.source.port.id.Substring(tmpct.id.Length + 1);
                        newChannel.GroupOriginalSource = be;
                    }
                }
            }

            // Set GroupOriginal Target
            string targetCompId = newChannel.target.component.id;
            if (deploymentComponentList.ContainsKey(targetCompId)) {
                if (groupsList.ContainsKey(deploymentComponentList[targetCompId].id)) {
                    int maxLength = 0;
                    componentType tmpct = null;
                    foreach (componentType ct in deploymentComponentList.Values) {
                        if (newChannel.target.port.id.StartsWith(ct.id)) {
                            if (ct.id.Length > maxLength) {
                                tmpct = ct;
                                maxLength = ct.id.Length;
                            }
                        }
                    }
                    if (tmpct != null) {
                        bindingEdge be = new bindingEdge();
                        be.component = new bindingEdgeComponentType();
                        be.component.id = tmpct.id;
                        be.port = new bindingEdgePortType();
                        be.port.id = newChannel.target.port.id.Substring(tmpct.id.Length + 1);
                        newChannel.GroupOriginalTarget = be;
                    }
                }
            }

            // new channel has a group source and a component target
            if (newChannel.GroupOriginalSource != null) {
                // add channel also to the both original componenttypes
                channel groupChannel = new channel();

                groupChannel.id = NewIdForGroupChannel();
                groupChannel.source.component.id = newChannel.GroupOriginalSource.component.id;
                groupChannel.source.port.id = newChannel.GroupOriginalSource.port.id;
                groupChannel.target.component.id = newChannel.target.component.id;
                groupChannel.target.port.id = newChannel.target.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel);

                groupChannel.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 20;


                groupChannel.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas);

                groupChannel.Line.Visibility = System.Windows.Visibility.Hidden;

                Canvas.SetZIndex(groupChannel.Line, Canvas.GetZIndex(groupChannel.Line) + 1000);

            }
            if (newChannel.GroupOriginalTarget != null) {
                // add channel also to the both original componenttypes
                channel groupChannel = new channel();

                groupChannel.id = NewIdForGroupChannel();
                groupChannel.source.component.id = newChannel.source.component.id;
                groupChannel.source.port.id = newChannel.source.port.id;
                groupChannel.target.component.id = newChannel.GroupOriginalTarget.component.id;
                groupChannel.target.port.id = newChannel.GroupOriginalTarget.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel);

                groupChannel.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 20;


                groupChannel.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas);

                groupChannel.Line.Visibility = System.Windows.Visibility.Hidden;

                Canvas.SetZIndex(groupChannel.Line, Canvas.GetZIndex(groupChannel.Line) + 1000);

            }

            if (newChannel.GroupOriginalSource != null && newChannel.GroupOriginalTarget != null) {
                channel groupChannel = new channel();

                groupChannel.id = NewIdForGroupChannel();
                groupChannel.source.component.id = newChannel.GroupOriginalSource.component.id;
                groupChannel.source.port.id = newChannel.GroupOriginalSource.port.id;
                groupChannel.target.component.id = newChannel.GroupOriginalTarget.component.id;
                groupChannel.target.port.id = newChannel.GroupOriginalTarget.port.id;
                if (!ChannelExists(groupChannel))
                    AddChannel(groupChannel);

                groupChannel.Line.Y1 = Canvas.GetTop(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X1 = Canvas.GetLeft(((outputPortType)(deploymentComponentList[groupChannel.source.component.id]).PortsList[groupChannel.source.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.source.component.id]).ComponentCanvas) + 20;


                groupChannel.Line.Y2 = Canvas.GetTop(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetTop((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas) + 5;
                groupChannel.Line.X2 = Canvas.GetLeft(((inputPortType)(deploymentComponentList[groupChannel.target.component.id]).PortsList[groupChannel.target.port.id]).PortRectangle) + Canvas.GetLeft((deploymentComponentList[groupChannel.target.component.id]).ComponentCanvas);

                groupChannel.Line.Visibility = System.Windows.Visibility.Hidden;

                Canvas.SetZIndex(groupChannel.Line, Canvas.GetZIndex(groupChannel.Line) + 1000);
            }

            newChannel.Line.ContextMenu = channelContextMenu;
            newChannel.Line.Focusable = true;
            newChannel.Line.GotKeyboardFocus += Channel_GotKeyboardFocus;
            newChannel.Line.LostKeyboardFocus += Channel_LostKeyboardFocus;
            newChannel.Line.KeyDown += Channel_KeyDown;
            // adding the new channel to the channel-array (required for the schema consistency)
            deploymentModel.channels = deploymentChannelList.Values.ToArray();
            modelHasBeenEdited = true;

            UpdateToolTips();
        }

        /// <summary>
        /// Delete a channel from the canvas and the data model
        /// </summary>
        /// <param name="deleteChannel"></param>
        /*private void DeleteChannel(channel deleteChannel) {
            // delete the reference in the source and target components
            componentType tempComponent = deploymentComponentList[deleteChannel.source.component.id];
            ((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).ChannelIds.Remove(deleteChannel.id);
            tempComponent = deploymentComponentList[deleteChannel.target.component.id];

            if (deleteChannel.id.StartsWith("group") && (((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).GroupChannelId != "")) { // && !deleteChannel.target.component.id.StartsWith("group")) {
                ((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).GroupChannelId = "";
            } else {
                ((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).ChannelId = "";
            }
            if (deleteChannel.id.StartsWith("group") && deleteChannel.source.component.id.StartsWith("group") ) {
                componentType tempRefComponent = deploymentComponentList[((outputPortType)deploymentComponentList[deleteChannel.source.component.id].PortsList[deleteChannel.source.port.id]).refs.componentID];
                outputPortType tempOutPort = (outputPortType)tempRefComponent.PortsList[((outputPortType)deploymentComponentList[deleteChannel.source.component.id].PortsList[deleteChannel.source.port.id]).refs.portID];
                //outputPortType tempOutPort = (outputPortType)deploymentComponentList[((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).refs.componentID].PortsList[((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).refs.portID];
                tempOutPort.ChannelIds.Remove(deleteChannel.id);
            }
            
            // delete the channel itself

            canvas.Children.Remove(deleteChannel.Line);
            deploymentChannelList.Remove(deleteChannel.id);
            // copy hashtable to array
            deploymentModel.channels = deploymentChannelList.Values.ToArray();
            // remove listeners
            deleteChannel.Line.GotKeyboardFocus -= Channel_GotKeyboardFocus;
            deleteChannel.Line.LostKeyboardFocus -= Channel_LostKeyboardFocus;
            deleteChannel.Line.KeyDown -= Channel_KeyDown;
            modelHasBeenEdited = true;
        }*/

        private void DeleteChannel(channel deleteChannel) {
            // delete the reference in the source and target components

            if (deploymentComponentList.ContainsKey(deleteChannel.source.component.id)) {
                componentType tempComponent = null;
                tempComponent = deploymentComponentList[deleteChannel.source.component.id];
                ((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).ChannelIds.Remove(deleteChannel.id);
                UpdatePortsToolTips(tempComponent);
            }

            if (deploymentComponentList.ContainsKey(deleteChannel.target.component.id)) {
                componentType tempComponent = null;
                tempComponent = deploymentComponentList[deleteChannel.target.component.id];
                if (deleteChannel.id.StartsWith("group") && (((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).GroupChannelId != "")) { // && !deleteChannel.target.component.id.StartsWith("group")) {
                    ((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).GroupChannelId = "";
                }
                else {
                    ((inputPortType)tempComponent.PortsList[deleteChannel.target.port.id]).ChannelId = "";
                }
                UpdatePortsToolTips(tempComponent);
            }
            
            if (deleteChannel.id.StartsWith("group") && deleteChannel.source.component.id.StartsWith("group")) {
                if (deploymentComponentList.ContainsKey(deleteChannel.source.component.id)) {
                    componentType tempRefComponent = deploymentComponentList[((outputPortType)deploymentComponentList[deleteChannel.source.component.id].PortsList[deleteChannel.source.port.id]).refs.componentID];

                    outputPortType tempOutPort = (outputPortType)tempRefComponent.PortsList[((outputPortType)deploymentComponentList[deleteChannel.source.component.id].PortsList[deleteChannel.source.port.id]).refs.portID];
                    tempOutPort.ChannelIds.Remove(deleteChannel.id);
                }
                //outputPortType tempOutPort = (outputPortType)deploymentComponentList[((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).refs.componentID].PortsList[((outputPortType)tempComponent.PortsList[deleteChannel.source.port.id]).refs.portID];
            }

            foreach (groupComponent gc in groupsList.Values) {
                gc.AddedChannelsList.Remove(deleteChannel);
            }
            
            // delete the channel itself
            canvas.Children.Remove(deleteChannel.Line);
            deploymentChannelList.Remove(deleteChannel.id);
            // copy hashtable to array
            deploymentModel.channels = deploymentChannelList.Values.ToArray();
            // remove listeners
            deleteChannel.Line.GotKeyboardFocus -= Channel_GotKeyboardFocus;
            deleteChannel.Line.LostKeyboardFocus -= Channel_LostKeyboardFocus;
            deleteChannel.Line.KeyDown -= Channel_KeyDown;
            modelHasBeenEdited = true;

            UpdateToolTips();
        }

        /// <summary>
        /// Delete an event channel line between two components and delete all events
        /// between the two components
        /// </summary>
        /// <param name="eventChannelToDelete"></param>
        private void DeleteEventChannelCommand(eventChannelLine eventChannelToDelete) {
            eventChannel eventCh;
            for (int index = eventChannelList.Count - 1; index >= 0; index--) {
                eventCh = (eventChannel)eventChannelList[index];
                if ((eventCh.sources.source.component.id == eventChannelToDelete.TriggerComponentId) && (eventCh.targets.target.component.id == eventChannelToDelete.ListenerComponentId)) {
                    eventChannelList.RemoveAt(index);
                }
                foreach (groupComponent gc in groupsList.Values) {
                    gc.AddedEventChannelsList.Remove(eventChannelToDelete);
                }
            }

            canvas.Children.Remove(eventChannelToDelete.Line);
            eventChannelLinesList.Remove(eventChannelToDelete);
            eventChannelToDelete.Line.GotKeyboardFocus -= EventChannel_GotKeyboardFocus;
            eventChannelToDelete.Line.LostKeyboardFocus -= EventChannel_LostKeyboardFocus;
            eventChannelToDelete.Line.KeyDown -= EventChannel_KeyDown;
            Canvas.SetZIndex(eventChannelToDelete.Line, Canvas.GetZIndex(eventChannelToDelete.Line) - 2000);
            //focusedEventChannel = null;
            //deleteEventChannelRibbonButton.IsEnabled = false;
            deploymentModel.eventChannels = (eventChannel[])eventChannelList.ToArray(typeof(eventChannel));
            ResetPropertyDock();
            modelHasBeenEdited = true;
            UpdateToolTips();
        }


        /// <summary>
        /// Add a new event channel between two components. No events are established between the components at this moment
        /// </summary>
        /// <param name="addEventChannel">The new event channel</param>
        /// <param name="showDialog">Switch, to deactivate the "Connect Events" dialog</param>
        private bool AddEventChannelCommand(eventChannelLine addEventChannel, bool showDialog) {
            // check if connection already exists
            foreach (eventChannelLine el in eventChannelLinesList) {
                if (el.ListenerComponentId == addEventChannel.ListenerComponentId && el.TriggerComponentId == addEventChannel.TriggerComponentId)
                {
                    return false;
                }
            }
            addEventChannel.Line.Focusable = true;
            KeyboardNavigation.SetTabIndex(addEventChannel.Line, canvas.Children.Count + 1);
            addEventChannel.Line.GotKeyboardFocus += EventChannel_GotKeyboardFocus;
            addEventChannel.Line.LostKeyboardFocus += EventChannel_LostKeyboardFocus;
            addEventChannel.Line.KeyDown += EventChannel_KeyDown;
            addEventChannel.Line.ContextMenu = eventChannelContextMenu;
            Canvas.SetZIndex(addEventChannel.Line, Canvas.GetZIndex(addEventChannel.Line) + 2000);
            eventChannelLinesList.Add(addEventChannel);

            if (showEventChannelConnectMessage && showDialog) {
                CustomMessageBox messageBox = new CustomMessageBox(Properties.Resources.AddEventChannelInfo, Properties.Resources.AddEventChannelInfoHeader, CustomMessageBox.messageType.Info, CustomMessageBox.resultType.OK);
                messageBox.Owner = this;
                messageBox.showCheckbox.IsChecked = showEventChannelConnectMessage;
                messageBox.ShowDialog();

                showEventChannelConnectMessage = (bool)messageBox.showCheckbox.IsChecked;
                if (showEventChannelConnectMessage) {
                    ini.IniWriteValue("Options", "showEventChannelConnectMessage", "true");
                }
                else {
                    ini.IniWriteValue("Options", "showEventChannelConnectMessage", "false");
                }
            }
            //MessageBox.Show(Properties.Resources.AddEventChannelInfo, Properties.Resources.AddEventChannelInfoHeader, MessageBoxButton.OK, MessageBoxImage.Information);
            if (deploymentComponentList.ContainsKey(addEventChannel.TriggerComponentId) && deploymentComponentList.ContainsKey(addEventChannel.ListenerComponentId)) {
                SetEventPropertyDock(deploymentComponentList[addEventChannel.TriggerComponentId], deploymentComponentList[addEventChannel.ListenerComponentId]);
            }
            focusedEventChannel = addEventChannel;
            ClearSelectedEventChannelList();
            AddSelectedEventChannel(addEventChannel);
            eventChannel ec = new eventChannel();
            ec.sources.source.component.id = addEventChannel.TriggerComponentId;
            //canvas.Children.Add(addEventChannel.Line);

            UpdateToolTips();
            return true;
        }


        /// <summary>
        /// Check for each property of each component, if this property contains a dynamic string list.
        /// If so, the list with the properties will be updated
        /// This function is called, when the ACS is in status "synchronised"
        /// </summary>
        private void GetAllDynamicProperties() {
            foreach (componentType comp in deploymentComponentList.Values) {
                foreach (propertyType property in comp.PropertyArrayList) {
                    if (property.GetStringList) {
                        try {
                            string oldValue = property.value;
                            property.ComboBoxStrings = asapiClient.getRuntimePropertyList(comp.id, property.name).ToArray<String>();

                            if (property.ComboBoxStrings.Length == 0)
                                property.ComboBoxStrings = new String[] { oldValue };
                            //property.ComboBoxStrings = new String[] { "eins", "zwei", "drei" };
                        } catch (Exception ex) {
                            MessageBox.Show(Properties.Resources.SetPropertyErrorDialog, Properties.Resources.SetPropertyErrorDialogHeader, MessageBoxButton.OK, MessageBoxImage.Error);
                            traceSource.TraceEvent(TraceEventType.Error, 3, ex.Message);
                        }
                    }
                }
            }
        }


        #endregion // Commands

        #region Accessors
        // Accessors for internal Properties

        /// <summary>
        /// Accessor for the modelHasBeedEdited bit, used by the ModelVersionUpdater
        /// </summary>
        public bool ModelHasBeenEdited {
            get { return modelHasBeenEdited; }
            set { modelHasBeenEdited = value; }
        }

        /// <summary>
        /// Accessor for the FocusedComponent, used by the Oscilloscope
        /// </summary>
        //public componentType FocusedComponent {
        //    get { return focusedComponent; }
        //    set { focusedComponent = value; }
        //}

        /// <summary>
        /// Accessor for the ini-File. Used by the options-dialog
        /// </summary>
        public IniFile Ini {
            get { return ini; }
            set { ini = value; }
        }

        /// <summary>
        /// Accessor for showNamingDialogOnComponentInsert. Used by the options-dialog
        /// </summary>
        public bool ShowNamingDialogOnComponentInsert {
            get { return showNamingDialogOnComponentInsert; }
            set { showNamingDialogOnComponentInsert = value; }
        }

        /// <summary>
        /// Accessor for showHostPortDialogOnConnect. Used by the options-dialog
        /// </summary>
        public bool ShowHostPortDialogOnConnect {
            get { return showHostPortDialogOnConnect; }
            set { showHostPortDialogOnConnect = value; }
        }

        /// <summary>
        /// Accessor for showEventChannelConnectMessage. Used by the options-dialog
        /// </summary>
        public bool ShowEventChannelConnectMessage {
            get { return showEventChannelConnectMessage; }
            set { showEventChannelConnectMessage = value; }
        }

        #endregion

        private void dispatcherHelperRibbonButton_IsEnabledChanged(object sender, DependencyPropertyChangedEventArgs e) {
            if (dispatcherHelperRibbonButton.IsEnabled == false) {
                areStatus.Status = AREStatus.ConnectionStatus.Disconnected;
            }
        }

    }

}

