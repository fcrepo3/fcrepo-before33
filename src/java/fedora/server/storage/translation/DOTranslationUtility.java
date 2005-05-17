package fedora.server.storage.translation;

import java.io.File;
import java.util.regex.Pattern;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Disseminator;
import fedora.server.utilities.DateUtility;

/**
 *
 * <p><b>Title:</b> DOTranslationUtility.java</p>
 * <p><b>Description:
 *       Utility methods for usage by digital object serializers and
 *       deserializers.  This class provides methods for detecting various
 *       forms of relative repository URLs, which are URLs that point to
 *       the hostname and port of the local repository.  Methods will detect
 *       these kinds of URLS in datastream location fields and in special
 *       cases of inline XML.  Methods are available to convert these URLS
 *       back and forth from relative URL syntax, to Fedora's internal local
 *       URL syntax, and to absolute URL sytnax.  This utility class defines
 *       different "translation contexts" and the format of these relative URLs
 *       will be set appropriately to the context.  Currently defined translation
 *       contexts are:
 *       0=Deserialize XML into java object appropriate for in-memory usage
 *       1=Serialize java object to XML appropriate for "public" export (absolute URLs)
 *       2=Serialize java object to XML appropriate for move/migrate to another repository
 *       3=Serialize java object to XML appropriate for internal storage</b> </p>
 *
 *       The public "normalize*" methods in this class should be called to make the
 *       right decisions about what conversions should occur for what contexts.
 *
 *       Other utility methods set default values for datastreams and
 *       disseminators.
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public abstract class DOTranslationUtility {

 	/**
 	 *
 	 * 	DESERIALIZE_INSTANCE: Deserialize XML into a java object appropriate
 	 *  for in-memory usage.  This will make the value of relative
 	 *  repository URLs appropriate for instantiations of the digital
 	 *  object in memory. For External (E) and Redirected (R) datastreams,
 	 *  any URLs that are relative to the local repository are converted
 	 *  to absolute URLs using the currently configured hostname:port of
 	 *  the repository. To do this, the dsLocation is searched for instances
 	 *  the Fedora local URL string ("http://local.fedora.server") which
 	 *  is the way Fedora internally keeps track of instances of relative
 	 *  repository URLs. For Managed Content (M) datastreams, the internal
 	 *  identifiers are instantiated as is.  Also, certain reserved inline
 	 *  XML datastreams (WSDL and SERVICE_PROFILE) are searched for relative
 	 *  repository URLs and they are made absolute.
 	 */
	public static final int DESERIALIZE_INSTANCE=0;

	/**
	 *
	 * 	SERIALIZE_EXPORT_PUBLIC:  Serialize digital object to XML appropriate
	 *  for "public" external use. This is context is appropriate 
	 *  when the exporting repository will continue to exist and will continue 
	 *  to support callback URLs for datastream content and disseminations.
	 *  This gives a "public" export of an object in which all relative repository 
	 *  URLs AND internal identifiers are converted to absolute callback URLs.  
	 * 
	 *  For External (E) and Redirected (R) datastreams, any URLs that are 
	 *  relative to the local repository are converted to absolute URLs using 
	 *  the currently configured hostname:port of the repository. 
	 *  For Managed Content (M) datastreams, the internal identifiers in 
	 *  dsLocation are converted to default dissemination URLs so they can 
	 *  serve as callbacks to the repository to obtain the internally managed content.  
	 *  Also, selected inline XML datastreams (i.e., WSDL and SERVICE_PROFILE)
	 *  are searched for relative repository URLs and they are made absolute.
	 */
	public static final int SERIALIZE_EXPORT_PUBLIC=1;

	/**
	 *
	 * 	SERIALIZE_EXPORT_MIGRATE:  Serialize digital object to XML
	 *  in a manner appropriate for migrating or moving objects from 
	 *  one repository to another.  This context is appropriate when the local 
	 *  repository will NOT be available after objects have been migrated 
	 *  to a new repository. 
	 * 
	 *  For External (E) and Redirected (R)datastreams, any URLs that are 
	 *  relative to the local repository will be expressed with the Fedora 
	 *  local URL syntax (which consists of the string "local.fedora.server" 
	 *  standing in place of the actual "hostname:port").
	 *  This enables a new repository to ingest the serialization and maintain
	 *  the relative nature of the URLs (they will become relative to the *new*
	 *  repository.  Also, for Managed Content (M) datastreams, the internal
	 *  identifiers in dsLocation are converted to default dissemination URLs.
	 *  This enables the new repository to callback to the old repository
	 *  to obtain the content bytestream to be stored in the new repository.
	 *  Also, within selected inline XML datastreams (i.e., WSDL and
	 *  SERVICE_PROFILE) any URLs that are relative to the local repository
	 *  will also be expressed with the Fedora local URL syntax. 
	 */
	public static final int SERIALIZE_EXPORT_MIGRATE=2;

	/**
	 * 	SERIALIZE_STORAGE_INTERNAL:   Serialize java object to XML appropriate
	 *  for persistent storage in the repository, ensuring that any URLs that
	 *  are relative to the local repository are stored with the Fedora local
	 *  URL syntax.  The Fedora local URL syntax consists of the string
	 *  "local.fedora.server" standing in place of the actual "hostname:port"
	 *  on the URL).  Managed Content (M) datastreams are stored with internal
	 *  identifiers in dsLocation.  Also, within selected inline XML datastreams
	 *  (i.e., WSDL and SERVICE_PROFILE) any URLs that are relative to the
	 *  local repository will also be stored with the Fedora local URL
	 *  syntax. Note that a view of the storage serialization can be obtained
	 *  via the getObjectXML method of API-M.
	 */
	public static final int SERIALIZE_STORAGE_INTERNAL=3;
	
	// Fedora URL LOCALIZATION Pattern:
	// Pattern that is used as the internal replacement syntax for URLs that
	// refer back to the local repository.  This pattern virtualized the
	// repository server address, so that if the host:port of the repository is
	// changed, objects that have URLs that refer to the local repository won't break.
	public static Pattern s_fedoraLocalPattern = Pattern.compile("http://local.fedora.server/");
	
	// PATTERN FOR DEPRECATED METHOD (getItem of the Default Disseminator), for example:
	public static Pattern s_getItemPattern = Pattern.compile("/fedora-system:3/getItem\\?itemID=");

	// ABSOLUTE REPOSITORY URL Patterns:
	// Patterns of how the protocol and repository server address may be encoded
	// in a URL that points back to the local repository.
	private static Pattern s_servernamePort; // "http://hostname:port/"
	private static Pattern s_servername;     // "http://hostname/"
	private static Pattern s_localhostPort;  // "http://localhost:port/"
	private static Pattern s_localhost;      // "http://localhost/"
    
	private static Pattern s_servernamePortSSL; // "https://hostname:redirectport/"
	private static Pattern s_servernameSSL;     // "https://hostname/"
	private static Pattern s_localhostPortSSL;  // "https://localhost:redirectport/"
	private static Pattern s_localhostSSL;      // "https://localhost/"

	// CALLBACK DISSEMINATION URL Pattern (for M datastreams in export files):
	// Pattern of how protocol, repository server address, and path is encoded
	// for a callback dissemination URL to the local repository.
	// This is used for encoding datastream location URLs for Managed Content
	// datastreams inside an export file.  Internal Fedora identifiers for
	// the Managed Content datastreams are replaced with public callback URLS.
	private static String s_localDissemUrlStart; // "http://hostname:port/fedora/get/"
	

	// The actual host and port of the Fedora repository server
	private static String s_hostInfo = null;
	private static boolean m_serverOnPort80=false;
	private static boolean m_serverOnRedirectPort443=false;

    // initialize static class with stuff that's used by all DO Serializerers
    static {
			// get host port from system properties (for testing without server instance)
			String fedoraHome=System.getProperty("fedora.home");
			String fedoraServerHost=System.getProperty("fedoraServerHost");
			String fedoraServerPort=System.getProperty("fedoraServerPort");
			String fedoraServerPortSSL=System.getProperty("fedoraRedirectPort");		
			if (fedoraServerPort != null){
				if (fedoraServerPort.equals("80")) {
					m_serverOnPort80=true;
				}
			}
			if (fedoraServerPortSSL != null){
				if (fedoraServerPortSSL.equals("443")) {
					m_serverOnRedirectPort443=true;
				}
			}

			// otherwise, get host port from the server instance if they are null			
			if (fedoraServerHost == null || fedoraServerPort == null) {
				// if fedoraServerHost or fedoraServerPort system properties
                // are not defined, assume we need to get a Server instance
                // to determine these values.
				try {
					Server s=Server.getInstance(new File(fedoraHome));
					fedoraServerHost=s.getParameter("fedoraServerHost");
					fedoraServerPort=s.getParameter("fedoraServerPort");
					fedoraServerPortSSL=s.getParameter("fedoraRedirectPort");
					
					if (fedoraServerPort.equals("80")) {
						m_serverOnPort80=true;
					}
					if (fedoraServerPortSSL.equals("443")) {
						m_serverOnRedirectPort443=true;
					}
				} catch (InitializationException ie) {
					// can only possibly happen during failed testing, in which
					// case it's ok to do a System.exit
					System.err.println("STARTUP ERROR: " + ie.getMessage());
					System.exit(1);
				}
			}
			// set the currently configured host:port of the repository
			s_hostInfo="http://" + fedoraServerHost;
			if (!fedoraServerPort.equals("80") && !fedoraServerPort.equals("443")) {
				s_hostInfo=s_hostInfo + ":" + fedoraServerPort;
			}
			s_hostInfo=s_hostInfo + "/";

			// compile the pattern for public dissemination URLs at local server
			s_localDissemUrlStart= s_hostInfo + "fedora/get/";

			// compile other patterns using the configured host and port
			s_servernamePort = Pattern.compile("http://" + fedoraServerHost + ":" + fedoraServerPort + "/");
			s_servername = Pattern.compile("http://" + fedoraServerHost + "/");
			s_localhostPort = Pattern.compile("http://localhost:" + fedoraServerPort + "/");
			s_localhost = Pattern.compile("http://localhost/");
			
			s_servernamePortSSL = Pattern.compile("https://" + fedoraServerHost + ":" + fedoraServerPortSSL + "/");
			s_servernameSSL = Pattern.compile("https://" + fedoraServerHost + "/");
			s_localhostPortSSL = Pattern.compile("https://localhost:" + fedoraServerPortSSL + "/");
			s_localhostSSL = Pattern.compile("https://localhost/");
    }

	/**
	 * Make URLs that are relative to the local Fedora repository ABSOLUTE URLs.
	 * First, see if any URLs are expressed in relative URL syntax
	 * (beginning with "fedora/get" or "fedora/search") and convert these
	 * to the special Fedora local URL syntax ("http://local.fedora.server/...").
	 * Then look for all URLs that contain the special Fedora local URL syntax
	 * and replace instances of this string with the actual host:port configured for
	 * the repository.  This ensures that all forms of relative repository URLs
	 * are converted to proper absolute URLs that reference the hostname:port of the
	 * local Fedora repository.  Examples:
	 *
	 * 	 "http://local.fedora.server/fedora/get/demo:1/DS1"
	 *        is converted to
	 *        "http://myrepo.com:8080/fedora/get/demo:1/DS1"
	 *
	 * 	 "fedora/get/demo:1/DS1"
	 *        is converted to
	 *        "http://myrepo.com:8080/fedora/get/demo:1/DS1"
	 *
	 * 	 "http://local.fedora.server/fedora/get/demo:1/bdef:1/getFoo?in="http://local.fedora.server/fedora/get/demo:2/DC"
	 *        is converted to
	 * 	      "http://myrepo.com:8080/fedora/get/demo:1/bdef:1/getFoo?in="http://myrepo.com:8080/fedora/get/demo:2/DC"
	 * @param xmlContent
	 * @return  String with all relative repository URLs and Fedora local URLs
	 *          converted to absolute URL syntax.
	 */
	private static String makeAbsoluteURLs(String input) {
		String output=input;
		
		// Make absolute URLs out of all instances of the Fedora local URL syntax ...
		output=s_fedoraLocalPattern.matcher(output).replaceAll(s_hostInfo);
		if (fedora.server.Debug.DEBUG) {
			System.out.println("makeAbsoluteURLs: input=" + input);
			System.out.println("makeAbsoluteURLs: output=" + output + "\n");
		}
		return output;
	}

	
	/**
	 * Detect all forms of URLs that point to the local Fedora repository and
	 * make sure they are encoded in the special Fedora local URL syntax
	 * (http://local.fedora.server/...").  First, look for relative URLs that
	 * begin with "fedora/get" or "fedora/search" replaces instances of these
	 * string patterns with the special Fedora relative URL syntax. Then, look
	 * for absolute URLs that have a host:port equal to the host:port
	 * currently configured for the Fedora repository and replace host:port with
	 * the special string. The special Fedora relative URL string provides a
	 * consistent unique string be easily searched for and either converted back
	 * to an absolute URL or a relative URL to the repository. Examples:
	 *
	 * 	 "http://myrepo.com:8080/fedora/get/demo:1/DS1"
	 *        is converted to
	 *        "http://local.fedora.server/fedora/get/demo:1/DS1"
	 *
	 * 	 "https://myrepo.com:8443/fedora/get/demo:1/bdef:1/getFoo?in="http://myrepo.com:8080/fedora/get/demo:2/DC"
	 *        is converted to
	 * 	      "http://local.fedora.server/fedora/get/demo:1/bdef:1/getFoo?in="http://local.fedora.server/fedora/get/demo:2/DC"
	 *
	 * 	 "http://myrepo.com:8080/saxon..." (internal service in bMech WSDL)
	 *        is converted to
	 * 	      "http://local.fedora.server/saxon..."
	 * @param input
	 * @return  String with all forms of relative repository URLs converted to
	 *          the Fedora local URL syntax.
	 */
	private static String makeFedoraLocalURLs(String input) {
		String output=input;
		
		// Detect any absolute URLs that refer to the local repository 
		// and convert them to the Fedora LOCALIZATION URL syntax
		// (i.e., "http://local.fedora.server/...")\
		
		// convert URLs that begin with http along with host and port
		// explicitly configured for the repository
		output=s_servernamePort.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());
		output=s_localhostPort.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());
			
		// convert URLs that begin with https along with the host and port
		// explicitly configured for the repository
		output=s_servernamePortSSL.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());
		output=s_localhostPortSSL.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());
			
		if (m_serverOnPort80) {
			// if the server is running on port 80, convert
			// URLs that begin with "http://localhost/"
			output=s_servername.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());
			output=s_localhost.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());
		}
		if (m_serverOnRedirectPort443) {
			// if the server is running on port 443, convert
			// URLs that begin with "https://localhost/"
			output=s_servernameSSL.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());
			output=s_localhostSSL.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());
		}
		if (fedora.server.Debug.DEBUG) {
			System.out.println("makeFedoraLocalURLs: input=" + input);
			System.out.println("makeFedoraLocalURLs: output=" + output + "\n");
		}
		return output;
	}

	/**
	 *  Utility method to detect instances of of dsLocation URLs that use a deprecated
	 *  default disseminator method (/fedora/get/{PID}/fedora-system:3/getItem?itemID={DSID} 
	 *  and replace it with the new API-A-LITE syntax for getting a datastream
	 *  (/fedora/get/{PID}/{DSID}
	 * @param input
	 * @return
	 */
	private static String convertGetItemURLs(String input) {
		String output=input;
		
		// Detect the old default disseminator syntax for getting datastreams
		// (i.e., getItem), and replace with new API-A-LITE syntax.
		
		output=s_getItemPattern.matcher(input).replaceAll("/");
		if (fedora.server.Debug.DEBUG) {
			System.out.println("convertGetItemURLs: input=" + input);
			System.out.println("convertGetItemURLs: output=" + output + "\n");
		}
		return output;
	}

	/*
	 * Utility method to normalize the value of datastream location depending
	 * on the translation context.  This is mainly to deal with External (E)
	 * and Redirected (R) datastream locations that are self-referential to the
	 * local repository (i.e., relative repository URLs) and with Managed
	 * Content (M) datastreams whose location is an internal identifier.
	 *
	 * @param PID  The PID of the object that contains the datastream
	 * @param ds   The datastream whose location is to be processed
	 * @param transContext  Integer value indicating the serialization or
	 *             deserialization context.  Valid values are defined as constants
	 *             in fedora.server.storage.translation.DOTranslationUtility:
	 *             0=DOTranslationUtility.DESERIALIZE_INSTANCE
	 *             1=DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC
	 *             2=DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE
	 *             3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
	 *
	 * @return
	 */
	public static Datastream normalizeDSLocationURLs(String PID, Datastream ds, int transContext) {

		if (transContext==DOTranslationUtility.DESERIALIZE_INSTANCE) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
				// MAKE ABSOLUTE REPO URLs
				ds.DSLocation = makeAbsoluteURLs(ds.DSLocation);
			}
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
				// MAKE ABSOLUTE REPO URLs
				ds.DSLocation = makeAbsoluteURLs(ds.DSLocation);
			} else if (ds.DSControlGrp.equals("M")) {
				// MAKE DISSEMINATION URLs
				if (ds.DSCreateDT==null) {
					ds.DSLocation = s_localDissemUrlStart
							+ PID
							+ "/"
							+ ds.DatastreamID;
				} else {
					ds.DSLocation = s_localDissemUrlStart
						+ PID
						+ "/"
						+ ds.DatastreamID
						+ "/"
						+ DateUtility.convertDateToString(ds.DSCreateDT);
				}
			}
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")){
				// MAKE FEDORA LOCAL REPO URLs
				ds.DSLocation=makeFedoraLocalURLs(ds.DSLocation);
			} else if (ds.DSControlGrp.equals("M")) {
				// MAKE DISSEMINATION URLs
				if (ds.DSCreateDT==null) {
					ds.DSLocation = s_localDissemUrlStart
							+ PID
							+ "/"
							+ ds.DatastreamID;
				} else {
					ds.DSLocation = s_localDissemUrlStart
						+ PID
						+ "/"
						+ ds.DatastreamID
						+ "/"
						+ DateUtility.convertDateToString(ds.DSCreateDT);
				}
			}
		} else if (transContext==DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL) {
			//String relativeLoc=ds.DSLocation;
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
				// MAKE FEDORA LOCAL REPO URLs
				ds.DSLocation = makeFedoraLocalURLs(ds.DSLocation);
			} else if (ds.DSControlGrp.equals("M")) {
				// MAKE INTERNAL IDENTIFIERS (PID+DSID+DSVersionID)
				ds.DSLocation = PID + "+" + ds.DatastreamID + "+" + ds.DSVersionID;
			}
		}
		
		// In any event, look for the deprecated getItem method of the default disseminator
		// (i.e., "/fedora-system:3/getItem?itemID=") and convert to new API-A-LITE syntax.
		if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")){
			ds.DSLocation = convertGetItemURLs(ds.DSLocation);
		}
		
		return ds;
	}

	/**
	 * Utility method to normalize a chunk of inline XML depending
	 * on the translation context.  This is mainly to deal with certain
	 * inline XML datastreams found in Behavior Mechanism objects that may
	 * contain a service URL that references the host:port of the local
	 * Fedora server.  This method will usually only ever be called to
	 * check WSDL and SERVICE_PROFILE inline XML datastream, but is of
	 * general utility for dealing with any datastreams that may contain
	 * URLs that reference the local Fedora server.  However, it this
	 * method should be used sparingly, and only on inline XML datastreams
	 * where the impact of the conversions is well understood.
	 * @param xml  a chunk of XML that's contents of an inline XML datastream
	 * @param transContext  Integer value indicating the serialization or
	 *             deserialization context.  Valid values are defined as constants
	 *             in fedora.server.storage.translation.DOTranslationUtility:
	 *             0=DOTranslationUtility.DESERIALIZE_INSTANCE
	 *             1=DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC
	 *             2=DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE
	 *             3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
	 * @return   the inline XML contents with appropriate conversions.
	 */
	public static String normalizeInlineXML(String xml, int transContext) {
		if (transContext==DOTranslationUtility.DESERIALIZE_INSTANCE) {
			// MAKE ABSOLUTE REPO URLs
			return makeAbsoluteURLs(xml);
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
			// MAKE ABSOLUTE REPO URLs
			return makeAbsoluteURLs(xml);
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE) {
			// MAKE FEDORA LOCAL REPO URLs
			return makeFedoraLocalURLs(xml);
		} else if (transContext==DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL) {
			// MAKE FEDORA LOCAL REPO URLs
			return makeFedoraLocalURLs(xml);
		}
		return xml;
	}

	/**
	 *  Check for null values in attributes and set them to empty string
	 *  so 'null' does not appear in XML attribute values.  This helps in
	 *  XML validation of required attributes.  If 'null' is the attribute
	 *  value then validation would incorrectly consider in a valid non-empty
	 *  value.  Also, we set some other default values here.
	 * @param ds The Datastream object to work on.
	 * @return   The Datastream value with default set.
	 * @throws ObjectIntegrityException
	 */

	public static Datastream setDatastreamDefaults(Datastream ds)
		throws ObjectIntegrityException {

		if ((ds.DSMIME==null || ds.DSMIME.equals(""))
			&& ds.DSControlGrp.equalsIgnoreCase("X")) {
				ds.DSMIME="text/xml";
		}
		
		if (ds.DSState==null || ds.DSState.equals("")) {
				ds.DSState="A";
		}
		
		// For METS backward compatibility
		if (ds.DSInfoType==null || ds.DSInfoType.equals("")
				|| ds.DSInfoType.equalsIgnoreCase("OTHER") ) {
			ds.DSInfoType="UNSPECIFIED";
		}

		// LOOK! For METS backward compatibility:
		// If we have a METS MDClass value, preserve MDClass and MDType in a DSFormatURI.
		// Note that the system is taking over the DSFormatURI in this case.
		// Therefore, if a client subsequently modifies the DSFormatURI
		// this METS legacy informatin will be lost, in which case the inline
		// datastream will default to amdSec/techMD in a subsequent METS export.
		if (ds.DSControlGrp.equalsIgnoreCase("X")) {
			if ( ((DatastreamXMLMetadata)ds).DSMDClass !=0 ) {
				String mdClassName = "";
				String mdType=ds.DSInfoType;
				String otherType="";
				if (((DatastreamXMLMetadata)ds).DSMDClass==1) {mdClassName = "techMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==2) {mdClassName = "sourceMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==3) {mdClassName = "rightsMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==4) {mdClassName = "digiprovMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==5) {mdClassName = "descMD";}
				if ( !mdType.equals("MARC") && !mdType.equals("EAD")
						&& !mdType.equals("DC") && !mdType.equals("NISOIMG")
						&& !mdType.equals("LC-AV") && !mdType.equals("VRA")
						&& !mdType.equals("TEIHDR") && !mdType.equals("DDI")
						&& !mdType.equals("FGDC") ) {
					mdType="OTHER";
					otherType=ds.DSInfoType;
				}
				ds.DSFormatURI =
					//"info:fedora/format:xml:mets:"
					"info:fedora/fedora-system:format/xml.mets."
					+ mdClassName + "." + mdType + "." + otherType;
			}
		}
		return ds;
	}

	public static Disseminator setDisseminatorDefaults(Disseminator diss) throws ObjectIntegrityException {

		// Until future when we implement selective versioning,
		// set default to true.
		diss.dissVersionable=true;
		
		if (diss.dissState==null || diss.dissState.equals("")) {
				diss.dissState="A";
		}
		return diss;
	}
}
