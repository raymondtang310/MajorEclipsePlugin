package analyzer;

import mutator.Mutator;

/**
 * 
 * 
 * @author Raymond Tang
 *
 */
public class MutantAnalyzer {
	// A mutator containing information about mutants
	private Mutator mutator;

	public MutantAnalyzer(Mutator mutator) {
		this.mutator = mutator;
	}
	
	/**
	 * Returns true if the given mutant is killed by some test. Returns false otherwise. 
	 * 
	 * @param mutantID the ID of the mutant
	 * @return true if the mutant is killed, false otherwise
	 */
	public boolean isMutantKilled(int mutantID) {
		/*
		if(killMatrix == null || mutantID <= 0 || mutantID > killMatrix.length) return false;
		int[] tests = killMatrix[mutantID - 1];
		for(int j = 0; j < tests.length; j++) {
			if(tests[j] == 1) return true;
		}
		*/
		return false;
	}
	
	/**
	 * Returns true if the given mutant is covered by some test. Returns false otherwise. 
	 * 
	 * @param mutantID the ID of the mutant
	 * @return true if the mutant is covered, false otherwise
	 */
	public boolean isMutantCovered(int mutantID) {
		return false;
		//return coveredMutants.contains(mutantID);
	}
}
