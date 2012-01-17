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
package cc.creativecomputing.simulation.gpuparticles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.gpuparticles.constrains.CCGPUConstraint;
import cc.creativecomputing.simulation.gpuparticles.forces.CCGPUForce;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticlePointRenderer;
import cc.creativecomputing.simulation.gpuparticles.render.CCGPUParticleRenderer;

/**
 * @author christianriekoff
 *
 */
public class CCGPUQueueParticles extends CCGPUParticles{
	
	private static class CCGPUParticleData {
		private int index;
		private CCVector3f position;
		private CCVector3f velocity;
		private float lifeTime;
		private float isPermanent;
	}
	
	private static class CCStateChange implements Comparable<CCStateChange>{
		private final int _myIndex;
		private final boolean _myIsPermanent;
		private final float _myMinAge;
		
		private CCStateChange(final int theIndex, final boolean theIsPermanent, final float theMinAge) {
			_myIndex = theIndex;
			_myIsPermanent = theIsPermanent;
			_myMinAge = theMinAge;
		}
		
		public int compareTo(CCStateChange arg0) {
			if(_myIndex < arg0._myIndex)return -1;
			return 1;
		}
	}
	
	protected PriorityQueue<Integer> _myAvailableIndices;
	protected PriorityQueue<CCGPUParticle> _myActiveParticles;
	protected final CCGPUParticle[] _myActiveParticlesArray;
	protected List<CCGPUParticleData> _myParticleDatas = new ArrayList<CCGPUParticleData>();
	protected int _myAllocatedParticleCounter = 0;
	
	protected List<CCStateChange> _myStateChanges = new ArrayList<CCStateChange>();
	
	protected Map<Integer, CCVector3f> _myPositionUpdates = new HashMap<Integer, CCVector3f>();
	protected List<CCGPUParticle> _myLifetimeUpdates = new ArrayList<CCGPUParticle>();

	public CCGPUQueueParticles(CCGraphics theG, CCGPUParticleRenderer theRender, List<CCGPUForce> theForces, List<CCGPUConstraint> theConstraints, int theWidth, int theHeight) {
		super(theG, theRender, theForces, theConstraints, theWidth, theHeight);
		
		_myActiveParticlesArray = new CCGPUParticle[size()];
		for(int i = 0; i < _myActiveParticlesArray.length;i++) {
			_myActiveParticlesArray[i] = new CCGPUParticle(this);
		}
	}

	public CCGPUQueueParticles(CCGraphics g, List<CCGPUForce> theForces, List<CCGPUConstraint> theConstraints, int theWidth, int theHeight) {
		this(g, new CCGPUParticlePointRenderer(), theForces, theConstraints, theWidth, theHeight);
	}

	public CCGPUQueueParticles(CCGraphics g, List<CCGPUForce> theForces, List<CCGPUConstraint> theConstraints) {
		this(g,theForces, theConstraints,200,200);
	}

	public CCGPUQueueParticles(CCGraphics g, List<CCGPUForce> theForces) {
		this(g, theForces, new ArrayList<CCGPUConstraint>());
	}
	
	public int nextFreeId() {
		if (_myAvailableIndices.isEmpty())
			return -1;

		return _myAvailableIndices.peek();
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle allocateParticle(
		final int theIndex,
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		boolean theIsPermanent
	){
		CCGPUParticle myActiveParticle = _myActiveParticlesArray[theIndex];
		myActiveParticle.timeOfDeath(theIsPermanent ? Float.MAX_VALUE : _myCurrentTime + theLifeTime);
		myActiveParticle.timeOfBirth(_myCurrentTime);
		myActiveParticle.index = theIndex;
		myActiveParticle.x = theIndex % _myWidth;
		myActiveParticle.y = theIndex / _myWidth;
		myActiveParticle.lifeTime(theLifeTime);

		_myActiveParticles.add(myActiveParticle);

		// only create new objects if necessary reuse old ones to avoid garbage collection
		CCGPUParticleData myParticleData;
		if(_myParticleDatas.size() > _myAllocatedParticleCounter) {
			myParticleData = _myParticleDatas.get(_myAllocatedParticleCounter); 
		}else {
			myParticleData = new CCGPUParticleData();
			_myParticleDatas.add(myParticleData);
		}
		_myAllocatedParticleCounter++;
		
		myParticleData.index = theIndex;
		myParticleData.position = thePosition;
		myParticleData.velocity = theVelocity;
		myParticleData.lifeTime = theLifeTime;
		myParticleData.isPermanent = theIsPermanent ? 1 : 0;
		
		return myActiveParticle;
	}

	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * You can also define if a particle is permanent or can die. The number
	 * of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @param theIsPermanent <code>true</code> if the particle is permanent otherwise<code>false</code>
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle allocateParticle(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime, 
		boolean theIsPermanent
	){
		if (_myAvailableIndices.isEmpty())
			return null;

		int myFreeIndex = _myAvailableIndices.poll();
		
		return allocateParticle(myFreeIndex, thePosition, theVelocity, theLifeTime, theIsPermanent);
	}
	
	/**
	 * Allocates a new particle with the given position, velocity and data.
	 * The number of particles you can create is limited by the size of the data texture
	 * that you define in the constructor of the particle system. If no particle
	 * could be allocated this method returns null.
	 * @param thePosition position of the particle
	 * @param theVelocity velocity of the particle
	 * @param theLifeTime lifetime of the particle
	 * @return the allocated particle or <code>null</code>
	 */
	public CCGPUParticle allocateParticle(
		final CCVector3f thePosition, 
		final CCVector3f theVelocity, 
		final float theLifeTime
	) {
		return allocateParticle(thePosition, theVelocity, theLifeTime,false);
	}
	
	public void isPermanent(final int theIndex, final boolean theIsPermanent) {
		_myStateChanges.add(new CCStateChange(theIndex, theIsPermanent,0));
	}
	
	public void isPermanent(final List<Integer> theIndices, final boolean theIsPermanent) {
		for(int myIndex:theIndices) {
			_myStateChanges.add(new CCStateChange(myIndex, theIsPermanent,0));
		}
	}
	
	public void isPermanent(final int theStartIndex, final int theEndIndex, final boolean theIsPermanent) {
		for(int i = theStartIndex; i <= theEndIndex;i++) {
			_myStateChanges.add(new CCStateChange(i, theIsPermanent,0));
		}
	}
	
	public void isPermanent(final boolean theIsPermanent) {

		_myGraphics.noBlend();
		
		// Render current position into texture.
		
		_myCurrentPositionTexture.beginDraw(1);
		_myInitValue1Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (CCGPUParticle myParticle:_myActiveParticles){
			float myTime = (float)(_myCurrentTime - myParticle.timeOfBirth());
			myParticle.timeOfDeath(theIsPermanent ? Float.MAX_VALUE : _myCurrentTime + myParticle.lifeTime());
			_myGraphics.textureCoords(1, 0, myParticle.lifeTime(), theIsPermanent ? 1 : 0,myTime);
			_myGraphics.vertex(myParticle.index % _myWidth,myParticle.index / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue1Shader.end();
		_myCurrentPositionTexture.endDraw();
	}
	
	/**
	 * Kills all permanent particles
	 */
	public void kill(){
		isPermanent(false);
	}
	
	/**
	 * Kills the particle with the given index
	 * @param theIndex
	 */
	public void kill(final int theIndex) {
		isPermanent(theIndex, false);
	}
	
	public void kill(final CCGPUParticle theParticle) {
		kill(theParticle.index);
	}
	
	/**
	 * Kills the particles between the given start and end index
	 * @param theStartIndex
	 * @param theEndIndex
	 */
	public void kill(final int theStartIndex, final int theEndIndex) {
		isPermanent(theStartIndex, theEndIndex, false);
	}
	
	public void minAge(final int theIndex, final float theMinAge) {
		_myStateChanges.add(new CCStateChange(theIndex, false, theMinAge));
	}
	
	public void minAge(final int theStartIndex, final int theEndIndex, final float theMinAge) {
		for(int i = theStartIndex; i <= theEndIndex;i++) {
			_myStateChanges.add(new CCStateChange(i, false,theMinAge));
		}
	}

	
	public CCGPUParticle particle(final int theID) {
		return _myActiveParticlesArray[theID];
	}
	
	/**
	 * Set the absolute position of the particle referenced by theIndex.
	 * @param theIndex index of the target particle
	 * @param thePosition target position of the particle
	 */
	public void setPosition(int theIndex, CCVector3f thePosition) {
		_myPositionUpdates.put(theIndex, thePosition);
	}
	
	private void updateManualPositionChanges() {
		
		if (_myPositionUpdates.size() == 0) {
			return;
		}
		
		// Render manually changed positions into the texture.
		_myCurrentPositionTexture.beginDraw(0);
		_myInitValue0Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
	
		Iterator<Entry<Integer, CCVector3f>> it = _myPositionUpdates.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<Integer, CCVector3f> pairs = (Map.Entry<Integer, CCVector3f>)it.next();
	        
	        _myGraphics.textureCoords(0, pairs.getValue());
			_myGraphics.vertex(pairs.getKey() % _myWidth, 
					           pairs.getKey() / _myWidth);
	    }
	    
		_myGraphics.endShape();
		
		_myInitValue0Shader.end();
		_myCurrentPositionTexture.endDraw();
		
		_myPositionUpdates.clear();
	}
	
	/**
	 * Update the lifetime of the given particle to what is specified in 
	 * the particle instance.
	 * @param theParticle particle instance containing new lifetime data
	 */
	public void updateLifecyle(CCGPUParticle theParticle) {
		_myLifetimeUpdates.add(theParticle);
	}
	
	
	
	private void updateManualLifetimeReset() {
		
		if (_myLifetimeUpdates.size() == 0) {
			return;
		}
		
		_myGraphics.noBlend();
		_myCurrentPositionTexture.beginDraw(1);
		_myInitValue1Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
	    for (CCGPUParticle myParticle:_myLifetimeUpdates) {    
			
	    	// myParticle.timeOfBirth(_myCurrentTime);
	    	// myParticle.timeOfDeath(_myCurrentTime + myParticle.lifeTime());

// 			_myGraphics.textureCoords(1, 0, myParticle.lifeTime(), 0, 0);
//			_myGraphics.vertex(myParticle.index % _myWidth,myParticle.index / _myWidth);
	
			float myTime = (float)(_myCurrentTime - myParticle.timeOfBirth());
			_myGraphics.textureCoords(1, myTime, myParticle.lifeTime(), 0, myTime);
			_myGraphics.vertex(myParticle.index % _myWidth, myParticle.index / _myWidth);
	    			
			_myActiveParticles.remove(myParticle);
			_myActiveParticles.add(myParticle);
	    }
		_myGraphics.endShape();
		
		_myInitValue1Shader.end();
		_myCurrentPositionTexture.endDraw();
				
		_myLifetimeUpdates.clear();
		
		//
	}
	
	private void changeStates() {
		_myCurrentPositionTexture.beginDraw(1);
		_myInitValue1Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (CCStateChange myStateChange:_myStateChanges){
			CCGPUParticle myActiveParticle = _myActiveParticlesArray[myStateChange._myIndex];
			if(myActiveParticle == null || myActiveParticle.index == -1) {
				continue;
			}
			
			double myAge = _myCurrentTime - myActiveParticle.timeOfBirth();
			if(myStateChange._myMinAge > myAge) {
				myActiveParticle.timeOfDeath(myActiveParticle.timeOfDeath() - (myStateChange._myMinAge - myAge));
				myAge = myStateChange._myMinAge;
			}
			myActiveParticle.timeOfDeath(myStateChange._myIsPermanent ? Float.MAX_VALUE : _myCurrentTime + myActiveParticle.lifeTime());
			
			float myTime = (float)(_myCurrentTime - myActiveParticle.timeOfBirth());

			_myGraphics.textureCoords(1, (float)myAge, myActiveParticle.lifeTime(), myStateChange._myIsPermanent ? 1 : 0,myTime);
			_myGraphics.vertex(myActiveParticle.index % _myWidth,myActiveParticle.index / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue1Shader.end();
		_myCurrentPositionTexture.endDraw();
		
		_myStateChanges.clear();
	}
	
	private void initializeNewParticles(){
		// Render velocity.
		_myCurrentVelocityTexture.beginDraw();
		_myInitValue01Shader.start();

		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myAllocatedParticleCounter;i++){
			CCGPUParticleData myParticle = _myParticleDatas.get(i);
			_myGraphics.textureCoords(0, myParticle.velocity);
			_myGraphics.vertex(myParticle.index % _myWidth, myParticle.index / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue01Shader.end();
		_myCurrentVelocityTexture.endDraw();
		
		// Render current position into texture.
		_myCurrentPositionTexture.beginDraw();
		_myInitValue01Shader.start();
		
		_myGraphics.beginShape(CCDrawMode.POINTS);
		for (int i = 0; i < _myAllocatedParticleCounter;i++){
			CCGPUParticleData myParticle = _myParticleDatas.get(i);
			_myGraphics.textureCoords(0, myParticle.position);
			_myGraphics.textureCoords(1, 0, myParticle.lifeTime, myParticle.isPermanent, 0);
			_myGraphics.vertex(myParticle.index % _myWidth,myParticle.index / _myWidth);
		}
		_myGraphics.endShape();
		
		_myInitValue01Shader.end();
		_myCurrentPositionTexture.endDraw();

		// reset counter
		_myAllocatedParticleCounter = 0;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.gpu.particles.CCGPUParticles#reset()
	 */
	@Override
	public void reset() {
		super.reset();

		_myAvailableIndices = new PriorityQueue<Integer>(size());
		_myActiveParticles = new PriorityQueue<CCGPUParticle>(size());

		for (int i = 0; i < size(); i++){
			_myAvailableIndices.add(i);
		}
	}
	
	public int particlesInUse(){
		return _myActiveParticles.size();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.gpu.particles.CCGPUParticles#beforeUpdate()
	 */
	@Override
	protected void beforeUpdate() {
		initializeNewParticles();
		changeStates();
	}
	
	private void cleanUpParticles() {
		if(_myActiveParticles.size() <= 0)
			return;
		
		_myCurrentPositionTexture.beginDraw(1);
		_myInitValue1Shader.start();
		_myGraphics.beginShape(CCDrawMode.POINTS);
		
		// System.out.println("cleanup? : " + _myActiveParticles.peek().timeOfDeath() + " " + _myCurrentTime);
				
		while (_myActiveParticles.peek() != null && _myActiveParticles.peek().timeOfDeath() < _myCurrentTime){
			CCGPUParticle myParticle = _myActiveParticles.poll();
			if(myParticle.index == -1) continue;
			_myAvailableIndices.add(myParticle.index);
			_myActiveParticlesArray[myParticle.index].index = -1;
			
			_myGraphics.textureCoords(0, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
			_myGraphics.textureCoords(1, 0, 0, 1, 0);
			_myGraphics.vertex(myParticle.index % _myWidth,myParticle.index / _myWidth);
		}
		
		_myGraphics.endShape();
		_myInitValue1Shader.end();
		_myCurrentPositionTexture.endDraw();
	}
	
	@Override
	protected void afterUpdate(){
		updateManualPositionChanges();
		updateManualLifetimeReset();
		cleanUpParticles();
	}
}
