// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DigestAuthentication.java

package com.sun.xml.rpc.client.http.handler;

import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import sun.net.www.HeaderParser;

// Referenced classes of package com.sun.xml.rpc.client.http.handler:
//            AuthenticationInfo, HttpURLConnection

class DigestAuthentication extends AuthenticationInfo {

    private PasswordAuthentication pw;
    private URL url;
    private String authMethod;
    private static final char charArray[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };

    public DigestAuthentication(URL url, String realm, String authMethod, PasswordAuthentication pw) {
        super('s', url.getHost(), url.getPort(), realm);
        this.authMethod = authMethod;
        this.url = url;
        this.pw = pw;
    }

    boolean supportsPreemptiveAuthorization() {
        return false;
    }

    String getHeaderName() {
        return "";
    }

    String getHeaderValue() {
        return "";
    }

    boolean setHeaders(HttpURLConnection conn, HeaderParser p) {
        String nonce = p.findValue("nonce");
        String uri = url.getFile();
        if(nonce == null || authMethod == null || pw == null || super.realm == null)
            return false;
        if(authMethod.length() >= 1)
            authMethod = Character.toUpperCase(authMethod.charAt(0)) + authMethod.substring(1).toLowerCase();
        String algorithm = p.findValue("algorithm");
        if(algorithm == null || "".equals(algorithm))
            algorithm = "MD5";
        char passwd[] = pw.getPassword();
        String response;
        try {
            response = computeDigest(pw.getUserName(), passwd, super.realm, conn.getMethod(), uri, nonce, algorithm);
            if(passwd != null)
                Arrays.fill(passwd, ' ');
            passwd = null;
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception) {
            boolean flag = false;
            return flag;
        }
        finally {
            if(passwd != null)
                Arrays.fill(passwd, ' ');
        }
        String value = authMethod + " username=\"" + pw.getUserName() + "\", realm=\"" + super.realm + "\", nonce=\"" + nonce + "\", uri=\"" + uri + "\", response=\"" + response + "\"";
        conn.setAuthenticationProperty("Authorization", value);
        return true;
    }

    private String computeDigest(String userName, char password[], String realm, String connMethod, String requestURI, String nonceString, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        String A1 = userName + ":" + realm + ":";
        String HashA1 = encode(A1, password, md);
        String A2 = connMethod + ":" + requestURI;
        String HashA2 = encode(A2, null, md);
        String combo = HashA1 + ":" + nonceString + ":" + HashA2;
        String finalHash = encode(combo, null, md);
        return finalHash;
    }

    private String encode(String src, char passwd[], MessageDigest md) {
        md.update(src.getBytes());
        if(passwd != null) {
            byte passwdBytes[] = new byte[passwd.length];
            for(int i = 0; i < passwd.length; i++)
                passwdBytes[i] = (byte)passwd[i];

            md.update(passwdBytes);
            Arrays.fill(passwdBytes, (byte)0);
        }
        byte digest[] = md.digest();
        StringBuffer res = new StringBuffer(digest.length * 2);
        for(int i = 0; i < digest.length; i++) {
            int hashchar = digest[i] >>> 4 & 0xf;
            res.append(charArray[hashchar]);
            hashchar = digest[i] & 0xf;
            res.append(charArray[hashchar]);
        }

        return res.toString();
    }

}
