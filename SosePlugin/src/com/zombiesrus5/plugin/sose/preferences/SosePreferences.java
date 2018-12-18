package com.zombiesrus5.plugin.sose.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
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

public class SosePreferences
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SosePreferences() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Specify preferences:");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
//		addField(new DirectoryFieldEditor(PreferenceConstants.SINS_INSTALLATION_PATH, 
//				"&Sins Installation:", getFieldEditorParent()));
//		addField(new DirectoryFieldEditor(PreferenceConstants.VANILLA_REFERENCE_PATH, 
//				"&Vanilla Reference:", getFieldEditorParent()));
//		addField(new DirectoryFieldEditor(PreferenceConstants.ENTRENCHMENT_REFERENCE_PATH, 
//				"&Entrenchment Reference:", getFieldEditorParent()));
//		addField(new DirectoryFieldEditor(PreferenceConstants.DIPLOMACY_REFERENCE_PATH, 
//				"&Diplomacy Reference:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.REBELLION_INSTALLATION_PATH, 
				"&Rebellion Installation:", getFieldEditorParent()));
		addField(new DirectoryFieldEditor(PreferenceConstants.REBELLION_REFERENCE_PATH, 
				"&Rebellion Reference:", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.FULL_BUILD_ON_NEW_FILE,
				"&Perform Full Build on New File", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES, 
				"&Ignore Case on Reference Files", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.ALLOW_DECIMAL_POINT_ON_INTEGER, 
				"&Allow Decimal Point in Integer Values", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.REQUIRES_DECIMAL_POINT_ON_DECIMAL, 
				"&Require Decimal Point in Decimal Values", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.WARN_ENTITY_NOT_REFERENCED,
				"&Warn when Entity is not referenced from another entity", getFieldEditorParent()));
		addField(new RadioGroupFieldEditor(
				PreferenceConstants.STRICT_VERSION,
			"Strict Version Validation:",
			1,
			new String[][] { 
//				{"&Vanilla", PreferenceConstants.STRICT_VANILLA }, 
//				{"&Entrenchment", PreferenceConstants.STRICT_ENTRENCHMENT }, 
//				{"&Diplomacy", PreferenceConstants.STRICT_DIPLOMACY}, 
				{"&Rebellion", PreferenceConstants.STRICT_REBELLION}
		}, getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_RESEARCH_COSTS, 
				"&Validate Research Costs", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.VALIDATE_PARTICLES,
				"&Validate Particle Files", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}