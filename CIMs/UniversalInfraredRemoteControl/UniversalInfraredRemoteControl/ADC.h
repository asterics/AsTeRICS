/*
 * ADC.h
 *
 * Created: 08.01.2015 08:58:26
 * Author: Christoph
 * Project: Bachelor Thesis "Design and Development of a Universal Remote Control"
 */


#include <stdint.h>

#define ADC_PRESCALER ((1<<ADPS2) | (1<<ADPS0))

#define ADHSM (7)

int16_t adc_read(uint8_t);				// Read ADC Value from channel