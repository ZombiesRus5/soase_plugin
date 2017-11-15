/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

package sose.tools.xml;

import java.io.IOException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Interface for defining handlers that deserialize Documents to POJO’s
 * 
 * @author ZombiesRus5
 *
 */
public interface XMLDeserializerInterface {
    public Object deserialize(String xml) throws XMLException, IOException, SAXException;
    public Object deserialize(Node node, XMLToolkit toolkit) throws XMLException;
}
