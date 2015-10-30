package eu.asterics.mw.services;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {

	/**
	 * Returns a list of files contained in the given path and the given extension.
	 * @param where
	 * @param extension
	 * @param maxDeep: Do recursive search at most to a level of maxDeep
	 * @return
	 */
	public static List<File> findFiles(File where, String extension, int maxDeep)
	 {
		 File[] files = where.listFiles();
		 ArrayList<File> result = new ArrayList<File>();
	
		 if(files != null)
		 {
			 for (File file : files) 
			 {
				 if (file.isFile()) {
					 if(file.getName().endsWith(extension)) {
						 result.add(file);
					 }
				 }
				 else if ( (file.isDirectory()) &&( maxDeep-1 > 0 ) )
				 {
					 // do the recursive crawling
			         List<File> temp = findFiles(file, extension, maxDeep-1);
		             result.addAll(temp);
				 }
			 }
		 }
		 return result;
	 }
	
	/**
	 * Returns a recursive list of URI objects starting with the directory path given in start.
	 * 
	 * start: The directory path to start with.
	 * relative: true: paths relative to start are returned.
	 * maxDeep: number of recursive invocations. 0, 1 means flat search. 
	 * filter: instance of {@link FilenameFilter} that filters accepted and unaccepted files.
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
	
		 if(files != null)
		 {
			 for (File file : files) 
			 {
				 if (file.isFile()) {
					 //Ask the given filter implementation if the file is accepted.
					 if(filter.accept(file.getParentFile(), file.getName())) {
						 //Convert to relative URI if required.
						 URI uri2Add=relative ? start.relativize(file.toURI()) : file.toURI();
						 result.add(uri2Add);
					 }
				 }
				 else if ( (file.isDirectory()) &&( maxDeep-1 > 0 ) )
				 {
					 // do the recursive crawling
			         List<URI> temp = findFiles(start, file, relative, maxDeep-1, filter);
			         result.addAll(temp);
				 }
			 }
		 }
		 return result;
	}
}
