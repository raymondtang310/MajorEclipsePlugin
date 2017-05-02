package analyzer;

/**
 * A Mutant object represents a mutant. This stores data related to
 * a mutant, including its ID.
 * 
 * @author Raymond Tang
 *
 */
public class Mutant {
	// This mutant's ID
	private int mutantID;
	
	public Mutant(int mutantID) {
		this.mutantID = mutantID;
	}
	
	/**
	 * Returns this mutant's ID.
	 * 
	 * @return the ID of this mutant
	 */
	public int getID() {
		return mutantID;
	}
}
