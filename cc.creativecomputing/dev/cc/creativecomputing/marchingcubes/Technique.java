package cc.creativecomputing.marchingcubes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public abstract class Technique implements TriangleTable, TriStrip{
	
	

	protected Config	m_config;

	public Technique( Config config )
	{m_config = config;}


	public abstract void render();
	
	public abstract String info();

	public abstract void reconfigure();

	

}