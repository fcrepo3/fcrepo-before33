package fedora.server.errors;

import java.util.Locale;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Signifies that an error occurred in the server.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ServerException 
        extends Exception {
        
    private String m_bundleName;
    private int m_code;
    private String m_messageId;
    private Object[] m_replacements;

    public ServerException(String bundleName, String messageId, String[] replacements, Throwable cause) {
        initCause(cause);
        fillInStackTrace();
        m_bundleName=bundleName;
        try {
            m_code=Integer.parseInt(MessageFormat.format(ResourceBundle.getBundle(messageId + ".code").getString(messageId), replacements));
        } catch (NumberFormatException nfe) { m_code=9000; }
    }

    public int getCode() {
        return m_code;
    }
    
    
    public String getMessage() {
        return MessageFormat.format(ResourceBundle.getBundle(m_bundleName).getString(m_messageId), m_replacements);
    }
    
    public String getMessage(Locale locale) {
        return MessageFormat.format(ResourceBundle.getBundle(m_bundleName, locale).getString(m_messageId), m_replacements);
    }

}