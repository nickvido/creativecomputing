package cc.creativecomputing.particles.forces.fluid;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.events.CCMouseEvent.CCMouseButton;
import cc.creativecomputing.events.CCMouseEvent.CCMouseEventType;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.opencl.CCOpenCL;

public class CCFluidSolverDemo extends CCApp {
	
	private CCFluidSolver _myFluidSolver;
	private CCTexture2D _myFluidColors;

	@Override
	public void setup() {
//		frameRate(10);
		CCOpenCL.printInfos();
		
		_myFluidSolver = new CCFluidSolver(400, 200);
		addControls("fluid", "fluid", _myFluidSolver);
		g.clearColor(255);
	}

	
	
	@Override
	public void update(final float theDeltaTime) {
		// solve fluid
		_myFluidSolver.update(0.2f);
		if(_myFluidColors == null)_myFluidColors = new CCTexture2D(_myFluidSolver.colorData());
		else _myFluidColors.data(_myFluidSolver.colorData());
	}

	@Override
	public void draw() {
		g.clear();
		g.translate(-width/2, -height/2);

		g.noBlend();
		g.color(255);
		g.image(_myFluidColors, 0,0, width, height);
		System.out.println(frameRate);
	}

	@Override
	public void mousePressed(CCMouseEvent theEvent) {
		updateLocation(theEvent);
	}

	@Override
	public void mouseDragged(CCMouseEvent theEvent) {
		updateLocation(theEvent);
	}

	private void updateLocation(CCMouseEvent theEvent) {
		// get index for fluid cell under mouse position
		CCVector2f myMouse = new CCVector2f(
			theEvent.x() / (float) width,	
			theEvent.y() / (float) height
		);

		// add density or velocity
		if (theEvent.button() == CCMouseButton.LEFT){
			CCColor myColor = new CCColor();
			myColor.setHSB((CCMath.sin(frameCount * 0.01f)+1) / 2, 1f, 1f);
			_myFluidSolver.addColor(myMouse, myColor, 50);
		}
		if (theEvent.eventType() == CCMouseEventType.MOUSE_DRAGGED) {
			_myFluidSolver.addVelocity(myMouse, theEvent.movement().clone().scale(5));
		}
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCFluidSolverDemo.class);
		myManager.settings().size(1600, 800);
		myManager.settings().antialiasing(8);
		myManager.settings().vsync(false);
		myManager.start();
	}
}
