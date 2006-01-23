package fedora.server.config;

import java.io.File;
import java.util.*;

import fedora.common.Constants;

/**
 *
 */
public class Parameter implements Constants {

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
    
    /**
     * 
     * @return parameter value as an absolute path (resolved relative to 
     * FEDORA_HOME as necessary)
     */
    public String getValueAsAbsolutePath() {
    	String path = m_value;
    	if (path != null) {
	    	File f = new File(path);
	    	if (!f.isAbsolute()) {
	    		path = FEDORA_HOME + File.separator + path;
			}
    	}
    	return path;
    }

    public void setValue(String newValue) {
        m_value = newValue;
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
