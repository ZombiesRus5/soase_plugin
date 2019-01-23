package sose.ant;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
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
public class MergeBrushes extends Task {

	String dataFileName = null;
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	String dataCountName = null;
	boolean failOnError = true;
	String encoding = "Cp1252";
	String fromEncoding = "Cp1252";
	

	public void add(ResourceCollection res) {
		resourceList.add(res);
	}
	
	public String getDataCountName() {
		return dataCountName;
	}

	public void setDataCountName(String dataCountName) {
		this.dataCountName = dataCountName;
	}

	public String getDataFieldName() {
		return dataFieldName;
	}

	public void setDataFieldName(String dataFieldName) {
		this.dataFieldName = dataFieldName;
	}

	String dataFieldName = null;

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

			File dataFile = new File(getDataFileName());
			int dataCount = 0;
			
			StringWriter buffer = new StringWriter();
			PrintWriter pw = new PrintWriter(buffer);

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
						System.out.println(srcFiles[i]);
						InputStreamReader is = readContents(fs, srcFiles[i]);
						dataCount += readDataFile(is, pw);
					}
				}
			}

			// System.out.println(buffer.toString());
			BufferedWriter dataFileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dataFile), Charset.forName( encoding)));
			PrintWriter dataFilePrintWriter = new PrintWriter(
					dataFileWriter);

			dataFilePrintWriter.println("TXT");
			dataFilePrintWriter.println("onlyLoadOnDemand FALSE");
			dataFilePrintWriter.println(dataCountName + " " + dataCount);
			dataFilePrintWriter.print(buffer.toString());

			dataFilePrintWriter.flush();
			dataFilePrintWriter.flush();
			dataFilePrintWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (failOnError) {
				throw new BuildException(e);
			}
		}

		super.execute();
	}
	
	private InputStreamReader readContents(FileSet fs, String entityFile) throws BuildException {
		File file = new File(fs.getDir().getAbsolutePath() + "/" + entityFile);
		InputStreamReader is;
		try {
			is = new InputStreamReader(new FileInputStream(file), Charset.forName(fromEncoding));
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		}
		return is;
	}
	
	private int readDataFile(InputStreamReader is, PrintWriter buffer) throws Exception {
		int dataCount = 0;
		LineNumberReader lnr = new LineNumberReader(is);
		
		String line = lnr.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			line = lnr.readLine();
		}

		line = lnr.readLine(); //read onlyLoadOnDemand
		line = lnr.readLine();
		if (!line.contains(dataCountName)) {
			throw new BuildException("Expected " + dataCountName + "!");
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

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFromEncoding() {
		return fromEncoding;
	}

	public void setFromEncoding(String fromEncoding) {
		this.fromEncoding = fromEncoding;
	}

}
