
/*
     AsTeRICS Universal HID Actuator - Mouse_Keyboard_Joystick version
	 file: Uart.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010

*/

#include <avr/io.h>
#include <avr/interrupt.h>
#include "Uart.h"
#include "CimProtocol.h"

struct ringbuf_i uart_in;
struct ringbuf_o output_buffer;

void init_InBuf(struct ringbuf_i * rb)
{
 	rb->buflen=0;
 	rb->rpos=0;
 	rb->wpos=0;
}

void insert_InBuf(struct ringbuf_i * rb, unsigned char c)
{
 	if (rb->buflen<INBUF_SIZE) {
    	rb->data[rb->wpos]=c; rb->wpos=(rb->wpos+1)%INBUF_SIZE;
    	rb->buflen++;
 	}
}

unsigned char read_InBuf(struct ringbuf_i * rb)
{
 	unsigned char c;
	DISABLE_RCV_ISR;
 	if (!rb->buflen)  return (0); 
 	c=rb->data[rb->rpos];
 	rb->rpos=(rb->rpos+1)%INBUF_SIZE;
 	rb->buflen--;
	ENABLE_RCV_ISR;
 	return(c);
}

int keys_in_InBuf(struct ringbuf_i * rb)
{
 	return rb->buflen;
}

int free_in_InBuf(struct ringbuf_i * rb)
{
 	return (INBUF_SIZE-rb->buflen);
}


void init_OutBuf(struct ringbuf_o * rb)
{
 	rb->buflen=0;
 	rb->rpos=0;
 	rb->wpos=0;
}

void insert_OutBuf(struct ringbuf_o * rb, unsigned char c)
{
 	if (rb->buflen<OUTBUF_SIZE) {
    	rb->data[rb->wpos]=c; rb->wpos=(rb->wpos+1)%OUTBUF_SIZE;
    	rb->buflen++;
 	}
}

unsigned char read_OutBuf(struct ringbuf_o * rb)
{
 	unsigned char c;
 	if (!rb->buflen)  return (0); 
 	c=rb->data[rb->rpos];
 	rb->rpos=(rb->rpos+1)%OUTBUF_SIZE;
 	rb->buflen--;
 	return(c);
}


int keys_in_OutBuf(struct ringbuf_o * rb)
{
 	return rb->buflen;
}

int free_in_OutBuf(struct ringbuf_o * rb)
{
 	return (OUTBUF_SIZE-rb->buflen);
}


ISR (USART1_RX_vect) // interrupt UART character received -> parse / store in ringbuf
{
	// parse_CIM_protocol(UDR1);
	insert_InBuf(&uart_in, UDR1);   // parse AsTeRICS CIM communication protocol
}

ISR (USART1_UDRE_vect) // Interrupt UART Data Register empty -> send next byte )
{
	if (keys_in_OutBuf(&output_buffer))        // characters left ?
		UDR1=read_OutBuf(&output_buffer); // send current character from the buffer
	else DISABLE_SEND_ISR; 	// Transmission of TXBuf finished -> disable USART_UDRE interrupts
}


void UART_Init(long baudrate)
{
	init_InBuf(&uart_in);
	init_OutBuf(&output_buffer);

	UBRR1 = F_CPU / baudrate / 8 - 1;
 
	UCSR1A |= (1<<U2X1);
	UCSR1B = (1<<RXEN1) | (1<<TXEN1) | (1<<RXCIE1);
    UCSR1C =  ( 1<<UCSZ10 | 1<<UCSZ11 );

}

void UART_Putchar(unsigned char c)
{
	while ( !( UCSR1A & (1<<UDRE1)) );
	UDR1 = c;
}

void UART_Print( char *str)  // polling
{
	while ( *str) {
      UART_Putchar ((unsigned char) *str); str++;
	}
}

void UART_Send_NonBlocking( char *str, int len) // interrupt driven
{
	  DISABLE_SEND_ISR;
	  while (len--) insert_OutBuf(&output_buffer, *str++);
	  ENABLE_SEND_ISR;
}

uint16_t init_timer(uint16_t periodTime)
{
	uint16_t RELOAD = (65536 - (( F_CPU / 1024) / (1000/periodTime)));

	TCNT1 = RELOAD;                     //  Set Timer1 Counter to reload
    TCCR1B = (1<<CS12) | (1<<CS10);     //  Set Prescaler to  1024
						                //  16000000 / 1024 = 15625 counts per second
	return RELOAD;
}

void init_adc()
{
	//Initialize the ADC

    // Timings for sampling of one 10-bit AD-value:
    // prescaler = 64 (ADPS2 = 1, ADPS1 = 1, ADPS0 = 0)
    // ADCYCLE = XTAL / prescaler = 115200Hz or 8.68 us/cycle
    // 14 (single conversion) cycles = 121.5 us (8230 samples/sec)
    // 26 (1st conversion) cycles = 225.69 us

    ADCSRA =  (1<<ADPS2) | (1<<ADPS1);  // Prescaler = 64, free running mode = off, interrupts off.
    ADCSRA |= (1<<ADIF);                // Reset any pending ADC interrupts
    ADCSRA |= (1<<ADEN);                // Enable the ADC
	ADCSRA |= (1<<ADIE);                // Enable ADC interrupts.
	// The first conversion will start after ADSC is set in the timer interrupt
}

