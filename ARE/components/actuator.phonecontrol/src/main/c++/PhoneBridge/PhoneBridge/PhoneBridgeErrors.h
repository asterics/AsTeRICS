#pragma once

const int default_error = -1;
const int Library_no_initialized = -2;
const int Library_initialized = -3;
const int Library_initialize_error = -4;
const int No_respond_from_remote_device = -5;

const int Devices_are_searching_now = -20;
const int Device_found_error = -21;

const int Device_is_connected = -31;
const int Device_connect_error = -32;
const int Device_is_not_connected = -33;
const int Device_default_port_error= -34;

const int Data_empty = -50;

const int Remote_default_error = -1001;
const int Bluetooth_init_error = -1011;
const int Packet_error= - 1015;

const int Messager_init_error = -1031;
const int Messager_no_initialized = -1032;
const int Messager_send_message_error = -1033;

const int Phone_int_error = -1051;
const int Phone_no_initialized = -1052;
const int Phone_accept_error = -1053;
const int Phone_drop_error = -1054;
const int Phone_call_error = -1055;

const int Phone_and_Messager_no_initialized = -1072;

const int dll_library_not_found = -2010;
const int get_function_error= -2011;
const int phone_not_found= -2012;
const int phone_search_error = -2013;