package analyzer;

/**
 * An Outcome refers to whether a mutant is killed or not by a test. If a test kills a mutant,
 * the Outcome is KILLED. If a test does not kill a mutant, the Outcome is ALIVE. 
 * 
 * Adapted from Rene Just's fault-localization-data
 * Link: https://bitbucket.org/rjust/fault-localization-data/src/1b5b3f155c41ef4d7c11ca422a94e5ff90b558e7/killmap/src/main/killmap/runners/communication/Outcome.java?at=icse17&fileviewer=file-view-default
 * Tag: icse17
 * 
 * @author Raymond Tang
 *
 */
public enum Outcome {
	ALIVE, KILLED
}
