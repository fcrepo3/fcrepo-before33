package fedora.server.storage.translation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Disseminator;

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
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public abstract class DOTranslationUtility {

	// Date formatter for serializers
	private static SimpleDateFormat date_formatter=
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		//new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		
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
	 * 	SERIALIZE_EXPORT_ABSOLUTE:  Serialize java object to XML appropriate 
	 *  for "public" export (absolute URLs). This gives a "public" export of an
	 *  object in which all relative repository URLs AND internal identifiers
	 *  are converted to public callback URLs.  For External (E) and 
	 *  Redirected (R) datastreams, any URLs that are relative to the 
	 *  local repository are converted to absolute URLs using the currently 
	 *  configured hostname:port of the repository. For Managed Content (M) 
	 *  datastreams, the internal identifiers in dsLocation are converted 
	 *  to default dissemination URLs so they can serve as callbacks
	 *  to the repository to obtain the internally managed content.  Also, 
	 *  selected inline XML datastreams (i.e., WSDL and SERVICE_PROFILE) 
	 *  are searched for relative repository URLs and they are made absolute.
	 */	
	public static final int SERIALIZE_EXPORT_ABSOLUTE=1;
	
	/**
	 * 
	 * 	SERIALIZE_EXPORT_RELATIVE:  Serialize java object to XML 
	 *  appropriate for migrating or moving objects from one repository 
	 *  to another.  For External (E) and Redirected (R)datastreams, 
	 *  any URLs that are relative to the local repository will be expressed 
	 *  with the Fedora local URL syntax (which consists of the string 
	 *  "local.fedora.server" standing in place of the actual "hostname:port").  
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
	public static final int SERIALIZE_EXPORT_RELATIVE=2;
	
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
	
	// RELATIVE REPOSITORY URL Patterns:
	// Patterns for URLs that are based at the local Fedora repository server.
	public static Pattern s_fedoraLocalPattern = Pattern.compile("http://local.fedora.server/");
	public static Pattern s_relativePattern = Pattern.compile("fedora/");
	public static Pattern s_relativeGetPattern = Pattern.compile("fedora/get/");
	public static Pattern s_relativeSearchPattern = Pattern.compile("fedora/search/");
	public static Pattern s_relativeGetPatternAsParm = Pattern.compile("=fedora/get/");
	public static Pattern s_relativeSearchPatternAsParm = Pattern.compile("=fedora/search/");

	// ABSOLUTE REPOSITORY URL Patterns:
	// Patterns of the various ways that the local repository server address may be encoded. 	
    private static Pattern s_localServerUrlStartWithPort; // "http://hostname:port/"
    private static Pattern s_localServerUrlStartWithoutPort; // "http://hostname/"
    private static Pattern s_localhostUrlStartWithPort; // "http://localhost:port/"
    private static Pattern s_localhostUrlStartWithoutPort; // "http://localhost/"
	
	// DEFAULT DISSEMINATION URL Pattern:    
	private static String s_localServerDissemUrlStart; // "http://hostname:port/fedora/get/"

	// The actual host and port of the Fedora repository server		
	private static String s_hostInfo = null;	
    private static boolean m_onPort80=false;
    
    // initialize static class with stuff that's used by all DO Serializerers
    static {   	
		// get the host info in a static var so search/replaces are quicker later
			String fedoraHome=System.getProperty("fedora.home");
			String fedoraServerHost=null;
			String fedoraServerPort=null;
			if (fedoraHome==null || fedoraHome.equals("")) {
				// if fedora.home is undefined or empty, assume we're testing,
				// in which case the host and port will be taken from system
				// properties
				fedoraServerHost=System.getProperty("fedoraServerHost");
				fedoraServerPort=System.getProperty("fedoraServerPort");
			} else {
				try {
					Server s=Server.getInstance(new File(fedoraHome));
					fedoraServerHost=s.getParameter("fedoraServerHost");
					fedoraServerPort=s.getParameter("fedoraServerPort");
					if (fedoraServerPort.equals("80")) {
						m_onPort80=true;
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
			if (!fedoraServerPort.equals("80")) {
				s_hostInfo=s_hostInfo + ":" + fedoraServerPort;
			}
			s_hostInfo=s_hostInfo + "/";
			
			// set the pattern for public dissemination URLs at local server
			s_localServerDissemUrlStart= s_hostInfo + "fedora/get/";

			// set other patterns using the configured host and port
			s_localServerUrlStartWithPort=Pattern.compile("http://"
					+ fedoraServerHost + ":" + fedoraServerPort + "/");
			s_localServerUrlStartWithoutPort=Pattern.compile("http://"
					+ fedoraServerHost + "/");
			s_localhostUrlStartWithoutPort=Pattern.compile("http://localhost/");
			s_localhostUrlStartWithPort=Pattern.compile("http://localhost:" + fedoraServerPort + "/");   	
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
	private static String makeFedoraAbsoluteURLs(String input) {
		String output=input;
		// First, convert any relative Fedora URLs to the special
		// Fedora local URL syntax (i.e., "http://local.fedora.server/...")
		output=convertRelativeToFedoraLocalURLs(output);		
		// Now, make absolute URLs out of all instances of the Fedora local URL syntax ...		
		output=s_fedoraLocalPattern.matcher(output).replaceAll(s_hostInfo);
		System.out.println("makeFedoraAbsoluteURLs: AFTER output=" + output);
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
	 * 	 "http://myrepo.com:8080/fedora/get/demo:1/bdef:1/getFoo?in="http://myrepo.com:8080/fedora/get/demo:2/DC" 
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
		// First, convert any relative Fedora URLs to the special
		// Fedora local URL syntax ("http://local.fedora.server/...")
		// Note that relative Fedora URLs may have come in via ingest 
		// or via API-M methods (addDatastream or modifyDatastream).
		output=convertRelativeToFedoraLocalURLs(output);
		// Then, detect absolute URLs that reference the local 
		// repository and convert them to the Fedora local URL syntax	
		output=s_localServerUrlStartWithPort.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());			
		output=s_localhostUrlStartWithPort.matcher(output).replaceAll(
			s_fedoraLocalPattern.pattern());			
		if (m_onPort80) {
			output=s_localServerUrlStartWithoutPort.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());				
			output=s_localhostUrlStartWithoutPort.matcher(output).replaceAll(
				s_fedoraLocalPattern.pattern());
		}
		System.out.println("makeFedoraLocalURLs: AFTER output=" + output);
		return output;		
	}
		
	/**
	 * Detect a relative repository URL by searching for the patterns 
	 * "/fedora/get/" or "/fedora/search/" in the input and convert 
	 * instances of these patterns to the Fedora local URL syntax 
	 * (e.g., "http://local.fedora.server/fedora/get/..."). This enables 
	 * Fedora clients to use a true relative URL syntax in ingest files or on
	 * API-M requests when encoding relative repository URLs, but it lets 
	 * the Fedora system manage and store relative URLs with a precise syntax
	 * that can always be depended on.  Note that relative repository
	 * URLs can occur on External (E) and Redirected (R) datastreams. 
	 * They represent the case where the datastream location is either a
	 * dissemination of a Fedora object in the local repository, or a
	 * Fedora API-A-LITE request.  Examples:
	 * 
	 *   "fedora/get/demo:1/DS1" 
	 *        is converted to 
	 *        "http://local.fedora.server/fedora/get/demo:1/DS1"
	 * 
	 *   "fedora/get/demo:1/bdef:1/getFoo?in=fedora/get/demo:2/DC" 
	 *        is converted to
	 * 	      "http://local.fedora.server/fedora/get/demo:1/bdef:1/getFoo?in=http://local.fedora.server/fedora/get/demo:2/DC"
	 * 
	 * @param input
	 * @return  String with relative repository URLs converted to Fedora local URLs
	 */
	private static String convertRelativeToFedoraLocalURLs(String input) {
		// LOOK!  is there any risk in replacing these patterns
		// in arbitrary places in a URL or inline XML? Is there a more 
		// efficient way to do this than how it's implemented here?
		// up this feature?
		String output=input;
		// The string must start with "fedora/" to initiate this conversion!
		// This means that if the input is some arbitrary chunk of xml 
		// (an inline XML datastream), it will not get checked for the
		// relative URL patterns. So, it's really only geared toward looking
		// for the patterns in datastream locations. 
		if (input.startsWith(s_relativePattern.pattern())) {
			// first, replace first occurrance of Fedora patterns when found at the 
			// beginning of the string
			if (input.startsWith(s_relativeGetPattern.pattern())) {
				output=s_relativeGetPattern.matcher(output).replaceFirst(
					s_fedoraLocalPattern.pattern() + s_relativeGetPattern.pattern());				
			} else if (input.startsWith(s_relativeSearchPattern.pattern())){
				output=s_relativeSearchPattern.matcher(output).replaceFirst(
					s_fedoraLocalPattern.pattern() + s_relativeSearchPattern.pattern());				
			}			
			// then, replace any occurances of the relative URL patterns found as
			// parameter values within the URL
			output=s_relativeGetPatternAsParm.matcher(output).replaceAll(
				"=" + s_fedoraLocalPattern.pattern() + s_relativeGetPattern.pattern());
			output=s_relativeSearchPatternAsParm.matcher(output).replaceAll(
				"=" + s_fedoraLocalPattern.pattern() + s_relativeSearchPattern.pattern());	
		}
		System.out.println("convertRelativeToFedoraLocalURLs: AFTER output=" + output);
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
	 *             1=DOTranslationUtility.SERIALIZE_EXPORT_ABSOLUTE
	 *             2=DOTranslationUtility.SERIALIZE_EXPORT_RELATIVE
	 *             3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
	 * 
	 * @return
	 */
	public static Datastream normalizeDSLocationURLs(String PID, Datastream ds, int transContext) {

		System.out.println("normalizeDSLocationURLs: ds.DatastreamID=" + ds.DatastreamID);	
		System.out.println("normalizeDSLocationURLs: ds.DSControlGrp=" + ds.DSControlGrp);
		System.out.println("normalizeDSLocationURLs: BEFORE DSLocation=" + ds.DSLocation);
			
		if (transContext==DOTranslationUtility.DESERIALIZE_INSTANCE) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
				// MAKE ABSOLUTE REPO URLs
				ds.DSLocation = makeFedoraAbsoluteURLs(ds.DSLocation);
			}
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_ABSOLUTE) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
				// MAKE ABSOLUTE REPO URLs
				ds.DSLocation = makeFedoraAbsoluteURLs(ds.DSLocation);
			} else if (ds.DSControlGrp.equals("M")) {
				// MAKE DISSEMINATION URLs
				if (ds.DSCreateDT==null) {
					ds.DSLocation = s_localServerDissemUrlStart 
							+ PID 
							+ "/fedora-system:3/getItem/"
							+ "?itemID=" + ds.DatastreamID;					
				} else {
					ds.DSLocation = s_localServerDissemUrlStart 
						+ PID 
						+ "/fedora-system:3/getItem/"
						+ date_formatter.format(ds.DSCreateDT)
						+ "?itemID=" + ds.DatastreamID;
				}
			}
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_RELATIVE) {
			if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")){
				// MAKE FEDORA LOCAL REPO URLs
				ds.DSLocation=makeFedoraLocalURLs(ds.DSLocation);
			} else if (ds.DSControlGrp.equals("M")) {
				// MAKE DISSEMINATION URLs
				if (ds.DSCreateDT==null) {
					ds.DSLocation = s_localServerDissemUrlStart 
							+ PID 
							+ "/fedora-system:3/getItem/"
							+ "?itemID=" + ds.DatastreamID;					
				} else {
					ds.DSLocation = s_localServerDissemUrlStart 
						+ PID 
						+ "/fedora-system:3/getItem/"
						+ date_formatter.format(ds.DSCreateDT)
						+ "?itemID=" + ds.DatastreamID;
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
		System.out.println("normalizeDSLocationURLs: AFTER DSLocation=" + ds.DSLocation);
		System.out.println("normalizeDSLocationURLs: ==================================");	
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
	 *             1=DOTranslationUtility.SERIALIZE_EXPORT_ABSOLUTE
	 *             2=DOTranslationUtility.SERIALIZE_EXPORT_RELATIVE
	 *             3=DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL
	 * @return   the inline XML contents with appropriate conversions.
	 */	
	public static String normalizeInlineXML(String xml, int transContext) {
		if (transContext==DOTranslationUtility.DESERIALIZE_INSTANCE) {
			// MAKE ABSOLUTE REPO URLs
			return makeFedoraAbsoluteURLs(xml);		
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_ABSOLUTE) {
			// MAKE ABSOLUTE REPO URLs
			return makeFedoraAbsoluteURLs(xml);
		} else if (transContext==DOTranslationUtility.SERIALIZE_EXPORT_RELATIVE) {
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

		// FIXME:  Can we get rid of these checks?  Trace validation code at ingest
		// and API-M to see which of these cases can never happen due to other checks.			
		if (ds.DatastreamID==null) {
			ds.DatastreamID="";
		}
		if (ds.DatastreamURI==null) {
			ds.DatastreamURI="";
		}		
		if (ds.DSMIME==null && ds.DSControlGrp.equalsIgnoreCase("X")) {
			ds.DSMIME="text/xml";
		} else if (ds.DSMIME==null) {
			ds.DSMIME="";
		}
		if (ds.DSFormatURI==null) {
			ds.DSFormatURI="";	
		}
		if (ds.DSVersionable==null || ds.DSVersionable.equals("")) {
			ds.DSVersionable="YES";
		}
		if (ds.DSLabel==null) {
			ds.DSLabel="";
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
					"info:fedora/format:xml:mets:" 
					+ mdClassName + ":" + mdType + ":" + otherType;
			}
		}
		return ds;
	}
	
	public static Disseminator setDisseminatorDefaults(Disseminator diss) throws ObjectIntegrityException {

		// FIXME:  Can we get rid of these checks?  Trace validation code at ingest
		// and API-M to see which of these cases can never happen due to other checks.		
		if (diss.dissID==null) {
			diss.dissID="";
		}
		if (diss.dissVersionID==null) {
			diss.dissVersionID="";
		}
		if (diss.bDefID==null) {
			diss.bDefID="";
		}
		if (diss.bMechID==null) {
			diss.bMechID="";
		}
		if (diss.dissLabel==null) {
			diss.dissLabel="";
		}	
		if (diss.dsBindMapID==null) {
			diss.dsBindMapID="";
		}
		// Until future when we implement selective versioning,
		// set default to YES.
		if (diss.dissVersionable==null || diss.dissVersionable.equals("")) {
			diss.dissVersionable="YES";
		}
		return diss;
	}
}
