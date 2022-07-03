

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
#include <util/delay.h>

extern struct CIM_frame_t CIM_frame;
extern uint8_t CLEAR_INPUT;


uint8_t process_JoystickReport(void)
{
	int i;
	uint8_t * data = (uint8_t *) &gamepad_state;

	if (!keys_in_InBuf(&input_joystick))	
		       return false;     // if no requests: leave 

    switch (read_InBuf(&input_joystick))   {  // requested feature

		case HID_CIM_FEATURE_JOYSTICKUPDATE:
			for (i=0;i<19;i++)
			   data[i]=read_InBuf(&input_joystick);


			return(true);

	}
	return(false);
}

/** Main program entry point. This routine contains the overall program flow, including initial
 *  setup of all components and the main program loop.
 */
int main(void)
{
	setupHardware();
	init_CIM_frame();


	LEDs_SetAllLEDs(LEDMASK_USB_NOTREADY);
	sei();

			
	usb_gamepad_reset_state();

	for (;;)
	{

	    parse_CIM_protocol();
		process_JoystickReport();
		usb_gamepad_send();

		//HID_Device_USBTask(&Joystick_HID_Interface);
		//USB_USBTask();
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

	usb_init();
	while (!usb_configured()) /* wait */ ;
	_delay_ms(1000);
}





