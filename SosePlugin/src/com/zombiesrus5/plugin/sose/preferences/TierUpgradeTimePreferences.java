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

public class TierUpgradeTimePreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public TierUpgradeTimePreferences() {
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
		addField(new IntegerFieldEditor(PreferenceConstants.TIER0 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 0 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 0 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER1 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 1 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 1 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER2 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 2 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 2 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER3 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 3 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 3 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER4 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 4 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 4 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER5 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 5 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 5 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER6 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 6 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 6 Per Level Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER7 + PreferenceConstants.BASEUPGRADETIME, 
				"&Tier 7 Upgrade Time:", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVELUPGRADETIME, 
				"&Tier 7 Per Level Upgrade Time:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}