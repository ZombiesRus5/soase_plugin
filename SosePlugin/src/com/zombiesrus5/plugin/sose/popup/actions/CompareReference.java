package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.File;
import java.util.Iterator;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.Workbench;

import sose.tools.EntityParser;
import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;

public class CompareReference implements IObjectActionDelegate {

	private ISelection selection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
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
					compareReference(resource);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * Toggles sample nature on a project
	 * 
	 * @param project
	 *            to have sample nature added or removed
	 */
	private void compareReference(IResource resource) {
		try {
			EntityParser parser = EntityBuilder.getParser(resource.getProject());
			IProject project = resource.getProject();
			String referenceRoot = parser.getVanillaReferenceDirectory();
			String resourceName = resource.getName();
			
			File file = null;
			if (resource.getFullPath().toOSString().contains("GameInfo")) {
				file = new File(referenceRoot + "/GameInfo/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("PipelineEffect")) {
				file = new File(referenceRoot + "/PipelineEffect/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("Mesh")) {
				file = new File(referenceRoot + "/Mesh/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("String")) {
				file = new File(referenceRoot + "/String/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("Galaxy")) {
				file = new File(referenceRoot + "/Galaxy/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("Particle")) {
				file = new File(referenceRoot + "/Particle/" + resourceName);
			} else if (resource.getFullPath().toOSString().contains("Window")) {
				file = new File(referenceRoot + "/Window/" + resourceName);
			} else {
				file = new File(referenceRoot + "/GameInfo/" + resourceName);
			}

			CompareEditorInput compare = new CompareInput(resource, file.toString(), "Compare Reference");
			
			CompareUI.openCompareEditor(compare);
		} catch (CoreException e) {
		}
	}

}
