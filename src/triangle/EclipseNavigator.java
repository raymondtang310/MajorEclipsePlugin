package triangle;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class EclipseNavigator {
	
	public EclipseNavigator() {
	}
	
	/**
	 * If a project is selected, this method returns the project's location as a string. 
	 * If a project is not selected, the current working directory is returned as a string instead
	 * 
	 * @return the location of the selected project as a string, if a project is selected, or the
	 * 		   current working directory as a string otherwise
	 */
	public static String getCurrentProjectLocation() {
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof IAdaptable) {
					IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
					IPath path = project.getLocation();
					return path.toString();
				}
			}
			return System.getProperty("user.dir");
		} catch (Exception e) {
			return System.getProperty("user.dir");
		}
	}
	
	public static void highlightLine(File file, int lineNumber) {
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ITextEditor editor = null;
		try {
			editor = (ITextEditor) IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e1) {
			return;
		}
		IDocument document = editor.getDocumentProvider().getDocument(
		editor.getEditorInput());
		if (document != null) {
			IRegion lineInfo = null;
			try {
				lineInfo = document.getLineInformation(lineNumber - 1);
		    } catch (Exception e) {
		    	return;
		    }
		    if (lineInfo != null) {
		    	editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
		    }
		}
	}
}
