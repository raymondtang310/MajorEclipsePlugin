package org.rayzor.mutantview.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import mutator.Major;

/**
 * Provides the mutant IDs to be listed in the view.
 * 
 * @author Raymond Tang
 *
 */
public class MutantIDProvider implements IStructuredContentProvider {
	
	// Major object which contains information about mutants and tests for some java file
	private Major m;
	
	/**
	 * Takes in a Major object which contains information
	 * about mutants and tests for some java file.
	 * 
	 * @param m a Major object
	 */
	public void setMajorObect(Major m) {
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
		Integer[] mutantNumbers = new Integer[numMutants];
		for(int i = 1; i <= mutantNumbers.length; i++) {
			mutantNumbers[i-1] = i;
		}
		return mutantNumbers;
	}
}
