package com.zombiesrus5.plugin.sose.editors.utils;

import java.util.Set;
import java.util.TreeSet;

import sose.tools.DefinitionHandler;

public class KeyWordCollector implements DefinitionHandler {
	private Set<String> keyWordSet = new TreeSet<String>();
	
	public Set<String> getKeyWordSet() {
		return keyWordSet;
	}
	
	@Override
	public void startStructure(String structureName, String structureType,
			boolean explicit, String[] references, String helpText)
			throws Exception {
		keyWordSet.add(structureName);
	}
	
	@Override
	public void startIteration(String fieldName, String helpText)
			throws Exception {
		keyWordSet.add(fieldName);
	}
	
	@Override
	public void startEntity(String entityType, String helpText)
			throws Exception {
		
	}
	
	@Override
	public void startConditionBlock(String field, String fieldRule, String[] conditions,
			String helpText) throws Exception {
		keyWordSet.add(field);
	}
	
	@Override
	public void startCondition(String fieldName, String condition,
			String helpText) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void field(String fieldName, String fieldRule, boolean explicit,
			String[] fieldValues, String fieldType, String[] references,
			String helpText) throws Exception {
		keyWordSet.add(fieldName);
		
	}
	
	@Override
	public void endStructure() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endIteration() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endEntity() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endConditionBlock() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void endCondition() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
