package com.rolemodelsoft.drawlet;

/**
 * @(#)DrawingCanvas.java
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
import java.awt.*;

/**
 * A generic DrawingCanvas on which Figures can be drawn and manipulated.
 * It is expected that this will typically implemented by some sort of graphical
 * Component but that is not necessary.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface DrawingCanvas extends SequenceOfFigures, Paintable {

	/**
	 * Add the handle to the receiver.
	 * Reflect the change if it's visible.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection.
	 *
	 * @param handle the handle to add
	 */
	public abstract void addHandle(Handle handle);
	/**
	 * Add the handles corresponding to the figure to the receiver.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 *
	 * @param figure the figure for which handles should be added.
	 */
	public abstract void addHandles(Figure figure);
	/**
	 * Add the handles corresponding to the figure to the receiver. 
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 *
	 * @param figure the figure for which handles are associated.
	 * @param handles the handles to add.
	 */
	public abstract void addHandles(Figure figure, Handle handles[]);
	/**
	 * Add the figure to the selections.
	 * Reflect the change if it's visible.
	 *
	 * @param figure the Figure to add
	 */
	public abstract void addSelection(Figure figure);
	/**
	 * Clear all the selections of the receiver.
	 * Reflect the change if it's visible.
	 */
	public abstract void clearSelections();
	/** 
	 * Answer the handles of the receiver.  The returned array 
	 * and its contents should be treated as read-only.
	 *
	 * @return the handles of the receiver in an array; should be treated as read-only
	 */
	public Handle[] getHandles();
	/**
	 * Answer the locator that should be used for the given coordinates
	 *
	 * @param x the horizontal coordinate
	 * @param y the vertical coordinate
	 * @return a Locator representing the proper posititioning for the specified x, y
	 */
	public abstract Locator getLocator( int x, int y );
	/** 
	 * Answer the selections of the receiver.  The returned array 
	 * and its contents should be treated as read-only.
	 *
	 * @return an array of Figures that represent the selections
	 * of the receiver; should be treated as read-only
	 */
	public abstract Figure[] getSelections();
	/** 
	 * Answer the style which defines how to paint on the canvas.
	 *
	 * @return the DrawingStyle which defines how to paint on the canvas
	 */
	public abstract DrawingStyle getStyle();
	/**
	 * Answer the active tool
	 *
	 * @return the InputEventHandler that is the active tool
	 */
	public abstract InputEventHandler getTool();
	/** 
	 * Answer the handle at a given point
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the Handle at the given point
	 */
	public abstract Handle handleAt(int x, int y);
	/**
	 * Remove the handle from the receiver.
	 * Reflect the change if it's visible.
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection.  Since it is public, don't assume
	 * the handle asked for is actually present.
	 *
	 * @param handle the handle to remove
	 */
	public abstract void removeHandle(Handle handle);
	/**
	 * Remove the handles corresponding to the figure to the receiver. 
	 * NOTE: Although this is public, it is assumed that
	 * most handles will be added/removed automatically through
	 * the process of selection, but some tools may wish to be more selective.
	 *
	 * @param figure the figure for which handles should be removed.
	 */
	public abstract void removeHandles(Figure figure);
	/**
	 * Remove the figure from the selections.
	 * Reflect the change if it's visible.
	 *
	 * @param figure the figure being deselected
	 */
	public abstract void removeSelection(Figure figure);
	/** 
	 * Repaints part of the canvas.
	 *
	 * @param rectangle is the region to be repainted
	 */
	public abstract void repaint(Rectangle rectangle);
	/**
	 * Make the figure the only selection
	 * Reflect the change if it's visible.
	 *
	 * @param figure the figure being deselected
	 */
	public abstract void select(Figure figure);
	/** 
	 * Set the size of the receiver.
	 *
	 * @param width the horizontal size
	 * @param height the vertical size
	 */
	public abstract void setSize( int width, int height );
	/** 
	 * Set the size of the receiver.
	 *
	 * @param size the new size
	 */
	public abstract void setSize( Dimension size );
	/** 
	 * Set the style defining how to paint on the canvas.
	 *
	 * @param style the specified DrawingStyle
	 */
	public abstract void setStyle(DrawingStyle style);
	/**
	 * Set the active tool
	 *
	 * @param tool the InputEventHandler to set as the active tool
	 */
	public abstract void setTool(InputEventHandler tool);
	/**
	 * Toggle whether or not the figure is selected. 
	 * Reflect the change if it's visible.
	 *
	 * @param figure the figure of interest
	 */
	public abstract void toggleSelection(Figure figure);
	/**
	 * Take appropriate action when the tool has completed its task.
	 *
	 * @param tool the tool which completed its task
	 */
	public abstract void toolTaskCompleted(InputEventHandler tool);
}
