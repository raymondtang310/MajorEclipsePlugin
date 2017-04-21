package mutator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.tools.JavaCompiler;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import com.sun.tools.javac.api.JavacTool;

import killmap.Outcome;
import killmap.TestFinder;
import killmap.TestMethod;
import killmap.WorkOrder;
import major.mutation.Config;
import util.EclipseNavigator;

/**
 * Given a java file, a Major object provides functionality such as compiling
 * and generating mutants. Also, a Major object can be used to perform mutation 
 * testing provided test classes. 
 * 
 * @author Raymond Tang
 *
 */

public class Major {
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
	// The most recently computed kill matrix
	private int[][] killMatrix;
	// Set of covered mutants
	private Set<Integer> coveredMutants;
	
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
	public Major(File javaFile, String fullyQualifiedName, String projectLocation, String binLocation) throws IOException {
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
		killMatrix = null;
		coveredMutants = new TreeSet<Integer>();
	}
	
	/**
	 * Compile and generate mutants.
	 * Returns true if successful. Returns false otherwise.
	 * 
	 * @return true for success and false otherwise
	 */
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
	 * An IllegalArgumentException is thrown if the given directory is null.
	 * 
	 * @param directory the directory to which mutant source files will be exported
	 */
	public void setExportDirectory(File directory) {
		if(directory == null) throw new IllegalArgumentException("directory cannot be null");
		exportDirectory = directory;
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
	}
	
	/**
	 * Returns the mutants.log file.
	 * 
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file
	 * @throws FileNotFoundException
	 */
	public File getMutantsLogFile() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + FILE_SEPARATOR + 
									"mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		return mutantsLog;
	}
	
	/**
	 * Parses mutants.log into an ArrayList of strings. The i-th string in the ArrayList
	 * is the i-th line in mutants.log. Returns the ArrayList.
	 * 
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file parsed as an ArrayList<String>
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> getMutantsLog() throws FileNotFoundException {
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
	 * An IllegalArgumentException is thrown if the given directory is null.
	 * 
	 * @param directory the directory to which the mutants.log file will be exported
	 */
	public void setMutantsLogDirectory(File directory) {
		if(directory == null) throw new IllegalArgumentException("directory cannot be null");
		mutantsLogDirectory = directory;
	}
	
	/**
	 * Highlights the line in the source file in which the mutant corresponding 
	 * with the given number occurs. The given number (mutantNumber) is the number of the line
	 * in mutants.log detailing the desired mutant
	 * 
	 * @param mutantNumber the number of the mutant to highlight
	 * @return true for success, false otherwise
	 */
	public boolean highlightMutantInSource(int mutantNumber) {
		if(!exportDirectory.exists() || exportDirectory.list().length <= 0) return false;
		try {
			ArrayList<String> log = this.getMutantsLog();
			if(mutantNumber <= 0 || mutantNumber > log.size()) return false;
			String logLine = log.get(mutantNumber - 1);
			int mutantLineNumber = this.getMutantLineNumber(logLine);
			return EclipseNavigator.highlightLine(javaFile, mutantLineNumber);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Highlights the line in the mutated source file in which the mutant corresponding 
	 * with the given number is located. The given number (mutantNumber) is the number of the line
	 * in mutants.log detailing the desired mutant
	 * 
	 * @param mutantNumber the number of the mutant to highlight
	 * @return true for success, false otherwise
	 */
	public boolean highlightMutantInMutatedSource(int mutantNumber) {
		if(!exportDirectory.exists() || exportDirectory.list().length <= 0) return false;
		try {
			ArrayList<String> log = this.getMutantsLog();
			if(mutantNumber <= 0 || mutantNumber > log.size()) return false;
			String logLine = log.get(mutantNumber - 1);
			String fullyQualifiedPath = this.fullyQualifiedName.replace('.', FILE_SEPARATOR);
			// Here we assume a particular file system structure for finding mutated source files
			// E.g., for mutant 5, its file path should be exportDirectory/5/packageName/sourceFileName
			String mutatedFileLocation = exportDirectory.getAbsolutePath() + FILE_SEPARATOR +
										 String.valueOf(mutantNumber) + 
										 FILE_SEPARATOR + fullyQualifiedPath + ".java";
			File mutatedFile = new File(mutatedFileLocation);
			int mutantLineNumber = this.getMutantLineNumber(logLine);
			return EclipseNavigator.highlightLine(mutatedFile, mutantLineNumber);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Returns the number of the source file line on which the mutant corresponding
	 * with the given mutants.log line occurs. 
	 * 
	 * An IllegalArgumentException is thrown if the given log line is null.
	 * 
	 * @param logLine a line in mutants.log
	 * @return the number of the source file line on which the mutant corresponding
	 * 		   with the given mutants.log line occurs
	 */
	private int getMutantLineNumber(String logLine) {
		// For any given line in mutants.log, the number of the source file line
		// on which the mutant occurs is the number just before the last colon
		if(logLine == null) throw new IllegalArgumentException("logLine cannot be null");
		String reverseLine = new StringBuilder(logLine).reverse().toString();
		StringTokenizer tokenizer = new StringTokenizer(reverseLine);
		tokenizer.nextToken(":");
		String reversedLineNoStr = tokenizer.nextToken(":");
		String lineNoStr = new StringBuilder(reversedLineNoStr).reverse().toString();
		return Integer.parseInt(lineNoStr);
	}
	
	/**
	 * Returns the timeout factor in seconds for test runtime. 
	 * 
	 * @return the timeout factor in seconds for test runtime
	 */
	public int getTimeoutFactor() {
		return timeoutFactor;
	}

	/**
	 * Sets the timeout factor for test runtime. 
	 * 
	 * @param timeoutFactor the amount of time in seconds to which the timeout factor will be set
	 */
	public void setTimeoutFactor(int timeoutFactor) {
		this.timeoutFactor = timeoutFactor;
		System.setProperty("timeoutFactor", String.valueOf(this.timeoutFactor));
	}

	/**
	 * Returns the directory in which the compiled mutated .class files get stored. 
	 * 
	 * @return the bin directory
	 */
	public File getBinDirectory() {
		return binDirectory;
	}

	/**
	 * Sets the bin directory to the given directory. 
	 * Compiled mutated .class files will be stored here. 
	 * 
	 * An IllegalArgumentException is thrown if the given directory is null.
	 * 
	 * @param binDirectory the directory to be used as the bin directory
	 */
	public void setBinDirectory(File binDirectory) {
		if(binDirectory == null) throw new IllegalArgumentException("bin directory cannot be null");
		this.binDirectory = binDirectory;
	}
	
	/**
	 * Returns the number of generated mutants. 
	 * 
	 * @return the number of generated mutants
	 */
	public int getNumberOfMutants() {
		return this.numMutants;
	}
	
	/**
	 * Generates and returns a kill map. 
	 * The mapping is between a WorkOrder (a mutant and a test) and an Outcome 
	 * (KILLED if the test killed the mutant, or ALIVE if the test did not kill the mutant). 
	 * 
	 * An IllegalArgumentException is thrown if the given collection of test classes is null.
	 * 
	 * @param testClasses classes containing tests to run against the given java program
	 * @return a map between WorkOrders and Outcomes, detailing the Outcome (KILLED or ALIVE) 
	 * 		   of a WorkOrder (a mutant and a test). Returns an empty map object if there are 
	 * 		   no mutants generated or if there are no test methods in the given test class. 
	 */
	public Map<WorkOrder, Outcome> getKillMap(Collection<Class<?>> testClasses) {
		if(testClasses == null) throw new IllegalArgumentException("test classes cannot be null");
		this.coveredMutants.clear();
		Collection<TestMethod> testMethods = TestFinder.getTestMethods(testClasses);
		Map<WorkOrder, Outcome> killMap = new TreeMap<WorkOrder, Outcome>();
		for(TestMethod test : testMethods) {
			Config.__M_NO = 0;
			JUnitCore core = new JUnitCore();
			core.run(Request.method(test.getTestClass(), test.getName()));
			List<Integer> coveredMutants = Config.getCoverageList();
			this.coveredMutants.addAll(coveredMutants);
			Config.reset();
			for(int mutantID = 1; mutantID <= this.numMutants; mutantID++) {
				WorkOrder workOrder = new WorkOrder(mutantID, test);
				Outcome outcome;
				if(coveredMutants.contains(mutantID)) {
					Config.__M_NO = mutantID;
					Result result = core.run(Request.method(test.getTestClass(), test.getName()));
					if(result.wasSuccessful()) outcome = Outcome.ALIVE;
					else outcome = Outcome.KILLED;
				}
				else outcome = Outcome.ALIVE;
				killMap.put(workOrder, outcome);
			}
		}
		return killMap;
	}
	
	/**
	 * Generates and prints out a kill map. 
	 * The mapping is between a WorkOrder (a mutant and a test) and an Outcome 
	 * (KILLED if the test killed the mutant, or ALIVE if the test did not kill the mutant). 
	 * 
	 * An IllegalArgumentException is thrown if the given collection of test classes is null.
	 * 
	 * @param testClasses classes containing tests to run against the given java program
	 */
	public void printKillMap(Collection<Class<?>> testClasses) {
		if(testClasses == null) throw new IllegalArgumentException("test classes cannot be null");
		Map<WorkOrder, Outcome> killMap = this.getKillMap(testClasses);
		if(killMap.isEmpty()) return;
		for(Map.Entry<WorkOrder, Outcome> entry : killMap.entrySet()) {
			WorkOrder workOrder = entry.getKey();
			int mutantID = workOrder.getMutantID();
			String testName = workOrder.getTestMethod().getName();
			Outcome outcome = entry.getValue();
			System.out.println("Mutant# " + mutantID + ", Test: " + testName + ", Outcome: " + outcome);
		}
	}
	
	/**
	 * Generates and returns a kill matrix, represented as an array of integer arrays.
	 * The rows of the matrix represent mutants, and the columns represent tests.
	 * An entry matrix[i][j] equals 1 if test j killed mutant i, or 0 if test j
	 * did not kill mutant i. The rows (mutants) are sorted (ascending) by mutantIDs.
	 * The columns (tests) are sorted by name.
	 * 
	 * An IllegalArgumentException is thrown if the given collection of test classes is null.
	 * 
	 * @param testClasses classes containing tests to run against the given java program
	 * @return a kill matrix, represented as a two dimensional integer array, 
	 * 		   where the rows represent mutants and the columns represent tests. 
	 * 		   Returns an empty int[][] if there are no mutants generated or if there are no test 
	 *		   methods in the given test class. 
	 */
	public int[][] getKillMatrix(Collection<Class<?>> testClasses) {
		this.coveredMutants.clear();
		if(testClasses == null) throw new IllegalArgumentException("test classes cannot be null");
		Collection<TestMethod> testMethodsCollection = TestFinder.getTestMethods(testClasses);
		ArrayList<TestMethod> testMethods = new ArrayList<TestMethod>(testMethodsCollection);
		int numTests = testMethods.size();
		if(numMutants == 0 || numTests == 0) return new int[0][0];
		int[][] killMatrix = new int[numMutants][numTests];
		for(int i = 0; i < numTests; i++) {
			TestMethod test = testMethods.get(i);
			Config.__M_NO = 0;
			JUnitCore core = new JUnitCore();
			core.run(Request.method(test.getTestClass(), test.getName()));
			List<Integer> coveredMutants = Config.getCoverageList();
			this.coveredMutants.addAll(coveredMutants);
			Config.reset();
			for(Integer coveredMutant : coveredMutants) {
				int mutantNumber = coveredMutant.intValue();
				Config.__M_NO = mutantNumber;
				Result result = core.run(Request.method(test.getTestClass(), test.getName()));
				if(!result.wasSuccessful()) killMatrix[mutantNumber - 1][i] = 1;
			}
		}
		this.killMatrix = killMatrix;
		return killMatrix;
	}
	
	/**
	 * Generates a CSV file, providing details on whether or not the
	 * provided tests killed the mutants. The CSV file is named killMatrix.csv
	 * and is stored in the current project directory.
	 * 
	 * An IllegalArgumentException is thrown if the given collection of test classes is null.
	 * 
	 * @param testClasses classes containing tests to run against the given java program
	 * @return true for success, false otherwise
	 */
	public boolean createKillMatrixCSV(Collection<Class<?>> testClasses) {
		if(testClasses == null) throw new IllegalArgumentException("test classes cannot be null");
		int[][] killMatrix = this.getKillMatrix(testClasses);
		if(killMatrix.length == 0) return false;
		String fileName = projectLocation + FILE_SEPARATOR + "killMatrix.csv";
		char csvSeparator = ',';
		Collection<TestMethod> tests = TestFinder.getTestMethods(testClasses);
		int numTests = tests.size();
		try {
			PrintWriter writer = new PrintWriter(fileName);
			writer.print("Mutant#");
			for(TestMethod test : tests) writer.print(csvSeparator + test.getName());
			writer.println();
			for(int i = 0; i < numMutants; i++) {
				int mutantID = i + 1;
				writer.print(mutantID);
				for(int j = 0; j < numTests; j++) writer.print("" + csvSeparator + killMatrix[i][j]);
				writer.println();
			}
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Generates and prints out a kill matrix, represented as an array of integer arrays. 
	 * The rows of the matrix represent mutants, and the columns represent tests. 
	 * An entry matrix[i][j] equals 1 if test j killed mutant i, or 0 if test j
	 * did not kill mutant i. The rows (mutants) are sorted (ascending) by mutantIDs.
	 * The columns (tests) are sorted by name. 
	 * 
	 * An IllegalArgumentException is thrown if the given collection of test classes is null.
	 * 
	 * @param testClasses classes containing tests to run against the given java program
	 */
	public void printKillMatrix(Collection<Class<?>> testClasses) {
		if(testClasses == null) throw new IllegalArgumentException("test classes cannot be null");
		int[][] killMatrix = this.getKillMatrix(testClasses);
		for(int i = 0; i < killMatrix.length; i++) {
			for(int j = 0; j < killMatrix[i].length; j++) {
				System.out.print(killMatrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Returns true if the given mutant is killed by some test. Returns false otherwise. 
	 * 
	 * @param mutantNumber the number of the mutant
	 * @return true if the mutant is killed, false otherwise
	 */
	public boolean isMutantKilled(int mutantNumber) {
		if(killMatrix == null || mutantNumber <= 0 || mutantNumber > killMatrix.length) return false;
		int[] tests = killMatrix[mutantNumber - 1];
		for(int j = 0; j < tests.length; j++) {
			if(tests[j] == 1) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the given mutant is covered by some test. Returns false otherwise. 
	 * 
	 * @param mutantNumber the number of the mutant
	 * @return true if the mutant is covered, false otherwise
	 */
	public boolean isMutantCovered(int mutantNumber) {
		return coveredMutants.contains(mutantNumber);
	}
	
}
