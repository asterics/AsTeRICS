

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

	// LED masks for the LEDs on the board
	#define ALL_LEDS    0xff
 	#define NO_LEDS     0
	#define LED0        (1 << 0)
	#define LED1        (1 << 1)
	#define LED2        (1 << 2)
	#define LED3        (1 << 3)
	#define LED4        (1 << 4)
	#define LED5        (1 << 5)
	#define LED6        (1 << 6)
	#define LED7        (1 << 7)

    // Functions

static inline void LEDs_Init(void)
{
	DDRC = 0x04;  /* set direction for the Latche Enable (LE) PortC.2 on the 
	                 eStick to output */
	PORTC = 0x04; /* make the Latch transparent */
	DDRB = 0xFF;  /* set Port PORTB to output */
	//DDRD &= ~(1<<PD5);
	//PORTD |= (1<<PD5);
}

static inline void LEDs_TurnOnLEDs(const uint8_t LEDMask)
{
	PORTB &= ~LEDMask;
}

static inline void LEDs_TurnOffLEDs(const uint8_t LEDMask)
{
	PORTB |= LEDMask;
}

static inline void LEDs_SetAllLEDs(const uint8_t LEDMask)
{
	PORTB = ~LEDMask;
}

static inline void LEDs_ChangeLEDs(const uint8_t LEDMask,
                                   const uint8_t ActiveMask)
{
	PORTB = ((PORTB | LEDMask) & ~ActiveMask);
}

static inline void LEDs_ToggleLEDs(const uint8_t LEDMask)
{
	PORTB = (PORTB ^ LEDMask);
}

static inline uint8_t LEDs_GetLEDs(void)
{
	return (PORTB);
}

void blink_Leds(unsigned int blinks, unsigned int sleeptime);
void scroll_leds (void);


#endif

