package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.exist.storage.BrokerPool;
import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.CollectionManagementService;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.QueryParseException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.DatastreamXMLMetadata;

/**
 * A FieldSearch implementation that uses an eXist XML Database backend, v0.9.
 * <p></p>
 * Note: Although this code has been written to be compliant with the 
 * implementation-neutral XML:DB api (see http://www.xmldb.org/xapi/),
 * it uses an embedded eXist instance and takes advantage of eXist's
 * extension XPath operators and functions (such as near(...)) in order
 * to get better performance.
 * <p></p>
 * More information about eXist can be found at http://exist-db.org/
 *
 * @author cwilper@cs.cornell.edu
 */ 
public class FieldSearchExistImpl
        extends StdoutLogging
        implements FieldSearch {
        
    Collection m_coll;
    XPathQueryService m_queryService;
        
    // logTarget=null if stdout
    public FieldSearchExistImpl(String existHome, Logging logTarget) 
            throws XMLDBException {
        super(logTarget);
        logFinest("Entering constructor");
        logFinest("Initializing driver");
        System.setProperty("exist.home", existHome);
        Database database = (Database) new DatabaseImpl();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);
        logFinest("Getting fieldsearch collection");
        m_coll=DatabaseManager.getCollection("xmldb:exist:///db/fieldsearch");
        if (m_coll == null) {
            logFinest("fieldsearch collection did not exist; creating it");
            Collection root=DatabaseManager.getCollection("xmldb:exist:///db");
            CollectionManagementService mgtService = 
                    (CollectionManagementService)
                    root.getService("CollectionManagementService", "1.0");
            m_coll=mgtService.createCollection("fieldsearch");
        }
        logFiner("The fieldsearch collection has " + m_coll.getResourceCount() 
                + " items.");
        m_queryService=(XPathQueryService) 
                m_coll.getService("XPathQueryService", "1.0");
        m_queryService.setProperty("pretty", "true");
        m_queryService.setProperty("encoding", "ISO-8859-1");
        logFinest("Exiting constructor");
    }
    
    public void shutdown() {
        logFinest("Entering shutdown");
        BrokerPool.stopAll();
        logFinest("Exiting shutdown");
    }

    public void update(DOReader reader) 
            throws ServerException {
        logFinest("Entering update(DOReader)");
        String pid=reader.GetObjectPID();
        try {
            XMLResource resource=(XMLResource) m_coll.getResource(pid);
            if (resource==null) {
                logFiner("Object " + pid + " not in XML db yet, will write content for the first time.");
                resource=(XMLResource) m_coll.createResource(pid, "XMLResource");
            } else {
                logFiner("Object " + pid + " found in XML db, will overwrite content.");
            }
            resource.setContent(getXMLString(reader));
            m_coll.storeResource(resource);
        } catch (XMLDBException xmldbe) {
            throw new StorageDeviceException("Error attempting update of " 
                    + "object with pid '" + pid + "': "
                    + xmldbe.getClass().getName() + ": " + xmldbe.getMessage());
        }
        logFinest("Exiting update(DOReader)");
    }                                       
    
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
        }
        out.append("</fields>");
        logFinest("Writing to XML DB: " + out.toString());
        return out.toString();
    }
    
    public boolean delete(String pid) 
            throws ServerException {
        logFinest("Entering delete(String)");
        try {
            XMLResource resource=(XMLResource) m_coll.getResource(pid);
            if (resource==null) {
                logFinest("Did not find resource with pid '" + pid + "'. Returning false.");
                logFinest("Exiting delete(String)");
                return false;
            }
            logFinest("Found resource with pid '" + pid + "'.  Deleting and returning true.");
            m_coll.removeResource(resource);
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

    // returns -1 if can't parse as date
    private long parseDateAsNum(String str) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            Date d=formatter.parse(str);
            return d.getTime();
        } catch (ParseException pe) {
            return -1;
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
                                    + "be given in yyyy-MM-dd hh:mm:ss format.");
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
        for (long i=0; i<resources.getSize(); i++) {
            ObjectFields f=new ObjectFields(fields);
            XMLResource res=(XMLResource) resources.getResource(i);
            res.getContentAsSAX(f);
            ret.add(f);
        }
        logFinest("Exiting getObjectFields(ResourceSet, String[])");
        return ret;
    }

}