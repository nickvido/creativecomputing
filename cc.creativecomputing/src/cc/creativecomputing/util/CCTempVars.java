/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package cc.creativecomputing.util;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.CCMatrix3f;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCTriangle3f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Temporary variables assigned to each thread. Engine classes may access
 * these temp variables with TempVars.get(), all retrieved TempVars
 * instances must be returned via TempVars.release().
 * This returns an available instance of the TempVar class ensuring this 
 * particular instance is never used elsewhere in the mean time.
 */
public class CCTempVars {

    /**
     * Allow X instances of TempVars in a single thread.
     */
    private static final int STACK_SIZE = 5;

    /**
     * <code>TempVarsStack</code> contains a stack of TempVars.
     * Every time TempVars.get() is called, a new entry is added to the stack,
     * and the index incremented.
     * When TempVars.release() is called, the entry is checked against
     * the current instance and  then the index is decremented.
     */
    private static class TempVarsStack {

        int index = 0;
        CCTempVars[] tempVars = new CCTempVars[STACK_SIZE];
    }
    /**
     * ThreadLocal to store a TempVarsStack for each thread.
     * This ensures each thread has a single TempVarsStack that is
     * used only in method calls in that thread.
     */
    private static final ThreadLocal<TempVarsStack> varsLocal = new ThreadLocal<TempVarsStack>() {

        @Override
        public TempVarsStack initialValue() {
            return new TempVarsStack();
        }
    };
    /**
     * This instance of TempVars has been retrieved but not released yet.
     */
    private boolean isUsed = false;

    private CCTempVars() {
    }

    /**
     * Acquire an instance of the TempVar class.
     * You have to release the instance after use by calling the 
     * release() method. 
     * If more than STACK_SIZE (currently 5) instances are requested 
     * in a single thread then an ArrayIndexOutOfBoundsException will be thrown.
     * 
     * @return A TempVar instance
     */
    public static CCTempVars get() {
        TempVarsStack stack = varsLocal.get();

        CCTempVars instance = stack.tempVars[stack.index];

        if (instance == null) {
            // Create new
            instance = new CCTempVars();

            // Put it in there
            stack.tempVars[stack.index] = instance;
        }

        stack.index++;

        instance.isUsed = true;

        return instance;
    }

    /**
     * Releases this instance of TempVars.
     * Once released, the contents of the TempVars are undefined.
     * The TempVars must be released in the opposite order that they are retrieved,
     * e.g. Acquiring vars1, then acquiring vars2, vars2 MUST be released 
     * first otherwise an exception will be thrown.
     */
    public void release() {
        if (!isUsed) {
            throw new IllegalStateException("This instance of TempVars was already released!");
        }

        isUsed = false;

        TempVarsStack stack = varsLocal.get();

        // Return it to the stack
        stack.index--;

        // Check if it is actually there
        if (stack.tempVars[stack.index] != this) {
            throw new IllegalStateException("An instance of TempVars has not been released in a called method!");
        }
    }
    /**
     * For interfacing with OpenGL in Renderer.
     */
    public final IntBuffer intBuffer1 = IntBuffer.allocate(1);
    public final IntBuffer intBuffer16 = IntBuffer.allocate(16);
    public final FloatBuffer floatBuffer16 = FloatBuffer.allocate(16);
    /**
     * Skinning buffers
     */
    public final float[] skinPositions = new float[512 * 3];
    public final float[] skinNormals = new float[512 * 3];
    /**
     * Fetching triangle from mesh
     */
    public final CCTriangle3f triangle = new CCTriangle3f();
    /**
     * Color
     */
    public final CCColor color = new CCColor();
    /**
     * General vectors.
     */
    public final CCVector3f vect1 = new CCVector3f();
    public final CCVector3f vect2 = new CCVector3f();
    public final CCVector3f vect3 = new CCVector3f();
    public final CCVector3f vect4 = new CCVector3f();
    public final CCVector3f vect5 = new CCVector3f();
    public final CCVector3f vect6 = new CCVector3f();
    public final CCVector3f vect7 = new CCVector3f();
    public final CCVector3f vect8 = new CCVector3f();
    public final CCVector3f vect9 = new CCVector3f();
    public final CCVector3f vect10 = new CCVector3f();
    
    public final CCVector4f vect4f = new CCVector4f();
    
    public final CCVector3f[] tri = {
    	new CCVector3f(),
        new CCVector3f(),
        new CCVector3f()
    };
    /**
     * 2D vector
     */
    public final CCVector2f vect2d = new CCVector2f();
    public final CCVector2f vect2d2 = new CCVector2f();
    /**
     * General matrices.
     */
    public final CCMatrix3f tempMat3 = new CCMatrix3f();
    public final CCMatrix4f tempMat4 = new CCMatrix4f();
    /**
     * General quaternions.
     */
    public final CCQuaternion quat1 = new CCQuaternion();
    public final CCQuaternion quat2 = new CCQuaternion();
//    /**
//     * Eigen
//     */
//    public final Eigen3f eigen = new Eigen3f();
//    /**
//     * Plane
//     */
//    public final Plane plane = new Plane();
//    /**
//     * BoundingBox ray collision
//     */
//    public final float[] fWdU = new float[3];
//    public final float[] fAWdU = new float[3];
//    public final float[] fDdU = new float[3];
//    public final float[] fADdU = new float[3];
//    public final float[] fAWxDdU = new float[3];
//    /**
//     * Maximum tree depth .. 32 levels??
//     */
//    public final Spatial[] spatialStack = new Spatial[32];
    public final float[] matrixWrite = new float[16];
//    /**
//     * BIHTree
//     */
    public final float[] bihSwapTmp = new float[9];
//    public final ArrayList<BIHStackData> bihStack = new ArrayList<BIHStackData>();
}
