package com.zombiesrus5.plugin.sose.editors.utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;

/**
 * Used to scan and detect for SQL keywords
 */
public class WordPartDetector {
  String wordPart = "";
  int docOffset;

  /**
   * Method WordPartDetector.
   * 
   * @param viewer
   *          is a text viewer
   * @param documentOffset
   *          into the SQL document
   */
  public WordPartDetector(ITextViewer viewer, int documentOffset) {
    docOffset = documentOffset - 1;
    try {
      while (((docOffset) >= viewer.getTopIndexStartOffset())
          && isWordPart(viewer)) {
        docOffset--;
      }
      //we've been one step too far : increase the offset
      docOffset++;
      wordPart = viewer.getDocument().get(docOffset,
          documentOffset - docOffset);
    } catch (BadLocationException e) {
      // do nothing
    }
  }

private boolean isWordPart(ITextViewer viewer) throws BadLocationException {
	return Character.isLetterOrDigit(viewer.getDocument()
	      .getChar(docOffset)) || 
	      Character.isJavaIdentifierPart(viewer.getDocument()
	    	      .getChar(docOffset)) ||
	    	      ':' == viewer.getDocument().getChar(docOffset);
}

  /**
   * Method getString.
   * 
   * @return String
   */
  public String getString() {
    return wordPart;
  }

  public int getOffset() {
    return docOffset;
  }

}
