package fedora.server.storage.types;

import java.util.Date;

public class AuditRecord {

    public AuditRecord() {
    }
    
    public String processType;
    public String action;
    public String responsibility;
    public Date date;
    public String justification;
}