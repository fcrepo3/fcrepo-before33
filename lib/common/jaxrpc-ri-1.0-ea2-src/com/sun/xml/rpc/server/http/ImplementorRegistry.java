// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ImplementorRegistry.java

package com.sun.xml.rpc.server.http;

import java.io.*;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.server.http:
//            ImplementorInfo, JAXRPCServletException

public class ImplementorRegistry {

    private Map _implementors;
    private static final String PROPERTY_PORT_COUNT = "portcount";
    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_TIE = "tie";
    private static final String PROPERTY_SERVANT = "servant";

    public ImplementorRegistry() {
        _implementors = new HashMap();
    }

    public ImplementorInfo getImplementorInfo(String name) {
        ImplementorInfo info = (ImplementorInfo)_implementors.get(name);
        if(info == null)
            throw new JAXRPCServletException("error.implementorRegistry.unknownName", name);
        else
            return info;
    }

    public boolean containsName(String name) {
        return _implementors.containsKey(name);
    }

    public Iterator names() {
        return _implementors.keySet().iterator();
    }

    public void readFrom(String filename) {
        try {
            readFrom(((InputStream) (new FileInputStream(filename))));
        }
        catch(FileNotFoundException filenotfoundexception) {
            throw new JAXRPCServletException("error.implementorRegistry.fileNotFound", filename);
        }
    }

    public void readFrom(InputStream inputStream) {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            int portCount = Integer.parseInt(properties.getProperty("portcount"));
            for(int i = 0; i < portCount; i++) {
                String portPrefix = "port" + Integer.toString(i) + ".";
                String name = properties.getProperty(portPrefix + "name");
                String tieClassName = properties.getProperty(portPrefix + "tie");
                String servantClassName = properties.getProperty(portPrefix + "servant");
                if(name == null || tieClassName == null || servantClassName == null)
                    throw new JAXRPCServletException("error.implementorRegistry.incompleteInformation");
                register(name, tieClassName, servantClassName);
            }

        }
        catch(IOException ioexception) {
            throw new JAXRPCServletException("error.implementorRegistry.cannotReadConfiguration");
        }
    }

    public void register(String name, String tieClassName, String servantClassName) {
        Class tieClass = null;
        Class servantClass = null;
        try {
            tieClass = Thread.currentThread().getContextClassLoader().loadClass(tieClassName);
        }
        catch(ClassNotFoundException classnotfoundexception) {
            throw new JAXRPCServletException("error.implementorRegistry.classNotFound", tieClassName);
        }
        try {
            servantClass = Thread.currentThread().getContextClassLoader().loadClass(servantClassName);
        }
        catch(ClassNotFoundException classnotfoundexception1) {
            throw new JAXRPCServletException("error.implementorRegistry.classNotFound", servantClassName);
        }
        register(name, new ImplementorInfo(tieClass, servantClass));
    }

    public void register(String name, ImplementorInfo info) {
        if(_implementors.containsKey(name)) {
            throw new JAXRPCServletException("error.implementorRegistry.duplicateName", name);
        } else {
            _implementors.put(name, info);
            return;
        }
    }
}
