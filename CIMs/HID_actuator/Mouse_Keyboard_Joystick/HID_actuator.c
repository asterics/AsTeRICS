

/*
     AsTeRICS - Assistive Technology Rapid Integration and Construction Set
  
  
         d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
        d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
       d88P888          888           888    888   888  888    888 Y88b.     
      d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
     d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
    d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
   d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
  d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 
 
                     homepage: http://www.asterics.org 
 
     This project has been partly funded by the European Commission, 
                       Grant Agreement Number 247730
 
  The AsTeRICS Universal HID Actuator is based upon	the
  LUFA Library, Copyright (C) Dean Camera, 2010
  dean [at] fourwalledcubicle [dot] com,  www.fourwalledcubicle.com

  Special thanks to Josh Kropf <josh@slashdev.ca> 
  for the PS3 teensy HID Gamepad code which helped to get the PS3 button
  working: 

    PS3 Teensy HID Gamepad
    Copyright (C) 2010 Josh Kropf <josh@slashdev.ca>
 
    Based on works by:
      grunskis <http://github.com/grunskis/gamepad>
      Toodles <http://forums.shoryuken.com/showthread.php?t=131230>
 
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
 
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
 
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

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


 	 file: HID_actuator.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010

	 based upon the LUFA Library,
     Copyright (C) Dean Camera, 2010.         
	 dean [at] fourwalledcubicle [dot] com
      www.fourwalledcubicle.com

*/

/** \file
 *
 *  Main source file for the Universal HID Actuator implementation
 */


#include "HID_actuator.h"
#include "Uart.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;
extern uint8_t CLEAR_INPUT;
uint8_t mouse_button_saved=0;


/** Buffer to hold the previously generated relativeMouse HID report, for comparison purposes inside the HID class driver. */
uint8_t PrevMouseRelHIDReportBuffer[sizeof(USB_MouseRelReport_Data_t)];

/** Buffer to hold the previously generated absoluteMouse HID report */
uint8_t PrevMouseAbsHIDReportBuffer[sizeof(USB_MouseAbsReport_Data_t)];

/** Buffer to hold the previously generated Keyboard HID report */
uint8_t PrevKeyboardHIDReportBuffer[sizeof(USB_KeyboardReport_Data_t)];

/** Buffer to hold the previously generated Joystick report */
uint8_t PrevJoystickHIDReportBuffer[sizeof(USB_JoystickReport_Data_t)];

static USB_JoystickReport_Data_t PROGMEM USB_Joystick_idle_state = {
	.triangle_btn = 0, .square_btn = 0, .cross_btn = 0, .circle_btn = 0,
	.l1_btn = 0, .r1_btn = 0, .l2_btn = 0, .r2_btn = 0,
	.select_btn = 0, .start_btn = 0, .ps_btn = 0,
	.direction = 0x08,
	.l_x_axis = 0x80, .l_y_axis = 0x80, .r_x_axis = 0x80, .r_y_axis = 0x80,
	.unknown = {0x00, 0x00, 0x00, 0x00},
	.circle_axis = 0x00, .cross_axis = 0x00, .square_axis = 0x00, .triangle_axis = 0x00,
	.l1_axis = 0x00, .r1_axis = 0x00, .l2_axis = 0x00, .r2_axis = 0x00
};



/** LUFA HID Class driver interface configuration and state information. This structure is
 *  passed to all HID Class driver functions, so that multiple instances of the same class
 *  within a device can be differentiated from one another. This is for the keyboard HID
 *  interface within the device.
 */
USB_ClassInfo_HID_Device_t Keyboard_HID_Interface =
	{
		.Config =
			{
				.InterfaceNumber              = 0,

				.ReportINEndpointNumber       = KEYBOARD_IN_EPNUM,
				.ReportINEndpointSize         = HID_EPSIZE,
				.ReportINEndpointDoubleBank   = false,

				.PrevReportINBuffer           = PrevKeyboardHIDReportBuffer,
				.PrevReportINBufferSize       = sizeof(PrevKeyboardHIDReportBuffer),
			},
	};
	
USB_ClassInfo_HID_Device_t MouseRel_HID_Interface =
	{
		.Config =
			{
				.InterfaceNumber              = 1,

				.ReportINEndpointNumber       = MOUSEREL_IN_EPNUM,
				.ReportINEndpointSize         = HID_EPSIZE,

				.ReportINEndpointDoubleBank   = false,
				.PrevReportINBuffer           = PrevMouseRelHIDReportBuffer,
				.PrevReportINBufferSize       = sizeof(PrevMouseRelHIDReportBuffer),
			},		
	};


USB_ClassInfo_HID_Device_t Joystick_HID_Interface =
	{
		.Config =
			{
				.InterfaceNumber              = 2,

				.ReportINEndpointNumber       = JOYSTICK_IN_EPNUM,
				.ReportINEndpointSize         = HID_EPSIZE,
				.ReportINEndpointDoubleBank   = false,

				.PrevReportINBuffer           = PrevJoystickHIDReportBuffer,
				.PrevReportINBufferSize       = sizeof(PrevJoystickHIDReportBuffer),
			},
	};

USB_ClassInfo_HID_Device_t MouseAbs_HID_Interface =
	{
		.Config =
			{
				.InterfaceNumber              = 3,

				.ReportINEndpointNumber       = MOUSEABS_IN_EPNUM,
				.ReportINEndpointSize         = HID_EPSIZE,

				.ReportINEndpointDoubleBank   = false,
				.PrevReportINBuffer           = PrevMouseAbsHIDReportBuffer,
				.PrevReportINBufferSize       = sizeof(PrevMouseAbsHIDReportBuffer),
			},		
	};


/** Main program entry point. This routine contains the overall program flow, including initial
 *  setup of all components and the main program loop.
 */
int main(void)
{
	setupHardware(); 
	init_CIM_frame();


	LEDs_TurnOffLEDs(LED6);
	LEDs_TurnOnLEDs(LED7);
	sei();

	for (;;)
	{
	    parse_CIM_protocol();
		HID_Device_USBTask(&Keyboard_HID_Interface);
	    parse_CIM_protocol();
		HID_Device_USBTask(&MouseRel_HID_Interface);
	    parse_CIM_protocol();
		HID_Device_USBTask(&Joystick_HID_Interface);
	    parse_CIM_protocol();
		HID_Device_USBTask(&MouseAbs_HID_Interface);
	    parse_CIM_protocol();
		USB_USBTask();
	}
}

/** Configures the board hardware and chip peripherals for the demo's functionality. */
void setupHardware(void)
{
	/* Disable watchdog if enabled by bootloader/fuses */
	MCUSR &= ~(1 << WDRF);
	wdt_disable();

	/* Disable clock division */
	clock_prescale_set(clock_div_1);
	UART_Init(UART_BAUDRATE);

	LEDs_Init();
    blink_Led(8,200);

    // UART_Print("AsTeRICS HID actuator ready!\r\n");
	USB_Init();
}


/** Event handler for the library USB Connection event. */
void EVENT_USB_Device_Connect(void)
{
    LEDs_TurnOffLEDs(LED7);
    LEDs_TurnOnLEDs(LED6);
}

/** Event handler for the library USB Disconnection event. */
void EVENT_USB_Device_Disconnect(void)
{
    LEDs_TurnOffLEDs(LED6);
    LEDs_TurnOnLEDs(LED7);

}

/** Event handler for the library USB Configuration Changed event. */
void EVENT_USB_Device_ConfigurationChanged(void)
{
    LEDs_TurnOffLEDs(LED6|LED7);

	if (!(HID_Device_ConfigureEndpoints(&Keyboard_HID_Interface)))
	  LEDs_TurnOnLEDs(LED7);
	
	if (!(HID_Device_ConfigureEndpoints(&MouseRel_HID_Interface)))
	  LEDs_TurnOnLEDs(LED7);

  	if (!(HID_Device_ConfigureEndpoints(&Joystick_HID_Interface)))
	  LEDs_TurnOnLEDs(LED7);

	if (!(HID_Device_ConfigureEndpoints(&MouseAbs_HID_Interface)))
	  LEDs_TurnOnLEDs(LED7);

	USB_Device_EnableSOFEvents();
}

/** Event handler for the library USB Unhandled Control Request event. */
void EVENT_USB_Device_UnhandledControlRequest(void)
{
	HID_Device_ProcessControlRequest(&Keyboard_HID_Interface);
	HID_Device_ProcessControlRequest(&MouseRel_HID_Interface);
	HID_Device_ProcessControlRequest(&Joystick_HID_Interface);
	HID_Device_ProcessControlRequest(&MouseAbs_HID_Interface);
}

/** Event handler for the USB device Start Of Frame event. */
void EVENT_USB_Device_StartOfFrame(void)
{
	HID_Device_MillisecondElapsed(&Keyboard_HID_Interface);
	HID_Device_MillisecondElapsed(&MouseRel_HID_Interface);
	HID_Device_MillisecondElapsed(&Joystick_HID_Interface);
	HID_Device_MillisecondElapsed(&MouseAbs_HID_Interface);
}


uint8_t process_MouseRelReport(USB_MouseRelReport_Data_t*  MouseReport,
                                         uint16_t* const ReportSize)
{
	int16_t  param;
	
	if (!keys_in_InBuf(&input_mouse))	
		       return false;     // if no requests: leave 

    *ReportSize = sizeof(USB_MouseRelReport_Data_t);

    switch (read_InBuf(&input_mouse))   {     // requested feature
		case HID_CIM_FEATURE_MOUSEPOS_XY:     // get x/y mousepos from ringbuf and create HID report !
	    	param=read_InBuf(&input_mouse);   // X-Pos low byte
	    	param+=((int16_t)read_InBuf(&input_mouse))<<8;  // X-Pos high byte
	    	MouseReport->X = param;
	    	param=read_InBuf(&input_mouse);   // Y-Pos low byte
	    	param+=((int16_t)read_InBuf(&input_mouse))<<8;  // Y-Pos high byte
	    	MouseReport->Y = param;
			MouseReport->Button=mouse_button_saved;
		    return(true);
		
		case HID_CIM_FEATURE_MOUSEBUTTONSTATE: // get mouse-buttonstate from ringbuffer and create HID report !
		    MouseReport->Button=read_InBuf(&input_mouse);   // buttonstate byte
			mouse_button_saved=MouseReport->Button;
		    return(true);

		case HID_CIM_FEATURE_MOUSEWHEEL: // get mouse-wheel change from ringbuffer and create HID report !
		    MouseReport->Wheel=(int8_t)read_InBuf(&input_mouse);   // buttonstate byte
		    return(true);
	}
	return(false);
}



uint8_t process_MouseAbsReport(USB_MouseAbsReport_Data_t*  MouseReport,
										uint16_t* const ReportSize)

{
	
	if (!keys_in_InBuf(&input_absmouse))	
		       return false;     // if no requests: leave 

	  *ReportSize = sizeof(USB_MouseAbsReport_Data_t);

    switch (read_InBuf(&input_absmouse))   {  // requested feature
		case HID_CIM_FEATURE_MOUSEABSPOS_XY:   // get x/y mousepos from ringbuf and create HID report !
	    	MouseReport->X=((uint16_t)read_InBuf(&input_absmouse));    // X-Pos low byte
	    	MouseReport->X+=
			((uint16_t)read_InBuf(&input_absmouse))<<8; // X-Pos high byte
	    	MouseReport->Y = ((uint16_t)read_InBuf(&input_absmouse));   // Y-Pos low byte
	    	MouseReport->Y+=((uint16_t)read_InBuf(&input_absmouse))<<8;  // Y-Pos high byte
			MouseReport->Button=mouse_button_saved;
		    return(true);
		
		case HID_CIM_FEATURE_MOUSEBUTTONSTATE: // get mouse-buttonstate from ringbuffer and create HID report !
		    MouseReport->Button=read_InBuf(&input_absmouse);   // buttonstate byte
			mouse_button_saved=MouseReport->Button;
		    return(true);
		case HID_CIM_FEATURE_MOUSEWHEEL: // get mouse-wheel change from ringbuffer and create HID report !
  			MouseReport->Wheel= (int8_t)read_InBuf(&input_absmouse);
		    return(true);
	}
	return(false);
}



uint8_t process_KeyboardReport(USB_KeyboardReport_Data_t*  KeyboardReport,
                                         uint16_t* const ReportSize)
{
	static char release_last =0;
	static uint8_t hold_keys =0;
	static uint8_t modifiers[6];
	static uint8_t keys[6];
	uint8_t i;
	uint8_t act_key;


	if (release_last) {
	   // handle pending key release immediately
	   release_last=0;
	   if (hold_keys) hold_keys--;
	   KeyboardReport->KeyCode[hold_keys] = 0;
	   modifiers[hold_keys]=0;
	   KeyboardReport->Modifier=0;
	   for (i=0;i<hold_keys;i++)
	   {
	      KeyboardReport->KeyCode[i] = keys[i];
		  KeyboardReport->Modifier |= modifiers[i];
	   } 
	   *ReportSize = sizeof(USB_KeyboardReport_Data_t);
	   return true;
    }

	if (!keys_in_InBuf(&input_keyboard))
		       return false;     // if no requests: leave 

    *ReportSize = sizeof(USB_KeyboardReport_Data_t);

    switch (read_InBuf(&input_keyboard))   {  // requested feature

		case HID_CIM_FEATURE_KEYPRESS: // get keycode and modifier from ringbuffer and create HID report !
			if (hold_keys<6) {
				keys[hold_keys]=read_InBuf(&input_keyboard);
				modifiers[hold_keys] = read_InBuf(&input_keyboard);
				hold_keys++;
				KeyboardReport->Modifier=0;
 			    for (i=0;i<hold_keys;i++)
				{
			      KeyboardReport->KeyCode[i] = keys[i];
				  KeyboardReport->Modifier |= modifiers[i];
				} 
				release_last=1;
				return(true); 
			}		

		case HID_CIM_FEATURE_KEYHOLD: 
			if (hold_keys<6) {
				keys[hold_keys]=read_InBuf(&input_keyboard);
				modifiers[hold_keys] = read_InBuf(&input_keyboard);
				hold_keys++;

				KeyboardReport->Modifier=0;
 			    for (i=0;i<hold_keys;i++)
				{
			      KeyboardReport->KeyCode[i] = keys[i];
				  KeyboardReport->Modifier |= modifiers[i];
				} 
				return(true); 
			}		

		case HID_CIM_FEATURE_KEYRELEASE: 
			   if (hold_keys) {
			      act_key=read_InBuf(&input_keyboard);
				  read_InBuf(&input_keyboard);  // dummy-read modifier
 			      for (i=0;i<hold_keys;i++)
				  {
				     if (keys[i]==act_key)
					 {
					   while (i<hold_keys-1)
					   {
					      keys[i]=keys[i+1];
						  modifiers[i]=modifiers[i+1];
						  i++;
					   }
 	   			   	   hold_keys--;
			   	       KeyboardReport->KeyCode[hold_keys] = 0;
				       modifiers[hold_keys]=0;
					 }
				  }

				  KeyboardReport->Modifier=0;
 			      for (i=0;i<hold_keys;i++)
				  {
			        KeyboardReport->KeyCode[i] = keys[i];
				    KeyboardReport->Modifier |= modifiers[i];
				  } 

			      *ReportSize = sizeof(USB_KeyboardReport_Data_t);
				}		
			return(true);
	}
	return(false);
}

uint8_t process_JoystickReport(USB_JoystickReport_Data_t*  JoystickReport,
                                         uint16_t* const ReportSize)
{
    uint8_t * jp = (uint8_t *) JoystickReport;
	int i;
	if (!keys_in_InBuf(&input_joystick))	
		       return false;     // if no requests: leave 

    *ReportSize = sizeof(USB_JoystickReport_Data_t);

    switch (read_InBuf(&input_joystick))   {  // requested feature

		case HID_CIM_FEATURE_JOYSTICKUPDATE:
			for (i=0;i<19;i++)
			   *jp++=read_InBuf(&input_joystick);
			return(true);			   
	}
	return(false);
}


/** HID class driver callback function for the creation of HID reports to the host.
 *
 *  \param[in]     HIDInterfaceInfo  Pointer to the HID class interface configuration structure being referenced
 *  \param[in,out] ReportID  Report ID requested by the host if non-zero, otherwise callback should set to the generated report ID
 *  \param[in]     ReportType  Type of the report to create, either REPORT_ITEM_TYPE_In or REPORT_ITEM_TYPE_Feature
 *  \param[out]    ReportData  Pointer to a buffer where the created report should be stored
 *  \param[out]    ReportSize  Number of bytes written in the report (or zero if no report is to be sent
 *
 *  \return Boolean true to force the sending of the report, false to let the library determine if it needs to be sent
 */

bool CALLBACK_HID_Device_CreateHIDReport(USB_ClassInfo_HID_Device_t* const HIDInterfaceInfo,
                                         uint8_t* const ReportID,
                                         const uint8_t ReportType,
                                         void* ReportData,
                                         uint16_t* const ReportSize)
{
	uint8_t  send_report=false;

	*ReportSize = 0;

	if (HIDInterfaceInfo == &MouseRel_HID_Interface) 
	    send_report=process_MouseRelReport((USB_MouseRelReport_Data_t*)ReportData,ReportSize);
	else if (HIDInterfaceInfo == &MouseAbs_HID_Interface) 
	    send_report=process_MouseAbsReport((USB_MouseAbsReport_Data_t*)ReportData,ReportSize);
      
    else if (HIDInterfaceInfo == &Keyboard_HID_Interface)
	   send_report=process_KeyboardReport((USB_KeyboardReport_Data_t*)ReportData,ReportSize);
    else if (HIDInterfaceInfo == &Joystick_HID_Interface)
	   send_report=process_JoystickReport((USB_JoystickReport_Data_t*)ReportData,ReportSize);
 
	return (send_report);
}



/** HID class driver callback function for the processing of HID reports from the host.
 *
 *  \param[in] HIDInterfaceInfo  Pointer to the HID class interface configuration structure being referenced
 *  \param[in] ReportID    Report ID of the received report from the host
 *  \param[in] ReportType  The type of report that the host has sent, either REPORT_ITEM_TYPE_Out or REPORT_ITEM_TYPE_Feature
 *  \param[in] ReportData  Pointer to a buffer where the created report has been stored
 *  \param[in] ReportSize  Size in bytes of the received HID report
 */
void CALLBACK_HID_Device_ProcessHIDReport(USB_ClassInfo_HID_Device_t* const HIDInterfaceInfo,
                                          const uint8_t ReportID,
                                          const uint8_t ReportType,
                                          const void* ReportData,
                                          const uint16_t ReportSize)
{
	if (HIDInterfaceInfo == &Keyboard_HID_Interface)
	{
		uint8_t  LEDMask   = NO_LEDS;
		uint8_t* LEDReport = (uint8_t*)ReportData;

		/*
		if (*LEDReport & HID_KEYBOARD_LED_NUMLOCK)
		  LEDs_ToggleLEDs(LED7);
		
		if (*LEDReport & HID_KEYBOARD_LED_CAPSLOCK)
		  LEDs_ToggleLEDs(LED7);

		if (*LEDReport & HID_KEYBOARD_LED_SCROLLLOCK)
		  LEDs_ToggleLEDs(LED7);
		  
		LEDs_SetAllLEDs(LEDMask);
		*/

	}
}

void EVENT_USB_Device_ControlRequest(void)
{
	static uint8_t PROGMEM magic_init_bytes[] = {
		0x21, 0x26, 0x01, 0x07, 0x00, 0x00, 0x00, 0x00
	};


   if (USB_ControlRequest.wIndex == 2)  // joystick
   {
       switch (USB_ControlRequest.bRequest)
       {
           case HID_REQ_GetReport:
               if (USB_ControlRequest.bmRequestType == (REQDIR_DEVICETOHOST | REQTYPE_CLASS | REQREC_INTERFACE))
               {
                   Endpoint_ClearSETUP();
                   Endpoint_Write_Control_PStream_LE(&magic_init_bytes, sizeof(magic_init_bytes));
                   Endpoint_ClearOUT();
 			       //  blink_Led(6,50);  // check if request is performed
               }

               break;
       }
   }
}

