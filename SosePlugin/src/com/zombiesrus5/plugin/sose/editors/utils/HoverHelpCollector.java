package com.zombiesrus5.plugin.sose.editors.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Stack;

import sose.tools.DefinitionHandler;
import sose.tools.EntityParser;
import sose.tools.WikiDefinitionBuilder;

public class HoverHelpCollector implements DefinitionHandler {
	public HoverHelpCollector(String hoverField) {
		super();
		// TODO Auto-generated constructor stub
		this.hoverField = hoverField;
	}

	public String getHoverInfo() {
		pw.flush();
		hoverInfo.flush();
		return hoverInfo.toString();
	}

	private Stack<Integer> depth = new Stack<Integer>();
	StringWriter hoverInfo = new StringWriter();
	PrintWriter pw = new PrintWriter(hoverInfo);
	boolean printStructureReference = true;
	String hoverField = null;

	public static void main(String[] args) throws Exception {
		EntityParser parser = new EntityParser();

		parser.setDebug(false);
		parser.setWarn(false);

		parser.setIgnoreCaseOnFiles(true);
		parser.setDebug(false);
		parser.setup();

		Iterator<String> definitions = parser.getStructureNames().iterator();

		while (definitions.hasNext()) {
			String definition = definitions.next();
			if (definition.equals("GenericLevel")) {
//				System.out.println("pause");
			}
			HoverHelpCollector builder = new HoverHelpCollector(definition);
			parser.processDefinition(definition, builder, "TXT");
//			System.out.println(builder.getHoverInfo());
		}
	}

	public void printHeader(int depth) {
		for (int i = 0; i < depth; i++) {
			pw.print("=");
		}
		pw.flush();
	}

	public void printIndent(int depth) {
		for (int i = 0; i < depth; i++) {
			pw.print(' ');
		}
		pw.flush();
	}

	public void print(String content) {
		pw.print(content);
	}

	public void println() {
		pw.println("<br>");
	}

	public void printBullet() {
		pw.print("<li>");
	}

	public void println(String contents) {
		pw.print(contents);
		println();
		pw.flush();
	}

	@Override
	public void endCondition() {
		depth.pop();
		pw.flush();
	}

	@Override
	public void endEntity() {
		depth.pop();
		println();
		pw.flush();
	}

	@Override
	public void endIteration() {
		depth.pop();
		println();
		pw.flush();
	}

	@Override
	public void endStructure() {
		depth.pop();
		println();
		pw.flush();
	}

	@Override
	public void field(String fieldName, String fieldRule, boolean explicit,
			String[] fieldValues, String fieldType, String[] references,
			String helpText) throws Exception {
		printIndent(depth.peek());

		if ((fieldValues != null && fieldValues.length > 0 && "Enumeration"
				.equals(fieldType))
				|| (helpText != null && helpText.trim().length() > 0)) {
			print(fieldName + ":  ");
			print(fieldType + " ");
			
				if (fieldValues != null) {
					for (int i = 0; i < fieldValues.length; i++) {
						printIndent(depth.peek().intValue() + 1);
						print("<li>");
						print(fieldValues[i]);
						print("</li>");
						print("\n");
					}
				}
			println();
		} else {
			print(fieldName + ":  ");
			print(fieldType + " ");
			println();
		}
	}

	@Override
	public void startCondition(String fieldName, String condition,
			String helpText) {
		printIndent(depth.peek());
		printBullet();
		print(condition);
		println();
		depth.push(depth.peek() + 1);
	}

	@Override
	public void startEntity(String entityType, String helpText) {
//		println("<b>" + entityType + " Definition" + "</b><br/>");
		// println("=" + entityType + " Definition=");
//		if (helpText != null && helpText.trim().length() > 0) {
//			println();
//			println(helpText);
//		}
		depth.push(0);
	}

	@Override
	public void startIteration(String fieldName, String helpText) {
		printIndent(depth.peek());
//		printBullet();
		print(fieldName + ": [Iteration]");
		println();
		depth.push(depth.peek() + 1);
	}

	@Override
	public void startStructure(String structureName, String structureType,
			boolean explicit, String[] references, String helpText) {
		if (printStructureReference) {
//			if (references != null && references.length > 0) {
//				println("==References==");
//				for (int i = 0; i < references.length; i++) {
//					print(" * [");
//					print(references[i]);
//					println("]");
//				}
//				println();
//			}
			printStructureReference = false;
		}

		printIndent(depth.peek());
//		printBullet();
		if (structureName.equals(structureType)) {
			print("<b>" + structureName + "</b>");
		} else {
			print("<b>" + structureName + "</b>: <a href='http://yahoo.com/' target='_blank'>" + structureType + "</a>");
		}
		println();
		depth.push(depth.peek() + 1);

	}

	@Override
	public void endConditionBlock() throws Exception {
		println();
		depth.pop();
	}

	@Override
	public void startConditionBlock(String field, String fieldRule, String[] conditions,
			String helpText) throws Exception {
		printIndent(depth.peek());
//		printBullet();
		print("<b>" + field + "</b>" + ": [Condition]");
		println();
		depth.push(1);
	}

}
