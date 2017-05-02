package mutator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface Mutator {

	/**
	 * Compile and generate mutants.
	 * Returns true if successful. Returns false otherwise.
	 * 
	 * @return true for success and false otherwise
	 */
	boolean mutate();
	
	/**
	 * Returns the java file that is being mutated by this mutator.
	 * 
	 * @return the java file that is being mutated by this mutator
	 */
	File getJavaFile();

	/**
	 * Returns the fully qualified name of the java file being mutated by this mutator.
	 * 
	 * @return the fully qualified name of the java file being mutated by this mutator
	 */
	String getFullyQualifiedName();

	
	/**
	 * Returns true if the option to generate mutant source files is set to true.
	 * Returns false otherwise.
	 * 
	 * @return the value of the exportMutants property
	 */
	boolean isExportMutants();

	/**
	 * Sets the exportMutants property to either true or false.
	 * 
	 * @param exportMutants the value to which the exportMutants property will be set
	 */
	void setExportMutants(boolean exportMutants);

	/**
	 * Returns the export directory.
	 * 
	 * @return the export directory
	 */
	File getExportDirectory();

	/**
	 * Sets the exportDirectory property to the given directory.
	 * 
	 * @param directory the directory to which mutant source files will be exported
	 */
	void setExportDirectory(File directory);

	/**
	 * Returns the mutants.log file.
	 * 
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file
	 * @throws FileNotFoundException
	 */
	File getMutantsLogFile() throws FileNotFoundException;

	/**
	 * Parses mutants.log into an ArrayList of strings. The i-th string in the ArrayList
	 * is the i-th line in mutants.log. Returns the ArrayList.
	 * 
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file parsed as an ArrayList<String>
	 * @throws FileNotFoundException
	 */
	ArrayList<String> getMutantsLog() throws FileNotFoundException;

	/**
	 * Returns the export directory of mutants.log.
	 * 
	 * @return the directory to which the mutants.log file is exported
	 */
	File getMutantsLogDirectory();

	/**
	 * Sets the mutants.log directory to the given directory.
	 * 
	 * @param directory the directory to which the mutants.log file will be exported
	 */
	void setMutantsLogDirectory(File directory);

	/**
	 * Returns the timeout factor in seconds for test runtime. 
	 * 
	 * @return the timeout factor in seconds for test runtime
	 */
	int getTimeoutFactor();

	/**
	 * Sets the timeout factor for test runtime. 
	 * 
	 * @param timeoutFactor the amount of time in seconds to which the timeout factor will be set
	 */
	void setTimeoutFactor(int timeoutFactor);

	/**
	 * Returns the directory in which the compiled mutated .class files get stored. 
	 * 
	 * @return the bin directory
	 */
	File getBinDirectory();

	/**
	 * Sets the bin directory to the given directory. 
	 * Compiled mutated .class files will be stored here. 
	 * 
	 * @param binDirectory the directory to be used as the bin directory
	 */
	void setBinDirectory(File binDirectory);

	/**
	 * Returns the number of generated mutants. 
	 * 
	 * @return the number of generated mutants
	 */
	int getNumberOfMutants();

}