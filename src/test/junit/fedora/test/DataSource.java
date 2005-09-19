package fedora.test;  

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */
public abstract class DataSource {
	
    protected static DocumentBuilderFactory factory;
    protected static DocumentBuilder builder;
    static {
    }
	
	private boolean expectingSuccess = false;
	private boolean clientThrowsStatusCodeException = false;
	
	public DataSource(boolean expectingSuccess, boolean clientThrowsStatusCodeException) throws Exception {
		this.expectingSuccess = expectingSuccess;
		this.clientThrowsStatusCodeException = clientThrowsStatusCodeException;
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		if (builder == null) {
			builder = factory.newDocumentBuilder();
		}
	}
	
	protected abstract boolean expectedStatusObtained();
	
    protected abstract void reset(IndividualTest test, boolean xml, String username, String password) throws Exception;
	    
	protected abstract Document getResults() throws Exception;
	
    protected final boolean expectingSuccess() {
    	return expectingSuccess;
    }

    protected final boolean clientThrowsStatusCodeException() {
    	return clientThrowsStatusCodeException;
    }
}
