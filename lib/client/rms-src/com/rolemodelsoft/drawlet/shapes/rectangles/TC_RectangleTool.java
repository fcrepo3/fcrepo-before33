package com.rolemodelsoft.drawlet.shapes.rectangles;

/**
 * @(#)TC_RectangelTool.java
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
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.awt.*;
import java.awt.*;
import java.awt.event.*;
import junit.framework.*;
import junit.ui.*;

public class TC_RectangleTool extends TestCase {
	protected DrawingCanvas staticCanvas, dynamicCanvas;
	protected Component staticComponent, dynamicComponent, component;
	protected RectangleTool staticTool, dynamicTool;
	protected MouseEvent pressedEventLoc1, pressedEventLoc2, pressedEventLoc3, pressedEventLoc4,
							draggedEventLoc1, draggedEventLoc2, draggedEventLoc3, draggedEventLoc4,
							releasedEventLoc1, releasedEventLoc2, releasedEventLoc3, releasedEventLoc4;
/**
 * 
 * @param name java.lang.String
 */
public TC_RectangleTool( String name ) {
	super( name );
}
/**
 */
public void setUp() {
	staticComponent = new DrawingCanvasComponent( staticCanvas = new SimpleDrawingCanvas( new SimpleDrawing( 40, 40 ) ) );
	staticTool = new RectangleTool( staticCanvas );
	dynamicComponent = new DrawingCanvasComponent( dynamicCanvas = new SimpleDrawingCanvas( new SimpleDrawing() ) );
	dynamicTool = new RectangleTool( dynamicCanvas );
	component = new Canvas();

	pressedEventLoc1 = new MouseEvent( component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 1, false );
	pressedEventLoc2 = new MouseEvent( component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 10, 10, 1, false );
	pressedEventLoc3 = new MouseEvent( component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 20, 20, 1, false );
	pressedEventLoc4 = new MouseEvent( component, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 50, 50, 1, false );

	draggedEventLoc1 = new MouseEvent( component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 0, 0, 0, false );
	draggedEventLoc2 = new MouseEvent( component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 10, 10, 0, false );
	draggedEventLoc3 = new MouseEvent( component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 20, 20, 0, false );
	draggedEventLoc4 = new MouseEvent( component, MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, 50, 50, 0, false );

	releasedEventLoc1 = new MouseEvent( component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 0, false );
	releasedEventLoc2 = new MouseEvent( component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 10, 10, 0, false );
	releasedEventLoc3 = new MouseEvent( component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 20, 20, 0, false );
	releasedEventLoc4 = new MouseEvent( component, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 50, 50, 0, false );

	dynamicTool.mousePressed( pressedEventLoc2 );
	staticTool.mousePressed( pressedEventLoc2 );
}
	public void testMouseDragged() {
		// Dynamic test
		assertEquals( "The figures bounds were incorrect.", new Rectangle( 10, 10, 10, 10 ), dynamicCanvas.figures().nextElement().getBounds() );
		dynamicTool.mouseDragged( draggedEventLoc4 );
		assertEquals( "The figure did not resize correctly.", new Rectangle( 10, 10, 40, 40 ), dynamicCanvas.figures().nextElement().getBounds() );
		// Static test
		assertEquals( "The figures bounds were incorrect.", new Rectangle( 10, 10, 10, 10 ), staticCanvas.figures().nextElement().getBounds() );
		staticTool.mouseDragged( draggedEventLoc4 );
		assertEquals( "The figure did not resize correctly.", new Rectangle( 10, 10, 29, 29 ), staticCanvas.figures().nextElement().getBounds() );
	}
	public void testMousePressed() {
		// Dynamic tests
		assert( "The canvas did not have the figure already.", dynamicCanvas.figures().hasMoreElements() );
	}
	public void testMouseReleased() {
		dynamicTool.mouseReleased( releasedEventLoc2 );
		assert( "The canvas had the figure.", ! dynamicCanvas.figures().hasMoreElements() );
		dynamicTool.mousePressed( pressedEventLoc2 );
		dynamicTool.mouseReleased( releasedEventLoc3 );
		assert( "The canvas did not have the figure.", dynamicCanvas.figures().hasMoreElements() );
		dynamicCanvas.removeFigure( dynamicCanvas.figures().nextElement() );
		assert( "The figure was not removed.", ! dynamicCanvas.figures().hasMoreElements() );
		dynamicTool.mousePressed( pressedEventLoc2 );
		dynamicTool.mouseReleased( releasedEventLoc1 );
		assert( "The canvas did not have the figure.", dynamicCanvas.figures().hasMoreElements() );
	}
}
