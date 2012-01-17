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
package cc.creativecomputing.graphics;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.d.CCVector2d;

/**
 * @author info
 *
 */
public class CCPath2D {
	
	private interface CCPath2DJoinType{
		public void createTriangles(CCPathSegment theSegment1, CCPathSegment theSegment2);
	}
	
	private class CCPath2DNoneJoinType implements CCPath2DJoinType{
		public void createTriangles(CCPathSegment theSegment1, CCPathSegment theSegment2) {
			_myTriangles.add(theSegment1._myVertexA);
			_myTriangles.add(theSegment1._myVertexB);
			_myTriangles.add(theSegment1._myVertexC);

			_myTriangles.add(theSegment1._myVertexB);
			_myTriangles.add(theSegment1._myVertexD);
			_myTriangles.add(theSegment1._myVertexC);
			
			_myAlphas.add(theSegment1._myAlphaA);
			_myAlphas.add(theSegment1._myAlphaB);
			_myAlphas.add(theSegment1._myAlphaC);

			_myAlphas.add(theSegment1._myAlphaB);
			_myAlphas.add(theSegment1._myAlphaD);
			_myAlphas.add(theSegment1._myAlphaC);
		}
	}
	
	private class CCPath2DMiterJoinType extends CCPath2DNoneJoinType{
		public void createTriangles(CCPathSegment theSegment1, CCPathSegment theSegment2) {
			if(theSegment2 == null) {
				super.createTriangles(theSegment1, theSegment2);
				return;
			}
			
			CCPathIntersection myResult1 = intersect(
				theSegment1._myVertexA, theSegment1._myVertexB, 
				theSegment2._myVertexA, theSegment2._myVertexB, 
				theSegment1._myVertexB
			);
			CCPathIntersection myResult2 = intersect(
				theSegment1._myVertexC, theSegment1._myVertexD, 
				theSegment2._myVertexC, theSegment2._myVertexD, 
				theSegment1._myVertexD
			);
			
			_myTriangles.add(theSegment1._myVertexA);
			_myTriangles.add(theSegment1._myVertexB);
			_myTriangles.add(theSegment1._myVertexC);

			_myTriangles.add(theSegment1._myVertexB);
			_myTriangles.add(theSegment1._myVertexD);
			_myTriangles.add(theSegment1._myVertexC);
			
			_myAlphas.add(theSegment1._myAlphaA);
			_myAlphas.add(theSegment1._myAlphaB);
			_myAlphas.add(theSegment1._myAlphaC);

			_myAlphas.add(theSegment1._myAlphaB);
			_myAlphas.add(theSegment1._myAlphaD);
			_myAlphas.add(theSegment1._myAlphaC);
			
			theSegment2._myVertexA.set(theSegment1._myVertexB);
			theSegment2._myVertexC.set(theSegment1._myVertexD);

			if(myResult1 == CCPathIntersection.PARALLEL)return;
			_myTriangles.add(theSegment1._myVertexB);
			_myTriangles.add(theSegment1._myVertexC);
			_myTriangles.add(theSegment2._myVertexA);

			_myAlphas.add(theSegment1._myAlphaB);
			_myAlphas.add(theSegment1._myAlphaC);
			_myAlphas.add(theSegment1._myAlphaA);
		}
	}
	
	private static class CCPathSegment{
		CCVector2d start;
		CCVector2d end;
		CCVector2d spacer;
		
		CCVector2d _myVertexA;
		CCVector2d _myVertexB;
		CCVector2d _myVertexC;
		CCVector2d _myVertexD;
		
		float _myAlphaA;
		float _myAlphaB;
		float _myAlphaC;
		float _myAlphaD;
		
		private CCPathSegment(final CCVector2d theStart, final CCVector2d theEnd, final float theStartAlpha, final float theEndAlpha, final float theStrokeWeight) {
			start = theStart;
			end = theEnd;
			spacer = new CCVector2d(start.x - end.x, start.y - end.y);
			spacer = spacer.normalize().cross().scale(theStrokeWeight);
			
			_myVertexA = new CCVector2d(start.x - spacer.x, start.y - spacer.y);;
			_myVertexB = new CCVector2d(end.x - spacer.x, end.y - spacer.y);
			_myVertexC = new CCVector2d(start.x + spacer.x, start.y + spacer.y);
			_myVertexD = new CCVector2d(end.x + spacer.x, end.y + spacer.y);
			
			_myAlphaA = theStartAlpha;
			_myAlphaB = theEndAlpha;
			_myAlphaC = theStartAlpha;
			_myAlphaD = theEndAlpha;
		}
	}
	
	public static enum CCStrokeJoin{
		NONE,ROUND,MITER,BEVEL
	}
	
	public static enum CCStrokeCap{
		SQUARE, ROUND, PROJECT
		
	}
	
	private float _myStrokeWeight;
	
	private CCStrokeJoin _myStrokeJoin = CCStrokeJoin.NONE;
	private CCPath2DJoinType _myJoinType;
	
	private List<CCVector2d> _myPoints = new ArrayList<CCVector2d>();
	private List<Float> _myPointAlphas = new ArrayList<Float>();
	
	private List<CCVector2d> _myTriangles = new ArrayList<CCVector2d>();
	private List<Float> _myAlphas = new ArrayList<Float>();

	public CCPath2D(final CCStrokeJoin theStrokeJoin, final float theStrokeWeight) {
		_myStrokeWeight = theStrokeWeight;
		strokeJoin(theStrokeJoin);
	}
	
	public void begin() {
		_myPoints.clear();
		_myPointAlphas.clear();
	}
	
	public void addPoint(final CCVector2d thePoint) {
		_myPoints.add(thePoint);
		_myPointAlphas.add(1f);
	}
	
	public void addPoint(final float theX, final float theY) {
		_myPoints.add(new CCVector2d(theX, theY));
		_myPointAlphas.add(1f);
	}
	
	public void addPoint(final CCVector2d thePoint, final float theAlpha) {
		_myPoints.add(thePoint);
		_myPointAlphas.add(theAlpha);
	}
	
	public void addPoint(final float theX, final float theY, final float theAlpha) {
		_myPoints.add(new CCVector2d(theX, theY));
		_myPointAlphas.add(theAlpha);
	}
	
	public void strokeJoin(final CCStrokeJoin theStrokeJoin) {
		_myStrokeJoin = theStrokeJoin;
		switch(_myStrokeJoin) {
		case NONE:
			_myJoinType = new CCPath2DNoneJoinType();
			break;
		case MITER:
			_myJoinType = new CCPath2DMiterJoinType();
			break;
		}
	}
	
	public void strokeWeight(final float theStrokeWeight) {
		_myStrokeWeight = theStrokeWeight;
	}
	
	public void end() {
		List<CCPathSegment> mySegments = new ArrayList<CCPathSegment>();
		
		for(int i = 0; i < _myPoints.size() - 1; i++) {
			mySegments.add(new CCPathSegment(
				_myPoints.get(i),_myPoints.get(i+1),
				_myPointAlphas.get(i),_myPointAlphas.get(i+1),
				_myStrokeWeight)
			);
		}
		
		if(mySegments.size()<=0)return;
		
		for(int i = 0;i < mySegments.size()-1;i++) {
			_myJoinType.createTriangles(mySegments.get(i), mySegments.get(i+1));
		}
		_myJoinType.createTriangles(mySegments.get(mySegments.size() - 1), null);
	}
	
	public void clear() {
		_myTriangles.clear();
		_myAlphas.clear();
	}
	
	public void draw(CCGraphics g) {
			g.beginShape(CCDrawMode.TRIANGLES);
			for(CCVector2d myVertex:_myTriangles) {
				g.vertex(myVertex.x,myVertex.y);
			}
			
			g.endShape();
	}
	
	public void draw(CCGraphics g, final CCColor theColor) {
			g.beginShape(CCDrawMode.TRIANGLES);
			for(int i = 0; i < _myTriangles.size();i++) {
				g.color(theColor.red(), theColor.green(), theColor.blue(),_myAlphas.get(i));
				g.vertex(_myTriangles.get(i));
			}
			
			g.endShape();
	}
	
	private static enum CCPathIntersection{
		COINCIDENT,PARALLEL,INTERSECTING
	}
	
	private CCPathIntersection intersect(
		final CCVector2d theBegin1,
		final CCVector2d theEnd1,
		final CCVector2d theBegin2,
		final CCVector2d theEnd2,
		final CCVector2d theIntersection
	) {
		double denom = 
			((theEnd2.y - theBegin2.y) * (theEnd1.x - theBegin1.x)) - 
			((theEnd2.x - theBegin2.x) * (theEnd1.y - theBegin1.y));

		double nume_a = 
			((theEnd2.x - theBegin2.x) * (theBegin1.y - theBegin2.y)) - 
			((theEnd2.y - theBegin2.y) * (theBegin1.x - theBegin2.x));

		double nume_b = 
			((theEnd1.x - theBegin1.x) * (theBegin1.y - theBegin2.y)) - 
			((theEnd1.y - theBegin1.y) * (theBegin1.x - theBegin2.x));

		if (denom == 0.0f) {
			if (nume_a == 0.0f && nume_b == 0.0f) {
				return CCPathIntersection.COINCIDENT;
			}
			return CCPathIntersection.PARALLEL;
		}

		double ua = nume_a / denom;
		double ub = nume_b / denom;

		// Get the intersection point.
		theIntersection.x = theBegin1.x + ua * (theEnd1.x - theBegin1.x);
		theIntersection.y = theBegin1.y + ua * (theEnd1.y - theBegin1.y);

		return CCPathIntersection.INTERSECTING;
	}
}
