/*
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package javax.help;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Enumeration;
import java.net.URL;

/**
 * A factory for creating HelpSets.  This can be used to reuse our parser.
 *
 * @author Eduardo Pelegri-Llopart
 * @(#)HelpSetFactory.java 1.6 03/16/99
 */

interface HelpSetFactory {
    /**
     * Parsing starts.
     *
     * @param url URL to the document being parsed
     */
    public void parsingStarted(URL source);

    /**
     * Processes a DOCTYPE
     *
     * @param root The root tag of the document
     * @param publicID PublicID from the DOCTYPE
     * @param systemID SystemID from the DOCTYPE
     */
    public void processDOCTYPE(String root, String publicID, String systemID);

    /**
     * A Processing Instruction.
     *
     * @param target The target of the PI
     * @param data A String for the data in the PI
     */
    public void processPI(HelpSet hs,
			  String target,
			  String data);

    /**
     * process <title>
     *
     * @param hs The Helpset
     * @param title The title of the HelpSet
     */
    public void processTitle(HelpSet hs,
			     String title);

    /**
     * Processes &lt;homeID&gt;.
     *
     * @param hs The Helpset
     * @param homeID The home ID for the helpset
     */
    public void processHomeID(HelpSet hs,
			      String homeID);

    /**
     * Process a &l;mapref&gt;.
     *
     * @param hs The HelpSet
     * @param Attributes for this tag
     */
    public void processMapRef(HelpSet hs,
			      Hashtable attributes);

    /**
     * Creates a NavigatorView from the data.
     *
     * @param hs The HelpSet
     */

    public void processView(HelpSet hs,
			    String name,
			    String label,
			    String type,
			    Hashtable viewAttributes,
			    String data,
			    Hashtable dataAttributes,
			    Locale locale);
    
    /**
     * Processes a sub-HelpSet tag.
     *
     * @param base The base URL from where to locate the sub-HelpSet.
     * @param att A collection of attributes that might be used.
     * @returns A HelpSet to be added.
     */
    public void processSubHelpSet(HelpSet hs,
				  Hashtable attributes);

    /**
     * Reports some parsing error.
     *
     * @param msg The message to report.
     * @param validParse Whether the on-going parse should return a valid object.
     */
    public void reportMessage(String msg, boolean validParse);

    /**
     * Enumerated all the error mesages.
     */
    public Enumeration listMessages();

    /**
     * Parsing ends.  Last chance to do something
     * to the HelpSet
     */
    public HelpSet parsingEnded(HelpSet hs);
}
