package fedora.server.utilities;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fedora.server.Logging;
import fedora.server.errors.InconsistentTableSpecException;
import fedora.server.storage.ConnectionPool;

public abstract class SQLUtility {

    public static void replaceInto(Connection conn, String tableName, 
            String[] columns, String[] values, String uniqueColumn) 
            throws SQLException {
        StringBuffer s=new StringBuffer(); // set clause
        s.append("SET ");
        String uVal=null;
        for (int i=0; i<columns.length; i++) {
            if (columns[i].equals(uniqueColumn)) {
                uVal=values[i];
            }
            if (i>0) {
                s.append(", ");
            }
            s.append(columns[i]);
            s.append(" = ");
            if (values[i]==null) {
                s.append("NULL");
            } else {
                s.append("'");
                s.append(values[i]);
                s.append("'");
            }
        }
        StringBuffer w=new StringBuffer(); // where clause
        w.append("WHERE ");
        w.append(uniqueColumn);
        w.append(" = '");
        w.append(uVal);
        w.append("'");
        StringBuffer u=new StringBuffer(); // update statement
        u.append("UPDATE ");
        u.append(tableName);
        u.append("\n");
        u.append(s.toString());
        u.append("\n");
        u.append(w.toString());
        Statement st=null;
        try {
            st=conn.createStatement();
System.out.println("SQLUtility.executeUpdate, trying: " + u.toString());
            if (st.executeUpdate(u.toString())==0) {
                StringBuffer i=new StringBuffer(); // insert statement
                i.append("INSERT INTO ");
                i.append(tableName);
                i.append("\n");
                i.append("(");
                for (int x=0; x<columns.length; x++) {
                    if (x>0) {
                        i.append(", ");
                    }
                    i.append(columns[x]);
                }
                i.append(") VALUES (");
                for (int x=0; x<values.length; x++) {
                    if (x>0) {
                        i.append(", ");
                    }
                    i.append("'");
                    i.append(values[x]);
                    i.append("'");
                }
                i.append(")");
System.out.println("SQLUtility.executeUpdate, now trying: " + i.toString());
                st.executeUpdate(i.toString());
            }
        } catch (SQLException sqle) {
			throw sqle;
        } finally {
            if (st!=null) {
                try {
                    st.close();
                } catch (SQLException sqle) { }
            }
        }
    }
    
    public static void createNonExistingTables(ConnectionPool cPool,
            InputStream dbSpec, Logging log) 
            throws IOException, InconsistentTableSpecException, SQLException {
        List nonExisting=null;
        Connection conn=null;
        try {
            conn=cPool.getConnection();
            nonExisting=SQLUtility.getNonExistingTables(conn,
                    TableSpec.getTableSpecs(dbSpec));
        } finally {
            if (conn!=null) {
                cPool.free(conn);
            }
        }
        if (nonExisting.size()>0) {
            TableCreatingConnection tcConn=null;
            try {
                tcConn=cPool.getTableCreatingConnection();
                if (tcConn==null) {
                    throw new SQLException(
                          "Unable to construct CREATE TABLE "
                        + "statement(s) because there is no DDLConverter "
                        + "registered for this connection type.");
                }
                SQLUtility.createTables(tcConn, nonExisting, log);
            } finally {
                if (tcConn!=null) {
                    cPool.free(tcConn);
                }
            }
        }
    }
    
    public static List getNonExistingTables(Connection conn,
            List tSpecs) 
            throws SQLException {
        ArrayList nonExisting=new ArrayList();
        DatabaseMetaData dbMeta=conn.getMetaData();
        Iterator tSpecIter=tSpecs.iterator();
        // Get a list of tables that don't exist, if any
        ResultSet r=dbMeta.getTables(null, null, "%", null);
        HashSet existingTableSet=new HashSet();
        while (r.next()) {
            existingTableSet.add(r.getString("TABLE_NAME").toLowerCase());
        }
        r.close();
        while (tSpecIter.hasNext()) {
            TableSpec spec=(TableSpec) tSpecIter.next();
            if (!existingTableSet.contains(spec.getName().toLowerCase())) {
                nonExisting.add(spec);
            }
        }
        return nonExisting;
    }
    
    public static void createTables(TableCreatingConnection tcConn, List tSpecs,
            Logging log) 
            throws SQLException {
        Iterator nii=tSpecs.iterator();
        while (nii.hasNext()) {
            TableSpec spec=(TableSpec) nii.next();
            if (log.loggingConfig()) {
                StringBuffer sqlCmds=new StringBuffer();
                Iterator iter=tcConn.getDDLConverter().getDDL(spec).iterator();
                while (iter.hasNext()) {
                    sqlCmds.append("\n");
                    sqlCmds.append((String) iter.next());
                    sqlCmds.append(";");
                }
                log.logConfig("Attempting to create nonexisting "
                        + "table '" + spec.getName() + "' with command(s): "
                        + sqlCmds.toString());
            }
            tcConn.createTable(spec);
        }
    }
    
}