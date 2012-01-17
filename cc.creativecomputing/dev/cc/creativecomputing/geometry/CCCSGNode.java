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
package cc.creativecomputing.geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a node in a BSP tree. A BSP tree is built from a collection of polygons by picking a polygon to split along.
 * That polygon (and all other coplanar polygons) are added directly to that node and the other polygons are added to
 * the front and/or back subtrees. This is not a leafy BSP tree since there is no distinction between internal and leaf
 * nodes.
 * 
 * @author christianriekoff
 * 
 */
public class CCCSGNode {
	// # class Node
	public CCCSGPlane plane;

	public List<CCCSGPolygon> polygons;

	public CCCSGNode front;
	public CCCSGNode back;

	public CCCSGNode(List<CCCSGPolygon> polygons) {
		this.plane = null;
		this.front = null;
		this.back = null;
		this.polygons = polygons;
		if (polygons != null)
			this.build(polygons);
	};

	public CCCSGNode clone() {
		CCCSGNode node = new CCCSGNode(new ArrayList<CCCSGPolygon>());
		node.plane = this.plane != null ? this.plane.clone() : null;
		node.front = this.front != null ? this.front.clone() : null;
		node.back = this.back != null ? this.back.clone() : null;

		node.polygons = new ArrayList<CCCSGPolygon>();
		for (CCCSGPolygon myPolygon : polygons) {
			node.polygons.add(myPolygon);
		}
		return node;
	}

	// Convert solid space to empty space and empty space to solid space.
	public void invert() {
		for (CCCSGPolygon myPolygon : polygons) {
			myPolygon.flip();
		}
		this.plane.flip();
		if (this.front != null)
			this.front.invert();
		if (this.back != null)
			this.back.invert();
		CCCSGNode temp = this.front;
		this.front = this.back;
		this.back = temp;
	}

	/**
	 * Recursively remove all polygons in `polygons` that are inside this BSP
	 * tree.
	 */
	public List<CCCSGPolygon> clipPolygons(List<CCCSGPolygon> polygons) {
		if (this.plane == null)
			return polygons.slice();
		List<CCCSGPolygon> front = new ArrayList<CCCSGPolygon>();
		List<CCCSGPolygon> back = new ArrayList<CCCSGPolygon>();
		for (CCCSGPolygon myPolygon : polygons) {
			this.plane.splitPolygon(myPolygon, front, back, front, back);
		}
		if (this.front != null)
			front = this.front.clipPolygons(front);
		if (this.back != null)
			back = this.back.clipPolygons(back);
		else
			back = new ArrayList<CCCSGPolygon>();
		front.addAll(back);
		return front;
	}

	/**
	 * Remove all polygons in this BSP tree that are inside the other BSP tree
	 * `bsp`.
	 */
	public void clipTo(CCCSGNode bsp) {
		this.polygons = bsp.clipPolygons(this.polygons);
		if (this.front != null)
			this.front.clipTo(bsp);
		if (this.back != null)
			this.back.clipTo(bsp);
	}

	// Return a list of all polygons in this BSP tree.
	public List<CCCSGPolygon> allPolygons() {
		List<CCCSGPolygon> polygons = this.polygons.slice();
		if (this.front != null)
			polygons.addAll(this.front.allPolygons());
		if (this.back != null)
			polygons.addAll(this.back.allPolygons());
		return polygons;
	}

	// Build a BSP tree out of `polygons`. When called on an existing tree, the
	// new polygons are filtered down to the bottom of the tree and become new
	// nodes there. Each set of polygons is partitioned using the first polygon
	// (no heuristic is used to pick a good split).
	public void build(List<CCCSGPolygon> polygons) {
		if (polygons == null || polygons.size() == 0)
			return;
		if (this.plane == null)
			this.plane = polygons.get(0).plane.clone();
		List<CCCSGPolygon> front = new ArrayList<CCCSGPolygon>();
		List<CCCSGPolygon> back = new ArrayList<CCCSGPolygon>();
		for (CCCSGPolygon myPolygon : polygons) {
			this.plane.splitPolygon(myPolygon, this.polygons, this.polygons, front, back);
		}
		if (front.size() > 0) {
			if (this.front == null)
				this.front = new CCCSGNode(null);
			this.front.build(front);
		}
		if (back.size() > 0) {
			if (this.back == null)
				this.back = new CCCSGNode(null);
			this.back.build(back);
		}
	}
}
