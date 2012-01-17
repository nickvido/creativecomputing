package cc.creativecomputing.simulation.gpuparticles.forces;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticles;
import cc.creativecomputing.simulation.gpuparticles.CCGPUVelocityShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;

public class CCGPUTimeForceBlend extends CCGPUForce{
	
	public static enum CCGPUTimeMode{
		TIME(0), LIFE_TIME(1);
		
		private float _myValue;
		
		private CCGPUTimeMode(final float theValue) {
			_myValue = theValue;
		}
	}
	
	public static enum CCGPUPlayMode{
		FORWARD(0), BACKWARD(1);
		
		private float _myValue;
		
		private CCGPUPlayMode(final float theValue) {
			_myValue = theValue;
		}
	}
	
	private CGparameter _myMinBlendParameter;
	private CGparameter _myMaxBlendParameter;
	
	private float _myMinBlend;
	private float _myMaxBlend;
	
	private CGparameter _myStartTimeParameter;
	private CGparameter _myEndTimeParameter;
	
	private float _myStartTime;
	private float _myEndTime;
	
	private CGparameter _myPowerParameter;

	private float _myPower;
	
	private CGparameter _myTimeModeParameter;
	private CGparameter _myPlayModeParameter;
	
	private CCGPUTimeMode _myTimeMode;
	private CCGPUPlayMode _myPlayMode;
	
	private CCGPUForce _myForce1;
	private CCGPUForce _myForce2;
	
	public CCGPUTimeForceBlend(
		final float theStartTime,
		final float theEndTime,
		final CCGPUForce theForce1,
		final CCGPUForce theForce2
	){
		super("TimeForceBlend");
		
		_myStartTime = theStartTime;
		_myEndTime = theEndTime;
		_myMinBlend = 0;
		_myMaxBlend = 1;
		_myPower = 1;
		
		_myForce1 = theForce1;
		_myForce2 = theForce2;
		
		_myTimeMode = CCGPUTimeMode.TIME;
		_myPlayMode = CCGPUPlayMode.FORWARD;
	}

	@Override
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, int theIndex, final int theWidth, final int theHeight) {
		_myVelocityShader = theShader;
		_myParameterIndex = "forces["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(theWidth, theHeight);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce1.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
		_myForce2.setShader(theParticles,theShader, _myParameterIndex + ".force2",theWidth, theHeight);
	}
	
	@Override
	public void setShader(CCGPUParticles theParticles, CCGPUVelocityShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		setupParameter(0, 0);
		_myVelocityShader.checkError("Problem creating force.");
		
		_myForce1.setShader(theParticles,theShader, _myParameterIndex + ".force1",theWidth, theHeight);
		_myForce2.setShader(theParticles,theShader, _myParameterIndex + ".force2",theWidth, theHeight);
	}
	
	@Override
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		_myStartTimeParameter = parameter("start");
		_myEndTimeParameter = parameter("end");
		
		_myMinBlendParameter = parameter("minBlend");
		_myMaxBlendParameter = parameter("maxBlend");
		
		_myPowerParameter = parameter("power");
		
		_myTimeModeParameter = parameter("timeMode");
		_myPlayModeParameter = parameter("backward");
	}

	@Override
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		_myForce1.setSize(theG, theWidth, theHeight);
		_myForce2.setSize(theG, theWidth, theHeight);
	}

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myVelocityShader.parameter(_myStartTimeParameter, _myStartTime);
		_myVelocityShader.parameter(_myEndTimeParameter, _myEndTime);
		
		_myVelocityShader.parameter(_myMinBlendParameter, _myMinBlend);
		_myVelocityShader.parameter(_myMaxBlendParameter, _myMaxBlend);
		
		_myVelocityShader.parameter(_myPowerParameter, _myPower);
		
		_myVelocityShader.parameter(_myTimeModeParameter, _myTimeMode._myValue);
		_myVelocityShader.parameter(_myPlayModeParameter, _myPlayMode._myValue);
		
		_myForce1.update(theDeltaTime);
		_myForce2.update(theDeltaTime);
	}
	
	public void startTime(final float theStartTime) {
		_myStartTime = theStartTime;
	}
	
	public void endTime(final float theEndTime) {
		_myEndTime = theEndTime;
	}
	
	public void blend(final float theMinBlend, final float theMaxBlend) {
		_myMinBlend = theMinBlend;
		_myMaxBlend = theMaxBlend;
	}
	
	public void blend(final float theMaxBlend) {
		_myMinBlend = 0;
		_myMaxBlend = theMaxBlend;
	}
	
	public void power(final float thePower) {
		_myPower = thePower;
	}
	
	public void timeMode(final CCGPUTimeMode theTimeMode) {
		_myTimeMode = theTimeMode;
	}
	
	public void playMode(final CCGPUPlayMode thePlayMode) {
		_myPlayMode = thePlayMode;
	}
}
