/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 * 
 * 
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.     
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 *
 *
 *                    homepage: http://www.asterics.org 
 *
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

/*
     AsTeRICS Arduino CIM Firmware

	 file: Timer.h
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 11/11/2011


*/

#ifndef _TIMER_H_
#define _TIMER_H_

void Timer_Init(void);
void enable_timer_ISR();
void disable_timer_ISR();
int8_t encode_read1( void );
int8_t encode_read2( void );


extern uint16_t ADC_updatetime;
extern uint8_t send_ADCFrame_now;
extern uint8_t check_PINChange_now;

extern uint16_t pwm3;
extern uint16_t pwm5;
extern uint16_t pwm6;

#endif
