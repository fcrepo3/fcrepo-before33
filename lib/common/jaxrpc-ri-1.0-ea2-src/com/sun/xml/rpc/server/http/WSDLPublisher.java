// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLPublisher.java

package com.sun.xml.rpc.server.http;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Referenced classes of package com.sun.xml.rpc.server.http:
//            WSDLPortInfo, JAXRPCServletException

public class WSDLPublisher {

    private ServletConfig _servletConfig;
    private ServletContext _servletContext;
    private String _wsdlLocation;
    private boolean _wsdlTransform;
    private Map _ports;
    private byte _xsltDocument[];
    private Templates _xsltTemplates;
    private static final String PROPERTY_PORT_COUNT = "portcount";
    private static final String PROPERTY_PORT = "port";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_WSDL = "wsdl";
    private static final String PROPERTY_TNS = "targetNamespace";
    private static final String PROPERTY_SERVICE_NAME = "serviceName";
    private static final String PROPERTY_PORT_NAME = "portName";
    private static final String PROPERTY_LOCATION = "location";
    private static final String PROPERTY_TRANSFORM = "transform";

    public WSDLPublisher(ServletConfig servletConfig) {
        _ports = new HashMap();
        _servletConfig = servletConfig;
    }

    public WSDLPublisher(ServletConfig servletConfig, InputStream configInputStream) {
        _ports = new HashMap();
        if(configInputStream == null) {
            throw new IllegalArgumentException("error.wsdlPublisher.noInputStream");
        } else {
            _servletConfig = servletConfig;
            _servletContext = servletConfig.getServletContext();
            readFrom(configInputStream);
            return;
        }
    }

    public boolean hasDocument() {
        return _wsdlLocation != null;
    }

    public void publish(String prefix, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");
        response.setStatus(200);
        OutputStream outputStream = response.getOutputStream();
        if(_wsdlTransform) {
            try {
                javax.xml.transform.Source wsdlDoc = new StreamSource(_servletContext.getResourceAsStream(_wsdlLocation));
                Transformer transformer = _xsltTemplates.newTransformer();
                transformer.setParameter("baseURI", prefix);
                transformer.transform(wsdlDoc, new StreamResult(outputStream));
            }
            catch(TransformerConfigurationException transformerconfigurationexception) {
                throw new IOException("cannot create transformer");
            }
            catch(TransformerException transformerexception) {
                throw new IOException("transformation failed");
            }
        } else {
            InputStream is = _servletContext.getResourceAsStream(_wsdlLocation);
            copyStream(is, outputStream);
            is.close();
        }
    }

    protected void readFrom(InputStream inputStream) {
        try {
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            _wsdlLocation = properties.getProperty("wsdl.location");
            if(_wsdlLocation != null) {
                _wsdlLocation = _wsdlLocation.trim();
                InputStream wsdlFile = _servletContext.getResourceAsStream(_wsdlLocation);
                if(wsdlFile != null) {
                    wsdlFile.close();
                } else {
                    _wsdlLocation = null;
                    return;
                }
                _wsdlTransform = true;
                String transform = properties.getProperty("wsdl.transform");
                if(transform != null && !Boolean.valueOf(transform).booleanValue())
                    _wsdlTransform = false;
                if(_wsdlTransform) {
                    int portCount = Integer.parseInt(properties.getProperty("portcount"));
                    for(int i = 0; i < portCount; i++) {
                        String portPrefix = "port" + Integer.toString(i) + ".";
                        String name = properties.getProperty(portPrefix + "name");
                        String portWsdlPrefix = portPrefix + "wsdl" + ".";
                        String targetNamespace = properties.getProperty(portWsdlPrefix + "targetNamespace");
                        String serviceName = properties.getProperty(portWsdlPrefix + "serviceName");
                        String portName = properties.getProperty(portWsdlPrefix + "portName");
                        if(name != null && targetNamespace != null && serviceName != null && portName != null)
                            _ports.put(name, new WSDLPortInfo(targetNamespace, serviceName, portName));
                    }

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    OutputStreamWriter writer = new OutputStreamWriter(bos, "UTF-8");
                    writer.write("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">\n");
                    writer.write("<xsl:param name=\"baseURI\"/>\n");
                    writer.write("<xsl:template match=\"/\"><xsl:apply-templates mode=\"copy\"/></xsl:template>\n");
                    for(Iterator iter = _ports.keySet().iterator(); iter.hasNext(); writer.write("</xsl:text></xsl:attribute></soap:address></xsl:template>")) {
                        String name = (String)iter.next();
                        WSDLPortInfo portInfo = (WSDLPortInfo)_ports.get(name);
                        writer.write("<xsl:template match=\"wsdl:definitions[@targetNamespace='");
                        writer.write(portInfo.getTargetNamespace());
                        writer.write("']/wsdl:service[@name='");
                        writer.write(portInfo.getServiceName());
                        writer.write("']/wsdl:port[@name='");
                        writer.write(portInfo.getPortName());
                        writer.write("']/soap:address\" mode=\"copy\">");
                        writer.write("<soap:address><xsl:attribute name=\"location\"><xsl:value-of select=\"$baseURI\"/><xsl:text>");
                        writer.write(name);
                    }

                    writer.write("<xsl:template match=\"@*|node()\" mode=\"copy\"><xsl:copy><xsl:apply-templates select=\"@*\" mode=\"copy\"/><xsl:apply-templates mode=\"copy\"/></xsl:copy></xsl:template>\n");
                    writer.write("</xsl:stylesheet>\n");
                    writer.close();
                    _xsltDocument = bos.toByteArray();
                    try {
                        javax.xml.transform.Source xsltDoc = new StreamSource(new ByteArrayInputStream(_xsltDocument));
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        _xsltTemplates = transformerFactory.newTemplates(xsltDoc);
                    }
                    catch(TransformerConfigurationException transformerconfigurationexception) {
                        _wsdlTransform = false;
                    }
                }
            }
        }
        catch(IOException ioexception) {
            throw new JAXRPCServletException("error.wsdlPublisher.cannotReadConfiguration");
        }
    }

    protected static void copyStream(InputStream istream, OutputStream ostream) throws IOException {
        byte buf[] = new byte[1024];
        for(int num = 0; (num = istream.read(buf)) != -1;)
            ostream.write(buf, 0, num);

        ostream.flush();
    }
}
