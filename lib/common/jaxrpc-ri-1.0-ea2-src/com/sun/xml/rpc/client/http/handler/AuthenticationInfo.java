// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AuthenticationInfo.java

package com.sun.xml.rpc.client.http.handler;

import java.net.URL;
import java.util.Hashtable;
import sun.net.www.HeaderParser;

// Referenced classes of package com.sun.xml.rpc.client.http.handler:
//            HttpURLConnection

abstract class AuthenticationInfo {

    static final char SERVER_AUTHENTICATION = 115;
    static final char PROXY_AUTHENTICATION = 112;
    private static Hashtable cache = new Hashtable();
    char type;
    String host;
    int port;
    String realm;
    String path;

    AuthenticationInfo(char type, String host, int port, String realm) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.realm = realm;
        path = null;
    }

    AuthenticationInfo(char type, URL url, String realm) {
        this.type = type;
        host = url.getHost();
        port = url.getPort();
        this.realm = realm;
        String urlPath = url.getPath();
        if(urlPath.length() == 0) {
            path = urlPath;
        } else {
            int sepIndex = urlPath.lastIndexOf('/');
            int targetSuffixIndex = urlPath.lastIndexOf('.');
            if(sepIndex != -1) {
                if(sepIndex < targetSuffixIndex)
                    path = urlPath.substring(0, sepIndex + 1);
                else
                    path = urlPath;
            } else {
                path = null;
            }
        }
    }

    static AuthenticationInfo getServerAuth(URL url) {
        String key = "s:" + url.getHost() + ":" + url.getPort();
        return getAuth(key, url);
    }

    static AuthenticationInfo getServerAuth(URL url, String realm) {
        String key = "s:" + url.getHost() + ":" + url.getPort() + ":" + realm;
        return getAuth(key, url);
    }

    private static AuthenticationInfo getAuth(String key, URL url) {
        AuthenticationInfo result = (AuthenticationInfo)cache.get(key);
        if(result != null) {
            String p = url.getPath();
            if(result.path != null && !p.startsWith(result.path))
                result = null;
        }
        return result;
    }

    static AuthenticationInfo getProxyAuth(String host, int port) {
        String key = "p:" + host + ":" + port;
        AuthenticationInfo result = (AuthenticationInfo)cache.get(key);
        return result;
    }

    static AuthenticationInfo getProxyAuth(String host, int port, String realm) {
        String key = "p:" + host + ":" + port + ":" + realm;
        AuthenticationInfo result = (AuthenticationInfo)cache.get(key);
        return result;
    }

    void addToCache() {
        cache.put(cacheKey(true), this);
        if(supportsPreemptiveAuthorization())
            cache.put(cacheKey(false), this);
    }

    void removeFromCache() {
        cache.remove(cacheKey(true));
        if(supportsPreemptiveAuthorization())
            cache.remove(cacheKey(false));
    }

    abstract boolean supportsPreemptiveAuthorization();

    abstract String getHeaderName();

    abstract String getHeaderValue();

    boolean setHeaders(HttpURLConnection conn, HeaderParser p) {
        conn.setAuthenticationProperty(getHeaderName(), getHeaderValue());
        return true;
    }

    String cacheKey(boolean includeRealm) {
        if(includeRealm)
            return type + ":" + host + ":" + port + ":" + realm;
        else
            return type + ":" + host + ":" + port;
    }

}
