#include <avr/io.h>
#include <avr/pgmspace.h>
#include <stdint.h>
#include <util/delay.h>


#define LED_CONFIG	(DDRD |= (1<<6))
#define PIN0_CONFIG	(DDRD |= (1<<0))

#define PIN0_ON		(PORTD |= (1<<0))
#define PIN0_OFF	(PORTD &= ~(1<<0))


void generate_frame(uint16_t channel1, uint16_t channel2, uint16_t channel3, uint16_t channel4, uint16_t channel5, uint16_t channel6, uint16_t channel7, uint16_t channel8);

