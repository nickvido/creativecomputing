package cc.creativecomputing.xml;


public abstract class CCAbstractXMLChildTool<XMLParentObject extends CCAbstractXMLObject,XMLChildObject extends CCAbstractXMLObject> extends CCAbstractXMLRootTool<XMLChildObject>{

	protected CCAbstractXMLRootTool<XMLParentObject> _myParentTool;
	
	public CCAbstractXMLChildTool(
		final CCAbstractXMLRootTool<XMLParentObject> theParentTool, 
		final String theToolTag
	){
		super(theToolTag);
		_myParentTool = theParentTool;
	}

	/**
	 * In this method you can call operations being executed after parsing an end tag.
	 * @param theString 
	 */
	public void doAfterEndTag(String theString){
		if(theString.equals(_myToolTag)){
			_myParser.setTool(_myParentTool);
		}
	}
}
