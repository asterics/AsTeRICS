

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
void enable_PWM_500Hz(uint8_t pinnumber);
//void enable_PWM_10kHz(uint8_t pinnumber);
//void enable_PWM_40kHz(uint8_t pinnumber);
void disable_PWM(uint8_t pinnumber);
uint8_t is_active_PWM(uint8_t pinnumber);

extern uint16_t ADC_updatetime;
extern uint8_t send_ADCFrame_now;

extern uint8_t pwm3;
extern uint8_t pwm5;
extern uint8_t pwm6;


void enable_servo_ISR();
void disable_servo_ISR();

/** enable byte for servo output 3, 0 if disabled */
extern volatile uint8_t servo3_en;
/** enable byte for servo output 5, 0 if disabled */
extern volatile uint8_t servo5_en;
/** enable byte for servo output 6, 0 if disabled */
extern volatile uint8_t servo6_en ;

/** 3 servo values (OCR register values). Range 16000 for a 1ms peak, 32000 for a 2ms peak 
 * [0]...output 3
 * [1]...output 5
 * [2]...output 6 **/
extern volatile uint16_t servo[3];

#endif
