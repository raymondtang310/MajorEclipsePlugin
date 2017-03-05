package killmap;

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
