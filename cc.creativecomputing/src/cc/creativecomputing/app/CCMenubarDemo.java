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
package cc.creativecomputing.app;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.CCApplicationSettings;

public class CCMenubarDemo extends CCApp {
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#frameSetup()
	 */
	@Override
	public void frameSetup() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JMenuBar _myMenuBar = new JMenuBar();
		_myFrame.setJMenuBar(_myMenuBar);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false); //so menus appear above GLCanvas 
		

      JMenu options = new JMenu("Options"); 
      options.add(new JCheckBoxMenuItem("Wireframe")); 
      options.add(new JCheckBoxMenuItem("Head Light")); 
      options.add(new JCheckBoxMenuItem("Directional Light"));
      options.add(new JCheckBoxMenuItem("Specular Lighting")); 
      options.add(new JCheckBoxMenuItem("Two-sided Lighting")); 

      JMenu levels = new JMenu("Level"); 
      ButtonGroup lbg = new ButtonGroup(); 
      JRadioButtonMenuItem[] levelItems = new JRadioButtonMenuItem[7]; 
      for (int i = 0; i < levelItems.length; i++) { 
          levelItems[i] = new JRadioButtonMenuItem(i + ""); 
          levels.add(levelItems[i]); 
          lbg.add(levelItems[i]); 
          
      } 
      _myMenuBar.add(levels); 
      
	}

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

	}

	public static void main(String[] args) {
		
		
		CCApplicationManager myManager = new CCApplicationManager(CCMenubarDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

