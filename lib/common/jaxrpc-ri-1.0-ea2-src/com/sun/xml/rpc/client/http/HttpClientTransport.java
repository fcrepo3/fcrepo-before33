// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   HttpClientTransport.java

package com.sun.xml.rpc.client.http;

import com.sun.xml.messaging.util.ByteInputStream;
import com.sun.xml.rpc.client.ClientTransport;
import com.sun.xml.rpc.client.ClientTransportException;
import com.sun.xml.rpc.client.StubPropertyConstants;
import com.sun.xml.rpc.client.http.handler.HttpURLConnection;
import com.sun.xml.rpc.encoding.simpletype.SimpleTypeEncoder;
import com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder;
import com.sun.xml.rpc.soap.message.SOAPMessageContext;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import com.sun.xml.rpc.util.localization.Localizable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

public class HttpClientTransport
    implements ClientTransport, StubPropertyConstants {

    public static final String HTTP_SOAPACTION_PROPERTY = "http.soap.action";
    private static final SimpleTypeEncoder base64Encoder = XSDBase64BinaryEncoder.getInstance();
    private MessageFactory _messageFactory;
    private OutputStream _logStream;
    private static final boolean _overrideDefaultHttpHandler;

    public HttpClientTransport() {
        this(null);
    }

    public HttpClientTransport(OutputStream logStream) {
        try {
            _messageFactory = MessageFactory.newInstance();
            _logStream = logStream;
        }
        catch(Exception exception) {
            throw new ClientTransportException("http.client.cannotCreateMessageFactory");
        }
    }

    public void invoke(String endpoint, SOAPMessageContext context) throws ClientTransportException {
        try {
            if(context.getMessage().saveRequired())
                context.getMessage().saveChanges();
            java.net.HttpURLConnection httpConnection = createConnection(endpoint);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "text/xml");
            String soapAction = (String)context.getProperty("http.soap.action");
            if(soapAction == null)
                context.getMessage().getMimeHeaders().setHeader("SOAPAction", "");
            else
                context.getMessage().getMimeHeaders().setHeader("SOAPAction", "\"" + soapAction + "\"");
            String credentials = (String)context.getProperty("javax.xml.rpc.security.auth.username");
            if(credentials != null) {
                credentials = credentials + ":" + (String)context.getProperty("javax.xml.rpc.security.auth.password");
                credentials = base64Encoder.objectToString(credentials.getBytes(), null);
                context.getMessage().getMimeHeaders().setHeader("Authorization", "Basic " + credentials);
            }
            MimeHeader header;
            for(Iterator iter = context.getMessage().getMimeHeaders().getAllHeaders(); iter.hasNext(); httpConnection.setRequestProperty(header.getName(), header.getValue()))
                header = (MimeHeader)iter.next();

            OutputStream contentOut = httpConnection.getOutputStream();
            context.getMessage().writeTo(contentOut);
            contentOut.flush();
            contentOut.close();
            if(_logStream != null) {
                String s = "******************\nRequest\n";
                _logStream.write(s.getBytes());
                for(Iterator iter = context.getMessage().getMimeHeaders().getAllHeaders(); iter.hasNext(); _logStream.write(s.getBytes())) {
                    MimeHeader thisHeader = (MimeHeader)iter.next();
                    s = thisHeader.getName() + ": " + thisHeader.getValue() + "\n";
                }

                _logStream.flush();
                context.getMessage().writeTo(_logStream);
            }
            httpConnection.connect();
            boolean isFailure = false;
            try {
                if(httpConnection.getResponseCode() == 500)
                    isFailure = true;
                else
                if(httpConnection.getResponseCode() != 200)
                    throw new ClientTransportException("http.client.cannot.connect", httpConnection.getResponseMessage());
            }
            catch(IOException e) {
                if(httpConnection.getResponseCode() == 500)
                    isFailure = true;
                else
                    throw e;
            }
            MimeHeaders headers = new MimeHeaders();
            int i = 1;
            do {
                String key = httpConnection.getHeaderFieldKey(i);
                if(key == null)
                    break;
                String value = httpConnection.getHeaderField(i);
                try {
                    headers.addHeader(key, value);
                }
                catch(IllegalArgumentException illegalargumentexception) { }
                i++;
            } while(true);
            InputStream contentIn = isFailure ? httpConnection.getErrorStream() : httpConnection.getInputStream();
            byte bytes[] = readFully(contentIn);
            int length = httpConnection.getContentLength() != -1 ? httpConnection.getContentLength() : bytes.length;
            ByteInputStream in = new ByteInputStream(bytes, length);
            SOAPMessage response = _messageFactory.createMessage(headers, in);
            contentIn.close();
            httpConnection = null;
            if(_logStream != null) {
                String s = "Response\n";
                _logStream.write(s.getBytes());
                for(Iterator iter = context.getMessage().getMimeHeaders().getAllHeaders(); iter.hasNext(); _logStream.write(s.getBytes())) {
                    MimeHeader thisheader = (MimeHeader)iter.next();
                    s = thisheader.getName() + ": " + thisheader.getValue() + "\n";
                }

                _logStream.flush();
                response.writeTo(_logStream);
                s = "******************\n\n";
                _logStream.write(s.getBytes());
            }
            context.setMessage(response);
        }
        catch(ClientTransportException e) {
            throw e;
        }
        catch(Exception e) {
            if(e instanceof Localizable)
                throw new ClientTransportException("http.client.failed", (Localizable)e);
            else
                throw new ClientTransportException("http.client.failed", new LocalizableExceptionAdapter(e));
        }
    }

    public void invokeOneWay(String endpoint, SOAPMessageContext context) {
        try {
            if(_logStream != null) {
                String s = "******************\nRequest\n";
                _logStream.write(s.getBytes());
                context.getMessage().writeTo(_logStream);
            }
            if(context.getMessage().saveRequired())
                context.getMessage().saveChanges();
            java.net.HttpURLConnection httpConnection = createConnection(endpoint);
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "text/xml");
            String soapAction = (String)context.getProperty("http.soap.action");
            if(soapAction == null)
                context.getMessage().getMimeHeaders().setHeader("SOAPAction", "");
            else
                context.getMessage().getMimeHeaders().setHeader("SOAPAction", "\"" + soapAction + "\"");
            String credentials = (String)context.getProperty("javax.xml.rpc.security.auth.username");
            if(credentials != null) {
                credentials = credentials + ":" + (String)context.getProperty("javax.xml.rpc.security.auth.password");
                credentials = base64Encoder.objectToString(credentials.getBytes(), null);
                context.getMessage().getMimeHeaders().setHeader("Authorization", "Basic " + credentials);
            }
            MimeHeader header;
            for(Iterator iter = context.getMessage().getMimeHeaders().getAllHeaders(); iter.hasNext(); httpConnection.setRequestProperty(header.getName(), header.getValue()))
                header = (MimeHeader)iter.next();

            OutputStream contentOut = httpConnection.getOutputStream();
            context.getMessage().writeTo(contentOut);
            contentOut.flush();
            contentOut.close();
        }
        catch(Exception e) {
            if(e instanceof Localizable)
                throw new ClientTransportException("http.client.failed", (Localizable)e);
            else
                throw new ClientTransportException("http.client.failed", new LocalizableExceptionAdapter(e));
        }
    }

    private java.net.HttpURLConnection createConnection(String endpoint) throws IOException {
        if(_overrideDefaultHttpHandler)
            return new HttpURLConnection(new URL(endpoint), null, 80);
        else
            return (java.net.HttpURLConnection)(new URL(endpoint)).openConnection();
    }

    private byte[] readFully(InputStream istream) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        for(int num = 0; (num = istream.read(buf)) != -1;)
            bout.write(buf, 0, num);

        byte ret[] = bout.toByteArray();
        return ret;
    }

    static  {
        String version = System.getProperty("java.specification.version");
        String vmVersion = System.getProperty("java.vm.version");
        boolean override = false;
        if(version.equals("1.3") && vmVersion.startsWith("1.3") && !vmVersion.startsWith("1.3.1_"))
            override = true;
        _overrideDefaultHttpHandler = override;
    }
}
