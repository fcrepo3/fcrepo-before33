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
 * @(#) Constants_ru.java 1.4 - last change made 11/12/99
 * @(#) Constants.java 1.8 - last change made 01/29/99
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values
 */

public class Constants_ru extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    public Object[][] getContents() {
        return new Object[][] {

            //  Constant strings and patterns
            { "helpset.oneMapOnly",
                  "JH 1.0 beta1 \u043c\u043e\u0436\u0435\u0442 \u0438\u043c\u0435\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u043e\u0434\u0438\u043d map-\u0444\u0430\u0439\u043b"},
            { "helpset.wrongPublicID",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 PublicID {0}"},
            { "helpset.wrongTitle",
                  "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 title \u043d\u0430 {0} \u0442\u0430\u043a \u043a\u0430\u043a \u043e\u043d \u0443\u0436\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d \u0432 {1}."},
            { "helpset.wrongHomeID",
                  "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435 homeID \u043d\u0430 {0} \u0442\u0430\u043a \u043a\u0430\u043a \u043e\u043d \u0443\u0436\u0435 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d \u0432 {1}."},
            { "helpset.subHelpSetTrouble",
                  "\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f subhelpset: {0}."},
            { "helpset.malformedURL",
                  "\u041e\u0448\u0438\u0431\u043e\u0447\u043d\u044b\u0439 URL: {0}."},
            { "helpset.incorrectURL",
                  "\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 URL: {0}."},
            { "helpset.wrongText",
                  "{0} \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c \u0442\u0435\u043a\u0441\u0442 {1}."},
            { "helpset.wrongTopLevel",
                  "{0} \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0442\u0435\u0433\u043e\u043c \u0432\u0435\u0440\u0445\u043d\u0435\u0433\u043e \u0443\u0440\u043e\u0432\u043d\u044f."},
            { "helpset.wrongParent",
                  "\u0420\u043e\u0434\u0438\u0442\u0435\u043b\u044c\u0441\u043a\u0438\u0439 \u0442\u0435\u0433 {0} \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c {1}."},
            { "helpset.unbalanced",
                  "\u041d\u0435\u0437\u0430\u043a\u0440\u044b\u0442\u044b\u0439 \u0442\u0435\u0433 {0}."},
            { "helpset.wrongLocale",
                  "\u041f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0435\u043d\u0438\u0435: xml: \u044f\u0437\u044b\u043a\u043e\u0432\u043e\u0439 \u0430\u0442\u0440\u0438\u0431\u0443\u0442 {0} \u043d\u0435 \u0441\u043e\u0433\u043b\u0430\u0441\u0443\u0435\u0442\u0441\u044f \u0441 \u0430\u0442\u0440\u0438\u0431\u0443\u0442\u043e\u043c \u043f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e {1} \u0438 {2}"},
            { "helpset.unknownVersion",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f {0}."},

                // IndexView messages
            { "index.invalidIndexFormat",
                  "\u041f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0435\u043d\u0438\u0435: \u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442 \u0438\u043d\u0434\u0435\u043a\u0441\u0430"},
            { "index.unknownVersion",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f {0}."},

                // TOCView messages
            { "toc.wrongPublicID",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 PublicID {0}"},
            { "toc.invalidTOCFormat",
                  "\u041f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0435\u043d\u0438\u0435: \u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442 \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u044f"},
            { "toc.unknownVersion",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f {0}."},

                // Map messages
            { "map.wrongPublicID",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439  PublicID {0}"},
            { "map.invalidMapFormat",
                  "\u041f\u0440\u0435\u0434\u0443\u043f\u0440\u0435\u0436\u0434\u0435\u043d\u0438\u0435: \u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442 \u043a\u0430\u0440\u0442\u044b"},
            { "map.unknownVersion",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u0430\u044f \u0432\u0435\u0440\u0441\u0438\u044f {0}."},

                // Indexer messages
            { "indexer.unknownKeyword",
                  "\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u043e\u0435 \u043a\u043b\u044e\u0447\u0435\u0432\u043e\u0435 \u0441\u043b\u043e\u0432\u043e {0}"},

            // GUI components
            // Labels
            { "index.findLabel", "\u041d\u0430\u0439\u0442\u0438: "},
            { "search.findLabel", "\u041d\u0430\u0439\u0442\u0438: "},
            // ToolTips
            { "tip.previous", "\u043d\u0430\u0437\u0430\u0434"},
            { "tip.next", "\u0432\u043f\u0435\u0440\u0435\u0434"},
            { "tip.history", "\u0438\u0441\u0442\u043e\u0440\u0438\u044f"},
            { "tip.print", "\u043f\u0435\u0447\u0430\u0442\u044c"},
	    { "tip.pageSetup", "\u043f\u0430\u0440\u0430\u043c\u0435\u0442\u0440\u044b \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b"},
            { "tip.reload", "\u043e\u0431\u043d\u043e\u0432\u0438\u0442\u044c"}
       };
    }
}
