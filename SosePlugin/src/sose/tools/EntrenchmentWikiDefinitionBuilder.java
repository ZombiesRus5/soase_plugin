package sose.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jface.preference.IPreferenceStore;

import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntrenchmentWikiDefinitionBuilder implements DefinitionHandler {
	private Stack<Integer> depth = new Stack<Integer>();
	private Stack<PrintWriter> pw = new Stack<PrintWriter>();
	boolean printStructureReference = true;
	
	public static void main(String[] args) throws Exception {
		EntityParser parser = new EntityParser();
		
		parser.setDebug(false);
		parser.setWarn(false);
		
		parser.setIgnoreCaseOnFiles(true);
		parser.setDebug(true);
		parser.setup();
		
		parser.setStrictValidation("Entrenchment");

		Iterator<String> definitions = parser.getStructureNames().iterator();
		
		while(definitions.hasNext()) {
			String definition = definitions.next();
			if (definition.equals("ResearchSubject")) {
				System.out.println("pause");
			}
			EntrenchmentWikiDefinitionBuilder builder = new EntrenchmentWikiDefinitionBuilder(definition);
			parser.processDefinition(definition, builder, "TXT");
		}
	}
	
	public EntrenchmentWikiDefinitionBuilder(String definitionName) throws IOException {
		pw.push(new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/Entrenchment" + definitionName + ".wiki"))));
	}
	
	public void printHeader(int depth) {
		for (int i=0; i<depth; i++) {
			pw.peek().print("=");
		}
		pw.peek().flush();
	}
	
	public void printIndent(int depth) {
		for (int i=0; i<depth; i++) {
			pw.peek().print(' ');
		}
		pw.peek().flush();
	}
	
	public void print(String content) {
		pw.peek().print(content);
	}
	
	public void println() {
		pw.peek().println();
	}
	
	public void printBullet() {
		pw.peek().print("* ");
	}
	
	public void println(String contents) {
		pw.peek().println(contents);
		pw.peek().flush();
	}
	
	@Override
	public void endCondition() {
		depth.pop();
		pw.peek().flush();
	}

	@Override
	public void endEntity() {
		depth.pop();
		pw.peek().println();
		pw.peek().flush();
	}

	@Override
	public void endIteration() {
		depth.pop();
		pw.peek().flush();
	}

	@Override
	public void endStructure() {
		depth.pop();
		pw.peek().flush();
		pw.pop();
	}

	@Override
	public void field(String fieldName, String fieldRule, boolean explicit, String[] fieldValues, String fieldType, String[] references, String helpText) throws Exception {
		printIndent(depth.peek());
		printBullet();
		
		if ((fieldValues != null && fieldValues.length >0 && "Enumeration".equals(fieldType)) || (helpText != null && helpText.trim().length() > 0)) {
			print("[Entrenchment" + fieldRule + " " + fieldName + "]: [" + fieldType + "]");
			println();
			
			if (explicit) {
				if (fieldValues != null) {
					for (int i=0; i<fieldValues.length; i++) {
						printIndent(depth.peek().intValue()+1);
						printBullet();
						println(fieldValues[i]);
					}
				}
			} else {
				// it referenced so create a page
				PrintWriter pwField = new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/Entrenchment" + fieldRule + ".wiki")));
				pwField.println("#labels SOASE, DynamicContent");
				pwField.println("#summary " + fieldName + " Definition");
	
				if (helpText != null && helpText.trim().length() > 0) {
					pwField.println();
					pwField.println(helpText);
				}
	
				if (fieldValues != null && fieldValues.length > 0) {
					pwField.println("==References==");
					for (int i=0; i<references.length; i++) {
						pwField.print(" * [");
						pwField.print("Entrenchment" + references[i] + " " + references[i]);
						pwField.println("]");
					}
					pwField.println();
	
					pwField.println("==Values==");
					for (int i=0; i<fieldValues.length; i++) {
						pwField.print(" * ");
						pwField.println("[Entrenchment" + fieldValues[i] + " " + fieldValues[i] + "]");
					}
				}
				pwField.flush();
			}
		} else {
			print(fieldName + ": [" + fieldType + "]");
			println();
		}
	}

	@Override
	public void startCondition(String fieldName, String condition, String helpText) {
		printIndent(depth.peek());
		printBullet();
		print(condition);
		println();
		depth.push(depth.peek()+1);
	}

	@Override
	public void startEntity(String entityType, String helpText) {
		println("#labels SOASE, DynamicContent");
		println("#summary " + entityType + " Definition");
		//println("=" + entityType + " Definition=");
		if (helpText != null && helpText.trim().length() > 0) {
			println();
			println(helpText);
		}
		depth.push(0);
	}

	@Override
	public void startIteration(String fieldName, String helpText) {
		printIndent(depth.peek());
		printBullet();
		print(fieldName + ": [Iteration]");
		println();
		depth.push(depth.peek()+1);
	}

	@Override
	public void startStructure(String structureName, String structureType, boolean explicit, String[] references, String helpText) {
		if (printStructureReference) {
			if (references != null && references.length > 0) {
				println("==References==");
				for (int i=0; i<references.length; i++) {
					print(" * [");
					print("Entrenchment" + references[i] + " " + references[i]);
					println("]");
				}
				println();
			}
			printStructureReference = false;
		}
		
		printIndent(depth.peek());
		printBullet();
		if (structureName.equals(structureType)) {
			print("[Entrenchment" + structureName + " " + structureName + "]");
		} else {
			print("[Entrenchment" + structureType + " " + structureName + "]: [" + structureType + "]");
		}
		println();
		depth.push(depth.peek()+1);
		if (!explicit) {
			pw.push(new PrintWriter(new StringWriter()));
		} else {
			pw.push(pw.peek());
		}
	}

	@Override
	public void endConditionBlock() throws Exception {
		depth.pop();
		
		pw.pop();
	}

	@Override
	public void startConditionBlock(String field, String fieldRule, String[] conditions,
			String helpText) throws Exception {
		printIndent(depth.peek());
		printBullet();
		if (fieldRule == null) {
			fieldRule = field;
		}
		print("[Entrenchment" + fieldRule + " " + field + "]" + ": [Condition]");
		println();
		depth.push(1);
		
		pw.push(new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/Entrenchment" + fieldRule + ".wiki"))));
		println("#labels SOASE, DynamicContent");
		println("#summary " + field + " Condition Definition");
		println("<wiki:toc max_depth=\"5\"/>");
		if (helpText != null && helpText.trim().length() > 0) {
			println();
			println(helpText);
		}
		print("[Entrenchment" + field + " " + fieldRule + "]" + ": [Condition]");
		println();
	}

}
