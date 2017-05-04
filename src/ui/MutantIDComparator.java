package ui;

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
		int e1Int = ((Mutant)e1).getID();
		int e2Int = ((Mutant)e2).getID();
		return e1Int - e2Int;
	}
}
