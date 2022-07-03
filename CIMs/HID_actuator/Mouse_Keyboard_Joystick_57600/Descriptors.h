

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


 	 file: Descriptors.h
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010

	 based upon the LUFA Library,
     Copyright (C) Dean Camera, 2010.         
	 dean [at] fourwalledcubicle [dot] com
      www.fourwalledcubicle.com

*/

 
#ifndef _DESCRIPTORS_H_
#define _DESCRIPTORS_H_

#include <LUFA/Drivers/USB/USB.h>
#include <avr/pgmspace.h>


	/* Type Defines: */
	/** Type define for the device configuration descriptor structure. This must be defined in the
	 *  application code, as the configuration descriptor contains several sub-descriptors which
	 *  vary between devices, and which describe the device's usage to the host.
	 */

	typedef struct
	{
		USB_Descriptor_Configuration_Header_t Config;

		USB_Descriptor_Interface_t            HID1_KeyboardInterface;
		USB_HID_Descriptor_HID_t              HID1_KeyboardHID;
        USB_Descriptor_Endpoint_t             HID1_ReportINEndpoint;
		USB_Descriptor_Interface_t            HID2_MouseRelInterface;
		USB_HID_Descriptor_HID_t              HID2_MouseRelHID;
        USB_Descriptor_Endpoint_t             HID2_ReportINEndpoint;
		USB_Descriptor_Interface_t            HID3_JoystickInterface;
		USB_HID_Descriptor_HID_t              HID3_JoystickHID;
        USB_Descriptor_Endpoint_t             HID3_ReportINEndpoint;
		USB_Descriptor_Interface_t            HID4_MouseAbsInterface;
		USB_HID_Descriptor_HID_t              HID4_MouseAbsHID;
        USB_Descriptor_Endpoint_t             HID4_ReportINEndpoint;

	} USB_Descriptor_Configuration_t;
				
/* Macros: */
	/** Endpoint number of the Keyboard HID reporting IN endpoint. */
	#define KEYBOARD_IN_EPNUM         1

	/** Endpoint number of the Mouse HID reporting IN endpoint. */
	#define MOUSEREL_IN_EPNUM         2

	/** Endpoint number of the Joystick HID reporting IN endpoint. */
	#define JOYSTICK_IN_EPNUM         3 

	/** Endpoint number of the Absolute Mouse HID reporting IN endpoint. */
	#define MOUSEABS_IN_EPNUM         4

	/** Size in bytes of each of the HID reporting IN and OUT endpoints. */
	#define HID_EPSIZE                8

/* Function Prototypes: */
	uint16_t CALLBACK_USB_GetDescriptor(const uint16_t wValue,
	                                    const uint8_t wIndex,
	                                    const void** const DescriptorAddress)
	                                    ATTR_WARN_UNUSED_RESULT ATTR_NON_NULL_PTR_ARG(3);


  typedef struct  {
	uint8_t Button; /**< Button mask for currently pressed buttons in the mouse. */
	uint16_t  X;    /**< Current X position of the mouse. */
	uint16_t  Y;    /**< Current Y position on the mouse. */
	int8_t  Wheel;  /**< Current Y position on the mouse. */
  } USB_MouseAbsReport_Data_t;


  typedef struct  {
	uint8_t Button; /**< Button mask for currently pressed buttons in the mouse. */
	int16_t  X; /**< Current X position of the mouse. */
	int16_t  Y; /**< Current Y position on the mouse. */
	int8_t  Wheel; /**< Current Y position on the mouse. */
  } USB_MouseRelReport_Data_t;


  typedef struct {
	// digital buttons, 0 = off, 1 = on

	uint8_t square_btn : 1;
	uint8_t cross_btn : 1;
	uint8_t circle_btn : 1;
	uint8_t triangle_btn : 1;

	uint8_t l1_btn : 1;
	uint8_t r1_btn : 1;
	uint8_t l2_btn : 1;
	uint8_t r2_btn : 1;

	uint8_t select_btn : 1;
	uint8_t start_btn : 1;
	uint8_t : 2;
	uint8_t ps_btn : 1;
	uint8_t : 3;

	// digital direction, use the dir_* constants(enum)
	// 8 = center, 0 = up, 1 = up/right, 2 = right, 3 = right/down
	// 4 = down, 5 = down/left, 6 = left, 7 = left/up

	uint8_t direction;

	// left and right analog sticks, 0x00 left/up, 0x80 middle, 0xff right/down

	uint8_t l_x_axis;
	uint8_t l_y_axis;
	uint8_t r_x_axis;
	uint8_t r_y_axis;

	uint8_t unknown[4];

	// button axis, 0x00 = unpressed, 0xff = fully pressed

	uint8_t triangle_axis;
	uint8_t circle_axis;
	uint8_t cross_axis;
	uint8_t square_axis;

	uint8_t l1_axis;
	uint8_t r1_axis;
	uint8_t l2_axis;
	uint8_t r2_axis;
 } USB_JoystickReport_Data_t;



#endif
