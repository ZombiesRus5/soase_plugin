package sose.tools;

public interface ContentHandler {
	public void startEntity(String entityType, int lineNumber);
	public void startStructure(String structureName, String structureType, int lineNumber);
	public void processField(String fieldName, String fieldValue, String fieldType, int lineNumber);
	public void endStructure();
	public void endEntity();
}
