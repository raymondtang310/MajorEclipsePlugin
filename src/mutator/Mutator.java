package mutator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.rayzor.mutantview.views.MutantView;

import killmap.ExtendedTestFinder;
import major.mutation.Config;

/**
 * This program generates and compiles mutants in the selected java file using Major.java. 
 * 
 * @author Raymond Tang
 *
 */
public class Mutator {
	
	public static void main(String[] args) {
		// Pathname of the file to mutate
		String fileToMutateLocation = args[0];
		// Pathname of the file's project
		String projectLocation = args[1];
		// Pathname of the project's bin directory
		String binLocation = args[2];
		// Pathname of the project's test directory
		String testLocation = args[3];
		File file = new File(fileToMutateLocation);
		// Fully qualified name of the file
		String fullyQualifiedName = getFullyQualifiedName(fileToMutateLocation, projectLocation);
		try {
			// Mutate the java file
			Major m = new Major(file, fullyQualifiedName, projectLocation);
			m.setExportMutants(true);
			m.mutate();
			
			// Add bin and test directories of Triangle project to classpath
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(binLocation).toURI().toURL(), new File(testLocation).toURI().toURL()}, Config.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			// Get test classes
			String[] testClassFilenames = ExtendedTestFinder.getTestClassesFromDirectory(testLocation);
			Collection<Class<?>> testClasses = new LinkedList<Class<?>>();
			for(String testClassFilename : testClassFilenames) {
				String testClassName = FilenameUtils.getBaseName(testClassFilename);
				String testFullyQualifiedName = "test." + testClassName;
				Class<?> testClass = Class.forName(testFullyQualifiedName, true, urlClassLoader);
				testClasses.add(testClass);
			}
			
			// Create kill matrix CSV file
			m.createKillMatrixCSV(testClasses);
			
			// Open view
			String viewId = "org.rayzor.mutantview.views.MutantView";
			MutantView view = (MutantView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
			view.setMajorObject(m);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method returns the fully qualified name of the file to mutate. 
	 * 
	 * @param fileLocation the location of the file to mutate
	 * @param projectLocation the location of the java file's project
	 * @return the fully qualified name of the file to mutate
	 */
	private static String getFullyQualifiedName(String fileLocation, String projectLocation) {
		char FILE_SEPARATOR = File.separatorChar;
		int projectPathLength = projectLocation.length();
		int filePathLength = fileLocation.length();
		// The (projectPathLength + 5) gets rid of project's path + "/src/" from the pathname and 
		// the (filePathLength - 5) gets rid of the ".java" file extension
		return fileLocation.substring(projectPathLength + 5, filePathLength - 5).replace(FILE_SEPARATOR, '.');
	}
}
