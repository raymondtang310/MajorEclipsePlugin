package analyzer;

import java.util.List;

import mutator.Mutator;

/**
 * A MutantAnalyzer is associated with a Mutator containing information
 * on mutants, and tests. A MutantAnalyzer performs mutation testing
 * by running tests against mutants.
 * 
 * @author Raymond Tang
 *
 */
public interface MutantAnalyzer {

	/**
	 * Returns the Mutator associated with this MutantAnalyzer.
	 * 
	 * @return the Mutator associated with this MutantAnalyzer
	 */
	Mutator getMutator();

	/**
	 * Returns the tests associated with this MutantAnalyzer.
	 * 
	 * @return the tests associated with this MutantAnalyzer
	 */
	List<TestMethod> getTests();

	/**
	 * Returns a KillMap containing the results of the mutation testing.
	 * 
	 * @return a KillMap containing the results of the mutation testing
	 */
	KillMap getKillMap();

	/**
	 * Generates a CSV file, providing details on whether or not the
	 * provided tests killed the mutants. The CSV file is named killMatrix.csv
	 * and is stored in the project directory of the java file that was mutated.
	 * 
	 * @return true for success, false otherwise
	 */
	boolean exportKillMatrixCSV();

	/**
	 * Returns true if the given mutant is killed by some test. Returns false otherwise.
	 * 
	 * @param mutant a mutant
	 * @return true if the mutant is killed, false otherwise
	 */
	boolean isMutantKilled(Mutant mutant);

	/**
	 * Returns true if the given mutant is covered by some test. Returns false otherwise.
	 * 
	 * @param mutant a mutant
	 * @return true if the mutant is covered, false otherwise
	 */
	boolean isMutantCovered(Mutant mutant);

}