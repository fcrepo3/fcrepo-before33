package fedora.client.objecteditor.types;

import java.io.*;
import java.util.*;

/**
 * Defines the datastreams that a behavior mechanism requires in
 * order to fulfill its behavior definition ("contract").
 *
 * A behavior mechanism object specifies this.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */
public class DatastreamInputSpec {

    private String m_label;
    private List m_bindingRules;

    /**
     * Initialize a DatastreamInputSpec object with all values.
     */
    public DatastreamInputSpec(String label, 
                               List bindingRules) {
        m_label=label;
        m_bindingRules=bindingRules;
    }

    /**
     * Parse a stream of XML and return the datastream input spec
     * defined therein.
     *
     * The parsing is very relaxed.  The xml may, but needn't be 
     * namespace-qualified, and will only be validated according to the
     * rules implied below in parentheses.
     * <pre>
     * &lt;DSInputSpec label="SPEC_LABEL"&gt;
     *     &lt;DSInput DSMax="MAX_DATASTREAMS" 
     *              DSMin="MIN_DATASTREAMS" 
     *       DSOrdinality="IS_ORDERED" 
     *    wsdlMsgPartName="BINDING_KEY"&gt;
     *         &lt;DSInputLabel&gt;BINDING_LABEL&lt;/DSInputLabel&gt;
     *         &lt;DSMIME&gt;MIME_TYPE_SPACE_SEPARATED_LIST&lt;/DSMIME&gt;
     *         &lt;DSInputInstruction&gt;INSTRUCTIONS&lt;/DSInputInstruction&gt;
     *     &lt;/DSInput&gt;
     * &lt;/DSInputSpec&gt;
     * </pre>
     */
    public static DatastreamInputSpec parse(InputStream xml) 
            throws IOException {
        return new DatastreamInputSpecDeserializer(xml).getResult();
    }

    /**
     * Get a short description of the input specification.  This may be null.
     */
    public String getLabel() {
        return m_label;
    }

    /**
     * Get the spec's list of <code>DatastreamBindingRule</code>s.
     */
    public List bindingRules() {
        return m_bindingRules;
    }

}