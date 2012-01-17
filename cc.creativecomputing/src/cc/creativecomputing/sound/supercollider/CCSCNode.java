package cc.creativecomputing.sound.supercollider;

public class CCSCNode {
	
	public enum NodeType {
		SYNTH,
		GROUP
	}
	
	public int id;
	public int parentId;
	public int previousId;
	public int nextId;
	public NodeType type;
	public int headId;
	public int tailId;

}
