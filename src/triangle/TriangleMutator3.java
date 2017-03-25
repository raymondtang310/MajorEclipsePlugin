package triangle;

import java.io.File;
import java.io.IOException;

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
		//Class<Triangle> triangleClass = Triangle.class;
		//Class<TriangleTest> testClass = TriangleTest.class;
		
		// Fully qualified name of Triangle.java
		String fullyQualifiedName = "triangle.Triangle";
		String testFullyQualifiedName = "test.TriangleTest";
		try {
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			Class<?> testClass = Class.forName(testFullyQualifiedName);
			m.createKillMatrixCSV(testClass);
			
			/*
			 * Uncomment the lines below to print out the kill map and kill matrix given the tests 
			 * from TriangleTest.java. 
			 */
			
			//m.printKillMap(testClass);
			//m.printKillMatrix(testClass);
			
			// Highlight mutant 2
			//m.highlightMutant(2);
		} catch (IOException | ClassNotFoundException e) {
			//e.printStackTrace();
		}
	}
}
