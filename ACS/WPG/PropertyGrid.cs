using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Reflection;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using WPG.Data;

namespace WPG {
    [TemplatePart(Name = "PART_PropertyName", Type = typeof(Label))]
    [TemplatePart(Name = "PART_PropertyDescription", Type = typeof(TextBlock))]
    [TemplatePart(Name = "PART_InstanceType", Type = typeof(TextBlock))]
    [TemplatePart(Name = "PART_InstanceName", Type = typeof(TextBlock))]
    [TemplatePart(Name = "PART_Thumb", Type = typeof(Thumb))]
    public class PropertyGrid : Control {
        static PropertyGrid() {
            DefaultStyleKeyProperty.OverrideMetadata(typeof(PropertyGrid),
                                                     new FrameworkPropertyMetadata(typeof(PropertyGrid)));
        }


        public PropertyGrid() {
            this.DataContext = this;
            PreviewMouseDown += new MouseButtonEventHandler(PropertyGrid_PreviewMouseDown);
            PreviewKeyDown += new KeyEventHandler(PropertyGrid_PreviewKeyDown);
            PreviewLostKeyboardFocus += new KeyboardFocusChangedEventHandler(PropertyGrid_PreviewFocusChanged);
        }

        void PropertyGrid_PreviewFocusChanged(object sender, KeyboardFocusChangedEventArgs e)
        {
            var OrginalSource = (e.OriginalSource as FrameworkElement);
            if (OrginalSource.DataContext != null && OrginalSource.DataContext.GetType() == typeof(Property))
            {
                SelectedProperty = (Property)OrginalSource.DataContext;
            }
        }

        //Finish Editing of a TextBox with Enter!
        void PropertyGrid_PreviewKeyDown(object sender, KeyEventArgs e) {
            if (this.SelectedProperty != null && e.Key == Key.Enter && e.OriginalSource is TextBox) {
                this.SelectedProperty.Value = ((TextBox)e.OriginalSource).Text;
            } else if (e.Key == Key.Down && e.OriginalSource is TextBox) {
                e.Handled = true;
                ((TextBox)e.OriginalSource).MoveFocus(new TraversalRequest(FocusNavigationDirection.Next));
            } else if (e.Key == Key.Up && e.OriginalSource is TextBox) {
                e.Handled = true;
                ((TextBox)e.OriginalSource).MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous));
            }
        }

        #region Selection of the Current property, and Reaction to Value Changes
        //(I don't like the implemention, TODO, this should really be done by DataBinding to current property!)

        //Call OnInstanceChanged Again, if the Template was Applied after setting the Instance!      
        public override void OnApplyTemplate() {
            base.OnApplyTemplate();

            object myThumb = Template.FindName("PART_Thumb", this);
            if (myThumb != null && myThumb is Thumb)
                ((Thumb)myThumb).DragDelta += new DragDeltaEventHandler(PART_Thumb_DragDelta);

            Refresh();
        }

        public Property SelectedProperty {
            get {
                return (Property)GetValue(SelectedPropertyProperty);
            }
            set {
                SetValue(SelectedPropertyProperty, value);
            }
        }

        // Using a DependencyProperty as the backing store for SelectedProperty.  This enables animation, styling, binding, etc...
        public static readonly DependencyProperty SelectedPropertyProperty =
            DependencyProperty.Register("SelectedProperty", typeof(Property), typeof(PropertyGrid), new FrameworkPropertyMetadata(null));

        private void PropertyGrid_PreviewMouseDown(object sender, MouseButtonEventArgs e) {
            var OrginalSource = (e.OriginalSource as FrameworkElement);

            if (OrginalSource.DataContext != null && OrginalSource.DataContext.GetType() == typeof(Property)) {
                //var selectedProperty = (Property)OrginalSource.DataContext;
                SelectedProperty = (Property)OrginalSource.DataContext;
                //selectedProperty.PropertyChanged += new PropertyChangedEventHandler(selectedProperty_PropertyChanged);


                object myDescriptionField = Template.FindName("Part_PropertyName", this);
                //ToolTipService.SetShowDuration((TextBlock)myDescriptionField, 15000);

                if (myDescriptionField != null && myDescriptionField is TextBlock)
                    ((TextBlock)myDescriptionField).Text = SelectedProperty.Name;

                object myNameField = Template.FindName("Part_PropertyDescription", this);
                //ToolTipService.SetShowDuration((TextBlock)myNameField, 15000);

                if (myNameField != null && myNameField is TextBlock)
                    ((TextBlock)myNameField).Text = SelectedProperty.Description;

            } else
                SelectedProperty = null;
        }

        void ClearDescription() {
            if (this.Template != null) {
                object myDescriptionField = Template.FindName("Part_PropertyName", this);

                if (myDescriptionField != null && myDescriptionField is TextBlock)
                    ((TextBlock)myDescriptionField).Text = "";

                object myNameField = Template.FindName("Part_PropertyDescription", this);

                if (myNameField != null && myNameField is TextBlock)
                    ((TextBlock)myNameField).Text = "";
            }
        }
        #endregion

        #region Refresh of the PropertyGrid

        public void Refresh() {
            if (Instance == null) {
                Properties = new ObservableCollection<Item>();
            } else {
                var properties = new PropertyCollection(Instance, !Categorized, AutomaticlyExpandObjects,
                                                        Filter.ToLower());
                Properties = properties.Items;

                if (Instance != null) {
                    /// Fill the Instance type and Name with the Data
                    if (Template != null) {
                        object myInstanceType = Template.FindName("PART_InstanceType", this);

                        if (myInstanceType != null && myInstanceType is TextBlock) {
                            string myType = "";
                            Object[] myAttributes = Instance.GetType().GetCustomAttributes(false);
                            if (myAttributes.Length > 0) {
                                foreach (Object attr in myAttributes) {
                                    if (attr is System.ComponentModel.DisplayNameAttribute) {
                                        myType = ((System.ComponentModel.DisplayNameAttribute)attr).DisplayName;
                                    }
                                }
                            }
                            if (myType.Length == 0) {
                                myType = Instance.GetType().ToString();
                                myType = myType.Substring(myType.LastIndexOf('.') + 1);
                            }
                            if (myType.Equals("componentType")) { 
                                myType = Asterics.ACS.Properties.Resources.PropertyEditorModelComponentAlias;
                            } else if (myType.Equals("inputPortType")) {
                                myType = Asterics.ACS.Properties.Resources.PropertyEditorInputPortTypeAlias;
                            } else if (myType.Equals("outputPortType")) {
                                myType = Asterics.ACS.Properties.Resources.PropertyEditorOutputPortTypeAlias;
                            }
                            ((TextBlock)myInstanceType).Text = myType;
                        }


                        object myInstanceName = Template.FindName("PART_InstanceName", this);

                        if (myInstanceName != null && myInstanceName is TextBlock) {
                            if (Instance is FrameworkElement)
                                ((TextBlock)myInstanceName).Text = ((FrameworkElement)Instance).Name;
                            else if (Instance is System.Windows.Forms.Control)
                                ((TextBlock)myInstanceName).Text = ((System.Windows.Forms.Control)Instance).Name;
                            else if (DisplayName != "")
                                ((TextBlock)myInstanceName).Text = DisplayName; // new code for AsTeRICS
                            else {
                                ((TextBlock)myInstanceName).Text = "";
                                PropertyInfo pi = Instance.GetType().GetProperty("Name");
                                if (pi != null)
                                    ((TextBlock)myInstanceName).Text = (string)pi.GetValue(Instance, null);
                            }
                        }

                    }
                }
            }
        }

        // new code for AsTeRICS
        string displayName = "";

        public string DisplayName {
            get {
                return displayName;
            }
            set {
                displayName = value;
            }
        }

        #endregion

        #region Instance Property

        /// <value>Identifies the Instance dependency property</value>
        public static readonly DependencyProperty InstanceProperty =
            DependencyProperty.Register("Instance", typeof(object), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(null, OnInstanceChanged, CoerceInstance));

        /// <value>description for Instance property</value>
        public object Instance {
            get {
                return (object)GetValue(InstanceProperty);
            }
            set {
                SetValue(InstanceProperty, value);
            }
        }


        /// <summary>
        /// Invoked on Instance change.
        /// </summary>
        /// <param name="d">The object that was changed</param>
        /// <param name="e">Dependency property changed event arguments</param>
        private static void OnInstanceChanged(DependencyObject d, DependencyPropertyChangedEventArgs e) {
            var propertyGrid = d as PropertyGrid;

            propertyGrid.SelectedProperty = null;

            propertyGrid.Refresh();

            propertyGrid.RaiseInstanceChanged();
        }

        private static object CoerceInstance(DependencyObject d, object value) {
            var propertyGrid = d as PropertyGrid;
            if (value == null) {
                return propertyGrid.NullInstance;
            }
            return value;
        }

        /// <summary>
        /// Event raised when Instance property is changed
        /// </summary>
        public event EventHandler InstanceChanged;

        private void RaiseInstanceChanged() {
            var handler = InstanceChanged;
            if (handler != null)
                handler(this, EventArgs.Empty);
        }
        #endregion

        #region Filter Property

        /// <value>Identifies the Filter dependency property</value>
        public static DependencyProperty FilterProperty =
            DependencyProperty.Register("Filter", typeof(string), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata("", OnFilterChanged));

        /// <value>description for Filter property</value>
        [Description("If you set a filter, only properties Containing this Text will be displayed")]
        public string Filter {
            get {
                return (string)GetValue(FilterProperty);
            }
            set {
                SetValue(FilterProperty, value);
            }
        }

        private static void OnFilterChanged(DependencyObject d, DependencyPropertyChangedEventArgs e) {
            var propertyGrid = d as PropertyGrid;

            propertyGrid.Refresh();
        }

        #endregion

        #region Categorized Property

        /// <value>Identifies the Categorized dependency property</value>
        public static DependencyProperty CategorizedProperty =
            DependencyProperty.Register("Categorized", typeof(bool), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(true, OnCategorizedChanged));

        /// <value>description for Categorized property</value>
        [Description("Propertys are Categorized or Alphabetical Sorted")]
        public bool Categorized {
            get {
                return (bool)GetValue(CategorizedProperty);
            }
            set {
                SetValue(CategorizedProperty, value);
            }
        }

        private static void OnCategorizedChanged(DependencyObject d, DependencyPropertyChangedEventArgs e) {
            var propertyGrid = d as PropertyGrid;

            propertyGrid.Refresh();
        }

        #endregion

        #region Show Preview Property

        /// <value>Identifies the ShowPreview dependency property</value>
        public static DependencyProperty ShowPreviewProperty =
            DependencyProperty.Register("ShowPreview", typeof(bool), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(false));

        /// <value>description for ShowPreview property</value>
        [Description("If you set the Instance to a XAML Object you can show a little Preview Object in the PropertyGrid"
            )]
        public bool ShowPreview {
            get {
                return (bool)GetValue(ShowPreviewProperty);
            }
            set {
                SetValue(ShowPreviewProperty, value);
            }
        }

        #endregion

        #region Show Description Property

        /// <value>Identifies the ShowDescription dependency property</value>
        public static DependencyProperty ShowDescriptionProperty =
            DependencyProperty.Register("ShowDescription", typeof(bool), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(false));

        /// <value>description for ShowDescription property</value>
        public bool ShowDescription {
            get {
                return (bool)GetValue(ShowDescriptionProperty);
            }
            set {
                SetValue(ShowDescriptionProperty, value);
            }
        }

        #endregion

        #region Headline Property

        /// <summary>
        /// DependenyProperty for the headline of the Propertygrid.
        /// </summary>
        public static readonly DependencyProperty HeadlineProperty = DependencyProperty.Register("Headline", typeof(string), typeof(PropertyGrid), new FrameworkPropertyMetadata("Properties"));

        /// <summary>
        /// Gets or sets the Headline of the Propertygrid.
        /// </summary>
        public string Headline {
            get {
                return (string)GetValue(HeadlineProperty);
            }
            set {
                SetValue(HeadlineProperty, value);
            }
        }

        #endregion


        #region Name Width Property and Dragging

        public static DependencyProperty NameWidthProperty =
            DependencyProperty.Register("NameWidth", typeof(double), typeof(PropertyGrid),
                                        new PropertyMetadata(120.0));

        public double NameWidth {
            get {
                return (double)GetValue(NameWidthProperty);
            }
            set {
                SetValue(NameWidthProperty, value);
            }
        }

        private void PART_Thumb_DragDelta(object sender, DragDeltaEventArgs e) {
            NameWidth = Math.Max(0, NameWidth + e.HorizontalChange);
        }

        #endregion

        #region Automaticly Expand Objects Property

        /// <value>Identifies the AutomaticlyExpandObjects dependency property</value>
        /// The Change of this Property calls OnInstanceChanged, because the property List needs to be refreshed!
        public static DependencyProperty AutomaticlyExpandObjectsProperty =
            DependencyProperty.Register("AutomaticlyExpandObjects", typeof(bool), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(false, OnInstanceChanged));

        /// <value>description for AutomaticlyExpandObjects Property</value>
        public bool AutomaticlyExpandObjects {
            get {
                return (bool)GetValue(AutomaticlyExpandObjectsProperty);
            }
            set {
                SetValue(AutomaticlyExpandObjectsProperty, value);
            }
        }

        #endregion

        #region NullInstance Property

        /// <value>Identifies the NullInstance dependency property</value>
        public static readonly DependencyProperty NullInstanceProperty =
            DependencyProperty.Register("NullInstance", typeof(object), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(null, OnNullInstanceChanged));

        /// <value>description for NullInstance property</value>
        public object NullInstance {
            get {
                return (object)GetValue(NullInstanceProperty);
            }
            set {
                SetValue(NullInstanceProperty, value);
            }
        }

        /// <summary>
        /// Invoked on NullInstance change.
        /// </summary>
        /// <param name="d">The object that was changed</param>
        /// <param name="e">Dependency property changed event arguments</param>
        private static void OnNullInstanceChanged(DependencyObject d, DependencyPropertyChangedEventArgs e) {
        }

        #endregion

        #region Properties Property

        /// <value>Identifies the Properties dependency property</value>
        public static readonly DependencyProperty PropertiesProperty =
            DependencyProperty.Register("Properties", typeof(ObservableCollection<Item>), typeof(PropertyGrid),
                                        new FrameworkPropertyMetadata(new ObservableCollection<Item>(),
                                                                      OnPropertiesChanged));

        /// <value>description for Properties property</value>        
        public ObservableCollection<Item> Properties {
            get {
                return (ObservableCollection<Item>)GetValue(PropertiesProperty);
            }
            set {
                SetValue(PropertiesProperty, value);
            }
        }

        private static void OnPropertiesChanged(DependencyObject d, DependencyPropertyChangedEventArgs e) {
            var propertyGrid = d as PropertyGrid;
            var properties = e.OldValue as ObservableCollection<Item>;
            foreach (Item item in properties) {
                item.Dispose();
            }
        }

        #endregion
    }
}