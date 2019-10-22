package mutator;

public class MutateException extends Exception {

	/**
	 * This exception should be thrown when an error occurs while trying to
	 * compile mutants.
	 */
	private static final long serialVersionUID = 5881814114009320374L;

	public MutateException(String message) {
		super(message);
	}
}
