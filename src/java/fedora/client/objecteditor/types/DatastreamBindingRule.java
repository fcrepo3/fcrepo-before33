package fedora.client.objecteditor.types;

import java.io.*;
import java.util.*;

/**
 * A single rule of a behavior mechanism's datastream binding specification.
 *
 * For a certain binding key (the "key" of the rule), a rule specifies a range
 * indicating the number of datastreams to be bound, whether order is important,
 * and the types of datastreams that may be bound.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */
public class DatastreamBindingRule {

    private String m_key;
    private String m_inputLabel;
    private String m_inputInstruction;
    private int m_min;
    private int m_max;
    private boolean m_orderMatters;
    private String[] m_types;
    private boolean m_acceptsAll=false;

    /**
     * Initialize a DatastreamBindingRule with all values.
     *
     * If maximum is -1, that means there is no maximum.
     *
     * If the array of types is null or empty, getTypes() will
     * return a single-valued array with the "any type" pattern.
     */
    public DatastreamBindingRule(String key, 
                                 String inputLabel,
                                 String inputInstruction,
                                 int min,
                                 int max,
                                 boolean orderMatters,
                                 String[] types) {
        m_key=key;
        m_inputLabel=inputLabel;
        m_inputInstruction=inputInstruction;
        m_min=min;
        m_max=max;
        m_orderMatters=orderMatters;
        m_types=types;
        if (m_types==null || m_types.length==0) {
            m_types=new String[] {"*/*"};
        }
        for (int i=0; i<m_types.length; i++) {
            if (m_types[i].equals("*/*") || m_types[i].equals("*")) {
                m_acceptsAll=true;
            }
        }
    }

    /**
     * Does this rule allow the given type?
     */
    public boolean accepts(String type) {
        if (m_acceptsAll) return true;
        String[] parts=type.split("/");
        if (parts.length!=2) return false;
        for (int i=0; i<m_types.length; i++) {
            if (m_types[i].equals(parts[0] + "/*") || m_types[i].equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the key of the rule.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Get the input label.  This may be null.
     */
    public String getInputLabel() {
        return m_inputLabel;
    }

    /**
     * Get the input instruction.  This may be null.
     */
    public String getInputInstruction() {
        return m_inputInstruction;
    }

    /**
     * Get the maximum number of datastreams that can be bound.
     * -1 means there is no maximum.
     */
    public int getMax() {
        return m_max;
    }

    /**
     * Get the minimum number of datastreams that can be bound.
     */
    public int getMin() {
        return m_max;
    }

    /**
     * Does order matter?
     */
    public boolean orderMatters() {
        return m_orderMatters;
    }

    /**
     * Get the type list.  This will always have at least one element.
     */
    public String[] getTypes() {
        return m_types;
    }

}