package com.rolemodelsoft.drawlet.util;

/**
 * @(#)ReverseVectorEnumerator.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996 Knowledge Systems Corporation (KSC). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS AND KSC MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. NEITHER RMS NOR KSC SHALL BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
import java.util.Enumeration;
import java.util.Vector;
import java.util.NoSuchElementException;

/**
 * This provides an Enumeration of a Vector that goes through
 * the Vector in reverse order.
 *
 * @version 	1.1.6, 12/30/98
 */
public class ReverseVectorEnumerator implements Enumeration {
	/**
	 * The Vector to enumerate over.
	 */
	Vector vector;

	/**
	 * Stores the current index into the Vector.
	 */
	int count;
	/** 
	 * Answer an instance prepared to iterate over a Vector.
	 *
	 * @param v the Vector over which to enumerate.
	 */
	public ReverseVectorEnumerator(Vector v) {
		vector = v;
		count = v.size() - 1;
	}
	/**
	 * Answers whether there are more elements in the vector.
	 *
	 * @return a boolean value of true if the enumeration
	 * contains more elements; false if it is empty.
	 */
	public boolean hasMoreElements() {
		return count >= 0;
	}
	/**
	 * Returns the next element of the enumeration. Calls to this
	 * method will enumerate successive elements.
	 *
	 * @exception NoSuchElementException If no more elements exist.
	 * @returns the next object in the Vector.
	 */
	public Object nextElement() {
		synchronized (vector) {
			if (count >= 0) {
				return vector.elementAt(count--);
			}
		}
		throw new NoSuchElementException("ReverseVectorEnumerator");
	}
}
