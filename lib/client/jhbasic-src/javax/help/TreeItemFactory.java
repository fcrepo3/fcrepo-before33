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
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A factory for creating TreeItems.  This can be used to reuse the parsers.
 *
 * @author Eduardo Pelegri-Llopart
 * @(#)TreeItemFactory.java 1.7 01/29/99
 */

public interface TreeItemFactory {
    /**
     * Starts parsing.
     *
     * @param source The URL of the document being parsed.
     */
    public void parsingStarted(URL source);


    /**
     * Processes a DOCTYPE.
     *
     * @param root The root tag of the document.
     * @param publicID PublicID from the DOCTYPE.
     * @param systemID SystemID from the DOCTYPE.
     */
    public void processDOCTYPE(String root, String publicID, String systemID);

    /**
     * A Processing Instruction.
     *
     * @param target The target of the PI.
     * @param data A String for the data in the PI.
     */
    public void processPI(HelpSet hs,
			  String target,
			  String data);

    /**
     * Creates a TreeItem from the given data.
     *
     * @param tagName The name of the tag (for example, treeItem, or tocItem)
     * @param attributes A hashtable with all the attributes.  Null is a valid value.
     * @param hs A HelpSet that provides context.
     * @param lang The locale.
     * @return A TreeItem.
     */
    public TreeItem createItem(String tagName,
			       Hashtable attributes,
			       HelpSet hs,
			       Locale locale);

    /**
     * Creates a default TreeItem.
     *
     * @return A TreeItem
     */
    public TreeItem createItem();

    /**
     * Reports a parsing error.
     *
     * @param msg The message to report.
     * @param validParse Whether the result of the parse is still valid.
     */
    public void reportMessage(String msg, boolean validParse);

    /**
     * Lists all the error messages.
     */
    public Enumeration listMessages();

    /**
     * Ends parsing.  Last chance to do something
     * to the node.  Return null to be sure the result is discarded.
     */
    public DefaultMutableTreeNode parsingEnded(DefaultMutableTreeNode node);
}

