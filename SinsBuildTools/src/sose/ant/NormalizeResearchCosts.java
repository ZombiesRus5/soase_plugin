package sose.ant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

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
public class NormalizeResearchCosts extends Task {
	String keyword = "pos";
	List<ResourceCollection> resourceList = new ArrayList<ResourceCollection>();
	Map<String, Integer> store = new HashMap<String, Integer>();

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
		
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 400);
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 0);
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 600);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 50);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 100);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 800);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 100);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 175);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1000);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 150);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 250);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1200);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 200);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 325);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1400);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 250);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 400);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1600);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 300);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 475);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1800);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 350);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 550);

		store.put(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 0);
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.BASEUPGRADETIME, 40);
		store.put(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.BASEUPGRADETIME, 45);
		store.put(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.BASEUPGRADETIME, 50);
		store.put(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.BASEUPGRADETIME, 60);
		store.put(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.BASEUPGRADETIME, 75);
		store.put(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.BASEUPGRADETIME, 90);
		store.put(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.BASEUPGRADETIME, 105);
		store.put(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.BASEUPGRADETIME, 120);
		store.put(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
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
						setData(fs, srcFiles[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BuildException(e);
		}

		super.execute();
	}
		
	private void setData(FileSet fs, String sourceFile) throws Exception {
		File sourceDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		File outputDef = new File(fs.getDir().getAbsolutePath() + "/" + sourceFile);
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
		StringWriter sw = new StringWriter();
		PrintWriter defWriter = new PrintWriter(sw);

		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new BuildException("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// read SinsArchiveVersion
			defWriter.println(line);
			line = defReader.readLine();
		}
		defWriter.println(line);
		System.out.println(keyword);
		Vector<String> keywordParts = StringUtils.split(keyword, '.');
		System.out.println(keywordParts);
		String keywordFinal = keywordParts.lastElement();
		for (int i=0; i<keywordParts.size()-1; i++) {
			while (defReader.ready()) {
				line = defReader.readLine();
				
				if (line.contains(keywordParts.get(i))) {
					defWriter.println(line);
					break;
				}
				defWriter.println(line);
			}
		}
		while (defReader.ready()) {
			line = defReader.readLine();
			
			if (line.contains(keywordFinal)) {
				break;
			}
			defWriter.println(line);
		}
		
		if (defReader.ready()) {
			for (int tabs=0; tabs<keywordParts.size()-1; tabs++) {
				defWriter.print('\t');
			}
			// read tier
			StringTokenizer stk = new StringTokenizer(line.trim(), "[, ", false);
			stk.nextElement(); // eat pos keyword
			String position = ((String)stk.nextElement()).trim();
			
			defWriter.println(line);
			
			// read to BaseUpgradeTime
			while (defReader.ready()) {
				line = defReader.readLine();
				
				if (line.contains("BaseUpgradeTime")) {
					break;
				}
				defWriter.println(line);
			}
			
			// set duration and costs
			defWriter.println("BaseUpgradeTime " + store.get(position + PreferenceConstants.BASEUPGRADETIME) + ".000000");
			defReader.readLine();
			defWriter.println("PerLevelUpgradeTime " + store.get(position + PreferenceConstants.PERLEVELUPGRADETIME) + ".000000");
			defReader.readLine();
			defWriter.println("BaseCost");
			defReader.readLine();
			defWriter.println("	credits " + store.get(position + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS) + ".000000");
			defReader.readLine();
			defWriter.println("	metal " + store.get(position + PreferenceConstants.BASECOST + PreferenceConstants.METAL) + ".000000");
			defReader.readLine();
			defWriter.println("	crystal " + store.get(position + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL) + ".000000");
			defReader.readLine();
			defWriter.println("PerLevelCostIncrease");
			defReader.readLine();
			defWriter.println("	credits " + store.get(position + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS) + ".000000");
			defReader.readLine();
			defWriter.println("	metal " + store.get(position + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL) + ".000000");
			defReader.readLine();
			defWriter.println("	crystal " + store.get(position + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL) + ".000000");
			defReader.readLine();
			defWriter.println("Tier " + position);
			
			while (defReader.ready()) {
				line = defReader.readLine();
				
				defWriter.println(line);
			}
		}
		
		defReader.close();
		
		defWriter.flush();
		sw.flush();
		PrintWriter fileWriter = new PrintWriter(new FileWriter(outputDef));
		fileWriter.print(sw.getBuffer());
		fileWriter.flush();
		fileWriter.close();
		defWriter.close();
		
		} finally {

		}
	}

}
