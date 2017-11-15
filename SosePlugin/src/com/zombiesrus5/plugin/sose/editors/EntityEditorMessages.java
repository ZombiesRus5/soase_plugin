package com.zombiesrus5.plugin.sose.editors;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class EntityEditorMessages {

	private static final String RESOURCE_BUNDLE= "com.zombiesrus5.plugin.sose.editors.EntityEditorMessages";//$NON-NLS-1$

	private static ResourceBundle fgResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private EntityEditorMessages() {
	}

	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";//$NON-NLS-2$ //$NON-NLS-1$
		}
	}
}
