package sose.tools.xml;
/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

/**
 * This type was created in VisualAge.
 */
public class ErrorHandler implements org.xml.sax.ErrorHandler {
/**
 * Handler constructor comment.
 */
public ErrorHandler() {
	super();
}
/**
 * error method comment.
 */
 
public void error(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
	
	throw exception;
}
/**
 * fatalError method comment.
 */

public void fatalError(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
	
	throw exception;
}
/**
 * warning method comment.
 */
public void warning(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
	//System.out.println("Trapped warning");
}
}
