package cc.creativecomputing.xml;

import java.util.*;


/**
 * Object for parsing a valid XML Document to convert its String 
 * into a HtmlTree representing its structure. After successful 
 * initialization of the tree you can use different methods and 
 * fields to get information of the page. <br>
 * The Example prints the tree structure of the processing reference 
 * page and the values of the fields of the initialized HtmlTree object. 
 * @example HtmlTree
 * @related pageTree
 */

public class CCXMLTree extends CCAbstractXMLTool{

    /**
     * PageTree is a field containing the tree structure of the document. 
     * It contains every element, excluding comments and scripts. 
     * PageTree is a HtmlElement so see its methods for further information.
     * @shortdesc This field holds the tree structure of the parsed document.
     * @example HtmlTree_pagetree
     * @related HtmlTree
     */
    private CCXMLElement _myRootElement;

    /**
     * field to keep the parent element to put the children elements in
     */
    private CCXMLElement _myActualElement;
    
    private boolean _myIsfirstTag = false;
    private boolean _myIsRootNode = false;


    /**
     * Initializes a new HtmlTree
     */

    public CCXMLTree() {
    }


    /**
     * This makes sure that the given kindOfElement is to UpperCase
     */

    public void initBeforeParsing() {
   	 _myIsfirstTag = true;
   	 _myIsRootNode = true;
    }
    
    /**
		 * Implments the handleStartTag method of HtmlCollection. Here all
		 * Elements are placed in the tree structure, according to its position in
		 * the page.
		 * 
		 * @param theTagName
		 *           String
		 * @param theAttributes
		 *           HashMap
		 */

    public void handleStartTag(final String theTagName, final Map<String, String> theAttributes, final boolean theIsStandAlone){

		if (_myIsfirstTag){
			_myIsfirstTag = false;
			if (!(theTagName.equals("doctype") || theTagName.equals("?xml"))){
//				throw new RuntimeException("XML File has no valid header");
			}
		}else{
			if (_myIsRootNode && !_myIsfirstTag){
				_myIsRootNode = false;
				_myRootElement = new CCXMLElement(theTagName, theAttributes);
				_myActualElement = _myRootElement;
				_myActualElement.line(_myParser.line);
			}else{
				CCXMLElement keep = new CCXMLElement(theTagName, theAttributes);
				keep.line(_myParser.line);
				_myActualElement.addChild(keep);
				if (!theIsStandAlone)
					_myActualElement = keep;
			}
			
			
		}
    }
    
    public void handleCDATASection(final String theCDATASection){
		final CCXMLElement myCDATASection = new CCXMLElement(theCDATASection);
		myCDATASection.cdata = true;
		myCDATASection._myIsPCData = true;
		_myActualElement.addChild(myCDATASection);
	}




    /**
     * In this Implementation all colorAttributes during parsing are stored
     * @param theAttributeName String
	  * @param theAttributeValue String
	  */
    public void handleAttribute(final String theAttributeName, final String theAttributeValue){}

    /**
     * After parsing an end tag. The acualElement is its parent Element
     */
    public void doAfterEndTag(final String theTag) {
		if (!_myActualElement.equals(_myRootElement))
			_myActualElement = _myActualElement.parent();
    }

    /**
		 * Places the text elements in the tree structure
		 * 
		 * @param toHandle
		 *           TextElement
		 */

    public void handleText(final String theText) {
		_myActualElement.addChild(new CCXMLElement(theText, true));
    }

/**
 * Returns the root of the XML tree
 * @return the root element
 */
	public CCXMLElement rootElement(){
		return _myRootElement;
	}

}
