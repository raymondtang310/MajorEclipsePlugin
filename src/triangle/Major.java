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
	
	public Major(File program) throws IOException {		
		String type = Files.probeContentType(program.toPath());
		if(type != "text/x-java") throw new IllegalArgumentException("File type is not java");
		this.program = program;
		exportMutants = false;
		exportDirectory = new File("mutants");
		mutantsLogDirectory = new File("mutants.log");
	}
	
	public boolean mutate() {
		return false;
	}
	
	public boolean isExportMutants() {
		return exportMutants;
	}
	
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
	}
	
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	public void setExportDirectory(File directory) {
		exportDirectory = directory;
	}
	
	public File getMutantsLog() {
		return null;
	}
	
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	public void setMutantsLogDirectory(File directory) {
		mutantsLogDirectory = directory;
	}
	
}
