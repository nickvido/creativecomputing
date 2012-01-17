package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.CCGPUVelocityShader;

import com.jogamp.opengl.cg.CGparameter;


public class CCGPUCurveField extends CCGPUForce{

	private float _myAngle = 0;
	private float _myOffset = CCMath.random(30);
	
	private float _myCurveSpeed = 1;
	private float _myCurveOutputScale = 1;
	private float _myCurveRadius = 1;
	
	private CGparameter _myPredictionParameter;
	
	private CGparameter _myCurveNoiseOffsetParameter;
	private CGparameter _myCurveScaleParameter;
	private CGparameter _myCurveScale;
	private CGparameter _myRadiusParameter;
	
	public CCGPUCurveField(final float theNoiseScale, final float theStrength){
		super("CurveForceFieldFollow");
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myPredictionParameter = parameter("prediction");

		_myCurveNoiseOffsetParameter = parameter("curveOffset");
		_myCurveScaleParameter = parameter("curveScale");
		_myCurveScale = parameter("curveOutputScale");
		_myRadiusParameter = parameter("radius");
	}
	
	public void prediction(final float thePrediction) {
		_myVelocityShader.parameter(_myPredictionParameter, thePrediction);
	}

	public void curveSpeed(float noiseSpeed) {
		_myCurveSpeed = noiseSpeed;
	}

	public void curveScale(float theNoiseScale) {
		_myVelocityShader.parameter(_myCurveScaleParameter, theNoiseScale);
	}

	public void curveOutputScale(float noiseOutputScale) {
		_myCurveOutputScale = noiseOutputScale;
	}
	
	public void noiseRadius(float noiseCurveRadius) {
		_myCurveRadius = noiseCurveRadius;
		_myVelocityShader.parameter(_myRadiusParameter, _myCurveRadius );
	}


	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myAngle += theDeltaTime;
		_myOffset += theDeltaTime * _myCurveSpeed;
		
		_myVelocityShader.parameter(_myCurveNoiseOffsetParameter, _myOffset);
		_myVelocityShader.parameter(_myCurveScale, _myCurveOutputScale);
		_myVelocityShader.parameter(_myRadiusParameter, _myCurveRadius);
	}
	
}
