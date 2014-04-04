/*
	Prototype taken from ARE plugin for adruino ADC plugin

*/


#include <avr/io.h>
#include <avr/interrupt.h>
#include <stdlib.h>
#include <stdio.h>
#include "usb_serial.h"
#include "CIM-n.h"
#include "PPM.h"
#include "globals.h"

#define ARE_MINIMAL_VERSION 1
#define FRAME_DONE 99


const uint32_t TEENSY_CIM_UNIQUE_NUMBER = 0x05060708;
uint8_t first_packet=1;
unsigned int  datapos=0;
struct ARE_frame_t ARE_frame;
struct CIM_frame_t CIM_frame;
unsigned char readstate=0;

uint16_t channel1;
uint16_t channel2;
uint16_t channel3;
uint16_t channel4;
uint16_t channel5;
uint16_t channel6;
uint16_t channel7;
uint16_t channel8;

const char TEENSY_CIM_FEATURELIST[]=
{
   0x00,0x00,  // unique number, data: 4 bytes 
   0x01,0x00,  // Set Controller Directions, data: 8 bytes, channel 1-8


};


void init_CIM_frame (void)
{
	CIM_frame.packet_id=CIM_FRAME_START;  // '@','T': Packet-ID/sync bytes
	CIM_frame.cim_id=CIM_ID_TEENSY;    
}


uint8_t process_ARE_frame(uint8_t status_code)
{
	
    uint8_t ack_needed;
	uint8_t data_size=0;
	uint8_t command;

	command=(uint8_t)ARE_frame.request_code;
	CIM_frame.cim_feature=ARE_frame.cim_feature;
	CIM_frame.serial_number=ARE_frame.serial_number;
	CIM_frame.reply_code=(((uint16_t)status_code)<<8) + command;
	data_size=(uint8_t)ARE_frame.data_size;

	ack_needed=1;

	if ((status_code & CIM_ERROR_INVALID_ARE_VERSION) == 0)
	{	
		switch (command)   // process requested command
		{
		
			case CMD_REQUEST_FEATURELIST:
				if(data_size==0) 
				{

					reply_FeatureList();  // reply requested feature list
					ack_needed=0;
				}
				else status_code |= CIM_ERROR_INVALID_FEATURE;
				break;

			case CMD_REQUEST_RESET_CIM:
					if (data_size!=0) status_code |= CIM_ERROR_INVALID_FEATURE;
					break;

		  	case CMD_REQUEST_START_CIM:
			        if(data_size==0) 
					{
						// do nothing?
					
					}
					else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;

		  	case CMD_REQUEST_STOP_CIM:
			       if (data_size==0)
				   { 
				   		//lets see
			   			
				   }
				   else status_code |= CIM_ERROR_INVALID_FEATURE;
				  break;			

			case CMD_REQUEST_WRITE_FEATURE:  //  write feature to CIM
				switch (ARE_frame.cim_feature)
				{  // which feature address ?

					case TEENSY_CIM_FEATURE_SET_PPM_VALUES:
						if(data_size == 16)
						{

						
							//Recieve and setup the incoming control values
							channel1 = ((ARE_frame.data[1] << 8) | ARE_frame.data[0]);
							channel2 = ((ARE_frame.data[3] << 8) | ARE_frame.data[2]);
							channel3 = ((ARE_frame.data[5] << 8) | ARE_frame.data[4]);
							channel4 = ((ARE_frame.data[7] << 8) | ARE_frame.data[6]);
							channel5 = ((ARE_frame.data[9] << 8) | ARE_frame.data[8]);
							channel6 = ((ARE_frame.data[11] << 8) | ARE_frame.data[10]);
							channel7 = ((ARE_frame.data[13] << 8) | ARE_frame.data[12]);
							channel8 = ((ARE_frame.data[15] << 8) | ARE_frame.data[14]);

						}
						else status_code |= CIM_ERROR_INVALID_FEATURE;

						break;

				}
				break;

			case CMD_REQUEST_READ_FEATURE:

					
					switch (ARE_frame.cim_feature) {

						case TEENSY_CIM_FEATURE_UNIQUENUMBER:				

							if(data_size == 0)
							{
								reply_UniqueNumber();
								ack_needed=0;
							}
							else
							{
								status_code |= CIM_ERROR_INVALID_FEATURE;

							}
						break;
					}
		break;

			default:

					break;

		}



	}else
		{

			 //DDRD|=(1<<6);
			 //PORTD|=(1<<6);

		}





return(1);
}


void reply_FeatureList(void)
{
	
	CIM_frame.data_size=sizeof(TEENSY_CIM_FEATURELIST);
	usb_serial_write((uint8_t *) &CIM_frame, CIM_HEADER_LEN);
	usb_serial_write((uint8_t *) &TEENSY_CIM_FEATURELIST, CIM_frame.data_size);

}

void reply_UniqueNumber(void)
{

	CIM_frame.data_size=4;
	usb_serial_write((uint8_t *)&CIM_frame, CIM_HEADER_LEN);
	usb_serial_write((uint8_t *)&TEENSY_CIM_UNIQUE_NUMBER, CIM_frame.data_size);

}


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
		  		  if (actbyte=='@')
				  {
				   readstate++;

		
				   } 
				  break;
		  case 1: // second sync byte
		  		  if (actbyte=='T')
				  {
				    readstate++;
				  
		

					// DDRD|=(1<<6);
					// PORTD|=(1<<6);

		
				  }
				  else readstate=0;
				  break;

 		  // packet in sync !

		  case 2: // ARE-ID: SW-version low byte
				  ARE_frame.are_id=actbyte;  
		  		  readstate++; 
				  break;
		  case 3: // ARE-ID: SW-version high byte
		  		  ARE_frame.are_id+=((uint16_t)actbyte)<<8;  
				  if (ARE_frame.are_id < ARE_MINIMAL_VERSION)
				  {    // outdated ARE ?
				     reply_status_code |= CIM_ERROR_INVALID_ARE_VERSION;

		  		 
				  }
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
