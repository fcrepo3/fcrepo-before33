package fedora.server.storage.types;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> AuditRecord.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class AuditRecord {

    public AuditRecord() {
    }

    public String id;
    public String processType;
    public String action;
    public String componentID;
    public String responsibility;
    public Date date;
    public String justification;
}