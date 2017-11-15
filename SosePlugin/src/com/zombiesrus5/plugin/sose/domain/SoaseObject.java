package com.zombiesrus5.plugin.sose.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SoaseObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6744746270528240766L;
	private List<SoaseObject> elements = new ArrayList<SoaseObject>();

	public List<SoaseObject> getElements() {
		return elements;
	}

	public void setElements(List<SoaseObject> elements) {
		this.elements = elements;
	}
	
	public void add(SoaseObject obj) {
		elements.add(obj);
	}
	

}
