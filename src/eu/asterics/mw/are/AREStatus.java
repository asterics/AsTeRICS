package eu.asterics.mw.are;


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


/**
 * @author 
 * This class depicts the status of the ARE. The status can be one of the following:
 * unknown, ok, deployed, running, paused, error or fatal error.
 * 
 * ARE Status Definition:
 * UNKNOWN		-	Initial state
 * OK			-	ARE is running, ready to deploy a model
 * DEPLOYED		-	A model has been deployed, ready to run the model
 * RUNNING		-	A model is running on the ARE
 * PAUSED		-	A model has been deployed, the model is in pause mode
 * ERROR		-	An error occurred
 * FATAL_ERROR	-	A fatal error occurred, model or deployment aborted.
 * Date: 
 */

public enum AREStatus {

	UNKNOWN("unknown"),
    OK("ok"),
    DEPLOYED("deployed"),
	RUNNING("running"),
	PAUSED("paused"),
	ERROR("error"),
	FATAL_ERROR("fatal error");

    private final String status;

    private AREStatus(final String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return status;
    }
}
