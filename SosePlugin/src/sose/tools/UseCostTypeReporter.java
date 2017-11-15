package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class UseCostTypeReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	EntityParser parser = null;
	
	public UseCostTypeReporter(EntityParser parser, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.parser = parser;
	}

	String currentStructure = null;
	String aiUseTime = null;
	int aiUseTimeLineNumber = 0;
	String aiUseTargetCondition = null;
	int aiUseTargetConditionLineNumber = 0;
	String isAutoCastAvailable = null;
	int isAutoCastAvailableLineNumber = 0;
	String isAutoCastOnByDefault = null;
	int isAutoCastOnByDefaultLineNumber = 0;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("aiUseTime")) {
			aiUseTime = fieldValue;
			aiUseTimeLineNumber = lineNumber;
		} else if (fieldName.equals("aiUseTargetCondition")) {
			aiUseTargetCondition = fieldValue;
			aiUseTargetConditionLineNumber = lineNumber;
		} else if (fieldName.equals("isAutoCastAvailable")) {
			isAutoCastAvailable = fieldValue;
			isAutoCastAvailableLineNumber = lineNumber;
		} else if (fieldName.equals("isAutoCastOnByDefault")) {
			isAutoCastOnByDefault = fieldValue;
			isAutoCastOnByDefaultLineNumber = lineNumber;
		} else if (fieldName.equals("useCostType") && fieldValue.equals("Passive")) {
			if (!"Invalid".equals(aiUseTime)) {
				String message = "aiUseTime must be Invalid for passive effects";
				error.warn(new EntityParseException(message, aiUseTimeLineNumber, "aiUseTime"));
			}
			if (!"Invalid".equals(aiUseTargetCondition)) {
				String message = "aiUseTargetCondition must be Invalid for passive effects";
				error.warn(new EntityParseException(message, aiUseTargetConditionLineNumber, "aiUseTargetCondition"));
			}
			if (!"FALSE".equals(isAutoCastAvailable)) {
				String message = "isAutoCastAvailable must be FALSE for passive effects";
				error.warn(new EntityParseException(message, isAutoCastAvailableLineNumber, "isAutoCastAvailable"));
			}
			if (!"FALSE".equals(isAutoCastOnByDefault)) {
				String message = "isAutoCastOnByDefault must be FALSE for passive effects";
				error.warn(new EntityParseException(message, isAutoCastOnByDefaultLineNumber, "isAutoCastOnByDefault"));
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
