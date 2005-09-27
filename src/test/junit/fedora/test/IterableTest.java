package fedora.test;  

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;

/**
 * @author Bill Niebel 
 */
public abstract class IterableTest extends FedoraServerTestCase {
	
    protected static DocumentBuilderFactory factory;
    protected static DocumentBuilder builder;
    protected static ServerConfiguration fcfg;
    protected static FedoraClient client;

    public static final String NS_XHTML_PREFIX = "xhtml";
    public static final String NS_XHTML = "http://www.w3.org/1999/xhtml";
    
    protected boolean XML = true;
    protected boolean XHTML = false;
    
    protected boolean TEST_XML = true;
    protected boolean TEST_XHTML = false;
    
    protected static Set demoObjects;
    protected static final Set badPids = new HashSet();
    static {
    	badPids.add("hoo%20doo:%20TheClash"); //unacceptable syntax
    }
    
    protected static final Set fewPids = new HashSet();
    static {
    	fewPids.add("demo:10");
    }
    
    protected static final Set missingPids = new HashSet();
    static {
    	missingPids.add("doowop:667"); //simply not in repository
    }
    
    
    protected static final String getLabel(String policies, String protocol, String objectset, int expectedStatus) {
    	String label = policies + "" + protocol + ((objectset == null) ? "" : objectset) + "" + expectedStatus;
    	return label;
    }

    
    public static final boolean samePolicies(String policiesA, String policiesB) {
    	boolean samePolicies = false;
    	if (policiesA == policiesB) {
    		samePolicies = true;
    	} else if (policiesA != null) {
    		samePolicies = policiesA.equals(policiesB); 
    	} else {
    		samePolicies = policiesB.equals(policiesA);
    	}
    	return samePolicies;
    }
    
    public void iterate(Set trials, DataSource dataSource, IndividualTest testXml, IndividualTest testXhtml, String label) throws Exception {
    	String lastPolicies = null;
    	//if (trials == null) return;
    	Iterator it = trials.iterator();
    	while (it.hasNext()) {
    		Trial trial = (Trial) it.next(); 
    		String policies = trial.policies;
    		if (samePolicies(policies, lastPolicies)) {
    			System.out.println("staying with policies==" + policies);
    		} else {
    			System.out.println("changing to policies==" + policies);
	            usePolicies(policies);
    			lastPolicies = policies;    			
    		}
    		HttpDataSource httpDataSource = (HttpDataSource) dataSource;
    		System.out.println("*** in Iterable.iterate()");
    		System.out.println("\t" + policies);
        	run(testXml, testXhtml, dataSource, trial.username, trial.password, label);
    	}
    } 
    private static int count = 0; 
    private static int kount = 0; 
	//public abstract void run(IndividualTest test, Connector connector, String username, String password) throws Exception;
    public void run(IndividualTest testXml, IndividualTest testXhtml, DataSource dataSource, String username, String password, String label) throws Exception {
		InputStream is = null;
		int loopcount = 0;
    	if (TEST_XML && (testXml != null) && testXml.handlesXml()) {
    		if (testXml.again()) {
    			if ("wellspringrathole".equals(username + password)) {
    				kount++;
        			System.out.println("wellspring/rathole of " + kount);    				
    			}
    			System.out.println("encountered loop " + count++ + " times");
    		
    		}
    		while (testXml.again()) { //URL MAY NEED FIXUP, CONSIDER DS.RESET()
    			is = null;
    			System.out.println("local loop it of " + loopcount++ + " dataSource.usecount==" + dataSource.usecount);

        		dataSource.reset(testXml, XML, username, password);
    			System.out.println("*** in Iterable.run(), b4 assertTrue()");
            	System.out.println("X:  " +  username + " " + password + " " + testXml.getUrl(XML) + " " + label);    			
    			assertTrue(dataSource.expectedStatusObtained());
            	Document results = null;
                if (dataSource.expectingSuccess()) {
                	System.out.println("OUT");
                	System.err.println("ERR");
                	results = dataSource.getResults();
                	testXml.checkResultsXml(results);
                	testXml.checkResults();
            	} else {
            		testXml.checkResultsElse(); //remove this to get runaway loop, which causes class-not-found error
            		dataSource.close();
            		/* abandoned this because some server action returning an unclosed img tag
            		try {
            			results = dataSource.getResults();
            		} catch (Exception e) { 
            		}
            		if (results != null) {
                   		test.checkResultsXmlElse(results);
            			test.checkResultsElse();            			
            		}
            		*/
            	}
    		}
    		//test.reuse();
    	} 
    	if (TEST_XHTML && (testXhtml != null) && testXhtml.handlesXhtml()) {
    		while (testXhtml.again()) {
    			is = null;
    			dataSource.reset(testXhtml, XHTML, username, password);
    			System.out.println("*** in Iterable.run(), b4 assertTrue()");
    			assertTrue(dataSource.expectedStatusObtained());
            	Document results = null;
                if (dataSource.expectingSuccess()) {
                	results = dataSource.getResults();
                	testXhtml.checkResultsXhtml(results);
                	testXhtml.checkResults();
            	} else {
            		testXhtml.checkResultsElse();
            		dataSource.close();
            		/* abandoned this because some server action returning an unclosed img tag            		
            		try {
            			results = dataSource.getResults();
            		} catch (Exception e) { 
            		}
            		if (results != null) {
                   		test.checkResultsXhtmlElse(results);
            			test.checkResultsElse();            			
            		}
            		*/
            	}     			
    		}
    	} 
    }
}
