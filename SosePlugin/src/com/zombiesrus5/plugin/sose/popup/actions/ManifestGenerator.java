package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
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

public class ManifestGenerator implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;
	protected String manifestFileName = "entity.manifest";
	protected String manifestType = "entityNameCount";
	protected String manifestFieldName = "entityName";
	protected String extension = ".entity";
	protected boolean includeExtension = true;
	
	private class ConvertVisitor implements IResourceVisitor {
		private IProgressMonitor monitor;
		SortedSet<String> manifestEntries = new TreeSet<String>();
		
		public ConvertVisitor(IProgressMonitor monitor, SortedSet<String> manifestEntries) {
			super();
			this.monitor = monitor;
			this.manifestEntries = manifestEntries;
		}


		@Override
		public boolean visit(IResource resource) throws CoreException {
			// TODO Auto-generated method stub
			try {
				if (resource.getType() == IFile.FILE) {
					generate(resource, monitor, manifestEntries);
					monitor.worked(1);
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
	public ManifestGenerator() {
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
	   MessageDialog dialog = new MessageDialog(
			      null, "Title", null, "The " + manifestFileName + " will be overwritten. Are you sure you want to continue",
			      MessageDialog.QUESTION,
			      new String[] {"Yes", "No"},
			      1); // yes is the default
	   int result = dialog.open();
	   
	   if (result == 1) {
		   return;
	   }
				      
		Job job = new Job("Convert Data") {
			SortedSet<String> manifestEntries = new TreeSet<String>();

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				
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
								if (resource.getType() == IFile.PROJECT) {
									monitor.beginTask("Generate Entity Manifest", ((IProject)resource).members().length);
									ConvertVisitor visitor = new ConvertVisitor(monitor, manifestEntries);
									resource.accept(visitor);
									
									// get related projects
									IProject p = (IProject) resource;
									IProject[] referenced = p.getReferencedProjects();
									if (referenced != null) {
										for (int i=0; i<referenced.length; i++) {
											referenced[i].accept(visitor);
										}
									}
									
									IProject[] referencing = p.getReferencingProjects();
									if (referencing != null) {
										for (int i=0; i<referencing.length; i++) {
											referencing[i].accept(visitor);
										}
									}
								}
							}
							IProject project = (IProject)resource;
							IResource manifest = project.findMember(manifestFileName);
							String manifestFilePath = null;
							if (manifest == null) {
								String modRoot = (String) project.getPersistentProperty(EntityBuilder.KEY_MOD_ROOT);
								manifestFilePath = project.getRawLocation() + "\\" + modRoot + "\\" + manifestFileName;
							} else {
								manifestFilePath = manifest.getRawLocation().toOSString();
							}
							File manifestFile = new File(manifestFilePath);
							FileWriter fw = null;
							try {
								fw = new FileWriter(manifestFile);
								PrintWriter pw = new PrintWriter(fw);
								pw.println("TXT");
								pw.print(manifestType + " ");
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
								monitor.setCanceled(true);
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
					
					//System.out.println("[+" + manifestFileName + "] manifest entries: " + manifestEntries.size());
					
					
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
	private void generate(IResource resource, IProgressMonitor monitor, SortedSet<String> manifestEntries) throws Exception {
			String resourceName = resource.getName();

			if (resource.getFullPath().toOSString().toLowerCase().contains(extension)) {
				manifestEntries.add(resource.getName());
			} 

	}

}
