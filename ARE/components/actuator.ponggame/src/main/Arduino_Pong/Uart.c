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
 	// UART_Print("reading in Buff");
	unsigned char c;
	DISABLE_RCV_ISR;
 	if (!rb->buflen){  return (0);ENABLE_RCV_ISR;} 
 	c=rb->data[rb->rpos];
 	rb->rpos=(rb->rpos+1)%INBUF_SIZE;
 	rb->buflen--;
	ENABLE_RCV_ISR;
 	return(c);
}

int keys_in_InBuf(struct ringbuf_i * rb)
{
 	// UART_Print("\n\r zeichen in buffer "+rb->buflen);
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


ISR (USART_RX_vect) // interrupt UART character received -> parse / store in ringbuf
{
	// parse_CIM_protocol(UDR1);
	insert_InBuf(&uart_in, UDR0);   // parse AsTeRICS CIM communication protocol
}

ISR (USART_UDRE_vect) // Interrupt UART Data Register empty -> send next byte )
{
	if (keys_in_OutBuf(&output_buffer))        // characters left ?
		UDR0=read_OutBuf(&output_buffer); // send current character from the buffer
	else DISABLE_SEND_ISR; 	// Transmission of TXBuf finished -> disable USART_UDRE interrupts
}


void UART_Init(long baudrate)
{
	init_InBuf(&uart_in);
	init_OutBuf(&output_buffer);

	UBRR0 = F_CPU / baudrate / 8 - 1;
 
	UCSR0A |= (1<<U2X0);
	UCSR0B = (1<<RXEN0) | (1<<TXEN0) | (1<<RXCIE0);
  //UCSR1C =  (1<<USBS1) | (3<<UCSZ10);

}

void UART_Putchar(unsigned char c)
{
	while ( !( UCSR0A & (1<<UDRE0)) );
	UDR0 = c;
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


