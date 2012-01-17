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
package cc.creativecomputing.io;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;

/**
 * @author christianriekoff
 * 
 */
public class CCClipboard implements ClipboardOwner {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
	 * java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard theArg0, Transferable theArg1) {
	// TODO Auto-generated method stub

	}

	private Clipboard _myClipboard;
	
	private static CCClipboard instance;
	
	public static  CCClipboard instance() {
		if(instance == null)instance = new CCClipboard();
		return instance;
	}

	private CCClipboard() {
		_myClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Place a String on the clipboard, and make this class the owner of the Clipboard's contents.
	 */
	public void setData(String theData) {
		StringSelection stringSelection = new StringSelection(theData);
		_myClipboard.setContents(stringSelection, this);
	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty String.
	 */
	public String getStringData() {
		String result = "";
		// odd: the Object param of getContents is not currently used
		Transferable myContents = _myClipboard.getContents(this);
		if(myContents == null)return result;
		if(!myContents.isDataFlavorSupported(DataFlavor.stringFlavor))return result;
		
		try {
			result = (String) myContents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
			// highly unlikely since we are using a standard DataFlavor
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
			
		return result;
	}
	
	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty String.
	 */
	public CCTextureData getTextureData() {
		CCTextureData result = null;
		// odd: the Object param of getContents is not currently used
		Transferable myContents = _myClipboard.getContents(null);
		
		if(myContents == null)return result;
		if(!myContents.isDataFlavorSupported(DataFlavor.imageFlavor))return result;
		
		try {
			Image myImage = (Image) myContents.getTransferData(DataFlavor.imageFlavor);
			result = CCTextureIO.newTextureData(myImage);
		} catch (UnsupportedFlavorException ex) {
			// highly unlikely since we are using a standard DataFlavor
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return result;
	}
	
	public List<File> getFileData(){
		// odd: the Object param of getContents is not currently used
		Transferable myContents = _myClipboard.getContents(null);
		
		if(myContents == null)return new ArrayList<File>();
		if(!myContents.isDataFlavorSupported(DataFlavor.imageFlavor))return new ArrayList<File>();
		
		try {
			return (List<File>) myContents.getTransferData(DataFlavor.javaFileListFlavor);
		} catch (UnsupportedFlavorException ex) {
			// highly unlikely since we are using a standard DataFlavor
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return new ArrayList<File>();
	}
	
	public static void main(String[] args) {
		CCClipboard myClipboard = CCClipboard.instance();
		System.out.println("String:" + myClipboard.getStringData());
		
		for(File myFile:myClipboard.getFileData()) {
			System.out.println(myFile);
		}
	}
}
