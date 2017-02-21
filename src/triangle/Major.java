package triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Given a java program, a Major object provides functionality such as compiling
 * and generating mutants. 
 * 
 * @author Raymond Tang
 *
 */

public class Major {
	
	// The given java program
	private File program;
	// The value of the exportMutants property (either true or false)
	private boolean exportMutants;
	// The directory to which mutant source files are exported
	private File exportDirectory;
	// The directory to which the mutants.log file is exported
	private File mutantsLogDirectory;
	
	/*
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants". 
	 * An IllegalArugmentException is thrown if the program does not exist or if the program is 
	 * not a java file. 
	 */
	public Major(File program) throws IOException {
		if(!program.exists()) throw new FileNotFoundException("File " + program.toString() + 
															  " does not exist");
		String type = Files.probeContentType(program.toPath());
		if(!type.equals("text/x-java")) throw new IllegalArgumentException(program.toString() + 
															         " is not a java file");
		this.program = program;
		String mutantsLogDirStr = "";
		mutantsLogDirectory = new File(mutantsLogDirStr);
		exportMutants = false;
		String exportDirStr = "mutants";
		exportDirectory = new File(exportDirStr);
		System.setProperty("major.export.mutants", "false");
		System.setProperty("major.export.directory", exportDirStr);
	}
	
	/*
	 * Compile and generate mutants. 
	 * Returns true if successful. Returns false otherwise. 
	 */
	public boolean mutate() {
		// Create directory in which compiled mutated files will be stored
		String binPathStr = "mutatedBin";
		File binDirectory = new File(binPathStr);
		binDirectory.mkdir();
		// Flag for mutation
		String mutateFlag = "-XMutator:ALL";
		String[] arguments = {"-d", binPathStr, mutateFlag, program.getPath()};
		// Create JavaCompiler object
		// Assuming that Major's javac is in the project directory, major's compiler will be used
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		// Compile and run the program
		int flag = compiler.run(null, null, null, arguments);
		// The run method returns 0 for success and nonzero for errors
		if(flag == 0) return true;
		return false;
	}
	
	/*
	 * Returns true if the option to generate mutant source files is set to true. 
	 * Returns false otherwise. 
	 */
	public boolean isExportMutants() {
		return exportMutants;
	}
	
	/*
	 * Sets the exportMutants property to either true or false. 
	 */
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
		System.setProperty("major.export.mutants", String.valueOf(this.exportMutants));
	}
	
	/*
	 * Returns the export directory. 
	 */
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	/*
	 * Sets the export directory to the given directory. 
	 */
	public void setExportDirectory(File directory) {
		exportDirectory = directory;
		System.setProperty("major.export.directory", exportDirectory.getPath());
	}
	
	/*
	 * Returns the mutants.log file. 
	 * Throws a NullPointerException if mutants.log does not exist. 
	 */
	public File getMutantsLogFile() throws FileNotFoundException {
		String mutantsLogPathStr = mutantsLogDirectory.getPath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathStr);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		return mutantsLog;
	}
	
	/*
	 * Parses mutants.log into an ArrayList of strings. The i-th string in the ArrayList
	 * is the i-th line in mutants.log. 
	 * Returns the ArrayList. 
	 * Throws a NullPointerException if mutants.log does not exist. 
	 */
	public ArrayList<String> getMutantsLog() throws FileNotFoundException {
		String mutantsLogPathStr = mutantsLogDirectory.getPath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathStr);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		Scanner scanner = new Scanner(mutantsLog);
		ArrayList<String> log = new ArrayList<String>();
		while(scanner.hasNext()) {
			String line = scanner.next();
			log.add(line);
		}
		scanner.close();
		return log;
	}
	
	/*
	 * Returns the export directory of mutants.log. 
	 */
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	/*
	 * Sets the mutants.log directory to the given directory. 
	 */
	public void setMutantsLogDirectory(File directory) {
		mutantsLogDirectory = directory;
	}
	
}
