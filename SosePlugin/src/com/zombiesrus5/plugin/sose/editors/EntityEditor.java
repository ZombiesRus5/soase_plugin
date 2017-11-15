package com.zombiesrus5.plugin.sose.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import sose.tools.EntityParser;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;

public class EntityEditor extends TextEditor {


	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		try {
			IResource resource = (IResource) input.getAdapter(IResource.class);
			if (resource != null) {
				EntityParser parser = EntityBuilder.getParser(resource.getProject());
			}
			if (resource == null) {
				colorManager = null;
				setSourceViewerConfiguration(new TextSourceViewerConfiguration(getPreferenceStore()));
				setDocumentProvider((IDocumentProvider)null);
				super.initializeEditor();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		super.doSetInput(input);
	}
	private ColorManager colorManager;
	private EntityOutlinePage outlinePage;

    /** The ID of this editor as defined in plugin.xml */
    public static final String EDITOR_ID = "com.zombiesrus5.plugin.sose.editors.EntityEditor";

    /** The ID of the editor context menu */
    public static final String EDITOR_CONTEXT = EDITOR_ID + ".context";

    /** The ID of the editor ruler context menu */
    public static final String RULER_CONTEXT = EDITOR_CONTEXT + ".ruler";

    protected void initializeEditor() {
            super.initializeEditor();
            
            setEditorContextMenuId(EDITOR_CONTEXT);
            setRulerContextMenuId(RULER_CONTEXT);
            setKeyBindingScopes(new String[] {EDITOR_CONTEXT});
    }

	public EntityEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new EntityConfiguration(this, colorManager));
		setDocumentProvider(new EntityDocumentProvider());
	}
	public void dispose() {
		if (colorManager != null) {
			colorManager.dispose();
		}
		super.dispose();
	}

	@Override
	public Object getAdapter(Class adapter) {
		try {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null) {
				outlinePage = new EntityOutlinePage(getDocumentProvider(), this);
				outlinePage.setFile((FileEditorInput)getEditorInput());
			}
			return outlinePage;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.getAdapter(adapter);
	}

	@Override
	protected void handleEditorInputChanged() {
		try {
			outlinePage.setFile((FileEditorInput)getEditorInput());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
