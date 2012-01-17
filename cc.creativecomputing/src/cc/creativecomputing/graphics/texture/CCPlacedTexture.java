package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.math.CCVector2f;

/**
 * Interface to indicate that a texture has parameters for positioning.
 * @author texone
 *
 */
public abstract class CCPlacedTexture extends CCTexture2D{
	/**
	 * Returns the offset of the texture
	 * @return
	 */
	public abstract CCVector2f offset();
}
