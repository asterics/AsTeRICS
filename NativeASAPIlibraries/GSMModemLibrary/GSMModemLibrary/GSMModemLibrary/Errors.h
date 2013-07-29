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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */


#pragma once


const int initialization_complete = 2;


const int default_error = -1;
const int Library_no_initialized = -2;
const int Library_initialized = -3;
const int Library_initialize_error = -4;
const int Library_is_not_ready=-5;
const int Abort = -7;

const int COMM_initialize_false=-10;
const int No_respond_on_AT_command=-11;
const int Cannot_register_to_the_network=-12;
const int Modem_initialize_false=-13;
const int Write_Modem_Port_error=-14;
const int Read_Modem_Port_error=-15;
const int Not_enought_space_in_buffer=-16;
const int No_modem_answer=-17;
const int Command_fail=-19;
const int SMS_read_error=-20;
const int SMS_send_error=-21;
const int Phone_id_empty=-22;
const int Message_content_empty=-23;
const int Error_respond_from_Modem=-24;
const int Undefined_modem_answer=-25;
const int String_is_not_a_number=-26;
const int Echo_detected=-27;

const int SMS_not_sent = -100;