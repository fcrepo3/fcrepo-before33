
package fedora.test;  

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

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

    public static final String DEFAULT_POLICIES = "defaultPolicies";
    public static final String SHIPPED_POLICIES = "shippedPolicies";
    public static final String NO_POLICIES = "noPolicies";
    public static final String PERMIT_DESC_REPO_POLICY = "permitDescRepoPolicy";    
    
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
    	if (!clients.containsKey(key)) {
			count++;
			System.out.println("about to new FedoraClient(), n==" + count);
    		FedoraClient client = new FedoraClient(baseurl, username, password);
    		clients.put(key, client);
    	}
		System.out.println("about to return (FedoraClient) clients.get(key)" + (FedoraClient) clients.get(key));
    	return (FedoraClient) clients.get(key);
    }

    
    public static final Set ALL_TRIALS_WITH_DEFAULT_POLICIES = new HashSet();
    static {
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTP_NO_NO);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTPS_NO_NO);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTP_END_END);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTPS_END_END);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTP_END_BAD);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTPS_END_BAD);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTP_ADMIN_ADMIN);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTP_ADMIN_BAD);
    	ALL_TRIALS_WITH_DEFAULT_POLICIES.add(SHIPPED_POLICIES_HTTPS_ADMIN_BAD); 
    }
    public static final Set ALL_SHIPPED_POLICY_TRIALS = ALL_TRIALS_WITH_DEFAULT_POLICIES;    	

    
    public static final Set ALL_TRIALS_WITH_NO_POLICIES = new HashSet();
    static {
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTP_NO_NO);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTPS_NO_NO);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTP_END_END);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTPS_END_END);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTP_END_BAD);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTPS_END_BAD);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTP_ADMIN_ADMIN);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTPS_ADMIN_ADMIN);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTP_ADMIN_BAD);
    	ALL_TRIALS_WITH_NO_POLICIES.add(NO_POLICIES_HTTPS_ADMIN_BAD);            	
    }
    public static final Set ALL_NO_POLICY_TRIALS = ALL_TRIALS_WITH_NO_POLICIES; 
    
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP  = new HashSet();
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_BADUSER = new HashSet();
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_ENDUSER = new HashSet();
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_ADMIN = new HashSet();
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_GOODUSER = new HashSet();
    static {
    	TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP.add(SHIPPED_POLICIES_HTTP_NO_NO);
    	TRIALS_WITH_DEFAULT_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_NO_NO);
    	TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP.add(SHIPPED_POLICIES_HTTP_END_END);
    	TRIALS_WITH_DEFAULT_POLICIES_ADMIN.add(SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN);
    	TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP.add(SHIPPED_POLICIES_HTTP_END_BAD);
    	TRIALS_WITH_DEFAULT_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_END_BAD);
    	TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP.add(SHIPPED_POLICIES_HTTP_ADMIN_ADMIN);
    	TRIALS_WITH_DEFAULT_POLICIES_ADMIN.add(SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN);
    	TRIALS_WITH_DEFAULT_POLICIES_GIVING_302_ON_HTTP.add(SHIPPED_POLICIES_HTTP_ADMIN_BAD);
    	TRIALS_WITH_DEFAULT_POLICIES_BADUSER.add(SHIPPED_POLICIES_HTTPS_ADMIN_BAD);            	
        TRIALS_WITH_DEFAULT_POLICIES_GOODUSER.addAll(TRIALS_WITH_DEFAULT_POLICIES_ENDUSER);
        TRIALS_WITH_DEFAULT_POLICIES_GOODUSER.addAll(TRIALS_WITH_DEFAULT_POLICIES_ADMIN);    	
    }
    
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_GIVING_401_ON_HTTPS = TRIALS_WITH_DEFAULT_POLICIES_BADUSER;
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_GIVING_403_ON_HTTPS = TRIALS_WITH_DEFAULT_POLICIES_ENDUSER;
    public static final Set TRIALS_WITH_DEFAULT_POLICIES_GIVING_200_ON_HTTPS = TRIALS_WITH_DEFAULT_POLICIES_ADMIN;

    public static final Set TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_BADUSER = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_ENDUSER = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_ADMIN = new HashSet();
    public static final Set TRIALS_WITH_NO_POLICIES_GOODUSER = new HashSet();
    static {
    	TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP.add(NO_POLICIES_HTTP_NO_NO);
    	TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_NO_NO);
    	TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP.add(NO_POLICIES_HTTP_END_END);
    	TRIALS_WITH_NO_POLICIES_ENDUSER.add(NO_POLICIES_HTTPS_END_END);
    	TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP.add(NO_POLICIES_HTTP_END_BAD);
    	TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_END_BAD);
    	TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP.add(NO_POLICIES_HTTP_ADMIN_ADMIN);
    	TRIALS_WITH_NO_POLICIES_ADMIN.add(NO_POLICIES_HTTPS_ADMIN_ADMIN);
    	TRIALS_WITH_NO_POLICIES_GIVING_302_ON_HTTP.add(NO_POLICIES_HTTP_ADMIN_BAD);
    	TRIALS_WITH_NO_POLICIES_BADUSER.add(NO_POLICIES_HTTPS_ADMIN_BAD);  
        TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(TRIALS_WITH_NO_POLICIES_ENDUSER);
        TRIALS_WITH_NO_POLICIES_GOODUSER.addAll(TRIALS_WITH_NO_POLICIES_ADMIN);
    }

    
    public static final Set TRIALS_WITH_NO_POLICIES_GIVING_401_ON_HTTPS = TRIALS_WITH_NO_POLICIES_BADUSER;
    public static final Set TRIALS_WITH_NO_POLICIES_GIVING_403_ON_HTTPS = TRIALS_WITH_NO_POLICIES_ENDUSER;
    public static final Set TRIALS_WITH_NO_POLICIES_GIVING_200_ON_HTTPS = TRIALS_WITH_NO_POLICIES_ADMIN;   
    
    private static final HashSet NULLSET = new HashSet();
    
    public static final String UNSECURE = "unsecure";
    public static final String SECURE_APIM = "secure-apim";
    public static final String SECURE_ALL = "secure-all";    
    private static final String DEMO_OBJECTS = "demoObjects";
    private static final String MISSING_PIDS = "missingPids";
    private static final String BAD_PIDS = "badPids";    
    private static final String OBJECT_PROFILE = "objectProfile";
    private static final String DESCRIBE_REPOSITORY = "describeRepository";    
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    
    private static final String makeKey(String config, String policies, String protocol, String op, String dataset, int desiredStatus) {
    	String key = (config + "|" + policies + "|" + protocol + "|" + op + "|" + dataset + "|" + Integer.toString(desiredStatus));
		System.out.println("trial key==" + key);
    	return key;
    }

    private static final String makeKey(String config, String policies, String protocol, String op, int desiredStatus) {
    	String key = (config + "|" + policies + "|" + protocol + "|" + op + "|" + Integer.toString(desiredStatus));
		System.out.println("trial key==" + key);
    	return key;
    }

    private static Hashtable trialsets = new Hashtable();
    static {
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, ALL_TRIALS_WITH_DEFAULT_POLICIES);    	
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(makeKey(UNSECURE, DEFAULT_POLICIES, HTTPS, OBJECT_PROFILE, DEMO_OBJECTS, 200), ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, TRIALS_WITH_NO_POLICIES_ENDUSER);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, TRIALS_WITH_NO_POLICIES_ADMIN);    	
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, NULLSET);

    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, TRIALS_WITH_DEFAULT_POLICIES_GOODUSER);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, TRIALS_WITH_DEFAULT_POLICIES_GOODUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, NULLSET);    	
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 403, TRIALS_WITH_NO_POLICIES_ENDUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + DEMO_OBJECTS + "|" + 200, TRIALS_WITH_NO_POLICIES_ADMIN);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 500, TRIALS_WITH_NO_POLICIES_GOODUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + BAD_PIDS + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 403, TRIALS_WITH_NO_POLICIES_ENDUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + OBJECT_PROFILE + "|" + MISSING_PIDS + "|" + 200, TRIALS_WITH_NO_POLICIES_ADMIN);    	
    	
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 200, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 403, NULLSET);
    	trialsets.put(UNSECURE + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(makeKey(UNSECURE, DEFAULT_POLICIES, HTTPS, DESCRIBE_REPOSITORY, 200), ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 403, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 200, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 403, ALL_TRIALS_WITH_NO_POLICIES);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(UNSECURE + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 200, NULLSET);

    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + DEFAULT_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 200, TRIALS_WITH_DEFAULT_POLICIES_GOODUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 302, ALL_TRIALS_WITH_DEFAULT_POLICIES);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 401, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 403, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTP + "|" + DESCRIBE_REPOSITORY + "|" + 200, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 302, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 401, TRIALS_WITH_DEFAULT_POLICIES_BADUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 403, TRIALS_WITH_NO_POLICIES_ENDUSER);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 500, NULLSET);
    	trialsets.put(SECURE_ALL + "|" + NO_POLICIES + "|" + HTTPS + "|" + DESCRIBE_REPOSITORY + "|" + 200, TRIALS_WITH_NO_POLICIES_ADMIN);
    	
    }
    
    private static final Set getTrialSet(String key) {
    	Set trialSet = null;
		if ( trialsets.containsKey(key)) {
			trialSet = (Set) trialsets.get(key);
		}
		return trialSet;
    }
    
    public static final Set getTrialSet(String config, String policies, String protocol, String op, String dataset, int desiredStatus) {
		String key = makeKey (config, policies, protocol, op, dataset, desiredStatus);
		return getTrialSet(key);
    }

    public static final Set getTrialSet(String config, String policies, String protocol, String op, int desiredStatus) {
		String key = makeKey (config, policies, protocol, op, desiredStatus);
		return getTrialSet(key);    }

    public static void main(String[] args) {
    }
}
