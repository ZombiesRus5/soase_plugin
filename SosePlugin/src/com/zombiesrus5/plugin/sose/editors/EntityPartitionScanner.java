package com.zombiesrus5.plugin.sose.editors;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import sose.tools.EntityParser;
import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.editors.utils.KeyWordCollector;
import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntityPartitionScanner extends RuleBasedPartitionScanner {
	public final static String ENTITY_KEY_WORD = "__entity_keyword";
	public final static String ENTITY_TXT = "__entity_txt";
	public final static String ENTITY_STRING_VALUE = "__entity_comment";
	IToken entityValue = new Token(ENTITY_STRING_VALUE);
	IToken keyWord = new Token(IDocument.DEFAULT_CONTENT_TYPE);

	//	public final static String ENTITY_TAG = "__entity_tag";
//	public final static String ENTITY_NUMBER = "__entity_number";

	public class ValueRule extends SingleLineRule {

		public ValueRule(String startSequence, String endSequence, IToken token,
				char escapeCharacter, boolean breaksOnEOF,
				boolean escapeContinuesLine) {
			super(startSequence, endSequence, token, escapeCharacter, breaksOnEOF,
					escapeContinuesLine);
			// TODO Auto-generated constructor stub
		}

		public ValueRule(String startSequence, String endSequence, IToken token,
				char escapeCharacter, boolean breaksOnEOF) {
			super(startSequence, endSequence, token, escapeCharacter, breaksOnEOF);
			// TODO Auto-generated constructor stub
		}

		public ValueRule(String startSequence, String endSequence, IToken token,
				char escapeCharacter) {
			super(startSequence, endSequence, token, escapeCharacter);
			// TODO Auto-generated constructor stub
		}

		public ValueRule(String startSequence, String endSequence, IToken token) {
			super(startSequence, endSequence, token);
			// TODO Auto-generated constructor stub
		}

		@Override
		public IToken evaluate(ICharacterScanner scanner) {
			IToken result= super.evaluate(scanner);
			if (result == fToken)
				return evaluateToken();
			return result;
		}
		
		private IToken evaluateToken() {
			try {
				final String token= fDocument.get(getTokenOffset(), getTokenLength()) + "."; //$NON-NLS-1$

				int offset= 0;
				char character= token.charAt(++offset);

				if (character == '/')
					character= token.charAt(++offset);

				while (Character.isWhitespace(character))
					character= token.charAt(++offset);

				while (Character.isLetterOrDigit(character))
					character= token.charAt(++offset);

				while (Character.isWhitespace(character))
					character= token.charAt(++offset);

				if (offset >= 2 && token.charAt(offset) == fEndSequence[0])
					return fToken;

			} catch (BadLocationException exception) {
				// Do nothing
			}
			return entityValue;
		}
	}

	public EntityPartitionScanner() {

//		IToken number = new Token(ENTITY_NUMBER);
//		IToken tag = new Token(ENTITY_TAG);

		IPredicateRule[] predicateRules = new IPredicateRule[2];

		predicateRules[0] = new SingleLineRule("\"", "\"", entityValue);
//		predicateRules[0] = new TagRule(tag);

//		predicateRules[1] = new SingleLineRule(" ", null, entityValue, '\\', true, true); 
//		predicateRules[2] = new SingleLineRule("\t", null, entityValue, '\\', true, true);

		predicateRules[1] = new ValueRule(" ", " ", keyWord); 
		//predicateRules[3] = new ValueRule("\t", " ", keyWord);
		//predicateRules[2] = new ValueRule("\t", "\t", keyWord, '\\', true, true);
		//predicateRules[4] = new ValueRule(" ", "\t", keyWord, '\\', true, true);
		
		setPredicateRules(predicateRules);
	}
}
