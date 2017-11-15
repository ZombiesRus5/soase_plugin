package com.zombiesrus5.plugin.sose.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import soseplugin.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class TierPerLevelCostPreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public TierPerLevelCostPreferences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("A demonstration of a preference page implementation");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new IntegerFieldEditor(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 0 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 0 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 0 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 1 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 1 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 1 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 2 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 2 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 2 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 3 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 3 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 3 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 4 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 4 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 4 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 5 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 5 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 5 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 6 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 6 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 6 Crystal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 
				"&Tier 7 Credits:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 
				"&Tier 7 Metal:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 
				"&Tier 7 Crystal:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}