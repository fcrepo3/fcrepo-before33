package com.rolemodelsoft.drawlet.util;

/**
 * @(#)Duplicatable.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.util.Hashtable;

/**
 * This interface defines a simple interface for objects that can be duplicated.
 * This would typically be used for copy/cut/paste type operations.  
 * The intended use is:
 *    1. One or more objects are sent duplicate().
 *    2. A Hashtable is built with each of the original objects as keys and their
 *       duplicates as corresponding values.
 *    3. Each of the duplicates are sent postDuplicate(Hashtable), giving them
 *       the opportunity to resolve any changes they might want to make.
 * E.g. Object A points to Object B, Object A' (the result of duplicate())
 * still points to Object B.  When Object A' receives postDuplicate(), it could
 * make a change to point to Object B', or some other object if B' is not available.
 *
 * @version 	1.1.6, 12/30/98
 */
public interface Duplicatable extends Cloneable {

	/**
	 * Answers a duplicate of this object.
	 * This is intended for copy/cut/paste operations, hence may be different
	 * than what you would like to do for other "cloning" type operations.
	 *
	 * @param Object the duplicate.
	 */
	public abstract Object duplicate();
	/**
	 * Answers a duplicate of this object.  While doing so, place the original
	 * as a key and the duplicate as a value into the duplicates HashTable.
	 * Implementers may also wish to place duplicates of components in the HashTable
	 * if they may be significant when resolving pointers after a group of objects
	 * have been duplicated.
	 * This is intended for copy/cut/paste operations, hence may be different
	 * than what you would like to do for other "cloning" type operations.
	 *
	 * @param duplicates the table which will be used to map the originals to the duplicates.
	 * @param Object the duplicate.
	 * @see #postDuplicate
	 */
	public abstract Object duplicateIn(Hashtable duplicates);
	/**
	 * After a series of objects are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.  
	 * For example, replacing observers with their duplicates, if available.
	 * 
	 * @param duplicates a Hashtable with originals as keys and duplicates as elements.
	 */
	public abstract void postDuplicate(Hashtable duplicates);
}
