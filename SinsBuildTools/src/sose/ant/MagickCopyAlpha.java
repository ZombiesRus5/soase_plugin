package sose.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class MagickCopyAlpha extends Task {

	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();	

	// for copying alpha
	String alphaFile = null;
	
	// output file
	String outputDir = null;
	
	boolean overwrite = false;
	
	boolean includeColor = false;
	
	String prefix = "";
	
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
		String color = "";
		
		File resourceDir = null;
		String resourceName = null;
		if ("Albedo".equalsIgnoreCase(sourceDef.getParentFile().getName()) ||
				"AlbedoAO".equalsIgnoreCase(sourceDef.getParentFile().getName())) {
			// need to go up one more directory
			resourceDir = sourceDef.getParentFile().getParentFile();
		} else {
			resourceDir = sourceDef.getParentFile();
		}
		resourceName = resourceDir.getName();
		System.out.println("Processing: " + resourceName);
		
		File illuminationFile = null;
		File normalFile = null;
		File specularFile = null;
		File bloomFile = null;
		File lightsFile = null;
		File reflectiveFile = null;
		File clFile = null;
		File daFile = null;
		File nmFile = null;
		
		if (includeColor) {
			// check if there's a color
			String[] colors = new String[] {"Blue", "Green", "Grey", "Red", "White", "Yellow"};
			for (String check: colors) {
				if (sourceFile.contains(check)) {
					color = check;
				}
			}
		}
		
		illuminationFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "Illumination.tga");
		normalFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "Normal.png");
		specularFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "PBRSpecular.tga");
		bloomFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "Illumination_bloom.tga");
		lightsFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "Illumination_lights.tga");
		reflectiveFile = new File(resourceDir.getAbsolutePath() + "/" + resourceName + "PBRSpecular_reflective.tga");
		File alphaFileFile = new File(alphaFile);
		
		clFile = new File(outputDir + "/" + prefix + resourceName + color + "-cl.tga");
		daFile = new File(outputDir + "/" + prefix + resourceName + "-da.tga");
		nmFile = new File(outputDir + "/" + prefix + resourceName + "-nm.tga");
		
		try {
			if (overwrite || !clFile.exists() || (sourceDef.lastModified() > clFile.lastModified()
					|| alphaFileFile.lastModified() > clFile.lastModified())
					) {
				// we want to convert this file
				generateDiffuse(sourceDef, clFile);
			} else {
//				System.out.println("no update: " + cursorOverFile);
			}
			
			if (overwrite || !daFile.exists() || 
					(illuminationFile.lastModified() > daFile.lastModified() ||
							specularFile.lastModified() > daFile.lastModified()
					)
					) {
				generateSpecular(illuminationFile, specularFile, bloomFile, lightsFile, reflectiveFile, daFile);
			} else {
//				System.out.println("no update: " + cursorOverFile);
			}
			
			if (overwrite || !nmFile.exists() || normalFile.lastModified() > nmFile.lastModified()) {
				generateNormal(normalFile, nmFile);
			} else {
//				System.out.println("no update: " + cursorOverFile);
			}
		} finally {

		}
	}

	private void generateNormal(File normalFile, File nmFile) throws Exception {
		System.out.println(normalFile + " generate normal " + nmFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + normalFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-separate");
		cmd.add("-swap");
		cmd.add("0,3");
		cmd.add("-combine");
		cmd.add("\"" + nmFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void generateSpecular(File illuminationFile, File specularFile,
			File bloomFile, File lightsFile, File reflectiveFile, File daFile) throws Exception{
		System.out.println("generate specular " + daFile);
		
		generateBloom(illuminationFile, bloomFile);
		generateLights(illuminationFile, lightsFile);
		generateReflective(specularFile, reflectiveFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + specularFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("\"" + lightsFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("\"" + reflectiveFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("\"" + bloomFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-channel");
		cmd.add("RGBA");
		cmd.add("-combine");
		cmd.add("\"" + daFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void generateReflective(File specularFile, File reflectiveFile) throws Exception {
		System.out.println(specularFile + " generate normal " + reflectiveFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + specularFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-separate");
		cmd.add("-swap");
		cmd.add("3,0");
		cmd.add("-combine");
		cmd.add("\"" + reflectiveFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void generateDiffuse(File sourceDef, File outputFileFile) throws Exception {
		System.out.println(sourceDef + " generate " + outputFileFile);
		File alphaFileFile = new File(alphaFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + sourceDef.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("\"" + alphaFileFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-alpha");
		cmd.add("off");
		cmd.add("-compose");
		cmd.add("copy_opacity");
		cmd.add("-composite");
		cmd.add("\"" + outputFileFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void generateLights(File sourceDef, File outputFileFile) throws Exception {
		System.out.println(sourceDef + " generate bloom " + outputFileFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + sourceDef.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-set");
		cmd.add("colorspace");
		cmd.add("Gray");
		cmd.add("-separate");
		cmd.add("-average");
		cmd.add("\"" + outputFileFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void generateBloom(File sourceDef, File outputFileFile) throws Exception {
		System.out.println(sourceDef + " generate bloom " + outputFileFile);
		
		List<String> cmd = new ArrayList<String>();
		cmd.add("magick");
		cmd.add("convert");
		cmd.add("\"" + sourceDef.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		cmd.add("-blur");
		cmd.add("0x5");
		cmd.add("\"" + outputFileFile.getAbsoluteFile().toString().replaceAll("\\\\", "/") + "\"");
		
		System.out.println(cmd);
		
		String[] cmds = new String[cmd.size()];
		cmd.toArray(cmds);
		Process p = Runtime.getRuntime().exec(cmds);
		p.waitFor();
	}

	private void printString(PrintWriter defWriter, String string, String value) {
		// TODO Auto-generated method stub
		
	}

	public String getAlphaFile() {
		return alphaFile;
	}

	public void setAlphaFile(String alphaFile) {
		this.alphaFile = alphaFile;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public boolean isIncludeColor() {
		return includeColor;
	}

	public void setIncludeColor(boolean includeColor) {
		this.includeColor = includeColor;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}




}
