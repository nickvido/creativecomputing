package cc.creativecomputing.demo.topic.interaction;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCSimplexNoise;
import cc.creativecomputing.math.util.CCMotionLimiter;
import cc.creativecomputing.util.CCFormatUtil;

public class CCLimitedMotionDemo extends CCApp {
	
	public static class CCValueBuffer<ValueType> implements Iterable<ValueType>{
		
		private LinkedList<ValueType> _myValues;
		
		private int _myBufferSize;
		
		public CCValueBuffer(int theBufferSize) {
			_myValues = new LinkedList<ValueType>();
			_myBufferSize = theBufferSize;
		}
		
		public void add(ValueType theValue) {
			if(_myValues.size() >= _myBufferSize)_myValues.removeFirst();
			_myValues.add(theValue);
		}

		@Override
		public Iterator<ValueType> iterator() {
			return _myValues.iterator();
		}
	}

	@CCControl(name = "speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	
	private float _myNoiseOffset = 0;

	private float _myTarget;
	private float _myLimitedPosition;
	
	private CCSimplexNoise _myNoise;
	private CCMotionLimiter _myLimiter;
	
	private CCValueBuffer<Float> _myTargetBuffer;
	private CCValueBuffer<Float> _myPositionBuffer;
	private CCValueBuffer<Float> _myFuturePositionBuffer;
	private CCValueBuffer<Float> _myVelocityBuffer;
	private CCValueBuffer<Float> _myAccelerationBuffer;
	
	@Override
	public void setup() {
		addControls("app", "app", this);

		_myTargetBuffer = new CCValueBuffer<Float>(height);
		_myPositionBuffer = new CCValueBuffer<Float>(height);
		_myVelocityBuffer = new CCValueBuffer<Float>(height);
		_myAccelerationBuffer = new CCValueBuffer<Float>(height);
		_myFuturePositionBuffer = new CCValueBuffer<Float>(height);
		
		_myLimiter = new CCMotionLimiter();
		addControls("app", "limiter", _myLimiter);
		frameRate(60);
		
		g.textFont(CCFontIO.createTextureMapFont("arial", 12));
	}
	
	
	private float target(float theTime){
		return (_myNoise.value(theTime) - 0.5f) * width;
	}

	@Override
	public void update(final float theDeltaTime) {
		_myNoise = new CCSimplexNoise();
		_myNoiseOffset += theDeltaTime * _cNoiseSpeed;
		
		_myTarget = target(_myNoiseOffset);
		_myLimitedPosition = _myLimiter.limit2(_myTarget, theDeltaTime);

		_myTargetBuffer.add(_myTarget);
		_myPositionBuffer.add(_myLimitedPosition);
		_myFuturePositionBuffer.add((float)_myLimiter.futurePosition());
		_myVelocityBuffer.add((float)_myLimiter.velocity() / _myLimiter._cMaxVelocity);
		_myAccelerationBuffer.add((float)_myLimiter.acceleration() / _myLimiter._cMaxAcceleration);
	}
	
	private void drawBuffer(CCValueBuffer<Float> theBuffer, float theScale) {
		int i = -height / 2;
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(float myPosition:theBuffer) {
			g.vertex(myPosition * theScale, i++);
		}
		g.endShape();
	}
	
	private void drawInfo(float theX, float theY, float theValue, String theLabel) {
		g.line(theValue, -height / 2, theValue, height / 2);
		g.rect(theX, theY, 10,10);
		g.text(CCFormatUtil.nd(theValue,3,3) + " " + theLabel, theX + 15, theY);
	}
	
	@Override
	public void draw() {
		g.clear();
		
		int y = 0;
		g.color(255,255,0);
		drawInfo(-width/2 + 10, height/2 - (y -= 20), _myTarget, "target");
		
		g.color(255);
		drawInfo(-width/2 + 10, height/2 - (y -= 20), _myLimitedPosition, "limited position");
		
		g.color(0,255,255);
		drawInfo(-width/2 + 10, height/2 - (y -= 20), (float)_myLimiter.futurePosition(), "future position");
		
		g.color(255);
		drawBuffer(_myPositionBuffer, 1);

		g.color(255,0,0);
		drawBuffer(_myVelocityBuffer, width / 3);

		g.color(0,255,0);
		drawBuffer(_myAccelerationBuffer, width / 3);
		
		g.color(0,0,255);
		drawBuffer(_myTargetBuffer, 1);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCLimitedMotionDemo.class);
		myManager.settings().size(1400, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

