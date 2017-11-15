/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */
package sose.tools.xml;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
/**
 * 
 * Abstract base class for defining handlers that serialize POJO's into
 * Documents.
 * 
 * 's
 * 
 * @author ZombiesRus5
 *  
 */
public abstract class XMLSerializerHandler implements XMLSerializerInterface {
    /**
     * 
     * 
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param dateTime
     *  
     */
    public void serializeDateTime(Node containingNode, String elementNamespace, String elementName, Calendar dateTime) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (dateTime != null) {
            //logger.log(LoggerSeverity.DEBUG, "serializeDateTime", elementName, dateTime.getTime());
            createElementNS(containingNode, elementNamespace, elementName, dateTimeFormat.format(dateTime.getTime()));
        } else {
            //logger.log(LoggerSeverity.DEBUG, "serializeDateTime", elementName, dateTime);
            createElementNS(containingNode, elementNamespace, elementName, null);
        }
    }
    /**
     * 
     * 
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param dateTime
     *  
     */
    public void serializeOptionalDateTime(Node containingNode, String elementNamespace, String elementName, Calendar dateTime) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (dateTime != null) {
            //logger.log(LoggerSeverity.DEBUG, "serializeDateTime", elementName, dateTime.getTime());
            createElementNS(containingNode, elementNamespace, elementName, dateTimeFormat.format(dateTime.getTime()));
        }
    }
    
    /**
     * 
     * 
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param date
     *  
     */
    public void serializeDate(Node containingNode, String elementNamespace, String elementName, Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            //logger.log(LoggerSeverity.DEBUG, "serializeDate", elementName, date.getTime());
            createElementNS(containingNode, elementNamespace, elementName, dateFormat.format(date.getTime()));
        } else {
            //logger.log(LoggerSeverity.DEBUG, "serializeDate", elementName, date);
            createElementNS(containingNode, elementNamespace, elementName, null);
        }
    }
    public void serializeDate(Node containingNode, String elementName, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            //logger.log(LoggerSeverity.DEBUG, "serializeDate", elementName, date.getTime());
            createElement(containingNode, elementName, dateFormat.format(date));
        } else {
            //logger.log(LoggerSeverity.DEBUG, "serializeDate", elementName, date);
            createElement(containingNode, elementName, null);
        }
    }
    /**
     * 
     * 
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param date
     *  
     */
    public void serializeOptionalDate(Node containingNode, String elementNamespace, String elementName, Calendar date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            //logger.log(LoggerSeverity.DEBUG, "serializeDate", elementName, date.getTime());
            createElementNS(containingNode, elementNamespace, elementName, dateFormat.format(date.getTime()));
        }
    }
    /*
     * 
     * (non-Javadoc)
     * 
     * 
     * 
     * @see com.sprint.eai.arch.xml.XMLSerializerInterface#serialize(java.lang.Object)
     *  
     */
    public Document serialize(Object objectToSerialize) throws XMLException {
        throw new RuntimeException("serialize(Object) not supported!");
    }
    /**
     * 
     * @param orderVoIPServiceRequest
     * 
     * @param string
     * 
     * @return  
     */
    protected Element createElementNS(Node containingNode, String elementNamespace, String elementName) {
        //logger.log(LoggerSeverity.DEBUG, "createElementNS", elementName);
        Element element = containingNode.getOwnerDocument().createElementNS(elementNamespace, elementName);
        containingNode.appendChild(element);
        return element;
    }
    /**
     * 
     * @param orderVoIPServiceRequest
     * 
     * @param string
     * 
     * @return  
     */
    protected Element createElement(Node containingNode, String elementName) {
        //logger.log(LoggerSeverity.DEBUG, "createElementNS", elementName);
        Element element = containingNode.getOwnerDocument().createElement(elementName);
        containingNode.appendChild(element);
        return element;
    }
    /**
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param elementValue
     *  
     */
    protected Element createElementNS(Node containingNode, String elementNamespace, String elementName, Object elementValue) {
        //logger.log(LoggerSeverity.DEBUG, "createElementNS", elementName, elementValue);
        Element element = createElementNS(containingNode, elementNamespace, elementName);
        if (elementValue != null) {
            Text elementText = containingNode.getOwnerDocument().createTextNode(String.valueOf(elementValue));
            element.appendChild(elementText);
        } else {
            element.setAttribute("xsi:nil", "true");
        }
        return element;
    }
    /**
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param elementValue
     *  
     */
    protected Element createElement(Node containingNode, String elementName, Object elementValue) {
        //logger.log(LoggerSeverity.DEBUG, "createElementNS", elementName, elementValue);
        Element element = createElement(containingNode, elementName);
        if (elementValue != null) {
            Text elementText = containingNode.getOwnerDocument().createTextNode(String.valueOf(elementValue));
            element.appendChild(elementText);
        } else {
            element.setAttribute("xsi:nil", "true");
        }
        return element;
    }
    /**
     * 
     * @param containingNode
     * 
     * @param elementNamespace
     * 
     * @param elementName
     * 
     * @param elementValue
     *  
     */
    protected Element createOptionalElementNS(Node containingNode, String elementNamespace, String elementName, Object elementValue) {
        //logger.log(LoggerSeverity.DEBUG, "createOptionalElementNS", elementName, elementValue);
        Element element = null;
        if (elementValue != null && String.valueOf(elementValue).length() > 0) {
            element = createElementNS(containingNode, elementNamespace, elementName);
            Text elementText = containingNode.getOwnerDocument().createTextNode(String.valueOf(elementValue));
            element.appendChild(elementText);
        }
        return element;
    }
}