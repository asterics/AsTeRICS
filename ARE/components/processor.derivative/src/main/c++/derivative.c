
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
#include <stdlib.h>
#include "derivative.h"


/****************************************************/
/*                                                  */
/*                   DERIVATIVE                     */
/*                                                  */
/****************************************************/


#define D_NUM_POINTS	5			// Number of points used in the derivative calculation

#define D_BUFFER_MAX	(D_NUM_POINTS - 1)	// size of the buffer
//#define D_SAMPLE_FREQUENCY		250			// Should be configurable?
//#define D_h						((double)(1/D_SAMPLE_FREQUENCY))	// distance between two consecutive samples.

/*static double D_buffer[D_BUFFER_MAX];		// Circular buffer where the older values to calculate the derivative
											// are saved.
static int D_i = 0;
static double D_h;
static BOOL D_initiated = 0;
static int D_sample_frequency = 250;

static int debugCounter = 0;*/

typedef struct _workspace
{
	double D_buffer[D_BUFFER_MAX];		// Circular buffer where the older values to calculate the derivative
										// are saved.
	int D_i;
	double D_h;
	BOOL D_initiated;
	int D_sample_frequency;
	int debugCounter;
	int D_id;
} workspace;

static int D_N_workspaces = 0;
static int D_IDs = 0;

static workspace ** ptrWorkspaces = NULL;

int Derivative_new ()
{
	int i;
	int nWs;
	workspace ** ptrWs;
	workspace * ptrNewWs;

	ptrNewWs = (workspace*)malloc(sizeof(workspace));
	if (ptrNewWs == NULL)
	{
		return -1;
	}
	nWs = D_N_workspaces + 1;
	ptrWs = (workspace**)malloc(sizeof(workspace*) * nWs);
	if (ptrWs == NULL)
	{
		return -1;
	}
	for (i = 0; i < D_N_workspaces; i++)
	{
		ptrWs[i] = ptrWorkspaces[i];
	}
	ptrWs[i] = ptrNewWs; // the last one
	if (ptrWorkspaces != NULL) // free previous memory allocation if it exists
	{
		free(ptrWorkspaces);
	}
	ptrWorkspaces = ptrWs;
	D_N_workspaces++;
	D_IDs++;
	ptrWorkspaces[i]->D_id = D_IDs;
	ptrWorkspaces[i]->D_i = 0;
	ptrWorkspaces[i]->D_h;
	ptrWorkspaces[i]->D_initiated = 0;
	ptrWorkspaces[i]->D_sample_frequency = 250;
	ptrWorkspaces[i]->debugCounter = 0;
	return D_IDs;
}

int _indexFromId (int id)
{
	int i;
	for (i = 0; i < D_N_workspaces; i++)
	{
		if (ptrWorkspaces[i]->D_id == id)
		{
			return i;
		}
	}
	return -1;
}

int Derivative_delete(int id)
{
	int i;
	int idx;
	int nWs;
	workspace ** ptrWs = NULL;

	idx = _indexFromId(id);

	if(idx < 0 || idx >= D_N_workspaces)
	{
		return -1;
	}
	nWs = D_N_workspaces - 1;
	if (nWs > 0)
	{
		ptrWs = (workspace**)malloc(sizeof(workspace*) * nWs);
		if (ptrWs == NULL)
		{
			return -1;
		}
	}
	for (i = 0; i < idx; i++)
	{
		ptrWs[i] = ptrWorkspaces[i];
	}
	for (i = idx +1; i < D_N_workspaces; i++)
	{
		ptrWs[i - 1] = ptrWorkspaces[i];
	}
	free(ptrWorkspaces);
	ptrWorkspaces = ptrWs;
	return 0;
}

void Derivative_reset (int id)
{
	int idx = _indexFromId(id);
	if (idx < 0)
	{
		return;
	}
	ptrWorkspaces[idx]->D_h = 1/(double)ptrWorkspaces[idx]->D_sample_frequency;
	ptrWorkspaces[idx]->D_i = 0;
	ptrWorkspaces[idx]->D_initiated = 0;
	memset(ptrWorkspaces[idx]->D_buffer, 0, D_BUFFER_MAX * sizeof(*ptrWorkspaces[idx]->D_buffer));
}

int getSampleFrequency (int id)
{
	int idx = _indexFromId(id);
	if (idx < 0)
	{
		return -1;
	}
	return ptrWorkspaces[idx]->D_sample_frequency;
}

int setSampleFrequency (int id, int sampleFrequency)
{
	int idx = _indexFromId(id);
	if (idx < 0)
	{
		return -1;
	}
	if (sampleFrequency != ptrWorkspaces[idx]->D_sample_frequency)
	{
		ptrWorkspaces[idx]->D_sample_frequency = sampleFrequency;
		Derivative_reset(id);
	}
	return sampleFrequency;
}

double Derivative (int id, double sample)
{
	//       -f(x + 2h) + 8f(x + h) -8f(x - h) + f(x - 2h)
	// f' =  ---------------------------------------------
	//                             12 h
	//
	//				D_buffer (circular)
	// |-------|	|-------|-------|-------|-------|    
	// |sample |	|D_i + 3|  D_i  |D_i + 1|D_i + 2|    
	// |-------|	|-------|-------|-------|-------|    
	//  
	//  f(x + 2h) ==> sample
	//  f(x + h)  ==> D_i + 3
	//  f(x)      ==> D_i + 2
	//  f(x - h)  ==> D_i + 1
	//  f(x - 2h) ==> D_i

	double ret;

	int idx = _indexFromId(id);
	if (idx < 0)
	{
		return 0;
	}

	 ret = (-sample +
			(8 * ptrWorkspaces[idx]->D_buffer[(ptrWorkspaces[idx]->D_i + 3) % D_BUFFER_MAX]) - 
			(8 * ptrWorkspaces[idx]->D_buffer[(ptrWorkspaces[idx]->D_i + 1) % D_BUFFER_MAX]) +
			ptrWorkspaces[idx]->D_buffer[ptrWorkspaces[idx]->D_i]) / 
			(12 * ptrWorkspaces[idx]->D_h);
	 
	ptrWorkspaces[idx]->D_buffer[ptrWorkspaces[idx]->D_i] = sample;
	if (++ptrWorkspaces[idx]->D_i >= D_BUFFER_MAX)
	{
		ptrWorkspaces[idx]->D_i = 0;
		ptrWorkspaces[idx]->D_initiated = 1;
	}
	return ret;
}
