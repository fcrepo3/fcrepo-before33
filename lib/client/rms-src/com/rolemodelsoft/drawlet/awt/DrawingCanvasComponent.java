package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)DrawingCanvasComponent.java
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

import java.beans.*;

/**
 * This provides basic functionality necessary to provide a meaningful
 * working version of a DrawingCanvas that can be tied to a Component in an AWT
 * Application.  It is expected that this would serve as the base for these sort
 * of Components, but not required.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class DrawingCanvasComponent extends Canvas {
	
	/**
	 * The drawing canvas we are displaying/manipulating.
	 */
	protected DrawingCanvas canvas;
	
	/**
	 * Create a default <code>DrawingCanvasComponent</code>.
	 */
	public DrawingCanvasComponent() {
		this(new SimpleDrawingCanvas());
	}
	/**
	 * Constructs a new <code>DrawingCanvasComponent</code>, associating
	 * it with the given <code>DrawingCanvas</code>
	 *
	 * @param canvas the canvas this component will be associated with.
	 */
	public DrawingCanvasComponent(DrawingCanvas canvas) {
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
	 * @return the DrawingCanvas we are drawing/manipulating
	 */
	public DrawingCanvas getCanvas() {
		return canvas;
	}
	/** 
	 * Gets the preferred size of the receiver.
	 *
	 * @return a dimension object indicating the receiver's preferred size.
	 */
	public Dimension getPreferredSize() {
		return getCanvas().getBounds().getSize();
	}
	/**
	 * Tell the AWT I am able to receive the focus.
	 *
	 * @return boolean value of <code>true</code> if the
	 * receiver is focus traversable;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isFocusTraversable() {
		return true;
	}
	/** 
	 * Paints the receiver.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		canvas.paint(g);
	}
	/** 
	 * Repaints part of the receiver. This will result in a
	 * call to update as soon as possible.
	 * 
	 * @param rectangle is the region to be repainted
	 */
	public void repaint(Rectangle rectangle) {
		/*
		 * Note that we fudge by a pixel to avoid
		 * differences in the way various drawing primitives determine 
		 * where to start/stop drawing between lines and fills.
		 */
		repaint(rectangle.x, rectangle.y, rectangle.width + 1, rectangle.height + 1);
	}
	/**
	 * Set the <code>DrawingCanvas</code> the receiver is associated with.
	 *
	 * @param canvas the DrawingCanvas we should draw/manipulate
	 */
	protected void setCanvas(DrawingCanvas canvas) {
		if (canvas instanceof ComponentHolder)
			((ComponentHolder)canvas).setComponent(this);
		this.canvas = canvas;
	}
}
