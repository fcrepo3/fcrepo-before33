package com.rolemodelsoft.drawlet.text;

/**
 * @(#)LabelEditHandle.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * A handle one can use to edit labels.
 * Currently this is rather primitive as it does not allow for insert and 
 * replacing of a subset of text... this should be a temporary limitation.
 * This would typically be invoked as an editTool (see Figure), but could
 * be used in other contexts.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class LabelEditHandle extends CanvasHandle {

	/**
	 * The figure whose label we are editing.  
	 * Note the figure must also implement LabelHolder.
	 */
	protected Figure figure;

	/**
	 * The local copy of the string being edited.
	 */
	protected String string = "";

	/**
	 * Answer a new instance prepared to edit the figure.
	 *
	 * @param figure the figure whose label is to be edited.
	 * @exception IllegalArgumentException If the figure does not implement LabelHolder interface.
	 */
	public LabelEditHandle(Figure figure) {
	if (!(figure instanceof LabelHolder))
		throw new IllegalArgumentException("Figure must also implement LabelHolder interface"); 
	this.figure = figure;
	}
	/** 
	 * Returns the current bounds of this handle.
	 *
	 * @return a Rectangle representing the current bounds of this handle.
	 */
	public Rectangle getBounds()  {
		Rectangle figureBounds = figure.getBounds();
		return new Rectangle(figureBounds.x - 3, figureBounds.y - 3, figureBounds.width + 6, figureBounds.height + 6);
	}
	/** 
	 * Returns the current bounds of the label.
	 *
	 * @return a Rectangle representing the current bounds of the label.
	 */
	public Rectangle getLabelBounds()  {
		return ((LabelHolder)figure).getLabelBounds();
	}
	/**
	 * Answer the DrawingStyle to use when displaying.
	 *
	 * @return the DrawingStyle to use when displaying.
	 */
	protected DrawingStyle getStyle() {
		if (canvas == null)
			return figure.getStyle();
		return canvas.getStyle();
	}
	/**
	 * Called if a character is typed.
	 * Edit the text.  For now this just means adding to what has been typed
	 * so far, or backspacing over it.  Should allow text insertion/replacement
	 * in future.
	 *
	 * @param evt the event
	 */
	public void keyTyped(KeyEvent evt) {
		// Filter out Ctrl or Alt and Function keys
		if (evt.isControlDown() || evt.isMetaDown() || evt.isAltDown() || evt.isActionKey()) {
			return;
		}
		Rectangle oldBounds = getBounds();
		char key = evt.getKeyChar();
		switch (key) {
			case '\b' :
				int length = string.length();
				if (length < 2)
					string = "";
				else
					string = string.substring(0, length - 1);
				break;
			case 127 : // delete key
			//			deleteSelections();
				break;
			case 10 : // Enter key
				if (!evt.isShiftDown()) {
					finished();
					evt.consume();
					return;
				}
			default :
				string = string + key;
		}
	 	((StringHolder) figure).setString(string);
		canvas.repaint(getBounds().union(oldBounds));
		evt.consume();
	}
	/**
	 * Called if the mouse is pressed.
	 * If outside our bounds, give up control and pass event on to underlying tool.
	 * Inside, do nothing.  In the future this should probably denote a type
	 * of "cursor" placement for specific editing.
	 *
	 * @param evt the event 
	 */
	public void mousePressed(MouseEvent evt) {
		if (!getBounds().contains(getX(evt), getY(evt))) {
			finished();
			canvas.getTool().mousePressed(evt);
			return;
		}
		// move the cursor
		evt.consume();
	}
	/**
	 * Called if the mouse goes up.
	 * Currently, do nothing.  In the future this should probably denote a
	 * block of text as selected for edit.
	 *
	 * @param evt the event.
	 */
	public void mouseReleased(MouseEvent evt) {
		evt.consume();
	}
	/** 
	 * Paints the receiver.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		Color oldColor = g.getColor();
		DrawingStyle myStyle = getStyle();
		DrawingStyle figureStyle = figure.getStyle();
		DrawingStyle myFigureStyle = (DrawingStyle)figureStyle.duplicate();
		g.setColor(myStyle.getHighlightColor());
		Rectangle myBounds = getBounds();
		g.fillRect(myBounds.x, myBounds.y, myBounds.width, myBounds.height);
		Rectangle innerBounds = getLabelBounds();
		if (((StringHolder)figure).getString().equals(string)) {
			// clearRect() doesn't work as expected on Netscape... 
			// fill it explicitly instead
		//	g.clearRect(innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);
			g.setColor(myStyle.getBackgroundColor());
			g.fillRect(innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);
			g.setColor(oldColor);
		} else {
			g.setColor(myStyle.getSelectionBackgroundColor());
			g.fillRect(innerBounds.x, innerBounds.y, innerBounds.width, innerBounds.height);
			g.setColor(myStyle.getSelectionForegroundColor());
			myFigureStyle.setBackgroundColor(myStyle.getSelectionBackgroundColor());
			myFigureStyle.setForegroundColor(myStyle.getSelectionForegroundColor());
			myFigureStyle.setTextColor(myStyle.getSelectionForegroundColor());
		}
		figure.setStyle(myFigureStyle);
		figure.paint(g);
		figure.setStyle(figureStyle);
	}
	/**
	 * Release control of the canvas and clean up if necessary.
	 * Since this is a public method,
	 * don't assume the receiver actually has control.
	 *
	 * @param canvas the canvas which the receiver is to release control
	 */
	public void releaseControl(DrawingCanvas canvas) {
		canvas.removeHandle(this);
		super.releaseControl(canvas);
	}
	/**  
	 * Make the handle be the event handler for the canvas.
	 * Note, once it takes control, it is obligated to return 
	 * at a future point in time.
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 */
	public void takeControl(DrawingCanvas canvas) {
		super.takeControl(canvas);
		canvas.addHandle(this);
	}
}
