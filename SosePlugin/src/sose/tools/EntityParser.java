package sose.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sose.tools.xml.NodeListIterator;
import sose.tools.xml.XMLException;
import sose.tools.xml.XMLToolkit;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed. 
 * 
 * <p>
 * EntityValidator uses the strategy, interpreter and prototype patterns for validating Sins of a Solar Empire entity files.
 * </p>
 * 
 * <p>SOSE files are in the form of:</p>
 * <code>
 * TXT
 * entityType "Ability"
 * entityType "Ability"
 * buffInstantActionType "ApplyBuffToSelf"
 * instantActionTriggerType "AlwaysPerform"
 * buffType "BuffNephilimAmplifyShieldsSpawner"
 * effectInfo
 * 		effectAttachInfo
 * 			attachType "Center"
 * smallEffectName ""
 * largeEffectName ""
 * soundID "EFFECT_DAMAGEOVERTIMEAURA"
 * </code>
 * 
 * <p>XML Definitions are used to validate this file in the form of:</p>
 * <code>
 * <entity_definitions>
 *    <structure name="Ability">
 *        <field name="buffInstantActionType"/>
 *        <field name="instantActionTriggerType"/>
 *        <field name="spaceMineType"/>
 *        ...
 *    </structure>
 * </tns:entity_definitions>
 * 
 * </code> 
 * 
 * <p>validation rules are in the form of:</p>
 * <code>
 * <!-- global rules -->
 * <element_rule name="fieldNameHere" validation="[ValidationType"/>
 * 
 * <!-- simple validation form -->
 * <element_rule name="fieldNameHere" validation="Simple" values="[comma delimited values for Simple validation]"/>
 * 
 * <!-- alternate form for Simple validation -->
 * <element_rule name="fieldNameHere" validation="Simple" values="[comman delimited values for Simple validation]">
 *    <value>value for Simple validation</value>
 *    <value>value2 for Simple validation</value>
 * </element_rule>
 * 
 * <!-- field validation form -->
 * <!-- uses global validation -->
 * <field name="fieldNameHere"/> 
 * 
 * <!-- uses explicit validatoin -->
 * <field name="fieldNameHere" [validation="[ValidationType]"]? [values="v1,v2,..."]?>
 *    <value></value>
 *    <value></value>
 * </field>
 * 
 * ValidationType := Simple|Brush|Decimal|Explosion|StringInfo|Integer|Sound|Texture|Exhaust|Mesh|Particle
 * 
 */
public class EntityParser {
	String definitions = 
		"cosmetic.xml;" + 
		"titan.xml;" + 
		"titan_upgrade.xml;" +
		"common.xml;" + 
		"ability.xml;" + 
		"aiUseTargetCondition.xml;" + 
		"aiUseTime.xml;" + 
		"brushes.xml;" + 
		"buff.xml;" + 
		"buffInstantActionType.xml;" + 
		"buffOverTimeActionType.xml;" +
		"canBomb.xml;" +
		"cannon_shell.xml;" + 
		"capital_ship.xml;" + 
		"constants.xml;" + 
		"entity_definitions.xsd;" +
		"entry_vehicle.xml;" + 
		"fighter.xml;" + 
		"finishConditionType.xml;" + 
		"frigate.xml;" + 
		"galaxy_scenario_def.xml;" + 
		"hasWeaponEffects.xml;" + 
		"instantActionTriggerType.xml;" + 
		"levelSourceType.xml;" + 
		"mission.xml;" + 
		"pip_cloud.xml;" + 
		"planet_bonus.xml;" + 
		"planet_module_hangar_defense.xml;" + 
		"planet_module_refinery.xml;" + 
		"planet_module_ship_factory.xml;" + 
		"planet_module_standard.xml;" + 
		"planet_module_trade_port.xml;" + 
		"planet_module_weapon_defense.xml;" + 
		"planet.xml;" + 
		"player.xml;" + 
		"quest.xml;" + 
		"research_subject.xml;" + 
		"resource_asteroid.xml;" + 
		"sound_data.xml;" + 
		"space_mine.xml;" + 
		"squad.xml;" + 
		"star_base_upgrade.xml;" + 
		"star_base.xml;" + 
		"star.xml;" + 
		"string_info.xml;" + 
		"useCostType.xml;" +
		"galaxy.xml;" + 
		"explosiondata.xml;" +
		"planetItemTypeCount.xml;" +
		"groups.xml;" +
		"random_event_defs.xml;" +
		"particle.xml;" +
		"rebellion.xml;" + 
		"asteroidDef.xml"
;
	String idsFile = null;
	String sinsInstallationDirectory = null;
	String diplomacyReferenceDirectory = null;
	String entrenchmentReferenceDirectory = null;
	String vanillaReferenceDirectory = null;
	String modDirectory = null;
	List<String> baseModDirectories = new ArrayList<String>();
	
	boolean includeInstallation = true;
	boolean includeReference = true;
	
	Map<String, Set<String>> references = new HashMap<String, Set<String>>();

	private ErrorHandler errorHandler = null;
	private ContentHandler contentHandler = null;
	private FileReferenceHandler fileReferenceHandler = null;
	
	boolean ignoreCaseOnFiles = true;
	boolean decimalPointRequired = false;
	boolean allowDecimalOnInteger = true;
	boolean failOnError = true;
	boolean defaultOrdered = true;
	boolean validateResearchCosts = false;
	Map<String, BigDecimal> researchCosts = new HashMap<String, BigDecimal>();
	Map<String, BigDecimal> upgradeTime = new HashMap<String, BigDecimal>();
	String currentFile = null;

	HashMap<String, Validator> txtPrototypeValidators = new HashMap<String, Validator>();
	HashMap<String, Validator> txtValidators = new HashMap<String, Validator>();
	HashMap<String, StructureValidator> txtStructures = new HashMap<String, StructureValidator>();

	HashMap<String, Validator> txt2PrototypeValidators = new HashMap<String, Validator>();
	HashMap<String, Validator> txt2Validators = new HashMap<String, Validator>();
	HashMap<String, StructureValidator> txt2Structures = new HashMap<String, StructureValidator>();
	
	HashMap<String, Validator> prototypeValidators = new HashMap<String, Validator>();
	HashMap<String, Validator> validators = new HashMap<String, Validator>();
	HashMap<String, StructureValidator> structures = new HashMap<String, StructureValidator>();
	HashMap<String, String> help = new HashMap<String, String>();
	private int currentLineNumber = 0;
	
	HashMap<String, HashMap<String, String>> metaData = new HashMap<String, HashMap<String, String>>();
	private List<String> excludedFileExtensions = new ArrayList<String>();
	
	boolean debug = true;
	boolean warn = false;
	
	private StringInfo stringInfo = null;
	private String stringDirectory = null;
	
	public void addExcludedFileExtension(String fileExtension) {
		excludedFileExtensions.add(fileExtension);
	}

	public void reset() {
		baseModDirectories.clear();
		baseModDirectories = null;
		references.clear();
		references = null;
		researchCosts.clear();
		researchCosts = null;
		upgradeTime.clear();
		upgradeTime = null;
//		prototypeValidators.clear();
//		prototypeValidators = null;
//		validators.clear();
//		validators = null;
//		structures.clear();
//		structures = null;
		txtPrototypeValidators.clear();
		txtPrototypeValidators = null;
		txtValidators.clear();
		txtValidators = null;
		txtStructures.clear();
		txtStructures = null;
		txt2PrototypeValidators.clear();
		txt2PrototypeValidators = null;
		txt2Validators.clear();
		txt2Validators = null;
		txt2Structures.clear();
		txt2Structures = null;
		help.clear();
		help = null;
	}
	public void resetReferences() {
		references.clear();
	}
	
	public void addReference(String referenceType, String reference) {
		Set<String> values = references.get(referenceType);
		if (values == null) {
			values = new HashSet<String>();
			references.put(referenceType, values);
			
			if ("Entity".equals(referenceType)) {
				values.add("Debris".toUpperCase());
				values.add("EmpireWindowNode".toUpperCase());
				values.add("Fleet".toUpperCase());
				values.add("Formation".toUpperCase());
				values.add("MissionAttackPlanet".toUpperCase());
				values.add("MissionBuildMines".toUpperCase());
				values.add("MissionBuildStarBase".toUpperCase());
				values.add("MissionCaptureResources".toUpperCase());
				values.add("MissionColonize".toUpperCase());
				values.add("MissionDefendFlagship".toUpperCase());
				values.add("MissionExplore".toUpperCase());
				values.add("MissionFosterRelationships".toUpperCase());
				values.add("MissionRaid".toUpperCase());
				values.add("PipCloudEnemyShips".toUpperCase());
				values.add("PipCloudFriendlyShips".toUpperCase());
				values.add("PipCloudModules".toUpperCase());
				values.add("PipCloudPlanets".toUpperCase());
				values.add("PlanetConnection".toUpperCase());
				values.add("ResourceAsteroidCrystal".toUpperCase());
				values.add("ResourceAsteroidMetal".toUpperCase());
				values.add("Trigger".toUpperCase());
				values.add("Weapon".toUpperCase());
				values.add("BuffDecloakMineForMovement".toUpperCase());
				values.add("BuffNeutralCapturableEntity".toUpperCase());
				values.add("BuffRecentlyColonized".toUpperCase());
			}
		}
		values.add(reference.toUpperCase());
	}
	
	public boolean isReferenced(String referenceType, String reference) {
		if (references.get(referenceType) == null) {
			return false;
		}
		return references.get(referenceType).contains(reference.toUpperCase());
	}
	
	public BigDecimal getResearchCost(String tier, String field, String costType) {
		return researchCosts.get("" + tier + field + costType);
	}
	
	public BigDecimal getUpgradeTime(String tier, String field) {
		return upgradeTime.get("" + tier + field);
	}
		
	public void addResearchCost(int tier, String field, String costType, int value) {
		researchCosts.put("" + tier + field + costType, new BigDecimal(value));
	}
	
	public void addUpgradeTime(int tier, String field, int value) {
		upgradeTime.put("" + tier + field, new BigDecimal(value));
	}
	
	public boolean isValidateResearchCosts() {
		return validateResearchCosts;
	}

	public void setValidateResearchCosts(boolean validateResearchCosts) {
		this.validateResearchCosts = validateResearchCosts;
	}

	private String strictValidation = "Diplomacy";
	
	public String getStrictValidation() {
		return strictValidation;
	}

	public void setStrictValidation(String strictValidation) {
		this.strictValidation = strictValidation;
	}

	public boolean isAllowDecimalOnInteger() {
		return allowDecimalOnInteger;
	}

	public void setAllowDecimalOnInteger(boolean allowDecimalOnInteger) {
		this.allowDecimalOnInteger = allowDecimalOnInteger;
	}

	public boolean isDecimalPointRequired() {
		return decimalPointRequired;
	}

	public void setDecimalPointRequired(boolean decimalPointRequired) {
		this.decimalPointRequired = decimalPointRequired;
	}

	public boolean isIgnoreCaseOnFiles() {
		return ignoreCaseOnFiles;
	}

	public void setIgnoreCaseOnFiles(boolean ignoreCaseOnFiles) {
		this.ignoreCaseOnFiles = ignoreCaseOnFiles;
	}

	public boolean isWarn() {
		return warn;
	}

	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public String getDefinitions() {
		return definitions;
	}

	public void setDefinitions(String definitions) {
		this.definitions = definitions;
	}
	public String getIdsFile() {
		return idsFile;
	}

	public void setIdsFile(String idsFile) {
		this.idsFile = idsFile;
	}

	private abstract class Validator {
		protected String fieldName;
		protected String validationType;
		protected String invalidValueMessage = "invalid value!";
		protected boolean showPossibleValues = true;
		protected boolean explicit = true;
		String getInvalidValueMessage() {
			return invalidValueMessage;
		}
		boolean showPossibleValues() {
			return showPossibleValues;
		}
		public Validator(String invalidValueMessage) {
			this.invalidValueMessage = invalidValueMessage;
		}
		public Validator() {
			
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			return readLine(contents);
		}
		public String validate(String currentLine, LineNumberReader contents, boolean report) throws Exception {
			return validate(currentLine, contents);
		}	
		public void validateValueRequiresQuotes(boolean criticalValue, String currentLine, String[] entityTypeValues) {
			validateValueRequiresQuotes(criticalValue, currentLine, entityTypeValues, ignoreCaseOnFiles);
		}
		
		public void validateRequiredMissing(String currentLine, String expectedTag) {
			String message = "Expected keyword missing: " + expectedTag + ". Found";
			warn(message, currentLineNumber, currentLine);
		}
		
		public void validateValue(boolean criticalValue, String currentLine, String[] validValues) {
			for (int i=0; i<validValues.length; i++) {
				if (currentLine.contains(validValues[i])) {
					return;
				}
			}
			String message = getInvalidValueMessage();
			if (showPossibleValues()) {
				ArrayList<String> possibleValues = new ArrayList<String>();
				for (int i=0; i<10 && i<validValues.length; i++) {
					possibleValues.add(validValues[i]);
				}
				if (validValues.length > possibleValues.size()) {
					possibleValues.add("...");
				}
				message += " expected: " + possibleValues;
			}
			if (criticalValue) {
				fail(message, currentLineNumber, currentLine);
			} else {
				warn(message, currentLineNumber, currentLine);
			}
		}
		
		public void validateValueRequiresQuotes(boolean criticalValue, String currentLine, String[] validValues, boolean ignoreCase) {
			String line = currentLine.trim();
			int index = line.indexOf(" ");
			String[] parts = null;
			if (index != -1) {
				parts = new String[2];
				parts[0] = line.substring(0, index);
				parts[1] = line.substring(index + 1);
			} else {
				parts = new String[1];
				parts[0] = currentLine;
			}
			if (parts.length <= 1) {
				fail("value expected for line", currentLineNumber, currentLine);
			}
			String value = parts[1].trim();
			if (!value.startsWith("\"")) {
				fail("expected \" (quote) at beginning of value", currentLineNumber, currentLine);
			}
			if (!value.endsWith("\"")) {
				fail("expected \" (quote) at end of value", currentLineNumber, currentLine);
			}
			if (currentLine.contains("MeshName")) {
				// check if there is a file path
				int lastSlash = value.lastIndexOf('\\');
				if (lastSlash > 0) {
					value = "\"" + value.substring(lastSlash+1);
				}
			}
			if (ignoreCase) {
				for (int i=0; i<validValues.length; i++) {
					if (value.toUpperCase().contains("\"" + validValues[i].toUpperCase() + "\"")) {
						return;
					}
				}
			} else {
				for (int i=0; i<validValues.length; i++) {
					if (value.contains("\"" + validValues[i] + "\"")) {
						return;
					}
				}
			}
			String message = getInvalidValueMessage();
			if (showPossibleValues()) {
				ArrayList<String> possibleValues = new ArrayList<String>();
				for (int i=0; i<10 && i<validValues.length; i++) {
					possibleValues.add(validValues[i]);
				}
				if (validValues.length > possibleValues.size()) {
					possibleValues.add("...");
				}
				message += " expected: " + possibleValues;
			}
			if (criticalValue) {
				fail(message, currentLineNumber, currentLine);
			} else {
				warn(message, currentLineNumber, currentLine);
			}
		}
		
		public void validateValueRequiresQuotes(boolean criticalValue, String currentLine) {
			String line = currentLine.trim();
			int index = line.indexOf(" ");
			String[] parts = null;
			if (index != -1) {
				parts = new String[2];
				parts[0] = line.substring(0, index);
				parts[1] = line.substring(index + 1);
			} else {
				parts = new String[1];
				parts[0] = currentLine;
			}
			if (parts.length <= 1) {
				fail("value expected for line", currentLineNumber, currentLine);
			}
			String value = parts[1].trim();
			if (!value.startsWith("\"")) {
				fail("expected \" (quote) at beginning of value", currentLineNumber, currentLine);
			}
			if (!value.endsWith("\"")) {
				fail("expected \" (quote) at end of value", currentLineNumber, currentLine);
			}
		}
		
		public void validateReferenced(String referenceType, String currentLine) {
			
			if (!isReferenced(referenceType, parseValue(currentLine))) {
				fail(referenceType + " entry does not appear to be referenced from another entity. This may or may not be an issue.", currentLineNumber, currentLine);
			}

		}

		public boolean isShowPossibleValues() {
			return showPossibleValues;
		}
		public void setShowPossibleValues(boolean showPossibleValues) {
			this.showPossibleValues = showPossibleValues;
		}
		public void setInvalidValueMessage(String invalidValueMessage) {
			this.invalidValueMessage = invalidValueMessage;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getValidationType() {
			return validationType;
		}
		public void setValidationType(String validationType) {
			this.validationType = validationType;
		}
		public abstract void processDefinition(String definitionName, DefinitionHandler handler, boolean explicit)  throws Exception;
		public boolean hasField(String fieldName2) {
			// TODO Auto-generated method stub
			return false;
		}
		public void validateValueAsColor(boolean criticalValue, String currentLine) {
			String value = parseValue(currentLine);
			if (value == null || value.length() == 0) {
				fail("Expected a Hex Color Value", currentLineNumber, currentLine);
			} else if ("0".equals(value)) {
				// ignore this value
			} else if (!(value.length() == 8 || value.length() == 6 || value.length() == 4)) {
				fail("Expected a Hex Color Value", currentLineNumber, currentLine);
			} else if (value.length() == 4) {
				String[] colors = {value.substring(0, 2), value.substring(2, 4)};
				for (int i=0; i<colors.length; i++) {
					try {
						int color = Integer.decode("0X" + colors[i].toLowerCase());
						if (color < 0 || color > 256) {
							fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
						}
					} catch (NumberFormatException e) {
						fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
					} catch (EntityParseException e) {
						fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
					}
				}
			} else if (value.length() == 6) {
				String[] colors = {value.substring(0, 2), value.substring(2, 4), value.substring(4, 5)};
				for (int i=0; i<colors.length; i++) {
					try {
						int color = Integer.decode("0X" + colors[i].toLowerCase());
						if (color < 0 || color > 256) {
							fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
						}
					} catch (NumberFormatException e) {
						fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
					} catch (EntityParseException e) {
						fail("Expected a Hex RGB Color Value", currentLineNumber, currentLine);
					}
				}
			} else if (value.length() == 8) {
				String[] colors = {value.substring(0, 2), value.substring(2, 4), value.substring(4, 5), value.substring(5, 6)};
				for (int i=0; i<colors.length; i++) {
					try {
						int color = Integer.decode("0X" + colors[i].toLowerCase());
						if (color < 0 || color > 256) {
							fail("Expected a Hex RGBA Color Value", currentLineNumber, currentLine);
						}
					} catch (NumberFormatException e) {
						fail("Expected a Hex RGBA Color Value", currentLineNumber, currentLine);
					} catch (EntityParseException e) {
						fail("Expected a Hex RGBA Color Value", currentLineNumber, currentLine);
					}
				}
			} else {
				fail("Unexpected Hex Color Value", currentLineNumber, currentLine);
			}
		}
		public void validateValueAsPosition(boolean criticalValue,
				String currentLine) {
			String value = parseValue(currentLine);
			
			if (value == null) {
				fail("value expected for line", currentLineNumber, currentLine);
			} else 	if (!value.startsWith("[")) {
				fail("expected [ at beginning of value", currentLineNumber, currentLine);
			} else if (!value.endsWith("]")) {
				fail("expected ] at end of value", currentLineNumber, currentLine);
			} else {
				StringTokenizer stk = new StringTokenizer(value, "[],", false);
				while (stk.hasMoreElements()) {
					String pos = stk.nextToken();
					try {
						Long.parseLong(pos.trim());
					} catch(Exception e) {
						fail("Expected a numeric value", currentLineNumber, currentLine);
					}
				}
			}
		}
		public void validateValueAsCoordinate(boolean criticalValue,
				String value, String currentLine) {
			if (value == null) {
				fail("value expected for line", currentLineNumber, currentLine);
			} else 	if (!value.startsWith("[")) {
				fail("expected [ at beginning of value", currentLineNumber, currentLine);
			} else if (!value.endsWith("]")) {
				fail("expected ] at end of value", currentLineNumber, currentLine);
			} else {
				StringTokenizer stk = new StringTokenizer(value, "[] ", false);
				while (stk.hasMoreElements()) {
					String pos = stk.nextToken();
					try {
						new BigDecimal(pos);
					} catch(Exception e) {
						fail("Expected a numeric value", currentLineNumber, currentLine);
					}
				}
			}
		}

		public boolean isExplicit() {
			return explicit;
		}
		public void setExplicit(boolean explicit) {
			this.explicit = explicit;
		}
		public Validator getValidator(String fieldName) {
			return null;
		}
	}
	
	private class StructureLookupValidator extends Validator {
		private String[] validValues = null;
		public StructureLookupValidator(String[] validValues) {
			this.validValues = validValues;
		}
		public StructureLookupValidator(String validValue) {
			this.validValues = new String[] {validValue};
		}
		public StructureLookupValidator(String fieldName, String validValue) {
			this.validValues = new String[] {validValue};
			this.fieldName = fieldName;
		}
		public boolean isExplicit() {
			return false;
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String value = parseValue(currentLine);
			String keyWord = parseKeyWord(currentLine);
			if (value == null || value.isEmpty()) {
				value = validValues[0];
			} else {
				validateValueRequiresQuotes(true, currentLine, validValues);
			}
			
			debug("looking up validator for: " + value);
			StructureValidator validator = structures.get(value);
			debug("validator: " + validator);
			currentLine = readLine(contents);
			if (validator == null) {
//				fail("Entity Validation not supported for type yet! [" + value + "]", currentLineNumber, currentLine);
				// might as well drop out at this point
				throw new EntityParseException("Entity Validation not supported for type yet! [" + value + "]", currentLineNumber, currentLine);
			}

			contentHandler.startStructure(getFieldName(), value, currentLineNumber);

			currentLine = validator.validate(currentLine, contents);
			
			contentHandler.endStructure();
			return currentLine;
		}
		@Override
		public void processDefinition(String definitionName, DefinitionHandler handler, boolean explicit)  throws Exception{
			Validator v = structures.get(validValues[0]);
			explicit = false;
			v.processDefinition(definitionName, handler, explicit);
		}
		public boolean hasField(String field) {
			return validValues[0].equals(field) || getFieldName().equals(field);
		}
		@Override
		public Validator getValidator(String fieldName) {
			Validator v = structures.get(validValues[0]);
			explicit = false;
			return v.getValidator(fieldName);

		}
	}

	private class FieldEnumerationValidator extends Validator {
		private String[] validValues = null;
		private boolean criticalValue = true;
		
		public FieldEnumerationValidator() {
		}
		public FieldEnumerationValidator(String[] validValues, boolean criticalValue) {
			this.validValues = validValues;
			this.criticalValue = criticalValue;
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			validateValueRequiresQuotes(criticalValue, currentLine, validValues);
			addReference(validationType, parseValue(currentLine));

			nextLine = readLine(contents);
			return nextLine;
		}
		public String[] getValidValues() {
			return validValues;
		}
		public void setValidValues(String[] validValues) {
			this.validValues = validValues;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, validValues, validationType, references, help.get(fieldName));
		}
	}
	
	private class FileReferenceValidator extends FieldEnumerationValidator {
		public FileReferenceValidator(String validationType, final String includes, List<String> locations, boolean criticalValue) throws Exception {
			this(validationType, includes, locations, criticalValue, false);
		}

		public FileReferenceValidator(String validationType, final String includes, List<String> locations, boolean criticalValue, boolean includeExtension) throws Exception {
			setupValidator(validationType, includes, locations, criticalValue,
					includeExtension);
		}

		public void setupValidator(String validationType,
				final String includes, List<String> locations,
				boolean criticalValue, boolean includeExtension) {
			Set<String> fileReferenceSet = new HashSet<String>();
			
			for (int i=0; i<locations.size(); i++) {
				File location = new File(locations.get(i));
				String[] srcFiles = location.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toUpperCase().endsWith(includes.toUpperCase());
					}
				}); 
				if (srcFiles!=null) {
					for (int j=0; j<srcFiles.length; j++) {
						if (srcFiles[j].contains(".")) {
							fileReferenceSet.add(srcFiles[j].substring(0, srcFiles[j].lastIndexOf(".")));
						}
						if (includeExtension == true) {
							fileReferenceSet.add(srcFiles[j]);
						} 
					}
				}
			}
			// special case validation
			if (ValidationType.ENTITY.equals(validationType)) {
				fileReferenceSet.add("BuffWormHoleTeleport");
			}
			String[] fileReferenceArray = new String[fileReferenceSet.size()];
			fileReferenceArray = fileReferenceSet.toArray(fileReferenceArray);
			setValidValues(fileReferenceArray);
			setCriticalValue(criticalValue);
			setValidationType(validationType);
			setInvalidValueMessage("missing reference [" + includes + "]");
			setShowPossibleValues(false);
		}

		@Override
		public String validate(String currentLine, LineNumberReader contents)
				throws Exception {
			String value = parseValue(currentLine);
			if ("".equals(value)) {
				//warn("empty content", currentLineNumber, currentLine);
				return readLine(contents);
			} else {
				String nextLine = null;
				validateValueRequiresQuotes(isCriticalValue(), currentLine, getValidValues(), ignoreCaseOnFiles);
				addReference(validationType, parseValue(currentLine));
				nextLine = readLine(contents);
				return nextLine;
			}
		}
	}

	private class FieldReferenceValidator extends FieldEnumerationValidator {
		public boolean isExplicit() {
			return false;
		}
		public FieldReferenceValidator(String validationType, final String referenceFieldName, final String includes, InputStream contents, boolean criticalValue) throws Exception {
			Set<String> fieldReferenceSet = new HashSet<String>();
			
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(contents));
			while (lnr.ready()) {
				String line = lnr.readLine();
				if (line.contains(referenceFieldName)) {
					String idName = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
					idName = idName.replace("\"", " ").trim();
					fieldReferenceSet.add(idName);
				}
			}

			String[] validValues = new String[fieldReferenceSet.size()];
			validValues = fieldReferenceSet.toArray(validValues);
			setValidValues(validValues);
			setValidationType(validationType);
			setCriticalValue(criticalValue);
			setInvalidValueMessage("missing reference [" + includes + "]");
			setShowPossibleValues(false);
		}
		public FieldReferenceValidator(String validationType, final String referenceFieldName, final String includes, List<String> locations, boolean criticalValue) throws Exception {
			setupValidator(validationType, referenceFieldName, includes,
					locations, null, criticalValue);
		}
		public FieldReferenceValidator(String validationType, final String referenceFieldName, final String includes, List<String> locations, List<String> initialValues, boolean criticalValue) throws Exception {
			setupValidator(validationType, referenceFieldName, includes,
					locations, initialValues, criticalValue);
		}
		public void setupValidator(String validationType,
				final String referenceFieldName, final String includes,
				List<String> locations, List<String> initialValues, boolean criticalValue)
				throws FileNotFoundException, IOException {
			Set<String> fieldReferenceSet = new HashSet<String>();
			List<String> filesProcessed = new ArrayList<String>();
			
			if (initialValues != null) {
				fieldReferenceSet.addAll(initialValues);
			}
			
			for (int i=0; i<locations.size(); i++) {
				File location = new File(locations.get(i));
				File[] srcFiles = location.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(includes);
					}
				}); 
			
				if (srcFiles != null) {
					for (int j=0; j<srcFiles.length; j++) {
						String fileName = srcFiles[j].getName();
//						System.out.println(fileName);
						if (!filesProcessed.contains(fileName)) {
							LineNumberReader lnr = new LineNumberReader(new FileReader(srcFiles[j]));
							while (lnr.ready()) {
								String line = lnr.readLine();
//								System.out.println(line);
								if (line.contains(referenceFieldName)) {
									String idName = line.substring(line.indexOf("\""), line.lastIndexOf("\""));
									idName = idName.replace("\"", " ").trim();
									fieldReferenceSet.add(idName);
								}
							}
							filesProcessed.add(fileName);
						}
					}
				}
			}
			String[] validValues = new String[fieldReferenceSet.size()];
			validValues = fieldReferenceSet.toArray(validValues);
			setValidValues(validValues);
			setValidationType(validationType);
			setCriticalValue(criticalValue);
			setInvalidValueMessage("missing reference [" + includes + "]");
			setShowPossibleValues(false);
		}

		@Override
		public String validate(String currentLine, LineNumberReader contents)
				throws Exception {
			String value = parseValue(currentLine);
			if ("".equals(value)) {
				return readLine(contents);
			} else {
				return super.validate(currentLine, contents);
			}
		}
	}
	private class BooleanValidator extends Validator {
		private String[] validValues = {"TRUE", "FALSE"};
		public BooleanValidator() {
			super("boolean value expected!");
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			validateValue(true, currentLine, validValues);
			nextLine = readLine(contents);
			return nextLine;
		}

		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, validValues, validationType, references, help.get(fieldName));
		}
	}
	
	private class DecimalValidator extends Validator {
		private String constraintType = null;
		
		public DecimalValidator(String constraintType) {
			super("decimal value expected!");
			this.constraintType = constraintType;
		}

		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			String decimalPart = parseValue(currentLine);
			debug(decimalPart);
			if (decimalPart.contains(".") || !decimalPointRequired) {
				try {
					BigDecimal value = new BigDecimal(decimalPart.trim());
					if (constraintType != null) {
						if (">0".equalsIgnoreCase(constraintType)) {
							if (value.compareTo(new BigDecimal(0.0)) <= 0) {
								fail("expected a positive decimal value: " + decimalPart.trim(), currentLineNumber, currentLine);
							}
						} else if (">=0".equalsIgnoreCase(constraintType)) {
							if (value.compareTo(new BigDecimal(0.0)) <= 0) {
								fail("expected a positive decimal value: " + decimalPart.trim(), currentLineNumber, currentLine);
							}
						}
					}				
				} catch (Exception e) {
					fail("expected '.' in decimal value: " + decimalPart.trim(), currentLineNumber, currentLine);
				}
			} else {
				fail("expected '.' in decimal value: " + decimalPart.trim(), currentLineNumber, currentLine);
			}

//			validateValue(currentLine, validValues);
			nextLine = readLine(contents);
			return nextLine;
		}

		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}
	
	private class NumericValidator extends Validator {
		public NumericValidator() {
			super("number expected!");
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			String numericPart = parseValue(currentLine);
			try {
				if (allowDecimalOnInteger) {
					new BigDecimal(numericPart.trim());
				} else {
					new Long(numericPart.trim());
				}
			} catch(Exception e) {
				fail("Decimal or Integer expected!", currentLineNumber, currentLine);
			}
			nextLine = readLine(contents);
			return nextLine;
		}

		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}
	
	private class FieldValidator extends Validator {
		public Validator getExplicitValidator() {
			return explicitValidator;
		}

		public void setExplicitValidator(Validator validator) {
			this.explicitValidator = validator;
		}

		private Validator explicitValidator = null;
		private String fieldRule = null;
		
		public String getFieldRule() {
			return fieldRule;
		}

		public void setFieldRule(String fieldRule) {
			this.fieldRule = fieldRule;
		}
		public boolean hasField(String field) {
			boolean found = false;
			if (field.equals(fieldRule)) {
				found = true;
			} else if (getExplicitValidator() != null) {
				found = getExplicitValidator().hasField(field);
			} else {
				if (validators.get(fieldRule) != null) {
					found = validators.get(fieldRule).hasField(field);
				}
			}
			return found;
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
//			try {
				String keyWord = parseKeyWord(currentLine);
				String value = parseValue(currentLine);
				Validator validator = null;
				

				if (explicitValidator != null) {
					validator = explicitValidator;
				} else {
					validator = validators.get(fieldRule);
				}
				debug("FieldValidator: " + validator);
				try {
					if (!(validator instanceof ConditionValidator)) {
						contentHandler.processField(fieldName, value, validator.getValidationType(), currentLineNumber);
					}
				} catch (Exception e) {
					debug(e);
				}

				if (validator != null) {
					currentLine = validator.validate(currentLine, contents);
				} else {
					debug("WARN missing validator", currentLine);
					currentLine = readLine(contents);
				}
//			} catch(Exception e) {
//				fail(e, currentLineNumber, currentLine);
//			}
			return currentLine;
		}

		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit)  throws Exception {
			if (explicitValidator != null) {
				explicitValidator.processDefinition(fieldName, handler, true);
			} else {
				Validator v = validators.get(fieldRule);
				if (v != null) {
					v.processDefinition(fieldName, handler, false);
				} else {
					String[] references = null;
					handler.field(fieldName, "Unknown", false, null, "Unknown", references, help.get(fieldName));
				}
			}
		}

		@Override
		public Validator getValidator(String fieldName) {
			if (getFieldName().equals(fieldName)) {
				return this;
			} else if (getExplicitValidator() != null) {
				return getExplicitValidator().getValidator(fieldName);
			} else {
				if (validators.get(fieldRule) != null) {
					return validators.get(fieldRule).getValidator(fieldName);
				}
			}
			return null;

		}
	}
	
	private class ConditionValidator extends Validator {
		private String conditionField = null;
		private Map<String, Validator> conditionValidators = new HashMap<String, Validator>();
		
		public ConditionValidator(String field) {
			conditionField = field;
		}
		
		public void addCondition(String condition, Validator validator) {
			conditionValidators.put(condition, validator);
		}
		
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			try {
				String keyWord = parseKeyWord(currentLine);
				String value = parseValue(currentLine);
				String[] validValues = new String[conditionValidators.keySet().size()]; 
				validValues = conditionValidators.keySet().toArray(validValues);
				if ("TRUE".equals(value) || "FALSE".equals(value)) {
					validateValue(true, currentLine, validValues);
				} else {
					validateValueRequiresQuotes(true, currentLine, validValues, ignoreCaseOnFiles);
				}
				// should this be structure???
				contentHandler.processField(keyWord, value, getValidationType(), currentLineNumber);
//				Validator validator = validators.get(keyWord);
//				if (validator != null) {
//					nextLine = validator.validate(currentLine, contents);
//				} else {
					nextLine = readLine(contents);
//				}
				debug("conditionValidator for:", value);
				if (conditionValidators.containsKey(value)) {
					debug("conditionValidator: " + conditionValidators.get(value));
					nextLine = conditionValidators.get(value).validate(nextLine, contents, false);
				} 
			} catch(Exception e) {
				fail(e, currentLineNumber, currentLine);
			}

			return nextLine;
		}

		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit)  throws Exception {
			Set<String> conditionSet = new TreeSet<String>(conditionValidators.keySet());
			Iterator<String> conditions = conditionSet.iterator();
			String[] conditionValues = new String[conditionSet.size()];
			conditionSet.toArray(conditionValues);
			
			handler.startConditionBlock(fieldName, conditionField, conditionValues, null);
			
			while (conditions.hasNext()) {
				String condition = conditions.next();
				handler.startCondition(conditionField, condition, help.get(fieldName));
				Validator v = conditionValidators.get(condition);
				(v).processDefinition(fieldName, handler, true);
				
				handler.endCondition();
			}
			
			handler.endConditionBlock();

		}
		public boolean hasField(String fieldName2) {
			boolean found = false;
			Collection<Validator> validators = conditionValidators.values();
			Iterator<Validator> itr = validators.iterator();
			while(itr.hasNext() && found == false) {
				Validator validator = itr.next();
				found = validator.hasField(fieldName2);
			}
			return found;

		}
		
		public Validator getValidator(String fieldName) {
			if (conditionField.equals(fieldName)) {
				return this;
			} else {
				Collection<Validator> validators = conditionValidators.values();
				Iterator<Validator> itr = validators.iterator();
				while(itr.hasNext()) {
					Validator validator = itr.next();
					Validator v2 = validator.getValidator(fieldName);
					if (v2 != null) {
						return v2;
					}
				}
			}
			return null;
		}
	}
	
	private class IterativeValidator extends Validator {
		private String iterativeField = null;
		private Validator structureValidator = null;
		private int limit = -1;
		
		public IterativeValidator(String field, Validator validator, int limit) {
			iterativeField = field;
			structureValidator = validator;
			this.limit = limit;
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			try {
				int iterativeLineNumber = currentLineNumber;
				String iterativeLine = currentLine;
				String numericPart = parseValue(currentLine);
				int repeats = new Integer(numericPart.trim()).intValue();
				
				if (limit != -1 && repeats > limit) {
					fail("exceeded hard limit of " + limit + " occurrences, found " + repeats, iterativeLineNumber, iterativeLine);
				}
				// eat repeat line
				currentLine = readLine(contents);
				contentHandler.startStructure(getFieldName(), numericPart, currentLineNumber);
				for (int i=0; i<repeats; i++) {
					int tempLine = currentLineNumber;
					currentLine = structureValidator.validate(currentLine, contents, false);
					if (tempLine == currentLineNumber && i<repeats) {
						fail("expected " + repeats + " occurences, only found " + (i), iterativeLineNumber, iterativeLine);
						fail("expected " + repeats + " occurences, unexpected value found", currentLineNumber, currentLine);
						break;
						//fail("expected " + repeats + " occurences, only found " + (i), currentLineNumber, currentLine);
					} 
				}
				// peak to see if they put too many
				// current line should only change if the next line is part of this iteration
				boolean check = true; 
				int found = repeats;
				while (check) {
					int tempLineNumber = currentLineNumber;
					String tempLine = currentLine;
					currentLine = structureValidator.validate(currentLine, contents, false);
					if (tempLineNumber != currentLineNumber) {
						fail("expected only " + repeats + " occurences", iterativeLineNumber, iterativeLine);
						fail("expected only " + repeats + " occurences", tempLineNumber, tempLine);
						found++;
					} else {
						check = false;
					}
				}
				if (found > repeats) {
					fail("expected only " + repeats + " occurences, found " + found, iterativeLineNumber, iterativeLine);
				}
				contentHandler.endStructure();
			} catch (Exception e) {
				fail(e, currentLineNumber, currentLine);
			}
			return currentLine;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit)  throws Exception{
			handler.startIteration(iterativeField, help.get(iterativeField));
			structureValidator.processDefinition(null, handler, true);
			handler.endIteration();
		}
		public boolean hasField(String fieldName2) {
			// TODO Auto-generated method stub
			return structureValidator.hasField(fieldName2);
		}
		@Override
		public Validator getValidator(String fieldName) {
			return structureValidator.getValidator(fieldName);
		}
	}
	private class StructureValidator extends Validator {
		public boolean isOrdered() {
			return ordered;
		}

		public void setOrdered(boolean ordered) {
			this.ordered = ordered;
		}

		private List<String> fields = new ArrayList<String>();
		private List<String> requiredFields = new ArrayList<String>();
		private Map<String, Validator> fieldValidators = new HashMap<String, Validator>();
		private boolean ordered = true;
		private boolean condition = false;
		
		public FieldValidator addField(String field, String fieldRule, String required) {
			fields.add(field);
			FieldValidator v = new FieldValidator();
			v.setFieldName(field);
			v.setFieldRule(fieldRule);
			
			fieldValidators.put(field, v);
			if (!"false".equals(required)) {
				requiredFields.add(field);
			}
			return v;
		}
		
		public void addNestedStructure(String field, StructureValidator validator, String required) {
			fields.add(field);
			validator.setFieldName(field);
			fieldValidators.put(field, validator);
			if (!"false".equals(required)) {
				requiredFields.add(field);
			}
		}
		public void addValidator(String field, Validator validator, String required) {
			fields.add(field);
			validator.setFieldName(field);
			fieldValidators.put(field, validator);
			if (!"false".equals(required)) {
				requiredFields.add(field);
			}
		}		
		public StructureValidator() {
			
		}

		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			return validate(currentLine, contents, true);
		}

		public String validate(String currentLine, LineNumberReader contents, boolean report) throws Exception {
			// spin through all fields looking for matching key words
//			currentLine = readLine(contents);
			boolean isReport = false;
			
			if (isOrdered()) {
				for (int i=0; i<fields.size(); i++) {
					String keyWord = parseKeyWord(currentLine);
					String field = fields.get(i);
//					debug("checking for field: " + field);
					if (keyWord != null && keyWord.equals(field)) {
						if (!isReport && isExplicit() && report) {
							contentHandler.startStructure(getFieldName(), getFieldName(), currentLineNumber);
							isReport = true;
						}
						// found a key word run the validator
						debug("validating: " + currentLine);
						Validator validator = fieldValidators.get(keyWord);
						debug("validator: " + validator);
						if (validator instanceof StructureValidator) {
							// need to eat next line
							currentLine = readLine(contents);
						}
						currentLine = validator.validate(currentLine, contents);
					} else {
						if (requiredFields.contains(field)) {
							// raise error, required field is missing...
							validateRequiredMissing(currentLine, fields.get(i));
						}
					}
				}
			} else {
				debug("unordered " + fields);
				for (int i=0; i<fields.size(); i++) {
					String keyWord = parseKeyWord(currentLine);
					String field = fields.get(i);
					debug("checking for unordered field: " + field);
					if (fieldValidators.containsKey(keyWord)) {
						if (!isReport && isExplicit()) {
							contentHandler.startStructure(getFieldName(), getFieldName(), currentLineNumber);
							isReport = true;
						}
						// found a key word run the validator
						debug("validating: " + currentLine);
						Validator validator = fieldValidators.get(keyWord);
						debug("validator: " + validator);
						if (validator instanceof StructureValidator) {
							// need to eat next line
							currentLine = readLine(contents);
						}
						currentLine = validator.validate(currentLine, contents);
					}
				}
			}
			if (isReport && isExplicit() && report) {
				contentHandler.endStructure();
			}
			return currentLine;
		}

		public void addStructureReference(String fieldName, String type, String required) {
			fields.add(fieldName);
			if (type == null || type.length() == 0) {
				type = fieldName;
			}
			if (!"false".equals(required)) {
				requiredFields.add(fieldName);
			}
			fieldValidators.put(fieldName, new StructureLookupValidator(fieldName, type));
		}

		@Override
		public void processDefinition(String definitionName, DefinitionHandler handler, boolean explicit)  throws Exception{
			if (definitionName != null && !isCondition()) {
				String[] references = getReferences(getFieldName());
				handler.startStructure(definitionName, fieldName, explicit, references, help.get(definitionName));
			}
			for (int i=0; i<fields.size(); i++) {
				String fieldName = fields.get(i);
				Validator validator = fieldValidators.get(fieldName);
				validator.processDefinition(fieldName, handler, true);
			}
			if (definitionName != null && !isCondition()) {
				handler.endStructure();
			}
		}

		public boolean hasField(String fieldName) {
			// TODO Auto-generated method stub
			boolean found = fields.contains(fieldName);
			if (!found) {
				// check sub-elements or types
				for (int i=0; i<fields.size() && found == false; i++) {
					String checkField = fields.get(i);
					Validator validator = fieldValidators.get(checkField);
					found = validator.hasField(fieldName);
				}
			}
			return found;
		}

		public boolean isCondition() {
			return condition;
		}

		public void setCondition(boolean condition) {
			this.condition = condition;
		}

		@Override
		public Validator getValidator(String fieldName) {
			// TODO Auto-generated method stub
			boolean found = fields.contains(fieldName);
			Validator v2 = null;
			if (!found) {
				// check sub-elements or types
				for (int i=0; i<fields.size() && found == false; i++) {
					String checkField = fields.get(i);
					Validator validator = fieldValidators.get(checkField);
					v2 = validator.getValidator(fieldName);
					found = v2 != null;
				}
			} else {
				v2 = fieldValidators.get(fieldName);
			}
			return v2;
		}
	}
	
	private class ColorValidator extends Validator {
		private boolean criticalValue = true;
		public ColorValidator() {
			setValidationType(ValidationType.Color);
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			validateValueAsColor(criticalValue, currentLine);
			nextLine = readLine(contents);
			return nextLine;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}

	private class CoordinateValidator extends Validator {
		private boolean criticalValue = true;
		public CoordinateValidator() {
			setValidationType(ValidationType.ORIENTATION);
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			String value = parseValue(currentLine);
			validateValueAsCoordinate(criticalValue, value, currentLine);
			nextLine = readLine(contents); // skip to what should be second point of orientation
			
			return nextLine;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}

	private class OrientationValidator extends Validator {
		private boolean criticalValue = true;
		public OrientationValidator() {
			setValidationType(ValidationType.ORIENTATION);
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			String orientationLine = currentLine;
			currentLine = readLine(contents); // skip to what should be first point of orientation
			validateValueAsCoordinate(criticalValue, currentLine.trim(), orientationLine);
			currentLine = readLine(contents); // skip to what should be second point of orientation
			validateValueAsCoordinate(criticalValue, currentLine.trim(), orientationLine);
			currentLine = readLine(contents); // skip to what should be third point of orientation
			validateValueAsCoordinate(criticalValue, currentLine.trim(), orientationLine);
			nextLine = readLine(contents);
			return nextLine;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}

	private class PositionValidator extends Validator {
		private boolean criticalValue = true;
		public PositionValidator() {
			setValidationType(ValidationType.POSITION);
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			validateValueAsPosition(criticalValue, currentLine);
			nextLine = readLine(contents);
			return nextLine;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
	}

	private class AnyValidator extends Validator {
		private boolean criticalValue = true;
		private String referenceType = null;
		
		public AnyValidator(String referenceType) {
			setValidationType(ValidationType.ANY);
			this.referenceType = referenceType;
		}
		public String validate(String currentLine, LineNumberReader contents) throws Exception {
			String nextLine = null;
			// check for well formedness...
			if (currentLine.contains("\"")) {
				validateValueRequiresQuotes(criticalValue, currentLine);
			}
			if (referenceType != null && !referenceType.trim().isEmpty()) {
				validateReferenced(referenceType, currentLine);
			}
			nextLine = readLine(contents);
			return nextLine;
		}
		public boolean isCriticalValue() {
			return criticalValue;
		}
		public void setCriticalValue(boolean criticalValue) {
			this.criticalValue = criticalValue;
		}
		@Override
		public void processDefinition(String fieldName, DefinitionHandler handler, boolean explicit) throws Exception {
			String[] references = null;
			if (!explicit) {
				references = getReferences(fieldName);
			}
			handler.field(fieldName, getFieldName(), explicit, null, validationType, references, help.get(fieldName));
		}
		public String getReferenceType() {
			return referenceType;
		}
		public void setReferenceType(String referenceType) {
			this.referenceType = referenceType;
		}
	}

	public void setup() throws EntityParseException {
		setup(new DefaultSetupMonitor());
	}
		
	public void setup(SetupMonitor monitor) throws EntityParseException {
		String[] definitions = getDefinitions().split(";");
		String[] archiveTypes = {"TXT", "TXT2"};
		String currentVersion = strictValidation;
		
		if (strictValidation.equals("Rebellion185")) {
			archiveTypes = new String[]{"TXT"};
		} else
		if (strictValidation.equals("Rebellion193")) {
			archiveTypes = new String[]{"TXT2"};
			txtValidators = txt2Validators;
			txtStructures = txt2Structures;
			prototypeValidators = txt2PrototypeValidators;
		}
		
		for (String archiveType: archiveTypes) {
			if (archiveType.equals("TXT")) {
				validators = txtValidators;
				structures = txtStructures;
				prototypeValidators = txtPrototypeValidators;
				strictValidation = "Rebellion185";
			} else {
				validators = txt2Validators;
				structures = txt2Structures;
				prototypeValidators = txt2PrototypeValidators;
				strictValidation = "Rebellion193";
			}
		for (int i=0; i<definitions.length; i++) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			try {
				debug("processing rule file: " + definitions[i]);
				InputStream is = getClass().getResourceAsStream("/" + definitions[i]);
				Document doc = factory.newDocumentBuilder().parse(is);
				XMLToolkit toolkit = XMLToolkit.getDefaultToolkit();
				if (doc.getDocumentElement().hasAttribute("ordered")) {
					defaultOrdered = true;
				}
				
				{
					NodeList rulesList = toolkit.getElementList(doc, "//entity_definitions/element_rule");
					monitor.beginTask("rules", rulesList.getLength());
					Iterator<Node> rules = new NodeListIterator(rulesList);
					parseRules(toolkit, rules, monitor);
				}
				
				{
					NodeList conditionsList = toolkit.getElementList(doc, "//entity_definitions/condition_field");
					monitor.beginTask("conditions", conditionsList.getLength());
					Iterator<Node> conditions = new NodeListIterator(conditionsList);
					parseConditions(toolkit, conditions, monitor);
				}
				
				{
					NodeList structuresList = toolkit.getElementList(doc, "//entity_definitions/structure");
					monitor.beginTask("structures", structuresList.getLength());
					Iterator<Node> structures = new NodeListIterator(structuresList);
					parseStructures(toolkit, structures, monitor);
				}
			} catch (Exception e) {
				e.printStackTrace();
				fatal(e);
			}
		}

		strictValidation = currentVersion;
		}
	}

	protected String[] getReferences(String fieldName) {
		TreeSet<String> references = new TreeSet<String>();
		Iterator<StructureValidator> validators = structures.values().iterator();
		while (validators.hasNext()) {
			StructureValidator v = (StructureValidator) validators.next();
			if (v.hasField(fieldName)) {
				references.add(v.getFieldName());
			}
		}
		String[] referenceArray = new String[references.size()];
		return (String[])references.toArray(referenceArray);
	}

	private void parseConditions(XMLToolkit toolkit, Iterator<Node> conditionsItr, SetupMonitor monitor)
		throws Exception {
		while (conditionsItr.hasNext()) {
			Element condition = (Element) conditionsItr.next();
			String version = condition.getAttribute("version");
			if (isVersionSupported(version)) {
				ConditionValidator validator = parseCondition(toolkit, condition);
				validator.setFieldName(condition.getAttribute("name"));
				monitor.subTask(validator.getFieldName());
				validators.put(condition.getAttribute("name"), validator);
				monitor.finished(1);
			}
		}
	}

	private ConditionValidator parseCondition(XMLToolkit toolkit, Element condition) throws Exception {
		String name = condition.getAttribute("name");

		debug("parseStructure: ", name);

		String fieldName = condition.getAttribute("name");
		Iterator<Element> conditions = toolkit.getChildElements(condition);
		
		ConditionValidator conditionValidator = new ConditionValidator(fieldName);

		while(conditions.hasNext()) {
			Element conditionElement = conditions.next();
			// single value scenario
			String conditionValue = conditionElement.getAttribute("value");
			debug("add condition: " + conditionValue);
			NodeList valueNodeList = toolkit.getNodeList(conditionElement, "value");
			List<String> valueList = new ArrayList<String>();
			for (int j=0; j<valueNodeList.getLength(); j++) {
				Element valueElement = (Element) valueNodeList.item(j);
				if (isVersionSupported(valueElement.getAttribute("version"))) {
					valueList.add(toolkit.getValueOfElement(valueNodeList.item(j)));
				}
			}
			if (conditionValue != null && !conditionValue.isEmpty()) {
				valueList.add(conditionValue);
			}
			StructureValidator nestedValidator = parseStructure(toolkit, conditionElement);
			nestedValidator.setFieldName(fieldName);
			nestedValidator.setCondition(true);
			Iterator<String> itr = valueList.iterator();
			if (isVersionSupported(conditionElement.getAttribute("version"))) {
				while (itr.hasNext()) {
					conditionValidator.addCondition(itr.next(), nestedValidator);
				}
			}
		}
		addHelpText(condition);
		return conditionValidator;
	}

	private void parseStructures(XMLToolkit toolkit, Iterator<Node> structureItr, SetupMonitor monitor)
			throws Exception {
		while (structureItr.hasNext()) {
			Element structure = (Element) structureItr.next();
			String version = structure.getAttribute("version");
			if (isVersionSupported(version)) {
				StructureValidator validator = parseStructure(toolkit, structure);
				validator.setFieldName(structure.getAttribute("name"));
				validator.setExplicit(false);
				monitor.subTask(validator.getFieldName());
				structures.put(structure.getAttribute("name"), validator);
				monitor.finished(1);
			}
		}
	}
	
	private StructureValidator parseStructure(XMLToolkit toolkit, Element structure) throws Exception {
		String name = structure.getAttribute("name");
		NodeList children = structure.getChildNodes();
		StructureValidator validator = new StructureValidator();

		debug("parseStructure: ", name);

		addHelpText(structure);
		for (int i=0; i<children.getLength(); i++) {
			String version = "";
			if (children.item(i) instanceof Element) {
				version = ((Element)children.item(i)).getAttribute("version");
			}
			if (isVersionSupported(version)) {
			if ("field".equals(children.item(i).getNodeName())) {
				Element fieldElement = (Element) children.item(i);
				String fieldName = fieldElement.getAttribute("name");
				String fieldRule = fieldElement.getAttribute("type");
				String fieldRequired = fieldElement.getAttribute("required");
				String helpText = fieldElement.getAttribute("help");
				if (fieldRule == null || fieldRule.isEmpty()) {
					fieldRule = fieldName;
				}
				help.put(fieldName, helpText);
				FieldValidator fieldValidator = validator.addField(fieldName, fieldRule, fieldRequired);
				
				addHelpText(fieldElement);
				// check if has built in rules
				if (fieldElement.hasAttribute("validation")) {
					Validator ruleValidator = parseRule(toolkit, fieldElement);
					fieldValidator.setExplicitValidator(ruleValidator);
				}
			} else if ("structure".equals(children.item(i).getNodeName())) {
				Element nestedStructure = (Element) children.item(i);
				boolean ordered = defaultOrdered;
				if (nestedStructure.hasAttribute("ordered")) {
					ordered = Boolean.valueOf(nestedStructure.getAttribute("ordered")).booleanValue();
				}
				String requiredValue = nestedStructure.getAttribute("required");
				StructureValidator nestedValidator = parseStructure(toolkit, nestedStructure);
				nestedValidator.setOrdered(ordered);
				nestedValidator.setExplicit(true);
				validator.addNestedStructure(nestedStructure.getAttribute("name"), nestedValidator, requiredValue);
				addHelpText(nestedStructure);
			} else if ("structure_reference".equals(children.item(i).getNodeName())) {
				Element fieldElement = (Element) children.item(i);
				String fieldName = fieldElement.getAttribute("name");
				String type = fieldElement.getAttribute("type");
				String requiredValue = fieldElement.getAttribute("required");
				//debug("type: " + type);
				validator.addStructureReference(fieldName, type, requiredValue);
				addHelpText(fieldElement);
			} else if ("iterative_field".equals(children.item(i).getNodeName())) {
				Element nestedStructure = (Element) children.item(i);
				StructureValidator nestedValidator = parseStructure(toolkit, nestedStructure);
				nestedValidator.setFieldName(nestedStructure.getAttribute("name"));
				String requiredValue = nestedStructure.getAttribute("required");
				int limit = -1;
				if (nestedStructure.getAttribute("limit") != null && !nestedStructure.getAttribute("limit").trim().isEmpty()) {
					limit = Integer.parseInt(nestedStructure.getAttribute("limit"));
				}
				IterativeValidator iterativeValidator = new IterativeValidator(nestedStructure.getAttribute("name"), nestedValidator, limit);
				validator.addValidator(nestedStructure.getAttribute("name"), iterativeValidator, requiredValue);
				addHelpText(nestedStructure);
			} else if ("condition_field".equals(children.item(i).getNodeName())) {
				debug("condition_field");
				Element nestedStructure = (Element) children.item(i);
				String fieldName = nestedStructure.getAttribute("name");
				String requiredValue = nestedStructure.getAttribute("required");
				Iterator<Element> conditions = toolkit.getChildElements(nestedStructure);
				
				ConditionValidator conditionValidator = new ConditionValidator(fieldName);

				while(conditions.hasNext()) {
					Element conditionElement = conditions.next();
					// single value scenario
					String conditionValue = conditionElement.getAttribute("value");
					debug("add condition: " + conditionValue);
					NodeList valueNodeList = toolkit.getNodeList(conditionElement, "value");
					List<String> valueList = new ArrayList<String>();
					for (int j=0; j<valueNodeList.getLength(); j++) {
						valueList.add(toolkit.getValueOfElement(valueNodeList.item(j)));
					}
					if (conditionValue != null && !conditionValue.isEmpty()) {
						valueList.add(conditionValue);
					}
					StructureValidator nestedValidator = parseStructure(toolkit, conditionElement);
					nestedValidator.setFieldName(fieldName);
					nestedValidator.setCondition(true);
					Iterator<String> itr = valueList.iterator();
					while (itr.hasNext()) {
						conditionValidator.addCondition(itr.next(), nestedValidator);
					}
				}
				validator.addValidator(fieldName, conditionValidator, requiredValue);
				addHelpText(nestedStructure);
			}
			}
		}
		return validator;
	}
	
	public void validateEntity(InputStream entityFile, DefaultHandler reporter) throws EntityParseException {
		setErrorHandler(reporter);
		setContentHandler(reporter);
		setFileReferenceHandler(reporter);
		
		validateEntity(entityFile);
		
	}
	
	public void validate(InputStream file, InputStream preValidate, String fileType, DefaultHandler reporter) {
		try {
		setErrorHandler(reporter);
		setFileReferenceHandler(reporter);
		setContentHandler(reporter);
		fileType = fileType.substring(0, 1).toUpperCase() + fileType.substring(1);
		FieldReferenceValidator galaxyScenarioValidator = (FieldReferenceValidator)prototypeValidators.get(ValidationType.GALAXY_TEMPLATE);
		String[] oldGalaxyScenarioValues = null;
		
		if (fileType.equals("Galaxy")) {
			// need to capture template contents then reset afterwards?
			if (prototypeValidators.containsKey(ValidationType.GALAXY_TEMPLATE)) {
				// can't validate galaxy files if they didn't include the scenario def in their mod
				try {
					List<String> dirs = new ArrayList<String>();
					dirs.add(modDirectory + "/GameInfo");
					FieldReferenceValidator galaxyValidator = new FieldReferenceValidator(ValidationType.GALAXY_TEMPLATE, "templateName", ".galaxyScenarioDef", preValidate, false);
					String[] galaxyValues = galaxyValidator.getValidValues();
					String[] galaxyScenarioValues = galaxyScenarioValidator.getValidValues();
					ArrayList<String> tempArray = new ArrayList<String>(galaxyValues.length + galaxyScenarioValues.length);
					tempArray.addAll(Arrays.asList(galaxyValues));
					tempArray.addAll(Arrays.asList(galaxyScenarioValues));
					String[] tempStringArray = new String[tempArray.size()];
					oldGalaxyScenarioValues = galaxyScenarioValidator.getValidValues();
					galaxyScenarioValidator.setValidValues(tempArray.toArray(tempStringArray));
				} catch(Exception e) {
					debug(e);
					//System.err.println(e.toString());
					fatal(e);
				}
			}
		}
		validate(file, fileType);
		if (fileType.equals("Galaxy")) {
			galaxyScenarioValidator.setValidValues(oldGalaxyScenarioValues);
			prototypeValidators.put(ValidationType.GALAXY_TEMPLATE, galaxyScenarioValidator);
		}
		} finally {
			// not the best idea but just saving time
			if (preValidate != null) {
				try {
					preValidate.close();
				} catch (Exception e) {
					//
				}
			}
			if (file != null) {
				try {
				file.close();
				} catch (Exception e) {
					//
				}
			}
		}
	}
	
	public void validate(InputStream is, String fileType) throws EntityParseException {
		try {
			LineNumberReader contents = readContents(is);
			String nextLine = readLine(contents);
			String sinsArchiveType = "TXT";
			String sinsArchiveVersion = null;
			
			validators = txtValidators;
			structures = txtStructures;
			prototypeValidators = txtPrototypeValidators;
			
			if (nextLine == null || nextLine.indexOf("TXT") == -1) {
				fail("Cannot validate binary files", 1, nextLine);
			} else {
				if (nextLine.indexOf("TXT2") != -1) {
					if (strictValidation.equalsIgnoreCase("Rebellion185")) {
						fail("TXT2 not supported in Rebellion 1.85", 1, nextLine);
					}
					// we've got a version to eat
					nextLine = readLine(contents);
					if (nextLine.indexOf("SinsArchiveVersion") == -1) {
						fail("TXT2 must be followed by SinsArchiveVersion", 2, nextLine);
					} else {
						sinsArchiveType = "TXT2";
						sinsArchiveVersion = parseValue(nextLine);
						
						validators = txt2Validators;
						structures = txt2Structures;
						prototypeValidators = txt2PrototypeValidators;

					}
				} else {
					if (strictValidation.equalsIgnoreCase("Rebellion185")) {
						//fail("TXT not supported in Rebellion 1.93", 1, nextLine);
					}
					if ("Galaxy".equals(fileType)) {
						nextLine = readLine(contents);
						String versionNumber = parseValue(nextLine);
						if ("193".equals(versionNumber)) {
							validators = txt2Validators;
							structures = txt2Structures;
							prototypeValidators = txt2PrototypeValidators;
						}
					}
				}
			}
			

			// what kind of file am I?
			if ("Entity".equals(fileType)) {
				nextLine = readLine(contents);
				String keyWord  = parseKeyWord(nextLine);
				validate_generic(keyWord, nextLine, contents);
			} else {
				nextLine = readLine(contents);
				Validator validator = structures.get(fileType);

				if (validator != null) {
					try {
						contentHandler.startEntity(fileType, currentLineNumber);
						nextLine = validator.validate(nextLine, contents);
						
						// do we have any unexpected content left?
						while (contents.ready()) {
							if (nextLine != null && !nextLine.trim().isEmpty()) {
								throw new EntityParseException("check structure definition. found unexpected keyword", currentLineNumber, nextLine);
							}
							nextLine = readLine(contents);
						}
						contentHandler.endEntity();
					} catch(Exception e) {
						// whoops
						fatal(e);
					}
				} else {
					fail("check structure definition. found unexpected keyword: " + fileType, currentLineNumber, nextLine);
					nextLine = readLine(contents);
				}
			}
		} catch(Exception e) {
			debug(e);
			//System.err.println(e.toString());
			fatal(e);
		}
	}
	
	public void validateEntity(InputStream is) throws EntityParseException {
		try {
			LineNumberReader contents = readContents(is);
			String nextLine = readLine(contents);
			String sinsArchiveType = "TXT";
			String sinsArchiveVersion = "185";
			
			validators = txtValidators;
			structures = txtStructures;
			prototypeValidators = txtPrototypeValidators;
			
			if (nextLine == null || nextLine.indexOf("TXT") == -1) {
				fail("Cannot validate binary files", 1, nextLine);
			} else {
				if (nextLine.indexOf("TXT2") != -1) {
					if (strictValidation.equalsIgnoreCase("Rebellion185")) {
						fail("TXT2 not supported in Rebellion 1.85", 1, nextLine);
					}
					// we've got a version to eat
					nextLine = readLine(contents);
					if (nextLine.indexOf("SinsArchiveVersion") == -1) {
						fail("TXT2 must be followed by SinsArchiveVersion", 2, nextLine);
					} else {
						sinsArchiveType = "TXT2";
						sinsArchiveVersion = parseValue(nextLine);
						
						validators = txt2Validators;
						structures = txt2Structures;
						prototypeValidators = txt2PrototypeValidators;
					}
				} else {
					if (strictValidation.equalsIgnoreCase("Rebellion185")) {
						fail("TXT not supported in Rebellion 1.93", 1, nextLine);
					}
				}
			}
			
			nextLine = readLine(contents);
			while (contents.ready()) {
				// find keyWord
				debug("validateEntity->nextLine: " + nextLine);
				if (nextLine == null || nextLine.isEmpty()) {
					return;
				}
				int spaceIndex = nextLine.indexOf(" "); 
				String keyWord = null;
				if (spaceIndex != -1) {
					keyWord = nextLine.substring(0, spaceIndex);
				} else {
					keyWord = nextLine;
				}
				keyWord = keyWord.trim();
				debug(keyWord);
				Class[] params = {String.class, LineNumberReader.class};
				Method m = null;
				try {
					m = EntityParser.class.getMethod("validate_" + keyWord, params);
					Object[] args = {nextLine, contents};
					nextLine = (String)m.invoke(this, args);
				} catch(NoSuchMethodException e) {
					nextLine = validate_generic(keyWord, nextLine, contents);
				}
			}
		} catch(Exception e) {
			debug(e);
//			System.err.println(e.toString());
			fatal(e);
		}
	}

	public String validate_generic(String keyWord, String currentLine, LineNumberReader contents) throws Exception {
		String nextLine = null;
		Validator validator = validators.get(keyWord);
		
		if (validators.containsKey(keyWord)) {
			if (validator != null) {
				try {
					ContentHandlerChain chain = null;
					if (validator instanceof StructureLookupValidator) {
						String value = parseValue(currentLine);
					
						chain = new WeaponTypeReporter(this, getContentHandler(), getErrorHandler());
						setContentHandler(chain);
						
						if (value.equals("Ability")) {
							chain = new AbilityReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							chain = new HasWeaponEffectsReporter(this, chain, getErrorHandler());
							chain = new UseCostTypeReporter(this, chain, getErrorHandler());
							setContentHandler(chain);
						} else
						if (value.equals("ResearchSubject")) {
							chain = new ResearchSubjectReporter(this, getContentHandler(), getErrorHandler(), isValidateResearchCosts());
							setContentHandler(chain);
						} else
						if (value.equals("Weapon")) {
							// This is the Weapon entity file with zero contents...
							validator = new StructureValidator();
						} else
						if (value.equals("Buff")) {
							chain = new BuffReporter(this, getContentHandler(), getErrorHandler(), isValidateResearchCosts());
							chain = new HasWeaponEffectsReporter(this, chain, getErrorHandler());
							setContentHandler(chain);
						} else
						if (value.equals("CapitalShip")) {
							chain = new CapitalShipReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("Frigate")) {
							chain = new FrigateReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("SpaceMine")) {
							chain = new FrigateReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("Squad")) {
							chain = new SquadReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("Titan")) {
							chain = new TitanReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("StarBase")) {
							chain = new StarBaseReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("PlanetModuleStandard")
								|| value.equals("PlanetModuleShipFactory")
								|| value.equals("PlanetModuleWeaponDefense")
								|| value.equals("PlanetModuleHangarDefense")
								|| value.equals("PlanetModuleTradePort")) {
							chain = new PlanetModuleReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("Planet")) {
							chain = new PlanetReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						} else if (value.equals("Star")) {
							chain = new StarReporter(this, getFileReferenceHandler(), getContentHandler(), getErrorHandler());
							setContentHandler(chain);
						}
						contentHandler.startEntity(keyWord, currentLineNumber);
						nextLine = validator.validate(currentLine, contents);
						contentHandler.endEntity();
						if (chain != null) {
							setContentHandler(chain.getChain());
						}
					} else {
						fail("check structure definition. found unexpected keyword: " + keyWord, currentLineNumber, currentLine);
					}
					// do we have any unexpected content left?
					while (contents.ready()) {
						if (nextLine != null && !nextLine.trim().isEmpty()) {
							throw new EntityParseException("check structure definition. found unexpected keyword", currentLineNumber, nextLine);
						}
						nextLine = readLine(contents);
					}
				} catch(Exception e) {
					// whoops
					fatal(e);
				}
			} else {
				fail("check structure definition. found unexpected keyword: " + keyWord, currentLineNumber, currentLine);
				nextLine = readLine(contents);
			}
		} else {
			fail("Invalid key word for current line", currentLineNumber, currentLine);
			// might be fail on error false
			nextLine = readLine(contents);
		}
		return nextLine;
	}

	
	private void fail(Exception e, int currentLineNumber, String currentLine) throws EntityParseException {
		debug(e.toString() + "[" + currentFile + ":" + currentLineNumber + ":" + currentLine + "]");
		EntityParseException parseException = new EntityParseException(e.getMessage(), currentLineNumber, currentLine, e);
		if (errorHandler != null) {
			errorHandler.error(parseException);
		}
		//throw parseException;
	}

	private void fail(String message, int currentLineNumber, String currentLine) throws EntityParseException {
		debug(message + "[" + currentFile + ":" + currentLineNumber + ":" + currentLine + "]");
		EntityParseException parseException = new EntityParseException(message, currentLineNumber, currentLine);
		if (errorHandler != null) {
			errorHandler.error(parseException);
		}
		//throw parseException;
	}
	
	private void warn(Exception e, int currentLineNumber, String currentLine) throws EntityParseException {
		if (warn) {
			System.err.println(e.toString() + "[" + currentFile + ":" + currentLineNumber + ":" + currentLine + "]");
		}
		EntityParseException parseException = new EntityParseException(e.getMessage(), currentLineNumber, currentLine, e);
		if (errorHandler != null) {
			errorHandler.warn(parseException);
		}
		//throw parseException;
	}

	private void warn(String message, int currentLineNumber, String currentLine) throws EntityParseException {
		debug(message + "[" + currentFile + ":" + currentLineNumber + ":" + currentLine + "]");
		EntityParseException parseException = new EntityParseException(message, currentLineNumber, currentLine);
		if (errorHandler != null) {
			errorHandler.warn(parseException);
		}
		//throw parseException;
	}
	private void debug(Exception e) {
		if (debug == true) {
			e.printStackTrace();
		}
	}
	
	private void debug(String message) {
		if (debug == true) {
			System.out.println(((currentFile != null) ? (currentFile + ": ") : ("")) + message);
		}
	}
	
	private void debug(String message, String arg) {
		if (debug == true) {
			System.out.println(((currentFile != null) ? (currentFile + ": ") : ("")) + message + " " + arg);
		}
	}

	private void warn(String message, String arg) {
		if (warn == true) {
			System.out.println(((currentFile != null) ? (currentFile + ": ") : ("")) + message + " " + arg);
		}
		if (errorHandler != null) {
			EntityParseException e = new EntityParseException(message, currentLineNumber, arg);
			errorHandler.warn(e);
		}
	}

	private LineNumberReader readContents(InputStream is) throws EntityParseException {
		currentLineNumber = 0;
		LineNumberReader lnr = null;
//		try {
			lnr = new LineNumberReader(new InputStreamReader(is));
//		} catch (FileNotFoundException e) {
//			fatal(e);
//		}
		return lnr;
	}
	
	private void fatal(Exception e) throws EntityParseException {
		EntityParseException parseException = null;
		if (e instanceof EntityParseException) {
			parseException = (EntityParseException) e;
		} else {
			parseException = new EntityParseException(e);
		}
		if (errorHandler != null) {
			errorHandler.fatal(parseException);
		}
		throw parseException;
	}

	private String readLine(LineNumberReader lnr) throws IOException {
		currentLineNumber++;
		String nextLine = "";
		if (lnr.ready()) {
			nextLine = lnr.readLine();
		}
		debug("readLine: " + nextLine + "[" + currentLineNumber + "]");
		return nextLine;
	}

	private void validateExtension(String entityFile) {
		if (entityFile.endsWith(".entity) == false)")) {
			throw new EntityParseException("Expected entity file: " + entityFile);
		}
	}

	/**
	 * Handle getMessage() for exceptions.
	 * 
	 * @param ex
	 *            the exception to handle
	 * @return ex.getMessage() if ex.getMessage() is not null otherwise return
	 *         ex.toString()
	 */
	private String getMessage(Exception ex) {
		return ex.getMessage() == null ? ex.toString() : ex.getMessage();
	}

	private void parseRules(XMLToolkit toolkit, Iterator<Node> rules, SetupMonitor monitor)
				throws Exception {
			while (rules.hasNext()) {
				Element rule = (Element) rules.next();
				String version = rule.getAttribute("version");
				if (isVersionSupported(version)) {
					Validator validator = parseRule(toolkit, rule);
					monitor.subTask(rule.getAttribute("name"));
					validators.put(rule.getAttribute("name"), validator);
					monitor.finished(1);
				}
			}
		}

	private boolean isVersionSupported(String version) {
		if (version == null || version.length() == 0) {
			return true;
		} else if (getStrictValidation().equals("Rebellion193")) {
			return version.equals("Rebellion193") || version.equals("Rebellion") || version.equals("Diplomacy") || version.equals("Entrenchment");
		} else if (getStrictValidation().equals("Rebellion185")) {
			return version.equals("Rebellion185") || version.equals("Rebellion") || version.equals("Diplomacy") || version.equals("Entrenchment");
		} else if (getStrictValidation().equals("Rebellion")) {
			return version.equals("Rebellion") || version.equals("Diplomacy") || version.equals("Entrenchment");
		} else if (getStrictValidation().equals("Diplomacy")) {
			return version.equals("DiplomacyOnly") || version.equals("Diplomacy") || version.equals("Entrenchment") || version.equals("NotRebellion");
		} else if (getStrictValidation().equals("Entrenchment")) {
			return version.equals("Entrenchment") || version.equals("EntrenchmentOnly") || version.equals("NotDiplomacyOrRebellion") || version.equals("NotRebellion");
		} else if (getStrictValidation().equals("Vanilla")) {
			return version.equals("Vanilla") || version.equals("NotRebellion") || version.equals("NotDiplomacyOrRebellion");
		} 
		return true;
	}

	private Validator parseRule(XMLToolkit toolkit, Element rule)
			throws XMLException, Exception {
		String myModDir = getModDirectory();
		List<String> baseModDirs = getBaseModDirectory();
		String sinsInstallationDir = getSinsInstallationDirectory();
		String diplomacyDir = getDiplomacyReferenceDirectory();
		String entrenchmentDir = getEntrenchmentReferenceDirectory();
		String vanillaDir = getVanillaReferenceDirectory();

		String name = rule.getAttribute("name");
		String validation = rule.getAttribute("validation");

		addHelpText(rule);
		
		Validator validator = null;
		if (validation == null || validation.length() == 0) {
			// this is a containing rule, possibly
			validators.put(name, null);
		} else if (validation.equals("Structure")) {
			String values = rule.getAttribute("values");
			if (values != null && values.length() > 0) {
				validator = new StructureLookupValidator(values.split(","));
			} else {
				// check for value elements
				NodeList valueNodeList = toolkit.getElementList(rule, "value");
				List<String> valueList = new ArrayList<String>();
				for (int i=0; i<valueNodeList.getLength(); i++) {
					String version = ((Element)valueNodeList.item(i)).getAttribute("version");
					if (isVersionSupported(version)) {
						valueList.add(toolkit.getValueOfElement(valueNodeList.item(i)));
					}
				}
				String[] valueArray = new String[valueList.size()];
				validator = new StructureLookupValidator((String[])valueList.toArray(valueArray));
			}
		} else if (validation.equals("Simple")) {
			String values = rule.getAttribute("values");
			if (values != null && values.length() > 0) {
				validator = new FieldEnumerationValidator(values.split(","), true);
			} else {
				// check for value elements
				NodeList valueNodeList = toolkit.getNodeList(rule, "value");
				Set<String> valueList = new TreeSet<String>();
				for (int i=0; i<valueNodeList.getLength(); i++) {
					Element valueElement = (Element) valueNodeList.item(i);
					String version = valueElement.getAttribute("version");
					if (isVersionSupported(version)) {
						valueList.add(toolkit.getValueOfElement(valueNodeList.item(i)));
					}
				}
				String[] valueArray = new String[valueList.size()];
				validator = new FieldEnumerationValidator((String[])valueList.toArray(valueArray), true);
			}
			validator.setValidationType(ValidationType.ENUMERATION);
		} else if (validation.equals("Any")) {
			String referenceType = rule.getAttribute("referenceType");
			validator = new AnyValidator(referenceType);
		} else if (validation.equals("Color")) {
			validator = new ColorValidator();
		} else if (validation.equals("Position")) {
			validator = new PositionValidator();
		} else if (validation.equals("Orientation")) {
			validator = new OrientationValidator();
		} else if (validation.equals("Coordinate")) {
			validator = new CoordinateValidator();
		} else if (validation.equals("Research") || validation.equals("Entity")) {
			if (!prototypeValidators.containsKey("Entity")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put("Entity", new FileReferenceValidator(ValidationType.ENTITY, ".entity", dirs, true) );
			}
			validator = prototypeValidators.get("Entity");
		} else if (validation.equals("Texture")) {
			if (!prototypeValidators.containsKey("Texture")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/Textures");
				addReferenceLocation(dirs, baseModDirs, "/Textures");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(sinsInstallationDir + "/Diplomacy/Textures");
					dirs.add(diplomacyDir + "/Textures");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/Textures");
					dirs.add(sinsInstallationDir + "/Entrenchment/Textures");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/Textures");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/Textures");
				}
			
				prototypeValidators.put("Texture", new FileReferenceValidator(ValidationType.TEXTURE, "", dirs, false, true) );
			}
			validator = prototypeValidators.get("Texture");
		} else if (validation.equals("SoundFile")) {
			if (!prototypeValidators.containsKey("SoundFile")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/Sound");
				addReferenceLocation(dirs, baseModDirs, "/Sound");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/Sound");
					dirs.add(sinsInstallationDir + "/Diplomacy/Sound");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(sinsInstallationDir + "/Entrenchment/Sound");
					dirs.add(entrenchmentDir + "/Sound");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/Sound");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/Sound");
				}
			
				prototypeValidators.put("SoundFile", new FileReferenceValidator(ValidationType.SOUNDFILE, "", dirs, false, true) );
			}
			validator = prototypeValidators.get("SoundFile");
		} else if (validation.equals("Sound")) {
			if (!prototypeValidators.containsKey("Sound")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put("Sound", new FieldReferenceValidator(ValidationType.SOUND, "name", ".sounddata", dirs, false));
			}
			validator =  prototypeValidators.get("Sound");
		} else if (validation.equals(ValidationType.GALAXY_DESIGN)) {
			if (!prototypeValidators.containsKey(ValidationType.GALAXY_DESIGN)) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put(ValidationType.GALAXY_DESIGN, new FieldReferenceValidator(ValidationType.GALAXY_DESIGN, "designName", ".galaxyScenarioDef", dirs, false));
			}
			validator =  prototypeValidators.get(ValidationType.GALAXY_DESIGN);
		} else if (validation.equals(ValidationType.GALAXY_TEMPLATE)) {
			if (!prototypeValidators.containsKey(ValidationType.GALAXY_TEMPLATE)) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put(ValidationType.GALAXY_TEMPLATE, new FieldReferenceValidator(ValidationType.GALAXY_TEMPLATE, "templateName", ".galaxyScenarioDef", dirs, false));
			}
			validator =  prototypeValidators.get(ValidationType.GALAXY_TEMPLATE);
		} else if (validation.equals(ValidationType.GALAXY_ORBIT_BODY_TYPE)) {
			if (!prototypeValidators.containsKey(ValidationType.GALAXY_ORBIT_BODY_TYPE)) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put(ValidationType.GALAXY_ORBIT_BODY_TYPE, new FieldReferenceValidator(ValidationType.GALAXY_ORBIT_BODY_TYPE, "typeName", ".galaxyScenarioDef", dirs, false));
			}
			validator =  prototypeValidators.get(ValidationType.GALAXY_ORBIT_BODY_TYPE);
		} else if (validation.equals("Explosion")) {
			if (!prototypeValidators.containsKey("Explosion")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/GameInfo");
				addReferenceLocation(dirs, baseModDirs, "/GameInfo");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(diplomacyDir + "/GameInfo");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/GameInfo");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/GameInfo");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/GameInfo");
				}
				
				prototypeValidators.put("Explosion", new FieldReferenceValidator(ValidationType.EXPLOSION, "groupName", ".explosiondata", dirs, false));
			}
			validator =  prototypeValidators.get("Explosion");
		} else if (validation.equals("Boolean")) {
			validator = new BooleanValidator();
			validator.setValidationType(ValidationType.BOOLEAN);
		} else if (validation.equals("Integer")) {
			validator = new NumericValidator();
			validator.setValidationType(ValidationType.INTEGER);
		} else if (validation.equals("Decimal")) {
			String constraint = rule.getAttribute("constraint");
			validator = new DecimalValidator(constraint);
			validator.setValidationType(ValidationType.DECIMAL);
		} else if (validation.equals("Mesh")) {
			if (!prototypeValidators.containsKey("Mesh")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/Mesh");
				addReferenceLocation(dirs, baseModDirs, "/Mesh");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(sinsInstallationDir + "/Diplomacy/Mesh");
					dirs.add(diplomacyDir + "/Mesh");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(sinsInstallationDir + "/Entrenchment/Mesh");
					dirs.add(entrenchmentDir + "/Mesh");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/Mesh");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/Mesh");
				}
			
				prototypeValidators.put("Mesh", new FileReferenceValidator(ValidationType.MESH, ".mesh", dirs, true, true) );
			}
			validator = prototypeValidators.get("Mesh");
		} else if (validation.equals("Particle")) {
			if (!prototypeValidators.containsKey("Particle")) {
				List<String> dirs = new ArrayList<String>();
				dirs.add(myModDir + "/Particle");
				addReferenceLocation(dirs, baseModDirs, "/Particle");
				if (isVersionSupported("Diplomacy")) {
					dirs.add(sinsInstallationDir + "/Diplomacy/Particle");
					dirs.add(diplomacyDir + "/Particle");
				}
				if (isVersionSupported("Entrenchment")) {
					dirs.add(entrenchmentDir + "/Particle");
					dirs.add(sinsInstallationDir + "/Entrenchment/Particle");
				}
				if (includeReference) {
					dirs.add(vanillaDir + "/Particle");
				}
				if (includeInstallation) {
					dirs.add(sinsInstallationDir + "/Particle");
				}

				prototypeValidators.put("Particle", new FileReferenceValidator(ValidationType.PARTICLE, ".particle", dirs, false) );
			}
			validator = prototypeValidators.get("Particle");
		} else if (validation.equals("StringInfo")) {
			if (!prototypeValidators.containsKey("StringInfo")) {
				List<String> idsFiles = new ArrayList<String>();
				idsFiles.add(myModDir + "/String/");
				if (stringDirectory != null) {
					idsFiles.add(stringDirectory);
					idsFiles.add(stringDirectory + "/String/");
				}
				
				prototypeValidators.put("StringInfo", new FieldReferenceValidator(ValidationType.STRINGINFO, "ID", "English.str", idsFiles, false) );
			}
			validator = prototypeValidators.get("StringInfo");
			
			// need to look up in String file
		} else if (validation.equals("Brush")) {
			if (!prototypeValidators.containsKey("Brush")) {
				// need to look up in Brush file
				List<String> hudDirectories = new ArrayList<String>();
				hudDirectories.add(myModDir + "/Window");
				addReferenceLocation(hudDirectories, baseModDirs, "/Window");
				if (isVersionSupported("Diplomacy")) {
					hudDirectories.add(diplomacyDir + "/Window");
				}
				if (isVersionSupported("Entrenchment")) {
					hudDirectories.add(entrenchmentDir + "/Window");
				}
				if (includeReference) {
					hudDirectories.add(vanillaDir + "/Window");
				}
				if (includeInstallation) {
				}
				
				prototypeValidators.put("Brush", new FieldReferenceValidator(ValidationType.BRUSH, "name", ".brushes", hudDirectories, false) );
			}
			validator = prototypeValidators.get("Brush");
		}
		if (validator == null) {
			//System.out.println(name);
		}
		validator.setFieldName(name);
		return validator;
	}

	public void resetValidators() throws Exception {
		prototypeValidators = new HashMap<String, EntityParser.Validator>();
		
//		String myModDir = getModDirectory();
//		List<String> baseModDirs = getBaseModDirectory();
//		String sinsInstallationDir = getSinsInstallationDirectory();
//		String diplomacyDir = getDiplomacyReferenceDirectory();
//		String entrenchmentDir = getEntrenchmentReferenceDirectory();
//		String vanillaDir = getVanillaReferenceDirectory();
//				
//		List<String> gameInfoDirs = new ArrayList<String>();
//		gameInfoDirs.add(myModDir + "/GameInfo");
//		addReferenceLocation(gameInfoDirs, baseModDirs, "/GameInfo");
//		if (isVersionSupported("Diplomacy")) {
//			gameInfoDirs.add(diplomacyDir + "/GameInfo");
//		}
//		if (isVersionSupported("Entrenchment")) {
//			gameInfoDirs.add(entrenchmentDir + "/GameInfo");
//		}
//		gameInfoDirs.add(vanillaDir + "/GameInfo");
//
//		if (prototypeValidators.containsKey("Entity")) {
//		
//			FileReferenceValidator entityValidator = (FileReferenceValidator) prototypeValidators.get("Entity");
//			entityValidator.setupValidator(ValidationType.ENTITY, ".entity", gameInfoDirs, true, false);
//		}
//
//		if (prototypeValidators.containsKey("Texture")) {
//			List<String> dirs = new ArrayList<String>();
//			dirs.add(myModDir + "/Textures");
//			addReferenceLocation(dirs, baseModDirs, "/Textures");
//			if (isVersionSupported("Diplomacy")) {
//				dirs.add(sinsInstallationDir + "/Diplomacy/Textures");
//				dirs.add(diplomacyDir + "/Textures");
//			}
//			if (isVersionSupported("Entrenchment")) {
//				dirs.add(entrenchmentDir + "/Textures");
//				dirs.add(sinsInstallationDir + "/Entrenchment/Textures");
//			}
//			dirs.add(vanillaDir + "/Textures");
//			dirs.add(sinsInstallationDir + "/Textures");
//
//			FileReferenceValidator entityValidator = (FileReferenceValidator) prototypeValidators.get("Texture");
//			entityValidator.setupValidator(ValidationType.TEXTURE, "", dirs, false, true);
//		}
//
//		if (prototypeValidators.containsKey("SoundFile")) {
//			List<String> dirs = new ArrayList<String>();
//			dirs.add(myModDir + "/Sound");
//			addReferenceLocation(dirs, baseModDirs, "/Sound");
//			if (isVersionSupported("Diplomacy")) {
//				dirs.add(diplomacyDir + "/Sound");
//				dirs.add(sinsInstallationDir + "/Diplomacy/Sound");
//			}
//			if (isVersionSupported("Entrenchment")) {
//				dirs.add(sinsInstallationDir + "/Entrenchment/Sound");
//				dirs.add(entrenchmentDir + "/Sound");
//			}
//			dirs.add(vanillaDir + "/Sound");
//			dirs.add(sinsInstallationDir + "/Sound");
//		
//			FileReferenceValidator entityValidator = (FileReferenceValidator) prototypeValidators.get("SoundFile");
//			entityValidator.setupValidator(ValidationType.SOUNDFILE, "", dirs, false, true);
//		}
//
//		if (prototypeValidators.containsKey("Sound")) {
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.SOUND);
//			entityValidator.setupValidator(ValidationType.SOUND, "name", ".sounddata", gameInfoDirs, false);
//		}
//
//		if (prototypeValidators.containsKey(ValidationType.GALAXY_DESIGN)) {
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.GALAXY_DESIGN);
//			entityValidator.setupValidator(ValidationType.GALAXY_DESIGN, "designName", ".galaxyScenarioDef", gameInfoDirs, false);
//		}
//
//		if (prototypeValidators.containsKey(ValidationType.GALAXY_TEMPLATE)) {
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.GALAXY_TEMPLATE);
//			entityValidator.setupValidator(ValidationType.GALAXY_TEMPLATE, "templateName", ".galaxyScenarioDef", gameInfoDirs, false);
//		}
//
//		if (prototypeValidators.containsKey(ValidationType.GALAXY_ORBIT_BODY_TYPE)) {
//			
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.GALAXY_ORBIT_BODY_TYPE);
//			entityValidator.setupValidator(ValidationType.GALAXY_ORBIT_BODY_TYPE, "typeName", ".galaxyScenarioDef", gameInfoDirs, false);
//		}
//
//		if (prototypeValidators.containsKey("Explosion")) {
//			
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.EXPLOSION);
//			entityValidator.setupValidator(ValidationType.EXPLOSION, "groupName", ".explosiondata", gameInfoDirs, false);
//		}
//
//		if (prototypeValidators.containsKey("Mesh")) {
//			List<String> dirs = new ArrayList<String>();
//			dirs.add(myModDir + "/Mesh");
//			addReferenceLocation(dirs, baseModDirs, "/Mesh");
//			if (isVersionSupported("Diplomacy")) {
//				dirs.add(sinsInstallationDir + "/Diplomacy/Mesh");
//				dirs.add(diplomacyDir + "/Mesh");
//			}
//			if (isVersionSupported("Entrenchment")) {
//				dirs.add(sinsInstallationDir + "/Entrenchment/Mesh");
//				dirs.add(entrenchmentDir + "/Mesh");
//			}
//			dirs.add(vanillaDir + "/Mesh");
//			dirs.add(sinsInstallationDir + "/Mesh");
//		
//			FileReferenceValidator entityValidator = (FileReferenceValidator) prototypeValidators.get(ValidationType.MESH);
//			entityValidator.setupValidator(ValidationType.MESH, ".mesh", dirs, true, true);
//		}
//
//		if (prototypeValidators.containsKey("Particle")) {
//			List<String> dirs = new ArrayList<String>();
//			dirs.add(myModDir + "/Particle");
//			addReferenceLocation(dirs, baseModDirs, "/Particle");
//			if (isVersionSupported("Diplomacy")) {
//				dirs.add(sinsInstallationDir + "/Diplomacy/Particle");
//				dirs.add(diplomacyDir + "/Particle");
//			}
//			if (isVersionSupported("Entrenchment")) {
//				dirs.add(entrenchmentDir + "/Particle");
//				dirs.add(sinsInstallationDir + "/Entrenchment/Particle");
//			}
//			dirs.add(vanillaDir + "/Particle");
//			dirs.add(sinsInstallationDir + "/Particle");
//	
//			FileReferenceValidator entityValidator = (FileReferenceValidator) prototypeValidators.get(ValidationType.PARTICLE);
//			entityValidator.setupValidator(ValidationType.PARTICLE, ".particle", dirs, false, false );
//		}
//
//		if (prototypeValidators.containsKey("StringInfo")) {
//			List<String> idsFiles = new ArrayList<String>();
//			idsFiles.add(myModDir + "/String/");
//			
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.STRINGINFO);
//			entityValidator.setupValidator(ValidationType.STRINGINFO, "ID", "English.str", idsFiles, false);
//		}
//
//		if (prototypeValidators.containsKey("Brush")) {
//			// need to look up in Brush file
//			List<String> hudDirectories = new ArrayList<String>();
//			hudDirectories.add(myModDir + "/Window");
//			addReferenceLocation(hudDirectories, baseModDirs, "/Window");
//			if (isVersionSupported("Diplomacy")) {
//				hudDirectories.add(diplomacyDir + "/Window");
//			}
//			if (isVersionSupported("Entrenchment")) {
//				hudDirectories.add(entrenchmentDir + "/Window");
//			}
//			hudDirectories.add(vanillaDir + "/Window");
//	
//			FieldReferenceValidator entityValidator = (FieldReferenceValidator) prototypeValidators.get(ValidationType.BRUSH);
//			entityValidator.setupValidator(ValidationType.BRUSH, "name", ".brushes", hudDirectories, false );
//		}
	}

	private void addReferenceLocation(List<String> dirs,
			List<String> baseModDirs, String subPath) {
		for (int i=0; i<baseModDirs.size(); i++) {
			dirs.add(baseModDirs.get(i) + subPath);
		}
		
	}

	private void addHelpText(Element rule) throws Exception {
		String name = rule.getAttribute("name");
		StringWriter sw = new StringWriter();
		PrintWriter helpWriter = new PrintWriter(sw);
		String helpText = rule.getAttribute("help");
		List<String> helpTexts = XMLToolkit.getDefaultToolkit().getValuesOfElements(rule, "helpText");
		
		if ((helpText != null && helpText.length() > 0) || (helpTexts != null && helpTexts.size()>0)) {
			if (helpText != null && helpText.length() > 0) {
				helpWriter.write(helpText);
				helpWriter.println();
			}
			for (int i=0; helpTexts != null && i<helpTexts.size(); i++) {
				helpWriter.println(helpTexts.get(i));
			}
			help.put(name, sw.toString());
		}
	}

	public ErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public String getDiplomacyReferenceDirectory() {
		return diplomacyReferenceDirectory;
	}

	public void setDiplomacyReferenceDirectory(String diplomacyReferenceDirectory) {
		this.diplomacyReferenceDirectory = diplomacyReferenceDirectory;
	}

	public String getEntrenchmentReferenceDirectory() {
		return entrenchmentReferenceDirectory;
	}

	public void setEntrenchmentReferenceDirectory(
			String entrenchmentReferenceDirectory) {
		this.entrenchmentReferenceDirectory = entrenchmentReferenceDirectory;
	}

	public String getVanillaReferenceDirectory() {
		return vanillaReferenceDirectory;
	}

	public void setVanillaReferenceDirectory(String vanillaReferenceDirectory) {
		this.vanillaReferenceDirectory = vanillaReferenceDirectory;
	}

	public String getModDirectory() {
		return modDirectory;
	}

	public void setModDirectory(String modDirectory) {
		this.modDirectory = modDirectory;
	}

	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void processDefinition(String definition, DefinitionHandler handler, String textType)  throws Exception{
		if (textType.equals("TXT")) {
			structures = txtStructures;
			validators = txtValidators;
		} else if (textType.equals("TXT2")) {
			structures = txt2Structures;
			validators = txt2Validators;
		} else {
			structures = txtStructures;
			validators = txtValidators;
		}
		
		Validator validator = structures.get(definition);
		
		if (validator != null && validator instanceof StructureValidator) {
			handler.startEntity(definition, help.get(definition));
			validator.processDefinition(definition, handler, true);
			handler.endEntity();
		}
	}

	public void processDefinition(String definition, String fieldName, DefinitionHandler handler, String textType)  throws Exception{
		if (textType.equals("TXT")) {
			structures = txtStructures;
			validators = txtValidators;
		} else if (textType.equals("TXT2")) {
			structures = txt2Structures;
			validators = txt2Validators;
		} else {
			structures = txtStructures;
			validators = txtValidators;
		}
		
		Validator validator = structures.get(definition);
		
		if (validator != null && validator instanceof StructureValidator) {
			handler.startEntity(definition, help.get(definition));
			Validator subValidator = validator.getValidator(fieldName);
			if (subValidator != null) {
				subValidator.processDefinition(fieldName, handler, true);
			}
			handler.endEntity();
		}
	}
	
	public Set<String> getStructureNames() {
		return structures.keySet();
	}

	public boolean isSupportsFileType(String fileExtension) {
		if (excludedFileExtensions.contains(fileExtension)) {
			return false;
		} else 
		if (fileExtension != null && !fileExtension.isEmpty()) {
			if ("entity".equals(fileExtension)) {
				return true;
			} else {
				fileExtension = fileExtension.substring(0, 1).toUpperCase() + fileExtension.substring(1);
				return getStructureNames().contains(fileExtension);
			}
		} else {
			return false;
		}
	}

	public String getSinsInstallationDirectory() {
		return sinsInstallationDirectory;
	}

	public void setSinsInstallationDirectory(String sinsInstallationDirectory) {
		this.sinsInstallationDirectory = sinsInstallationDirectory;
	}

	public String parseValue(String currentLine) {
		String value = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			value = currentLine.substring(index).trim();
		}
		if (value != null) {
			value = value.replace("\"", " ").trim();
		}
		return value;
	}
	
	public String parseKeyWord(String currentLine) {
		// find keyWord
		String keyWord = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			keyWord = currentLine.substring(0, index);
		} else {
			keyWord = currentLine;
		}
//		if (!currentLine.isEmpty()) {
//			int spaceIndex = currentLine.indexOf(" "); 
//			if (spaceIndex != -1) {
//				keyWord = currentLine.substring(0, spaceIndex);
//			} else {
//				keyWord = currentLine;
//			}
//			keyWord = keyWord.trim();
//		}
//		debug("parseKeyWord: " + keyWord);
		return keyWord;
	}

	public List<String> getBaseModDirectory() {
		return baseModDirectories;
	}

	public void setBaseModDirectory(List<String> baseModDirectory) {
		this.baseModDirectories = baseModDirectory;
	}

	public FileReferenceHandler getFileReferenceHandler() {
		return fileReferenceHandler;
	}

	public void setFileReferenceHandler(FileReferenceHandler fileReferenceHandler) {
		this.fileReferenceHandler = fileReferenceHandler;
	}

	public HashMap<String, String> getMetaData(String key) {
		HashMap<String, String> meta = metaData.get(key);
		if (meta == null) {
			meta = new HashMap<String, String>();
			metaData.put(key, meta);
		}
		return meta;
	}

	public String getMetaData(String key, String property) {
		HashMap<String, String> meta = getMetaData(key);
		return meta.get(property);
	}

	public void setStringInfo(StringInfo stringInfo) {
		this.stringInfo = stringInfo;
	}

	public StringInfo getStringInfo() {
		return stringInfo;
	}

	public String getStringDirectory() {
		return stringDirectory;
	}

	public void setStringDirectory(String stringDirectory) {
		this.stringDirectory = stringDirectory;
	}

	public boolean isIncludeInstallation() {
		return includeInstallation;
	}

	public void setIncludeInstallation(boolean includeInstallation) {
		this.includeInstallation = includeInstallation;
	}

	public boolean isIncludeReference() {
		return includeReference;
	}

	public void setIncludeReference(boolean includeReference) {
		this.includeReference = includeReference;
	}

}
