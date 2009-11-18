/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package fedora.server.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A DDLConverter for MS SQL Server.
 *
 * @author David Handy
 */
public class MsSQLDDLConverter
        implements DDLConverter {

    public MsSQLDDLConverter() {
    }

    public boolean supportsTableType() {
        return true;
    }

    public String getDropDDL(String command) {
        String[] parts = command.split(" ");
        String tableName = parts[2];
        return "DROP TABLE " + tableName;
    }

    public List<String> getDDL(TableSpec spec) {
        StringBuffer out = new StringBuffer();
        StringBuffer end = new StringBuffer();
        StringBuffer indexes = new StringBuffer();
        out.append("CREATE TABLE " + spec.getName() + " (\n");
        Iterator<ColumnSpec> csi = spec.columnSpecIterator();
        int csNum = 0;
        while (csi.hasNext()) {
            if (csNum > 0) {
                out.append(",\n");
            }
            csNum++;
            ColumnSpec cs = csi.next();
            out.append(" ");
            out.append(cs.getName());
            out.append(' ');
            if (cs.getType().equalsIgnoreCase("varchar")) {
                if (cs.getBinary()) {
                    out.append("BINARY");
                } else {
                    out.append(cs.getType());
                }
            } else {
                out.append(cs.getType());
            }
            if (cs.isNotNull()) {
                out.append(" NOT NULL");
            }
            if (cs.isAutoIncremented()) {
                out.append(" IDENTITY (1, 1)");
            }
            if (cs.getDefaultValue() != null) {
                out.append(" DEFAULT '");
                out.append(cs.getDefaultValue());
                out.append("'");
            }
            if (cs.isUnique()) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append(" CONSTRAINT");
                end.append(cs.getName());
                end.append("_unique UNIQUE KEY NONCLUSTERED (");
                end.append(cs.getName());
                end.append(" ON PRIMARY)");
            }
            if (cs.getIndexName() != null) {
                indexes.append(" CREATE INDEX ");
                indexes.append(cs.getIndexName());
                indexes.append("ON ");
                indexes.append(spec.getName());
                indexes.append(" (");
                indexes.append(cs.getName());
                indexes.append(") ON PRIMARY GO");
            }
            if (cs.getForeignTableName() != null) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append(" CONSTRAINT ");
                end.append(cs.getName());
                end.append("_fk FOREIGN KEY (");
                end.append(cs.getName());
                end.append(") REFERENCES ");
                end.append(cs.getForeignTableName());
                end.append(" (");
                end.append(cs.getForeignColumnName());
                end.append(")");
                if (cs.getOnDeleteAction() != null) {
                    end.append(" ON DELETE ");
                    end.append(cs.getOnDeleteAction());
                }
            }
        }
        if (spec.getPrimaryColumnName() != null) {
            end.append(",\n CONSTRAINT ");
            end.append(spec.getName() + "_fk");
            end.append(" PRIMARY KEY CLUSTERED (");
            end.append(spec.getPrimaryColumnName());
            end.append(")");
        }
        out.append("\n");
        out.append(") ON PRIMARY GO");
        if (!end.toString().equals("")) {
            out.append("\n ALTER TABLE ");
            out.append(spec.getName());
            out.append(" ADD \n");
            out.append(end);
            out.append(" GO ");
        }
        if (!indexes.toString().equals("")) {
            out.append(indexes);
        }
        ArrayList<String> l = new ArrayList<String>();
        l.add(out.toString());
        return l;
    }
}
