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

    /** The bundle in which the message, identified by m_code, resides. */
    private String m_bundleName;
    
    /** The identifier for the message in the bundle. */
    private String m_code;
    
    /** The message in the default locale, if it's already been determined. */
    private String m_defaultMessage;
    
    /** Replacements for placeholders in the message, starting at {0}. */
    private Object[] m_values;

    /** 
     * Constructs a new ServerException.
     *
     * @param bundleName The bundle in which the message resides.
     * @param code The identifier for the message in the bundle, aka the key.
     * @param values Replacements for placeholders in the message, where 
     *        placeholders are of the form {num} where num starts at 0,
     *        indicating the 0th (1st) item in this array.
     * @param cause The underlying exception if known, null meaning unknown or 
     *        none.
     */
    public ServerException(String bundleName, String code, String[] values, 
            Throwable cause) {
        super(code, cause);
        m_bundleName=bundleName;
        m_code=code;
        m_values=values;
    }

    /**
     * Gets the identifier for the message.
     * 
     * @return The code, which is also the key in the <code>MessageBundle</code>
     *         for this exception.
     */
    public String getCode() {
        return m_code;
    }
    
    /**
     * Gets the message, preferring the default locale.
     * <p></p>
     * The preferred locale is given by the System properties 
     * <code>locale.language</code>, <code>locale.country</code>, and 
     * (optionally) <code>locale.variant</code>, if they exist, or the
     * system default locale otherwise.
     *
     * @return The message, with {num}-indexed placeholders populated, if 
     *         needed.
     */
    public String getMessage() {
        if (m_defaultMessage==null) {
            String language=System.getProperty("locale.language");
            String country=System.getProperty("locale.country");
            String variant=System.getProperty("locale.variant");
            Locale locale;
            if ((language!=null) && (country!=null)) {
                if (variant!=null) {
                    locale=new Locale(language, country, variant);
                } else {
                    locale=new Locale(language, country);
                }
            } else {
                locale=Locale.getDefault();
            }
            m_defaultMessage=getMessage(locale);
        }
        return m_defaultMessage;
    }
    
    /**
     * Gets the message, preferring the provided locale.
     * <p></p>
     * When a message in the desired locale is not found, the locale selection 
     * logic described by <a href="http://java.sun.com/j2se/1.4/docs/api/java/util/ResourceBundle.html">the
     * java.util.ResourceBundle</a> class javadoc is used.
     * 
     * @param locale The preferred locale.
     * @return The message, with {num}-indexed placeholders populated, if 
     *         needed.
     */
    public String getMessage(Locale locale) {
        if (m_values==null) {
            return ResourceBundle.getBundle(m_bundleName, 
                    locale).getString(m_code);
        }
        return MessageFormat.format(ResourceBundle.getBundle(m_bundleName, 
                locale).getString(m_code), m_values);
    }

}