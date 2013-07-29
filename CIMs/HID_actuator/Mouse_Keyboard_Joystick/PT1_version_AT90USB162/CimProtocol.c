

/*
     AsTeRICS - Assistive Technology Rapid Integration and Construction Set
  
  
         d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b. 
        d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
       d88P888          888           888    888   888  888    888 Y88b.     
      d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b.  
     d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
    d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
   d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
  d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P" 
 
 
                     homepage: http://www.asterics.org 
 
     This project has been partly funded by the European Commission, 
                       Grant Agreement Number 247730
 
  The AsTeRICS Universal HID Actuator is based upon	the
  LUFA Library, Copyright (C) Dean Camera, 2010
  dean [at] fourwalledcubicle [dot] com,  www.fourwalledcubicle.com

  Permission to use, copy, modify, distribute, and sell this 
  software and its documentation for any purpose is hereby granted
  without fee, provided that the above copyright notice appear in 
  all copies and that both that the copyright notice and this
  permission notice and warranty disclaimer appear in supporting 
  documentation, and that the name of the author not be used in 
  advertising or publicity pertaining to distribution of the 
  software without specific, written prior permission.

  The author disclaim all warranties with regard to this
  software, including all implied warranties of merchantability
  and fitness.  In no event shall the author be liable for any
  special, indirect or consequential damages or any damages
  whatsoever resulting from loss of use, data or profits, whether
  in an action of contract, negligence or other tortious action,
  arising out of or in connection with the use or performance of
  this software.


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
#include "Leds.h"
#include "Uart.h"


#define ARE_MINIMAL_VERSION 1
const uint32_t HID_CIM_UNIQUE_NUMBER = 0x01020304;  


const char HID_CIM_FEATURELIST[]=
{
   0x00,0x00,  // unique number            data: 4 bytes 
   0x01,0x00,  // mouse x/y pos            data: 4 bytes xx/yy
   0x02,0x00,  // mouse buttonstate        data: 1 byte
   0x03,0x00,  // mouse wheel	           data: 1 byte
   0x10,0x00,  // keyboard keypress        data: 2 bytes (keycode, modifier)
   0x11,0x00,  // keyboard keyhold         data: 2 bytes (keycode, modifier)
   0x12,0x00,  // keyboard keyrelease      data: 2 bytes (keycode, modifier)
   0x20,0x00,  // joystick joy1pos-analog  data: 4 bytes xx/yy
   0x21,0x00,  // joystick joy2pos-analog  data: 4 bytes xx/yy
   0x22,0x00,  // joystick joy3pos-digital data: 1 byte
   0x23,0x00,  // joystick buttonstate     data: 2 bytes
} ;


struct ARE_frame_t ARE_frame;
struct CIM_frame_t CIM_frame;

unsigned char readstate=0;
unsigned char databuf[DATABUF_SIZE];
unsigned int  datapos=0;
uint8_t first_packet=1;

uint8_t CLEAR_INPUT=0;

void init_CIM_frame (void)
{
	CIM_frame.packet_id=CIM_FRAME_START;  // '@','T': Packet-ID/sync bytes
	CIM_frame.cim_id=CIM_ID_HID_ACTUATOR;    
}


uint8_t process_ARE_frame(uint8_t status_code)
{
    uint8_t ack_needed, i;
	uint8_t data_size=0;
	uint8_t command;
	struct ringbuf_i * new_insert;


	LEDs_ToggleLEDs(LED0);  // indicate correct frame

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
		  	case CMD_REQUEST_STOP_CIM:
			       if (data_size==0) {
			   		 LEDs_ToggleLEDs(LED4);  // indicate reset CIM
				     reset_HID_CIM();  // reset first frame indicator etc.
					} else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;

			case CMD_REQUEST_READ_FEATURE:  //  read feature from CIM
				  switch (ARE_frame.cim_feature) {
				     case HID_CIM_FEATURE_UNIQUENUMBER:   // read unique serial number
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

					scroll_leds();

					switch (ARE_frame.cim_feature) {  // which feature address ?
					 
						case HID_CIM_FEATURE_MOUSEPOS_XY:   // insert x/y mousepos into ringbuf
						  if (data_size==4)  {
						     if (free_in_InBuf(&input_mouse) > 5) {
						  		insert_InBuf(&input_mouse,(uint8_t)ARE_frame.cim_feature);
								for(i=0;i<data_size;i++)
  					  			   insert_InBuf(&input_mouse,ARE_frame.data[i]);
  								new_insert=&input_mouse;
							 }
						  } else status_code |= CIM_ERROR_INVALID_FEATURE;
						  break;
	
						case HID_CIM_FEATURE_MOUSEBUTTONSTATE:  // insert mouse-buttonstate into ringbuf
						case HID_CIM_FEATURE_MOUSEWHEEL: 		// insert mouse-wheel change into ringbuf
						  if (data_size==1) {
						     if (free_in_InBuf(&input_mouse) > 2) {
  						  		insert_InBuf(&input_mouse,(uint8_t)ARE_frame.cim_feature);
						  		insert_InBuf(&input_mouse,ARE_frame.data[0]);
								new_insert=&input_mouse;
							 }
						  } else status_code |= CIM_ERROR_INVALID_FEATURE;
						  break;

						case HID_CIM_FEATURE_KEYPRESS: 		// insert keycode into ringbuf
						case HID_CIM_FEATURE_KEYHOLD: 
						case HID_CIM_FEATURE_KEYRELEASE: 
						  if (data_size==2) {
						     if (free_in_InBuf(&input_keyboard) > 3) {
  						  		insert_InBuf(&input_keyboard,(uint8_t)ARE_frame.cim_feature);
						  		insert_InBuf(&input_keyboard,ARE_frame.data[0]);
						  		insert_InBuf(&input_keyboard,ARE_frame.data[1]);
								new_insert=&input_keyboard;
							 }
						  } else status_code |= CIM_ERROR_INVALID_FEATURE;
						  break;


						case HID_CIM_FEATURE_JOY1POSANALOG:
						  if (data_size==4)  {
  						     if (free_in_InBuf(&input_joystick) > 5) {
					  			insert_InBuf(&input_joystick,(uint8_t)ARE_frame.cim_feature);
								for(i=0;i<data_size;i++)
  					  		   		insert_InBuf(&input_joystick,ARE_frame.data[i]);
								new_insert=&input_joystick;
							 }
						  } else status_code |= CIM_ERROR_INVALID_FEATURE;
						  break;

						// TODO: implement features for 2nd Joystick if necessary

						case HID_CIM_FEATURE_JOYBUTTONSTATE:
						  if (data_size==2) {
						     if (free_in_InBuf(&input_joystick) > 3) {
  						  		insert_InBuf(&input_joystick,(uint8_t)ARE_frame.cim_feature);
					  			insert_InBuf(&input_joystick,ARE_frame.data[0]);
					  			insert_InBuf(&input_joystick,ARE_frame.data[1]);
								new_insert=&input_joystick;
							 }
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

	if (status_code & CIM_ERROR_INVALID_FEATURE)   {  // invalid data size or feature
		LEDs_ToggleLEDs(LED5);  // indicate wrong feature
	}
 
    if (new_insert)
	  if (free_in_InBuf(new_insert) < INBUF_WARNING) {  // indicate input buffer warning
		 LEDs_ToggleLEDs(LED7);
 		 status_code |= CIM_ERROR_CIM_NOT_READY;
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
		 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 27) {
		CIM_frame.data_size=sizeof(HID_CIM_FEATURELIST);     // feature list length
	    UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &HID_CIM_FEATURELIST, CIM_frame.data_size);
	} 
}

void reply_UniqueNumber(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8);
		 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 15) {
		CIM_frame.data_size=4;    // lenght of unique number
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
    	UART_Send_NonBlocking ((char *) &HID_CIM_UNIQUE_NUMBER, CIM_frame.data_size);
	} 
}

void reply_Acknowledge(void)
{
    uint8_t free_bytes = free_in_OutBuf(&output_buffer);

	if (free_bytes < OUTBUF_WARNING) {
	     CIM_frame.reply_code |= (CIM_ERROR_CIM_NOT_READY<<8) ;
		 LEDs_TurnOnLEDs(LED6);  // indicate frame loss
    }
	if (free_bytes > 11) {
		CIM_frame.data_size=0;     // no data in ack frame
    	UART_Send_NonBlocking ((char *) &CIM_frame, CIM_HEADER_LEN);
	} 
}

void reset_HID_CIM(void)
{ 
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

