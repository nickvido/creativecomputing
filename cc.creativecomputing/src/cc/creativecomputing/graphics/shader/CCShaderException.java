package cc.creativecomputing.graphics.shader;

public class CCShaderException extends RuntimeException{

	/**
	 * 
	 */
	public CCShaderException() {
		super();
	}

	/**
	 * @param theMessage
	 * @param theCause
	 */
	public CCShaderException(String theMessage, Throwable theCause) {
		super(theMessage, theCause);
	}

	/**
	 * @param theMessage
	 */
	public CCShaderException(String theMessage) {
		super(theMessage);
	}

	/**
	 * @param theCause
	 */
	public CCShaderException(Throwable theCause) {
		super(theCause);
	}

}
