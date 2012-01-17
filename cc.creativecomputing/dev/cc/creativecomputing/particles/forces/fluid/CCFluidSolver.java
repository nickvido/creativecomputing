package cc.creativecomputing.particles.forces.fluid;

import java.nio.FloatBuffer;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.opencl.CCCLKernel;
import cc.creativecomputing.opencl.CCCLProgram;
import cc.creativecomputing.opencl.CCOpenCL;

import com.jogamp.opencl.CLBuffer;
import com.jogamp.opencl.CLCommandQueue;
import com.jogamp.opencl.CLMemory.Mem;

/**
 * Jos Stam style fluid solver with vorticity confinement and buoyancy force.
 **/

public class CCFluidSolver {
	@CCControl(name = "diffusion", min = 0, max = 10)
	private float _cDiffusion = 0.00005f;
	
	@CCControl(name = "viscosity", min = 0, max = 10)
	private float _cViscosity = 0.0f;
	
	@CCControl(name = "vorticity", min = 0, max = 1)
	private float _cVorticity = 0.0f;
	
	@CCControl(name = "iterations", min = 0, max = 20)
	private int _cIterations = 20;

	private int _myWidth;
	private int _myHeight;
	private int _mySize;
	private int _myWSSize = 0;

	private float _myDeltaTime;
	
	private CCOpenCL _myOpenCL;
	private CCCLProgram _myFluidProgram;
	private CLCommandQueue _myCommandQueue;
	
	private CCCLKernel _myAddSourceKernel1D;
	private CCCLKernel _myAddSourceKernel2D;
	private CCCLKernel _myAddSourceKernel4D;
	
	private CCCLKernel _myLinearSolverKernel1D;
	private CCCLKernel _myLinearSolverKernel2D;
	private CCCLKernel _myLinearSolverKernel4D;
	
	private CCCLKernel _mySetBoundaryKernel1D;
	private CCCLKernel _mySetBoundaryKernel2D;
	private CCCLKernel _mySetBoundaryKernel4D;
	
	private CCCLKernel _myAdvectKernel1D;
	private CCCLKernel _myAdvectKernel2D;
	private CCCLKernel _myAdvectKernel4D;
	
	private CCCLKernel _myClearKernel1D;
	private CCCLKernel _myClearKernel2D;
	private CCCLKernel _myClearKernel4D;
	
	private CCCLKernel _myCurlKernel;
	private CLBuffer<FloatBuffer> _myCurlBuffer;
	private CCCLKernel _myVorticityKernel;
	
	private CCCLKernel _myDivergencyKernel;
	private CCCLKernel _myMassConserveKernel;
	private CCCLKernel _myProjectionBoundaryKernel;

	private CLBuffer<FloatBuffer> _myVelocityBuffer;
	private CLBuffer<FloatBuffer> _myTemp2DBuffer;

	private CLBuffer<FloatBuffer> _myTemp1DBuffer1;
	private CLBuffer<FloatBuffer> _myTemp1DBuffer2;
	
	private CLBuffer<FloatBuffer> _myDensityBuffer;
	private CLBuffer<FloatBuffer> _myDensityTempBuffer;

	/**
	 * Set the grid size and timestep.
	 **/

	public CCFluidSolver(int theWidth, int theHeight) {
		_myWidth = theWidth + 2;
		_myHeight = theHeight + 2;
		_mySize = _myWidth * _myHeight;
		
		_myOpenCL = new CCOpenCL("Apple", 0, null);
		_myFluidProgram = new CCCLProgram(_myOpenCL);
		_myFluidProgram.appendSource(CCFluidSolver.class, "fluid2.cl");
		_myFluidProgram.build();
		
		_myCommandQueue = _myOpenCL.createCommandQueue();
		System.out.println(_myCommandQueue.getDevice().getVendor());
		System.out.println("MaxComputeUnits:" + _myCommandQueue.getDevice().getMaxComputeUnits());
		System.out.println("MaxComputeUnits:" + _myCommandQueue.getDevice().getMaxWorkGroupSize());
		
		_myDensityBuffer = _myOpenCL.createCLFloatBuffer(_mySize * 4, Mem.READ_WRITE);
		_myDensityTempBuffer = _myOpenCL.createCLFloatBuffer(_mySize * 4, Mem.READ_WRITE);
		
		_myVelocityBuffer = _myOpenCL.createCLFloatBuffer(_mySize * 2, Mem.READ_WRITE);
		_myTemp2DBuffer = _myOpenCL.createCLFloatBuffer(_mySize * 2, Mem.READ_WRITE);
		
		_myTemp1DBuffer1 = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);
		_myTemp1DBuffer2 = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);
		
		_myClearKernel1D = _myFluidProgram.createKernel("clear1");
		_myClearKernel2D = _myFluidProgram.createKernel("clear2");
		_myClearKernel4D = _myFluidProgram.createKernel("clear4");
		
		_myAddSourceKernel1D = _myFluidProgram.createKernel("addSource1");
		_myAddSourceKernel2D = _myFluidProgram.createKernel("addSource2");
		_myAddSourceKernel4D = _myFluidProgram.createKernel("addSource4");
		
		_myLinearSolverKernel1D = _myFluidProgram.createKernel("linearSolver1");
		_myLinearSolverKernel2D = _myFluidProgram.createKernel("linearSolver2");
		_myLinearSolverKernel4D = _myFluidProgram.createKernel("linearSolver4");
		
		_mySetBoundaryKernel1D = _myFluidProgram.createKernel("setBoundary1");
		_mySetBoundaryKernel2D = _myFluidProgram.createKernel("setBoundary2");
		_mySetBoundaryKernel4D = _myFluidProgram.createKernel("setBoundary4");
		
		_myAdvectKernel1D = _myFluidProgram.createKernel("advect1");
		_myAdvectKernel2D = _myFluidProgram.createKernel("advect2");
		_myAdvectKernel4D = _myFluidProgram.createKernel("advect4");
		
		_myCurlBuffer = _myOpenCL.createCLFloatBuffer(_mySize, Mem.READ_WRITE);	
		_myCurlKernel = _myFluidProgram.createKernel("curl");
		_myVorticityKernel = _myFluidProgram.createKernel("vorticityConfinement");
		
		_myDivergencyKernel = _myFluidProgram.createKernel("divergency");
		_myMassConserveKernel = _myFluidProgram.createKernel("massConserve");
		_myProjectionBoundaryKernel = _myFluidProgram.createKernel("projectBoundary");
		
		reset();
	}
	
	/**
	 * Calculates the cell index based on the given 2D position in the grid.
	 * @param theX x coord of the 2D cell index
	 * @param theY y coord of the 2D cell index
	 * @return 1D cell index
	 */
	private int index(int theX, int theY) {
		return theX + theY * _myWidth;
	}
	
	private int index(CCVector2i theCell) {
		return index(theCell.x, theCell.y);
	}
	
	private CCVector2i cell(float theX, float theY){
		int myX = (int) (theX * (_myWidth - 1) + 1);
		int myY = (int) (theY * (_myHeight - 1) + 1);

		// set boundries
		if (myX > _myWidth - 2)
			myX = _myWidth - 2;
		if (myX < 1)
			myX = 1;
		if (myY > _myHeight - 2)
			myY = _myHeight - 2;
		if (myY < 1)
			myY = 1;
		
		return new CCVector2i(myX, myY);
	}
	
	private CCVector2i cell(CCVector2f theCell){
		return cell(theCell.x, theCell.y);
	}
	
	public CCTextureData colorData() {
		return _myOpenCL.createTextureData(_myDensityBuffer, _myWidth, _myHeight);
	}
	
	public void addColor(CCVector2i theCell, CCColor theColor, float theDensity){
		int myIndex = index(theCell) * 4;
		_myDensityTempBuffer.getBuffer().put(myIndex, theColor.r * 255);
		_myDensityTempBuffer.getBuffer().put(myIndex + 1, theColor.g * 255);
		_myDensityTempBuffer.getBuffer().put(myIndex + 2, theColor.b * 255);
		_myDensityTempBuffer.getBuffer().put(myIndex + 3, theDensity);
	}
	
	public void addColor(CCVector2f theCell, CCColor theColor, float theDensity){
		addColor(cell(theCell), theColor, theDensity);
	}
	
	public CCColor getColor(int theX, int theY){
		int myIndex = index(theX, theY) * 4;
		return new CCColor(
			_myDensityBuffer.getBuffer().get(myIndex), 
			_myDensityBuffer.getBuffer().get(myIndex + 1), 
			_myDensityBuffer.getBuffer().get(myIndex + 2)
		);
	}
	
	public void addVelocity(CCVector2i theCell, CCVector2f theVelocity) {
		int myIndex = index(theCell) * 2;
		_myTemp2DBuffer.getBuffer().put(myIndex, theVelocity.x);
		_myTemp2DBuffer.getBuffer().put(myIndex + 1, theVelocity.y);
	}
	
	public void addVelocity(CCVector2f theCell, CCVector2f theVelocity) {
		addVelocity(cell(theCell), theVelocity);
	}
	
	public CCVector2f getVelocity(int theX, int theY){
		int myIndex = index(theX, theY);
		return new CCVector2f(
			_myVelocityBuffer.getBuffer().get(myIndex * 2),
			_myVelocityBuffer.getBuffer().get(myIndex * 2 + 1)
		);
	}

	public void reset() {
		clear4D(_myDensityBuffer);
		clear4D(_myDensityTempBuffer);
		clear2D(_myVelocityBuffer);
		clear2D(_myTemp2DBuffer);
		_myCommandQueue.finish();
	}
	
	public void add1D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput
	) {
		_myAddSourceKernel1D.argument(0, theOutput);
		_myAddSourceKernel1D.argument(1, theInput);
		_myAddSourceKernel1D.argument1f(2, _myDeltaTime);
		_myCommandQueue.put1DRangeKernel(_myAddSourceKernel1D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	public void add2D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput
	) {
		_myAddSourceKernel2D.argument(0, theOutput);
		_myAddSourceKernel2D.argument(1, theInput);
		_myAddSourceKernel2D.argument1f(2, _myDeltaTime);
		_myCommandQueue.put1DRangeKernel(_myAddSourceKernel2D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	public void add4D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput
	) {
		_myAddSourceKernel4D.argument(0, theOutput);
		_myAddSourceKernel4D.argument(1, theInput);
		_myAddSourceKernel4D.argument1f(2, _myDeltaTime);
		_myCommandQueue.put1DRangeKernel(_myAddSourceKernel4D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	public void clear1D(CLBuffer<FloatBuffer> theData) {
		_myClearKernel1D.argument(0, theData);
		_myCommandQueue.put1DRangeKernel(_myClearKernel1D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	public void clear2D(CLBuffer<FloatBuffer> theData) {
		_myClearKernel2D.argument(0, theData);
		_myCommandQueue.put1DRangeKernel(_myClearKernel2D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	public void clear4D(CLBuffer<FloatBuffer> theData) {
		_myClearKernel4D.argument(0, theData);
		_myCommandQueue.put1DRangeKernel(_myClearKernel4D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	/**
	 * Iterative linear system solver using the Gauss-sidel relaxation
	 * technique. Room for much improvement here...
	 * 
	 **/
	private void linearSolver1D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theA,
		float theC
	) {
		_myLinearSolverKernel1D.argument(0, theOutput);
		_myLinearSolverKernel1D.argument(1, theInput);
		_myLinearSolverKernel1D.argument1f(2, theA);
		_myLinearSolverKernel1D.argument1f(3, theC);
		_myLinearSolverKernel1D.argument1i(4, _myWidth);
		_myLinearSolverKernel1D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myLinearSolverKernel1D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void linearSolver2D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theA,
		float theC
	) {
		_myLinearSolverKernel2D.argument(0, theOutput);
		_myLinearSolverKernel2D.argument(1, theInput);
		_myLinearSolverKernel2D.argument1f(2, theA);
		_myLinearSolverKernel2D.argument1f(3, theC);
		_myLinearSolverKernel2D.argument1i(4, _myWidth);
		_myLinearSolverKernel2D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myLinearSolverKernel2D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void linearSolver4D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theA,
		float theC
	) {
		_myLinearSolverKernel4D.argument(0, theOutput);
		_myLinearSolverKernel4D.argument(1, theInput);
		_myLinearSolverKernel4D.argument1f(2, theA);
		_myLinearSolverKernel4D.argument1f(3, theC);
		_myLinearSolverKernel4D.argument1i(4, _myWidth);
		_myLinearSolverKernel4D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myLinearSolverKernel4D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void setBoundary1D(CLBuffer<FloatBuffer> theData) {
		_mySetBoundaryKernel1D.argument(0, theData);
		_mySetBoundaryKernel1D.argument1i(1, _myWidth);
		_mySetBoundaryKernel1D.argument1i(2, _myHeight);
		_myCommandQueue.put1DRangeKernel(_mySetBoundaryKernel1D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void setBoundary2D(CLBuffer<FloatBuffer> theData) {
		_mySetBoundaryKernel2D.argument(0, theData);
		_mySetBoundaryKernel2D.argument1i(1, _myWidth);
		_mySetBoundaryKernel2D.argument1i(2, _myHeight);
		_myCommandQueue.put1DRangeKernel(_mySetBoundaryKernel2D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void setBoundary4D(CLBuffer<FloatBuffer> theData) {
		_mySetBoundaryKernel4D.argument(0, theData);
		_mySetBoundaryKernel4D.argument1i(1, _myWidth);
		_mySetBoundaryKernel4D.argument1i(2, _myHeight);
		_myCommandQueue.put1DRangeKernel(_mySetBoundaryKernel4D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	/**
	 * Recalculate the input array with diffusion effects. Here we consider a stable method of diffusion by finding the
	 * densities, which when diffused backward in time yield the same densities we started with. This is achieved
	 * through use of a linear solver to solve the sparse matrix built from this linear system.
	 * 
	 * @param theOutput The array to store the results of the diffusion computation.
	 * @param theInput The input array on which we should compute diffusion.
	 * @param theDiffusion The factor of diffusion.
	 **/
	
	private void diffuse1D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theDiffusion
	) {
		float a = _myDeltaTime * theDiffusion * (_myWidth - 2) * (_myHeight - 2);
		float c = 1 + 4 * a;
		for(int i = 0; i < _cIterations;i++) {
			linearSolver1D(theOutput, theInput, a, 1 + 4 * a);
			setBoundary1D(theOutput);
		}
	}
	
	private void diffuse2D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theDiffusion
	) {
		float a = _myDeltaTime * theDiffusion;// * (_myWidth - 2) * (_myHeight - 2);
		float c = 1 + 4 * a;
		for(int i = 0; i < _cIterations;i++) {
			linearSolver2D(theOutput, theInput, a, 1 + 4 * a);
			setBoundary2D(theOutput);
		}
	}
	
	private void diffuse4D(
		CLBuffer<FloatBuffer> theOutput,
		CLBuffer<FloatBuffer> theInput,
		float theDiffusion
	) {
		float a = _myDeltaTime * theDiffusion;// * (_myWidth - 2) * (_myHeight - 2);
		float c = 1 + 4 * a;
		for(int i = 0; i < _cIterations;i++) {
			linearSolver4D(theOutput, theInput, a, 1 + 4 * a);
			setBoundary4D(theOutput);
		}
	}
	
	/**
	 * Calculate the input array after advection. We start with an input array
	 * from the previous timestep and an and output array. For all grid cells we
	 * need to calculate for the next timestep, we trace the cell's center
	 * position backwards through the velocity field. Then we interpolate from
	 * the grid of the previous timestep and assign this value to the current
	 * grid cell.
	 * 
	 * @param theOutput Array to store the advected field.
	 * @param theInput the array to advect.
	 * @param theVelocities the velocity field.
	 **/
	
	private void advect1d(
		CLBuffer<FloatBuffer> theOutput, 
		CLBuffer<FloatBuffer> theInput, 
		CLBuffer<FloatBuffer> theVelocities
	) {
		_myAdvectKernel1D.argument(0, theOutput);
		_myAdvectKernel1D.argument(1, theInput);
		_myAdvectKernel1D.argument(2, theVelocities);
		_myAdvectKernel1D.argument1f(3, _myDeltaTime);
		_myAdvectKernel1D.argument1i(4, _myWidth);
		_myAdvectKernel1D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myAdvectKernel1D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void advect2d(
		CLBuffer<FloatBuffer> theOutput, 
		CLBuffer<FloatBuffer> theInput, 
		CLBuffer<FloatBuffer> theVelocities
	) {
		_myAdvectKernel2D.argument(0, theOutput);
		_myAdvectKernel2D.argument(1, theInput);
		_myAdvectKernel2D.argument(2, theVelocities);
		_myAdvectKernel2D.argument1f(3, _myDeltaTime);
		_myAdvectKernel2D.argument1i(4, _myWidth);
		_myAdvectKernel2D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myAdvectKernel2D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	private void advect4d(
		CLBuffer<FloatBuffer> theOutput, 
		CLBuffer<FloatBuffer> theInput, 
		CLBuffer<FloatBuffer> theVelocities
	) {
		_myAdvectKernel4D.argument(0, theOutput);
		_myAdvectKernel4D.argument(1, theInput);
		_myAdvectKernel4D.argument(2, theVelocities);
		_myAdvectKernel4D.argument1f(3, _myDeltaTime);
		_myAdvectKernel4D.argument1i(4, _myWidth);
		_myAdvectKernel4D.argument1i(5, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myAdvectKernel4D.clKernel(), 0, _mySize, _myWSSize);
	}
	
	/**
	 * Calculate the vorticity confinement force for each cell in the fluid grid. At a point (i,j), Fvc = N x w where w
	 * is the curl at (i,j) and N = del |w| / |del |w||. N is the vector pointing to the vortex center, hence we add
	 * force perpendicular to N.
	 * 
	 * @param theVelocities The array to store the vorticity confinement force for each cell.
	 **/
	private void vorticity(CLBuffer<FloatBuffer> theVelocities) {
		_myCurlKernel.argument(0, _myCurlBuffer);
		_myCurlKernel.argument(1, theVelocities);
		_myCurlKernel.argument1i(2, _myWidth);
		_myCurlKernel.argument1i(3, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myCurlKernel.clKernel(), 0, _mySize, _myWSSize);

		_myVorticityKernel.argument(0, _myCurlBuffer);
		_myVorticityKernel.argument(1, theVelocities);
		_myVorticityKernel.argument1f(2, _myDeltaTime * _cVorticity);
		_myVorticityKernel.argument1i(3, _myWidth);
		_myVorticityKernel.argument1i(4, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myVorticityKernel.clKernel(), 0, _mySize, _myWSSize);
	}
	
	/**
	 * Makes the velocity a mass conserving, incompressible field. Achieved through a Hodge decomposition. 
	 * First we calculate the divergence field of our velocity using the mean finite difference approach,
	 * and apply the linear solver to compute the Poisson equation and obtain a "height" field. Now we 
	 * subtract the gradient of this field to obtain our mass conserving velocity field.
	 * 
	 * @param theVelocities The array in which our final velocity field is stored.
	 **/
	
	private void project(CLBuffer<FloatBuffer> theVelocities) {
		_myDivergencyKernel.argument(0, _myTemp1DBuffer1);
		_myDivergencyKernel.argument(1, theVelocities);
		_myDivergencyKernel.argument1i(2, _myWidth);
		_myDivergencyKernel.argument1i(3, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myDivergencyKernel.clKernel(), 0, _mySize, _myWSSize);
		
		clear1D(_myTemp1DBuffer2);
		
		setBoundary1D(_myTemp1DBuffer1);
		setBoundary1D(_myTemp1DBuffer2);
		
		for(int k = 0; k < _cIterations; k++) {
			linearSolver1D(_myTemp1DBuffer2, _myTemp1DBuffer1, 1, 4);
		}
		
		_myMassConserveKernel.argument(0, theVelocities);
		_myMassConserveKernel.argument(1, _myTemp1DBuffer2);
		_myMassConserveKernel.argument1i(2, _myWidth);
		_myMassConserveKernel.argument1i(3, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myMassConserveKernel.clKernel(), 0, _mySize, _myWSSize);
		
		_myProjectionBoundaryKernel.argument(0, theVelocities);
		_myProjectionBoundaryKernel.argument1i(1, _myWidth);
		_myProjectionBoundaryKernel.argument1i(2, _myHeight);
		_myCommandQueue.put1DRangeKernel(_myProjectionBoundaryKernel.clKernel(), 0, _mySize, _myWSSize);
	}

	public void update(float theDeltaTime) {
		_myDeltaTime = theDeltaTime;
		_myVelocityBuffer.getBuffer().rewind();
		_myCommandQueue.putWriteBuffer(_myVelocityBuffer, false);
		
		_myTemp2DBuffer.getBuffer().rewind();
		_myCommandQueue.putWriteBuffer(_myTemp2DBuffer, false);
		
		add2D(_myVelocityBuffer, _myTemp2DBuffer);
		vorticity(_myVelocityBuffer);
		diffuse2D(_myTemp2DBuffer, _myVelocityBuffer, _cViscosity);
		
		project(_myTemp2DBuffer);
		
		advect2d(_myVelocityBuffer, _myTemp2DBuffer, _myTemp2DBuffer);
		setBoundary2D(_myVelocityBuffer);
		
		// make an incompressible field
		project(_myVelocityBuffer);
		
		// solve densities
		_myDensityTempBuffer.getBuffer().rewind();
		_myCommandQueue.putWriteBuffer(_myDensityTempBuffer, false);
		
		_myDensityBuffer.getBuffer().rewind();
		_myCommandQueue.putWriteBuffer(_myDensityBuffer, false);

		add4D(_myDensityBuffer, _myDensityTempBuffer);
		diffuse4D(_myDensityTempBuffer, _myDensityBuffer, _cDiffusion);
		advect4d(_myDensityBuffer, _myDensityTempBuffer, _myVelocityBuffer);
		setBoundary4D(_myDensityBuffer);
		
		
		clear4D(_myDensityTempBuffer);
		clear2D(_myTemp2DBuffer);

		_myCommandQueue.putReadBuffer(_myVelocityBuffer, true);
		_myCommandQueue.putReadBuffer(_myTemp2DBuffer, true);
		_myCommandQueue.putReadBuffer(_myDensityTempBuffer, true);
		_myCommandQueue.putReadBuffer(_myDensityBuffer, true);
		_myCommandQueue.finish();
	}

}