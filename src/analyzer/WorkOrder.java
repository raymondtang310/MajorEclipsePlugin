package analyzer;

/**
 * A WorkOrder is a wrapper for a mutant and a test.
 * 
 * Adapted from Rene Just's fault-localization-data
 * Link: https://bitbucket.org/rjust/fault-localization-data/src/1b5b3f155c41ef4d7c11ca422a94e5ff90b558e7/killmap/src/main/killmap/runners/communication/WorkOrder.java?at=icse17&fileviewer=file-view-default
 * Tag: icse17
 * 
 * @author Raymond Tang
 *
 */
public class WorkOrder implements Comparable<WorkOrder> {
	// The mutant
	private Mutant mutant;
	// The test method
	private TestMethod testMethod;
	
	public WorkOrder(Mutant mutant, TestMethod testMethod) {
		this.mutant = mutant;
		this.testMethod = testMethod;
	}

	/**
	 * Returns the mutant's ID
	 * 
	 * @return the mutant's ID
	 */
	public Mutant getMutant() {
		return mutant;
	}

	/**
	 * Returns the test method
	 * 
	 * @return the test method
	 */
	public TestMethod getTestMethod() {
		return testMethod;
	}

	/**
	 * Compares the given WorkOrder to this WorkOrder. Two WorkOrders are considered
	 * equal if their mutant IDs are equal and their test methods are equal. This WorkOrder
	 * is considered less than the given WorkOrder if this WorkOrder's mutant ID is less than the mutant ID
	 * of the given WorkOrder, or if their mutant IDs are the same, if this WorkOrder's test method is less than
	 * the given WorkOrder's test method. This WorkOrder is considered greater than the given WorkOrder
	 * if this WorkOrder's mutant ID is greater than the mutant ID of the given WorkOrder, or if their 
	 * mutant IDs are the same, if this WorkOrder's test method is greater than the given WorkOrder's test method.
	 * 
	 * @param the WorkOrder to compare to this WorkOrder
	 * @return the value 0 if these WorkOrders are the same;
	 * 		   a value less than 0 if this WorkOrder is less than the given WorkOrder; or
	 * 		   a value greater than 0 if this WorkOrder is greater than the given WorkOrder
	 */
	@Override
	public int compareTo(WorkOrder workOrder) {
		int thisMutantID = mutant.getID();
		int otherMutantId = workOrder.getMutant().getID();
		if(thisMutantID < otherMutantId) return -1;
		if(thisMutantID > otherMutantId) return 1;
		return this.testMethod.compareTo(workOrder.getTestMethod());
	}
	
	/**
	 * Checks if the given WorkOrder is equal to this WorkOrder. They are equal if and only if
	 * their mutants are equal and their test methods are equal. 
	 * 
	 * @return true if the given WorkOrder is equal to this WorkOrder, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof WorkOrder)) return false;
		WorkOrder otherWorkOrder = (WorkOrder)obj;
		Mutant otherMutant = otherWorkOrder.getMutant();
		TestMethod otherTestMethod = otherWorkOrder.getTestMethod();
		return (this.mutant == otherMutant && this.testMethod == otherTestMethod);
	}
	
}
