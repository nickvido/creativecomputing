package cc.creativecomputing.model.obj;

import java.io.Reader;

import cc.creativecomputing.io.CCAbstractFileParser;


abstract class CCAbstractOBJParser extends CCAbstractFileParser {
	

	// ObjectFileParser constructor
	CCAbstractOBJParser(Reader r) throws CCOBJParsingException{
		super(r);
		setup();
	}
	
	public abstract void readFile()throws CCOBJParsingException;

	@Override
	/**
	 * Sets up StreamTokenizer for reading ViewPoint .obj file format.
	 */
	public void setup() {
		// keep setup of the file parser
		super.setup();

		// Comment from ! to end of line
		commentChar('!');

		// These characters returned as tokens
		ordinaryChar('#');
		ordinaryChar('/');
	}
}
