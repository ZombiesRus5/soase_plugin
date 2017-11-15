/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
package sose.tools.xml;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

/**
 * XMLToolkit provides a useful utility for creating and managing XML Documents.
 * 
 * @author ZombiesRus5
 * @history 1/31/2005 ZombiesRus5 Enhanced getDefaultToolkit to set a default LocalEntityResolver.
 */
public abstract class XMLToolkit {
	private static Object lock = new Object();

	private static Map registry;

	private static XMLToolkit toolkit = null;

	/**
	 * 
	 * @return LTD.ACM.Xml.XMLToolkit
	 */
	public static XMLToolkit getDefaultToolkit() {
		// double lock avoids the overhead of synchronization if not necessary
		if (toolkit == null) {
			synchronized (lock) {
				if (toolkit == null) {
					String toolkitClassName = System
							.getProperty(XMLToolkit.class.getName());
					if (toolkitClassName == null) {
						toolkitClassName = JavaxXmlToolkit.class.getName();
					}
					try {
						Class toolkitClass = Class.forName(toolkitClassName);
						toolkit = (XMLToolkit) getRegistry().get(toolkitClass);
						if (toolkit == null) {
							// attempt to allocate a toolkit if it is not
							// registered
							toolkit = (XMLToolkit) Class.forName(
									toolkitClassName).newInstance();
							toolkit
									.setEntityResolver(new LocalEntityResolver());
							register(toolkit);
						}
					} catch (Exception e) {
						e.printStackTrace(System.err);
					}
				}
			}
		}
		return toolkit;
	}
	
	public static XMLToolkit createXMLToolkit() {
	    XMLToolkit newToolkit = null;
	    
		String toolkitClassName = System
				.getProperty(XMLToolkit.class.getName());
		if (toolkitClassName == null) {
			toolkitClassName = JavaxXmlToolkit.class.getName();
		}
		try {
			Class toolkitClass = Class.forName(toolkitClassName);
			newToolkit = (XMLToolkit) Class.forName(
						toolkitClassName).newInstance();
			newToolkit.setEntityResolver(new LocalEntityResolver());
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return newToolkit;
	}

	/**
	 * 
	 * @return java.util.Dictionary
	 */
	private static Map getRegistry() {
		synchronized (lock) {
			if (registry == null) {
				registry = new HashMap();
			}
		}
		return registry;
	}

	/**
	 * 
	 * @return LTD.ACM.Xml.XMLToolkit
	 * @param aNode
	 *            org.w3c.dom.Node
	 */
	public static XMLToolkit getXMLToolkitFor(Node aNode) {
		Iterator itr = getRegistry().values().iterator();
		while (itr.hasNext()) {
			XMLToolkit toolkit = (XMLToolkit) itr.next();
			if (toolkit.isToolkitFor(aNode)) {
				return toolkit;
			}
		}
		return null;
	}

	/**
	 * Starts the application.
	 * 
	 * @param args
	 *            an array of command-line arguments
	 */
	public static void main(java.lang.String[] args) throws Exception {
		XMLToolkit xmlkit = XMLToolkit.getDefaultToolkit();

		Document doc = xmlkit.parse("c:\\temp\\xml\\personal.xml");

		Writer writer = new OutputStreamWriter(System.out);

		xmlkit = XMLToolkit.getXMLToolkitFor(doc);

		if (xmlkit != null) {
			//System.out.println("Toolkit found!");
			xmlkit.print(writer, doc);
		} else {
			System.err.println("error");
		}

		doc = xmlkit.createDocument();

		doc.appendChild(doc.createElement("root"));
		Element root = doc.getDocumentElement();
		root.appendChild(doc.createTextNode("Sample Text"));
		root.setAttribute("attr1", "attr2");
		Element elem = doc.createElement("elem1");
		elem.setAttribute("attr2", "value2");
		elem.appendChild(doc.createCDATASection("Sample CDATA"));
		root.appendChild(elem);

		xmlkit.print(writer, doc);
	}

	/**
	 * 
	 * @param aToolkit
	 *            LTD.ACM.Xml.XMLToolkit
	 */
	protected static void register(XMLToolkit aToolkit) {
		getRegistry().put(aToolkit.getClass(), aToolkit);
	}

	/**
	 * 
	 * @param newValue
	 *            LTD.ACM.Xml.XMLToolkit
	 */
	public static void setDefaultToolkit(XMLToolkit newValue) {
		XMLToolkit.toolkit = newValue;
	}

	/**
	 * 
	 * @param newValue
	 *            LTD.ACM.Xml.XMLToolkit
	 */
	private static void setToolkit(XMLToolkit newValue) {
		XMLToolkit.toolkit = newValue;
	}

	private EntityResolver entityResolver;

	//protected Tracer tracer = Tracer.getTracerFor(this);

	private URIResolver uriResolver;

	/**
	 * XMLToolkit constructor comment.
	 */
	protected XMLToolkit() {
		super();
	}
	
	public void setNonValidatingFeature(String feature, boolean onOff) {
	    throw new RuntimeException("Not supported!");
	}
	
	public void setValidatingFeature(String feature, boolean onOff){
	    throw new RuntimeException("Not supported!");
	}
	
	/**
	 * @param element
	 * @param simplePath
	 * @throws XMLException
	 */
	public Element appendElement(Node node, String simplePath)
			throws XMLException {
		StringTokenizer stk = new StringTokenizer(simplePath, "/", false);
		Document doc = null;
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			doc = (Document) node;
		} else {
			doc = node.getOwnerDocument();
		}
		Node currentNode = node;
		while (stk.hasMoreTokens()) {
			String elementName = stk.nextToken();
			Element tempElement = getElement(currentNode, elementName);
			if (null == tempElement || stk.hasMoreTokens() == false) {
				// append a new child if one does not exist or we are at the
				// last element.
				currentNode = (Element) currentNode.appendChild(doc
						.createElement(elementName));
			} else {
				currentNode = tempElement;
			}
		}
		return (Element) currentNode;
	}

	/**
	 * @param element
	 * @param simplePath
	 * @param value
	 * @throws XMLException
	 */
	public Element appendElementWithValue(Node node, String simplePath,
			String value) throws XMLException {
		Element target = appendElement(node, simplePath);

		target.appendChild(target.getOwnerDocument().createTextNode(value));
		return target;
	}

	/**
	 * 
	 * @return org.w3c.dom.Document
	 */
	public abstract Document createDocument() throws XMLException;

	/**
	 * Abstract method to create a Document initialized with doc type, xml
	 * version, stand alone (yes|no), a public string (PUBLIC | SYSTEM), and a
	 * url string representing the dtd file location.
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
	public abstract Document createDocument(String docType, String version,
			String standAlone, String publicString, String dtdLocation) throws XMLException;

	/**
	 * @param element
	 * @param simplePath
	 * @throws XMLException
	 */
	public Element createElement(Node node, String simplePath)
			throws XMLException {
		StringTokenizer stk = new StringTokenizer(simplePath, "/", false);
		Document doc = null;
		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			doc = (Document) node;
		} else {
			doc = node.getOwnerDocument();
		}
		Node currentNode = node;
		while (stk.hasMoreTokens()) {
			String elementName = stk.nextToken();
			Element tempElement = getElement(currentNode, elementName);
			if (null == tempElement) {
				currentNode = (Element) currentNode.appendChild(doc
						.createElement(elementName));
			} else {
				currentNode = tempElement;
			}
		}
		return (Element) currentNode;
	}

	/**
	 * Return any direct children of the given parentNode that are ELEMENT_NODE
	 * type.
	 * 
	 * @return java.util.Vector
	 * @param parentNode
	 *            Node
	 */
	public Iterator<Element> getChildElements(Node parentNode) {
		List<Element> list = new ArrayList<Element>();
		Node node = null;
		NodeListIterator nodelist = new NodeListIterator(parentNode
				.getChildNodes());

		while (nodelist.hasNext() == true) {
			node = (Node) nodelist.next();
			if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
				list.add((Element)node);
			}
		}
		return list.iterator();
	}

	/**
	 * this method returns the element defined by location off of node.
	 * 
	 * @return org.w3c.dom.Element
	 * @param node
	 *            org.w3c.dom.Node
	 * @param location
	 *            java.lang.String
	 * @throws XMLException
	 */
	public Element getElement(Node node, String location) throws XMLException {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile(location);
			return (Element) expr.evaluate(node, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * 
	 * @return org.w3c.dom.Element
	 * @param path
	 *            LTD.ACM.Util.QualifiedPath
	 * @throws XMLException
	 */
	public Iterator<Node> getElements(Node currentNode, String path)
			throws XMLException {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile(path);

			NodeList elements = (NodeList)expr.evaluate(currentNode, XPathConstants.NODESET);

			return new NodeListIterator(elements);
		} catch (XPathExpressionException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * 
	 * @return org.w3c.dom.Element
	 * @param path
	 *            LTD.ACM.Util.QualifiedPath
	 * @throws XMLException
	 */
	public NodeList getElementList(Node currentNode, String path)
			throws XMLException {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile(path);

			NodeList elements = (NodeList)expr.evaluate(currentNode, XPathConstants.NODESET);

			return elements;
		} catch (XPathExpressionException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Insert the method's description here.
	 * 
	 * @return org.xml.sax.EntityResolver
	 */
	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	/**
	 * @param currentNode
	 * @param path
	 * @return @throws
	 *         XMLException
	 */
	public NodeList getNodeList(Node currentNode, String path)
			throws XMLException {
		return getElementList(currentNode, path);

	}

	/**
	 * 
	 * @return javax.xml.transform.TransformerFactory
	 */
	public TransformerFactory getTransformerFactory() {
		TransformerFactory cybertron = TransformerFactory.newInstance();
		cybertron.setURIResolver(getURIResolver());
		return cybertron;
	}

	/**
	 * 
	 * @return javax.xml.transform.URIResolver
	 */
	public URIResolver getURIResolver() {
		return uriResolver;
	}

	/**
	 * @param node
	 * @return
	 */
	public String getValueOfElement(Node node) {
		String value = null;
		org.w3c.dom.Node aNode = null;
		org.w3c.dom.NodeList nodes = null;

		//
		StringBuffer sb = new StringBuffer();
		if (node == null) {
			return null;
		}
		nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			aNode = nodes.item(i);
			value = aNode.getNodeValue();
			if (value != null) {
				sb.append(aNode.getNodeValue());
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @return org.w3c.dom.Element
	 * @param path
	 *            LTD.ACM.Util.QualifiedPath
	 * @throws TransformerException
	 */
	public String getValueOfElement(Node node, String path) throws XMLException {

		Node element = getElement(node, path);
		return getValueOfElement(element);
	}

	/**
	 * Returns the collection of values for a repeating element.
	 * 
	 * @return org.w3c.dom.Element
	 * @param path
	 *            LTD.ACM.Util.QualifiedPath
	 * @throws TransformerException
	 */
	public List getValuesOfElements(Node node, String path)
			throws XMLException {

		Iterator elements = getElements(node, path);
		List values = new ArrayList();
		while (elements.hasNext()) {
			Element element = (Element) elements.next();
			String value = getValueOfElement(element);
			values.add(value);
		}
		return values;
	}

	/**
	 * 
	 * @return boolean
	 * @param aNode
	 *            org.w3c.dom.Node
	 */
	public abstract boolean isToolkitFor(Node aNode);

	/**
	 * 
	 * @return org.w3c.dom.Document
	 * @param input
	 *            java.io.Reader
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(Reader input)
			throws IOException, SAXException, XMLException;

	/**
	 * 
	 * @return org.w3c.dom.Document
	 * @param input
	 *            java.io.Reader
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(Reader input, boolean validate)
			throws IOException, SAXException, XMLException;

	/**
	 * ticket 60660
	 * @param input
	 * @param validate
	 * @param xmlProperties
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(Reader input, boolean validate,
			Map xmlProperties) throws IOException, SAXException, XMLException;

	/**
	 * Parse an XML document from a system identifier (URI).
	 * 
	 * @return org.w3c.dom.Document
	 * @param systemId
	 *            java.lang.String
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(String systemId)
			throws IOException, SAXException, XMLException;

	/**
	 * Parse an XML document from a system identifier (URI).
	 * 
	 * @return org.w3c.dom.Document
	 * @param systemId
	 *            java.lang.String
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(String systemId, boolean validate)
			throws IOException, SAXException, XMLException;

	/**
	 * ticket 60660
	 * @param systemId
	 * @param validate
	 * @param xmlProperties
	 * @return @throws
	 *         IOException
	 * @throws SAXException
	 * @throws XMLException 
	 */
	public abstract org.w3c.dom.Document parse(String systemId,
			boolean validate, Map xmlProperties) throws IOException,
			SAXException, XMLException;
	
	/**
	 * 
	 * @param writer
	 *            java.io.Writer
	 * @param doc
	 *            org.w3c.dom.Document
	 * 
	 * @history c6910 02/02/2004 JRL Added print(Writer, Element)
	 */
	public abstract void print(Writer writer, Document doc) throws IOException;

	/**
	 * 
	 * @param writer
	 *            java.io.Writer
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	public abstract void print(Writer writer, Element elem) throws IOException;

	/**
	 * 
	 * @param writer
	 *            java.io.Writer
	 * @param doc
	 *            org.w3c.dom.Document
	 */
	public abstract void printWithoutFormat(Writer writer, Document doc)
			throws IOException;

	/**
	 * Insert the method's description here.
	 * 
	 * @param newEntityResolver
	 *            org.xml.sax.EntityResolver
	 */
	public void setEntityResolver(EntityResolver newEntityResolver) {
		entityResolver = newEntityResolver;
	}

	/**
	 * 
	 * @param newUriResolver
	 *            javax.xml.transform.URIResolver
	 */
	public void setURIResolver(URIResolver newUriResolver) {
		uriResolver = newUriResolver;
	}

	/**
	 * @param element
	 * @param simplePath
	 * @param value
	 * @throws XMLException
	 */
	public void setValueOfElement(Node node, String simplePath, String value)
			throws XMLException {

		if (value != null) {
			Element target = createElement(node, simplePath);

			target.appendChild(target.getOwnerDocument().createTextNode(value));
		}
	}

	/**
	 * Returns a String that represents the value of this object.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or
		// supplement this.
		return super.toString();
	}

	/**
	 * 
	 * 
	 * @return java.lang.String
	 * @param document
	 *            org.w3c.dom.Document
	 */
	public String toString(Document document) {
		StringWriter sw = new StringWriter();
		try {
			print(sw, document);
		} catch (IOException e) {
			// shouldn't ever happen since we are writing to a string.
			e.printStackTrace();
		}
		return sw.toString();
	}
}