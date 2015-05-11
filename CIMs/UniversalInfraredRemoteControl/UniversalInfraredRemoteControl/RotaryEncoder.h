/*
 * RotaryEncoder.h
 *
 *
 * Created: 15.10.2014 06:05:37
 * Author:  Christoph Ulbinger
 * Project: universal remote control
 */ 

#include <avr/io.h>


#define DT PORTA1
#define CLK PORTA2
#define SW PORTA0

uint16_t rotarycounter;
uint8_t rotaryButton;
uint16_t rotaryButtonDelay;
uint8_t DialPos;
uint8_t Last_DialPos;

void getRotaryEncoderState(void);			// Read the position of the rotary encoder

void init_RotaryEncoder(void);				// Initialize the ports