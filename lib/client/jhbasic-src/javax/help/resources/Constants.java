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
/*
 * @(#) Constants.java 1.13 - last change made 04/05/01
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
		  "NYI: JH 1.0 only accepts one map data"},
	    { "helpset.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Attempting to set title to {0} but already has value {1}."},
	    { "helpset.wrongHomeID",
		  "Attempting to set homeID to {0} but already has value {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Trouble creating subhelpset: {0}."},
	    { "helpset.malformedURL",
		  "Malformed URL: {0}."},
	    { "helpset.incorrectURL",
		  "Incorrect URL: {0}."},
	    { "helpset.wrongText",
		  "{0} cannot contain text {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} cannot be a top level tag."},
	    { "helpset.wrongParent",
		  "The parent tag for {0} cannot be {1}."},
	    { "helpset.unbalanced",
		  "Unbalanced tag {0}."},
	    { "helpset.wrongLocale",
		  "Warning: xml:lang attribute {0} conflicts with default {1} and with default {2}"},
	    { "helpset.unknownVersion",
		  "Unknown Version {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Warning: Invalid Index format"},
	    { "index.unknownVersion",
		  "Unknown Version {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Warning: Invalid TOC format"},
	    { "toc.unknownVersion",
		  "Unknown Version {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Unknown PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Warning: Invalid Map format"},
	    { "map.unknownVersion",
		  "Unknown Version {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Find: "},
	    { "search.findLabel", "Find: "},
	    { "search.hitDesc", "Number of occurances in document"},
	    { "search.qualityDesc", "Lowest penality value in document" },
	    { "search.high", "High"},
	    { "search.midhigh", "Medium high"},
	    { "search.mid", "Medium"},
	    { "search.midlow", "Medium low"},
	    { "search.low", "Low"},
	    // ToolTips
	    { "tip.previous", "Previous"},
	    { "tip.next", "Next"},
	    { "tip.history", "History"},
	    { "tip.print", "Print"},
	    { "tip.pageSetup", "Page setup"},
	    { "tip.reload", "Reload"}
       };
    }
}
