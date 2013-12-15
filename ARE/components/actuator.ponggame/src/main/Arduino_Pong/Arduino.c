
/*
     AsTeRICS Arduino CIM Firmware

	 file: Arduino.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <inttypes.h>
#include <stdio.h>
#include <string.h>
#include <util/delay.h>
#include "Uart.h"
#include "Adc.h"
#include "Timer.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;
extern unsigned char PIND_Mask;
extern unsigned char PINB_Mask;

extern uint8_t COUNTERBITS;


uint16_t buttoncnt=0;

uint8_t old_PIND,old_PINB;
uint8_t autoreply_num=0x80;   // sequential number for automatic replies, 0x80-0xff

void setupHardware(void)
{
	MCUSR &= ~(1 << WDRF); 	// Disable watchdog if enabled by bootloader/fuses 
 	//	wdt_disable();
	//	LEDs_Init();	
	DDRB=(1<<2)+(1<<3);  // Leds for P1 and P2
	PORTB|=1;  // Pullup for GO button
	UART_Init(115200);
	ADC_Init();
	Timer_Init();
}



int main(void  )
{

 	setupHardware();
	init_CIM_frame();
	old_PIND=PIND;
	old_PINB=PINB;
    sei();           // enable global interrupts


	while (1)
	{  
		parse_CIM_protocol();

	    if (send_ADCFrame_now)   // this is updated in the timer ISR !!
	    {
		    send_ADCFrame_now=0;
		    autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;

		    CIM_frame.cim_feature=ARDUINO_CIM_FEATURE_ADCREPORT;
		    CIM_frame.serial_number=autoreply_num;
		    CIM_frame.reply_code=CMD_EVENT_REPLY;
			generate_ADCFrame();
			reply_DataFrame();
	     
		   //DDRB |= (1<<5); PORTB ^= (1<<5);  // indicate frame send with led
	    }

		if ((PINB & 1) != old_PINB)
		{
			buttoncnt++;
			if (buttoncnt >= 10000)
			{
			    buttoncnt=0;
				old_PINB= PINB & 1;
				if (old_PINB) 	COUNTERBITS|=4; else COUNTERBITS &= ~4;
			    check_PINChange_now=1;
			}
		}

		if (check_PINChange_now)  // this is updated in the timer ISR !!
		{ 
		    check_PINChange_now=0;

		    autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;
		    CIM_frame.cim_feature=ARDUINO_CIM_FEATURE_GET_PINVALUES;
		    CIM_frame.serial_number=autoreply_num;
			CIM_frame.reply_code=CMD_EVENT_REPLY;
		    generate_PINFrame();	     
		    reply_DataFrame();
		}
	}
}




