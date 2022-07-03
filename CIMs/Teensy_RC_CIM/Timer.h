#ifndef _TIMER_H_
#define _TIMER_H_


extern int timecounter; // the array which controls the timer interrupts

#define PIN0_ON		(PORTD |= (1<<0)) // power on D0
#define PIN0_OFF	(PORTD &= ~(1<<0)) // power off D0
#define LED_ON		(DDRD|=(1<<6)); // turn on LED
#define LED_OFF		(PORTD|=(1<<6)) // turn off LED



void Timer1_init(void);


#endif
