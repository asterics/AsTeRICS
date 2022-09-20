using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Data;
using System.Collections.ObjectModel;
using System.Collections;
using System.Globalization;

namespace WPG.Converters
{
    [ValueConversion(typeof(bool), typeof(bool))]
    public class BoolToOppositeConverter : IValueConverter
    {
        #region IValueConverter Members

        public object Convert(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
        {
            return !(bool)value;
        }

        public object ConvertBack(object value, Type targetType, object parameter,
            System.Globalization.CultureInfo culture)
        {
            return !(bool)value;            
        }

        #endregion
    }
}
