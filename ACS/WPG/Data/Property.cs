using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.ComponentModel;
using System.Windows.Data;

namespace WPG.Data {

    public class Property : Item, IDisposable, INotifyPropertyChanged {
        public event PropertyChangedEventHandler PropertyChanged;

        private void NotifyPropertyChanged(String info) {
            if (PropertyChanged != null) {
                PropertyChanged(this, new PropertyChangedEventArgs(info));
            }
        }


        #region Fields

        protected object _instance;
        protected PropertyDescriptor _property;

        // NEW CODE for AsTeRICS
        // Beside changes here, also PropertCollection.cs has beed edited
        protected string name;
        protected Type properyType;
        private bool isFlatProperty;
        private Asterics.ACS.propertyType origValue;
        private string description;


        #endregion

        #region Initialization

        public Property(object instance, PropertyDescriptor property) {
            if (instance is ICustomTypeDescriptor) {
                this._instance = ((ICustomTypeDescriptor)instance).GetPropertyOwner(property);
            } else {
                this._instance = instance;
            }

            this._property = property;

            // NEW CODE for AsTeRICS
            this.isFlatProperty = false;
            this.properyType = _property.PropertyType;
            if (_property.DisplayName != "") {
                this.name = _property.DisplayName;
            } else {
                this.name = _property.Name;
            }

            this._property.AddValueChanged(_instance, instance_PropertyChanged);
            NotifyPropertyChanged("PropertyType");
        }

        // NEW CODE for AsTeRICS
        public Property(object instance, PropertyDescriptor property, string displayName, string description, Type propType, Asterics.ACS.propertyType origValue) {
            if (instance is ICustomTypeDescriptor) {
                this._instance = ((ICustomTypeDescriptor)instance).GetPropertyOwner(property);
            } else {
                this._instance = instance;
            }

            this._property = property;
            this.properyType = propType;
            this.name = displayName;
            this.isFlatProperty = true;
            this.origValue = origValue;
            this.description = description;

            // The following line is comment at the moment, should be reworked!!!
            //this._property.AddValueChanged(_instance, instance_PropertyChanged);
        }

        #endregion

        #region Properties

        /// <value>
        /// Initializes the reflected instance property
        /// </value>
        /// <exception cref="NotSupportedException">
        /// The conversion cannot be performed
        /// </exception>
        public object Value {
            get {
                // NEW CODE for AsTeRICS
                if (isFlatProperty) {
                    return _instance;
                } else {
                    return _property.GetValue(_instance);
                }
            }
            set {
                if (isFlatProperty) {
                    _instance = value;

                    if (value is double) {
                        origValue.value = ((double)value).ToString(System.Globalization.CultureInfo.InvariantCulture);
                    } else {
                        origValue.value = value.ToString();
                    }
                } else {
                    object currentValue = _property.GetValue(_instance);
                    if (value != null && value.Equals(currentValue)) {
                        return;
                    }
                    Type propertyType = _property.PropertyType;
                    if (propertyType == typeof(object) ||
                        value == null && propertyType.IsClass ||
                        value != null && propertyType.IsAssignableFrom(value.GetType())) {
                        _property.SetValue(_instance, value);
                    } else {
                        TypeConverter converter = TypeDescriptor.GetConverter(_property.PropertyType);
                        try {
                            object convertedValue = converter.ConvertFrom(value);
                            _property.SetValue(_instance, convertedValue);
                        } catch (Exception) {
                        }
                    }
                }
            }
        }



        // NEW CODE for ASTERICS
        // CurrentValue will be used in List's (comboboxes), pointing the selected value
        public object CurrentValue {
            get {
                // if the target type is integer, the string value will be mapped to the index
                if (origValue.DataType == Asterics.ACS2.dataType.integer) {
                    int intex = 0;
                    int.TryParse(origValue.value, out intex);
                    return origValue.ComboBoxStrings[intex];
                } else {
                    return origValue.value;
                }
            }
            set {
                if (origValue.DataType == Asterics.ACS2.dataType.integer) {
                    for (int i = 0; i < origValue.ComboBoxStrings.Length; i++) {
                        if (origValue.ComboBoxStrings[i].Equals((string)value)) {
                            origValue.value = i.ToString();
                            break;
                        }
                    }
                } else {
                    if (value == null)
                        origValue.value = "";
                    else
                        origValue.value = (string)value;
                }
                NotifyPropertyChanged("Value");
            }
        }

        public string Name {
            // NEW CODE for AsTeRICS
            //get { return _property.DisplayName ?? _property.Name; }
            get {
                return this.name;
            }
            // new for language support
            set {
                this.name = value;
            }
        }

        public string Description {
            get {
                if (this.isFlatProperty) {
                    return this.description;
                } else {
                    return _property.Description;
                }
            }
            set {
                if (this.isFlatProperty) {
                    this.description = value;
                } 
            }
        }

        public bool IsWriteable {
            get {
                return !IsReadOnly;
            }
        }

        public bool IsReadOnly {
            get {
                return _property.IsReadOnly;
            }
        }

        public Type PropertyType {
            // NEW CODE for AsTeRICS
            //get { return _property.PropertyType; }
            get {
                return this.properyType;
            }
        }

        public string Category {
            get {
                if (this.isFlatProperty) {
                    return Asterics.ACS.Properties.Resources.PropertyEditorPropertyCategory;
                } else if (_property.Category.Equals("ACSInternalProperty")) {
                    return Asterics.ACS.Properties.Resources.PropertyEditorInternalPropertyCategory;
                } else {
                    return _property.Category;
                }
            }
        }

        #endregion

        #region Event Handlers

        void instance_PropertyChanged(object sender, EventArgs e) {
            NotifyPropertyChanged("Value");
        }

        #endregion

        #region IDisposable Members

        protected override void Dispose(bool disposing) {
            if (Disposed) {
                return;
            }
            if (disposing) {
                // NEW CODE for AsTeRICS
                if (!isFlatProperty) {
                    _property.RemoveValueChanged(_instance, instance_PropertyChanged);
                }
            }
            base.Dispose(disposing);
        }

        #endregion

        #region Comparer for Sorting

        private class ByCategoryThenByNameComparer : IComparer<Property> {

            public int Compare(Property x, Property y) {
                if (ReferenceEquals(x, null) || ReferenceEquals(y, null))
                    return 0;
                if (ReferenceEquals(x, y))
                    return 0;
                int val = x.Category.CompareTo(y.Category);
                if (val == 0)
                    return x.Name.CompareTo(y.Name);
                return val;
            }
        }

        private class ByNameComparer : IComparer<Property> {

            public int Compare(Property x, Property y) {
                if (ReferenceEquals(x, null) || ReferenceEquals(y, null))
                    return 0;
                if (ReferenceEquals(x, y))
                    return 0;
                return x.Name.CompareTo(y.Name);
            }
        }

        public readonly static IComparer<Property> CompareByCategoryThenByName = new ByCategoryThenByNameComparer();
        public readonly static IComparer<Property> CompareByName = new ByNameComparer();

        #endregion


    }
}

