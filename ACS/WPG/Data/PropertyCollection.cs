using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.ComponentModel;
using System.Collections;
using System.IO;

namespace WPG.Data {

    public class propertyType {

        private string stringType;

        public string StringType {
            get {
                return stringType;
            }
            set {
                stringType = value;
            }
        }
        private int intType;

        public int IntType {
            get {
                return intType;
            }
            set {
                intType = value;
            }
        }
        private bool boolType;

        public bool BoolType {
            get {
                return boolType;
            }
            set {
                boolType = value;
            }
        }
        private byte byteType;

        public byte ByteType {
            get {
                return byteType;
            }
            set {
                byteType = value;
            }
        }

        private char charType;

        public char CharType {
            get {
                return charType;
            }
            set {
                charType = value;
            }
        }

        private double doubleType;

        public double DoubleType {
            get {
                return doubleType;
            }
            set {
                doubleType = value;
            }
        }

        private Array arrayType;

        public Array ArrayType {
            get {
                return arrayType;
            }
            set {
                arrayType = value;
            }
        }

    }

    public class PropertyCollection : CompositeItem {
        #region Initialization

        public PropertyCollection() {
        }

        //public PropertyCollection(object instance)
        //    : this(instance, false)
        //{ }
        PropertyDescriptorCollection properties;

        // new with lang-support
        private string componentID = "";

        public PropertyCollection(object instance, bool noCategory, bool automaticlyExpandObjects, string filter) {
            Dictionary<string, PropertyCategory> groups = new Dictionary<string, PropertyCategory>();

            bool useCustomTypeConverter = false;

            //PropertyDescriptorCollection properties;
            if (instance != null) {
                TypeConverter tc = TypeDescriptor.GetConverter(instance);
                if (tc == null || !tc.GetPropertiesSupported()) {

                    if (instance is ICustomTypeDescriptor)
                        properties = ((ICustomTypeDescriptor)instance).GetProperties();
                    else
                        properties = TypeDescriptor.GetProperties(instance.GetType());  //I changed here from instance to instance.GetType, so that only the Direct Properties are shown!
                } else {
                    properties = tc.GetProperties(instance);
                    useCustomTypeConverter = true;
                }
            } else
                properties = new PropertyDescriptorCollection(new PropertyDescriptor[] { });

            List<Property> propertyCollection = new List<Property>();

            // new with lang-support
            if (instance.GetType() == typeof(Asterics.ACS.componentType)) {
                componentID = ((Asterics.ACS.componentType)instance).type_id;
            } else if (instance.GetType() == typeof(Asterics.ACS.inputPortType)) {
                componentID = ((Asterics.ACS.inputPortType)instance).ComponentTypeId;
            } else if (instance.GetType() == typeof(Asterics.ACS.outputPortType)) {
                componentID = ((Asterics.ACS.outputPortType)instance).ComponentTypeId;
            }

            foreach (PropertyDescriptor propertyDescriptor in properties) {
                if (useCustomTypeConverter) {
                    Property property = new Property(instance, propertyDescriptor);
                    propertyCollection.Add(property);
                } else {
                    CollectProperties(instance, propertyDescriptor, propertyCollection, automaticlyExpandObjects, filter);
                    //if (noCategory)
                    //    propertyCollection.Sort(Property.CompareByName);
                    //else
                    //    propertyCollection.Sort(Property.CompareByCategoryThenByName);
                }
            }

            if (noCategory) {

                foreach (Property property in propertyCollection) {
                    if (filter == "" || property.Name.ToLower().Contains(filter))
                        Items.Add(property);
                }
            } else {
                foreach (Property property in propertyCollection) {
                    if (filter == "" || property.Name.ToLower().Contains(filter)) {
                        PropertyCategory propertyCategory;
                        var category = property.Category ?? string.Empty; // null category handled here

                        if (groups.ContainsKey(category)) {
                            propertyCategory = groups[category];
                        } else {
                            propertyCategory = new PropertyCategory(property.Category);
                            groups[category] = propertyCategory;
                            Items.Add(propertyCategory);
                        }
                        propertyCategory.Items.Add(property);
                    }
                }
            }
        }

        private void CollectProperties(object instance, PropertyDescriptor descriptor, List<Property> propertyCollection, bool automaticlyExpandObjects, string filter) {
            
            Asterics.ACS.IniFile langFile = null; 
            Asterics.ACS.IniFile ini = null;
            // read lang from lini-file, then try to find a file, named like the file

            // loading the asterics.ini file, containing some basic settings
            // UNCOMMENT to activate language support in the property editor. Currentyl in Beta
            if (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini")) {
                ini = new Asterics.ACS.IniFile(AppDomain.CurrentDomain.BaseDirectory + "asterics.ini");
            }
            if ((ini != null) && (File.Exists(AppDomain.CurrentDomain.BaseDirectory + "lang\\" + ini.IniReadValue("Options", "language") + ".txt"))) {
                langFile = new Asterics.ACS.IniFile(AppDomain.CurrentDomain.BaseDirectory + "lang\\" + ini.IniReadValue("Options", "language") + ".txt");
            }

            if (descriptor.Attributes[typeof(FlatAttribute)] == null) {
                Property property = new Property(instance, descriptor);

                if (descriptor.IsBrowsable) {


                    
                    // new with lang-support
                    // replace the internal properties
                    if (langFile != null) {
                        if (property.Value != null && langFile.IniReadValue(componentID, property.Value.ToString()) != "") {
                            if (property.Value.GetType() == typeof(string)) {
                                property.Value = langFile.IniReadValue(componentID, property.Value.ToString());
                            }
                        }
                        if (langFile.IniReadValue(componentID, property.Name) != "") {
                            property.Name = langFile.IniReadValue(componentID, property.Name);
                        }
                    }
                    
                    
                    
                    //Add a property with Name: AutomaticlyExpandObjects
                    Type propertyType = descriptor.PropertyType;
                    if (automaticlyExpandObjects && propertyType.IsClass && !propertyType.IsArray && propertyType != typeof(string)) {
                        propertyCollection.Add(new ExpandableProperty(instance, descriptor, automaticlyExpandObjects, filter));
                    } else if (descriptor.Converter.GetType() == typeof(ExpandableObjectConverter)) {
                        propertyCollection.Add(new ExpandableProperty(instance, descriptor, automaticlyExpandObjects, filter));
                    } else if (descriptor.Name.Equals("PropertyArrayList")) {
                        // NEW CODE for AsTeRICS
                        // Beside changes here, also Properties.cs has beed edited
                        ArrayList propList = (ArrayList)property.Value;
                        foreach (Asterics.ACS.propertyType propFromList in propList) {
                            // Sets an PropertyDescriptor to the specific property.
                            propertyType pT = new propertyType();
                            PropertyDescriptorCollection innerProperties = TypeDescriptor.GetProperties(pT);
                            System.ComponentModel.PropertyDescriptor myProperty = null;
                            Type propType = typeof(string);
                            object objectValue = null;
                            switch (propFromList.DataType) {
                                case Asterics.ACS2.dataType.boolean:
                                    propType = typeof(bool);
                                    myProperty = properties.Find("boolType", true);
                                    try {
                                        pT.BoolType = Boolean.Parse(propFromList.value);
                                    } catch (Exception) {
                                        pT.BoolType = false;
                                    }
                                    objectValue = pT.BoolType;
                                    break;
                                case Asterics.ACS2.dataType.integer:
                                    propType = typeof(int);
                                    myProperty = properties.Find("intType", true);
                                    try {
                                        pT.IntType = int.Parse(propFromList.value);
                                    } catch (Exception) {
                                        pT.IntType = 0;
                                    }
                                    objectValue = pT.IntType;
                                    break;
                                case Asterics.ACS2.dataType.@byte:
                                    propType = typeof(byte);
                                    myProperty = properties.Find("byteType", true);
                                    try {
                                        pT.ByteType = byte.Parse(propFromList.value);
                                    } catch (Exception) {
                                        pT.ByteType = 0;
                                    }
                                    objectValue = pT.ByteType;
                                    break;
                                case Asterics.ACS2.dataType.@char:
                                    propType = typeof(char);
                                    myProperty = properties.Find("charType", true);
                                    try {
                                        pT.CharType = char.Parse(propFromList.value);
                                    } catch (Exception) {
                                        pT.CharType = ' ';
                                    }
                                    objectValue = pT.CharType;
                                    break;
                                case Asterics.ACS2.dataType.@double:
                                    propType = typeof(double);
                                    myProperty = properties.Find("doubleType", true);
                                    try {
                                        pT.DoubleType = double.Parse(propFromList.value, System.Globalization.CultureInfo.InvariantCulture);
                                    } catch (Exception) {
                                        pT.DoubleType = 0;
                                    }
                                    objectValue = pT.DoubleType;
                                    break;
                                case Asterics.ACS2.dataType.@string:
                                    propType = typeof(string);
                                    myProperty = properties.Find("stringType", true);
                                    objectValue = propFromList.value;
                                    break;
                            }
                            if ((propType == typeof(string)) && propFromList.ComboBoxStrings != null) {
                                // text
                                propType = typeof(IList);
                                List<string> stringList = new List<string>(propFromList.ComboBoxStrings.Length);
                                foreach (string s in propFromList.ComboBoxStrings) {
                                    stringList.Add(s);
                                }
                                myProperty = properties.Find("listType", true);
                                objectValue = stringList;
                            } else if ((propType == typeof(int)) && propFromList.ComboBoxStrings != null) {
                                // text

                                // new with lang-support
                                //compProperty.ComboBoxStrings = sourceProperties[outPortIndex].combobox.Split(new String[] { "//" }, StringSplitOptions.None);
                                string stringToCompare = "";
                                foreach (string comboString in propFromList.ComboBoxStrings) {
                                    stringToCompare += comboString + "//";
                                }
                                stringToCompare = stringToCompare.Substring(0, stringToCompare.Length - 2);
                                if (langFile != null) {
                                    if (langFile.IniReadValue(componentID, stringToCompare) != "") {
                                        stringToCompare = langFile.IniReadValue(componentID, stringToCompare);
                                    }
                                }
                                propFromList.ComboBoxStrings = stringToCompare.Split(new String[] { "//" }, StringSplitOptions.None);


                                propType = typeof(IList);
                                List<string> stringList = new List<string>(propFromList.ComboBoxStrings.Length);
                                foreach (string s in propFromList.ComboBoxStrings) {
                                    stringList.Add(s);
                                }
                                myProperty = properties.Find("listType", true);
                                objectValue = stringList;
                            }
                            Property innerProperty = new Property(objectValue, myProperty, propFromList.name, propFromList.Description, propType, propFromList);

                            // new with lang-support
                            // replace the properties  
                            if (langFile != null) {
                                if (langFile.IniReadValue(componentID, innerProperty.Name) != "") {
                                    innerProperty.Name = langFile.IniReadValue(componentID, innerProperty.Name);
                                }
                                if (langFile.IniReadValue(componentID, innerProperty.Description) != "") {
                                    innerProperty.Description = langFile.IniReadValue(componentID, innerProperty.Description);
                                }
                            }


                            propertyCollection.Add(innerProperty);
                        }
                    } else
                        propertyCollection.Add(property);
                }
            } else {
                instance = descriptor.GetValue(instance);
                PropertyDescriptorCollection properties = TypeDescriptor.GetProperties(instance);
                foreach (PropertyDescriptor propertyDescriptor in properties) {
                    CollectProperties(instance, propertyDescriptor, propertyCollection, automaticlyExpandObjects, filter);
                }
            }
        }

        #endregion
    }
}
