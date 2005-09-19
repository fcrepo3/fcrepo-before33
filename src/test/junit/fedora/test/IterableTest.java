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
    
    protected boolean testXML = true;
    protected boolean testXHTML = false;
    protected boolean XML = true;
    protected boolean XHTML = false;
    
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
    
    public void iterate(Set trials, DataSource dataSource, IndividualTest test) throws Exception {
    	String lastPolicies = null;
    	Iterator it = trials.iterator();
    	while (it.hasNext()) {
    		Trial trial = (Trial) it.next(); 
    		String policies = trial.policies;
    		if (samePolicies(policies, lastPolicies)) {
    			System.out.println("no need to change policies");
    		} else {
    			System.out.println("changing to policies==" + policies);
	            usePolicies(policies);
    			lastPolicies = policies;    			
    		}
    		HttpDataSource httpDataSource = (HttpDataSource) dataSource;
    		System.out.println("*** in Iterable.iterate()");
    		System.out.println("\t" + policies);
        	run(test, dataSource, trial.username, trial.password);
    	}
    } 
    private static int count = 0; 
	//public abstract void run(IndividualTest test, Connector connector, String username, String password) throws Exception;
    public void run(IndividualTest test, DataSource dataSource, String username, String password) throws Exception {
		InputStream is = null;
    	if (testXML) {
    		while (test.again()) { //URL MAY NEED FIXUP, CONSIDER DS.RESET()
    			is = null;
    			count++;
    			System.out.println("about to dataSource.reset(), n==" + count);
        		dataSource.reset(test, XML, username, password);
    			System.out.println("*** in Iterable.run(), b4 assertTrue()");    		
    			assertTrue(dataSource.expectedStatusObtained());
                if (dataSource.expectingSuccess()) {
                	Document results = dataSource.getResults();
               		test.checkResultsXml(results);
               		test.checkResults();
            	} else {
            		test.checkResultsElse();
            	}
    		}
    	} 
    	if (testXHTML) {
    		while (test.again()) {
    			is = null;
    			dataSource.reset(test, XHTML, username, password);
    			System.out.println("*** in Iterable.run(), b4 assertTrue()");
    			assertTrue(dataSource.expectedStatusObtained());
                if (dataSource.expectingSuccess()) {
                	Document results = dataSource.getResults();
               		test.checkResultsXhtml(results);
               		test.checkResults();
            	} else {
            		test.checkResultsElse();               		
            	}     			
    		}
    	} 
    }
}
