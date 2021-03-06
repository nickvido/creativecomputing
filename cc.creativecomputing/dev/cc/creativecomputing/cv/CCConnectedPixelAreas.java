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
package cc.creativecomputing.cv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.math.CCVector2i;

/**
 * @author christianriekoff
 * 
 */
public class CCConnectedPixelAreas {

	private Map<Integer, CCConnectedPixelArea> _myPixelAreaMap = new HashMap<Integer, CCConnectedPixelArea>();

	/**
	 * Checks the given two rows for connectivity and adds the connected pixel rows to the related areas.
	 * @param theRuns1
	 * @param theRuns2
	 */
	private void store_runs(List<CCConnectedPixelRow> theRuns1, List<CCConnectedPixelRow> theRuns2) {
		CCConnectedPixelArea p_blob;
		CCConnectedPixelArea c_blob;

		for (CCConnectedPixelRow run1 : theRuns1) {
			for (CCConnectedPixelRow run2 : theRuns2) {
				if (run1.isConnectedWith(run2)) {
					p_blob = _myPixelAreaMap.get(run1.label());
					c_blob = _myPixelAreaMap.get(run2.label());
					
					while (p_blob.hasParent()) {
						p_blob = p_blob.parent();
					}
					while (c_blob.hasParent()) {
						c_blob = c_blob.parent();
					}
					
					if (c_blob != p_blob) {
						p_blob.merge(c_blob); // destroys c_blobs runs_list
						c_blob.parent(p_blob);
					}
				}

			}
		}
	}

	private CCConnectedPixelRow createRow(final int theY, final int theStartX, final int theEndX) {
		CCConnectedPixelRow myPixelRow = new CCConnectedPixelRow(theY, theStartX, theEndX);
		CCConnectedPixelArea myPixelArea = new CCConnectedPixelArea(myPixelRow);
		_myPixelAreaMap.put(myPixelRow.label(), myPixelArea);
		return myPixelRow;
	}
	
	/**
	 * Scans one pixel line for connected Rows of pixels
	 * @param theRaster
	 * @param theY
	 * @param theThreshold
	 * @return a list with the connected pixel rows
	 */
	private List<CCConnectedPixelRow> scanPixelLine(final CCPixelRaster theRaster, final int theY, final int theThreshold) {
		int myRowStartX = 0;
		int myRowEndX = 0;
		boolean myLastIsPixelOverThreshold = theRaster.get(0, theY) > theThreshold;
		
		List<CCConnectedPixelRow> myResult = new ArrayList<CCConnectedPixelRow>();
		
		for (int x = 0; x < theRaster.width(); x++) {
			boolean myIsPixelOverThreshold = theRaster.get(x, theY) > theThreshold;
			
			if (myLastIsPixelOverThreshold != myIsPixelOverThreshold) {
				myRowEndX = x - 1;
				
				if (myLastIsPixelOverThreshold)
					myResult.add(createRow(theY, myRowStartX, myRowEndX));
				
				myRowStartX = x;
				myLastIsPixelOverThreshold = myIsPixelOverThreshold;
			}
		}
		
		if (myLastIsPixelOverThreshold)
			myResult.add(createRow(theY, myRowStartX, theRaster.width() - 1));
		
		return myResult;
	}

	/**
	 * Scans the given raster for connected Pixel areas. Connected pixel area
	 * are area with pixel with a value higher than the given threshold.
	 * @param theRaster the source raster
	 * @param theThreshold the threshold
	 * @return list with connected pixel areas
	 */
	public List<CCConnectedPixelArea> connectedPixelAreas(final CCPixelRaster theRaster, int theThreshold) {
		_myPixelAreaMap.clear();

		CCVector2i mySize = new CCVector2i(theRaster.width(), theRaster.height());

		List<CCConnectedPixelRow> myLastRows = scanPixelLine(theRaster, 0, theThreshold);
		
		// All other lines
		for (int y = 1; y < mySize.y(); y++) {
			List<CCConnectedPixelRow> myCurrentRows = scanPixelLine(theRaster, y, theThreshold);
				
			store_runs(myLastRows, myCurrentRows);
			myLastRows = myCurrentRows;
		}
		
		List<CCConnectedPixelArea> myResult = new ArrayList<CCConnectedPixelArea>();
		for (CCConnectedPixelArea myBlob : _myPixelAreaMap.values()) {
			if (!myBlob.hasParent()) {
				myResult.add(myBlob);
			}
		}

		return myResult;
	}
}
