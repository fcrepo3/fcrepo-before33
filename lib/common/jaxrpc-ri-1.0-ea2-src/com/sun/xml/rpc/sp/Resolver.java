// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Resolver.java

package com.sun.xml.rpc.sp;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            XmlReader

public class Resolver
    implements EntityResolver {

    private boolean ignoringMIME;
    private Map id2uri;
    private Map id2resource;
    private Map id2loader;
    private static final String types[] = {
        "application/xml", "text/xml", "text/plain", "text/html", "application/x-netcdf", "content/unknown"
    };

    public Resolver() {
    }

    public static InputSource createInputSource(String contentType, InputStream stream, boolean checkType, String scheme) throws IOException {
        String charset = null;
        if(contentType != null) {
            contentType = contentType.toLowerCase();
            int index = contentType.indexOf(';');
            if(index != -1) {
                String attributes = contentType.substring(index + 1);
                contentType = contentType.substring(0, index);
                index = attributes.indexOf("charset");
                if(index != -1) {
                    attributes = attributes.substring(index + 7);
                    if((index = attributes.indexOf(';')) != -1)
                        attributes = attributes.substring(0, index);
                    if((index = attributes.indexOf('=')) != -1) {
                        attributes = attributes.substring(index + 1);
                        if((index = attributes.indexOf('(')) != -1)
                            attributes = attributes.substring(0, index);
                        if((index = attributes.indexOf('"')) != -1) {
                            attributes = attributes.substring(index + 1);
                            attributes = attributes.substring(0, attributes.indexOf('"'));
                        }
                        charset = attributes.trim();
                    }
                }
            }
            if(checkType) {
                boolean isOK = false;
                for(int i = 0; i < types.length; i++) {
                    if(!types[i].equals(contentType))
                        continue;
                    isOK = true;
                    break;
                }

                if(!isOK)
                    throw new IOException("Not XML: " + contentType);
            }
            if(charset == null) {
                contentType = contentType.trim();
                if(contentType.startsWith("text/") && !"file".equalsIgnoreCase(scheme))
                    charset = "US-ASCII";
            }
        }
        InputSource retval = new InputSource(XmlReader.createReader(stream, charset));
        retval.setByteStream(stream);
        retval.setEncoding(charset);
        return retval;
    }

    public static InputSource createInputSource(URL uri, boolean checkType) throws IOException {
        URLConnection conn = uri.openConnection();
        InputSource retval;
        if(checkType) {
            String contentType = conn.getContentType();
            retval = createInputSource(contentType, conn.getInputStream(), false, uri.getProtocol());
        } else {
            retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
        }
        retval.setSystemId(conn.getURL().toString());
        return retval;
    }

    public static InputSource createInputSource(File file) throws IOException {
        InputSource retval = new InputSource(XmlReader.createReader(new FileInputStream(file)));
        String path = file.getAbsolutePath();
        if(File.separatorChar != '/')
            path = path.replace(File.separatorChar, '/');
        if(!path.startsWith("/"))
            path = "/" + path;
        if(!path.endsWith("/") && file.isDirectory())
            path = path + "/";
        retval.setSystemId("file:" + path);
        return retval;
    }

    public InputSource resolveEntity(String name, String uri) throws IOException, SAXException {
        String mappedURI = name2uri(name);
        InputSource retval;
        InputStream stream;
        if(mappedURI == null && (stream = mapResource(name)) != null) {
            uri = "java:resource:" + (String)id2resource.get(name);
            retval = new InputSource(XmlReader.createReader(stream));
        } else {
            if(mappedURI != null)
                uri = mappedURI;
            else
            if(uri == null)
                return null;
            URL url = new URL(uri);
            URLConnection conn = url.openConnection();
            uri = conn.getURL().toString();
            if(ignoringMIME) {
                retval = new InputSource(XmlReader.createReader(conn.getInputStream()));
            } else {
                String contentType = conn.getContentType();
                retval = createInputSource(contentType, conn.getInputStream(), false, url.getProtocol());
            }
        }
        retval.setSystemId(uri);
        retval.setPublicId(name);
        return retval;
    }

    public boolean isIgnoringMIME() {
        return ignoringMIME;
    }

    public void setIgnoringMIME(boolean value) {
        ignoringMIME = value;
    }

    private String name2uri(String publicId) {
        if(publicId == null || id2uri == null)
            return null;
        else
            return (String)id2uri.get(publicId);
    }

    public void registerCatalogEntry(String publicId, String uri) {
        if(id2uri == null)
            id2uri = new HashMap(17);
        id2uri.put(publicId, uri);
    }

    private InputStream mapResource(String publicId) {
        if(publicId == null || id2resource == null)
            return null;
        String resourceName = (String)id2resource.get(publicId);
        ClassLoader loader = null;
        if(resourceName == null)
            return null;
        if(id2loader != null)
            loader = (ClassLoader)id2loader.get(publicId);
        if(loader == null)
            return ClassLoader.getSystemResourceAsStream(resourceName);
        else
            return loader.getResourceAsStream(resourceName);
    }

    public void registerCatalogEntry(String publicId, String resourceName, ClassLoader loader) {
        if(id2resource == null)
            id2resource = new HashMap(17);
        id2resource.put(publicId, resourceName);
        if(loader != null) {
            if(id2loader == null)
                id2loader = new HashMap(17);
            id2loader.put(publicId, loader);
        }
    }

}
