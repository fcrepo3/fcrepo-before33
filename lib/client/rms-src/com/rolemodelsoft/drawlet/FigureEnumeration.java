package com.rolemodelsoft.drawlet;

/**
 * @(#)FigureEnumeration.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

/**
 * An object that implements the FigureEnumeration interface generates a
 * series of Figures, one at a time. Successive calls to the 
 * <code>nextFigure</code> method return successive Figures of the 
 * series.  This is just like a standard Enumeration, except we know we are dealing
 * with Figures and therefore users don't have to do any casting.
 * <p>
 * For example, to print all Figures of a vector <i>v</i>:
 * <blockquote><pre>
 *     for (FigureEnumeration e = MyFigureEnumerator(v); e.hasMoreElements() ;) {
 *         System.out.println(e.nextElement());<br>
 *     }
 * </pre></blockquote>
 * <p>
 *
 * @see     java.util.Enumeration
 *
 * @version 	1.1.6, 12/28/98
 * @since   JDK1.1
 */
 
public interface FigureEnumeration {
	/**
	 * Tests if this enumeration contains more figures.
	 *
	 * @return  boolean value of <code>true</code> if this enumeration contains more figures;
	 *          <code>false</code> otherwise.
	 * @since   JDK1.1
	 */
	boolean hasMoreElements();
	/**
	 * Returns the next Figure of this enumeration.
	 *
	 * @return     the Figure which is the next element of this enumeration. 
	 * @exception  NoSuchElementException  if no more elements exist.
	 * @since      JDK1.1
	 */
	Figure nextElement();
}
