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
 * Constants_de.java 
 *
 * Originaly: Constants.java 1.8 - last change made 01/29/99
 * Translated to German by iXOS Software AG, 03/03/1999, Martin Balin
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_de extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {
	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
		  "NYI: JH 1.0 akzeptiert nur eine map-Zuordnung"},
	    { "helpset.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "helpset.wrongTitle",
		  "Versuche, Titel auf {0} zu setzen, aber Wert {1} ist schon gesetzt."},
	    { "helpset.wrongHomeID",
		  "Versuche HomeID auf {0} zu setzen, aber Wert {1} ist schon gesetzt."},
	    { "helpset.subHelpSetTrouble",
		  "Probleme beim Erzeugen des Subhelpset: {0}."},
	    { "helpset.malformedURL",
		  "Formfehler in URL: {0}."},
	    { "helpset.incorrectURL",
		  "Fehlerhafte URL: {0}."},
	    { "helpset.wrongText",
		  "{0} darf nicht den Text {1} enthalten."},
	    { "helpset.wrongTopLevel",
		  "{0} darf kein Top Level Tag sein."},
	    { "helpset.wrongParent",
		  "Parent Tag f\u00fcr {0} darf nicht {1} sein."},
	    { "helpset.unbalanced",
		  "Einseitiger Tag {0}."},
	    { "helpset.wrongLocale",
		  "Warning: xml:lang-Attribut {0} widerspricht Voreinstellung {1} und Voreinstellung {2}"},
	    { "helpset.unknownVersion",
		  "Unbekannte Version {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "Warnung: Ung\u00fcltiges Index-Format"},
	    { "index.unknownVersion",
		  "Unbekannte Version {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "Warnung: Ung\u00fcltiges Format f\u00fcr Inhaltsverzeichnis"},
	    { "toc.unknownVersion",
		  "Unbekannte Version {0}."},

		// Map messages
	    { "map.wrongPublicID",
		  "Unbekannte PublicID {0}"},
	    { "map.invalidMapFormat",
		  "Warnung: Ung\u00fcltiges Map-Format"},
	    { "map.unknownVersion",
		  "Unbekannte Version {0}."},


	    // GUI components
	    // Labels
	    { "index.findLabel", "Suche: "},
	    { "search.findLabel", "Suche: "},
	    // ToolTips
	    { "tip.previous", "Voriger"},
	    { "tip.next", "N\u00e4chster"},
	    { "tip.history", "Verlauf"},
	    { "tip.print", "Drucken"},
	    { "tip.pageSetup", "Seite einrichten"},
	    { "tip.reload", "Neu laden"}
       };
    }
}
