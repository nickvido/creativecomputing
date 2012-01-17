package cc.creativecomputing.xml;

/**
 * An InvalidDocumentException occurs when you try to load a file that does not contain XML.
 * Or the XML file you load has mistakes.
 * @nosuperclasses
 */

public class CCInvalidDocumentException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3635832302276564720L;

	public CCInvalidDocumentException () {
        super("This is not a parsable URL");
    }

    public CCInvalidDocumentException (String url) {
        super(url+" is not a parsable URL");
    }
    
    public CCInvalidDocumentException (String url, Exception i_exception) {
       super(url+" is not a parsable URL",i_exception);
   }

}
