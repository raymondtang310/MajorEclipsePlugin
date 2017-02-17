package triangle;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * This program generates and compiles mutants in Triangle.java using JavaCompiler. 
 * 
 * @author Raymond Tang
 *
 */
public class Mutator {

	public static void main(String[] args) {		
		// Get the project directory
		String root = "/home/raymond/workspace/org.rayzor.mutant";
		// Path to the Triangle program
		String path = root + "/src/triangle/Triangle.java";
		// Create File object
		File file = new File(path);
		
		// Directory in which the mutated .class files will be stored
		String binPath = root + "/mutatedBin";
		// Create directory in which compiled mutated files will be stored
		File binDirectory = new File(binPath);
		binDirectory.mkdir();
			
		// Generate mutated source files and store them into a directory named mutants
		String mutantsPath = root + "/mutants";
		System.setProperty("major.export.mutants", "true");
		System.setProperty("major.export.directory", mutantsPath);
		String mutateFlag = "-XMutator:ALL";
		String[] arguments = {"-d", binPath, mutateFlag, file.getPath()};
		
		// Create JavaCompiler object
		// Assuming that Major's javac is in the project directory, major's compiler will be used
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// Compile and run the program
		compiler.run(null, null, null, arguments);
	}

}
