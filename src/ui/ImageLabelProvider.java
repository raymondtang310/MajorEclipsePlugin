package ui;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import analyzer.KillMatrix;

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
	
	// KillMatrix which contains information about mutants and tests for some java file
	private KillMatrix k;
		
	/**
	 * Sets the KillMatrix for this LabelProvider to use. 
	 * 
	 * @param k a KillMatrix
	 */
	public void setKillMatrix(KillMatrix k) {
		this.k = k;
	}
	
	public String getColumnText(Object obj, int index) {
		return getText(obj);
	}
	
	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}
	
	public Image getImage(Object obj) {
		if(k == null) return PlatformUI.getWorkbench().getSharedImages().
								getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
		int mutantID = ((Integer)obj).intValue();
		// Display green plus sign next to mutant ID if it is killed
		if(k.isMutantKilled(mutantID)) return PlatformUI.getWorkbench().getSharedImages().
													getImage(ISharedImages.IMG_OBJ_ADD);
		// Display blue circles next to mutant ID if it is covered but alive
		if(k.isMutantCovered(mutantID)) return PlatformUI.getWorkbench().getSharedImages().
													getImage(ISharedImages.IMG_OBJ_ELEMENT);
		// Display red X next to mutant ID if it is uncovered
		return PlatformUI.getWorkbench().getSharedImages().
				getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
	}
}
