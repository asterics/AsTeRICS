
/*
     AsTeRICS LipMouse CIM Firmware

	 file: IR.h
	 Version: 0.1
	 Author: Christoph Ulbinger (FHTW)
	 Date: 03/04/2015


*/

#define ir_input_port PORTD
#define ir_input_ddr DDRD
#define ir_input_pin PORTD0

#define IR_LED1 PORTD5
#define IR_LED2 PORTD6
#define IR_LED3 PORTD7

#define ACTIVATE_EXT_INT0 EIMSK |= (1 << INT0);     // Turns on INT0
#define DEACTIVATE_EXT_INT0 EIMSK &= ~(1 << INT0);     // Turns off INT0

//#define ACTIVATE_PWM   TCCR1B |= (1<<CS11);
//#define DEACTIVATE_PWM TCCR1B &= ~(1<<CS11);
//#define TOGGLE_PWM     TCCR1B ^= (1<<CS11);

uint8_t irBuffer[512];
uint8_t* irPointer;
uint8_t irBufferOverflow;
uint16_t irBufferCounter;
uint8_t get_ir_status; // 0 : idle
				  	   // 1 : Sending
				   	   // 2 : Recording
				       // 3 : Record ready
uint8_t set_ir_status; // 0 : idle
				       // 1 : Sending
				       // 2 : Recording

void Init_IR(void);

void init_ir_record(void);		// initializes the external interrupt

void start_send_ir(void);				// initiates the sending process

void start_record_ir(void);				// initiates the recording process

void stop_send_ir(void);				// stops the sending process

void stop_record_ir(void);				// stops the recording process

void DEACTIVATE_PWM(void);

void ACTIVATE_PWM(void);

void TOGGLE_PWM(void);

void Record_IR_Code(void);

void Init_ICP1(void);

void ACTIVATE_ICP1(void);

void DEACTIVATE_ICP1(void);

void TOGGLE_ICP1(void);

void Set_IR_LED(void);

void Clear_IR_LED(void);

void Init_IR_LED(void);
