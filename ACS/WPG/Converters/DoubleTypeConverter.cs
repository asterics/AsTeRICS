using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Data;
using System.Collections.ObjectModel;
using System.Collections;
using System.Globalization;

namespace WPG.Converters
{
	public class DoubleTypeConverter : IValueConverter
	{
		#region IValueConverter Members

        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string retVal = value.ToString();            
            return value == null ? null : retVal.Replace(',', '.');
            //return value == null ? null : ((double)value).ToString(System.Globalization.CultureInfo.InvariantCulture);
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            double dRes;
            if (double.TryParse((string)value, out dRes)) {
                return value == null
                           ? 0.0
                           : double.Parse((string)value, System.Globalization.CultureInfo.InvariantCulture);
            } else {
                return value;
            }
        }

		#endregion
	}
}
