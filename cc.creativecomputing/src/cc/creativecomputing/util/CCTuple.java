package cc.creativecomputing.util;


public class CCTuple<Type1, Type2> {

	private Type1 _myFirst;
	private Type2 _mySecond;
	
	
	
	public CCTuple(Type1 theFirst, Type2 theSecond) {
		super();
		_myFirst = theFirst;
		_mySecond = theSecond;
	}

	public Type1 first(){
		return _myFirst;
	}
	
	public Type2 second() {
		return _mySecond;
	}
}
