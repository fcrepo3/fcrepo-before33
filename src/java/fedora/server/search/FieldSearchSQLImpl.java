package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.QueryParseException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.DatastreamXMLMetadata;

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
    private static long s_maxResults=200;
        
    public FieldSearchSQLImpl(ConnectionPool cPool, Logging logTarget) {
        super(logTarget);
        logFinest("Entering constructor");
        logFinest("Exiting constructor");
    }
    
    public void update(DOReader reader) 
            throws ServerException {
        logFinest("Entering update(DOReader)");
        String pid=reader.GetObjectPID();
        Connection conn=null;
        try {
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            conn=m_cPool.getConnection();
            
/*
                   out.append("<pid>" + reader.GetObjectPID() + "</pid>\n");
        String label=reader.GetObjectLabel();
        if (label==null) label="";
        out.append("<label>" + label + "</label>\n");
        out.append("<fType>" + reader.getFedoraObjectType() + "</fType>\n");
        String cModel=reader.getContentModelId();
        if (cModel==null) cModel="";
        out.append("<cModel>" + cModel + "</cModel>\n");
        out.append("<state>" + reader.GetObjectState() + "</state>\n");
        String locker=reader.getLockingUser();
        if (locker==null) locker="";
        out.append("<locker>" + locker + "</locker>\n");
        out.append("<cDate>" + formatter.format(reader.getCreateDate()) + "</cDate>\n");
        out.append("<cDateAsNum>" + reader.getCreateDate().getTime() + "</cDateAsNum>\n");
        out.append("<mDate>" + formatter.format(reader.getLastModDate()) + "</mDate>\n");
        out.append("<mDateAsNum>" + reader.getLastModDate().getTime() + "</mDateAsNum>\n"); 
        DatastreamXMLMetadata dcmd=null;
        try {
            dcmd=(DatastreamXMLMetadata) reader.GetDatastream("DC", null);
        } catch (ClassCastException cce) {
            throw new ObjectIntegrityException("Object " + reader.GetObjectPID() 
                    + " has a DC datastream, but it's not inline XML.");
        }
        if (dcmd!=null) {
            logFine("Had DC Metadata datastream for this object.");
            out.append("<dcmDate>" + formatter.format(dcmd.DSCreateDT) + "</dcmDate>\n");
            out.append("<dcmDateAsNum>" + dcmd.DSCreateDT.getTime() + "</dcmDateAsNum>\n");
            InputStream in=dcmd.getContentStream();
            DCFields dc=new DCFields(in);
            for (int i=0; i<dc.titles().size(); i++) {
                out.append("<title>");
                out.append((String) dc.titles().get(i));
                out.append("</title>\n");
            }
            for (int i=0; i<dc.creators().size(); i++) {
                out.append("<creator>");
                out.append((String) dc.creators().get(i));
                out.append("</creator>\n");
            }
            for (int i=0; i<dc.subjects().size(); i++) {
                out.append("<subject>");
                out.append((String) dc.subjects().get(i));
                out.append("</subject>\n");
            }
            for (int i=0; i<dc.descriptions().size(); i++) {
                out.append("<description>");
                out.append((String) dc.descriptions().get(i));
                out.append("</description>\n");
            }
            for (int i=0; i<dc.publishers().size(); i++) {
                out.append("<publisher>");
                out.append((String) dc.publishers().get(i));
                out.append("</publisher>\n");
            }
            for (int i=0; i<dc.contributors().size(); i++) {
                out.append("<contributor>");
                out.append((String) dc.contributors().get(i));
                out.append("</contributor>\n");
            }
            for (int i=0; i<dc.dates().size(); i++) {
                String dateString=(String) dc.dates().get(i);
                out.append("<date>");
                out.append(dateString);
                out.append("</date>\n");
                long dateNum=parseDateAsNum(dateString);
                if (dateNum!=-1) {
                    out.append("<dateAsNum>");
                    out.append(dateNum);
                    out.append("</dateAsNum>");
                }
            }
            for (int i=0; i<dc.types().size(); i++) {
                out.append("<type>");
                out.append((String) dc.types().get(i));
                out.append("</type>\n");
            }
            for (int i=0; i<dc.formats().size(); i++) {
                out.append("<format>");
                out.append((String) dc.formats().get(i));
                out.append("</format>\n");
            }
            for (int i=0; i<dc.identifiers().size(); i++) {
                out.append("<identifier>");
                out.append((String) dc.identifiers().get(i));
                out.append("</identifier>\n");
            }
            for (int i=0; i<dc.sources().size(); i++) {
                out.append("<source>");
                out.append((String) dc.sources().get(i));
                out.append("</source>\n");
            }
            for (int i=0; i<dc.languages().size(); i++) {
                out.append("<language>");
                out.append((String) dc.languages().get(i));
                out.append("</language>\n");
            }
            for (int i=0; i<dc.relations().size(); i++) {
                out.append("<relation>");
                out.append((String) dc.relations().get(i));
                out.append("</relation>\n");
            }
            for (int i=0; i<dc.coverages().size(); i++) {
                out.append("<coverage>");
                out.append((String) dc.coverages().get(i));
                out.append("</coverage>\n");
            }
            for (int i=0; i<dc.rights().size(); i++) {
                out.append("<rights>");
                out.append((String) dc.rights().get(i));
                out.append("</rights>\n");
            }
        } else {
            logFine("Did not have DC Metadata datastream for this object.");
        }
        out.append("</fields>");
        logFinest("Writing to XML DB: " + out.toString());
        return out.toString(); */
        } catch (SQLException sqle) {
            throw new StorageDeviceException("Error attempting update of " 
                    + "object with pid '" + pid + ": " + sqle.getMessage());
        } finally {
            if (conn!=null) {
                m_cPool.free(conn);
            }
            logFinest("Exiting update(DOReader)");
        }
    }                                       
    
    public boolean delete(String pid) 
            throws ServerException {
        logFinest("Entering delete(String)");
        try {
            // delete from doFields where pid=pid
            return true;
//        } catch (SQLException sqle) {
//            throw new StorageDeviceException("Error attempting delete of " 
//                    + "object with pid '" + pid + "': " 
//                    + sqle.getMessage());
        } finally {
            logFinest("Exiting delete(String)");
        }
    }
    
    public List search(String[] resultFields, String terms) 
            throws StorageDeviceException, QueryParseException, ServerException {
        try {
            logFinest("Entering search(String, String)");
            if (terms.indexOf("'")!=-1) {
                throw new QueryParseException("Query cannot contain the ' character.");
            }
            String whereClause="";
            if (!terms.equals("*") && !terms.equals("")) {
                whereClause=" WHERE ";
                // formulate the rest...
            }
            logFinest("Doing search using whereClause: '" + terms + "'");
            ResultSet results=null;
            logFinest("Finished search, getting result.");
            List ret=getObjectFields(results, resultFields);
            return ret;
//        } catch (SQLException sqle) {
//            throw new StorageDeviceException("Error attempting word search: \"" 
//                    + terms + "\": " + sqle.getMessage());
        } finally {
            logFinest("Exiting search(String, String)");
        }
    }

    public List search(String[] resultFields, List conditions) 
            throws ServerException {
        try {
            logFinest("Entering search(String, List)");
            return null;
        } finally {
            logFinest("Exiting search(String, List)");
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
    
        

    
    private List getObjectFields(ResultSet results, String[] resultFields) {
        return null;
    }
    
    // returns -1 if can't parse as date
    private long parseDateAsNum(String str) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse(str).getTime();
        } catch (ParseException pe) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").parse(str).getTime();
            } catch (ParseException pe2) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(str).getTime();
                } catch (ParseException pe3) {
                    return -1;
                }
            }
        }
    }
    
/*
    
    private String getXMLString(DOReader reader) 
            throws ServerException {
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        out.append("<fields>\n");
        out.append("<pid>" + reader.GetObjectPID() + "</pid>\n");
        String label=reader.GetObjectLabel();
        if (label==null) label="";
        out.append("<label>" + label + "</label>\n");
        out.append("<fType>" + reader.getFedoraObjectType() + "</fType>\n");
        String cModel=reader.getContentModelId();
        if (cModel==null) cModel="";
        out.append("<cModel>" + cModel + "</cModel>\n");
        out.append("<state>" + reader.GetObjectState() + "</state>\n");
        String locker=reader.getLockingUser();
        if (locker==null) locker="";
        out.append("<locker>" + locker + "</locker>\n");
        out.append("<cDate>" + formatter.format(reader.getCreateDate()) + "</cDate>\n");
        out.append("<cDateAsNum>" + reader.getCreateDate().getTime() + "</cDateAsNum>\n");
        out.append("<mDate>" + formatter.format(reader.getLastModDate()) + "</mDate>\n");
        out.append("<mDateAsNum>" + reader.getLastModDate().getTime() + "</mDateAsNum>\n");
        DatastreamXMLMetadata dcmd=null;
        try {
            dcmd=(DatastreamXMLMetadata) reader.GetDatastream("DC", null);
        } catch (ClassCastException cce) {
            throw new ObjectIntegrityException("Object " + reader.GetObjectPID() 
                    + " has a DC datastream, but it's not inline XML.");
        }
        if (dcmd!=null) {
            logFine("Had DC Metadata datastream for this object.");
            out.append("<dcmDate>" + formatter.format(dcmd.DSCreateDT) + "</dcmDate>\n");
            out.append("<dcmDateAsNum>" + dcmd.DSCreateDT.getTime() + "</dcmDateAsNum>\n");
            InputStream in=dcmd.getContentStream();
            DCFields dc=new DCFields(in);
            for (int i=0; i<dc.titles().size(); i++) {
                out.append("<title>");
                out.append((String) dc.titles().get(i));
                out.append("</title>\n");
            }
            for (int i=0; i<dc.creators().size(); i++) {
                out.append("<creator>");
                out.append((String) dc.creators().get(i));
                out.append("</creator>\n");
            }
            for (int i=0; i<dc.subjects().size(); i++) {
                out.append("<subject>");
                out.append((String) dc.subjects().get(i));
                out.append("</subject>\n");
            }
            for (int i=0; i<dc.descriptions().size(); i++) {
                out.append("<description>");
                out.append((String) dc.descriptions().get(i));
                out.append("</description>\n");
            }
            for (int i=0; i<dc.publishers().size(); i++) {
                out.append("<publisher>");
                out.append((String) dc.publishers().get(i));
                out.append("</publisher>\n");
            }
            for (int i=0; i<dc.contributors().size(); i++) {
                out.append("<contributor>");
                out.append((String) dc.contributors().get(i));
                out.append("</contributor>\n");
            }
            for (int i=0; i<dc.dates().size(); i++) {
                String dateString=(String) dc.dates().get(i);
                out.append("<date>");
                out.append(dateString);
                out.append("</date>\n");
                long dateNum=parseDateAsNum(dateString);
                if (dateNum!=-1) {
                    out.append("<dateAsNum>");
                    out.append(dateNum);
                    out.append("</dateAsNum>");
                }
            }
            for (int i=0; i<dc.types().size(); i++) {
                out.append("<type>");
                out.append((String) dc.types().get(i));
                out.append("</type>\n");
            }
            for (int i=0; i<dc.formats().size(); i++) {
                out.append("<format>");
                out.append((String) dc.formats().get(i));
                out.append("</format>\n");
            }
            for (int i=0; i<dc.identifiers().size(); i++) {
                out.append("<identifier>");
                out.append((String) dc.identifiers().get(i));
                out.append("</identifier>\n");
            }
            for (int i=0; i<dc.sources().size(); i++) {
                out.append("<source>");
                out.append((String) dc.sources().get(i));
                out.append("</source>\n");
            }
            for (int i=0; i<dc.languages().size(); i++) {
                out.append("<language>");
                out.append((String) dc.languages().get(i));
                out.append("</language>\n");
            }
            for (int i=0; i<dc.relations().size(); i++) {
                out.append("<relation>");
                out.append((String) dc.relations().get(i));
                out.append("</relation>\n");
            }
            for (int i=0; i<dc.coverages().size(); i++) {
                out.append("<coverage>");
                out.append((String) dc.coverages().get(i));
                out.append("</coverage>\n");
            }
            for (int i=0; i<dc.rights().size(); i++) {
                out.append("<rights>");
                out.append((String) dc.rights().get(i));
                out.append("</rights>\n");
            }
        } else {
            logFine("Did not have DC Metadata datastream for this object.");
        }
        out.append("</fields>");
        logFinest("Writing to XML DB: " + out.toString());
        return out.toString();
    }
    
    public boolean delete(String pid) 
            throws ServerException {
        logFinest("Entering delete(String)");
        try {
            Collection coll=getCollection(pid);
            XMLResource resource=(XMLResource) coll.getResource(pid);
            if (resource==null) {
                logFinest("Did not find resource with pid '" + pid + "'. Returning false.");
                logFinest("Exiting delete(String)");
                return false;
            }
            logFinest("Found resource with pid '" + pid + "'.  Deleting and returning true.");
            coll.removeResource(resource);
        } catch (XMLDBException xmldbe) {
            throw new StorageDeviceException("Error attempting delete of " 
                    + "object with pid '" + pid + "': "
                    + xmldbe.getClass().getName() + ": " + xmldbe.getMessage());
        }
        logFinest("Exiting delete(String)");
        return true;
    }

    public List search(String[] resultFields, String terms) 
            throws StorageDeviceException, QueryParseException, ServerException {
        try {
            logFinest("Entering search(String, String)");
            if (terms.indexOf("'")!=-1) {
                throw new QueryParseException("Query cannot contain the ' character.");
            }
            logFinest("Doing search using queryPart: . &= '" + terms + "'");
            ResourceSet res=m_queryService.query("document(*)/fields[. &= '" + terms + "']");
            if (res==null) {
                return new ArrayList();
            }
            logFinest("Finished search, getting result.");
            List ret=getObjectFields(res, resultFields);
            logFinest("Exiting search(String, String)");
            return ret;
        } catch (XMLDBException xmldbe) {
            throw new StorageDeviceException("Error attempting search of terms: \"" 
                    + terms + "\": " + xmldbe.getClass().getName() + ": " 
                    + xmldbe.getMessage());
        }
    }

    public List search(String[] resultFields, List conditions) 
            throws ServerException {
        try {
            logFinest("Entering search(String, List)");
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
                        }
                        queryPart.append(cond.getProperty());
                        queryPart.append("AsNum");
                        queryPart.append(' ');
                        queryPart.append(cond.getOperator().getSymbol());
                        queryPart.append(' ');
                        queryPart.append(n);
                    } else {
                        // try AsNum with 'or' on string ... if op is '='... otherwise just as string
                        long n=-1;
                        if (op.equals("=")) {
                            n=parseDateAsNum(cond.getValue());
                        }
                        if (n!=-1) {
                            queryPart.append("(");
                            queryPart.append(cond.getProperty());
                            queryPart.append("AsNum");
                            queryPart.append(' ');
                            queryPart.append(cond.getOperator().getSymbol());
                            queryPart.append(' ');
                            queryPart.append(n);
                            queryPart.append(" or ");
                        }
                        queryPart.append(cond.getProperty());
                        queryPart.append(' ');
                        if (cond.getOperator().getSymbol().equals("~")) {
                            queryPart.append("&");
                        }
                        queryPart.append("= '");
                        queryPart.append(cond.getValue());
                        queryPart.append("'");
                        if (n!=-1) {
                            queryPart.append(")");
                        }
                    }
                } else {
                    queryPart.append(cond.getProperty());
                    if (op.equals("~")) {
                        queryPart.append("&=");
                    } else {
                        queryPart.append(op);
                    }
                    queryPart.append(" '");
                    queryPart.append(cond.getValue());
                    queryPart.append("'");
                }
            }
            logFinest("Doing search using queryPart: " + queryPart.toString());
            ResourceSet res=m_queryService.query("document(*)/fields[" + queryPart.toString() + "]");
            if (res==null) {
                return new ArrayList();
            }
            logFinest("Finished search, getting result.");
            List ret=getObjectFields(res, resultFields);
            logFinest("Exiting search(String, List)");
            return ret;
        } catch (XMLDBException xmldbe) {
            throw new StorageDeviceException("Error attempting advanced search: "
                    + xmldbe.getClass().getName() + ": " + xmldbe.getMessage());
        }
    }
    
    private List getObjectFields(ResourceSet resources, String fields[]) 
            throws XMLDBException, ServerException {
        logFinest("Entering getObjectFields(ResourceSet, String[])");
        ArrayList ret=new ArrayList();
        long numResults=resources.getSize();
        if (s_maxResults<resources.getSize()) {
            numResults=s_maxResults;
        }
        for (long i=0; i<numResults; i++) {
            ObjectFields f=new ObjectFields(fields);
            XMLResource res=(XMLResource) resources.getResource(i);
            res.getContentAsSAX(f);
            ret.add(f);
        }
        logFinest("Exiting getObjectFields(ResourceSet, String[])");
        return ret;
    }
    */

}