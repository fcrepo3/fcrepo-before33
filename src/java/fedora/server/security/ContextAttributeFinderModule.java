package fedora.server.security;

import java.util.Hashtable;
import java.net.URI;
import java.net.URISyntaxException;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import fedora.server.Context;

/*package*/ class ContextAttributeFinderModule extends AttributeFinderModule {
	
	protected boolean canHandleAdhoc() {
		return true;
	}
	
	//protected boolean adhoc() { return true; }
	
	static private final ContextAttributeFinderModule singleton = new ContextAttributeFinderModule();
	
	private final Hashtable contexts = new Hashtable(); 
 
	private ContextAttributeFinderModule() {
		super();
		try {
			registerAttribute(Authorization.ENVIRONMENT_CURRENT_DATETIME_URI_STRING, StringAttribute.identifier);
			registerAttribute(Authorization.ENVIRONMENT_CLIENT_IP_URI_STRING, StringAttribute.identifier);
			attributesDenied.add(Authorization.ACTION_CONTEXT_URI_STRING);
			attributesDenied.add(Authorization.SUBJECT_ID_URI_STRING);
			attributesDenied.add(Authorization.ACTION_ID_URI_STRING);
			attributesDenied.add(Authorization.ACTION_API_URI_STRING);
			registerSupportedDesignatorType(AttributeDesignator.SUBJECT_TARGET);
			registerSupportedDesignatorType(AttributeDesignator.ACTION_TARGET); //<<??????
			registerSupportedDesignatorType(AttributeDesignator.RESOURCE_TARGET); //<<?????
			registerSupportedDesignatorType(AttributeDesignator.ENVIRONMENT_TARGET);
			
			setInstantiatedOk(true);
		} catch (URISyntaxException e1) {
			setInstantiatedOk(false);
		}
	}

	static public final ContextAttributeFinderModule getInstance() {
		return singleton;
	}

	private final String getContextId(EvaluationCtx context) {
		URI contextIdType = null; 
		URI contextIdId = null;
		try {
			contextIdType = new URI(StringAttribute.identifier);
		} catch (URISyntaxException e) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "couldn't make URI for contextId type");
		}
		try {
			contextIdId = new URI(Authorization.ACTION_CONTEXT_URI_STRING);
		} catch (URISyntaxException e) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "couldn't make URI for contextId itself");
		}
		log("ContextAttributeFinder:findAttribute" + " about to call getAttributeFromEvaluationCtx");

		EvaluationResult attribute = context.getActionAttribute(contextIdType, contextIdId, null);
		Object element = getAttributeFromEvaluationCtx(attribute);
		if (element == null) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "can't get contextId on request callback");
			return null;
		}

		if (! (element instanceof StringAttribute)) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "couldn't get contextId from xacml request " + "non-string returned");
			return null;			
		}
 
		String contextId = ((StringAttribute) element).getValue();			
		
		if (contextId == null) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "null contextId");
			return null;			
		}

		if (! validContextId(contextId)) {
			log("ContextAttributeFinder:getContextId" + " exit on " + "invalid context-id");
			return null;			
		}
		
		return contextId;			
	}

	
	private final boolean validContextId(String contextId) {
		if (contextId == null)
			return false;		
		if ("".equals(contextId))
			return false;
		if (" ".equals(contextId))
			return false;
		return true;
	}

	protected final Object getAttributeLocally(int designatorType, String attributeId, URI resourceCategory, EvaluationCtx ctx) {
		log("getAttributeLocally context");
		String contextId = getContextId(ctx);		
		log("contextId=" + contextId);
		Context context = (Context) contexts.get(contextId);
		log("got context");
		Object values = null;			
		log("designatorType" + designatorType);
		switch (designatorType) {
			case AttributeDesignator.SUBJECT_TARGET:
				if (0 > context.nSubjectValues(attributeId)) {
					values = null;
				} else {
					switch(context.nSubjectValues(attributeId)) {
						case 0: 
							values = new String[1];
							((String[])values)[0] = Authorization.UNDEFINED;
							break;
						case 1: 
							values = new String[1];
							((String[])values)[0] = context.getSubjectValue(attributeId); 
							break;
						default:
							values = context.getSubjectValues(attributeId);
					}
				}
				break;
			case AttributeDesignator.ACTION_TARGET:
				if (0 > context.nActionValues(attributeId)) {
					values = null;
				} else {
					switch(context.nActionValues(attributeId)) {
						case 0: 
							values = new String[1];
							((String[])values)[0] = Authorization.UNDEFINED;
							break;
						case 1: 
							values = new String[1];
							((String[])values)[0] = context.getActionValue(attributeId); 
							break;
						default:
							values = context.getActionValues(attributeId);
					}
				} 
				break;
			case AttributeDesignator.RESOURCE_TARGET:
				if (0 > context.nResourceValues(attributeId)) {
					values = null;
				} else {
					switch(context.nResourceValues(attributeId)) {
						case 0: 
							values = new String[1];
							((String[])values)[0] = Authorization.UNDEFINED;
							break;
						case 1: 
							values = new String[1];
							((String[])values)[0] = context.getResourceValue(attributeId); 
							break;
						default:
							values = context.getResourceValues(attributeId);
					}
				}
				break;
			case AttributeDesignator.ENVIRONMENT_TARGET:
				if (0 > context.nEnvironmentValues(attributeId)) {
					values = null;
				} else {
					switch(context.nEnvironmentValues(attributeId)) {
						case 0: 
							values = new String[1];
							((String[])values)[0] = Authorization.UNDEFINED;
							break;
						case 1: 
							values = new String[1];
							((String[])values)[0] = context.getEnvironmentValue(attributeId); 
							break;
						default:
							values = context.getEnvironmentValues(attributeId);
					}
				} 
				break;
			default:
		}
		if (values instanceof String) {
			log("getAttributeLocally string value=" + ((String)values));			
		} else if (values instanceof String[]) {
			log("getAttributeLocally string values=" + values);
			for (int i=0; i<((String[])values).length; i++) {
				log("another string value=" + ((String[])values)[i]);	
			}
		} else {
			log("getAttributeLocally object value=" + values);			
		}
		return values;
	}
	
	/*package*/ final void registerContext(Object key, Context value) {
		log("registering " + key);
		contexts.put(key, value);
	}
	
	/*package*/ final void unregisterContext(Object key) {
		log("unregistering " + key);
		contexts.remove(key);
	}

}

