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
 * Given a Mutator and test classes, a MajorMutantAnalyzer performs
 * mutation testing by running each test method given by the test classes
 * against each mutant given by the Mutator. This MutantAnalyzer provides
 * coverage information on mutants in addition to whether or not mutants are killed.
 * 
 * @author Raymond Tang
 *
 */
public class MajorMutantAnalyzer implements MutantAnalyzer {
	// File separator. Differs depending on operating system
	private static final char FILE_SEPARATOR = File.separatorChar;
	// A mutator containing information about mutants
	private Mutator mutator;
	// Set of covered mutants
	private Set<Integer> coveredMutants;
	// KillMap
	private KillMap killMap;
	// Tests to run against mutants
	private List<TestMethod> tests;

	public MajorMutantAnalyzer(Mutator mutator, Collection<Class<?>> testClasses) {
		this.mutator = mutator;
		this.tests = new ArrayList<TestMethod>(TestFinder.getTestMethods(testClasses));
		this.coveredMutants = new TreeSet<Integer>();
		killMap = executeMutationTests();
	}

	/**
	 * Performs mutation testing by running each test against each mutant in isolation. 
	 * Returns a KillMap containing the results of the mutation testing.
	 * 
	 * @return a KillMap containing the results of the mutation testing
	 */
	private KillMap executeMutationTests() {
		KillMap killMap = new KillMap();
		int numMutants = mutator.getNumberOfMutants();
		for(TestMethod test : tests) {
			Config.__M_NO = 0;
			JUnitCore core = new JUnitCore();
			Result original = core.run(Request.method(test.getTestClass(), test.getName()));
			boolean originalResult = original.wasSuccessful();
			List<Integer> coveredMutants = Config.getCoverageList();
			this.coveredMutants.addAll(coveredMutants);
			Config.reset();
			for(int mutantID = 1; mutantID <= numMutants; mutantID++) {
				Mutant mutant = new Mutant(mutantID);
				Outcome outcome;
				if(coveredMutants.contains(mutantID)) {
					Config.__M_NO = mutantID;
					Result resultWithMutant = core.run(Request.method(test.getTestClass(), test.getName()));
					boolean newResult = resultWithMutant.wasSuccessful();
					if(newResult == originalResult) outcome = Outcome.ALIVE;
					else outcome = Outcome.KILLED;
				}
				else outcome = Outcome.ALIVE;
				killMap.put(mutant, test, outcome);
			}
		}
		return killMap;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#getMutator()
	 */
	@Override
	public Mutator getMutator() {
		return mutator;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#getTests()
	 */
	@Override
	public List<TestMethod> getTests() {
		return tests;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#getKillMap()
	 */
	@Override
	public KillMap getKillMap() {
		return killMap;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#exportKillMatrixCSV()
	 */
	@Override
	public boolean exportKillMatrixCSV() {
		if(killMap.size() == 0) return false;
		String fileName = mutator.getProjectLocationOfJavaFile() + FILE_SEPARATOR + "killMatrix.csv";
		char csvSeparator = ',';
		int numMutants = mutator.getNumberOfMutants();
		try {
			PrintWriter writer = new PrintWriter(fileName);
			writer.print("Mutant#");
			for(TestMethod test : tests) writer.print(csvSeparator + test.getName());
			writer.println();
			for(int i = 0; i < numMutants; i++) {
				int mutantID = i + 1;
				writer.print(mutantID);
				Mutant mutant = new Mutant(mutantID);
				for(TestMethod test: tests) writer.print("" + csvSeparator + killMap.get(mutant, test));
				writer.println();
			}
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#isMutantKilled(int)
	 */
	@Override
	public boolean isMutantKilled(int mutantID) {
		if(killMap.size() == 0 || mutantID < 1 || mutantID > killMap.getMutants().size()) return false;
		Mutant mutant = new Mutant(mutantID);
		for(TestMethod test : tests) {
			if(killMap.get(mutant, test) == Outcome.KILLED) return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see analyzer.MutantAnalyzer#isMutantCovered(int)
	 */
	@Override
	public boolean isMutantCovered(int mutantID) {
		return coveredMutants.contains(mutantID);
	}

}
