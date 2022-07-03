

/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
	 file: HID_actuator.h
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
 *  Header file for Mouse.c.
 */

	/* Includes: */
		#include <avr/io.h>
		#include <avr/wdt.h>
		#include <avr/interrupt.h>
		#include <avr/power.h>
		#include <avr/interrupt.h>
		#include <stdbool.h>
		#include <string.h>

	/* Function Prototypes: */
		void setupHardware(void);
