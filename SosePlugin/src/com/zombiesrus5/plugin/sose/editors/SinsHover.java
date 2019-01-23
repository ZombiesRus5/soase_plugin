package com.zombiesrus5.plugin.sose.editors;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.FileEditorInput;

import sose.tools.EntityParser;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;
import com.zombiesrus5.plugin.sose.editors.utils.HoverHelpCollector;
import com.zombiesrus5.plugin.sose.views.EntityDefinitionView;

public class SinsHover extends DefaultTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {
	public SinsHover(EntityEditor editor, ISourceViewer viewer) {
		super(viewer);
		this.editor = editor;
	}

	private EntityEditor editor = null;

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String hoverInfo = null;
		
		try {
		String hoverFocus = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			
		String entityType = null;
		String textType = "TXT";
		IResource resource = (IResource) editor.getEditorInput().getAdapter(IResource.class);
		EntityParser parser = EntityBuilder.getParser(resource.getProject());
		if (editor.getEditorInput() instanceof FileEditorInput) {
			if (((FileEditorInput)editor.getEditorInput()).getName().endsWith(".entity")) {
				entityType = EntityDefinitionView.getEntityType((FileEditorInput)editor.getEditorInput());
			} else if (parser.isSupportsFileType(((FileEditorInput)editor.getEditorInput()).getFile().getFileExtension())) {
				entityType = ((FileEditorInput)editor.getEditorInput()).getFile().getFileExtension();
				entityType = entityType.substring(0, 1).toUpperCase() + entityType.substring(1);
			} else {
				entityType = "";
			}
			textType = EntityDefinitionView.getTextType((FileEditorInput)editor.getEditorInput());
		}
		
		HoverHelpCollector collector = new HoverHelpCollector(hoverFocus);
		parser.processDefinition(entityType, hoverFocus, collector, textType);
		hoverInfo = collector.getHoverInfo();
		
		} catch (Exception e) {
			// do nothing
		}
		return hoverInfo;

	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		// TODO Auto-generated method stub
		return super.getHoverRegion(textViewer, offset);
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		final String selection = getHoverInfo(textViewer, hoverRegion);
		
		return selection;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				IInformationControl control = new DefaultInformationControl(parent, EditorsUI.getTooltipAffordanceString());
//				IInformationControl control = new SinsHoverInformationalControl(parent);
				control.setSize(500, 500);
				return control;
			}
		};
	}

}
