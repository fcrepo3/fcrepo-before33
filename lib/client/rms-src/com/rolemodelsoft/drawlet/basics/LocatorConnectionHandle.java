package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)LocatorConnectionHandle.java
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
import java.awt.event.*;

/**
 * This class provides a handle that allows the corresponding figure to be
 * connected to (or disconnected from) another figure (as a slave to its location).
 * For example, this could be used to attach a label to another figure.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class LocatorConnectionHandle extends CanvasHandle implements FigureHolder {

	/**
	 * The figure we may connect/disconnect.
	 */
	protected Figure figure;

	/**
	 * The locator at which to visibly place the handle.
	 */
	protected Locator locator;

	/**
	 * The locator at which to show the current or pending connection 
	 * (or null while there is none to be displayed).
	 */
	protected Locator connection;

	/**
	 * The x coordinate at which the handle was first pressed.
	 */
	protected int anchorX;

	/**
	 * The y coordinate at which the handle was first pressed.
	 */
	protected int anchorY;

	/** 
	 * Constructs and initializes a new instance of a handle which can connect
	 * a Figure's location to another figure
	 *
	 * @param figure the figure which we may wish to connect/disconnect/reconnect
	 */
	public LocatorConnectionHandle(Figure figure) {
	this.setFigure(figure);
	}
	/** 
	 * Answer the default Color to use to draw the connection.
	 * 
	 * @return	the default Color to use to draw the connection
	 */
	protected Color defaultConnectionColor() {
		return Color.gray;
	}
	/** 
	 * Answer the default/initial locator.
	 * 
	 * @param figure the figure to return the default locator for
	 * @return	the default/initial Locator
	 */
	protected Locator defaultLocator(Figure figure) {
		return new FigureRelativePoint(figure,0.0,0.5);
	}
	/** 
	 * Returns the current bounds of this handle.
	 * 
	 * @return	a Rectangle representing the current bounds of this handle
	 */
	public Rectangle getBounds()  {
		Rectangle myBounds = getLocatorBounds();
		if (connection != null) 
			myBounds = myBounds.union(getConnectionBounds());
		myBounds.grow(1,1);  // fudge factor
		return myBounds;
	}
	/** 
	 * Returns the current bounds of the part of this handle that identifies
	 * to what the figure is connected.
	 * 
	 * @return	a Rectangle representing the current bounds of the part of
	 * this handle that identifies to what the figure is connected
	 */
	protected Rectangle getConnectionBounds()  {
		Locator center;
		if (connection == null)
			center = locator;
		else
			center = connection;
		int myRadius = getHandleWidth() / 2;
		return new Rectangle(center.x() - myRadius, center.y() - myRadius, getHandleWidth(), getHandleHeight());
	}
	/** 
	 * Answer the Color to use to draw the connection.
	 * 
	 * @return	the Color to use to draw the connection
	 */
	protected Color getConnectionColor() {
		if (canvas != null)
			return canvas.getStyle().getHighlightColor();
		return defaultConnectionColor();
	}
	/** 
	 * Returns the figure associated with this handle.
	 * 
	 * @return	the Figure associated with this handle
	 */
	public Figure getFigure()  {
		return figure;
	}
	/** 
	 * Returns the current bounds of the part of this handle that identifies
	 * the point of connection to the figure.
	 * 
	 * @return	a Rectangle representing the current bounds of the part of
	 * this handle that identifies the point of connection to the figure
	 */
	protected Rectangle getLocatorBounds()  {
		int myRadius = getHandleWidth() / 2;
		return new Rectangle(locator.x() - myRadius, locator.y() - myRadius, getHandleWidth(), getHandleHeight());
	}
	/** 
	 * Answer whether or not the figure is actually connected to something
	 * 
	 * @return	boolean value of <code>true</code> if the figure is actually
	 * connected to something;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isConnected()  {
		return AbstractFigure.figureFromLocator(figure.getLocator()) != null;
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * Ask for a connection if over a figure.  Use it, or the coordinates
	 * to display the other side of the pending connection.
	 *
	 * @param evt the event
	 */
	public void mouseDragged(MouseEvent evt) {
		Rectangle oldBounds = getBounds();
		int x = getX(evt);
		int y = getY(evt);
		Figure connectFigure = canvas.otherFigureAt(figure, x, y);
		if (connectFigure != null)
			connection = connectFigure.requestConnection(figure, x, y);
		else
			connection = new DrawingPoint(x, y);
		canvas.repaint(getBounds().union(oldBounds));
		evt.consume();
	}
	/**
	 * Called if the mouse is down.  Show the connection point, if there is one
	 * and record where the mouse went down.
	 *
	 * @param evt the event 
	 */
	public void mousePressed(MouseEvent evt) {
		anchorX = getX(evt);
		anchorY = getY(evt);
		Rectangle oldBounds = getBounds();
		if (isConnected())
			connection = ((RelativeLocator) figure.getLocator()).getBase();
		// move the cursor to connection... beats me how to do it.
		canvas.repaint(getBounds().union(oldBounds));
		evt.consume();
	}
	/**
	 * Called if the mouse is released.  If it goes up over a figure that provides
	 * a valid connection, use it as the base of the new relative locator for 
	 * the figure and make sure the figure is in front of the figure to which
	 * we are connecting it.  Otherwise, (if the mouse did not go up in the 
	 * same place it went down) change the figure's locator to one that is not
	 * relative to any others.
	 * 
	 * @param evt the event
	 */
	public void mouseReleased(MouseEvent evt) {
		Locator oldLocator = figure.getLocator();
		Rectangle damage = getBounds();
		int x = getX(evt);
		int y = getY(evt);
		if (anchorX != x || anchorY != y) {
			Figure target = canvas.otherFigureAt(figure, x, y);
			if (target == null)
				figure.move(new DrawingPoint(oldLocator.x(), oldLocator.y()));
			else {
				Locator newLocator = target.requestConnection(figure, x, y);
				if (newLocator != null) {
					connection = new RelativePoint(newLocator, oldLocator.x() - newLocator.x(), oldLocator.y() - newLocator.y());
					figure.move(connection);
					canvas.moveFigureInFront(figure, target);
				} else
					figure.move(new DrawingPoint(oldLocator.x(), oldLocator.y()));
			}
		}
		damage = damage.union(getBounds());
		damage.grow(1, 1);
		canvas.repaint(damage);
		connection = null;
		super.mouseReleased(evt);
	}
	/** 
	 * Paints the handle.  
	 * Filled in if connected, hollow if not.
	 * Draw the otherwise invisible connection if actively editing.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		Rectangle myLocatorBounds = getLocatorBounds();
		if ((connection == null && isConnected()) || (connection != null && AbstractFigure.figureFromLocator(connection) != null) ) {
			Rectangle myConnectionBounds = getConnectionBounds();
			g.fillOval(myConnectionBounds.x, myConnectionBounds.y, myConnectionBounds.width, myConnectionBounds.height);	
		}
		g.drawOval(myLocatorBounds.x, myLocatorBounds.y, myLocatorBounds.width, myLocatorBounds.height);	
		if (connection != null) {
			g.setColor(getConnectionColor());
			g.drawLine(locator.x(), locator.y(), connection.x(), connection.y());
		}
	}
	/** 
	 * Set the figure associated with this handle.  Reset the locator.
	 *
	 * @param figure the Figure to hold
	 */
	public void setFigure(Figure figure)  {
	this.figure = figure;
	locator = defaultLocator(figure);
	}
}
