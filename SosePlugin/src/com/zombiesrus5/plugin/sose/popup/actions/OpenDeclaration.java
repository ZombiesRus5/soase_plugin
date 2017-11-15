package com.zombiesrus5.plugin.sose.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.DialogUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.part.FileEditorInput;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;

public class OpenDeclaration implements IEditorActionDelegate {

	private Shell shell;
	private IEditorPart activeEditor = null;
	private String selectedText = null;
	
	/**
	 * Constructor for Action1.
	 */
	public OpenDeclaration() {
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
//		System.out.println("...");
//		MessageDialog.openInformation(
//			shell,
//			"SosePlugin",
//			"Open Declaration was executed.");
//	
        try {
            boolean activate = OpenStrategy.activateOnOpen();
//            System.out.println(action.getText());
            IResource currentResource = (IResource) activeEditor.getEditorInput().getAdapter(IResource.class);
            IProject currentProject = currentResource.getProject();
            String modRoot = (String) currentProject.getPersistentProperty(EntityBuilder.KEY_MOD_ROOT);
            
            IFile file = currentProject.getFile(modRoot + "/GameInfo/" + selectedText + ".entity");
            IDE.openEditor(activeEditor.getSite().getPage(), file, activate);
        } catch (PartInitException e) {
            DialogUtil.openError(activeEditor.getSite().getPage().getWorkbenchWindow()
                    .getShell(), IDEWorkbenchMessages.OpenFileAction_openFileShellTitle,
                    e.getMessage(), e);
        } catch (Exception e) {
        	//
        }
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TextSelection) {
//			System.out.println(((TextSelection)selection).getText());
			selectedText = ((TextSelection)selection).getText();
		}
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// TODO Auto-generated method stub
		activeEditor = targetEditor;
	}

}
