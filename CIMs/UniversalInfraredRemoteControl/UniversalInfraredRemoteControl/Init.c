/*
 * Init.c
 *
 * Created: 29.10.2014 18:39:18
 *  Author: Christoph
 */ 

#define F_CPU 8000000L
#define CPU_PRESCALE(n) (CLKPR = 0x80, CLKPR = (n))

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <string.h>

#include "Init.h"
#include "lcd.h"
#include "Init.h"
#include "fat.h"
#include "CimProtocol.h"

char bootsequence[8];

void init()
{
	autoreply_num=0x80;   // sequential number for automatic replies, 0x80-0xff
	
	CPU_PRESCALE(1);	
	
	DDRD |= (1<<PORTD7);
	PORTD &= ~(1<<PORTD7);
	
	DDRD |= (1<<PORTD6);
	PORTD &= ~(1<<PORTD6);
			
	
	// Init LCD Module
	lcd_port_init();
	lcd_init_4Bit();
	
	displayMain();
	_delay_ms(500);
	
	
	lcd_write_first_line("      5%");
	Bootsequence(0,0);
	lcd_write_second_line(bootsequence);
	
	// Init USB Serial
	usb_init();
	//while(!usb_configured());
	_delay_ms(500);
	
	lcd_write_first_line("     10%");
	lcd_write_second_line(bootsequence);
	
	//Init RotaryEncoder Module
	init_RotaryEncoder();
	
	
	lcd_write_first_line("     15%");
	Bootsequence(1,1);
	lcd_write_second_line(bootsequence);
	
	//init IR Receiver/Sending Module
	Init_IR();
	
	
	lcd_write_first_line("     20%");
	Bootsequence(2,2);
	lcd_write_second_line(bootsequence);
	
	// init SD Card	
	uint8_t init_process = 0;
	while ((mmc_init() !=0)&&(init_process<6))
	{
		init_process++;
		_delay_ms(250);
	}
	
	if(init_process>=6)
	{
		lcd_write_first_line("     45%");		// Error init SD Card
		lcd_write_second_line("Error at SD Card");
		_delay_ms(2000);
		LoadSettingsWithoutSDcard();
	}
	else
	{
		lcd_write_first_line("     50%");		// init SD Card OK
		Bootsequence(2,4);
		lcd_write_second_line(bootsequence);
		_delay_ms(100);
		
		fat_init();
		
		lcd_write_first_line("     65%");
		Bootsequence(4,6);
		lcd_write_second_line(bootsequence);
		_delay_ms(100);
		
		LoadSettingsFromSDcard();
		LoadDevices();
	}
	
	InitBuffer();
	
	
	init_CIM_frame();
	
	lcd_write_first_line("    100%");
	Bootsequence(2,7);
	lcd_write_second_line(bootsequence);
	_delay_ms(1000);
	
	init_menu();

	
	sei();
	
}

void Bootsequence(uint8_t start, uint8_t stop)
{
	uint8_t i;
	for(i = start; i<=stop;i++)
		bootsequence[i] = 0xFF;
	
	if(i < 8)
		bootsequence[i+1] = 0;
}

