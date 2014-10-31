package eu.asterics.mw.services;

import java.io.File;
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
				 if (file.isFile() && file.getName().endsWith(extension))
					 result.add(file);
				 else if ( (file.isDirectory()) &&( maxDeep-1 > 0 ) )
				 {
					 // do the recursive crawling
			         List<File> temp = findFiles(file, extension, maxDeep-1);
		             for(File thisFile : temp)
		                   result.add(thisFile);
				 }
			 }
		 }
		 return result;
	 }
	

}
