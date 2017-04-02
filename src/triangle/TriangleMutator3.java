package triangle;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

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
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			
			String binPathname = "/home/raymond/workspace/Triangle/bin/";
			//URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(binPathname).toURI().toURL()});
			
			ClassLoader currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
			URLClassLoader urlClassLoader
			 = new URLClassLoader(new URL[]{new File(binPathname).toURI().toURL()},
					 Config.class.getClassLoader());
			
			Thread.currentThread().setContextClassLoader(urlClassLoader);
			
			String fileName = "/home/raymond/Desktop/TriangleMutator3.txt";
			PrintWriter writer = new PrintWriter(fileName);
			
			Class<?> testClass = Class.forName(testFullyQualifiedName, true, urlClassLoader);
			
			writer.println("Class.forName worked");
			boolean success = m.createKillMatrixCSV(testClass);
			System.out.println(success);
			if(success) writer.println("Created killMatrix.csv");
			else writer.println("Could not create killMatrix.csv");
			
			/*
			 * Uncomment the lines below to print out the kill map and kill matrix given the tests 
			 * from TriangleTest.java. 
			 */
			
			//m.printKillMap(testClass);
			//m.printKillMatrix(testClass);
			
			// Highlight mutant 2
			//m.highlightMutant(2);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
