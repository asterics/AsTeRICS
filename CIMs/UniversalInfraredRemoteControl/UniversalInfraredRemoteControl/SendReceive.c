/*
 * SendReceive.c
 *
 * Created: 29.10.2014 18:39:18
 * Author: Christoph
 */ 
#define F_CPU 8000000L

#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include "usb_serial.h"
#include "SendReceive.h"
#include "lcd.h"
#include "Buffer.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;

void Init_IR()
{
	// External Interrupt
	ir_input_ddr &= ~(1 << ir_input_pin);
	ir_input_port |= (1 << ir_input_pin);
	
	EICRA |= (1 << ISC00);    // set INT0 to trigger on ANY logic change
	DEACTIVATE_EXT_INT0;	  // deactivate external interrupt
	
	InitTimer3(0);			 //	 turn off timer 0
	InitPWM();				 //  PWM modulation initialization
}

void InitTimer3(uint8_t onoff)
{
	if(onoff == 0)
	{
		//Timer3
		TIMSK3 &= ~(1<<TOIE3);			//deactivate Timer/Counter3 Interrupt
		TCCR3B &= ~(1<<CS31);
		TCNT3 = 0;
	}
	else
	{
		//Timer3
		TIMSK3 |= (1<<TOIE3);			//activate Timer/Counter3 Interrupt
		TCCR3B |= (1<<CS31);
		TCNT3 = 0;
	}
}

void InitPWM()			                 //  PWM modulation initialization
{
	DDRB |= (1<<6);
	TCCR1B |= (1<<WGM13);
	TCCR1B |= (1<<WGM12);
	TCCR1A |= (1<<WGM11);
	TCCR1A &= ~(1<<WGM10);
	DEACTIVATE_PWM;
	// Set the value of TOP
	ICR1 = 25;		//38 kHz
	TCCR1A |= (1<<COM1B1) | (1<<COM1B0);
	OCR1B = (ICR1>>1); // 50% duty cycle
}

void Record_IR_Code()
{
	if((TCCR3B &(1<<CS31))== 0x00)			// Timer 3 activated?
	{
		InitTimer3(1);
	}
	if(irBufferCounter>=507)				// End of buffer?
	{
		DEACTIVATE_EXT_INT0;
		*irBufferPointer = 0xFF;			// End of IR code signature
		*(irBufferPointer+1) = 0xFF;		// End of IR code signature
		*(irBufferPointer+2) = 0xFF;		// End of IR code signature
		
		get_ir_status = 3;					// Record available

		CIM_frame.cim_feature=UIRC_CIM_FEATURE_SET_IR_MODE;
		CIM_frame.reply_code=CMD_REQUEST_WRITE_FEATURE;
		
		autoreply_num++;
		if (autoreply_num==0) 
			autoreply_num=0x80;
		
		CIM_frame.serial_number=autoreply_num;		
		CIM_frame.data[0]=get_ir_status;
		CIM_frame.data_size = 1;
		reply_DataFrame();			// Sending message to AsTeRICS
		stop_record_ir();			// Stopping the recording
		record_status(0);			// Display the status on LCD
	}
	else
	{
		*irBufferPointer = irBufferOverflow;		// time overflow
		*(irBufferPointer+1) = ((TCNT3>>8)&0xFF);   // High byte first
		*(irBufferPointer+2) = ((TCNT3)&0xFF);		// low byte
		irBufferPointer+=3;
		irBufferCounter+=3;
		TCNT3 = 0;
	}
}

ISR (INT0_vect)					// External Interrupt at every logic change
{
	Record_IR_Code();
}

void Clear_IR_LED()
{
	TCCR1A ^= (1<<COM1B1);
	TCCR1A ^= (1<<COM1B0);
	PORTB &= ~(1<<PORTB6);
}

ISR(TIMER3_OVF_vect)			// ISR of Timer 3
{
	if(get_ir_status == 1)		// Sending IR code
	{
			Clear_IR_LED();		// Preventing an undesired state
			TOGGLE_PWM;			// Change level of IR signal
			
			// Load counter value for time difference up to next change in level
			TCNT3 = 65535 - (*(irBufferPointer+1)*256 + *(irBufferPointer+2));
			if((TCNT3 == 0) || (irBufferCounter>=507))		// End of buffer
			{
				stop_send_ir();
			}
			irBufferPointer += 3;
			irBufferCounter += 3;
			
		}
		else if(get_ir_status == 2)		// Recording of IR signal
		{
			if(irBufferOverflow==76)	// Timeout after 5 seconds if no IR signal is detected
			{
				irBufferOverflow = 0;
				get_ir_status = 0;						// Idle
				CIM_frame.data[0]=get_ir_status;
				
				autoreply_num++;
				if (autoreply_num==0) 
					autoreply_num=0x80;
				
				CIM_frame.serial_number=autoreply_num;
				CIM_frame.cim_feature=UIRC_CIM_FEATURE_SET_IR_MODE;
				CIM_frame.reply_code=CMD_REQUEST_WRITE_FEATURE;
				CIM_frame.data_size = 1;
				reply_DataFrame();			// Sending message to AsTeRICS
				stop_record_ir();			// Stopping the recording
				record_status(1);			// Display the failed status on LCD
			}
			irBufferOverflow++;
		}
}

void start_record_ir()			   // initiates the sending process
{
	lcd_write_first_line("Recording...");
	get_ir_status = 2;		// Recording
	cli();
	InitBuffer();			// Reset the buffer for IR code
	InitTimer3(1);			// start the timer
	DEACTIVATE_PWM;			// deactivate the IR LED PWM
	ACTIVATE_EXT_INT0;		// Activate flank detection of IR signal
	sei();
}


void start_send_ir()			   // initiates the sending process
{
	cli();
	get_ir_status = 1; 	// Sending
	InitBuffer();		// Reset the buffer for IR code
	InitTimer3(1);		// start the timer
	InitPWM();			// Set up the IR LED PWM
	ACTIVATE_PWM;		// Activate the PWM modulation
	TCNT3 = 65535 - (*(irBufferPointer+1)*256 + *(irBufferPointer+2));		// Set the first value for the counter
	irBufferPointer += 3;	// set pointer to the next value
	sei();
}

void stop_record_ir()			   // stop the recording process
{
	cli();
	get_ir_status = 0; 	// Idle
	irBufferPointer = irBuffer;
	irBufferCounter = 0;
	irBufferOverflow = 0;
	InitTimer3(0);
	DEACTIVATE_PWM;
	sei();
}

void stop_send_ir()			   // initiates the sending process
{

	cli();
	get_ir_status = 0;
	irBufferPointer = irBuffer;
	irBufferCounter = 0;
	InitTimer3(0);
	DEACTIVATE_PWM;
	TCNT3 = 0;
	sei();
}