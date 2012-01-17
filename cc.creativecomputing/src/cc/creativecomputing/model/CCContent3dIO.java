package cc.creativecomputing.model;

import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.model.obj.CCOBJParser;
import cc.creativecomputing.model.stl.CCSTLparser;

public class CCContent3dIO {
	
	public static enum CCContent3DFormat{
		OBJ,STL;
	}
	
	public static final CCContent3DFormat OBJ = CCContent3DFormat.OBJ;
	public static final CCContent3DFormat STL = CCContent3DFormat.STL;
	
	
	public static CCModel createModel(final String theFileName){
		final String myFormatExtension = CCIOUtil.fileExtension(theFileName).toUpperCase();
		CCContent3DFormat myFormat;
		try {
			myFormat = CCContent3DFormat.valueOf(myFormatExtension);
		} catch (RuntimeException e1) {
			throw new RuntimeException("The given format is not supported: " + myFormatExtension);
		}
		
		return createModel(theFileName,myFormat);
	}
	
	public static CCModel createModel(final String theFileName, CCContent3DFormat theFormat){
		final CCModel myModel = new CCModel();
		switch(theFormat){
		case OBJ:
			new CCOBJParser(theFileName,myModel).readFile();
			return myModel;
		case STL:
			new CCSTLparser(theFileName,myModel).readFile();
			return myModel;
		}
		
		throw new RuntimeException("The given format is not supported: " + theFormat);
	}
}
