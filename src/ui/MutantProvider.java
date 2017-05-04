package ui;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import analyzer.Mutant;
import mutator.Mutator;

/**
 * Provides the mutant IDs to be listed in the view.
 * 
 * @author Raymond Tang
 *
 */
public class MutantProvider implements IStructuredContentProvider {
	
	// Mutator which contains information about mutants for some java file
	private Mutator mutator;
	
	/**
	 * Takes in a mutator which contains information
	 * about mutants for some java file.
	 * 
	 * @param mutator a mutator
	 */
	public void setMutator(Mutator mutator) {
		this.mutator = mutator;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	/**
	 * This method provides an array of all mutants to the view.
	 */
	public Object[] getElements(Object parent) {
		if(mutator == null) return new Object[0];
		List<Mutant> mutants = mutator.getMutants();
		return mutants.toArray();
	}
}
