package fedora.server.search;

import java.util.ArrayList;
import java.util.List;

import fedora.server.errors.InvalidOperatorException;
import fedora.server.errors.QueryParseException;

public class Condition {
        
    private String m_property;
    private Operator m_operator;
    private String m_value;
    
    public Condition(String property, Operator operator, String value) {
        m_property=property;
        m_operator=operator;
        m_value=value;
    }
    
    public Condition(String property, String operator, String value) 
            throws InvalidOperatorException {
        m_property=property;
        if (operator.equals("eq")) {
            m_operator=new Operator("=", "eq");
        } else if (operator.equals("has")) {
            m_operator=new Operator("~", "has");
        } else if (operator.equals("gt")) {
            m_operator=new Operator(">", "gt");
        } else if (operator.equals("ge")) {
            m_operator=new Operator(">=", "ge");
        } else if (operator.equals("lt")) {
            m_operator=new Operator("<", "lt");
        } else if (operator.equals("le")) {
            m_operator=new Operator("<=", "le");
        } else {
            throw new InvalidOperatorException("Operator, '" + operator + "' does "
                    + "not match one of eq, has, gt, ge, lt, or le.");
        }
        m_value=value;
    }

    /**
     * Gets a List of Conditions from a string like: a=x b~'that\'s' c>='z'
     *
     * @param query The query string.
     * @return The Conditions.
     */
    public static List getConditions(String query) 
            throws QueryParseException {
        Operator EQUALS=new Operator("=", "eq");
        Operator CONTAINS=new Operator("~", "has");
        Operator GREATER_THAN=new Operator(">", "gt");
        Operator GREATER_OR_EQUAL=new Operator(">=", "ge");
        Operator LESS_THAN=new Operator("<", "lt");
        Operator LESS_OR_EQUAL=new Operator("<=", "le");
        StringBuffer prop=new StringBuffer();
        Operator oper=null;
        StringBuffer val=new StringBuffer();
        ArrayList ret=new ArrayList();
        boolean inProp=true;
        boolean inValue=false;
        boolean firstValueChar=false;
        boolean valueStartsWithQuote=false;
        for (int i=0; i<query.length(); i++) {
            char c=query.charAt(i);
            if (inProp) {
                if (c==' ') {
                    throw new QueryParseException("Found <space> at character " + i 
                            + " but expected <operator> or <alphanum>");
                } else if (c=='=') {
                    oper=EQUALS;
                    inProp=false;
                    inValue=true;
                    firstValueChar=true;
                } else if (c=='~') {
                    oper=CONTAINS;
                    inProp=false;
                    inValue=true;
                    firstValueChar=true;
                } else if (c=='>') {
                    if (i+1<query.length()) {
                        char d=query.charAt(i+1);
                        if (d=='=') {
                            i++;
                            oper=GREATER_OR_EQUAL;
                        } else {
                            oper=GREATER_THAN;
                        }
                        inProp=false;
                        inValue=true;
                        firstValueChar=true;
                    } else {
                        throw new QueryParseException("Found <end-of-string> "
                                + "immediately following '>' operator, but "
                                + "expected a value.");
                    }
                } else if (c=='<') {
                    if (i+1<query.length()) {
                        char d=query.charAt(i+1);
                        if (d=='=') {
                            i++;
                            oper=LESS_OR_EQUAL;
                        } else {
                            oper=LESS_THAN;
                        }
                        inProp=false;
                        inValue=true;
                        firstValueChar=true;
                    } else {
                        throw new QueryParseException("Found <end-of-string> "
                                + "immediately following '<' operator, but "
                                + "expected a value.");
                    }
                } else {
                    prop.append(c);
                }
            } else if (inValue) {
                if (prop.toString().length()==0) {
                    throw new QueryParseException("Found "
                            + "operator but expected a non-zero length "
                            + "property.");
                }
                if (firstValueChar) {
                    // allow ', and mark it if it's there, add one to i
                    if (c=='\'') {
                        i++;
                        if (i>=query.length()) {
                            throw new QueryParseException("Found <end-of-string> "
                                    + "immediately following start quote, but "
                                    + "expected a value.");
                        }
                        c=query.charAt(i);
                        valueStartsWithQuote=true;
                    }
                    firstValueChar=false;
                }
                if (c=='\'') {
                    if (!valueStartsWithQuote) {
                        throw new QueryParseException("Found ' character in "
                                + "value at position " + i + ", but the value "
                                + "did not start with a string, so this can't "
                                + " be a value terminator.");
                    }
                    // end of value part
                    // next must be space or empty... check
                    i++;
                    if (i<query.length()) {
                        if (query.charAt(i)!=' ') {
                            throw new QueryParseException("Found value-terminator "
                                    + "' but it was not followed by <end-of-string> "
                                    + "or <space>.");
                        }
                    }
                    ret.add(new Condition(prop.toString(), oper, val.toString()));
                    prop=new StringBuffer();
                    oper=null;
                    val=new StringBuffer();
                    inValue=false;
                    inProp=true;
                    valueStartsWithQuote=false;
                } else if (c=='\\') {
                    i++;
                    if (i>=query.length()) {
                        throw new QueryParseException("Found character-escaping "
                                + "character as last item in string.");
                    }
                    val.append(query.charAt(i));
                } else if (c==' ') {
                    // end of value part... or inside string?
                    if (valueStartsWithQuote) {
                        // was inside string..ok
                        val.append(c);
                    } else {
                        // end of value part...cuz not quotes
                        ret.add(new Condition(prop.toString(), oper, val.toString()));
                        prop=new StringBuffer();
                        oper=null;
                        val=new StringBuffer();
                        inValue=false;
                        inProp=true;
                    }
                } else if (c=='=') {
                    throw new QueryParseException("Found <operator> at position " 
                            + i + ", but expected <value>");
                } else if (c=='~') {
                    throw new QueryParseException("Found <operator> at position " 
                            + i + ", but expected <value>");
                } else if (c=='>') {
                    throw new QueryParseException("Found <operator> at position " 
                            + i + ", but expected <value>");
                } else if (c=='<') {
                    throw new QueryParseException("Found <operator> at position " 
                            + i + ", but expected <value>");
                } else {
                    val.append(c);
                }
            }
        }
        if (inProp) {
            if (prop.toString().length()>0) {
                throw new QueryParseException("String ended before operator "
                        + "was found");
            }
        }
        if (inValue) {
            if (valueStartsWithQuote) {
                throw new QueryParseException("String ended before quoted value"
                        + "'s ending quote.");
            }
            ret.add(new Condition(prop.toString(), oper, val.toString()));
        }
        return ret;
    }
    
    public String getProperty() {
        return m_property;
    }
    
    public Operator getOperator() {
        return m_operator;
    }
    
    public String getValue() {
        return m_value;
    }
    
    public static void main(String[] args) {
        try {
            List l=Condition.getConditions(args[0]);
            for (int i=0; i<l.size(); i++) {
                Condition c=(Condition) l.get(i);
                System.out.println("<" + c.getOperator().getAbbreviation() 
                        + " prop=\"" + c.getProperty() + "\" val=\"" + c.getValue() + "\" />");
            }
        } catch (QueryParseException qpe) {
            System.out.println("PARSE ERROR: " + qpe);
        }
    }
    
}