package fedora.server.security;

import java.util.Date;
import fedora.server.Context;
import fedora.server.errors.NotAuthorizedException;

public interface Authorization {
	

	//subject
	public static final String SUBJECT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:subject";
	public static final String SUBJECT_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
                                     
	//action	
	public static final String ACTION_CATEGORY = "urn:oasis:names:tc:xacml:1.0:action";
	public static final String ACTION_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:action-category:access-action";

	//resource
	public static final String RESOURCE_CATEGORY = "urn:oasis:names:tc:xacml:1.0:resource";
	public static final String RESOURCE_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:resource-category:access-resource";
	
	//environment
	public static final String ENVIRONMENT_CATEGORY = "urn:oasis:names:tc:xacml:1.0:environment";
	public static final String ENVIRONMENT_CATEGORY_ACCESS = "urn:oasis:names:tc:xacml:1.0:environment-category:access-environment";

	public void enforceAddDatastream(Context context, String pid, String dsId, String[] altIDs, 
			String MIMEType, String formatURI, String dsLocation, String controlGroup, String dsState) 
	throws NotAuthorizedException;
	
	public void enforceAddDisseminator(Context context, String pid, String bDefPid, String bMechPid, String dissState) 
	throws NotAuthorizedException;
	
	public void enforceExportObject(Context context, String pid, String format, String exportContext, String exportEncoding) 
	throws NotAuthorizedException;

	public void enforceGetDatastream(Context context, String pid, String datastreamId, Date asOfDateTime) //x 
	throws NotAuthorizedException; 

	public void enforceGetDatastreamHistory(Context context, String pid, String datastreamId) 
	throws NotAuthorizedException;

	public void enforceGetDatastreams(Context context, String pid, Date asOfDate, String state) 
	throws NotAuthorizedException;
	
	public void enforceGetDisseminator(Context context, String pid, String disseminatorId, Date asOfDateTime) 
	throws NotAuthorizedException;
	
	public void enforceGetDisseminators(Context context, String pid, Date asOfDate, String disseminatorState) 
	throws NotAuthorizedException;

	public void enforceGetDisseminatorHistory(Context context, String pid, String disseminatorPid) 
	throws NotAuthorizedException;
	
	public void enforceGetNextPid(Context context, String namespace, int nNewPids) 
	throws NotAuthorizedException; 
	
	public void enforceGetObjectProperties(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceGetObjectXML(Context context, String pid, String objectXmlEncoding) 
	throws NotAuthorizedException;

	public void enforceIngestObject(Context context, String pid, String format, String ingestEncoding) 
	throws NotAuthorizedException;
	
	public void enforceListObjectInFieldSearchResults(Context context, String pid) 
	throws NotAuthorizedException;
	
	public void enforceListObjectInResourceIndexResults(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceModifyDatastreamByReference(Context context, String pid, String datastreamId, String[] altIDs, 
			String mimeType, String formatURI, String datastreamNewLocation, String datastreamNewState)
	throws NotAuthorizedException;

	public void enforceModifyDatastreamByValue(Context context, String pid, String datastreamId, String[] altIDs, 
			String mimeType, String formatURI, String newDatastreamState)
	throws NotAuthorizedException;
	
	public void enforceModifyDisseminator(Context context, String pid, String disseminatorId, String mechanismPid, String disseminatorState) 
	throws NotAuthorizedException;
	
	public void enforceModifyObject(Context context, String pid, String objectState) 
	throws NotAuthorizedException;
	
	public void enforcePurgeDatastream(Context context, String pid, String datastreamId, Date endDT) 
	throws NotAuthorizedException;

	public void enforcePurgeDisseminator(Context context, String pid, String disseminatorId, Date endDT) //x
	throws NotAuthorizedException;

	public void enforcePurgeObject(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceSetDatastreamState(Context context, String pid, String datastreamId, String datastreamNewState) 
	throws NotAuthorizedException;

	public void enforceSetDisseminatorState(Context context, String pid, String disseminatorId, String disseminatorNewState) 
	throws NotAuthorizedException;

	//APIA

	public void enforceDescribeRepository(Context context) 
	throws NotAuthorizedException;

	public void enforceFindObjects(Context context) 
	throws NotAuthorizedException;
	
	public void enforceRIFindObjects(Context context) 
	throws NotAuthorizedException;

	public void enforceGetDatastreamDissemination(Context context, String pid, String datastreamId, Date asOfDate) 
	throws NotAuthorizedException;
	
	public void enforceGetDissemination(Context context, String pid, String bDefPID, String methodName, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceGetObjectHistory(Context context, String pid) 
	throws NotAuthorizedException;

	public void enforceGetObjectProfile(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceListDatastreams(Context context, String pid, Date asOfDate) 
	throws NotAuthorizedException;

	public void enforceListMethods(Context context, String pid, Date ofAsDate) 
	throws NotAuthorizedException;
	
	public void enforceAdminPing(Context context) 
	throws NotAuthorizedException;
	
	public void enforceServerShutdown(Context context)
	throws NotAuthorizedException;
	
	public void enforceServerStatus(Context context)
	throws NotAuthorizedException;

	public void enforceOAIRespond(Context context)
	throws NotAuthorizedException;	

}



