package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class ResearchSubjectReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	EntityParser parser = null;
	
	public ResearchSubjectReporter(EntityParser parser, ContentHandler reporter, ErrorHandler errorHandler, boolean validateCost) {
		super(reporter);
		this.error = errorHandler;
		this.validateCost = validateCost;
		this.parser = parser;
	}

	String position;
	String researchField;
	
	boolean validateCost = true;
	String currentStructure = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		currentStructure = structureName;
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("pos")) {
			try {
				StringTokenizer stk = new StringTokenizer(fieldValue, "[ ", false);
				position = (String)stk.nextElement();
			} catch(Exception e) {
				// something else will validate for valid positions
			}
		} else if (fieldName.equals("Tier")) {
			if (!fieldValue.equals(position) && !"Artifact".equals(researchField)) {
				String message = MessageFormat.format("Tier {0} does not match block position {1}", fieldValue, position);
				error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("ResearchField")) {
			researchField = fieldValue;
		} else if (!"Artifact".equals(researchField) && validateCost && fieldName.equals("credits")) {
			BigDecimal expectedValue = getCostForTier(position, currentStructure, fieldName);
			BigDecimal value = new BigDecimal(fieldValue);
			if (expectedValue != null && expectedValue.compareTo(value) != 0) {
				String message = MessageFormat.format("{0} credits of {1} does not match expected amount of {2} for Tier {3}", currentStructure, fieldValue, expectedValue, position);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (!"Artifact".equals(researchField) && validateCost && fieldName.equals("metal")) {
			BigDecimal expectedValue = getCostForTier(position, currentStructure, fieldName);
			BigDecimal value = new BigDecimal(fieldValue);
			if (expectedValue != null && expectedValue.compareTo(value) != 0) {
				String message = MessageFormat.format("{0} metal of {1} does not match expected amount of {2} for Tier {3}", currentStructure, fieldValue, expectedValue, position);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (!"Artifact".equals(researchField) && validateCost && fieldName.equals("crystal")) {
			BigDecimal expectedValue = getCostForTier(position, currentStructure, fieldName);
			BigDecimal value = new BigDecimal(fieldValue);
			if (expectedValue != null && expectedValue.compareTo(value) != 0) {
				String message = MessageFormat.format("{0} crystal of {1} does not match expected amount of {2} for Tier {3}", currentStructure, fieldValue, expectedValue, position);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (!"Artifact".equals(researchField) && validateCost && fieldName.equals("BaseUpgradeTime")) {
			BigDecimal expectedValue = parser.getUpgradeTime(position, fieldName);
			BigDecimal value = new BigDecimal(fieldValue);
			if (expectedValue != null && expectedValue.compareTo(value) != 0) {
				String message = MessageFormat.format("BaseUpgradeTime of {1} does not match expected amount of {2} for Tier {3}", currentStructure, fieldValue, expectedValue, position);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (!"Artifact".equals(researchField) && validateCost && fieldName.equals("PerLevelUpgradeTime")) {
			BigDecimal expectedValue = parser.getUpgradeTime(position, fieldName);
			BigDecimal value = new BigDecimal(fieldValue);
			if (expectedValue != null && expectedValue.compareTo(value) != 0) {
				String message = MessageFormat.format("PerLevelUpgradeTime of {1} does not match expected amount of {2} for Tier {3}", currentStructure, fieldValue, expectedValue, position);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} 
		super.processField(fieldName, fieldValue, fieldType, lineNumber);
	}

	private BigDecimal getCostForTier(String tier, String costStructure, String costType) {
		
		return parser.getResearchCost(tier, costStructure, costType);
	}

	public ErrorHandler getErrorHandler() {
		return error;
	}

	public void setErrorHandler(ErrorHandler error) {
		this.error = error;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
}
