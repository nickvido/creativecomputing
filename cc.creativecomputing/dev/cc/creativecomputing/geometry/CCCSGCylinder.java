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

import cc.creativecomputing.geometry.CSG;
import cc.creativecomputing.geometry.Construct;
import cc.creativecomputing.geometry.Example;
import cc.creativecomputing.geometry.Optional;
import cc.creativecomputing.geometry.function;
import cc.creativecomputing.geometry.parameter;
import cc.creativecomputing.geometry.solid;
import cc.creativecomputing.geometry.stack;
import cc.creativecomputing.geometry.the;
import cc.creativecomputing.geometry.var;

/**
 * @author christianriekoff
 *
 */
public class CCCSGCylinder {
	* Construct a solid cylinder. Optional parameters are `start`, `end`,
	* `radius`, and `slices`, which default to `[0, -1, 0]`, `[0, 1, 0]`, `1`, and
	* `16`. The `slices` parameter controls the tessellation.
	* 
	* Example usage:
	* 
*	     var cylinder = CSG.cylinder({
*	       start: [0, -1, 0],
*	       end: [0, 1, 0],
*	       radius: 1,
*	       slices: 16
*	     });
	CSG.cylinder = function(options) {
	  options = options || {};
	  var s = new CSG.Vector(options.start || [0, -1, 0]);
	  var e = new CSG.Vector(options.end || [0, 1, 0]);
	  var ray = e.minus(s);
	  var r = options.radius || 1;
	  var slices = options.slices || 16;
	  var axisZ = ray.unit(), isY = (Math.abs(axisZ.y) > 0.5);
	  var axisX = new CSG.Vector(isY, !isY, 0).cross(axisZ).unit();
	  var axisY = axisX.cross(axisZ).unit();
	  var start = new CSG.Vertex(s, axisZ.negated());
	  var end = new CSG.Vertex(e, axisZ.unit());
	  var polygons = [];
	  function point(stack, slice, normalBlend) {
	    var angle = slice * Math.PI * 2;
	    var out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
	    var pos = s.plus(ray.times(stack)).plus(out.times(r));
	    var normal = out.times(1 - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
	    return new CSG.Vertex(pos, normal);
	  }
	  for (var i = 0; i < slices; i++) {
	    var t0 = i / slices, t1 = (i + 1) / slices;
	    polygons.push(new CSG.Polygon([start, point(0, t0, -1), point(0, t1, -1)]));
	    polygons.push(new CSG.Polygon([point(0, t1, 0), point(0, t0, 0), point(1, t0, 0), point(1, t1, 0)]));
	    polygons.push(new CSG.Polygon([end, point(1, t1, 1), point(1, t0, 1)]));
	  }
	  return CSG.fromPolygons(polygons);
	};
}
