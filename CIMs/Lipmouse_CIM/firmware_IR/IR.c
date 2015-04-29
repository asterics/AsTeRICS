/*
     AsTeRICS LipMouse CIM Firmware

	 file: IR.c
	 Version: 0.1
	 Author: Christoph Ulbinger (FHTW)
	 Date: 03/04/2015

*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include "CimProtocol.h"
#include <util/delay.h>
#include "IR.h"
#include "CimProtocol.h"

extern struct CIM_frame_t CIM_frame;

void Init_IR()			// initializes the external interrupt
{
	DEACTIVATE_ICP1();
	Init_IR_LED();
	Init_ICP1();
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


/*

	PWM Modulation
	
*/

void ACTIVATE_PWM()
{
	TIMSK2 = 0;   // interrupts off
    TCCR2A= 0;    // normal mode
    TCNT2 = 245;    // 38kHz
    TCCR2B= (1<<CS21); // Prescaler 8
    TIFR2=0;   	  //clear interrupts
    TIMSK2=(1<<TOIE2);
}

void DEACTIVATE_PWM()
{
	TIMSK2 = 0;   // interrupts off
    TCCR2A= 0;    // normal mode
    TCCR2B= 0; // Prescaler 8
    TIFR2=0;   	  //clear interrupts
    TIMSK2=0;
}

void TOGGLE_PWM()
{
	TCNT2 = 245;
	TCCR2B ^= (1<<CS21);
	TIMSK2 ^= (1<<TOIE2);
}

/*

	IR LED Init, Set & Clear
	
*/

void Init_IR_LED()
{
	DDRD |= (1<<IR_LED1);
	DDRD |= (1<<IR_LED2);
	DDRD |= (1<<IR_LED3);

	Clear_IR_LED();
}

void Set_IR_LED()
{
	#ifdef IR_LED_WITH_MOSFET				// For the use of a lipmouse with mosfet amplified IR Leds
		PORTD &= ~(1<<IR_LED1) & ~(1<<IR_LED2) & ~(1<<IR_LED3);  // High sets the IR Leds
	#else
		PORTD |= (1<<IR_LED1) | (1<<IR_LED2) | (1<<IR_LED3);     // Low clears the IR Leds
	#endif
}

void Clear_IR_LED()
{
	#ifdef IR_LED_WITH_MOSFET				// For the use of a lipmouse with mosfet amplified IR Leds
		PORTD |= (1<<IR_LED1) | (1<<IR_LED2) | (1<<IR_LED3);     // Low sets the IR Leds
	#else
		PORTD &= ~(1<<IR_LED1) & ~(1<<IR_LED2) & ~(1<<IR_LED3);  // High clears the IR Leds		
	#endif	
}

void Toggle_IR_LED()
{
	PORTD ^= (1<<IR_LED1) + (1<<IR_LED2) + (1<<IR_LED3);	// Toggle IR Leds
}

/*

	IR Recording Routine
	
*/

void Record_IR_Code()
{
	if((TCCR3B &(1<<CS31))== 0x00)			// Timer 3 activated?
	{
		InitTimer3(1);
	}
	if(irBufferCounter>=507)
	{
		DEACTIVATE_ICP1();

		*irPointer 	   = 0xFF;		// End of IR Code
		*(irPointer+1) = 0xFF;		// End of IR Code
		*(irPointer+2) = 0xFF;		// End of IR Code
		InitTimer3(0);

		CIM_frame.cim_feature=LIPMOUSE_CIM_FEATURE_SET_IR_MODE;
		CIM_frame.reply_code=CMD_REQUEST_WRITE_FEATURE;
		autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;
		CIM_frame.serial_number=autoreply_num;
		get_ir_status = 3;						// Record available	
		CIM_frame.data[0]=get_ir_status;
		CIM_frame.data_size = 1;
		reply_DataFrame();				

	}
	else
	{
		*irPointer = irBufferOverflow;
		*(irPointer+1) = ((TCNT3>>8)&0xFF);
		*(irPointer+2) = ((TCNT3)&0xFF);
		irPointer+=3;
		irBufferOverflow = 0;
		irBufferCounter+=3;
		TCNT3 = 0;
	}
}

/*

	IR Detection via Input Capture
	
*/

ISR (TIMER1_CAPT_vect)					//  Interrupt at logic change
{
	Record_IR_Code();	
	TOGGLE_ICP1();
}

/*

	IR PWM Modulation via Timer2 Overflow
	
*/

ISR(TIMER2_OVF_vect)
{
	TCNT2 = 245;
    Toggle_IR_LED();
}

/*

	IR Sending Interrupt
	
*/

ISR(TIMER3_OVF_vect)
{	
	if(get_ir_status == 1)
	{		
		Clear_IR_LED();
		TOGGLE_PWM();
		
		TCNT3 = 65535 - (*(irPointer+1)*256 + *(irPointer+2));
		if((TCNT3 == 0) || (irBufferCounter>=507))
		{
			stop_send_ir();
		}
		irPointer += 3;	
		irBufferCounter += 3;	
		
	}
	else if(get_ir_status == 2)
	{	
		if(irBufferOverflow==76)
		{
			irBufferOverflow = 0;
			get_ir_status = 0;						// Idle	
		CIM_frame.cim_feature=LIPMOUSE_CIM_FEATURE_SET_IR_MODE;
		CIM_frame.reply_code=CMD_REQUEST_WRITE_FEATURE;
		autoreply_num++; 
		    if (autoreply_num==0) autoreply_num=0x80;
		CIM_frame.serial_number=autoreply_num;
		CIM_frame.data[0]=get_ir_status;
		CIM_frame.data_size = 1;
		reply_DataFrame();		
			stop_record_ir();
		}
		irBufferOverflow++;
	}
}

/*

	ICP Init, Activation, Deactivation and Toggle Routine
	
*/


void Init_ICP1()
{
     DDRD  &= ~(1<<PORTD4);  		// ICP Input
	 PORTD &= ~(1<<PORTD4);         // No Pullup on ICP
}

void ACTIVATE_ICP1()
{
		// Timer1 initialisieren:
	 TCCR1A = 0;                         // normal mode, keine PWM Ausgänge
	 TCCR1B = (1<< ICNC1) + (1<<CS10)    // start Timer mit Systemtakt
	          + (1 << ICES1);            // fallende Flanke auswählen
	 TIMSK1 = (1<<ICIE1);                // overflow und Input-capture aktivieren
	 TIFR1 =  (1<<ICIE1);                // Schon aktive Interrupts löschen
}

void DEACTIVATE_ICP1()
{
	// Timer1 deactivation
	TCCR1A = 0;
	TCCR1B = 0;
	TIMSK1 = 0;
	TIFR1 = 0;
}

void TOGGLE_ICP1()
{
	cli();
	TCCR1B ^= (1 << ICES1);            // toggle detection (rising falling edge)
	sei();
}

/*

	Start and Stop Sending/Recording Process
	
*/


void start_record_ir()			   // initiates the recording process
{
	cli();
	get_ir_status = 2; 	// Recording
	irPointer = irBuffer;
	irBufferCounter = 0; 
	irBufferOverflow = 0;
	InitTimer3(1);
	DEACTIVATE_PWM();
	ACTIVATE_ICP1();
	sei();	
}

void stop_record_ir()			   // stopd the recording process
{
	cli();
	get_ir_status = 0; 	// Idle
	irPointer = irBuffer;
	irBufferCounter = 0; 
	irBufferOverflow = 0;
	InitTimer3(0);
	DEACTIVATE_PWM();
	DEACTIVATE_ICP1();
	sei();
}


void start_send_ir()			   // initiates the sending process
{

	cli();
	get_ir_status = 1;
	irPointer = irBuffer;
	InitTimer3(1);
	ACTIVATE_PWM();
	TCNT3 = 65535 - (*(irPointer+1)*256 + *(irPointer+2));
	irPointer += 3;
	irBufferCounter = 3;
	sei();
}


void stop_send_ir()			   // initiates the sending process
{
	cli();
	get_ir_status = 0;
	irPointer = irBuffer;
	irBufferCounter = 0;
	InitTimer3(0);	
	DEACTIVATE_PWM();	
	TCNT3 = 0;
	sei();
}
