package fedora.server.security;


import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;

import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;

/*package*/ abstract class AttributeFinderModule extends com.sun.xacml.finder.AttributeFinderModule {
	
	private ServletContext servletContext = null;
	
	protected void setServletContext(ServletContext servletContext) {
		if (this.servletContext == null) {
			this.servletContext = servletContext;
		}
	}




	protected AttributeFinderModule() {
		
		URI temp;

		try {
			temp = new URI(StringAttribute.identifier);
		} catch (URISyntaxException e1) {
			temp = null;
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		STRING_ATTRIBUTE_URI = temp;
		

		
		
		
	}
	
	private Boolean instantiatedOk = null;
	protected final void setInstantiatedOk(boolean value) {
		System.err.println("setInstantiatedOk() " + value);
		if (instantiatedOk == null) {
			instantiatedOk = new Boolean(value);
		}
	}
	
	public boolean isDesignatorSupported() {
		System.err.println("isDesignatorSupported() will return " + iAm() + " " + ((instantiatedOk != null) && instantiatedOk.booleanValue()));
		return (instantiatedOk != null) && instantiatedOk.booleanValue();
	}

	
	private final boolean parmsOk(
			URI attributeType,
			URI attributeId,
			//URI issuer,
			//URI resourceCategory,
			//EvaluationCtx context,
			int designatorType) {
		System.err.println("in parmsOk "  + iAm());
		if (! getSupportedDesignatorTypes().contains(new Integer(designatorType))) {
		//if (designatorType != AttributeDesignator.RESOURCE_TARGET) {
			log("AttributeFinder:parmsOk" + iAm() + " exit on " + "target not supported");
			return false;
		}

		if (attributeType == null) {
			log("AttributeFinder:parmsOk" + iAm() + " exit on " + "null attributeType");
			return false;
		}

		if (attributeId == null) {
			log("AttributeFinder:parmsOk" + iAm() + " exit on " + "null attributeId");
			return false;		}

		log("AttributeFinder:parmsOk" + iAm() + " considering " + attributeId.toString());
		showRegisteredAttributes();
		
		if (hasAttribute(attributeId.toString())) {
			if (! (getAttributeType(attributeId.toString()).equals(attributeType.toString()))) {
				log("AttributeFinder:parmsOk" + iAm() + " exit on " + "attributeType incorrect for attributeId");
				return false;
			}
		} else {
			if (! (StringAttribute.identifier).equals(attributeType.toString())) {
				log("AttributeFinder:parmsOk" + iAm() + " exit on " + "attributeType incorrect for attributeId");
				return false;
			}			
		}
		
		System.err.println("exiting parmsOk normally " + iAm());
		return true;
	}
	
	protected String iAm() {
		return this.getClass().getName();
	}
	
	protected final Object getAttributeFromEvaluationCtx(EvaluationResult attribute /*URI type, URI id, URI category, EvaluationCtx context*/) {
		
		if (attribute.indeterminate()) {
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "indeterminate");
			return null;			
		}

		if ((attribute.getStatus() != null) && ! Status.STATUS_OK.equals(attribute.getStatus())) { 
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "bad status");
			return null;
		} // (resourceAttribute.getStatus() == null) == everything is ok

		AttributeValue attributeValue = attribute.getAttributeValue();
		if (! (attributeValue instanceof BagAttribute)) {
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "no bag");
			return null;
		}

		BagAttribute bag = (BagAttribute) attributeValue;
		if (1 != bag.size()) {
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "wrong bag n=" + bag.size());
			return null;
		} 
			
		Iterator it = bag.iterator();
		Object element = it.next();
		
		if (element == null) {
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "null returned");
			return null;
		}
		
		if (it.hasNext()) {
			log("AttributeFinder:getAttributeFromEvaluationCtx" + iAm() + " exit on " + "couldn't get resource attribute from xacml request " + "too many returned");
			log(element.toString());
			while(it.hasNext()) {
				log((it.next()).toString());									
			}
			return null;
		}
		
		log("AttributeFinder:getAttributeFromEvaluationCtx " + iAm() + " returning " + element.toString());
		return element;
	}
	
	/*
	private final String getResourceId(URI resourceCategory, EvaluationCtx context) {
		URI resourceIdType = null;
		URI resourceIdId = null;
		try {
			//type = new URI("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
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
		Object element = getAttributeFromEvaluationCtx(resourceIdType, resourceIdId, resourceCategory, context);
		if (element == null) {
			log("AttributeFinder:getResourceId" + " exit on " + "can't get resource-id on request callback");
			return null;
		}

		if (! (element instanceof StringAttribute)) {
			log("AttributeFinder:getResourceId" + " exit on " + "couldn't get resource-id from xacml request " + "non-string returned");
			return null;			
		}
 
		String resourceId = ((StringAttribute) element).getValue();			
		
		if (resourceId == null) {
			log("AttributeFinder:getResourceId" + " exit on " + "null resource-id");
			return null;			
		}

		if (! validResourceId(resourceId)) {
			log("AttributeFinder:getResourceId" + " exit on " + "invalid resource-id");
			return null;			
		}
		
		return resourceId;			
	}
	*/

	
	protected final HashSet attributesDenied = new HashSet();
	
	private final Hashtable attributeIdUris = new Hashtable();	
	private final Hashtable attributeTypes = new Hashtable();
	private final Hashtable attributeTypeUris = new Hashtable();
	protected final void registerAttribute(String id, String type) throws URISyntaxException {
		System.err.println("registering attribute " + iAm() + " " +  id);
		attributeIdUris.put(id, new URI(id));
		attributeTypeUris.put(id, new URI(type));
		attributeTypes.put(id, type);			
	}

	protected final URI getAttributeIdUri(String id) {
		return (URI) attributeIdUris.get(id);	
	}
	
	protected final boolean hasAttribute(String id) {
		return attributeIdUris.containsKey(id);
	}

	private final void showRegisteredAttributes() {
		Iterator it = attributeIdUris.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			log("showing attribute  = " + iAm() + " "  + key);
		}
	}

	
	protected final String getAttributeType(String id) {
		return (String) attributeTypes.get(id);
	}
	
	protected final URI getAttributeTypeUri(String id) {
		return (URI) attributeTypeUris.get(id);
	}
	
	private static final Set NULLSET = new HashSet();
	private final Set supportedDesignatorTypes = new HashSet();
	protected final void registerSupportedDesignatorType(int designatorType) {
		System.err.println("registerSupportedDesignatorType() "  + iAm());
		supportedDesignatorTypes.add(new Integer(designatorType));
	}
	
	public Set getSupportedDesignatorTypes() {
		if ((instantiatedOk != null) && instantiatedOk.booleanValue()) {
			System.err.println("getSupportedDesignatorTypes() will return "+ iAm() +" set of elements, n=" + supportedDesignatorTypes.size());
			return supportedDesignatorTypes;			
		}
		System.err.println("getSupportedDesignatorTypes() will return "  + iAm() +  "NULLSET");
		return NULLSET;
	}

	protected abstract boolean canHandleAdhoc();
	
	private final boolean willService(URI attributeId) {
		String temp = attributeId.toString();
		if (hasAttribute(temp)) {
			System.err.println("willService() " + iAm() + " accept this known serviced attribute");
			return true;
		}
		if (! canHandleAdhoc()) {
			System.err.println("willService() " + iAm() + " deny any adhoc attribute");
			return false;								
		}
		if (attributesDenied.contains(temp)) {
			System.err.println("willService() " + iAm() + " deny this known adhoc attribute");
			return false;					
		}
		System.err.println("willService() " + iAm() + " allow this unknown adhoc attribute");
		return true;
	}
	
	public EvaluationResult findAttribute(
		URI attributeType,
		URI attributeId,
		URI issuer,
		URI category,
		EvaluationCtx context,
		int designatorType) {
		log("AttributeFinder:findAttribute " + iAm());
		log("attributeType=[" + attributeType + "], attributeId=[" + attributeId + "]" + iAm());

		if (! parmsOk(attributeType, attributeId, designatorType)) {
			log("AttributeFinder:findAttribute" + " exit on " + "parms not ok" + iAm());
			if (attributeType == null) {
				try {
					attributeType = new URI(StringAttribute.identifier);
				} catch (URISyntaxException e) {
					//we tried
				}
			}
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
		}
		
		if (! willService(attributeId)) {
			log("AttributeFinder:willService() " + iAm() + " returns false" + iAm());
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));			
		}

		if (category != null) {
			log("++++++++++ AttributeFinder:findAttribute " + iAm() + " category=" + category.toString());
		}
		log("++++++++++ AttributeFinder:findAttribute " + iAm() + " designatorType="  + designatorType);

		
		log("about to get temp " + iAm());
		Object temp = getAttributeLocally(designatorType, attributeId.toASCIIString(), category, context);
		log(iAm() + " got temp=" + temp);

		if (temp == null) {
			log("AttributeFinder:findAttribute" + " exit on " + "attribute value not found" + iAm());
			return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));			
		}

		Set set = new HashSet();
		if (temp instanceof String) {
			log("AttributeFinder:findAttribute" + " will return a " + "String " + iAm());
			set.add(new StringAttribute((String)temp));			
		} else if (temp instanceof String[]) {
			log("AttributeFinder:findAttribute" + " will return a " + "String[] " + iAm());
			for (int i = 0; i < ((String[])temp).length; i++) {
				set.add(new StringAttribute(((String[])temp)[i]));			
			}
		} 
		return new EvaluationResult(new BagAttribute(attributeType, set));
				
	}
	
	//protected abstract boolean adhoc();
	
	/*
	private final boolean validResourceId(String resourceId) {
		if (resourceId == null)
			return false;		
		if ("".equals(resourceId))
			return false;
		if (" ".equals(resourceId))
			return false;
		return true;
	}
	*/
	
	protected final URI STRING_ATTRIBUTE_URI;
	
	abstract protected Object getAttributeLocally(int designatorType, String attributeId, URI resourceCategory, EvaluationCtx context);
	

	
	protected final void log(String msg) {
		if (servletContext != null) {
			servletContext.log(msg);
		} else {
			System.err.println(msg);			
		}
	}
}

