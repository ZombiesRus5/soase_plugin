package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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

public class ConvertDataTo implements IObjectActionDelegate {

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
					convertToText(resource, monitor);
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
	public ConvertDataTo() {
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
									monitor.beginTask("Convert Data", 1);
									convertToText(resource, monitor);
								} else if (resource.getType() == IFile.FOLDER) {
									monitor.beginTask("Convert Data", ((IFolder)resource).members().length);
									ConvertVisitor visitor = new ConvertVisitor(monitor);
									resource.accept(visitor);
								} else if (resource.getType() == IFile.PROJECT) {
									monitor.beginTask("Convert Data", ((IProject)resource).members().length);
									ConvertVisitor visitor = new ConvertVisitor(monitor);
									resource.accept(visitor);
								}
							}
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
	private synchronized void convertToText(IResource resource, IProgressMonitor monitor) throws Exception {
		try {
			EntityParser parser = EntityBuilder.getParser(resource.getProject());
			IProject project = resource.getProject();
			String resourceName = resource.getName();
			String installationDirectory = parser.getSinsInstallationDirectory();
			
			String command = installationDirectory + "\\ConvertData.exe";
			if (parser.getStrictValidation().startsWith(PreferenceConstants.STRICT_REBELLION)) {
				command = installationDirectory + "\\ConvertData_Rebellion.exe";
			}
			String[] cmd = new String[5];
			cmd[0] = command;
			cmd[2] = resource.getRawLocation().toOSString();
			cmd[3] = resource.getRawLocation().toOSString();
			cmd[4] = format;

			if (resource.getFullPath().toOSString().toLowerCase().contains(".entity")) {
				cmd[1] = "entity";
			} else if (resource.getFullPath().toOSString().toLowerCase().contains(".mesh")) {
				cmd[1] = "mesh";
			} else if (resource.getFullPath().toOSString().toLowerCase().contains(".particle")) {
				cmd[1] = "particle";
			} else if (resource.getFullPath().toOSString().toLowerCase().contains(".brushes")) {
				cmd[1] = "brushes";
			} else {
				// nothing to do
				return;
			}
			
			monitor.subTask(resource + ": convert to " + format);
			Process p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
			
//			System.out.println(": "+ p.exitValue());
			BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));
		 
		    String line = "";			
		    while ((line = reader.readLine())!= null) {
				System.out.println(line);
		    }
		    
		    if (line != null) {
		    	monitor.subTask(resource + ": convert to " + format + ": "+ p.exitValue() + " " + line);
		    } else {
		    	monitor.subTask(resource + ": convert to " + format + ": "+ p.exitValue());
		    }
			resource.refreshLocal(0, new IProgressMonitor() {
				
				@Override
				public void worked(int work) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void subTask(String name) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setTaskName(String name) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void setCanceled(boolean value) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public boolean isCanceled() {
					// TODO Auto-generated method stub
					return false;
				}
				
				@Override
				public void internalWorked(double work) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void done() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beginTask(String name, int totalWork) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (CoreException e) {
		}
	}

}
