package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)BufferedDrawingCanvas.java
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
 * This is just a DrawingCanvasComponent that uses double buffering for smooth
 * drawing.  This would probably be the typical approach if users are doing
 * anything update-intensive.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class BufferedDrawingCanvasComponent extends DrawingCanvasComponent implements ComponentListener {

	/**
	 * The image used for double buffering.
	 */
	transient protected Image offScreenImage;

	/**
	 * The graphics used for double buffering.
	 */
	transient protected Graphics offScreenGraphics;

	/**
	 * Constructs a new BufferedDrawingCanvasComponent.
	 */
	public BufferedDrawingCanvasComponent() {
		super();
		this.addComponentListener(this);
	}
	/**
	 * Constructs a new BufferedDrawingCanvasComponent, associating it with the given DrawingCanvas
	 * @see DrawingCanvas
	 *
	 * @param canvas the canvas this component will be associated with
	 */
	public BufferedDrawingCanvasComponent(DrawingCanvas canvas) {
		super(canvas);
		this.addComponentListener(this);
	}
	/**
	 * Sent when this component is hidden by something else. Currently does nothing.
	 *
	 * @param e the event which triggered this method
	 */
	public void componentHidden(ComponentEvent e) {}
	/**
	 * Sent when this component is moved. Currently does nothing.
	 *
	 * @param e the event which triggered this method
	 */
	public void componentMoved(ComponentEvent e) {}
	/**
	 * Sent when this component resized; resets the buffer.
	 *
	 * @param e the event which triggered this method
	 */
	public void componentResized(ComponentEvent e) {
		resetBuffer();
	}
	/**
	 * Sent when this component is shown. Currently does nothing.
	 *
	 * @param e the event which triggered this method
	 */
	public void componentShown(ComponentEvent e) {}
	/**
	 * Creates the buffer.
	 */
	public void createBuffer() {
		offScreenImage = createImage( getSize().width, getSize().height );
		offScreenGraphics = offScreenImage.getGraphics();
	}
	/** 
	 * Paints the component.
	 * @see #update
	 * 
	 * @param g the Graphics within which to paint.
	 */
	public void paint( Graphics g ) {
		if ( offScreenGraphics == null ) createBuffer();
		super.paint( offScreenGraphics );
		g.drawImage( offScreenImage, 0, 0, this );
	}
	/**
	 * Reset the buffer
	 */
	public void resetBuffer() {
		offScreenImage = null;
		offScreenGraphics = null;
	}
	/** 
	 * Updates the component. This method is called in
	 * response to a call to repaint. You can assume that
	 * the background is not cleared.
	 * @see #paint
	 * 
	 * @param g the Graphics object within which to update
	 */
	public void update( Graphics g ) {
		if ( offScreenGraphics == null ) createBuffer();
		paint( g );
	}
}
