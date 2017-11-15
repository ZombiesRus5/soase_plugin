package com.zombiesrus5.plugin.sose.editors.utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

/**
 * Used to scan and detect for SQL keywords
 */
public class KeyWordDetector {
  String keyWord = "";
  String valuePart = null;
  public String getValuePart() {
	return valuePart;
}

public void setValuePart(String valuePart) {
	this.valuePart = valuePart;
}

int docOffset;

  /**
   * Method WordPartDetector.
   * 
   * @param viewer
   *          is a text viewer
   * @param documentOffset
   *          into the SQL document
   */
  public KeyWordDetector(ITextViewer viewer, int documentOffset) {
    try {
      IRegion region = viewer.getDocument().getLineInformationOfOffset(documentOffset);
      String lineContents = viewer.getDocument().get(region.getOffset(), region.getLength());
      keyWord = parseKeyWord(lineContents);
      
      int length = lineContents.length();
      char lastChar = lineContents.charAt(length-1);
      if (Character.isWhitespace(lastChar)) {
    	  // expecting a character
    	  valuePart = " ";
      } else {
    	  // a value may already be started...
    	  valuePart = parseValue(lineContents);
      }
      
    } catch (BadLocationException e) {
      // do nothing
    }
  }
  
  public boolean hasValuePart() {
	  return valuePart != null;
  }

  /**
   * Method getString.
   * 
   * @return String
   */
  public String getString() {
    return keyWord;
  }

  public int getOffset() {
    return docOffset;
  }

	public String parseKeyWord(String currentLine) {
		// find keyWord
		String keyWord = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			keyWord = currentLine.substring(0, index);
		} else {
			keyWord = currentLine;
		}
		return keyWord;
	}
	
	public String parseValue(String currentLine) {
		String value = null;
		currentLine = currentLine.trim();
		int index = -1;
		for (int i=0; i<currentLine.length(); i++) {
			char c = currentLine.charAt(i);
			if (Character.isWhitespace(c)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			value = currentLine.substring(index).trim();
		}
		if (value != null) {
			value = value.replace("\"", " ").trim();
		}
		return value;
	}
}
