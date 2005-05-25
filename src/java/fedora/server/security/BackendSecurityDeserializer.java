package fedora.server.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.server.errors.GeneralException;
import fedora.server.errors.StreamIOException;

/**
 * <p><b>Title:</b> BackendSecurityDeserializer.java</p>
 * <p><b>Description:</b> 
 *       SAX parser to deserialize the beSecurity XML file 
 * 		 that contains configuration properties for backend services. </p>
 *
 * @author payette@cs.cornell.edu
 * @version 
 */
public class BackendSecurityDeserializer extends DefaultHandler {

	private String inFilePath = null;
	private String outFilePath = null;	
	
	/** The namespace and attributes in the beSecurity spec file */
	public static final String BE="info:fedora/fedora-system:def/beSecurity#";
	
	/** Attribute names in the beSecurity spec file */	
	public static final String CALL_BASIC_AUTH = "callBasicAuth";
	public static final String CALL_SSL = "callSSL";
	public static final String CALL_USERNAME = "callUsername";
	public static final String CALL_PASSWORD = "callPassword";
	public static final String CALLBACK_BASIC_AUTH = "callbackBasicAuth";
	public static final String CALLBACK_SSL = "callbackSSL";
	public static final String CALLBACK_USERNAME = "callbackUsername";
	public static final String CALLBACK_PASSWORD = "callbackPassword";
	public static final String IPLIST = "iplist";
	public static final String ROLE = "role";
	
	/** Target objects for deserialization. */
	private BackendSecuritySpec beSS;
	private Hashtable beProperties = new Hashtable();

    
	/** Temp variables for SAX parse */
	private SAXParser tmp_parser;
	private String tmp_characterEncoding;
	private HashMap tmp_prefixMap;
	private boolean tmp_rootElementFound;
	private int tmp_level;
	private String tmp_parentRole;
	private Hashtable tmp_rootProperties;
	private Hashtable tmp_serviceProperties;
	private String tmp_role;
	
	
	public BackendSecurityDeserializer(String characterEncoding, boolean validate)
			throws FactoryConfigurationError, ParserConfigurationException,
			SAXException, UnsupportedEncodingException {
				
		tmp_characterEncoding=characterEncoding;
		StringBuffer buf=new StringBuffer();
		buf.append("test");
		byte[] temp=buf.toString().getBytes(tmp_characterEncoding);
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(validate);
		spf.setNamespaceAware(true);
		tmp_parser=spf.newSAXParser();
		
		// set up objects for the parsed information
		beSS = new BackendSecuritySpec();
		beProperties = new Hashtable();
	}
	
	public BackendSecuritySpec deserialize(String inFilePath)
			throws GeneralException, StreamIOException, UnsupportedEncodingException {
            	
		if (fedora.server.Debug.DEBUG) System.out.println("Parsing beSecurity file...");
		System.out.println("Parsing beSecurity file " + inFilePath);
				 
		tmp_level=0;
		try {
			FileInputStream fis = new FileInputStream(new File(inFilePath));
			tmp_parser.parse(fis, this);
		} catch (IOException ioe) {
			throw new StreamIOException("BackendSecurityDeserializer: "
				+ "Stream IO problem while parsing backend security config file.");
		} catch (SAXException se) {
			throw new GeneralException("BackendSecurityDeserializer: "
				+ "Error parsing backend security config file. " + se.getMessage());
		}
		if (!tmp_rootElementFound) {
			throw new GeneralException("BackendSecurityDeserializer: "
				+ "Root element not found in backend security config file.");
		}
		 
		if (fedora.server.Debug.DEBUG) System.out.println("Parse successful.");
		System.out.println("Parse successful.");  
		return beSS;    
	}

/**	
	public void startPrefixMapping(String prefix, String uri) {
		tmp_prefixMap.put(prefix, uri);
	}

	public void endPrefixMapping(String prefix) {
		tmp_prefixMap.remove(prefix);
	}
**/
	
	public void startElement(String uri, String localName, String qName, Attributes a) 
		throws SAXException {

		if (uri.equals(BE) && localName.equals("serviceSecurityDescription")) {
			System.out.println("============================================");
			System.out.println("start element uri=" + uri + " localName=" + localName + " tmp_level=" + tmp_level);
			
			tmp_role = grab(a, BE, ROLE);
			beProperties = new Hashtable();
			System.out.println("tmp_role = " + tmp_role);				
			setProperty(CALL_BASIC_AUTH, grab(a, BE, CALL_BASIC_AUTH));
			setProperty(CALL_SSL, grab(a, BE, CALL_SSL));
			setProperty(CALL_USERNAME, grab(a, BE, CALL_USERNAME));
			setProperty(CALL_PASSWORD, grab(a, BE, CALL_PASSWORD));
			setProperty(CALLBACK_BASIC_AUTH, grab(a, BE, CALLBACK_BASIC_AUTH));
			setProperty(CALLBACK_SSL, grab(a, BE, CALLBACK_SSL));
			setProperty(CALLBACK_USERNAME, grab(a, BE, CALLBACK_USERNAME));
			setProperty(CALLBACK_PASSWORD, grab(a, BE, CALLBACK_PASSWORD));
			setProperty(IPLIST, grab(a, BE, IPLIST));
			
			try {
				if (tmp_level == 0) {
					tmp_rootElementFound=true;
					tmp_rootProperties = new Hashtable();
					tmp_rootProperties.putAll(beProperties);
					beSS.setDefaultSecuritySpec(beProperties);
				} else if (tmp_level == 1){
					tmp_parentRole = tmp_role;
					tmp_serviceProperties = new Hashtable();
					tmp_serviceProperties.putAll(beProperties);
					inheritProperties(tmp_rootProperties);
					beSS.setSecuritySpec(tmp_role, null, beProperties);
				} else if (tmp_level == 2){
					inheritProperties(tmp_serviceProperties);
					inheritProperties(tmp_rootProperties);
					beSS.setSecuritySpec(tmp_parentRole, tmp_role, beProperties);					
				} else {
					System.out.println("ERROR: xml depth exceeded for serviceSecurityDescription");
					throw new SAXException("BackendSecurityDeserializer: "
						+ "serviceSecurityDescription elements can only "
						+ "be nested two levels deep from root element!");
				}
			} catch (Exception e) {
				throw new SAXException("BackendSecurityDeserializer: "
					+ "Error setting properties for role " + tmp_role);
			}
			tmp_level++;				
		}
	}
		
	public void endElement(String uri, String localName, String qName) {
		System.out.println("end element uri=" + uri + " localName=" + localName + " tmp_level=" + tmp_level);
		
		if (uri.equals(BE) && localName.equals("serviceSecurityDescription")) {
			tmp_level--;
			
			System.out.println("end element serviceSecurityDescription uri=" + uri + " localName=" + localName + " tmp_level=" + tmp_level);
		}
	}

	private static String grab(Attributes a, String namespace, String elementName) {
		String ret=a.getValue(namespace, elementName);
		if (ret==null) {
			ret=a.getValue(elementName);
		}
		return ret;
	}
	
	private void setProperty(String key, String value){
		if (key != null && value != null){
			System.out.println("Setting propery.  key=" + key + " value=" + value);
			beProperties.put(key, value);					
		}		
	}
	
	private void inheritProperties(Hashtable inheritableProperties){
		
		// overlay the current properties on the inheritable properties.
		// Effect will be to add properties not originally specificed by
		// in the inheritable properties, any properties that were
		// already in the inheritable properties with the values set in
		// the current properties.
		//beProperties.putAll(inheritableProperties);
		System.out.println("Setting inherited properties...");
		Iterator it = inheritableProperties.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (!beProperties.containsKey(key)){
				setProperty(key, (String) inheritableProperties.get(key));
			}

		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("BackendSecurityDeserializer start...");
		BackendSecurityDeserializer bds = new BackendSecurityDeserializer("UTF-8", false);
		BackendSecuritySpec beSS = bds.deserialize(args[0]);
		
		// Let's see all the stuff...
		Set allRoleKeys = beSS.listRoleKeys();	
		Iterator iterRoles = allRoleKeys.iterator();
		while (iterRoles.hasNext()) {
			String roleKey = (String) iterRoles.next();
			System.out.println("************ ROLEKEY = " + roleKey);
			// let's see all the properties for this role...	
			Hashtable roleProperties = (Hashtable)beSS.getSecuritySpec(roleKey);	
			Iterator iterProps = roleProperties.keySet().iterator();
			while (iterProps.hasNext()) {
				String propKey = (String) iterProps.next();
				String propValue = (String)roleProperties.get(propKey);
				System.out.println(propKey + "=" + propValue);
			}
		}
	}
}
