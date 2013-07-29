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