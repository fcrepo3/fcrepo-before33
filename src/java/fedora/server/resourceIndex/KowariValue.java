package fedora.server.resourceIndex;

import java.io.PrintWriter;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.rdql.QueryPrintUtils;
import com.hp.hpl.jena.rdql.Value;
import com.hp.hpl.jena.rdql.ValueException;

/**
 * @author eddie
 *
 */
public class KowariValue implements Value {
    private boolean isBoolean = false;
    private boolean isDouble = false;
    private boolean isInt = false;
    private boolean isRDFLiteral = false;
    private boolean isRDFResource = false;
    private boolean isString = false;
    private boolean isURI = false;
    
    private boolean isTyped = false;
    
    private long valInt;
    private boolean valBoolean;
    private double valDouble;
    private Resource valRDFResource ; 
    private String valString;
    private String valURI;
    
    private Node m_node;
    private LiteralLabel m_literalLabel;
    private Literal m_literal;
    
    public KowariValue(Node node) {
        m_node = node;
        valString = null;
    	if (m_node.isLiteral()) {
    	    isRDFLiteral = true;
    		m_literalLabel = m_node.getLiteral();
    		valString = m_literalLabel.getLexicalForm();
            String lang = m_literalLabel.language();
            RDFDatatype datatype = m_literalLabel.getDatatype();
            
            if ( (lang != null) || (!lang.equals("")) ) {
                m_literal = new LiteralImpl(valString, lang, false);
            } else if (datatype != null) { // TODO: need to test for empty string?
                isTyped = true;
                m_literal = new LiteralImpl(Node.createLiteral(valString, lang, datatype));
            } else {
                m_literal = new LiteralImpl(valString);
            }
    	} else if (m_node.isBlank()) {
    	    System.out.println("*** isNode_Blank: " +m_node);
    	    isRDFResource = true;
    	    isURI = true;
    	    
    	    //FIXME this isn't right: we're not getting out what we put in.
    	    valURI = m_node.getBlankNodeId().toString();
    	    valRDFResource = ResourceFactory.createResource(valURI);
    	    //valString = m_node.getBlankNodeId().toString();
        } else if (m_node.isURI()) {
            isRDFResource = true;
            isURI = true;
            //valURI = valString = m_node.getURI();
            valURI = m_node.getURI();
            valRDFResource = ResourceFactory.createResource(valURI);
        }
        // TODO: handle m_node.isConcrete(), m_node.isVariable()?
        if (valString != null) {
            isString = true;
        }
    }
    
    public com.hp.hpl.jena.rdf.model.AnonId test() {
        return m_node.getBlankNodeId();
    }
    
    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isNumber()
     */
    public boolean isNumber() {
    	forceNumber();
        return isInt || isDouble;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isInt()
     */
    public boolean isInt() {
    	forceInt();
        return isInt;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isDouble()
     */
    public boolean isDouble() {
    	forceDouble();
        return isDouble;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isBoolean()
     */
    public boolean isBoolean() {
    	// TODO: finish this...
        return isBoolean;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isString()
     */
    public boolean isString() {
        return isString;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isURI()
     */
    public boolean isURI() {
        return isURI;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isRDFLiteral()
     */
    public boolean isRDFLiteral() {
        return isRDFLiteral;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#isRDFResource()
     */
    public boolean isRDFResource() {
        return isRDFResource;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getInt()
     */
    public long getInt() {
        if (!isInt) {
            throw new ValueException("Not an int: " + this);
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getDouble()
     */
    public double getDouble() {
        if (!isDouble || !isInt) {
            throw new ValueException("Not a double: " + this);
        }
        if (isInt) {
            return getInt();
        } else {
            if (isTyped) {
                return asNumber(m_literalLabel.getValue()).doubleValue();
            } else {
                return Double.parseDouble(m_literalLabel.getLexicalForm());
            }
        }
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getBoolean()
     */
    public boolean getBoolean() {
    	// TODO: finish this...
        if (!isBoolean) {
            throw new ValueException("Not a boolean: " + this);
        }
        Object value = m_node.getLiteral().getValue();
        return false;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getString()
     */
    public String getString() {
        if (!isString) {
            throw new ValueException("Not a string: " + this);
        }
        return m_literalLabel.getLexicalForm();
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getURI()
     */
    public String getURI() {
        if (!isURI) {
            throw new ValueException("Not a URI: " + this);
        }
        return valURI;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getRDFLiteral()
     */
    public Literal getRDFLiteral() {
        if (!isRDFLiteral) {
            throw new ValueException("Not a literal: " + this);
        }
        return m_literal;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#getRDFResource()
     */
    public Resource getRDFResource() {
        if (!isRDFResource) {
            throw new ValueException("Not an RDFResource: " + this);
        }
        return valRDFResource;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#asQuotedString()
     */
    public String asQuotedString() {
        if ( isInt ) return asUnquotedString() ;
        if ( isDouble ) return asUnquotedString() ;
        if ( isBoolean ) return asUnquotedString() ;
        if ( isRDFLiteral ) {
            StringBuffer sb = new StringBuffer() ;
            sb.append('"').append(getRDFLiteral().toString()).append('"') ;
            if ( ! getRDFLiteral().getLanguage().equals("") )
                sb.append('@').append(getRDFLiteral().getLanguage()) ;
            if ( getRDFLiteral().getDatatypeURI() != null )
                sb.append("^^<").append(getRDFLiteral().getDatatypeURI()).append(">") ; 
            return  sb.toString() ;
        }
        if ( isRDFResource) return "<"+asUnquotedString()+">" ;
        if ( isURI ) return "<"+asUnquotedString()+">" ;
        if ( isString ) return "\""+asUnquotedString()+"\"" ;

        return "literal:unknown" ;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#asUnquotedString()
     */
    public String asUnquotedString() {
        if ( isInt ) return Long.toString(getInt()) ;
        if ( isDouble ) return Double.toString(getDouble()) ;
        if ( isBoolean ) return (getBoolean()?"true":"false") ;
        if ( isRDFLiteral) return getRDFLiteral().getLexicalForm() ;
        if ( isRDFResource ) return getRDFResource().toString() ;
        if ( isURI ) return getURI() ;
        if ( isString ) return getString() ;

        return "literal:unknown" ;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Value#valueString()
     */
    public String valueString() {
        return asUnquotedString() ;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Printable#asInfixString()
     */
    public String asInfixString() {
        return asQuotedString();
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Printable#asPrefixString()
     */
    public String asPrefixString() {
        if ( isInt ) return "int:"+asUnquotedString() ;
        if ( isDouble ) return "double:"+asUnquotedString() ;
        if ( isBoolean ) return "boolean:"+asUnquotedString() ;
        if ( isRDFLiteral) {
            if ( getRDFLiteral().getDatatypeURI() == null )
                return "RDF:\""+getRDFLiteral().getLexicalForm()+"\"" ;
            return "RDF:\""+getRDFLiteral().getLexicalForm()+"\"^^"+getRDFLiteral().getDatatypeURI() ;
        }
        if ( isRDFResource ) return "RDF:<"+asUnquotedString()+">" ;
        if ( isURI ) return "URI:"+asUnquotedString() ;
        if ( isString ) return "string:"+asUnquotedString() ;

        return "literal:unknown" ;
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdql.Printable#print(java.io.PrintWriter, int)
     */
    public void print(PrintWriter pw, int level) {
        QueryPrintUtils.indent(pw, level) ;
        pw.println(this.asPrefixString()) ;
    }
    
    public String toString() {
        return asUnquotedString();
    }
    
    // Internal helper method to convert a value to number
    private Number asNumber(Object value) {
        if (value instanceof Number) {
            return ((Number)value);
        } else {
            throw new DatatypeFormatException(value.toString() + " is not a Number");
        }
    }
    
    private void forceInt() {
        if ( isInt || !isString ) return;
        try {
            valInt = Long.parseLong(valString);
            isInt = true;
            isDouble = true;
            valDouble = valInt;
        } catch (NumberFormatException e) { return ; }
    }

    private void forceDouble() {
        if ( isDouble || !isString ) return;
        try {
            valDouble = Double.parseDouble(valString);
            isDouble = true;
        } catch (NumberFormatException e) { return ; }
    }

    private void forceNumber() {
        if ( isInt || isDouble || !isString ) return;
        
        forceInt() ;
        if ( !isInt ) forceDouble() ;
    }
}
