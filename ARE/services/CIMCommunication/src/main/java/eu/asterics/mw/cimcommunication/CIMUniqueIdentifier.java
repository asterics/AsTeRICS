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
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 *
 */

package eu.asterics.mw.cimcommunication;

public class CIMUniqueIdentifier {

    final short CIMId;
    final long CIMUniqueNumber;

    public CIMUniqueIdentifier(short CIMId, long CIMUniqueNumber) {
        this.CIMId = CIMId;
        this.CIMUniqueNumber = CIMUniqueNumber;
    }

    @Override
    public String toString() {
        return String.format("CIM Id: 0x%x, UniqueNumber: 0x%x", CIMId, CIMUniqueNumber);
    }

    public String toIdentifierString() {
        return String.format("0x%x-0x%x", CIMId, CIMUniqueNumber);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + CIMId;
        result = prime * result + (int) (CIMUniqueNumber ^ (CIMUniqueNumber >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CIMUniqueIdentifier other = (CIMUniqueIdentifier) obj;
        if (CIMId != other.CIMId) {
            return false;
        }
        if (CIMUniqueNumber != other.CIMUniqueNumber) {
            return false;
        }
        return true;
    }
}
