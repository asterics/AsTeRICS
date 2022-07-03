
/*
     AsTeRICS Proximity CIM Firmware
	 using Teensy 2.0++ Controller board

	 file: Adc.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>

#define BUFSIZE 8


volatile uint16_t ADC_updates=0;     // counts ADC updates in ISR 
static volatile int16_t buffer[BUFSIZE];

static int16_t sum=0;
static uint8_t buffer_pos=0, measure_top=1;


void ADC_Init(void) {

    ADC_updates=0;

    ADCSRA = (1<<ADIF);
	ADCSRB = 0;
	ADMUX=0;

	sum=0; buffer_pos=0; measure_top=1;
	for (int i=0;i<BUFSIZE;i++) buffer[i]=0;

  	ADCSRA |= ((1<<5) | (1<<3)); //ADC Auto Trigger enable, ADC Interrupt enable
	ADCSRA |= ((1<<2) | (1<<1)); //ADC Prescaler 64 --> 250kHz
	ADCSRB |= ((1<<2) | (1<<0) | (1<<7)); //Trigger-Source: Timer/Counter1 Compare Match B, High Speed Mode
	ADMUX = ((1<<6) | (1<<0) | (1<<1)); //VCC with external capacitor on AREF pin, Single Ended Input ADC3
	DIDR0 |= (1<<3); //Digital Input Disable
	ADCSRA|= (1<<7); //ADC enable --> in Auto-Trigger-Mode 
					//the first conversion is started on a positive edge of the trigger signal
}
 

/*periodische ADC-Messungen --> über Compare Match ISR OCR1B getriggert*/
ISR(ADC_vect)
{
	static int16_t berg;
	unsigned char low, high;

	low=ADCL;
	high=ADCH;
	uint16_t diff = 0;

	if (measure_top)
	{
		berg = (((uint16_t)high<<8)+low); //1ste Messung --> Bergwert messen
		measure_top=0;
	}
	else
	{		
		diff = berg - (((uint16_t)high<<8)+low); //2te Messung = Differenz bilden --> diff = berg - Talwert
		sum = sum - buffer[buffer_pos];
		buffer[buffer_pos] = diff;
		sum += diff;
		if (!( buffer_pos= (buffer_pos+1) % BUFSIZE))  // buffer filled with values !
			ADC_updates++;

		measure_top=1;
	}
}



uint16_t mittl_berechnen()
{	
    if (sum < 0) { measure_top=!measure_top; return (0);}  // this is a workaround for the init-comparematch problem !
	return (uint16_t)(sum/BUFSIZE);
}
