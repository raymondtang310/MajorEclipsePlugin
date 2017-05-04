package ui.comparators;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import analyzer.Mutant;

/**
 * This comparator is used to sort mutants listed in the view
 * by their ID in ascending order. 
 * 
 * @author Raymond Tang
 *
 */
public class MutantIDComparator extends ViewerComparator {
	
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if(!(e1 instanceof Mutant && e2 instanceof Mutant)) return super.compare(viewer, e1, e2);
		Mutant mutant1 = (Mutant)e1;
		Mutant mutant2 = (Mutant)e2;
		return mutant1.getID() - mutant2.getID();
	}
}
