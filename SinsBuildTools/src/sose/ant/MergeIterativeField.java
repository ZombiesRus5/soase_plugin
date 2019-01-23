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
import org.apache.tools.ant.util.StringUtils;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class MergeIterativeField extends Task {

	String outputDef = null;
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	String sourceDef = null;
	String iterativeCountFieldName = "";
	String endFieldName = "";

	String dir = null;
	String includes = null;
	
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
			if (sourceDef != null) {
				File sourceFile = new File(getSourceDef());
				File outputFile = null;
				
				if (outputDef == null || outputDef.length() == 0) {
					System.out.println("outputing to: " + sourceFile);
					outputFile = sourceFile;
				} else {
					System.out.println("outputing to: " + getOutputDef());
					outputFile = new File(getOutputDef());
				}
				mergeIterativeField(sourceFile, outputFile);
			} else {
				FileSet fs = new FileSet();
				fs.setDir(new File(dir));
				fs.setIncludes(includes);
				
				DirectoryScanner ds = null;
				
				try {
					ds = fs.getDirectoryScanner(getProject());
				} catch (BuildException e) {
					if (!getMessage(e).endsWith(" not found.")) {
						throw e;
					} else {
						log("Warning: " + getMessage(e), Project.MSG_ERR);
						return;
					}
				}

				String[] srcFiles = ds.getIncludedFiles();
				System.out.println(srcFiles.length);
				for (int i=0; i<srcFiles.length; i++) {
					System.out.println(srcFiles[i]);
					File sourceFile = new File(fs.getDir().getAbsolutePath() + "/" + srcFiles[i]);
					mergeIterativeField(sourceFile, sourceFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}

	private void mergeIterativeField(File sourceDef, File outputDef)
			throws FileNotFoundException, IOException, Exception {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
		StringWriter buffer = new StringWriter();
		PrintWriter defWriter = new PrintWriter(buffer);

		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			defWriter.println(line);
			line = defReader.readLine();
		}
		if (iterativeCountFieldName == null || iterativeCountFieldName.length() <= 0) {
			throw new BuildException("iterativeCountFieldName required!");
		}
		defWriter.println(line);
		
		line = defReader.readLine();
		line = mergeIterativeField(iterativeCountFieldName, endFieldName, defReader, defWriter, line);
		if (line != null) {
			defWriter.println(line);
		}
		while (defReader.ready()) {
			line = defReader.readLine();
			defWriter.println(line);
		}
		
		defWriter.flush();
		defWriter.flush();
		defWriter.close();
		PrintWriter fileWriter = new PrintWriter(new FileWriter(outputDef));
		fileWriter.print(buffer.toString());
		fileWriter.flush();
		fileWriter.close();
	}
	
	private String mergeIterativeField(String fieldName, String endFieldName,
			LineNumberReader defReader, PrintWriter defWriter, String line) throws Exception {
		int dataCount;
		String lastLine = null;
		StringWriter buffer = new StringWriter();
		PrintWriter pw = new PrintWriter(buffer);
		
		while (!line.contains(fieldName)) {
			if (defReader.ready()) {
				defWriter.println(line);
				line = defReader.readLine();
			} else {
				throw new BuildException("Invalid line found! expected " + fieldName);
			}
		}
		String count = line.trim().split(" ")[1];
		dataCount = Integer.parseInt(count);
		System.out.println("base data count: " + dataCount);
		while (defReader.ready()) {
			String lineContents = defReader.readLine();
			if (lineContents != null && !lineContents.trim().isEmpty() &&
					!lineContents.contains(endFieldName)) {
				pw.println(lineContents);
			} else {
				lastLine = lineContents;
				break;
			}
		}

		Iterator<ResourceCollection> itr = resourceList.iterator();
		
		System.out.println("Starting");
		while (itr.hasNext()) {
			ResourceCollection rc = itr.next();
			System.out.println(rc.getClass());
			if (rc instanceof FileSet && rc.isFilesystemOnly()) {
				System.out.println(rc.getClass());
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
				System.out.println(srcFiles.length);
				for (int i=0; i<srcFiles.length; i++) {
					if (srcFiles[i].endsWith(fieldName)) {
						System.out.println(srcFiles[i]);
						InputStream is = readContents(fs, srcFiles[i]);
						dataCount += readDataFile(fieldName, is, pw);
					}
				}
			}
		}

		// if fieldName is planetItemsTemplate then process again for groups to add
		// based on template names
//		if ("planetItemsTemplate".equals(fieldName)) {
//			buffer = mergePlanetItemsTemplate(buffer.toString(), defReader, defWriter);
//		}
		
		// System.out.println(buffer.toString());
		defWriter.println(fieldName + " " + dataCount);
		defWriter.print(buffer.toString());
		System.out.println(fieldName + " " + dataCount);
		System.out.println(buffer.toString());
		
		return lastLine;
	}

	private InputStream readContents(FileSet fs, String entityFile) throws BuildException {
		File file = new File(fs.getDir().getAbsolutePath() + "/" + entityFile);
		InputStream is;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		}
		return is;
	}
	
	private int readDataFile(String fieldName, InputStream is, PrintWriter buffer) throws Exception {
		int dataCount = 0;
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		
		String line = lnr.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = lnr.readLine();
		}
		line = lnr.readLine();
		if (!line.contains(fieldName)) {
			throw new BuildException("Expected " + fieldName + "!");
		}
		String count = line.trim().split(" ")[1];
		dataCount = Integer.parseInt(count);
		System.out.println("base data count: " + dataCount);
		while (lnr.ready()) {
			String lineContents = lnr.readLine();
			if (lineContents != null && !lineContents.trim().isEmpty()) {
				buffer.println(lineContents);
			}
		}
		return dataCount;
	}

	public String getOutputDef() {
		return outputDef;
	}


	public void setOutputDef(String ouputDef) {
		this.outputDef = ouputDef;
	}


	public String getSourceDef() {
		return sourceDef;
	}


	public void setSourceDef(String sourceDef) {
		this.sourceDef = sourceDef;
	}

	public String getIterativeCountFieldName() {
		return iterativeCountFieldName;
	}

	public void setIterativeCountFieldName(String iterativeCountFieldName) {
		this.iterativeCountFieldName = iterativeCountFieldName;
	}

	public String getEndFieldName() {
		return endFieldName;
	}

	public void setEndFieldName(String endFieldName) {
		this.endFieldName = endFieldName;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getIncludes() {
		return includes;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

}
