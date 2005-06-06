/*
 * Created on May 4, 2005
 *
 */
package fedora.server.security;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
//import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import fedora.common.PID;

/**
 * @author wdn5e
 */
public class BackendPolicies {

	/*private static final String SUBKEY_BASIC_AUTH = "basicAuth";
	private static final String SUBKEY_SSL = "ssl";
	private static final String SUBKEY_IPLIST = "iplist";
	private static final HashSet allowedSubkeys = new HashSet();
	static {
		allowedSubkeys.add(SUBKEY_BASIC_AUTH);
		allowedSubkeys.add(SUBKEY_SSL);
		allowedSubkeys.add(SUBKEY_IPLIST);
		//neither INCLUDED_ROLE nor EXCLUDED_ROLES belong in this set
	}*/

	private String inFilePath = null;
	private String outFilePath = null;	
	private BackendSecuritySpec backendSecuritySpec = null; 
	
	public BackendPolicies(String inFilePath, String outFilePath) {
		this.inFilePath = inFilePath;
		this.outFilePath = outFilePath;		
	}
	
	public BackendPolicies(String inFilePath) {
		this(inFilePath, null);
	}	
	
	public Hashtable generateBackendPolicies() throws Exception {
    	log("in BackendPolicies.generateBackendPolicies() 1");			
		Hashtable tempfiles = null;
		if (inFilePath.endsWith(".xml")) { // replacing code for .properties
	    	log("in BackendPolicies.generateBackendPolicies() .xml 1");			
			BackendSecurityDeserializer bds = new BackendSecurityDeserializer("UTF-8", false);
	    	log("in BackendPolicies.generateBackendPolicies() .xml 2");			
			backendSecuritySpec = bds.deserialize(inFilePath);
	    	log("in BackendPolicies.generateBackendPolicies() .xml 3");			
			tempfiles = writePolicies();
	    	log("in BackendPolicies.generateBackendPolicies() .xml 4");						
		}
		return tempfiles;
	}
	
	private static final String[] parseForSlash(String key) throws Exception {
		int lastSlash = key.lastIndexOf("/");
		if (lastSlash+1 == key.length()) {
			throw new Exception("BackendPolicies.newWritePolicies() " + "can't handle key ending with '/'");
		}
		if (lastSlash != key.indexOf("/")) {
			throw new Exception("BackendPolicies.newWritePolicies() " + "can't handle key containing multiple instances of '/'");				
		}
		String[] parts = null;
		if ((-1 < lastSlash) && (lastSlash < key.length())) {
			parts = key.split("/");
		} else {
			parts = new String[] {key};			
		}
		return parts;
	}
	
	private static final String getExcludedRolesText(String key, Set roles) {
		StringBuffer excludedRolesText = new StringBuffer();
		if ("default".equals(key) && (roles.size() > 1)) {
			excludedRolesText.append("\t\t<ExcludedRoles>\n");
			Iterator excludedRoleIterator = roles.iterator();
			while (excludedRoleIterator.hasNext()) {
		    	slog("in BackendPolicies.newWritePolicies() another inner it");			
				String excludedRole = (String) excludedRoleIterator.next();
				if ("default".equals(excludedRole)) {
					continue;
				}					
		    	slog("in BackendPolicies.newWritePolicies() excludedRole=" + excludedRole);					
				excludedRolesText.append("\t\t\t<ExcludedRole>");
				excludedRolesText.append(excludedRole);
				excludedRolesText.append("</ExcludedRole>\n");				
			}
			excludedRolesText.append("\t\t</ExcludedRoles>\n");				
		}
		return excludedRolesText.toString();
	}
	
	private static final String writeRules(String callbackBasicAuth, String callbackSsl, String iplist, String role, Set roles) throws Exception {
		StringBuffer temp = new StringBuffer();
		temp.append("\t<Rule RuleId=\"1\" Effect=\"Permit\">\n");
		temp.append(getExcludedRolesText(role, roles));
		if ("true".equals(callbackBasicAuth)) {
			temp.append("\t\t<AuthnRequired/>\n");				
		}
		if ("true".equals(callbackSsl)) {
			temp.append("\t\t<SslRequired/>\n");				
		}
		slog("DEBUGGING IPREGEX0 [" + iplist + "]");
		String[] ipRegexes = new String[0];
		if ((iplist != null) && ! "".equals(iplist.trim())) {
			ipRegexes = iplist.trim().split("\\s");
		}
		/*
		if (ipRegexes.length == 1) { //fixup
			ipRegexes[0] = ipRegexes[0].trim();
		}
		*/
		slog("DEBUGGING IPREGEX1 [" + iplist.trim() + "]");		
		if (ipRegexes.length != 0) {
			temp.append("\t\t<IpRegexes>\n");
			for (int i = 0; i < ipRegexes.length; i++) {
				slog("DEBUGGING IPREGEX2 " + ipRegexes[i]);
				temp.append("\t\t\t<IpRegex>");
				temp.append(ipRegexes[i]);
				temp.append("</IpRegex>\n");				
			}
			temp.append("\t\t</IpRegexes>\n");			
		}
		temp.append("\t</Rule>\n");		
		if (("true".equals(callbackBasicAuth)) 
				||  ("true".equals(callbackSsl)) 
				||  (ipRegexes.length != 0)) {
					temp.append("\t<Rule RuleId=\"2\" Effect=\"Deny\">\n");
					temp.append(getExcludedRolesText(role, roles));
					temp.append("\t</Rule>\n");
				}
		
		return temp.toString();
	}

	private Hashtable writePolicies() throws Exception {
    	log("in BackendPolicies.newWritePolicies() 1");			
		StringBuffer sb = null;
		Hashtable tempfiles = new Hashtable();
		Iterator coarseIterator = backendSecuritySpec.listRoleKeys().iterator();
		while (coarseIterator.hasNext()) {
			String key = (String) coarseIterator.next();
			String[] parts = parseForSlash(key);
			String filename1 = "";
			String filename2 = "";
			switch (parts.length) {
				case 2:
					filename2 = "-method-" + parts[1];	
					//break purposely absent:  fall through
				case 1: 
			    	if (-1 == parts[0].indexOf(":")) {
		    			filename1 = "callback-by:" + parts[0];			    			
			    	} else {
						filename1 = "callback-by-bmech-" + parts[0];			    		
			    	}
			    	if ("".equals(filename2)) {
			    		if (! "default".equals(parts[0])) {
			    			filename2 = "-other-methods";
			    		}
			    	}
					break;
				default:
					//bad value
					throw new Exception("BackendPolicies.newWritePolicies() " + "didn't correctly parse key " + key);
			}
			sb = new StringBuffer();
	    	log("in BackendPolicies.newWritePolicies() another outer it, key=" + key);			
			Hashtable coarseProperties = backendSecuritySpec.getSecuritySpec(key);	
	    	log("in BackendPolicies.newWritePolicies() coarseProperties.size()=" + coarseProperties.size());
	    	log("in BackendPolicies.newWritePolicies() coarseProperties.get(BackendSecurityDeserializer.ROLE)=" + coarseProperties.get(BackendSecurityDeserializer.ROLE));				    	
			String coarseCallbackBasicAuth = (String) coarseProperties.get(BackendSecurityDeserializer.CALLBACK_BASIC_AUTH);
			if (coarseCallbackBasicAuth == null) {
				coarseCallbackBasicAuth = "false";
			}
			String coarseCallBasicAuth = (String) coarseProperties.get(BackendSecurityDeserializer.CALL_BASIC_AUTH);
			if (coarseCallBasicAuth == null) {
				coarseCallBasicAuth = "false";
			}			
			String coarseCallUsername = "";
			String coarseCallPassword = "";			
			if ("true".equals(coarseCallbackBasicAuth)) {
				coarseCallUsername = (String) coarseProperties.get(BackendSecurityDeserializer.CALL_USERNAME);
				coarseCallPassword = (String) coarseProperties.get(BackendSecurityDeserializer.CALL_PASSWORD);
			}
			if (coarseCallUsername == null) {
				coarseCallUsername = "";
			}			
			if (coarseCallPassword == null) {
				coarseCallPassword = "";
			}						
	    	log("in BackendPolicies.newWritePolicies() callbackBasicAuth=" + coarseCallbackBasicAuth);			
			String coarseCallbackSsl = (String) coarseProperties.get(BackendSecurityDeserializer.CALLBACK_SSL);
			if (coarseCallbackSsl == null) {
				coarseCallbackSsl = "false";
			}
			String coarseCallSsl = (String) coarseProperties.get(BackendSecurityDeserializer.CALL_SSL);
			if (coarseCallSsl == null) {
				coarseCallSsl = "false";
			}			
	    	log("in BackendPolicies.newWritePolicies() callbackSsl=" + coarseCallbackSsl);			
			String coarseIplist = (String) coarseProperties.get(BackendSecurityDeserializer.IPLIST);
			if (coarseIplist == null) {
				coarseIplist = "";
			}
	    	log("in BackendPolicies.newWritePolicies() coarseIplist=" + coarseIplist);			
			String id = "generated_for_" + key.replace(':','-');
	    	log("in BackendPolicies.newWritePolicies() id=" + id);
	    	log("in BackendPolicies.newWritePolicies() " + filename1 + " " + filename2);
	    	String filename = filename1 + filename2;  //was id.replace(':','-');
	    	log("in BackendPolicies.newWritePolicies() " + filename);	    	
	    	PID tempPid = new PID(filename);
	    	log("in BackendPolicies.newWritePolicies() got PID " + tempPid);
	    	filename = tempPid.toFilename();
	    	log("in BackendPolicies.newWritePolicies() filename=" + filename);			
			sb.append("<Policy xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\" PolicyId=\"" + id + "\">\n");
			sb.append("\t<Description>this policy is machine-generated at each Fedora server startup.  edit beSecurity.xml to change this policy.</Description>\n");
			sb.append("\t<Target>\n");
			sb.append("\t\t<Subjects>\n");
			if ("default".equals(key)) {
				sb.append("\t\t\t<AnySubject/>\n");				
			} else {
				sb.append("\t\t\t<Subject>\n");
				sb.append("\t\t\t\t<SubjectMatch>\n");
				sb.append("\t\t\t\t\t<AttributeValue>" + key + "</AttributeValue>\n");
				sb.append("\t\t\t\t</SubjectMatch>\n");				
				sb.append("\t\t\t</Subject>\n");				
			}
			sb.append("\t\t</Subjects>\n");
			sb.append("\t</Target>\n");

			String temp = writeRules(coarseCallbackBasicAuth, coarseCallbackSsl, coarseIplist, key, backendSecuritySpec.listRoleKeys());
			sb.append(temp);

			sb.append("</Policy>\n");
			log("\ndumping policy\n" + sb + "\n");
			File outfile = null;
			if (outFilePath == null) {
				outfile = File.createTempFile(filename,".xml");
			} else {
				outfile = new File(outFilePath + File.separator + filename + ".xml");
			}
			tempfiles.put(filename + ".xml", outfile.getAbsolutePath());
			PrintStream pos = new PrintStream(new FileOutputStream(outfile));
			pos.println(sb);
			pos.close();
		}	
		log("finished writing temp files");
		return tempfiles;
	}
		
	  private static boolean log = false;
	  
	  private final void log(String msg) {
	  	if (log) {
		  	System.err.println(msg);	  		
	  	}
	  }
	  
	  private static boolean slog = false;
	  
	  private static final void slog(String msg) {
	  	if (slog) {
		  	System.err.println(msg);	  		
	  	}
	  }	
	
	public static void main(String[] args) throws Exception {
		BackendPolicies backendPolicies = new BackendPolicies(args[0], args[1]);
		backendPolicies.generateBackendPolicies();
	}
}
