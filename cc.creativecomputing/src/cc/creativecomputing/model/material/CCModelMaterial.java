package cc.creativecomputing.model.material;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMaterial;

public class CCModelMaterial extends CCMaterial{
	
	public static CCModelMaterial DEFAULT = new CCModelMaterial(
		new CCColor(0.2f,0.2f,0.2f),
		new CCColor(0.8f,0.8f,0.8f),
		new CCColor(0.1f,0.1f,0.1f),
		null,
		-1
	);
	
	private List<CCModelMaterialProperty> _myProperties = new ArrayList<CCModelMaterialProperty>();
	
	
	
	/**
	 * 
	 */
	public CCModelMaterial() {
		super();
	}

	/**
	 * @param theAmbient
	 * @param theDiffuse
	 * @param theSpecular
	 * @param theEmission
	 * @param theShininess
	 */
	public CCModelMaterial(CCColor theAmbient, CCColor theDiffuse, CCColor theSpecular, CCColor theEmission, int theShininess) {
		super(theAmbient, theDiffuse, theSpecular, theEmission, theShininess);
	}

	public void addProperty(final CCModelMaterialProperty theProperty){
		_myProperties.add(theProperty);
	}
	
	public void begin(CCGraphics g){
		for(CCModelMaterialProperty myProperty:_myProperties){
			myProperty.begin(g);
		}
		super.draw(g);
	}
	
	public void end(CCGraphics g){
		for(CCModelMaterialProperty myProperty:_myProperties){
			myProperty.end(g);
		}
	}
}
