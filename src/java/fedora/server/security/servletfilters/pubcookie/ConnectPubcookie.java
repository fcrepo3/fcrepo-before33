package fedora.server.security.servletfilters.pubcookie;
import java.net.URL;
import java.net.MalformedURLException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpVersion;
import java.util.Map;
import java.util.Iterator;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory; 
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import fedora.server.security.servletfilters.HttpTidyConnect;

/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class ConnectPubcookie {	
	
    private Log log = LogFactory.getLog(ConnectPubcookie.class);

	private boolean completedFully = false;
	private Node responseDocument = null;
	private Cookie[] responseCookies = null;

	public final boolean completedFully() {
		return completedFully;
	}
	
	public final Node getResponseDocument() {
		return responseDocument;
	}
	
	public final Cookie[] getResponseCookies() {
		return responseCookies;
	}
	
	private static final HttpMethodBase setup(HttpClient client, URL url, Map requestParameters, Cookie[] requestCookies) {
		LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()");		
		HttpMethodBase method = null;
		if (requestParameters == null) {
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " requestParameters == null");		
			method = new GetMethod(url.toExternalForm());
			//GetMethod is superclass to ExpectContinueMethod, so we don't require method.setUseExpectHeader(false);
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " after getting method");		
		} else {
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " requestParameters != null");		
			method = new PostMethod(url.toExternalForm()); // "http://localhost:8080/"
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " after getting method");
			
			//XXX method.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false); //new way
			//XXX method.getParams().setIntParameter(HttpMethodParams.SO_TIMEOUT, 10000);			
			//XXX method.getParams().setVersion(HttpVersion.HTTP_0_9); //or HttpVersion.HTTP_1_0 HttpVersion.HTTP_1_1
			
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " after setting USE_EXPECT_CONTINUE");

			//PostMethod is subclass of ExpectContinueMethod, so we require here:			
			//((PostMethod)method).setUseExpectHeader(false);
			//client.setTimeout(30000); // increased from 10000 as temp fix; 2005-03-17 wdn5e
			//HttpClientParams httpClientParams = new HttpClientParams();
			//httpClientParams.setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true); //old way
			//httpClientParams.setIntParameter(HttpMethodParams.SO_TIMEOUT, 30000);
			
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " A");

			Part[] parts = new Part[requestParameters.size()];
			Iterator iterator = requestParameters.keySet().iterator();			
			for (int i=0; iterator.hasNext(); i++) {
				String fieldName = (String) iterator.next();
				String fieldValue = (String) requestParameters.get(fieldName);
				StringPart stringPart = new StringPart(fieldName, fieldValue);
				parts[i] = stringPart;
				LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()"
						+ " part[" + i + "]==" + fieldName + "=" + fieldValue);		

				((PostMethod)method).addParameter(fieldName, fieldValue);  //old way
			}	
			
			LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " B");
			
			//XXX MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, method.getParams());
			// ((PostMethod)method).setRequestEntity(multipartRequestEntity); //new way			
		}
		HttpState state = client.getState();
		for (int i=0; i < requestCookies.length; i++) {
			Cookie cookie = requestCookies[i];
			state.addCookie(cookie);
		}		
		//method.setFollowRedirects(true); this is disallowed at runtime, so redirect won't be honored
		
		LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " C");
		LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " method==" + method);
		LogFactory.getLog(ConnectPubcookie.class).debug(ConnectPubcookie.class.getName() + ".setup()" + " method==" + method.toString());
		return method;
	}

	public final void connect(String urlString, Map requestParameters, Cookie[] requestCookies, String truststoreLocation, String truststorePassword) {
		log.fatal(this.getClass().getName() + ".connect() " + " url==" + urlString + " requestParameters==" + requestParameters + " requestCookies==" + requestCookies);		
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException mue) {
			log.fatal(this.getClass().getName() + ".connect() " + "bad configured url==" + urlString);
		}
		
		if (urlString.startsWith("https:") 
		&& (null != truststoreLocation) && (! "".equals(truststoreLocation))
		&& (null != truststorePassword) && (! "".equals(truststorePassword))) {
			System.err.println("setting " + FilterPubcookie.TRUSTSTORE_LOCATION_KEY + " to " + truststoreLocation);
			System.setProperty(FilterPubcookie.TRUSTSTORE_LOCATION_KEY, truststoreLocation);
			System.err.println("setting " + FilterPubcookie.TRUSTSTORE_PASSWORD_KEY + " to " + truststorePassword);
			System.setProperty(FilterPubcookie.TRUSTSTORE_PASSWORD_KEY, truststorePassword);

			System.err.println("setting " + FilterPubcookie.KEYSTORE_LOCATION_KEY + " to " + truststoreLocation);
			System.setProperty(FilterPubcookie.KEYSTORE_LOCATION_KEY, truststoreLocation);
			System.err.println("setting " + FilterPubcookie.KEYSTORE_PASSWORD_KEY + " to " + truststorePassword);
			System.setProperty(FilterPubcookie.KEYSTORE_PASSWORD_KEY, truststorePassword);

			
			System.setProperty("javax.net.debug","ssl,handshake,data,trustmanager");
			
			/*
			System.setProperty("javax.net.ssl.TRUSTSTORE", "C:\\Program Files\\Apache Software Foundation\\Tomcat 5.0\\TRUSTSTORE.p12");
			System.setProperty("javax.net.ssl.TRUSTSTOREPassword", "mypass");
			System.setProperty("javax.net.ssl.TRUSTSTOREType", "pkcs12");
			System.setProperty("javax.net.ssl.trustStore", "C:\\j2sdk1.4.2_05\\jre\\lib\\security\\cacerts");
			System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
			*/
		} else {
			System.err.println("DIAGNOSTIC urlString==" + urlString);
			System.err.println("didn't set " + FilterPubcookie.TRUSTSTORE_LOCATION_KEY + " to " + truststoreLocation);
			System.err.println("didn't set " + FilterPubcookie.TRUSTSTORE_PASSWORD_KEY + " to " + truststorePassword);
		}
 
		/*
		System.err.println("\n-a-");
		Protocol easyhttps = null;
		try {
			easyhttps = new Protocol("https", (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443); 
		} catch (Throwable t) {
			System.err.println(t);
			System.err.println(t.getMessage());
			if (t.getCause() != null) System.err.println(t.getCause().getMessage());
		}
		System.err.println("\n-b-");
		Protocol.registerProtocol("https", easyhttps); 
		System.err.println("\n-c-");
		*/
		
		HttpClient client = new HttpClient();
		log.fatal(this.getClass().getName() + ".connect() " + " b4 calling setup");		
		HttpMethodBase method = setup(client, url, requestParameters, requestCookies);
		log.fatal(this.getClass().getName() + ".connect() " + " after calling setup");	
		int statusCode = 0;
		try {
			log.fatal(this.getClass().getName() + ".connect() " + " b4 calling executeMethod");	
			client.executeMethod(method);
			log.fatal(this.getClass().getName() + ".connect() " + " after calling executeMethod");	
			statusCode = method.getStatusCode();
			log.fatal(this.getClass().getName() + ".connect() " + "(with configured url) statusCode==" + statusCode);
		} catch (Exception e) {
			log.fatal(this.getClass().getName() + ".connect() " + "failed original connect, url==" + urlString);
			System.err.println(e);
			System.err.println(e.getMessage());
			if (e.getCause() != null) System.err.println(e.getCause().getMessage());
			e.printStackTrace();
		}

		log.fatal(this.getClass().getName() + ".connect() " + " status code==" + statusCode);		

		if (302 == statusCode) {
			Header redirectHeader = method.getResponseHeader("Location");
			if (redirectHeader != null) {
				String redirectString = redirectHeader.getValue();
				if (redirectString != null) {
					URL redirectURL = null;
					try {
						redirectURL = new URL(redirectString);
						method = setup(client, redirectURL, requestParameters, requestCookies);
					} catch (MalformedURLException mue) {
						log.fatal(this.getClass().getName() + ".connect() " + "bad redirect, url==" + urlString);
					}
					statusCode = 0;
					try {
						client.executeMethod(method);
						statusCode = method.getStatusCode();
						log.fatal(this.getClass().getName() + ".connect() " + "(on redirect) statusCode==" + statusCode);
					} catch (Exception e) {
						log.fatal(this.getClass().getName() + ".connect() " + "failed redirect connect");
					}
				}
			}
		}
		if (statusCode == 200) { // this is either the original, non-302, status code or the status code after redirect
			log.fatal(this.getClass().getName() + ".connect() " + "status code 200");
			String content = null;
			//try {
			log.fatal(this.getClass().getName() + ".connect() " + "b4 gRBAS()");
				content = method.getResponseBodyAsString();
				log.fatal(this.getClass().getName() + ".connect() " + "after gRBAS() content==" + content);
			//} catch (IOException e) {
			//	log.error(this.getClass().getName() + ".connect() " + "couldn't get content");
			//	return;
			//}
			if (content == null) {
				log.fatal(this.getClass().getName() + ".connect() content==null");
				return;
			} else {
				log.fatal(this.getClass().getName() + ".connect() content != null, about to new Tidy");
				Tidy tidy = null;
				try {
					tidy = new Tidy();
				} catch (Throwable t) {
					System.err.println("new Tidy didn't");
					System.err.println(t);
					System.err.println(t.getMessage());
					if (t != null) System.err.println(t.getCause().getMessage());
				}
				log.fatal(this.getClass().getName() + ".connect() after newing Tidy, tidy==" + tidy);
				byte[] inputBytes = content.getBytes();
				log.fatal(this.getClass().getName() + ".connect() A1");
				ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes); 
				log.fatal(this.getClass().getName() + ".connect() A2");
				responseDocument = tidy.parseDOM(inputStream, null); //use returned root node as only output
				log.fatal(this.getClass().getName() + ".connect() A3");
			}
			log.fatal(this.getClass().getName() + ".connect() " + "b4 getState()");
			HttpState state = client.getState();
			log.fatal(this.getClass().getName() + ".connect() state==" + state);
	        try {
				responseCookies = state.getCookies();
				log.fatal(this.getClass().getName() + ".connect() responseCookies==" + responseCookies);
	        } catch (Throwable t) {
				log.fatal(this.getClass().getName() + ".connect() exception==" + t.getMessage());
				if (t.getCause() != null) log.fatal(this.getClass().getName() + ".connect() cause==" + t.getCause().getMessage());
	        }
			completedFully = true;
			log.fatal(this.getClass().getName() + ".connect() completedFully==" + completedFully);
		}
	}
	
}
