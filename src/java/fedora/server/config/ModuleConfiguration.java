package fedora.server.config;

import java.util.*;

/**
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
 */
public class ModuleConfiguration 
        extends Configuration {

    private String m_roleName;
    private String m_className;
    private String m_comment;

    public ModuleConfiguration(List parameters,
                               String roleName,
                               String className,
                               String comment) {
        super(parameters);
        m_roleName = roleName;
        m_className = className;
        m_comment = comment;
    }

    public String getRole() {
        return m_roleName;
    }

    public String getClassName() {
        return m_className;
    }

    public String getComment() {
        return m_comment;
    }

}
