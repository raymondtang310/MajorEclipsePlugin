package triangle;

import java.io.File;
import java.io.IOException;

import test.TriangleTest;

/**
 * This program generates and compiles mutants in Triangle.java using Major.java. 
 * 
 * @author Raymond Tang
 *
 */
public class TriangleMutator3 {
	
	public static void main(String[] args) {
		// Pathname of Triangle.java
		String pathName = "/home/raymond/workspace/org.rayzor.mutant/src/triangle/Triangle.java";
		File file = new File(pathName);
		Class<Triangle> triangleClass = Triangle.class;
		Class<TriangleTest> testClass = TriangleTest.class;
		// Fully qualified name of Triangle.java
		String fullyQualifiedName = triangleClass.getName();
		try {
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			int[][] killMatrix = m.getKillMatrix(testClass);
			for(int i = 0; i < killMatrix.length; i++) {
				for(int j = 0; j < killMatrix[i].length; j++) {
					System.out.print(killMatrix[i][j] + " ");
				}
				System.out.println();
			}
			// Highlight mutant 2
			m.highlightMutant(2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
