package fedora.server;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * <p><b>Title:</b> WritableContext.java</p>
 * <p><b>Description:</b> A Context object whose values can be written after
 * instantiation.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class WritableContext
        extends Parameterized implements Context {
	
	private final Date now = new Date();
	
    private MultiValueMap m_environmentAttributes;

    private MultiValueMap m_subjectAttributes;
    
    private MultiValueMap m_actionAttributes;
    
    private MultiValueMap m_resourceAttributes;
    
    private String password;

    /**
     * Creates and initializes the <code>WritableContext</code>.
     * <p></p>
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the context.
     */
    public WritableContext(Map parameters, MultiValueMap environmentAttributes, MultiValueMap subjectAttributes, String password) {
        super(parameters);
        m_environmentAttributes=environmentAttributes;
        if (m_environmentAttributes==null) {
            m_environmentAttributes=new MultiValueMap();
        }
        m_environmentAttributes.lock();
        m_subjectAttributes=subjectAttributes;
        if (m_subjectAttributes==null) {
            m_subjectAttributes=new MultiValueMap();
        }
        this.password = password;
        m_subjectAttributes.lock();        
    }
    
    
    public WritableContext(Map parameters) {
        this(parameters, null, null, "");
    }

    public String get(String name) {
        return getParameter(name);
    }

    public void set(String name, String value) {
        setParameter(name, value);
    }

    public Iterator names() {
        return parameterNames();
    }
    
    public Iterator environmentAttributes() {
        return m_environmentAttributes.names();
    }

    public int nEnvironmentValues(String name) {
        return m_environmentAttributes.length(name);
    }
    
    public String getEnvironmentValue(String name) {
        return m_environmentAttributes.getString(name);
    }
    
    public String[] getEnvironmentValues(String name) {
        return m_environmentAttributes.getStringArray(name);
    }

    public Iterator subjectAttributes() {
        return m_subjectAttributes.names();
    }

    public int nSubjectValues(String name) {
        return m_subjectAttributes.length(name);
    }
    
    public String getSubjectValue(String name) {
        return m_subjectAttributes.getString(name);
    }
    
    public String[] getSubjectValues(String name) {
        return m_subjectAttributes.getStringArray(name);
    }
    
    public void setActionAttributes(MultiValueMap actionAttributes) {
        m_actionAttributes = actionAttributes;
        if (m_actionAttributes == null) {
            m_actionAttributes = new MultiValueMap();
        }
        m_actionAttributes.lock();    	
    }

    public Iterator actionAttributes() {
        return m_actionAttributes.names();
    }

    public int nActionValues(String name) {
        return m_actionAttributes.length(name);
    }
    
    public String getActionValue(String name) {
        return m_actionAttributes.getString(name);
    }
    
    public String[] getActionValues(String name) {
        return m_actionAttributes.getStringArray(name);
    }

    public Iterator resourceAttributes() {
        return m_resourceAttributes.names();
    }

    public void setResourceAttributes(MultiValueMap resourceAttributes) {
        m_resourceAttributes = resourceAttributes;
        if (m_resourceAttributes == null) {
            m_resourceAttributes = new MultiValueMap();
        }
        m_resourceAttributes.lock();    	
    }
    
    public int nResourceValues(String name) {
        return m_resourceAttributes.length(name);
    }
    
    public String getResourceValue(String name) {
        return m_resourceAttributes.getString(name);
    }
    
    public String[] getResourceValues(String name) {
        return m_resourceAttributes.getStringArray(name);
    }
    
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("WRITABLE CONTEXT:\n");
    	buffer.append(m_environmentAttributes);
    	buffer.append(m_subjectAttributes);
    	buffer.append(m_actionAttributes);
    	buffer.append(m_resourceAttributes);
    	buffer.append("(END WRITABLE CONTEXT)\n");
    	return buffer.toString();
    }
    
    public Date now() {
    	return now;
    }
    
    public String getPassword() {
    	return password;
    }


}