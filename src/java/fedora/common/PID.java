package fedora.common;

import java.io.*;  // BufferedReader, InputStreamReader

/**
 * A persistent identifier for Fedora digital objects.
 *
 * <p>The following describes the syntactic constraints for PIDs in normalized
 * form.  The only differences with non-normalized PIDs are that the 
 * colon delimiter may be encoded as "%3a" or "%3A", and hex-digits may
 * use lowercase [a-f].</p>
 *
 * <pre>
 * PID:
 *   Length : maximum 64
 *   Syntax : namespace-id ":" object-id
 *
 * namespace-id:
 *   Syntax : ( [A-Z] / [a-z] / [0-9] / "-" / "." ) 1+
 *
 * object-id:
 *   Syntax : ( [A-Z] / [a-z] / [0-9] / "-" / "." / "~" / "_" / escaped-octet ) 1+
 *
 * escaped-octet:
 *   Syntax : "%" hex-digit hex-digit
 *
 * hex-digit:
 *   Syntax : [0-9] / [A-F]
 * </pre>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @version $Id$
 * @author cwilper@cs.cornell.edu
 */
public class PID {

    public static final int MAX_LENGTH = 64;

    private String m_normalized;
    private String m_filename;

    /**
     * Construct a PID from a string, throwing a MalformedPIDException
     * if it's not well-formed.
     */
    public PID(String pidString) 
            throws MalformedPIDException {
        m_normalized = normalize(pidString);
    }

    /**
     * Construct a PID given a filename of the form produced by toFilename(), 
     * throwing a MalformedPIDException if it's not well-formed.
     */
    public static PID fromFilename(String filenameString) 
            throws MalformedPIDException {
        String decoded = filenameString.replaceFirst("_", ":");
        while (decoded.endsWith("%")) {
            decoded = decoded.substring(0, decoded.length() - 1) + ".";
        }
        return new PID(decoded);
    }

    /**
     * Return the normalized form of the given pid string,
     * or throw a MalformedPIDException.
     */
    public static String normalize(String pidString) 
            throws MalformedPIDException {
        // First, do null & length checks
        if (pidString == null) {
            throw new MalformedPIDException("PID is null.");
        }
        if (pidString.length() > MAX_LENGTH) {
            throw new MalformedPIDException("PID length exceeds " 
                    + MAX_LENGTH + ".");
        }

        // Then normalize while checking syntax
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < pidString.length(); i++) {
            char c = pidString.charAt(i);
           
        }

        // If we got here, it's well-formed, so return it.
        return out.toString();
    }

    /**
     * Return the normalized form of this PID.
     */
    public String toString() {
        return m_normalized;
    }

    /**
     * Return the URI form of this PID.
     * 
     * This is just the PID, prepended with "info:fedora/".
     */
    public String toURI() {
        return "info:fedora/" + m_normalized;
    }

    /**
     * Return a string representing this PID that can be safely used
     * as a filename on any OS.
     *
     * <ul>
     *   <li> The colon (:) is replaced with an underscore (_).</li>
     *   <li> Trailing dots are encoded as percents (%).</li>
     * </ul>
     */
    public String toFilename() {
        if (m_filename == null) { // lazily convert, since not always needed
            m_filename = m_normalized.replaceAll(":", "_");
            while (m_filename.endsWith(".")) {
                m_filename = m_filename.substring(0, m_filename.length() - 1) + "%";
            }
        }
        return m_filename;
    }

    /**
     * Command-line interactive tester.
     *
     * If one arg given, prints normalized form of that PID and exits.
     * If no args, enters interactive mode.
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            PID p = new PID(args[0]);
            System.out.println("Normalized    : " + p.toString());
            System.out.println("To filename   : " + p.toFilename());
            System.out.println("From filename : " + PID.fromFilename(p.toFilename()).toString());
        } else {
            System.out.println("--------------------------------------");
            System.out.println("PID Syntax Checker - Interactive mode");
            System.out.println("--------------------------------------");
            boolean done = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (!done) {
                try {
                    System.out.print("Enter a PID (ENTER to exit): ");
                    String line = reader.readLine();
                    if (line.equals("")) {
                        done = true;
                    } else {
                        PID p = new PID(line);
                        System.out.println("Normalized    : " + p.toString());
                        System.out.println("To filename   : " + p.toFilename());
                        System.out.println("From filename : " + PID.fromFilename(p.toFilename()).toString());
                    }
                } catch (MalformedPIDException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }
    }

}
