package com.zombiesrus5.plugin.sose.editors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.part.FileEditorInput;

import sose.tools.EntityParser;
import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;
import com.zombiesrus5.plugin.sose.editors.utils.KeyWordCollector;
import com.zombiesrus5.plugin.sose.editors.utils.KeyWordDetector;
import com.zombiesrus5.plugin.sose.editors.utils.WordPartDetector;
import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;
import com.zombiesrus5.plugin.sose.views.EntityDefinitionView;

public class EntityCompletionProcessor implements IContentAssistProcessor {
	private Map<String, String> cachedProposals = new HashMap<String, String>();
	
	public EntityCompletionProcessor(EntityEditor editor) {
		super();
		
		this.editor = editor;
		
	}

	EntityEditor editor = null;
	
	/**
	 * Simple content assist tip closer. The tip is valid in a range
	 * of 5 characters around its popup location.
	 */
	protected static class Validator implements IContextInformationValidator, IContextInformationPresenter {

		protected int fInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation, ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer, int offset) {
			fInstallOffset= offset;
		}
		
		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}

	protected IContextInformationValidator fValidator= new Validator();

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		WordPartDetector wordPart = new WordPartDetector(viewer, documentOffset);
		
		ICompletionProposal[] result= null;
		
		String[] possibleProposals = getPossibleProposals(viewer, documentOffset);
		List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();
		for (int i= 0; i < possibleProposals.length; i++) {
			if (possibleProposals[i].startsWith(wordPart.getString())) {
				IContextInformation info= new ContextInformation(possibleProposals[i], MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.Proposal.ContextInfo.pattern"), new Object[] { possibleProposals[i] })); //$NON-NLS-1$
				String replacementProposal = possibleProposals[i].substring(wordPart.getString().length());
				//System.out.println(replacementProposal);
				CompletionProposal proposal = new CompletionProposal(replacementProposal, documentOffset, 0, replacementProposal.length(), null, possibleProposals[i], info, /*MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] { possibleProposals[i]}) */ null); //$NON-NLS-1$
				proposals.add(proposal);

				if (cachedProposals.containsKey(possibleProposals[i])) {
					replacementProposal = cachedProposals.get(possibleProposals[i]);
				} else {
					InputStream is = getClass().getResourceAsStream("/proposals/" + possibleProposals[i] + ".proposal");
					if (is != null) {
						Scanner s = new Scanner(is).useDelimiter("\\A");
						replacementProposal += "\n";
						replacementProposal += s.hasNext() ? s.next() : replacementProposal;
						proposal = new CompletionProposal(replacementProposal, documentOffset, 0, replacementProposal.length(), null, possibleProposals[i] + " (Template)", info, /*MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] { possibleProposals[i]}) */ null); //$NON-NLS-1$
						proposals.add(proposal);
					} else {
						// check for custom proposal
						IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						String customPath = store.getString(PreferenceConstants.CUSTOM_PROPOSAL_PATH);
						if (customPath != null) {
							try {
								InputStream customIS = new FileInputStream(customPath + "/" + possibleProposals[i] + ".proposal");
								if (customIS != null) {
									Scanner s = new Scanner(customIS).useDelimiter("\\A");
									replacementProposal += s.hasNext() ? s.next() : replacementProposal;
									proposal = new CompletionProposal(replacementProposal, documentOffset, 0, replacementProposal.length(), null, possibleProposals[i] + " (Template)", info, /*MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.Proposal.hoverinfo.pattern"), new Object[] { possibleProposals[i]}) */ null); //$NON-NLS-1$
									proposals.add(proposal);
								}
							} catch (FileNotFoundException e) {
								// ignore
							}
							
						}
						
					}
				}
			}
		}
		result = new ICompletionProposal[proposals.size()];
		proposals.toArray(result);
		return result;
	}
	
	private String[] getPossibleProposals(ITextViewer viewer, int documentOffset) {
		String[] keyWords = {""};

		try {
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
		
			KeyWordCollector collector = new KeyWordCollector();
			parser.processDefinition(entityType, collector, textType);
			keyWords = new String[collector.getKeyWordSet().size()];
			collector.getKeyWordSet().toArray(keyWords);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keyWords;
	}

	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		IContextInformation[] result= new IContextInformation[5];
		for (int i= 0; i < result.length; i++)
			result[i]= new ContextInformation(
				MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.ContextInfo.display.pattern"), new Object[] { new Integer(i), new Integer(documentOffset) }),  //$NON-NLS-1$
				MessageFormat.format(EntityEditorMessages.getString("CompletionProcessor.ContextInfo.value.pattern"), new Object[] { new Integer(i), new Integer(documentOffset - 5), new Integer(documentOffset + 5)})); //$NON-NLS-1$
		return result;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '.', '(' };
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#' };
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public IContextInformationValidator getContextInformationValidator() {
		return fValidator;
	}
	
	/* (non-Javadoc)
	 * Method declared on IContentAssistProcessor
	 */
	public String getErrorMessage() {
		return null;
	}

}
