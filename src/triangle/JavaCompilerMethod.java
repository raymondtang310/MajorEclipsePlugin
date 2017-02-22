package triangle;

import java.io.File;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * This program uses JavaCompiler from javax.tools to programmatically
 * compile and run two sample programs, Triangle and Main. 
 * 
 * @author Raymond Tang
 *
 */

public class JavaCompilerMethod {

	public static void main(String[] args) {
		// Get the current working directory
		String root = System.getProperty("user.dir");
		// Path to the Triangle program
		String pathname1 = root + "/src/triangle/Triangle.java";
		// Path to the Main program
		String pathname2 = root + "/src/triangle/Main.java";
		// Create File objects
		File file1 = new File(pathname1);
		File file2 = new File(pathname2);
		// Create JavaCompiler object
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// Assuming that Major's javac is in the project directory, 
		// major's compiler will be used and the commented line below will also work
		// JavaCompiler compiler = JavacTool.create();
		// Directory in which the .class files will be stored
		String binPathname = root + "/bin";
		String[] args1 = {"-d", binPathname, file1.getPath()};
		String[] args2 = {"-d", binPathname, file2.getPath()};
		// Compile and run the programs
		compiler.run(null, null, null, args1);
		compiler.run(null, null, null, args2);
	}

}
