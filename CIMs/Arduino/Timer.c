
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
volatile uint8_t send_ADCFrame_now=0;     // flag for adc send packet requests


////// timer PWM operation //////

/** enable byte for pwm 3, 0 if disabled **/
volatile uint8_t pwm3_en = 0;
/** enable byte for pwm 5, 0 if disabled **/
volatile uint8_t pwm5_en = 0;
/** enable byte for pwm 6, 0 if disabled **/
volatile uint8_t pwm6_en = 0;

/** pwm value for pwm channel 3 */
volatile uint8_t pwm3 = 0;
/** pwm value for pwm channel 5 */
volatile uint8_t pwm5 = 0;
/** pwm value for pwm channel 6 */
volatile uint8_t pwm6 = 0;

////// timer servo operation //////

/** 3 servo values (OCR register values). Range 16000 for a 1ms peak, 32000 for a 2ms peak 
 * [0]...output 3
 * [1]...output 5
 * [2]...output 6 **/
volatile uint16_t servo[3] = {0,0,0};
/** enable byte for servo output 3, 0 if disabled */
volatile uint8_t servo3_en = 0;
/** enable byte for servo output 5, 0 if disabled */
volatile uint8_t servo5_en = 0;
/** enable byte for servo output 6, 0 if disabled */
volatile uint8_t servo6_en = 0;
/** select variable to differ OCR1A; 0 if servo3 is actual loaded, 1 if servo 5 is loaded **/
volatile uint8_t servo_select = 0;





void Timer_Init(void) 
{

	//**** ADC periodic update timer ****//
	
	//set Timer0 to interrupt-interval of 1ms
    TCNT0 = RELOAD;     //  Set the initial value for the Timer0 Counter Register 
    TCCR0B = (1<<CS01) | (1<<CS00);
	      		        //  Set the Timer0 Control Register for Prescaler F_CPU / 64
						//  = 250000 counter steps per second

   //**** PWM servo timer ****//

    TCNT1 = 0;     //  50kHz
    TCCR1B = (1<<CS10); //set prescaler to 1 (16MHz timer clock)
    
    
    //**** PWM timer ****//
    
    TCCR2A |= (1<<WGM21); 	//set CTC mode for timer 2
    OCR2A = 125;			//128kHz ISR frequency -> 500Hz PWM
    TCCR2B |= (1<<CS20);	// prescaler 1
    
}

void enable_timer_ISR()
{
    TIMSK0|=(1<<TOIE0);   // Timer Interrupt Mask: Enable overflow interrupt
}

void disable_timer_ISR()
{
    TIMSK0 &= ~(1<<TOIE0); 
}

void enable_servo_ISR()
{
	TIMSK1=(1<<TOIE1)|(1<<OCIE1A)|(1<<OCIE1B);   // Timer1 Interrupt Mask: Enable overflow interrupt
}

void disable_servo_ISR()
{
	TIMSK1 &= ~((1<<TOIE1)|(1<<OCIE1A)|(1<<OCIE1B));   // Timer1 Interrupt Mask: Enable overflow interrupt
}

void enable_PWM_500Hz(uint8_t pinnumber)
{
	switch(pinnumber) //select PWM channel
	{
		case 3:
			pwm3_en = 1;		//enable PWM
			DDRD |= (1<<PD3);	//set direction to output
		break;
		
		case 5:
			pwm5_en = 1;		//enable PWM
			DDRD |= (1<<PD5);	//set direction to output
		break;
		
		case 6:
			pwm6_en = 1;		//enable PWM
			DDRD |= (1<<PD6);	//set direction to output
		break;
	}
	TIMSK2 |=(1<<OCIE2A);
}

void disable_PWM(uint8_t pinnumber)
{
	switch(pinnumber) //select PWM channel
	{
		case 3:
			pwm3_en = 0;		//disable PWM
			DDRD &= ~(1<<PD3);	//set direction to input
			pwm3 = 0;
		break;
		
		case 5:
			pwm5_en = 0;		//disable PWM
			DDRD &= ~(1<<PD5);	//set direction to input
			pwm5 = 0;
		break;
		
		case 6:
			pwm6_en = 0;		//disable PWM
			DDRD &= ~(1<<PD6);	//set direction to input
			pwm6 = 0;
		break;
	}
	
	if(!(pwm3_en || pwm5_en || pwm6_en)) TIMSK1 &= ~(1<<OCIE2A); //if every pwm is disabled -> disable ISR
}

uint8_t is_active_PWM(uint8_t pinnumber)
{
	if(pinnumber == 3) return pwm3_en;
	if(pinnumber == 5) return pwm5_en;
	if(pinnumber == 6) return pwm6_en;
	return 0;
}


ISR (TIMER0_OVF_vect)           // Timer0 overflow interrupt service routine, triggered every millisecond (ADC periodic)
{
   static uint16_t adc_counter=0;   // millisecond_counter for adc reports
   
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

}

ISR(TIMER2_COMPA_vect)			// Timer 2 compare A ISR, used for PWM output
{
	static uint16_t pwm_count=0;
	
	if ((pwm_count==pwm3) && (pwm3_en)) PORTD &= ~(1<<PD3);
	if ((pwm_count==pwm5) && (pwm5_en)) PORTD &= ~(1<<PD5);
	if ((pwm_count==pwm6) && (pwm6_en)) PORTD &= ~(1<<PD6);
	
	pwm_count++;
	
	if (pwm_count==254)
	{
		if (pwm3_en && (pwm3 != 0)) PORTD |= (1<<PD3);
		if (pwm5_en && (pwm5 != 0)) PORTD |= (1<<PD5);
		if (pwm6_en && (pwm6 != 0)) PORTD |= (1<<PD6);
		pwm_count = 0;
	}
}

ISR(TIMER1_OVF_vect)			// Timer 1 overflow ISR; used for servo PWM
{
	/** state variable:
	 * 0...1-2ms peak & 3-2ms pause
	 * 1...16ms pause*/
	static uint8_t state = 0;
	
	if(state == 0)
	{
		
		TCCR1B &= ~(1<<CS10); //clear bit 0
		TCCR1B |= (1<<CS11); //set bit 1 -> prescaler 8
		TCNT1 = 33700; //preload value -> 16ms until next overflow (value is a little bit higher due to measured differences)
		TIMSK1 &= ~((1<<OCIE1A)|(1<<OCIE1B)); //disable compare interrupts
		state = 1;
	} else {
		TCCR1B |= (1<<CS10); //set bit 0
		TCCR1B &= ~(1<<CS11); //clear bit 1 -> prescaler 1
		TIMSK1 |= ((1<<OCIE1A)|(1<<OCIE1B)); //enable compare interrupts
		
		if(servo3_en)
		{
			PORTD |= (1<<PD3);
		}
		
		if(servo5_en)
		{
			PORTD |= (1<<PD5);
		}
		
		if(servo6_en)
		{
			PORTD |= (1<<PD6);
		}
		
		//check for running servo outputs & load values
		if(servo6_en)
		{
			OCR1B = servo[2];
		}
		
		if(servo3_en && servo5_en) //only switch OCR value, if both outputs are used
		{		
			if(servo[0] < servo[1]) //load lower value first
			{
				OCR1A = servo[0];
				servo_select = 0;
			} else {
				OCR1A = servo[1];
				servo_select = 1;
			}
		} else { //otherwise, load only one value
			if(servo3_en)
			{
				OCR1A = servo[0];
				servo_select = 0;
			}
			
			if(servo5_en)
			{
				OCR1A = servo[1];
				servo_select = 1;
			}
		}
		
		state = 0;
	}
}

ISR(TIMER1_COMPA_vect)				//Timer 1 compare A ISR; used for servo PWM (channel PD3/PD5)
{
	if(servo_select == 0) 
	{ //servo 3 is actual loaded
		if(servo3_en)
		{
			PORTD &= ~(1<<PD3);
		}
		
		if(servo5_en)
		{
			if((servo[1] - servo[0]) < 50) PORTD &= ~(1<<PD5); //to avoid failure of other servo due to ISR durance
			OCR1A = servo[1];
			servo_select = 1;
		}
	} else { //servo 5 is actual loaded
		if(servo5_en)
		{
			PORTD &= ~(1<<PD5);
		}
		
		if(servo3_en)
		{
			if((servo[0] - servo[1]) < 50) PORTD &= ~(1<<PD3); //to avoid failure of other servo due to ISR durance
			OCR1A = servo[0];
			servo_select = 0;
		}
	}
}

ISR(TIMER1_COMPB_vect)				//Timer 1 compare B ISR; used for servo PWM (channel PD6)
{
	if(servo6_en) //check for enabled servo output
	{
		PORTD &= ~(1<<PD6);
	}
}
