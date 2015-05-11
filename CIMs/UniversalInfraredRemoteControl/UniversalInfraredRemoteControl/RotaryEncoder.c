/*
 * RotaryEncoder.c
 *
 *
 * Created: 15.10.2014 06:05:37
 * Author:  Christoph Ulbinger
 * Project:  universal remote control
 */ 

#include <avr/io.h>

#include "RotaryEncoder.h"
#include "interface.h"
#include "settings.h"

void init_RotaryEncoder()					// Initialize the ports
{
	DDRA &= ~(1<<DT);
	DDRA &= ~(1<<CLK);
	DDRA &= ~(1<<SW);
	PORTA |= (1<<SW);
	PORTA |= (1<<CLK);
	PORTA |= (1<<DT);
	
	DialPos = 0;
	Last_DialPos = 0;
	rotaryButton = 0;
	rotaryButtonDelay = 0;
	rotarycounter = 0;
	
	init_timer0();
}

void getRotaryEncoderState()			// Read the position of the rotary encoder
{		
		/* Read the status of the dial */
		DialPos = (((PINA&(1<<CLK))>>1) | ((PINA&(1<<DT))>>1));

		/* Is the dial being turned anti-clockwise? */
		if (DialPos == 3 && Last_DialPos == 1)
		{
			rotarycounter--;
			if(rotarycounter==(65536-joystick_sensibility))		// Sensibility
			{
				interfaceUp();
				rotarycounter = 0;
			}
			
		}
		
		/* Is the dial being turned clockwise? */
		if (DialPos == 3 && Last_DialPos == 2)
		{
			rotarycounter++;
			if(rotarycounter==(joystick_sensibility))			// Sensibility
			{
				interfaceDown();
				rotarycounter = 0;
			}
		}
		
		/* GEt state of the switch */
		if((PINA&(1<<SW))==0x00)
		{
			if(rotaryButtonDelay == joystick_speed)	//Enable Switch again after joystick_speed, 1 == 0,00816 sec
			{
				rotaryButtonDelay = 0;
			}
			
			if(rotaryButtonDelay == 0)
			{
				interfaceSelect();
			}			
			rotaryButtonDelay++;
		}
		else
		{
			rotaryButtonDelay = 0;
		}
		
		/* Save the state of the rotary encoder */
		Last_DialPos = DialPos;
}