package fedora.server.utilities;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.server.errors.InconsistentTableSpecException;

/**
 *
 * <p><b>Title:</b> TableSpecDeserializer.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class TableSpecDeserializer
        extends DefaultHandler {

    private ArrayList m_tableSpecList;
    private InconsistentTableSpecException m_itse;

    private ArrayList m_columnSpecList;
    private String m_table_name;
    private String m_table_primaryKey;
    private String m_table_type;
    private String m_column_name;
    private String m_column_type;
    private boolean m_column_binary;
    private boolean m_column_autoIncrement;
    private String m_column_index;
    private boolean m_column_notNull;
    private boolean m_column_unique;
    private String m_column_default;
    private String m_column_foreignKey_foreignTableName;
    private String m_column_foreignKey_columnName;
    private String m_column_foreignKey_onDeleteAction;

    protected TableSpecDeserializer() {
        m_tableSpecList=new ArrayList();
        m_columnSpecList=new ArrayList();
    }

    public List getTableSpecs() {
        return m_tableSpecList;
    }

    public void assertTableSpecsConsistent()
            throws InconsistentTableSpecException {
        if (m_itse!=null) {
            throw m_itse;
        }
    }

    public void startElement(String uri, String localName, String qName,
            Attributes a) throws SAXException {
        if (localName.equals("table")) {
            m_table_name=a.getValue("name");
            if (m_table_name==null) {
                throw new SAXException("table element must have a name attribute");
            }
            m_table_primaryKey=a.getValue("primaryKey");
            m_table_type=a.getValue("type");
        } else if (localName.equals("column")) {
            m_column_name=a.getValue("name");
            if (m_column_name==null) {
                throw new SAXException("column element must have a name attribute");
            }
            m_column_type=a.getValue("type");
            if (m_column_type==null) {
                throw new SAXException("column element must have a type attribute");
            }
            if (a.getValue("binary")!=null && a.getValue("binary").equalsIgnoreCase("true")) {
                m_column_binary = true;
            }
            m_column_autoIncrement=getBoolean(a, "autoIncrement");
            m_column_index=a.getValue("index");
            m_column_notNull=getBoolean(a, "notNull");
            m_column_unique=getBoolean(a, "unique");
            m_column_default=a.getValue("default");
            String f=a.getValue("foreignKey");
            if (f==null) {
                f=a.getValue("foriegnKey");
            }
            if (f!=null) {
                int dotPos=f.indexOf(".");
                if (dotPos==-1) {
                    throw new SAXException("table.column not given in foreignKey attribute");
                }
                m_column_foreignKey_foreignTableName=f.substring(0,dotPos);
                int spacePos=f.indexOf(" ");
                if (spacePos==-1) {
                    m_column_foreignKey_columnName=f.substring(dotPos+1);
                } else {
                    m_column_foreignKey_columnName=f.substring(dotPos+1, spacePos);
                    m_column_foreignKey_onDeleteAction=f.substring(spacePos+1);
                }
            }
        }
    }

    private boolean getBoolean(Attributes a, String name) {
        String v=a.getValue(name);
        if (v==null) {
            return false;
        }
        if ((v.equalsIgnoreCase("true"))||(v.equalsIgnoreCase("yes"))) {
            return true;
        }
        return false;
    }

    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("table")) {
            try {
                m_tableSpecList.add(new TableSpec(m_table_name,
                        m_columnSpecList, m_table_primaryKey, m_table_type));
            } catch (InconsistentTableSpecException itse) {
                m_itse=itse;
            }
            m_table_name=null;
            m_table_primaryKey=null;
            m_table_type=null;
            m_columnSpecList=new ArrayList();
        } else if (localName.equals("column")) {
            m_columnSpecList.add(new ColumnSpec(m_column_name, m_column_type, m_column_binary,
                    m_column_default, m_column_autoIncrement, m_column_index,
                    m_column_unique, m_column_notNull,
                    m_column_foreignKey_foreignTableName,
                    m_column_foreignKey_columnName,
                    m_column_foreignKey_onDeleteAction));
            m_column_name=null;
            m_column_type=null;
            m_column_binary=false;
            m_column_default=null;
            m_column_autoIncrement=false;
            m_column_index=null;
            m_column_unique=false;
            m_column_notNull=false;
            m_column_foreignKey_foreignTableName=null;
            m_column_foreignKey_columnName=null;
            m_column_foreignKey_onDeleteAction=null;
        }
    }

}