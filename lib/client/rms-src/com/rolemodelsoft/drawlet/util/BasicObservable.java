package com.rolemodelsoft.drawlet.util;

/**
 * @(#)BasicObservable.java
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
import java.util.Enumeration;
import java.util.Vector;
import java.io.Serializable;

/**
 * This provides basic default functionality for Observables that maintain and notify their
 * Observers.  It is an abstract class as there is nothing to observe until subclasses provide
 * some content.  Note that the list of observers is serializable.
 *
 * @version 	1.1.6, 12/30/98
 */
public abstract class BasicObservable implements Observable, Serializable {

	/**
	 * The list of Observers.  Could be null, a single Observer, or a
	 * Vector of Observers.
	 */
	protected Object observerList;

	/**
	 * Adds an observer to the observer list.
	 *
	 * @param observer the observer to be added.
	 */
	public synchronized void addObserver(Observer observer) {
		if (observerList != null) {
			if (observerList instanceof Vector) {
				if (!((Vector) observerList).contains(observer)) {
					((Vector) observerList).addElement(observer);
				}
			} else
				if (observerList != observer) {
					Vector tmp = new Vector();
					tmp.addElement(observerList);
					tmp.addElement(observer);
					observerList = tmp;
				}
		} else {
			observerList = observer;
		}
	}
	/**
	 * Deletes an observer from the observer list.
	 *
	 * @param observer the observer to be deleted
	 */
	public synchronized void deleteObserver(Observer observer) {
		if (observerList == observer) {
			observerList = null;
		} else
			if (observerList != null && observerList instanceof Vector) {
				((Vector) observerList).removeElement(observer);
			}
	}
	/**
	 * Deletes all observers from the observer list.
	 * Though this is public, caution should be used before anything other
	 * than "this" invokes it.
	 */
	public synchronized void deleteObservers() {
		observerList = null;
	}
	/** 
	 * Answers the array of current Observers.
	 *
	 * @return an array of Observers.
	 */
	public Observer[] getObservers() {
		Observer observers[] = new Observer[0];
		if (observerList != null) {
			if (observerList instanceof Vector) {
				Vector v = (Vector)observerList;
				observers = new Observer[v.size()];
				v.copyInto(observers);
	 	   	} 
			else {
				observers = new Observer[1];
				observers[0] = (Observer)observerList;
			}
		}
		return observers;
	}
	/**
	 * Notifies a particular observer that a change in the receiver occurred.
	 *
	 * @param observer the particular observer to be notified.
	 * @param arg info to pass along to the observer being notified.
	 */
	protected void notifyObserver(Object observer, Object arg)  {
		((Observer)observer).update(this, arg);
	}
	/**
	 * Notifies all observers that an observable change occurred.
	 * Though this is public, caution should be used before anything other
	 * than "this" invokes it.
	 */
	public void notifyObservers()  {
		notifyObservers(null);
	}
	/**
	 * Notifies all observers that an observable change occurs.
	 * Though this is public, caution should be used before anything other
	 * than "this" invokes it.
	 *
	 * @param arg info to pass along to those being notified.
	 */
	public synchronized void notifyObservers(Object arg) {
		if (observerList != null) {
			if (observerList instanceof Vector)
				for (Enumeration e = new ReverseVectorEnumerator((Vector) observerList); e.hasMoreElements();)
					notifyObserver(e.nextElement(), arg);
				else
					notifyObserver(observerList, arg);
		}
	}
}
