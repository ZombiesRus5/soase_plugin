/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
package sose.tools.xml;

/**
 * @author ZombiesRus5
 */
public class XMLException extends Exception {
    /**
     * @param message
     * @param cause
     */
    public XMLException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public XMLException(Throwable cause) {
        super(cause);
    }
/**
 * XMLException constructor comment.
 */
public XMLException() {
	super();
}
/**
 * XMLException constructor comment.
 * @param s java.lang.String
 */
public XMLException(String s) {
	super(s);
}
}
