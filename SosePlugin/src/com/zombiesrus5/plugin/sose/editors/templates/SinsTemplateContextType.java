package com.zombiesrus5.plugin.sose.editors.templates;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class SinsTemplateContextType extends TemplateContextType {
	/** This context's id */
	public static final String SINS_CONTEXT_TYPE= "com.zombiesrus5.plugin.sose.editors.templates.sins"; //$NON-NLS-1$

	/**
	 * Creates a new XML context type. 
	 */
	public SinsTemplateContextType() {
		addGlobalResolvers();
	}

	private void addGlobalResolvers() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
	}
}
