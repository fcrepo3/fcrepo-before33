package fedora.server.journal.entry;

import java.util.Date;
import java.util.Iterator;

import fedora.server.Context;
import fedora.server.MultiValueMap;
import fedora.server.RecoveryContext;

/**
 * 
 * <p>
 * <b>Title:</b> JournalEntryContext.java
 * </p>
 * <p>
 * <b>Description:</b> A fully writable context that can be used when
 * recovering entries from a Journal.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class JournalEntryContext implements RecoveryContext {
    private MultiValueMap environmentAttributes = new MultiValueMap();
    private MultiValueMap subjectAttributes = new MultiValueMap();
    private MultiValueMap actionAttributes = new MultiValueMap();
    private MultiValueMap resourceAttributes = new MultiValueMap();
    private MultiValueMap recoveryAttributes = new MultiValueMap();
    private String password = "";
    private boolean noOp = false;
    private Date now = new Date();

    /**
     * Create an empty context.
     */
    public JournalEntryContext() {
        // nothing to do
    }

    /**
     * A "copy constructor" that creates a writable context from one that might
     * be read-only.
     */
    public JournalEntryContext(Context source) {
        this.password = source.getPassword();
        this.noOp = source.getNoOp();
        this.now = source.now();
        for (Iterator keys = source.environmentAttributes(); keys.hasNext();) {
            String key = (String) keys.next();
            storeInMap(this.environmentAttributes, key, source
                    .getEnvironmentValues(key));
        }
        for (Iterator keys = source.subjectAttributes(); keys.hasNext();) {
            String key = (String) keys.next();
            storeInMap(this.subjectAttributes, key, source
                    .getSubjectValues(key));
        }
        for (Iterator keys = source.actionAttributes(); keys.hasNext();) {
            String key = (String) keys.next();
            storeInMap(this.actionAttributes, key, source.getActionValues(key));
        }
        for (Iterator keys = source.resourceAttributes(); keys.hasNext();) {
            String key = (String) keys.next();
            storeInMap(this.resourceAttributes, key, source
                    .getResourceValues(key));
        }
    }

    /**
     * This method covers the totally bogus Exception that is thrown by
     * MultiValueMap.set(), and wraps it in an IllegalArgumentException, which
     * is more appropriate.
     */
    private void storeInMap(MultiValueMap map, String key, String[] values) {
        try {
            map.set(key, values);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public MultiValueMap getEnvironmentAttributes() {
        return environmentAttributes;
    }

    public Iterator environmentAttributes() {
        return environmentAttributes.names();
    }

    public int nEnvironmentValues(String name) {
        return environmentAttributes.length(name);
    }

    public String getEnvironmentValue(String name) {
        return environmentAttributes.getString(name);
    }

    public String[] getEnvironmentValues(String name) {
        return environmentAttributes.getStringArray(name);
    }

    public Iterator subjectAttributes() {
        return subjectAttributes.names();
    }

    public int nSubjectValues(String name) {
        return subjectAttributes.length(name);
    }

    public String getSubjectValue(String name) {
        return subjectAttributes.getString(name);
    }

    public String[] getSubjectValues(String name) {
        return subjectAttributes.getStringArray(name);
    }

    public Iterator actionAttributes() {
        return actionAttributes.names();
    }

    public int nActionValues(String name) {
        return actionAttributes.length(name);
    }

    public String getActionValue(String name) {
        return actionAttributes.getString(name);
    }

    public String[] getActionValues(String name) {
        return actionAttributes.getStringArray(name);
    }

    public Iterator resourceAttributes() {
        return resourceAttributes.names();
    }

    public int nResourceValues(String name) {
        return resourceAttributes.length(name);
    }

    public String getResourceValue(String name) {
        return resourceAttributes.getString(name);
    }

    public String[] getResourceValues(String name) {
        return resourceAttributes.getStringArray(name);
    }

    public void setActionAttributes(MultiValueMap actionAttributes) {
        if (actionAttributes == null) {
            actionAttributes = new MultiValueMap();
        }
        this.actionAttributes = actionAttributes;
    }

    public void setResourceAttributes(MultiValueMap resourceAttributes) {
        if (resourceAttributes == null) {
            resourceAttributes = new MultiValueMap();
        }
        this.resourceAttributes = resourceAttributes;
    }

    public String getPassword() {
        return password;
    }

    public Date now() {
        return now;
    }

    public boolean getNoOp() {
        return noOp;
    }

    public Iterator getRecoveryNames() {
        return recoveryAttributes.names();
    }

    public String getRecoveryValue(String attribute) {
        return recoveryAttributes.getString(attribute);
    }

    public String[] getRecoveryValues(String attribute) {
        return recoveryAttributes.getStringArray(attribute);
    }

    // -------------------------------------------------------------------------
    // Additional methods
    // -------------------------------------------------------------------------

    public String toString() {
        return this.getClass().getName() + "[environmentAttributes="
                + environmentAttributes + ", subjectAttributes="
                + subjectAttributes + ", actionAttributes=" + actionAttributes
                + ", resourceAttributes=" + resourceAttributes
                + ", recoveryAttributes=" + recoveryAttributes + ", password="
                + password + ", noOp=" + noOp + ", date=" + now + "]\n";
    }

    // Make the class fully read/write.
    
    public MultiValueMap getActionAttributes() {
        return actionAttributes;
    }

    public void setEnvironmentAttributes(MultiValueMap environmentAttributes) {
        this.environmentAttributes = environmentAttributes;
    }

    public void setSubjectAttributes(MultiValueMap subjectAttributes) {
        this.subjectAttributes = subjectAttributes;
    }

    public MultiValueMap getSubjectAttributes() {
        return subjectAttributes;
    }

    public MultiValueMap getResourceAttributes() {
        return resourceAttributes;
    }

    public void setRecoveryAttributes(MultiValueMap recoveryAttributes) {
        this.recoveryAttributes = recoveryAttributes;
    }

    public MultiValueMap getRecoveryAttributes() {
        return recoveryAttributes;
    }

    public void setRecoveryValue(String attribute, String value) {
        setRecoveryValues(attribute, new String[] { value });
    }

    public void setRecoveryValues(String attribute, String[] values) {
        storeInMap(this.recoveryAttributes, attribute, values);
    }

    public void setNoOp(boolean noOp) {
        this.noOp = noOp;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNow(Date now) {
        this.now = now;
    }

}
