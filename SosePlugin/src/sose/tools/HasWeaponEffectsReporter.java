package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Map;
import java.util.StringTokenizer;

public class HasWeaponEffectsReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	EntityParser parser = null;
	
	public HasWeaponEffectsReporter(EntityParser parser, ContentHandler reporter, ErrorHandler errorHandler) {
		super(reporter);
		this.error = errorHandler;
		this.parser = parser;
	}

	String currentStructure = null;
	String weaponEffectImpactOffsetType = null;
	String canWeaponEffectHitHull = null;
	String canWeaponEffectHitShields = null;
	
	@Override
	public void startStructure(String structureName, String structureType, int lineNumber) {
		if (structureName.equals("hasWeaponEffects")) {
			weaponEffectImpactOffsetType = null;
			canWeaponEffectHitHull = null;
			canWeaponEffectHitShields = null;
		}
		super.startStructure(structureName, structureType, lineNumber);
	}

	@Override
	public void processField(String fieldName, String fieldValue,
			String fieldType, int lineNumber) {
		if (fieldName.equals("weaponEffectImpactOffsetType")) {
			weaponEffectImpactOffsetType = fieldValue;
		} else if (fieldName.equals("canWeaponEffectHitHull")) {
			canWeaponEffectHitHull = fieldValue;
			if ("TRUE".equalsIgnoreCase(canWeaponEffectHitHull) && !("RandomMesh".equals(weaponEffectImpactOffsetType) || "CenterOffsetBySpatialRadius".equals(weaponEffectImpactOffsetType)) ) {
				String message = MessageFormat.format("'canWeaponEffectHitHull' and 'canWeaponEffectHitShields' only make sense when then ImpactOffsetType is 'RandomMesh'. If the impact isn't on the mesh then these values are ignored.", weaponEffectImpactOffsetType, canWeaponEffectHitHull);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			}
		} else if (fieldName.equals("canWeaponEffectHitShields")) {
			canWeaponEffectHitShields = fieldValue;
			if ("TRUE".equalsIgnoreCase(canWeaponEffectHitShields) && !("RandomMesh".equals(weaponEffectImpactOffsetType) || "CenterOffsetBySpatialRadius".equals(weaponEffectImpactOffsetType)) ) {
				String message = MessageFormat.format("'canWeaponEffectHitHull' and 'canWeaponEffectHitShields' only make sense when then ImpactOffsetType is 'RandomMesh'. If the impact isn't on the mesh then these values are ignored.", weaponEffectImpactOffsetType, canWeaponEffectHitShields);
				error.info(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
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
