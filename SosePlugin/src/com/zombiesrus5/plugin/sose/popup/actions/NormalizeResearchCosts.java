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

public class NormalizeResearchCosts implements IObjectActionDelegate {

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
	public NormalizeResearchCosts() {
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
		defWriter.println(line);
		line = defReader.readLine();
		if (!line.contains("ResearchSubject")) {
			throw new Exception("Only works for ResearchSubject!");
		}
		defWriter.println(line);
		String keyword = "pos";
		//System.out.println(keyword);
		while (defReader.ready()) {
			line = defReader.readLine();
			
			if (line.contains(keyword)) {
				break;
			}
			defWriter.println(line);
		}
		
		if (defReader.ready()) {
//			for (int tabs=0; tabs<keywordParts.size()-1; tabs++) {
//				defWriter.print('\t');
//			}
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
			defWriter.println("BaseUpgradeTime " + parser.getUpgradeTime(position, PreferenceConstants.BASEUPGRADETIME) + ".000000");
			defReader.readLine();
			defWriter.println("PerLevelUpgradeTime " + parser.getUpgradeTime(position, PreferenceConstants.PERLEVELUPGRADETIME) + ".000000");
			defReader.readLine();
			defWriter.println("BaseCost");
			defReader.readLine();
			defWriter.println("	credits " + parser.getResearchCost(position, PreferenceConstants.BASECOST, PreferenceConstants.CREDITS) + ".000000");
			defReader.readLine();
			defWriter.println("	metal " + parser.getResearchCost(position, PreferenceConstants.BASECOST, PreferenceConstants.METAL) + ".000000");
			defReader.readLine();
			defWriter.println("	crystal " + parser.getResearchCost(position, PreferenceConstants.BASECOST, PreferenceConstants.CRYSTAL) + ".000000");
			defReader.readLine();
			defWriter.println("PerLevelCostIncrease");
			defReader.readLine();
			defWriter.println("	credits " + parser.getResearchCost(position, PreferenceConstants.PERLEVEL, PreferenceConstants.CREDITS) + ".000000");
			defReader.readLine();
			defWriter.println("	metal " + parser.getResearchCost(position, PreferenceConstants.PERLEVEL, PreferenceConstants.METAL) + ".000000");
			defReader.readLine();
			defWriter.println("	crystal " + parser.getResearchCost(position, PreferenceConstants.PERLEVEL, PreferenceConstants.CRYSTAL) + ".000000");
			defReader.readLine();
			defWriter.println("Tier " + position);
			
			while (defReader.ready()) {
				line = defReader.readLine();
			
				if (line.contains("CapitalShipMaxSlots") ||
						line.contains("ShipMaxSlots") ||
						line.contains("ResearchVictory")) {
							// return we aren't changing these
							return;
						}
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
