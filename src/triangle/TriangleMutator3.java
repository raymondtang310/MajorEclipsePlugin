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
	
	public static void main(String[] args) throws IOException {
		// Pathname of Triangle program
		String pathStr = "/home/raymond/workspace/org.rayzor.mutant/src/triangle/Triangle.java";
		File file = new File(pathStr);
		try {
			Major m = new Major(file);
			m.setExportMutants(true);
			m.mutate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
