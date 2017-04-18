package mutator;

public class JavaFileNotSelectedException extends Exception {

	/**
	 * This exception should be thrown when a selection is not a java file when it is intended to be.
	 */
	private static final long serialVersionUID = 1L;

	public JavaFileNotSelectedException(String message) {
		super(message);
	}
	
}
