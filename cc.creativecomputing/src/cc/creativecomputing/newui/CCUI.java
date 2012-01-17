/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.newui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.events.CCDrawListener;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.CCVector1f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.newui.actions.CCUIAction;
import cc.creativecomputing.newui.actions.CCUIAnimationAction;
import cc.creativecomputing.newui.animation.CCPropertyObject;
import cc.creativecomputing.newui.animation.CCPropertyUtil;
import cc.creativecomputing.newui.animation.CCUIAnimation;
import cc.creativecomputing.newui.decorator.CCUIDecorator;
import cc.creativecomputing.newui.decorator.CCUITextDecorator;
import cc.creativecomputing.newui.decorator.background.CCUIFillBackgroundDecorator;
import cc.creativecomputing.newui.decorator.background.CCUIGradientBackgroundDecorator;
import cc.creativecomputing.newui.decorator.background.CCUIRoundedBackgroundDecorator;
import cc.creativecomputing.newui.decorator.background.CCUITexture;
import cc.creativecomputing.newui.decorator.background.CCUITextureBackgroundDecorator;
import cc.creativecomputing.newui.decorator.border.CCUILineBorderDecorator;
import cc.creativecomputing.newui.widget.CCUISliderWidget;
import cc.creativecomputing.newui.widget.CCUITextFieldWidget;
import cc.creativecomputing.newui.widget.CCUIValueBoxWidget;
import cc.creativecomputing.newui.widget.CCUIWidget;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;
import cc.creativecomputing.xml.CCXMLObjectSerializer;

/**
 * @author christianriekoff
 *
 */
public class CCUI {
	
	public Map<String, Class> _myNodeClassMap = new HashMap<String, Class>();
	
	public void addNodeClass(Class<?> theClass) {
		CCPropertyObject myNode = theClass.getAnnotation(CCPropertyObject.class);
		if(myNode == null)return;
		_myNodeClassMap.put(myNode.name(), theClass);
	}
	
	private Map<String, CCXMLElement> _myWidgetMap = new HashMap<String, CCXMLElement>();
	private Map<String, CCXMLElement> _myTemplateMap = new HashMap<String, CCXMLElement>();
	private Map<String, CCFont<?>> _myFontMap = new HashMap<String, CCFont<?>>();
	private Map<String, CCUITexture> _myTextureMap = new HashMap<String, CCUITexture>();
	private Map<String, CCUIAnimation> _myAnimations = new HashMap<String, CCUIAnimation>();
	private Map<String, List<CCXMLElement>> _myActionsMap = new HashMap<String, List<CCXMLElement>>();
	
	private List<CCUIWidget> _myWidgets = new ArrayList<CCUIWidget>();
	
	private CCApp _myApp;
	
	private CCXMLObjectSerializer _mySerializer;

	public CCUI(CCApp theApp) {
		_myApp = theApp;
		
		addNodeClass(CCColor.class);
		addNodeClass(CCVector1f.class);
		addNodeClass(CCVector2f.class);
		
		addNodeClass(CCUITexture.class);
		addNodeClass(CCUITexture.CCUITextureSplice.class);
		
		addNodeClass(CCUIWidget.class);
		addNodeClass(CCUISliderWidget.class);
		addNodeClass(CCUITextFieldWidget.class);
		addNodeClass(CCUIValueBoxWidget.class);
		
		addNodeClass(CCUIFillBackgroundDecorator.class);
		addNodeClass(CCUIRoundedBackgroundDecorator.class);
		addNodeClass(CCUIGradientBackgroundDecorator.class);
		addNodeClass(CCUITextureBackgroundDecorator.class);
		
		addNodeClass(CCUILineBorderDecorator.class);
		addNodeClass(CCUITextDecorator.class);
		
		addNodeClass(CCUIAnimation.class);
		addNodeClass(CCUIAnimation.CCPropertyTarget.class);
		
		addNodeClass(CCUIAnimationAction.class);
		
		_mySerializer = new CCXMLObjectSerializer(_myNodeClassMap);
	}
	
	private void loadFonts(CCXMLElement theUIXML) {
		CCXMLElement myFontsXML = theUIXML.child("fonts");
		if(myFontsXML == null)return;
		
		for(CCXMLElement myFontXML:myFontsXML) {
			CCFont<?>myFont;
			float mySize = myFontXML.floatAttribute("size");
			String myType = myFontXML.attribute("font_type", "texture");
			String myFontName = myFontXML.attribute("name");
			
			if(myType.equals("texture")) {
				myFont = CCFontIO.createTextureMapFont(myFontName, mySize);
				_myFontMap.put(myFontXML.attribute("id"), myFont);
			}
			
		}
	}
	
	private void loadTextures(CCXMLElement theUIXML) {
		CCXMLElement myTexturesXML = theUIXML.child("textures");
		if(myTexturesXML == null)return;
		
		for(CCXMLElement myTextureXML:myTexturesXML) {
			String myID = myTextureXML.attribute("id");
			CCUITexture myTexture = _mySerializer.toObject(myTextureXML,CCUITexture.class);
			_myTextureMap.put(myID, myTexture);
		}
	}
	
	private void replaceTemplates(CCXMLElement theElement) {
		for(CCXMLElement myElement:new ArrayList<CCXMLElement>(theElement.children())) {
			if(myElement.isTextElement())continue;
			if(myElement.name().equals("template")) {
				if(!myElement.hasAttribute("id")) {
					throw new CCUIException("To use a template you need to define the id of the template you want to use!");
				}
				String myId = myElement.attribute("id");
				CCXMLElement myReplacement = _myTemplateMap.get(myId);
				if(myReplacement == null) {
					throw new CCUIException("You haven't defined a template for the id:" + myId);
				}
				
				int myIndex = theElement.children().indexOf(myElement);
				theElement.children().remove(myIndex);
				for(CCXMLElement myTemplateChild:myReplacement) {
					theElement.children().add(myIndex++,myTemplateChild);
				}
			}else {
				replaceTemplates(myElement);
			}
		}
	}
	
	private void loadTemplates(CCXMLElement theUIXML) {
		CCXMLElement myTemplatesXML = theUIXML.child("templates");
		if(myTemplatesXML == null)return;
		
		for(CCXMLElement myTemplateXML:myTemplatesXML) {
			//TODO check for id and throw exception
			_myTemplateMap.put(myTemplateXML.attribute("id"), myTemplateXML);
		}
		
		for(CCXMLElement myTemplateXML:myTemplatesXML) {
			replaceTemplates(myTemplateXML);
		}
	}
	
	private void loadWidgets(CCXMLElement theUIXML) {
		CCXMLElement myWidgetsXML = theUIXML.child("widgets");
		
		for(CCXMLElement myWidgetXML:myWidgetsXML) {
			replaceTemplates(myWidgetXML);
			//TODO check for id and throw exception
			_myWidgetMap.put(myWidgetXML.attribute("id"), myWidgetXML);
		}
	}
	
	private void loadAnimations(CCXMLElement theUIXML) {
		CCXMLElement myAnimationsXML = theUIXML.child("animations");
		if(myAnimationsXML == null)return;
		for(CCXMLElement myAnimationXML:myAnimationsXML) {
			String myID = myAnimationXML.attribute("id");
			CCUIAnimation myAnimation = (CCUIAnimation)_mySerializer.toObject(myAnimationXML);
			_myAnimations.put(myID, myAnimation);
		}
	}
	
	public CCUIAnimation animation(String theId) {
		return _myAnimations.get(theId);
	}
	
	private void loadActions(CCXMLElement theUIXML) {
		CCXMLElement myActionsXML = theUIXML.child("actions");
		if(myActionsXML == null)return;
		
		for(CCXMLElement myActionXML:myActionsXML) {
			if(!myActionXML.hasAttribute("widget")) {
				throw new CCUIException(
					"Error in ui xml element " + myActionXML.name() + " line:" + myActionXML.line()+"\n"+
					"Action needs to define the attribute widget, to define a widget this action is assigned to.\n"+
					myActionXML.toString()
				);
			}
			String myWidget = myActionXML.attribute("widget").split("\\.")[0];
			if(!_myActionsMap.containsKey(myWidget)) {
				_myActionsMap.put(myWidget, new ArrayList<CCXMLElement>());
			}
			_myActionsMap.get(myWidget).add(myActionXML);
		}
	}
	
	public void loadUI(String theFile) {
		CCXMLElement myUIXML = CCXMLIO.createXMLElement(theFile, false);
		
		loadFonts(myUIXML);
		loadTextures(myUIXML);
		loadAnimations(myUIXML);
		loadActions(myUIXML);
		loadTemplates(myUIXML);
		loadWidgets(myUIXML);
	}
	
	public CCFont<?> font(String theFont){
		return _myFontMap.get(theFont);
	}
	
	public CCUITexture texture(String theTexture) {
		return _myTextureMap.get(theTexture);
	}
	
	public List<CCUIDecorator> createDecorator(CCXMLElement theDecoratorXML){
		List<CCUIDecorator> myDecorators = new ArrayList<CCUIDecorator>();
		
		return myDecorators;
	}
	
	public CCUIWidget createWidget(String theID) {
		return createWidget(theID, null);
	}
	
	public <Type extends CCUIWidget> Type createWidget(String theID, Class<Type> theClass) {
		CCXMLElement myWidgetXML = _myWidgetMap.get(theID);
		
		if(myWidgetXML == null) {
			throw new CCUIException(
				"The given Widget:" + theID +
				" could not be created as there is no such widget defined in the ui description xml file."
			);
		}
		
		Type myWidget = _mySerializer.toObject(myWidgetXML,theClass);
		myWidget.setup(this, null);
		
		if(_myActionsMap.containsKey(theID)) {
			for(CCXMLElement myActionXML:_myActionsMap.get(theID)) {
				String myWidgetPath = myActionXML.attribute("widget");
				
				CCUIAction myAction = (CCUIAction)_mySerializer.toObject(myActionXML);
				
				if(myWidgetPath.indexOf(".")>=0) {
					myWidgetPath = myWidgetPath.substring(myWidgetPath.indexOf("."));
					CCUIWidget mySubwidget = (CCUIWidget)CCPropertyUtil.property(myWidgetPath, myWidget);
					myAction.widget(mySubwidget);
				}else {
					myAction.widget(myWidget);
				}
				
				myAction.init(this);
			}
		}
		
		_myWidgets.add(myWidget);
		
		return myWidget;
	}
	
	public void checkEvent(CCVector2f theVector, CCUIInputEventType theEventType) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.checkEvent(theVector, theEventType);
		}
	}
	
	public void update(float theDeltaTime) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.update(theDeltaTime);
		}
		for(CCUIAnimation myAnimation:_myAnimations.values()) {
			myAnimation.update(theDeltaTime);
		}
	}

	
	public void draw(CCGraphics g) {
		for(CCUIWidget myWidget:_myWidgets) {
			myWidget.draw(g);
		}
	}
	
	public List<CCUIWidget> widgets(){
		return _myWidgets;
	}
	
}
