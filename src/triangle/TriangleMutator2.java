package triangle;

import java.io.File;

/**
 * This program generates and compiles mutants in Triangle.java using Runtime.getRuntime().exec. 
 * 
 * @author Raymond Tang
 *
 */
public class TriangleMutator2 {

	public static void main(String[] args) {
		// The commented line below this does NOT work as I would like it to
		// String root = System.getProperty("user.dir");
		// Project directory
		String root = "/home/raymond/workspace/org.rayzor.mutant";
		// Name of directory in which compiled mutated files will be stored
		String majorOutputPath = root + "/mutatedBin";
		// Path to major's compiler
		String command = "/home/raymond/major/bin/major ";
		String arg0 = "-d " + majorOutputPath + " ";
		// Generate mutated source files and store them into a directory named mutants
		String arg1 = "-J-Dmajor.export.mutants=true ";
		String arg2 = "-J-Dmajor.export.directory=" + root + "/mutants ";
		String arg3 = "-XMutator:ALL ";
		// Path to Triangle.java
		String path1 = root + "/src/triangle/Triangle.java";
		// Create directory in which compiled mutated files will be stored
		File majorDirectory = new File(majorOutputPath);
		majorDirectory.mkdir();
		try {
			// Generate and compile mutants
			Process p1 = Runtime.getRuntime().exec(command + arg0 + arg1 + arg2 + arg3 + path1);
			p1.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
