package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)ConstructionTool.java
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
import java.awt.event.*;

/**
 * This abstract class offers a simple base for Tools which create new Figures
 * and place them on a drawing canvas.
 * Concrete subclasses need to supply, at minmum:
 * 	basicNewFigure(int,int);
 *	some means of initializing canvas (e.g. Constructor)
 *
 * @version 	1.1.6, 12/28/98
 * @author 	Ken Auer
 */
 
public abstract class ConstructionTool extends CanvasTool {

	/**
	 * The figure the receiver is currently constructing
	 * (or null if the tool is not active).
	 */
	protected Figure figure;

	/**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	a new Figure
	 */
	protected abstract Figure basicNewFigure(int x, int y);
	/**
	 * Called if the mouse is dragged (the mouse button is down and mouse is moving).  
	 * By default, move the figure we created, if we have one.  Consume the event
	 * since we handle it.
	 * 
	 * @param e the event
	 */
	public void mouseDragged(MouseEvent e) {
		if (figure == null)
			return;
		figure.move( getX(e), getY(e) );
		e.consume();
	}
	/**
	 * Called if the mouse is pressed.  By default, create a new figure and add it
	 * to the canvas.  Subclasses may wish to consider newFigure() instead of
	 * this method.  Consume the event since we handle it.
	 *
	 * @param e the event 
	 * @see #newFigure
	 */
	public void mousePressed(MouseEvent e) {
		if ( e.getX() > canvas.getBounds().width || e.getY() > canvas.getBounds().height ) return;
		figure = newFigure(e.getX(), e.getY());
		canvas.addFigure(figure);
		e.consume();
	}
	/**
	 * Called if the mouse is released.  By default, we're done constructing and
	 * manipulating the figure, so forget about the figure and prepare to
	 * create a new one, in addition to inherited behavior.
	 * 
	 * @param e the event
	 */
	public void mouseReleased(MouseEvent e) {
		figure = null;
		super.mouseReleased(e);
	}
	/**
	 * Create and answer a new Figure.  This is a TemplateMethod/FactoryMethod.
	 * hooks are basicNewFigure() and setProperties().
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	a newly created Figure
	 * @see #basicNewFigure
	 * @see #setProperties
	 */
	protected Figure newFigure(int x, int y) {
		Figure newFigure = basicNewFigure(x,y);
		setProperties(newFigure);
		return newFigure;
	}
	/**
	 * Set the properties of aFigure.  By default, use the canvas' style to
	 * set that of aFigure.
	 *
	 * @param aFigure the Figure to set properties.
	 */
	protected void setProperties(Figure aFigure) {
		aFigure.setStyle(canvas.getStyle());
	}
}
