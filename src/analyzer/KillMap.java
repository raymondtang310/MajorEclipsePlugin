package analyzer;

import java.util.Set;

/**
 * A KillMap stores information about which tests killed which mutants.
 * 
 * @author Raymond Tang
 *
 */
public class KillMap {
	// Mutants
	private Set<Mutant> mutants;
	// Tests
	private Set<TestMethod> tests;
	
	
}
