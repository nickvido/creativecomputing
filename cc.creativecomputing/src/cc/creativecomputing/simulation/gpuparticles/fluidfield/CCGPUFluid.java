/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.simulation.gpuparticles.fluidfield;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCShaderTexture;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCVector2f;

/**
 * @author christianriekoff
 *
 */
public class CCGPUFluid {
	private CCGPUFluidBoundaryShader _myBoundaryShader;
	private CCShaderTexture _myBoundaryTexture;
	
	private CCGPUFluidAdvectShader _myAdvectShader;
	
	private CCGPUFluidAddImpulseShader _myAddImpulseShader;
	
	private CCGPUFluidAddColorShader _myAddColorShader;
	
	private CCGPUFluidVorticityShader _myVorticityShader;
	private CCShaderTexture _myVorticityTexture;
	
	private CCGPUFluidVorticityForceShader _myVorticityForceShader;
	private CCShaderTexture _myVorticityForceTexture;
	
	private CCGPUFluidDiffusionShader _myDiffusionShader;
	private float _myViscousity = 0.5f;
	private float _myDiffusionSteps = 5;
	
	private float _myColorDarking = 0;
	private float _myAdvectSpeed = 1;
	
	private CCGPUFluidDivergenceShader _myDivergenceShader;
	private CCShaderTexture _myDivergencyTexture;
	
	private CCGPUFluidSubtractGradientShader _mySubtractGradientShader;
	
	private CCAABoundingRectangle _myOffBoundaryRect;
	
	private CCShaderTexture _myVelocityTexture;
	private CCShaderTexture _myTmpVelocityTexture;
	
	private CCShaderTexture _myColorTexture;
	private CCShaderTexture _myTmpColorTexture;
	
	private CCShaderTexture _myPressureTexture;
	private CCShaderTexture _myTmpPressureTexture;
	
	
	private float _myGridScale = 1;
	
	
	private CCGraphics g;
	private int _myWidth;
	private int _myHeight;
	
	public CCGPUFluid(CCGraphics g, final int theWidth, final int theHeight) {
		this.g = g;
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myOffBoundaryRect = new CCAABoundingRectangle(1, 1, _myWidth - 1, _myHeight - 1);
		
		_myVelocityTexture = new CCShaderTexture(_myWidth, _myHeight);
		_myTmpVelocityTexture = new CCShaderTexture(_myWidth, _myHeight);
		
		_myColorTexture = new CCShaderTexture(_myWidth, _myHeight);
		_myColorTexture.textureFilter(CCTextureFilter.LINEAR);
		_myTmpColorTexture = new CCShaderTexture(_myWidth, _myHeight);
		_myTmpColorTexture.textureFilter(CCTextureFilter.LINEAR);
		
		_myBoundaryShader = new CCGPUFluidBoundaryShader(g);
		_myBoundaryTexture = new CCShaderTexture(32, 3,_myWidth,_myHeight);
		
		_myAdvectShader = new CCGPUFluidAdvectShader(g);
		_myAdvectShader.dissipation(1);
		_myAdvectShader.gridScale(1);
		
		_myAddImpulseShader = new CCGPUFluidAddImpulseShader(g);
		_myAddImpulseShader.windowDimension(_myWidth, _myHeight);
		_myAddImpulseShader.radius(0.1f);
		
		_myAddColorShader = new CCGPUFluidAddColorShader(g);
		_myAddColorShader.windowDimension(_myWidth, _myHeight);
		_myAddColorShader.radius(0.1f);
		
		_myVorticityShader = new CCGPUFluidVorticityShader(g);
		_myVorticityShader.gridScale(1f);
		_myVorticityTexture = new CCShaderTexture(_myWidth, _myHeight);
		
		_myVorticityForceShader = new CCGPUFluidVorticityForceShader(g);
		_myVorticityForceShader.gridScale(1);
		_myVorticityForceTexture = new CCShaderTexture(32, 3,_myWidth,_myHeight);
		
		_myDiffusionShader = new CCGPUFluidDiffusionShader(g);
		
		_myDivergenceShader = new CCGPUFluidDivergenceShader(g);
		_myDivergenceShader.halfRdx(0.5f);
		_myDivergencyTexture = new CCShaderTexture(32, 3,_myWidth,_myHeight);
		
		_mySubtractGradientShader = new CCGPUFluidSubtractGradientShader(g);
		_mySubtractGradientShader.halfRdx(0.5f);
		
		_myPressureTexture = new CCShaderTexture(32, 3,_myWidth,_myHeight);
		_myTmpPressureTexture = new CCShaderTexture(32, 3,_myWidth,_myHeight);
	}
	
	public void colorRadius(final float theColorRadius) {
		_myAddColorShader.radius(theColorRadius);
	}
	
	public void impulseRadius(final float theImpulseRadius) {
		_myAddImpulseShader.radius(theImpulseRadius);
	}
	
	public void viscousity(final float theViscousity) {
		_myViscousity = theViscousity;
	}
	
	private void swapVelocities() {
		CCShaderTexture myTmpTexture = _myVelocityTexture;
		_myVelocityTexture = _myTmpVelocityTexture;
		_myTmpVelocityTexture = myTmpTexture;
	}
	
	private void swapColors() {
		CCShaderTexture myTmpTexture = _myColorTexture;
		_myColorTexture = _myTmpColorTexture;
		_myTmpColorTexture = myTmpTexture;
	}
	
	private void swapPressure() {
		CCShaderTexture myTmpTexture = _myPressureTexture;
		_myPressureTexture = _myTmpPressureTexture;
		_myTmpPressureTexture = myTmpTexture;
	}
	
	public void adImpulse(final CCVector2f thePosition, final CCVector2f theDirection) {
		_myAddImpulseShader.baseTexture(_myVelocityTexture);
		_myAddImpulseShader.start();
		_myAddImpulseShader.position(thePosition);
		_myAddImpulseShader.color(theDirection.x, theDirection.y,0);
		_myTmpVelocityTexture.draw();
		_myAddImpulseShader.end();
		
		swapVelocities();
	}
	
	public void addColor(final CCVector2f thePosition, final CCColor theColor) {

			_myAddColorShader.baseTexture(_myColorTexture);
			_myAddColorShader.start();
			_myAddColorShader.position(thePosition);
			_myAddColorShader.color(theColor);
			_myTmpColorTexture.draw();
			_myAddColorShader.end();
			
			swapColors();
	}
	
	/**
	 * This renders the four 1-pixel wide boundaries of the slab.
	 * texcoord 0 is the base texture coordinate.
	 * texcoord 1 is the offset 
	 * (done here to avoid proliferation of fragment 
	 * programs, or of conditionals inside the fragment program.)
	 */
	private void drawBoundaries() {
		g.beginShape(CCDrawMode.LINES);  
		
		// left boundary
		g.textureCoords(1, 1, 0); // offset amount
		g.textureCoords(0, 0, 0);
		g.vertex(1, 0);
		g.textureCoords(0, 0, _myHeight);
		g.vertex(1, _myHeight);

		// right boundary
		g.textureCoords(1, -1, 0); // offset amount
		g.textureCoords(0, _myWidth - 1, 0);
		g.vertex(_myWidth, 0);
		g.textureCoords(0, _myWidth - 1, _myHeight);
		g.vertex(_myWidth, _myHeight);

		// top boundary
		g.textureCoords(1, 0, 1); // offset amount
		g.textureCoords(0, 0, 0);
		g.vertex(0, 1);
		g.textureCoords(0, _myWidth, 0);
		g.vertex(_myWidth, 1);

		// bottom boundary
		g.textureCoords(1, 0, -1); // offset amount
		g.textureCoords(0, 0, _myHeight - 1);
		g.vertex(0, _myHeight);
		g.textureCoords(0, _myWidth, _myHeight-1);
		g.vertex(_myWidth, _myHeight);
		g.endShape();

	}
	
	public void advectSpeed(final float theAdvectSpeed) {
		_myAdvectSpeed = theAdvectSpeed;
	}
	
	/**
	 * pulls the velocity and Ink fields forward along the velocity field.
	 * This results in a divergent field, which must be corrected using diffusion.
	 * @param theDeltaTime
	 */
	private void advectVelocities(final float theDeltaTime) {
		_myBoundaryShader.texture(_myVelocityTexture);
		_myBoundaryShader.start();
		_myBoundaryShader.scale(-1);
		_myTmpVelocityTexture.beginDraw();
		drawBoundaries();
		_myTmpVelocityTexture.endDraw();
		_myBoundaryShader.end();

		_myAdvectShader.targetTexture(_myVelocityTexture);
		_myAdvectShader.velocityTexture(_myVelocityTexture);
		_myAdvectShader.start();
		_myAdvectShader.dissipation(1);
		_myAdvectShader.timeStep(theDeltaTime * _myAdvectSpeed);
		_myTmpVelocityTexture.draw(_myOffBoundaryRect);
		_myAdvectShader.end();
		
		swapVelocities();
	}
	
	public void colorDarking(final float theColorDarking) {
		_myColorDarking = theColorDarking;
	}
	
	private void advectColors(final float theDeltaTime) {
		_myBoundaryShader.texture(_myColorTexture);
		_myBoundaryShader.start();
		_myBoundaryShader.scale(0);
		_myTmpColorTexture.beginDraw();
		drawBoundaries();
		_myTmpColorTexture.endDraw();
		_myBoundaryShader.end();

		_myAdvectShader.targetTexture(_myColorTexture);
		_myAdvectShader.velocityTexture(_myVelocityTexture);
		_myAdvectShader.start();
		_myAdvectShader.dissipation(1);
		_myAdvectShader.timeStep(theDeltaTime * _myAdvectSpeed);
		_myAdvectShader.darking(_myColorDarking);
		_myTmpColorTexture.draw(_myOffBoundaryRect);
		_myAdvectShader.darking(0);
		_myAdvectShader.end();
		
		swapColors();
	}
	
	/**
	 * Applies vorticity confinement
	 * @param theDeltaTime
	 */
	private void vorticity(final float theDeltaTime) {
		_myVorticityShader.velocityTexture(_myVelocityTexture);
		_myVorticityShader.start();
		_myVorticityTexture.draw();
		_myVorticityShader.end();

		_myBoundaryShader.texture(_myVelocityTexture);
		_myBoundaryShader.start();
		_myBoundaryShader.scale(-1);
		_myTmpVelocityTexture.beginDraw();
		drawBoundaries();
		_myTmpVelocityTexture.endDraw();
		_myBoundaryShader.end();

		_myVorticityForceShader.velocityTexture(_myVelocityTexture);
		_myVorticityForceShader.vorticityTexture(_myVorticityTexture);
		_myVorticityForceShader.start();
		_myVorticityForceShader.deltaTime(theDeltaTime);
		_myTmpVelocityTexture.draw();
		_myVorticityForceShader.end();
		
		swapVelocities();
	}
	
	/**
	 * If this is a viscous fluid, solve the poisson problem for the viscous diffusion
	 * @param theDeltaTime
	 */
	private void diffuseVelocity(final float theDeltaTime) {
		if(_myViscousity <= 0)return;

		float myCenterFactor = _myGridScale * _myGridScale / (_myViscousity * theDeltaTime);
		float myStencilFactor = 1.0f / (4.0f + myCenterFactor);

		for (int i = 0; i < _myDiffusionSteps; i++) {
			_myDiffusionShader.textureX(_myVelocityTexture);
			_myDiffusionShader.textureB(_myVelocityTexture);
			_myDiffusionShader.start();
			_myDiffusionShader.alpha(myCenterFactor);
			_myDiffusionShader.rBeta(myStencilFactor);
			_myTmpVelocityTexture.draw(_myOffBoundaryRect);
			_myDiffusionShader.end();
		
			swapVelocities();
		}
	}
	
	/**
	 * Compute the divergence of the velocity field
	 */
	private void divergence() {
		// ---------------
//		// 4a. compute divergence
//		// ---------------
//		// Compute the divergence of the velocity field
		_myDivergenceShader.velocityTexture(_myVelocityTexture);
		_myDivergenceShader.start();
		_myDivergencyTexture.draw(_myOffBoundaryRect);
		_myDivergenceShader.end();
	}
	
	/**
	 * Compute pressure disturbance
	 */
	public void pressure() {
		// Clear the pressure texture, to initialize the pressure disturbance to 
		// zero before iterating.  If this is disabled, the solution converges 
		// faster, but tends to have oscillations.
//		if (_bClearPressureEachStep)
//			_myPressureTexture.clear();

		for (int i = 0; i < _myDiffusionSteps; i++) {
			// Apply pure neumann boundary conditions
			_myBoundaryShader.texture(_myPressureTexture);
			_myBoundaryShader.start();
			_myBoundaryShader.scale(1);
			_myTmpPressureTexture.beginDraw();
			drawBoundaries();
			_myTmpPressureTexture.endDraw();
			_myBoundaryShader.end();

			// Apply pure neumann boundary conditions to arbitrary
			// interior boundaries if they are enabled.
//			if (_bArbitraryBC) {
//				_myArbitaryPressureBoundaryShader.start();
//				_myArbitaryPressureBoundaryShader.pressureTexture(_myPressureTexture);
//				_myArbitaryPressureBoundaryShader.offsetTexture(_myPressureOffsetTexture);
//				_myTmpPressureTexture.draw(_myOffBoundaryRect);
//				_myArbitaryPressureBoundaryShader.end();
//
//				mySwapTexture = _myTmpPressureTexture;
//				_myTmpPressureTexture = _myPressureTexture;
//				_myPressureTexture = mySwapTexture;
//			}

			// Solve for the pressure disturbance caused by the divergence, by solving
			// the poisson problem Laplacian(p) = div(u)
			_myDiffusionShader.textureX(_myPressureTexture);
			_myDiffusionShader.textureB(_myDivergencyTexture);
			_myDiffusionShader.start();
			_myDiffusionShader.alpha(-_myGridScale * _myGridScale);
			_myDiffusionShader.rBeta(0.25f);
			_myTmpPressureTexture.draw(_myOffBoundaryRect);
			_myDiffusionShader.end();

			swapPressure();
		}
	}
	
	public void subtractGradient() {
		_mySubtractGradientShader.pressureTexture(_myPressureTexture);
		_mySubtractGradientShader.velocityTexture(_myVelocityTexture);
		_mySubtractGradientShader.start();
		_myTmpVelocityTexture.draw(_myOffBoundaryRect);
		_mySubtractGradientShader.end();
		
		swapVelocities();
	}

	/**
	 * 
	 * Update solves the incompressible Navier-Stokes equations for a single 
	 * time step.  It consists of four main steps:
	 *
	 * 1. Add Impulse
	 * 2. Advect
	 * 3. Apply Vorticity Confinement
	 * 4. Diffuse (if viscosity > 0)
	 * 5. Project (computes divergence-free velocity from divergent field).
	 *
	 * 
	 * 2. Add Impulse: simply add an impulse to the velocity (and optionally,Ink)
	 *    field where the user has clicked and dragged the mouse.
	 * 3. Apply Vorticity Confinement: computes the amount of vorticity in the 
	 *            flow, and applies a small impulse to account for vorticity lost 
	 *            to numerical dissipation.
	 * 4. Diffuse: viscosity causes the diffusion of velocity.  This step solves
	 *             a poisson problem: (I - nu*dt*Laplacian)u' = u.
	 * 5. Project: In this step, we correct the divergence of the velocity field
	 *             as follows.
	 *        a.  Compute the divergence of the velocity field, div(u)
	 *        b.  Solve the Poisson equation, Laplacian(p) = div(u), for p using 
	 *            Jacobi iteration.
	 *        c.  Now that we have p, compute the divergence free velocity:
	 *            u = gradient(p)
	 */ 
	public void update(final float theDeltaTime) {
		
		advectVelocities(theDeltaTime);
		advectColors(theDeltaTime);
//		vorticity(theDeltaTime);
		diffuseVelocity(theDeltaTime);
		divergence();
		pressure();
//		subtractGradient();
	}
	
	public CCShaderTexture velocityTexture() {
		return _myVelocityTexture;
	}
	
	public CCShaderTexture colorTexture() {
		return _myColorTexture;
	}
}
