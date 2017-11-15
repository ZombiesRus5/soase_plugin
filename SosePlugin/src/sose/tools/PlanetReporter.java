package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class PlanetReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	
	public PlanetReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.fileReference = fileReference;
		this.parser = parser;
	}
	
	String currentStructure = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		currentStructure = structureName;
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("ability:0") ||
				fieldName.equals("ability:1") ||
				fieldName.equals("ability:2") ||
				fieldName.equals("ability:3") ||
				fieldName.equals("ability:4")) {
			String levelSourceType = parser.getMetaData(fieldValue, "levelSourceType");
			if (levelSourceType == null) {
//				String message = MessageFormat.format("levelSourceType unknown {0}", levelSourceType);
//				error.error(new EntityParseException(message, lineNumber, fieldName));
			} else if (levelSourceType.equals("FixedLevel0")) {
				// we're good here
			} else {
				String message = MessageFormat.format("levelSourceType should not be set to {0}", levelSourceType);
				error.error(new EntityParseException(message, lineNumber, fieldName));
			}
			String useCostType = parser.getMetaData(fieldValue, "useCostType");
			if (useCostType == null) {
//				String message = MessageFormat.format("useCostType unknown {0}", levelSourceType);
//				error.error(new EntityParseException(message, lineNumber, fieldName));
			} else if (useCostType.equals("Passive")) {
				// we're good here
			} else {
				String message = MessageFormat.format("useCostType should not be set to {0}", levelSourceType);
				error.error(new EntityParseException(message, lineNumber, fieldName));
			}
		}
		super.processField(fieldName, fieldValue, fieldType, lineNumber);
	}

	public ErrorHandler getErrorHandler() {
		return error;
	}

	public void setErrorHandler(ErrorHandler error) {
		this.error = error;
	}	
}
