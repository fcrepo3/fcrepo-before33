package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)FigureVectorEnumerator.java
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

import com.rolemodelsoft.drawlet.*;
import java.util.*;

/**
 * @version 	1.1.6, 12/28/98
 */

 public class FigureVectorEnumerator implements FigureEnumeration {
	/**
	 * The vector to enumerate over.
	 */
	Vector vector;

	/**
	 * The current index into the vector.
	 */
	int count;
	/**
	 * Create a FigureEnumeration over the Vector of Figures
	 * 
	 * @param v the vector
	 */
	public FigureVectorEnumerator(Vector v) {
		vector = v;
		count = 0;
	}
	/**
	 * Tests if this enumeration contains more figures.
	 *
	 * @return  boolean value of <code>true</code> if this
	 * enumeration contains more figures;
	 *          <code>false</code> otherwise.
	 */
	public boolean hasMoreElements() {
		return count < vector.size();
	}
	/**
	 * Returns the next figure of this enumeration.
	 *
	 * @return     the next Figure of this enumeration. 
	 * @exception  NoSuchElementException  if no more elements exist.
	 */
	public Figure nextElement() {
		synchronized (vector) {
		    if (count < vector.size()) {
				return (Figure)vector.elementAt(count++);
		    }
		}
		throw new NoSuchElementException("VectorEnumerator");
	}
}
