package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;

import org.exist.xmldb.DatabaseImpl;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;

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
        m_coll = DatabaseManager.getCollection("xmldb:exist:///db/fieldsearch");
        if (m_coll == null) {
            logFinest("fieldsearch collection did not exist; creating it");
            Collection root = DatabaseManager.getCollection("xmldb:exist:///db");
            CollectionManagementService mgtService = 
                    (CollectionManagementService)
                    root.getService("CollectionManagementService", "1.0");
            m_coll=mgtService.createCollection("fieldsearch");
        }
        logFinest("Exiting constructor");
    }

    public void update(DOReader reader) 
            throws ServerException {
        logFinest("Entering update(DOReader)");
        logFinest("Exiting update(DOReader)");
    }
    
    public void delete(String pid) 
            throws ServerException {
        logFinest("Entering delete(DOReader)");
        logFinest("Exiting delete(DOReader)");
    }

    public Object[][] search(String[] resultFields, String condition, 
            int firstResultIndex, int lastResultIndex) 
            throws ServerException {
        logFinest("Entering search(...)");
        logFinest("Exiting search(...)");
        return new Object[1][1];
    }
    
    public int count(String condition) 
            throws ServerException {
        logFinest("Entering count(...)");
        logFinest("Exiting count(...)");
        return 1;
    }
    
    public static void printUsage(String message) {
        System.out.println("ERROR: " + message);
        System.out.println("Usage:");
        System.out.println("  FieldSearchExistImpl existHomeDir");
    }
    
    public static void main(String[] args) {
        if (args.length!=1) {
            printUsage("Need to give one argument.");
        } else {
            try {
                FieldSearchExistImpl fs=new FieldSearchExistImpl(args[0], null);
            } catch (Exception e) {
                printUsage(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
    
}