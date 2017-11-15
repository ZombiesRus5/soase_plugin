package com.zombiesrus5.plugin.sose.domain;

import java.util.ArrayList;
import java.util.List;

public class StructureObject extends SoaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3635220551218033473L;
	private String labelName;
	private String structureType;
	private int lineNumber;
	
	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getStructureType() {
		return structureType;
	}

	public void setStructureType(String structureType) {
		this.structureType = structureType;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String toString() {
		return "Structure(" + labelName + ", " + structureType + ", " + lineNumber + ") Elements(" + getElements() + ")";
	}
}
