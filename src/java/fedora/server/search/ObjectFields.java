package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnrecognizedFieldException;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.DCFields;

/**
 *
 * <p><b>Title:</b> ObjectFields.java</p>
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
public class ObjectFields
        extends DCFields {

    private String m_pid;
    private String m_label;
    private String m_fType;
    private String m_cModel;
    private String m_state;
    private String m_ownerId;
    private Date m_cDate;
    private Date m_mDate;
    private Date m_dcmDate;
    private ArrayList m_bDefs=new ArrayList();
    private ArrayList m_bMechs=new ArrayList();

    private StringBuffer m_currentContent;
    private boolean[] m_want=new boolean[26];

    public final static int PID=0;
    public final static int LABEL=1;
    public final static int FTYPE=2;
    public final static int CMODEL=3;
    public final static int STATE=4;
    public final static int OWNERID=5;
    public final static int CDATE=6;
    public final static int MDATE=7;
    public final static int TITLE=8;
    public final static int CREATOR=9;
    public final static int SUBJECT=10;
    public final static int DESCRIPTION=11;
    public final static int PUBLISHER=12;
    public final static int CONTRIBUTOR=13;
    public final static int DATE=14;
    public final static int TYPE=15;
    public final static int FORMAT=16;
    public final static int IDENTIFIER=17;
    public final static int SOURCE=18;
    public final static int LANGUAGE=19;
    public final static int RELATION=20;
    public final static int COVERAGE=21;
    public final static int RIGHTS=22;
    public final static int DCMDATE=23;
    public final static int BDEF=24;
    public final static int BMECH=25;

    public ObjectFields() {
    }

    public ObjectFields(String[] fieldNames)
            throws UnrecognizedFieldException {
        for (int i=0; i<fieldNames.length; i++) {
            String s=fieldNames[i];
            if (s.equalsIgnoreCase("pid")) {
                m_want[PID]=true;
            } else if (s.equalsIgnoreCase("label")) {
                m_want[LABEL]=true;
            } else if (s.equalsIgnoreCase("fType")) {
                m_want[FTYPE]=true;
            } else if (s.equalsIgnoreCase("cModel")) {
                m_want[CMODEL]=true;
            } else if (s.equalsIgnoreCase("state")) {
                m_want[STATE]=true;
            } else if (s.equalsIgnoreCase("ownerId")) {
                m_want[OWNERID]=true;
            } else if (s.equalsIgnoreCase("cDate")) {
                m_want[CDATE]=true;
            } else if (s.equalsIgnoreCase("mDate")) {
                m_want[MDATE]=true;
            } else if (s.equalsIgnoreCase("title")) {
                m_want[TITLE]=true;
            } else if (s.equalsIgnoreCase("creator")) {
                m_want[CREATOR]=true;
            } else if (s.equalsIgnoreCase("subject")) {
                m_want[SUBJECT]=true;
            } else if (s.equalsIgnoreCase("description")) {
                m_want[DESCRIPTION]=true;
            } else if (s.equalsIgnoreCase("publisher")) {
                m_want[PUBLISHER]=true;
            } else if (s.equalsIgnoreCase("contributor")) {
                m_want[CONTRIBUTOR]=true;
            } else if (s.equalsIgnoreCase("date")) {
                m_want[DATE]=true;
            } else if (s.equalsIgnoreCase("type")) {
                m_want[TYPE]=true;
            } else if (s.equalsIgnoreCase("format")) {
                m_want[FORMAT]=true;
            } else if (s.equalsIgnoreCase("identifier")) {
                m_want[IDENTIFIER]=true;
            } else if (s.equalsIgnoreCase("source")) {
                m_want[SOURCE]=true;
            } else if (s.equalsIgnoreCase("language")) {
                m_want[LANGUAGE]=true;
            } else if (s.equalsIgnoreCase("relation")) {
                m_want[RELATION]=true;
            } else if (s.equalsIgnoreCase("coverage")) {
                m_want[COVERAGE]=true;
            } else if (s.equalsIgnoreCase("rights")) {
                m_want[RIGHTS]=true;
            } else if (s.equalsIgnoreCase("dcmDate")) {
                m_want[DCMDATE]=true;
            } else if (s.equalsIgnoreCase("bDef")) {
                m_want[BDEF]=true;
            } else if (s.equalsIgnoreCase("bMech")) {
                m_want[BMECH]=true;
            } else {
                throw new UnrecognizedFieldException("Unrecognized field: '" + s + "'");
            }
        }
    }

    public ObjectFields(String[] fieldNames, InputStream in)
            throws UnrecognizedFieldException, RepositoryConfigurationException,
            ObjectIntegrityException, StreamIOException {
        this(fieldNames);
        SAXParser parser=null;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            parser=spf.newSAXParser();
        } catch (Exception e) {
            throw new RepositoryConfigurationException("Error getting SAX "
                    + "parser for DC metadata: " + e.getClass().getName()
                    + ": " + e.getMessage());
        }
        try {
            parser.parse(in, this);
        } catch (SAXException saxe) {
            throw new ObjectIntegrityException(
                    "Parse error parsing ObjectFields: " + saxe.getMessage());
        } catch (IOException ioe) {
            throw new StreamIOException("Stream error parsing ObjectFields: "
                    + ioe.getMessage());
        }
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) {
        m_currentContent=new StringBuffer();
    }

    public void characters(char[] ch, int start, int length) {
        m_currentContent.append(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) {
        if ( (m_want[PID]) && (localName.equals("pid")) ) {
            setPid(m_currentContent.toString());
        } else if ( (m_want[LABEL]) && (localName.equals("label")) ) {
            setLabel(m_currentContent.toString());
        } else if ( (m_want[FTYPE]) && (localName.equals("fType")) ) {
            setFType(m_currentContent.toString());
        } else if ( (m_want[CMODEL]) && (localName.equals("cModel")) ) {
            setCModel(m_currentContent.toString());
        } else if ( (m_want[STATE]) && (localName.equals("state")) ) {
            setState(m_currentContent.toString());
        } else if ( (m_want[OWNERID]) && (localName.equals("ownerId")) ) {
            setOwnerId(m_currentContent.toString());
        } else if ( (m_want[CDATE]) && (localName.equals("cDate")) ) {
            setCDate(DateUtility.convertStringToDate(m_currentContent.toString()));
        } else if ( (m_want[MDATE]) && (localName.equals("mDate")) ) {
            setMDate(DateUtility.convertStringToDate(m_currentContent.toString()));
        } else if ( (m_want[DCMDATE]) && (localName.equals("dcmDate")) ) {
            setDCMDate(DateUtility.convertStringToDate(m_currentContent.toString()));
        } else if ( (m_want[TITLE]) && (localName.equals("title")) ) {
            titles().add(m_currentContent.toString());
        } else if ( (m_want[CREATOR]) && (localName.equals("creator")) ) {
            creators().add(m_currentContent.toString());
        } else if ( (m_want[SUBJECT]) && (localName.equals("subject")) ) {
            subjects().add(m_currentContent.toString());
        } else if ( (m_want[DESCRIPTION]) && (localName.equals("description")) ) {
            descriptions().add(m_currentContent.toString());
        } else if ( (m_want[PUBLISHER]) && (localName.equals("publisher")) ) {
            publishers().add(m_currentContent.toString());
        } else if ( (m_want[CONTRIBUTOR]) && (localName.equals("contributor")) ) {
            contributors().add(m_currentContent.toString());
        } else if ( (m_want[DATE]) && (localName.equals("date")) ) {
            dates().add(m_currentContent.toString());
        } else if ( (m_want[TYPE]) && (localName.equals("type")) ) {
            types().add(m_currentContent.toString());
        } else if ( (m_want[FORMAT]) && (localName.equals("format")) ) {
            formats().add(m_currentContent.toString());
        } else if ( (m_want[IDENTIFIER]) && (localName.equals("identifier")) ) {
            identifiers().add(m_currentContent.toString());
        } else if ( (m_want[SOURCE]) && (localName.equals("source")) ) {
            sources().add(m_currentContent.toString());
        } else if ( (m_want[LANGUAGE]) && (localName.equals("language")) ) {
            languages().add(m_currentContent.toString());
        } else if ( (m_want[RELATION]) && (localName.equals("relation")) ) {
            relations().add(m_currentContent.toString());
        } else if ( (m_want[COVERAGE]) && (localName.equals("coverage")) ) {
            coverages().add(m_currentContent.toString());
        } else if ( (m_want[RIGHTS]) && (localName.equals("rights")) ) {
            rights().add(m_currentContent.toString());
        } else if ( (m_want[BDEF]) && (localName.equals("bDef")) ) {
            bDefs().add(m_currentContent.toString());
        } else if ( (m_want[BMECH]) && (localName.equals("bMech")) ) {
            bMechs().add(m_currentContent.toString());
        }
    }

    public void setPid(String pid) {
        m_pid=pid;
    }

    public String getPid() {
        return m_pid;
    }

    public void setLabel(String label) {
        m_label=label;
    }

    public String getLabel() {
        return m_label;
    }

    public void setFType(String fType) {
        m_fType=fType;
    }

    public String getFType() {
        return m_fType;
    }

    public void setCModel(String cModel) {
        m_cModel=cModel;
    }

    public String getCModel() {
        return m_cModel;
    }

    public void setState(String state) {
        m_state=state;
    }

    public String getState() {
        return m_state;
    }

    public void setOwnerId(String ownerId) {
        m_ownerId=ownerId;
    }

    public String getOwnerId() {
        return m_ownerId;
    }

    public void setCDate(Date cDate) {
        m_cDate=cDate;
    }

    public Date getCDate() {
        return m_cDate;
    }

    public void setMDate(Date mDate) {
        m_mDate=mDate;
    }

    public Date getMDate() {
        return m_mDate;
    }

    public void setDCMDate(Date dcmDate) {
        m_dcmDate=dcmDate;
    }

    public Date getDCMDate() {
        return m_dcmDate;
    }

    public List bDefs() {
        return m_bDefs;
    }

    public List bMechs() {
        return m_bMechs;
    }

}
