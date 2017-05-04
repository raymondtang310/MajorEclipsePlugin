package ui.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import analyzer.Mutant;
import analyzer.MutantAnalyzer;

/**
 * Assigns images to items (mutants) listed in the view.
 * A green plus sign is displayed next to a mutant ID if it is killed.
 * Blue circles are displayed next to a mutant ID if it is covered but alive.
 * A red X is displayed next to a mutant ID if it is uncovered.
 * 
 * @author Raymond Tang
 *
 */
public class ImageLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	// MutantAnalyzer which contains information about mutants and tests for some java file
	private MutantAnalyzer analyzer;
		
	/**
	 * Sets the MutantAnalyzer for this LabelProvider to use. 
	 * 
	 * @param analyzer a MutantAnalyzer
	 */
	public void setMutantAnalyzer(MutantAnalyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public String getColumnText(Object obj, int index) {
		return getText(obj);
	}
	
	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}
	
	public Image getImage(Object obj) {
		if(analyzer == null) return PlatformUI.getWorkbench().getSharedImages().
								getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
		Mutant mutant = (Mutant)obj;
		// Display green plus sign next to mutant ID if it is killed
		if(analyzer.isMutantKilled(mutant)) return PlatformUI.getWorkbench().getSharedImages().
													getImage(ISharedImages.IMG_OBJ_ADD);
		// Display blue circles next to mutant ID if it is covered but alive
		if(analyzer.isMutantCovered(mutant)) return PlatformUI.getWorkbench().getSharedImages().
													getImage(ISharedImages.IMG_OBJ_ELEMENT);
		// Display red X next to mutant ID if it is uncovered
		return PlatformUI.getWorkbench().getSharedImages().
				getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
	}
}
