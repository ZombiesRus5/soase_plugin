package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BuffReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	EntityParser parser = null;
	
	public BuffReporter(EntityParser parser, ContentHandler reporter, ErrorHandler errorHandler, boolean validateCost) {
		super(reporter);
		this.error = errorHandler;
		
		this.parser = parser;
	}

	String stackingLimit;
	String allowFirstSpawnerToStack = null;
	
	String currentStructure = null;
	LinkedList<String> structures = new LinkedList<String>();
	static List<String> instantActionTriggerTypes = new ArrayList<String>();
	static List<String> periodicActionTriggerTypes = new ArrayList<String>();
	
	static {
		instantActionTriggerTypes.add("OnDelay");
		instantActionTriggerTypes.add("OnCondition");
		instantActionTriggerTypes.add("OnWeaponFired");
		instantActionTriggerTypes.add("OnDamageTaken");
		instantActionTriggerTypes.add("OnAbilityUsed");
		instantActionTriggerTypes.add("OnHyperspaceExit");
		instantActionTriggerTypes.add("OnOwnerDeath");
		instantActionTriggerTypes.add("OnBuffFinish");
		instantActionTriggerTypes.add("OnColonizeAbilityUsed");
		
		periodicActionTriggerTypes.add("AlwaysPerform");
		periodicActionTriggerTypes.add("OnChance");
		periodicActionTriggerTypes.add("OnCondition");
		periodicActionTriggerTypes.add("OnWeaponFired");
		periodicActionTriggerTypes.add("OnDamageTaken");
		periodicActionTriggerTypes.add("OnAbilityUsed");
		periodicActionTriggerTypes.add("OnHyperspaceExit");
		periodicActionTriggerTypes.add("OnOwnerDeath");
		periodicActionTriggerTypes.add("OnBuffFinish");

	}
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		currentStructure = structureName;
		super.startStructure(structureName, structureType, lineNumber);
		structures.push(structureName);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("stackingLimit")) {
			stackingLimit = fieldValue;
		} else if (fieldName.equals("allowFirstSpawnerToStack")) {
			allowFirstSpawnerToStack = fieldValue;
			if ("-1".equals(stackingLimit) || "0".equals(stackingLimit)) {
				// this field shouldn't be set
				String message = MessageFormat.format("allowFirstSpawnerToStack should not be set when stackingLimit is {0}", stackingLimit);
				error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("buffExclusivityForAIType")) {
			if (Integer.parseInt(stackingLimit) > 0 && allowFirstSpawnerToStack == null) {
				String message = "allowFirstSpawnerToStack should be set when stackingLimit is >0";
				error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("instantActionTriggerType")) {
			String instantActionTriggerType = fieldValue;
			if (!instantActionTriggerTypes.contains(instantActionTriggerType) && structures.contains("instantAction")) {
				String message = MessageFormat.format("instantActionTriggerType generally should not be set to {0}", instantActionTriggerType);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
			if (!periodicActionTriggerTypes.contains(instantActionTriggerType) && structures.contains("periodicAction")) {
				String message = MessageFormat.format("instantActionTriggerType generally should not be set to {0}", instantActionTriggerType);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldValue.equals("IncreaseOwnerAbilityLevel")) {
			String message = MessageFormat.format("Please confirm Frigate ability is set to Intrinsic or Passive when buffed with {0}", fieldValue);
			error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
		}
		super.processField(fieldName, fieldValue, fieldType, lineNumber);
	}

	public ErrorHandler getErrorHandler() {
		return error;
	}

	public void setErrorHandler(ErrorHandler error) {
		this.error = error;
	}

	@Override
	public void endStructure() {
		// TODO Auto-generated method stub
		super.endStructure();
		if (structures.size() > 0) {
			structures.pop();
		}
	}	
}
