package fedora.server.config;

import java.util.*;

public class DatastoreConfiguration 
        extends Configuration {

    private String m_id;
    private String m_comment;

    public DatastoreConfiguration(List parameters,
                                  String id,
                                  String comment) {
        super(parameters);
        m_id = id;
        m_comment = comment;
    }

    public String getId() {
        return m_id;
    }

    public String getComment() {
        return m_comment;
    }

}
