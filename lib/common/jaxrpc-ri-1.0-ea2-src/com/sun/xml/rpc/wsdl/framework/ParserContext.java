// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParserContext.java

package com.sun.xml.rpc.wsdl.framework;

import com.sun.xml.rpc.sp.NamespaceSupport;
import com.sun.xml.rpc.util.xml.XmlUtil;
import java.util.*;
import javax.xml.rpc.namespace.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            ParseException, ParserListener, AbstractDocument, Entity

public class ParserContext {

    private static final String PREFIX_XMLNS = "xmlns";
    private boolean _followImports;
    private AbstractDocument _document;
    private NamespaceSupport _nsSupport;
    private ArrayList _listeners;

    public ParserContext(AbstractDocument doc, ArrayList listeners) {
        _document = doc;
        _listeners = listeners;
        _nsSupport = new NamespaceSupport();
    }

    public AbstractDocument getDocument() {
        return _document;
    }

    public boolean getFollowImports() {
        return _followImports;
    }

    public void setFollowImports(boolean b) {
        _followImports = b;
    }

    public void push() {
        _nsSupport.pushContext();
    }

    public void pop() {
        _nsSupport.popContext();
    }

    public String getNamespaceURI(String prefix) {
        return _nsSupport.getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _nsSupport.getPrefixes();
    }

    public String getDefaultNamespaceURI() {
        return getNamespaceURI("");
    }

    public void registerNamespaces(Element e) {
        for(Iterator iter = XmlUtil.getAllAttributes(e); iter.hasNext();) {
            Attr a = (Attr)iter.next();
            if(a.getName().equals("xmlns")) {
                _nsSupport.declarePrefix("", a.getValue());
            } else {
                String prefix = XmlUtil.getPrefix(a.getName());
                if(prefix != null && prefix.equals("xmlns")) {
                    String nsPrefix = XmlUtil.getLocalPart(a.getName());
                    String uri = a.getValue();
                    _nsSupport.declarePrefix(nsPrefix, uri);
                }
            }
        }

    }

    public QName translateQualifiedName(String s) {
        if(s == null)
            return null;
        String prefix = XmlUtil.getPrefix(s);
        String uri = null;
        if(prefix == null) {
            uri = getDefaultNamespaceURI();
        } else {
            uri = getNamespaceURI(prefix);
            if(uri == null)
                throw new ParseException("parsing.unknownNamespacePrefix", prefix);
        }
        return new QName(uri, XmlUtil.getLocalPart(s));
    }

    public void fireIgnoringExtension(QName name, QName parent) {
        List _targets = null;
        synchronized(this) {
            if(_listeners != null)
                _targets = (List)_listeners.clone();
        }
        if(_targets != null) {
            ParserListener l;
            for(Iterator iter = _targets.iterator(); iter.hasNext(); l.ignoringExtension(name, parent))
                l = (ParserListener)iter.next();

        }
    }

    public void fireDoneParsingEntity(QName element, Entity entity) {
        List _targets = null;
        synchronized(this) {
            if(_listeners != null)
                _targets = (List)_listeners.clone();
        }
        if(_targets != null) {
            ParserListener l;
            for(Iterator iter = _targets.iterator(); iter.hasNext(); l.doneParsingEntity(element, entity))
                l = (ParserListener)iter.next();

        }
    }
}
