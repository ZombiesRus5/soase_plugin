package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class FindReferencesAction implements IObjectActionDelegate {

	private Shell shell;
	private ISelection selection;

	/**
	 * Constructor for Action1.
	 */
	public FindReferencesAction() {
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
		try {
		String fileNameNoExt = "";
		List<IResource> roots = new ArrayList<IResource>();
		List<String> patterns = new ArrayList<String>();
		
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
					fileNameNoExt = resource.getName().substring(0, resource.getName().indexOf("."));
					roots.add(resource.getProject());
					for (IProject ref: resource.getProject().getReferencedProjects()) {
						roots.add(ref);
					}
				}
				if (resource.getFullPath().toOSString().contains("GameInfo")) {
					patterns.add("*.entity");
					patterns.add("*.manifest");
				} else if (resource.getFullPath().toOSString().contains("Mesh")) {
					patterns.add("*.entity");
					patterns.add("*.particle");
				} else if (resource.getFullPath().toOSString().contains("Galaxy")) {
					patterns.add("*.manifest");
				} else if (resource.getFullPath().toOSString().contains("Window")) {
					patterns.add("*.manifest");
				} else if (resource.getFullPath().toOSString().contains("Particle")) {
					patterns.add("*.entity");
					patterns.add("*.mesh");
				} else if (resource.getFullPath().toOSString().contains("Textures")) {
					patterns.add("*.mesh");
					patterns.add("*.particle");
					patterns.add("*.brushes");
					patterns.add("*.texanim");
				} else if (resource.getFullPath().toOSString().contains("Sound")) {
					patterns.add("*.sounddata");
				} else {
					patterns.add("*");
				}
			}
		}

		IResource[] rootsArray = new IResource[roots.size()];
		String[] patternArray = new String[patterns.size()];
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots.toArray(rootsArray), patterns.toArray(patternArray), false);
		
		FileSearchQuery query = new FileSearchQuery(fileNameNoExt, false, false, scope);
		NewSearchUI.runQueryInBackground(query);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openInformation(
					shell,
					"SoasePlugin",
					"Unable to execute search.");

		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
