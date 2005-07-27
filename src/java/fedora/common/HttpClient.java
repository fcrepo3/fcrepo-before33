package fedora.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection; //for response status codes
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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
    	log("doNoAuthnGet.../ ");
			return doNoAuthnGet(millisecondsWait, redirectDepth, maxConnectionAttemptsPerUrl, SLEEP_MILLISECONDS);
    }    
	
	public GetMethod doNoAuthnGet(int millisecondsWait, int redirectDepth, int maxConnectionAttemptsPerUrl, int millisecondsSleep) 
	throws Exception {
    	log("doNoAuthnGet.../ ");
			return doAuthnGet(millisecondsWait, redirectDepth, null, null, maxConnectionAttemptsPerUrl, millisecondsSleep);
    }
	
	private static final String SLEEP_MILLISECONDS_KEY = "HTTPCLIENT_SLEEP_MILLISECONDS";
	private static int intSleepMilliseconds = 1000;
	static {
		String stringSleepMilliseconds = System.getProperty(SLEEP_MILLISECONDS_KEY);
		try {
			intSleepMilliseconds = Integer.parseInt(stringSleepMilliseconds);
		} catch (Exception e) {
			//go with hardcoded default
		}
	}
	private static final int SLEEP_MILLISECONDS = intSleepMilliseconds;

	
    public GetMethod doAuthnGet(int millisecondsWait, int redirectDepth, String username, String password, int maxConnectionAttemptsPerUrl) 
    throws Exception {
    	return doAuthnGet(millisecondsWait, redirectDepth, username, password, maxConnectionAttemptsPerUrl, SLEEP_MILLISECONDS);
    }
    
    private static final Hashtable instancesTable = new Hashtable();

    private static final boolean DEBUG = false;
    private static final boolean VERBOSE = false;
    private static final boolean DEBUG_MISSING_THISUSEFINISHED = false;
    private static final boolean DEBUG_NEEDLESS_THISUSEFINISHED = false;
    private static final int THRESHOLD = 0;
    
    private static final void dumpInstancesTable() {
    	Iterator it = instancesTable.keySet().iterator();
    	while (it.hasNext()) {
    		Object key = it.next();
    		Hashtable values = (Hashtable) instancesTable.get(key);
    		dumpInstancesTableEntry((Thread) key, values);
    	}
    }

    private static final void dumpInstancesTableEntry(Thread key, Hashtable values) {
    	System.err.println("thread (key) == " + key);
		Iterator it2 = values.keySet().iterator();
    	while (it2.hasNext()) {
    		Object key2 = it2.next();
    		if ("callStack".equals(key2)) {
    			printStackTrace(System.err, "setup trace was:", (StackTraceElement[]) values.get(key2));
    		} else {
        		System.err.println(key2 + " == " + values.get(key2));
    		}
    	}    		
    }

    private static final StackTraceElement[] getStackTrace() {
		try {
			throw new Exception();
		} catch (Exception e) {
			return e.getStackTrace();
		}    	
    }
    
    private static final void printStackTrace(PrintStream printStream, String header, StackTraceElement[] stackTraceElements) {
    	printStream.println(header);
		for (int i = 0; i < stackTraceElements.length; i++) {
			printStream.println("\t" + stackTraceElements[i]);    					
		}   	
    }
    
    /**
     * the care taken here is to avoid ConcurrentModificationException
     *
     */
    private static final void cleanInstancesTable() {
		System.err.println("begin cleanInstancesTable()");	
		Set deleteSet = new HashSet();
		int sizeBefore = instancesTable.size();
		Iterator iterator = instancesTable.keySet().iterator();
		while (iterator.hasNext()) {
			Thread thread = (Thread) iterator.next();
			if ((sizeBefore >= THRESHOLD) && (thread.getName().equals(Thread.currentThread().getName()) || ! thread.isAlive())) {
				if (DEBUG && VERBOSE) {
					if (thread.getName().equals(Thread.currentThread().getName())) {
						System.err.println("deleting current thread (by ref) from instancesTable");
					    dumpInstancesTableEntry(thread, (Hashtable) instancesTable.get(thread));
					} else if (! thread.isAlive()) {
						System.err.println("deleting dead thread from instancesTable");						
					    dumpInstancesTableEntry(thread, (Hashtable) instancesTable.get(thread));
					}
				}
				deleteSet.add(thread);
			} else {
				if (DEBUG && VERBOSE) {
					System.err.println("NOT deleting from instancesTable");						
					System.err.println("thread not deleted == " + thread);
					System.err.println("current thread == " + Thread.currentThread());						
				    dumpInstancesTableEntry(thread, (Hashtable) instancesTable.get(thread));						
				}				
			}
		}  
		Iterator deleteIterator = deleteSet.iterator();
		while (deleteIterator.hasNext()) {
	        thisUseFinished((Thread) deleteIterator.next());
		}
		System.err.println("end cleanInstancesTable()");		
    }
    
    private GetMethod checkOut(String workingPath, String usage) throws Exception {
    	if (DEBUG) System.err.println(">checkOut() " + instancesTable.size());
    	if (instancesTable.size() > THRESHOLD) {
    		System.err.println("HttpClient instancesTable abnormally large at " + instancesTable.size());
    		cleanInstancesTable();
    		if (DEBUG && VERBOSE) {
    			dumpInstancesTable();
    		}
    	}
    	GetMethod getMethod = new GetMethod(workingPath);
		if (getMethod == null) {
			if (DEBUG) {
				System.err.println("HttpClient no getMethod made to " + workingPath + " for " + usage);				
			}
		} else {
	    	Thread key = Thread.currentThread();
			if (instancesTable.get(key) != null) {
				if (DEBUG_MISSING_THISUSEFINISHED) { 
					Exception e =  new Exception("HttpClient this thread already in instance table");
					System.err.println(e.getMessage());				
					throw e;
				}
			}
			Hashtable instanceTable = new Hashtable();

			instancesTable.put(key, instanceTable);
			instanceTable.put("httpClient", this);
			instanceTable.put("workingPath", workingPath);
			instanceTable.put("usage", usage);
			instanceTable.put("callStack", getStackTrace());
			if (DEBUG && VERBOSE) {
				System.err.println("HttpClient new connection made to " + workingPath + " for " + usage);
	    		System.err.println("HttpClient instancesTable size now at " + instancesTable.size());
			}
		}
		if (DEBUG) System.err.println("<checkOut() " + instancesTable.size());
    	return getMethod;
    }

    private static synchronized void thisUseFinished(Thread key) {
    	if (DEBUG) System.err.println(">thisUseFinished() " + instancesTable.size());
		if (! instancesTable.containsKey(key)) {
			if (DEBUG) {
				System.err.println("HttpClient can't putBack():  this thread not in instancesTable " + key);				
				if (VERBOSE) {
        			printStackTrace(System.err, "teardown trace is:", getStackTrace());
				}
			}
		} else {
			Hashtable instanceTable = (Hashtable) instancesTable.get(key);
			if (instanceTable == null) {
				if (DEBUG_NEEDLESS_THISUSEFINISHED) {
					System.err.println("HttpClient can't putBack():  this thread has empty hashtable in instancesTable" + key);
					if (VERBOSE) {
	        			printStackTrace(System.err, "teardown trace is:", getStackTrace());
					}
				}
				
			} else {
				HttpClient httpClient = (HttpClient) instanceTable.get("httpClient");
				if (httpClient == null) {
					if (DEBUG_NEEDLESS_THISUSEFINISHED) System.err.println("HttpClient can't putBack():  httpClient not in instanceTable");				
				} else {
					if (DEBUG && VERBOSE) {
						dumpInstancesTable();
	        			printStackTrace(System.err, "teardown trace is:", getStackTrace());
					}
					if (DEBUG) System.err.println("closing httpClient");				
					httpClient.close();	
				}
				instancesTable.remove(key);
				if (DEBUG && VERBOSE) {
					if (DEBUG) System.err.println("HttpClient removal successful");				
				}			
			}			
		}
		if (DEBUG) System.err.println("<thisUseFinished() " + instancesTable.size());
    }    
    
    public static void thisUseFinished() {
        thisUseFinished(Thread.currentThread());
    }

    private static final String getUsage(String username) {
    	return (username == null) ? "" : username;
    }
    
    public GetMethod doAuthnGet(int millisecondsWait, int redirectDepth, String username, String password, int maxConnectionAttemptsPerUrl, int millisecondsSleep) 
    throws Exception 
	{
    	DefaultMethodRetryHandler retryhandler = new DefaultMethodRetryHandler();
    	retryhandler.setRequestSentRetryEnabled(true);
    	retryhandler.setRetryCount(5);

    	log("doAuthnGet... " + this.relativePath + "for " + username + " " + password + 
    			" " );
    	
    	//printStackTrace(System.err, "CALLTRACE", getStackTrace());
    	
	  	getMethod = null;
	  	String workingPath = "";
	  	try {
	  		try {
		  		boolean authenticate = false;
	  			log("setting timeouts (milliseconds) = " + millisecondsWait);	
		  		apacheCommonsClient.setConnectionTimeout(0); // waiting for connection
		  		apacheCommonsClient.setTimeout(0); // waiting for data
		  		if (
		  				((username == null) && (password == null)) 
		  		||  	((username != null) && "".equals(username) && (password != null) && "".equals(password))
				) {
		  			log("doAuthnGet(), don't authenticate " + username + " " + password);
		  		} else {
			  		if ((username == null) || (password == null) || ("".equals(username))) {
			  			throw new Exception("unexpected username password mix");
			  		}
		  			log("doAuthnGet(), do authenticate " + username + " " + password);		  		
			  		apacheCommonsClient.getState().setCredentials(null, null, new UsernamePasswordCredentials(username, password));
			  		apacheCommonsClient.getState().setAuthenticationPreemptive(true);
		  			log("doAuthnGet(), apacheCommonsClient=" + apacheCommonsClient);
			  		authenticate = true;
		  		}
		  		log("doAuthnGet(), after setup");
		  		int resultCode = -1;
		  		int connectionAttemptsPerUrl = 0;
		  		workingPath = absoluteUrl;
		  		for (int loops = 0; (workingPath != null) && (loops < redirectDepth) && (connectionAttemptsPerUrl < maxConnectionAttemptsPerUrl) ; loops++) {
		  			getMethod = checkOut(workingPath, getUsage(username));
		  	    	getMethod.setMethodRetryHandler(retryhandler);
		  			
		  			log("doAuthnGet(), getMethod=" + getMethod);
		  			log("doAuthnGet(), validate()=" + getMethod.validate());
		  			
		  			log("doAuthnGet(), workingpath="+workingPath);

		  			getMethod.setDoAuthentication(authenticate);
		  			log("doAuthnGet(), got GetMethod object=" + getMethod);
		  			getMethod.setFollowRedirects(true);
		  			
		  			
		  			
		  	    	log("just setFollowRedirects(true)");   	  
		  	    	
		  	    	log("getMethod.getRecoverableExceptionCount()" + getMethod.getRecoverableExceptionCount());
		  	    	log("getMethod.validate()" + getMethod.validate());
		  	    	
		  	    	try {
				  		log("doAuthnGet trying get, go=" + connectionAttemptsPerUrl);	
		  	    		resultCode = apacheCommonsClient.executeMethod(getMethod);
			  			if (300 <= resultCode && resultCode <= 399) {
			  				workingPath=getMethod.getResponseHeader("Location").getValue();
			  				connectionAttemptsPerUrl = 0;
			  				log("doAuthnGet(), got redirect, new url=" + workingPath);
			  				thisUseFinished();
			  			} else {
				  			workingPath = null; //signal loop completion			  				
				  			//don't putBack() here
			  			}
		  	    	} catch (IOException ioe) {
		  	    		connectionAttemptsPerUrl++;
				  		log("doAuthnGet got --inner-- IOException: " + ioe.getMessage());	
		  	    		Thread.currentThread().sleep(millisecondsSleep);
		  	    		thisUseFinished();
		  	    	}
		  	    	log("resultCode=" + resultCode + " absoluteUrl=" +absoluteUrl); 

		  		}
		  	} catch (HttpException httpe) {
		  		log("doAuthnGet got HttpException: " + httpe.getMessage());
		  		throw new Exception("got HttpException", httpe);		  		
		  	} catch (IOException ioe) {
		  		log("doAuthnGet got IOException: " + ioe.getMessage());	
		  		if (ioe.getCause() != null) {
			  		log(ioe.getCause().getMessage());	  			
		  		}		  		
	            ioe.printStackTrace();		  		
		  		throw new Exception("got IOException", ioe);		  		
		  	} catch (Exception e) {
		  		log("doAuthnGet got Exception: " + e.getMessage());		  		
		  		e.printStackTrace();
		  		throw new Exception("got Exception", e);		  		
		  	}
	  	} catch (Throwable th) {
  			if (getMethod != null) {
  				thisUseFinished();
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
	  		log(t.getMessage());
	  		if (t.getCause() != null) {
		  		log(t.getCause().getMessage());	  			
	  		}
		}
	}

	private final void closeStream() {
		try {
			getGetMethod().getResponseBodyAsStream().close();
		} catch (Throwable t) {
	  		log(t.getMessage());
	  		if (t.getCause() != null) {
		  		log(t.getCause().getMessage());	  			
	  		}
		}
    }

    private final void close() {
    	closeStream();
    	releaseConnection();
    }
       
    private static final String captureProtocol = "([^:]+?)://";
    private static final String captureHostWithPort = "([^:]+?):([^/]+?)";
    private static final String captureHostWithoutPort = "([^/]+?)";
    private static final String capturePath = "/(.*)";
    private static final String captureWithPort = captureProtocol + captureHostWithPort + capturePath;
    private static final String captureWithoutPort = captureProtocol + captureHostWithoutPort + capturePath;
    private static final Pattern patternWithPort = Pattern.compile(captureWithPort);
    private static final Pattern patternWithoutPort = Pattern.compile(captureWithoutPort);
    
    public HttpClient(String protocol, String host, String port, String path) {
    	log("HttpClient " + protocol + " " + host + " " + port + " " + path);
    	if ( (protocol == null) || "".equals(protocol)
    	||   (host == null) || "".equals(host)
    	||   (port == null) || "".equals(port) ) {
    		absoluteUrl = path;
			log("parsing " + absoluteUrl + " against " + patternWithPort);
    		//parse url as absolute url into components
    		Matcher matcherWithPort = patternWithPort.matcher(absoluteUrl);
    		if (matcherWithPort.matches()) {
    			protocol = matcherWithPort.group(1);
    			host = matcherWithPort.group(2);
    			port = matcherWithPort.group(3);
    			relativePath = matcherWithPort.group(4);    
				log("matched with port " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
    		} else {
    			log("parsing " + absoluteUrl + " against " + patternWithoutPort);    			
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
        				log("unsupported protocol");
        			}
    				log("matched without port " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
        		} else {
    				log("didn't match");        			
    				log("captureWithPort="+captureWithPort);        			
    				log("captureWithoutPort="+captureWithoutPort);        			        			
        		}
    		}
    	} else {
    		relativePath = path;
        	absoluteUrl = HttpClient.makeUrl(protocol, host, port, relativePath);
			log("not matched " + protocol + " " + host + " " + port + " " + absoluteUrl + " " + relativePath);
    	}
		log("protocol="+protocol);
		log("host="+host);
		log("port="+port);
		log("relativePath="+relativePath);
		log("absoluteUrl="+absoluteUrl);
		this.protocol = protocol;
		this.host = host;
		this.port = port;

        try {      	
        	apacheCommonsClient = new org.apache.commons.httpclient.HttpClient(new MultiThreadedHttpConnectionManager());
        } catch (Exception e) {
    		log("caught exception");
	  		log(e.getMessage());
	  		if (e.getCause() != null) {
		  		log(e.getCause().getMessage());	  			
	  		}
        }
        log("PROTOCOL-=" + getProtocol());
        log("HOST-=" + getHost());
  		log("PORT-=" + getPort());
  		log("REL URL-=" + relativePath);
  		log("ABS URL-=" + absoluteUrl);
		log("exiting constructor");
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
  			//putBack();
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

    public static final void main(String[] args) {
    	
    	try {
			slog("ping="+ServerUtility.pingServletContainerStartup("/", 2));
			slog("ping="+ServerUtility.pingServletContainerRunning("/", 2));
		} catch (GeneralException e1) {
			e1.printStackTrace();
		}
    	HttpClient httpClient = null;
   		slog("SC:call HttpClient()...");
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
   		slog("...SC:call HttpClient()");
   		try {
	   		switch (args.length) {
				case 1:
				case 4:					
		       		slog("SC:call HttpClient.doNoAuthnGet()...");
					httpClient.doNoAuthnGet(20000, 25, 1);
		       		slog("...SC:call HttpClient.doNoAuthnGet()");					
					break;
				case 3:
		       		slog("SC:call HttpClient.doAuthnGet()...");					
					httpClient.doAuthnGet(20000, 25, args[1], args[2], 1);
		       		slog("...SC:call HttpClient.doAuthnGet()");										
					break;
				case 6:
		       		slog("SC:call HttpClient.doAuthnGet()...");					
					httpClient.doAuthnGet(20000, 25, args[4], args[5], 1);
		       		slog("...SC:call HttpClient.doAuthnGet()");					
					break;
				default:
	   		}
   		} catch (Exception e) {
   	   		slog("exception thrown by HttpClient.do(No)AuthnGet()");   			
   		}
   		
   		slog("SC:call HttpClient.getLineResponseUrl()...");			
    	String line = httpClient.getLineResponseUrl();
    	slog("line response = " + line);
   		slog("...SC:call HttpClient.getLineResponseUrl()");		
   		thisUseFinished();
    }
    
}
