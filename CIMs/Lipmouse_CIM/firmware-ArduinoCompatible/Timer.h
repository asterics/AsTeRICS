

/*
     AsTeRICS LipMouse CIM Firmware

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

extern uint16_t ADC_updatetime;
extern uint8_t send_ADCFrame_now;
extern uint8_t check_PINChange_now;

//extern uint16_t pwm3;
//extern uint16_t pwm5;
//extern uint16_t pwm6;

#endif
