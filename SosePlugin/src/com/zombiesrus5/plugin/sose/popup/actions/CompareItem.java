package com.zombiesrus5.plugin.sose.popup.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

public class CompareItem extends BufferedContent implements ITypedElement,
		IEditableContent {
	private String name;
	private File file;
	FileInputStream input;
 
	CompareItem(String name, String fileName) {
		this.name = name;
		file = new File(fileName);
	}
 
	public InputStream getContents() throws CoreException {
		return createStream();
	}
 
	protected InputStream createStream() throws CoreException {
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {}
 
		return input;
	}
 
	public Image getImage() {return null;}
	public String getName() {return name;}
	public String getType() {return ITypedElement.TEXT_TYPE;}
 
	@Override
	public boolean isEditable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ITypedElement replace(ITypedElement dest, ITypedElement src) {
		// TODO Auto-generated method stub
		return null;
	}


}
