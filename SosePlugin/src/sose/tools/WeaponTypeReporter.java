package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class WeaponTypeReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	EntityParser parser = null;
	
	public WeaponTypeReporter(EntityParser parser, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.parser = parser;
	}

	String currentStructure = null;
	String weaponType = null;
	String weaponEffectType = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		if (structureName.equals("Weapon")) {
			weaponType = null;
			weaponEffectType = null;
		}
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("WeaponType")) {
			weaponType = fieldValue;
		} else if (fieldName.equals("weaponType")) {
			weaponEffectType = fieldValue;
			if (weaponType != null && weaponEffectType != null && !weaponType.equals(weaponEffectType)) {
				String message = MessageFormat.format("WeaponEffect's weaponType '{0}' must match Weapon's WeaponType '{1}'", weaponEffectType, weaponType);
				error.warn(new EntityParseException(message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("TravelSpeed")) {
			BigDecimal value = new BigDecimal(fieldValue);
			if ("Beam".equals(weaponType)) {
				// travelSpeed should be 0.000000
				if (value.compareTo(new BigDecimal("0.0")) != 0) {
					String message = "TravelSpeed for Beam weapons should be 0.00000";
					error.warn(new EntityParseException(message, lineNumber, fieldName));
				}
			} else {
				// travelSpeed should be 0.000000
				if (value.compareTo(new BigDecimal("0.0")) <= 0) {
					String message = MessageFormat.format("TravelSpeed for {0} weapons should be greater than 0.00000", weaponType);
					error.warn(new EntityParseException(message, lineNumber, fieldName));
				}
				
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
