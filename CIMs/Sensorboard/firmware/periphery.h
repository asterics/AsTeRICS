#ifndef periphery_h__
#define periphery_h__

#include <inttypes.h>

#define ADXL 0x53
#define ADXL_W 0xA6
#define ADXL_R 0xA7
#define gyro 0x68
#define gyro_W 0xD0
#define gyro_R 0xD1
#define compass 0x1E
#define compass_W 0x3C
#define compass_R 0x3D
#define ircam 0x58
#define ircam_W 0xB0
#define ircam_R 0xB1

#define MEASUREMENT_BUFSIZE 0x03

struct sensor_data_t {
	uint16_t ADXL_x;
	uint16_t ADXL_y;
	uint16_t ADXL_z;
	uint16_t gyro_x;
	uint16_t gyro_y;
	uint16_t gyro_z;
	uint16_t compass_x;
	uint16_t compass_y;
	uint16_t compass_z;
	uint16_t ir1_x;
	uint16_t ir1_y;
	uint16_t ir2_x;
	uint16_t ir2_y;
	uint16_t ir3_x;
	uint16_t ir3_y;
	uint16_t ir4_x;
	uint16_t ir4_y;
	uint8_t  pressure;
};


void init_LEDs(void);
void toggle_orangeLED(void);
void on_orangeLED(void);
void toggle_greenLED(void);
void on_greenLED(void);
void init_Timer0 (void);
void enable_measurement (void);
void disable_measurement (void);
void set_measurement_period(uint16_t val);
void init_ADC(void);
void start_ADC_single_conv(void);

uint8_t measurement_data_available(void);
void clear_measurement_data(void);
struct sensor_data_t *fetch_struct(uint8_t sel_measurement);

void finish_measurement(void);
void write_ADXL_measurement (uint8_t *);
void write_gyro_measurement (uint8_t *);
void write_compass_measurement (uint8_t *);
void write_ir_measurement (uint8_t *);
void write_ADC_measurement (uint8_t data);

#endif
