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
 * Implements the Outlier Reduction algorithm for relative position points. If
 * the cursor move is greater than the the maximum allowed distance, it is
 * reduced to the maximum allowed distance.
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl] Date: Mar 15, 2012 Time: 10:06:15
 *         AM
 */
class OutlierReductionAlgorithmRelative extends TremorReductionBasicAlgorithm {

    private double distance;

    /**
     * The class constructor.
     */
    public OutlierReductionAlgorithmRelative() {
        distance = 10.0;
    }

    /**
     * The class constructor.
     * 
     * @param distance
     *            maximum allowed distance
     */
    public OutlierReductionAlgorithmRelative(double distance) {
        this.distance = distance;
    }

    /**
     * Calculates the new cursor position.
     * 
     * @param inputPoint
     *            current cursor position
     * @return new cursor position
     */
    @Override
    public AlgorithmPoint calcualteNewPoint(AlgorithmPoint inputPoint) {

        double factor = inputPoint.getX() * inputPoint.getX() + inputPoint.getY() * inputPoint.getY();
        factor = Math.sqrt(factor);

        if (factor > distance) {
            double x = (double) (inputPoint.getX()) / factor;
            double y = (double) (inputPoint.getY()) / factor;

            int newX = (int) (x * distance);
            int newY = (int) (y * distance);

            if (Math.abs(x * distance - (double) newX) > 0.5) {
                if (x >= 0) {
                    newX++;
                } else {
                    newX--;
                }
            }

            if (Math.abs(y * distance - (double) newY) > 0.5) {
                if (y >= 0) {
                    newY++;
                } else {
                    newY--;
                }
            }
            AlgorithmPoint outputPoint = new AlgorithmPoint(newX, newY);
            return outputPoint;

        } else {
            return inputPoint;
        }

    }

    /**
     * Sets the new maximum distance.
     * 
     * @param distance
     *            maximum allowed distance
     */
    public void SetDistance(double distance) {
        if (distance > 0) {
            this.distance = distance;
        }
    }

    /**
     * Do nothing.
     */
    @Override
    public void clean() {

    }
}