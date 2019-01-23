/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
package sose.tools.xml;

/**
 * @author ZombiesRus5
 */
public class XMLParseException extends XMLException {
    /**
     * @param message
     * @param cause
     */
    public XMLParseException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public XMLParseException(Throwable cause) {
        super(cause);
    }
/**
 * XMLParseException constructor comment.
 */
public XMLParseException() {
	super();
}
/**
 * XMLParseException constructor comment.
 * @param s java.lang.String
 */
public XMLParseException(String s) {
	super(s);
}
}
