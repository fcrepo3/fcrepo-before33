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
 * @(#) Constants_zh.java 1.2 - last change made 11/16/99
 */

package javax.help.resources;

import java.util.ListResourceBundle;

/**
 * Constants used for localizing JavaHelp.
 *
 * These are in the form of key, value.
 * Translators take care to only translate the values.
 */

public class Constants_zh extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle.
     */
    public Object[][] getContents() {
        return new Object[][] {

	    //  Constant strings and patterns
	    { "helpset.oneMapOnly",
		  "NYI\uff1aJH 1.0 \u53ea\u63a5\u53d7\u4e00\u4e2a\u6620\u5c04\u6570\u636e"},
	    { "helpset.wrongPublicID",
		  "\u672a\u77e5\u7684 PublicID {0}"},
	    { "helpset.wrongTitle",
		  "\u8bd5\u56fe\u5c06\u6807\u9898\u8bbe\u7f6e\u4e3a {0}\uff0c\u4f46\u6807\u9898\u5df2\u8d4b\u503c\u4e3a {1}\u3002"},
	    { "helpset.wrongHomeID",
		  "\u8bd5\u56fe\u5c06 homeID \u8bbe\u7f6e\u4e3a {0} \u4f46 homeID \u5df2\u8d4b\u503c\u4e3a {1}\u3002"},
	    { "helpset.subHelpSetTrouble",
		  "\u521b\u5efa subhelpset \u65f6\u51fa\u9519\uff1a{0}\u3002"},
	    { "helpset.malformedURL",
		  "\u683c\u5f0f\u9519\u8bef\u7684 URL\uff1a{0}\u3002"},
	    { "helpset.incorrectURL",
		  "\u9519\u8bef\u7684 URL\uff1a{0}\u3002"},
	    { "helpset.wrongText",
		  "{0} \u4e0d\u80fd\u5305\u542b\u6587\u672c {1}\u3002"},
	    { "helpset.wrongTopLevel",
		  "{0} \u4e0d\u80fd\u4e3a\u9876\u7ea7\u6807\u8bb0\u3002"},
	    { "helpset.wrongParent",
		  "{0} \u7684\u7236\u6807\u8bb0\u4e0d\u80fd\u4e3a {1}\u3002"},
	    { "helpset.unbalanced",
		  "\u4e0d\u5747\u8861\u7684\u6807\u8bb0 {0}\u3002"},
	    { "helpset.wrongLocale",
		  "\u8b66\u544a\uff1axml:lang \u7279\u6027 {0} \u4e0e\u7f3a\u7701\u503c {1} \u53ca\u7f3a\u7701\u503c {2} \u51b2\u7a81\u3002"},
	    { "helpset.unknownVersion",
		  "\u672a\u77e5\u7248\u672c {0}\u3002"},

		// IndexView messages
	    { "index.invalidIndexFormat",
		  "\u8b66\u544a\uff1a\u65e0\u6548\u7684\u7d22\u5f15\u683c\u5f0f"},
	    { "index.unknownVersion",
		  "\u672a\u77e5\u7248\u672c {0}\u3002"},

		// TOCView messages
	    { "toc.wrongPublicID",
		  "\u672a\u77e5 PublicID {0}"},
	    { "toc.invalidTOCFormat",
		  "\u8b66\u544a\uff1a\u65e0\u6548\u7684\u76ee\u5f55\u683c\u5f0f"},
	    { "toc.unknownVersion",
		  "\u672a\u77e5\u7248\u672c {0}\u3002"},

		// Map messages
	    { "map.wrongPublicID",
		  "\u672a\u77e5 PublicID {0}"},
	    { "map.invalidMapFormat",
		  "\u8b66\u544a\uff1a\u65e0\u6548\u7684\u6620\u5c04\u683c\u5f0f"},
	    { "map.unknownVersion",
		  "\u672a\u77e5\u7248\u672c {0}\u3002"},

	    // GUI components
	    // Labels
	    { "index.findLabel", "\u67e5\u627e\uff1a"},
	    { "search.findLabel", "\u67e5\u627e\uff1a"},
	    { "search.hitDesc", "\u6587\u6863\u4e2d\u51fa\u73b0\u7684\u6b21\u6570"},
	    { "search.qualityDesc", "\u6587\u6863\u7684\u6700\u4f4e\u8865\u507f\u503c" },
	    { "search.high", "\u9ad8"},
	    { "search.midhigh", "\u8f83\u9ad8"},
	    { "search.mid", "\u4e2d\u7b49"},
	    { "search.midlow", "\u8f83\u4f4e"},
	    { "search.low", "\u4f4e"},
	    // ToolTips
	    { "tip.previous", "\u4e0a\u4e00\u9875"},
	    { "tip.next", "\u4e0b\u4e00\u9875"},
	    { "tip.history", "\u5386\u53f2\u8bb0\u5f55"},
	    { "tip.print", "\u6253\u5370"},
	    { "tip.pageSetup", "\u9875\u8bbe\u7f6e"},
	    { "tip.reload", "\u91cd\u65b0\u88c5\u8f7d"}
       };
    }
}

