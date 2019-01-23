package sose.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.ResourceCollection;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
public class PlanetBonusGenerator extends Task {


	String planetBonusFileTemplate = null;
	String outputDir = null;
	String stringOutputFile = null;
	
	String prefix = null;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public void execute() throws BuildException {
//		System.out.println("yay!!!!");
		
		try {

		File templateFile = new File(getPlanetBonusFileTemplate());
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(templateFile));
		
		// BURN FIRST ROW
		lnr.readLine();

		File stringFile = new File(stringOutputFile);
		FileWriter stringFileWriter = new FileWriter(stringFile);
		PrintWriter stringFilePrintWriter = new PrintWriter(stringFileWriter);
		stringFilePrintWriter.println("|||||||");
		
		while (lnr.ready()) {
			String lineContents = lnr.readLine();
			System.out.println(lineContents);
			StringTokenizer brushRow = new StringTokenizer(lineContents, "\t", false);
			
			System.out.println(brushRow.countTokens());
			if (brushRow.countTokens() != 22) {
				continue;
			}
			
			String planetBonus = brushRow.nextToken();
			String minUpgradeLevelNeeded = brushRow.nextToken();
			String maxUpgradeLevelNeeded = brushRow.nextToken();
			String planetBonusName = brushRow.nextToken();
			String planetbonusDescription = brushRow.nextToken();
			String cultureSpread = brushRow.nextToken();
			String population = brushRow.nextToken();
			String planetHealth = brushRow.nextToken();
			String slotsCivilian = brushRow.nextToken();
			String slotsTactical = brushRow.nextToken();
			String taxIncome = brushRow.nextToken();
			String tradeIncomePerc = brushRow.nextToken();
			String metalIncomePerc = brushRow.nextToken();
			String crystalIncomePerc = brushRow.nextToken();
			String gravityWellRadius = brushRow.nextToken();
			String moduleBuildCost = brushRow.nextToken();
			String moduleBuildRate = brushRow.nextToken();
			String planetUpgradeBuildCost = brushRow.nextToken();
			String planetUpgradeBuildRate = brushRow.nextToken();
			String populationGrowthPerc = brushRow.nextToken();
			String moduleConstructors = brushRow.nextToken();
			
//			System.out.println(iconName);
			String fileName = outputDir + "/PlanetBonus";
			if (prefix != null) {
				fileName += prefix;
			}
			fileName += planetBonus + ".entity";
			
			File planetBonusFile = new File(fileName);
			FileWriter planetBonusFileWriter = new FileWriter(planetBonusFile);
			PrintWriter planetBonusPrintWriter = new PrintWriter(planetBonusFileWriter);
			
			planetBonusPrintWriter.println("TXT");
			planetBonusPrintWriter.println("entityType \"PlanetBonus\"");
			planetBonusPrintWriter.println("minUpgradeLevelNeeded " + minUpgradeLevelNeeded);
			planetBonusPrintWriter.println("maxUpgradeLevelNeeded " + maxUpgradeLevelNeeded);
			String idsName = "IDS_PLANETBONUS_";
			if (prefix != null) {
				idsName += prefix + "_";
			}
			idsName += planetBonus;
			idsName = idsName.toUpperCase();
			planetBonusPrintWriter.println("nameStringID \"" + idsName + "_NAME\"");
			planetBonusPrintWriter.println("descStringID \"" + idsName + "_DESCRIPTION\"");
			
			planetBonusPrintWriter.println("floatBonus:AdditiveCultureSpreadPerc " + cultureSpread); //0.000000
			planetBonusPrintWriter.println("floatBonus:AdditivePopulation " + population); // -15.000000
			planetBonusPrintWriter.println("floatBonus:AdditivePlanetHealth " + planetHealth); // 0.000000
			planetBonusPrintWriter.println("floatBonus:AdditiveSlotsCivilian " + slotsCivilian); // 0.000000
			planetBonusPrintWriter.println("floatBonus:AdditiveSlotsTactical " + slotsTactical); // 0.000000
			planetBonusPrintWriter.println("floatBonus:AdditiveTaxIncome " + taxIncome); // 0.000000
			planetBonusPrintWriter.println("floatBonus:AdditiveTradeIncomePerc " + tradeIncomePerc); // 0.000000
			planetBonusPrintWriter.println("floatBonus:MetalIncomePer " + metalIncomePerc); //c 0.000000
			planetBonusPrintWriter.println("floatBonus:CrystalIncomePerc " + crystalIncomePerc); // 0.000000
			planetBonusPrintWriter.println("floatBonus:GravityWellRadiusPerc " + gravityWellRadius); // 0.000000
			planetBonusPrintWriter.println("floatBonus:ModuleBuildCostPerc " + moduleBuildCost); // 0.000000
			planetBonusPrintWriter.println("floatBonus:ModuleBuildRatePerc " + moduleBuildRate); // 0.000000
			planetBonusPrintWriter.println("floatBonus:PlanetUpgradeBuildCostPerc " + planetUpgradeBuildCost); // 0.200000
			planetBonusPrintWriter.println("floatBonus:PlanetUpgradeBuildRatePerc " + planetUpgradeBuildRate); // 0.000000
			planetBonusPrintWriter.println("floatBonus:PopulationGrowthPerc " + populationGrowthPerc); // 0.000000
			planetBonusPrintWriter.println("intBonus:ModuleConstructors " + moduleConstructors); // 0
			planetBonusPrintWriter.println("intBonus:SpacePonies 0");
			String hudIconName = "HUDICON_PLANETBONUS_";
			if (prefix != null) {
				hudIconName += prefix + "_";
			}
			hudIconName += planetBonus;
			hudIconName = hudIconName.toUpperCase();
			planetBonusPrintWriter.println("hudIcon \"" + hudIconName + "\"");
			planetBonusPrintWriter.println("smallHudIcon \"" + hudIconName + "\"");
			planetBonusPrintWriter.println("infoCardIcon \"" + hudIconName + "\"");

			planetBonusPrintWriter.println();
			
			planetBonusPrintWriter.flush();
			planetBonusFileWriter.flush();
			planetBonusFileWriter.close();
	
			stringFilePrintWriter.println("PlanetBonus|"+prefix+"||"+planetBonus+"|"+planetBonusName+"|"+planetbonusDescription);
		}
		
		stringFilePrintWriter.flush();
		stringFileWriter.flush();
		stringFileWriter.close();
		
		
//		System.out.println(buffer.toString());
			
	} catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
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

	public String getPlanetBonusFileTemplate() {
		return planetBonusFileTemplate;
	}

	public void setPlanetBonusFileTemplate(String planetBonusFileTemplate) {
		this.planetBonusFileTemplate = planetBonusFileTemplate;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getStringOutputFile() {
		return stringOutputFile;
	}

	public void setStringOutputFile(String stringOutputFile) {
		this.stringOutputFile = stringOutputFile;
	}

}
