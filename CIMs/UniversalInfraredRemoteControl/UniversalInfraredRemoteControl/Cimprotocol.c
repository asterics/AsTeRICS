

/*
     AsTeRICS IRSol CIM Firmware
	 using Teensy 2.0++ Controller board


	 file: CimProtocol.c
	 Version: 0.1
	 Author: Lukas Reihs (FHTW)
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
#include "usb_serial.h"
#include "Buffer.h"
#include "SendReceive.h"
#include <util/delay.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
//#include <string.h>
//#include "Leds.h"


#define ARE_MINIMAL_VERSION 1
const uint32_t ARDUINO_CIM_UNIQUE_NUMBER = 0x05031415;  


const char ARDUINO_CIM_FEATURELIST[]=
{
   0x00,0x00,  // unique number,								data: 4 bytes 
   0x08,0x00,  // Set Send/Record Mode
   0x09,0x00,  // Get Send/Record Status
   0x0B,0x00,  // Get recorded IR Codes from ARE
   0x0A,0x00,  // Send IR Codes to ARE

} ;


struct ARE_frame_t ARE_frame;
struct CIM_frame_t CIM_frame;

unsigned char readstate=0;
unsigned int  datapos=0;

uint8_t first_packet=1;

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
					  
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;
		  	case CMD_REQUEST_STOP_CIM:
			       if (data_size==0) {
			   		// LEDs_ToggleLEDs(LED4);  // indicate reset CIM
				     first_packet=1;  // reset first frame indicator etc.
					
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;

			case CMD_REQUEST_READ_FEATURE:  //  read feature from CIM

			  switch (ARE_frame.cim_feature) {
			     case UIRC_CIM_FEATURE_UNIQUENUMBER:   // read unique serial number
  		            if (data_size==0) {    
				     reply_UniqueNumber();
					 ack_needed=0;
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
  			     	break;
					
			     case UIRC_CIM_FEATURE_GET_IR_STATE:
					 CIM_frame.data[0]=get_ir_status;
					 CIM_frame.data_size=1;
					 reply_DataFrame();
					 ack_needed=0;
			     break;
			     
			     case UIRC_CIM_FEATURE_GET_IR_CODE:		// Send IR Code to ARE
					 generate_RecordFrame();
					 CIM_frame.data_size=DATABUF_SIZE;
					 reply_DataFrame();
					 ack_needed=0;
			     break;


  			      default: 				// not a valid read  feature;		 
					status_code |= CIM_ERROR_INVALID_FEATURE;
					
			   }
			   break;
				   

			case CMD_REQUEST_WRITE_FEATURE:  //  write feature to CIM						

				switch (ARE_frame.cim_feature) {  // which feature address ?

						case UIRC_CIM_FEATURE_SET_IR_MODE:
							set_ir_status=(uint8_t)ARE_frame.data[0];
							CIM_frame.cim_feature=UIRC_CIM_FEATURE_SET_IR_MODE;
							CIM_frame.reply_code=CMD_REQUEST_WRITE_FEATURE;
							CIM_frame.data[0]=set_ir_status;
							CIM_frame.data_size = 1;
							reply_DataFrame();
							ack_needed=0;
							break;
						
						case UIRC_CIM_FEATURE_SET_IR_CODE:		// Get IR Code from ARE
							memcpy(irBuffer, ARE_frame.data, DATABUF_SIZE);

							CIM_frame.cim_feature=UIRC_CIM_FEATURE_GET_IR_STATE;
							CIM_frame.reply_code=CMD_REPLY_READ_FEATURE;

							CIM_frame.data[0] = 0x04;			// 4 : Got IR Code from ARE
							CIM_frame.data_size=1;
							reply_DataFrame();
							ack_needed=0;							
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
  
    if (UEINTX & (1<<RWAL)) { //wenn Buffer nicht voll ist 
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
    }
	if (!(UEINTX & (1<<RWAL))) { //wenn Buffer voll ist
		CIM_frame.data_size=sizeof(ARDUINO_CIM_FEATURELIST);     // feature list length
	    usb_serial_write ( (uint8_t *)&CIM_frame, CIM_HEADER_LEN);
    	usb_serial_write ( (uint8_t *)&ARDUINO_CIM_FEATURELIST, CIM_frame.data_size);
	} 
}

void reply_UniqueNumber(void)
{
		CIM_frame.data_size=4;    // lenght of unique number
    	usb_serial_write ((uint8_t *) &CIM_frame, CIM_HEADER_LEN);
    	usb_serial_write ((uint8_t *) &ARDUINO_CIM_UNIQUE_NUMBER, CIM_frame.data_size);
}

void reply_Acknowledge(void)
{
	if ((UEINTX & (1<<RWAL))) { //wenn Buffer nicht voll ist 
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
    }

	if (!(UEINTX & (1<<RWAL))) {	//wenn Buffer voll ist
		CIM_frame.data_size=0;     // no data in ack frame
    	usb_serial_write ((uint8_t *) &CIM_frame, CIM_HEADER_LEN);
	} 
}

void reply_DataFrame(void)
{	
	usb_serial_write ((uint8_t *) &CIM_frame, CIM_HEADER_LEN);
	usb_serial_write ((uint8_t *) CIM_frame.data, CIM_frame.data_size); 
}

void generate_RecordFrame()
{
	memcpy(CIM_frame.data, irBuffer, DATABUF_SIZE);
}

void generate_StateFrame()
{
	CIM_frame.data[0]=recordready;
	CIM_frame.data_size=1;
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

	

    while (usb_serial_available()) 
	{ 
      actbyte=usb_serial_getchar();
		

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

