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
        m_name = name;
        m_value = value;
        m_comment = comment;
        m_profileValues = profileValues;
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

    public String getComment() {
        return m_comment;
    }

    public void setComment(String comment) {
        m_comment = comment;
    }

    public String toString() {
        return m_name;
    }

}
