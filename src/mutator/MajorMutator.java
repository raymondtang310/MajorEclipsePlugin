package mutator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.tools.JavaCompiler;

import com.sun.tools.javac.api.JavacTool;

import analyzer.Mutant;

/**
 * Given a java file, this mutator provides functionality such as compiling
 * and generating mutants. 
 * 
 * @author Raymond Tang
 *
 */

public class MajorMutator implements Mutator {
	// File separator. Differs depending on operating system
	private static final char FILE_SEPARATOR = File.separatorChar;	
	// The given java file
	private File javaFile;
	// The fully qualified name of the java file
	private String fullyQualifiedName;
	// The location of the java file's project
	private String projectLocation;
	// The value of the exportMutants property (either true or false)
	private boolean exportMutants;
	// The directory to which mutant source files are exported
	private File exportDirectory;
	// The directory to which the mutants.log file is exported
	private File mutantsLogDirectory;
	// The directory to which the mutated .class files is exported
	private File binDirectory;
	// Base timeout factor (seconds) for test runtime
	private int timeoutFactor;
	// Number of generated mutants
	private int numMutants;
	
	/**
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants".
	 * 
	 * An IllegalArgumentException is thrown if any of the given parameters are null or
	 * if the file is not a java file.
	 * A FileNotFoundException is thrown if the file does not exist.
	 * 
	 * @param javaFile a java File
	 * @param fullyQualifiedName the fully qualified name of the java file
	 * @param projectLocation the location of the java file's project a pathname string
	 * @param binLocation the location of the java project's bin as a pathname string
	 * @throws IOException
	 */
	public MajorMutator(File javaFile, String fullyQualifiedName, String projectLocation, String binLocation) throws IOException {
		if(javaFile == null || fullyQualifiedName == null || projectLocation == null) throw new IllegalArgumentException("parameters cannot be null");
		if(!javaFile.exists()) throw new FileNotFoundException("File " + javaFile.toString() + 
															  " does not exist");
		String type = Files.probeContentType(javaFile.toPath());
		if(!type.equals("text/x-java")) throw new IllegalArgumentException(javaFile.toString() + 
															         " is not a java file");
		this.javaFile = javaFile;
		this.fullyQualifiedName = fullyQualifiedName;
		this.projectLocation = projectLocation;
		mutantsLogDirectory = new File(this.projectLocation);
		binDirectory = new File(binLocation);
		exportMutants = false;
		exportDirectory = new File(this.projectLocation + FILE_SEPARATOR + "mutants");
		setExportMutants(false);
		setExportDirectory(exportDirectory);
		setTimeoutFactor(8);
		numMutants = 0;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#mutate()
	 */
	@Override
	public boolean mutate() {
		// The directory in which compiled mutated files will be stored
		String binLocation = binDirectory.getAbsolutePath();
		// Flag for mutation
		String mutateFlag = "-XMutator:ALL";
		String[] arguments = {"-d", binLocation, mutateFlag, javaFile.getPath()};
		// Create JavaCompiler object
		// Assuming that Major's javac is in the project directory, major's compiler will be used
		JavaCompiler compiler = JavacTool.create();
		// Compile and run the program
		compiler.run(null, null, null, arguments);
		// mutants.log is created in the working directory by default
		// Move mutants.log into the project directory
		String sourceDirectory = System.getProperty("user.dir");
		Path source = Paths.get(sourceDirectory + FILE_SEPARATOR + "mutants.log");
		Path target = Paths.get(mutantsLogDirectory.getAbsolutePath() + FILE_SEPARATOR + 
								"mutants.log");
		try {
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			return false;
		}
		// Record number of mutants generated
		numMutants = this.getNumberOfMutantsAfterCompile();
		return true;
	}
	
	/**
	 * Returns the number of generated mutants.
	 * This method is run directly after compiling in the mutate method.
	 * 
	 * @return the number of generated mutants
	 */
	private int getNumberOfMutantsAfterCompile() {
		try {
			File mutantsLog = this.getMutantsLogFile();
			Scanner sc = new Scanner(mutantsLog);
			int numMutants = 0;
			while(sc.hasNext()) {
				sc.nextLine();
				numMutants++;
			}
			sc.close();
			return numMutants;
		} catch (Exception e) {
			return 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getJavaFile()
	 */
	@Override
	public File getJavaFile() {
		return javaFile;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getJavaFile()
	 */
	@Override
	public String getFullyQualifiedNameOfJavaFile() {
		return fullyQualifiedName;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getProjectLocation()
	 */
	@Override
	public String getProjectLocationOfJavaFile() {
		return projectLocation;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#isExportMutants()
	 */
	@Override
	public boolean isExportMutants() {
		return exportMutants;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#setExportMutants(boolean)
	 */
	@Override
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
		System.setProperty("major.export.mutants", String.valueOf(this.exportMutants));
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getExportDirectory()
	 */
	@Override
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#setExportDirectory(java.io.File)
	 */
	@Override
	public void setExportDirectory(File directory) {
		if(directory == null) throw new IllegalArgumentException("directory cannot be null");
		exportDirectory = directory;
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getMutantsLogFile()
	 */
	@Override
	public File getMutantsLogFile() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + FILE_SEPARATOR + 
									"mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		return mutantsLog;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getMutantsLog()
	 */
	@Override
	public List<String> getMutantsLog() throws FileNotFoundException {
		File mutantsLog = this.getMutantsLogFile();
		Scanner scanner = new Scanner(mutantsLog);
		ArrayList<String> log = new ArrayList<String>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			log.add(line);
		}
		scanner.close();
		return log;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getMutantsLogDirectory()
	 */
	@Override
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#setMutantsLogDirectory(java.io.File)
	 */
	@Override
	public void setMutantsLogDirectory(File directory) {
		if(directory == null) throw new IllegalArgumentException("directory cannot be null");
		mutantsLogDirectory = directory;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getTimeoutFactor()
	 */
	@Override
	public int getTimeoutFactor() {
		return timeoutFactor;
	}

	/* (non-Javadoc)
	 * @see mutator.Mutator#setTimeoutFactor(int)
	 */
	@Override
	public void setTimeoutFactor(int timeoutFactor) {
		this.timeoutFactor = timeoutFactor;
		System.setProperty("timeoutFactor", String.valueOf(this.timeoutFactor));
	}

	/* (non-Javadoc)
	 * @see mutator.Mutator#getBinDirectory()
	 */
	@Override
	public File getBinDirectory() {
		return binDirectory;
	}

	/* (non-Javadoc)
	 * @see mutator.Mutator#setBinDirectory(java.io.File)
	 */
	@Override
	public void setBinDirectory(File binDirectory) {
		if(binDirectory == null) throw new IllegalArgumentException("bin directory cannot be null");
		this.binDirectory = binDirectory;
	}
	
	/* (non-Javadoc)
	 * @see mutator.Mutator#getNumberOfMutants()
	 */
	@Override
	public int getNumberOfMutants() {
		return this.numMutants;
	}

	/* (non-Javadoc)
	 * @see mutator.Mutator#getMutants()
	 */
	@Override
	public List<Mutant> getMutants() {
		List<Mutant> mutants = new ArrayList<Mutant>();
		for(int mutantID = 1; mutantID <= this.numMutants; mutantID++) {
			Mutant mutant = new Mutant(mutantID);
			mutants.add(mutant);
		}
		return mutants;
	}
	
}
