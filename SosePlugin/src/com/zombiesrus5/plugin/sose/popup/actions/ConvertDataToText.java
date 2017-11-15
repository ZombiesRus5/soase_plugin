package com.zombiesrus5.plugin.sose.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ConvertDataToText extends ConvertDataTo {

	/**
	 * Constructor for Action1.
	 */
	public ConvertDataToText() {
		super();
		format = "txt";
	}

}
