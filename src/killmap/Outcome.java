package killmap;

/**
 * An Outcome refers to whether a mutant is killed or not by a test. If a test kills a mutant,
 * the Outcome is KILLED. If a test does not kill a mutant, the Outcome is ALIVE. 
 * 
 * @author Raymond Tang
 *
 */
public enum Outcome {
	ALIVE, KILLED
}
