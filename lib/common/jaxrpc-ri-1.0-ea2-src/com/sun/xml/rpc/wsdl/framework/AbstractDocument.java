// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AbstractDocument.java

package com.sun.xml.rpc.wsdl.framework;

import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Entity, DuplicateEntityException, NoSuchEntityException, GloballyKnown, 
//            Identifiable, EntityAction, Kind, Defining, 
//            EntityReferenceValidator

public abstract class AbstractDocument {

    private Map _kinds;
    private Map _identifiables;
    private String _systemId;
    private Set _importedDocuments;
    private List _importedEntities;

    protected AbstractDocument() {
        _kinds = new HashMap();
        _identifiables = new HashMap();
        _importedEntities = new ArrayList();
        _importedDocuments = new HashSet();
    }

    public String getSystemId() {
        return _systemId;
    }

    public void setSystemId(String s) {
        if(_systemId != null && !_systemId.equals(s))
            throw new IllegalArgumentException();
        _systemId = s;
        if(s != null)
            _importedDocuments.add(s);
    }

    public void addImportedDocument(String systemId) {
        _importedDocuments.add(systemId);
    }

    public boolean isImportedDocument(String systemId) {
        return _importedDocuments.contains(systemId);
    }

    public void addImportedEntity(Entity entity) {
        _importedEntities.add(entity);
    }

    public void withAllSubEntitiesDo(EntityAction action) {
        if(getRoot() != null)
            action.perform(getRoot());
        for(Iterator iter = _importedEntities.iterator(); iter.hasNext(); action.perform((Entity)iter.next()));
    }

    public Map getMap(Kind k) {
        Map m = (Map)_kinds.get(k.getName());
        if(m == null) {
            m = new HashMap();
            _kinds.put(k.getName(), m);
        }
        return m;
    }

    public void define(GloballyKnown e) {
        Map map = getMap(e.getKind());
        if(e.getName() == null)
            return;
        QName name = new QName(e.getDefining().getTargetNamespaceURI(), e.getName());
        if(map.containsKey(name)) {
            throw new DuplicateEntityException(e);
        } else {
            map.put(name, e);
            return;
        }
    }

    public void undefine(GloballyKnown e) {
        Map map = getMap(e.getKind());
        if(e.getName() == null)
            return;
        QName name = new QName(e.getDefining().getTargetNamespaceURI(), e.getName());
        if(map.containsKey(name)) {
            throw new NoSuchEntityException(name);
        } else {
            map.remove(name);
            return;
        }
    }

    public GloballyKnown find(Kind k, QName name) {
        Map map = getMap(k);
        Object result = map.get(name);
        if(result == null)
            throw new NoSuchEntityException(name);
        else
            return (GloballyKnown)result;
    }

    public void defineID(Identifiable e) {
        String id = e.getID();
        if(id == null)
            return;
        if(_identifiables.containsKey(id)) {
            throw new DuplicateEntityException(e);
        } else {
            _identifiables.put(id, e);
            return;
        }
    }

    public void undefineID(Identifiable e) {
        String id = e.getID();
        if(id == null)
            return;
        if(_identifiables.containsKey(id)) {
            throw new NoSuchEntityException(id);
        } else {
            _identifiables.remove(id);
            return;
        }
    }

    public Identifiable findByID(String id) {
        Object result = _identifiables.get(id);
        if(result == null)
            throw new NoSuchEntityException(id);
        else
            return (Identifiable)result;
    }

    public Set collectAllQNames() {
        Set result = new HashSet();
        EntityAction action = new AbstractDocument$1(this, result);
        withAllSubEntitiesDo(action);
        return result;
    }

    public Set collectAllNamespaces() {
        Set result = new HashSet();
        EntityAction action = new AbstractDocument$3(this, result);
        withAllSubEntitiesDo(action);
        return result;
    }

    public void validateLocally() {
        AbstractDocument$LocallyValidatingAction action = new AbstractDocument$LocallyValidatingAction(this);
        withAllSubEntitiesDo(action);
        if(action.getException() != null)
            throw action.getException();
        else
            return;
    }

    public void validate() {
        validate(null);
    }

    public abstract void validate(EntityReferenceValidator entityreferencevalidator);

    protected abstract Entity getRoot();
}
