package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import major.mutation.Config;
import mutator.Mutator;
import util.TestFinder;

/**
 * A KillMatrix stores information about which tests killed which mutants.
 * 
 * @author Raymond Tang
 *
 */
public class KillMatrix {
	// File separator. Differs depending on operating system
	private static final char FILE_SEPARATOR = File.separatorChar;
	// A mutator containing information about mutants
	private Mutator mutator;
	// Set of covered mutants
	private Set<Integer> coveredMutants;
	// Kill matrix represented as a 2D integer array
	private int[][] killMatrix;
	// Test classes containing tests to run against mutants
	private Collection<Class<?>> testClasses;

	public KillMatrix(Mutator mutator, Collection<Class<?>> testClasses) {
		this.mutator = mutator;
		this.testClasses = testClasses;
		this.coveredMutants = new TreeSet<Integer>();
		this.killMatrix = computeKillMatrix();
	}

	/**
	 * Generates and returns a kill matrix represented as a 2D integer array.
	 * The rows of the matrix represent mutants and the columns represent tests.
	 * An entry matrix[i][j] equals 1 if test j killed mutant i, or 0 if test j
	 * did not kill mutant i. The rows (mutants) are sorted (ascending) by mutantIDs.
	 * The columns (tests) are sorted by name.
	 * 
	 * @return a kill matrix represented as a two dimensional integer array,
	 * 		   where the rows represent mutants and the columns represent tests.
	 * 		   Returns an empty int[][] if there are no mutants generated or if there are no test
	 *		   methods in the given test class.
	 */
	private int[][] computeKillMatrix() {
		Collection<TestMethod> testMethodsCollection = TestFinder.getTestMethods(testClasses);
		ArrayList<TestMethod> testMethods = new ArrayList<TestMethod>(testMethodsCollection);
		int numTests = testMethods.size();
		int numMutants = mutator.getNumberOfMutants();
		if(numMutants == 0 || numTests == 0) return new int[0][0];
		int[][] killMatrix = new int[numMutants][numTests];
		for(int i = 0; i < numTests; i++) {
			TestMethod test = testMethods.get(i);
			Config.__M_NO = 0;
			JUnitCore core = new JUnitCore();
			Result original = core.run(Request.method(test.getTestClass(), test.getName()));
			boolean originalResult = original.wasSuccessful();
			List<Integer> coveredMutants = Config.getCoverageList();
			this.coveredMutants.addAll(coveredMutants);
			Config.reset();
			for(Integer coveredMutant : coveredMutants) {
				int mutantID = coveredMutant.intValue();
				Config.__M_NO = mutantID;
				Result resultWithMutant = core.run(Request.method(test.getTestClass(), test.getName()));
				boolean newResult = resultWithMutant.wasSuccessful();
				if(newResult != originalResult) killMatrix[mutantID - 1][i] = 1;
			}
		}
		return killMatrix;
	}

	/**
	 * Generates a CSV file, providing details on whether or not the
	 * provided tests killed the mutants. The CSV file is named killMatrix.csv
	 * and is stored in the project directory of the java file that was mutated.
	 * 
	 * @return true for success, false otherwise
	 */
	public boolean exportCSV() {
		if(killMatrix.length == 0) return false;
		String fileName = mutator.getProjectLocationOfJavaFile() + FILE_SEPARATOR + "killMatrix.csv";
		char csvSeparator = ',';
		Collection<TestMethod> tests = TestFinder.getTestMethods(testClasses);
		int numTests = tests.size();
		int numMutants = mutator.getNumberOfMutants();
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
	 * Returns true if the given mutant is killed by some test. Returns false otherwise.
	 * 
	 * @param mutantID the ID of the mutant
	 * @return true if the mutant is killed, false otherwise
	 */
	public boolean isMutantKilled(int mutantID) {
		if(killMatrix == null || mutantID < 1 || mutantID > killMatrix.length) return false;
		int[] tests = killMatrix[mutantID - 1];
		for(int j = 0; j < tests.length; j++) {
			if(tests[j] == 1) return true;
		}
		return false;
	}
	
	/**
	 * Returns true if the given mutant is covered by some test. Returns false otherwise.
	 * 
	 * @param mutantID the ID of the mutant
	 * @return true if the mutant is covered, false otherwise
	 */
	public boolean isMutantCovered(int mutantID) {
		return coveredMutants.contains(mutantID);
	}

}
