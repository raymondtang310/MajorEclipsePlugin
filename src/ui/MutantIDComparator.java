package ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

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
		if(!(e1 instanceof Integer && e2 instanceof Integer)) return super.compare(viewer, e1, e2);
		int e1Int = ((Integer)e1).intValue();
		int e2Int = ((Integer)e2).intValue();
		return e1Int - e2Int;
	}
}
