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
 *     This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

#include <math.h>
#include "computebandpower.h"

/****************************************************/
/*                                                  */
/*                   CBP FFT                        */
/*                                                  */
/****************************************************/


#define CBP_NMAX 8192
#define CBP_NMAXSQRT 64

static int ip[CBP_NMAXSQRT + 2];	// work area for bit reversal
static double w[CBP_NMAX / 2];		// memory space for cos/sin table

// extern functions
void cbp_rdft(int n, int isgn, double *a, int *ip, double *w);

void CBP_initialization ()
{
	ip[0] = 0;	// w[],ip[] are initialized if ip[0] == 0.
}

void CBP_implementation (double * samples, int len, double * output)
{
	int i;
	int len_2 = len/2;

	cbp_rdft(len, 1, samples, ip, w);	// samples[2*k] = R[k], 0<=k<n/2
									// samples[2*k+1] = I[k], 0<k<n/2
									// samples[1] = R[n/2]

	output[0] = abs(samples[0]);
	for(i = 1; i < len_2; i++)	// module calculation
	{
		output[i] = sqrt(samples[i * 2] * samples[i * 2] + samples[(i * 2) + 1] * samples[(i * 2) + 1]);
	}
}

