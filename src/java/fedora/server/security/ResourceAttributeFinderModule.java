package fedora.server.security;
import java.util.Date;
import java.net.URI;
import java.net.URISyntaxException;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;

import fedora.common.Constants;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;

/*package*/ class ResourceAttributeFinderModule extends AttributeFinderModule {
	
	protected boolean canHandleAdhoc() {
		return false;
	}

	//protected boolean adhoc() { return false; }

	static private final ResourceAttributeFinderModule singleton = new ResourceAttributeFinderModule();
 
	private ResourceAttributeFinderModule() {
		super();
		try {
			registerAttribute(Constants.OBJECT.STATE.uri, StringAttribute.identifier);
			registerAttribute(Constants.DATASTREAM.STATE.uri, StringAttribute.identifier);			
			registerAttribute(Constants.OBJECT.OWNER.uri, StringAttribute.identifier);
			registerAttribute(Constants.OBJECT.CONTENT_MODEL.uri, StringAttribute.identifier);			
			registerSupportedDesignatorType(AttributeDesignator.RESOURCE_TARGET);
			setInstantiatedOk(true);
		} catch (URISyntaxException e1) {
			setInstantiatedOk(false);
		}
	}

	static public final ResourceAttributeFinderModule getInstance() {
		return singleton;
	}

	private DOManager doManager = null;
	
	protected void setDOManager(DOManager doManager) {
		if (this.doManager == null) {
			this.doManager = doManager;
		}
	}
	
	private final String getResourceId(EvaluationCtx context) {
		URI resourceIdType = null;
		URI resourceIdId = null;
		try {
			resourceIdType = new URI(StringAttribute.identifier);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			resourceIdId = new URI(EvaluationCtx.RESOURCE_ID);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EvaluationResult attribute = context.getResourceAttribute(resourceIdType, resourceIdId, null);

		Object element = getAttributeFromEvaluationCtx(attribute);
		if (element == null) {
			log("ResourceAttributeFinder:findAttribute" + " exit on " + "can't get resource-id on request callback");
			return null;
		}

		if (! (element instanceof StringAttribute)) {
			log("ResourceAttributeFinder:findAttribute" + " exit on " + "couldn't get resource-id from xacml request " + "non-string returned");
			return null;			
		}
 
		String resourceId = ((StringAttribute) element).getValue();			
		
		if (resourceId == null) {
			log("ResourceAttributeFinder:findAttribute" + " exit on " + "null resource-id");
			return null;			
		}

		if (! validResourceId(resourceId)) {
			log("ResourceAttributeFinder:findAttribute" + " exit on " + "invalid resource-id");
			return null;			
		}
		
		return resourceId;			
	}

	private final boolean validResourceId(String resourceId) {
		if (resourceId == null)
			return false;		
		// "" is a valid resource id, for it represents a don't-care condition
		if (" ".equals(resourceId))
			return false;
		return true;
	}
	
	private final String getDatastreamId(EvaluationCtx context) {
		URI datastreamIdUri = null;
		try {
			datastreamIdUri = new URI(Constants.DATASTREAM.ID.uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		EvaluationResult attribute = context.getResourceAttribute(STRING_ATTRIBUTE_URI, datastreamIdUri, null);

		Object element = getAttributeFromEvaluationCtx(attribute);
		if (element == null) {
			log("getDatastreamId: " + " exit on " + "can't get resource-id on request callback");
			return null;
		}

		if (! (element instanceof StringAttribute)) {
			log("getDatastreamId: " + " exit on " + "couldn't get resource-id from xacml request " + "non-string returned");
			return null;			
		}
 
		String datastreamId = ((StringAttribute) element).getValue();			
		
		if (datastreamId == null) {
			log("getDatastreamId: " + " exit on " + "null resource-id");
			return null;			
		}

		if (! validDatastreamId(datastreamId)) {
			log("getDatastreamId: " + " exit on " + "invalid resource-id");
			return null;			
		}
		
		return datastreamId;			
	}

	private final boolean validDatastreamId(String datastreamId) {
		if (datastreamId == null)
			return false;		
		// "" is a valid resource id, for it represents a don't-care condition
		if (" ".equals(datastreamId))
			return false;
		return true;
	}

	
	protected final Object getAttributeLocally(int designatorType, String attributeId, URI resourceCategory, EvaluationCtx context) {
		String resourceId = getResourceId(context);		
		if ("".equals(resourceId)) {
			log("no resourceId");
			return null;
		}
		log("getResourceAttribute, resourceId=" + resourceId);
		DOReader reader = null;
		try {
			log("resourceId="+resourceId);			
			reader = doManager.getReader(ReadOnlyContext.EMPTY, resourceId);
		} catch (ServerException e) {
			log("couldn't get object reader");
			return null;
		}
		String[] values = null;
		if (Constants.MODEL.OBJECT_STATE.uri.equals(attributeId)) {
			try {
				values = new String[1];
				values[0] = reader.GetObjectState();
				log("got " + Constants.MODEL.OBJECT_STATE.uri + "=" + values[0]);
			} catch (ServerException e) {
				log("failed getting " + Constants.MODEL.OBJECT_STATE.uri);
				return null;					
			}
		} else if (Constants.MODEL.OWNER.uri.equals(attributeId)) { 
				try {
					values = new String[1];
					values[0] = reader.getOwnerId();
					log("got " + Constants.MODEL.OWNER.uri + "=" + values[0]);
				} catch (ServerException e) {
					log("failed getting " + Constants.MODEL.OWNER.uri);
					return null;					
				}
		} else if (Constants.MODEL.CONTENT_MODEL.uri.equals(attributeId)) { 
			try {
				values = new String[1];
				values[0] = reader.getContentModelId();
				log("got " + Constants.MODEL.CONTENT_MODEL.uri + "=" + values[0]);
			} catch (ServerException e) {
				log("failed getting " + Constants.MODEL.CONTENT_MODEL.uri);
				return null;					
			}				
		} else if (Constants.MODEL.DATASTREAM_STATE.uri.equals(attributeId)) {
			String datastreamId = getDatastreamId(context);
			if ("".equals(datastreamId)) {
				log("no datastreamId");
				return null;
			}
			log("datastreamId=" + datastreamId);
			Datastream datastream;
			try {
				datastream = reader.GetDatastream(datastreamId, new Date()); //right import (above)?
			} catch (ServerException e) {
				log("couldn't get datastream");
				return null;					
			}
			if (datastream == null) {
				log("got null datastream");
				return null;
			}
			values = new String[1];
			values[0] = datastream.DSState;
		} else {
			log("looking for unknown resource attribute=" + attributeId);			
		}
		return values;
	}
	
}

