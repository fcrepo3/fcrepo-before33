// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCServletDelegate.java

package com.sun.xml.rpc.server.http;

import com.sun.xml.messaging.util.ByteInputStream;
import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.soap.message.Handler;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.localization.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogSource;

// Referenced classes of package com.sun.xml.rpc.server.http:
//            ImplementorFactory, WSDLPublisher, JAXRPCServletException, ServletDelegate

public class JAXRPCServletDelegate
    implements ServletDelegate {

    private ServletConfig _servletConfig;
    private ServletContext _servletContext;
    private ImplementorFactory _implementorFactory;
    private WSDLPublisher _wsdlPublisher;
    private Localizer _localizer;
    private LocalizableMessageFactory _messageFactory;
    private static final String CONFIG_FILE_PROPERTY = "configuration.file";
    private static final String WSDL_QUERY_STRING = "WSDL";
    private static final String FAULT_STRING_MISSING_PORT = "Missing port information";
    private static final String FAULT_STRING_PORT_NOT_FOUND = "Port not found";
    private static final String FAULT_STRING_INTERNAL_SERVER_ERROR = "Internal Server Error";
    private static final Log _logger = LogSource.getInstance("com.sun.xml.rpc.server.http");

    public JAXRPCServletDelegate() {
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        try {
            _servletConfig = servletConfig;
            _servletContext = servletConfig.getServletContext();
            _localizer = new Localizer();
            _messageFactory = new LocalizableMessageFactory("com.sun.xml.rpc.resources.jaxrpcservlet");
            if(_logger.isInfoEnabled())
                _logger.info(_localizer.localize(_messageFactory.getMessage("info.servlet.initializing")));
            _implementorFactory = new ImplementorFactory(servletConfig, getConfigFile(servletConfig));
            _wsdlPublisher = new WSDLPublisher(servletConfig, getConfigFile(servletConfig));
        }
        catch(JAXRPCServletException e) {
            String message = _localizer.localize(e);
            throw new ServletException();
        }
        catch(Throwable e) {
            String message = _localizer.localize(_messageFactory.getMessage("error.servlet.caughtThrowable", new Object[] {
                e
            }));
            throw new ServletException(message);
        }
    }

    protected InputStream getConfigFile(ServletConfig servletConfig) {
        String configFilePath = servletConfig.getInitParameter("configuration.file");
        if(configFilePath == null)
            throw new JAXRPCServletException("error.servlet.init.config.parameter.missing", new Object[] {
                "configuration.file"
            });
        InputStream configFile = _servletContext.getResourceAsStream(configFilePath);
        if(configFile == null)
            throw new JAXRPCServletException("error.servlet.init.config.fileNotFound", new Object[] {
                configFilePath
            });
        else
            return configFile;
    }

    public void destroy() {
        if(_logger.isInfoEnabled())
            _logger.info(_localizer.localize(_messageFactory.getMessage("info.servlet.destroying")));
        if(_implementorFactory != null)
            _implementorFactory.destroy();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            MimeHeaders headers = getHeaders(req);
            InputStream is = req.getInputStream();
            byte bytes[] = readFully(is);
            int length = req.getContentLength() != -1 ? req.getContentLength() : bytes.length;
            ByteInputStream in = new ByteInputStream(bytes, length);
            SOAPMessageContext messageContext = new SOAPMessageContext();
            SOAPMessage message = messageContext.createMessage(headers, in);
            if(message == null) {
                if(_logger.isInfoEnabled())
                    _logger.info(_localizer.localize(_messageFactory.getMessage("info.servlet.gotEmptyRequestMessage")));
                messageContext.writeInternalServerErrorResponse();
            } else {
                messageContext.setMessage(message);
                String pathInfo = req.getPathInfo();
                if(pathInfo != null && pathInfo.length() > 1) {
                    String name = pathInfo.charAt(0) != '/' ? pathInfo : pathInfo.substring(1);
                    if(_logger.isTraceEnabled())
                        _logger.trace(_localizer.localize(_messageFactory.getMessage("trace.servlet.requestForPortNamed", name)));
                    Handler implementor = _implementorFactory.getImplementorFor(name);
                    if(implementor == null) {
                        _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.noImplementorForPort", name)));
                        messageContext.writeSimpleErrorResponse(SOAPConstants.FAULT_CODE_SERVER, "Port not found(\"" + name + "\")");
                    } else {
                        if(_logger.isTraceEnabled())
                            _logger.trace(_localizer.localize(_messageFactory.getMessage("trace.servlet.handingRequestOverToImplementor", implementor.toString())));
                        implementor.handle(messageContext);
                        if(_logger.isTraceEnabled())
                            _logger.trace(_localizer.localize(_messageFactory.getMessage("trace.servlet.gotResponseFromImplementor", implementor.toString())));
                        _implementorFactory.releaseImplementor(name, implementor);
                    }
                } else {
                    _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.noPortSpecified")));
                    messageContext.writeSimpleErrorResponse(SOAPConstants.FAULT_CODE_SERVER, "Missing port information");
                }
            }
            SOAPMessage reply = messageContext.getMessage();
            if(message == reply) {
                _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.noResponseWasProduced")));
                resp.setStatus(500);
                messageContext.writeInternalServerErrorResponse();
            }
            if(reply.saveRequired())
                reply.saveChanges();
            writeReply(resp, messageContext);
        }
        catch(JAXRPCExceptionBase e) {
            _logger.error(_localizer.localize(e), e);
            resp.setStatus(500);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(SOAPConstants.FAULT_CODE_SERVER, "Internal Server Error (" + _localizer.localize(e) + ")");
                writeReply(resp, messageContext);
            }
            catch(Throwable e2) {
                _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.caughtThrowableWhileRecovering", new Object[] {
                    e2
                })));
            }
        }
        catch(Throwable e) {
            if(e instanceof Localizable)
                _logger.error(_localizer.localize((Localizable)e), e);
            else
                _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.caughtThrowable", new Object[] {
                    e
                })));
            resp.setStatus(500);
            try {
                SOAPMessageContext messageContext = new SOAPMessageContext();
                messageContext.writeSimpleErrorResponse(SOAPConstants.FAULT_CODE_SERVER, "Missing port information");
                writeReply(resp, messageContext);
            }
            catch(Throwable e2) {
                _logger.error(_localizer.localize(_messageFactory.getMessage("error.servlet.caughtThrowableWhileRecovering", new Object[] {
                    e2
                })));
            }
        }
    }

    protected void writeReply(HttpServletResponse resp, SOAPMessageContext messageContext) throws SOAPException, IOException {
        SOAPMessage reply = messageContext.getMessage();
        if(messageContext.isFailure()) {
            if(_logger.isTraceEnabled())
                _logger.trace(_localizer.localize(_messageFactory.getMessage("trace.servlet.writingFaultResponse")));
            resp.setStatus(500);
        } else {
            if(_logger.isTraceEnabled())
                _logger.trace(_localizer.localize(_messageFactory.getMessage("trace.servlet.writingSuccessResponse")));
            resp.setStatus(200);
        }
        OutputStream os = resp.getOutputStream();
        String headers[] = reply.getMimeHeaders().getHeader("Content-Type");
        if(headers != null && headers.length > 0)
            resp.setContentType(headers[0]);
        else
            resp.setContentType("text/xml");
        putHeaders(reply.getMimeHeaders(), resp);
        reply.writeTo(os);
        os.flush();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if(request.getPathInfo() != null) {
                response.setContentType("text/html");
                response.setStatus(200);
                PrintWriter httpOut = response.getWriter();
                httpOut.println("<html>");
                httpOut.println("<head><title>");
                httpOut.println(_localizer.localize(_messageFactory.getMessage("html.nonRootPage.title")));
                httpOut.println("</title></head><body>");
                httpOut.println(_localizer.localize(_messageFactory.getMessage("html.nonRootPage.body1")));
                String requestURI = request.getRequestURI();
                int i = requestURI.lastIndexOf(request.getPathInfo());
                if(i == -1) {
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.nonRootPage.body2")));
                } else {
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.nonRootPage.body3a")));
                    httpOut.println(requestURI.substring(0, i));
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.nonRootPage.body3b")));
                }
                httpOut.println("</body></html>");
            } else
            if(request.getQueryString() != null && request.getQueryString().equals("WSDL")) {
                if(_wsdlPublisher.hasDocument()) {
                    _wsdlPublisher.publish(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI() + "/", response);
                } else {
                    response.setContentType("text/html");
                    response.setStatus(200);
                    PrintWriter httpOut = response.getWriter();
                    httpOut.println("<html>");
                    httpOut.println("<head><title>");
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.wsdlPage.title")));
                    httpOut.println("</title></head><body>");
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.wsdlPage.noWsdl")));
                    httpOut.println("</body></html>");
                }
            } else {
                response.setContentType("text/html");
                response.setStatus(200);
                PrintWriter httpOut = response.getWriter();
                httpOut.println("<html>");
                httpOut.println("<head><title>");
                httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.title")));
                httpOut.println("</title></head><body>");
                httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body1")));
                if(_implementorFactory != null) {
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body2a")));
                    Iterator iterator = _implementorFactory.names();
                    if(!iterator.hasNext()) {
                        httpOut.print("NONE");
                    } else {
                        for(boolean first = true; iterator.hasNext(); first = false) {
                            String portName = (String)iterator.next();
                            if(!first)
                                httpOut.print(", ");
                            httpOut.print('"');
                            httpOut.print(portName);
                            String portURI = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI() + "/" + portName;
                            httpOut.print('"');
                            httpOut.print(" (");
                            httpOut.print(portURI);
                            httpOut.print(')');
                        }

                    }
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body2b")));
                    if(_wsdlPublisher.hasDocument()) {
                        httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body3a")));
                        httpOut.println(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getRequestURI() + "?WSDL");
                        httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body3b")));
                    }
                } else {
                    httpOut.println(_localizer.localize(_messageFactory.getMessage("html.rootPage.body4")));
                }
                httpOut.println("</body></html>");
            }
        }
        catch(IOException e) {
            _logger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }

    protected static MimeHeaders getHeaders(HttpServletRequest req) {
        Enumeration enum = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();
        String headerName;
        String headerValue;
        for(; enum.hasMoreElements(); headers.addHeader(headerName, headerValue)) {
            headerName = (String)enum.nextElement();
            headerValue = req.getHeader(headerName);
        }

        return headers;
    }

    protected static void putHeaders(MimeHeaders headers, HttpServletResponse res) {
        headers.removeHeader("Content-Type");
        headers.removeHeader("Content-Length");
        MimeHeader header;
        for(Iterator it = headers.getAllHeaders(); it.hasNext(); res.setHeader(header.getName(), header.getValue()))
            header = (MimeHeader)it.next();

    }

    protected static byte[] readFully(InputStream istream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        for(int num = 0; (num = istream.read(buf)) != -1;)
            bout.write(buf, 0, num);

        byte ret[] = bout.toByteArray();
        return ret;
    }

}
