package analyzer;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	// This KillMap represented as a Map<WorkOrder, Outcome>
	private Map<WorkOrder, Outcome> map;

	public KillMap() {
		mutants = new TreeSet<Mutant>();
		tests = new TreeSet<TestMethod>();
		map = new TreeMap<WorkOrder, Outcome>();
	}

	/**
	 * Returns the set of mutants contained in this KillMap.
	 * 
	 * @return the set of mutants contained in this KillMap
	 */
	public Set<Mutant> getMutants() {
		return mutants;
	}

	/**
	 * Returns the set of tests contained in this KillMap.
	 * 
	 * @return the set of tests contained in this KillMap
	 */
	public Set<TestMethod> getTests() {
		return tests;
	}

	/**
	 * Associates the given mutant and test with the given outcome.
	 * Returns the previous outcome associated with the (mutant, test) pair,
	 * or null if there was no mapping for the (mutant, test) pair.
	 * 
	 * @param mutant a mutant
	 * @param test a test method
	 * @param outcome an outcome
	 * @return the previous outcome associated with the (mutant, test) pair,
	 * 		   or null if there was no mapping for the (mutant, test) pair.
	 */
	public Outcome put(Mutant mutant, TestMethod test, Outcome outcome) {
		mutants.add(mutant);
		tests.add(test);
		WorkOrder workOrder = new WorkOrder(mutant, test);
		return map.put(workOrder, outcome);
	}

	/**
	 * Returns the Outcome to which the (mutant, test) pair is mapped to,
	 * or null if this KillMap does not contain the (mutant, test) pair.
	 * 
	 * @param mutant a mutant
	 * @param test a test method
	 * @return the Outcome to which the (mutant, test) pair is mapped to,
	 * 		   or null if this KillMap does not contain the (mutant, test) pair
	 */
	public Outcome get(Mutant mutant, TestMethod test) {
		WorkOrder workOrder = new WorkOrder(mutant, test);
		return map.get(workOrder);
	}
	
	/**
	 * Returns the number of (mutant, test) pairings in this KillMap.
	 * 
	 * @return the number of (mutant, test) pairings in this KillMap
	 */
	public int size() {
		return map.size();
	}

}
