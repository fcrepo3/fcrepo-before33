package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)AbstractFigure.java
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
 
import com.rolemodelsoft.drawlet.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import com.rolemodelsoft.drawlet.util.Duplicatable;

/**
 * This provides basic default functionality for Figures that are assumed to be
 * movable but not necessarily reshapable, and that have observers that want to
 * know when their location changes.  It provides very basic functionality for most operations
 * and forces concrete subclasses to define, at a minimum:
 *	paint(Graphics);
 *	getBounds();
 *	basicTranslate(int,int);
 *
 * @version 	1.1.6, 12/28/98
 */
 
public abstract class AbstractFigure extends AbstractPaintable implements Figure {
	/**
	 * The <code>Figure's</code> listeners.
	 */
	protected transient Vector listeners;

	/**
	 * The <code>Figure's</code> location listeners.
	 */
	protected Vector locationListeners;
	/**
	 * Add a PropertyChangeListener to the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be added
	 */

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
		    listeners = new Vector();
		}
		if ( ! listeners.contains( listener ) ) {
			listeners.addElement(listener);
		}
	}
	/**
	 * Add a RelatedLocationListener to the listener list.
	 *
	 * @param listener  The RelatedLocationListener to be added
	 */

	public synchronized void addRelatedLocationListener(RelatedLocationListener listener) {
		if (locationListeners == null) {
		    locationListeners = new Vector();
		}
		if ( ! locationListeners.contains( listener ) ) {
			locationListeners.addElement(listener);
		}
	}
	/** 
	 * Moves the Figure to a new location. The x and y coordinates
	 * are in the parent's coordinate space.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #location
	 * @see #reshape
	 */
	protected void basicMove(int x, int y) {
		basicTranslate(x - getLeft(), y - getTop());
	}
	/** 
	 * Moves the Figure in the x and y direction.
	 * Subclasses should probably provide synchronized versions if they're 
	 * modifying attributes of the receiver.
	 *
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 * @see #location
	 * @see #translate
	 */
	protected abstract void basicTranslate(int x, int y);
	/**
	 * Denote that location changed.
	 *
	 * @param point the old location.
	 */
	protected void changedLocation(Point oldPoint) {
		Point newPoint = getLocation();
		if (oldPoint != null && oldPoint.equals(newPoint)) {
		    return;
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this,
						    LOCATION_PROPERTY, oldPoint, newPoint);
		fireLocationChange(evt);
		firePropertyChange(evt);
	}
	/**
	 * Denote that the shape changed. Assume that
	 * the shape has changed even if the bounds haven't.
	 * 
	 * @param oldBounds the old bounds.
	 */
	protected void changedShape(Rectangle oldBounds) {
		Rectangle newBounds = getBounds();
		PropertyChangeEvent evt = new PropertyChangeEvent(this,
						    SHAPE_PROPERTY, oldBounds, newBounds);
		fireShapeChange(evt);
		firePropertyChange(evt);
	}
	/**
	 * Denote that size changed.
	 * @param oldDimension the old dimensions.
	 */
	protected void changedSize(Dimension oldDimension) {
		Dimension newDimension = getSize();
		if (oldDimension != null && oldDimension.equals(newDimension)) {
		    return;
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this,
						    SIZE_PROPERTY, oldDimension, newDimension);
		fireSizeChange(evt);
		firePropertyChange(evt);
	}
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * Figure. <code>x</code> and <code>y</code> are defined to be relative to the 
	 * coordinate system of this figure.
	 * By default, just check if it is within the receiver's bounds.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified x,y
	 * location is "inside" this Figure;
	 * 			<code>false</code> otherwise.
	 * @see #isWithin
	 */
	public boolean contains(int x, int y) {
		Rectangle bigBounds = new Rectangle(getBounds().x, getBounds().y, getBounds().width + 1, getBounds().height + 1);
		return bigBounds.contains(x, y);
	}
	/**  
	 * Checks whether the specified <code>Figure</code> is "inside" this
	 * <code>Figure</code>. The <code>Figures</code> are assumed to share
	 * the same coordinate system.
	 * By default, just check if it is within the receiver's bounds.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param figure the Figure to test for inclusion
	 * @return	boolean value of <code>true</code> if the specified Figure
	 * is completely "inside" this Figure;
	 * 			<code>false</code> otherwise.
	 * @see #isWithin
	 */
	public boolean contains(Figure figure) {
		return contains(figure.getBounds());
	}
	/**  
	 * Checks whether a specified Rectangle is "inside" this Figure, 
	 * where the Rectangle and this Figure are in the same coordinate system  
	 * By default, just check if its topLeft and bottomRight is inside the receiver.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param box the rectangle to test for inclusion
	 * @return	boolean value of <code>true</code> if the specified Rectangle
	 * is "inside" this Figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(Rectangle box) {
	Rectangle bigBounds = new Rectangle(getBounds().x, getBounds().y, getBounds().width + 1, getBounds().height + 1);
	return bigBounds.contains(box.x,box.y) && bigBounds.contains(box.x + box.width, box.y + box.height);
	}
	/**
	 * Deletes all location listeners.
	 * Though this is public, caution should be used before anything other
	 * than "this" does.
	 */
	protected synchronized void deleteLocationListeners() {
		locationListeners = null;
	}
	/**
	 * Called to allow the <code>Figure</code> to respond to being disconnected.
	 * Should clean up as appropriate if the figure is no longer valid.
	 */
	public void disconnect() {
		fireRelationChange(new PropertyChangeEvent(this,RELATION_PROPERTY,null,null));
		deleteLocationListeners();
	}
	/**
	 * Duplicates the receiver.  Copy non-transient observers... let postDuplicate resolve
	 * whether observers have also been copied.
	 * 
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicate() {
		Hashtable duplicates = new Hashtable(estimatedDuplicateSize());
		Duplicatable dup = (Duplicatable)duplicateIn(duplicates);
		dup.postDuplicate(duplicates);
		return dup;
	}
	/**
	 * Duplicates the receiver into the given <code>Hashtable</code>.
	 * Copy non-transient observers... let postDuplicate resolve
	 * whether observers have also been copied.
	 * 
	 * @param duplicates the Hashtable to put the new duplicate in
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		try { 
		    AbstractFigure clone = (AbstractFigure)clone();
			clone.deleteLocationListeners();
		    if (locationListeners != null) {
		    	for (Enumeration e = relatedLocationListeners(); e.hasMoreElements(); ) 
					clone.addRelatedLocationListener((RelatedLocationListener)e.nextElement());
		    }
		    duplicates.put(this,clone);
	    	return clone;
		} catch (CloneNotSupportedException e) { 
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
	}
	/** 
	 * Answers a Handle that will provide 
	 * editing capabilities on the receiver, or null.
	 * By default, answer null.
	 * Subclasses may wish to provide something more meaningful.
	 *
	 * @param x the x coordinate to potentially begin editing
	 * @param y the y coordinate to potentially begin editing
	 * @return	a Handle that will provide default capabilities
	 * on the receiver, or null if there is no default
	 */
	public Handle editTool(int x, int y)  {
		return null;
	}
	/**
	 * Answers the expected number of significant duplicates when duplicating the receiver.
	 * Subclasses may wish to override.
	 * 
	 * @returns an integer estimate of the number of objects to be duplicated 
	 */
	protected int estimatedDuplicateSize() {
		return 1;
	}
	/**
	 * Answer the figure, if any, associated with the locator.
	 * This can be a useful utility to determine whether or not a particular
	 * locator is tied to some figure.
	 *
	 * @param aLocator the Locator to mine for figures
	 * @return	the Figure associated with the locator, or null if none
	 */
	public static Figure figureFromLocator(Locator aLocator) {
		if (aLocator instanceof FigureHolder) {
			return ((FigureHolder)aLocator).getFigure();
		}
		if (aLocator instanceof RelativeLocator) {
			return figureFromLocator(((RelativeLocator)aLocator).getBase());
		}
		return null;
	}
	/**
	 * Denote that location changed.
	 *
	 * @param event
	 */
	protected void fireLocationChange(PropertyChangeEvent event) {
		java.util.Vector targets;
		synchronized (this) {
			if (locationListeners == null) {
				return;
			}
			targets = (java.util.Vector) locationListeners.clone();
		}
		for (int i = 0; i < targets.size(); i++) {
			RelatedLocationListener target = (RelatedLocationListener) targets.elementAt(i);
			target.locationChanged(event);
		}
	}
	/**
	 * Report a property update to any registered listeners.
	 *
	 * @param event the <code>PropertyChangeEvent</code> to report.
	 */
	protected void firePropertyChange(PropertyChangeEvent event) {
		java.util.Vector targets;
		synchronized (this) {
			if (listeners == null) {
				return;
			}
			targets = (java.util.Vector) listeners.clone();
		}
		for (int i = 0; i < targets.size(); i++) {
			PropertyChangeListener target = (PropertyChangeListener) targets.elementAt(i);
			target.propertyChange(event);
		}
	}
	/**
	 * Report a property update to any registered listeners.
	 * No event is fired if old and new are equal and non-null.
	 *
	 * @param propertyName  The programmatic name of the property
	 *		that was changed.
	 * @param oldValue  The old value of the property.
	 * @param newValue  The new value of the property.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (oldValue != null && oldValue.equals(newValue)) {
			return;
		}
		java.util.Vector targets;
		synchronized (this) {
			if (listeners == null) {
				return;
			}
			targets = (java.util.Vector) listeners.clone();
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		for (int i = 0; i < targets.size(); i++) {
			PropertyChangeListener target = (PropertyChangeListener) targets.elementAt(i);
			target.propertyChange(evt);
		}
	}
	/**
	 * Report that the relation has changed.
	 *
	 * @param event the actual <code>PropertyChangeEvent</code> to report.
	 */
	protected void fireRelationChange(PropertyChangeEvent event) {
		java.util.Vector targets;
		synchronized (this) {
			if (locationListeners == null) {
				return;
			}
			targets = (java.util.Vector) locationListeners.clone();
		}
		for (int i = 0; i < targets.size(); i++) {
			RelatedLocationListener target = (RelatedLocationListener) targets.elementAt(i);
			target.relationChanged(event);
		}
	}
	/**
	 * Report that the shape changed.
	 *
	 * @param event the actual <code>PropertyChangeEvent</code> to report.
	 */
	protected void fireShapeChange(PropertyChangeEvent event) {
		java.util.Vector targets;
		synchronized (this) {
			if (locationListeners == null) {
				return;
			}
			targets = (java.util.Vector) locationListeners.clone();
		}
		for (int i = 0; i < targets.size(); i++) {
			RelatedLocationListener target = (RelatedLocationListener) targets.elementAt(i);
			target.shapeChanged(event);
		}
	}
	/**
	 * Report that the size changed.
	 *
	 * @param event the actual <code>PropertyChangeEvent</code> to report.
	 */
	protected void fireSizeChange(PropertyChangeEvent event) {
		java.util.Vector targets;
		synchronized (this) {
			if (locationListeners == null) {
				return;
			}
			targets = (java.util.Vector) locationListeners.clone();
		}
		for (int i = 0; i < targets.size(); i++) {
			RelatedLocationListener target = (RelatedLocationListener) targets.elementAt(i);
			target.sizeChanged(event);
		}
	}
	/** 
	 * Returns the current rectangular area covered by this figure.
	 * 
	 * @return	a Rectangle representing the current rectangular area
	 * covered by this figure
	 */
	public abstract Rectangle getBounds();
	/** 
	 * Answer the handles associated with the receiver.
	 * By default, there are none.
	 * Subclasses may wish to provide other handles that may allow for editing
	 * the receiver in some way.
	 * 
	 * @return	an array of the Handles associated with the receiver
	 */
	public Handle[] getHandles() {
		return new Handle[0];
	}
	/** 
	 * Answer a point indicating the location of the receiver... typically the topLeft.
	 * NOTE: This may not correspond to the point indicated by getLocator() as this method
	 * is often used to determine the position before a Locator has already been affected.
	 * 
	 * @return	a Point indicating the location of the receiver
	 */
	protected Point getLocation() {
		Rectangle myBounds = getBounds();
		return new java.awt.Point(myBounds.x,myBounds.y);
	}
	/** 
	 * Returns the current locator of this figure.
	 * This may or may not represent the top left of the receiver's area.
	 * By default, it does.
	 * Subclasses may wish to provide something more meaningful.
	 * 
	 * @return	the current Locator of this figure
	 */
	public Locator getLocator() {
		return new DrawingPoint(getLeft(), getTop());
	}
	/** 
	 * Answer the style which defines how to paint the figure.
	 * 
	 * @return	the DrawingStyle which defines how to paint the figure
	 */
	public DrawingStyle getStyle() {
		return new SimpleDrawingStyle();
	}
	/** 
	 * Answers whether the receiver intersects another figure.
	 * By default, just check if the bounds intersect.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param anotherFigure the figure the receiver is potentially intersecting.
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified figure;
	 * 			<code>false</code> otherwise.
	 * @see #bounds
	 */
	public boolean intersects(Figure anotherFigure) {
	return intersects(anotherFigure.getBounds());
	}
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 * By default, just check if the bounds intersects.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified Rectangular area;
	 * 			<code>false</code> otherwise.
	 * @see #bounds
	 */
	public boolean intersects(Rectangle box) {
		Rectangle bigBounds = new Rectangle(getBounds().x, getBounds().y, getBounds().width + 1, getBounds().height + 1);
		Rectangle bigBox = new Rectangle(box.x, box.y, box.width + 1, box.height + 1);
		return bigBounds.intersects(bigBox);
	}
	/** 
	 * Answers whether the receiver is obsolete
	 * True if some event has happened that makes this a meaningless object.
	 * 
	 * @return	boolean value of <code>true</code> if the receiver is obsolete;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isObsolete() {
	return false;
	}
	/** 
	 * Answers whether the receiver is fully within another Figure.
	 * By default, we ask the other figure if the receiver is inside it.  
	 * Subclasses may want to do something more sophisticated.
	 *
	 * @param anotherFigure the figure the receiver is potentially inside.
	 * @return	boolean value of <code>true</code> if the receiver is fully within
	 * the Figure specified;
	 * 			<code>false</code> otherwise.
	 * @see #inside
	 */
	public boolean isWithin(Figure anotherFigure) {
	return anotherFigure.contains(this);
	}
	/** 
	 * Answers whether the receiver is fully within a Rectangular area.
	 * By default, just check if the topLeft and bottomRight are inside the area.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver is fully within
	 * the Rectangle specified;
	 * 			<code>false</code> otherwise.
	 * @see #left
	 * @see #top
	 * @see #right
	 * @see #bottom
	 */
	public boolean isWithin(Rectangle box) {
		return (box.contains(getLeft(), getTop())) && (box.contains(getRight(), getBottom()));
	}
	/** 
	 * Answers a locator corresponding to a significant point on the receiver.
	 * By default, answer a point with the same relative position as the x and y
	 * are at the time of the request.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return	a Locator corresponding to a significant point on the receiver
	 */
	public Locator locatorAt(int x, int y) {
		double relativeX = ((double)x - (double)getLeft()) / (double)getWidth();
		double relativeY = ((double)y - (double)getTop()) / (double)getHeight();
		return new FigureRelativePoint(this, relativeX, relativeY);
	}
	/** 
	 * Moves the Figure to a new location. The x and y coordinates
	 * are in the parent's coordinate space.
	 * Let observers know what changed.
	 * This is a TemplateMethod with hooks:
	 * 	resetLocationCache()
	 * 	basicMove()
	 *  changedLocation()
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #location
	 */
	public void move(int x, int y)  {
		Point oldLocation = getLocation();
		basicMove(x,y);
		resetLocationCache();
		changedLocation(oldLocation);
	}
	/** 
	 * Moves the Figure to a new location.
	 * Note: Subclasses may wish to update dependencies based on this new location
	 *
	 * @param locator the Locator which identifies the desired x, y coordinates.
	 * @see #getLocator
	 */
	public void move(Locator locator) {
		move(locator.x(),locator.y());
	}
	/** 
	 * Paints the figure.
	 *
	 * @param g the specified Graphics window
	 */
	public abstract void paint(Graphics g);
	/**
	 * After a series of Figures are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes they might like to reconcile.  In this case,
	 * get rid of observers and replace them with any duplicates available.
	 *
	 * @param duplicates a Hashtable where originals as keys and duplicates as elements
	 */
	public void postDuplicate(Hashtable duplicates) {
		Enumeration e = relatedLocationListeners();
		deleteLocationListeners();
		while ( e.hasMoreElements() ) {
			RelatedLocationListener duplicate = (RelatedLocationListener)duplicates.get(e.nextElement());
			if (duplicate != null)
				addRelatedLocationListener(duplicate);
		}
	}
	/**
	 * Answer with an enumeration over the RelatedLocationListeners.
	 */

	public Enumeration relatedLocationListeners() {
		if (locationListeners == null)
			return (new Vector()).elements();
		return locationListeners.elements();
	}
	/**
	 * Remove the PropertyChangeListener from the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be removed
	 */

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
		    return;
		}
		listeners.removeElement(listener);
	}
	/**
	 * Remove the RelatedLocationListener from the listener list.
	 *
	 * @param listener  The RelatedLocationListener to be removed
	 */

	public synchronized void removeRelatedLocationListener(RelatedLocationListener listener) {
		if (locationListeners == null) {
			return;
		}
		locationListeners.removeElement(listener);
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other figure.
	 * By default, make it the middle of the receiver.
	 * Subclasses may wish to do something more meaningful.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
		if (requestor == this)
			return null;
		return new FigureRelativePoint(this,0.5,0.5);
	}
	/**
	 * Flush caches with respect to determining location.  This is a hook method.
	 * Subclasses may wish to override.
	 */
	protected void resetLocationCache() {
	}
	/** 
	 * Reshapes the Figure to the specified bounding box.
	 * By default, ignore width and height.  Subclasses may wish to override.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 * @see #move
	 */
	public void setBounds(int x, int y, int width, int height) {
		move(x,y);
	}
	/**
	 * Resizes the receiver to the specified width and height.
	 * By default, do nothing.  Subclasses may wish to override.
	 *
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	public void setSize(int width, int height)  {
	}
	/** 
	 * Resizes the receiver to the specified dimension.
	 *
	 * @param d the figure dimension
	 * @see #getSize
	 * @see #setBounds
	 */
	public void setSize(Dimension d)  {
		setSize(d.width, d.height);
	}
	/** 
	 * Set the style defining how to paint the receiver.
	 * The default is to ignore the style.
	 * Most subclasses will want to do something more useful.
	 *
	 * @param style the specified DrawingStyle
	 */
	public void setStyle(DrawingStyle style) {
		firePropertyChange(STYLE_PROPERTY,this.getStyle(),style);
	}
	/** 
	 * Moves the Figure in the x and y direction.
	 * Let observers know what changed.
	 * This is a TemplateMethod with hooks:
	 * 	resetLocationCache()
	 * 	basicTranslate()
	 *  changedLocation()
	 *
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 * @see #location
	 */
	public void translate(int x, int y) {
		Point oldLocation = getLocation();
		basicTranslate(x, y);
		resetLocationCache();
		changedLocation(oldLocation);
	}
}
