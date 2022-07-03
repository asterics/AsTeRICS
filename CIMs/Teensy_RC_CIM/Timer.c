#include <avr/io.h>
#include <avr/interrupt.h>
#include "Timer.h"
#include "globals.h"

 

int timecounter = 0;

void Timer0_enable_ISR()
{
	TIMSK0 |= (1<<TOIE0);


}

void Timer0_disable_ISR()
{

	TIMSK0 &= ~(1<<TOIE0);


}



void Timer1_init(void)
{


  TCCR1A = 0;
  TCCR1B = 0;
  TCNT1 = 0;
  TIMSK1 = 0;

  TCCR1A |= (1 << COM1A0);
  // set mode to CTC with compare to OCR1A register
  TCCR1B |= (1 << WGM12);
  // set /8 prescale 2000 cycles/ms
  TCCR1B |= (1 << CS11);
  // enable ouptut compare A interrupts
  TIMSK1 |= (1 << OCIE1A);

  OCR1A = 2000;

}

ISR(TIMER1_COMPA_vect) 
{
	int times[] = {400, 1100, 400, 1100, 400, 700, 400, 1100, 400, 700, 400, 700, 400, 700, 400, 700, 400, 11168};	

	times[1] = channel1frame;
	times[3] = channel2frame;
	times[5] = channel3frame;
	times[7] = channel4frame;
	times[9] = channel5frame;
	times[11] = channel6frame;
	times[13] = channel7frame;
	times[15] = channel8frame;
	times[17] = startframe;

  OCR1A = times[timecounter]*2;
	
	if(timecounter % 2)
	{
		PIN0_ON;
		
	}
	else
	{
		PIN0_OFF;
		
	}

  timecounter++;
  if(timecounter >= 18)
    timecounter = 0;


}
