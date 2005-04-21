package fedora.server.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> TableCreatingConnection.java</p>
 * <p><b>Description:</b> A ConnectionWrapper that creates tables on the
 * target database given a TableSpec.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class TableCreatingConnection
        extends ConnectionWrapper {

    private DDLConverter m_converter;

    /**
     * Constructs a TableCreatingConnection.
     *
     * @param wrapped The wrapped connection.
     * @param converter A converter that can translate from a TableSpec to
     *                  DB-specific DDL.
     */
    public TableCreatingConnection(Connection wrapped, DDLConverter converter) {
        super(wrapped);
        m_converter=converter;
    }

    /**
     * Get the DDLConverter this TableCreatingConnection works with.
     *
     * @return The converter.
     */
    public DDLConverter getDDLConverter() {
        return m_converter;
    }

    /**
     * Creates a table in the target database.
     * <p></p>
     * This method may execute more than one update
     * command and it ignores the transaction state of
     * the connection.
     *
     * @param spec A description of the table to be created.
     */
    public void createTable(TableSpec spec)
            throws SQLException {
        Statement s=createStatement();
        Iterator iter=m_converter.getDDL(spec).iterator();
        while (iter.hasNext()) {
            String updateSQL=(String) iter.next();
            s.executeUpdate(updateSQL);
        }
    }

}