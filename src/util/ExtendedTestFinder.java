package util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;

import analyzer.TestMethod;

/**
 * This class acts an extension to TestFinder.java, as it provides additional
 * methods for retrieving tests.
 * 
 * @author Raymond Tang
 *
 */

public class ExtendedTestFinder extends TestFinder {

	/**
	 * This method returns all the JUnit test methods defined within a given
	 * test class.
	 * 
	 * @param clazz
	 *            a test class
	 * @return the test methods in the given test class
	 */
	public static Collection<TestMethod> getTestMethods(Class<?> clazz) {
		Collection<Class<?>> col = new ArrayList<Class<?>>();
		col.add(clazz);
		return TestFinder.getTestMethods(col);
	}

	/**
	 * This method returns the pathname strings of all files in the given
	 * directory containing test classes.
	 * 
	 * @param testLocation
	 *            the location of the test directory as a pathname string
	 * @return the pathname strings of all files in the given directory
	 *         containing test classes
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

	/**
	 * Returns all test classes found in the java file's project.
	 * 
	 * @param testLocation
	 *            the directory in which test classes are located
	 * @param urlClassLoader
	 *            a classloader used to help retrieve a java class's Class
	 *            object
	 * @return all test classes found in the java file's project
	 * @throws ClassNotFoundException
	 */
	public static Collection<Class<?>> getTestClasses(String testLocation, ClassLoader urlClassLoader)
			throws ClassNotFoundException {
		String[] testClassFilenames = getTestClassPathsFromDirectory(testLocation);
		Collection<Class<?>> testClasses = new LinkedList<Class<?>>();
		for (String testClassFilename : testClassFilenames) {
			// String testClassName =
			// FilenameUtils.getBaseName(testClassFilename);
			String testClassName = "TestSuite";
			// For now, we assume that test classes are stored under a directory
			// named test
			String testFullyQualifiedName = "test." + testClassName;
			try {
				Class<?> testClass = Class.forName(testFullyQualifiedName, true, urlClassLoader);
				testClasses.add(testClass);
			} catch (ClassNotFoundException e) {
				throw new ClassNotFoundException("Could not locate/load/link test class named " + testClassName
						+ " from file named " + testClassFilename + " at " + testLocation, e);
			}
		}
		return testClasses;
	}

}
