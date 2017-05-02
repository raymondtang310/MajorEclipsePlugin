package org.rayzor.mutant.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.rayzor.mutantview.MutantView;

import eclipseFacade.EclipseNavigator;
import killmap.ExtendedTestFinder;
import major.mutation.Config;
import mutator.JavaFileNotSelectedException;
import mutator.Major;
import mutator.SelectionNotAdaptableException;

/**
 * Most of this class, including the comments, were auto generated by Eclipse. 
 * The interesting part of this class is the run method. When the button to run this plugin 
 * is clicked, the run method is invoked. See below for details. 
 * <p><p>
 * This program generates and compiles mutants in the selected java file and runs tests against the mutants.
 * This program produces the mutants.log file, a mutants folder containing mutated source files,
 * and a CSV file, killMatrix.csv, all in the java project's directory.
 * killMatrix.csv contains information about which mutants are killed by which test.
 * Also, this program opens up this plugin's view (MutantView.java). 
 * 
 * @author Raymond Tang
 * <p><p>
 * Auto generated comment by Eclipse:
 * <p><p>
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class MutantAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public MutantAction() {
	}

	/**
	 * This method is invoked when this plugin's button is clicked.
	 * This method finds the selected java file's file path, project path, test path, and bin path,
	 * and passes these as string parameters to the Mutator class, where the real work of mutation
	 * and testing is done.
	 * <p><p>
	 * Auto generated comment by Eclipse:
	 * <p><p>
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		try {
			// Get selected java file (the highlighted java file in the package explorer)
			ICompilationUnit fileToMutate = EclipseNavigator.getSelectedJavaFile();
			String fileToMutateLocation = EclipseNavigator.getAdaptableSelectionLocation(fileToMutate);
			String projectLocation = EclipseNavigator.getAdaptableSelectionLocation(fileToMutate.getJavaProject());
			String binLocation = EclipseNavigator.getBinLocation(fileToMutate.getJavaProject());
			String testLocation = EclipseNavigator.getTestLocation(fileToMutate.getJavaProject());
			// Generate and compile mutants in the selected java file
			Major m = mutate(fileToMutateLocation, projectLocation, binLocation);
			// Create a classloader which puts the bin and test directories on the classpath
			ClassLoader classLoader = configureClassLoader(binLocation, testLocation);
			// Create killMatrix.csv
			exportKillMatrixCsv(testLocation, classLoader, m);
			// Open view
			openView(m);
		} catch (JavaFileNotSelectedException | SelectionNotAdaptableException | JavaModelException | IOException | ClassNotFoundException | PartInitException e) {
			MessageDialog.openInformation(
				window.getShell(),
				"org.rayzor.mutant",
				"Error: a java file is not selected");
		}
	}
	
	private Major mutate(String fileToMutateLocation, String projectLocation, String binLocation) throws IOException {
		File file = new File(fileToMutateLocation);
		// Fully qualified name of the file
		String fullyQualifiedName = getFullyQualifiedName(fileToMutateLocation, projectLocation);
		// Mutate the java file
		Major m = new Major(file, fullyQualifiedName, projectLocation, binLocation);
		m.setExportMutants(true);
		m.mutate();
		return m;
	}
	
	private ClassLoader configureClassLoader(String binLocation, String testLocation) throws MalformedURLException {
		// Add bin and test directories of the java project to the classpath
		URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(binLocation).toURI().toURL(), 
				new File(testLocation).toURI().toURL()}, 
				Config.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(urlClassLoader);
		return urlClassLoader;
	}

	private void exportKillMatrixCsv(String testLocation, ClassLoader urlClassLoader, Major m) throws ClassNotFoundException {
		// Get test classes
		Collection<Class<?>> testClasses = getTestClasses(testLocation, urlClassLoader);
		// Create kill matrix CSV file
		m.createKillMatrixCSV(testClasses);
	}
	
	private void openView(Major m) throws PartInitException {
		// Open view
		String viewId = MutantView.ID;
		MutantView view = (MutantView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
		view.setMajorObject(m);
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
	
	/**
	 * Returns all test classes found in the java file's project. 
	 * 
	 * @param testLocation the directory in which test classes are located
	 * @param urlClassLoader a classloader used to help retrieve a java class's Class object
	 * @return all test classes found in the java file's project
	 * @throws ClassNotFoundException
	 */
	private static Collection<Class<?>> getTestClasses(String testLocation, ClassLoader urlClassLoader) throws ClassNotFoundException {
		String[] testClassFilenames = ExtendedTestFinder.getTestClassesFromDirectory(testLocation);
		Collection<Class<?>> testClasses = new LinkedList<Class<?>>();
		for(String testClassFilename : testClassFilenames) {
			String testClassName = FilenameUtils.getBaseName(testClassFilename);
			String testFullyQualifiedName = "test." + testClassName;
			Class<?> testClass = Class.forName(testFullyQualifiedName, true, urlClassLoader);
			testClasses.add(testClass);
		}
		return testClasses;
	}

	/**
	 * Auto generated comment by Eclipse:
	 * <p><p>
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * Auto generated comment by Eclipse:
	 * <p><p>
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * Auto generated comment by Eclipse:
	 * <p><p>
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}