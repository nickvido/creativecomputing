package cc.creativecomputing.sound.supercollider;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CCSoundLibraryReader {
	
	CCSoundLibrary _mySoundLibrary = null;

	public CCSoundLibraryReader(File theFile) {
		
		_mySoundLibrary = new CCSoundLibrary();
		
		DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder myDocumentBuilder = myFactory.newDocumentBuilder();
			Document myDocument = myDocumentBuilder.parse(theFile);
			
			XPathFactory myXPathFactory = XPathFactory.newInstance();
			XPath myXPath = myXPathFactory.newXPath();
			XPathExpression myExpr = myXPath.compile("/sounds");
			
			Node mySoundNode = (Node) myExpr.evaluate(myDocument, XPathConstants.NODE);
			if (mySoundNode != null) {
				NodeList mySounds = mySoundNode.getChildNodes();
				for (int i = 0; i < mySounds.getLength(); i++) {
					if (mySounds.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element myCurrentSound = (Element) mySounds.item(i);
						String myID = myCurrentSound.getAttribute("id");
						String myPath = myCurrentSound.getAttribute("path");
						String myLoop = myCurrentSound.getAttribute("loop");
						boolean loop = false;
						if (myLoop != null && !myLoop.isEmpty()) {
							loop = new Integer(myLoop) > 0;
						}
						CCSound mySound = new CCSound(myID, myPath, loop);
						_mySoundLibrary.setSound(myID, mySound);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	public CCSoundLibrary getSoundLibrary() {
		return _mySoundLibrary;
	}
	                                       
}
