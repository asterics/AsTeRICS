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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *         Dual License: MIT or GPL v3.0 with "CLASSPATH" exception
 *         (please refer to the folder LICENSE)
 * 
 */

package eu.asterics.component.processor.basictralgorithms;

/**
 * 
 * Implements the class which keeps coordinates of the point for tremor
 * reduction algorithms
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Mar 15, 2012 Time: 10:06:15
 *         AM
 */
class AlgorithmPoint {
    private int x;
    private int y;

    /**
     * The class constructor.
     */
    public AlgorithmPoint() {
        x = 0;
        y = 0;
    }

    /**
     * The class constructor.
     * 
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     */
    public AlgorithmPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The class constructor.
     * 
     * @param p
     *            the point
     */
    public AlgorithmPoint(AlgorithmPoint p) {
        this.x = p.getX();
        this.y = p.getY();
    }

    /**
     * Return the x coordinate.
     * 
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Return the y coordinate.
     * 
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x coordinate.
     * 
     * @param x
     *            coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate.
     * 
     * @param y
     *            coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

}

/**
 * 
 * implements the abstract class for tremor reduction algorithms. Classes which
 * implement tremor reduction algorithms should inherit from this class.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Mar 15, 2012 Time: 10:06:15
 *         AM
 */
abstract class TremorReductionBasicAlgorithm {
    /**
     * This method calculates the new cursor position based on actual cursor
     * position.
     * 
     * @param inputPoint
     *            actual cursor position
     * @return new cursor position
     */
    public abstract AlgorithmPoint calcualteNewPoint(AlgorithmPoint inputPoint);

    /**
     * Cleans the algorithm internal data and state to default.
     */
    public abstract void clean();
}