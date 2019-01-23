package sose.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
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
public class ConvertTGAtoDDS extends Task {

	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();	

	String boxFilter = null;
	
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
		File outputDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile.replaceAll(".[tT][gG][aA]", ".dds"));
		
		try {
//			System.out.println(sourceDef.lastModified());
//			System.out.println(outputDef.lastModified());
			if (outputDef.lastModified() > sourceDef.lastModified()) {
//				System.out.println("no update: " + sourceDef);
				return;
			} else {
				System.out.println("generating dds: " + sourceDef);
			}
			List<String> cmd = new ArrayList<String>();
			cmd.add("TheCompressonator.exe");
			cmd.add("-convert");
			cmd.add("-overwrite");
			cmd.add("-datecheck");
			cmd.add(sourceDef.getAbsoluteFile().toString());
			cmd.add(outputDef.getAbsoluteFile().toString());
			cmd.add("+fourCC");
			cmd.add("DXT5");
			
			if ("true".equalsIgnoreCase(boxFilter)) {
				cmd.add("-mipper");
				cmd.add("boxfilter.dll");
			}
			
			System.out.println(cmd);
			
			String[] cmds = new String[cmd.size()];
			cmd.toArray(cmds);
			Process p = Runtime.getRuntime().exec(cmds);
			p.waitFor();
			
			System.out.println(": "+ p.exitValue());
			BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));
		 
		} finally {

		}
	}

	private void printString(PrintWriter defWriter, String string, String value) {
		// TODO Auto-generated method stub
		
	}

	public String getBoxFilter() {
		return boxFilter;
	}

	public void setBoxFilter(String format) {
		this.boxFilter = format;
	}

}
