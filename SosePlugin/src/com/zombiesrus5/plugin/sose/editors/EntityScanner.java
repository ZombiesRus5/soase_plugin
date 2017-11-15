package com.zombiesrus5.plugin.sose.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.swt.graphics.RGB;

import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntityScanner extends RuleBasedScanner {

	public EntityScanner(ColorManager manager) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_NUMBER);
		RGB booleanColor = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_BOOLEAN);

		IToken numberToken =
			new Token(
				new TextAttribute(manager.getColor(color)));
		IToken booleanToken =
			new Token(
				new TextAttribute(manager.getColor(booleanColor)));

		IRule[] rules = new IRule[4];
		//Add rule for processing instructions
		rules[0] = new NumberRule(numberToken);
		rules[1] = new EndOfLineRule("TRUE", booleanToken);
		rules[2] = new EndOfLineRule("FALSE", booleanToken);
		// Add generic whitespace rule.
		rules[3] = new WhitespaceRule(new EntityWhitespaceDetector());

		setRules(rules);
	}
}
