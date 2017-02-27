package triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.tools.JavaCompiler;

import com.sun.tools.javac.api.JavacTool;

/**
 * Given a java file, a Major object provides functionality such as compiling
 * and generating mutants. 
 * 
 * @author Raymond Tang
 *
 */

public class Major {
	
	// The given java file
	private File javaFile;
	// The fully qualified name of the java file
	private String fullyQualifiedName;
	// The value of the exportMutants property (either true or false)
	private boolean exportMutants;
	// The directory to which mutant source files are exported
	private File exportDirectory;
	// The directory to which the mutants.log file is exported
	private File mutantsLogDirectory;
	// The directory to which the mutated .class files is exported
	private File mutatedBinDirectory;
	
	/**
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants".
	 * 
	 * A FileNotFoundException is thrown if the file does not exist.
	 * An IllegalArgumentException is thrown if the file is not a java file.
	 * 
	 * @param javaFile a java File
	 * @throws IOException
	 */
	public Major(File javaFile) throws IOException {
		if(!javaFile.exists()) throw new FileNotFoundException("File " + javaFile.toString() + 
															  " does not exist");
		String type = Files.probeContentType(javaFile.toPath());
		if(!type.equals("text/x-java")) throw new IllegalArgumentException(javaFile.toString() + 
															         " is not a java file");
		this.javaFile = javaFile;
		String currentProjectPathname = EclipseNavigator.getCurrentProjectLocation();
		mutantsLogDirectory = new File(currentProjectPathname);
		mutatedBinDirectory = new File(currentProjectPathname + "/mutatedBin");
		exportMutants = false;
		exportDirectory = new File(currentProjectPathname + "/mutants");
		System.setProperty("major.export.mutants", "false");
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		//if(!exportDirectory.getAbsolutePath().equals("/home/raymond/workspace/org.rayzor.mutant/mutants")) {
		//	throw new NullPointerException("You know what time it is");
		//}
		//System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		//this.setExportDirectory(new File("/home/raymond/workspace/org.rayzor.mutant/mutants"));
		//this.setExportDirectory(new File(currentProjectPathname + "/mutants"));
	}
	
	/**
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants".
	 * 
	 * A FileNotFoundException is thrown if the file does not exist.
	 * An IllegalArgumentException is thrown if the file is not a java file.
	 * 
	 * @param javaFile a java File
	 * @param fullyQualifiedName the fully qualified name of the java file
	 * @throws IOException
	 */
	public Major(File javaFile, String fullyQualifiedName) throws IOException {
		this(javaFile);
		this.fullyQualifiedName = fullyQualifiedName;
	}
	
	/**
	 * Compile and generate mutants.
	 * Returns true if successful. Returns false otherwise.
	 * 
	 * @return true for success and false otherwise
	 */
	public boolean mutate() {
		// Create directory in which compiled mutated files will be stored
		String binPathname = mutatedBinDirectory.getAbsolutePath();
		mutatedBinDirectory.mkdir();
		// Flag for mutation
		String mutateFlag = "-XMutator:ALL";
		String[] arguments = {"-d", binPathname, mutateFlag, javaFile.getPath()};
		// Create JavaCompiler object
		// Assuming that Major's javac is in the project directory, major's compiler will be used
		JavaCompiler compiler = JavacTool.create();
		//if(isExportMutants()) exportDirectory.mkdir();
		//String mutantsPathname = mutatedBinDirectory.getParent() + "/mutants";
		//String mutantsPathname = "/home/raymond/workspace/org.rayzor.mutant/mutants";
		//this.setExportMutants(true);
		//System.setProperty("major.export.directory", mutantsPathname);
		//System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		// Compile and run the program
		int flag = compiler.run(null, null, null, arguments);
		// The run method returns 0 for success and nonzero for errors
		if(flag == 0) return true;
		return false;
	}
	
	/**
	 * Returns true if the option to generate mutant source files is set to true.
	 * Returns false otherwise.
	 * 
	 * @return the value of the exportMutants property
	 */
	public boolean isExportMutants() {
		return exportMutants;
	}
	
	/**
	 * Sets the exportMutants property to either true or false.
	 * 
	 * @param exportMutants the value to which the exportMutants property will be set
	 */
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
		System.setProperty("major.export.mutants", String.valueOf(this.exportMutants));
	}
	
	/**
	 * Returns the export directory.
	 * 
	 * @return the export directory
	 */
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	/**
	 * Sets the exportDirectory property to the given directory.
	 * 
	 * @param directory the directory to which mutant source files will be exported
	 */
	public void setExportDirectory(File directory) {
		exportDirectory = directory;
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
	}
	
	/**
	 * Returns the mutants.log file.
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file
	 * @throws FileNotFoundException
	 */
	public File getMutantsLogFile() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		return mutantsLog;
	}
	
	/**
	 * Parses mutants.log into an ArrayList of strings. The i-th string in the ArrayList
	 * is the i-th line in mutants.log.
	 * Returns the ArrayList.
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file parsed as an ArrayList<String>
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> getMutantsLog() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		Scanner scanner = new Scanner(mutantsLog);
		ArrayList<String> log = new ArrayList<String>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			log.add(line);
		}
		scanner.close();
		return log;
	}
	
	/**
	 * Returns the export directory of mutants.log.
	 * 
	 * @return the directory to which the mutants.log file is exported
	 */
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	/**
	 * Sets the mutants.log directory to the given directory.
	 * 
	 * @param directory the directory to which the mutants.log file will be exported
	 */
	public void setMutantsLogDirectory(File directory) {
		mutantsLogDirectory = directory;
	}
	
	/**
	 * Highlights the line in the mutated source file in which the mutant corresponding 
	 * with the given number is located. The given number (mutantNumber) is the number of the line
	 * in mutants.log detailing the desired mutant
	 * 
	 * @param mutantNumber the number of the mutant to highlight
	 * @return true for success, false otherwise
	 */
	public boolean highlightMutant(int mutantNumber) {
		if(!exportDirectory.exists() || exportDirectory.list().length <= 0) return false;
		ArrayList<String> log = null;
		try {
			log = this.getMutantsLog();
		} catch (FileNotFoundException e) {
			return false;
		}
		String logLine = log.get(mutantNumber - 1);
		String path = this.fullyQualifiedName.replace('.', '/');
		String mutatedFileLocation = exportDirectory.getAbsolutePath() + "/" +
									 String.valueOf(mutantNumber) + "/" + path + ".java";
		File mutatedFile = new File(mutatedFileLocation);
		int mutantLineNumber = this.getMutantLineNumber(logLine);
		EclipseNavigator.highlightLine(mutatedFile, mutantLineNumber);
		return true;
	}
	
	/**
	 * Returns the number of the source file line on which the mutant corresponding
	 * with the given mutants.log line occurs
	 * 
	 * @param logLine a line in mutants.log
	 * @return the number of the source file line on which the mutant corresponding
	 * 		   with the given mutants.log line occurs
	 */
	private int getMutantLineNumber(String logLine) {
		String reverseLine = new StringBuilder(logLine).reverse().toString();
		StringTokenizer tokenizer = new StringTokenizer(reverseLine);
		tokenizer.nextToken(":");
		String reversedLineNoStr = tokenizer.nextToken(":");
		String lineNoStr = new StringBuilder(reversedLineNoStr).reverse().toString();
		return Integer.parseInt(lineNoStr);
	}
}
