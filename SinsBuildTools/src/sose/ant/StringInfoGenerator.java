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
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class StringInfoGenerator extends Task {

	String stringInfoTemplate = null;
	String stringInfoFileName = null;
	String baseStringInfoFileName = null;

	String prefix = "IDS";
	
	boolean appendNameWhenNoDescription = true;

	int ENTITY_INDEX = 0;
	int RACE_INDEX = 1;
	int ID_PREFIX_INDEX = 2;
	int ID_INDEX = 3;
	int NAME_INDEX = 4;
	int DESCRIPTION_INDEX = 5;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void execute() throws BuildException {
		try {

			File templateFile = new File(getStringInfoTemplate());
			File baseStringFile = new File(getBaseStringInfoFileName());
			
			StringWriter buffer = new StringWriter();
			PrintWriter pw = new PrintWriter(buffer);

			int stringInfoCount = readBaseStringFile(baseStringFile, pw);
			
			LineNumberReader lnr = new LineNumberReader(new FileReader(
					templateFile));

			// BURN FIRST ROW
			lnr.readLine();

			while (lnr.ready()) {
				String lineContents = lnr.readLine();

				StringTokenizer stringInfoRow = new StringTokenizer(
						lineContents, "|", true);

				String[] stringInfo = new String[DESCRIPTION_INDEX + 1];
				
				int index = 0;
				while (stringInfoRow.hasMoreTokens()) {
					String token = stringInfoRow.nextToken();
					
					if (token.equals("|")) {
						index++;
					} else {
						stringInfo[index] = token;
					}
				}

				String qualifiedID = "";
				if (!getPrefix().isEmpty()) {
					qualifiedID = getPrefix() + "_";
				}
				if (stringInfo[ENTITY_INDEX] != null) {
					qualifiedID += stringInfo[ENTITY_INDEX] + "_";
				}
				if (stringInfo[RACE_INDEX] != null) {
					qualifiedID += stringInfo[RACE_INDEX] + "_";
				}
				if (stringInfo[ID_PREFIX_INDEX] != null) {
					qualifiedID += stringInfo[ID_PREFIX_INDEX] + "_";
				}
				if (stringInfo[ID_INDEX] != null) {
					qualifiedID += stringInfo[ID_INDEX] + "_";
				}

				System.out.println(qualifiedID);
								
				qualifiedID = qualifiedID.toUpperCase();
				pw.println("StringInfo");
				if (stringInfo[DESCRIPTION_INDEX] != null && !stringInfo[DESCRIPTION_INDEX].isEmpty()) {
					String nameID = qualifiedID + "NAME";
					String descriptionID = qualifiedID + "DESCRIPTION";
					pw.println("	ID \"" + nameID + "\"");
					pw.println("	Value \"" + stringInfo[NAME_INDEX] + "\"");
					pw.println("StringInfo");
					pw.println("	ID \"" + descriptionID + "\"");
					pw.println("	Value \"" + stringInfo[DESCRIPTION_INDEX] + "\"");
					stringInfoCount += 2;
				} else {
					String nameID = qualifiedID;
					if (appendNameWhenNoDescription == false) {
						nameID = nameID.substring(0, nameID.lastIndexOf("_"));
					} else {
						nameID = nameID + "NAME";
					}
					pw.println("	ID \"" + nameID + "\"");
					pw.println("	Value \"" + stringInfo[NAME_INDEX] + "\"");
					stringInfoCount += 1;
				}
			}

			// System.out.println(buffer.toString());

			File stringInfoFile = new File(stringInfoFileName);
			FileWriter stringInfoFileWriter = new FileWriter(stringInfoFile);
			PrintWriter stringInfoFilePrintWriter = new PrintWriter(
					stringInfoFileWriter);

			stringInfoFilePrintWriter.println("TXT");
			stringInfoFilePrintWriter.println("NumStrings " + stringInfoCount);
			stringInfoFilePrintWriter.println(buffer.toString());
			stringInfoFilePrintWriter.println();

			stringInfoFilePrintWriter.flush();
			stringInfoFileWriter.flush();
			stringInfoFileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}

	private int readBaseStringFile(File baseStringFile, PrintWriter buffer) throws Exception {
		int stringInfoCount = 0;
		LineNumberReader lnr = new LineNumberReader(new FileReader(baseStringFile));
		
		String line = lnr.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = lnr.readLine();
		}
		line = lnr.readLine();
		if (!line.contains("NumStrings")) {
			throw new BuildException("Expected NumStrings!");
		}
		String count = line.split(" ")[1];
		stringInfoCount = Integer.parseInt(count);
		System.out.println("base string info count: " + stringInfoCount);
		while (lnr.ready()) {
			buffer.println(lnr.readLine());
		}
		return stringInfoCount;
	}

	public String getStringInfoTemplate() {
		return stringInfoTemplate;
	}

	public void setStringInfoTemplate(String stringInfoTemplate) {
		this.stringInfoTemplate = stringInfoTemplate;
	}

	public String getStringInfoFileName() {
		return stringInfoFileName;
	}

	public void setStringInfoFileName(String stringInfoFileName) {
		this.stringInfoFileName = stringInfoFileName;
	}

	public String getBaseStringInfoFileName() {
		return baseStringInfoFileName;
	}

	public void setBaseStringInfoFileName(String baseStringInfoFileName) {
		this.baseStringInfoFileName = baseStringInfoFileName;
	}

	public boolean isAppendNameWhenNoDescription() {
		return appendNameWhenNoDescription;
	}

	public void setAppendNameWhenNoDescription(boolean appendNameWhenNoDescription) {
		this.appendNameWhenNoDescription = appendNameWhenNoDescription;
	}

}
