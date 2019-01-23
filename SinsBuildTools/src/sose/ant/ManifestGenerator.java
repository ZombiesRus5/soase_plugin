package sose.ant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
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
public class ManifestGenerator extends Task {
	List<ResourceCollection> resourseList = new ArrayList<ResourceCollection>();
	String manifestFileName = null;
	String manifestType = null;
	String manifestFieldName = null;
	
	boolean includeExtension = true;
	

	public String getManifestType() {
		return manifestType;
	}

	public void setManifestType(String manifestType) {
		this.manifestType = manifestType;
	}

	public String getManifestFileName() {
		return manifestFileName;
	}

	public void setManifestFileName(String manifestFileName) {
		this.manifestFileName = manifestFileName;
	}

	public void add(ResourceCollection res) {
		resourseList.add(res);
	}

	@Override
	public void execute() throws BuildException {
//		System.out.println("yay!!!!");
		Iterator<ResourceCollection> itr = resourseList.iterator();
		SortedSet<String> manifestEntries = new TreeSet<String>();

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
					String fileName = srcFiles[i];
					if (fileName.contains("/")) {
						fileName = fileName.substring(fileName.lastIndexOf("/")+1);
					}
					if (fileName.contains("\\")) {
						fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
					}
					manifestEntries.add(fileName);
				}
			}
		}
			
		System.out.println("[+" + manifestFileName + "] manifest entries: " + manifestEntries.size());
			
		File manifestFile = new File(manifestFileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(manifestFile);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("TXT");
			pw.print(manifestType + "Count ");
			pw.print(manifestEntries.size());
			pw.println();
			
			Iterator entriesItr = manifestEntries.iterator();
			while (entriesItr.hasNext()) {
				String entry = (String) entriesItr.next();
				if (manifestFieldName == null) {
					pw.println(manifestType + " \"" + entry + "\"");
				} else {
					if (includeExtension == false) {
						entry = entry.substring(0, entry.indexOf("."));
					}
					pw.println(manifestFieldName + " \"" + entry + "\"");
				}
			}
			pw.println();
			
			pw.flush();
			fw.flush();
			fw.close();
		} catch(IOException e) {
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

	public String getManifestFieldName() {
		return manifestFieldName;
	}

	public void setManifestFieldName(String manifestFieldName) {
		this.manifestFieldName = manifestFieldName;
	}

	public boolean isIncludeExtension() {
		return includeExtension;
	}

	public void setIncludeExtension(boolean includeExtension) {
		this.includeExtension = includeExtension;
	}

}
