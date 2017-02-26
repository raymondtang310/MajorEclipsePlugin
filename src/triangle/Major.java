package triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.tools.JavaCompiler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.sun.tools.javac.api.JavacTool;

/**
 * Given a java program, a Major object provides functionality such as compiling
 * and generating mutants. 
 * 
 * @author Raymond Tang
 *
 */

public class Major {
	
	// The given java program
	private File program;
	// The value of the exportMutants property (either true or false)
	private boolean exportMutants;
	// The directory to which mutant source files are exported
	private File exportDirectory;
	// The directory to which the mutants.log file is exported
	private File mutantsLogDirectory;
	// The directory to which the mutated .class files is exported
	private File mutatedBinDirectory;
	
	/**
	 * By default, mutant source files are not generated. If the option to generate mutant source
	 * files is set to true, then the default export directory is "./mutants".
	 * 
	 * A FileNotFoundException is thrown if the program does not exist.
	 * An IllegalArgumentException is thrown if the program is not a java file.
	 * 
	 * @param program a java File
	 * @throws IOException
	 */
	public Major(File program) throws IOException {
		if(!program.exists()) throw new FileNotFoundException("File " + program.toString() + 
															  " does not exist");
		String type = Files.probeContentType(program.toPath());
		if(!type.equals("text/x-java")) throw new IllegalArgumentException(program.toString() + 
															         " is not a java file");
		this.program = program;
		String currentProjectPathname = this.getCurrentProjectLocation();
		mutantsLogDirectory = new File(currentProjectPathname);
		mutatedBinDirectory = new File(currentProjectPathname + "/mutatedBin");
		exportMutants = false;
		exportDirectory = new File(currentProjectPathname + "/mutants");
		System.setProperty("major.export.mutants", "false");
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		//if(!exportDirectory.getAbsolutePath().equals("/home/raymond/workspace/org.rayzor.mutant/mutants")) {
		//	throw new NullPointerException("You know what time it is");
		//}
		//System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		//this.setExportDirectory(new File("/home/raymond/workspace/org.rayzor.mutant/mutants"));
		//this.setExportDirectory(new File(currentProjectPathname + "/mutants"));
	}
	
	/**
	 * Compile and generate mutants.
	 * Returns true if successful. Returns false otherwise.
	 * 
	 * @return true for success and false otherwise
	 */
	public boolean mutate() {
		// Create directory in which compiled mutated files will be stored
		String binPathname = mutatedBinDirectory.getAbsolutePath();
		mutatedBinDirectory.mkdir();
		// Flag for mutation
		String mutateFlag = "-XMutator:ALL";
		String[] arguments = {"-d", binPathname, mutateFlag, program.getPath()};
		// Create JavaCompiler object
		// Assuming that Major's javac is in the project directory, major's compiler will be used
		JavaCompiler compiler = JavacTool.create();
		//if(isExportMutants()) exportDirectory.mkdir();
		String mutantsPathname = mutatedBinDirectory.getParent() + "/mutants";
		//String mutantsPathname = "/home/raymond/workspace/org.rayzor.mutant/mutants";
		//this.setExportMutants(true);
		//System.setProperty("major.export.directory", mutantsPathname);
		//System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
		// Compile and run the program
		int flag = compiler.run(null, null, null, arguments);
		// The run method returns 0 for success and nonzero for errors
		if(flag == 0) return true;
		return false;
	}
	
	/**
	 * Returns true if the option to generate mutant source files is set to true.
	 * Returns false otherwise.
	 * 
	 * @return the value of the exportMutants property
	 */
	public boolean isExportMutants() {
		return exportMutants;
	}
	
	/**
	 * Sets the exportMutants property to either true or false.
	 * 
	 * @param exportMutants the value to which the exportMutants property will be set
	 */
	public void setExportMutants(boolean exportMutants) {
		this.exportMutants = exportMutants;
		System.setProperty("major.export.mutants", String.valueOf(this.exportMutants));
	}
	
	/**
	 * Returns the export directory.
	 * 
	 * @return the export directory
	 */
	public File getExportDirectory() {
		return exportDirectory;
	}
	
	/**
	 * Sets the exportDirectory property to the given directory.
	 * 
	 * @param directory the directory to which mutant source files will be exported
	 */
	public void setExportDirectory(File directory) {
		exportDirectory = directory;
		System.setProperty("major.export.directory", exportDirectory.getAbsolutePath());
	}
	
	/**
	 * Returns the mutants.log file.
	 * Throws a FileNotFoundException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file
	 * @throws FileNotFoundException
	 */
	public File getMutantsLogFile() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		return mutantsLog;
	}
	
	/**
	 * Parses mutants.log into an ArrayList of strings. The i-th string in the ArrayList
	 * is the i-th line in mutants.log.
	 * Returns the ArrayList.
	 * Throws a NullPointerException if mutants.log does not exist.
	 * 
	 * @return the mutants.log file parsed as an ArrayList<String>
	 * @throws FileNotFoundException
	 */
	public ArrayList<String> getMutantsLog() throws FileNotFoundException {
		String mutantsLogPathname = mutantsLogDirectory.getAbsolutePath() + "/mutants.log";
		File mutantsLog = new File(mutantsLogPathname);
		if(!mutantsLog.exists()) throw new FileNotFoundException("mutants.log does not exist");
		Scanner scanner = new Scanner(mutantsLog);
		ArrayList<String> log = new ArrayList<String>();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			log.add(line);
		}
		scanner.close();
		return log;
	}
	
	/**
	 * Returns the export directory of mutants.log.
	 * 
	 * @return the directory to which the mutants.log file is exported
	 */
	public File getMutantsLogDirectory() {
		return mutantsLogDirectory;
	}
	
	/**
	 * Sets the mutants.log directory to the given directory.
	 * 
	 * @param directory the directory to which the mutants.log file will be exported
	 */
	public void setMutantsLogDirectory(File directory) {
		mutantsLogDirectory = directory;
	}
	
	/**
	 * If a project is selected, this method returns the project's location as a string. 
	 * If a project is not selected, the current working directory is returned as a string instead
	 * 
	 * @return the location of the selected project as a string, if a project is selected, or the
	 * 		   current working directory as a string otherwise
	 */
	private String getCurrentProjectLocation() {
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
	
}
