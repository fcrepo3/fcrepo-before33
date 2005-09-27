package fedora.test;  

import java.util.Hashtable;
import java.util.Iterator;

import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;

/**
 * @author Bill Niebel 
 */
public class HttpDataSource extends DataSource {
	
	public String baseurl = "";
	public int expectedStatus = -1;
    HttpInputStream stream = null;
	
	public HttpDataSource(String baseurl, int expectedStatus) throws Exception {
		super(expectedStatus == 200, (expectedStatus != 302)&&(expectedStatus != 401)&&(expectedStatus != 403)&&(expectedStatus != 500)&&(expectedStatus != 200));
		this.baseurl = baseurl;
		this.expectedStatus = expectedStatus;
	}
    private static int count = 0; 
    protected final void reset(IndividualTest test, boolean xml, String username, String password) throws Exception {
    	usecount++;
		count++;
		System.out.println("about to Trial.getClient(), n==" + count);
    	FedoraClient client = Trial.getClient(baseurl, username, password);
    	System.out.println("*** in HttpDataSource.reset()");
    	System.out.println("\texpectingSuccess()==" + expectingSuccess());
    	System.out.println("\t" + username + " " + password);    	
    	System.out.println("\tbaseurl + test.getUrl(xml)==" + baseurl + test.getUrl(xml));    	
    	System.out.println("\tclientThrowsStatusCodeException()==" + clientThrowsStatusCodeException());
    	try {
    		System.out.println("about to use " + test.getUrl(xml));
    		stream = client.get(baseurl + test.getUrl(xml), clientThrowsStatusCodeException());
    		System.out.println("nothing caught");
    	} catch (Throwable t) {
    		System.out.println("caught " + t.getMessage() + ((t.getCause() == null)?"":t.getCause().getMessage()));
    	} finally {
    		System.out.println("finally");
    	}
    }
    
	protected final boolean expectedStatusObtained() {
		boolean expectedStatusObtained = false;
		int statusObtained = -1;
		if (stream != null) {
			statusObtained = stream.getStatusCode();
		}
		expectedStatusObtained = (statusObtained == expectedStatus);
		if (! expectedStatusObtained) {
			System.out.println("*** in HttpDataSource.expectedStatusObtained()");
			System.out.println("\tstream==" + stream);
			System.out.println("\texpectedStatus==" + expectedStatus);
			System.out.println("\tstream.getStatusCode()==" + statusObtained);
			
		}
		
		
		return expectedStatusObtained;
	}

	protected final void close() throws Exception {
        stream.close();        	
        stream = null;
	}
	
	
	
	protected final Document getResults() throws Exception {
		Document results = null;
        try {
    		results = builder.parse(stream);        	
        } finally {
            close();        	
        }
		return results;
	}
	
	private static Hashtable dataSourceTable = new Hashtable();
	static {
		try {
			dataSourceTable.put("HTTP302", new HttpDataSource(Trial.HTTP_BASE_URL, 302));
			dataSourceTable.put("HTTP401", new HttpDataSource(Trial.HTTP_BASE_URL, 401));
			dataSourceTable.put("HTTPS401", new HttpDataSource(Trial.HTTPS_BASE_URL, 401));
			dataSourceTable.put("HTTP403", new HttpDataSource(Trial.HTTP_BASE_URL, 403));
			dataSourceTable.put("HTTPS403", new HttpDataSource(Trial.HTTPS_BASE_URL, 403));
			dataSourceTable.put("HTTP500", new HttpDataSource(Trial.HTTP_BASE_URL, 500));
			dataSourceTable.put("HTTPS500", new HttpDataSource(Trial.HTTPS_BASE_URL, 500));
			dataSourceTable.put("HTTP200", new HttpDataSource(Trial.HTTP_BASE_URL, 200));
			dataSourceTable.put("HTTPS200", new HttpDataSource(Trial.HTTPS_BASE_URL, 200));
		} catch (Exception e) {
			System.err.println("COULDN'T MAKE HTTP DATA SOURCES");
			dataSourceTable = null;
		}
	}
	
	private static final Hashtable counts = new Hashtable();

	public static final HttpDataSource getDataSource(String protocol, int expectedStatus) {
		HttpDataSource dataSource = null;
		String key = protocol + Integer.toString(expectedStatus);
		System.out.println("KEY IN getDataSource() = " + key);
		if (dataSourceTable.containsKey(key)) {
			dataSource = (HttpDataSource) dataSourceTable.get(key);
		}
		if (dataSource != null) {
			if (!counts.containsKey(key)) {
				counts.put(key, new Integer(0));
			}
			Integer i = (Integer) counts.get(key);
			counts.put(key, new Integer(i.intValue() + 1));
		}
		Iterator iterator = counts.keySet().iterator();
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			Integer i = (Integer) counts.get(key);
			System.out.println("another count:  " + key + " => " + i.intValue());
		}
		return dataSource;
	}

	

}
