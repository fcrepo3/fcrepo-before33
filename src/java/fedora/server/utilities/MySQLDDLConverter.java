package fedora.server.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

public class MySQLDDLConverter
        implements DDLConverter {
        
    public MySQLDDLConverter() { 
    }

    public boolean supportsTableType() {
        return true;
    }

    public String getDDL(TableSpec spec) {
        StringBuffer out=new StringBuffer();
        StringBuffer end=new StringBuffer();
        out.append("CREATE TABLE " + spec.getName() + " (\n");
        Iterator csi=spec.columnSpecIterator();
        int csNum=0;
        while (csi.hasNext()) {
            if (csNum>0) {
                out.append(",\n");
            }
            csNum++;
            ColumnSpec cs=(ColumnSpec) csi.next();
            out.append("  ");
            out.append(cs.getName());
            out.append(' ');
            out.append(cs.getType());
            if (cs.isNotNull()) {
                out.append(" NOT NULL");
            }
            if (cs.isAutoIncremented()) {
                out.append(" auto_increment");
            }
            if (cs.getDefaultValue()!=null) {
                out.append(" default '");
                out.append(cs.getDefaultValue());
                out.append("'");
            }
            if (cs.isUnique()) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  UNIQUE KEY ");
                end.append(cs.getName());
                end.append(" (");
                end.append(cs.getName());
                end.append(")");
            }
            if (cs.getIndexName()!=null) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  KEY ");
                end.append(cs.getIndexName());
                end.append(" (");
                end.append(cs.getName());
                end.append(")");
            }
            if (cs.getForeignTableName()!=null) {
                if (!end.toString().equals("")) {
                    end.append(",\n");
                }
                end.append("  FOREIGN KEY ");
                end.append(cs.getName());
                end.append(" (");
                end.append(cs.getName());
                end.append(") REFERENCES ");
                end.append(cs.getForeignTableName());
                end.append(" (");
                end.append(cs.getForeignColumnName());
                end.append(")");
                if (cs.getOnDeleteAction()!=null) {
                    end.append(" ON DELETE ");
                    end.append(cs.getOnDeleteAction());
                }
            }
        }
        if (spec.getPrimaryColumnName()!=null) {
            out.append(",\n  PRIMARY KEY (");
            out.append(spec.getPrimaryColumnName());
            out.append(")");
        }
        if (!end.toString().equals("")) {
            out.append(",\n");
            out.append(end);
        }
        out.append("\n");
        out.append(")");
        if (spec.getType()!=null) {
            out.append(" TYPE=" + spec.getType());
        }
        return out.toString();
    }
    
    public static void main(String[] args) {
        try {
            MySQLDDLConverter conv=new MySQLDDLConverter();
            List l=TableSpec.getTableSpecs(new FileInputStream(new File(args[0])));
            Iterator iter=l.iterator();
            while (iter.hasNext()) {
                TableSpec spec=(TableSpec) iter.next();
                System.out.println(conv.getDDL(spec));
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

}

