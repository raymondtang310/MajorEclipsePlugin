package org.rayzor.mutantview;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import mutator.Mutator;

/**
 * Provides the mutant IDs to be listed in the view.
 * 
 * @author Raymond Tang
 *
 */
public class MutantIDProvider implements IStructuredContentProvider {
	
	// Major object which contains information about mutants and tests for some java file
	private Mutator m;
	
	/**
	 * Takes in a Major object which contains information
	 * about mutants and tests for some java file.
	 * 
	 * @param m a Major object
	 */
	public void setMajorObect(Mutator m) {
		this.m = m;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	/**
	 * This method provides an array of all mutant IDs to the view.
	 */
	public Object[] getElements(Object parent) {
		if(m == null) return new Object[0];
		int numMutants = m.getNumberOfMutants();
		Integer[] mutantIDs = new Integer[numMutants];
		for(int i = 1; i <= mutantIDs.length; i++) {
			mutantIDs[i-1] = i;
		}
		return mutantIDs;
	}
}
