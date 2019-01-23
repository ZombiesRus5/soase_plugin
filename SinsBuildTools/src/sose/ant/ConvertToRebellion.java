package sose.ant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class ConvertToRebellion extends Task {

	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();	

	String entityType = null;
	
	Map<String, String> cache = new HashMap<String, String>();
	
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
						cache.clear();
						convert(fs, srcFiles[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}
		
	private void convert(FileSet fs, String sourceFile) throws Exception {
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

		while (defReader.ready()) {
			line = defReader.readLine();
			
			processLine(line, defReader, defWriter);
		}
		if (entityType.equalsIgnoreCase("StarBaseUpgrade")) {
			defWriter.println("xpModifier 25.00000");
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

	private void processLine(String line, LineNumberReader defReader,
			PrintWriter defWriter) throws Exception {
		
		String keyword = parseKeyWord(line);
		System.out.println(keyword);
		
		if ("smallEffectName".equalsIgnoreCase(keyword)) {
			defWriter.println(line);
			String mediumEffectName = line.replaceFirst("smallEffectName", "mediumEffectName");
			defWriter.println(mediumEffectName);
		} else if ("Level:1".equalsIgnoreCase(keyword)) {
			double value1 = parseDouble(line);
			String level2 = defReader.readLine();
			String value2String = parseValue(level2);
			double value2 = parseDouble(level2);
			
			defWriter.println(line);
			defWriter.println(level2);
			
			double value3 = (value2-value1) + value2;
			String level3 = level2.replaceAll("Level:2", "Level:3");
			String value3String = new DecimalFormat("#0.000000").format(value3);
			level3 = level3.replaceAll(value2String, value3String);
			defWriter.println(level3);
		} else if ("MinimumArtifactLevel".equalsIgnoreCase(keyword)) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("nameStringID".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Ability")) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("roleType".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("CapitalShip")) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("numRandomDebrisLarge".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Frigate")) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("MaxHullPoints".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Module")) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("NumSoundsFor".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Squad")) {
			defWriter.println("\tRequiredFactionNameID \"\"");
			defWriter.println("\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("hudIcon".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("StarBaseUpgrade")) {
			defWriter.println("\t\tRequiredFactionNameID \"\"");
			defWriter.println("\t\tRequiredCompletedResearchSubjects 0");
			defWriter.println(line);
		} else if ("mass".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("StarBase")) {
			defWriter.println(line);
			defWriter.println("HyperspaceChargingSoundID \"\"");
			defWriter.println("HyperspaceTravelSoundID 0");
		} else if ("artifactPicture".equalsIgnoreCase(keyword)) {
			defWriter.println(line);
			defWriter.println("uniqueOverlayBrush \"\"");
		} else if ("burstDelay".equalsIgnoreCase(keyword)) {
			defWriter.println(line);
			String fireDelay = line.replaceAll("burstDelay", "fireDelay");
			fireDelay = fireDelay.substring(0, fireDelay.lastIndexOf(" "));
			fireDelay += " 0.000000";
			defWriter.println(fireDelay);
		} else if ("picture".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Planet")) {
			defWriter.println(line);
			defWriter.println("\tshouldPrefer FALSE");
		} else if ("planetRuinDef".equals(keyword)) {
			cache.put(keyword, line);
			defWriter.println(line);
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("Planet") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"\"");
			defWriter.println("	creditsOnStrippedToTheCore 0.000000");
			defWriter.println("	metalOnStrippedToTheCore 0.000000");
			defWriter.println("	crystalOnStrippedToTheCore 0.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetVolcanic") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_TerranHome\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 14000.000000");
			defWriter.println("	metalOnStrippedToTheCore 10000.000000");
			defWriter.println("	crystalOnStrippedToTheCore 750.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetIce") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_TerranHome\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 10000.000000");
			defWriter.println("	metalOnStrippedToTheCore 750.000000");
			defWriter.println("	crystalOnStrippedToTheCore 10000.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetTerran") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_TerranHome\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 20000.000000");
			defWriter.println("	metalOnStrippedToTheCore 10000.000000");
			defWriter.println("	crystalOnStrippedToTheCore 5000.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetDesert") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_TerranHome\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 12000.000000");
			defWriter.println("	metalOnStrippedToTheCore 1500.000000");
			defWriter.println("	crystalOnStrippedToTheCore 500.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetMoon") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_Asteroid\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 2000.000000");
			defWriter.println("	metalOnStrippedToTheCore 750.000000");
			defWriter.println("	crystalOnStrippedToTheCore 250.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetAsteroid") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"StripToTheCore_Planet_Asteroid\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"EFFECT_STRIPPEDTOTHECORE\"");
			defWriter.println("	creditsOnStrippedToTheCore 2000.000000");
			defWriter.println("	metalOnStrippedToTheCore 750.000000");
			defWriter.println("	crystalOnStrippedToTheCore 250.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetDeadAsteroid") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetDeadAsteroid\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"\"");
			defWriter.println("	creditsOnStrippedToTheCore 0.000000");
			defWriter.println("	metalOnStrippedToTheCore 0.000000");
			defWriter.println("	crystalOnStrippedToTheCore 0.000000");
			
			defWriter.println(line);	
		} else if ("ShieldMeshName".equalsIgnoreCase(keyword) && entityType.equalsIgnoreCase("PlanetGasGiant") && !cache.containsKey("planetRuinDef")) {
			defWriter.println("planetRuinDef");
			defWriter.println("	ruinPlanetType \"PlanetGasGiant\"");
			defWriter.println("	attachType \"Center\"");
			defWriter.println("	effectName \"\"");
			defWriter.println("	startTime 9.000000");
			defWriter.println("	soundID \"\"");
			defWriter.println("	creditsOnStrippedToTheCore 0.000000");
			defWriter.println("	metalOnStrippedToTheCore 0.000000");
			defWriter.println("	crystalOnStrippedToTheCore 0.000000");
			
			defWriter.println(line);	
		} else {
			defWriter.println(line);
		}
	}
	private double parseDouble(String line) {
		// TODO Auto-generated method stub
		String value = parseValue(line);
		BigDecimal decMaxValue = new BigDecimal(value);
		return decMaxValue.doubleValue();
	}

	private void printString(PrintWriter defWriter, String string, String value) {
		// TODO Auto-generated method stub
		
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

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

}
