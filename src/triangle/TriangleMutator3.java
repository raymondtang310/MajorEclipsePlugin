package triangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.rayzor.mutantview.views.MutantView;

import major.mutation.Config;

/**
 * This program generates and compiles mutants in Triangle.java using Major.java. 
 * 
 * @author Raymond Tang
 *
 */
public class TriangleMutator3 {
	
	public static void main(String[] args) {
		// Pathname of Triangle.java
		String pathName = "/home/raymond/workspace/Triangle/src/triangle/Triangle.java";
		File file = new File(pathName);
		
		// Fully qualified name of Triangle.java
		String fullyQualifiedName = "triangle.Triangle";
		String testFullyQualifiedName = "test.TriangleTest";
		try {
			// Mutate Triangle program
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			
			// Add bin directory of Triangle project to classpath
			String binPathname = "/home/raymond/workspace/Triangle/bin/";
			URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(binPathname).toURI().toURL()}, Config.class.getClassLoader());
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			// Create kill matrix CSV file
			Class<?> testClass = Class.forName(testFullyQualifiedName, true, urlClassLoader);
			boolean success = m.createKillMatrixCSV(testClass);
			System.out.println(success);
			
			// Open view
			String viewId = "org.rayzor.mutantview.views.MutantView";
			try {
				MutantView view = (MutantView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewId);
				view.setMajorObject(m);
			} catch (PartInitException e) {
				e.printStackTrace();
			}

			/*
			 * Uncomment the lines below to print out the kill map and kill matrix given the tests 
			 * from TriangleTest.java. 
			 */
			
			//m.printKillMap(testClass);
			//m.printKillMatrix(testClass);
			
			// Highlight mutant 1 location in original source file
			//m.highlightMutantInSource(1);
			
			// Highlight mutant 2 in mutated source file
			//m.highlightMutantInMutatedSource(2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
