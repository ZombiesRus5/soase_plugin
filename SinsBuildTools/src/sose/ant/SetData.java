package sose.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.StringUtils;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class SetData extends Task {

	String keyword = null;
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	String value = null;
	
	String rule = RULE_SET;
	
	String condition = null;
	
	static String RULE_SET = "set";
	static String RULE_MULTIPLY = "multiply";
	static String RULE_DIVIDE = "divide";
	static String RULE_INSERTAFTER = "insertAfter";
	static String RULE_INSERTBEFORE = "insertBefore";

	String maxValue = "999999.0";
	
	static String CONDITION_HAS_FLOAT_MODIFIERS = "hasFloatModifiers";
	static String CONDITION_EQUALS = "equals";
	
	static String conditionValue = null;

	public void add(ResourceCollection res) {
		resourceList.add(res);
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
	
	@Override
	public void execute() throws BuildException {
		try {		
			Iterator<ResourceCollection> itr = resourceList.iterator();

			while (itr.hasNext()) {
				ResourceCollection rc = itr.next();
				if (rc instanceof FileSet && rc.isFilesystemOnly()) {
					FileSet fs = (FileSet) rc;
					DirectoryScanner ds = null;
					
					try {
						ds = fs.getDirectoryScanner(getProject());
					} catch (BuildException e) {
						if (!getMessage(e).endsWith(" not found.")) {
							throw e;
						} else {
							log("Warning: " + getMessage(e), Project.MSG_ERR);
							continue;
						}
					}
	
					String[] srcFiles = ds.getIncludedFiles();
					for (int i=0; i<srcFiles.length; i++) {
	//					System.out.println(srcFiles[i]);
						setData(fs, srcFiles[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}
		
	private void setData(FileSet fs, String sourceFile) throws Exception {
		File sourceDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		File outputDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
		StringWriter sw = new StringWriter();
		PrintWriter defWriter = new PrintWriter(sw);

		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			defWriter.println(line);
			line = defReader.readLine();
		}
		defWriter.println(line);
		System.out.println(keyword);
		Vector<String> keywordParts = StringUtils.split(keyword, '.');
		System.out.println(keywordParts);
		String keywordFinal = keywordParts.lastElement();
		for (int i=0; i<keywordParts.size()-1; i++) {
			while (defReader.ready()) {
				line = defReader.readLine();
				
				if (line.contains(keywordParts.get(i))) {
					defWriter.println(line);
					break;
				}
				defWriter.println(line);
			}
		}
		while (defReader.ready()) {
			line = defReader.readLine();
			
			if (line.contains(keywordFinal)) {
				break;
			}
			defWriter.println(line);
		}
		
		// need to check this line
		if (CONDITION_EQUALS.equalsIgnoreCase(condition)) {
			StringTokenizer parts = new StringTokenizer(line.trim(), " \t", false);
			String keywordPart = parts.nextToken();
			String keywordValue = parts.nextToken();
			System.out.println(keywordValue);
			if (line.contains("\"") && !(keywordValue.equals("\"" + conditionValue + "\"") || 
					keywordValue.equals(conditionValue))) {
				System.out.println("Not equal to: " + "\"" + conditionValue + "\"");
				return;
			} 
		}
		
		if (defReader.ready() || !line.isEmpty()) {
			for (int tabs=0; tabs<keywordParts.size()-1; tabs++) {
				defWriter.print('\t');
			}
			if (RULE_SET.equalsIgnoreCase(rule)) {
				if (line.contains("\"")) {
					defWriter.println(keywordFinal + " \"" + value + "\"");
				} else {
					defWriter.println(keywordFinal + " " + value);
				}
			} else if (RULE_MULTIPLY.equalsIgnoreCase(rule)) {
				StringTokenizer parts = new StringTokenizer(line.trim(), " \t", false);
				String keywordPart = parts.nextToken();
				String keywordValue = parts.nextToken();
				if (keywordValue.indexOf('.') == -1) {
					int intValue = Integer.parseInt(keywordValue);
					BigDecimal decMaxValue = new BigDecimal(maxValue);
					float multiplier = Float.parseFloat(value);
					double result = multiplier * intValue;
					if (result > decMaxValue.doubleValue()) {
						result = decMaxValue.doubleValue();
					}
					defWriter.println(keywordFinal + " " + (result));
				} else {
					BigDecimal decValue = new BigDecimal(keywordValue);
					BigDecimal decMaxValue = new BigDecimal(maxValue);
					float multiplier = Float.parseFloat(value);
					defWriter.print(keywordFinal + " ");
					double result = multiplier * decValue.doubleValue();
					if (result > decMaxValue.doubleValue()) {
						result = decMaxValue.doubleValue();
					}
					defWriter.print(new DecimalFormat("#0.00000").format(result));
					defWriter.println();
				}
			} else if (RULE_DIVIDE.equalsIgnoreCase(rule)) {
				StringTokenizer parts = new StringTokenizer(line.trim(), " \t", false);
				String keywordPart = parts.nextToken();
				String keywordValue = parts.nextToken();
				if (keywordValue.indexOf('.') == -1) {
					int intValue = Integer.parseInt(keywordValue);
					BigDecimal decMaxValue = new BigDecimal(maxValue);
					float multiplier = Float.parseFloat(value);
					double result = multiplier / intValue;
					if (result > decMaxValue.doubleValue()) {
						result = decMaxValue.doubleValue();
					}
					defWriter.println(keywordFinal + " " + (result));
				} else {
					BigDecimal decValue = new BigDecimal(keywordValue);
					BigDecimal decMaxValue = new BigDecimal(maxValue);
					float multiplier = Float.parseFloat(value);
					defWriter.print(keywordFinal + " ");
					double result = multiplier / decValue.doubleValue();
					if (result > decMaxValue.doubleValue()) {
						result = decMaxValue.doubleValue();
					}
					defWriter.print(new DecimalFormat("#0.00000").format(result));
					defWriter.println();
				}
			} else if (RULE_INSERTAFTER.equalsIgnoreCase(rule)) {
				defWriter.println(line);
				System.out.println(value);
				for (int i=0; i<value.length(); i++) {
					if (value.charAt(i) == '\\') {
						if (value.charAt(i + 1) == 'n') {
							i++;
							defWriter.println();
						} else if (value.charAt(i+1) == 't') {
							i++;
							defWriter.print('\t');
						}
					} else {
						defWriter.print(value.charAt(i));
					}
				}
				defWriter.println();
			} else if (RULE_INSERTBEFORE.equalsIgnoreCase(rule)) {
				System.out.println(value);
				for (int i=0; i<value.length(); i++) {
					if (value.charAt(i) == '\\') {
						if (value.charAt(i + 1) == 'n') {
							i++;
							defWriter.println();
						} else if (value.charAt(i+1) == 't') {
							i++;
							defWriter.print('\t');
						}
					} else {
						defWriter.print(value.charAt(i));
					}
				}
				defWriter.println();
				defWriter.println(line);
			}

		
			while (defReader.ready()) {
				line = defReader.readLine();

				if (CONDITION_HAS_FLOAT_MODIFIERS.equalsIgnoreCase(condition)) {
					// check line for float modifiers
					if (line.contains("researchFloatModifiers")) {
						StringTokenizer parts = new StringTokenizer(line.trim(), " \t", false);
						String keywordPart = parts.nextToken();
						String keywordValue = parts.nextToken();
						int intValue = Integer.parseInt(keywordValue);
						if (intValue == 0) {
							return;
						}
					}
				} 
				defWriter.println(line);
			}
		}
		
		defReader.close();
		
		defWriter.flush();
		sw.flush();
		PrintWriter fileWriter = new PrintWriter(new FileWriter(outputDef));
		fileWriter.print(sw.getBuffer());
		fileWriter.flush();
		fileWriter.close();
		defWriter.close();
		
		} finally {

		}
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public static String getConditionValue() {
		return conditionValue;
	}

	public static void setConditionValue(String conditionValue) {
		SetData.conditionValue = conditionValue;
	}

}
