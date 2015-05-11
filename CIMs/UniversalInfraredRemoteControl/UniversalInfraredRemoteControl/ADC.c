/*
 * ADC.h
 *
 * Created: 08.01.2015 08:58:26
 * Author: Christoph
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */

#include <avr/io.h>
#include "ADC.h"
#include "usb_serial.h"

int16_t adc_read(uint8_t channel)		// Read ADC Value from channel
{
	uint8_t lowByte;

	ADCSRA = (1<<ADEN) | ADC_PRESCALER;
	ADCSRB = (1<<ADHSM) | (channel & 0x20);
	ADMUX = (1<<REFS0) | (channel & 0x1F);
	ADCSRA = (1<<ADEN) | ADC_PRESCALER | (1<<ADSC);
	while (ADCSRA & (1<<ADSC)) ;
	lowByte = ADCL;	
	return (ADCH << 8) | lowByte;
}
