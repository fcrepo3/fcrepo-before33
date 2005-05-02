package fedora.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection; //for response status codes
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.httpclient.DefaultMethodRetryHandler;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;
import fedora.server.errors.GeneralException;
import fedora.server.errors.StreamIOException;
import fedora.server.utilities.ServerUtility;

public class HttpClient {

	private org.apache.commons.httpclient.HttpClient apacheCommonsClient;

	public GetMethod doNoAuthnGet(int millisecondsWait, int redirectDepth, int maxConnectionAttemptsPerUrl) 
	throws Exception {
    	System.err.println("doNoAuthnGet.../ ");
			return doNoAuthnGet(millisecondsWait, redirectDepth, maxConnectionAttemptsPerUrl, SLEEP_MILLISECONDS);
    }    
	
	public GetMethod doNoAuthnGet(int millisecondsWait, int redirectDepth, int maxConnectionAttemptsPerUrl, int millisecondsSleep) 
	throws Exception {
    	System.err.println("doNoAuthnGet.../ ");
			return doAuthnGet(millisecondsWait, redirectDepth, null, null, maxConnectionAttemptsPerUrl, millisecondsSleep);
    }
	
	private static final int SLEEP_MILLISECONDS = 1000;

	
    public GetMethod doAuthnGet(int millisecondsWait, int redirectDepth, String username, String password, int maxConnectionAttemptsPerUrl) 
    throws Exception {
    	return doAuthnGet(millisecondsWait, redirectDepth, username, password, maxConnectionAttemptsPerUrl, SLEEP_MILLISECONDS);
    }
	
    public GetMethod doAuthnGet(int millisecondsWait, int redirectDepth, String username, String password, int maxConnectionAttemptsPerUrl, int millisecondsSleep) 
    throws Exception 
	{
    	DefaultMethodRetryHandler retryhandler = new DefaultMethodRetryHandler();
    	retryhandler.setRequestSentRetryEnabled(true);
    	retryhandler.setRetryCount(5);

    	System.err.println("doAuthnGet... " + this.relativePath + "for " + username + " " + password + 
    			" " );
	  	getMethod = null;
	  	try {
	  		try {
		  		boolean authenticate = false;
	  			System.err.println("setting timeouts (milliseconds) = " + millisecondsWait);	
		  		apacheCommonsClient.setConnectionTimeout(0); // waiting for connection
		  		apacheCommonsClient.setTimeout(0); // waiting for data
		  		if (
		  				((username == null) && (password == null)) 
		  		||  	((username != null) && "".equals(username) && (password != null) && "".equals(password))
				) {
		  			System.err.println("doAuthnGet(), don't authenticate " + username + " " + password);
		  		} else {
			  		if ((username == null) || (password == null) || ("".equals(username))) {
			  			throw new Exception("unexpected username password mix");
			  		}
		  			System.err.println("doAuthnGet(), do authenticate " + username + " " + password);		  		
			  		apacheCommonsClient.getState().setCredentials(null, null, new UsernamePasswordCredentials(username, password));
			  		apacheCommonsClient.getState().setAuthenticationPreemptive(true);
		  			System.err.println("doAuthnGet(), apacheCommonsClient=" + apacheCommonsClient);
			  		authenticate = true;
		  		}
		  		System.err.println("doAuthnGet(), after setup");
		  		int resultCode = -1;
		  		int connectionAttemptsPerUrl = 0;
		  		String workingPath = absoluteUrl;
		  		for (int loops = 0; (workingPath != null) && (loops < redirectDepth) && (connectionAttemptsPerUrl < maxConnectionAttemptsPerUrl) ; loops++) {
		  			getMethod = new GetMethod(workingPath);
		  	    	getMethod.setMethodRetryHandler(retryhandler);
		  			
		  			System.err.println("doAuthnGet(), getMethod=" + getMethod);
		  			System.err.println("doAuthnGet(), validate()=" + getMethod.validate());
		  			
		  			System.err.println("doAuthnGet(), workingpath="+workingPath);

		  			getMethod.setDoAuthentication(authenticate);
		  			System.err.println("doAuthnGet(), got GetMethod object=" + getMethod);
		  			getMethod.setFollowRedirects(true);
		  			
		  			
		  			
		  	    	System.err.println("just setFollowRedirects(true)");   	  
		  	    	
		  	    	System.err.println("getMethod.getRecoverableExceptionCount()" + getMethod.getRecoverableExceptionCount());
		  	    	System.err.println("getMethod.validate()" + getMethod.validate());
		  	    	
		  	    	try {
				  		log("doAuthnGet trying get, go=" + connectionAttemptsPerUrl);	
		  	    		resultCode = apacheCommonsClient.executeMethod(getMethod);
			  			if (300 <= resultCode && resultCode <= 399) {
			  				workingPath=getMethod.getResponseHeader("Location").getValue();
			  				connectionAttemptsPerUrl = 0;
			  				System.err.println("doAuthnGet(), got redirect, new url=" + workingPath);
			  			} else {
				  			workingPath = null; //signal loop completion			  				
			  			}
		  	    	} catch (IOException ioe) {
		  	    		connectionAttemptsPerUrl++;
				  		log("doAuthnGet got --inner-- IOException: " + ioe.getMessage());	
		  	    		Thread.currentThread().sleep(millisecondsSleep);
		  	    	}
		  	    	System.err.println("resultCode=" + resultCode + " absoluteUrl=" +absoluteUrl); 

		  		}
		  	} catch (HttpException httpe) {
		  		log("doAuthnGet got HttpException: " + httpe.getMessage());
		  		throw new Exception("got HttpException", httpe);		  		
		  	} catch (IOException ioe) {
		  		log("doAuthnGet got IOException: " + ioe.getMessage());	
		  		if (ioe.getCause() != null) {
			  		System.err.println(ioe.getCause().getMessage());	  			
		  		}		  		
	            ioe.printStackTrace();		  		
		  		throw new Exception("got IOException", ioe);		  		
		  	} catch (Exception e) {
		  		log("doAuthnGet got Exception: " + e.getMessage());		  		
		  		throw new Exception("got Exception", e);		  		
		  	}
	  	} catch (Throwable th) {
	  		if (getMethod != null) {
	  			getMethod.releaseConnection();
	  		}
	  		throw new Exception("failed connection", th);
	    }
	  	return getMethod;
    }
    
	private String absoluteUrl = null;
	private String relativePath = null;
    private String protocol = "";
    private String host = "";
    private String port = "";
    public final String getProtocol() {
    	return protocol;
    }
    public final String getHost() {
    	return host;
    }
    public final String getPort() {
    	return port;
    }
    
    private String relativeUrl = null;
    public String getRelativeUrl() {
    	return relativeUrl;
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
    
    private static final int sslPort = 8443; 
    //private static final String host = "localhost";
    private static final boolean allowSelfSignedCertificates = true;
   
    private static final String captureProtocol = "([^:]+?)://";
    private static final String captureHostWithPort = "([^:]+?):([^/]+?)";
    private static final String captureHostWithoutPort = "([^/]+?)";
    private static final String capturePath = "/(.*)";
    private static final String captureWithPort = captureProtocol + captureHostWithPort + capturePath;
    private static final String captureWithoutPort = captureProtocol + captureHostWithoutPort + capturePath;
    private static final Pattern patternWithPort = Pattern.compile(captureWithPort);
    private static final Pattern patternWithoutPort = Pattern.compile(captureWithoutPort);
    
    public HttpClient(String protocol, String host, String port, String path) {
    	System.err.println("HttpClient " + protocol + " " + host + " " + port + " " + path);
    	if ( (protocol == null) || "".equals(protocol)
    	||   (host == null) || "".equals(host)
    	||   (port == null) || "".equals(port) ) {
    		absoluteUrl = path;
			System.err.println("parsing " + absoluteUrl + " against " + patternWithPort);
    		//parse url as absolute url into components
    		Matcher matcherWithPort = patternWithPort.matcher(absoluteUrl);
    		if (matcherWithPort.matches()) {
    			protocol = matcherWithPort.group(1);
    			host = matcherWithPort.group(2);
    			port = matcherWithPort.group(3);
    			relativePath = matcherWithPort.group(4);    
				System.err.println("matched with port " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
    		} else {
    			System.err.println("parsing " + absoluteUrl + " against " + patternWithoutPort);    			
        		Matcher matcherWithoutPort = patternWithoutPort.matcher(absoluteUrl);
        		if (matcherWithoutPort.matches()) {
        			protocol = matcherWithoutPort.group(1);
        			host = matcherWithoutPort.group(2);
        			relativePath = matcherWithoutPort.group(3);
        			if ("http".equals(protocol)) {  // SUPER FIXUP HERE XACML wdn5ef
        				port = "80";
        			} else if ("http".equals(protocol)) {
        				port = "443";        				
        			} else {
        				System.err.println("unsupported protocol");
        			}
    				System.err.println("matched without port " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
        		} else {
    				System.err.println("didn't match");        			
    				System.err.println("captureWithPort="+captureWithPort);        			
    				System.err.println("captureWithoutPort="+captureWithoutPort);        			        			
        		}
    		}
    	} else {
    		relativePath = path;
        	absoluteUrl = HttpClient.makeUrl(protocol, host, port, relativePath);
			System.err.println("not matched " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
    	}
		System.err.println("protocol="+protocol);
		System.err.println("host="+host);
		System.err.println("port="+port);
		System.err.println("relativePath="+relativePath);
		System.err.println("absoluteUrl="+absoluteUrl);

        try {
        	Protocol easyhttps = null;
        	if (allowSelfSignedCertificates) {
        		//required to use EasySSLProtocolSocketFactory
        		easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), sslPort);
        	}
        	if (allowSelfSignedCertificates) {
        		/* http://jakarta.apache.org/commons/httpclient/sslguide.html seems to say that this should
        		enable self-signed certificates.  can't make it work.
        		Protocol.registerProtocol("https", easyhttps);
        		//check it out:
        		Protocol x = Protocol.getProtocol("https");
        		System.err.println("proto equals?="+easyhttps.equals(x));
        		System.err.println("proto ==?="+(easyhttps==x)); 
        		System.err.println("x="+x.toString());
        		System.err.println("x="+x.getScheme() + " " + x.getDefaultPort() + " " + x.isSecure());        		
        		*/
        	}        	
        	apacheCommonsClient = new org.apache.commons.httpclient.HttpClient(new MultiThreadedHttpConnectionManager());
        	if (allowSelfSignedCertificates) {
        		/* http://jakarta.apache.org/commons/httpclient/sslguide.html says that this works per client
        		instance to enable self-signed certificates.  and it does.
        		//check it out:
        		HostConfiguration hostConfiguration = httpClient.getHostConfiguration();
        		Protocol y = hostConfiguration.getProtocol();
        		System.err.println("proto equals?="+easyhttps.equals(y));
        		System.err.println("proto ==?="+(easyhttps==y)); 
        		System.err.println("y="+y.toString());
        		System.err.println("y="+y.getScheme() + " " + y.getDefaultPort() + " " + y.isSecure());        		
        		*/
        		apacheCommonsClient.getHostConfiguration().setHost(host, sslPort, easyhttps); //required
        	}
        } catch (Exception e) {
	  		log(e.getMessage());
	  		if (e.getCause() != null) {
		  		log(e.getCause().getMessage());	  			
	  		}
        }
    }

    public HttpClient(String url) {
        this(null, null, null, url);    	
    }    
    
    public static String makeUrl(String protocol, String host, String port, String more) {
    	String url = protocol + "://" + host 
		+ (((port != null) && ! "".equals(port)) ? (":" + port) : "") 
		+ more;
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
    
    public InputStream getStreamResponse() throws StreamIOException {
    	InputStream streamResponse = null;
  		if (getStatusCode() != HttpURLConnection.HTTP_OK) { 
  			throw new StreamIOException(
                "Server returned a non-200 response code ("
                + getStatusCode() + ") from GET request of URL: "
                + absoluteUrl);
  		}   
      	try {
      		streamResponse = getMethod.getResponseBodyAsStream();
      	} catch (Throwable th) {
      		th.printStackTrace();
      		throw new StreamIOException("[DatastreamReferencedContent] "
      			+ "returned an error.  The underlying error was a "
    			+ th.getClass().getName() + "  The message "
    			+ "was  \"" + th.getMessage() + "\"  .  ");
      	}    	
    	return streamResponse;
    }
    
    public int getContentLength() throws Throwable {
  		if (getStatusCode() != HttpURLConnection.HTTP_OK) {
  			throw new Exception(
                "Server returned a non-200 response code ("
                + getStatusCode() + ") from GET request of URL: "
                + absoluteUrl);
  		}       	
  		int contentLength = 0;
    	try {
  			contentLength = Integer.parseInt(getMethod.getResponseHeader("Content-Length").getValue());    		
    	} catch (Throwable t) {
    		log("HttpClient.getContentLength() " + t.getMessage());
    		throw t;
    	}
    	return contentLength;
    }
 
    private boolean log = true;
    
    private final void log(String msg) {
    	if (log) {
  	  	System.err.println(msg);	  		
    	}
    }
    
    public static final void main(String[] args) {
    	
    	try {
			System.err.println("ping="+ServerUtility.pingServletContainerStartup("/", 2));
			System.err.println("ping="+ServerUtility.pingServletContainerRunning("/", 2));
		} catch (GeneralException e1) {
			e1.printStackTrace();
		}
    	HttpClient httpClient = null;
   		System.err.println("SC:call HttpClient()...");
   		switch (args.length) {
   			case 1:
   			case 3:
   				httpClient = new HttpClient(args[0]);   				
   				break;
   			case 4:
   			case 6:
				httpClient = new HttpClient(args[0], args[1], args[2], args[3]);
				break;
			default:
   		}
   		System.err.println("...SC:call HttpClient()");
   		try {
	   		switch (args.length) {
				case 1:
				case 4:					
		       		System.err.println("SC:call HttpClient.doNoAuthnGet()...");
					httpClient.doNoAuthnGet(20000, 25, 1);
		       		System.err.println("...SC:call HttpClient.doNoAuthnGet()");					
					break;
				case 3:
		       		System.err.println("SC:call HttpClient.doAuthnGet()...");					
					httpClient.doAuthnGet(20000, 25, args[1], args[2], 1);
		       		System.err.println("...SC:call HttpClient.doAuthnGet()");										
					break;
				case 6:
		       		System.err.println("SC:call HttpClient.doAuthnGet()...");					
					httpClient.doAuthnGet(20000, 25, args[4], args[5], 1);
		       		System.err.println("...SC:call HttpClient.doAuthnGet()");					
					break;
				default:
	   		}
   		} catch (Exception e) {
   	   		System.err.println("exception thrown by HttpClient.do(No)AuthnGet()");   			
   		}
   		
   		System.err.println("SC:call HttpClient.getLineResponseUrl()...");			
    	String line = httpClient.getLineResponseUrl();
    	System.err.println("line response = " + line);
   		System.err.println("...SC:call HttpClient.getLineResponseUrl()");			
    }
    
}
