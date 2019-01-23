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
public class EmptyPlanetMeshInfo extends Task {

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
		
		if ("meshInfoCount".equalsIgnoreCase(keyword)) {
			defWriter.println("meshInfoCount 0");
			while (defReader.ready()) {
				line = defReader.readLine();
				keyword = parseKeyWord(line);
				if ("minZoomDistanceMult".equalsIgnoreCase(keyword)) {
					defWriter.println(line);
					break;
				}
			}

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
