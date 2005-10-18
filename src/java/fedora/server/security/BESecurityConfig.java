package fedora.server.security;

import java.io.*;
import java.util.*;

/**
 * Security configuration for backend services.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BESecurityConfig {

    private static final String _CONFIG            = "serviceSecurityDescription";
    private static final String _INTERNAL_PREFIX   = "fedoraInternalCall-";
    private final static String _ROLE              = "role";
    private final static String _CALLSSL           = "callSSL";
    private final static String _CALLBASICAUTH     = "callBasicAuth";
    private final static String _CALLUSERNAME      = "callUsername";
    private final static String _CALLPASSWORD      = "callPassword";
    private final static String _CALLBACKSSL       = "callbackSSL";
    private final static String _CALLBACKBASICAUTH = "callbackBasicAuth";
    private final static String _IPLIST            = "iplist";

    /** 
     * The default role configuration, specifying the values to be assumed
     * for any internal call or BMech configuration value which is null.
     */
    private DefaultRoleConfig m_defaultConfig;

    /**
     * Whether Fedora-to-self calls should use SSL.
     */
    private Boolean           m_internalSSL;

    /**
     * Whether Fedora-to-self calls should use basic auth.
     */
    private Boolean           m_internalBasicAuth;

    /**
     * The username to be used for basic-authenticaed Fedora-to-self calls.
     * This value, along with the username, should also be configured in 
     * tomcat-users.xml or whatever other authentication database is in effect.
     */
    private String            m_internalUsername;

    /**
     * The password to be used for basic-authenticaed Fedora-to-self calls.
     * This value, along with the password, should also be configured in 
     * tomcat-users.xml or whatever other authentication database is in effect.
     */
    private String            m_internalPassword;

    /**
     * The list of IP addresses that are allowed for Fedora-to-self calls.
     * This should normally contain 127.0.0.1 and the external IP address
     * of the running server, if known.
     */
    private String[]          m_internalIPList;

    /**
     * A sorted, PID-keyed map of <code>BMechRoleConfig</code>s.
     */
    private SortedMap         m_bMechConfigs;

    /**
     * Create an empty BESecurityConfig with an empty map of 
     * <code>BMechRoleConfig</code>s and <code>null</code> values for 
     * everything else.
     */
    public BESecurityConfig() {
        m_bMechConfigs = new TreeMap();
    }

    /**
     * Get the default role configuration.
     */
    public DefaultRoleConfig getDefaultConfig() {
        return m_defaultConfig;
    }

    /**
     * Set the default role configuration.
     */
    public void setDefaultConfig(DefaultRoleConfig config) {
        m_defaultConfig = config;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get whether SSL should be used for Fedora-to-self calls.
     *
     * This should be true if API-A is only available via SSL.
     */
    public Boolean getInternalSSL() {
        return m_internalSSL;
    }

    /**
     * Get whether SSL is effectively used for Fedora-to-self calls.
     *
     * This will be the internalSSL value, if set, or the inherited
     * call value from the default role, if set, or Boolean.FALSE.
     */
    public Boolean getEffectiveInternalSSL() {
        if (m_internalSSL != null) {
            return m_internalSSL;
        } else if (m_defaultConfig != null) {
            return m_defaultConfig.getEffectiveCallSSL();
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Set whether SSL is used for Fedora-to-self calls.
     */
    public void setInternalSSL(Boolean value) {
        m_internalSSL = value;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get whether basic auth should be used for Fedora-to-self calls.
     *
     * This should be true if API-A requires basic auth.
     */
    public Boolean getInternalBasicAuth() {
        return m_internalBasicAuth;
    }

    /**
     * Get whether basic auth is effectively used for Fedora-to-self calls.
     *
     * This will be the internalBasicAuth value, if set, or the inherited
     * call value from the default role, if set, or Boolean.FALSE.
     */
    public Boolean getEffectiveInternalBasicAuth() {
        if (m_internalBasicAuth != null) {
            return m_internalBasicAuth;
        } else if (m_defaultConfig != null) {
            return m_defaultConfig.getEffectiveCallBasicAuth();
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Set whether basic auth is used for Fedora-to-self calls.
     */
    public void setInternalBasicAuth(Boolean value) {
        m_internalBasicAuth = value;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the internal username.
     */
    public String getInternalUsername() {
        return m_internalUsername;
    }

    /**
     * Get the effective internal username for basic auth Fedora-to-self calls.
     *
     * This will be the internal username, if set, or the inherited
     * call value from the default role, if set, or null.
     */
    public String getEffectiveInternalUsername() {
        if (m_internalUsername != null) {
            return m_internalUsername;
        } else if (m_defaultConfig != null) {
            return m_defaultConfig.getEffectiveCallUsername();
        } else {
            return null;
        }
    }

    /**
     * Set the internal username.
     */
    public void setInternalUsername(String username) {
        m_internalUsername = username;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the internal password.
     */
    public String getInternalPassword() {
        return m_internalPassword;
    }

    /**
     * Get the effective internal password for basic auth Fedora-to-self calls.
     *
     * This will be the internal password, if set, or the inherited
     * call value from the default role, if set, or null.
     */
    public String getEffectiveInternalPassword() {
        if (m_internalPassword != null) {
            return m_internalPassword;
        } else if (m_defaultConfig != null) {
            return m_defaultConfig.getEffectiveCallPassword();
        } else {
            return null;
        }
    }

    /**
     * Set the internal password.
     */
    public void setInternalPassword(String password) {
        m_internalPassword = password;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the list of internal IP addresses.
     */
    public String[] getInternalIPList() {
        return m_internalIPList;
    }

    /**
     * Get the effective list of internal IP addresses.
     *
     * This will be the internalIPList value, if set, or the inherited value
     * from the default role, if set, or null.
     */
    public String[] getEffectiveInternalIPList() {
        if (m_internalIPList != null) {
            return m_internalIPList;
        } else if (m_defaultConfig != null) {
            return m_defaultConfig.getEffectiveIPList();
        } else {
            return null;
        }
    }

    /**
     * Set the list of internal IP addresses.
     */
    public void setInternalIPList(String[] ips) {
        m_internalIPList = ips;
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     * Get the mutable, sorted, PID-keyed map of <code>BMechRoleConfig</code>s.
     */
    public SortedMap getBMechConfigs() {
        return m_bMechConfigs;
    }

    //
    // Deserialization/serialization to/from XML streams.
    //

    /**
     * Instantiate a <code>BESecurityConfig</code> from an XML stream.
     */
    public static BESecurityConfig fromStream(InputStream in) throws Exception {
        return null;
    }

    /**
     * Serialize to the given stream, closing it when finished.
     *
     * If skipNonOverrides is true, any configuration whose values are all
     * null will not be written.
     */
    public void toStream(boolean skipNonOverrides, OutputStream out) throws Exception {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            write(skipNonOverrides, writer);
        } finally {
            try { writer.close(); } catch (Throwable th) { }
            try { out.close(); } catch (Throwable th) { }
        }
    }

    /**
     * Serialize to the given writer, keeping it open when finished.
     *
     * If skipNonOverrides is true, any configuration whose values are all
     * null will not be written.
     */
    public void write(boolean skipNonOverrides, PrintWriter writer) {

        // useful constants while serializing
        final String ns = "info:fedora/fedora-system:def/beSecurity#";
        final String xsi_ns = "http://www.w3.org/2001/XMLSchema-instance";
        final String indent = "                            ";
        final String schemaURL = "http://www.fedora.info/definitions/1/0/api/beSecurity.xsd";

        // header
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<" + _CONFIG + " xmlns=\"" + ns + "\"");
        writer.println(indent + "xmlns:xsi=\"" + xsi_ns + "\"");
        writer.println(indent + "xsi:schemaLocation=\"" + ns + " " + schemaURL + "\"");

        // default values
        writer.print(indent);
        write(m_defaultConfig, false, skipNonOverrides, writer);
        writer.println("/>");

        // fedoraInternalCall-1 and -2
        writeInternalConfig(1, m_internalSSL, 
                               m_internalBasicAuth, 
                               m_internalUsername, 
                               m_internalPassword, 
                               m_internalIPList, 
                               writer);
        writeInternalConfig(2, Boolean.FALSE, 
                               Boolean.FALSE, 
                               null, 
                               null, 
                               m_internalIPList, 
                               writer);

        // bMech roles
        Iterator bIter = m_bMechConfigs.keySet().iterator();
        while (bIter.hasNext()) {
            String bMechRole = (String) bIter.next();
            BMechRoleConfig bConfig = (BMechRoleConfig) m_bMechConfigs.get(bMechRole);
            write(bConfig, true, skipNonOverrides, writer);
            // per-method roles
            Iterator mIter = bConfig.getMethodConfigs().keySet().iterator();
            while (mIter.hasNext()) {
                String methodName = (String) mIter.next();
                MethodRoleConfig mConfig = (MethodRoleConfig) bConfig.getMethodConfigs().get(methodName);
                write(mConfig, true, skipNonOverrides, writer);
            }
        }

        // closing element for entire doc
        writer.println("</" + _CONFIG + ">");
    }

    private static void writeInternalConfig(int n,
                                            Boolean ssl,
                                            Boolean basicAuth,
                                            String username,
                                            String password,
                                            String[] ipList,
                                            PrintWriter writer) {
        writer.print("  " + _CONFIG);
        writeAttribute(_ROLE,              _INTERNAL_PREFIX + n, writer);
        writeAttribute(_CALLSSL,           ssl,                  writer);
        writeAttribute(_CALLBASICAUTH,     basicAuth,            writer);
        writeAttribute(_CALLUSERNAME,      username,             writer);
        writeAttribute(_CALLPASSWORD,      password,             writer);
        writeAttribute(_CALLBACKSSL,       ssl,                  writer);
        writeAttribute(_CALLBACKBASICAUTH, basicAuth,            writer);
        writeAttribute(_IPLIST,            ipList,               writer);
    }


    /**
     * Write all the defined attributes of the given <code>BERoleConfig</code>,
     * surrounding them with the appropriate element start/end text if
     * <code>wholeElement</code> is true.
     *
     * Skip the entire element if skipIfAllNull is true.
     */
    private static void write(BERoleConfig config, 
                              boolean wholeElement, 
                              boolean skipIfAllNull,
                              PrintWriter writer) {

        if (wholeElement) {
            if (skipIfAllNull
                    && config.getCallSSL()           == null
                    && config.getCallBasicAuth()     == null
                    && config.getCallUsername()      == null
                    && config.getCallPassword()      == null
                    && config.getCallbackSSL()       == null
                    && config.getCallbackBasicAuth() == null
                    && config.getIPList()            == null) {
                return;
            }
            writer.print("  <" + _CONFIG);
        }

        writeAttribute(_ROLE,              config.getRole(),              writer);
        writeAttribute(_CALLSSL,           config.getCallSSL(),           writer);
        writeAttribute(_CALLBASICAUTH,     config.getCallBasicAuth(),     writer);
        writeAttribute(_CALLUSERNAME,      config.getCallUsername(),      writer);
        writeAttribute(_CALLPASSWORD,      config.getCallPassword(),      writer);
        writeAttribute(_CALLBACKSSL,       config.getCallbackSSL(),       writer);
        writeAttribute(_CALLBACKBASICAUTH, config.getCallbackBasicAuth(), writer);
        writeAttribute(_IPLIST,            config.getIPList(),            writer);

        if (wholeElement) {
            writer.println("/>");
        }
    }

    /**
     * Write (space)name="value" to the given PrintWriter if value is defined.
     */
    private static void writeAttribute(String name, Object value, PrintWriter writer) {
        if (value != null) {
            String s;
            if (value instanceof String || value instanceof Boolean) {
                // for String/Boolean we can just use toString()
                s = value.toString();
            } else {
                // otherwise its a String[], so space-delimit the values
                String[] tokens = (String[]) value;
                StringBuffer buf = new StringBuffer();
                for (int i = 0; i < tokens.length; i++) {
                    if (i > 0) buf.append(' ');
                    buf.append(tokens[i]);
                }
                s = buf.toString();
            }
            writer.print(" " + name + "=\"" + s + "\"");
        }
    }


    /**
     * Simple command-line test entry point.
     *
     * Takes two args, inputFile and outputFile.
     * Will deserialize from inputFile and serialize to outputFile.
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            BESecurityConfig config = BESecurityConfig.fromStream(
                    new FileInputStream(new File(args[0])));
            config.toStream(false, new FileOutputStream(new File(args[1])));
        } else {
            System.err.println("Expected 2 args: inputFile and outputFile");
        }
    }

}