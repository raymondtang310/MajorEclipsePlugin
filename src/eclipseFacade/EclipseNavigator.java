package eclipseFacade;

import java.io.File;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
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

/**
 * This class provides functionality such as retrieving selection locations and highlighting lines
 * in a file. 
 * 
 * @author Raymond Tang
 *
 */
public class EclipseNavigator {
	
	// File separator. Differs depending on operating system
	private static final char FILE_SEPARATOR = File.separatorChar;
	
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
		} catch (Exception e) {
			return "";
		}
		return "";
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
		try {
			IAdaptable adaptableElement = getAdaptableSelection();
			if(adaptableElement instanceof ICompilationUnit) return (ICompilationUnit) adaptableElement;
		} catch (SelectionNotAdaptableException e) {
			throw new JavaFileNotSelectedException("Selection is not a java file");
		}
	    throw new JavaFileNotSelectedException("Selection is not a java file");
	}
	
	/**
	 * Returns the location of the given java project's bin directory as a pathname string.
	 * 
	 * @param project the java project
	 * @return the location of the java project's bin directory
	 * @throws JavaModelException
	 */
	public static String getBinLocation(IJavaProject project) throws JavaModelException {
		IPath path = project.getOutputLocation();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder binDirectory = root.getFolder(path);
		return binDirectory.getLocation().toOSString();
	}
	
	/**
	 * Returns the location of the given java project's test directory as a pathname string.
	 * This method assumes that there is a directory named "test" located in a directory named "src"
	 * located in the project. We might want to change this in the future.
	 * 
	 * @param project the java project
	 * @return the location of the given java project's test directory
	 * @throws SelectionNotAdaptableException
	 */
	public static String getTestLocation(IJavaProject project) throws SelectionNotAdaptableException {
		String projectLocation = getAdaptableSelectionLocation(project);
		return projectLocation + FILE_SEPARATOR + "src" + FILE_SEPARATOR + "test";
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
		if(file == null || lineNumber < 0) return false;
		if(!file.exists()) return false;
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			ITextEditor editor = (ITextEditor) IDE.openEditorOnFileStore(page, fileStore);
			IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
			if (document != null) {
				IRegion lineInfo = null;
				lineInfo = document.getLineInformation(lineNumber - 1);
			    if (lineInfo != null) editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
			    else return false;
			}
		} catch (PartInitException | BadLocationException e) {
			return false;
		}
		return true;
	}
}
