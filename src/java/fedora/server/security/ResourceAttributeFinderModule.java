package fedora.server.security;
import java.util.Date;
import java.net.URI;
import java.net.URISyntaxException;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;

/*package*/ class ResourceAttributeFinderModule extends AttributeFinderModule {
	
	/*
	protected String iAm() {
		System.err.println("+++++ iAm would equal " + this.getClass().getName());
		return "ResourceAttributeFinder";
	}
	*/
	
	protected boolean canHandleAdhoc() {
		return false;
	}

	
	static private final ResourceAttributeFinderModule singleton = new ResourceAttributeFinderModule();
 
	private ResourceAttributeFinderModule() {
		super();
		try {
			registerAttribute(Authorization.RESOURCE_OBJECT_STATE_URI_STRING, StringAttribute.identifier);
			registerAttribute(Authorization.RESOURCE_DATASTREAM_STATE_URI_STRING, StringAttribute.identifier);
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
		//URI resourceCategory = null;
		try {
			//type = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
			resourceIdType = new URI(StringAttribute.identifier);
			//resourceCategory = new URI(Authorization.RESOURCE_CATEGORY);
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
		// if ("".equals(resourceId)) return false;
		if (" ".equals(resourceId))
			return false;
		return true;
	}
	
	protected final Object getAttributeLocally(int designatorType, String attributeId, URI resourceCategory, EvaluationCtx context) {
		String resourceId = getResourceId(context);		
		if ("".equals(resourceId)) {
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
		if (Authorization.RESOURCE_OBJECT_STATE_URI_STRING.equals(attributeId)) {
			log("looking for fedora-object-state");
			try {
				values = new String[1];
				values[0] = reader.GetObjectState();
			} catch (ServerException e) {
				log("couldn't get datastream");
				return null;					
			}
			log("got fedora-object-state=" + values);
		} else if (Authorization.RESOURCE_DATASTREAM_STATE_URI_STRING.equals(attributeId)) {
			log("looking for fedora-datastream-state");			
			URI temp = getAttributeIdUri(Authorization.RESOURCE_DATASTREAM_ID_URI_STRING);

			EvaluationResult attribute = context.getResourceAttribute(STRING_ATTRIBUTE_URI, temp, resourceCategory);
			Object element = getAttributeFromEvaluationCtx(attribute);
			if (element == null) {
				log("ResourceAttributeFinder:findAttribute" + " exit on " + "can't get resource-id on request callback");
				return null;
			}
			if (! (element instanceof StringAttribute)) {
				log("ResourceAttributeFinder:findAttribute" + " exit on " + "couldn't get datastream-pid from xacml request " + "non-string returned");
				return null;			
			}
			String datastreamId = ((StringAttribute) element).getValue();
			Datastream datastream;
			try {
				datastream = reader.GetDatastream(datastreamId, new Date()); //right import (above)?
			} catch (ServerException e) {
				log("couldn't get datastream");
				return null;					
			}
			values = new String[1];
			 values[0] = datastream.DSState;
		} else {
			log("looking for unknown resource attribute=" + attributeId);			
		}
		return values;
	}
	
	protected boolean adhoc() { return false; }

}

