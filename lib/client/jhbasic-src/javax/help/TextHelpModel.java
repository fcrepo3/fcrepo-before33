/*
 * @(#) TextHelpModel.java 1.4 - last change made 01/29/99
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

package javax.help;

import javax.help.event.*;

/**
 * The interface to a HelpModel that manipulates text.
 *
 * It provides additional text operations.
 */

public interface TextHelpModel extends HelpModel {
    /**
     * Gets the title of the document.
     *
     * @return The title of document visited.
     */
    public String getDocumentTitle();

    /**
     * Sets the title of the document.
     * A property change event is generated.
     *
     * @param title The title currently shown.
     */
    public void setDocumentTitle(String title);


    /**
     * Removes all highlights on the current document.
     */
    public void removeAllHighlights();

    /**
     * Adds a highlight to a range of positions in a document.
     *
     * @param pos0 Start position.
     * @param pos1 End position.
     */
    public void addHighlight(int pos0, int pos1);

    /**
     * Sets the highlights to be a range of positions in a document.
     *
     * @param h The array of highlight objects.
     */
    public void setHighlights(Highlight[] h);

    /**
     * Gets all highlights.
     */
    public Highlight[] getHighlights();

    /**
     * Adds a listener for a TextHelpModel.
     */
    public void addTextHelpModelListener(TextHelpModelListener l);

    /**
     * Removes a listener for a TextHelpModel.
     */
    public void removeTextHelpModelListener(TextHelpModelListener l);

    /**
     * This is very similar to javax.swing.text.Highlighter.Highlight
     * except that it does not use the notion of HighlightPainter.
     */
    public interface Highlight {
	/**
	 * Gets the starting model offset of the highlight.
	 *
	 * @return The starting offset >= 0.
	 */
	public int getStartOffset();

	/**
	 * Gets the ending model offset of the highlight.
	 *
	 * @return The ending offset >= 0.
	 */
	public int getEndOffset();
    }
}
