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
		Class<Triangle> c = Triangle.class;
		// Fully qualified name of Triangle.java
		String fullyQualifiedName = c.getName();
		try {
			Major m = new Major(file, fullyQualifiedName);
			m.setExportMutants(true);
			m.mutate();
			// Highlight mutant 1
			m.highlightMutant(2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
