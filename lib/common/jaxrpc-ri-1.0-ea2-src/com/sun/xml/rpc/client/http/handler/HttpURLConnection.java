// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HttpURLConnection.java

package com.sun.xml.rpc.client.http.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sun.net.www.HeaderParser;
import sun.net.www.MessageHeader;
import sun.net.www.http.HttpClient;
import sun.security.action.GetPropertyAction;

// Referenced classes of package com.sun.xml.rpc.client.http.handler:
//            Handler, EmptyInputStream, BasicAuthentication, DigestAuthentication, 
//            AuthenticationInfo, HttpAuthenticator

public class HttpURLConnection extends java.net.HttpURLConnection {

    static final String version = (String)AccessController.doPrivileged(new GetPropertyAction("java.version"));
    public static final String userAgent = (String)AccessController.doPrivileged(new GetPropertyAction("http.agent", "Java" + version));
    static final String httpVersion = "HTTP/1.1";
    static final String acceptString = "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
    static final int maxRedirects = 5;
    protected HttpClient http;
    protected Handler handler;
    protected PrintStream ps;
    private static HttpAuthenticator defaultAuth;
    private MessageHeader requests;
    protected ProgressEntry pe;
    private MessageHeader responses;
    private InputStream inputStream;
    private ByteArrayOutputStream poster;
    private boolean setRequests;
    private boolean failedOnce;
    private Exception rememberedException;

    private static PasswordAuthentication privilegedRequestPasswordAuthentication(InetAddress addr, int port, String protocol, String prompt, String scheme) {
        return (PasswordAuthentication)AccessController.doPrivileged(new HttpURLConnection$1(addr, port, protocol, prompt, scheme));
    }

    private void checkMessageHeader(String key, String value) {
        char LF = '\n';
        int index = key.indexOf(LF);
        if(index != -1)
            throw new IllegalArgumentException("Illegal character(s) in message header field: " + key);
        index = value.indexOf(LF);
        while(index != -1)  {
            if(++index < value.length()) {
                char c = value.charAt(index);
                if(c == ' ' || c == '\t') {
                    index = value.indexOf(LF, index);
                    continue;
                }
            }
            throw new IllegalArgumentException("Illegal character(s) in message header value: " + value);
        }
    }

    private void writeRequests() throws IOException {
        if(!setRequests) {
            if(!failedOnce)
                requests.prepend(super.method + " " + http.getURLFile() + " " + "HTTP/1.1", null);
            requests.setIfNotSet("User-Agent", userAgent);
            int port = super.url.getPort();
            String host = super.url.getHost();
            if(port != -1 && port != 80)
                host = host + ":" + String.valueOf(port);
            requests.setIfNotSet("Host", host);
            requests.setIfNotSet("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            if(!failedOnce && http.getHttpKeepAliveSet()) {
                if(http.usingProxy && !super.method.equals("POST") && !super.method.equals("PUT"))
                    requests.setIfNotSet("Proxy-Connection", "keep-alive");
                else
                if(!http.usingProxy)
                    requests.setIfNotSet("Connection", "keep-alive");
            } else {
                requests.set("Connection", "close");
            }
            if(http.usingProxy) {
                AuthenticationInfo pauth = AuthenticationInfo.getProxyAuth(http.getProxyHostUsed(), http.getProxyPortUsed());
                if(pauth != null && pauth.supportsPreemptiveAuthorization())
                    requests.setIfNotSet(pauth.getHeaderName(), pauth.getHeaderValue());
            }
            long modTime = getIfModifiedSince();
            if(modTime != 0L) {
                Date date = new Date(modTime);
                SimpleDateFormat fo = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'");
                fo.setTimeZone(TimeZone.getTimeZone("GMT"));
                requests.setIfNotSet("If-Modified-Since", fo.format(date));
            }
            AuthenticationInfo sauth = AuthenticationInfo.getServerAuth(super.url);
            if(sauth != null && sauth.supportsPreemptiveAuthorization())
                requests.setIfNotSet(sauth.getHeaderName(), sauth.getHeaderValue());
            if(poster != null)
                synchronized(poster) {
                    if(!super.method.equals("PUT")) {
                        String type = "application/x-www-form-urlencoded";
                        requests.setIfNotSet("Content-type", type);
                    }
                    requests.set("Content-length", String.valueOf(poster.size()));
                }
            setRequests = true;
        }
        try {
            try {
                Method method = (sun.net.www.http.HttpClient.class).getMethod("writeRequests", new Class[] {
                    sun.net.www.MessageHeader.class
                });
                method.invoke(http, new Object[] {
                    requests
                });
                if(poster != null) {
                    poster.writeTo(ps);
                    ps.flush();
                }
            }
            catch(NoSuchMethodException nosuchmethodexception) {
                try {
                    Method method = (sun.net.www.http.HttpClient.class).getMethod("writeRequests", new Class[] {
                        sun.net.www.MessageHeader.class, java.io.ByteArrayOutputStream.class
                    });
                    method.invoke(http, new Object[] {
                        requests, poster
                    });
                }
                catch(NoSuchMethodException nosuchmethodexception1) {
                    throw new UnsupportedOperationException("HttpURLConnection.writeRequests(1)");
                }
            }
        }
        catch(IllegalAccessException illegalaccessexception) {
            throw new UnsupportedOperationException("HttpURLConnection.writeRequests(2)");
        }
        catch(InvocationTargetException invocationtargetexception) {
            throw new UnsupportedOperationException("HttpURLConnection.writeRequests(3)");
        }
        if(ps.checkError()) {
            disconnect();
            if(failedOnce)
                throw new IOException("Error writing to server");
            failedOnce = true;
            http = getNewClient(super.url);
            ps = (PrintStream)http.getOutputStream();
            super.connected = true;
            responses = new MessageHeader();
            setRequests = false;
            writeRequests();
        }
    }

    protected HttpClient getNewClient(URL url) throws IOException {
        return new HttpClient(url, null, -1);
    }

    protected HttpClient getProxiedClient(URL url, String proxyHost, int proxyPort) throws IOException {
        return new HttpClient(url, proxyHost, proxyPort);
    }

    protected HttpURLConnection(URL u, Handler handler) throws IOException {
        super(u);
        ps = null;
        inputStream = null;
        poster = null;
        setRequests = false;
        failedOnce = false;
        rememberedException = null;
        requests = new MessageHeader();
        responses = new MessageHeader();
        this.handler = handler;
    }

    public HttpURLConnection(URL u, String host, int port) throws IOException {
        this(u, new Handler(host, port));
    }

    /**
     * @deprecated Method setDefaultAuthenticator is deprecated
     */

    public static void setDefaultAuthenticator(HttpAuthenticator a) {
        defaultAuth = a;
    }

    public static InputStream openConnectionCheckRedirects(URLConnection c) throws IOException {
        int redirects = 0;
        InputStream in = null;
        boolean redir;
        do {
            if(c instanceof HttpURLConnection)
                ((HttpURLConnection)c).setInstanceFollowRedirects(false);
            in = c.getInputStream();
            redir = false;
            if(c instanceof HttpURLConnection) {
                HttpURLConnection http = (HttpURLConnection)c;
                int stat = http.getResponseCode();
                if(stat >= 300 && stat <= 305 && stat != 304) {
                    URL base = http.getURL();
                    String loc = http.getHeaderField("Location");
                    URL target = null;
                    if(loc != null)
                        target = new URL(base, loc);
                    http.disconnect();
                    if(target == null || !base.getProtocol().equals(target.getProtocol()) || base.getPort() != target.getPort() || !hostsEqual(base, target) || redirects >= 5)
                        throw new SecurityException("illegal URL redirect");
                    redir = true;
                    c = target.openConnection();
                    redirects++;
                }
            }
        } while(redir);
        return in;
    }

    private static boolean hostsEqual(URL u1, URL u2) {
        String h1 = u1.getHost();
        String h2 = u2.getHost();
        if(h1 == null)
            return h2 == null;
        if(h2 == null)
            return false;
        if(h1.equalsIgnoreCase(h2)) {
            return true;
        } else {
            boolean result[] = {
                false
            };
            AccessController.doPrivileged(new HttpURLConnection$2(h1, h2, result));
            return result[0];
        }
    }

    public void connect() throws IOException {
        if(super.connected)
            return;
        try {
            if("http".equals(super.url.getProtocol()) && !failedOnce)
                http = HttpClient.New(super.url);
            else
                http = new HttpClient(super.url, handler.proxy, handler.proxyPort);
            ps = (PrintStream)http.getOutputStream();
        }
        catch(IOException e) {
            throw e;
        }
        super.connected = true;
    }

    public synchronized OutputStream getOutputStream() throws IOException {
        try {
            if(!super.doOutput)
                throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)");
            if(super.method.equals("GET"))
                setRequestMethod("POST");
            if(!"POST".equals(super.method) && !"PUT".equals(super.method) && "http".equals(super.url.getProtocol()))
                throw new ProtocolException("HTTP method " + super.method + " doesn't support output");
            if(inputStream != null)
                throw new ProtocolException("Cannot write output after reading input.");
            connect();
            ps = (PrintStream)http.getOutputStream();
            if(poster == null)
                poster = new ByteArrayOutputStream();
            return poster;
        }
        catch(RuntimeException e) {
            disconnect();
            throw e;
        }
        catch(IOException e) {
            disconnect();
            throw e;
        }
    }

    public synchronized InputStream getInputStream() throws IOException {
        if(!super.doInput)
            throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))");
        if(inputStream != null && !super.connected && rememberedException != null)
            if(rememberedException instanceof RuntimeException)
                throw (RuntimeException)rememberedException;
            else
                throw (IOException)rememberedException;
        if(inputStream != null)
            return inputStream;
        int redirects = 0;
        AuthenticationInfo serverAuthentication = null;
        AuthenticationInfo proxyAuthentication = null;
        try {
            do {
                pe = new ProgressEntry(super.url.getFile(), null);
                ProgressData.pdata.register(pe);
                connect();
                ps = (PrintStream)http.getOutputStream();
                writeRequests();
                http.parseHTTP(responses, pe);
                inputStream = http.getInputStream();
                int respCode = getResponseCode();
                if(respCode == 407) {
                    if(proxyAuthentication != null)
                        proxyAuthentication.removeFromCache();
                    proxyAuthentication = getHttpProxyAuthentication();
                    if(proxyAuthentication != null) {
                        disconnect();
                        redirects++;
                        continue;
                    }
                }
                if(respCode == 401) {
                    if(serverAuthentication != null)
                        serverAuthentication.removeFromCache();
                    serverAuthentication = getServerAuthentication();
                    if(serverAuthentication != null) {
                        disconnect();
                        redirects++;
                        continue;
                    }
                }
                if(respCode == 200 || respCode >= 300 && respCode <= 305) {
                    if(proxyAuthentication != null)
                        proxyAuthentication.addToCache();
                    if(serverAuthentication != null)
                        serverAuthentication.addToCache();
                }
                if(followRedirect()) {
                    redirects++;
                } else {
                    if(super.method.equals("HEAD") || super.method.equals("TRACE")) {
                        disconnect();
                        return inputStream = new EmptyInputStream();
                    }
                    String fname = super.url.getFile();
                    if(respCode >= 400) {
                        if(respCode == 404 || respCode == 410)
                            throw new FileNotFoundException(super.url.toString());
                        else
                            throw new IOException("Server returned HTTP code >= 400");
                    } else {
                        return inputStream;
                    }
                }
            } while(redirects < 5);
            throw new ProtocolException("Server redirected too many times (" + redirects + ")");
        }
        catch(RuntimeException e) {
            disconnect();
            rememberedException = e;
            throw e;
        }
        catch(IOException e) {
            rememberedException = e;
            throw e;
        }
    }

    public InputStream getErrorStream() {
        if(super.connected && super.responseCode >= 400 && inputStream != null)
            return inputStream;
        else
            return null;
    }

    private AuthenticationInfo getHttpProxyAuthentication() {
        AuthenticationInfo ret = null;
        String raw = getHeaderField("Proxy-authenticate");
        String host = http.getProxyHostUsed();
        int port = http.getProxyPortUsed();
        if(host != null && raw != null) {
            HeaderParser p = new HeaderParser(raw);
            String realm = p.findValue("realm");
            String scheme = p.findKey(0);
            ret = AuthenticationInfo.getProxyAuth(host, port, realm);
            if(ret == null && "basic".equalsIgnoreCase(scheme)) {
                InetAddress addr = null;
                try {
                    addr = InetAddress.getByName(host);
                }
                catch(UnknownHostException unknownhostexception) { }
                PasswordAuthentication a = privilegedRequestPasswordAuthentication(addr, port, "http", realm, scheme);
                if(a != null)
                    ret = new BasicAuthentication(true, host, port, realm, a);
            }
            if(ret == null && defaultAuth != null && defaultAuth.schemeSupported(scheme))
                try {
                    URL u = new URL("http", host, port, "/");
                    String a = defaultAuth.authString(u, scheme, realm);
                    if(a != null)
                        ret = new BasicAuthentication(true, host, port, realm, a);
                }
                catch(MalformedURLException malformedurlexception) { }
            if(ret != null && !ret.setHeaders(this, p))
                ret = null;
        }
        return ret;
    }

    private AuthenticationInfo getServerAuthentication() {
        AuthenticationInfo ret = null;
        String raw = getHeaderField("WWW-Authenticate");
        if(raw != null) {
            HeaderParser p = new HeaderParser(raw);
            String realm = p.findValue("realm");
            String scheme = p.findKey(0);
            ret = AuthenticationInfo.getServerAuth(super.url, realm);
            InetAddress addr = null;
            if(ret == null)
                try {
                    addr = InetAddress.getByName(super.url.getHost());
                }
                catch(UnknownHostException unknownhostexception) { }
            if(ret == null && "basic".equalsIgnoreCase(scheme)) {
                PasswordAuthentication a = privilegedRequestPasswordAuthentication(addr, super.url.getPort(), super.url.getProtocol(), realm, scheme);
                if(a != null)
                    ret = new BasicAuthentication(false, super.url, realm, a);
            }
            if(ret == null && "digest".equalsIgnoreCase(scheme)) {
                PasswordAuthentication a = privilegedRequestPasswordAuthentication(addr, super.url.getPort(), super.url.getProtocol(), realm, scheme);
                if(a != null)
                    ret = new DigestAuthentication(super.url, realm, scheme, a);
            }
            if(ret == null && defaultAuth != null && defaultAuth.schemeSupported(scheme)) {
                String a = defaultAuth.authString(super.url, scheme, realm);
                if(a != null)
                    ret = new BasicAuthentication(false, super.url, realm, a);
            }
            if(ret != null && !ret.setHeaders(this, p))
                ret = null;
        }
        return ret;
    }

    private boolean followRedirect() throws IOException {
        if(!getInstanceFollowRedirects())
            return false;
        int stat = getResponseCode();
        if(stat < 300 || stat > 305 || stat == 304)
            return false;
        String loc = getHeaderField("Location");
        if(loc == null)
            return false;
        disconnect();
        responses = new MessageHeader();
        if(stat == 305) {
            URL urlp = new URL(loc);
            http = getProxiedClient(super.url, urlp.getHost(), urlp.getPort());
            requests.set(0, super.method + " " + http.getURLFile() + " " + "HTTP/1.1", null);
            super.connected = true;
        } else {
            super.url = new URL(super.url, loc);
            if(super.method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect")) {
                requests = new MessageHeader();
                setRequests = false;
                setRequestMethod("GET");
                poster = null;
                connect();
            } else {
                connect();
                requests.set(0, super.method + " " + http.getURLFile() + " " + "HTTP/1.1", null);
                requests.set("Host", super.url.getHost() + (super.url.getPort() != -1 && super.url.getPort() != 80 ? ":" + String.valueOf(super.url.getPort()) : ""));
            }
        }
        return true;
    }

    public void disconnect() {
        super.responseCode = -1;
        if(pe != null)
            ProgressData.pdata.unregister(pe);
        if(http != null) {
            http.closeServer();
            http = null;
            super.connected = false;
        }
    }

    public boolean usingProxy() {
        if(http != null)
            return http.usingProxy;
        else
            return false;
    }

    public String getHeaderField(String name) {
        try {
            getInputStream();
        }
        catch(IOException ioexception) { }
        return responses.findValue(name);
    }

    public String getHeaderField(int n) {
        try {
            getInputStream();
        }
        catch(IOException ioexception) { }
        return responses.getValue(n);
    }

    public String getHeaderFieldKey(int n) {
        try {
            getInputStream();
        }
        catch(IOException ioexception) { }
        return responses.getKey(n);
    }

    public int getResponseCode() throws IOException {
        if(super.responseCode != -1)
            return super.responseCode;
        String resp = getHeaderField(0);
        try {
            int ind;
            for(ind = resp.indexOf(' '); resp.charAt(ind) == ' '; ind++);
            super.responseCode = Integer.parseInt(resp.substring(ind, ind + 3));
            super.responseMessage = resp.substring(ind + 4).trim();
            return super.responseCode;
        }
        catch(Exception exception) { }
        try {
            getInputStream();
        }
        catch(IOException ioe) {
            throw ioe;
        }
        return super.responseCode;
    }

    public void setRequestProperty(String key, String value) {
        if(super.connected) {
            throw new IllegalAccessError("Already connected");
        } else {
            checkMessageHeader(key, value);
            requests.set(key, value);
            return;
        }
    }

    void setAuthenticationProperty(String key, String value) {
        checkMessageHeader(key, value);
        requests.set(key, value);
    }

    public String getRequestProperty(String key) {
        return requests.findValue(key);
    }

    public void finalize() {
    }

    String getMethod() {
        return super.method;
    }

}
