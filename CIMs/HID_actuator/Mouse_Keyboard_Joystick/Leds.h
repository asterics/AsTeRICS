

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

	 file: Leds.h
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010

*/

#ifndef _LEDS_H_
#define _LEDS_H_

#include <avr/io.h>

#define LED_PORT PORTD
#define LED_DDR DDRD

// LED masks for the LEDs on the board
//#define LED0        (1 << 0)
//#define LED1        (1 << 1)
//#define LED2        (1 << 2)
//#define LED3        (1 << 3)
//#define LED4        (1 << 4)
//#define LED5        (1 << 5)
#define LED6        (1 << 6)
#define LED7        (1 << 7)
#define ALL_LEDS    LED6 | LED7
#define NO_LEDS     0

// Functions

static inline void LEDs_Init(void)
{
	LED_DDR=ALL_LEDS;  /* set Leds Pins on Led Port to output */
}

static inline void LEDs_TurnOffLEDs(const uint8_t LEDMask)
{
	LED_PORT &= ~LEDMask;
}

static inline void LEDs_TurnOnLEDs(const uint8_t LEDMask)
{
	LED_PORT |= LEDMask;
}

static inline void LEDs_SetAllLEDs(const uint8_t LEDMask)
{
	LED_PORT = LEDMask;
}

static inline void LEDs_ChangeLEDs(const uint8_t LEDMask,
                                   const uint8_t ActiveMask)
{
	LED_PORT = ((LED_PORT & ~LEDMask) | ActiveMask);
}

static inline void LEDs_ToggleLEDs(const uint8_t LEDMask)
{
	LED_PORT = (LED_PORT ^ LEDMask);
}

static inline uint8_t LEDs_GetLEDs(void)
{
	return (LED_PORT);
}

void blink_Led(unsigned int blinks, unsigned int sleeptime);



#endif

