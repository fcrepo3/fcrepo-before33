package fedora.server.storage;

import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.Header;
import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.security.BackendPolicies;
import fedora.server.security.BackendSecurity;
import fedora.server.security.BackendSecuritySpec;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.Property;
import fedora.server.utilities.ServerUtility;
import fedora.common.HttpClient;

/**
 *
 * <p><b>Title:</b> DefaultExternalContentManager.java</p>
 * <p><b>Description:</b> Provides a mechanism to obtain external HTTP-accessible
 * content.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DefaultExternalContentManager extends Module
    implements ExternalContentManager
{

  private String m_userAgent;
  private String fedoraServerHost;
  private String fedoraServerPort;
  private String fedoraServerRedirectPort;

  /**
   * <p> Creates a new DefaultExternalContentManager.</p>
   *
   * @param moduleParameters The name/value pair map of module parameters.
   * @param server The server instance.
   * @param role The module role name.
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public DefaultExternalContentManager(Map moduleParameters,
                                       Server server, String role)
      throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
  }

  /**
   * Initializes the Module based on configuration parameters. The
   * implementation of this method is dependent on the schema used to define
   * the parameter names for the role of
   * <code>fedora.server.storage.DefaultExternalContentManager</code>.
   *
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public void initModule() throws ModuleInitializationException
  {
    try
    {
      Server s_server = this.getServer();
      s_server.logInfo("DefaultExternalContentManager initialized");
      m_userAgent=getParameter("userAgent");
      if (m_userAgent==null) {
        m_userAgent="Fedora";
      }

      fedoraServerPort = s_server.getParameter("fedoraServerPort");
      fedoraServerHost = s_server.getParameter("fedoraServerHost");
      fedoraServerRedirectPort = s_server.getParameter("fedoraRedirectPort");


    } catch (Throwable th)
    {
      throw new ModuleInitializationException("[DefaultExternalContentManager] "
          + "An external content manager "
          + "could not be instantiated. The underlying error was a "
          + th.getClass().getName() + "The message was \""
          + th.getMessage() + "\".", getRole());
    }
  }

  /**
   * A method that reads the contents of the specified URL and returns the
   * result as a MIMETypedStream
   *
   * @param url The URL of the external content.
   * @return A MIME-typed stream.
   * @throws HttpServiceNotFoundException If the URL connection could not
   *         be established.
   */
  public MIMETypedStream getExternalContent(String url, Context context)
      throws GeneralException, HttpServiceNotFoundException {
  	log("in getExternalContent(), url=" + url);
  	MIMETypedStream httpContent = null;
  	try {  		 
  		
  		String backendUsername = "";
  		String backendPassword = "";
  		boolean backendSSL = false;
  		String modURL = url;
		if (isURLFedoraServer(modURL)) {
		    BackendSecuritySpec m_beSS;
		    BackendSecurity m_beSecurity = (BackendSecurity) getServer().getModule("fedora.server.security.BackendSecurity");
		    try {
		    	m_beSS = m_beSecurity.getBackendSecuritySpec();
		    } catch (Exception e) {
		    	throw new ModuleInitializationException("Can't intitialize BackendSecurity module (in default access) from Server.getModule", getRole());
		    }	            
		    Hashtable beHash = m_beSS.getSecuritySpec(BackendPolicies.FEDORA_INTERNAL_CALL);
		    backendUsername = (String) beHash.get("callUsername");
		    backendPassword = (String) beHash.get("callPassword");	            
		    backendSSL = new Boolean((String) beHash.get("callBasicAuth")).booleanValue();
		    if (backendSSL) {
		    	if (modURL.startsWith("http:")) {
		    		modURL = modURL.replaceFirst("http:", "https:");
		    	}
		    	modURL = modURL.replaceFirst(":"+fedoraServerPort+"/", ":"+fedoraServerRedirectPort+"/");
		    }    
		}
		if (fedora.server.Debug.DEBUG) {
		   	System.out.println("************************* backendUsername: "+backendUsername+ "     backendPassword: "+backendPassword+"     backendSSL: "+backendSSL);
		   	System.out.println("************************* doAuthnGetURL: "+modURL);
		}
		HttpClient client = new HttpClient(modURL);
  		client.doAuthnGet(20000, 25, backendUsername, backendPassword, 1);
  		if (client.getStatusCode() != HttpURLConnection.HTTP_OK) {
  			log("in getExternalContent(), got bad code=" + client.getStatusCode());
  			throw new StreamIOException(
                "Server returned a non-200 response code ("
                + client.getStatusCode() + ") from GET request of URL: "
                + url);
  		}          
  		log("in getExternalContent(), got 200");
//comment from earlier implementation; means anything?:  connection.setInstanceFollowRedirects(true);
  		Header[] headers = client.getGetMethod().getResponseHeaders();
  		Property[] headerArray = new Property[headers.length];
  		for (int i = 0; i < headers.length; i++) {
  			headerArray[i] = new Property();
  			headerArray[i].name = headers[i].getName();
  			headerArray[i].value = headers[i].getValue();
  			log("in getExternalContent(), (after loop) " + headerArray[i].name + "=" + headerArray[i].value);
  		}
  		String contentType = "text/plain";
  		if (client.getGetMethod().getResponseHeader("Content-Type") != null) {
  			contentType = client.getGetMethod().getResponseHeader("Content-Type").getValue();
  		}
  		log("in getExternalContent(), contentType=" + contentType);
  		for (int ha=0; ha<headerArray.length; ha++) {
  			log("in getExternalContent(), header=" + headerArray[ha].name + "=" + headerArray[ha].value);
  		}
  		httpContent = new MIMETypedStream(contentType, client.getGetMethod().getResponseBodyAsStream(), headerArray);
  		//get.releaseConnection() before stream is read would give java.io.IOException: Attempted read on closed stream. 
  		log("in getExternalContent(), httpContent=" + httpContent);
  	} catch (Throwable th) {
  		th.printStackTrace();
  		throw new HttpServiceNotFoundException("[DefaultExternalContentManager] "
  			+ "returned an error.  The underlying error was a "
			+ th.getClass().getName() + "  The message "
			+ "was  \"" + th.getMessage() + "\"  .  ");
  	} finally {
  		log("in getExternalContent(), in finally");
 	
  	}    	
	return(httpContent);
  }
  
  private boolean isURLFedoraServer(String url) {
      boolean isFedoraLocalService = false;
      
      // Check for Fedora Local Services like saxon, fop, imagemanip, and soapclient
      // Although these webapps are in the same web container as the Fedora server
      // local services are treated like other backend services so must check for
      // more than just hostname and port to determine if URL is a fedora-to-fedora
      // server callback or a callback to a local service.
      if (url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/saxon") ||
          url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/fop") ||
          url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/imagemanip") ||
          url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/soapclient") ||
          url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/saxon") ||
          url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/fop") ||
          url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/imagemanip") ||
          url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/soapclient")) {
          isFedoraLocalService = true;
          if (fedora.server.Debug.DEBUG) System.out.println("******************URL was Local Service callback: "+url);
      }
      if ( (url.startsWith("http://"+fedoraServerHost) || url.startsWith("https://"+fedoraServerHost)) &&
          !isFedoraLocalService) {
          if (fedora.server.Debug.DEBUG) System.out.println("******************URL was Fedora-to-Fedora callback: "+url);
          return true;
      } else {
          if (fedora.server.Debug.DEBUG) System.out.println("******************URL was Backend Service callback: "+url);
          return false;
      }
          
  }  
  
  private boolean log = false;
  
  private final void log(String msg) {
  	if (log) {
	  	System.err.println(msg);	  		
  	}
  }
  
}
