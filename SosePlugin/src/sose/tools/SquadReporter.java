package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class SquadReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	
	public SquadReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
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
//				error.error(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			} else if (levelSourceType.equals("ResearchStartsAt0") 
					|| levelSourceType.equals("FixedLevel0")
					|| levelSourceType.equals("ResearchWithBase")
					|| levelSourceType.equals("ResearchWithoutBase")) {
				// we're good here
			} else if (levelSourceType.equals("Intrinsic")) {
				String message = MessageFormat.format("levelSourceType should not be set to {0}", levelSourceType);
				error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			} else {
				String message = MessageFormat.format("levelSourceType should not be set to {0}", levelSourceType);
				error.error(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
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
