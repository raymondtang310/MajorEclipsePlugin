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
		String pathName = "/home/raymond/workspace/org.rayzor.mutant/src/triangle/Triangle.java";
		File file = new File(pathName);
		Class<Triangle> triangleClass = Triangle.class;
		// Fully qualified name of Triangle.java
		String fullyQualifiedName = triangleClass.getName();
		try {
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			// Highlight mutant 2
			m.highlightMutant(2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
