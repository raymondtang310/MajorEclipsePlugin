package util;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

import triangle.JavaFileNotSelectedException;
import triangle.SelectionNotAdaptableException;

/**
 * This class provides functionality such as retrieving project locations and highlighting lines
 * in a file. 
 * 
 * @author Raymond Tang
 *
 */
public class EclipseNavigator {
	
	public EclipseNavigator() {
	}
	
	/**
	 * If a project is selected, this method returns the project's location as a string. 
	 * Otherwise, the empty string is returned instead. 
	 * 
	 * @return the location of the selected project as a string, if a project is selected, or the
	 * 		   empty string otherwise
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
			return "";
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * If an object of type IAdaptable is selected, this method returns the selection. Otherwise, a
	 * SelectionNotAdaptableException is thrown. 
	 * 
	 * @return the IAdaptable selection
	 * @throws SelectionNotAdaptableException 
	 */
	public static IAdaptable getAdaptableSelection() throws SelectionNotAdaptableException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    if (page != null) {
	    	ISelection selection = page.getSelection();
	    	if(selection != null && selection instanceof IStructuredSelection) {
		    	IStructuredSelection structuredSelection = (IStructuredSelection) selection;
	        	Object firstElement = structuredSelection.getFirstElement();
	        	if(firstElement instanceof IAdaptable) {
	        		return (IAdaptable)firstElement;
	        	}
	    	}
	    }
	    // Fix this later
	    throw new SelectionNotAdaptableException("Selection is not IAdaptable");
	}
	
	/**
	 * This method returns the location of the given IAdaptable element as a pathname string. 
	 * Otherwise, a SelectionNotAdaptableException is thrown. 
	 * 
	 * @param adaptableElement the IAdaptable element
	 * @return the location of the given IAdaptable element as a pathname string
	 * @throws SelectionNotAdaptableException 
	 */
	public static String getAdaptableSelectionLocation(IAdaptable adaptableElement) throws SelectionNotAdaptableException {
		if(adaptableElement == null) throw new NullPointerException();
		if(adaptableElement instanceof IResource) return ((IResource)adaptableElement).getLocation().toOSString();
		else if(adaptableElement instanceof IProject) return ((IProject)adaptableElement).getLocation().toOSString();
		else if(adaptableElement instanceof IJavaProject) return ((IJavaProject)adaptableElement).getResource().getLocation().toOSString();
		else if(adaptableElement instanceof IJavaElement) return ((IJavaElement)adaptableElement).getResource().getLocation().toOSString();
	    throw new SelectionNotAdaptableException("Selection is not IAdaptable");
	}
	
	/**
	 * If an object of type IAdaptable is selected, this method returns the location of the selection. 
	 * Otherwise, a SelectionNotAdaptableException is thrown. 
	 * 
	 * @return the IAdaptable selection
	 * @throws SelectionNotAdaptableException 
	 */
	public static String getAdaptableSelectionLocation() throws SelectionNotAdaptableException {
		IAdaptable adaptableElement = getAdaptableSelection();
		if(adaptableElement instanceof IResource) return ((IResource)adaptableElement).getLocation().toOSString();
		else if(adaptableElement instanceof IProject) return ((IProject)adaptableElement).getLocation().toOSString();
		else if(adaptableElement instanceof IJavaProject) return ((IJavaProject)adaptableElement).getResource().getLocation().toOSString();
		else if(adaptableElement instanceof IJavaElement) return ((IJavaElement)adaptableElement).getResource().getLocation().toOSString();
	    throw new SelectionNotAdaptableException("Selection is not IAdaptable");
	}
	
	/**
	 * If a java file is selected, then it is returned as an object of type ICompilationUnit. 
	 * Otherwise, a JavaFileNotSelectedException is thrown. 
	 * 
	 * @return the selected java file
	 * @throws JavaFileNotSelectedException
	 */
	public static ICompilationUnit getSelectedJavaFile() throws JavaFileNotSelectedException {
		IAdaptable adaptableElement = null;
		try {
			adaptableElement = getAdaptableSelection();
		} catch (SelectionNotAdaptableException e) {
			throw new JavaFileNotSelectedException("Selection is not a java file");
		}
		if(adaptableElement instanceof ICompilationUnit) return (ICompilationUnit) adaptableElement;
	    throw new JavaFileNotSelectedException("Selection is not a java file");
	}
	
	/**
	 * Highlights the line corresponding to the given line number in the given file. 
	 * Returns true for success. Returns false otherwise. 
	 * 
	 * @param file the file containing the line to highlight
	 * @param lineNumber the number of the line to highlight
	 * @return true for success, false otherwise
	 */
	public static boolean highlightLine(File file, int lineNumber) {
		if(!file.exists()) return false;
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		ITextEditor editor = null;
		try {
			editor = (ITextEditor) IDE.openEditorOnFileStore(page, fileStore);
		} catch (PartInitException e1) {
			return false;
		}
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if (document != null) {
			IRegion lineInfo = null;
			try {
				lineInfo = document.getLineInformation(lineNumber - 1);
		    } catch (Exception e) {
		    	return false;
		    }
		    if (lineInfo != null) {
		    	editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
		    }
		    else return false;
		}
		return true;
	}
}
