package cc.creativecomputing.control;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.control.connect.CCReflectionConnector;
import cc.creativecomputing.control.modulators.CCEnvelope;
import cc.creativecomputing.control.timeline.CCTimedData;
import cc.creativecomputing.control.timeline.CCTimedDataUI;
import cc.creativecomputing.control.ui.CCUIComponent;
import cc.creativecomputing.control.ui.CCUIElement;
import cc.creativecomputing.control.ui.CCUITab;
import cc.creativecomputing.control.ui.layout.CCUIVerticalLayoutManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.events.CCKeyListener;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseListener;
import cc.creativecomputing.events.CCMouseMotionListener;
import cc.creativecomputing.events.CCPostListener;
import cc.creativecomputing.events.CCUpdateListener;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphicsCore.CCShapeMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCGlutFont;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;


public class CCControlUI extends CCUIComponent implements CCMouseListener, CCMouseMotionListener, CCKeyListener, CCPostListener{
	
	public static CCGlutFont FONT = CCFontIO.createGlutFont(CCFontIO.BITMAP_HELVETICA_10);
	
	private Map<String, CCControlPresetTab> _myTabMap = new HashMap<String, CCControlPresetTab>();
	private Map<String,String> _myTabNameMap = new HashMap<String, String>();
	private Map<String, CCValueControl<?>> _myValueControls = new HashMap<String,CCValueControl<?>>();
	private CCUITab _myActiveTab;
	private CCControlGlobalTab _myGlobalTab;
	
	private final CCApp _myApp;
	
	private float _myDefaultSliderWidth = 150;
	private float _myDefaultSliderHeight = 14;
	private float _mySpace = 10;
	private float _myTabX = 0;
	
	private CCXMLElement _myXML;
	private boolean _myIsVisible = true;
	
	private int _myOpenKey = CCKeyEvent.VK_SPACE;
	
	private boolean _myDrawBackground = false;
	
	private CCVector2f _myTranslation = new CCVector2f();
	
	/**
	 * List containing external controls
	 */
	private List<CCExternalController> _myExternalControls = new ArrayList<CCExternalController>();
	
	public CCControlUI(final CCApp theApp){
		
		super("",0,0,0,0);
		
		_myApp = theApp;
		_myApp.addMouseListener(this);
		_myApp.addMouseMotionListener(this);
		_myApp.addKeyListener(this);
		_myApp.addPostListener(this);
		
		String myFileName = _myApp.getClass().getSimpleName() + "_ui.xml";
		
		if(CCIOUtil.exists("settings/" + myFileName)){
			_myXML = CCXMLIO.createXMLElement("settings/"+myFileName);
		}
		if(CCIOUtil.exists(myFileName)){
			_myXML = CCXMLIO.createXMLElement(myFileName);
		}

		createGlobalTab();
//		addEditable("default",theApp);
		
		_myActiveTab = _myTabMap.get("global");
		_myActiveTab.button().value(true);
	}
	
	public void addExternalController(final CCExternalController theController) {
		_myExternalControls.add(theController);
	}
	
	public int openKey() {
		return _myOpenKey;
	}
	
	public void openKey(final int theOpenKey) {
		_myOpenKey = theOpenKey;
	}
	
	public void show(){
		_myIsVisible = true;
	}
	
	public void hide(){
		_myIsVisible = false;
	}
	
	public boolean isVisible(){
		return _myIsVisible;
	}
	
	public float space(){
		return _mySpace;
	}
	
	public float defaultWidth(){
		return _myDefaultSliderWidth;
	}
	
	public float defaultHeight(){
		return _myDefaultSliderHeight;
	}
	
	public void drawBackground(final boolean theDrawBackground) {
		_myDrawBackground = theDrawBackground;
	}
	
	public void activeTab(final CCUITab theTab){
		for(CCUITab myElement:_myTabMap.values()){
			myElement.button().value(false);
		}
		theTab.button().value(true);
		_myActiveTab = theTab;
	}
	
	private CCControlGlobalTab createGlobalTab(){
		float myTabWidth = FONT.width("global") * FONT.size()+20;
		CCControlGlobalTab myGlobalTab = new CCControlGlobalTab(this,"global",0,0,myTabWidth,20, _myTabX);
		
		add(myGlobalTab.button());
		_myTabX += myTabWidth + 2;
		_myTabMap.put("global", myGlobalTab);
		_myTabNameMap.put("global", "global");
		return myGlobalTab;
	}
	
	private void getFields(final Class<?> theClass, final List<Field> theFields) {
		if(theClass.getSuperclass() == null) return;
			
		getFields(theClass.getSuperclass(), theFields);
		
		for (Field myField : theClass.getDeclaredFields()) {
			myField.setAccessible(true);

			CCControl myControl = myField.getAnnotation(CCControl.class);
			if (myControl != null)theFields.add(myField);
			if(myField.getType().getSuperclass() == CCValueControl.class)theFields.add(myField);
		}
	}
	
	private void getMethods(final Class<?> theClass, final List<Method> theMethods) {
		if(theClass.getSuperclass() == null) return;
			
		getMethods(theClass.getSuperclass(), theMethods);
		
		for (Method myMethod : theClass.getDeclaredMethods()) {
			myMethod.setAccessible(true);

			CCControl myControl = myMethod.getAnnotation(CCControl.class);

			if (myControl != null)theMethods.add(myMethod);
		}
	}
	
	/**
	 * Adds an object with controllable parameters to user interface, for static access the object is null.
	 * @param theTabName name of the tab that will contain the controls for the given object
	 * @param theClass class of the given object
	 * @param theObject instance of the class to create controls this might be null for static access
	 */
	private void addEditable(final String theTabName, final String theObjectID, final int theColumn, final Class<?> theClass, final Object theObject, List<String> thePath) {
		CCControlPresetTab myTab = _myTabMap.get(theTabName);
		
		if(myTab == null) {
			CCXMLElement myTabXML = null;
			if (_myXML != null) {
				for (CCXMLElement myTabElement : _myXML.children()) {
					if (myTabElement.attribute("label").equals(theTabName)) {
						myTabXML = myTabElement;
						break;
					}
				}
			}
			
			float myTabButtonWidth = FONT.width(theTabName) * FONT.size() + 20;
			myTab = new CCControlPresetTab(myTabXML, this, theTabName, 0, 0, myTabButtonWidth, 20,_myTabX);
			add(myTab.button());
			
			_myTabMap.put(theTabName, myTab);
			_myTabNameMap.put(theTabName, theClass.getName());
			_myTabX += myTabButtonWidth + 2;
		}
		
		List<Field> myFields = new ArrayList<Field>();
		getFields(theClass, myFields);
		
		for (Field myField : myFields) {
			myField.setAccessible(true);

			CCControl myControl = myField.getAnnotation(CCControl.class);
			Class<?> myClass = myField.getType();
			
			if (myControl == null) {
				continue;
			}

			if (myClass.isPrimitive())continue;
			if (myClass == CCVector2f.class) continue;
			if (myClass == CCEnvelope.class) continue;
			if (myClass.isEnum()) continue;
			
			try {
				Object myObject = myField.get(theObject);
				String myTabName = myControl.tabName();
				if(myTabName == null || myTabName.equals(""))myTabName = theTabName;
				
				int myColumn = myControl.column();
				if(myColumn == -1)myColumn = theColumn;
				
//				addControls(myTabName, myControl.name(), myColumn, myObject);
				List<String> myPath = new ArrayList<String>();
				if(thePath != null)myPath.addAll(thePath);
				myPath.add(theObjectID);
				addEditable(myTabName, myControl.name(), myColumn, myObject.getClass(), myObject, myPath);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		CCUIComponent myComponent = new CCUIComponent(theObjectID, 0, 0, 0, 0);
		myComponent.layoutManager(new CCUIVerticalLayoutManager(myComponent, _mySpace));

		for (Field myField : myFields) {
			myField.setAccessible(true);

			CCControl myControl = myField.getAnnotation(CCControl.class);
			
//			if (myControl == null) {
//				continue;
//				_myValueControls.put(theTabName +"_" + theObjectID + "_" + myValueControl.name(), myValueControl);
//			}

			Class<?> myClass = myField.getType();
			
			CCValueControl<?> myValueControl = null;

			if (myClass == Float.TYPE) {
				CCReflectionConnector<Float> myControlConnector = new CCReflectionConnector<Float>(myControl, myField, theObject);
				myValueControl = new CCFloatControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (myClass == Integer.TYPE) {
				CCReflectionConnector<Integer> myControlConnector = new CCReflectionConnector<Integer>(myControl, myField, theObject);
				myValueControl = new CCIntegerControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (myClass == Boolean.TYPE) {
				CCReflectionConnector<Boolean> myControlConnector = new CCReflectionConnector<Boolean>(myControl, myField, theObject);
				myValueControl = new CCBooleanControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (myClass == CCVector2f.class) {
				CCReflectionConnector<CCVector2f> myControlConnector = new CCReflectionConnector<CCVector2f>(myControl, myField, theObject);
				myValueControl = new CCVector2fControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderWidth, myTab.numberOfPresets());
			}
			if(myClass == CCEnvelope.class) {
				try {
					CCReflectionConnector<CCTimedData[]> myControlConnector = new CCReflectionConnector<CCTimedData[]>(myControl, myField, theObject);
					Field myDataField = myClass.getDeclaredField("_myTimedDatas");
					myDataField.setAccessible(true);
					CCEnvelope myObject = (CCEnvelope)myField.get(theObject);
					if(myObject == null) {
						myObject = (CCEnvelope)myClass.getConstructor().newInstance();
					}
					myObject.min(myControl.min());
					myObject.max(myControl.max());
					_myApp.addUpdateListener((CCUpdateListener)myObject);
					myValueControl = new CCEnvelopeControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderWidth / 2, myTab.numberOfPresets());
				} catch (Exception e) {
					throw new RuntimeException("Could not create envelope",e);
				}
			}
			if(myClass.getSuperclass() == CCValueControl.class) {
				try {
					myValueControl = (CCValueControl)myField.get(theObject);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			if (myClass.isEnum()) {
				CCReflectionConnector<Enum<?>> myControlConnector = new CCReflectionConnector<Enum<?>>(myControl, myField, theObject);
				myValueControl = new CCEnumControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}

			if(myValueControl != null) {
				myComponent.add(myValueControl.element());
				if(myValueControl instanceof CCEnvelopeControl) {
					CCEnvelopeControl myEnvelopeControl = (CCEnvelopeControl)myValueControl;
					myComponent.add(((CCTimedDataUI)myEnvelopeControl.element()).envelopeControls());
				}
			}
			
			if(myValueControl == null)continue;
			_myValueControls.put(theTabName +"_" + theObjectID + "_" + myValueControl.name(), myValueControl);
			
			if(myControl != null && myControl.external()) {
				StringBuilder myPath = new StringBuilder();
				if(thePath != null) {
					for(String myName:thePath) {
						myPath.append(myName);
						myPath.append("/");
					}
				}
				myPath.append(theObjectID);
				for(CCExternalController myController:_myExternalControls) {
					myController.addControl(theTabName, myPath.toString(), myValueControl);
				}
			}
		}
		
		List<Method> myMethods = new ArrayList<Method>();
		getMethods(theClass, myMethods);

		for (Method myMethod : myMethods) {
			myMethod.setAccessible(true);
			CCControl myControl = myMethod.getAnnotation(CCControl.class);

			if (myControl == null)
				continue;

			Class<?>[] _myParameters = myMethod.getParameterTypes();
			CCValueControl<?> myValueControl = null;
			
			if (_myParameters.length == 1 && _myParameters[0] == Float.TYPE) {
				CCReflectionConnector<Float> myControlConnector = new CCReflectionConnector<Float>(myControl, myMethod, theObject);
				myValueControl = new CCFloatControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if(_myParameters.length == 1 && _myParameters[0] == Integer.TYPE){
				CCReflectionConnector<Integer> myControlConnector = new CCReflectionConnector<Integer>(myControl, myMethod, theObject);
				myValueControl = new CCIntegerControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (myControl.toggle() && _myParameters.length == 1 && _myParameters[0] == Boolean.TYPE) {
				CCReflectionConnector<Boolean> myControlConnector = new CCReflectionConnector<Boolean>(myControl, myMethod, theObject);
				myValueControl = new CCBooleanControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (!myControl.toggle() && _myParameters.length == 0) {
				CCReflectionConnector<Boolean> myControlConnector = new CCReflectionConnector<Boolean>(myControl, myMethod, theObject);
				myValueControl = new CCBooleanControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			if (_myParameters.length == 1 && _myParameters[0].isEnum()) {
				CCReflectionConnector<Enum<?>> myControlConnector = new CCReflectionConnector<Enum<?>>(myControl, myMethod, theObject);
				myValueControl = new CCEnumControl(myControlConnector, _myDefaultSliderWidth, _myDefaultSliderHeight, myTab.numberOfPresets());
			}
			// if(myClass == CCVector2f.class){
			// new CCUIVector2fControl(myTabXML,myTab,myControl,myMethod,theObject);
			// }

			if(myValueControl != null) {
				myComponent.add(myValueControl.element());
			}
			
			_myValueControls.put(theTabName +"_" + theObjectID + "_" + myControl.name(), myValueControl);
			
			if(myControl.external()) {
				StringBuilder myPath = new StringBuilder();
				if(thePath != null) {
					for(String myName:thePath) {
						myPath.append(myName);
						myPath.append("/");
					}
				}
				myPath.append(theObjectID);
				for(CCExternalController myController:_myExternalControls) {
					myController.addControl(theTabName, myPath.toString(), myValueControl);
				}
			}
		}

		myTab.add(myComponent, theColumn);

	}
	
	/**
	 * Checks if the given class has the {@link CCControlClass} annotation attached to it.
	 * In this case a new tab with controls for the parameters is created.
	 * If the class has no {@link CCControlClass} annotation attached it scanned for
	 * inner classes that have the annotation attached.
	 * @param theClass the class to check for annotations
	 */
	public void addControls(final Class<?> theClass) {
		CCControlClass myEditable = theClass.getAnnotation(CCControlClass.class);
		
		if(myEditable != null) {
			addControls(myEditable.name(),myEditable.name(),0, theClass);
			return;
		}
		
		for(Class<?> myClass:theClass.getDeclaredClasses()) {
			myEditable = myClass.getAnnotation(CCControlClass.class);
			
			if(myEditable != null) {
				addControls(myEditable.name(), myClass);
			}
		}
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theColumn column in which to place the ui elements
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final int theColumn, final Class<?> theClass) {
		CCControlClass myEditable = theClass.getAnnotation(CCControlClass.class);
		
		String myName = theClass.getSimpleName();
		
		if(myEditable != null) {
			myName = myEditable.name();
		}

		addControls(myName, myName, theColumn, theClass);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final Class<?> theClass){
		CCControlClass myEditable = theClass.getAnnotation(CCControlClass.class);
		
		String myName = theClass.getSimpleName();
		
		if(myEditable != null) {
			myName = myEditable.name();
		}
		addEditable(theTabName, myName, 0, theClass, null, null);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theColumn column in which to place the ui elements
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final int theColumn, final Class<?> theClass){
		addEditable(theTabName, theClass.getSimpleName(), theColumn, theClass, null, null);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectName, final Class<?> theClass){
		addEditable(theTabName, theObjectName, 0, theClass, null, null);
	}
	
	/**
	 * For static access of fields only a class is needed as we need no object instance.
	 * @param theTabName name of the tab to create
	 * @param theColumn column in which to place the ui elements
	 * @param theObjectName id of the object to safe the preset data
	 * @param theClass class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectName, final int theColumn, final Class<?> theClass){
		addEditable(theTabName, theObjectName, theColumn, theClass, null, null);
	}
	
	/**
	 * To add controls for an object instance simply pass the instance of the object
	 * and a tab name.
	 * @param theTabName name of the tab to create
	 * @param theObject class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final Object theObject){
		addEditable(theTabName, theTabName, 0, theObject.getClass(), theObject, null);
	}
	
	/**
	 * To add controls for an object instance simply pass the instance of the object
	 * and a tab name.
	 * @param theTabName name of the tab to create
	 * @param theColumn column in which to place the ui elements
	 * @param theObject class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final int theColumn, final Object theObject){
		addEditable(theTabName, theTabName, theColumn, theObject.getClass(), theObject, null);
	}
	
	/**
	 * To add controls for an object instance simply pass the instance of the object
	 * and a tab name.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theObject class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectName, final Object theObject){
		addEditable(theTabName, theObjectName, 0, theObject.getClass(), theObject, null);
	}
	
	/**
	 * To add controls for an object instance simply pass the instance of the object
	 * and a tab name.
	 * @param theTabName name of the tab to create
	 * @param theObjectName id of the object to safe the preset data
	 * @param theColumn column in which to place the ui elements
	 * @param theObject class with parameters to be controlled
	 */
	public void addControls(final String theTabName, final String theObjectName, final int theColumn, final Object theObject){
		addEditable(theTabName, theObjectName, theColumn, theObject.getClass(), theObject, null);
	}
	
	public void add(final String theTabName, final CCUIElement theElement){
		if(theElement instanceof CCControlPresetTab){
			CCControlPresetTab myTab = (CCControlPresetTab)theElement;
			_myTabMap.put(theTabName, myTab);
		}else{
			_myTabMap.get(_myTabNameMap.get(theTabName)).add(theElement);
		}
	}
	
	public static float NEAR = -1;
	public static float FAR = 1;
	
	public Map<String, CCControlPresetTab> tabMap(){
		return _myTabMap;
	}
	
	/*
	 * IMPLEMENT APPLICATION LISTENER
	 */
	
	
	public void draw(CCGraphics g){
		CCColor myStoreColor = g.color();
		CCShapeMode myStoreShapeMode = g.rectMode();
		
		boolean myIsLighting = g.lighting;
		g.noLights();
		g.pushAttribute();
		g.noDepthTest();

		g.rectMode(CCShapeMode.CORNER);
		g.beginOrtho();
		g.pushMatrix();
		g.translate(_myTranslation);
		
		if(_myDrawBackground) {
			g.color(0,100);
			g.rect(0,0,g.width,g.height);
		}
		
		g.color(255);
		for(CCUIElement myElement:_myTabMap.values()){
			myElement.draw(g);
		}
		g.popMatrix();
		g.endOrtho();
		g.color(myStoreColor);
		g.rectMode(myStoreShapeMode);
		g.popAttribute();
		if(myIsLighting)g.lights();
	}
	
	public void post(){
		if(_myIsVisible)draw(_myApp.g);
	}
	
	public void save() {
		
		CCXMLElement myUIXML = new CCXMLElement("ui");
		for(CCUITab myTab:_myTabMap.values()){
			myUIXML.addChild(myTab.toXML());
		}
		CCXMLIO.saveXMLElement(myUIXML, "data/settings/"+_myApp.getClass().getSimpleName()+"_ui.xml");
	}
	
	public void refreshValues() {
		for (CCValueControl<?> myControl:_myValueControls.values()) {
			if(myControl == null)continue;
			myControl.readBack();
		}
	}
	
	public CCValueControl<?> control(String theTab, String theObject, String theName){
		return _myValueControls.get(theTab + "_" + theObject + "_" + theName);
	}
	
	/* 
	 * IMPLEMENT MOUSE LISTENERS
	 */

	public void mouseClicked(CCMouseEvent theEvent) {}

	public void mouseEntered(CCMouseEvent theEvent) {}

	public void mouseExited(CCMouseEvent theEvent) {}

	public void mousePressed(CCMouseEvent theEvent) {
		CCMouseEvent myEvent = theEvent.clone();
		myEvent.position().subtract(_myTranslation);
		myEvent.pPosition().subtract(_myTranslation);
		
		if(!_myIsVisible)return;
		_myActiveTab.onPress(myEvent);
		onPress(myEvent);
	}

	public void mouseReleased(CCMouseEvent theEvent) {
		CCMouseEvent myEvent = theEvent.clone();
		myEvent.position().subtract(_myTranslation);
		myEvent.pPosition().subtract(_myTranslation);
		
		if(!_myIsVisible)return;
		_myActiveTab.onRelease(myEvent);
		onRelease(myEvent);
	}

	public void mouseDragged(CCMouseEvent theEvent) {
		CCMouseEvent myEvent = theEvent.clone();
		myEvent.position().subtract(_myTranslation);
		myEvent.pPosition().subtract(_myTranslation);
		
		if(!_myIsVisible)return;
		_myActiveTab.onDragg(myEvent);
		onDragg(myEvent);
	}

	public void mouseMoved(CCMouseEvent theEvent) {
		CCMouseEvent myEvent = theEvent.clone();
		myEvent.position().subtract(_myTranslation);
		myEvent.pPosition().subtract(_myTranslation);
		
		if(!_myIsVisible)return;
		_myActiveTab.onMove(myEvent);
		onMove(myEvent);
	}

	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		if (!_myIsVisible)return;
		_myActiveTab.onKey(theKeyEvent);
		onKey(theKeyEvent);
	}

	@Override
	public void keyReleased(CCKeyEvent theKeyEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(CCKeyEvent theKeyEvent) {
		// TODO Auto-generated method stub
		
	}
	
	public CCVector2f translation(){
		return _myTranslation;
	}

}
