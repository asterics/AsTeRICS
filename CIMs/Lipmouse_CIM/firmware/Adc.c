
/*
     AsTeRICS LipMouse CIM Firmware

	 file: Adc.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011

*/

#include <avr/io.h>
#include <avr/interrupt.h>

#define PCB_VERSION

void ADC_Init(void) {
 
  uint16_t result;
 
  ADMUX = (1<<REFS0);    //Avcc(+5v) as voltage reference
  // ADMUX = (1<<REFS1) | (1<<REFS0);      // interne Referenzspannung nutzen
  ADCSRA = (1<<ADPS0) | (1<<ADPS1) | (1<<ADPS2);    // Frequenzvorteiler
  ADCSRA |= (1<<ADEN);                  // ADC aktivieren
 
  /* nach Aktivieren des ADC wird ein "Dummy-Readout" empfohlen, man liest
     also einen Wert und verwirft diesen, um den ADC "warmlaufen zu lassen" */
 
  ADCSRA |= (1<<ADSC);                  // eine ADC-Wandlung 
  while (ADCSRA & (1<<ADSC) );          // auf Abschluss der Konvertierung warten
  /* ADCW muss einmal gelesen werden, sonst wird Ergebnis der nächsten
     Wandlung nicht übernommen. */
  result = ADCW;
}
 

/* ADC Einzelmessung */
uint16_t ADC_Read(uint8_t channel )
{

#ifndef PCB_VERSION
  if (channel==4) channel=1;
  else if (channel==5) channel=3;
  else if (channel==6) channel=2;
  else if (channel==7) channel=4;
#endif


  // Kanal waehlen, ohne andere Bits zu beeinflußen
  ADMUX = (1<<REFS0) | channel;
  ADCSRA |= (1<<ADSC);            // eine Wandlung "single conversion"
  while (ADCSRA & (1<<ADSC) )     // auf Abschluss der Konvertierung warten
    ;
  return ADCW;                    // ADC auslesen und zurückgeben
}


