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

    public static final String DEFAULT_POLICIES = null;
    public static final String SHIPPED_POLICIES = "shippedPolicies";
    public static final String NO_POLICIES = "noPolicies";
    public static final String PERMIT_DESC_REPO_POLICY = "permitDescRepoPolicy";    
    
    public static final String BAD_USERNAME = "SaddamWhosSane";
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
    
    public static final FedoraClient getClient(String baseurl, String username, String password) throws Exception {
    	String key = baseurl + "|" + username + "|" + password;
    	if (!clients.containsKey(key)) {
    		FedoraClient client = new FedoraClient(baseurl, username, password);
    		clients.put(key, client);
    	}
    	return (FedoraClient) clients.get(key);
    }

    
    public static final Set ALL_SHIPPED_POLICY_TRIALS = new HashSet();
    static {
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTP_NO_NO);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTPS_NO_NO);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTP_END_END);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTPS_END_END);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTP_END_BAD);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTPS_END_BAD);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTP_ADMIN_ADMIN);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTPS_ADMIN_ADMIN);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTP_ADMIN_BAD);
        ALL_SHIPPED_POLICY_TRIALS.add(SHIPPED_POLICIES_HTTPS_ADMIN_BAD);            	
    }

    public static final Set ALL_NO_POLICY_TRIALS = new HashSet();
    static {
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTP_NO_NO);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTPS_NO_NO);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTP_END_END);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTPS_END_END);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTP_END_BAD);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTPS_END_BAD);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTP_ADMIN_ADMIN);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTPS_ADMIN_ADMIN);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTP_ADMIN_BAD);
        ALL_NO_POLICY_TRIALS.add(NO_POLICIES_HTTPS_ADMIN_BAD);            	
    }
    
    public static final Set PROTECTED_URL_NOT_401 = new HashSet();
    public static final Set PROTECTED_URL_401 = new HashSet();
    static {
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTP_NO_NO);
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTPS_NO_NO);
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTP_END_END);
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTPS_END_END);
    	PROTECTED_URL_401.add(PERMIT_DESC_REPO_HTTP_END_BAD);
    	PROTECTED_URL_401.add(PERMIT_DESC_REPO_HTTPS_END_BAD);
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTP_ADMIN_ADMIN);
    	PROTECTED_URL_NOT_401.add(PERMIT_DESC_REPO_HTTPS_ADMIN_ADMIN);
    	PROTECTED_URL_401.add(PERMIT_DESC_REPO_HTTP_ADMIN_BAD);
    	PROTECTED_URL_401.add(PERMIT_DESC_REPO_HTTPS_ADMIN_BAD);            	
    }
    
    public static void main(String[] args) {
    }
}
