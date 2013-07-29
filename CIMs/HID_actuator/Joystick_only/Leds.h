

/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
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
//#define LED7        (1 << 7)
#define ALL_LEDS    LED6
#define NO_LEDS     0

// Functions

static inline void LEDs_Init(void)
{
	LED_DDR=ALL_LEDS;  /* set Leds Pins on Led Port to output */
}

static inline void LEDs_TurnOnLEDs(const uint8_t LEDMask)
{
	LED_PORT &= ~LEDMask;
}

static inline void LEDs_TurnOffLEDs(const uint8_t LEDMask)
{
	LED_PORT |= LEDMask;
}

static inline void LEDs_SetAllLEDs(const uint8_t LEDMask)
{
	LED_PORT = ~LEDMask;
}

static inline void LEDs_ChangeLEDs(const uint8_t LEDMask,
                                   const uint8_t ActiveMask)
{
	LED_PORT = ((LED_PORT | LEDMask) & ~ActiveMask);
}

static inline void LEDs_ToggleLEDs(const uint8_t LEDMask)
{
	LED_PORT = (LED_PORT ^ LEDMask);
}

static inline uint8_t LEDs_GetLEDs(void)
{
	return (LED_PORT);
}

void blink_Leds(unsigned int blinks, unsigned int sleeptime);
void scroll_leds (void);


#endif

