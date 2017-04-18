package mutator;

public class SelectionNotAdaptableException extends Exception {

	/**
	 * This exception should be thrown when a selection is not of type IAdaptable 
	 * when it is intended to be.
	 */
	private static final long serialVersionUID = 1L;

	public SelectionNotAdaptableException(String message) {
		super(message);
	}
	
}
