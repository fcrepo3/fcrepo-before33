package fedora.server.utilities;

/**
 *
 * <p><b>Title:</b> ColumnSpec.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class ColumnSpec {

    private String m_name;
    private String m_type;
    private String m_defaultValue;
    private boolean m_isAutoIncremented;
    private String m_indexName;
    private boolean m_isUnique;
    private boolean m_isNotNull;
    private String m_foreignTableName;
    private String m_foreignColumnName;
    private String m_onDeleteAction;

    public ColumnSpec(String name, String type, String defaultValue,
            boolean isAutoIncremented, String indexName, boolean isUnique,
            boolean isNotNull, String foreignTableName, String foreignColumnName,
            String onDeleteAction) {
        m_name=name;
        m_type=type;
        m_defaultValue=defaultValue;
        m_isAutoIncremented=isAutoIncremented;
        m_indexName=indexName;
        m_isUnique=isUnique;
        m_isNotNull=isNotNull;
        m_foreignTableName=foreignTableName;
        m_foreignColumnName=foreignColumnName;
        m_onDeleteAction=onDeleteAction;
    }

    public String getName() {
        return m_name;
    }

    public String getType() {
        return m_type;
    }

    public String getForeignTableName() {
        return m_foreignTableName;
    }

    public String getForeignColumnName() {
        return m_foreignColumnName;
    }

    public String getOnDeleteAction() {
        return m_onDeleteAction;
    }

    public boolean isUnique() {
        return m_isUnique;
    }

    public boolean isNotNull() {
        return m_isNotNull;
    }

    public String getIndexName() {
        return m_indexName;
    }

    public boolean isAutoIncremented() {
        return m_isAutoIncremented;
    }

    public String getDefaultValue() {
        return m_defaultValue;
    }

}
