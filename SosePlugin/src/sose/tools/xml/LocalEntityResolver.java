package sose.tools.xml;
/*
 * @author ZombiesRus5
 * @copyright (C) 2010 ZombiesRus5
 * @copyright Everyone is permitted to copy and distribute verbatim copies of this source, but changing it is not allowed.
 */

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.InputSource;

/**
 * Class used to resolve entity references using an optional local path.
 * Creation date: (4/4/01 1:05:55 PM)
 * 
 * @author: Kevin Stueve
 * @history 01/31/2005 Randy Laffoon added getResourceAsStream to
 *          getInputStream()
 */
public class LocalEntityResolver implements org.xml.sax.EntityResolver {
    private String path;

    /**
     * LocalEntityResolver constructor comment.
     */
    public LocalEntityResolver() {
        super();
    }

    /**
     * LocalEntityResolver constructor sets the local entity path to newPath
     */
    public LocalEntityResolver(String newPath) {
        super();
        path = newPath;
    }

    /**
     * create a URL and return the InputStream from it
     * 
     * @author: Kevin E. Stueve (2/19/03 10:31:03 AM)
     * @return java.io.InputStream
     * @throws IOException
     */
    private InputStream getInputStream(String location) throws IOException {
        InputStream byteStream = null;

        if (location.startsWith("http:") || location.startsWith("ftp:") || location.startsWith("file:")) {
            java.net.URL url = new java.net.URL(location);
            byteStream = url.openStream();
        } else {
            byteStream = new java.io.FileInputStream(location);
        }

        return byteStream;
    }

    /**
     * If a local entity path was provided use that plus the file name off of
     * arg2 to build an imput source else use arg2 to provide the input source.
     */
    public org.xml.sax.InputSource resolveEntity(String arg1, String arg2) throws java.io.IOException, org.xml.sax.SAXException {
        org.xml.sax.InputSource source = null;
        InputStream byteStream = null;
        try {
            if (path != null) {
                int index = arg2.lastIndexOf("/");
                String temp = arg2.substring(index + 1, arg2.length());
                temp = path + temp;
                byteStream = getInputStream(temp);

            } else {
                int index = arg2.lastIndexOf("/");
                String temp = arg2.substring(index + 1, arg2.length());
                String resource = "/" + temp;
                byteStream = getClass().getResourceAsStream(resource);
                if (byteStream == null) {
                    byteStream = getInputStream(arg2);
                }
            }

        } catch (IOException e) {
            byteStream = getInputStream(arg2);

        }
        source = new InputSource(byteStream);
        source.setSystemId(arg2);
        return source;
    }
}