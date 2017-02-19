package triangle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Given a java program, a Major object provides functionality such as compiling
 * and generating mutants. 
 * 
 * @author Raymond Tang
 *
 */

public class Major {
	
	private File program;
	private boolean exportMutants;
	private File exportDirectory;
	private File mutantsLogDirectory;
	
	/*
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants". 
	 * An IllegalArugmentException is thrown if the program does not exist or if the program is 
	 * not a java file. 
	 */
	public Major(File program) throws IOException {		
		if(!program.exists()) throw new IllegalArgumentException("Invalid file");
		String type = Files.probeContentType(program.toPath());
		if(type != "text/x-java") throw new IllegalArgumentException("File type is not java");
		this.program = program;
		exportMutants = false;
		exportDirectory = new File("mutants");
		mutantsLogDirectory = new File("mutants.log");
	}
	
	/*
	 * Compile and generate mutants. 
	 * Returns true if successful. Returns false otherwise. 
	 */
	public boolean mutate() {
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
	 * Sets the exportMutants property to either true or false
	 */
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
	}
	
	/*
	 * Returns the export directory
	 */
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	/*
	 * Sets the export directory to the given directory
	 */
	public void setExportDirectory(File directory) {
		exportDirectory = directory;
	}
	
	/*
	 * Returns the mutants.log file
	 */
	public File getMutantsLog() {
		return null;
	}
	
	/*
	 * Returns the export directory of mutants.log
	 */
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	/*
	 * Sets the mutants.log directory to the given directory
	 */
	public void setMutantsLogDirectory(File directory) {
		mutantsLogDirectory = directory;
	}
	
}
