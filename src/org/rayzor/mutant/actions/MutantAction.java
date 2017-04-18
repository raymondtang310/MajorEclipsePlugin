package org.rayzor.mutant.actions;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import triangle.JavaFileNotSelectedException;
import triangle.TriangleMutator3;
import util.EclipseNavigator;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class MutantAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	/**
	 * The constructor.
	 */
	public MutantAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		ICompilationUnit fileToMutate = null;
		try {
			// Get selected java file (the highlighted java file in the package explorer)
			fileToMutate = EclipseNavigator.getSelectedJavaFile();
		} catch (JavaFileNotSelectedException e) {
			MessageDialog.openInformation(
				window.getShell(),
				"org.rayzor.mutant",
				"Error: a java file is not selected");
			return;
		}
		String fileLocation = fileToMutate.getResource().getLocation().toOSString();
		String projectLocation = fileToMutate.getJavaProject().getResource().getLocation().toOSString();
		// Run the Mutator program to generate and compile mutants in Triangle.java
		TriangleMutator3.main(new String[]{fileLocation, projectLocation});
	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}