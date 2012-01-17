package cc.creativecomputing.graphics;

public class CCGraphicsException extends RuntimeException{

	/**
	 * 
	 */
	public CCGraphicsException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCGraphicsException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCGraphicsException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCGraphicsException(Throwable theCause) {
		super(theCause);
	}

}
