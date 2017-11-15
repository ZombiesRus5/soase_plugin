package sose.tools;

public interface DefinitionHandler {
	public void startEntity(String entityType, String helpText) throws Exception;
	public void startStructure(String structureName, String structureType, boolean explicit, String[] references, String helpText) throws Exception;
	public void field(String fieldName, String fieldRule, boolean explicit, String[] fieldValues, String fieldType, String[] references, String helpText) throws Exception;
	public void endStructure() throws Exception;
	public void endEntity() throws Exception;
	public void startConditionBlock(String field, String fieldRule, String[] conditions, String helpText) throws Exception;
	public void startCondition(String fieldName, String condition, String helpText) throws Exception;
	public void endCondition() throws Exception;
	public void endConditionBlock() throws Exception;
	public void startIteration(String fieldName, String helpText) throws Exception;
	public void endIteration() throws Exception;
}
