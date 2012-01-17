/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCIcoSphere;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.simulation.gpuparticles.CCGPUParticle;
import cc.creativecomputing.simulation.gpuparticles.CCGPUQueueParticles;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUAnchoredSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUDampedSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForceField;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUSprings;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetForce;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUTargetMaskSetup;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUViscousDrag;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.util.CCFormatUtil;

public class CCParticleSphereDemo extends CCApp {
private CCGLSLShader _myGLSLShader;
	
//	static class Colors{
//	@CCControl(name = "back red", min = 0, max = 1)
//	private static float bred = 0;
//	@CCControl(name = "back green", min = 0, max = 1)
//	private static float bgreen = 0;
//	@CCControl(name = "back blue", min = 0, max = 1)
//	private static float bblue = 0;

	@CCControl(name = "red", min = 0, max = 1)
	private static float red = 0;
	@CCControl(name = "green", min = 0, max = 1)
	private static float green = 0;
	@CCControl(name = "blue", min = 0, max = 1)
	private static float blue = 0;
	
	@CCControl(name = "ambient red", min = 0, max = 1)
	private static float ared = 0;
	@CCControl(name = "ambient green", min = 0, max = 1)
	private static float agreen = 0;
	@CCControl(name = "ambient blue", min = 0, max = 1)
	private static float ablue = 0;

	
//	@CCControl(name = "specular red", min = 0, max = 1)
//	private static float sred = 0;
//	@CCControl(name = "specular green", min = 0, max = 1)
//	private static float sgreen = 0;
//	@CCControl(name = "specular blue", min = 0, max = 1)
//	private static float sblue = 0;
//	@CCControl(name = "shininess", min = 0, max = 10)
//	private static float shininess = 0;
//	@CCControl(name = "normalScale", min = 0, max = 1)
//	private static float normalScale = 0;
	

	@CCControl(name = "x", min = -1, max = 1)
	private static float x = 0;
	@CCControl(name = "y", min = -1, max = 1)
	private static float y = 0;
	@CCControl(name = "z", min = -1, max = 1)
	private static float z = 0;
//	}
	
	private CCIcoSphere _mySphere;
	private CCArcball _myArcball;
	
	private CCGPUQueueParticles _myParticles;
	private CCGPUParticlePointRenderer _myRenderer;
	private CCGPUAnchoredSprings _myAnchoredSprings;
	
	private CCGPUForceField _myForceField = new CCGPUForceField(0.005f,1,new CCVector3f(100,20,30));
	private CCGPUTargetForce _myTargetForce = new CCGPUTargetForce();
	private CCGPUTargetMaskSetup _myTargetMaskSetup;
	
	@CCControl(name = "spring strength", min = 0, max = 1)
	private float _cSpringStrength = 0;
	
	@CCControl(name = "spring damping", min = 0, max = 1)
	private float _cSpringDamping = 0;
	
	@CCControl(name = "noise strength", min = 0, max = 1)
	private float _cNoiseStrength = 0;
	
	@CCControl(name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 0;
	
	@CCControl(name = "noise speed", min = 0, max = 1)
	private float _cNoiseSpeed = 0;
	
	@CCControl(name = "blend", min = 0, max = 1)
	private float _cBlend = 0;
	

	@Override
	public void setup() {
		_mySphere = new CCIcoSphere(new CCVector3f(), 200, 6);
		System.out.println(_mySphere.vertices().size());
		List<CCGPUForce> myForces = new ArrayList<CCGPUForce>();
		myForces.add(_myForceField);
		myForces.add(new CCGPUViscousDrag(0.2f));
		myForces.add(_myAnchoredSprings = new CCGPUAnchoredSprings(g,4f,0,10f));
		myForces.add(_myTargetForce);
		List<CCGPUConstraint> myConstraints = new ArrayList<CCGPUConstraint>();
		
		_myRenderer = new CCGPUParticlePointRenderer();
		_myParticles = new CCGPUQueueParticles(g,_myRenderer,myForces,myConstraints,300,300);
		_myArcball = new CCArcball(this);

		_myTargetMaskSetup = new CCGPUTargetMaskSetup(CCTextureIO.newTextureData("demo/particles/texone.png"),1);
		_myTargetForce.addTargetSetup(_myTargetMaskSetup);
		Map<Integer, List<Integer>> myIndexMap = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < _mySphere.indices().size();i+=3) {
			int i0 = _mySphere.indices().get(i);
			int i1 = _mySphere.indices().get(i + 1);
			int i2 = _mySphere.indices().get(i + 1);
			if(!myIndexMap.containsKey(i0))myIndexMap.put(i0, new ArrayList<Integer>());
			if(!myIndexMap.containsKey(i1))myIndexMap.put(i1, new ArrayList<Integer>());
			if(!myIndexMap.containsKey(i2))myIndexMap.put(i2, new ArrayList<Integer>());
			
			myIndexMap.get(i0).add(i1);
			myIndexMap.get(i0).add(i2);
			myIndexMap.get(i1).add(i0);
			myIndexMap.get(i1).add(i2);
			myIndexMap.get(i2).add(i0);
			myIndexMap.get(i2).add(i1);
		}
		
		List<CCGPUParticle> myParticles = new ArrayList<CCGPUParticle>();
		List<CCVector4f> myTextureCoords = new ArrayList<CCVector4f>();
		
		for(CCVector3f myVertex:_mySphere.vertices()) {
			CCGPUParticle myParticle = _myParticles.allocateParticle(
				myVertex,
				new CCVector3f(),
				3000, true
			);
			myParticles.add(myParticle);
			_myAnchoredSprings.addSpring(myParticle, myVertex);
			myTextureCoords.add(new CCVector4f());
		}
		
//		for(int myKey: myIndexMap.keySet()) {
//			List<Integer> myIndices = myIndexMap.get(myKey);
//			CCGPUParticle myParticle1 = myParticles.get(myKey);
//			CCVector3f myP1 = _mySphere.vertices().get(myKey);
//			for(int myIndex:myIndices) {
//				CCGPUParticle myParticle2 = myParticles.get(myIndex);
//				CCVector3f myP2 = _mySphere.vertices().get(myIndex);
//			}
//			
//			CCGPUParticle myParticleA = myParticles.get(myIndices.get(0));
//			CCGPUParticle myParticleB = myParticles.get(myIndices.get(1));
//			
//			CCVector4f myTexCoords = myTextureCoords.get(myKey);
//			myTexCoords.x = myParticleA.x();
//			myTexCoords.y = myParticleA.y();
//			myTexCoords.z = myParticleB.x();
//			myTexCoords.w = myParticleB.y();
//		}
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "app", this);
		
//		_myRenderer.mesh().drawMode(CCDrawMode.TRIANGLES);
//		_myRenderer.mesh().indices(_mySphere.indices());
//		_myRenderer.mesh().textureCoords(0, myTextureCoords);
//		
//		_myGLSLShader = new CCGLSLShader(
//			CCIOUtil.classPath(this, "shader/triangles_vertex.glsl"), 
//			CCIOUtil.classPath(this, "shader/triangles_fragment.glsl")
//		);
//		_myGLSLShader.load();
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * _cNoiseSpeed;
		float myBlend = _cBlend;
		float myBlend2 = 1 - myBlend;
		_myTargetForce.strength(myBlend);
		
		_myForceField.noiseOffset(new CCVector3f(_myTime*0.5f,0,0));
		_myForceField.strength(_cNoiseStrength * myBlend2);
		_myForceField.noiseScale(_cNoiseScale * 0.01f);
		_myAnchoredSprings.strength(_cSpringStrength * myBlend2);
		_myAnchoredSprings.springDamping(_cSpringDamping);
		
		_myParticles.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		_myArcball.draw(g);

		g.noDepthTest();
		g.color(1f, 0.1f);
		g.blend(CCBlendMode.ADD);
		_myParticles.draw();
//		_myGLSLShader.start();
//		_myGLSLShader.uniform1i("positionTexture", 0);
//		_myGLSLShader.uniform("diffuse", new CCColor(red, green ,blue));
//		_myGLSLShader.uniform("ambient", new CCColor(ared, agreen ,ablue));
//		_myGLSLShader.uniform3f("lightDir", new CCVector3f(x,y,z).normalize());
//		g.texture(_myParticles.positions());
//		_myRenderer.mesh().draw(g);
//		g.noTexture();
//		_myGLSLShader.end();
	}
	
	private int i = 0;
	
	public void keyPressed(CCKeyEvent theEvent) {
		switch(theEvent.getKeyCode()){
		case CCKeyEvent.VK_R:
			_myParticles.reset();
			break;
		case CCKeyEvent.VK_S:
			CCScreenCapture.capture("export/db03/"+CCFormatUtil.nf(i++, 4)+".png", width, height);
			break;
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCParticleSphereDemo.class);
		myManager.settings().size(800, 600);
		myManager.start();
	}
}

