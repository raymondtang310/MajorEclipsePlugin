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
	 * 
	 * 
	 * @return
	 */
	public Set<Mutant> getMutants() {
		return mutants;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public Set<TestMethod> getTests() {
		return tests;
	}

	/**
	 * 
	 * 
	 * @param mutant
	 * @param test
	 * @param outcome
	 * @return
	 */
	public Outcome put(Mutant mutant, TestMethod test, Outcome outcome) {
		mutants.add(mutant);
		tests.add(test);
		WorkOrder workOrder = new WorkOrder(mutant, test);
		return map.put(workOrder, outcome);
	}

	/**
	 * 
	 * 
	 * @param mutant
	 * @param test
	 * @return
	 */
	public Outcome get(Mutant mutant, TestMethod test) {
		WorkOrder workOrder = new WorkOrder(mutant, test);
		return map.get(workOrder);
	}

}
