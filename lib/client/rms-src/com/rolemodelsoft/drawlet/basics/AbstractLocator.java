package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)AbstractLocator.java
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
import java.util.Hashtable;
/**
 * Provides a default implementation of <code>Locator</code>.
 */
public abstract class AbstractLocator implements Locator {
/**
 * Creates a new AbstractLocator.
 */
public AbstractLocator() {
	super();
}
	/**
	 * Duplicates the receiver.
	 * 
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicate() {
		try { 
		    return clone();
		} catch (CloneNotSupportedException e) { 
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
	}
	/**
	 * Duplicates the receiver into the given <code>Hashtable</code>.
	 * 
	 * @param duplicates the Hashtable to put the new duplicate in
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		Object dup = duplicate();
		duplicates.put(this,dup);
		return dup;
	}
	/**
	 * After a series of Objects are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.
	 * In this case, do nothing.
	 *
	 * @param duplicates a Hashtable with originals as keys and duplicates
	 * as elements
	 */
	public void postDuplicate(Hashtable duplicates) {
	}
	/** 
	 * Answer the radius of the Locator (as a PolarCoordinate).
	 * 
	 * @return	an integer representing the radius
	 */
	public int r() {
		int myX = x();
		int myY = y();
		return (int)Math.sqrt((myX * myX) + (myY * myY));
	}
	/** 
	 * Answer the angle in radians of the Locator (as a PolarCoordinate).
	 * 
	 * @return double representing theta
	 */
	public double theta() {
		double theta = Math.atan((double)y()/(double)x());
		if (x() < 0)
			theta = theta + Math.PI;
		return theta;
	}
/**
 * Returns a String that represents the value of this object.
 *
 * @return a string representation of the receiver
 */
public String toString() {
	return getClass().getName() + " [x=" + x() + ",y=" + y() + ";r=" + r() + ",theta=" + theta() + "]";
}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public abstract int x();
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public abstract int y();
}
