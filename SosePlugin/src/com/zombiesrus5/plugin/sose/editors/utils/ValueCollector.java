package com.zombiesrus5.plugin.sose.editors.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import sose.tools.DefinitionHandler;
import sose.tools.ValidationType;

public class ValueCollector implements DefinitionHandler {
	public ValueCollector(String keyWord) {
		super();
		this.keyWord = keyWord;
	}

	private String keyWord = null;
	private Set<String> values = new TreeSet<String>();
	private LinkedList<String> structure = new LinkedList<String>();
	
	public Set<String> getValues() {
		return values;
	}
	
	@Override
	public void startStructure(String structureName, String structureType,
			boolean explicit, String[] references, String helpText)
			throws Exception {
		structure.push(structureName);
	}
	
	@Override
	public void startIteration(String fieldName, String helpText)
			throws Exception {
		if (keyWord.equals(fieldName)) {
			values.add("0");
			values.add("1");
			values.add("2");
			values.add("3");
			values.add("4");
			values.add("5");
			values.add("6");
			values.add("7");
			values.add("8");
			values.add("9");
		}
	}
	
	@Override
	public void startEntity(String entityType, String helpText)
			throws Exception {
		
	}
	
	@Override
	public void startConditionBlock(String field, String fieldRule, String[] conditions,
			String helpText) throws Exception {
		if (keyWord.equals(field)) {
			values.addAll(Arrays.asList(conditions));
		}
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
		if (keyWord.equals("playerType") && structure.peek().equals("player")) {
			// this isn't a field
			return;
		}
		if (keyWord.equals(fieldName) && fieldValues != null) {
			values.addAll(Arrays.asList(fieldValues));
		} else if (keyWord.equals(fieldName) && ValidationType.DECIMAL.equals(fieldType)) {
			values.add("0.00000");
		} else if (keyWord.equals(fieldName) && ValidationType.INTEGER.equals(fieldType)) {
			values.add("0");
			values.add("1");
			values.add("2");
			values.add("3");
			values.add("4");
			values.add("5");
			values.add("6");
			values.add("7");
			values.add("8");
			values.add("9");
		}
	}
	
	@Override
	public void endStructure() throws Exception {
		// TODO Auto-generated method stub
		structure.pop();
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

