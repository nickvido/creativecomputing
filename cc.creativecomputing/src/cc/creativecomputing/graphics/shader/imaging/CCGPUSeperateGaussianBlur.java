package cc.creativecomputing.graphics.shader.imaging;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderTexture;
import cc.creativecomputing.math.CCMath;

public class CCGPUSeperateGaussianBlur extends CCGPUConvolutionShader{
	
	private int _myRows;
	private int _myIntRadius;
	private float[] _myMatrix;
	
	private CCRenderTexture _myTexture1;
	private CCRenderTexture _myTexture2;
	
	private int _myWidth;
	private int _myHeight;

	/**
	 * Creates a new GaussianBlur. Instead of applying a full size two dimensional kernel.
	 * The blur is applied using a separate vertical and horizontal kernel. This way the
	 * amount of calculations per pixel is reduced from <code>kernelSize * kernelSize</code>
	 * to <code>kernelSize + kernelSize</code>. The radius of the blur is variable. You have to
	 * however define a maximum radius because the size of the array keeping the kernel on the
	 * shaderside can not be changed at runtime.
	 * @param theGraphics reference to the graphics object
	 * @param theMaximumRadius the maximum possible blur radius
	 * @param theWidth width for the render texture used by the blur
	 * @param theHeight height for the render texture used by the blur
	 */
	public CCGPUSeperateGaussianBlur(CCGraphics theGraphics, float theMaximumRadius, final int theWidth, final int theHeight) {
		super(theGraphics);

		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myIntRadius = CCMath.ceil(theMaximumRadius);
		_myRows = _myIntRadius * 2+1;
		_myMatrix = new float[_myRows];
		
		setKernel(calculateKernel(theMaximumRadius), _myRows, 1);
	}
	
	/**
	 * Creates a new GaussianBlur. 
	 * @param theGraphics reference to the graphics object
	 * @param theMaximumRadius the maximum possible blur radius
	 */
	public CCGPUSeperateGaussianBlur(CCGraphics theGraphics, float theMaximumRadius) {
		this(theGraphics,theMaximumRadius, theGraphics.width, theGraphics.height);
	}
	
	/**
	 * Make a Gaussian blur kernel.
     * @param radius the blur radius
     * @return the kernel
	 */
	private List<Float> calculateKernel(final float theRadius){
		
		float sigma = theRadius/3;
		float sigma22 = 2*sigma*sigma;
		float sigmaPi2 = CCMath.TWO_PI * sigma;
		float sqrtSigmaPi2 = CCMath.sqrt(sigmaPi2);
		
		float radius2 = theRadius * theRadius;
		float total = 0;
		int index = 0;
		
		for (int row = -_myIntRadius; row <= _myIntRadius; row++) {
			float distance = row * row;
			
			if (distance > radius2)
				_myMatrix[index] = 0;
			else
				_myMatrix[index] = (float)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			
			total += _myMatrix[index];
			index++;
		}
		
		List<Float> myKernel = new ArrayList<Float>();
		
		for (int i = 0; i < _myRows; i++) 
			myKernel.add(_myMatrix[i] / total);
		
		return myKernel;
	}

	/**
	 * Changes the blur radius. The radius is clamped to the maximum blur value
	 * passed to the constructor. Be aware you have to define a maximum radius because
	 * the size of the array holding the kernel on the shaderside can not be changed
	 * at runtime. For a smaller radius kernel cells not needed will be filled with 0.
	 * @param theRadius
	 */
	public void radius(final float theRadius){
		updateKernel(calculateKernel(theRadius));
	}
	
	/**
	 * Call this method to start the blur. Everything drawn between the 
	 * <code>beginDraw()</code> and <code>endDraw()</code>
	 * will be blurred. 
	 */
	public void beginDraw(CCGraphics g){
		if(_myTexture1 == null){
			_myTexture1 = new CCRenderTexture(g, _myWidth, _myHeight);
			_myTexture2 = new CCRenderTexture(g, _myWidth, _myHeight);
			texture(_myTexture1);
		}
		_myTexture1.beginDraw();
		g.clear();
	}
	
	/**
	 * Call this method to end the first pass of the blur, this allows
	 * you too call the second pass while drawing into another texture.
	 * @param g
	 */
	public void endFirstPass(CCGraphics g) {
		_myTexture1.endDraw();
		
		_myTexture2.beginDraw();
		g.clear();
		start();
		
		g.image(_myTexture1, - _myWidth/2, -_myHeight/2);
		end();
		flipKernel();
		_myTexture2.endDraw();
	}
	
	/**
	 * Call this method to end the second pass of the blur, this allows
	 * you too call the second pass while drawing into another texture.
	 * @param g
	 */
	public void endSecondPass(CCGraphics g) {
		start();
		g.image(_myTexture2, - _myWidth/2, -_myHeight/2);
		end();
		flipKernel();
	}
	
	/**
	 * Call this method to end the blur. Everything drawn between the 
	 * <code>beginDraw()</code> and <code>endDraw()</code>
	 * will be blurred. 
	 */
	public void endDraw(CCGraphics g){
		endFirstPass(g);
		endSecondPass(g);
	}
}
