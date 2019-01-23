package sose.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jface.preference.IPreferenceStore;

import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class RebellionSinsWikiDefinitionBuilder implements DefinitionHandler {
	private Stack<Integer> depth = new Stack<Integer>();
	private Stack<PrintWriter> pw = new Stack<PrintWriter>();
	boolean printStructureReference = false;
	
	private static List<String> definitionsList = new ArrayList<String>();
	
	static boolean printSubStructures = false;
	static boolean newFileOnCondition = false;
	static boolean newFileOnEnumeration = false;
	
	public static void main(String[] args) throws Exception {
		EntityParser parser = new EntityParser();
		
		parser.setDebug(false);
		parser.setWarn(false);
		
		parser.setIgnoreCaseOnFiles(true);
		parser.setDebug(true);
		parser.setStrictValidation("Rebellion193");

		parser.setup();
		
		Iterator<String> definitions = parser.getStructureNames().iterator();
		
		definitionsList.add("Rebellion");
		definitionsList.add("Ability");
		definitionsList.add("Buff");
		definitionsList.add("CapitalShip");
		definitionsList.add("Constants");
		definitionsList.add("Fighter");
		definitionsList.add("Frigate");
		definitionsList.add("Galaxy");
		definitionsList.add("GalaxyScenarioDef");
		definitionsList.add("PlanetBonus");
		definitionsList.add("PlanetModuleHangarDefense");
		definitionsList.add("PlanetModuleRefinery");
		definitionsList.add("PlanetModuleShipFactory");
		definitionsList.add("PlanetModuleStandard");
		definitionsList.add("PlanetModuleTradePort");
		definitionsList.add("PlanetModuleWeaponDefense");
		definitionsList.add("Planet");
		definitionsList.add("Player");
		definitionsList.add("ResearchSubject");
		definitionsList.add("SpaceMine");
		definitionsList.add("StarBaseUpgrade");
		definitionsList.add("StarBase");
		definitionsList.add("Star");
		definitionsList.add("Str");
		definitionsList.add("Sounddata");
		
		while(definitions.hasNext()) {
			String definition = definitions.next();
			if (definition.equals("ResearchSubject")) {
				System.out.println("pause");
			}
			if (definitionsList.contains(definition)) {
				if ("Rebellion".equals(definition)) {
					printSubStructures = false;
				} else {
					printSubStructures = true;
				}
				RebellionSinsWikiDefinitionBuilder builder = new RebellionSinsWikiDefinitionBuilder(definition);
				parser.processDefinition(definition, builder, "TXT2");
			} 
		}
	}
	
	public RebellionSinsWikiDefinitionBuilder(String definitionName) throws IOException {
		pw.push(new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/rebellion/" + conform(definitionName) + ".md"))));
	}
	
	public void printHeader(int depth) {
		for (int i=0; i<depth; i++) {
			pw.peek().print("=");
		}
		pw.peek().flush();
	}
	
	public void printIndent(int depth) {
		for (int i=0; i<depth; i++) {
			pw.peek().print(":");
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
		pw.peek().print(": ");
	}
	
	public void println(String contents) {
		pw.peek().println(contents);
		pw.peek().flush();
	}
	
	@Override
	public void endCondition() {
		println();
		print("</div>");
		print("</div>");
		println();
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
		
		if ((fieldValues != null && fieldValues.length >0 && "Enumeration".equals(fieldType)) || (helpText != null && helpText.trim().length() > 0)) {
			if (fieldValues != null && !newFileOnEnumeration) {
			print("<div class=\"toccolours mw-collapsible mw-collapsed\">");
			println();
			}
			printIndent(depth.peek());
			printBullet();
			if (!newFileOnEnumeration) {
				print(fieldName + ": [[" + conform(fieldType) + "|" + fieldType + "]]");
			} else {
				print("[[" + conform(fieldRule) + "|" + fieldName + "]]" + ": [[" + conform(fieldType) + "|" + fieldType + "]]");
			}
			println();
			
			if (!newFileOnEnumeration || (explicit && (helpText == null || helpText.isEmpty()))) {
				if (fieldValues != null) {
					print("<div class=\"mw-collapsible-content\">");
					println();
					for (int i=0; i<fieldValues.length; i++) {
						printIndent(depth.peek().intValue()+1);
						printBullet();
						println(fieldValues[i]);
					}
					println();
					print("</div>");
					print("</div>");
					println();
				}

			} else {
				// it referenced so create a page
				PrintWriter pwField = new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/rebellion/" + conform(fieldRule) + ".md")));
//				pwField.println("#labels SOASE, DynamicContent");
//				pwField.println("#summary " + fieldName + " Definition");
				pwField.println("[[RebellionSyntax|Rebellion Syntax]]");
				pwField.println("=" + fieldRule + " Definition=");

				if (helpText != null && helpText.trim().length() > 0) {
					pwField.println();
					pwField.println(helpText);
				}
	
				if (fieldValues != null && fieldValues.length >0 && "Enumeration".equals(fieldType)) {
					pwField.println("==References==");
					for (int i=0; i<references.length; i++) {
						pwField.print(" * [[");
						pwField.print("" + conform(references[i]) + "|" + references[i]);
						pwField.println("]]");
					}
					pwField.println();
	
					pwField.println("==Values==");
					for (int i=0; i<fieldValues.length; i++) {
						pwField.print(" * ");
						pwField.println("[[" + conform(fieldValues[i]) + "|" + fieldValues[i] + "]]");
					}
				}
				pwField.flush();
				pwField.close();
			}
		} else {
			printIndent(depth.peek());
			printBullet();
			print(fieldName + ": [[" + fieldType + "]]");
			println();
		}
	}

	private String conform(String fieldRule) {
		// TODO Auto-generated method stub
		return fieldRule.replace(':', '_') + "Syntax";
	}

	@Override
	public void startCondition(String fieldName, String condition, String helpText) {
		print("<div class=\"toccolours mw-collapsible mw-collapsed\">");
		println();
//		printIndent(depth.peek());
//		printBullet();
		print(";");
		print(condition);
		println();
		print("<div class=\"mw-collapsible-content\">");
		println();
		depth.push(0);
	}

	@Override
	public void startEntity(String entityType, String helpText) {
//		println("#labels SOASE, DynamicContent");
//		println("#summary " + entityType + " Definition");
		println("[[RebellionSyntax|Rebellion Syntax]]");
		println("=" + entityType + " Definition=");
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
		print(fieldName + ": [[IterationSyntax|Iteration]]");
		println();
		depth.push(depth.peek()+1);
	}

	@Override
	public void startStructure(String structureName, String structureType, boolean explicit, String[] references, String helpText) {
		if (printStructureReference) {
			if (references != null && references.length > 0) {
				println("==References==");
				for (int i=0; i<references.length; i++) {
					print(" * [[");
					print("" + conform(references[i]) + "|" + references[i]);
					println("]]");
				}
				println();
			}
			printStructureReference = false;
		}
		
		printIndent(depth.peek());
		printBullet();
		if (!printSubStructures) {
			if (structureName.equals(structureType)) {
				print("[[" + conform(structureName) + "| " + structureName + "]]");
			} else {
				print("[[" + conform(structureType) + "|" + structureName + "]]"); //: [[" + conform(structureType) + " | Structure ]]");
			}
		} else {
			print(structureName);
		}
		println();
		depth.push(depth.peek()+1);
		if (!explicit) {
			if (printSubStructures) {
				pw.push(pw.peek());
			} else {
				pw.push(new PrintWriter(new StringWriter()));
			}
		} else {
			pw.push(pw.peek());
		}
	}

	@Override
	public void endConditionBlock() throws Exception {
		println();
		print("</div>");
		print("</div>");
		println();

		depth.pop();
		
		pw.pop();
	}

	@Override
	public void startConditionBlock(String field, String fieldRule, String[] conditions,
			String helpText) throws Exception {
		if (fieldRule == null) {
			fieldRule = field;
		}
		if (newFileOnCondition == true) {
			printIndent(depth.peek());
			printBullet();
			print("[[" + conform(fieldRule) + "|" + field + "]]" + ": [[ConditionSyntax|Condition]]");
			println();
			depth.push(1);
		
			pw.push(new PrintWriter(new FileWriter(new File("C:/Users/LaffoonGame/workspace/Sins Wiki/rebellion/" + conform(fieldRule) + ".md"))));
//			println("#labels SOASE, DynamicContent");
//			println("#summary " + field + " Condition Definition");
			println("[[RebellionSyntax|Rebellion Syntax]]");
			//println("=" + fieldRule + " Definition=");
			//println("<wiki:toc max_depth=\"5\"/>");
			if (helpText != null && helpText.trim().length() > 0) {
				println();
				println(helpText);
			}
			print("= [[" + field + "|" + fieldRule + "]]" + ": [[ConditionSyntax|Condition]] =");
			println();
		} else {
			print("<div class=\"toccolours mw-collapsible mw-collapsed\">");
			println();
			printIndent(depth.peek());
			printBullet();
			print(field + ": [[ConditionSyntax|Condition]]");
			println();
			print("<div class=\"mw-collapsible-content\">");
			println();
			depth.push(1);
		
			pw.push(pw.peek());
		}
		
	}

}
