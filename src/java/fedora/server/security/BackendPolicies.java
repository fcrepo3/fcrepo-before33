/*
 * Created on May 4, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fedora.server.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;


/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BackendPolicies {
	
	private static final String KEY_ALL = "all";
	private static final String SUBKEY_BASIC_AUTH = "basicAuth";
	private static final String SUBKEY_SSL = "ssl";
	private static final String SUBKEY_IPLIST = "iplist";
	private static final String ID = "PolicyId";
	private static final String DESCRIPTION = "description";
	private static final String ROLES = "roles";	
	private static final String ROLE = "role";
	private static final HashSet allowedSubkeys = new HashSet();
	static {
		allowedSubkeys.add(SUBKEY_BASIC_AUTH);
		allowedSubkeys.add(SUBKEY_SSL);
		allowedSubkeys.add(SUBKEY_IPLIST);
		//neither INCLUDED_ROLE nor EXCLUDED_ROLES belong in this set
	}

	private String inFilePath = null;
	private String outFilePath = null;	
	private Properties properties = null;
	private Hashtable policies = new Hashtable();
	
	public BackendPolicies(String inFilePath, String outFilePath) {
		this.inFilePath = inFilePath;
		this.outFilePath = outFilePath;		
	}
	
	public BackendPolicies(String inFilePath) {
		this(inFilePath, null);
	}
	
	private void readSpecs() throws Exception {
		File file = new File(inFilePath);
		FileInputStream fis = new FileInputStream(file);
		properties = new Properties();
		properties.load(fis);
		fis.close();		
		Enumeration enum = properties.keys();
		while (enum.hasMoreElements()) {
			String raw = (String) enum.nextElement();
			String data = properties.getProperty(raw);
			int i = raw.lastIndexOf('.');
			if ((i < 1) || ((raw.length()-1)  < i)) {
				throw new Exception();
			}
			String key = raw.substring(0, i);
			key = key.replaceAll("\\:", ":");
			String subkey = raw.substring(i+1);
			log(key + " | " + subkey + "|" + data);			
			if (! allowedSubkeys.contains(subkey)) {
				throw new Exception();				
			}
			Hashtable policy;
			if (policies.containsKey(key)) {
				policy = (Hashtable) policies.get(key);
			} else {
				policy = new Hashtable();
				policy.put(ROLE, key);
				if (KEY_ALL.equals(key)) {
					policy.put(DESCRIPTION, "policy guarding callbacks from non-specific backend services");
					policy.put(ID, "guard-callbacks-from-non-specific-backend-services");					
				} else {
					policy.put(DESCRIPTION, "policy guarding callbacks from backend service through " + key);									
					policy.put(ID, "guard-callbacks-from-backend-service-through-" + key);									
				}
				policies.put(key, policy); 
			}
			if (SUBKEY_IPLIST.equals(subkey)) {
				HashSet ips;
				if (policies.containsKey(subkey)) {
					ips = (HashSet) policy.get(subkey);
				} else {
					ips = new HashSet();
					policy.put(subkey, ips);
				}
				String[] regex = data.split("\\s");
				for (int ii=0; ii<regex.length; ii++) {
					if (regex[ii].length() > 0) {
						ips.add(regex[ii]);
					}
				}
			} else {
				if (policies.containsKey(subkey)) {
					//error
				} else {
					policy.put(subkey, data); 
				}
			}
		}
	}
	
	public Hashtable generateBackendPolicies() throws Exception {
		readSpecs();
		complete();
		return writePolicies();
	}
	
	private void completeDefaults() throws Exception {
		if (! policies.containsKey(KEY_ALL)) {
			throw new Exception("defaults weren't specified");
		}
		Hashtable policy = (Hashtable) policies.get(KEY_ALL);
		Iterator subkeys = allowedSubkeys.iterator();
		while (subkeys.hasNext()) {
			String subkey = (String) subkeys.next();
			if (! policy.containsKey(subkey)) {
				Object object = null;
				if (SUBKEY_BASIC_AUTH.equals(subkey)) {
					object = "false";
				} else if (SUBKEY_SSL.equals(subkey)) {
					object = "false";
				} else if (SUBKEY_IPLIST.equals(subkey)) {
					object = new HashSet();
				}
				policy.put(subkey, object);
			}
		}
	}
	
	private void complete() throws Exception{
		completeDefaults();
		HashSet roles = new HashSet();
		Hashtable defaults = (Hashtable) policies.get(KEY_ALL);
		Iterator it = policies.keySet().iterator();
		while (it.hasNext()) {
			String role = (String) it.next();
			if (! KEY_ALL.equals(role)) {
				roles.add(role);
			}
			Hashtable policy = (Hashtable) policies.get(role);
			Iterator it2 = allowedSubkeys.iterator();
			while (it2.hasNext()) {
				String subkey = (String) it2.next();
				if (! policy.containsKey(subkey)) {
					policy.put(subkey, defaults.get(subkey));
				}
			}
		}
		defaults.put(ROLES, roles);
	}
	
	private Hashtable writePolicies() throws Exception {
		StringBuffer sb = null;
		StringBuffer sb2 = null;
		Hashtable tempfiles = new Hashtable();
		Iterator it = policies.keySet().iterator();
		while (it.hasNext()) {
			sb = new StringBuffer();
			sb2 = new StringBuffer();
			String key = (String) it.next();
			Hashtable policy = (Hashtable) policies.get(key);
			String role = (String) policy.get(ROLE);
			String id = (String) policy.get(ID);
			sb.append("<Policy xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\" PolicyId=\"" + id + "\">\n");
			String desc = (String) policy.get(DESCRIPTION);
			if ((desc != null) && (! "".equals(desc))) {
				sb.append("\t<Description>" + desc + "</Description>\n");
			}
			sb.append("\t<Target>\n");
			sb.append("\t\t<Subjects>\n");
			if (KEY_ALL.equals(role)) {
				sb.append("\t\t\t<AnySubject/>\n");				
			} else {
				sb.append("\t\t\t<Subject>\n");
				sb.append("\t\t\t\t<SubjectMatch>\n");
				sb.append("\t\t\t\t\t<AttributeValue>" + role + "</AttributeValue>\n");
				sb.append("\t\t\t\t</SubjectMatch>\n");				
				sb.append("\t\t\t</Subject>\n");				
			}
			sb.append("\t\t</Subjects>\n");
			sb.append("\t</Target>\n");
			
			HashSet roles = (HashSet) policy.get(ROLES);
			if (roles != null) {
				sb2.append("\t\t<ExcludedRoles>\n");
				Iterator roleIterator = roles.iterator();
				while (roleIterator.hasNext()) {
					String excludedRole = (String) roleIterator.next();
					sb2.append("\t\t\t<ExcludedRole>");
					sb2.append(excludedRole);
					sb2.append("</ExcludedRole>\n");				
				}
				sb2.append("\t\t</ExcludedRoles>\n");				
			}
			sb.append("\t<Rule RuleId=\"rule-1\" Effect=\"Permit\">\n");
			sb.append(sb2);
			if ("true".equals(policy.get(SUBKEY_BASIC_AUTH))) {
				sb.append("\t\t<AuthnRequired/>\n");				
			}
			if ("true".equals(policy.get(SUBKEY_SSL))) {
				sb.append("\t\t<SslRequired/>\n");				
			}
			HashSet ipRegexes = (HashSet) policy.get(SUBKEY_IPLIST);
			if ((ipRegexes != null) && ! ipRegexes.isEmpty()) {
				sb.append("\t\t<IpRegexes>\n");
				Iterator ipRegexIterator = ipRegexes.iterator();
				while (ipRegexIterator.hasNext()) {
					String ipRegex = (String) ipRegexIterator.next();
					sb.append("\t\t\t<IpRegex>");
					sb.append(ipRegex);
					sb.append("</IpRegex>\n");				
				}
				sb.append("\t\t</IpRegexes>\n");			
			}
			sb.append("\t</Rule>\n");

			if (("true".equals(policy.get(SUBKEY_BASIC_AUTH))) 
			||  ("true".equals(policy.get(SUBKEY_SSL))) 
			||  ((ipRegexes != null) && ! ipRegexes.isEmpty())) {
				sb.append("\t<Rule RuleId=\"rule-2\" Effect=\"Deny\">\n");
				sb.append(sb2);
				sb.append("\t</Rule>\n");
			}
			sb.append("</Policy>\n");
			String filename = id.replace(':','-');
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
