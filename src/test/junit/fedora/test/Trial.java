
package fedora.test;  

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fedora.client.FedoraClient;
import java.util.Iterator;
import org.apache.commons.httpclient.HttpClient;

/**
 * @author Bill Niebel 
 */
public class Trial {
	String policies = null;
	String baseurl = null;
	String username = null;
	String password = null;
	Trial(String policies, String baseurl, String username, String password) {
		this.policies = policies;
		this.baseurl = baseurl;
		this.username = username;
		this.password = password;
	}
    
    public static final String HTTP_BASE_URL = "http://localhost:8080/fedora";
    public static final String HTTPS_BASE_URL = "https://localhost:8443/fedora";

    //public static final String SHIPPED_POLICIES = "defaultPolicies";
    public static final String SHIPPED_POLICIES = "shippedPolicies";
    public static final String NO_POLICIES = "noPolicies";
    public static final String PERMIT_DESC_REPO_POLICY = "permitDescRepoPolicy";    

    public static final Set ALL_POLICY_CHOICES = new HashSet();
    static {
    	ALL_POLICY_CHOICES.add(SHIPPED_POLICIES);
    	ALL_POLICY_CHOICES.add(NO_POLICIES);    	
    }

    public static final Set ALL_RESULT_CODES = new HashSet();
    static {
    	ALL_RESULT_CODES.add("302");
    	ALL_RESULT_CODES.add("401");
    	ALL_RESULT_CODES.add("403");
    	ALL_RESULT_CODES.add("500");
    	ALL_RESULT_CODES.add("200");
    }

    
    
    public static final String BAD_USERNAME = "yoohoo";
    public static final String BAD_PASSWORD = "sesame";
    public static final String ADMIN_USERNAME = "fedoraAdmin";
    public static final String ADMIN_PASSWORD = "fedoraAdmin";
    public static final String END_USERNAME = "wellspring";
    public static final String END_PASSWORD = "rathole";

    public static final Trial SHIPPED_POLICIES_HTTP_NO_NO = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, null, null);
    public static final Trial SHIPPED_POLICIES_HTTPS_NO_NO = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, null, null);
    public static final Trial SHIPPED_POLICIES_HTTP_BAD_BAD = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, BAD_USERNAME, BAD_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTPS_BAD_BAD = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, BAD_USERNAME, BAD_PASSWORD);    
    public static final Trial SHIPPED_POLICIES_HTTP_END_END = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTPS_END_END = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTP_END_BAD = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTPS_END_BAD = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTP_ADMIN_ADMIN = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTP_ADMIN_BAD = new Trial(SHIPPED_POLICIES, HTTP_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);
    public static final Trial SHIPPED_POLICIES_HTTPS_ADMIN_BAD = new Trial(SHIPPED_POLICIES, HTTPS_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);

    public static final Trial NO_POLICIES_HTTP_NO_NO = new Trial(NO_POLICIES, HTTP_BASE_URL, null, null);
    public static final Trial NO_POLICIES_HTTPS_NO_NO = new Trial(NO_POLICIES, HTTPS_BASE_URL, null, null);
    public static final Trial NO_POLICIES_HTTP_BAD_BAD = new Trial(NO_POLICIES, HTTP_BASE_URL, BAD_USERNAME, BAD_PASSWORD);
    public static final Trial NO_POLICIES_HTTPS_BAD_BAD = new Trial(NO_POLICIES, HTTPS_BASE_URL, BAD_USERNAME, BAD_PASSWORD);    
    public static final Trial NO_POLICIES_HTTP_END_END = new Trial(NO_POLICIES, HTTP_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial NO_POLICIES_HTTPS_END_END = new Trial(NO_POLICIES, HTTPS_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial NO_POLICIES_HTTP_END_BAD = new Trial(NO_POLICIES, HTTP_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial NO_POLICIES_HTTPS_END_BAD = new Trial(NO_POLICIES, HTTPS_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial NO_POLICIES_HTTP_ADMIN_ADMIN = new Trial(NO_POLICIES, HTTP_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial NO_POLICIES_HTTPS_ADMIN_ADMIN = new Trial(NO_POLICIES, HTTPS_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial NO_POLICIES_HTTP_ADMIN_BAD = new Trial(NO_POLICIES, HTTP_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);
    public static final Trial NO_POLICIES_HTTPS_ADMIN_BAD = new Trial(NO_POLICIES, HTTPS_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);

    public static final Trial PERMIT_DESC_REPO_HTTP_NO_NO = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, null, null);
    public static final Trial PERMIT_DESC_REPO_HTTPS_NO_NO = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, null, null);
    public static final Trial PERMIT_DESC_REPO_HTTP_BAD_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, BAD_USERNAME, BAD_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTPS_BAD_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, BAD_USERNAME, BAD_PASSWORD);    
    public static final Trial PERMIT_DESC_REPO_HTTP_END_END = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTPS_END_END = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, END_USERNAME, END_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTP_END_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTPS_END_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, END_USERNAME, BAD_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTP_ADMIN_ADMIN = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTPS_ADMIN_ADMIN = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, ADMIN_USERNAME, ADMIN_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTP_ADMIN_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTP_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);
    public static final Trial PERMIT_DESC_REPO_HTTPS_ADMIN_BAD = new Trial(PERMIT_DESC_REPO_POLICY, HTTPS_BASE_URL, ADMIN_USERNAME, BAD_PASSWORD);

    
    private static final Map clients = new Hashtable();
    private static int count = 0;     
    public static final FedoraClient getClient(String baseurl, String username, String password) throws Exception {
    	String key = baseurl + "|" + username + "|" + password;
    	if (clients.containsKey(key)) {
			System.out.println("clients table contains needed client, has " + clients.size() + " entries");
			Set keyset = clients.keySet();
			Iterator keysetIterator = keyset.iterator();
			while (keysetIterator.hasNext()) {
				String temp = (String) keysetIterator.next();
				System.out.println("\t" + temp);				
			}
    	} else {
			count++;
			System.out.println("about to new FedoraClient(), n==" + count);
    		FedoraClient client = new FedoraClient(baseurl, username, password);
    		clients.put(key, client);
    	}
    	FedoraClient tempClient = (FedoraClient) clients.get(key);
		System.out.println("about to return (FedoraClient) clients.get(key)" + tempClient);
		HttpClient httpClient = tempClient.getHttpClient();
		System.out.println("\thas HttpClient " + httpClient);
		
		return (FedoraClient) clients.get(key);
    }

    
    
    public static final Set HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER = new HashSet();
    static {
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTP_NO_NO);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_NO_NO);
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTP_END_BAD);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_END_BAD);
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTP_ADMIN_BAD);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_ADMIN_BAD);
    }
    
    public static final Set HTTP_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER = new HashSet();
    static {
        HTTP_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER.add(SHIPPED_POLICIES_HTTP_END_END);
        HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER.add(SHIPPED_POLICIES_HTTPS_END_END);
    }
    
    public static final Set HTTP_TRIALS_WITH_SHIPPED_POLICIES_ADMIN = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ADMIN = new HashSet();
    static {
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES_ADMIN.add(SHIPPED_POLICIES_HTTP_ADMIN_ADMIN);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ADMIN.add(SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN);    	
    }

    public static final Set HTTP_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER = new HashSet();
    static {
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
        HTTP_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);    	
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
        HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);    	
    }
    
    public static final Set TRIALS_WITH_SHIPPED_POLICIES_BADUSER = new HashSet();
    public static final Set TRIALS_WITH_SHIPPED_POLICIES_ENDUSER = new HashSet();
    public static final Set TRIALS_WITH_SHIPPED_POLICIES_ADMIN = new HashSet();
    public static final Set TRIALS_WITH_SHIPPED_POLICIES_GOODUSER = new HashSet();
    static {
    	TRIALS_WITH_SHIPPED_POLICIES_BADUSER.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	TRIALS_WITH_SHIPPED_POLICIES_BADUSER.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	TRIALS_WITH_SHIPPED_POLICIES_ENDUSER.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
    	TRIALS_WITH_SHIPPED_POLICIES_ENDUSER.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
    	TRIALS_WITH_SHIPPED_POLICIES_ADMIN.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);
    	TRIALS_WITH_SHIPPED_POLICIES_ADMIN.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);
    	TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);
    	TRIALS_WITH_SHIPPED_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);
    }

    public static final Set HTTP_TRIALS_WITH_SHIPPED_POLICIES = new HashSet();
    static {
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
    	HTTP_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTP_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);
    }

    public static final Set HTTPS_TRIALS_WITH_SHIPPED_POLICIES = new HashSet();
    static {
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
    	HTTPS_TRIALS_WITH_SHIPPED_POLICIES.addAll(HTTPS_TRIALS_WITH_SHIPPED_POLICIES_ADMIN);
    }

    public static final Set TRIALS_WITH_SHIPPED_POLICIES = new HashSet();
    static {
    	TRIALS_WITH_SHIPPED_POLICIES.addAll(TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	TRIALS_WITH_SHIPPED_POLICIES.addAll(TRIALS_WITH_SHIPPED_POLICIES_ENDUSER);
    	TRIALS_WITH_SHIPPED_POLICIES.addAll(TRIALS_WITH_SHIPPED_POLICIES_ADMIN);
    }
    
   
    public static final Set HTTP_TRIALS_WITH_NO_POLICIES_BADUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER = new HashSet();
    static {
    	HTTP_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTP_NO_NO);
    	HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_NO_NO);
    	HTTP_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTP_END_BAD);
    	HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_END_BAD);
    	HTTP_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTP_ADMIN_BAD);
    	HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_ADMIN_BAD);
    }
    
    public static final Set HTTP_TRIALS_WITH_NO_POLICIES_ENDUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER = new HashSet();
    static {
        HTTP_TRIALS_WITH_NO_POLICIES_ENDUSER.add(NO_POLICIES_HTTP_END_END);
        HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER.add(NO_POLICIES_HTTPS_END_END);
    }
    
    public static final Set HTTP_TRIALS_WITH_NO_POLICIES_ADMIN = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN = new HashSet();
    static {
    	HTTP_TRIALS_WITH_NO_POLICIES_ADMIN.add(NO_POLICIES_HTTP_ADMIN_ADMIN);
    	HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN.add(NO_POLICIES_HTTPS_ADMIN_ADMIN);    	
    }

    public static final Set HTTP_TRIALS_WITH_NO_POLICIES_GOODUSER = new HashSet();
    public static final Set HTTPS_TRIALS_WITH_NO_POLICIES_GOODUSER = new HashSet();
    static {
    	HTTP_TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ENDUSER);
        HTTP_TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ADMIN);    	
    	HTTPS_TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER);
        HTTPS_TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);    	
    }
    
    public static final Set HTTP_TRIALS_WITH_NO_POLICIES = new HashSet();
    static {
    	HTTP_TRIALS_WITH_NO_POLICIES.addAll(HTTP_TRIALS_WITH_NO_POLICIES_BADUSER);
    	HTTP_TRIALS_WITH_NO_POLICIES.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	HTTP_TRIALS_WITH_NO_POLICIES.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ADMIN);
    }

    public static final Set HTTPS_TRIALS_WITH_NO_POLICIES = new HashSet();
    static {
    	HTTPS_TRIALS_WITH_NO_POLICIES.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	HTTPS_TRIALS_WITH_NO_POLICIES.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	HTTPS_TRIALS_WITH_NO_POLICIES.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);
    }
    
    
    
    
    
    
    
    
    
    
    

    public static final Set TRIALS_WITH_NO_POLICIES_BADUSER = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_ENDUSER = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_ADMIN = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_GOODUSER = new HashSet();
    static {
    	TRIALS_WITH_NO_POLICIES_BADUSER.addAll(HTTP_TRIALS_WITH_NO_POLICIES_BADUSER);
    	TRIALS_WITH_NO_POLICIES_BADUSER.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	TRIALS_WITH_NO_POLICIES_ENDUSER.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	TRIALS_WITH_NO_POLICIES_ENDUSER.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	TRIALS_WITH_NO_POLICIES_ADMIN.addAll(HTTP_TRIALS_WITH_NO_POLICIES_ADMIN);
    	TRIALS_WITH_NO_POLICIES_ADMIN.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);
    	TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTP_TRIALS_WITH_NO_POLICIES_GOODUSER);
    	TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(HTTPS_TRIALS_WITH_NO_POLICIES_GOODUSER);
    }

    public static final Set TRIALS_WITH_NO_POLICIES = new HashSet();
    static {
    	TRIALS_WITH_NO_POLICIES.addAll(TRIALS_WITH_NO_POLICIES_BADUSER);
    	TRIALS_WITH_NO_POLICIES.addAll(TRIALS_WITH_NO_POLICIES_ENDUSER);
    	TRIALS_WITH_NO_POLICIES.addAll(TRIALS_WITH_NO_POLICIES_ADMIN);
    }
    
    
    
    
    private static final HashSet NULLSET = new HashSet();
    
    //configurations
    public static final String UNSECURE_CONFIG = "unsecure";
    public static final String SECURE_APIM_CONFIG = "secure-apim";
    public static final String SECURE_ALL_CONFIG = "secure-all";  

    private static final Set ONLY_UNSECURE_CONFIG = new HashSet();
    static {
    	ONLY_UNSECURE_CONFIG.add(UNSECURE_CONFIG);
    }

    private static final Set ONLY_SECURE_APIM_CONFIG = new HashSet();
    static {
    	ONLY_SECURE_APIM_CONFIG.add(SECURE_APIM_CONFIG);
    }

    private static final Set ONLY_SECURE_ALL_CONFIG = new HashSet();
    static {
    	ONLY_SECURE_ALL_CONFIG.add(SECURE_ALL_CONFIG);
    }
    
    private static final Set UNSECURE_CONFIGS = new HashSet();
    static {
    	UNSECURE_CONFIGS.addAll(ONLY_UNSECURE_CONFIG);
    }
    
    private static final Set SECURE_CONFIGS = new HashSet();
    static {
    	SECURE_CONFIGS.addAll(ONLY_SECURE_APIM_CONFIG);
    	SECURE_CONFIGS.addAll(ONLY_SECURE_ALL_CONFIG);
    }
    
    private static final Set ALL_CONFIGS = new HashSet();
    static {
    	ALL_CONFIGS.addAll(UNSECURE_CONFIGS);
    	ALL_CONFIGS.addAll(SECURE_CONFIGS);
    }
    
    private static final String DEMO_OBJECTS = "demoObjects";
    private static final String MISSING_PIDS = "missingPids";
    private static final String BAD_PIDS = "badPids";    

    private static final Set ALL_OBJECT_SETS = new HashSet();
    static {
    	ALL_OBJECT_SETS.add(DEMO_OBJECTS);
    	ALL_OBJECT_SETS.add(MISSING_PIDS);
    	ALL_OBJECT_SETS.add(BAD_PIDS);
    }

    
    //for keys
    public static final String APIM = "apim";
    public static final String APIA = "apia";

    private static final Set ALL_PER_OBJECT_ACTIONS = new HashSet();
    static {
    	ALL_PER_OBJECT_ACTIONS.add(APIA);
    	ALL_PER_OBJECT_ACTIONS.add(APIM);
    }

    private static final Set ALL_NON_PER_OBJECT_ACTIONS = new HashSet();
    static {
    	ALL_NON_PER_OBJECT_ACTIONS.add(APIA);
    	ALL_NON_PER_OBJECT_ACTIONS.add(APIM);
    }

    
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    private static final Set ALL_PROTOCOLS = new HashSet();
    static {
    	ALL_PROTOCOLS.add(HTTP);
    	ALL_PROTOCOLS.add(HTTPS);
    }

    
    private static final String makeKey(String config, String policies, String protocol, String op, String dataset, String desiredStatus) {
    	String key = (config + "|" + policies + "|" + protocol + "|" + op + "|" + dataset + "|" + desiredStatus);
		System.out.println("trial key==" + key);
    	return key;
    }
    
    private static final String makeKey(String config, String policies, String protocol, String op, String dataset, int desiredStatus) {
    	String key = makeKey(config, policies, protocol, op, dataset, Integer.toString(desiredStatus));
    	return key;
    }


    private static final String makeKey(String config, String policies, String protocol, String op, String desiredStatus) {
    	String key = (config + "|" + policies + "|" + protocol + "|" + op + "|" + desiredStatus);
		System.out.println("trial key==" + key);
    	return key;
    }
    
    private static final String makeKey(String config, String policies, String protocol, String op, int desiredStatus) {
    	String key = makeKey(config, policies, protocol, op, Integer.toString(desiredStatus));
    	return key;
    }

    private static Hashtable trialsets = new Hashtable();
    static {
    	putMultiple(ALL_CONFIGS, ALL_POLICY_CHOICES, ALL_PROTOCOLS, ALL_PER_OBJECT_ACTIONS, ALL_OBJECT_SETS, ALL_RESULT_CODES, NULLSET);
    	putMultiple(ALL_CONFIGS, ALL_POLICY_CHOICES, ALL_PROTOCOLS, ALL_NON_PER_OBJECT_ACTIONS, ALL_RESULT_CODES, NULLSET);
    	
    	//begin api-a per-object
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 302, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 302, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 401, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 200, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, DEMO_OBJECTS, 200, NULLSET);
    	
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 302, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 302, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 401, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 403, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 500, HTTP_TRIALS_WITH_SHIPPED_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 500, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, BAD_PIDS, 200, NULLSET);

    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 302, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 302, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 401, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 200, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, MISSING_PIDS, 200, NULLSET);
    	
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 401, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 401, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 500, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES);
    	
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 401, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 401, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 403, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 500, HTTPS_TRIALS_WITH_SHIPPED_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 500, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, BAD_PIDS, 200, NULLSET);
    	
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 401, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 401, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, MISSING_PIDS, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);
    	
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 302, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 302, HTTP_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 401, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 403, HTTP_TRIALS_WITH_NO_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 500, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, DEMO_OBJECTS, 200, NULLSET);

    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 302, NULLSET);
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 302, HTTP_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 401, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 403, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 500, HTTP_TRIALS_WITH_NO_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 500, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, BAD_PIDS, 200, NULLSET);
    	
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 302, NULLSET);
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 302, HTTP_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 401, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 403, HTTP_TRIALS_WITH_NO_POLICIES); //no 401s, as no authn imposed    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 500, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, MISSING_PIDS, 200, NULLSET);

    	
    	//REMEMBER TO COMPLETE NULLSETS, BELOW <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    	
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 401, NULLSET);
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 401, HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 403, HTTPS_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ONLY_SECURE_APIM_CONFIG, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 403, HTTPS_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 403, HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 500, NULLSET);
    	putMultiple(ONLY_UNSECURE_CONFIG,  NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 200, NULLSET);
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, DEMO_OBJECTS, 200, HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);
    	    	
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 401, NULLSET);
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 401, HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 403, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 500, HTTPS_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ONLY_SECURE_APIM_CONFIG, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 500, HTTPS_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 500, HTTPS_TRIALS_WITH_NO_POLICIES_GOODUSER);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, BAD_PIDS, 200, NULLSET);
    	
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 401, NULLSET);    	
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 401, HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 403, HTTPS_TRIALS_WITH_NO_POLICIES);    	
    	putMultiple(ONLY_SECURE_APIM_CONFIG, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 403, HTTPS_TRIALS_WITH_NO_POLICIES);    	
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 403, HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 200, NULLSET);    	
    	putMultiple(ONLY_SECURE_ALL_CONFIG, NO_POLICIES, HTTPS, APIA, MISSING_PIDS, 200, HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);    	
    	//end api-a per-object
 
    	//begin api-a not per-object
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 302, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 302, HTTP_TRIALS_WITH_SHIPPED_POLICIES);    	
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 401, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 200, HTTP_TRIALS_WITH_SHIPPED_POLICIES);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTP, APIA, 200, NULLSET);    	
    	
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 401, NULLSET);
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 401, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_BADUSER);
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 403, NULLSET); //
    	putMultiple(ALL_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES);    	
    	putMultiple(SECURE_CONFIGS, SHIPPED_POLICIES, HTTPS, APIA, 200, HTTPS_TRIALS_WITH_SHIPPED_POLICIES_GOODUSER);

    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTP, APIA, 302, HTTP_TRIALS_WITH_NO_POLICIES);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, 401, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, 403, NULLSET);
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTP, APIA, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTP, APIA, 200, NULLSET);

    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, 302, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 401, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 401, HTTPS_TRIALS_WITH_NO_POLICIES_BADUSER);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 403, HTTPS_TRIALS_WITH_NO_POLICIES); //    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 403, HTTPS_TRIALS_WITH_NO_POLICIES_ENDUSER); //
    	putMultiple(ALL_CONFIGS, NO_POLICIES, HTTPS, APIA, 500, NULLSET);
    	putMultiple(UNSECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 200, NULLSET);    	
    	putMultiple(SECURE_CONFIGS, NO_POLICIES, HTTPS, APIA, 200, HTTPS_TRIALS_WITH_NO_POLICIES_ADMIN);
    	//end api-a not per-object
    	
    }
    
    
    private static final void putMultiple(Set configs, Set policyChoices, Set protocols, Set operations, Set datasets, Set resultCodes, Set trialSet) {
    	Iterator configsIterator = configs.iterator();
    	while (configsIterator.hasNext()) {
    		String config = (String) configsIterator.next();
    	   	Iterator policyChoicesIterator = policyChoices.iterator();
        	while (policyChoicesIterator.hasNext()) {
        		String policyChoice = (String) policyChoicesIterator.next();
        	   	Iterator protocolsIterator = protocols.iterator();
            	while (protocolsIterator.hasNext()) {
            		String protocol = (String) protocolsIterator.next();
            	   	Iterator operationsIterator = operations.iterator();
                	while (operationsIterator.hasNext()) {
                		String operation = (String) operationsIterator.next();
                	   	Iterator resultCodesIterator = resultCodes.iterator();
                    	while (resultCodesIterator.hasNext()) {
                    		String resultCode = (String) resultCodesIterator.next();
                    		String key = "";
                    		if (datasets == null) {
                    			key = makeKey(config, policyChoice, protocol, operation, resultCode);
                        		trialsets.put(key, trialSet);
                    		} else {
    	                	   	Iterator datasetsIterator = datasets.iterator();
    	                    	while (datasetsIterator.hasNext()) {
    	                    		String dataset = (String) datasetsIterator.next();
	                    			key = makeKey(config, policyChoice, protocol, operation, dataset, resultCode);
	                        		trialsets.put(key, trialSet);
	                    		}
                    		}
                    	}	
                	}
            	}
        	}
    	}
    }

    private static final void putMultiple(Set configs, Set policyChoices, Set protocols, Set operations, Set resultCodes, Set trialSet) {
        putMultiple(configs, policyChoices, protocols, operations, null, resultCodes, trialSet);
    }    
    
    private static final void putMultiple(Set configs, String policies, String protocol, String operation, String dataset, int resultCode, Set trialSet) {
    	Iterator iterator = configs.iterator();
    	while (iterator.hasNext()) {
    		String config = (String) iterator.next();
    		String key = "";
    		if (dataset == null) {
    			key = makeKey(config, policies, protocol, operation, resultCode);
    		} else {
    			key = makeKey(config, policies, protocol, operation, dataset, resultCode);
    		}
    		trialsets.put(key, trialSet);
    	}
    }

    private static final void putMultiple(Set configs, String policies, String protocol, String operation, int resultCode, Set trialSet) {
        putMultiple(configs, policies, protocol, operation, null, resultCode, trialSet);
    }
    
    private static final void putSingle(String config, String policies, String protocol, String operation, String dataset, int resultCode, Set trialSet) {
		String key = "";
		if (dataset == null) {
			key = makeKey(config, policies, protocol, operation, resultCode);
		} else {
			key = makeKey(config, policies, protocol, operation, dataset, resultCode);
		}
    	trialsets.put(key, trialSet);
    }

    private static final void putSingle(String config, String policies, String protocol, String operation, int resultCode, Set trialSet) {
    	putSingle(config, policies, protocol, operation, null, resultCode, trialSet);
    }

    private static final Set getTrialSet(String key) {
    	Set trialSet = null;
		if ( trialsets.containsKey(key)) {
			trialSet = (Set) trialsets.get(key);
		}
		return trialSet;
    }
    
    public static final Set getTrialSet(String config, String policies, String protocol, String op, String dataset, int desiredStatus) {
    	Set trialSet = null;
		String key = makeKey (config, policies, protocol, op, dataset, desiredStatus);
    	trialSet = getTrialSet(key);
    	System.out.println("about to return null trial set for " + key + " " + trialSet);
		return trialSet;
    }

    public static final Set getTrialSet(String config, String policies, String protocol, String op, int desiredStatus) {
    	Set trialSet = null;
		String key = makeKey (config, policies, protocol, op, desiredStatus);
    	trialSet = getTrialSet(key);
    	System.out.println("about to return null trial set for " + key + " " + trialSet);
		return trialSet;    }

    public static void main(String[] args) {
    }
}
