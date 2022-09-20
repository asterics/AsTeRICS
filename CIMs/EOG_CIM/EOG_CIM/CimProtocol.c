
/*
     AsTeRICS EOG Sensor/Actuator 
	 file: CimProtocol.c
	 Version: 0.1
	 Author: Chris Veigl (FHTW) modified by Benedikt Roßboth
	 Date: 04/09/2010 ... 10/2011


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


#define ARE_MINIMAL_VERSION 1

const uint32_t EOG_CIM_UNIQUE_NUMBER = 0x10203040;  

const char EOG_CIM_FEATURELIST[]=
{
   0x00,0x00,  // unique number								data: 4 bytes 
   0x01,0x00,  // active periodic value reports				data: 2 bytes - 0(off), 4ms-200ms period time
   0x02,0x00,  // channel value report						data: 4 bytes
} ;


struct ARE_frame_t ARE_frame;
struct CIM_frame_t CIM_frame;

unsigned char channel=0;
unsigned char readstate=0;
unsigned char databuf[DATABUF_SIZE];
unsigned int  datapos=0;
uint8_t first_packet=1;

uint8_t CLEAR_INPUT=0;
volatile uint16_t reload;

volatile uint8_t first =0;

void init_CIM_frame (void)
{
	CIM_frame.packet_id=CIM_FRAME_START;  // '@','T': Packet-ID/sync bytes
	CIM_frame.cim_id=CIM_ID_EOG_ACTUATOR;    
}


uint8_t process_ARE_frame(uint8_t status_code)
{

    uint8_t ack_needed, i;
	uint8_t data_size=0;
	uint8_t command;
	struct ringbuf_i * new_insert;
	
	command=(uint8_t)ARE_frame.request_code;
	
	CIM_frame.cim_feature=ARE_frame.cim_feature;
	CIM_frame.serial_number=ARE_frame.serial_number;
	CIM_frame.reply_code=(((uint16_t)status_code)<<8) + command;
	data_size=(uint8_t)ARE_frame.data_size;

	ack_needed=1;
	new_insert=0;

//	if ((status_code & (CIM_ERROR_INVALID_ARE_VERSION | CIM_ERROR_CRC_MISMATCH)) == 0)
	if ((status_code & CIM_ERROR_INVALID_ARE_VERSION) == 0)
	{
		   // no serious packet error 
	      switch (command)   {  // process requested command

		  	case CMD_REQUEST_FEATURELIST:
			       if (data_size==0) {
				     reply_FeatureList();  // reply requested feature list
					 ack_needed=0;
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  	break;

		  	case CMD_REQUEST_RESET_CIM:
		  	case CMD_REQUEST_START_CIM:
			  		if (data_size==0) {
				     reset_EOG_CIM();  // reset first frame indicator etc.
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;																	 						  		
		  	case CMD_REQUEST_STOP_CIM:
			       if (data_size==0) {
				     reset_EOG_CIM();  // reset first frame indicator etc.
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;

			case CMD_REQUEST_READ_FEATURE:  //  read feature from CIM
				  switch (ARE_frame.cim_feature) {
				     case EOG_CIM_FEATURE_UNIQUENUMBER:   // read unique serial number
	  		            if (data_size==0) {    
					     reply_UniqueNumber();
						 ack_needed=0;
						} else status_code |= CIM_ERROR_INVALID_FEATURE;
	  			     	break;

	  			     default: 				 
						status_code |= CIM_ERROR_INVALID_FEATURE;
				   }
				   break;

			case CMD_REQUEST_WRITE_FEATURE:  //  write feature to CIM
					switch (ARE_frame.cim_feature) {  // which feature address ?
						case EOG_CIM_FEATURE_ACTIVE_PERIODIC_VALUE_REPORTS :   // activate timer reg. periodic value + adc + uart																			
						  if (data_size==2)  
						  {		
							  
							  reply_Acknowledge();							  
							  reload = init_timer((uint16_t)ARE_frame.data[0]);
							  init_adc();						  							  
							  CIM_frame.serial_number=127;
							  CIM_frame.cim_feature=0x0002;
							  TIMSK1 |= (1<<TOIE1);	
							  sei();
							  ack_needed=0;
							  					 
						  } else status_code |= CIM_ERROR_INVALID_FEATURE;
						  break;
					
						default:
							status_code |= CIM_ERROR_INVALID_FEATURE;
					}
					break;

			default:
			   status_code |= CIM_ERROR_INVALID_FEATURE;
		}
    }
 
    if (new_insert)
	  if (free_in_InBuf(new_insert) < INBUF_WARNING) {  // indicate input buffer warning
 		 status_code |= CIM_ERROR_CIM_NOT_READY;
	  }

	if (ack_needed) {
	    reply_Acknowledge();
	}

    return(1);
}

ISR(TIMER1_OVF_vect)   // Sampling timer (timer 1) interrupt service routine
{
	TCNT1= reload;     // reload the timer counter for 256 Hz sampling frequency 
	if (CIM_frame.serial_number==255)
	{
		CIM_frame.serial_number=128;
	}
	else CIM_frame.serial_number++;
	    
    UCSR1B &= ~(1<<UDRIE1);    //  Ensure UART IRQ's are disabled.

    ADMUX = (1<<REFS0);        //  start sampling with ADC chn 0, use internal 5V reference
    ADCSRA |= (1<<ADIF);       //  Reset any pending ADC interrupts
    ADCSRA |= (1<<ADSC);       //  Start the ADC
}

ISR(ADC_vect)      //  AD-conversion-complete interrupt service routine
{											  															
    unsigned char low,high,low1,high1;
	if (channel==0)
	{
	    low = ADCL;               // read ADC value (low byte first !)
		high = ADCH;              // read ADC value high byte
		channel++;
		ADMUX = (1<<REFS0)+channel;  // select the next channel, use internal 5V reference
		ADCSRA |= (1<<ADSC); 
	}	
	if (channel==1)
	{
	    low1 = ADCL;               
		high1 = ADCH;         
	}

	char values[]={high,low,high1,low1};
	reply_Values(&values);	
}

void reply_FeatureList(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);
    if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
    }
	if (free_bytes > 17) {
		CIM_frame.data_size=sizeof(EOG_CIM_FEATURELIST);     // feature list length
	    UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &EOG_CIM_FEATURELIST, CIM_frame.data_size);
	} 
}

void reply_UniqueNumber(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8);
    }
	if (free_bytes > 15) {
		CIM_frame.data_size=4;    // length of unique number
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &EOG_CIM_UNIQUE_NUMBER, CIM_frame.data_size);
	} 
}

void reply_Acknowledge(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
    }
	if (free_bytes > 11 && ARE_frame.cim_feature!=EOG_CIM_FEATURE_ACTIVE_PERIODIC_VALUE_REPORTS) {
		CIM_frame.data_size=0;     // no data in ack frame
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
	} 
	if (ARE_frame.cim_feature==EOG_CIM_FEATURE_ACTIVE_PERIODIC_VALUE_REPORTS && free_bytes>11)
	{
		UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
	}
}

void reply_Values(char *values)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
    }
	if (free_bytes > 15) {
		CIM_frame.data_size=4;
		CIM_frame.reply_code=0x0020;     
		UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
		UART_Send_NonBlocking ((char *) &values, CIM_frame.data_size);
	} 
}

void reset_EOG_CIM(void)
{ 
	DISABLE_T1_ISR;
    first_packet=1;
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

    while (keys_in_InBuf(&uart_in)) {
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
					ARE_frame.data= databuf; 
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

