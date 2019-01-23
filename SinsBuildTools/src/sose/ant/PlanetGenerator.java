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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
public class PlanetGenerator extends Task {
	// Planet textures
	List<ResourceCollection> resourseList = new ArrayList<ResourceCollection>();
	
	String tempDirectory = null;
	String planetPrefix = null;
	String textureDirectory = null;
	String defaultDataTexture = null;
	String meshDirectory = null;
	
	String planetTypes = null;
	String defaultTemplateName = null;
	
	String planetEntity = null;
	String planetTemplate = null;
	String parentPlanetTemplate = null;
	String asteroidTemplates = null;
	String dustCloudTemplates = null;
	
	List<String> asteroidList = new LinkedList<String>();
	Iterator<String> asteroidIterator = null;
	List<String> dustCloudList = new LinkedList<String>();
	Iterator<String> dustCloudIterator = null;
	
	String cloudColor = "0";
	String nullMeshRadius = "0.00000";
	String nullMeshParticleEffect = "";
	
	String planetSize = null;
	
	String planetMeshHeaderFileName = null;
	String planetMeshFooterFileName = null;
	
	String requiredPlanetBonuses = null;
	String optionalPlanetBonuses = null;
	
	String version="Diplomacy";

	List<String> meshInfos = new ArrayList<String>();
	Random r = new Random(Calendar.getInstance().getTimeInMillis());
	
	public void add(ResourceCollection res) {
		resourseList.add(res);
	}

	@Override
	public void execute() throws BuildException {
//		System.out.println("yay!!!!");
		try {
		Iterator<ResourceCollection> itr = resourseList.iterator();

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
					if (srcFiles[i].toUpperCase().endsWith("DDS")) {
						generatePlanet(srcFiles[i]);
					} else if (srcFiles[i].toUpperCase().endsWith("MESH")) {
						generatePlanetFromMesh(srcFiles[i]);
					}
				}
			}
		}
			
		if (!meshInfos.isEmpty()) {
			System.out.println("[+" + planetTemplate + "] meshInfo entries: " + meshInfos.size());
				
			File planetEntityFile = new File(planetEntity);
			File planetTemplateFile = new File(planetTemplate);
			File parentPlanetTemplateFile = new File(parentPlanetTemplate);
			
			FileWriter fw = null;
			FileReader fr = null;
			try {
				fw = new FileWriter(planetEntityFile);
				try {
					fr = new FileReader(planetTemplateFile);
				} catch(FileNotFoundException e) {
					System.out.println("planetTemplate " + planetTemplate + "not found, trying parentPlanetTemplate");
					fr = new FileReader(parentPlanetTemplateFile);
				}
				LineNumberReader lnr = new LineNumberReader(fr);
				lnr.readLine(); // eat TXT
				lnr.readLine(); // eat entityType
				lnr.readLine(); // eat meshInfoCount 0
				PrintWriter pw = new PrintWriter(fw);
				pw.println("TXT");
				pw.println("entityType \"Planet\"");
				pw.print("meshInfoCount ");
				pw.print(meshInfos.size());
				pw.println();
				
				Iterator<String> meshInfosItr = meshInfos.iterator();
				while (meshInfosItr.hasNext()) {
					String entry = (String) meshInfosItr.next();
					
					pw.print(entry);
				}
				
				// read until requiredPlanetBonusesCount 0
				// print requiredPlanetBonses from attribute
				
				// read until possibleRandomPlanetBonusesCount 0
				// print random planet bonuses from attribute
				
				while (lnr.ready()) {
					pw.println(lnr.readLine());
				}
				
				pw.flush();
				fw.flush();
				fw.close();
			} catch(IOException e) {
				throw new BuildException(e);
			}
			
			try {
			String entityDefName = planetEntityFile.getName().replace(".entity", "");
			String orbitBodyTypeName = entityDefName.replace("Planet", "");
			
			generatePlanetTypeCount(orbitBodyTypeName);
			generatePlanetTypeTemplates(orbitBodyTypeName);
			generateOrbitBodyTypeTemplate(orbitBodyTypeName, entityDefName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		} catch(Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		super.execute();
	}

	private void generateOrbitBodyTypeTemplate(String orbitBodyTypeName,
			String entityDefName) {
		try {
			File orbityBodyTypeFile = new File(tempDirectory + "/" + orbitBodyTypeName + ".orbitBodyTypeCount");
			FileWriter fw = new FileWriter(orbityBodyTypeFile);
			PrintWriter pw = new PrintWriter(fw);
			pw.println("TXT");
			pw.println("orbitBodyTypeCount 1");
			pw.println("orbitBodyType");
			pw.println("\ttypeName \"" + orbitBodyTypeName + "\"");
			pw.println("\tentityDefName \"" + entityDefName + "\"");
			pw.println("\tdefaultTemplateName \"" + defaultTemplateName + "\"");
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void generatePlanetTypeTemplates(String orbitBodyTypeName) {
		String[] planetTypesArray = planetTypes.split(",");
		for (int i=0; i<planetTypesArray.length; i++) {
			try {
				StringTokenizer planetType = new StringTokenizer(planetTypesArray[i], "()", false);
				String designName = planetType.nextToken();
				int occurences = 1;
				if (planetType.hasMoreElements()) {
					occurences = Integer.parseInt(planetType.nextToken());
				}
				generatePlanetTypeDesign(orbitBodyTypeName, designName,	occurences);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void generatePlanetTypeDesign(String orbitBodyTypeName,
			String designName, int occurences) throws IOException {
		File planetTypeTemplateFile = new File(tempDirectory + "/" +  orbitBodyTypeName + "." + designName + ".planetTypeDesign");
		FileWriter fw = new FileWriter(planetTypeTemplateFile);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("TXT");
		pw.println("orbitBodyTypeCount " + occurences);
		for (int j=0; j<occurences; j++) {
			pw.println("\torbitBodyType \"" + orbitBodyTypeName + "\"");
		}
		fw.flush();
		fw.close();
	}

	private void generatePlanetTypeCount(String orbitBodyTypeName) throws IOException {
		File planetTypeTemplateFile = new File(tempDirectory + "/" +  orbitBodyTypeName + ".planetTypeCount");
		FileWriter fw = new FileWriter(planetTypeTemplateFile);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("TXT");
		pw.println("planetTypeCount 1");
		pw.println("planetType");
		pw.println("\tdesignName \"" + orbitBodyTypeName + "\"");
		pw.println("\tdesignStringId \"IDS_GALAXYSCENARIOTYPEPLANET_" + orbitBodyTypeName.toUpperCase() + "\"");
		pw.println("\torbitBodyTypeCount 1");
		pw.println("\t\torbitBodyType \"" + orbitBodyTypeName + "\"");
		fw.flush();
		fw.close();
	}

	private void generatePlanet(String colorTexture) {
		System.out.println(colorTexture);
		
		String dataTexture = getDataTexture(colorTexture);
		
		String planetResource = colorTexture.substring(0, colorTexture.indexOf("-cl"));
		
		generateMeshFile(planetResource, colorTexture, dataTexture);
		
		generateMeshInfo(planetResource);
		
		
	}

	private void generatePlanetFromMesh(String meshFile) {
		System.out.println(meshFile);
		
		String planetResource = meshFile.substring(0, meshFile.indexOf("."));
				
		generateMeshInfo(planetResource);
	}

	private void generateMeshFile(String planetResource, String colorTexture, String dataTexture) {
		String meshFileName = meshName(planetResource);
		meshFileName = meshFileName + ".mesh";
		File meshInfosFile = new File(meshDirectory + "/" + meshFileName);
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(meshInfosFile);
			PrintWriter pw = new PrintWriter(fw);
			
			// read header portion
			InputStream header = new FileInputStream(new File(getPlanetMeshHeaderFileName()));
			readDataFile(header, pw);
			
			// pw.println(header);
			pw.println("\t\tDiffuseTextureFileName " + "\"" + colorTexture + "\"");
			pw.println("\t\tSelfIlluminationTextureFileName " +  "\"" + dataTexture + "\"");
			
			// read footer portion
			InputStream footer = new FileInputStream(new File(getPlanetMeshFooterFileName()));
			readDataFile(footer, pw);

			pw.println();
			
			pw.flush();
			fw.flush();
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private String meshName(String planetResource) {
		// TODO Auto-generated method stub
		if (planetSize != null) {
			String replaceFrom = planetResource.replaceAll("([0-9])$", "").replaceAll("([0-9])$", "");
			String replaceTo = replaceFrom + "_" + planetSize;
			return planetResource.replace(replaceFrom, replaceTo);
		} else {
			return planetResource;
		}
	}

	private void generateMeshInfo(String planetResource) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		
		String typeNameStringID = planetResource.replaceAll("([0-9])$", "").replaceAll("([0-9])$", "");
		if (planetSize != null) {
			typeNameStringID += "_" + planetSize.toUpperCase();
		}
		pw.println("meshInfo");
		pw.println("\ttypeNameStringID " + toName("IDS_PLANETTYPE_", typeNameStringID));
		pw.println("\tasteroidTemplate \"" + nextAsteroidTemplate() + "\"");
		pw.println("\tdustCloudTemplate \"" + nextDustCloudTemplate() + "\"");
		pw.println("\tmeshName \"" + meshName(planetResource) + "\"");
		pw.println("\tcloudColor " + cloudColor);
		pw.println("\tnullMeshRadius " + nullMeshRadius);
		pw.println("\tnullMeshParticleEffect \"" + nullMeshParticleEffect + "\"");
		if ("Moon".equalsIgnoreCase(planetSize)) {
			pw.println("\thudIcon " + toName("HUDICON_MOON_", planetResource));
			pw.println("\tsmallHudIcon " + toName("HUDICONSMALL_MOON_", planetResource));
			pw.println("\tinfoCardIcon " + toName("INFOCARDICON_MOON_", planetResource));
			pw.println("\tmainViewIcon " + toName("INFOCARDICON_MOON_", planetResource));
			pw.println("\tundetectedMainViewIcon " + toName("UNDETECTEDICON_MOON_", planetResource));
			pw.println("\tpicture " + toName("PICTURE_MOON_", planetResource));
		} else if ("Dwarf".equalsIgnoreCase(planetSize)) {
			pw.println("\thudIcon " + toName("HUDICON_DWARF_", planetResource));
			pw.println("\tsmallHudIcon " + toName("HUDICONSMALL_DWARF_", planetResource));
			pw.println("\tinfoCardIcon " + toName("INFOCARDICON_DWARF_", planetResource));
			pw.println("\tmainViewIcon " + toName("HUDICONSMALL_DWARF_", planetResource));
			pw.println("\tundetectedMainViewIcon " + toName("UNDETECTEDICON_DWARF_", planetResource));
			pw.println("\tpicture " + toName("PICTURE_DWARF_", planetResource));
		} else if ("Giant".equalsIgnoreCase(planetSize)) {
			pw.println("\thudIcon " + toName("HUDICON_GIANT_", planetResource));
			pw.println("\tsmallHudIcon " + toName("HUDICONSMALL_GIANT_", planetResource));
			pw.println("\tinfoCardIcon " + toName("INFOCARDICON_GIANT_", planetResource));
			pw.println("\tmainViewIcon " + toName("MAINVIEWICON_GIANT_", planetResource));
			pw.println("\tundetectedMainViewIcon " + toName("UNDETECTEDICON_GIANT_", planetResource));
			pw.println("\tpicture " + toName("PICTURE_GIANT_", planetResource));
		} else {
			pw.println("\thudIcon " + toName("HUDICON_PLANET_", planetResource));
			pw.println("\tsmallHudIcon " + toName("HUDICONSMALL_PLANET_", planetResource));
			pw.println("\tinfoCardIcon " + toName("INFOCARDICON_PLANET_", planetResource));
			pw.println("\tmainViewIcon " + toName("MAINVIEWICON_PLANET_", planetResource));
			pw.println("\tundetectedMainViewIcon " + toName("UNDETECTEDICON_PLANET_", planetResource));
			pw.println("\tpicture " + toName("PICTURE_PLANET_", planetResource));
		}
		if ("Rebellion".equalsIgnoreCase(version)) {
			pw.println("shouldPrefer FALSE");
		}
		
		pw.flush();
		meshInfos.add(sw.toString());
		
	}

	private String nextAsteroidTemplate() {
		// TODO Auto-generated method stub
		if (!asteroidIterator.hasNext()) {
			asteroidIterator = asteroidList.iterator();
		}
		return asteroidIterator.next();
	}
	
	private String nextDustCloudTemplate() {
		if (!dustCloudIterator.hasNext()) {
			dustCloudIterator = dustCloudList.iterator();
		}
		return dustCloudIterator.next();
	}

	private String randomize(String possibilities) {
		// TODO Auto-generated method stub
		String[] array = possibilities.split(",");
		return array[r.nextInt(array.length)].trim();
	}

	private String toName(String pre, String planetResource) {
		// TODO Auto-generated method stub
		return "\"" + pre + planetResource.toUpperCase().replace("PLANET_", "").replace("PLANET", "") + "\"";
	}

	private String getDataTexture(String planetResource) {
		String dataFile = planetResource.replace("-cl", "-da");
		File temp = new File(textureDirectory + "/" + dataFile);
		if (temp.isFile()) {
			return dataFile;
		}
		return defaultDataTexture;
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

	public String getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public String getTextureDirectory() {
		return textureDirectory;
	}

	public void setTextureDirectory(String textureDirectory) {
		this.textureDirectory = textureDirectory;
	}

	public String getDefaultDataTexture() {
		return defaultDataTexture;
	}

	public void setDefaultDataTexture(String defaultDataTexture) {
		this.defaultDataTexture = defaultDataTexture;
	}

	public String getMeshDirectory() {
		return meshDirectory;
	}

	public void setMeshDirectory(String meshDirectory) {
		this.meshDirectory = meshDirectory;
	}

	public String getPlanetTemplate() {
		return planetTemplate;
	}

	public void setPlanetTemplate(String planetTemplate) {
		this.planetTemplate = planetTemplate;
	}

	public String getAsteroidTemplates() {
		return asteroidTemplates;
	}

	public void setAsteroidTemplates(String asteroidTemplates) {
		this.asteroidTemplates = asteroidTemplates;
		
		String[] array = asteroidTemplates.split(",");
		asteroidList.addAll(Arrays.asList(array));
		asteroidIterator = asteroidList.iterator();
	}

	public String getDustCloudTemplates() {
		return dustCloudTemplates;
	}

	public void setDustCloudTemplates(String dustCloudTemplates) {
		this.dustCloudTemplates = dustCloudTemplates;
		
		String[] array = dustCloudTemplates.split(",");
		dustCloudList.addAll(Arrays.asList(array));
		dustCloudIterator = dustCloudList.iterator();
	}

	public String getCloudColor() {
		return cloudColor;
	}

	public void setCloudColor(String cloudColor) {
		this.cloudColor = cloudColor;
	}

	public String getNullMeshRadius() {
		return nullMeshRadius;
	}

	public void setNullMeshRadius(String nullMeshRadius) {
		this.nullMeshRadius = nullMeshRadius;
	}

	public String getNullMeshParticleEffect() {
		return nullMeshParticleEffect;
	}

	public void setNullMeshParticleEffect(String nullMeshParticleEffect) {
		this.nullMeshParticleEffect = nullMeshParticleEffect;
	}

	public String getPlanetMeshHeaderFileName() {
		return planetMeshHeaderFileName;
	}

	public void setPlanetMeshHeaderFileName(String planetMeshHeaderFileName) {
		this.planetMeshHeaderFileName = planetMeshHeaderFileName;
	}

	public String getPlanetMeshFooterFileName() {
		return planetMeshFooterFileName;
	}

	public void setPlanetMeshFooterFileName(String planetMeshFooterFileName) {
		this.planetMeshFooterFileName = planetMeshFooterFileName;
	}

	private int readDataFile(InputStream is, PrintWriter buffer) throws Exception {
		int dataCount = 0;
		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		
		while (lnr.ready()) {
			String lineContents = lnr.readLine();
			buffer.println(lineContents);
		}
		return dataCount;
	}

	public String getPlanetPrefix() {
		return planetPrefix;
	}

	public void setPlanetPrefix(String planetPrefix) {
		this.planetPrefix = planetPrefix;
	}

	public String getPlanetEntity() {
		return planetEntity;
	}

	public void setPlanetEntity(String planetEntity) {
		this.planetEntity = planetEntity;
	}

	public String getPlanetTypes() {
		return planetTypes;
	}

	public void setPlanetTypes(String planetTypes) {
		this.planetTypes = planetTypes;
	}

	public String getDefaultTemplateName() {
		return defaultTemplateName;
	}

	public void setDefaultTemplateName(String defaultTemplateName) {
		this.defaultTemplateName = defaultTemplateName;
	}

	public String getPlanetSize() {
		return planetSize;
	}

	public void setPlanetSize(String planetSize) {
		this.planetSize = planetSize;
	}

	public String getParentPlanetTemplate() {
		return parentPlanetTemplate;
	}

	public void setParentPlanetTemplate(String parentPlanetTemplate) {
		this.parentPlanetTemplate = parentPlanetTemplate;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
