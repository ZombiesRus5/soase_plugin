package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.ResourceNode;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CompareInput extends CompareEditorInput {
	 
	ITypedElement resultItem;
	ITypedElement expectedItem;
 
	public CompareInput(String result, String expected, String title) {
		super(new CompareConfiguration());
		init(result, expected, title);
	}
	public CompareInput(IResource result, String expected, String title) {
		super(new CompareConfiguration());
		init(result, expected, title);
	}
	public CompareInput(IResource result, IResource expected, String title) {
		super(new CompareConfiguration());
		init(result, expected, title);
	}
	private void init(String result, String expected, String title) {
		resultItem = new CompareItem("Result", result);
		expectedItem = new CompareItem("Expected", expected);
		setTitle(title);
		getCompareConfiguration().setLeftEditable(true);
		getCompareConfiguration().setRightEditable(false);
		getCompareConfiguration().setLeftLabel("Modded");
		getCompareConfiguration().setRightLabel("Reference");
	}
	
	private void init(IResource result, String expected, String title) {
		resultItem = new CompareResourceItem(result);
		expectedItem = new CompareItem("Expected", expected);
		setTitle(title);
		getCompareConfiguration().setLeftEditable(true);
		getCompareConfiguration().setRightEditable(false);
		getCompareConfiguration().setLeftLabel("Modded");
		getCompareConfiguration().setRightLabel("Reference");
	}
	private void init(IResource result, IResource expected, String title) {
		resultItem = new CompareResourceItem(result);
		expectedItem = new CompareResourceItem(expected);
		setTitle(title);
		getCompareConfiguration().setLeftEditable(true);
		getCompareConfiguration().setRightEditable(false);
		getCompareConfiguration().setLeftLabel("Modded");
		getCompareConfiguration().setRightLabel("Reference");
	}
	
	protected Object prepareInput(IProgressMonitor pm) {
		
		return new DiffNode(resultItem, expectedItem);
	}
	
	public void saveChanges(IProgressMonitor pm) throws CoreException {
		//System.out.println("Saved changes STUB");
		super.saveChanges(pm);
		if (!(resultItem instanceof ResourceNode && resultItem instanceof BufferedContent)) {
			return;
		}
		ResourceNode rn = (ResourceNode)resultItem;
		BufferedContent bc = (BufferedContent)resultItem;
		IResource resource= rn.getResource();
		if (resource instanceof IFile) {
			byte[] bytes= bc.getContent();
			ByteArrayInputStream is= new ByteArrayInputStream(bytes);
			try {
				IFile file= (IFile) resource;
				if (file.exists()) {
					file.setContents(is, false, true, pm);
				}else {
					file.create(is, false, pm);
				}
			} finally {
				if (is != null)
					try {
						is.close();
						setDirty(false);
					} catch(IOException ex) {
						// Ignored
					}
					resource.getParent().refreshLocal(IResource.DEPTH_INFINITE, pm);
			}
		}
	}
}
