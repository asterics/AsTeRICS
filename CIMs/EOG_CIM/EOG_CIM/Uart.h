

/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
	 file: Uart.h
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010


*/

#ifndef _UART_H_
#define _UART_H_

#include <inttypes.h>


#ifndef F_CPU
#define F_CPU 16000000
#endif

#define INBUF_SIZE 45      // 50 bytes for ringbuffers to store HID requests
#define INBUF_WARNING 10

#define OUTBUF_SIZE 35      // 35 bytes for ringbuffer to store CIM replies
#define OUTBUF_WARNING 15  

#define DATABUF_SIZE 5   // max. 4 bytes needed for x/y pos etc.

#define DISABLE_SEND_ISR UCSR1B &= ~(1<<UDRIE1);
#define ENABLE_SEND_ISR UCSR1B |= (1<<UDRIE1);

#define DISABLE_RCV_ISR UCSR1B &= ~(1<<RXCIE1);
#define ENABLE_RCV_ISR UCSR1B |= (1<<RXCIE1);

#define ENABLE_T1_ISR TIMSK1 |= (1<<TOIE1);  
#define DISABLE_T1_ISR TIMSK1 &= ~(1<<TOIE1);  

struct ringbuf_i {
	unsigned int buflen;
	unsigned int rpos;
	unsigned int wpos;
	unsigned char data[INBUF_SIZE];
} ;

struct ringbuf_o {
	unsigned int buflen;
	unsigned int rpos;
	unsigned int wpos;
	unsigned char data[OUTBUF_SIZE];
} ;

extern struct ringbuf_o output_buffer;
extern struct ringbuf_i uart_in;

void init_InBuf(struct ringbuf_i * rb);
int keys_in_InBuf(struct ringbuf_i * rb);
int free_in_InBuf(struct ringbuf_i * rb);
unsigned char read_InBuf(struct ringbuf_i * rb);
void insert_InBuf(struct ringbuf_i * rb, unsigned char c);

void init_OutBuf(struct ringbuf_o * rb);
int keys_in_OutBuf(struct ringbuf_o * rb);
int free_in_OutBuf(struct ringbuf_o * rb);
unsigned char read_OutBuf(struct ringbuf_o * rb);
void insert_OutBuf(struct ringbuf_o * rb, unsigned char c);

void UART_Init(long baud);
void UART_Putchar(unsigned char c);		 // polling 
void UART_Print (char * str);            // polling
void UART_Send_NonBlocking( char *str, int len); // interrupt driven

uint16_t init_timer(uint16_t periodTime);
void init_adc();

#endif
