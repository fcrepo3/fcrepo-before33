package fedora.server.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
    
}