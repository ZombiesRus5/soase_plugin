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
public class SoundDataGenerator extends Task {
	List<ResourceCollection> resourseList = new ArrayList<ResourceCollection>();
	String soundDataFileName = null;
	String soundDataType = null;
	String soundDataFieldName = null;
	

	public String getSoundDataType() {
		return soundDataType;
	}

	public void setSoundDataType(String soundDataType) {
		this.soundDataType = soundDataType;
	}

	public String getSoundDataFileName() {
		return soundDataFileName;
	}

	public void setSoundDataFileName(String soundDataFileName) {
		this.soundDataFileName = soundDataFileName;
	}

	public void add(ResourceCollection res) {
		resourseList.add(res);
	}

	@Override
	public void execute() throws BuildException {
//		System.out.println("yay!!!!");
		Iterator<ResourceCollection> itr = resourseList.iterator();
		SortedSet<String> soundDataEntries = new TreeSet<String>();

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
					soundDataEntries.add(srcFiles[i]);
				}
			}
		}
			
		System.out.println("[+" + soundDataFileName + "] soundData entries: " + soundDataEntries.size());
			
		File soundDataFile = new File(soundDataFileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(soundDataFile);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("TXT");
			pw.print("numEffects ");
			pw.print(soundDataEntries.size());
			pw.println();
			
			Iterator entriesItr = soundDataEntries.iterator();
			while (entriesItr.hasNext()) {
				String entry = (String) entriesItr.next();
				pw.println("effect");
				pw.print("\tname \"");
				pw.print(entry.toUpperCase().substring(0, entry.indexOf(".")));
				pw.print("\"");
				pw.println();
				
				pw.print("\tfileName \"");
				pw.print(entry);
				pw.print("\"");
				pw.println();
				
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

	public String getSoundDataFieldName() {
		return soundDataFieldName;
	}

	public void setSoundDataFieldName(String soundDataFieldName) {
		this.soundDataFieldName = soundDataFieldName;
	}

}
