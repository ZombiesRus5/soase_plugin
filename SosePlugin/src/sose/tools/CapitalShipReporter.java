package sose.tools;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jdt.internal.ui.preferences.formatter.ProfileManager.KeySet;

public class CapitalShipReporter extends ContentHandlerChain {
	ErrorHandler error = null;
	FileReferenceHandler fileReference = null;
	EntityParser parser = null;
	
	public CapitalShipReporter(EntityParser parser, FileReferenceHandler fileReference, ContentHandler reporter, ErrorHandler errorHandler) {
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
		if (fieldValue.isEmpty() && (fieldName.equals("ability:0") ||
				fieldName.equals("ability:1") ||
				fieldName.equals("ability:2") ||
				fieldName.equals("ability:3") ||
				fieldName.equals("ability:4"))) {
			String levelSourceType = parser.getMetaData(fieldValue, "levelSourceType");
			if (levelSourceType == null) {
//				String message = MessageFormat.format("levelSourceType unknown {0}", levelSourceType);
//				error.error(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
			} else if (levelSourceType.equals("Intrinsic")) {
				// we're good here
				if (fieldName.equals("ability:4")) {
					String useCostType = parser.getMetaData(fieldValue, "useCostType");
					if (useCostType.equals("Passive") == false) {
						String message = MessageFormat.format("useCostType must be passive for ability:4 {0}", useCostType);
						error.error(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
					}
				}
			} else if (levelSourceType.equals("ResearchStartsAt0") 
					|| levelSourceType.equals("FixedLevel0")
					|| levelSourceType.equals("ResearchWithBase")
					|| levelSourceType.equals("ResearchWithoutBase")) {
				// these should only be passive
				String useCostType = parser.getMetaData(fieldValue, "useCostType");
				if (useCostType.equals("Passive") == false) {
					String message = MessageFormat.format("useCostType must be passive for non Intrinsic abilities {0}", levelSourceType);
					error.error(new EntityParseException(ValidationType.ENTITY, message, lineNumber, fieldName));
				}
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
