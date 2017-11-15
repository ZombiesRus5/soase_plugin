package com.zombiesrus5.plugin.sose.domain;

public class EntityObject extends StructureObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6602118283647731519L;
	private String entityType;
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String toString() {
		return "Entity(" + entityType + "," + "Elements(" + getElements() + ")" + ")";
	}
}
