package cc.creativecomputing.animation;

public interface CCAnimation {

	public Object getTarget();
	
	public void update(float theDeltaTime);
	
	public boolean finished();
	
	public String targetValueName();
}
