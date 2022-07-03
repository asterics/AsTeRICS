
#ifndef twi_master_h__
#define twi_master_h__

#define TWI_buf_size 20


void init_twi(void);
uint8_t TWI_device_status(void);
void init_ADXL (void);
void init_gyro (void);
void init_compass (void);
void init_ir (void);
void write_loop_twi (uint8_t SLA_W, uint8_t data_register, uint8_t data);
void write_loop_twi_multiple_data (uint8_t SLA_W, uint8_t data_register, uint8_t *data, uint8_t size);
uint8_t  start_twi_with_data(uint8_t *message, uint8_t msg_size);
void waitForTransmission (void);
void TWI_errorRoutine(uint8_t);

#endif
