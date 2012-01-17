package cc.creativecomputing.io;

public class CCIOException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6260852642259168506L;

	/**
	 * 
	 */
	public CCIOException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCIOException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCIOException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCIOException(Throwable theCause) {
		super(theCause);
	}

}
