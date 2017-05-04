package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.StringTokenizer;

import eclipseFacade.EclipseFacade;
import mutator.Mutator;

/**
 * Given a mutator, a MutantHighlighter provides methods to highlight
 * a mutant in its source file as well as its mutated source file.
 * 
 * @author Raymond Tang
 *
 */
public class MutantHighlighter {
	// File separator. Differs depending on operating system
	private static final char FILE_SEPARATOR = File.separatorChar;
	// A mutator containing information about mutants
	private Mutator mutator;
	
	public MutantHighlighter(Mutator mutator) {
		this.mutator = mutator;
	}
	
	/**
	 * Highlights the line in the source file in which the mutant corresponding 
	 * with the given ID occurs. The given ID (mutantID) is the number of the line
	 * in mutants.log detailing the desired mutant.
	 * 
	 * @param mutantID the ID of the mutant to highlight
	 * @return true for success, false otherwise
	 */
	public boolean highlightMutantInSource(int mutantID) {
		File exportDirectory = mutator.getExportDirectory();
		if(!exportDirectory.exists() || exportDirectory.list().length <= 0) return false;
		try {
			List<String> log = mutator.getMutantsLog();
			if(mutantID <= 0 || mutantID > log.size()) return false;
			String logLine = log.get(mutantID - 1);
			int mutantLineNumber = this.getMutantLineNumber(logLine);
			return EclipseFacade.highlightLine(mutator.getJavaFile(), mutantLineNumber);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Highlights the line in the mutated source file in which the mutant corresponding 
	 * with the given ID is located. The given ID (mutantID) is the number of the line
	 * in mutants.log detailing the desired mutant
	 * 
	 * @param mutantID the ID of the mutant to highlight
	 * @return true for success, false otherwise
	 */
	public boolean highlightMutantInMutatedSource(int mutantID) {
		File exportDirectory = mutator.getExportDirectory();
		if(!exportDirectory.exists() || exportDirectory.list().length <= 0) return false;
		try {
			List<String> log = mutator.getMutantsLog();
			if(mutantID <= 0 || mutantID > log.size()) return false;
			String logLine = log.get(mutantID - 1);
			String fullyQualifiedPath = mutator.getFullyQualifiedNameOfJavaFile().replace('.', FILE_SEPARATOR);
			// Here we assume a particular file system structure for finding mutated source files
			// E.g., for mutant 5, its file path should be exportDirectory/5/packageName/sourceFileName
			String mutatedFileLocation = exportDirectory.getAbsolutePath() + FILE_SEPARATOR +
										 String.valueOf(mutantID) + 
										 FILE_SEPARATOR + fullyQualifiedPath + ".java";
			File mutatedFile = new File(mutatedFileLocation);
			int mutantLineNumber = this.getMutantLineNumber(logLine);
			return EclipseFacade.highlightLine(mutatedFile, mutantLineNumber);
		} catch (FileNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Returns the number of the source file line on which the mutant corresponding
	 * with the given mutants.log line occurs. 
	 * 
	 * An IllegalArgumentException is thrown if the given log line is null.
	 * 
	 * @param logLine a line in mutants.log
	 * @return the number of the source file line on which the mutant corresponding
	 * 		   with the given mutants.log line occurs
	 */
	private int getMutantLineNumber(String logLine) {
		// For any given line in mutants.log, the number of the source file line
		// on which the mutant occurs is the number just before the last colon
		if(logLine == null) throw new IllegalArgumentException("logLine cannot be null");
		String reverseLine = new StringBuilder(logLine).reverse().toString();
		StringTokenizer tokenizer = new StringTokenizer(reverseLine);
		tokenizer.nextToken(":");
		String reversedLineNoStr = tokenizer.nextToken(":");
		String lineNoStr = new StringBuilder(reversedLineNoStr).reverse().toString();
		return Integer.parseInt(lineNoStr);
	}
}
