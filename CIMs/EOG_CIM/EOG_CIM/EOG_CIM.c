
/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
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

#include "EOG_CIM.h"
#include "Uart.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;
extern uint8_t CLEAR_INPUT;

/** Main program entry point. This routine contains the overall program flow, including initial
 *  setup of all components and the main program loop.
 */
int main(void)
{
	setupHardware();
	init_CIM_frame();
	sei();

	for (;;)
	{
	    parse_CIM_protocol();
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

    // UART_Print("AsTeRICS HID actuator ready!\r\n");

}



