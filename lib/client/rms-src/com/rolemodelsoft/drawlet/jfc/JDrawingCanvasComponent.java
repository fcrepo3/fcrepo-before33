package com.rolemodelsoft.drawlet.jfc;

/**
 * @(#)JDrawingCanvasComponent.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
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
import com.rolemodelsoft.drawlet.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.awt.datatransfer.*;
import javax.swing.JComponent;

import java.beans.*;

/**
 * This is a Swing component which owns and acts on a DrawingCanvas.
 *
 * @version 	1.1.6, 12/29/98
 */ 

 public class JDrawingCanvasComponent extends JComponent {
	
	/**
	 * The drawing canvas we are displaying/manipulating.
	 */
	protected DrawingCanvas canvas;
	
	/**
	 * Creates a new, default JDrawingCanvas Component.
	 */
	public JDrawingCanvasComponent() {
		this(new SimpleDrawingCanvas());
	}
	/**
	 * Creates a new JDrawingCanvasComponent and initializes it with the given DrawingCanvas.
	 *
	 * @param canvas the canvas we are displaying/manipulating
	 */
	public JDrawingCanvasComponent(DrawingCanvas canvas) {
		super();
		this.setCanvas(canvas);
		if (canvas instanceof MouseListener)
			this.addMouseListener((MouseListener)canvas);
		if (canvas instanceof MouseMotionListener)
			this.addMouseMotionListener((MouseMotionListener)canvas);
		if (canvas instanceof KeyListener)
			this.addKeyListener((KeyListener)canvas);
	}
	/**
	 * Answers the receiver's canvas.
	 *
	 * @return the DrawingCanvas we are displaying/manipulating
	 */
	public DrawingCanvas getCanvas() {
		return canvas;
	}
	/**
	 * @return the value of the preferredSize property.
	 */
	public Dimension getPreferredSize() {
		return getCanvas().getBounds().getSize();
	}
	/**
	 * Tell the awt that this object is able to handle the focus
	 *
	 * @return boolean value of <code>true</code> if the receiver
	 * is focus traversable;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isFocusTraversable() {
		return true;
	}
	/** 
	 * Paints the component.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		super.paint(g);
		canvas.paint(g);
	}
	/** 
	 * Repaints part of the component. This will result in a
	 * call to update as soon as possible.
	 * 
	 * @param rectangle is the region to be repainted
	 */
	public void repaint(Rectangle rectangle) {
	/*
	 * Note that we're fudging by a pixel to avoid
	 * differences in the way various drawing primitives determine 
	 * where to start/stop drawing (draw vs. fill)
	 */
		repaint(rectangle.x, rectangle.y, rectangle.width + 1, rectangle.height + 1);
	}
	/**
	 * Sets the canvas we are associated with.
	 *
	 * @param canvas the canvas we are to draw/manipulate
	 */
	protected void setCanvas(DrawingCanvas canvas) {
		if (canvas instanceof ComponentHolder)
			((ComponentHolder)canvas).setComponent(this);
		this.canvas = canvas;
	}
}
