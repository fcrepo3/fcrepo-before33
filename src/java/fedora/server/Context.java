package fedora.server;

import java.util.Date;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> Context.java</p>
 * <p><b>Description:</b> A holder of context name-value pairs.</p>
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
    
    public String toString();
    
    public Date now();
    
}