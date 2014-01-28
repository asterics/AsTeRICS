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

package eu.asterics.mw.data;

import java.util.logging.Logger;

import eu.asterics.mw.services.AstericsErrorHandling;

/**
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 * Date: Feb 1, 2010
 * Time: 9:17:33 AM
 */
public class ImmutableArray<T> implements Immutable
{
    private final T [] array;
    private Logger logger = null;
    
    public ImmutableArray(final T [] array)
    {
        super();
        logger = AstericsErrorHandling.instance.getLogger();

        if(array == null)
        {
        	logger.severe(this.getClass().getName()+
      		": ImmutableArray-> Invalid null argument");
            throw new IllegalArgumentException("Invalid null argument");
        }

        this.array = array.clone();
    }

    public T get(final int index) throws ArrayIndexOutOfBoundsException
    {
        return array[index];
    }

    public int getSize()
    {
        return array.length;
    }

    @Override
    public String toString()
    {
        final int numOfElements = array.length;
        int counter = 0;
        final StringBuilder stringBuilder = new StringBuilder("[");
        for(final T t : array)
        {
            stringBuilder
                    .append(t.toString())
                    .append(counter++ == numOfElements - 1 ? "]" : ", ");
        }
        return stringBuilder.toString();
    }

    public String toShortString()
    {
        return super.toString() + " (" + array.length + " elements of type " +
                array[0].getClass() + ")";
    }
}