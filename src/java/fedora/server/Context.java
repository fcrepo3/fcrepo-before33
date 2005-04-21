package fedora.server;

import java.util.Date;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> Context.java</p>
 * <p><b>Description:</b> A holder of context name-value pairs.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface Context {

    public abstract String get(String name);

    public abstract Iterator names();
    
    public Iterator environmentAttributes();

    public int nEnvironmentValues(String name);
    
    public String getEnvironmentValue(String name);
    
    public String[] getEnvironmentValues(String name);

    public Iterator subjectAttributes();

    public int nSubjectValues(String name);
    
    public String getSubjectValue(String name);
    
    public String[] getSubjectValues(String name);

    public Iterator actionAttributes();

    public int nActionValues(String name);
    
    public String getActionValue(String name);
    
    public String[] getActionValues(String name);

    public Iterator resourceAttributes();

    public int nResourceValues(String name);
    
    public String getResourceValue(String name);
    
    public String[] getResourceValues(String name);
    
    public void setActionAttributes(MultiValueMap actionAttributes);
    
    public void setResourceAttributes(MultiValueMap resourceAttributes);
    
    public String getPassword();
    
    public String toString();
    
    public Date now();
    
    public boolean getNoOp();
    
}