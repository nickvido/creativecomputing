package cc.creativecomputing.xml;

import java.util.Map;

public abstract class CCAbstractXMLTool {
	protected CCXMLIO.CCXMLParser<? extends CCAbstractXMLTool> _myParser;

	protected String _myCurrentTag;

	public CCAbstractXMLTool() {}

	final void setParser(final CCXMLIO.CCXMLParser<? extends CCAbstractXMLTool> theParser) {
		_myParser = theParser;
	}

	/**
	 * Implement this method to initialize values that you need for your parsing
	 */
	public abstract void initBeforeParsing();

	/**
	 * In this method you have to define how parsed start tags are handles. For Example HtmlElementFinder filters the
	 * elements according to its given kindOfElement in its implementation of this method.
	 * 
	 * @param theTag String
	 * @param theAttributes HashMap
	 * @param theIsStandAlone boolean
	 */
	public abstract void handleStartTag(final String theTag, final Map<String, String> theAttributes, final boolean theIsStandAlone);

	/**
	 * Implementing this Method gives you the possibility to handle Attributes during parsing. For Example HtmlTree save
	 * the color values of a page right here.
	 * 
	 * @param theAttributeName String
	 * @param theAttributeValue String
	 */
	public abstract void handleAttribute(final String theAttributeName, final String theAttributeValue);

	/**
	 * In this method you can call operations being executed after parsing an end tag.
	 * 
	 * @param theString
	 */
	public abstract void doAfterEndTag(String theString);

	/**
	 * You have to implement this method to describe what happens to TextElements
	 * 
	 * @param theTextElement TextElement
	 */

	public abstract void handleText(final String theText);

	/**
	 * You have to implement this method to describe what happens to CDATA sections
	 * 
	 * @param theTextElement TextElement
	 */
	public abstract void handleCDATASection(final String theCDATASection);
}
