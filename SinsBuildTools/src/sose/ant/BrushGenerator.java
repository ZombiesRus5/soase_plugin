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
public class BrushGenerator extends Task {


	String brushFileTemplate = null;
	String brushFileName = null;
	String normalFileName = null;
	String pressedFileName = null;
	String disabledFileName = null;
	String cursorOverFileName = null;
	String onlyLoadOnDemand = "FALSE";
	String content = null;
	String fileName = null;
	
	String prefix = null;
	

	public String getBrushFileTemplate() {
		return brushFileTemplate;
	}

	public void setBrushFileTemplate(String brushFileTemplate) {
		this.brushFileTemplate = brushFileTemplate;
	}

	public String getBrushFileName() {
		return brushFileName;
	}

	public void setBrushFileName(String brushFileName) {
		this.brushFileName = brushFileName;
	}

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

		File templateFile = new File(getBrushFileTemplate());
		StringWriter buffer = new StringWriter();
		
		LineNumberReader lnr = new LineNumberReader(new FileReader(templateFile));
		PrintWriter pw = new PrintWriter(buffer);
		
		int brushCount = 0;

		// BURN FIRST ROW
		lnr.readLine();
		
		while (lnr.ready()) {
			String lineContents = lnr.readLine();
			
			StringTokenizer brushRow = new StringTokenizer(lineContents, ",", false);
			
			if (brushRow.countTokens() != 7) {
//				System.out.println("Invalid number of tokens: " + lineContents);
				continue;
			}
			String row = brushRow.nextToken();
			String column = brushRow.nextToken();
			String iconName = brushRow.nextToken().toUpperCase();
			String startX = brushRow.nextToken();
			String startY = brushRow.nextToken();
			String width = brushRow.nextToken();
			String height = brushRow.nextToken();
						
//			System.out.println(iconName);
			
			pw.println("brush");
			pw.println("	name \"" + getPrefix() + iconName + "\"");
			if ("Simple".equals(content)) {
				String pixelBox = "	pixelBox [ " + startX + " , " + startY + " , " + height + " , " + width + " ]";
				pw.println("	content \"Simple\"");
				pw.println("	fileName \"" + fileName + "\"");
				pw.println(pixelBox);
			} else {
				String pixelBox = "		pixelBox [ " + startX + " , " + startY + " , " + height + " , " + width + " ]";
				pw.println("	content \"States\"");
				pw.println("	Disabled");
				pw.println("		fileName \"" + disabledFileName + "\"");
				pw.println(pixelBox);
				pw.println("	Pressed");
				pw.println("		fileName \"" + pressedFileName + "\"");
				pw.println(pixelBox);
				pw.println("	CursorOver");
				pw.println("		fileName \"" + cursorOverFileName + "\"");
				pw.println(pixelBox);
				pw.println("	Focused");
				pw.println("		fileName \"" + normalFileName + "\"");
				pw.println(pixelBox);
				pw.println("	Normal");
				pw.println("		fileName \"" + normalFileName + "\"");
				pw.println(pixelBox);
			}
			brushCount ++;
		}
		
//		System.out.println(buffer.toString());
			
			File brushFile = new File(brushFileName);
			FileWriter brushFileWriter = new FileWriter(brushFile);
			PrintWriter brushFilePrintWriter = new PrintWriter(brushFileWriter);
			
			brushFilePrintWriter.println("TXT");
			brushFilePrintWriter.println("onlyLoadOnDemand " + onlyLoadOnDemand);
			brushFilePrintWriter.println("brushCount " + brushCount);
			brushFilePrintWriter.println(buffer.toString());
			brushFilePrintWriter.println();
			
			brushFilePrintWriter.flush();
			brushFileWriter.flush();
			brushFileWriter.close();
		} catch(IOException e) {
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

	public String getNormalFileName() {
		return normalFileName;
	}

	public void setNormalFileName(String normalFileName) {
		this.normalFileName = normalFileName;
	}

	public String getPressedFileName() {
		return pressedFileName;
	}

	public void setPressedFileName(String pressedFileName) {
		this.pressedFileName = pressedFileName;
	}

	public String getDisabledFileName() {
		return disabledFileName;
	}

	public void setDisabledFileName(String disabledFileName) {
		this.disabledFileName = disabledFileName;
	}

	public String getCursorOverFileName() {
		return cursorOverFileName;
	}

	public void setCursorOverFileName(String cursorOverFileName) {
		this.cursorOverFileName = cursorOverFileName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOnlyLoadOnDemand() {
		return onlyLoadOnDemand;
	}

	public void setOnlyLoadOnDemand(String onlyLoadOnDemand) {
		this.onlyLoadOnDemand = onlyLoadOnDemand;
	}

}
