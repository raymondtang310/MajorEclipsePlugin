package org.rayzor.mutantview.views;

import org.eclipse.jface.viewers.Viewer;

import mutator.Major;

/**
 * This comparator is used to sort mutants listed in the view. 
 * This comparator causes mutants that are covered but alive to show up first, 
 * then uncovered mutants, and killed mutants last. 
 * 
 * @author Raymond Tang
 *
 */
public class AliveAndCoveredMutantComparator extends MutantIDComparator {

	// Major object which contains information about mutants and tests for some java file
	private Major m;
		
	/**
	 * Sets the Major object for this comparator to use. 
	 * 
	 * @param m a Major object
	 */
	public void setMajorObject(Major m) {
		if(m == null) throw new IllegalArgumentException("Major object cannot be null");
		this.m = m;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(!(e1 instanceof Integer && e2 instanceof Integer) || m == null) return super.compare(viewer, e1, e2);
		int e1Int = ((Integer)e1).intValue();
		int e2Int = ((Integer)e2).intValue();
		boolean e1Covered = m.isMutantCovered(e1Int);
		boolean e2Covered = m.isMutantCovered(e2Int);
		boolean e1Killed = m.isMutantKilled(e1Int);
		boolean e2Killed = m.isMutantKilled(e2Int);
		// If mutant e1 is covered and alive and mutant e2 is killed, e1 shows up earlier in the view
		if(e1Covered && !e1Killed && e2Killed) return -1;
		// If mutant e1 is covered and alive and mutant e2 is uncovered, e1 shows up earlier in the view
		if(e1Covered && !e1Killed && !e2Covered) return -1;
		// If mutant e1 is uncovered and mutant e2 is killed, e1 shows up earlier in the view
		if(!e1Covered && e2Killed) return -1;
		// If mutant e2 is covered and alive and mutant e1 is killed, e2 shows up earlier in the view
		if(e2Covered && !e2Killed && e1Killed) return 1;
		// If mutant e2 is covered and alive and mutant e1 is uncovered, e2 shows up earlier in the view
		if(e2Covered && !e2Killed && !e1Covered) return 1;
		// If mutant e2 is uncovered and mutant e1 is killed, e2 shows up earlier in the view
		if(!e2Covered && e1Killed) return 1;
		return e1Int - e2Int;
	}
}
