/*
 * @(#) Constants_sk.java 1.2 - last change made 01/17/01
 *
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

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 *
 * @author Richard Gregor 
 * @version	1.2	03/08/01
 *
 */

public class Constants_sk extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
		  "NYI: JH 1.0 akceptuje iba jedno mapovanie"},
	    { "helpset.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Pokus o nastavenie Title na {0} Title u\u009e m\u00e1 hodnotu {1}."},
	    { "helpset.wrongHomeID",
		  "Pokus o nastavenie homeID na {0} homeID u\u009e m\u00e1 hodnotu {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Probl\u00e9m pri vytv\u00e1ran\u00ed subhelpsetu: {0}."},
	    { "helpset.malformedURL",
		  "Chybn\u00fd form\u00e1t URL: {0}."},
	    { "helpset.incorrectURL",
		  "Nespr\u00e1vne URL: {0}."},
	    { "helpset.wrongText",
		  "{0} nem\u00f4\u009ee obsahova? text {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} Nespr\u00e1vny top level tag."},
	    { "helpset.wrongParent",
		  "Rodi?ovsk\u00fd tag {0} nem\u00f4\u009ee by? {1}."},
	    { "helpset.unbalanced",
		  "Neukon?en\u00fd tag {0}."},
	    { "helpset.wrongLocale",
		  "Pozor!: xml:lang atrib\u00fat {0} je v konflikte s {1} a s {2}"},
	    { "helpset.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Pozor!: Index m\u00e1 chybn\u00fd form\u00e1t"},
	    { "index.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Pozor!: TOC m\u00e1 chybn\u00fd form\u00e1t"},
	    { "toc.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Nezn\u00e1me PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Pozor!: Map m\u00e1 nespr\u00e1vny form\u00e1t"},
	    { "map.unknownVersion",
		  "Nezn\u00e1ma verzia {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Vyh?ada?: "},
	    { "search.findLabel", "Vyh?ada?: "},
	    { "search.hitDesc", "Po?et v\u00fdskytov v dokumente"},
	    { "search.qualityDesc", "Miera nepresnosti" },
	    { "search.high", "Najvy\u009a\u009aia"},
	    { "search.midhigh", "Vysok\u00e1"},
	    { "search.mid", "Stredn\u00e1"},
	    { "search.midlow", "N\u00edzka"},
	    { "search.low", "Najni\u009e\u009aia"},
	    // ToolTips
	    { "tip.previous", "Predch\u00e1dzaj\u00face"},
	    { "tip.next", "?Al\u009aie"},
	    { "tip.history", "Hist\u00f3ria"},
	    { "tip.print", "Tla?"},
	    { "tip.pageSetup", "Nastavenie str\u00e1nky"},
	    { "tip.reload", "Obnovi?"}
       };
    
    }

    
}
