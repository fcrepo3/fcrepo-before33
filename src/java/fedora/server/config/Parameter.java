package fedora.server.config;

import java.util.*;

public class Parameter {

    private String m_name;
    private String m_value;
    private String m_comment;
    private Map m_profileValues;

    public Parameter(String name,
                     String value,
                     String comment,
                     Map profileValues) {
    }

    public String getName() {
        return m_name;
    }

    public String getValue() {
        return m_value;
    }

    public Map getProfileValues() {
        return m_profileValues;
    }

    public void setComment(String comment) {
        m_comment = comment;
    }

}