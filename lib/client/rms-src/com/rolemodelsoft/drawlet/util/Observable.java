package com.rolemodelsoft.drawlet.util;

/**
 * @(#)Observable.java
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
 
/**
 * This interface defines a generic Observable interface.
 * This is desirable as the java.util.Observable is a class which
 * could force unnatural hierarchies.  It also allows public access
 * to a list of Observers as an array which may not be the actual
 * collection of observers, allowing implementers to let others know
 * what they are observing without allowing them to modify that list
 * through "illegal" channels.
 *
 * @version 	1.1.6, 12/30/98
 */
public interface Observable {
	/**
	 * Adds an observer to the observer list.
	 *
	 * @param observer the observer to be added.
	 */
	public void addObserver(Observer observer);
	/**
	 * Deletes an observer from the observer list.
	 *
	 * @param observer the observer to be deleted.
	 */
	public void deleteObserver(Observer observer);
	/**
	 * Deletes all observers from the observer list.
	 * Though this is public, caution should be used before anything other
	 * than "this" invokes it.
	 */
	public void deleteObservers();
	/** 
	 * Answers a collection of observers.
	 *
	 * @return an array of Observers.
	 */
	public Observer[] getObservers();
	/**
	 * Notifies all observers that an observable change occurs.
	 * Though this is public, caution should be used before anything other
	 * than "this" does.
	 */
	public void notifyObservers();
	/**
	 * Notifies all observers that an observable change occurred.
	 * Though this is public, caution should be used before anything other
	 * than "this" invokes it.
	 *
	 * @param arg info to pass along to those being notified.
	 */
	public void notifyObservers(Object arg);
}
