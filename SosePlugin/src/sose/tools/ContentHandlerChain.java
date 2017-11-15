package sose.tools;

public class ContentHandlerChain implements ContentHandler {
	ContentHandler chain = null;
	
	

	public ContentHandlerChain(ContentHandler chain) {
		super();
		this.chain = chain;
	}

	@Override
	public void endEntity() {
		chain.endEntity();
	}

	@Override
	public void endStructure() {
		chain.endStructure();
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		chain.processField(fieldName, fieldValue, fieldType, lineNumber);
	}

	@Override
	public void startEntity(String entityType, int lineNumber) {
		chain.startEntity(entityType, lineNumber);
	}

	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		chain.startStructure(structureName, structureType, lineNumber);
	}

	public ContentHandler getChain() {
		return chain;
	}

	public void setChain(ContentHandler chain) {
		this.chain = chain;
	}

}
