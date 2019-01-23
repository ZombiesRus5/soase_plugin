package com.zombiesrus5.plugin.sose.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import soseplugin.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.VALIDATE_PARTICLES, true);
		store.setDefault(PreferenceConstants.REBELLION_INSTALLATION_VALIDATE_PATH, true);
		store.setDefault(PreferenceConstants.REBELLION_REFERENCE_VALIDATE_PATH, false);
		store.setDefault(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES, true);
		store.setDefault(PreferenceConstants.REQUIRES_DECIMAL_POINT_ON_DECIMAL, false);
		store.setDefault(PreferenceConstants.ALLOW_DECIMAL_POINT_ON_INTEGER, true);
		store.setDefault(PreferenceConstants.WARN_ENTITY_NOT_REFERENCED, false);
		store.setDefault(PreferenceConstants.FULL_BUILD_ON_NEW_FILE, true);
		store.setDefault(PreferenceConstants.STRICT_VERSION, PreferenceConstants.STRICT_REBELLION);
		store.setDefault(PreferenceConstants.P_STRING,
				"Default value");
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 4);
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, true);
		store.setDefault(PreferenceConstants.EDITOR_SHOW_HOVER_HELP, true);
		store.setDefault(PreferenceConstants.REBELLION_INSTALLATION_PATH, "C:\\Program Files (x86)\\Steam\\steamapps\\common\\sins of a solar empire rebellion");
		
		store.setDefault(PreferenceConstants.VALIDATE_RESEARCH_COSTS, false);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 400);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 0);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 600);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 50);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 100);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 800);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 100);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 175);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1000);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 150);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 250);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1200);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 200);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 325);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1400);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 250);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 400);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1600);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 300);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 475);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS, 1800);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.METAL, 350);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL, 550);

		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 0);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS, 100);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL, 25);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL, 25);
		
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.BASEUPGRADETIME, 40);
		store.setDefault(PreferenceConstants.TIER0 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.BASEUPGRADETIME, 45);
		store.setDefault(PreferenceConstants.TIER1 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.BASEUPGRADETIME, 50);
		store.setDefault(PreferenceConstants.TIER2 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.BASEUPGRADETIME, 60);
		store.setDefault(PreferenceConstants.TIER3 + PreferenceConstants.PERLEVELUPGRADETIME, 5);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.BASEUPGRADETIME, 75);
		store.setDefault(PreferenceConstants.TIER4 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.BASEUPGRADETIME, 90);
		store.setDefault(PreferenceConstants.TIER5 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.BASEUPGRADETIME, 105);
		store.setDefault(PreferenceConstants.TIER6 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.BASEUPGRADETIME, 120);
		store.setDefault(PreferenceConstants.TIER7 + PreferenceConstants.PERLEVELUPGRADETIME, 10);
}

}
