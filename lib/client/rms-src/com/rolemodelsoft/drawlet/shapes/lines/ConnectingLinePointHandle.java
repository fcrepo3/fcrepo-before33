package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)ConnectingLinePointHandle.java
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
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides a handle that shows up at a particular Locator on a line
 * and reshapes the figure accordingly as it is dragged, potentially turning
 * the specific locator into one relative to another figure.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class ConnectingLinePointHandle extends LinePointHandle {

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the the position of a particular point defining a line and
	 * connect that point to a figure if appropriate.
	 *
	 * @param figure the figure whose point (vertex) we may wish to modify.
	 * @param index the identifying index of the point of interest.
	 */
	public ConnectingLinePointHandle(LineFigure figure, int index) {
		super(figure,index);
	}
	/** 
	 * Answer whether the handle's locator is connected to a figure (or about
	 * to request it).
	 * 
	 * @return	boolean value of <code>true</code> if the handle's locator is
	 * connected to a figure (or about to request it);
	 * 			<code>false</code> otherwise.
	 */
	protected boolean isConnected()  {
		Locator locator = figure.getLocator(index);
		return (locator instanceof FigureHolder) || (canvas != null && canvas.otherFigureAt(figure, locator.x(), locator.y()) != null);
	}
	/**
	 * Called if the mouse goes up.
	 * If there is a figure at the given point, connect to it if possible before
	 * giving up control.
	 *
	 * @param evt the event
	 */
	public void mouseReleased(MouseEvent evt) {
		int x = getX(evt);
		int y = getY(evt);
		Figure target = canvas.otherFigureAt(figure, x, y);
		if (target != null) {
			Locator newLocator = target.requestConnection(figure, x, y);
			if (newLocator != null) {
				Rectangle bounds = figure.getBounds();
				figure.setLocator(index, newLocator);
				canvas.moveFigureBehind(figure, target);
				bounds = bounds.union(figure.getBounds());
				bounds.grow(halfWidth, halfWidth);
				canvas.repaint(bounds);
			}
		}
		super.mouseReleased(evt);
	}
	/** 
	 * Paints the handle.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		if (isConnected())
			g.fillRect(centerX() - halfWidth, centerY() - halfWidth, 2*halfWidth, 2*halfWidth);
		else
			g.drawRect(centerX() - halfWidth, centerY() - halfWidth, 2*halfWidth, 2*halfWidth);
	}
}
