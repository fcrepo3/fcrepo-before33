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
public abstract class Configuration {

    private List m_parameters;

    protected Configuration(List parameters) {
        m_parameters = parameters;
    }

    public List getParameters() {
        return m_parameters;
    }

    public Parameter getParameter(String name) {
        for (int i = 0; i < m_parameters.size(); i++) {
            Parameter param = (Parameter) m_parameters.get(i);
            if (param.getName().equals(name)) return param;
        }
        return null;
    }

}
