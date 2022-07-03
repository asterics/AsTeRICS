using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows;


namespace Asterics.ACS
{
    public class EvtChannelDescriptionTextBox : TextBox 
    {
        public UIElement eventListenerTextBox;
        public UIElement eventTriggerComboBox;

        public EvtChannelDescriptionTextBox()
        {
            Margin = new Thickness(0, 0, 0, 0);
            FontSize = 12;
            FontFamily = new FontFamily("Segoe UI");
            IsReadOnly = false;
        }

    }
}
