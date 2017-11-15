/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

package sose.tools.xml;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @author ZombiesRus5
 */
public class XMLDeserializerHandler implements XMLDeserializerInterface {
    /**
     * http://www.w3.org/TR/xmlschema-2/#dateTime
     * 
     * The �lexical space� of dateTime consists of finite-length sequences of
     * characters of the form: '-'? yyyy '-' mm '-' dd 'T' hh ':' mm ':' ss ('.'
     * s+)? (zzzzzz)?,
     * 
     * The lexical representation of a timezone is a string of the form: (('+' |
     * '-') hh ':' mm) | 'Z',
     * 
     * This deserialize method currently handles the following cases
     * 
     * yyyy-mm-ddThh:mm:ssZ yyyy-mm-ddThh:mm:ss+hh:mm yyyy-mm-ddThh:mm:ss-hh:mm
     * 
     * @param node
     * @param toolkit
     * @return @throws
     *         XMLException
     */
    public Calendar deserializeDateTime(Node node, XMLToolkit toolkit) throws XMLException {
        Calendar result = null;
        final int TIMEZONE_OFFSET_LENGTH = 6;
        TimeZone gmt = TimeZone.getTimeZone("GMT");
        try {
            String dateTimeValue = toolkit.getValueOfElement(node);
            if (dateTimeValue != null) {
                String format = "yyyy-MM-dd'T'HH:mm:ss";
                
                // determine if milliseconds provided...
                int periodIndex = dateTimeValue.indexOf(".");
                if (periodIndex != -1) {
                    String afterTime = dateTimeValue.substring(periodIndex);
                    StringTokenizer stk = new StringTokenizer(afterTime, ".Z+-", false);
                
                    String millis = stk.nextToken();
                    format += ".";
                    for (int i=0; i<millis.length(); i++) {
                        format += "S";
                    }
                }
                // determine if timezone offset provided...
                char checkForTimezoneOffset = dateTimeValue.charAt(dateTimeValue.length() - TIMEZONE_OFFSET_LENGTH);
                if (checkForTimezoneOffset == '-' || checkForTimezoneOffset == '+') {
                    format += "z";
                }
                
                // determine if GMT offset. Ensures timezone is set to GMT when parsing...
                if (dateTimeValue.indexOf("Z") != -1) {
                    // xsd:dateTime is already in GMT
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(format + "'Z'");
                    dateTimeFormat.setTimeZone(gmt);
                    //logger.debug(dateTimeValue);
                    result = new GregorianCalendar();
                    Date date = dateTimeFormat.parse(dateTimeValue);
                    result.setTime(date);
                    result.setTimeZone(gmt);
                } else {
                    // need to correct timezone for simple date parsing
                    SimpleDateFormat dateTimeFormat = new SimpleDateFormat(format);
                    //logger.debug(dateTimeValue);
                    int lastColon = dateTimeValue.lastIndexOf(":");
                    dateTimeValue = dateTimeValue.substring(0, lastColon) + dateTimeValue.substring(lastColon + 1);
                    //logger.debug(dateTimeValue);
                    result = new GregorianCalendar();
                    Date date = dateTimeFormat.parse(dateTimeValue);
                    result.setTime(date);
                }
            }
        } catch (java.text.ParseException e) {
            throw new XMLException(e);
        }
        return result;
    }

    /**
     * 
     * @return
     */
    public Calendar deserializeDate(Node node, XMLToolkit toolkit) throws XMLException {
        Calendar cal = null;

        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateTimeValue = toolkit.getValueOfElement(node);
            if (dateTimeValue != null) {
                Date dt = null;
                dt = dateTimeFormat.parse(dateTimeValue);
                cal = new GregorianCalendar();
                cal.setTime(dt);
                // we are not setting time zone here because we do not want to inadvertantly
                // change the date if received around the date change time of midnight.
                //logger.log(LoggerSeverity.DEBUG, "Converted Date:", cal.getTime());
            }
        } catch (java.text.ParseException e) {
            throw new XMLException(e);
        }

        return cal;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sprint.eai.arch.xml.XMLDeserializerInterface#deserialize(java.lang.String)
     */
    public Object deserialize(String xml) throws XMLException, IOException, SAXException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sprint.eai.arch.xml.XMLDeserializerInterface#deserialize(org.w3c.dom.Node,
     *      com.sprint.eai.arch.xml.XMLToolkit)
     */
    public Object deserialize(Node node, XMLToolkit toolkit) throws XMLException {
    
        return null;
    } 
    /**
     * 
     * @param valueOfElement
     * @return
     */
    
     /**
     * 
     * @param node
     * @param toolkit
     * @return
     */
   public BigInteger deserializeBigInteger(Node node, XMLToolkit toolkit) {
       
       BigDecimal decimal = null;
       BigInteger integer = null;
       String valueOfElement= toolkit.getValueOfElement(node);
       if (valueOfElement != null) {
           decimal = new BigDecimal(valueOfElement);
           integer = decimal.toBigInteger();
       }
       return integer;
    
   }
       
   }