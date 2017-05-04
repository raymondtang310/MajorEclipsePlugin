package ui;

import org.eclipse.jface.viewers.Viewer;

import analyzer.MutantAnalyzer;

/**
 * This comparator is used to sort mutants listed in the view. 
 * This comparator causes mutants that are covered but alive to show up first, 
 * then uncovered mutants, and killed mutants last. 
 * 
 * @author Raymond Tang
 *
 */
public class AliveAndCoveredMutantComparator extends MutantIDComparator {

	// MutantAnalyzer which contains information about mutants and tests for some java file
	private MutantAnalyzer analyzer;
		
	/**
	 * Sets the MutantAnalyzer for this comparator to use. 
	 * 
	 * @param analyzer a MutantAnalyzer
	 */
	public void setKillMatrix(MutantAnalyzer analyzer) {
		if(analyzer == null) throw new IllegalArgumentException("KillMatrix cannot be null");
		this.analyzer = analyzer;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(!(e1 instanceof Integer && e2 instanceof Integer) || analyzer == null) return super.compare(viewer, e1, e2);
		int e1Int = ((Integer)e1).intValue();
		int e2Int = ((Integer)e2).intValue();
		boolean e1Covered = analyzer.isMutantCovered(e1Int);
		boolean e2Covered = analyzer.isMutantCovered(e2Int);
		boolean e1Killed = analyzer.isMutantKilled(e1Int);
		boolean e2Killed = analyzer.isMutantKilled(e2Int);
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
