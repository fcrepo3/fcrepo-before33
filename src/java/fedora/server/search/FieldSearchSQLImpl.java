package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;

import fedora.server.Logging;
import fedora.server.ReadOnlyContext;
import fedora.server.StdoutLogging;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.QueryParseException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnrecognizedFieldException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOReader;
import fedora.server.storage.RepositoryReader;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.SQLUtility;

/**
 * A FieldSearch implementation that uses a relational database
 * as a backend.
 *
 * @author cwilper@cs.cornell.edu
 */ 
public class FieldSearchSQLImpl
        extends StdoutLogging
        implements FieldSearch {

    private ConnectionPool m_cPool;
    private RepositoryReader m_repoReader;
    private static long s_maxResults=200;
    private static String[] s_dbColumnNames=new String[] {"pid", "label", 
            "fType", "cModel", "state", "locker", "cDate", "mDate", "dcmDate",
            "dcTitle", "dcCreator", "dcSubject", "dcDescription", "dcPublisher",
            "dcContributor", "dcDate", "dcType", "dcFormat", "dcIdentifier",
            "dcSource", "dcLanguage", "dcRelation", "dcCoverage", "dcRights"};
            
    private static ReadOnlyContext s_nonCachedContext;
    static {
        HashMap h=new HashMap();
        h.put("useCachedObject", "false");
        s_nonCachedContext=new ReadOnlyContext(h);
    }
        
    public FieldSearchSQLImpl(ConnectionPool cPool, RepositoryReader repoReader, 
            Logging logTarget) {
        super(logTarget);
        logFinest("Entering constructor");
        m_cPool=cPool;
        m_repoReader=repoReader;
        logFinest("Exiting constructor");
    }
    
    public void update(DOReader reader) 
            throws ServerException {
        logFinest("Entering update(DOReader)");
        String pid=reader.GetObjectPID();
        Connection conn=null;
        Statement st=null;
        try {
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            conn=m_cPool.getConnection();
            String[] dbRowValues=new String[24];
            dbRowValues[0]=reader.GetObjectPID();
            String v;
            v=reader.GetObjectLabel();
            if (v!=null) v=v.toLowerCase();
            dbRowValues[1]=v;
            dbRowValues[2]=reader.getFedoraObjectType().toLowerCase();
            v=reader.getContentModelId();
            if (v!=null) v=v.toLowerCase();
            dbRowValues[3]=v;
            dbRowValues[4]=reader.GetObjectState().toLowerCase();
            v=reader.getLockingUser();
            if (v!=null) v=v.toLowerCase();
            dbRowValues[5]=v;
            Date date=reader.getCreateDate();
            if (date==null) {  // should never happen, but if it does, don't die
                date=new Date();
            }
            dbRowValues[6]=formatter.format(date);
            date=reader.getLastModDate();
            if (date==null) {  // should never happen, but if it does, don't die
                date=new Date();
            }
            dbRowValues[7]=formatter.format(date);
            DatastreamXMLMetadata dcmd=null;
            try {
                dcmd=(DatastreamXMLMetadata) reader.GetDatastream("DC", null);
            } catch (ClassCastException cce) {
                throw new ObjectIntegrityException("Object " + reader.GetObjectPID() 
                        + " has a DC datastream, but it's not inline XML.");
            }
            if (dcmd==null) {
                logFine("Did not have DC Metadata datastream for this object.");
            } else {
                logFine("Had DC Metadata datastream for this object.");
                InputStream in=dcmd.getContentStream();
                DCFields dc=new DCFields(in);
                dbRowValues[8]=formatter.format(dcmd.DSCreateDT);
                dbRowValues[9]=getDbValue(dc.titles()); 
                dbRowValues[10]=getDbValue(dc.creators()); 
                dbRowValues[11]=getDbValue(dc.subjects()); 
                dbRowValues[12]=getDbValue(dc.descriptions()); 
                dbRowValues[13]=getDbValue(dc.publishers()); 
                dbRowValues[14]=getDbValue(dc.contributors()); 
                dbRowValues[15]=getDbValue(dc.dates()); 
                // get any dc.dates strings that are formed such that they
                // can be treated as a timestamp
                List wellFormedDates=null;
                for (int i=0; i<dc.dates().size(); i++) {
                    if (i==0) {
                        wellFormedDates=new ArrayList();
                    }
                    Date p=parseDate((String) dc.dates().get(i));
                    if (p!=null) {
                        wellFormedDates.add(p);
                    }
                }
                if (wellFormedDates!=null && wellFormedDates.size()>0) {
                    // found at least one... so delete the existing dates
                    // in that table for this pid, then add these.
                    st=conn.createStatement();
                    st.executeUpdate("DELETE FROM dcDates WHERE pid='" + pid 
                            + "'");
                    for (int i=0; i<wellFormedDates.size(); i++) {
                        Date dt=(Date) wellFormedDates.get(i);
                        st.executeUpdate("INSERT INTO dcDates (pid, dcDate) "
                                + "values ('" + pid + "', '" 
                                + formatter.format(dt) + "')");
                    }
                }
                dbRowValues[16]=getDbValue(dc.types()); 
                dbRowValues[17]=getDbValue(dc.formats()); 
                dbRowValues[18]=getDbValue(dc.identifiers()); 
                dbRowValues[19]=getDbValue(dc.sources()); 
                dbRowValues[20]=getDbValue(dc.languages()); 
                dbRowValues[21]=getDbValue(dc.relations()); 
                dbRowValues[22]=getDbValue(dc.coverages()); 
                dbRowValues[23]=getDbValue(dc.rights()); 
            }
            logFine("Formulating SQL and inserting/updating...");
            SQLUtility.replaceInto(conn, "doFields", s_dbColumnNames,
                    dbRowValues, "pid", this);
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error attempting update of " 
                    + "object with pid '" + pid + ": " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                if (st!=null) {
                    try {
                        st.close();
                    } catch (Exception e) { }
                }
                m_cPool.free(conn);
            }
            logFinest("Exiting update(DOReader)");
        }
    }
    
    // delete from doFields where pid=pid, dcDates where pid=pid
    public boolean delete(String pid) 
            throws ServerException {
        logFinest("Entering delete(String)");
        Connection conn=null;
        Statement st=null;
        try {
            conn=m_cPool.getConnection();
            st=conn.createStatement();
            st.executeUpdate("DELETE FROM doFields WHERE pid='" + pid + "'");
            st.executeUpdate("DELETE FROM dcDates WHERE pid='" + pid + "'");
            return true;
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error attempting delete of " 
                    + "object with pid '" + pid + "': " 
                    + sqle.getMessage());
        } finally {
            if (conn!=null) {
                if (st!=null) {
                    try {
                        st.close();
                    } catch (Exception e) { }
                }
                m_cPool.free(conn);
            }
            logFinest("Exiting delete(String)");
        }
    }
    
    public List search(String[] resultFields, String terms) 
            throws StorageDeviceException, QueryParseException, ServerException {
        Connection conn=null;
        try {
            logFinest("Entering search(String[], String)");
            if (terms.indexOf("'")!=-1) {
                throw new QueryParseException("Query cannot contain the ' character.");
            }
            StringBuffer whereClause=new StringBuffer();
            if (!terms.equals("*") && !terms.equals("")) {
                whereClause.append(" WHERE");
                // formulate the where clause if the terms aren't * or ""
                int usedCount=0;
                boolean needsEscape=false;
                for (int i=0; i<s_dbColumnNames.length; i++) {
                    String column=s_dbColumnNames[i];
                    // use only stringish columns in query
                    boolean use=column.indexOf("Date")==-1;
                    if (!use) {
                        if (column.equals("dcDate")) {
                            use=true;
                        }
                    }
                    if (use) {
                        if (usedCount>0) {
                            whereClause.append(" OR");
                        }
                        String qPart=toSql(column, terms);
                        if (qPart.charAt(0)==' ') {
                            needsEscape=true;
                        } else {
                            whereClause.append(" ");
                        }
                        whereClause.append(qPart);
                        usedCount++;
                    }
                }
                if (needsEscape) {
                    whereClause.append(" {escape '/'}");
                }
            }
            logFinest("Doing search using whereClause: '" 
                    + whereClause.toString() + "'");
            conn=m_cPool.getConnection();
            List ret=getObjectFields(conn, "SELECT pid FROM doFields" 
                    + whereClause.toString(), resultFields);
            return ret;
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error attempting word search: \"" 
                    + terms + "\": " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_cPool.free(conn);
            }
            logFinest("Exiting search(String[], String)");
        }
    }

    public List search(String[] resultFields, List conditions) 
            throws ServerException {
        try {
            logFinest("Entering search(String[], List)");
            return null;
        } finally {
            logFinest("Exiting search(String[], List)");
        }
            /*
            StringBuffer queryPart=new StringBuffer();
            for (int i=0; i<conditions.size(); i++) {
                Condition cond=(Condition) conditions.get(i);
                if (i>0) {
                    queryPart.append(" and ");
                }
                queryPart.append(' ');
                String op=cond.getOperator().getSymbol();
                if (cond.getProperty().toLowerCase().endsWith("date")) {
                    if ( (op.startsWith(">")) || (op.startsWith("<")) ) {
                        // num by itself
                        long n=parseDateAsNum(cond.getValue());
                        if (n==-1) { 
                            throw new QueryParseException("Bad date given with "
                                    + "lt, le, gt, or ge operator.  Dates must "
                                    + "be given in yyyy-MM-ddThh:mm:ss[Z] or yyyy-MM-dd format.");
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error attempting advanced search: \"" 
                    + sqle.getMessage());
        }
             */
                                    
    }

    /**
     * Get the string that should be inserted for a dublin core column,
     * given a list of values.  Turn each value to lowercase and separate them 
     * all by space characters.  If the list is empty, return null.
     */
    private static String getDbValue(List dcItem) {
        if (dcItem.size()==0) {
            return null;
        }
        StringBuffer out=new StringBuffer();
        for (int i=0; i<dcItem.size(); i++) {
            String val=(String) dcItem.get(i);
            out.append(" ");
            out.append(val.toLowerCase());
        }
        out.append(" ");
        return out.toString();
    }

    /**
     * Perform the given query for 'pid' using the given connection
     * and return the result as a List of ObjectFields objects
     * with resultFields populated.
     */
    private List getObjectFields(Connection conn, String query,
            String[] resultFields) 
            throws SQLException, UnrecognizedFieldException, 
            ObjectIntegrityException, ServerException {
        Statement st=null;
        try {
            ArrayList fields=new ArrayList();
            st=conn.createStatement();
            ResultSet results=st.executeQuery(query);
            while (results.next()) {
                String pid=results.getString("pid");
                fields.add(getObjectFields(pid, resultFields));
            }
            return fields;
        } finally {
            if (st!=null) {
                try {
                    st.close();
                } catch (Exception e) { }
            }
        }
    }

    /**
     * For the given pid, get a reader on the object from the repository
     * and return an ObjectFields object with resultFields fields populated.
     */
    private ObjectFields getObjectFields(String pid, String[] resultFields) 
            throws UnrecognizedFieldException, ObjectIntegrityException,
            RepositoryConfigurationException, StreamIOException, 
            ServerException {
        DOReader r=m_repoReader.getReader(s_nonCachedContext, pid);
        ObjectFields f;
        // If there's a DC record available, use SAX to parse the most 
        // recent version of it into f.
        DatastreamXMLMetadata dcmd=null;
        try {
            dcmd=(DatastreamXMLMetadata) r.GetDatastream("DC", null);
        } catch (ClassCastException cce) {
            throw new ObjectIntegrityException("Object " + r.GetObjectPID() 
                    + " has a DC datastream, but it's not inline XML.");
        }
        if (dcmd!=null) {
            logFinest("");
            f=new ObjectFields(resultFields, dcmd.getContentStream());
            // add dcmDate if wanted
            for (int i=0; i<resultFields.length; i++) {
                if (resultFields[i].equals("dcmDate")) {
                    f.setDCMDate(dcmd.DSCreateDT);
                }
            }
        } else {
            f=new ObjectFields();
        }
        // add non-dc values from doReader for the others in resultFields[]
        for (int i=0; i<resultFields.length; i++) {
            String n=resultFields[i];
            if (n.equals("pid")) {
                f.setPid(pid);
            }
            if (n.equals("label")) {
                f.setLabel(r.GetObjectLabel());
            }
            if (n.equals("fType")) {
                f.setFType(r.getFedoraObjectType());
            }
            if (n.equals("cModel")) {
                f.setCModel(r.getContentModelId());
            }
            if (n.equals("state")) {
                f.setState(r.GetObjectState());
            }
            if (n.equals("locker")) {
                f.setLocker(r.getLockingUser());
            }
            if (n.equals("cDate")) {
                f.setCDate(r.getCreateDate());
            }
            if (n.equals("mDate")) {
                f.setMDate(r.getLastModDate());
            }
        }
        return f;
    }
    
    /**
     * Attempt to parse the given string of form: yyyy-MM-dd[Thh:mm:ss[Z]] 
     * as a Date.  If the string is not of that form, return null.
     */
    private static Date parseDate(String str) {
        if (str.indexOf("T")!=-1) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(str);
            } catch (ParseException pe) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").parse(str);
                } catch (ParseException pe2) {
                    return null;
                }
            }
        } else {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(str);
            } catch (ParseException pe3) {
                return null;
            }
        }
        
    }
    
    /**
     * Return a condition suitable for a SQL WHERE clause, given a column
     * name and a string with a possible pattern (using * and ? wildcards).
     * If the string has any characters that need to be escaped, it will
     * begin with a space, indicating to the caller that the entire WHERE
     * clause should end with " {escape '/'}".
     */
    public static String toSql(String name, String in) {
        in=in.toLowerCase();
        if (name.startsWith("dc")) {
            StringBuffer newIn=new StringBuffer();
            if (!in.startsWith("*")) {
                newIn.append("* ");
            }
            newIn.append(in);
            if (!in.endsWith("*")) {
                newIn.append(" *");
            }
            in=newIn.toString();
        }
        if (in.indexOf("\\")!=-1) {
            // has one or more escapes, un-escape and translate
            StringBuffer out=new StringBuffer();
            out.append("\'");
            boolean needLike=false;
            boolean needEscape=false;
            boolean lastWasEscape=false;
            for (int i=0; i<in.length(); i++) {
                char c=in.charAt(i);
                if ( (!lastWasEscape) && (c=='\\') ) {
                    lastWasEscape=true;
                } else {
                    char nextChar='!';
                    boolean useNextChar=false;
                    if (!lastWasEscape) {
                        if (c=='?') {
                            out.append('_');
                            needLike=true;
                        } else if (c=='*') {
                            out.append('%');
                            needLike=true;
                        } else {
                            nextChar=c;
                            useNextChar=true;
                        }
                    } else {
                        nextChar=c;
                        useNextChar=true;
                    }
                    if (useNextChar) {
                        if (nextChar=='\"') {
                            out.append("\\\"");
                            needEscape=true;
                        } else if (nextChar=='\'') {
                            out.append("\\\'");
                            needEscape=true;
                        } else if (nextChar=='%') {
                            out.append("\\%");
                            needEscape=true;
                        } else if (nextChar=='_') {
                            out.append("\\_");
                            needEscape=true;
                        } else {
                            out.append(nextChar);
                        }
                    }
                    lastWasEscape=false;
                }
            }
            out.append("\'");
            if (needLike) {
                out.insert(0, " LIKE ");
            } else {
                out.insert(0, " = ");
            }
            out.insert(0, name);
            if (needEscape) {
                out.insert(0, ' ');
            }
            return out.toString();
        } else {
            // no escapes, just translate if needed
            StringBuffer out=new StringBuffer();
            out.append("\'");
            boolean needLike=false;
            boolean needEscape=false;
            for (int i=0; i<in.length(); i++) {
                char c=in.charAt(i);
                if (c=='?') {
                    out.append('_');
                    needLike=true;
                } else if (c=='*') {
                    out.append('%');
                    needLike=true;
                } else if (c=='\"') {
                    out.append("\\\"");
                    needEscape=true;
                } else if (c=='\'') {
                    out.append("\\\'");
                    needEscape=true;
                } else if (c=='%') {
                    out.append("\\%");
                    needEscape=true;
                } else if (c=='_') {
                    out.append("\\_");
                    needEscape=true;
                } else {
                    out.append(c);
                }
            }
            out.append("\'");
            if (needLike) {
                out.insert(0, " LIKE ");
            } else {
                out.insert(0, " = ");
            }
            out.insert(0, name);
            if (needEscape) {
                out.insert(0, ' ');
            }
            return out.toString();
        }
    }

}