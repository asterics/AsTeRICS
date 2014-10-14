

/*
     AsTeRICS Arduino CIM Firmware

	 file: CimProtocol.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW)
	 Date: 04/09/2010



   AsTeRICS CIM Protocol Packet Frame:
   ===================================

 	Packet ID				2 bytes	"@T" (0x4054 )
 	ARE ID / CIM ID			2 bytes	
	Data Size				2 bytes	0x0000-0x0800
	Packet serial number	1 byte	0x00-0x7f (0x80-0xff for event-replies from CIM )
	CIM-Feature address		2 bytes	
	Request / Reply code	2 bytes	
	-------------------------------------------> 11 bytes = minimum frame length
	Optional data			0-2048 bytes	
	Optional CRC checksum	0 or 4 bytes CRC32
	-------------------------------------------> 2063 bytes = maximum frame length  

*/


#include "CimProtocol.h"
#include "Uart.h"
#include "Timer.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <stdio.h>
//#include <string.h>
//#include "Leds.h"


#define ARE_MINIMAL_VERSION 1
const uint32_t ARDUINO_CIM_UNIQUE_NUMBER = 0x05060708;  


const char ARDUINO_CIM_FEATURELIST[]=
{
   0x00,0x00,  // unique number, data: 4 bytes 
   0x01,0x00,  // Set Pin Directions, data: 2 bytes, in(0) or out(1) for pins 0-13
   0x02,0x00,  // Set Pin States (output state or pullup), data: 2 bytes: 1/0 or pullup enabled/disabled
   0x03,0x00,  // Input Pin values,  data: 2 bytes (pins 0-13)
   0x04,0x00,  // Activate periodic ADC Reports, data: 2 bytes: period in milliseconds (0=off)
   0x05,0x00,  // ADC value report, data: 12 bytes (chn low / high byte for chn1-6)
   0x06,0x00,  // Set InputPin Mask for Pin state updates, data: 2 bytes: active input change reports for pins 0-13
   0x07,0x00,  // Set PWM; data: byte 1: PWM-number 3,5 or 6, byte 2: PWM value(0-100%)
} ;


//#define ARDUINO_CIM_FEATURE_UNIQUENUMBER       0x00
//#define ARDUINO_CIM_FEATURE_SET_PINDIRECTION   0x01
//#define ARDUINO_CIM_FEATURE_SET_PINSTATE       0x02
//#define ARDUINO_CIM_FEATURE_GET_PINVALUES 	 0x03
//#define ARDUINO_CIM_FEATURE_SET_ADCPERIOD 	 0x04
//#define ARDUINO_CIM_FEATURE_ADCREPORT 	     0x05
//#define ARDUINO_CIM_FEATURE_SET_PINMASK 	     0x06
//#define ARDUINO_CIM_FEATURE_SET_PWM 	   	     0x07


struct ARE_frame_t ARE_frame;
struct CIM_frame_t CIM_frame;


unsigned char readstate=0;
unsigned int  datapos=0;
uint8_t first_packet=1;
int ADCValues[6];

unsigned char PIND_Mask =0;
unsigned char PINB_Mask =0;

void init_CIM_frame (void)
{
	CIM_frame.packet_id=CIM_FRAME_START;  // '@','T': Packet-ID/sync bytes
	CIM_frame.cim_id=CIM_ID_ARDUINO;    
}


uint8_t process_ARE_frame(uint8_t status_code)
{
    uint8_t ack_needed;
	uint8_t data_size=0;
	uint8_t command;
	uint8_t bitmask = 0;


//	LEDs_ToggleLEDs(LED0);  // indicate correct frame

	command=(uint8_t)ARE_frame.request_code;
	CIM_frame.cim_feature=ARE_frame.cim_feature;
	CIM_frame.serial_number=ARE_frame.serial_number;
	CIM_frame.reply_code=(((uint16_t)status_code)<<8) + command;
	data_size=(uint8_t)ARE_frame.data_size;

	ack_needed=1;


//	if ((status_code & (CIM_ERROR_INVALID_ARE_VERSION | CIM_ERROR_CRC_MISMATCH)) == 0)
	if ((status_code & CIM_ERROR_INVALID_ARE_VERSION) == 0)
	{
	      // UART_Print(" feature ");  UART_Putchar(command);
		  // no serious packet error 
	      switch (command)   {  // process requested command

		  	case CMD_REQUEST_FEATURELIST:
			       if (data_size==0) {
				     reply_FeatureList();  // reply requested feature list
					 ack_needed=0;
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  	break;

		  	case CMD_REQUEST_RESET_CIM:
					if (data_size!=0) status_code |= CIM_ERROR_INVALID_FEATURE;
					break;
		  	case CMD_REQUEST_START_CIM:
			        if (data_size==0) {
					  enable_timer_ISR();
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;
		  	case CMD_REQUEST_STOP_CIM:
			       if (data_size==0) {
			   		// LEDs_ToggleLEDs(LED4);  // indicate reset CIM
				     first_packet=1;  // reset first frame indicator etc.
					 disable_timer_ISR();
					 DDRD = 0x00;
					 servo3_en = 0;
					 disable_PWM(3);
					 servo5_en = 0;
					 disable_PWM(5);
					 servo6_en = 0;
					 disable_PWM(6);
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;

			case CMD_REQUEST_READ_FEATURE:  //  read feature from CIM

			  switch (ARE_frame.cim_feature) {
			     case ARDUINO_CIM_FEATURE_UNIQUENUMBER:   // read unique serial number
  		            if (data_size==0) {    
				     reply_UniqueNumber();
					 ack_needed=0;
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
  			     	break;
					
			     case ARDUINO_CIM_FEATURE_GET_PINVALUES:
						generate_PINFrame();
						reply_DataFrame();				
					    ack_needed=0;
				     break;

			     case ARDUINO_CIM_FEATURE_ADCREPORT:
						generate_ADCFrame();
						reply_DataFrame();
					    ack_needed=0;
				     break;


  			      default: 				// not a valid read  feature;		 
					status_code |= CIM_ERROR_INVALID_FEATURE;
					
			   }
			   break;
				   

			case CMD_REQUEST_WRITE_FEATURE:  //  write feature to CIM						

				switch (ARE_frame.cim_feature) {  // which feature address ?

					case ARDUINO_CIM_FEATURE_SET_PINDIRECTION:   // set DDR registers
						
	  		            if (data_size==2) {    
				 		  DDRD=ARE_frame.data[0];
				 		  DDRB=ARE_frame.data[1];
  						} else status_code |= CIM_ERROR_INVALID_FEATURE;
					  break;	
					  				
					case ARDUINO_CIM_FEATURE_SET_PINSTATE:   // set PORT registers
						
	  		            if (data_size==2) {
							//check for running PWM
							bitmask = 0xFF;
							if(is_active_PWM(3)) bitmask &= ~(1<<PD3);
							if(is_active_PWM(6)) bitmask &= ~(1<<PD5);
							if(is_active_PWM(5)) bitmask &= ~(1<<PD6);
							
							if(servo3_en) bitmask &= ~(1<<PD3);
							if(servo5_en) bitmask &= ~(1<<PD5);
							if(servo6_en) bitmask &= ~(1<<PD6);
							
							PORTD=ARE_frame.data[0] & bitmask;
							PORTB=ARE_frame.data[1];
  						} else status_code |= CIM_ERROR_INVALID_FEATURE;
					  break;					

			        case ARDUINO_CIM_FEATURE_SET_ADCPERIOD:
	  		            if (data_size==2) {    
							cli();
							ADC_updatetime=  (uint16_t)ARE_frame.data[0];
							ADC_updatetime+= ((uint16_t)ARE_frame.data[1])<<8;
							sei();
						} else status_code |= CIM_ERROR_INVALID_FEATURE;
				     break;

			        case ARDUINO_CIM_FEATURE_SET_PINMASK:
	  		            if (data_size==2) {
							//check for running PWM
							bitmask = 0xFF;
							if(is_active_PWM(3)) bitmask &= ~(1<<PD3);
							if(is_active_PWM(5)) bitmask &= ~(1<<PD5);
							if(is_active_PWM(6)) bitmask &= ~(1<<PD6);
							
							if(servo3_en) bitmask &= ~(1<<PD3);
							if(servo5_en) bitmask &= ~(1<<PD5);
							if(servo6_en) bitmask &= ~(1<<PD6);
							
							PIND_Mask=ARE_frame.data[0] & bitmask;
							PINB_Mask=ARE_frame.data[1];
				 		  
							PCMSK0 = ARE_frame.data[1] & 0x3F; //enable pin change sensing on pins PB0-PB5 (PB6-7 are not connected)
							PCMSK2 = ARE_frame.data[0] & bitmask; //pin change sensing enable on port D
				 		  
						} else status_code |= CIM_ERROR_INVALID_FEATURE;
				     break;

			        case ARDUINO_CIM_FEATURE_SET_PWM:
	  		            if (data_size==2) {  
							switch(ARE_frame.data[0] & 0xF0) //high nibble: operational mode of the output pin (PWM 1kHz-28kHz, servo or disabled)
							{	
								case 0x10: // Servo output
									switch(ARE_frame.data[0] & 0x0F) //low nibble: selected output pin
									{
										case 0x03: //output pin 3
											disable_PWM(3);
											servo3_en = 1;
											servo[0] = ARE_frame.data[1] * 79 + 13000; //extend range to timer values
											DDRD |= (1<<PD3);
											enable_servo_ISR();
										break;
										
										case 0x05: //output pin 5
											disable_PWM(5);
											servo5_en = 1;
											servo[1] = ARE_frame.data[1] * 79 + 13000; //extend range to timer values
											DDRD |= (1<<PD5);
											enable_servo_ISR();
										break;
										
										case 0x06: //output pin 6
											disable_PWM(6);
											servo6_en = 1;
											servo[2] = ARE_frame.data[1] * 79 + 13000; //extend range to timer values
											DDRD |= (1<<PD6);
											enable_servo_ISR();
										break;
									}
								break;
								
								case 0x20: //PWM 500Hz output
									switch(ARE_frame.data[0] & 0x0F) //low nibble: selected output pin
									{
										case 0x03: //output pin 3
											servo3_en = 0;
											pwm3 = ARE_frame.data[1];
											enable_PWM_500Hz(3);
										break;
										
										case 0x05: //output pin 5
											servo5_en = 0;
											pwm5 = ARE_frame.data[1];
											enable_PWM_500Hz(5);
										break;
										
										case 0x06: //output pin 6
											servo6_en = 0;
											pwm6 = ARE_frame.data[1];
											enable_PWM_500Hz(6);
										break;
									}
								break;
								
								//disabled due to speed limitations of the controller
								/*case 0x30: //PWM 10kHz output
									switch(ARE_frame.data[0] & 0x0F) //low nibble: selected output pin
									{
										case 0x03: //output pin 3
											pwm3 = ARE_frame.data[1];
											enable_PWM_10kHz(3);
										break;
										
										case 0x05: //output pin 5
											pwm5 = ARE_frame.data[1];
											enable_PWM_10kHz(5);
										break;
										
										case 0x06: //output pin 6
											pwm6 = ARE_frame.data[1];
											enable_PWM_10kHz(6);
										break;
									}
								break;
								
								case 0x40: //PWM 28kHz output
									switch(ARE_frame.data[0] & 0x0F) //low nibble: selected output pin
									{
										case 0x03: //output pin 3
											pwm3 = ARE_frame.data[1];
											enable_PWM_28kHz(3);
										break;
										
										case 0x05: //output pin 5
											pwm5 = ARE_frame.data[1];
											enable_PWM_28kHz(5);
										break;
										
										case 0x06: //output pin 6
											pwm6 = ARE_frame.data[1];
											enable_PWM_28kHz(6);
										break;
									}
								break;*/
								
								default: //default: disable
									switch(ARE_frame.data[0] & 0x0F) //low nibble: selected output pin
									{
										case 0x03: //output pin 3
											DDRD &= ~(1<<PD3);
											PORTD &= ~(1<<PD3);
											servo3_en = 0;
											disable_PWM(3);
											if(!(servo5_en | servo6_en)) disable_servo_ISR();
										break;
										
										case 0x05: //output pin 5
											DDRD &= ~(1<<PD5);
											PORTD &= ~(1<<PD5);
											servo5_en = 0;
											disable_PWM(5);
											if(!(servo3_en | servo6_en)) disable_servo_ISR();
										break;
										
										case 0x06: //output pin 6
											DDRD &= ~(1<<PD6);
											PORTD &= ~(1<<PD6);
											servo6_en = 0;
											disable_PWM(6);
											if(!(servo3_en | servo5_en)) disable_servo_ISR();
										break;
									}
								break;
							}
						} else status_code |= CIM_ERROR_INVALID_FEATURE;
				     break;
					default:         // not a valid write  feature;
		   				status_code |= CIM_ERROR_INVALID_FEATURE;
				}

			}
    }

	if (status_code & CIM_ERROR_INVALID_FEATURE)   {  // invalid data size or feature
	//	LEDs_ToggleLEDs(LED5);  // indicate wrong feature
	//	UART_Print(" invalid data size or no feature ");
	}
 

	if (ack_needed) {
	    reply_Acknowledge();
	}

    return(1);
}



void reply_FeatureList(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);
    if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
//		 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 27) {
		CIM_frame.data_size=sizeof(ARDUINO_CIM_FEATURELIST);     // feature list length
	    UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &ARDUINO_CIM_FEATURELIST, CIM_frame.data_size);
	} 
}

void reply_UniqueNumber(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8);
		// LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 15) {
		CIM_frame.data_size=4;    // lenght of unique number
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &ARDUINO_CIM_UNIQUE_NUMBER, CIM_frame.data_size);
	} 
}

void reply_Acknowledge(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
	//	 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 11) {
		CIM_frame.data_size=0;     // no data in ack frame
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
	} 
}

void reply_DataFrame(void)
{
	if (free_in_OutBuf(&output_buffer) <  CIM_HEADER_LEN+CIM_frame.data_size) 
	{
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
	//	 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
    else
	{
	   	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
	   	UART_Send_NonBlocking ((char *) CIM_frame.data, CIM_frame.data_size);
	}
	    
}

void generate_ADCFrame()
{
	uint16_t adcval;
	uint8_t i;

    for (i=0;i<6;i++)
	{
		adcval=ADC_Read(i);
		CIM_frame.data[i<<1]=(uint8_t)(adcval&0xff);
		CIM_frame.data[(i<<1) +1]=(uint8_t)(adcval>>8);
	}
	CIM_frame.data_size=12;
}

void generate_PINFrame()
{
	CIM_frame.data[0]=PIND;
	CIM_frame.data[1]=PINB;
	CIM_frame.data_size=2;
}

#define FRAME_DONE 99

//void parse_CIM_protocol(unsigned char actbyte)
void parse_CIM_protocol(void)
{
    uint32_t checksum=0;
    static uint8_t transmission_mode;
	static uint8_t reply_status_code;
	static uint8_t last_serial;
	uint8_t actbyte;


    while (keys_in_InBuf(&uart_in)) 
	{
      actbyte=read_InBuf(&uart_in);
   
      switch (readstate) 
	  {
		  case 0: // first sync byte
		  		  reply_status_code=0;
		  		  if (actbyte=='@') readstate++; 
				  break;
		  case 1: // second sync byte
		  		  if (actbyte=='T')  readstate++;
				  else readstate=0;
				  break;

 		  // packet in sync !

		  case 2: // ARE-ID: SW-version low byte
				  ARE_frame.are_id=actbyte;  
		  		  readstate++; 
				  break;
		  case 3: // ARE-ID: SW-version high byte
		  		  ARE_frame.are_id+=((uint16_t)actbyte)<<8;  
				  if (ARE_frame.are_id < ARE_MINIMAL_VERSION)    // outdated ARE ?
				     reply_status_code |= CIM_ERROR_INVALID_ARE_VERSION;
		  		  readstate++; 
				  break;
		  case 4: // data length low byte
		  		  ARE_frame.data_size=actbyte;
				  readstate++; 
				  break;
		  case 5: // data length high byte
		  		  ARE_frame.data_size+=((uint16_t)actbyte)<<8;
				  if (ARE_frame.data_size > DATABUF_SIZE)  { // dismiss packets of excessive length
				       readstate=0;
					   //ARE_frame.data_size=0;
  				       //reply_status_code |= CIM_ERROR_INVALID_FEATURE;
				  }
				  else readstate++; 
				 
				  break;
	      case 6: // serial_number 
		  		  ARE_frame.serial_number = actbyte;
				  if (first_packet)    // don't check first serial
				      first_packet=0;
				  else if (actbyte != (last_serial+1)%0x80) // check current serial number
				     reply_status_code |= CIM_ERROR_LOST_PACKETS;

				  last_serial=actbyte;
			      readstate++;
				  break;
		  case 7: // CIM-feature low byte
		  		  ARE_frame.cim_feature= actbyte; 
		  		  readstate++; 
				  
				  break;
		  case 8: // CIM-feature high byte
		  		  ARE_frame.cim_feature+=((int)actbyte)<<8; 
		  		  readstate++; 

				  break;
		  case 9: // Request code low byte ( command )
		  		  ARE_frame.request_code=actbyte;
		  		  readstate++; 
				  break;
		  case 10:// Request code high byte (transmission mode)
		  		  transmission_mode=actbyte;  // bit 0: CRC enable
				  // reply_status_code|=(actbyte & CIM_STATUS_CRC);   
				  // remember CRC state for reply
				  
		  		  if (ARE_frame.data_size>0) {
				  	readstate++;
					datapos=0;
				  }
				  else {  // no data in packet 
				    if (transmission_mode & CIM_STATUS_CRC) 
					   readstate+=2;      // proceed with CRC 
					else readstate=FRAME_DONE;  // frame is finished here !
				  }
				  break;

		  case 11: // read out data
		  		  ARE_frame.data[datapos]=actbyte;
				  datapos++;
				  if (datapos==ARE_frame.data_size)
				  {
				     if (transmission_mode & CIM_STATUS_CRC)  // with CRC: get checksum 
 				        readstate++;  					 
					 else  readstate=FRAME_DONE; // no CRC:  frame is finished here !
				  } 
				  break;
		  case 12: // checksum byte 1
		  		  checksum=actbyte;
				  readstate++;
				  break;
		  case 13: // checksum byte 2
		  		  checksum+=((long)actbyte)<<8;
				  readstate++;
				  break;
		  case 14: // checksum byte 3
		  		  checksum+=((long)actbyte)<<16;
				  readstate++;
				  break;
		  case 15: // checksum byte 4
		  		  checksum+=((long)actbyte)<<24;	  

				  // check CRC now (currently not used):
				  // if (checksum != crc32(ARE_frame.data, ARE_frame.data_size))
				  reply_status_code |= CIM_ERROR_CRC_MISMATCH;

				  // frame finished here !  
 				  readstate=FRAME_DONE;
				  break;
		  default: readstate=0; break;
	 }		

     if (readstate==FRAME_DONE)  {  // frame finished: store command in ringbuffer
			process_ARE_frame(reply_status_code);
			readstate=0;
		}
   }
}

