package com.zombiesrus5.plugin.sose.domain;

public class FieldObject extends SoaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9093766615592134179L;
	private String name;
	private String value;
	private String fieldType;
	private int lineNumber;
	public String getName() {
		return name;
	}
	public void setName(String labelName) {
		this.name = labelName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public String toString() {
		return "Field(" + name + ", " + value + "," + fieldType + ", " + lineNumber + ")";
	}
}
