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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.util.StringUtils;

import sose.tools.EntityParser;
import sose.tools.StringInfo;
import sose.tools.VanillaWikiDefinitionBuilder;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class ExtractData extends Task {
	String stringInfoFile = null;
	StringInfo stringInfo = null;
	
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();

	String entityType = "";
	
	String toFile = "";
	
	String delimeter = ",";
	
	static String TITAN = "Titan";
	static String CAPITALSHIP = "CapitalShip";
	static String FRIGATE = "Frigate";
	
	boolean printHeader = true;
	boolean summary = false;
	
	String summaryHeaders = null;
	
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
			loadStringInfos();
			
			Iterator<ResourceCollection> itr = resourceList.iterator();
			File outputFile = new File(toFile);
			PrintWriter fileWriter = new PrintWriter(new FileWriter(outputFile));

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
						
						if (entityType.equals(CAPITALSHIP)) {
							extractCapitalShipData(fileWriter, fs, srcFiles[i]);
						} else if (entityType.equals(FRIGATE)) {
							extractFrigateData(fileWriter, fs, srcFiles[i]);
						} else if (entityType.equals(TITAN)) {
							extractTitanData(fileWriter, fs, srcFiles[i]);
						}
					}
				}
			}
			
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}
		
	private void loadStringInfos() throws Exception {
		EntityParser parser = new EntityParser();
		
		parser.setDebug(false);
		parser.setWarn(false);
		
		parser.setIgnoreCaseOnFiles(true);
		parser.setDebug(true);
		parser.setup();
		
		parser.setStrictValidation("Rebellion");

		InputStream stringInfoStream = new FileInputStream(stringInfoFile);
		stringInfo = new StringInfo(parser, stringInfoStream);
	}

	private void extractTitanData(PrintWriter writer, FileSet fs,
			String sourceFile) {
		File sourceDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		Map<String, String> data = new HashMap<String, String>();
		LinkedList<String> header = new LinkedList<String>();
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));

		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = defReader.readLine();
		}

		header.add("entityName");
		data.put("entityName", sourceFile);
		extract(header, data, "ability:0", defReader);
		extract(header, data, "ability:1", defReader);
		extract(header, data, "ability:2", defReader);
		extract(header, data, "ability:3", defReader);
		
		String canBomb = get("canBomb", defReader);
		if ("TRUE".equals(canBomb)) {
			extract(header, data, "baseDamage", defReader);
			extract(header, data, "basePopulationKilled", defReader);
			extract(header, data, "bombingFreqTime", defReader);
			extract(header, data, "baseRange", defReader);
		} else {
			empty(header, data, "baseDamage");
			empty(header, data, "basePopulationKilled");
			empty(header, data, "bombingFreqTime");
			empty(header, data, "baseRange");
		}
		
		String[] basePriceData = {"credits", "metal", "crystal" };
		extract(header, data, "basePrice", basePriceData, defReader);
		extract(header, data, "slotCount", defReader);
		
		String[] subData = {"StartValue", "ValueIncreasePerLevel"};
		extract(header, data, "MaxHullPoints", subData, defReader);
		extract(header, data, "MaxShieldPoints", subData, defReader);
		extract(header, data, "HullPointRestoreRate", subData, defReader);
		extract(header, data, "ShieldPointRestoreRate", subData, defReader);
		extract(header, data, "ArmorPointsFromExperience", subData, defReader);
		extract(header, data, "maxMitigation", subData, defReader);
		extract(header, data, "MaxAntiMatter", subData, defReader);
		extract(header, data, "AntiMatterRestoreRate", subData, defReader);
		extract(header, data, "CultureProtectRate", subData, defReader);

		extractStringInfo(header, data, "NameStringID", defReader);
		extractStringInfo(header, data, "DescriptionStringID", defReader);
		extract(header, data, "statCountType", defReader);
		extract(header, data, "armorType", defReader);
		
		String numWeapons = get("NumWeapons", defReader);
		int weapons = Integer.parseInt(numWeapons);
		String[] subWeaponData = {
				"WeaponType",
				"AttackType",
				"DamageAffectType",
				"DamageApplyType",
				"DamageType",
				"WeaponClassType",
				"DamagePerBank:FRONT",
				"DamagePerBank:BACK",
				"DamagePerBank:LEFT",
				"DamagePerBank:RIGHT",
				"Range",
				"PreBuffCooldownTime",
			};
			
		for (int i=0; i<weapons; i++) {
			get("Weapon", defReader);
			extract(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		for (int i=weapons; i<5; i++) {
			empty(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		String frontBankValue = extract(header, data, "TargetCountPerBank:FRONT", defReader);
		String backBankValue = extract(header, data, "TargetCountPerBank:BACK", defReader);
		String leftBankValue = extract(header, data, "TargetCountPerBank:LEFT", defReader);
		String rightBankValue = extract(header, data, "TargetCountPerBank:RIGHT", defReader);
		
		int frontBank = Integer.parseInt(frontBankValue);
		int backBank = Integer.parseInt(backBankValue);
		int leftBank = Integer.parseInt(leftBankValue);
		int rightBank = Integer.parseInt(rightBankValue);
		
		extract(header, data, "CommandPoints", subData, defReader);
		extract(header, data, "weaponCooldownDecreasePerc", subData, defReader);
		extract(header, data, "weaponDamageIncreasePerc", subData, defReader);
		
		double totalFront = 0;
		double totalBack = 0;
		double totalLeft = 0;
		double totalRight = 0;
		
		double totalFrontBanks = 0;
		double totalBackBanks = 0;
		double totalLeftBanks = 0;
		double totalRightBanks = 0;

		double totalFrontLevel10 = 0;
		double totalBackLevel10 = 0;
		double totalLeftLevel10 = 0;
		double totalRightLevel10 = 0;
		
		String weaponCooldownDecreaseValue = data.get("weaponCooldownDecreasePerc.ValueIncreasePerLevel");
		String weaponDamageIncreaseValue = data.get("weaponDamageIncreasePerc.ValueIncreasePerLevel");
		double weaponCooldownDecrease = Double.parseDouble(weaponCooldownDecreaseValue);
		double weaponDamageIncrease = Double.parseDouble(weaponDamageIncreaseValue);
		
		for (int i=0; i<5; i++) {
			String frontValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:FRONT");
			String backValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:BACK");
			String leftValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:LEFT");
			String rightValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:RIGHT");
			
			String cooldownValue = data.get("Weapon[" + i + "]" + "." + "PreBuffCooldownTime");
			
			double front = 0;
			double back = 0;
			double left = 0;
			double right = 0;
			double frontLevel10 = 0;
			double backLevel10 = 0;
			double leftLevel10 = 0;
			double rightLevel10 = 0;
			double cooldown = 0;
			if (frontValue != null && !frontValue.isEmpty()) {
				cooldown = Double.parseDouble(cooldownValue);
				
				front = Double.parseDouble(frontValue) / cooldown;
				back = Double.parseDouble(backValue) / cooldown;
				left = Double.parseDouble(leftValue) / cooldown;
				right = Double.parseDouble(rightValue) / cooldown;
				
				totalFront += front;
				totalBack += back;
				totalLeft += left;
				totalRight += right;

				totalFrontBanks += front * frontBank;
				totalBackBanks += back * backBank;
				totalLeftBanks += left * leftBank;
				totalRightBanks += right * rightBank;
				
				frontLevel10 = (Double.parseDouble(frontValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				backLevel10 = (Double.parseDouble(backValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				leftLevel10 = (Double.parseDouble(leftValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				rightLevel10 = (Double.parseDouble(rightValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				
				totalFrontLevel10 += frontLevel10 * frontBank;
				totalBackLevel10 += backLevel10 * backBank;
				totalLeftLevel10 += leftLevel10 * leftBank;
				totalRightLevel10 += rightLevel10 * rightBank;
			}
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT(Single)", front);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK(Single)", back);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT(Single)", left);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT(Single)", right);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL(Single)", front + back + left + right);

			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT(Total)", front * frontBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK(Total)", back * backBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT(Total)", left * leftBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT(Total)", right * rightBank);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL(Total)", front * frontBank + back * backBank + left * leftBank + right * rightBank);

			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT(10)", frontLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK(10)", backLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT(10)", leftLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT(10)", rightLevel10);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL(10)", frontLevel10 + backLevel10 + leftLevel10 + rightLevel10);
		}

		defaultValue(header, data, "DPS:FRONT(Single)", totalFront);
		defaultValue(header, data, "DPS:BACK(Single)", totalBack);
		defaultValue(header, data, "DPS:LEFT(Single)", totalLeft);
		defaultValue(header, data, "DPS:RIGHT(Single)", totalRight);

		defaultValue(header, data, "DPS:ALL(Single)", totalFront + totalBack + totalLeft + totalRight);
		
		defaultValue(header, data, "DPS:FRONT(Total)", totalFrontBanks);
		defaultValue(header, data, "DPS:BACK(Total)", totalBackBanks);
		defaultValue(header, data, "DPS:LEFT(Total)", totalLeftBanks);
		defaultValue(header, data, "DPS:RIGHT(Total)", totalRightBanks);

		defaultValue(header, data, "DPS:ALL(Total)", totalFrontBanks + totalBackBanks + totalLeftBanks + totalRightBanks);

		defaultValue(header, data, "DPS:FRONT(10)", totalFrontLevel10);
		defaultValue(header, data, "DPS:BACK(10)", totalBackLevel10);
		defaultValue(header, data, "DPS:LEFT(10)", totalLeftLevel10);
		defaultValue(header, data, "DPS:RIGHT(10)", totalRightLevel10);

		defaultValue(header, data, "DPS:ALL(10)", totalFrontLevel10 + totalBackLevel10 + totalLeftLevel10 + totalRightLevel10);

		if (summary == true) {
			header = getSummaryHeadersAsList();
		}
		if (printHeader) {
			printHeader(header, data, writer);
			//printTitanHeader(header, data, writer);
			printHeader = false;
		}
		printData(header, data, writer);
		
		//printTitanBaseData
		//printTitanPerLevelData
		//for (i=0; i<10; i++) {
		//	printTitanLeveledData
		//}
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
	private void defaultValue(LinkedList<String> header, Map<String, String> data,
			String string, double result) {
		header.add(string);
		data.put(string, "" + new DecimalFormat("#0.00000").format(result));
		
	}
	private void defaultValueAfter(String afterHeader, LinkedList<String> header, Map<String, String> data,
			String string, double result) {
		int index = header.indexOf(afterHeader);
		header.add(index+1, string);
		data.put(string, "" + new DecimalFormat("#0.00000").format(result));
		
	}
	private void empty(List<String> header, Map<String, String> data, String keyword,
			String[] subData, LineNumberReader defReader) {
		// TODO Auto-generated method stub
		for (int i=0; i<subData.length; i++) {
			header.add(keyword + "." + subData[i]);
			data.put(keyword + "." + subData[i], "");
		}
	}

	private void extract(List<String> header, Map<String, String> data, String keyword,
			String[] subData, LineNumberReader defReader) throws Exception {
		for (int i=0; i<subData.length; i++) {
			String value = get(subData[i], defReader);
			header.add(keyword + "." + subData[i]);
			data.put(keyword + "." + subData[i], value);
		}
	}
	
	private String extractValue(List<String> header, Map<String, String> data, String keyword,
			String[] subData, LineNumberReader defReader) throws Exception {
		for (int i=0; i<subData.length; i++) {
			String value = get(subData[i], defReader);
			header.add(keyword + "." + subData[i]);
			data.put(keyword + "." + subData[i], value);
			return value;
		}
		return null;
	}

	private void printHeader(List<String> header, Map<String, String> data, PrintWriter writer) {
		for (int i=0; i<header.size(); i++) {
			System.out.println(header.get(i) + ",");
			writer.print(header.get(i).trim() + ",");
		}
		writer.println();
	}
	
	private void printData(List<String> header, Map<String, String> data, PrintWriter writer) {
		for (int i=0; i<header.size(); i++) {
			System.out.println(header.get(i) + " " + data.get(header.get(i)) + ",");
			writer.print(data.get(header.get(i).trim()) + ",");
		}
		writer.println();
	}

	private void empty(List<String> header, Map<String, String> data, String keyword) {
		// TODO Auto-generated method stub
		header.add(keyword);
		data.put(keyword, "");
	}

	public String parseValue(String currentLine) {
		String value = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			value = currentLine.substring(index).trim();
		}
		if (value != null) {
			value = value.replace("\"", " ").trim();
		}
		return value;
	}
	
	public String parseKeyWord(String currentLine) {
		// find keyWord
		String keyWord = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			keyWord = currentLine.substring(0, index);
		} else {
			keyWord = currentLine;
		}
//		if (!currentLine.isEmpty()) {
//			int spaceIndex = currentLine.indexOf(" "); 
//			if (spaceIndex != -1) {
//				keyWord = currentLine.substring(0, spaceIndex);
//			} else {
//				keyWord = currentLine;
//			}
//			keyWord = keyWord.trim();
//		}
//		debug("parseKeyWord: " + keyWord);
		return keyWord;
	}
	
	private String extractStringInfo(List<String> header, Map<String, String> data, String keyword,
			LineNumberReader defReader) throws Exception {
		System.out.println(keyword);

		String line = null;
		while (defReader.ready()) {
			String currentLine = defReader.readLine();
			
			if (currentLine.contains(keyword)) {
				line = currentLine;
				break;
			}
		}
		
		if (line != null) {
			String value = parseValue(line);
			header.add(keyword);
			data.put(keyword, stringInfo.getValue(value));
			System.out.println(value);
			return value;
		}
		return null;
	}
	
	private String extract(List<String> header, Map<String, String> data, String keyword,
			LineNumberReader defReader) throws Exception {
		System.out.println(keyword);

		String line = null;
		while (defReader.ready()) {
			String currentLine = defReader.readLine();
			
			if (currentLine.contains(keyword)) {
				line = currentLine;
				break;
			}
		}
		
		if (line != null) {
			String value = parseValue(line);
			header.add(keyword);
			data.put(keyword, value);
			System.out.println(value);
			return value;
		}
		return null;
	}

	private String get(String keyword,
			LineNumberReader defReader) throws Exception {
		System.out.println(keyword);

		String line = null;
		while (defReader.ready()) {
			String currentLine = defReader.readLine();
			
			if (currentLine.contains(keyword)) {
				line = currentLine;
				break;
			}
		}
		
		if (line != null) {
			String value = parseValue(line);
			System.out.println(value);
			return value;
		}
		return null;
	}
	
	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getToFile() {
		return toFile;
	}

	public void setToFile(String toFile) {
		this.toFile = toFile;
	}

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	private void extractFrigateData(PrintWriter writer, FileSet fs,
			String sourceFile) {
		File sourceDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		Map<String, String> data = new HashMap<String, String>();
		LinkedList<String> header = new LinkedList<String>();
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
	
		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = defReader.readLine();
		}

		header.add("entityName");
		data.put("entityName", sourceFile);
		
		String canBomb = get("canBomb", defReader);
		if ("TRUE".equals(canBomb)) {
			extract(header, data, "baseDamage", defReader);
			extract(header, data, "basePopulationKilled", defReader);
			extract(header, data, "bombingFreqTime", defReader);
			extract(header, data, "baseRange", defReader);
		} else {
			empty(header, data, "baseDamage");
			empty(header, data, "basePopulationKilled");
			empty(header, data, "bombingFreqTime");
			empty(header, data, "baseRange");
		}
		
		extract(header, data, "frigateRoleType", defReader);
		extract(header, data, "statCountType", defReader);

		String[] basePriceData = {"credits", "metal", "crystal" };
		extract(header, data, "basePrice", basePriceData, defReader);
		String slotCountValue = extract(header, data, "slotCount", defReader);
		double slotCount = Double.parseDouble(slotCountValue); 
		
		extract(header, data, "ExperiencePointsForDestroying", defReader);
		String maxHullPointsValue = extract(header, data, "MaxHullPoints", defReader);
		String maxShieldPointsValue = extract(header, data, "MaxShieldPoints", defReader);
		extract(header, data, "HullPointRestoreRate", defReader);
		extract(header, data, "ShieldPointRestoreRate", defReader);
		String baseArmorPointsValue = extract(header, data, "BaseArmorPoints", defReader);
		extract(header, data, "maxMitigation", defReader);
	
		extract(header, data, "armorType", defReader);
		
		String numWeapons = get("NumWeapons", defReader);
		int weapons = Integer.parseInt(numWeapons);
		String[] subWeaponData = {
				"WeaponType",
				"AttackType",
				"DamageAffectType",
				"DamageApplyType",
				"DamageType",
				"WeaponClassType",
				"DamagePerBank:FRONT",
				"DamagePerBank:BACK",
				"DamagePerBank:LEFT",
				"DamagePerBank:RIGHT",
				"Range",
				"PreBuffCooldownTime",
			};
			
		for (int i=0; i<weapons; i++) {
			get("Weapon", defReader);
			extract(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		for (int i=weapons; i<5; i++) {
			empty(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		String frontBankValue = extract(header, data, "TargetCountPerBank:FRONT", defReader);
		String backBankValue = extract(header, data, "TargetCountPerBank:BACK", defReader);
		String leftBankValue = extract(header, data, "TargetCountPerBank:LEFT", defReader);
		String rightBankValue = extract(header, data, "TargetCountPerBank:RIGHT", defReader);
		
		int frontBank = Integer.parseInt(frontBankValue);
		int backBank = Integer.parseInt(backBankValue);
		int leftBank = Integer.parseInt(leftBankValue);
		int rightBank = Integer.parseInt(rightBankValue);
		
		extract(header, data, "maxNumCommandPoints", defReader);
		
		extract(header, data, "ability:0", defReader);
		extract(header, data, "ability:1", defReader);
		extract(header, data, "ability:2", defReader);
		extract(header, data, "ability:3", defReader);
		extract(header, data, "MaxAntiMatter", defReader);
		extract(header, data, "AntiMatterRestoreRate", defReader);

		double totalFront = 0;
		double totalBack = 0;
		double totalLeft = 0;
		double totalRight = 0;
		for (int i=0; i<5; i++) {
			String frontValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:FRONT");
			String backValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:BACK");
			String leftValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:LEFT");
			String rightValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:RIGHT");
			
			String cooldownValue = data.get("Weapon[" + i + "]" + "." + "PreBuffCooldownTime");
			
			double front = 0;
			double back = 0;
			double left = 0;
			double right = 0;
			double cooldown = 0;
			if (!frontValue.isEmpty()) {
				cooldown = Double.parseDouble(cooldownValue);
				
				front = Double.parseDouble(frontValue) / cooldown;
				back = Double.parseDouble(backValue) / cooldown;
				left = Double.parseDouble(leftValue) / cooldown;
				right = Double.parseDouble(rightValue) / cooldown;
				
				totalFront += front * frontBank;
				totalBack += back * backBank;
				totalLeft += left * leftBank;
				totalRight += right * rightBank;
			}
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT", front);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK", back);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT", left);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT", right);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL", front + back + left + right);
		}
	
		defaultValue(header, data, "DPS:FRONT", totalFront);
		defaultValue(header, data, "DPS:BACK", totalBack);
		defaultValue(header, data, "DPS:LEFT", totalLeft);
		defaultValue(header, data, "DPS:RIGHT", totalRight);
	
		double dpsAll = totalFront + totalBack + totalLeft + totalRight;
		defaultValue(header, data, "DPS:ALL", dpsAll);
		defaultValue(header, data, "DPS:SUPPLY", dpsAll / slotCount);
		
		double maxHullPoints = Double.parseDouble(maxHullPointsValue);
		double maxShieldPoints = Double.parseDouble(maxShieldPointsValue);
		defaultValue(header, data, "HULL:SUPPLY", maxHullPoints / slotCount);
		defaultValue(header, data, "SHIELDS:SUPPLY", maxShieldPoints / slotCount);
		
		double baseArmorPoints = Double.parseDouble(baseArmorPointsValue);
		defaultValue(header, data, "ARMOR:SUPPLY", baseArmorPoints / slotCount);
		
		defaultValue(header, data, "DURABILITY:SUPPLY", (maxHullPoints * (1+0.05*baseArmorPoints) + maxShieldPoints) / slotCount);
		
		if (summary == true) {
			header = getSummaryHeadersAsList();
		}
		if (printHeader) {
			printHeader(header, data, writer);
			printHeader = false;
		}
		
		printData(header, data, writer);
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
	
		}
	}

	private LinkedList<String> getSummaryHeadersAsList() {
		String[] headersArray = getSummaryHeaders().split(",");
		LinkedList<String> headersList = new LinkedList<String>(Arrays.asList(headersArray));
		return headersList;
	}

	private void extractCapitalShipData(PrintWriter writer, FileSet fs,
			String sourceFile) {
		File sourceDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		Map<String, String> data = new HashMap<String, String>();
		LinkedList<String> header = new LinkedList<String>();
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
	
		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = defReader.readLine();
		}

		header.add("entityName");
		data.put("entityName", sourceFile);
		System.out.println("entityName");
		System.out.println(sourceFile);
		extract(header, data, "ability:0", defReader);
		extract(header, data, "ability:1", defReader);
		extract(header, data, "ability:2", defReader);
		extract(header, data, "ability:3", defReader);
		
		String canBomb = get("canBomb", defReader);
		if ("TRUE".equals(canBomb)) {
			extract(header, data, "baseDamage", defReader);
			extract(header, data, "basePopulationKilled", defReader);
			extract(header, data, "bombingFreqTime", defReader);
			extract(header, data, "baseRange", defReader);
		} else {
			empty(header, data, "baseDamage");
			empty(header, data, "basePopulationKilled");
			empty(header, data, "bombingFreqTime");
			empty(header, data, "baseRange");
		}
		
		String[] basePriceData = {"credits", "metal", "crystal" };
		extract(header, data, "basePrice", basePriceData, defReader);
		String slotCountValue = extract(header, data, "slotCount", defReader);
		double slotCount = Double.parseDouble(slotCountValue); 
		
		String[] subData = {"StartValue", "ValueIncreasePerLevel"};
		extract(header, data, "MaxHullPoints", subData, defReader);
		String maxHullPointsValue = data.get("MaxHullPoints.StartValue");
		String increaseHullPointsValue = data.get("MaxHullPoints.ValueIncreasePerLevel");
		extract(header, data, "MaxShieldPoints", subData, defReader);
		String maxShieldPointsValue = data.get("MaxShieldPoints.StartValue");
		String increaseShieldPointsValue = data.get("MaxShieldPoints.ValueIncreasePerLevel");
		extract(header, data, "HullPointRestoreRate", subData, defReader);
		extract(header, data, "ShieldPointRestoreRate", subData, defReader);
		extract(header, data, "ArmorPointsFromExperience", subData, defReader);
		String baseArmorPointsValue = data.get("ArmorPointsFromExperience.StartValue");
		String increaseArmorPointsValue = data.get("ArmorPointsFromExperience.ValueIncreasePerLevel");
		extract(header, data, "maxMitigation", subData, defReader);
		extract(header, data, "MaxAntiMatter", subData, defReader);
		extract(header, data, "AntiMatterRestoreRate", subData, defReader);
		extract(header, data, "CultureProtectRate", subData, defReader);
	
		extractStringInfo(header, data, "NameStringID", defReader);
		extractStringInfo(header, data, "DescriptionStringID", defReader);

		extract(header, data, "statCountType", defReader);
		extract(header, data, "armorType", defReader);
		
		String numWeapons = get("NumWeapons", defReader);
		int weapons = Integer.parseInt(numWeapons);
		String[] subWeaponData = {
				"WeaponType",
				"AttackType",
				"DamageAffectType",
				"DamageApplyType",
				"DamageType",
				"WeaponClassType",
				"DamagePerBank:FRONT",
				"DamagePerBank:BACK",
				"DamagePerBank:LEFT",
				"DamagePerBank:RIGHT",
				"Range",
				"PreBuffCooldownTime",
			};
			
		for (int i=0; i<weapons; i++) {
			get("Weapon", defReader);
			extract(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		for (int i=weapons; i<5; i++) {
			empty(header, data, "Weapon[" + i + "]", subWeaponData, defReader);
		}
		
		String frontBankValue = extract(header, data, "TargetCountPerBank:FRONT", defReader);
		String backBankValue = extract(header, data, "TargetCountPerBank:BACK", defReader);
		String leftBankValue = extract(header, data, "TargetCountPerBank:LEFT", defReader);
		String rightBankValue = extract(header, data, "TargetCountPerBank:RIGHT", defReader);
		
		int frontBank = Integer.parseInt(frontBankValue);
		int backBank = Integer.parseInt(backBankValue);
		int leftBank = Integer.parseInt(leftBankValue);
		int rightBank = Integer.parseInt(rightBankValue);
		
		extract(header, data, "CommandPoints", subData, defReader);
		extract(header, data, "weaponCooldownDecreasePerc", subData, defReader);
		extract(header, data, "weaponDamageIncreasePerc", subData, defReader);
		
		double totalFront = 0;
		double totalBack = 0;
		double totalLeft = 0;
		double totalRight = 0;
		
		double totalFrontBanks = 0;
		double totalBackBanks = 0;
		double totalLeftBanks = 0;
		double totalRightBanks = 0;

		double totalFrontLevel10 = 0;
		double totalBackLevel10 = 0;
		double totalLeftLevel10 = 0;
		double totalRightLevel10 = 0;
		
		String weaponCooldownDecreaseValue = data.get("weaponCooldownDecreasePerc.ValueIncreasePerLevel");
		String weaponDamageIncreaseValue = data.get("weaponDamageIncreasePerc.ValueIncreasePerLevel");
		double weaponCooldownDecrease = Double.parseDouble(weaponCooldownDecreaseValue);
		double weaponDamageIncrease = Double.parseDouble(weaponDamageIncreaseValue);

		for (int i=0; i<5; i++) {
			String frontValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:FRONT");
			String backValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:BACK");
			String leftValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:LEFT");
			String rightValue = data.get("Weapon[" + i + "]" + "." + "DamagePerBank:RIGHT");
			
			String cooldownValue = data.get("Weapon[" + i + "]" + "." + "PreBuffCooldownTime");
			
			double front = 0;
			double back = 0;
			double left = 0;
			double right = 0;
			double frontLevel10 = 0;
			double backLevel10 = 0;
			double leftLevel10 = 0;
			double rightLevel10 = 0;
			double cooldown = 0;
			if (!frontValue.isEmpty()) {
				cooldown = Double.parseDouble(cooldownValue);
				
				front = Double.parseDouble(frontValue) / cooldown;
				back = Double.parseDouble(backValue) / cooldown;
				left = Double.parseDouble(leftValue) / cooldown;
				right = Double.parseDouble(rightValue) / cooldown;
				
				totalFront += front * frontBank;
				totalBack += back * backBank;
				totalLeft += left * leftBank;
				totalRight += right * rightBank;
				
				frontLevel10 = (Double.parseDouble(frontValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				backLevel10 = (Double.parseDouble(backValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				leftLevel10 = (Double.parseDouble(leftValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				rightLevel10 = (Double.parseDouble(rightValue) * (1+9*weaponDamageIncrease)) / (cooldown / (1+9*weaponCooldownDecrease));
				
				totalFrontLevel10 += frontLevel10 * frontBank;
				totalBackLevel10 += backLevel10 * backBank;
				totalLeftLevel10 += leftLevel10 * leftBank;
				totalRightLevel10 += rightLevel10 * rightBank;
			}
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT", front);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK", back);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT", left);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT", right);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL", front + back + left + right);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT(Total)", front * frontBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK(Total)", back * backBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT(Total)", left * leftBank);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT(Total)", right * rightBank);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL(Total)", front * frontBank + back * backBank + left * leftBank + right * rightBank);

			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:FRONT(10)", frontLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:BACK(10)", backLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:LEFT(10)", leftLevel10);
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:RIGHT(10)", rightLevel10);
			
			defaultValue(header, data, "Weapon[" + i + "]" + "." + "DPS:ALL(10)", frontLevel10 + backLevel10 + leftLevel10 + rightLevel10);

		}
	
		defaultValue(header, data, "DPS:FRONT", totalFront);
		defaultValue(header, data, "DPS:BACK", totalBack);
		defaultValue(header, data, "DPS:LEFT", totalLeft);
		defaultValue(header, data, "DPS:RIGHT", totalRight);
	
		defaultValue(header, data, "DPS:ALL", totalFront + totalBack + totalLeft + totalRight);
		
		defaultValue(header, data, "DPS:FRONT(Total)", totalFrontBanks);
		defaultValue(header, data, "DPS:BACK(Total)", totalBackBanks);
		defaultValue(header, data, "DPS:LEFT(Total)", totalLeftBanks);
		defaultValue(header, data, "DPS:RIGHT(Total)", totalRightBanks);

		defaultValue(header, data, "DPS:ALL(Total)", totalFrontBanks + totalBackBanks + totalLeftBanks + totalRightBanks);

		defaultValue(header, data, "DPS:FRONT(10)", totalFrontLevel10);
		defaultValue(header, data, "DPS:BACK(10)", totalBackLevel10);
		defaultValue(header, data, "DPS:LEFT(10)", totalLeftLevel10);
		defaultValue(header, data, "DPS:RIGHT(10)", totalRightLevel10);

		defaultValue(header, data, "DPS:ALL(10)", totalFrontLevel10 + totalBackLevel10 + totalLeftLevel10 + totalRightLevel10);

		double dpsAll = totalFront + totalBack + totalLeft + totalRight;
				
		double maxHullPoints = Double.parseDouble(maxHullPointsValue);
		double maxShieldPoints = Double.parseDouble(maxShieldPointsValue);
		double increaseHullPoints = Double.parseDouble(increaseHullPointsValue);
		double increaseShieldPoints = Double.parseDouble(increaseShieldPointsValue);
		defaultValue(header, data, "HULL:SUPPLY", maxHullPoints / slotCount);
		defaultValue(header, data, "SHIELDS:SUPPLY", maxShieldPoints / slotCount);
		
		double baseArmorPoints = Double.parseDouble(baseArmorPointsValue);
		double increaseArmorPoints = Double.parseDouble(increaseArmorPointsValue);
		defaultValue(header, data, "ARMOR:SUPPLY", baseArmorPoints / slotCount);
		
		defaultValue(header, data, "DURABILITY:SUPPLY", (maxHullPoints * (1+0.05*baseArmorPoints) + maxShieldPoints) / slotCount);
		defaultValue(header, data, "DURABILITY", (maxHullPoints * (1+0.05*baseArmorPoints) + maxShieldPoints));

		double maxHull10 = maxHullPoints + (9 * increaseHullPoints);
		double maxShield10 = maxShieldPoints + (9 * increaseShieldPoints);
		double maxArmor10 = baseArmorPoints + (9 * increaseArmorPoints);
		defaultValue(header, data, "HULL(10)", maxHull10);
		defaultValue(header, data, "SHIELDS(10)", maxShield10);
		defaultValue(header, data, "ARMOR(10)", maxArmor10);
		
		defaultValue(header, data, "DURABILITY(10)", (maxHull10 * (1+0.05*maxArmor10) + maxShield10));
		if (summary == true) {
			header = getSummaryHeadersAsList();
		}
		if (printHeader) {
			printHeader(header, data, writer);
			printHeader = false;
		}
		
		printData(header, data, writer);
		
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
	
		}
	}

	private LinkedList<String> getCapitalShipSummaryHeaders() {
		LinkedList<String> headers = new LinkedList<String>();
		
		headers.add("entityName");
		headers.add("ability:0");
		headers.add("ability:1");
		headers.add("ability:2");
		headers.add("ability:3");
		headers.add("basePrice.credits");
		headers.add("basePrice.metal");
		headers.add("basePrice.crystal");
		headers.add("slotCount");	
		headers.add("MaxHullPoints.StartValue");	
		headers.add("MaxHullPoints.ValueIncreasePerLevel");	
		headers.add("MaxShieldPoints.StartValue");	
		headers.add("MaxShieldPoints.ValueIncreasePerLevel");	
		headers.add("HullPointRestoreRate.StartValue");	
		headers.add("HullPointRestoreRate.ValueIncreasePerLevel");	
		headers.add("ShieldPointRestoreRate.StartValue");	
		headers.add("ShieldPointRestoreRate.ValueIncreasePerLevel");	
		headers.add("ArmorPointsFromExperience.StartValue");	
		headers.add("ArmorPointsFromExperience.ValueIncreasePerLevel");	
		headers.add("maxMitigation.StartValue");	
		headers.add("maxMitigation.ValueIncreasePerLevel");	
		headers.add("MaxAntiMatter.StartValue");	
		headers.add("MaxAntiMatter.ValueIncreasePerLevel");	
		headers.add("AntiMatterRestoreRate.StartValue");	
		headers.add("AntiMatterRestoreRate.ValueIncreasePerLevel");
		headers.add("statCountType");	
		headers.add("armorType");
		headers.add("DPS:FRONT");	
		headers.add("DPS:BACK");	
		headers.add("DPS:LEFT");	
		headers.add("DPS:RIGHT");	
		headers.add("DPS:ALL");

		return headers;
	}

	public String getStringInfoFile() {
		return stringInfoFile;
	}

	public void setStringInfoFile(String stringInfoFile) {
		this.stringInfoFile = stringInfoFile;
	}

	public boolean isSummary() {
		return summary;
	}

	public void setSummary(boolean summary) {
		this.summary = summary;
	}

	public String getSummaryHeaders() {
		return summaryHeaders;
	}

	public void setSummaryHeaders(String summaryHeaders) {
		this.summaryHeaders = summaryHeaders;
	}

}
