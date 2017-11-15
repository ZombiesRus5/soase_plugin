package com.zombiesrus5.plugin.sose.editors.quickFixProcessor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.dialogs.MessageDialog;

public class SinsQuickFixProcessor implements IQuickFixProcessor {

	@Override
	public boolean hasCorrections(ICompilationUnit unit, int problemId) {
		// TODO Auto-generated method stub
		 MessageDialog.openInformation(null, "QuickFix Demo",
         "This quick-fix is not yet implemented for " + problemId);

		return false;
	}

	@Override
	public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		// TODO Auto-generated method stub
		
		for (int i=0; i<locations.length; i++) {
			int problemId = locations[i].getProblemId();
			
			 MessageDialog.openInformation(null, "QuickFix Demo",
			         "This quick-fix is not yet implemented for " + problemId);

		}
		 return null;
	}



}
