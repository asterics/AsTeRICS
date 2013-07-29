
/*
     AsTeRICS Arduino CIM Firmware

	 file: Timer.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>

#define INTFREQ 1000
#define RELOAD (256 - ((F_CPU / 64) / INTFREQ))
//  Here we calculate our reload value for the timer counter register:
//  We use a prescaler of 64, so the counter is increased FCPU/64 = 250000 times per second
//  An interrupt is generated as the counter (8-bit) reaches 256 and produces an overflow.
//  If we leave the counter running freely, we get an interrupt 250000/256 = 976,5625 times per second
//  To produce an overflow 1000 times a second: 250000 / 1000 = 250  counter values are needed
//  256 - 250 = 6 is the value to reload the counter for getting 1000 interrupts per second


// updated in timer ISR (-> volatile is important !!)
volatile uint16_t ADC_updatetime=0;     // milliseconds for ADC update Frame (0=off)
volatile uint8_t send_ADCFrame_now=0;     // flag for adc send packet request
volatile uint8_t check_PINChange_now=0;   // flag for pinstate update

volatile uint16_t pwm3=0;   
volatile uint16_t pwm5=0;   
volatile uint16_t pwm6=0;   


void Timer_Init(void) {

	//set Timer0 to interrupt-interval of 1ms
    TCNT0 = RELOAD;     //  Set the initial value for the Timer0 Counter Register 
    TCCR0B = (1<<CS01) | (1<<CS00);
	      		        //  Set the Timer0 Control Register for Prescaler F_CPU / 64
						//  = 250000 counter steps per second

   // TIMSK0=(1<<TOIE0);   // Timer0 Interrupt Mask: Enable overflow interrupt

    TCNT1 = 65531;     //  50kHz
    TCCR1B = (1<<CS11) | (1<<CS10);
  //  TIMSK1=(1<<TOIE1);   // Timer1 Interrupt Mask: Enable overflow interrupt


}

void enable_timer_ISR()
{
    TIMSK0|=(1<<TOIE0);   // Timer Interrupt Mask: Enable overflow interrupt
}

void disable_timer_ISR()
{
    TIMSK0 &= ~(1<<TOIE0); 
}


ISR (TIMER0_OVF_vect)           // Timer0 overflow interrupt service routine, triggered every millisecond
{
   static uint16_t adc_counter=0;   // millisecond_counter for adc reports
   static uint16_t pinstate_counter=0;   // millisecond_counter for pinstate reports
   
   TCNT0 = RELOAD;        // Reload timer value to maintain the desired frequency of 1000Hz.
   if (ADC_updatetime >0)
   {
     adc_counter++;
     if (adc_counter>=ADC_updatetime)
     {
        adc_counter=0;
	    send_ADCFrame_now=1;
     }
   }

   pinstate_counter++;
   if (pinstate_counter>=100)   // update pinstates 10 times a second
   {
        pinstate_counter=0;
	    check_PINChange_now=1;
   }

}


ISR (TIMER1_OVF_vect)           // Timer1 overflow interrupt service routine, triggered 50000 times per second
{
   static uint16_t pwm_count=0;
   
   TCNT1 =  65531;        // Reload timer value to maintain the desired frequency of 50000Hz.

   pwm_count++;
   if (pwm_count>1000)
   {
   	  if (pwm3) {DDRD |= (1<<3); PORTD|= (1<<3);}
   	  if (pwm5) {DDRD |= (1<<5); PORTD|= (1<<5);}
   	  if (pwm6) {DDRD |= (1<<6); PORTD|= (1<<6);}
	  pwm_count=1;
   }

   if (pwm_count==pwm3) PORTD&=~(1<<3);
   if (pwm_count==pwm5) PORTD&=~(1<<5);
   if (pwm_count==pwm6) PORTD&=~(1<<6);
}
