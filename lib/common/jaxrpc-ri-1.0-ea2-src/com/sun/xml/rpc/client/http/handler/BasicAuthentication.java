// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   BasicAuthentication.java

package com.sun.xml.rpc.client.http.handler;

import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterEncoder;

// Referenced classes of package com.sun.xml.rpc.client.http.handler:
//            AuthenticationInfo

class BasicAuthentication extends AuthenticationInfo {

    String auth;

    public BasicAuthentication(boolean isProxy, String host, int port, String realm, PasswordAuthentication pw) {
        super(isProxy ? 'p' : 's', host, port, realm);
        String plain = pw.getUserName() + ":";
        byte nameBytes[] = plain.getBytes();
        char passwd[] = pw.getPassword();
        byte passwdBytes[] = new byte[passwd.length];
        for(int i = 0; i < passwd.length; i++)
            passwdBytes[i] = (byte)passwd[i];

        byte concat[] = new byte[nameBytes.length + passwdBytes.length];
        System.arraycopy(nameBytes, 0, concat, 0, nameBytes.length);
        System.arraycopy(passwdBytes, 0, concat, nameBytes.length, passwdBytes.length);
        auth = "Basic " + (new BASE64Encoder()).encode(concat);
        Arrays.fill(passwd, ' ');
        Arrays.fill(passwdBytes, (byte)0);
        Arrays.fill(concat, (byte)0);
    }

    public BasicAuthentication(boolean isProxy, String host, int port, String realm, String auth) {
        super(isProxy ? 'p' : 's', host, port, realm);
        this.auth = "Basic " + auth;
    }

    public BasicAuthentication(boolean isProxy, URL url, String realm, PasswordAuthentication pw) {
        super(isProxy ? 'p' : 's', url, realm);
        String plain = pw.getUserName() + ":";
        byte nameBytes[] = plain.getBytes();
        char passwd[] = pw.getPassword();
        byte passwdBytes[] = new byte[passwd.length];
        for(int i = 0; i < passwd.length; i++)
            passwdBytes[i] = (byte)passwd[i];

        byte concat[] = new byte[nameBytes.length + passwdBytes.length];
        System.arraycopy(nameBytes, 0, concat, 0, nameBytes.length);
        System.arraycopy(passwdBytes, 0, concat, nameBytes.length, passwdBytes.length);
        auth = "Basic " + (new BASE64Encoder()).encode(concat);
        Arrays.fill(passwd, ' ');
        Arrays.fill(passwdBytes, (byte)0);
        Arrays.fill(concat, (byte)0);
    }

    public BasicAuthentication(boolean isProxy, URL url, String realm, String auth) {
        super(isProxy ? 'p' : 's', url, realm);
        this.auth = "Basic " + auth;
    }

    boolean supportsPreemptiveAuthorization() {
        return true;
    }

    String getHeaderName() {
        if(super.type == 's')
            return "Authorization";
        else
            return "Proxy-authorization";
    }

    String getHeaderValue() {
        return auth;
    }
}
