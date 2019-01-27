package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class AbilityReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	
	public AbilityReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.fileReference = fileReference;
		this.parser = parser;
	}

	String needsToFaceTarget;
	String buffInstantActionType = null;
	
	String currentStructure = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		currentStructure = structureName;
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("buffInstantActionType")) {
			buffInstantActionType = fieldValue;
		} else if (fieldName.equals("needsToFaceTarget")) {
			needsToFaceTarget = fieldValue;
			if (needsToFaceTarget.contains("ApplyBuffToSelf") || needsToFaceTarget.contains("ApplyBuffToTargets")) {
				// this field shouldn't be set
				String message = MessageFormat.format("needsToFaceTarget should not be set to TRUE when applying buff to self or multiple targets", needsToFaceTarget);
				error.warn(new EntityParseException(message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("instantActionTriggerType")) {
			String instantActionTriggerType = fieldValue;
			if (instantActionTriggerType.equals("OnDelay")) {
				String message = MessageFormat.format("instantActionTriggerType should not be set to {0}", instantActionTriggerType);
				error.warn(new EntityParseException(message, lineNumber, fieldName));

			}
		} else if (fieldName.equals("levelSourceType") 
				|| fieldName.equals("useCostType")) {
			parser.setMetaData(fileReference.getFileName(), fieldName, fieldValue);
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
