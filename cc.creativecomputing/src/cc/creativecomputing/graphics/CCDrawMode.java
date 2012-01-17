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
package cc.creativecomputing.graphics;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GL2GL3;

public enum CCDrawMode{
	/**
	 * Treats each vertex as a single point. Vertex n defines point n. N points are drawn.
	 */
	POINTS(GL.GL_POINTS),
	/**
	 * Treats each pair of vertices as an independent line segment. 
	 * Vertices 2n-1 and 2n define line n. N/2 lines are drawn.
	 */
	LINES(GL.GL_LINES),
	LINES_ADJACENCY(GL2GL3.GL_LINES_ADJACENCY_ARB),
	/**
	 * Draws a connected group of line segments from the first vertex to the last. 
	 * Vertices n and n+1 define line n. N-1 lines drawn.
	 */
	LINE_STRIP(GL.GL_LINE_STRIP),
	LINE_STRIP_ADJACENCY(GL2GL3.GL_LINE_STRIP_ADJACENCY_ARB),
	/**
	 * Draws a connected group of line segments from the first vertex to the last, 
	 * then back to the first. Vertices n and n+1 define line n. 
	 * The last line, however, is defined by vertices N and 1. N lines are drawn.
	 */
	LINE_LOOP(GL.GL_LINE_LOOP),
	/**
	 * Treates each triplet of vertices as an independent triangle. 
	 * Vertices 3n-2, 3n-1, and 3n define triangle n. N/3 triangles are drawn.
	 */
	TRIANGLES(GL.GL_TRIANGLES),
	TRIANGLES_ADJACENCY(GL2GL3.GL_TRIANGLES_ADJACENCY_ARB),
	/**
	 * Draws a connected group of triangles. One triangle is defined for each 
	 * vertex presented after the first two vertices. 
	 * For odd n, vertices n, n+1, and n+2 define triangle n. 
	 * For even n, vertices n+1, n, and n+2 define triangle n. N-2 triangles are drawn.
	 */
	TRIANGLE_STRIP(GL.GL_TRIANGLE_STRIP),
	TRIANGLE_STRIP_ADJACENCY(GL2GL3.GL_TRIANGLE_STRIP_ADJACENCY_ARB),
	/**
	 * Draws a connected group of triangles. One triangle is defined for each 
	 * vertex presented after the first two vertices. 
	 * For odd n, vertices n, n+1, and n+2 define triangle n. 
	 * For even n, vertices n+1, n, and n+2 define triangle n. N-2 triangles are drawn.
	 */
	TRIANGLE_FAN(GL2ES2.GL_TRIANGLE_FAN),
	QUADS(GL2.GL_QUADS),
	QUAD_STRIP(GL2.GL_QUAD_STRIP),
	POLYGON(GL2.GL_POLYGON);
	
	public int glID;
	
	private CCDrawMode(final int theGlID){
		glID = theGlID;
	}
}