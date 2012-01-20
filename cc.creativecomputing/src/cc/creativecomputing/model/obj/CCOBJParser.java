package cc.creativecomputing.model.obj;

import java.io.File;
import java.io.StreamTokenizer;

import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.model.CCFace;
import cc.creativecomputing.model.CCGroup;
import cc.creativecomputing.model.CCModel;
import cc.creativecomputing.model.CCSegment;
import cc.creativecomputing.model.material.CCModelMaterial;


/**
 * http://local.wasp.uwa.edu.au/~pbourke/dataformats/obj/
 * @author texone
 *
 */
public class CCOBJParser extends CCAbstractOBJParser{

	private final CCModel _myModel;
	private CCGroup _myCurrentGroup;
	private String _myCurrentMaterial;
	private CCSegment _myCurrentSegment;
	
	private String _mySourceFolder;
	
	public CCOBJParser(final String theFileName, final CCModel theModel) throws CCOBJParsingException {
		super(CCIOUtil.createReader(theFileName));
		
		_myModel = theModel;
		
		// creating the default material
		_myCurrentMaterial = "default";
		_myModel.materialMap().put(_myCurrentMaterial, CCModelMaterial.DEFAULT);
		
		_mySourceFolder = new File(theFileName).getParent();
		
		if(_mySourceFolder == null)_mySourceFolder = "";
		else _mySourceFolder += File.separator;
		
	}
	
	private void readVertex() throws CCOBJParsingException {
		CCVector3f myVertex = new CCVector3f(
			getFloat(),
			getFloat(),
			getFloat()
		);
		_myModel.addVertex(myVertex);
		skipToNextLine();		
	}
	
	/**
	 * readNormal
	 */
	private void readNormal() throws CCOBJParsingException {
		_myModel.normals().add(new CCVector3f(
			getFloat(),
			getFloat(),
			getFloat()
		).normalize(0.2f));

		skipToNextLine();	
	}
	
	/**
	 * readTexture
	 */
	private void readTexture() throws CCOBJParsingException {
		_myModel.textureCoords().add(new CCVector2f(
			getFloat(),
			getFloat()
		));

		skipToNextLine();	
	}
	
	/**
	 * readFace
	 *
	 *    Adds the indices of the current face to the arrays.
	 *
	 *    ViewPoint files can have up to three arrays:  Vertex Positions,
	 *    Texture Coordinates, and Vertex Normals.  Each vertex can
	 *    contain indices into all three arrays.
	 */
	private void readFace() throws CCOBJParsingException {
		if(_myCurrentGroup == null) {
			// creating the default group
			_myCurrentGroup = new CCGroup("default");
			
			// adding default variables to the global data table
			_myModel.groupMap().put(_myCurrentGroup.groupName(), _myCurrentGroup);
		}
		if(_myCurrentSegment == null){
			_myCurrentSegment = new CCSegment(_myModel);
			_myCurrentSegment.materialName(_myCurrentMaterial);
			_myCurrentGroup.segments().add(_myCurrentSegment);
		}
		final CCFace myFace = new CCFace(_myModel);
		
		int vertIndex, texIndex = 0, normIndex = 0;
		int count = 0;

		//   There are n vertices on each line.  Each vertex is comprised
		//   of 1-3 numbers separated by slashes ('/').  The slashes may
		//   be omitted if there's only one number.

		getToken();

		while (ttype != StreamTokenizer.TT_EOL) {
			// First token is always a number (or EOL)
			pushBack();
			vertIndex = getInt() - 1;
			if (vertIndex < 0)
				vertIndex += _myModel.vertices().size() + 1;
			myFace.vertexIndices().add(vertIndex);

			// Next token is a slash, a number, or EOL.  Continue on slash
			getToken();
			if (ttype == '/') {

				// If there's a number after the first slash, read it
				getToken();
				if (ttype == StreamTokenizer.TT_WORD) {
					// It's a number
					pushBack();
					texIndex = getInt() - 1;
					if (texIndex < 0)
						texIndex += _myModel.textureCoords().size() + 1;
					myFace.textureIndices().add(texIndex);
					getToken();
				}

				// Next token is a slash, a number, or EOL.  Continue on slash
				if (ttype == '/') {

					// There has to be a number after the 2nd slash
					normIndex = getInt() - 1;
					if (normIndex < 0)
						normIndex += _myModel.normals().size() + 1;
					myFace.normalIndices().add(normIndex);
					getToken();
				}
			}
			count++;
		}
		
		_myCurrentSegment.faces().add(myFace);
		_myModel.faces().add(myFace);

		// In case we exited early
		skipToNextLine();
	}
	
	/**
	 * readPartName
	 */
	private void readPartName() throws CCOBJParsingException {
		String groupName = "default";
			
		getToken();

		StringBuilder parsedGroupName = new StringBuilder();
		// New faces will be added to the curGroup
		while (ttype != StreamTokenizer.TT_EOL) {
//		if (ttype == OBJParser.TT_WORD)
			parsedGroupName.append(sval);
			getToken();
		}
		
		if(parsedGroupName.length() > 0)groupName = parsedGroupName.toString();

		// See if this group has Material Properties yet
		if (_myModel.groupMap().get(groupName) == null) {
			// It doesn't - carry over from last group
			final CCGroup newGroup = new CCGroup(groupName);
			_myModel.groupMap().put(groupName, newGroup);
		}
		_myCurrentGroup = _myModel.groupMap().get(groupName);
		
		_myCurrentSegment = null;

		skipToNextLine();
	}
	
	/**
	 * readSmoothingGroup
	 * Implement smoothing groups
	 */
	private void readSmoothingGroup() throws CCOBJParsingException {
		getToken();
		if (ttype != CCOBJParser.TT_WORD) {
			skipToNextLine();
			return;
		}
//		if (st.sval.equals("off"))
//			curSgroup = "0";
//		else
//			curSgroup = st.sval;
		skipToNextLine();
	}
	
	/**
	 * loadMaterialFile
	 *
	 *	Both types of slashes are returned as tokens from our parser,
	 *	so we go through the line token by token and keep just the
	 *	last token on the line.  This should be the filename without
	 *	any directory info.
	 */
	private void loadMaterialFile() throws CCOBJParsingException {
		String s = null;

		// Filenames are case sensitive
		lowerCaseMode(false);

		// Get name of material file (skip path)
		do {
			getToken();
			if (ttype == CCOBJParser.TT_WORD)
				s = sval;
		} while (ttype != CCOBJParser.TT_EOL);
		
		try {
			new CCMTLParser(_mySourceFolder+s,_myModel.materialMap()).readFile();
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

		lowerCaseMode(true);
		skipToNextLine();
	}
	
	/**
	 * readMaterialName
	 */
	private void readMaterialName() throws CCOBJParsingException {
		getToken();
		if (ttype == CCOBJParser.TT_WORD) {
			String myMaterial = sval;
			_myCurrentSegment = new CCSegment(_myModel);
			_myCurrentSegment.materialName(myMaterial);
			_myCurrentMaterial = myMaterial;
			_myCurrentGroup.segments().add(_myCurrentSegment);
		}
		skipToNextLine();
	}

	@Override
	public void readFile(){
		
		getToken();
		while (ttype != CCOBJParser.TT_EOF) {
			if (ttype == CCOBJParser.TT_WORD) {
				if (sval.equals("v")) {
					readVertex();
				} else if (sval.equals("vn")) {
					readNormal();
				} else if (sval.equals("vt")) {
					readTexture();
				} else if (sval.equals("f")) {
					readFace();
				} else if (sval.equals("fo")) { // Not sure what the dif is
					readFace();
				} else if (sval.equals("g")) {
					readPartName();
				}  else if (sval.equals("o")) {
					readPartName();
				} else if (sval.equals("s")) {
					readSmoothingGroup();
				} else if (sval.equals("p")) {
					skipToNextLine();
				} else if (sval.equals("l")) {
					skipToNextLine();
				} else if (sval.equals("mtllib")) {
					loadMaterialFile();
				} else if (sval.equals("usemtl")) {
					readMaterialName();
				} else if (sval.equals("maplib")) {
					skipToNextLine();
				} else if (sval.equals("usemap")) {
					skipToNextLine();
				} else {
					throw new CCOBJParsingException("Unrecognized token, line " + lineno());
				}
			}

			skipToNextLine();

			// Get next token
			getToken();
		}
	}

}