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
 * @(#) Constants.java 1.11 - last change made 08/25/99
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_fr extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
	          "NYI: JH 1.0 accepte uniquement une donn\u00e9e d'application"},
	    { "helpset.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "helpset.wrongTitle",
	          "Essai de r\u00e9glage du titre sur {0} mais valeur {1} existe d\u00e9j\u00e0."},
	    { "helpset.wrongHomeID",
	          "Essai de r\u00e9glage de l''ID domestique sur {0} mais valeur {1} existe d\u00e9j\u00e0."},
	    { "helpset.subHelpSetTrouble",
	          "Probl\u00e8me lors de la cr\u00e9ation de l''ensemble de sous-aide : {0}."},
	    { "helpset.malformedURL",
	          "URL mal form\u00e9 : {0}."},
	    { "helpset.incorrectURL", 
	          "URL incorrect : {0}."},
	    { "helpset.wrongText",
	          "{0} ne peut contenir de texte {1}."},
	    { "helpset.wrongTopLevel",
	          "{0} ne peut pas \u00eatre une \u00e9tiquette de haut niveau."},
	    { "helpset.wrongParent",
	          "L''\u00e9tiquette parent pour {0} ne peut pas \u00eatre {1}."},
	    { "helpset.unbalanced",
	          "\u00c9tiquette non \u00e9quilibr\u00e9e {0}."},
	    { "helpset.wrongLocale",
	          "Attention : xml:lang attribue {0} des conflits avec d\u00e9faut {1} et avec d\u00e9faut {2}"},
	    { "helpset.unknownVersion",
	          "Version inconnue {0}."},

		// IndexView messages
	    { "index.invalidIndexFormat",
	          "Attention : format d'Index invalide"},
	    { "index.unknownVersion",
	          "Version inconnue {0}."},

		// TOCView messages
	    { "toc.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "toc.invalidTOCFormat",
	          "Attention : Format TOC invalide"},
	    { "toc.unknownVersion",
	          "Version inconnue {0}."},

		// Map messages
	    { "map.wrongPublicID",
	          "ID Publique inconnue {0}"},
	    { "map.invalidMapFormat",
	          "Attention : Format Map inconnu"},
	    { "map.unknownVersion",
	          "Version inconnue {0}."},

	    // GUI components
	    // Labels
	    { "index.findLabel", "Trouver : "},
	    { "search.findLabel", "Trouver : "},
	    { "search.hitDesc", "Nombre d'apparitions dans le document"},
	    { "search.qualityDesc", "Valeur de p\u00e9nalit\u00e9 la plus basse du document"
},
	    { "search.high", "Haut"},
	    { "search.midhigh", "Moyen haut"},
	    { "search.mid", "Moyen"},
	    { "search.midlow", "Moyen bas"},
	    { "search.low", "Bas"},
	    // ToolTips
	    { "tip.previous", "Pr\u00e9c\u00e9dent"},
	    { "tip.next", "Suivant"},
	    { "tip.history", "Histoire"},
	    { "tip.print", "Imprimer"},
	    { "tip.pageSetup", "Iise en page"},
	    { "tip.reload", "Recharger"}
       };
    }
}
