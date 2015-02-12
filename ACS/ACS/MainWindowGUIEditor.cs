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
 * Filename: MainWindowGUIEditor.xaml.cs
 * Class(es):
 *   Classname: MainWindow
 *   Description: Initialisation and functions for the GUI editor
 * Author: Roland Ossmann
 * Date: 08.09.2011
 * Version: 0.1
 * Comment: Partial class of MainWindow, other parts of this class in file
 *   MainWindow.xaml and MainWondow.xaml.cs
 * --------------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows;
using AvalonDock;
using System.Windows.Media.Imaging;

namespace Asterics.ACS {

    /// <summary>
    /// Initialisation and functions for the GUI editor
    /// </summary>
    public partial class MainWindow {

        private int guiOffsetX = 0;
        private int guiOffsetY = 0;
        private componentType guiSelectedModelComponent = null;
        private Canvas guiCanvas;
        private Canvas guiSelectedCanvas;
        private bool guiElementResize = false;
        private Rectangle guiSelectedRectangle;
        private ScrollViewer guiScrollViewer;
        private DocumentContent GUIEditorCanvas;
        private GUIProperties guiProp;
        private Canvas guiGridCanvas;
        private Border guiCanvasBorder;
        private componentType modelCompGUIARE;

        private ContextMenu gUIComponentContextMenu;
        private MenuItem gUIComponentContextMenuResize;
        private MenuItem gUIComponentContextMenuResizeStop;
        private MenuItem gUIComponentContextMenuMove;
        private MenuItem gUIComponentContextMenuMoveStop;

        // The max X and Y coordinate of components within the ARE window
        // Used to define the minimal width and height of the ARE window
        private int guiComponentsMaxX = 0;
        private int guiComponentsMaxY = 0;

        private const int GUIFRAMEWIDTH = 800;
        private const int GUIFRAMEHEIGHTFIVEFOUR = 480;
        private const int GUIFRAMEHEIGHTFOURTHREE = 600;
        private const int GUIFRAMEHEIGHTSIXTEENNINE = 450;

        private Rectangle controlRect;
        
        private Rectangle decorationCanvas;
        private TextBlock controlTextBlock;

        private int areBottomMargin = 0;
        private int areRightMargin = 0;
        
        private int decorationHeight = 20;

        #region init

        /// <summary>
        /// Initialication of the GUIEditor Tab, creating the context menu for the GUIEditor elements
        /// </summary>
        private void InitGUITab() {

            // Context menu
            gUIComponentContextMenu = new ContextMenu();
            gUIComponentContextMenuResize = new MenuItem();
            gUIComponentContextMenuResize.Header = Properties.Resources.GUIEditorKeyboardResize;
            gUIComponentContextMenu.Items.Add(gUIComponentContextMenuResize);
            gUIComponentContextMenuResize.Click += gUIComponentContextMenuResize_Click;

            gUIComponentContextMenuResizeStop = new MenuItem();
            gUIComponentContextMenuResizeStop.Header = Properties.Resources.GUIEditorKeyboardResizeStop;
            gUIComponentContextMenuResizeStop.IsEnabled = false;
            gUIComponentContextMenu.Items.Add(gUIComponentContextMenuResizeStop);
            gUIComponentContextMenuResizeStop.Click += gUIComponentContextMenuResizeStop_Click;

            gUIComponentContextMenu.Items.Add(new Separator());
            gUIComponentContextMenuMove = new MenuItem();
            gUIComponentContextMenuMove.Header = Properties.Resources.GUIEditorKeyboardMove;
            gUIComponentContextMenu.Items.Add(gUIComponentContextMenuMove);
            gUIComponentContextMenuMove.Click += gUIComponentContextMenuMove_Click;

            gUIComponentContextMenuMoveStop = new MenuItem();
            gUIComponentContextMenuMoveStop.Header = Properties.Resources.GUIEditorKeyboardMoveStop;
            gUIComponentContextMenuMoveStop.IsEnabled = false;
            gUIComponentContextMenu.Items.Add(gUIComponentContextMenuMoveStop);
            gUIComponentContextMenuMoveStop.Click += gUIComponentContextMenuMoveStop_Click;

            // creating the tab for the gui designer
            DockPanel guiDockPanel = new DockPanel();
            guiDockPanel.LastChildFill = true;

            guiScrollViewer = new ScrollViewer();
            guiScrollViewer.VerticalScrollBarVisibility = ScrollBarVisibility.Auto;
            guiScrollViewer.HorizontalScrollBarVisibility = ScrollBarVisibility.Auto;
            guiDockPanel.Children.Add(guiScrollViewer);

            guiCanvas = new Canvas();
            InitGUICanvas();
            guiCanvasBorder = new Border();
            guiCanvasBorder.BorderThickness = new Thickness(2);
            guiCanvasBorder.BorderBrush = Brushes.DarkGray;
            guiCanvasBorder.Width = guiCanvas.Width + 4;
            guiCanvasBorder.Height = guiCanvas.Height + 4;
            guiCanvasBorder.Child = guiCanvas;
            guiScrollViewer.Content = guiCanvasBorder;
            GUIEditorCanvas = new DocumentContent() {
                Title = Properties.Resources.DockGUIWindow,
                Content = guiDockPanel,
                Name = "dockGUIEditorWindowName"
            };
            GUIEditorCanvas.IsCloseable = false;
            GUIEditorCanvas.IsActiveDocumentChanged += GUIEditorCanvas_IsActiveDocumentChanged;
            GUIEditorCanvas.Show(dockManager);

            guiProp = new GUIProperties();
            guiProp.PropertyChanged += guiProp_PropertyChanged;

            // Adapt screen resolution. Screen width is 800
            switch (guiProp.ScreenRes) {
                case GUIProperties.ScreenResolution.FiveFour:
                    guiCanvas.Height = GUIFRAMEHEIGHTFIVEFOUR;
                    break;
                case GUIProperties.ScreenResolution.FourThree:
                    guiCanvas.Height = GUIFRAMEHEIGHTFOURTHREE;
                    break;
                case GUIProperties.ScreenResolution.SixteenNine:
                    guiCanvas.Height = GUIFRAMEHEIGHTSIXTEENNINE;
                    break;
            }

            guiGridCanvas = new Canvas();
            guiGridCanvas.Width = guiCanvas.Width;
            guiGridCanvas.Height = guiCanvas.Height;
            DrawGridLines();
            guiCanvas.Children.Add(guiGridCanvas);
            if (guiProp.ShowGrid == true) {
                guiGridCanvas.Visibility = System.Windows.Visibility.Visible;
            }
            else {
                guiGridCanvas.Visibility = System.Windows.Visibility.Hidden;
            }

            AddAREGUIComponent();
        }

        /// <summary>
        /// Property changed listener for the guiProp-Object. Properties of the GUI editor and the ARE window are saved within this object
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void guiProp_PropertyChanged(object sender, System.ComponentModel.PropertyChangedEventArgs e) {
            if (e.PropertyName.Equals("showGrid")) {
                if (guiProp.ShowGrid == true) {  
                    guiGridCanvas.Visibility = System.Windows.Visibility.Visible;
                }
                else {
                    guiGridCanvas.Visibility = System.Windows.Visibility.Hidden;
                }
            }
            else if (e.PropertyName.Equals("step")) {
                DrawGridLines();
            }
            else if (e.PropertyName.Equals("screenRes")) {
                // Adapt screen resolution. Screen width is 800
                switch (guiProp.ScreenRes) {
                    case GUIProperties.ScreenResolution.FiveFour:
                        guiCanvas.Height = GUIFRAMEHEIGHTFIVEFOUR;
                        break;
                    case GUIProperties.ScreenResolution.FourThree:
                        guiCanvas.Height = GUIFRAMEHEIGHTFOURTHREE;
                        break;
                    case GUIProperties.ScreenResolution.SixteenNine:
                        guiCanvas.Height = GUIFRAMEHEIGHTSIXTEENNINE;
                        break;
                }
                guiGridCanvas.Height = guiCanvas.Height;
                DrawGridLines();
                guiCanvasBorder.Height = guiCanvas.Height + 4;
            }
            else if (e.PropertyName.Equals("decoration")) {
                deploymentModel.modelGUI.Decoration = guiProp.Decoration;
                if (guiProp.Decoration == true) {
                    decorationCanvas.Visibility = System.Windows.Visibility.Visible;
                } else
                    decorationCanvas.Visibility = System.Windows.Visibility.Hidden;
            }
            else if (e.PropertyName.Equals("fullscreen")) {
                deploymentModel.modelGUI.Fullscreen = guiProp.Fullscreen;
            }
            else if (e.PropertyName.Equals("alwaysOnTop")) {
                deploymentModel.modelGUI.AlwaysOnTop = guiProp.AlwaysOnTop;
            }
            else if (e.PropertyName.Equals("toSystemTray")) {
                deploymentModel.modelGUI.ToSystemTray = guiProp.ToSystemTray;
            }
            else if (e.PropertyName.Equals("showControlPanel")) {
                deploymentModel.modelGUI.ShopControlPanel = guiProp.ShowControlPanel;
                if (guiProp.ShowControlPanel) {
                    areRightMargin = 0;
                    controlRect.Visibility = System.Windows.Visibility.Visible;
                    
                    controlTextBlock.Visibility = System.Windows.Visibility.Visible;
                } else {
                    areRightMargin = 0;
                    controlRect.Visibility = System.Windows.Visibility.Hidden;
                    controlTextBlock.Visibility = System.Windows.Visibility.Hidden;
                }
            }
        }

        /// <summary>
        /// Draws the grid lines on the GUI editor
        /// </summary>
        private void DrawGridLines() {
            guiGridCanvas.Children.Clear();
            for (int i = (int)guiProp.GridSteps; i < guiGridCanvas.Width; i += (int)guiProp.GridSteps) {
                Line l = new Line();
                l.X1 = l.X2 = i;
                l.Y1 = 0;
                l.Y2 = guiGridCanvas.Height;
                l.Stroke = System.Windows.Media.Brushes.LightGray;
                guiGridCanvas.Children.Add(l);
            }
            for (int i = (int)guiProp.GridSteps; i < guiGridCanvas.Height; i += (int)guiProp.GridSteps) {
                Line l = new Line();
                l.Y1 = l.Y2 = i;
                l.X1 = 0;
                l.X2 = guiGridCanvas.Width;
                l.Stroke = System.Windows.Media.Brushes.LightGray;
                guiGridCanvas.Children.Add(l);
            }
        }

        /// <summary>
        /// Setting the GUI property as active when the editor is switched to GUIEditor
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void GUIEditorCanvas_IsActiveDocumentChanged(object sender, EventArgs e) {

            WPG.PropertyGrid pe = new WPG.PropertyGrid();
            pe.Instance = guiProp;
            pe.DisplayName = "GUI Properties";
            pe.ShowPreview = false;

            if (dockableComponentProperties.ContainerPane.ActualWidth > 15) {
                pe.Width = dockableComponentProperties.ContainerPane.ActualWidth - 15;
            }
            if (dockableComponentProperties.ContainerPane.ActualHeight > 48) {
                pe.Height = dockableComponentProperties.ContainerPane.ActualHeight - 48;
            }
            propertyDockScrollViewer.Content = pe;
        }


        /// <summary>
        /// Initialisation of the GUICanvas (the drawing area for the GUI elements)
        /// </summary>
        private void InitGUICanvas() {
            guiCanvas.Width = GUIFRAMEWIDTH;
            guiCanvas.Height = GUIFRAMEHEIGHTSIXTEENNINE;
            guiCanvas.Background = Brushes.White;
            guiCanvas.Focusable = true;
            // Adding mouse listeners to the drawing canvas
            guiCanvas.MouseLeftButtonDown += OnLeftDownGUI;
            guiCanvas.MouseLeftButtonUp += OnLeftUpGUI;
            guiCanvas.MouseMove += OnMouseMoveGUI;
            //KeyboardNavigation.SetIsTabStop(guiCanvas, true);
            KeyboardNavigation.SetTabNavigation(guiCanvas, KeyboardNavigationMode.Cycle);
            guiCanvas.KeyDown += new KeyEventHandler(guiCanvas_KeyDown);
        }

        /// <summary>
        /// Adding the ARE window to the GUI editor
        /// </summary>
        private void AddAREGUIComponent() {
            modelCompGUIARE = new componentType();
            modelCompGUIARE.id = "ARE_GUI_WINDOW";
            modelCompGUIARE.gui = new guiType();
            if (deploymentModel.modelGUI == null) {
                NewAREGUIWindow();
            }
            modelCompGUIARE.gui.width = deploymentModel.modelGUI.AREGUIWindow.width;
            modelCompGUIARE.gui.height = deploymentModel.modelGUI.AREGUIWindow.height;
            modelCompGUIARE.gui.posX = deploymentModel.modelGUI.AREGUIWindow.posX;
            modelCompGUIARE.gui.posY = deploymentModel.modelGUI.AREGUIWindow.posY;
            AddGUIComponent(modelCompGUIARE);
            guiProp.Decoration = deploymentModel.modelGUI.Decoration;
            guiProp.Fullscreen = deploymentModel.modelGUI.Fullscreen;
            guiProp.AlwaysOnTop = deploymentModel.modelGUI.AlwaysOnTop;
            guiProp.ToSystemTray = deploymentModel.modelGUI.ToSystemTray;
            guiProp.ShowControlPanel = deploymentModel.modelGUI.ShopControlPanel;
        }

        #endregion

        #region GUI Operations

        /// <summary>
        /// Deleting all elements on the GUI Canvas
        /// </summary>
        private void CleanGUICanvas() {
            guiCanvas.Children.Clear();
            guiCanvas.Children.Add(guiGridCanvas);

            AddAREGUIComponent();
        }

        /// <summary>
        /// Creating a new GUI element and placing it on the GUI canvas
        /// GUI elements are positioned and measured in percentage, not in pixel
        /// </summary>
        /// <param name="modelComp">Component, which is represented by the GUI element</param>
        public void AddGUIComponent(componentType modelComp) {
            foreach (propertyType p in modelComp.properties)
            {
              //  Console.Out.WriteLine("hello:" + p.name + " " + p.value);
                if ((p.name.ToLower().Equals("displaygui")) && (p.value.ToLower().Equals("false")))
                    return;
            }

            Canvas guiComponentCanvas = new Canvas();
            Rectangle guiComponent = new Rectangle();

            guiComponentCanvas.Width = guiCanvas.Width * int.Parse(modelComp.gui.width) / 10000;
            guiComponentCanvas.Height = guiCanvas.Height * int.Parse(modelComp.gui.height) / 10000;
            guiComponentCanvas.Name = "guiCanvas";

            guiCanvas.Children.Add(guiComponentCanvas);
            KeyboardNavigation.SetTabIndex(guiComponentCanvas, guiCanvas.Children.Count + 1);

            guiComponent.Height = guiComponentCanvas.Height;
            guiComponent.Width = guiComponentCanvas.Width;
            guiComponentCanvas.Focusable = true;
            KeyboardNavigation.SetIsTabStop(guiComponentCanvas, true);
            //KeyboardNavigation.SetTabNavigation(guiComponentCanvas, KeyboardNavigationMode.Cycle);

            guiComponentCanvas.GotKeyboardFocus += guiComponentCanvas_GotKeyboardFocus;
            guiComponentCanvas.LostKeyboardFocus += guiComponentCanvas_LostKeyboardFocus;
            guiComponentCanvas.Children.Add(guiComponent);

            guiComponent.Stroke = Brushes.Black;
            BrushConverter bc = new BrushConverter();
            if (modelComp.gui.IsExternalGUIElement) {
                guiComponent.Fill = (Brush)bc.ConvertFrom("#99084ECC"); // External GUI Element: blue
            }
            else {
                guiComponent.Fill = (Brush)bc.ConvertFrom("#99E21616"); // Internal GUI Elment: red     gray: #12121212
            }
            //guiComponent.Focusable = true;
            guiComponent.RadiusX = 2;
            guiComponent.RadiusY = 2;
            Canvas.SetLeft(guiComponentCanvas, guiCanvas.Width * int.Parse(modelComp.gui.posX) / 10000);
            Canvas.SetTop(guiComponentCanvas, guiCanvas.Height * int.Parse(modelComp.gui.posY) / 10000);
            Canvas.SetLeft(guiComponent, 0);
            Canvas.SetTop(guiComponent, 0);
            TextBlock textblockLabel = new TextBlock();
            textblockLabel.Text = modelComp.id;
            textblockLabel.Width = guiComponentCanvas.Width;
            textblockLabel.TextAlignment = System.Windows.TextAlignment.Center;
            textblockLabel.TextWrapping = System.Windows.TextWrapping.Wrap;
            guiComponentCanvas.Children.Add(textblockLabel);
            Canvas.SetLeft(textblockLabel, 5);
            Canvas.SetTop(textblockLabel, guiComponentCanvas.Height / 2 - 10);
            modelComp.gui.GuiElementCanvas = guiComponentCanvas;

            if (modelComp.id.Equals(modelCompGUIARE.id)) {
                // add Right Control area
                controlRect = new Rectangle();
                SolidColorBrush scb = new SolidColorBrush();
                scb.Color = Color.FromArgb(128, 120, 120, 120);
                controlRect.Fill = scb;
                controlRect.Width = 38;
                
                controlRect.Height = guiComponentCanvas.Height;
                Canvas.SetRight(controlRect, 0);
                modelCompGUIARE.gui.GuiElementCanvas.Children.Add(controlRect);

                controlTextBlock = new TextBlock();
                controlTextBlock.Text = "Control";
                controlTextBlock.FontSize = 9;
                Canvas.SetRight(controlTextBlock, 4);
                Canvas.SetTop(controlTextBlock, guiComponentCanvas.Height / 2);
                
                modelCompGUIARE.gui.GuiElementCanvas.Children.Add(controlTextBlock);

                decorationCanvas = new Rectangle();
                ImageBrush ib = new ImageBrush();
                try {
                    if (System.IO.File.Exists(@"images\are_deco.png")) {
                        Uri uri = new Uri(@"images\are_deco.png", UriKind.Relative);
                        ib.ImageSource = new BitmapImage(uri);
                        decorationCanvas.Fill = ib;
                    }
                
                }
                catch (Exception) {
                }
                decorationCanvas.Width = guiComponentCanvas.Width-2;
                decorationCanvas.Height = decorationHeight;
                modelCompGUIARE.gui.GuiElementCanvas.Children.Add(decorationCanvas);
                Canvas.SetLeft(decorationCanvas, 1);
                Canvas.SetTop(decorationCanvas, 1);
            }

            // Small black rectangle, indicating the resize possibility of a component
            Polygon moveSizeTriangle = new Polygon();
            PointCollection trianglePointCollection = new PointCollection();
            trianglePointCollection.Add(new Point(0, 15));
            trianglePointCollection.Add(new Point(15, 15));
            trianglePointCollection.Add(new Point(15, 0));
            moveSizeTriangle.Points = trianglePointCollection;
            moveSizeTriangle.Fill = Brushes.Black;
            moveSizeTriangle.MouseEnter += moveSizeTriangle_MouseEnter;
            moveSizeTriangle.MouseLeave += moveSizeTriangle_MouseLeave;
            guiComponentCanvas.Children.Add(moveSizeTriangle);
            Canvas.SetRight(moveSizeTriangle, 1);
            Canvas.SetBottom(moveSizeTriangle, 1);
            guiComponentCanvas.ContextMenu = gUIComponentContextMenu;
            guiComponentCanvas.KeyDown += guiComponent_KeyDown;
            guiComponentCanvas.KeyUp += guiComponent_KeyUp;

            // special condition for the ARE GUI window
            if (modelComp.id.Equals(modelCompGUIARE.id)) {
                textblockLabel.Text = "ARE";
                guiComponentCanvas.Name = "guiCanvasARE";
                guiComponent.Fill = (Brush)bc.ConvertFrom("#88E2E6E6");
            }
            else {
                // post new GUI element within the ARE window, if it is not an independent GUI window (like the webcam window)
                // this code will only be executed, if the canvas has the cooridinates (0,0), meaning it is a new element and not loaded
                if ((deploymentModel.modelGUI != null) && !modelComp.gui.IsExternalGUIElement && (Canvas.GetLeft(guiComponentCanvas) == 0) && (Canvas.GetTop(guiComponentCanvas) == 0)) {
                    Canvas.SetLeft(guiComponentCanvas, Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas));
                    if (guiProp.Decoration) {
                        Canvas.SetTop(guiComponentCanvas, Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas) + decorationHeight);
                        modelComp.gui.posY = Convert.ToInt16((Canvas.GetTop(guiComponentCanvas)) / guiCanvas.Height * 10000).ToString();
                    }
                    else
                        Canvas.SetTop(guiComponentCanvas, Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas));

                    if (modelCompGUIARE.gui.GuiElementCanvas.Width <= (guiComponentCanvas.Width - areRightMargin)) {
                        guiComponentCanvas.Width = modelCompGUIARE.gui.GuiElementCanvas.Width - areRightMargin;
                        ((Rectangle)modelComp.gui.GuiElementCanvas.Children[0]).Width = guiComponentCanvas.Width;
                    }
                    if (modelCompGUIARE.gui.GuiElementCanvas.Height <= (guiComponentCanvas.Height + areBottomMargin)) {
                        guiComponentCanvas.Height = modelCompGUIARE.gui.GuiElementCanvas.Height - areBottomMargin;
                        ((Rectangle)modelComp.gui.GuiElementCanvas.Children[0]).Height = guiComponentCanvas.Height;
                    }
                    Canvas.SetLeft(textblockLabel, 5);
                    Canvas.SetTop(textblockLabel, (guiComponentCanvas.Height / 2 - 10));
                }
            }
            CalcMaxXandYofGUI();
            guiComponentCanvas.ToolTip = modelComp.id + " (" + modelComp.type_id + ")" + "\n" + modelComp.description;
        }

        /// <summary>
        /// Removing one element from the GUI canvas
        /// </summary>
        /// <param name="modelComp">Component which contains the GUI element that should be removed</param>
        private void RemoveGUIComponent(componentType modelComp) {
            guiCanvas.Children.Remove(modelComp.gui.GuiElementCanvas);
            CalcMaxXandYofGUI();
        }


        /// <summary>
        /// Calculate the max X and Y cooridnates of the components within the ARE window. Used the define the min size of the ARE window
        /// </summary>
        private void CalcMaxXandYofGUI() {
            guiComponentsMaxX = 0;
            guiComponentsMaxY = 0;
            foreach (Canvas c in guiCanvas.Children) {
                if (c.Name == "guiCanvas") {
                    // find parent component to the canvas
                    foreach (componentType tempComponent in deploymentComponentList.Values) {
                        if ((tempComponent.gui != null) && (tempComponent.gui.GuiElementCanvas == c)) {
                            // calculate max x and y point, if the componentGUI is not an external GUI element
                            if (!tempComponent.gui.IsExternalGUIElement) {
                                if ((Canvas.GetLeft(c) + c.Width - Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas)) > guiComponentsMaxX) {
                                    guiComponentsMaxX = (int)Canvas.GetLeft(c) + (int)c.Width - (int)Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas);
                                }
                                if ((Canvas.GetTop(c) + c.Height - Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas)) > guiComponentsMaxY) {
                                    guiComponentsMaxY = (int)Canvas.GetTop(c) + (int)c.Height - (int)Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas);
                                }
                            }
                            break;
                        }
                    }

                }
            }
        }

        #endregion

        #region eventheldlers

        /// <summary>
        /// Keyboard event listener. Needed to set focus on the first element
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void guiCanvas_KeyDown(object sender, KeyEventArgs e) {
            if (e.Key == Key.Tab) {
                Keyboard.Focus(guiCanvas.Children[0]);
                e.Handled = true;
            }
        }

        /// <summary>
        /// Keyboard event listener. Used to move or resize the selected element by keyboard.
        /// Furthermore, focus wil be moved using the 'Tab'-key
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void guiComponent_KeyDown(object sender, KeyEventArgs e) {
            Canvas selectedGUIElement = (Canvas)sender;
            Rectangle selectedGUIRectangle = (Rectangle)selectedGUIElement.Children[0];
            Rectangle focusGUIRectangle = selectedGUIRectangle;
            if (selectedGUIElement.Children[selectedGUIElement.Children.Count - 1] is Rectangle) {
                focusGUIRectangle = (Rectangle)selectedGUIElement.Children[selectedGUIElement.Children.Count - 1];
            }
            if (e.Key == Key.Tab) {
                e.Handled = true;
                int focusCount = 0;
                foreach (Canvas c in guiCanvas.Children) {
                    if (c == selectedGUIElement) {
                        break;
                    }
                    focusCount++;
                }
                if (focusCount >= guiCanvas.Children.Count - 1) {
                    Keyboard.Focus(guiCanvas.Children[0]);
                }
                else {
                    Keyboard.Focus(guiCanvas.Children[focusCount + 1]);
                }

            }
            if (gUIComponentContextMenuResizeStop.IsEnabled) {
                e.Handled = true;
                if (e.Key == Key.Left) {
                    if (selectedGUIElement.Width > 15) {
                        selectedGUIElement.Width--;
                        selectedGUIRectangle.Width--;
                        focusGUIRectangle.Width--;
                    }
                }
                else if (e.Key == Key.Right) {
                //    if ((selectedGUIElement.Width + Canvas.GetLeft(selectedGUIElement)) < guiCanvas.Width) {
                        selectedGUIElement.Width++;
                        selectedGUIRectangle.Width++;
                        focusGUIRectangle.Width++;
                //    }
                }
                else if (e.Key == Key.Up) {
                    if (selectedGUIElement.Height > 15) {
                        selectedGUIElement.Height--;
                        selectedGUIRectangle.Height--;
                        focusGUIRectangle.Height--;
                    }
                }
                else if (e.Key == Key.Down) {
                 //   if ((selectedGUIElement.Height + Canvas.GetTop(selectedGUIElement)) < guiCanvas.Height) {
                        selectedGUIElement.Height++;
                        selectedGUIRectangle.Height++;
                        focusGUIRectangle.Height++;
                 //   }
                }
            }
            else if (gUIComponentContextMenuMoveStop.IsEnabled) {
                e.Handled = true;
                if (e.Key == Key.Left) {
                  //  if (Canvas.GetLeft(selectedGUIElement) > 0) {
                        Canvas.SetLeft(selectedGUIElement, Canvas.GetLeft(selectedGUIElement) - 1);
                  //  }
                }
                else if (e.Key == Key.Right) {
                 //   if ((Canvas.GetLeft(selectedGUIElement) + selectedGUIElement.Width) < guiCanvas.Width) {
                        Canvas.SetLeft(selectedGUIElement, Canvas.GetLeft(selectedGUIElement) + 1);
                 //   }
                }
                else if (e.Key == Key.Up) {
                 //   if (Canvas.GetTop(selectedGUIElement) > 0) {
                        Canvas.SetTop(selectedGUIElement, Canvas.GetTop(selectedGUIElement) - 1);
                 //   }
                }
                else if (e.Key == Key.Down) {
                 //   if ((Canvas.GetTop(selectedGUIElement) + selectedGUIElement.Height) < guiCanvas.Height) {
                        Canvas.SetTop(selectedGUIElement, Canvas.GetTop(selectedGUIElement) + 1);
                 //   }
                }
            }
        }

        /// <summary>
        /// Keyboard listner to react on the 'App'-key
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void guiComponent_KeyUp(object sender, KeyEventArgs e) {
            if (e.Key == Key.Apps) {

                e.Handled = true;
                Canvas selectedGUIElement = (Canvas)sender;

                foreach (componentType tempComponent in deploymentComponentList.Values) {
                    if ((tempComponent.gui != null) && (tempComponent.gui.GuiElementCanvas == selectedGUIElement)) {
                        guiSelectedModelComponent = tempComponent;
                        break;
                    }
                }

                selectedGUIElement.ContextMenu.PlacementTarget = selectedGUIElement.Children[0];
                selectedGUIElement.ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Center;
                selectedGUIElement.ContextMenu.IsOpen = true;
            }
        }

        /// <summary>
        /// Context menu event listener
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void gUIComponentContextMenuMoveStop_Click(object sender, RoutedEventArgs e) {
            gUIComponentContextMenuMove.IsEnabled = true;
            gUIComponentContextMenuMoveStop.IsEnabled = false;
        }

        /// <summary>
        /// Context menu event listener
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void gUIComponentContextMenuMove_Click(object sender, RoutedEventArgs e) {
            gUIComponentContextMenuMove.IsEnabled = false;
            gUIComponentContextMenuMoveStop.IsEnabled = true;
        }

        /// <summary>
        /// Context menu event listener
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void gUIComponentContextMenuResizeStop_Click(object sender, RoutedEventArgs e) {
            gUIComponentContextMenuResize.IsEnabled = true;
            gUIComponentContextMenuResizeStop.IsEnabled = false;
        }

        /// <summary>
        /// Context menu event listener
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        void gUIComponentContextMenuResize_Click(object sender, RoutedEventArgs e) {
            gUIComponentContextMenuResize.IsEnabled = false;
            gUIComponentContextMenuResizeStop.IsEnabled = true;
        }

        /// <summary>
        /// Focus listner: if an element lost its focus, the focus frame will be removed and the new values
        /// will be writen
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void guiComponentCanvas_LostKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e) {
            Canvas focusCanvas = (Canvas)sender;
            foreach (UIElement uie in focusCanvas.Children) {
                if ((uie is Rectangle) && (((Rectangle)uie).Name.Equals("keyboardFocusRectangle"))) {
                    focusCanvas.Children.Remove((Rectangle)uie);
                    break;
                }
            }
            if (gUIComponentContextMenuResizeStop.IsEnabled) {
                gUIComponentContextMenuResizeStop.IsEnabled = false;
                gUIComponentContextMenuResize.IsEnabled = true;
                foreach (UIElement uie in focusCanvas.Children) {
                    if (uie is TextBlock) {
                        ((TextBlock)uie).Width = focusCanvas.Width;
                        Canvas.SetTop(uie, focusCanvas.Height / 2 - 10);
                        break;
                    }
                }
                // write new size to selected component
                guiSelectedModelComponent.gui.width = Convert.ToInt16(focusCanvas.Width / guiCanvas.Width * 10000).ToString();
                guiSelectedModelComponent.gui.height = Convert.ToInt16(focusCanvas.Height / guiCanvas.Height * 10000).ToString();
                modelHasBeenEdited = true;
            }
            if (gUIComponentContextMenuMoveStop.IsEnabled) {
                gUIComponentContextMenuMoveStop.IsEnabled = false;
                gUIComponentContextMenuMove.IsEnabled = true;
                // write new coordinates to the selected component
                guiSelectedModelComponent.gui.posX = Convert.ToInt16((Canvas.GetLeft(focusCanvas)) / guiCanvas.Width * 10000).ToString();
                guiSelectedModelComponent.gui.posY = Convert.ToInt16((Canvas.GetTop(focusCanvas)) / guiCanvas.Height * 10000).ToString();
                modelHasBeenEdited = true;
            }
        }

        /// <summary>
        /// Focus listener: the foucs frame around the active element will be set
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void guiComponentCanvas_GotKeyboardFocus(object sender, KeyboardFocusChangedEventArgs e) {
            Canvas focusCanvas = (Canvas)sender;

            Rectangle cr = new Rectangle();
            cr.Stroke = new SolidColorBrush(Colors.Blue);
            cr.StrokeThickness = 2;
            DoubleCollection dashes = new DoubleCollection();
            //dashes.Add(1.0000001);
            //dashes.Add(2.0000001);
            //cr.StrokeDashArray = dashes;
            cr.Width = focusCanvas.Width + 4;
            cr.Height = focusCanvas.Height + 4;
            focusCanvas.Children.Add(cr);
            Canvas.SetTop(cr, -2);
            Canvas.SetLeft(cr, -2);
            cr.Name = "keyboardFocusRectangle";
        }


        /// <summary>
        /// Set cursor to arrow cursor, when the mouse is over the 'change size' rectangle
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void moveSizeTriangle_MouseEnter(object sender, MouseEventArgs e) {
            this.Cursor = Cursors.SizeNWSE;
        }

        /// <summary>
        /// Reset mouse cursor to standard cursor
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void moveSizeTriangle_MouseLeave(object sender, MouseEventArgs e) {
            this.Cursor = Cursors.Arrow;
        }

        /// <summary>
        /// Left mouse button down on the GUICanvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnLeftDownGUI(object sender, MouseEventArgs args) {

            if (args.Source is Rectangle) {
                guiSelectedCanvas = (Canvas)((Rectangle)args.Source).Parent;
            }
            else if (args.Source is TextBlock) {
                guiSelectedCanvas = (Canvas)((TextBlock)args.Source).Parent;
            }
            else if (args.Source is Polygon) {
                guiSelectedCanvas = (Canvas)((Polygon)args.Source).Parent;
                guiElementResize = true;
            }
            if (guiSelectedCanvas != null) {
                // special condition, if mouse is pressed over the ARE element
                if (guiSelectedCanvas.Name.Equals("guiCanvasARE")) {
                    guiSelectedModelComponent = modelCompGUIARE;
                    foreach (UIElement uie in guiSelectedCanvas.Children) {
                        if (uie is Rectangle) {
                            guiSelectedRectangle = (Rectangle)uie;
                            break;
                        }
                    }
                }
                else {
                    // search the rectangle, selected when mouse button is pressed
                    foreach (componentType tempComponent in deploymentComponentList.Values) {
                        if ((tempComponent.gui != null) && (tempComponent.gui.GuiElementCanvas == guiSelectedCanvas)) {
                            guiSelectedModelComponent = tempComponent;
                            foreach (UIElement uie in guiSelectedCanvas.Children) {
                                if (uie is Rectangle) {
                                    guiSelectedRectangle = (Rectangle)uie;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }

                guiOffsetX = (int)args.GetPosition(guiSelectedCanvas).X;
                guiOffsetY = (int)args.GetPosition(guiSelectedCanvas).Y;

                if (guiCanvas.Children.Count > 0) {
                    Keyboard.Focus(guiCanvas.Children[0]);
                }
            }
        }

        /// <summary>
        /// Left mouse button up on the GUICanvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnLeftUpGUI(object sender, MouseEventArgs args) {
            if (guiSelectedModelComponent != null && guiSelectedCanvas != null) {
                foreach (UIElement uie in guiSelectedCanvas.Children) {
                    if (uie is TextBlock) {
                        ((TextBlock)uie).Width = guiSelectedCanvas.Width;
                        Canvas.SetTop(uie, guiSelectedCanvas.Height / 2 - 10);
                        break;
                    }
                }
                guiSelectedModelComponent = null;
                guiSelectedCanvas = null;
                guiElementResize = false;
            }
            CalcMaxXandYofGUI();

        }

        /// <summary>
        /// Mouse move on the GUICanvas
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="args"></param>
        private void OnMouseMoveGUI(object sender, MouseEventArgs args) {
            if ((guiSelectedModelComponent != null) && (guiSelectedCanvas != null) && (Mouse.LeftButton == MouseButtonState.Pressed)) {
                if (!guiElementResize) { // move object
                    int intNewXpos = (int)args.GetPosition(guiCanvas).X;
                    int intNewYpos = (int)args.GetPosition(guiCanvas).Y;

                    double deltaX;
                    double deltaY;

                    /*
                    // check, if the x-cooridnate is within the canvas
                    if (guiSelectedModelComponent.id == modelCompGUIARE.id || guiSelectedModelComponent.gui.IsExternalGUIElement) { // or an GUI element without the ARE, e.g. the webcam. These objects can move on the GUI canvas
                        if ((intNewXpos - guiOffsetX) < 1) {
                            intNewXpos = guiOffsetX;
                        }
                        else if ((intNewXpos - guiOffsetX + guiSelectedRectangle.Width) > guiCanvas.Width) { // original Code, move elements within guiCanvas, not ARE
                            intNewXpos = Convert.ToInt16(guiCanvas.Width - guiSelectedRectangle.Width) + guiOffsetX;
                        }
                    }
                    else { // move only within the ARE GUI element
                        if ((intNewXpos - guiOffsetX) < Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas)) {
                            intNewXpos = guiOffsetX + (int)Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas);
                        }
                        else if ((intNewXpos - guiOffsetX + guiSelectedRectangle.Width) + areRightMargin > modelCompGUIARE.gui.GuiElementCanvas.Width + Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas)) {
                            intNewXpos = Convert.ToInt16(modelCompGUIARE.gui.GuiElementCanvas.Width - guiSelectedRectangle.Width) + guiOffsetX + (int)Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas) - areRightMargin;
                        }
                    }
                    

                    // check, if the y-cooridnate is within the canvas
                    if (guiSelectedModelComponent.id == modelCompGUIARE.id || guiSelectedModelComponent.gui.IsExternalGUIElement) { // or an GUI element without the ARE, e.g. the webcam. These objects can move on the GUI canvas
                        if ((intNewYpos - guiOffsetY) < 1) {
                            intNewYpos = guiOffsetY;
                        }
                        else if ((intNewYpos - guiOffsetY + guiSelectedRectangle.Height) > guiCanvas.Height) {
                            intNewYpos = Convert.ToInt16(guiCanvas.Height - guiSelectedRectangle.Height) + guiOffsetY;
                        }
                    }
                    else { // move only within the ARE GUI element
                        if ((intNewYpos - guiOffsetY) < Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas)) {
                            intNewYpos = guiOffsetY + (int)Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas);
                            //changed
                        }
                        else if ((intNewYpos - guiOffsetY + guiSelectedRectangle.Height) + areBottomMargin > modelCompGUIARE.gui.GuiElementCanvas.Height + Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas)) {
                            intNewYpos = Convert.ToInt16(modelCompGUIARE.gui.GuiElementCanvas.Height - guiSelectedRectangle.Height) + guiOffsetY + (int)Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas) - areBottomMargin;
                        }
                    }
                    */

                    if (guiProp.EnableGrid) {
                        int stepDivX = (intNewXpos - guiOffsetX) / (int)guiProp.GridSteps;
                        deltaX = stepDivX * (double)guiProp.GridSteps - (double)Canvas.GetLeft(guiSelectedCanvas);
                        double canvasLeft = stepDivX * (double)guiProp.GridSteps;
                        if (guiSelectedModelComponent != modelCompGUIARE) {
                            double areLeft = (double)(Int32.Parse(deploymentModel.modelGUI.AREGUIWindow.posX));
                            double scaledAreLeft = (double)((areLeft / 10000.0) * GUIFRAMEWIDTH);
                            if (canvasLeft < scaledAreLeft) {
                            //    canvasLeft = scaledAreLeft;
                            }
                        }
                        Canvas.SetLeft( guiSelectedCanvas, canvasLeft);
                        int stepDivY = (intNewYpos - guiOffsetY) / (int)guiProp.GridSteps;
                        deltaY = stepDivY * (double)guiProp.GridSteps - (double)Canvas.GetTop(guiSelectedCanvas);
                        double canvasTop = stepDivY * (double)guiProp.GridSteps;
                        if (guiSelectedModelComponent != modelCompGUIARE) {
                            double areTop = (double)(Int32.Parse(deploymentModel.modelGUI.AREGUIWindow.posY));
                            double scaledAreTop = 0;
                            switch (guiProp.ScreenRes) {
                                case GUIProperties.ScreenResolution.FiveFour:
                                    scaledAreTop = (double)((areTop / 10000.0) * GUIFRAMEHEIGHTFIVEFOUR);
                                    break;
                                case GUIProperties.ScreenResolution.FourThree:
                                    scaledAreTop = (double)((areTop / 10000.0) * GUIFRAMEHEIGHTFOURTHREE);
                                    break;
                                case GUIProperties.ScreenResolution.SixteenNine:
                                    scaledAreTop = (double)((areTop / 10000.0) * GUIFRAMEHEIGHTSIXTEENNINE);
                                    break;
                            }
                            if (canvasTop < scaledAreTop) {
                            //    canvasTop = scaledAreTop;
                            }
                        }
                        Canvas.SetTop(guiSelectedCanvas, canvasTop);
                        if (guiSelectedModelComponent == modelCompGUIARE) {
                            // write new coordinates of the ARE
                            int posX = (int)((intNewXpos - guiOffsetX) / guiCanvas.Width * 10000);
                            posX = posX - (posX % (int)guiProp.GridSteps);
                            deploymentModel.modelGUI.AREGUIWindow.posX = posX.ToString();
                            deploymentModel.modelGUI.AREGUIWindow.posY = Convert.ToInt16((intNewYpos - guiOffsetY) / guiCanvas.Height * 10000).ToString();
                        }
                        else {
                            int posX = (int)(intNewXpos - guiOffsetX);
                            posX = posX - (posX % (int)guiProp.GridSteps);
                            posX = (int)(posX / guiCanvas.Width * 10000);
                            posX += 2 * (int)guiProp.GridSteps;
                            // write new coordinates to the selected component
                            guiSelectedModelComponent.gui.posX = posX.ToString();

                            int posY = (int)(intNewYpos - guiOffsetY);
                            posY = posY - (posY % (int)guiProp.GridSteps);
                            posY = (int)(posY / guiCanvas.Height * 10000);
                            posY += 2 * (int)guiProp.GridSteps;
                            guiSelectedModelComponent.gui.posY = posY.ToString();
                        }
                    }
                    else {
                        deltaX = intNewXpos - guiOffsetX - (double)Canvas.GetLeft(guiSelectedCanvas);
                        Canvas.SetLeft(guiSelectedCanvas, intNewXpos - guiOffsetX);
                        deltaY = intNewYpos - guiOffsetY - (double)Canvas.GetTop(guiSelectedCanvas);
                        Canvas.SetTop(guiSelectedCanvas, intNewYpos - guiOffsetY);
                        if (guiSelectedModelComponent == modelCompGUIARE) {
                            // write new cooridinates of the ARE
                            deploymentModel.modelGUI.AREGUIWindow.posX = Convert.ToInt16((intNewXpos - guiOffsetX) / guiCanvas.Width * 10000).ToString();
                            deploymentModel.modelGUI.AREGUIWindow.posY = Convert.ToInt16((intNewYpos - guiOffsetY) / guiCanvas.Height * 10000).ToString();
                        }
                        else {
                            // write new coordinates to the selected component
                            guiSelectedModelComponent.gui.posX = Convert.ToInt16((intNewXpos - guiOffsetX) / guiCanvas.Width * 10000).ToString();
                            guiSelectedModelComponent.gui.posY = Convert.ToInt16((intNewYpos - guiOffsetY) / guiCanvas.Height * 10000).ToString();
                        }
                    }


                    modelHasBeenEdited = true;

                    // move all gui objects within the ARE GUI element
                    if (guiSelectedCanvas.Name.Equals("guiCanvasARE")) {
                        foreach (UIElement tempCanvas in guiCanvas.Children) {
                            if (tempCanvas is Canvas) {
                                componentType compToMove = null;
                                if ((((Canvas)tempCanvas).Name != "guiCanvasARE")) { // || ( ((Rectangle)((Canvas)tempCanvas).Children[0]).Fill != (Brush)bc.ConvertFrom("#99E21616") )) {
                                    foreach (componentType tempComponent in deploymentComponentList.Values) {
                                        if ((tempComponent.gui != null) && (tempComponent.gui.GuiElementCanvas == tempCanvas)) {
                                            compToMove = tempComponent;
                                            break;
                                        }
                                    }
                                    if (compToMove != null && !compToMove.gui.IsExternalGUIElement) {
                                        Canvas.SetLeft(tempCanvas, Canvas.GetLeft(tempCanvas) + deltaX);
                                        Canvas.SetTop(tempCanvas, Canvas.GetTop(tempCanvas) + deltaY);

                                        compToMove.gui.posX = Convert.ToInt16(Canvas.GetLeft(tempCanvas) / guiCanvas.Width * 10000).ToString();
                                        compToMove.gui.posY = Convert.ToInt16(Canvas.GetTop(tempCanvas) / guiCanvas.Height * 10000).ToString();
                                    }
                                }
                            }
                        }
                    }
                }
                else { // resize object
                    double newWidth = (int)args.GetPosition(guiCanvas).X - Canvas.GetLeft(guiSelectedCanvas) + 4;
                    double newHeight = (int)args.GetPosition(guiCanvas).Y - Canvas.GetTop(guiSelectedCanvas) + 4;


                    // check, if new size is not smaller 1% or bigger 100%
                    if (guiSelectedModelComponent.id == modelCompGUIARE.id || guiSelectedModelComponent.gui.IsExternalGUIElement) { // or an GUI element without the ARE, e.g. the webcam. These objects can be resized on the GUI canvas
                        if (newWidth < 15) {
                            newWidth = 15;
                        }
                        else if (newWidth > guiCanvas.Width) {
                      //      newWidth = guiCanvas.Width;
                        }
                    }
                    else {
                        if (newWidth < 15) {
                            newWidth = 15;
                        }
                        else if (newWidth + areRightMargin > Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas) + modelCompGUIARE.gui.GuiElementCanvas.Width - Canvas.GetLeft(guiSelectedCanvas)) {
                        //    newWidth = Canvas.GetLeft(modelCompGUIARE.gui.GuiElementCanvas) + modelCompGUIARE.gui.GuiElementCanvas.Width - Canvas.GetLeft(guiSelectedCanvas) - areRightMargin;
                        }
                    }

                    // check, if new size is not smaller 1% or bigger 100%
                    if (guiSelectedModelComponent.id == modelCompGUIARE.id || guiSelectedModelComponent.gui.IsExternalGUIElement) { // or an GUI element without the ARE, e.g. the webcam. These objects can be resized on the GUI canvas
                        if (newHeight < 15) {
                            newHeight = 15;
                        }
                        else if (newHeight > guiCanvas.Height) {
                        //    newHeight = guiCanvas.Height;
                        }
                    }
                    else {
                        if (newHeight < 15) {
                            newHeight = 15;
                        }
                        else if (newHeight + areBottomMargin > Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas) + modelCompGUIARE.gui.GuiElementCanvas.Height - Canvas.GetTop(guiSelectedCanvas)) {
                         //   newHeight = Canvas.GetTop(modelCompGUIARE.gui.GuiElementCanvas) + modelCompGUIARE.gui.GuiElementCanvas.Height - Canvas.GetTop(guiSelectedCanvas) - areBottomMargin;
                        }
                    }


                   // check, if ARE window is not smaller than the space, needed for the components
                    /*
                    if (guiSelectedModelComponent == modelCompGUIARE) {
                        if (newWidth - areRightMargin < guiComponentsMaxX) {
                            newWidth = guiComponentsMaxX + areRightMargin;
                        }
                        if (newHeight - areBottomMargin < guiComponentsMaxY) {
                            newHeight = guiComponentsMaxY + areBottomMargin;
                        }
                        if (newWidth < areRightMargin + 10)
                            newWidth = areRightMargin + 10;
                        if (newHeight < areBottomMargin + 10)
                            newHeight = areBottomMargin + 10;
                    }

                    */

                    if (guiProp.EnableGrid) {
                        int stepDivX = (int)newWidth / (int)guiProp.GridSteps;
                        newWidth = stepDivX * (int)guiProp.GridSteps;
                        int stepDivY = (int)newHeight / (int)guiProp.GridSteps;
                        newHeight = stepDivY * (int)guiProp.GridSteps;
                    }

                    guiSelectedRectangle.Width = newWidth;
                    guiSelectedRectangle.Height = newHeight;
                    guiSelectedCanvas.Height = guiSelectedRectangle.Height;
                    guiSelectedCanvas.Width = guiSelectedRectangle.Width;

                    if (guiSelectedModelComponent == modelCompGUIARE) {
                        // write new cooridinates of the ARE
                        updateAreDecoration(guiSelectedCanvas);
                        deploymentModel.modelGUI.AREGUIWindow.width = Convert.ToInt16(guiSelectedRectangle.Width / guiCanvas.Width * 10000).ToString();
                        deploymentModel.modelGUI.AREGUIWindow.height = Convert.ToInt16(guiSelectedRectangle.Height / guiCanvas.Height * 10000).ToString();
                    }
                    else {
                        // write new size to the selected component
                        guiSelectedModelComponent.gui.width = Convert.ToInt16(guiSelectedRectangle.Width / guiCanvas.Width * 10000).ToString();
                        guiSelectedModelComponent.gui.height = Convert.ToInt16(guiSelectedRectangle.Height / guiCanvas.Height * 10000).ToString();
                    }
                    modelHasBeenEdited = true;
                }
            }

        }

        private void updateAreDecoration(Canvas c) {
            decorationCanvas.Width = c.Width-2;
            controlRect.Height = c.Height;
            Canvas.SetTop(controlTextBlock, c.Height / 2);
        }

        /// <summary>
        /// ZoomSlider Event-Listener:
        /// If the zoom-slider will be changed, not only the main canvas will be changed, also the zoom factor
        /// of the gui-editor will be changed
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void zoomSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e) {
            if (canvas != null) {
                canvas.LayoutTransform = new ScaleTransform(zoomSlider.Value, zoomSlider.Value);
            }
            /*if (guiCanvas != null) {
                guiCanvas.LayoutTransform = new ScaleTransform(zoomSlider.Value, zoomSlider.Value);
            }*/
        }

        #endregion

    }
}
