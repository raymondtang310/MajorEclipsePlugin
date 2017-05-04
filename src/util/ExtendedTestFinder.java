package util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

import analyzer.TestMethod;

/**
 * This class acts an extension to TestFinder.java, 
 * as it provides additional methods for retrieving tests. 
 * 
 * @author Raymond Tang
 *
 */

public class ExtendedTestFinder extends TestFinder {

	/**
	 * This method returns all the JUnit test methods defined within a given test class. 
	 * 
	 * @param clazz a test class
	 * @return the test methods in the given test class
	 */
	public static Collection<TestMethod> getTestMethods(Class<?> clazz) {
		Collection<Class<?>> col = new ArrayList<Class<?>>();
        col.add(clazz);
        return TestFinder.getTestMethods(col);
	}
	
	/**
	 * This method returns the pathname strings of all files in the given directory 
	 * containing test classes.
	 * 
	 * @param testLocation the location of the test directory as a pathname string
	 * @return the pathname strings of all files in the given directory containing test classes
	 */
	public static String[] getTestClassPathsFromDirectory(String testLocation) {
		File testDirectory = new File(testLocation);
		return testDirectory.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isFile();
			}
		});
	}
	
}
