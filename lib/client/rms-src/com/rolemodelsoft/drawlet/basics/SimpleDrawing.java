package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)SimpleDrawing.java
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
import com.rolemodelsoft.drawlet.util.*;
import java.awt.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * This provides basic functionality necessary to provide a meaningful
 * working version of a Drawing.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class SimpleDrawing extends AbstractPaintable implements Drawing {
	static final long serialVersionUID = -6724809038122913127L;

	/**
	 * The figures which appear on the canvas.
	 */
	protected Vector figures = defaultFigures();

	/**
	 * The width of the drawing.
	 */
	protected int width = defaultWidth();

	/**
	 * The height of the drawing.
	 */
	protected int height;

	/**
	 * The property change listeners.
	 */
	transient protected Vector listeners;

	/**
	 * The background color.
	 */
	protected Color backgroundColor = defaultBackgroundColor();

	/**
	 * Stores whether this drawing is to be sized dynamically or not.
	 */
	protected boolean dynamicSize = defaultDynamicSize();
/**
 * Create a new instance of a Drawing
 */
public SimpleDrawing() {
}
/**
 * Create a new instance of a Drawing
 */
public SimpleDrawing( int width, int height ) {
	this.width = width;
	this.height = height;
	dynamicSize = false;
}
	/** 
	 * Add the figure to the contents of the canvas.
	 *
	 * @param figure the figure to add
	 */
	public void addFigure(Figure figure) {
		figures.addElement(figure);
	}
	/** 
	 * Add the figure to the contents of the canvas, sticking it behind 
	 * an existingFigure which is already there.
	 * Reflect the change if it's visible.
	 * Become an observer on the figure.
	 *
	 * @param figure the figure to add
	 * @param existingFigure the figure to which the new figure should be behind
	 */
	public void addFigureBehind(Figure figure, Figure existingFigure) {
		int existingIndex = figures.indexOf(existingFigure);
		if (existingIndex == -1) figures.addElement(figure);
		else figures.insertElementAt(figure,existingIndex);
	}
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
		firePropertyChange(evt);
	}
	/**
	 * Answer the default color for this Drawing's background.
	 *
	 * @return a Color representing this Drawing's background color.
	 */
	protected Color defaultBackgroundColor() {
		return SystemColor.window;
	}
	/** 
	 * Answer the default for whether this Drawing should be dynamically sized.
	 *
	 * @return a boolean representing whether this Drawing should be dynamically sized by default.
	 */
	protected boolean defaultDynamicSize() {
		return true;
	}
	/** 
	 * Answer the default Vector to contain this Drawing's figures.
	 *
	 * @return a Vector to contain this Drawing's figures.
	 */
	protected Vector defaultFigures() {
		return new Vector(10);
	}
	/** 
	 * Answer the default width for this Drawing.
	 *
	 * @return an integer representing this Drawing's default width.
	 */
	protected int defaultWidth() {
		return 0;
	}
	/** 
	 * Answer the figure at a given point
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Figure at x,y; null if none found
	 */
	public Figure figureAt(int x, int y) {
		for (Enumeration e = new ReverseVectorEnumerator(figures); e.hasMoreElements();) {
			Figure figure = (Figure)e.nextElement();
			if (figure.contains(x,y)) return figure;
		}
		return null;
	}
	/** 
	 * Answer a FigureEnumeration over the figures of the receiver.
	 * 
	 * @return	a FigureEnumeration over the figures of the receiver
	 */
	public FigureEnumeration figures() {
		return new FigureVectorEnumerator(figures);
	}
	/**
	 * Report a property update to any registered listeners.
	 *
	 * @param event
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
	 * Returns the current rectangular area covered by the receiver.
	 * 
	 * @return	a Rectangle representing the current rectangular area.
	 */
	public Rectangle getBounds() {
		if ( isDynamicSize() ) {
			Rectangle encompass = new Rectangle( 0, 0, 0, 0 );
			for (FigureEnumeration e = figures() ; e.hasMoreElements() ;) {
	 		    encompass.add(e.nextElement().getBounds());
			}
			return encompass;
		}
		return new Rectangle( 0, 0, width, height );
	}
	/** 
	 * Returns the current size of the receiver.
	 * 
	 * @return	a Dimension representing the current size.
	 */
	public Dimension getSize() {
		return new Dimension( getBounds().width, getBounds().height );
	}
	/** 
	 * Get the style defining how to paint on the canvas.
	 *
	 * @return the receivers current DrawingStyle
	 */
	public DrawingStyle getStyle() {
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor( backgroundColor );
		return style;
	}
	/**
	 * Answers whether this Drawing is currently dynamically sized or not.
	 *
	 * @return a boolean value of true if this drawing is dynamically sized;
	 * false otherwise.
	 */
	public boolean isDynamicSize() {
		return dynamicSize;
	}
	/** 
	 * Move the figure behind an existingFigure if it is not already there.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be behind
	 * @exception IllegalArgumentException if one or both figures are unknown to receiver.
	 */
	public void moveFigureBehind(Figure figure, Figure existingFigure) {
		int index = figures.indexOf(figure);
		int existingIndex = figures.indexOf(existingFigure);
		if (index == -1 || existingIndex == -1) {
			throw new IllegalArgumentException("At least one of the figures do not exist on this drawing"); 
		}
		if (index > existingIndex) {
			figures.removeElement(figure);
			figures.insertElementAt(figure,existingIndex);
		}
	}
	/** 
	 * Move the figure in front of an existingFigure if it is not already there.
	 * 
	 * @param figure the figure to move
	 * @param existingFigure the figure to which the new figure should be in front
	 * @exception IllegalArgumentException if one or both figures are unknown to receiver.
	 */
	public void moveFigureInFront(Figure figure, Figure existingFigure) {
		int index = figures.indexOf(figure);
		int existingIndex = figures.indexOf(existingFigure);
		if (index == -1 || existingIndex == -1) { 
			throw new IllegalArgumentException("At least one of the figures do not exist on this drawing"); 
		}
		if (index < existingIndex) {
			figures.removeElement(figure);
			figures.insertElementAt(figure,existingIndex);
		}
	}
	/** 
	 * Move the figure behind all other figures.
	 * 
	 * @param figure the figure to move
	 * @exception IllegalArgumentException if figure is unknown to receiver.
	 */
	public void moveFigureToBack(Figure figure) {
		if (figures.removeElement(figure)) 
			figures.insertElementAt(figure,0);
		else
			throw new IllegalArgumentException("The figure does not exist on this drawing"); 
	}
	/** 
	 * Move the figure in front of all other figures.
	 * 
	 * @param figure the figure to move
	 * @exception IllegalArgumentException if figure is unknown to receiver.
	 */
	public void moveFigureToFront(Figure figure) {
		if (figures.removeElement(figure)) 
			figures.addElement(figure);
		else
			throw new IllegalArgumentException("The figure does not exist on this drawing"); 
	}
	/** 
	 * Answer the figure at a given point excluding the identified figure
	 *
	 * @param figure the figure to exclude from the search
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Figure at the given point, excluding the specified figure;
	 * null if none found
	 */
	public Figure otherFigureAt(Figure excludedFigure, int x, int y) {
		for (Enumeration e = new ReverseVectorEnumerator(figures); e.hasMoreElements();) {
			Figure figure = (Figure)e.nextElement();
			if ((excludedFigure != figure) && figure.contains(x,y)) return figure;
		}
		return null;
	}
	/** 
	 * Paints the component.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		paintBackground(g);
		paintAll(g);
	}
	/** 
	 * Paints everything in the receiver.
	 * Don't bother asking figures to paint themselves if they
	 * are not in the clipped region.
	 *
	 * @param g the specified Graphics window
	 */
	protected void paintAll(Graphics g) {
		Rectangle clip = g.getClipBounds();
		if (clip == null || clip.height == -1 || clip.width == -1) {
			paintCompletely(g);
			return;
		}
		for (Enumeration e = figures.elements() ; e.hasMoreElements() ;) {
			Figure fig = (Figure)e.nextElement();
	    	if (fig.intersects(clip))
				fig.paint(g);
		}
	}
	/** 
	 * Paints the background of the receiver.
	 *
	 * @param g the specified Graphics window
	 */
	protected void paintBackground(Graphics g) {
		Rectangle myBounds = getBounds();
		g.setColor(backgroundColor);
		g.fillRect(myBounds.x, myBounds.y, myBounds.width, myBounds.height);
	}
	/** 
	 * Paints everything in the receiver, ignoring the clipping region.
	 * 
	 * @param g the specified Graphics window
	 */
	protected void paintCompletely(Graphics g) {
		for (Enumeration e = figures.elements() ; e.hasMoreElements() ;) {
 		    ((Figure)e.nextElement()).paint(g);
		}
	}
	/** 
	 * Remove the figure.
	 * We may want to also tell the figure to dispose of itself completely...
	 * currently this is left up to the sender to allow them to do other things
	 * before disposing.  This is somewhat arbitraty however and a different
	 * approach may be desired by different implementations.
	 * 
	 * @param figure the figure to remove
	 */
	public void removeFigure(Figure figure) {
		figures.removeElement(figure);
	}
	/**
	 * Remove a PropertyChangeListener from the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be removed.
	 */

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
		    return;
		}
		listeners.removeElement(listener);
	}
	/**
	 * Sets whether this Drawing is dynamically sized or not.
	 *
	 * @param dynamicSize a boolean specifying this drawing's new
	 * dynamic size state.
	 */
	public void setDynamicSize( boolean dynamicSize ) {
		this.dynamicSize = dynamicSize;
	}
	/** 
	 * Sets the current size covered by this drawing.
	 *
	 * @param width an integer representing the new width.
	 * @param height an integer representing the new height.
	 */
	public void setSize( int width, int height ) {
		Dimension oldDimension = getSize();
		this.width = width;
		this.height = height;
		changedSize( oldDimension );
	}
	/** 
	 * Sets the current size covered by this drawing.
	 *
	 * @param d a Dimension representing the new size.
	 */
	public void setSize( Dimension d ) {
		setSize( d.width, d.height );
	}
	/** 
	 * Set the style defining how to paint on the canvas.
	 *
	 * @param style the specified DrawingStyle
	 */
	public void setStyle(DrawingStyle style) {
		backgroundColor = style.getBackgroundColor();
	}
}
