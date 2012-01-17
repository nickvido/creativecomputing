package cc.creativecomputing.xml;



public interface CCIXMLFeedbackHandler<XMLTool extends CCAbstractXMLTool>{
	public void onXMLFeedback(final XMLTool theTool);
}
