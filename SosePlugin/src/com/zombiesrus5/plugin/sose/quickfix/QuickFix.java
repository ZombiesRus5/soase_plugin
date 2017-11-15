package com.zombiesrus5.plugin.sose.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IMarkerResolution;

public class QuickFix implements IMarkerResolution {
	public QuickFix(String label) {
		super();
		this.label = label;
	}

	String label;
    
	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return label;
	}

	@Override
	public void run(IMarker marker) {
		// TODO Auto-generated method stub
		 MessageDialog.openInformation(null, "QuickFix Demo",
         "This quick-fix is not yet implemented");
	}

}
