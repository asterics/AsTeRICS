/*
 * SendReceive.h
 *
 * Created: 29.10.2014 18:39:18
 * Author: Christoph
 */ 


#define ir_input_port PORTD
#define ir_input_ddr DDRD
#define ir_input_pin PORTD0  

#define ACTIVATE_EXT_INT0 EIMSK |= (1 << INT0);     // Turns on INT0
#define DEACTIVATE_EXT_INT0 EIMSK &= ~(1 << INT0);     // Turns on INT0

#define ACTIVATE_PWM   TCCR1B |= (1<<CS11);
#define DEACTIVATE_PWM TCCR1B &= ~(1<<CS11);
#define TOGGLE_PWM     TCCR1B ^= (1<<CS11);

uint16_t code_array_counter;
uint16_t ir_timing;

uint8_t get_ir_status;  // 0 : idle
						// 1 : Sending
						// 2 : Recording
						// 3 : Record ready
uint8_t set_ir_status;  // 0 : idle
						// 1 : Sending
						// 2 : Recording

void InitPWM();

void Init_IR_Record(void);

void init_ir_record(void);				// initializes the external interrupt

void start_send_ir(void);				// initiates the sending process

void start_record_ir(void);				// initiates the recording process

void stop_record_ir(void);			    // stop the recording process

void stop_send_ir(void);				    // stop the sending process

void Init_IR(void);