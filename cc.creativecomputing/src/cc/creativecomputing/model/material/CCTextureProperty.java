package cc.creativecomputing.model.material;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture;

public class CCTextureProperty implements CCModelMaterialProperty{
	
	private CCTexture _myTexture;
	
	public CCTextureProperty(final CCTexture theTexture){
		_myTexture = theTexture;
	}
	
	public void texture(final CCTexture theTexture){
		_myTexture = theTexture;
	}

	public void begin(CCGraphics g) {
		g.texture(_myTexture);
	}

	public void end(CCGraphics g) {
		g.noTexture();
	}

}
