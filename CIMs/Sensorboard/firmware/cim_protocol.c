#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/twi.h>
#include <avr/sleep.h>
#include <util/delay.h>
#include <inttypes.h>

#include "periphery.h"
#include "usb_serial.h"
#include "cim_protocol.h"
#include "twi_master.h"

#define ARE_MINIMAL_VERSION 1

const uint32_t CIM_UNIQUE_SERIAL_NUMBER = 0xFF00FF01;

const char SENSORBOARD_CIM_FEATURELIST[]=
{
	CIM_FEATURE_UNIQUENUMBER,				// unique number, data: 4 bytes 
	CIM_FEATURE_PERIODICREPORTS,		// activate periodic reports, write data: 2 bytes, report data: 46 bytes
	CIM_FEATURE_SENSORVALUEREPORT,	// report of measurement, data: 46 bytes
} ;

struct CIM_frame_t CIM_header = {
	0x5440,			/* packet_id [0][1]*/
	0xA201,			/* cim_id [2][3]*/
	0x0023,			/* data_size [4][5]*/
	0x00, 			/* serial_number [6]*/
	0x0002,			/* cim_feature; [7][8]*/
  0x0020,			/* reply_code [9][10]*/
  0x00,				/* *data [11]*/
  0x0000000000000000	/* crc */
};

struct sensor_data_t *ptr_p_sensor_data;
volatile uint8_t packet_counter = 0x80;

struct ARE_frame_t ARE_packet;
volatile uint8_t ARE_data [ARE_PACKET_SIZE-11];
volatile uint8_t reply_msg [CIM_PACKET_SIZE];

/*****************************************
reads in the usb_serial buffer values
******************************************/
void fetch_ARE_packet(void) {
	char synced = 0;

	do {	/* resync to character string 0x4054 */
		ARE_packet.packet_id = usb_serial_getchar();
		if (ARE_packet.packet_id == 0x40) {
			ARE_packet.packet_id |= (usb_serial_getchar() << 8);
			if (ARE_packet.packet_id == 0x5440) synced = 1;
		}
	}while (!synced && usb_serial_available());
	
	if (synced) {
		ARE_packet.are_id = usb_serial_getchar();
		ARE_packet.are_id |= (usb_serial_getchar() << 8);		
		ARE_packet.data_size = usb_serial_getchar();
		ARE_packet.data_size |= (usb_serial_getchar() << 8);
		ARE_packet.serial_number = usb_serial_getchar();		
		ARE_packet.cim_feature = usb_serial_getchar();
		ARE_packet.cim_feature |= (usb_serial_getchar() << 8);
		ARE_packet.request_code = usb_serial_getchar();
		ARE_packet.request_code |= (usb_serial_getchar() << 8);
		
		for (int i = 0; i < ARE_packet.data_size; i ++) {
			ARE_data[i] = usb_serial_getchar();
		}
	}
}

/*****************************************
processes the data sent from ARE
******************************************/
void process_ARE_packet(void) {
	uint8_t ack_needed = 1;	
	static uint8_t next_serial = 0;


	if (ARE_packet.packet_id == 0x5440) { /* check if data is synced */
		uint8_t status_code = (ARE_packet.request_code >> 8);
		uint8_t command = (ARE_packet.request_code & 0xFF);

		if ((status_code & CIM_ERROR_INVALID_ARE_VERSION) == 0) {
		
			if (next_serial == ARE_packet.serial_number) {
				next_serial = ((next_serial + 1) & 0x7F);
			}
			else {	
				status_code |= CIM_ERROR_LOST_PACKETS;
				next_serial = ARE_packet.serial_number +1;
			}
			switch (command) {
				case CMD_REQUEST_FEATURELIST:
					if (ARE_packet.data_size == 0) {
						reply_FeatureList ();
						ack_needed=0;
					}
					else {
						status_code |= CIM_ERROR_INVALID_FEATURE;
					}
					break;

				case CMD_REQUEST_WRITE_FEATURE:
					switch (ARE_packet.cim_feature) {

						case CIM_FEATURE_PERIODICREPORTS://Activate Periodic Value Reports	bytes 0,1: period time 0 (off) to 65535 milliseconds
							set_measurement_period((ARE_data[0])|ARE_data[1]<<8);
							enable_measurement();	
						break;

						default:
						break;
					}
					break;

				case CMD_REQUEST_READ_FEATURE:					
					switch (ARE_packet.cim_feature) {
						case CIM_FEATURE_UNIQUENUMBER://request unique serial number
							reply_UniqueNumber();
							ack_needed = 0;
							
						break;

						case CIM_FEATURE_SENSORVALUEREPORT://Sensor Value Report; send measurement once	
							reply_SensorValueReport();
							ack_needed = 0;
						break;
			
						default:
							status_code |= CIM_ERROR_INVALID_FEATURE;
						break;
					}
					break;

				case CMD_REQUEST_RESET_CIM:
					if (ARE_packet.data_size == 0) {
					}		else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;

				case CMD_REQUEST_START_CIM:
					if (ARE_packet.data_size == 0) {
						enable_measurement();	
					}	else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;

				case CMD_REQUEST_STOP_CIM:
					if (ARE_packet.data_size == 0) {
						disable_measurement();
					}	else status_code |= CIM_ERROR_INVALID_FEATURE;
					break;

				default:
					break;		
			
		
				
				}
			if (ack_needed == 1) {
					reply_Acknowledge(status_code);		/* send back the data received for ACK */
			}
		}	
		else {
			//invalid ARE version
		}
	}
}

/*****************************************
generate the header for the periodic transmit buffer
******************************************/
void create_CIM_template (uint8_t *buf) {
	*buf = CIM_header.packet_id & 0x00FF;
	buf++;
	*buf = CIM_header.packet_id >> 8;
	buf++;
	*buf = CIM_header.cim_id & 0x00FF;
	buf++;
	*buf = CIM_header.cim_id >> 8;
	buf++;
	*buf = CIM_header.data_size & 0x00FF;
	buf++;
	*buf = CIM_header.data_size >>8;
	buf++;
	*buf = CIM_header.serial_number;
	buf++;
	*buf = CIM_header.cim_feature & 0x00FF;
	buf++;
	*buf = CIM_header.cim_feature >> 8;
	buf++;
	*buf = CIM_header.reply_code & 0x00FF;
	buf++;
	*buf = CIM_header.reply_code >> 8;
}


/*****************************************
generate the periodic message to be sent to the ARE
******************************************/
uint8_t create_CIM_data_packet(struct sensor_data_t *ptr_m_values, uint8_t *buf) {
	uint16_t temp;

	buf = buf + 6;
	*buf = packet_counter;
	packet_counter = (packet_counter + 1) | 0x80;
	buf = buf + 5;

	temp = (*ptr_m_values).ADXL_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ADXL_x;
	*buf = (temp >> 8);
	buf++;
	temp = (*ptr_m_values).ADXL_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).ADXL_y;
	*buf = (temp >> 8);
	buf++;
	temp = (*ptr_m_values).ADXL_z;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ADXL_z;
	*buf = (temp >> 8);
	buf++;

	temp = (*ptr_m_values).gyro_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).gyro_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).gyro_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).gyro_y;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).gyro_z;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).gyro_z;
	*buf = temp >> 8;
	buf++;

	temp = (*ptr_m_values).compass_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).compass_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).compass_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).compass_y;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).compass_z;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).compass_z;
	*buf = temp >> 8;
	buf++;
	
	temp = (*ptr_m_values).ir1_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ir1_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).ir1_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).ir1_y;
	*buf = temp >> 8;
	buf++;

	temp = (*ptr_m_values).ir2_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ir2_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).ir2_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).ir2_y;
	*buf = temp >> 8;
	buf++;
		
	temp = (*ptr_m_values).ir3_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ir3_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).ir3_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).ir3_y;
	*buf = temp >> 8;
	buf++;
		
	temp = (*ptr_m_values).ir4_x;
	*buf = temp & 0x00FF;
	buf++;
	temp = (*ptr_m_values).ir4_x;
	*buf = temp >> 8;
	buf++;
	temp = (*ptr_m_values).ir4_y;
	*buf = temp & 0x00FF;
	buf++;
	temp=  (*ptr_m_values).ir4_y;
	*buf = temp >> 8;
	buf++;

	*buf = (*ptr_m_values).pressure;

	return 1;
}

/*****************************************
send the data via usb/serial interface
******************************************/
uint8_t send_CIM_packet(uint8_t *buf) {
	usb_serial_write (buf, CIM_PACKET_SIZE);
	return 1;
}

void create_reply_package (uint16_t data_size, uint8_t status_code) {
	reply_msg [0] = (CIM_header.packet_id &0xFF);
	reply_msg [1] = (CIM_header.packet_id >>8);
	reply_msg [2] = (CIM_header.cim_id & 0xFF);
	reply_msg [3] = (CIM_header.cim_id >> 8);
	reply_msg [4] = (data_size & 0xFF);
	reply_msg [5] = (data_size >> 8);
	reply_msg [6] = ARE_packet.serial_number;
	reply_msg [7] = (ARE_packet.cim_feature & 0xFF);
	reply_msg [8] = (ARE_packet.cim_feature >> 8);
	reply_msg [9] = (ARE_packet.request_code & 0xFF);
	reply_msg [10] = status_code;
}

void reply_FeatureList (void) {	
	
	uint16_t data_size=sizeof(SENSORBOARD_CIM_FEATURELIST); 
	create_reply_package (data_size, 0);

	int j = 0;
	for (int i = 11; i < (data_size*2+11); i ++) {
		reply_msg [i] = (SENSORBOARD_CIM_FEATURELIST[j] & 0xFF);
		i++;
		reply_msg [i] = (SENSORBOARD_CIM_FEATURELIST[j] >> 8);
		j++;
	}
}

void reply_UniqueNumber (void) {
	
	create_reply_package (4, 0);

	reply_msg[11] = (CIM_UNIQUE_SERIAL_NUMBER & 0xFF);
	reply_msg[12] = ((CIM_UNIQUE_SERIAL_NUMBER >> 8) & 0xFF);
	reply_msg[13] = ((CIM_UNIQUE_SERIAL_NUMBER >> 16) & 0xFF);
	reply_msg[14] = (CIM_UNIQUE_SERIAL_NUMBER >> 24);

	usb_serial_write ((uint8_t*)reply_msg,15);
}

void reply_SensorValueReport (void) {
	
}

void reply_Acknowledge (uint8_t status_code) {
	create_reply_package (0, status_code);
	usb_serial_write ((uint8_t*)reply_msg,11);
}


