package fedora.server.search;

import fedora.server.StdoutLogging;
import fedora.server.errors.ServerException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOReader;
import fedora.server.utilities.DDLConverter;

public class FieldSearchDBImpl
        extends StdoutLogging {
        
    private ConnectionPool m_cPool;

    public FieldSearchDBImpl(ConnectionPool cPool) {
        m_cPool=cPool;
        logFinest("Entering constructor");
        // create required tables if they don't exist yet
        // PID, INT, VARCHAR(255) . . . 
        // Instead of putting this in DefaultDOManager.dbspec,
        // put it in it's own.  Move DefaultDOManager's table
        // creation code to a utility class and then call
        // the utility class from here!
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
        System.out.println("  FieldSearchDBImpl user pass jdbcURL jdbcDriver ddlConverter");
        System.out.println("Example:");
        System.out.println("  FieldSearchDBImpl fedoraAdmin fedoraAdmin jdbc:mckoi//localhost/\\");
        System.out.println("                    com.mckoi.JDBCDriver                          \\");
        System.out.println("                    fedora.server.utilities.MckoiDDLConverter");
    }
    
    public static void main(String[] args) {
        if (args.length!=5) {
            printUsage("Need to give 5 arguments.");
        } else {
            try {
                DDLConverter ddlConverter=(DDLConverter)
                        Class.forName(args[4]).newInstance();
                ConnectionPool cPool=new ConnectionPool(args[3], args[2], 
                        args[0], args[1], 2, 5, true, ddlConverter);
                FieldSearchDBImpl fs=new FieldSearchDBImpl(cPool);
            } catch (Exception e) {
                printUsage(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
    
}