// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   QName.java

package javax.xml.rpc.namespace;


public final class QName {

    private String namespaceURI;
    private String localPart;

    public QName(String localPart) {
        if(localPart == null) {
            throw new IllegalArgumentException("Local part not allowed to be null");
        } else {
            namespaceURI = "";
            this.localPart = localPart;
            return;
        }
    }

    public QName(String namespaceURI, String localPart) {
        if(localPart == null)
            throw new IllegalArgumentException("Local part not allowed to be null");
        if(namespaceURI == null)
            namespaceURI = "";
        this.namespaceURI = namespaceURI;
        this.localPart = localPart;
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String toString() {
        if(namespaceURI.equals(""))
            return localPart;
        else
            return "{" + namespaceURI + "}" + localPart;
    }

    public static QName valueOf(String s) {
        if(s == null || s.equals(""))
            throw new IllegalArgumentException("invalid QName literal");
        if(s.charAt(0) == '{') {
            int i = s.indexOf('}');
            if(i == -1)
                throw new IllegalArgumentException("invalid QName literal");
            if(i == s.length() - 1)
                throw new IllegalArgumentException("invalid QName literal");
            else
                return new QName(s.substring(1, i), s.substring(i + 1));
        } else {
            return new QName(s);
        }
    }

    public int hashCode() {
        return namespaceURI.hashCode() ^ localPart.hashCode();
    }

    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        if(!(obj instanceof QName)) {
            return false;
        } else {
            QName qname = (QName)obj;
            return namespaceURI.equals(qname.namespaceURI) && localPart.equals(qname.localPart);
        }
    }
}
