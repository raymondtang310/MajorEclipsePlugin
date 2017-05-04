package ui.comparators;

import org.eclipse.jface.viewers.Viewer;

import analyzer.Mutant;
import analyzer.MutantAnalyzer;

/**
 * This comparator is used to sort mutants listed in the view. 
 * This comparator causes uncovered mutants to show up first, then mutants 
 * that are covered but alive, and killed mutants last. 
 * 
 * @author Raymond Tang
 *
 */
public class UncoveredMutantComparator extends MutantIDComparator {

	// MutantAnalyzer which contains information about mutants and tests for some java file
	private MutantAnalyzer analyzer;
		
	/**
	 * Sets the MutantAnalyzer for this comparator to use. 
	 * 
	 * @param analyzer a MutantAnalyzer
	 */
	public void setKillMatrix(MutantAnalyzer analyzer) {
		if(analyzer == null) throw new IllegalArgumentException("Mutator cannot be null");
		this.analyzer = analyzer;
	}
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(!(e1 instanceof Mutant && e2 instanceof Mutant) || analyzer == null) return super.compare(viewer, e1, e2);
		Mutant mutant1 = (Mutant)e1;
		Mutant mutant2 = (Mutant)e2;
		boolean e1Covered = analyzer.isMutantCovered(mutant1);
		boolean e2Covered = analyzer.isMutantCovered(mutant2);
		boolean e1Killed = analyzer.isMutantKilled(mutant1);
		boolean e2Killed = analyzer.isMutantKilled(mutant2);
		// If mutant e1 is covered and alive and mutant e2 is killed, e1 shows up earlier in the view
		if(e1Covered && !e1Killed && e2Killed) return -1;
		// If mutant e1 is covered and alive and mutant e2 is uncovered, e2 shows up earlier in the view
		if(e1Covered && !e1Killed && !e2Covered) return 1;
		// If mutant e1 is uncovered and mutant e2 is killed, e1 shows up earlier in the view
		if(!e1Covered && e2Killed) return -1;
		// If mutant e2 is covered and alive and mutant e1 is killed, e2 shows up earlier in the view
		if(e2Covered && !e2Killed && e1Killed) return 1;
		// If mutant e2 is covered and alive and mutant e1 is uncovered, e1 shows up earlier in the view
		if(e2Covered && !e2Killed && !e1Covered) return -1;
		// If mutant e2 is uncovered and mutant e1 is killed, e2 shows up earlier in the view
		if(!e2Covered && e1Killed) return 1;
		return mutant1.getID() - mutant2.getID();
	}
}
