package analyzer;

/**
 * A Mutant object represents a mutant. This stores data related to
 * a mutant, including its ID.
 * 
 * @author Raymond Tang
 *
 */
public class Mutant implements Comparable<Mutant>{
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
	
	/**
	 * Checks if the given Mutant is equal to this Mutant. Two Mutants are equal
	 * if and only if their IDs are equal.
	 * 
	 * @return true if the given mutant is equal to this mutant, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Mutant)) return false;
		Mutant otherMutant = (Mutant)obj;
		return this.mutantID == otherMutant.getID();
	}

	/**
	 * Compares the given Mutant to this Mutant by their IDs.
	 * If this Mutant's ID is greater/less than the given Mutant's ID,
	 * than this Mutant is considered greater/less than the given Mutant.
	 * Mutants are considered equal if their IDs are equal.
	 * 
	 * @param the Mutant to compare to
	 * @return a positive integer, negative integer, or 0 if this Mutant
	 * 		   is greater than, less than, or equal to the given Mutant,
	 * 		   respectively
	 */
	@Override
	public int compareTo(Mutant otherMutant) {
		return this.mutantID - otherMutant.getID();
	}
}
