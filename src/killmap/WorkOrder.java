package killmap;

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
	
	private int mutantID;
	private TestMethod testMethod;
	
	public WorkOrder(int mutantID, TestMethod testMethod) {
		this.mutantID = mutantID;
		this.testMethod = testMethod;
	}

	public int getMutantID() {
		return mutantID;
	}

	public TestMethod getTestMethod() {
		return testMethod;
	}

	@Override
	public int compareTo(WorkOrder workOrder) {
		if(this.mutantID < workOrder.getMutantID()) return -1;
		if(this.mutantID > workOrder.getMutantID()) return 1;
		return this.testMethod.compareTo(workOrder.getTestMethod());
	}
	
}
