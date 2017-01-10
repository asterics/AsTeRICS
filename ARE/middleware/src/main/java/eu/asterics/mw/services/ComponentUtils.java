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

package eu.asterics.mw.services;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {

    /**
     * Returns a list of files contained in the given path and the given
     * extension.
     * 
     * @param where
     * @param extension
     * @param maxDeep:
     *            Do recursive search at most to a level of maxDeep
     * @return
     */
    public static List<File> findFiles(File where, String extension, int maxDeep) {
        File[] files = where.listFiles();
        ArrayList<File> result = new ArrayList<File>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(extension)) {
                        result.add(file);
                    }
                } else if ((file.isDirectory()) && (maxDeep - 1 > 0)) {
                    // do the recursive crawling
                    List<File> temp = findFiles(file, extension, maxDeep - 1);
                    result.addAll(temp);
                }
            }
        }
        return result;
    }

    /**
     * Returns a recursive list of URI objects starting with the directory path
     * given in start.
     * 
     * start: The directory path to start with. relative: true: paths relative
     * to start are returned. maxDeep: number of recursive invocations. 0, 1
     * means flat search. filter: instance of {@link FilenameFilter} that
     * filters accepted and unaccepted files.
     * 
     * @param start
     * @param relative
     * @param maxDeep
     * @param filter
     * @return
     */
    public static List<URI> findFiles(URI start, boolean relative, int maxDeep, FilenameFilter filter) {
        return findFiles(start, new File(start), relative, maxDeep, filter);
    }

    private static List<URI> findFiles(URI start, File where, boolean relative, int maxDeep, FilenameFilter filter) {
        File[] files = where.listFiles();
        List<URI> result = new ArrayList<URI>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Ask the given filter implementation if the file is
                    // accepted.
                    if (filter.accept(file.getParentFile(), file.getName())) {
                        // Convert to relative URI if required.
                        URI uri2Add = relative ? start.relativize(file.toURI()) : file.toURI();
                        result.add(uri2Add);
                    }
                } else if ((file.isDirectory()) && (maxDeep - 1 > 0)) {
                    // do the recursive crawling
                    List<URI> temp = findFiles(start, file, relative, maxDeep - 1, filter);
                    result.addAll(temp);
                }
            }
        }
        return result;
    }
}
