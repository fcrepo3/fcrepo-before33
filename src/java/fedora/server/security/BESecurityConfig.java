package fedora.server.security;

import java.io.*;
import java.util.*;

/**
 * Security configuration for backend services.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BESecurityConfig {

    public static final String CONFIG_ELEMENT = "serviceSecurityDescription";

    /** 
     * The default role configuration, specifying the values to be assumed
     * for any BMech configuration value which is null.
     */
    private DefaultRoleConfig m_defaultConfig;

    /**
     * The list of IP addresses that are allowed for Fedora-to-Fedora calls.
     * This should normally contain 127.0.0.1 and the external IP address
     * of the running server, if known.
     */
    private String[]     m_internalIPs;

    /**
     * The username to be used for basic-authenticaed Fedora-to-Fedora calls.
     * This value, along with the username, should also be configured in 
     * tomcat-users.xml or whatever other authentication database is in effect.
     */
    private String       m_internalUsername;

    /**
     * The password to be used for basic-authenticaed Fedora-to-Fedora calls.
     * This value, along with the password, should also be configured in 
     * tomcat-users.xml or whatever other authentication database is in effect.
     */
    private String       m_internalPassword;

    /**
     * A sorted, PID-keyed map of <code>BMechRoleConfig</code>s.
     */
    private SortedMap    m_bMechConfigs;

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

    /**
     * Get the list of internal IP addresses.
     */
    public String[] getInternalIPs() {
        return m_internalIPs;
    }

    /**
     * Set the list of internal IP addresses.
     */
    public void setInternalIPs(String[] ips) {
        m_internalIPs = ips;
    }

    /**
     * Get the internal username.
     */
    public String getInternalUsername() {
        return m_internalUsername;
    }

    /**
     * Set the internal username.
     */
    public void setInternalUsername(String username) {
        m_internalUsername = username;
    }

    /**
     * Get the internal password.
     */
    public String getInternalPassword() {
        return m_internalPassword;
    }

    /**
     * Set the internal password.
     */
    public void setInternalPassword(String password) {
        m_internalPassword = password;
    }

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
     */
    public void toStream(OutputStream out) throws Exception {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
            write(writer);
        } finally {
            try { writer.close(); } catch (Throwable th) { }
            try { out.close(); } catch (Throwable th) { }
        }
    }

    /**
     * Serialize to the given writer, keeping it open when finished.
     */
    public void write(PrintWriter writer) {

        // useful constants while serializing
        final String ns = "info:fedora/fedora-system:def/beSecurity#";
        final String xsi_ns = "http://www.w3.org/2001/XMLSchema-instance";
        final String indent = "                            ";
        final String schemaURL = "http://www.fedora.info/definitions/1/0/api/beSecurity.xsd";

        // header
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<" + CONFIG_ELEMENT + " xmlns=\"" + ns + "\"");
        writer.println(indent + "xmlns:xsi=\"" + xsi_ns + "\"");
        writer.println(indent + "xsi:schemaLocation=\"" + ns + " " + schemaURL + "\"");

        // default values
        writer.print(indent);
        write(m_defaultConfig, false, writer);
    }

    /**
     * Write all the defined attributes of the given <code>BERoleConfig</code>,
     * surrounding them with the appropriate element start/end text if
     * <code>wholeElement</code> is true.
     */
    private static void write(BERoleConfig config, boolean wholeElement, PrintWriter writer) {
        if (wholeElement) {
            writer.print("  <" + CONFIG_ELEMENT);
        }
        writeAttribute("role", config.getRole(), writer);
//        writeAttribute("", config.get(), writer);
        if (wholeElement) {
            writer.println("/>\n");
        }
    }

    /**
     * Write (space)name="value" to the given PrintWriter if value is defined.
     */
    private static void writeAttribute(String name, Object value, PrintWriter writer) {
        if (value != null) {
            writer.print(" " + name + "=\"" + value.toString() + "\"");
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
            config.toStream(new FileOutputStream(new File(args[1])));
        } else {
            System.err.println("Expected 2 args: inputFile and outputFile");
        }
    }

}