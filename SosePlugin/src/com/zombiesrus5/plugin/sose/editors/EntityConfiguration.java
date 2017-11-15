package com.zombiesrus5.plugin.sose.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntityConfiguration extends TextSourceViewerConfiguration {
	private EntityDoubleClickStrategy doubleClickStrategy;
	private EntityTagScanner tagScanner;
	private EntityScanner scanner;
	private ColorManager colorManager;
	private EntityEditor editor;

	public EntityConfiguration(EntityEditor editor, ColorManager colorManager) {
		super();
		this.editor = editor;
		this.colorManager = colorManager;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			EntityPartitionScanner.ENTITY_STRING_VALUE };
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new EntityDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected EntityScanner getEntityScanner() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_TAG);
		if (scanner == null) {
			scanner = new EntityScanner(colorManager);
			scanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(color))));
		}
		return scanner;
	}
	protected EntityTagScanner getEntityTagScanner() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_TAG);
		if (tagScanner == null) {
			tagScanner = new EntityTagScanner(colorManager);
			tagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(color))));
		}
		return tagScanner;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB stringColor = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_STRING);
		RGB numberColor = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_NUMBER);

		DefaultDamagerRepairer dr =
			new DefaultDamagerRepairer(getEntityTagScanner());
		reconciler.setDamager(dr, EntityPartitionScanner.ENTITY_STRING_VALUE);
		reconciler.setRepairer(dr, EntityPartitionScanner.ENTITY_STRING_VALUE);

		dr = new DefaultDamagerRepairer(getEntityScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		NonRuleBasedDamagerRepairer ndr =
			new NonRuleBasedDamagerRepairer(
				new TextAttribute(
					colorManager.getColor(stringColor)));
		reconciler.setDamager(ndr, EntityPartitionScanner.ENTITY_TXT);
		reconciler.setRepairer(ndr, EntityPartitionScanner.ENTITY_TXT);

		return reconciler;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		
		ContentAssistant assistant = new ContentAssistant();
		assistant.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));
		assistant.setContentAssistProcessor(new EntityCompletionProcessor(editor), IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(new EntityValueCompletionProcessor(editor), EntityPartitionScanner.ENTITY_STRING_VALUE);
		
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(IContentAssistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(colorManager.getColor(new RGB(150, 150, 0)));
		
		return assistant;
	}
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		if (store.getBoolean(PreferenceConstants.EDITOR_SHOW_HOVER_HELP)) {
			return new SinsHover(editor, sourceViewer);
		} else {
			return super.getTextHover(sourceViewer, contentType);
		}
	}

}