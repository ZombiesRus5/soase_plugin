/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
package sose.tools.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Interface for defining handlers that serialize POJO’s into Documents.
 * 
 * @author ZombiesRus5
 *
 */
public interface XMLSerializerInterface {
    public Document serialize(Object objectToSerialize) throws XMLException;
    public void serialize(Element containingElement, Object objectToSerialize);
}
