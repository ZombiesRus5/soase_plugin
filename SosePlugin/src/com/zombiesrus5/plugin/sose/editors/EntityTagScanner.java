package com.zombiesrus5.plugin.sose.editors;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.swt.graphics.RGB;

import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntityTagScanner extends RuleBasedScanner {

	public EntityTagScanner(ColorManager manager) {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		RGB color = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_STRING);
		RGB numberColor = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_NUMBER);
		RGB booleanColor = PreferenceConverter.getColor(store, PreferenceConstants.EDITOR_COLOR_BOOLEAN);

		IToken string =
			new Token(
				new TextAttribute(manager.getColor(color)));

		IToken numberToken =
			new Token(
				new TextAttribute(manager.getColor(numberColor)));
		IToken booleanToken =
			new Token(
				new TextAttribute(manager.getColor(booleanColor)));

		//Add rule for processing instructions
		IRule[] rules = new IRule[6];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string);
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string);
		// Add generic whitespace rule.
		rules[2] = new NumberRule(numberToken);
		rules[3] = new EndOfLineRule("TRUE", booleanToken);
		rules[4] = new EndOfLineRule("FALSE", booleanToken);
		rules[5] = new WhitespaceRule(new EntityWhitespaceDetector());

		setRules(rules);
	}
}
