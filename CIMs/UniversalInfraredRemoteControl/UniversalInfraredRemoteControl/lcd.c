/*
* lcd.c
*
* Description : initialzes the lcd module and send characters to it
*
* Created: 09.09.2014 17:37:37
* Author:  Christoph Ulbinger
* Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
*
*/
#define F_CPU 8000000UL

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <string.h>

#include "lcd.h"

void lcd_port_init()						// initialize the ports for the lcd module
{
	 /*
	 Set Direction of Data Bus Lines
	 PD4 - PD7
	 */	 
	 DirectionControl |= 0xFF;
	 DirectionData |= 0xFF;
	 

	 /*
	 Set Direction of Register Select
	 PB0
	 */
	 PortControl |= (1<<RegisterSelect);
	 
	 /*
	 Set Direction of Enable
	 PB1
	 */
	 PortControl |= (1<<Enable);  
	 
	 PortControl &= ~(1<<ReadWrite);
}

void lcd_init_4Bit()						// initialize the lcd module in a 4 bit mode
{
	//Power Up Delay
	_delay_ms(30);
		
	//Delete RegisterSelect and Enable to write instructions
	PortControl &= ~(1<<RegisterSelect);
	PortControl &= ~(1<<Enable);
	
	//	Reset LCD Controller
	lcd_write_4Bit(lcd_FunctionReset);
	_delay_ms(10);
	
	lcd_write_4Bit(lcd_FunctionReset);
	_delay_us(200);
	
	lcd_write_4Bit(lcd_FunctionReset);
	_delay_us(200);
	
	//Set LCD Controller to 4 Bit mode
	lcd_write_4Bit(lcd_FunctionSet4bit);
	_delay_us(80);
	
	// Set LCD Controller through Instructions
	lcd_write_instruction_4Bit(lcd_FunctionSet4bit);
	_delay_us(80);

	// Display Off instruction
	lcd_write_instruction_4Bit(lcd_DisplayOff);
	_delay_us(80);

	// Clear Display instruction
	lcd_write_instruction_4Bit(lcd_Clear);
	_delay_ms(4);

	// Entry Mode Set instruction
	lcd_write_instruction_4Bit(lcd_EntryMode);
	_delay_us(80);
	
	// Display On instruction
	lcd_write_instruction_4Bit(lcd_DisplayOn);
	_delay_us(80);
}

void lcd_write_instruction_4Bit(uint8_t nibble)		// prepare to write nibbles to the lcd module
{	
	PortControl &= ~(1<<RegisterSelect);            // set Instruction Register (RS -> low)
	PortControl &= ~(1<<Enable);
	lcd_write_4Bit(nibble);						    // write the upper Nibble
	lcd_write_4Bit(nibble << 4);				    // write the lower Nibble
}

void lcd_write_4Bit(uint8_t nibble)					// write data to the lcd module
{
	PortData &= ~(1<<Data7);
	if (nibble & 1<<7)								// MSB, Bit 7
	{
		 PortData |= (1<<Data7);
	}

	PortData &= ~(1<<Data6);
	if (nibble & 1<<6)								// Bit 6
	{
		PortData |= (1<<Data6);
	}
	
	PortData &= ~(1<<Data5);						// Bit 5
	if (nibble & 1<<5) 
	{
		PortData |= (1<<Data5);
	}
	
	PortData &= ~(1<<Data4);
	if (nibble & 1<<4)								// LSB, Bit 4
	{
		PortData |= (1<<Data4);
	}
	
	PortControl |= (1<<Enable);
	_delay_us(1);
	PortControl &= ~(1<<Enable); 
	_delay_us(1);
}

void lcd_write_string(char* string)				// write one string to the lcd module
{
	volatile int index = 0;
	while (string[index] != 0)
	{
		lcd_write_char(string[index]);
		index++;
		_delay_us(80);
	}
}

void lcd_write_char(uint8_t character)					// write ine character to the lcd module
{
	PortControl |= (1<<RegisterSelect);              
	PortControl &= ~(1<<Enable);
	lcd_write_4Bit(character);                          // write the upper Nibble
	lcd_write_4Bit(character << 4);                     // write the lower Nibble
}

void lcd_write_first_line(char* string)
{
	lcd_write_instruction_4Bit(lcd_Clear);
	_delay_ms(4);
	lcd_write_instruction_4Bit(lcd_SetCursor | lcd_Line_One);
	_delay_ms(4);
	lcd_write_string(string);
	
}

void lcd_write_second_line(char* string)
{
	lcd_write_instruction_4Bit(lcd_SetCursor | lcd_Line_Two);
	_delay_us(80);
	lcd_write_string(string);
}