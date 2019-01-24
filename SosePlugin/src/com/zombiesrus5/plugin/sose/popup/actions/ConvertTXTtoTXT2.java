package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter; 
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import sose.tools.EntityParser;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;
import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class ConvertTXTtoTXT2 implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;
	protected String format;
	
	private class ConvertVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;
		
		
		public ConvertVisitor(IProgressMonitor monitor) {
			super();
			this.monitor = monitor;
		}


		@Override
		public boolean visit(IResource resource) throws CoreException {
			// TODO Auto-generated method stub
			try {
				if (resource.getType() == IFile.FILE) {
					normalize(resource, monitor);
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		
	}
	
	/**
	 * Constructor for Action1.
	 */
	public ConvertTXTtoTXT2() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Job job = new Job("Convert Data") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				monitor.beginTask("Normalize Research", LONG);
				try {
					if (selection instanceof IStructuredSelection) {
						for (Iterator it = ((IStructuredSelection) selection).iterator(); it
								.hasNext();) {
							Object element = it.next();
							IResource resource = null;
							if (element instanceof IResource) {
								resource = (IResource) element;
							} else if (element instanceof IAdaptable) {
								resource = (IResource) ((IAdaptable) element)
										.getAdapter(IResource.class);
							}
							if (resource != null) {
								if (resource.getType() == IFile.FILE) {
									normalize(resource, monitor);
								} else if (resource.getType() == IFile.FOLDER) {
									ConvertVisitor visitor = new ConvertVisitor(monitor);
									resource.accept(visitor);
								} else if (resource.getType() == IFile.PROJECT) {
									ConvertVisitor visitor = new ConvertVisitor(monitor);
									resource.accept(visitor);
								}
							}
							
							resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						}
					}
					} catch (Exception e) {
						MessageDialog.openInformation(
							shell,
							"SoasePlugin",
							"Convert Data (TXT) was not executed successfully. " + e);
						return Status.CANCEL_STATUS;
					}
					return Status.OK_STATUS;
			}
			
		};
		
		job.schedule();
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/**
	 * Toggles sample nature on a project
	 * 
	 * @param project
	 *            to have sample nature added or removed
	 */
	private void normalize(IResource resource, IProgressMonitor monitor) throws Exception {
		EntityParser parser = EntityBuilder.getParser(resource.getProject());
		IProject project = resource.getProject();
		String resourceName = resource.getName();
		String installationDirectory = parser.getSinsInstallationDirectory();
		String type = "";
		String value = "";
		boolean hasLevels = true;
		
		File sourceDef = new File(resource.getRawLocation().toOSString());
		File outputDef = new File(resource.getRawLocation().toOSString());
		
		try {
		LineNumberReader defReader = new LineNumberReader(new FileReader(sourceDef));
		StringWriter sw = new StringWriter();
		PrintWriter defWriter = new PrintWriter(sw);

		String line = defReader.readLine();
		if (!line.contains("TXT")) {
			throw new Exception("Cannot read binary files!");
		}
		if (line.contains("TXT2")) {
			// file is already in TXT2 format
			return;
		}
		
		// need to handle Galaxy Files too
		if (resource.getFileExtension().endsWith("galaxy")) {
			type = "Galaxy";
			// verify if the Version is 193
			line = defReader.readLine();
			
			if (line.contains("193")) { 
				return;
			}
			
			defWriter.println("TXT");
			defWriter.println("versionNumber 193");
		} else {
			defWriter.println("TXT2");
			defWriter.println("SinsArchiveVersion 193");
			line = defReader.readLine();
			if (line.contains("Ability")) {
				type = "Ability";
			} else if (line.contains("Frigate")) {
				type = "Frigate";
			}
			defWriter.println(line);
		}
		
		//System.out.println(keyword);
		while (defReader.ready()) {
			line = defReader.readLine();

			if (type.equals("Ability") && line.contains("useCostType \"Resources")) {
					defWriter.println(line);
					
					line = defReader.readLine();
					// eat resourceCost Line
					
					// should be credits
					line = defReader.readLine();
					value = parseValue(line);
					
					defWriter.println("creditCost");
					printLevels(defWriter, value, value, value, value, 1);

					line = defReader.readLine();
					value = parseValue(line);
					
					defWriter.println("metalCost");
					printLevels(defWriter, value, value, value, value, 1);

					line = defReader.readLine();
					value = parseValue(line);
					
					defWriter.println("crystalCost");
					printLevels(defWriter, value, value, value, value, 1);

			} else if (type.equalsIgnoreCase("Frigate") && line.contains("hasLevels FALSE")) {
					defWriter.println(line);
					hasLevels = false;
			} else if (type.equalsIgnoreCase("Frigate") && line.contains("hasLevels TRUE")) {
					defWriter.println(line);
					hasLevels = true;
			} else if (type.equalsIgnoreCase("Frigate") && line.trim().equals("Prerequisites")) {
					if (hasLevels) {
						defWriter.println("relationshipChangeRate");
						defWriter.println("Level:0 0.000000");
						defWriter.println("Level:1 0.000000");
						defWriter.println("Level:2 0.000000");
						defWriter.println("Level:3 0.000000");
					} else {
						defWriter.println("relationshipChangeRate 0.000000");
					}
					defWriter.println(line);
			} else if (line.contains("spawnShipsHyperspaceSpawnType")) {
				defWriter.println(line);
				int spaceIndex = line.indexOf("spawnShipsHyperspaceSpawnType");
				String spacing = line.substring(0, spaceIndex);
				defWriter.println(spacing + "shouldAdoptFirstSpawnersMinorFactionState TRUE");
				defWriter.println(spacing + "spawnShipsMinStartingHullPerc 1.0");
				defWriter.println(spacing + "spawnShipsMaxStartingHullPerc 1.0");
			} else if (line.contains("cannonGravityWellRatioDenominator")) {
				defWriter.println(line);
				int spaceIndex = line.indexOf("cannonGravityWellRatioDenominator");
				String spacing = line.substring(0, spaceIndex);
				defWriter.println(spacing + "maxAiCannons 3");
			} else if (line.contains("isPsi FALSE")) {
				//defWriter.println(line);
				defWriter.println("playerRaceType \"Other\"");
			} else if (line.contains("isPsi TRUE")) {
				//defWriter.println(line);
				defWriter.println("playerRaceType \"Psi\"");
			} else if (line.contains("GameEventSound:UseAbilityUnmetConstraintHasAbilityWithCooldown")) {
				defWriter.println(line);
				String spacing ="\t";
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetConstraintHasColonizeAbility \"UI_COMMON_DEFAULTERROR\"");
			} else if (line.contains("GameEventSound:UseAbilityUnmetConstraintIsExplored")) {
				defWriter.println(line);
				String spacing ="\t";
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetConstraintNotIsExplored \"UI_COMMON_DEFAULTERROR\"");
			} else if (line.contains("GameEventSound:UseAbilityUnmetConstraintIsPsi")) {
				defWriter.println(line);
				String spacing ="\t";
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetConstraintNotIsTech \"UI_COMMON_DEFAULTERROR\"");
			} else if (line.contains("GameEventSound:UseAbilityUnmetConstraintIsInNonFriendlyGravityWell")) {
				defWriter.println(line);
				String spacing ="\t";
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetConstraintNotIsMinorFactionOwned \"UI_COMMON_DEFAULTERROR\"");
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetConstraintNotIsOutlaw \"UI_COMMON_DEFAULTERROR\"");
			} else if (line.contains("GameEventSound:UseAbilityUnmetOwnerAlliedOrEnemy")) {
				defWriter.println(line);
				String spacing ="\t";
				defWriter.println(spacing + "GameEventSound:UseAbilityUnmetOwnerUncontrolledMinorFaction \"UI_COMMON_DEFAULTERROR\"");
			} else if (line.contains("isNormalPlayer")) {
				String space = line.substring(0, line.indexOf("is"));
				String normalPlayer = parseValue(line);
				String raidingPlayer = parseValue(defReader.readLine());
				String insurgentPlayer = parseValue(defReader.readLine());
				String occupationPlayer = parseValue(defReader.readLine());
				String madVasariPlayer = parseValue(defReader.readLine());
				
				String playerType = "NPCMilitia";
				if (normalPlayer.equalsIgnoreCase("TRUE")) {
					playerType = "Normal";
				} else if (raidingPlayer.equalsIgnoreCase("TRUE")) {
					playerType = "NPCPirateRaider";
				} else if (insurgentPlayer.equalsIgnoreCase("TRUE")) {
					playerType = "NPCRebel";
				} else if (occupationPlayer.equalsIgnoreCase("TRUE")) {
					playerType = "NPCOccupationVictory";
				} else if (madVasariPlayer.equalsIgnoreCase("TRUE")) {
					playerType = "NPCMadVasari";
				} 
				defWriter.println(space + "playerType \"" + playerType + "\"");
			} else if (line.trim().isEmpty() && type.equals("Galaxy")) {
				defWriter.println("dlcID 204880");
			} else {
				defWriter.println(line);
			}
		}
		
		if (!line.trim().isEmpty() && !line.contains("dlcID") && type.equals("Galaxy")) {
			defWriter.println("dlcID 204880");
			defWriter.println();
		}
		
//		if (defReader.ready()) {
//
//			while (defReader.ready()) {
//				line = defReader.readLine();
//			
//				defWriter.println(line);
//			}
//		}
		
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

	private void printLevels(PrintWriter defWriter, String value, String value2, String value3, String value4, int tabs) {
		String prefix = "";
		for (int i=0; i<tabs; i++) {
			prefix += "\t";
		}
		defWriter.println(prefix + "Level:0 " + value);
		defWriter.println(prefix + "Level:1 " + value2);
		defWriter.println(prefix + "Level:2 " + value3);
		defWriter.println(prefix + "Level:3 " + value4);
	}

	public String parseValue(String currentLine) {
		String value = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			value = currentLine.substring(index).trim();
		}
		if (value != null) {
			value = value.replace("\"", " ").trim();
		}
		return value;
	}
}
