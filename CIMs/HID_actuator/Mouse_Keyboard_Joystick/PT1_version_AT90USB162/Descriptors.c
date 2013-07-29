/*
             LUFA Library
     Copyright (C) Dean Camera, 2010.
              
  dean [at] fourwalledcubicle [dot] com
      www.fourwalledcubicle.com
*/

/*
  Copyright 2010  Dean Camera (dean [at] fourwalledcubicle [dot] com)
  
  Permission to use, copy, modify, distribute, and sell this 
  software and its documentation for any purpose is hereby granted
  without fee, provided that the above copyright notice appear in 
  all copies and that both that the copyright notice and this
  permission notice and warranty disclaimer appear in supporting 
  documentation, and that the name of the author not be used in 
  advertising or publicity pertaining to distribution of the 
  software without specific, written prior permission.

  The author disclaim all warranties with regard to this
  software, including all implied warranties of merchantability
  and fitness.  In no event shall the author be liable for any
  special, indirect or consequential damages or any damages
  whatsoever resulting from loss of use, data or profits, whether
  in an action of contract, negligence or other tortious action,
  arising out of or in connection with the use or performance of
  this software.
*/

/** \file
 *
 *  USB Device Descriptors, for library use when in USB device mode. Descriptors are special 
 *  computer-readable structures which the host requests upon device enumeration, to determine
 *  the device's capabilities and functions.  
 */

#include "Descriptors.h"


/** HID class report descriptor. This is a special descriptor constructed with values from the
 *  USBIF HID class specification to describe the reports and capabilities of the HID device. This
 *  descriptor is parsed by the host and its contents used to determine what data (and in what encoding)
 *  the device will send, and what it may be sent back from the host. Refer to the HID specification for
 *  more details on HID report descriptors.
 *
 *  This descriptor describes the mouse HID interface's report structure.
 */

#ifndef _USE_ABS_MOUSEPOINTER_

USB_Descriptor_HIDReport_Datatype_t PROGMEM MouseReport[] =
{
	0x05, 0x01,          /* Usage Page (Generic Desktop)                    */
	0x09, 0x02,          /* Usage (Mouse)                                   */
	0xA1, 0x01,          /* Collection (Application)                        */
	0x09, 0x01,          /*   Usage (Pointer)                               */
	0xA1, 0x00,          /*   Collection (Physical)                         */
	0x95, 0x03,          /*     Report Count (3)                            */
	0x75, 0x01,          /*     Report Size (1)                             */
	0x05, 0x09,          /*     Usage Page (Button)                         */
	0x19, 0x01,          /*     Usage Minimum (Button 1)                    */
	0x29, 0x03,          /*     Usage Maximum (Button 3)                    */
	0x15, 0x00,          /*     Logical Minimum (0)                         */
	0x25, 0x01,          /*     Logical Maximum (1)                         */
	0x81, 0x02,          /*     Input (Data, Variable, Absolute)            */
	0x95, 0x01,          /*     Report Count (1)                            */
	0x75, 0x05,          /*     Report Size (5)                             */
	0x81, 0x01,          /*     Input (Constant)                            */
	0x75, 0x08,          /*     Report Size (8)                             */
	0x95, 0x03,          /*     Report Count (3)                            */
	0x05, 0x01,          /*     Usage Page (Generic Desktop Control)        */
	0x09, 0x30,          /*     Usage X                                     */
	0x09, 0x31,          /*     Usage Y                                     */
	0x09, 0x38,          //     USAGE (Wheel)
	0x15, 0x81,          /*     Logical Minimum (-127)                      */
	0x25, 0x7F,          /*     Logical Maximum (127)                       */
	0x81, 0x06,          /*     Input (Data, Variable, Relative)            */
	0xC0,                /*   End Collection                                */
	0xC0,                /* End Collection                                  */
};

#else
USB_Descriptor_HIDReport_Datatype_t PROGMEM MouseReport[] =
{
 	 0x05, 0x01,          /* Usage Page (Generic Desktop)             */
	 0x09, 0x02,          /* Usage (Mouse)                            */
	 0xA1, 0x01,          /* Collection (Application)                 */
	 0x09, 0x01,          /*   Usage (Pointer)                        */
	 0xA1, 0x00,          /*   Collection (Application)               */
	 0x95, 0x03,          /*     Report Count (3)                     */
	 0x75, 0x01,          /*     Report Size (1)                      */
	 0x05, 0x09,          /*     Usage Page (Button)                  */
	 0x19, 0x01,          /*     Usage Minimum (Button 1)             */
	 0x29, 0x03,          /*     Usage Maximum (Button 3)             */
	 0x15, 0x00,          /*     Logical Minimum (0)                  */
	 0x25, 0x01,          /*     Logical Maximum (1)                  */
	 0x81, 0x02,          /*     Input (Data, Variable, Absolute)     */
	0x95, 0x01,          /*     Report Count (1)                     */
	0x75, 0x05,          /*     Report Size (5)                      */
    0x81, 0x01,          /*     Input (Constant)                     */
	0x75, 0x10,          /*     Report Size (16)                      */
	0x95, 0x02,          /*     Report Count (2)                     */
	 0x05, 0x01,          /*     Usage Page (Generic Desktop Control) */
	 0x09, 0x30,          /*     Usage X                              */
	 0x09, 0x31,          /*     Usage Y                              */
    0x35, 0x00,                    //     PHYSICAL_MINIMUM (0)
    0x46, 0x9d, 0x0b,              //     PHYSICAL_MAXIMUM (2973)
    0x15, 0x00,                    //     LOGICAL_MINIMUM (0)
    0x26, 0x9d, 0x0b,              //     LOGICAL_MAXIMUM (2973)
    0x65, 0x11,                    //     UNIT (SI Lin:Distance)
    0x55, 0x0e,                    //     UNIT_EXPONENT (-2)
    0x81, 0x02,          /*     Input (Data, Variable, Absolute)     */
	0xC0,                /*   End Collection                         */
	0xC0                 /* End Collection                           */
};

#endif





/** Same as the MouseReport structure, but defines the keyboard HID interface's report structure. */
USB_Descriptor_HIDReport_Datatype_t PROGMEM KeyboardReport[] =
{
	0x05, 0x01,          /* Usage Page (Generic Desktop)                    */
	0x09, 0x06,          /* Usage (Keyboard)                                */
	0xa1, 0x01,          /* Collection (Application)                        */
	0x75, 0x01,          /*   Report Size (1)                               */
	0x95, 0x08,          /*   Report Count (8)                              */
	0x05, 0x07,          /*   Usage Page (Key Codes)                        */
	0x19, 0xe0,          /*   Usage Minimum (Keyboard LeftControl)          */
	0x29, 0xe7,          /*   Usage Maximum (Keyboard Right GUI)            */
	0x15, 0x00,          /*   Logical Minimum (0)                           */
	0x25, 0x01,          /*   Logical Maximum (1)                           */
	0x81, 0x02,          /*   Input (Data, Variable, Absolute)              */
	0x95, 0x01,          /*   Report Count (1)                              */
	0x75, 0x08,          /*   Report Size (8)                               */
	0x81, 0x03,          /*   Input (Const, Variable, Absolute)             */

//  comment this out if led-notifications not needed:
	0x95, 0x05,          /*   Report Count (5)                              */
	0x75, 0x01,          /*   Report Size (1)                               */
	0x05, 0x08,          /*   Usage Page (LEDs)                             */
	0x19, 0x01,          /*   Usage Minimum (Num Lock)                      */
	0x29, 0x05,          /*   Usage Maximum (Kana)                          */
	0x91, 0x02,          /*   Output (Data, Variable, Absolute)             */
	0x95, 0x01,          /*   Report Count (1)                              */
	0x75, 0x03,          /*   Report Size (3)                               */
	0x91, 0x03,          /*   Output (Const, Variable, Absolute)            */
//
	0x95, 0x06,          /*   Report Count (6)                              */
	0x75, 0x08,          /*   Report Size (8)                               */
	0x15, 0x00,          /*   Logical Minimum (0)                           */
	0x25, 0x65,          /*   Logical Maximum (101)                         */
	0x05, 0x07,          /*   Usage Page (Keyboard)                         */
	0x19, 0x00,          /*   Usage Minimum (Reserved (no event indicated)) */
	0x29, 0x65,          /*   Usage Maximum (Keyboard Application)          */
	0x81, 0x00,          /*   Input (Data, Array, Absolute)                 */
	0xC0                 /* End Collection                                  */
};

/**  defines the joystcik HID interface's report structure. */

USB_Descriptor_HIDReport_Datatype_t PROGMEM JoystickReport[] =
{
	0x05, 0x01,          /* Usage Page (Generic Desktop)                       */
	0x09, 0x04,          /* Usage (Joystick)                                   */
	0xa1, 0x01,          /* Collection (Application)                           */
	0x09, 0x01,          /*   Usage (Pointer)                                  */
	0xa1, 0x00,          /*   Collection (Physical)                            */
	0x05, 0x01,          /*     Usage Page (Generic Desktop)                   */
	0x09, 0x30,          /*     Usage (X)                                      */
	0x09, 0x31,          /*     Usage (Y)                                      */
	0x15, 0x9c,          /*     Logical Minimum (-100)                         */
	0x25, 0x64,          /*     Logical Maximum (100)                          */
	0x75, 0x08,          /*     Report Size (8)                                */
	0x95, 0x02,          /*     Report Count (2)                               */
	0x81, 0x82,          /*     Input (Data, Variable, Absolute, Volatile)     */
	0xc0,                /*   End Collection                                   */
	0x05, 0x09,          /*   Usage Page (Button)                              */
	0x09, 0x02,          /*   Usage (Button 2)                                 */
	0x09, 0x01,          /*   Usage (Button 1)                                 */
	0x15, 0x00,          /*   Logical Minimum (0)                              */
	0x25, 0x01,          /*   Logical Maximum (1)                              */
	0x75, 0x01,          /*   Report Size (1)                                  */
	0x95, 0x02,          /*   Report Count (2)                                 */
	0x81, 0x02,          /*   Input (Data, Variable, Absolute)                 */
	0x75, 0x06,          /*   Report Size (6)                                  */
	0x95, 0x01,          /*   Report Count (1)                                 */
	0x81, 0x01,          /*   Input (Constant)                                 */
	0xc0                 /* End Collection                                     */
};


/** Device descriptor structure. This descriptor, located in FLASH memory, describes the overall
 *  device characteristics, including the supported USB version, control endpoint size and the
 *  number of device configurations. The descriptor is read out by the USB host when the enumeration
 *  process begins.
 */

USB_Descriptor_Device_t PROGMEM DeviceDescriptor =
{
	.Header                 = {.Size = sizeof(USB_Descriptor_Device_t), .Type = DTYPE_Device},
		
	.USBSpecification       = VERSION_BCD(01.10),
	.Class                  = 0x00,
	.SubClass               = 0x00,
	.Protocol               = 0x00,
				
	.Endpoint0Size          = FIXED_CONTROL_ENDPOINT_SIZE,
		
//	.VendorID               = 0x03EB,
//	.ProductID              = 0x204D,
	.VendorID               = 0xffff,  // free vendor ID
	.ProductID              = 0xac95,  // new dummy PID to get device reenumeration
	.ReleaseNumber          = 0x0000,
		
	.ManufacturerStrIndex   = 0x01,
	.ProductStrIndex        = 0x02,
	.SerialNumStrIndex      = NO_DESCRIPTOR,
		
	.NumberOfConfigurations = FIXED_NUM_CONFIGURATIONS
};





/** Configuration descriptor structure. This descriptor, located in FLASH memory, describes the usage
 *  of the device in one of its supported configurations, including information about any device interfaces
 *  and endpoints. The descriptor is read out by the USB host during the enumeration process when selecting
 *  a configuration so that the host may correctly communicate with the USB device.
 */
USB_Descriptor_Configuration_t PROGMEM ConfigurationDescriptor =
{
	.Config = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Configuration_Header_t), .Type = DTYPE_Configuration},

			.TotalConfigurationSize = sizeof(USB_Descriptor_Configuration_t),
			.TotalInterfaces        = 3,
				
			.ConfigurationNumber    = 1,
			.ConfigurationStrIndex  = NO_DESCRIPTOR,
				
			.ConfigAttributes       = (USB_CONFIG_ATTR_BUSPOWERED | USB_CONFIG_ATTR_SELFPOWERED),
			
			.MaxPowerConsumption    = USB_CONFIG_POWER_MA(100)
		},
		
	.HID1_KeyboardInterface = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Interface_t), .Type = DTYPE_Interface},

			.InterfaceNumber        = 0x00,
			.AlternateSetting       = 0x00,
			
			.TotalEndpoints         = 1,
				
			.Class                  = 0x03,
			.SubClass               = 0x01,
			.Protocol               = HID_BOOT_KEYBOARD_PROTOCOL,
				
			.InterfaceStrIndex      = NO_DESCRIPTOR
		},

	.HID1_KeyboardHID = 
		{  
			.Header                 = {.Size = sizeof(USB_HID_Descriptor_t), .Type = DTYPE_HID},
			
			.HIDSpec                = VERSION_BCD(01.11),
			.CountryCode            = 0x00,
			.TotalReportDescriptors = 1,
			.HIDReportType          = DTYPE_Report,
			.HIDReportLength        = sizeof(KeyboardReport)
		},
		
	.HID1_ReportINEndpoint = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Endpoint_t), .Type = DTYPE_Endpoint},

			.EndpointAddress        = (ENDPOINT_DESCRIPTOR_DIR_IN | KEYBOARD_IN_EPNUM),
			.Attributes             = (EP_TYPE_INTERRUPT | ENDPOINT_ATTR_NO_SYNC | ENDPOINT_USAGE_DATA),
			.EndpointSize           = HID_EPSIZE,
			.PollingIntervalMS      = 0x0A
		},

	.HID2_MouseInterface = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Interface_t), .Type = DTYPE_Interface},

			.InterfaceNumber        = 0x01,
			.AlternateSetting       = 0x00,
			
			.TotalEndpoints         = 1,
				
			.Class                  = 0x03,
			.SubClass               = 0x01,
			.Protocol               = HID_BOOT_MOUSE_PROTOCOL,
				
			.InterfaceStrIndex      = NO_DESCRIPTOR
		},

	.HID2_MouseHID = 
		{  
			.Header                 = {.Size = sizeof(USB_HID_Descriptor_t), .Type = DTYPE_HID},
			
			.HIDSpec                = VERSION_BCD(01.11),
			.CountryCode            = 0x00,
			.TotalReportDescriptors = 1,
			.HIDReportType          = DTYPE_Report,
			.HIDReportLength        = sizeof(MouseReport)
		},
		
	.HID2_ReportINEndpoint = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Endpoint_t), .Type = DTYPE_Endpoint},

			.EndpointAddress        = (ENDPOINT_DESCRIPTOR_DIR_IN | MOUSE_IN_EPNUM),
			.Attributes             = (EP_TYPE_INTERRUPT | ENDPOINT_ATTR_NO_SYNC | ENDPOINT_USAGE_DATA),
			.EndpointSize           = HID_EPSIZE,
			.PollingIntervalMS      = 0x0A
		},

	.HID3_JoystickInterface = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Interface_t), .Type = DTYPE_Interface},

			.InterfaceNumber        = 0x02,
			.AlternateSetting       = 0x00,
			
			.TotalEndpoints         = 1,
				
			.Class                  = 0x03,
			.SubClass               = 0x00,
			.Protocol               = HID_NON_BOOT_PROTOCOL,
				
			.InterfaceStrIndex      = NO_DESCRIPTOR
		},

	.HID3_JoystickHID = 
		{
			.Header                 = {.Size = sizeof(USB_HID_Descriptor_t), .Type = DTYPE_HID},
			
			.HIDSpec                = VERSION_BCD(01.11),
			.CountryCode            = 0x00,
			.TotalReportDescriptors = 1,
			.HIDReportType          = DTYPE_Report,
			.HIDReportLength        = sizeof(JoystickReport)
		},

	.HID3_ReportINEndpoint = 
		{
			.Header                 = {.Size = sizeof(USB_Descriptor_Endpoint_t), .Type = DTYPE_Endpoint},
			
			.EndpointAddress        = (ENDPOINT_DESCRIPTOR_DIR_IN | JOYSTICK_IN_EPNUM),
			.Attributes             = (EP_TYPE_INTERRUPT | ENDPOINT_ATTR_NO_SYNC | ENDPOINT_USAGE_DATA),
			.EndpointSize           = HID_EPSIZE,
			.PollingIntervalMS      = 0x0A
		}	

};

/** Language descriptor structure. This descriptor, located in FLASH memory, is returned when the host requests
 *  the string descriptor with index 0 (the first index). It is actually an array of 16-bit integers, which indicate
 *  via the language ID table available at USB.org what languages the device supports for its string descriptors.
 */
USB_Descriptor_String_t PROGMEM LanguageString =
{
	.Header                 = {.Size = USB_STRING_LEN(1), .Type = DTYPE_String},
		
	.UnicodeString          = {LANGUAGE_ID_ENG}
};

/** Manufacturer descriptor string. This is a Unicode string containing the manufacturer's details in human readable
 *  form, and is read out upon request by the host when the appropriate string ID is requested, listed in the Device
 *  Descriptor.
 */
USB_Descriptor_String_t PROGMEM ManufacturerString =
{
	.Header                 = {.Size = USB_STRING_LEN(11), .Type = DTYPE_String},
		
	.UnicodeString          = L"Dean Camera"
};

/** Product descriptor string. This is a Unicode string containing the product's details in human readable form,
 *  and is read out upon request by the host when the appropriate string ID is requested, listed in the Device
 *  Descriptor.
 */
USB_Descriptor_String_t PROGMEM ProductString =
{
	.Header                 = {.Size = USB_STRING_LEN(28), .Type = DTYPE_String},
		
	.UnicodeString          = L"LUFA Mouse and Keyboard Demo"
};
/** This function is called by the library when in device mode, and must be overridden (see library "USB Descriptors"
 *  documentation) by the application code so that the address and size of a requested descriptor can be given
 *  to the USB library. When the device receives a Get Descriptor request on the control endpoint, this function
 *  is called so that the descriptor details can be passed back and the appropriate descriptor sent back to the
 *  USB host.
 */
uint16_t CALLBACK_USB_GetDescriptor(const uint16_t wValue,
                                    const uint8_t wIndex,
                                    void** const DescriptorAddress)
{
	const uint8_t  DescriptorType   = (wValue >> 8);
	const uint8_t  DescriptorNumber = (wValue & 0xFF);

	void*    Address = NULL;
	uint16_t Size    = NO_DESCRIPTOR;

	switch (DescriptorType)
	{
		case DTYPE_Device: 
			Address = (void*)&DeviceDescriptor;
			Size    = sizeof(USB_Descriptor_Device_t);
			break;
		case DTYPE_Configuration: 
			Address = (void*)&ConfigurationDescriptor;
			Size    = sizeof(USB_Descriptor_Configuration_t);
			break;
		case DTYPE_String: 
			switch (DescriptorNumber)
			{
				case 0x00: 
					Address = (void*)&LanguageString;
					Size    = pgm_read_byte(&LanguageString.Header.Size);
					break;
				case 0x01: 
					Address = (void*)&ManufacturerString;
					Size    = pgm_read_byte(&ManufacturerString.Header.Size);
					break;
				case 0x02: 
					Address = (void*)&ProductString;
					Size    = pgm_read_byte(&ProductString.Header.Size);
					break;
			}
			
			break;
		case DTYPE_HID: 
			 switch (wIndex) {
				case 0:
					Address = (void*)&ConfigurationDescriptor.HID1_KeyboardHID;
					Size    = sizeof(USB_HID_Descriptor_t);
			    	break;
				case 1:
					Address = (void*)&ConfigurationDescriptor.HID2_MouseHID;
					Size    = sizeof(USB_HID_Descriptor_t);			
					break;
				case 2:
					Address = (void*)&ConfigurationDescriptor.HID3_JoystickHID;
					Size    = sizeof(USB_HID_Descriptor_t);
					break;
				}
			break;
		case DTYPE_Report: 
			 switch (wIndex) {
				case 0:
					Address = (void*)&KeyboardReport;
					Size    = sizeof(KeyboardReport);
					break;
				case 1:
					Address = (void*)&MouseReport;
					Size    = sizeof(MouseReport);
					break;
				case 2:
					Address = (void*)&JoystickReport;
					Size    = sizeof(JoystickReport);
					break;
				}
			
			break;
	}
	
	*DescriptorAddress = Address;
	return Size;
}
