

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

typedef struct  {
	int8_t  X; /**< Current absolute joystick X position, as a signed 8-bit integer */
	int8_t  Y; /**< Current absolute joystick Y position, as a signed 8-bit integer */
	uint8_t Button; /**< Bit mask of the currently pressed joystick buttons */
} USB_JoystickReport_Data_t;

int8_t joy_x_saved=0;
int8_t joy_y_saved=0;
uint8_t joy_button_saved=0;
uint8_t mouse_button_saved=0;


#ifdef _USE_ABS_MOUSEPOINTER_
  typedef struct  {
	uint8_t Button; /**< Button mask for currently pressed buttons in the mouse. */
	uint16_t  X; /**< Current X position of the mouse. */
	uint16_t  Y; /**< Current Y position on the mouse. */
  } USB_MouseAbsReport_Data_t;

  uint8_t PrevMouseHIDReportBuffer[sizeof(USB_MouseAbsReport_Data_t)];
#else
  typedef struct  {
	uint8_t Button; /**< Button mask for currently pressed buttons in the mouse. */
	int8_t  X; /**< Current X position of the mouse. */
	int8_t  Y; /**< Current Y position on the mouse. */
	int8_t  Wheel; /**< Current Y position on the mouse. */
  } USB_MouseRelReport_Data_t;

  /** Buffer to hold the previously generated Mouse HID report, for comparison purposes inside the HID class driver. */
  uint8_t PrevMouseHIDReportBuffer[sizeof(USB_MouseRelReport_Data_t)];
#endif


/** Buffer to hold the previously generated Keyboard HID report, for comparison purposes inside the HID class driver. */
uint8_t PrevKeyboardHIDReportBuffer[sizeof(USB_KeyboardReport_Data_t)];
/** Buffer to hold the previously generated Joystick report, for comparison purposes inside the HID class driver. */
uint8_t PrevJoystickHIDReportBuffer[sizeof(USB_JoystickReport_Data_t)];





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
	
USB_ClassInfo_HID_Device_t Mouse_HID_Interface =
	{
		.Config =
			{
				.InterfaceNumber              = 1,

				.ReportINEndpointNumber       = MOUSE_IN_EPNUM,
				.ReportINEndpointSize         = HID_EPSIZE,

				.ReportINEndpointDoubleBank   = false,
				.PrevReportINBuffer           = PrevMouseHIDReportBuffer,
				.PrevReportINBufferSize       = sizeof(PrevMouseHIDReportBuffer),
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


/** Main program entry point. This routine contains the overall program flow, including initial
 *  setup of all components and the main program loop.
 */
int main(void)
{
	setupHardware();
	init_CIM_frame();


	LEDs_SetAllLEDs(LEDMASK_USB_NOTREADY);
	sei();

	for (;;)
	{
	    parse_CIM_protocol();
		HID_Device_USBTask(&Keyboard_HID_Interface);
	    parse_CIM_protocol();
		HID_Device_USBTask(&Mouse_HID_Interface);
	    parse_CIM_protocol();
		HID_Device_USBTask(&Joystick_HID_Interface);
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
	UART_Init(115200);

	LEDs_Init();
    blink_Leds(10,80);

    // UART_Print("AsTeRICS HID actuator ready!\r\n");
    blink_Leds(20,40);

	USB_Init();
}


/** Event handler for the library USB Connection event. */
void EVENT_USB_Device_Connect(void)
{
    LEDs_SetAllLEDs(LEDMASK_USB_ENUMERATING);
}

/** Event handler for the library USB Disconnection event. */
void EVENT_USB_Device_Disconnect(void)
{
    LEDs_SetAllLEDs(LEDMASK_USB_NOTREADY);
}

/** Event handler for the library USB Configuration Changed event. */
void EVENT_USB_Device_ConfigurationChanged(void)
{
	LEDs_SetAllLEDs(LEDMASK_USB_READY);

	if (!(HID_Device_ConfigureEndpoints(&Keyboard_HID_Interface)))
	  LEDs_SetAllLEDs(LEDMASK_USB_ERROR);
	
	if (!(HID_Device_ConfigureEndpoints(&Mouse_HID_Interface)))
	  LEDs_SetAllLEDs(LEDMASK_USB_ERROR);

  	if (!(HID_Device_ConfigureEndpoints(&Joystick_HID_Interface)))
	  LEDs_SetAllLEDs(LEDMASK_USB_ERROR);


	USB_Device_EnableSOFEvents();
}

/** Event handler for the library USB Unhandled Control Request event. */
void EVENT_USB_Device_UnhandledControlRequest(void)
{
	HID_Device_ProcessControlRequest(&Keyboard_HID_Interface);
	HID_Device_ProcessControlRequest(&Mouse_HID_Interface);
	HID_Device_ProcessControlRequest(&Joystick_HID_Interface);
}

/** Event handler for the USB device Start Of Frame event. */
void EVENT_USB_Device_StartOfFrame(void)
{
	HID_Device_MillisecondElapsed(&Keyboard_HID_Interface);
	HID_Device_MillisecondElapsed(&Mouse_HID_Interface);
	HID_Device_MillisecondElapsed(&Joystick_HID_Interface);
}


#ifdef _USE_ABS_MOUSEPOINTER_
uint8_t process_MouseReport(USB_MouseAbsReport_Data_t*  MouseReport,
										uint16_t* const ReportSize)

#else
uint8_t process_MouseReport(USB_MouseRelReport_Data_t*  MouseReport,
                                         uint16_t* const ReportSize)
#endif
{
	int16_t  param;
	if (!keys_in_InBuf(&input_mouse))	
		       return false;     // if no requests: leave 

	#ifdef _USE_ABS_MOUSEPOINTER_
	  *ReportSize = sizeof(USB_MouseAbsReport_Data_t);
    #else
	  *ReportSize = sizeof(USB_MouseRelReport_Data_t);
	#endif


    switch (read_InBuf(&input_mouse))   {  // requested feature
		case HID_CIM_FEATURE_MOUSEPOS_XY:   // get x/y mousepos from ringbuf and create HID report !
		    #ifdef _USE_ABS_MOUSEPOINTER_
		    	MouseReport->X =(uint16_t)read_InBuf(&input_mouse);    // X-Pos low byte
		    	MouseReport->X+=((uint16_t)read_InBuf(&input_mouse))<<8; // X-Pos high byte
		    	MouseReport->Y =(uint16_t)read_InBuf(&input_mouse);   // Y-Pos low byte
		    	MouseReport->Y+=((uint16_t)read_InBuf(&input_mouse))<<8;  // Y-Pos high byte
			#else
		    	param=read_InBuf(&input_mouse);   // X-Pos low byte
		    	param+=((int16_t)read_InBuf(&input_mouse))<<8;  // X-Pos high byte
		    	MouseReport->X = param;
		    	param=read_InBuf(&input_mouse);   // Y-Pos low byte
		    	param+=((int16_t)read_InBuf(&input_mouse))<<8;  // Y-Pos high byte
		    	MouseReport->Y = param;
			#endif
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
	int16_t  param;
	if (!keys_in_InBuf(&input_joystick))	
		       return false;     // if no requests: leave 

    *ReportSize = sizeof(USB_JoystickReport_Data_t);
    switch (read_InBuf(&input_joystick))   {  // requested feature

		case HID_CIM_FEATURE_JOY1POSANALOG:
		    param=read_InBuf(&input_joystick);   // X-Pos low byte
		    param+=((int16_t)read_InBuf(&input_joystick))<<8;  // X-Pos high byte
		    JoystickReport->X = param;
		    param=read_InBuf(&input_joystick);   // Y-Pos low byte
		    param+=((int16_t)read_InBuf(&input_joystick))<<8;  // Y-Pos high byte
		    JoystickReport->Y = param;
			joy_x_saved =JoystickReport->X;
			joy_y_saved =JoystickReport->Y;
			JoystickReport->Button = joy_button_saved;
			return(true);

		// TODO: implement features for 2nd and 3rd Joystick if necessary

		case HID_CIM_FEATURE_JOYBUTTONSTATE:
		    param=read_InBuf(&input_joystick);   // buttonstate low byte
		    JoystickReport->Button = param;
			param=read_InBuf(&input_joystick);   // buttonstate high byte 
						                                     // (TODO: include high byte into HID report)
			joy_button_saved=JoystickReport->Button;
			JoystickReport->X = joy_x_saved;
			JoystickReport->Y = joy_y_saved;
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

	if (HIDInterfaceInfo == &Mouse_HID_Interface) 
      #ifdef _USE_ABS_MOUSEPOINTER_
	    send_report=process_MouseReport((USB_MouseAbsReport_Data_t*)ReportData,ReportSize);
      #else
	    send_report=process_MouseReport((USB_MouseRelReport_Data_t*)ReportData,ReportSize);
      #endif
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
		if (*LEDReport & HID_KEYBOARD_LED_NUMLOCK)
		  LEDMask |= LED3;
		
		if (*LEDReport & HID_KEYBOARD_LED_CAPSLOCK)
		  LEDMask |= LED3;

		if (*LEDReport & HID_KEYBOARD_LED_SCROLLLOCK)
		  LEDMask |= LED3;
		  
		LEDs_SetAllLEDs(LEDMask);

	}
}
