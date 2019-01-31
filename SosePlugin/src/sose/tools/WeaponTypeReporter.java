package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class WeaponTypeReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	int numWeapon = -1;
	
	public WeaponTypeReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.fileReference = fileReference;
		this.parser = parser;
	}

	String currentStructure = null;
	String weaponType = null;
	String weaponEffectType = null;
	
	private void validateMeshPointExists(String meshPoint, String structureName, int lineNumber) {
		String meshName = parser.getMetaData(fileReference.getFileName(), "meshName");
		HashMap<String, String> meshMetaData = parser.getMetaData(meshName);
		if (meshMetaData != null && !meshMetaData.isEmpty()) {
			if (!meshMetaData.containsKey("DataString." + meshPoint)) {
				error.warn(new EntityParseException(ValidationType.ENTITY, "Missing mesh point " + meshPoint + ", is this intended?", lineNumber, structureName));
			}
		}
	}
	
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
			numWeapon = numWeapon + 1;
		} else if (fieldName.equals("weaponType")) {
			weaponEffectType = fieldValue;
			if (weaponType != null && weaponEffectType != null && !weaponType.equals(weaponEffectType)) {
				String message = MessageFormat.format("WeaponEffect's weaponType '{0}' must match Weapon's WeaponType '{1}'", weaponEffectType, weaponType);
				error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("TravelSpeed")) {
			BigDecimal value = new BigDecimal(fieldValue);
			if ("Beam".equals(weaponType)) {
				// travelSpeed should be 0.000000
				if (value.compareTo(new BigDecimal("0.0")) != 0) {
					String message = "TravelSpeed for Beam weapons should be 0.00000";
					error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
				}
			} else {
				// travelSpeed should be 0.000000
				if (value.compareTo(new BigDecimal("0.0")) <= 0) {
					String message = MessageFormat.format("TravelSpeed for {0} weapons should be greater than 0.00000", weaponType);
					error.warn(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
				}
				
			}
		} else if (fieldName.equals("canBomb")) {
			if ("TRUE".equalsIgnoreCase(fieldValue)) {
				validateMeshPointExists("Bomb", fieldName, lineNumber);
			}
		} else if (fieldName.equals("meshName")) {
			String meshName = parser.getMetaData(fileReference.getFileName(), "meshName");
			if (meshName == null) {
				meshName = fieldValue;
			} else {
				meshName = meshName + "," + fieldValue;
			}
			parser.setMetaData(fileReference.getFileName(), fieldName, meshName);
			validateMeshPointExists("Aura", fieldName, 1);
			validateMeshPointExists("Above", fieldName, 1);
			validateMeshPointExists("Center", fieldName, 1);
		} else if (fieldName.contains("DamagePerBank:")) {
			String meshNames = parser.getMetaData(fileReference.getFileName(), "meshName");
			double weaponDamage = Double.parseDouble(fieldValue);
			boolean meshFound = false;
			
			StringTokenizer stk = new StringTokenizer(meshNames, ",");
			
			boolean hasWeapon = false;
			if (weaponDamage > 0) {
				String direction = fieldName.substring(fieldName.indexOf(":")+1);
				while (stk.hasMoreTokens()) {
					String meshName = stk.nextToken();
					HashMap<String, String> meshMetaData = parser.getMetaData(meshName);
					
					
					if (meshMetaData.isEmpty()) {
						// Can't validate weapons because the mesh doesn't reside in the same project
					} else {
						meshFound = true;
						String exists = meshMetaData.get("Weapon-" + numWeapon + "." + direction);
						if ("TRUE".equalsIgnoreCase(exists)) {
							hasWeapon = true;
						}
					}
				}
				if (!hasWeapon && meshFound) {
					error.warn(new EntityParseException(ValidationType.ENTITY, "Mesh appears to be missing Weapon-" + numWeapon + " for " + direction + " bank", lineNumber, fieldName));
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

	@Override
	public void endEntity() {
		
		super.endEntity();
	}

	@Override
	public void startEntity(String entityType, int lineNumber) {
		parser.setMetaData(fileReference.getFileName(), "meshName", null);
		super.startEntity(entityType, lineNumber);
	}

}
