/*
 * @(#) Constants_cs.java 1.3 - last change made 04/05/01
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
 * @author Stepan Marek
 * @version	1.3	04/05/01
 *
 */
 
public class Constants_cs extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
		  "NYI: JH 1.0 p\u0159ipou\u0161t\u00ed pouze jedno mapov\u00e1n\u00ed"},
	    { "helpset.wrongPublicID",
		  "Nezn\u00e1m\u00e9 PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Zkou\u0161\u00edm nastavit nadpis na {0}, ale nadpis m\u00e1 ji\u017e hodnotu {1}."},
	    { "helpset.wrongHomeID",
		  "Zkou\u0161\u00edm nastavit homeID na {0}, ale homeID ma ji\u017e hodnotu {1}."},
	    { "helpset.subHelpSetTrouble",
		  "Nelze vytvo\u0159it subhelpset: {0}"},
	    { "helpset.malformedURL",
		  "Chybn\u00fd form\u00e1t URL: {0}"},
	    { "helpset.incorrectURL",
		  "Chybn\u00e9 URL: {0}"},
	    { "helpset.wrongText",
		  "{0} nem\u016f\u017ee obsahovat text {1}."},
	    { "helpset.wrongTopLevel",
		  "{0} nem\u016f\u017ee b\u00fdt top level tag."},
	    { "helpset.wrongParent",
		  "P\u0159edch\u016fdce tagu {0} nem\u016f\u017ee b\u00fdt {1}."},
	    { "helpset.unbalanced",
		  "Nesoum\u011brn\u00fd tag {0}"},
	    { "helpset.wrongLocale",
		  "Upozorn\u011bn\u00ed: xml:lang atribut {0} je v konfliktu s implicitn\u00edmi hodnotami {1} a {2}"},
	    { "helpset.unknownVersion",
		  "Nezn\u00e1m\u00e1 verze {0}"},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Upozorn\u011bn\u00ed: Index m\u00e1 chybn\u00fd form\u00e1t"},
	    { "index.unknownVersion",
		  "Nezn\u00e1m\u00e1 verze {0}"},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Nezn\u00e1m\u00e9 PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Upozorn\u011bn\u00ed: TOC m\u00e1 chybn\u00fd form\u00e1t TOC"},
	    { "toc.unknownVersion",
		  "Nezn\u00e1m\u00e1 verze {0}"},

		// Map messages
	    { "map.wrongPublicID",
		  "Nezn\u00e1m\u00e9 PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Upozorn\u011bn\u00ed: Map m\u00e1 chybn\u00fd form\u00e1t"},
	    { "map.unknownVersion",
		  "Nezn\u00e1m\u00e1 verze {0}"},

	        // Indexer messages
            { "indexer.unknownKeyword",
                  "Nezn\u00e1m\u00e9 kl\u00ed\u010dov\u00e9 slovo {0}"},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Vyhledat: "},
	    { "search.findLabel", "Vyhledat: "},
	    { "search.hitDesc", "Po\u010det v\u00fdskyt\u016f v dokumentu"},
	    { "search.qualityDesc", "Nejni\u017e\u0161\u00ed chybovost v dokumentu" },
	    { "search.high", "Velmi vysok\u00e1"},
	    { "search.midhigh", "Vysok\u00e1"},
	    { "search.mid", "St\u0159edn\u00ed"},
	    { "search.midlow", "N\u00edzk\u00e1"},
	    { "search.low", "Velmi n\u00edzk\u00e1"},	    
	    // ToolTips
	    { "tip.previous", "P\u0159edchoz\u00ed"},
	    { "tip.next", "N\u00e1sleduj\u00edc\u00ed"},
	    { "tip.history", "Historie"},
	    { "tip.print", "Tisk"},
	    { "tip.pageSetup", "Nastaven\u00ed str\u00e1nky"},
	    { "tip.reload", "Obnovit"}
       };
    }
}
