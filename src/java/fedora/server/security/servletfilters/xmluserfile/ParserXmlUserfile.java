package fedora.server.security.servletfilters.xmluserfile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.server.security.servletfilters.FinishedParsingException;

public class ParserXmlUserfile 
        extends DefaultHandler {
	
    protected static Log log = LogFactory.getLog(ParserXmlUserfile.class);

    private SAXParser m_parser;
    private InputStream m_xmlStream;
	
	
    public ParserXmlUserfile(InputStream xmlStream) throws IOException {
		log.fatal(this.getClass().getName() + ".init<> " + " begin");			

        m_xmlStream = xmlStream;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
    		log.fatal(this.getClass().getName() + ".init<> " + " after newInstance");			
            spf.setNamespaceAware(true);
    		log.fatal(this.getClass().getName() + ".init<> " + " after setNamespaceAware");			
            m_parser=spf.newSAXParser();
    		log.fatal(this.getClass().getName() + ".init<> " + " after newSAXParser");			
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error getting XML parser: " + e.getMessage());
        } catch (Throwable t) {
    		log.fatal(this.getClass().getName() + ".init<> " + " caught me throwable");			        	
			t.printStackTrace();
			log.fatal(this.getClass().getName() + ".populateCacheElement() " +  t);			
			log.fatal(this.getClass().getName() + ".populateCacheElement() " + t.getMessage() + " " + 
					((t.getCause() == null) ? "" : t.getCause().getMessage()));			
        }
    }
    
    private Set validRoles = null;

    private String username = null;
    private String password = null;
    private Boolean authenticated = null;

    private Map namedAttributes = null;
    private Set roles = null;
    
    private String attributeName = null;
    private Set attributeValues = null;
    
    public final Boolean getAuthenticated() {
    	return authenticated;
    }

    public final Set getUserRoles() {
    	return roles;
    }

    public final Map getNamedAttributes() {
    	return namedAttributes;
    }
    
    private StringBuffer value = null;
    private boolean inValue = false;
    
    private boolean foundUser = false;
	
    public void startElement(String uri, 
            String localName, 
            String qName,
            Attributes a) throws SAXException {
		if (localName.equals("users")) {
			System.err.println("<users> foundUser==" + foundUser);
		} else if (localName.equals("role")) {
			System.err.println("<role> foundUser==" + foundUser);
			validRoles.add(a.getValue("rolename"));
		} else if (localName.equals("user")) {
			System.err.println("<user> foundUser==" + foundUser);
			System.err.println("<<user>> this node username==" + a.getValue("name") + " password==" + a.getValue("password"));
			if (username.equals(a.getValue("name"))) {
				foundUser = true;
				authenticated =  Boolean.valueOf((password != null) && password.equals(a.getValue("password")));
				String rolesCSV = a.getValue("roles");
				if (rolesCSV != null) {
					String[] rolesArray = rolesCSV.split(",");
					for (int i = 0; i < rolesArray.length; i++) {
						String consideredRole = rolesArray[i];
						if (validRoles.contains(consideredRole) && ! roles.contains(consideredRole)) {
							roles.add(consideredRole);
						}
					}
				}
			}
		} else if (localName.equals("attribute")) {
			System.err.println("<attribute> foundUser==" + foundUser);
			if (foundUser) {
				attributeName = a.getValue("name");				
				attributeValues = new HashSet();
				System.err.println("attributeName==" + attributeName);
			}
		} else if (localName.equals("value")) {
			System.err.println("<value> foundUser==" + foundUser);
			inValue = true;
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("users")) {
			System.err.println("</users> foundUser==" + foundUser);
			authenticated =  Boolean.FALSE;
		} else if (localName.equals("roles")) {
				System.err.println("</roles> foundUser==" + foundUser);			
		} else if (localName.equals("user")) {
			System.err.println("</user> foundUser==" + foundUser);
			if (foundUser) {
				System.err.println("at </user> (quick audit)");
				System.err.println("authenticated==" + authenticated);
				System.err.println("roles n=="  + roles.size());
				System.err.println("namedAttributes n=="  + namedAttributes.size());
//System.err.println("namedAttributes[group] n=="  + ((Set)namedAttributes.get("group")).size());
				throw new FinishedParsingException("");				
			}
		} else if (localName.equals("attribute")) {
			System.err.println("</attribute> foundUser==" + foundUser);
			if (foundUser) {
				System.err.println("set n=="  + attributeValues.size());
				namedAttributes.put(attributeName,attributeValues);
				System.err.println("just added values for " + attributeName);
			}
			attributeName = null;
			attributeValues = null;
		} else if (localName.equals("value")) {
			System.err.println("</value> foundUser==" + foundUser);
			if (foundUser) {
				attributeValues.add(value.toString());
				System.err.println("just added " + value);
			}
			System.err.println("quick audit of value string =="  + value);
			value.setLength(0);
			inValue = false;
		} 
	}

	public void characters(char[] ch, int start, int length) {
		if (inValue && foundUser && (value != null)) {
			value.append(ch, start, length);
			System.err.println("characters called start=="  + start + " length==" + length);
		}
	}
	
	public void parse(String username, String password) throws IOException, FinishedParsingException {
    	this.username = username;
    	this.password = password;    	
	    try {
	    	validRoles = new HashSet();
			value = new StringBuffer();	    	
	    	authenticated = null;
	    	roles =  new HashSet();
	    	namedAttributes = new Hashtable();
	    	foundUser = false;
	        m_parser.parse(m_xmlStream, this);
	    } catch (FinishedParsingException fpe) {
	    	throw fpe;
	    } catch (Throwable e) {
    		e.printStackTrace();
    		throw new IOException("Error parsing XML: " + e.getMessage());
	    }
	}

}
