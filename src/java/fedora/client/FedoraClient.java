package fedora.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jrdf.graph.Literal;
import org.trippi.RDFFormat;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.DateUtility;
import fedora.server.types.gen.RepositoryInfo;

/**
 * General-purpose utility class for Fedora clients.  
 * 
 * Provides methods to get SOAP stubs for Fedora APIs.  Also serves as 
 * one-stop-shopping for issuing HTTP requests using Apache's HttpClient.  
 * 
 * Provides option for client to handle HTTP redirects
 * (notably 302 status that occurs with SSL auto-redirects at server.)
 *
 *
 * @author cwilper@cs.cornell.edu
 * @author payette@cs.cornell.edu
 * @version $Id$
 */

public class FedoraClient implements Constants {
	private static final String FEDORA_HOME = System.getProperty("fedora.home");
	private static final String LOG4J_PROPS = "fedora.client.resources.log4j";
    private static final String LOG4J_PATTERN = "log4j\\.appender\\.(\\w+)\\.File";
    public static final String FEDORA_URI_PREFIX = "info:fedora/";

    /** Seconds to wait before a connection is established. */
    public int TIMEOUT_SECONDS = 20;

    /** Seconds to wait while waiting for data over the socket (SO_TIMEOUT). */
    public int SOCKET_TIMEOUT_SECONDS = 120;

    /** Maxiumum http connections per host (for REST calls only). */
    public int MAX_CONNECTIONS_PER_HOST = 5;

    /** Maxiumum total http connections (for REST calls only). */
    public int MAX_TOTAL_CONNECTIONS = 20;

    /** Whether to automatically follow HTTP redirects. */
    public boolean FOLLOW_REDIRECTS = true;

    private static final Logger logger =
        Logger.getLogger(FedoraClient.class.getName());

    private String m_baseURL;
    private String m_user;
    private String m_pass;

    private String m_host;
    private UsernamePasswordCredentials m_creds;

    private MultiThreadedHttpConnectionManager m_cManager;

    private String m_serverVersion;

    public FedoraClient(String baseURL, String user, String pass) throws MalformedURLException {
        initLogger();
    	m_baseURL = baseURL;
        m_user = user;
        m_pass = pass;
        if (!baseURL.endsWith("/")) m_baseURL += "/";
        URL url = new URL(m_baseURL);
        m_host = url.getHost();
        m_creds = new UsernamePasswordCredentials(user, pass);
        m_cManager = new MultiThreadedHttpConnectionManager();       
    }

	public HttpClient getHttpClient() {
		m_cManager.setMaxConnectionsPerHost(MAX_CONNECTIONS_PER_HOST);
		m_cManager.setMaxTotalConnections(MAX_TOTAL_CONNECTIONS);
		HttpClient client = new HttpClient(m_cManager);
		client.setConnectionTimeout(TIMEOUT_SECONDS * 1000);
		client.setTimeout(SOCKET_TIMEOUT_SECONDS * 1000);
		client.getState().setCredentials(null, m_host, m_creds);
		client.getState().setAuthenticationPreemptive(true);
		return client;
	}

	/**
	 * Get an HTTP resource with the response as an InputStream, given a resource
     * locator that either begins with 'info:fedora/' , 'http://', or '/'.
     *
     * This method will follow redirects if FOLLOW_REDIRECTS is true.
     *
     * Note that if the HTTP response has no body, the InputStream will
     * be empty.  The success of a request can be checked with
     * getResponseCode().  Usually you'll want to see a 200.
     * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html for other codes.
     * 
	 * @param locator         A URL, relative Fedora URL, or Fedora URI that we want to 
	 *                        do an HTTP GET upon
	 * @param failIfNotOK     boolean value indicating if an exception should be thrown
	 *                        if we do NOT receive an HTTP 200 response (OK)
	 * @return HttpInputStream  the HTTP response
	 * @throws IOException
     */
    public HttpInputStream get(String locator, boolean failIfNotOK) throws IOException {
        return get(locator, failIfNotOK, FOLLOW_REDIRECTS);
    }
	  
	/**
	 * Get an HTTP resource with the response as an InputStream, given a URL.
     *
     * This method will follow redirects if FOLLOW_REDIRECTS is true.
     *
     * Note that if the HTTP response has no body, the InputStream will
     * be empty.  The success of a request can be checked with
     * getResponseCode().  Usually you'll want to see a 200.
     * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html for other codes.
     * 
	 * @param url             A URL that we want to do an HTTP GET upon
	 * @param failIfNotOK     boolean value indicating if an exception should be thrown
	 *                        if we do NOT receive an HTTP 200 response (OK)
	 * @return HttpInputStream  the HTTP response
	 * @throws IOException
     */
    public HttpInputStream get(URL url, boolean failIfNotOK) throws IOException {
        return get(url, failIfNotOK, FOLLOW_REDIRECTS);
    }
	   
	/**
	 * Get an HTTP resource with the response as an InputStream, given a resource
     * locator that either begins with 'info:fedora/' , 'http://', or '/'.
     *
     * Note that if the HTTP response has no body, the InputStream will
     * be empty.  The success of a request can be checked with
     * getResponseCode().  Usually you'll want to see a 200.
     * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html for other codes.
     * 
	 * @param locator         A URL, relative Fedora URL, or Fedora URI that we want to 
	 *                        do an HTTP GET upon
	 * @param failIfNotOK     boolean value indicating if an exception should be thrown
	 *                        if we do NOT receive an HTTP 200 response (OK)
	 * @param followRedirects boolean value indicating whether HTTP redirects
	 *                        should be handled in this method, or be passed along
	 *                        so that they can be handled later.
	 * @return HttpInputStream  the HTTP response
	 * @throws IOException
	 */
    public HttpInputStream get(String locator, boolean failIfNotOK, boolean followRedirects) throws IOException {

        // Convert the locator to a proper Fedora URL and the do a get.
        String url = getLocatorAsURL(locator);
        return get(new URL(url), failIfNotOK, followRedirects);
    }
    
	/**
	 * Get an HTTP resource with the response as an InputStream, given a URL.
	 *
	 * Note that if the HTTP response has no body, the InputStream will
	 * be empty.  The success of a request can be checked with
	 * getResponseCode().  Usually you'll want to see a 200.
	 * See http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html for other codes.
	 * 
	 * @param url             A URL that we want to do an HTTP GET upon
	 * @param failIfNotOK     boolean value indicating if an exception should be thrown
	 *                        if we do NOT receive an HTTP 200 response (OK)
	 * @param followRedirects boolean value indicating whether HTTP redirects
	 *                        should be handled in this method, or be passed along
	 *                        so that they can be handled later.
	 * @return HttpInputStream  the HTTP response
	 * @throws IOException
	 */
	public HttpInputStream get(URL url, boolean failIfNotOK, boolean followRedirects) throws IOException {

		String urlString = url.toString();
		logger.debug("FedoraClient is getting " + urlString);		
		HttpClient client = getHttpClient();
		GetMethod getMethod = new GetMethod(urlString);
		getMethod.setDoAuthentication(true);
		getMethod.setFollowRedirects(followRedirects);
		HttpInputStream in = new HttpInputStream(client, getMethod, urlString);
		int status = in.getStatusCode();
		if (failIfNotOK) {
			if (status != 200) {
				//if (followRedirects && in.getStatusCode() == 302){
				if (followRedirects && (300 <= status && status <= 399)) {
					// Handle the redirect here !
					logger.debug("FedoraClient is handling redirect for HTTP STATUS=" + status);
					//System.out.println("FedoraClient is handling redirect for HTTP STATUS=" + status);
					Header hLoc = in.getResponseHeader("location");
					if (hLoc != null) {
						logger.debug("FedoraClient is trying redirect location: " + hLoc.getValue());
						//System.out.println("FedoraClient is trying redirect location: " + hLoc.getValue());
						// Try the redirect location, but don't try to handle another level of redirection.						
						return get(hLoc.getValue(), true, false);	
					} else {
						try { 
							throw new IOException("Request failed [" + status + " " + in.getStatusText() + "]");
						} finally {
							try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
						}
					}
				} else {
					try { 
						throw new IOException("Request failed [" + in.getStatusCode() + " " + in.getStatusText() + "]");
					} finally {
						try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
					}
				}
			}
		}
		return in;
	}

	/**
     * Get an HTTP resource with the response as a String instead of an InputStream, 
     * given a resource locator that either begins with 'info:fedora/' , 'http://', or '/'.
	 * 
	 * @param locator         A URL, relative Fedora URL, or Fedora URI that we want to 
	 *                        do an HTTP GET upon
	 * @param failIfNotOK     boolean value indicating if an exception should be thrown
	 *                        if we do NOT receive an HTTP 200 response (OK)
	 * @param followRedirects boolean value indicating whether HTTP redirects
	 *                        should be handled in this method, or be passed along
	 *                        so that they can be handled later. 
	 * @return String  the HTTP response as a string
	 * @throws IOException
	 */
    public String getResponseAsString(String locator, boolean failIfNotOK, boolean followRedirects) throws IOException {
       
        InputStream in = get(locator, failIfNotOK, followRedirects);
        
        // Convert the response into a String.
        try {
            BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(in));
            StringBuffer buffer = new StringBuffer();
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line + "\n");
                line = reader.readLine();
            }
            return buffer.toString();
        } finally {
			try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
        }
    }

    private String getLocatorAsURL(String locator) throws IOException {
        
        String url;
        if (locator.startsWith(FEDORA_URI_PREFIX)) {
            url = m_baseURL + "get/" + locator.substring(FEDORA_URI_PREFIX.length());
        } else if (locator.startsWith("http://") || locator.startsWith("https://")) {
            url = locator;
        } else if (locator.startsWith("/")) {
            // assume it's for something within this Fedora server
            while (locator.startsWith("/")) {
                locator = locator.substring(1);
            }
            url = m_baseURL + locator;
        } else {
            throw new IOException("Bad locator (must start with '" + FEDORA_URI_PREFIX + "', 'http[s]://', or '/'");
        }
        return url;
    }
    
	/**
	 * Get SOAP stub for APIA with SSL redirect.  If the SOAP service endpoint 
	 * is configured for SSL auto-redirect, then get a stub that points to the 
	 * SSL redirect location.
	 * 
	 * Use of this stub will prevent the client from receiving a exception due
	 * to underlying HTTP 302 status (SSL redirect) being returned from server.
	 * @return
	 * @throws Exception
	 */
	public FedoraAPIA getAPIA() throws Exception {
		URL baseURL = new URL(m_baseURL);		
		String protocol = baseURL.getProtocol();
		String host = baseURL.getHost();
		int port = baseURL.getPort();
		String path = baseURL.getPath();
		if (port == -1) port = baseURL.getDefaultPort();
		APIAStubFactory.SOCKET_TIMEOUT_SECONDS = SOCKET_TIMEOUT_SECONDS;
		// Note that SSL auto redirect not supported in Fedora 2.0 
		// so we don't look for a redirect URL.
		if (getServerVersion().equals("2.0")) {
			return APIAStubFactory.getStubAltPath(protocol,
												  host, 
												  port,
												  baseURL.getPath() + "access/soap",  
												  m_user,
												  m_pass);
		} else {
			// Check whether there is SSL redirecting at the server for APIM 
			// (HTTP status 302) and get appropriate SOAP stub.
			URL redirectURL = getSSLRedirectLocationAPIA();
			if (redirectURL == null){
				//System.out.println("Using APIA stub with original URL...");
				return APIAStubFactory.getStubAltPath(protocol,
													  host, 
													  port,
													  baseURL.getPath() + "services/access",  
													  m_user,
													  m_pass);
			} else {
				//System.out.println("Using APIA stub with redirect URL: " + redirectURL);
				return APIAStubFactory.getStubAltPath(redirectURL.getProtocol(),
													  redirectURL.getHost(), 
													  redirectURL.getPort(),
													  redirectURL.getPath(),  
													  m_user,
													  m_pass);				
			}
		}
	}
	
	/**
	 * Get SOAP stub for APIM with SSL redirect.  If the SOAP service endpoint 
	 * is configured for SSL auto-redirect, then get a stub that points to the 
	 * SSL redirect location.
	 * 
	 * Use of this stub will prevent the client from receiving a exception due
	 * to underlying HTTP 302 status (SSL redirect) being returned from server.
	 * @return
	 * @throws Exception
	 */   
	public FedoraAPIM getAPIM() throws Exception {

		URL baseURL = new URL(m_baseURL);
		
		String protocol = baseURL.getProtocol();
		String host = baseURL.getHost();
		int port = baseURL.getPort();
		String path = baseURL.getPath();
		if (port == -1) port = baseURL.getDefaultPort();
		APIMStubFactory.SOCKET_TIMEOUT_SECONDS = SOCKET_TIMEOUT_SECONDS; 
		// Note that SSL auto redirect not supported in Fedora 2.0
		// so we don't look for a redirect URL.     
		if (getServerVersion().equals("2.0")) {
			return APIMStubFactory.getStubAltPath(protocol,
												  host, 
												  port,
												  baseURL.getPath() + "management/soap",  
												  m_user,
												  m_pass);
		} else {
			// Check whether there is SSL redirecting at the server for APIM
			// (HTTP status 302) and get appropriate SOAP stub.
			URL redirectURL = getSSLRedirectLocationAPIM();
			if (redirectURL == null){
				//System.out.println("Using APIM stub with original URL...");
				return APIMStubFactory.getStubAltPath(protocol,
													  host, 
													  port,
													  baseURL.getPath() + "services/management",   
													  m_user,
													  m_pass);			
			} else {
				//System.out.println("Using APIM stub with redirect URL: " + redirectURL);
				return APIMStubFactory.getStubAltPath(redirectURL.getProtocol(),
													  redirectURL.getHost(), 
													  redirectURL.getPort(),
													  redirectURL.getPath(),  
													  m_user,
													  m_pass);				
			}

		}
	}
	
	/**
	 * Ping the APIM SOAP endpoint to see if an HTTP 302 status 
	 * code is returned.  If so, this means the server has set 
	 * up an SSL redirect on that endpoint.  We obtain the redirect 
	 * URL from the HTTP header for later use.
	 * 
	 * @return  URL  the URL that the server returns as the SSL redirect location
	 *               or null if there is no redirect location
	 * @throws IOException
	 */    
	private URL getSSLRedirectLocationAPIM() throws IOException {

		URL redirectURL = null;
		HttpInputStream in = null;
			
		// Ping APIM service endpoint and get a redirect URL if one exists.
		try {
			in = get("/services/management", false, false);		
			logger.debug("Check for SSL redirect on APIM... HTTP STATUS=" + in.getStatusCode());
			//System.out.println("HTTP STATUS CODE = " + in.getStatusCode());
			if (in.getStatusCode() == 302) {
				Header h = in.getResponseHeader("location");
				if (h != null) {
					logger.debug("Detected SSL redirect for APIM: " + h.getValue());
					redirectURL = new URL(h.getValue());	
				}
			}
			//in.close();
			return redirectURL;
		} finally {
			try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
		}
	}
	
	/**
	 * Ping the APIA SOAP endpoint to see if an HTTP 302 status 
	 * code is returned.  If so, this means the server has set 
	 * up an SSL redirect on that endpoint.  We obtain the redirect 
	 * URL from the HTTP header for later use.
	 * 
	 * @return  URL  the URL that the server returns as the SSL redirect location
	 *               or null if there is no redirect location
	 * @throws IOException
	 */    
	private URL getSSLRedirectLocationAPIA() throws IOException {

		URL redirectURL = null;
		HttpInputStream in = null;
		
		// Ping APIA service endpoint and get a redirect URL if one exists.
		try {
			in = get("/services/access", false, false);		
			logger.debug("Check for SSL redirect on APIA... HTTP STATUS=" + in.getStatusCode());
			//System.out.println("HTTP STATUS CODE = " + in.getStatusCode());
			if (in.getStatusCode() == 302) {
				Header h = in.getResponseHeader("location");
				if (h != null) {
					logger.debug("Detected SSL redirect for APIA: " + h.getValue());
					redirectURL = new URL(h.getValue());	
				}
			}
			//in.close();
			return redirectURL;
		} finally {
			try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
		}
	}
    
    public String getServerVersion() throws IOException {
        if (m_serverVersion == null) {
            
            // Make the APIA call for describe repository
            // and make sure that HTTP 302 status is handled.
			String desc = getResponseAsString("/describe?xml=true", true, true);
            //System.out.println("DESCRIBE=" + desc);
            String[] parts = desc.split("<repositoryVersion>");
            if (parts.length < 2) {
                throw new IOException("Could not find repositoryVersion element in content of /describe?xml=true");
            }
            int i = parts[1].indexOf("<");
            if (i == -1) {
                throw new IOException("Could not find end of repositoryVersion element in content of /describe?xml=true");
            }
            m_serverVersion = parts[1].substring(0, i).trim();
            logger.debug("Server version is: " + m_serverVersion);
        }
        return m_serverVersion;
    }

    /**
     * Return the current date as reported by the Fedora server.
     *
     * @throws IOException if the HTTP Date header is not provided by the server
     *                     for any reason, or it is in the wrong format.
     */
    public Date getServerDate() throws IOException {
        HttpInputStream in = get("/describe", false, false);
        String dateString = null;
        try {
            Header header = in.getResponseHeader("Date");
            if (header == null) {
                throw new IOException("Date was not supplied in HTTP response "
                        + "header for " + m_baseURL + "describe");
            }
            dateString = header.getValue();

            // This is the date format recommended by RFC2616
            SimpleDateFormat format = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z");
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format.parse(dateString);

        } catch (ParseException e) {
            throw new IOException("Unparsable date (" + dateString 
                    + ") in HTTP response header for " + m_baseURL + "describe");
        } finally {
            in.close();
        }
    }

    public Date getLastModifiedDate(String locator) throws IOException {
    	if (locator.startsWith(FEDORA_URI_PREFIX)) {
    		String query = "select $date " +
    					   "from <#ri> " +
    					   "where <" + locator + "> <" + VIEW.LAST_MODIFIED_DATE.uri + "> $date";
    		Map map = new HashMap();
    		map.put("lang", "itql");
    		map.put("query", query);
    		TupleIterator tuples = getTuples(map);
    		try {
				if (tuples.hasNext()) {
					Map row = tuples.next();
					Literal dateLiteral = (Literal) row.get("date");
				    if (dateLiteral == null) {
				        throw new IOException("A row was returned, but it did not contain a 'date' binding");
				    }
				    return DateUtility.parseDateAsUTC(dateLiteral.getLexicalForm());
				} else {
					throw new IOException("No rows were returned");
				}
			} catch (TrippiException e) {
				throw new IOException(e.getMessage());
			} finally {
                try { tuples.close(); } catch (Exception e) { }
			}
    	} else {
	    	HttpClient client = getHttpClient();

	    	HeadMethod head = new HeadMethod(locator);
            head.setDoAuthentication(true);
	    	head.setFollowRedirects(FOLLOW_REDIRECTS);

            try {
    	    	int statusCode = client.executeMethod(head);
    	    	if (statusCode != HttpStatus.SC_OK) {
    	            throw new IOException("Method failed: " + head.getStatusLine());
    	          }
    	    	Header[] headers = head.getResponseHeaders();
    	
    	        // Retrieve just the last modified header value.
    	    	Header header = head.getResponseHeader("last-modified");
    	    	if (header != null) {
    	    		String lastModified = header.getValue();
    	    		return DateUtility.convertStringToDate(lastModified);
    	    	} else {
    	    		// return current date time
    	    		return new Date();
    	    	}
            } finally {
                head.releaseConnection();
            }
    	}
    }

	public void reloadPolicies() throws IOException {

		InputStream in = null;		
		try {
			in = get("/management/control?action=reloadPolicies", true, true);
		} finally {
			try { in.close(); } catch (Exception e) {logger.error("Can't close InputStream: " + e.getMessage());}
		}
	}


    /**
     * Get tuples from the remote resource index.
     *
     * The map contains <em>String</em> values for parameters that should be 
     * passed to the service. Two parameters are required:
     *
     * 1) lang
     * 2) query
     *
     * Two parameters to the risearch service are implied: 
     * 
     * 1) type = tuples
     * 2) format = sparql
     *
     * See http://www.fedora.info/download/2.0/userdocs/server/webservices/risearch/#app.tuples
     */
    public TupleIterator getTuples(Map params) throws IOException {
        params.put("type", "tuples");
        params.put("format", RDFFormat.SPARQL.getName());
        try {
            String url = getRIQueryURL(params);
            return TupleIterator.fromStream(get(url, true, true), RDFFormat.SPARQL);
        } catch (TrippiException e) {
            throw new IOException("Error getting tuple iterator: " + e.getMessage());
        }
    }

    private String getRIQueryURL(Map params) throws IOException {
        if (params.get("type") == null) throw new IOException("'type' parameter is required");
        if (params.get("lang") == null) throw new IOException("'lang' parameter is required");
        if (params.get("query") == null) throw new IOException("'query' parameter is required");
        if (params.get("format") == null) throw new IOException("'format' parameter is required");
        return m_baseURL + "risearch?" + encodeParameters(params);
    }

    private String encodeParameters(Map params) {
        StringBuffer encoded = new StringBuffer();
        Iterator iter = params.keySet().iterator();
        int n = 0;
        while (iter.hasNext()) {
            String name = (String) iter.next();
            if (n > 0) {
                encoded.append("&");
            }
            n++;
            encoded.append(name);
            encoded.append('=');
            try {
                encoded.append(URLEncoder.encode((String) params.get(name), "UTF-8"));
            } catch (UnsupportedEncodingException e) { // UTF-8 won't fail
            }
        }
        return encoded.toString();
    }
    
    private void initLogger() {
    	File logDir = new File(FEDORA_HOME, "client/logs");
    	Pattern pattern = Pattern.compile(LOG4J_PATTERN);
		Properties props = new Properties();
		ResourceBundle res = ResourceBundle.getBundle(LOG4J_PROPS);
		Enumeration keys = res.getKeys();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			String value = res.getString(key);
			Matcher matcher = pattern.matcher(key);
			// set a default location (e.g. in $FEDORA_HOME/logs/) if File appender location is empty
			if (matcher.matches() && (value == null || value.equals(""))) {
				value = new File(logDir, matcher.group(1).toLowerCase() + ".log").getAbsolutePath();
			}
			props.put(key, value);
		}
		PropertyConfigurator.configure(props);
    }
    
	// for quick testing
	public static void main(String[] args) {
		try {
			String protocol = "http";
			String host = "localhost";
			String port = "8080";
			String baseURL = protocol + "://" + host + ":" + port + "/fedora";
			System.out.println(">>>baseURL = " + baseURL);
			
			FedoraClient fc =  
				new FedoraClient(baseURL, "fedoraAdmin", "fedoraAdmin");
				//new FedoraClient("https://localhost:8443/fedora", "fedoraAdmin", "fedoraAdmin");
			URL newAPIM = fc.getSSLRedirectLocationAPIM();
			if (newAPIM != null) {
				System.out.println(">>>Redirect location for APIM is: " + newAPIM.toExternalForm());
			}
			URL newAPIA = fc.getSSLRedirectLocationAPIA();
			if (newAPIA != null) {
				System.out.println(">>>Redirect location for APIA is: " + newAPIA.toExternalForm());
			}

			Administrator.APIA=fc.getAPIA();
			Administrator.APIM=fc.getAPIM();
            	
			System.out.println(">>>Adminstrator is trying describeRepository using SOAP stub...");
			RepositoryInfo info=Administrator.APIA.describeRepository();
			System.out.println(">>>Repository baseURL from Administrator: " + info.getRepositoryBaseURL());
		} catch (Exception e) { 
			System.out.println("ERROR: " + e.getClass().getName() + " : " + e.getMessage());
		}
	}

}