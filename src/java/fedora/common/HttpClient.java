package fedora.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpClient {

    private GetMethod doGetMethod(String url, String username, String password,
    		org.apache.commons.httpclient.HttpClient client, 
			int millisecondsWait, int redirectDepth) throws Exception {
    	System.err.println("doing get for " + url + "for " + username + " (" + password );
	  	GetMethod get = null;
	  	try {
	  		client.setConnectionTimeout(millisecondsWait);
	  		if (
	  				((username == null) && (password == null)) 
	  		||  	((username != null) && "".equals(username) && (password != null) && "".equals(password))
			) {
	  			// don't authenticate
	  		} else {
		  		if ((username == null) || (password == null) || ("".equals(username))) {
		  			throw new Exception("unexpected username password mix");
		  		}
		  		client.getState().setCredentials(null, null, new UsernamePasswordCredentials(username, password));
		  		client.getState().setAuthenticationPreemptive(true);		  		
	  		}
	  		System.err.println("in getExternalContent(), after setup");
	  		int resultCode = -1;
	  		for (int loops = 0; (url != null) && (loops < redirectDepth); loops++) {
	  			System.err.println("in getExternalContent(), new loop, url=" + url);
	  			get = new GetMethod(url);
	  			url = null;
	  			System.err.println("in getExternalContent(), got GetMethod object=" + get);
	  			get.setDoAuthentication(true);
	  			get.setFollowRedirects(true);
	  			resultCode=client.executeMethod(get);
	  			if (300 <= resultCode && resultCode <= 399) {
	  				url=get.getResponseHeader("Location").getValue();
	  				System.err.println("in getExternalContent(), got redirect, new url=" + url);
	  			}
	  		}
	  	} catch (Throwable th) {
	  		if (get != null) {
	  			get.releaseConnection();
	  		}
	  		System.err.println("x " + th.getMessage());
	  		if (th.getCause() != null) {
		  		System.err.println("x " + th.getCause().getMessage());	  			
	  		}
	  		throw new Exception("failed connection");
	    }
	  	return get;
    }
    
    private String url = null;
    public String getUrl() {
    	return url;
    }
    
    private GetMethod getMethod = null;
    public GetMethod getGetMethod() {
    	return getMethod;
    }
    
    public int getStatusCode() {
    	return (getMethod == null) ? -1 : getMethod.getStatusCode();
    }
    
    private final void releaseConnection() {
		try {   	
			getMethod.releaseConnection();
		} catch (Throwable t) {
	  		System.err.println(t.getMessage());
	  		if (t.getCause() != null) {
		  		System.err.println(t.getCause().getMessage());	  			
	  		}
		}
	}

	private final void closeStream() {
		try {
			getGetMethod().getResponseBodyAsStream().close();
		} catch (Throwable t) {
	  		System.err.println(t.getMessage());
	  		if (t.getCause() != null) {
		  		System.err.println(t.getCause().getMessage());	  			
	  		}
		}
    }

    public final void close() {
    	closeStream();
    	releaseConnection();
    }
    
    public HttpClient(String url, String username, String password) {
    	this.url = url;
        try {
        	org.apache.commons.httpclient.HttpClient httpClient 
				= new org.apache.commons.httpclient.HttpClient(new MultiThreadedHttpConnectionManager());
        	getMethod = doGetMethod(url, username, password, httpClient, 20000, 25); // wait 20 seconds max; 25 redirects max
        } catch (Exception e) {
	  		log(e.getMessage());
	  		if (e.getCause() != null) {
		  		log(e.getCause().getMessage());	  			
	  		}
        }
    }    
    
    public HttpClient(String url) {
    	this(url, null, null);
    }    
    
    public HttpClient(String protocol, String host, String port, String username, String password, String url) {
    	this(HttpClient.makeUrl(protocol, host, port, url), username, password);
    }
    
    public HttpClient(String protocol, String host, String port, String url) {
    	this(HttpClient.makeUrl(protocol, host, port, url), null, null);
    }
    
    public static String makeUrl(String protocol, String host, String port, String more) {
    	String url = protocol + "://" + host 
		+ (((port != null) && ! "".equals(port)) ? (":" + port) : "") 
		+ "/fedora" + more;
    	return url;
    }
    
    public String getLineResponseUrl() {
    	String textResponse = "";
        try {
            if (getStatusCode() != 200) {
            	textResponse = "ERROR: request failed, response code was " + getStatusCode();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(getGetMethod().getResponseBodyAsStream()));
                textResponse = in.readLine();
                if (textResponse == null) {
                	textResponse = "ERROR: response was empty.";
                }
            }
        } catch (Exception e) {
        	textResponse =  "ERROR: couldn't connect";
	  		log(e.getMessage());
	  		if (e.getCause() != null) {
		  		log(e.getCause().getMessage());	  			
	  		}
        } finally {
  			close();
        }
        return textResponse;
    }
 
    private boolean log = false;
    
    private final void log(String msg) {
    	if (log) {
  	  	System.err.println(msg);	  		
    	}
    }
    
}
