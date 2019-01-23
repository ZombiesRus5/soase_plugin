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
public class BrushCSVGenerator extends Task {

	String iconNameFile = null;
	String brushFileTemplate = null;
	int rows = 0;
	int columns = 0;
	int startx = 0;
	int starty = 0;
	int width = 0;
	int height = 0;
	int offsetx = 0;
	int offsety = 0;
	
	public String getBrushFileTemplate() {
		return brushFileTemplate;
	}

	public void setBrushFileTemplate(String brushFileTemplate) {
		this.brushFileTemplate = brushFileTemplate;
	}
	
	@Override
	public void execute() throws BuildException {
//		System.out.println("yay!!!!");
		
		try {

			File templateFile = new File(getIconNameFile());
			StringWriter buffer = new StringWriter();
			
			LineNumberReader lnr = new LineNumberReader(new FileReader(templateFile));
			PrintWriter pw = new PrintWriter(buffer);
			
			int brushCount = 0;
	
			// BURN FIRST ROW
			lnr.readLine();
			
			pw.println("ROW,COLUMN,ICON NAME,STARTX,STARTY,HEIGHT,WIDTH");
			int rowCount = 1;
			int columnCount = 1;
			while (lnr.ready()) {
				String lineContents = lnr.readLine();
				
				StringTokenizer iconNames = new StringTokenizer(lineContents, ",", false);
				
				while (iconNames.hasMoreElements()) {
					String iconName = iconNames.nextToken();
					
					pw.print(rowCount);
					pw.print(",");
					pw.print(columnCount);
					pw.print(",");
					pw.print(iconName);
					pw.print(",");
					pw.print(startx + (offsetx * (columnCount - 1)));
					pw.print(",");
					pw.print(starty + (offsety * (rowCount - 1)));
					pw.print(",");
					pw.print(height);
					pw.print(",");
					pw.print(width);
					pw.println();
					
					columnCount++;
				}
				rowCount ++;
				columnCount = 1;
			}
		
//		System.out.println(buffer.toString());
			
			File brushFile = new File(brushFileTemplate);
			FileWriter brushFileWriter = new FileWriter(brushFile);
			PrintWriter brushFilePrintWriter = new PrintWriter(brushFileWriter);
			
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

	public String getIconNameFile() {
		return iconNameFile;
	}

	public void setIconNameFile(String iconNameFile) {
		this.iconNameFile = iconNameFile;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public int getStartx() {
		return startx;
	}

	public void setStartx(int startx) {
		this.startx = startx;
	}

	public int getStarty() {
		return starty;
	}

	public void setStarty(int starty) {
		this.starty = starty;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getOffsetx() {
		return offsetx;
	}

	public void setOffsetx(int offsetx) {
		this.offsetx = offsetx;
	}

	public int getOffsety() {
		return offsety;
	}

	public void setOffsety(int offsety) {
		this.offsety = offsety;
	}


}
