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

/**
 * Adds an orbitBodyType name to a specific planetType designName in GSD
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class AddOrbitBodyType extends Task {

	String outputDef = null;
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	String sourceDef = null;
	String designName = null;



	public String getDesignName() {
		return designName;
	}

	public void setDesignName(String designName) {
		this.designName = designName;
	}

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

			File sourceDef = new File(getSourceDef());
			File outputDef = new File(getOutputDef());
			
			LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));			
			PrintWriter defWriter = new PrintWriter(new FileWriter(outputDef));

			String line = defReader.readLine();
			if (!line.contains("TXT")) {
				throw new BuildException("Cannot read binary files!");
			}
			if (line.contains("TXT2")) {
				// read SinsArchiveVersion
				defWriter.println(line);
				line = defReader.readLine();
			}
			System.out.println("looking for: " + designName);
			defWriter.println(line);
			while (defReader.ready()) {
				line = defReader.readLine();
				defWriter.println(line);
				if (line.contains("designName \"" + designName + "\"")) {
					System.out.println("found: " + designName);
					break;
				}
			}
			if (defReader.ready()) {
			line = defReader.readLine();
			System.out.println(line);
			if (line.contains("designStringId")) {
				defWriter.println(line);
				line = defReader.readLine();
				System.out.println(line);
			}
			line = mergeIterativeField("orbitBodyTypeCount", "", defReader, defWriter, line);
			defWriter.println(line);
			while (defReader.ready()) {
				line = defReader.readLine();
				defWriter.println(line);
			}
			}
			defWriter.flush();
			defWriter.flush();
			defWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}
	
	private String mergeIterativeField(String fieldName, String endFieldName,
			LineNumberReader defReader, PrintWriter defWriter, String line) throws Exception {
		int dataCount;
		String lastLine = null;
		StringWriter buffer = new StringWriter();
		PrintWriter pw = new PrintWriter(buffer);
		
		if (!line.contains(fieldName)) {
			throw new BuildException("Invalid line found! expected " + fieldName);
		}
		String count = line.trim().split(" ")[1];
		dataCount = Integer.parseInt(count);
		System.out.println("base data count: " + dataCount);
		while (defReader.ready()) {
			String lineContents = defReader.readLine();
			System.out.println(lineContents);
			if (lineContents != null && !lineContents.trim().isEmpty() &&
					!lineContents.contains(endFieldName)) {
				System.out.println(lineContents);
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
//					if (srcFiles[i].endsWith(fieldName)) {
						System.out.println(srcFiles[i]);
						InputStream is = readContents(fs, srcFiles[i]);
						dataCount += readDataFile(fieldName, is, pw);
//					}
				}
			}
		}

		// if fieldName is planetItemsTemplate then process again for groups to add
		// based on template names
//		if ("planetItemsTemplate".equals(fieldName)) {
//			buffer = mergePlanetItemsTemplate(buffer.toString(), defReader, defWriter);
//		}
//		
		// System.out.println(buffer.toString());
		defWriter.println("\t" + fieldName + " " + dataCount);
		defWriter.print(buffer.toString());
		
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
		String count = line.split(" ")[1];
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

}
