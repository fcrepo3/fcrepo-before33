// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLReaderImpl.java

package com.sun.xml.rpc.streaming;

import com.sun.xml.rpc.sp.*;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;
import org.xml.sax.InputSource;

// Referenced classes of package com.sun.xml.rpc.streaming:
//            XMLReaderBase, ElementIdStack, XMLReaderException, Attributes, 
//            XMLReader

public class XMLReaderImpl extends XMLReaderBase {

    private int _state;
    private QName _name;
    private InputStream _stream;
    private XMLReaderImpl$AttributesAdapter _attributeAdapter;
    private ElementIdStack _elementIds;
    private int _elementId;
    private Parser2 _parser;
    private static final int DOC_END = -1;
    private static final int DOC_START = -2;
    private static final int EMPTY = -3;
    private static final int EXCEPTION = -4;

    public XMLReaderImpl(InputSource source) {
        _state = 0;
        _stream = source.getByteStream();
        _parser = new Parser2(_stream, true, true);
        _elementIds = new ElementIdStack();
        _attributeAdapter = new XMLReaderImpl$AttributesAdapter(this);
    }

    public void close() {
        try {
            _state = 5;
            _stream.close();
        }
        catch(IOException e) {
            throw new XMLReaderException("xmlreader.ioException", new LocalizableExceptionAdapter(e));
        }
    }

    public int getState() {
        return _state;
    }

    public QName getName() {
        if(_name == null)
            _name = new QName(getURI(), getLocalName());
        return _name;
    }

    public String getURI() {
        return _parser.getCurURI();
    }

    public String getLocalName() {
        return _parser.getCurName();
    }

    public Attributes getAttributes() {
        _attributeAdapter.setTarget(_parser.getAttributes());
        return _attributeAdapter;
    }

    public String getValue() {
        return _parser.getCurValue();
    }

    public int getElementId() {
        return _elementId;
    }

    public int getLineNumber() {
        return _parser.getLineNumber();
    }

    public String getURI(String prefix) {
        return _parser.getNamespaceSupport().getURI(prefix);
    }

    public Iterator getPrefixes() {
        return _parser.getNamespaceSupport().getPrefixes();
    }

    public int next() {
        if(_state == 5)
            return 5;
        _name = null;
        try {
            _state = _parser.parse();
            if(_state == -1)
                _state = 5;
        }
        catch(ParseException e) {
            throw new XMLReaderException("xmlreader.parseException", new LocalizableExceptionAdapter(e));
        }
        catch(IOException e) {
            throw new XMLReaderException("xmlreader.ioException", new LocalizableExceptionAdapter(e));
        }
        switch(_state) {
        case 1: // '\001'
            _elementId = _elementIds.pushNext();
            break;

        case 2: // '\002'
            _elementId = _elementIds.pop();
            break;

        default:
            throw new XMLReaderException("xmlreader.illegalStateEncountered", Integer.toString(_state));

        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
            break;
        }
        return _state;
    }

    public XMLReader recordElement() {
        throw new UnsupportedOperationException();
    }

    public void skipElement(int elementId) {
        while(_state != 5 && (_state != 2 || _elementId != elementId)) 
            next();
    }
}
