package sose.tools.xml;
/*
 * @author ZombiesRus5 
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Hashtable;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * Class used to resolve entity references and cache the results to minimize subsequent calls to resolve the
 * same entity reference.
 * @author: ZombiesRus5 (2/18/03 9:31:16 AM)
 */
public class CachingEntityResolver implements EntityResolver {
	private org.xml.sax.EntityResolver baseResolver;
	private Hashtable cache;
/**
 * CachingEntityResolver constructor shouldn't be used as the behavior of this class relys on it
 * being instantiated with another EntityResolver
 */
public CachingEntityResolver() {
	super();
}
/**
 * CachingEntityResolver constructor initializes cache and baseResolver. The base resolver is assumed to
 * return an InputSource containing a byteStream.
 */
public CachingEntityResolver(EntityResolver resolver) {
	super();
	baseResolver = resolver;
	cache = new Hashtable();
}
 /**
     * Allow the application to resolve external entities.
     *
     * <p>The Parser will call this method before opening any external
     * entity except the top-level document entity (including the
     * external DTD subset, external entities referenced within the
     * DTD, and external entities referenced within the document
     * element): the application may request that the parser resolve
     * the entity itself, that it use an alternative URI, or that it
     * use an entirely different input source.</p>
     *
     * <p>Application writers can use this method to redirect external
     * system identifiers to secure and/or local URIs, to look up
     * public identifiers in a catalogue, or to read an entity from a
     * database or other input source (including, for example, a dialog
     * box).</p>
     *
     * <p>If the system identifier is a URL, the SAX parser must
     * resolve it fully before reporting it to the application.</p>
     *
     * @param publicId The public identifier of the external entity
     *        being referenced, or null if none was supplied.
     * @param systemId The system identifier of the external entity
     *        being referenced.
     * @return An InputSource object describing the new input source,
     *         or null to request that the parser open a regular
     *         URI connection to the system identifier.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException A Java-specific IO exception,
     *            possibly the result of creating a new InputStream
     *            or Reader for the InputSource.
     * @see org.xml.sax.InputSource
     */
public InputSource resolveEntity(String publicId, String systemId) throws java.io.IOException, SAXException {
	InputSource source = null;
	StringReader stringReader = null;
	String entityString = null;
	
	if(cache.containsKey(systemId)){
		entityString = (String)cache.get(systemId);
		stringReader = new StringReader(entityString);
		source = new InputSource(stringReader);
	}
	else {
		InputSource temp = baseResolver.resolveEntity(publicId, systemId);
		InputStream byteStream = temp.getByteStream();
		if(byteStream == null) {
			source = temp;
		}
		else {
			StringBuffer buffer = new StringBuffer();
			InputStreamReader reader = new InputStreamReader(byteStream);
			int c = -1;
			 while ((c = reader.read()) != -1) {
                buffer.append((char) c);
            }
            reader.close();
           
			entityString = buffer.toString().trim();
			cache.put(systemId, entityString);
			stringReader = new StringReader(entityString);
			source = new InputSource(stringReader);
		}
	}
	source.setSystemId(systemId);
	return source;
}
}
