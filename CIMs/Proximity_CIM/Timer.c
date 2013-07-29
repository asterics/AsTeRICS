
/*
     AsTeRICS Proximity CIM Firmware
	 using Teensy 2.0++ Controller board

	 file: Timer.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>


void Timer_Init(void) {

	TIMSK1=0;
	TIFR1=(1<<2)|(1<<1);

	TCNT1=0;
	OCR1A = 109; // Wert bei dem der Compare Match Interrupt (LED) ausgelöst wird (vorher Reload-Value 149 --> 256-149=107)
 	OCR1B = 109+40;//Wert bei dem der Compare Match Interrupt (ADC) das erste Mal ausgelöst wird 

	// TCCR1C=0xE0;  // force output compare
	// TCCR1A=0x00;
	
	TCCR1A |= (1<<6); //OC1A toggeln bei Compare Match, keine PWM (alle WGMn-Bits sind 0)
	TIMSK1 |= ((1<<1) | (1<<2)); //Output Compare A und B interrupt enabled

}

void start_timer1()
{
   TCCR1B |= (1<<1);   //Prescaler 8 --> 1 Timer-Tick 0,5us;
}

void stop_timer1()
{
   TCCR1B = 0; 
}



ISR(TIMER1_COMPA_vect)
{	
	OCR1A = OCR1A + 109;
}


ISR(TIMER1_COMPB_vect)
{

	PORTD = PORTD ^ (1<<5); 
	OCR1B = OCR1B + 4251;
}



