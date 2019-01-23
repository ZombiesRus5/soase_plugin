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
import org.apache.tools.ant.util.FileUtils;

/**
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of
 *            this source, but changing it is not allowed.
 */
public class RenameRaceTemplate extends Task {
	boolean copyOnly = false;
	public boolean isCopyOnly() {
		return copyOnly;
	}

	public void setCopyOnly(boolean copyOnly) {
		this.copyOnly = copyOnly;
	}

	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	String fromRace = null; // Upper camal version

	public String getFromRace() {
		return fromRace;
	}

	public void setFromRace(String fromRace) {
		this.fromRace = fromRace;
		this.fromRaceUpperCase = fromRace.toUpperCase();
	}

	public String getToRace() {
		return toRace;
	}

	public void setToRace(String toRace) {
		this.toRace = toRace;
		this.toRaceUpperCase = toRace.toUpperCase();
	}

	String toRace = null; // Upper camal version
	String todir = null;
	
	String fromRaceUpperCase = null;
	String toRaceUpperCase = null;

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
			if (todir == null || todir.isEmpty()) {
				throw new BuildException("todir must be specified!");
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
					System.out.println(ds.getBasedir());
					System.out.println("# files found: " + srcFiles.length);
					for (int i=0; i<srcFiles.length; i++) {
						String inputFileName = srcFiles[i];
						if (inputFileName.contains(fromRace) ||
								inputFileName.contains(fromRaceUpperCase)) {
							String outputFileName = inputFileName.replaceAll(fromRace, toRace);
							outputFileName = outputFileName.replaceAll(fromRaceUpperCase, toRaceUpperCase);
							File outputFile = new File(todir + "/" + outputFileName);
							
							System.out.println(inputFileName + " -> " + outputFileName);
							if (copyOnly) {
								File f = new File(todir + "/" + outputFileName);
								f.delete();
								FileCopy.copy(fs.getDir().getAbsolutePath() + "/" + inputFileName, todir + "/" + outputFileName);
							} else {
								StringWriter buffer = new StringWriter();
								PrintWriter pw = new PrintWriter(buffer);
		
								InputStream is = readContents(fs, srcFiles[i]);
								readDataFile(is, pw);
								
								// System.out.println(buffer.toString());
								FileWriter outputFileWriter = new FileWriter(outputFile);
								PrintWriter outputFilePrintWriter = new PrintWriter(
										outputFileWriter);
								
								String contents = buffer.toString().replaceAll(fromRace, toRace);
								contents = contents.replaceAll(fromRaceUpperCase, toRaceUpperCase);
	
								outputFilePrintWriter.print(contents);
	
								outputFilePrintWriter.flush();
								outputFilePrintWriter.flush();
								outputFilePrintWriter.close();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
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
	
	private int readDataFile(InputStream is, PrintWriter buffer) throws Exception {
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
		buffer.println(line);
		while (lnr.ready()) {
			String lineContents = lnr.readLine();
			buffer.println(lineContents);
		}
		return dataCount;
	}

	public String getTodir() {
		return todir;
	}

	public void setTodir(String todir) {
		this.todir = todir;
	}

}
