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
 *         This project has been funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

#pragma once
//-----------------------------------------------------------------------------
#include <iostream>
#include <fstream>
//-----------------------------------------------------------------------------
#include <boost/chrono.hpp>
//-----------------------------------------------------------------------------
//#define __UPMC_USE_SYSTEM_CLOCK__
//-----------------------------------------------------------------------------
namespace upmc{
//-----------------------------------------------------------------------------
///
class tictoc_t
{
public:
	inline friend std::ostream& operator<<(std::ostream &out, const tictoc_t& ob);
	typedef boost::chrono::milliseconds ms_t;
public:
	inline void tic() ;
	inline double toc_sec() const;
	inline double toc_msec() const;
private:

#if defined(__UPMC_USE_SYSTEM_CLOCK__)
//system clock
	boost::chrono::system_clock::time_point _start;
#else
//thread clock
	boost::chrono::thread_clock::time_point _start;//=boost::chrono::thread_clock::now();
#endif
};
//#############################################################################
//#############################################################################
#if defined(__UPMC_USE_SYSTEM_CLOCK__)
void tictoc_t::tic()
{
	_start = boost::chrono::system_clock::now();
}
//-----------------------------------------------------------------------------
double tictoc_t::toc() const
{
	boost::chrono::duration<double> _sec = boost::chrono::system_clock::now() - _start;
	return (_sec.count());
}
//-----------------------------------------------------------------------------
std::ostream& operator<<(std::ostream& out,  const tictoc_t& ob)
{
	boost::chrono::duration<double> _sec = boost::chrono::system_clock::now() - ob._start;
	out << _sec.count();
	return out;
}
#else
void tictoc_t::tic()
{
	_start = boost::chrono::thread_clock::now();
}
//-----------------------------------------------------------------------------
double tictoc_t::toc_sec() const
{
	return (boost::chrono::thread_clock::now() - _start).count();
}
//-----------------------------------------------------------------------------
double tictoc_t::toc_msec() const
{
	return boost::chrono::duration_cast<tictoc_t::ms_t>(boost::chrono::thread_clock::now() - _start).count() ;
}
//-----------------------------------------------------------------------------
std::ostream& operator<<(std::ostream& out,  const tictoc_t& ob)
{
	tictoc_t::ms_t _msec = 
		boost::chrono::duration_cast<tictoc_t::ms_t>(boost::chrono::thread_clock::now() - ob._start) ;

	out << _msec.count() << " msec.";
	return out;
}
#endif
//-----------------------------------------------------------------------------
//-----------------------------------------------------------------------------
}//namespace upmc