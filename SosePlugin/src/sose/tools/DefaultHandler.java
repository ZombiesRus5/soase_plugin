package sose.tools;

public class DefaultHandler implements ErrorHandler, ContentHandler,
		FileReferenceHandler {

	@Override
	public void error(EntityParseException e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fatal(EntityParseException e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(EntityParseException e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endEntity() {
		// TODO Auto-generated method stub

	}

	@Override
	public void endStructure() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startEntity(String entityType, int lineNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getFileExtension() {
		return null;
	}

	@Override
	public void setProperty(String property, String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProperty(String property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void info(EntityParseException e) {
		// TODO Auto-generated method stub
		
	}

}
