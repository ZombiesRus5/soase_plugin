package sose.tools.xml;
/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author ZombiesRus5
 */
public class JavaxXmlToolkit extends XMLToolkit {
    protected Map nonValidatingFeatures = new HashMap();
    protected Map validatingFeatures = new HashMap();
    
	static {
		JavaxXmlToolkit xml = new JavaxXmlToolkit();
		xml.setEntityResolver(new LocalEntityResolver());
		XMLToolkit.register(xml);
	}
	
	public void setNonValidatingFeature(String feature, boolean onOff) {
	    nonValidatingFeatures.put(feature, new Boolean(onOff));
	}
	
	public void setValidatingFeature(String feature, boolean onOff) {
	    validatingFeatures.put(feature, new Boolean(onOff));
	}
	
	/**
	 * TXDOMToolkit constructor comment.
	 */
	public JavaxXmlToolkit() {
		super();
	}
	/**
	 * createDocument method comment.
	 */
	public Document createDocument() throws XMLException {
        // Create a builder factory
        DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setValidating(false);

        // parse our input
        Document doc;
		try {
			doc = factory.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
        
        return doc;
	}
	/**
	 * This method creates a TXDocument initialized with doc type, xml version,
	 * stand alone (yes|no), a public string (PUBLIC | SYSTEM), and a url string
	 * representing the dtd file location. This method also creates the root
	 * element based on the doc type.
	 * 
	 * @param docType
	 *            java.lang.String the name of the root element of the xml
	 *            document
	 * @param version
	 *            java.lang.String the xml version currently 1.0
	 * @param standAlone
	 *            java.lang.String no if the file uses external entities yes
	 *            otherwise.
	 * @param publicString
	 *            java.lang.String PUBLIC if the dtd is to be retrieved via http
	 *            SYSTEM if the dtd is on the local machine
	 * @param dtdLocation
	 *            java.lang.String
	 * @return org.w3c.dom.Document
	 */
	public Document createDocument(String docType, String version,
			String standAlone, String publicString, String dtdLocation) throws XMLException {
        throw null;
	}
	/**
	 * Insert the method's description here. Creation date: (8/3/01 1:49:04 PM)
	 * 
	 * @return com.ibm.xml.parsers.TXDOMParser
	 */
	public DocumentBuilderFactory getNonValidatingParser()
			throws org.xml.sax.SAXNotSupportedException,
			org.xml.sax.SAXNotRecognizedException {
        // Create a builder factory
        DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        return factory;
	}
	
	/**
	 * Insert the method's description here. Creation date: (8/3/01 1:49:04 PM)
	 * 
	 * @return com.ibm.xml.parsers.TXDOMParser
	 */
	public DocumentBuilderFactory getValidatingParser()
			throws org.xml.sax.SAXNotSupportedException,
			org.xml.sax.SAXNotRecognizedException {
        // Create a builder factory
        DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        return factory;
	}
	/**
	 * ticket 60660
	 * @param parser
	 * @param xmlProperties
	 * @throws org.xml.sax.SAXNotSupportedException
	 * @throws org.xml.sax.SAXNotRecognizedException
	 */
	private void setXMLProperties(DocumentBuilderFactory factory, Map xmlProperties)
			throws org.xml.sax.SAXNotSupportedException,
			org.xml.sax.SAXNotRecognizedException {
		for (Iterator iterator = xmlProperties.entrySet().iterator(); iterator
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			String key = (String) entry.getKey();
			Object value = (Object) entry.getValue();
			factory.setAttribute(key, value);
		}
	}
	/**
	 * isToolkitFor method comment.
	 */
	public boolean isToolkitFor(Node aNode) {
		return true;
//		return (aNode instanceof ????);
	}
	/**
	 * Starts the application.
	 * 
	 * @param args
	 *            an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) {
		// Insert code to start the application here.
	}
	/**
	 * parse method comment.
	 * @throws XMLException 
	 */
	public Document parse(Reader input) throws IOException, SAXException, XMLException {
		return parse(input, false, null);
	}
	/**
	 * parse method comment.
	 * @throws XMLException 
	 */
	public Document parse(Reader input, boolean validate) throws IOException,
			SAXException, XMLException {
		return parse(new org.xml.sax.InputSource(input), validate, null);
	}
	/**
	 * ticket 60660
	 * @throws XMLException 
	 */
	public Document parse(Reader input, boolean validate, Map xmlProperties)
			throws IOException, SAXException, XMLException {
		return parse(new org.xml.sax.InputSource(input), validate, xmlProperties);
	}
	/**
	 * parse method comment.
	 * @throws XMLException 
	 */
	public Document parse(String systemId) throws IOException, SAXException, XMLException {
		return parse(systemId, false);
	}
	/**
	 * parse method comment.
	 * @throws XMLException 
	 */
	public Document parse(String systemId, boolean validate)
			throws IOException, SAXException, XMLException {
		return parse(new org.xml.sax.InputSource(systemId), validate, null);
	}

	/**
	 *  ticket 60660
	 * @throws XMLException 
	 */
	public Document parse(String systemId, boolean validate, Map xmlProperties)
			throws IOException, SAXException, XMLException {
		return parse(new org.xml.sax.InputSource(systemId), validate,
				xmlProperties);
	}

	/**
	 * This method was created in VisualAge.
	 * 
	 * @return org.w3c.dom.Document
	 * @param input
	 *            org.xml.sax.InputSource
	 * @throws XMLException 
	 */
	public Document parse(org.xml.sax.InputSource input) throws IOException,
			SAXException, XMLException {
		return parse(input, false, null);
	}
	/**
	 * This method was created in VisualAge.
	 * 
	 * @return org.w3c.dom.Document
	 * @param input
	 *            org.xml.sax.InputSource
	 * @throws XMLException 
	 */
	public Document parse(org.xml.sax.InputSource input, boolean validate,
			Map xmlProperties) throws IOException, SAXException, XMLException {
		//tracer.traceDebug("start parsing");
		Document doc = null;
		try {
			DocumentBuilderFactory parser = null;
			if (validate == false) {
				parser = getNonValidatingParser();
				if (xmlProperties != null) {
				    setXMLProperties(parser, xmlProperties);
				}
			} else {
				parser = getValidatingParser();
				//ticket 60660
				if (xmlProperties != null) {
					setXMLProperties(parser, xmlProperties);
				}
			}
			doc = parser.newDocumentBuilder().parse(input);
		} catch (org.xml.sax.SAXException e) {
			throw e;
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
		//tracer.traceDebug("finished parsing");
		return doc;
	}
	/**
	 * print method comment.
	 */
	public void print(Writer writer, Document doc) throws IOException {
//		if (!(doc instanceof org.apache.xerces.dom.DocumentImpl)) {
//			throw new IllegalArgumentException("Illegal Document Type");
//		}
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer serializer = null;
		try {
			serializer = factory.newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            serializer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }

		
	}
	/**
	 * print method comment.
	 */
	public void print(Writer writer, Element elem) throws IOException {
//		if (!(elem instanceof org.apache.xerces.dom.ElementImpl)) {
//			throw new IllegalArgumentException("Illegal Element Type");
//		}

		writer.write(elem.getTextContent());
	}
	/**
	 * This method was created in VisualAge.
	 * 
	 * @param writer
	 *            java.io.Writer
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	public void printWithoutFormat(java.io.Writer writer,
			org.w3c.dom.Document doc) throws java.io.IOException {
//		if (!(doc instanceof org.apache.xerces.dom.DocumentImpl)) {
//			throw new IllegalArgumentException("Illegal Document Type");
//		}

		writer.write(doc.getTextContent());
	}
	/**
	 * Returns a String that represents the value of this object.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return super.toString();
	}
}