package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_CanvasTool.java
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
import com.rolemodelsoft.drawlet.awt.*;
import junit.framework.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 
 */
public class TC_CanvasTool extends TestCase{
	SimpleDrawingCanvas canvas;
	AbstractInputEventHandler tool;
	MouseEvent event;
/**
 * @param name java.lang.String
 */
public TC_CanvasTool(String name) {
	super(name);
}
/**
 */
public void setUp() {
	canvas = new SimpleDrawingCanvas();
	new DrawingCanvasComponent( canvas );
	tool = new SelectionTool( canvas );
	event = new MouseEvent( canvas.getComponent(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, 0, 0, 1, false );
}
/**
 * Test to make sure that the event is consumed when the mouse is released.
 */
public void testMouseReleased() {
	tool.mouseReleased( event );
	assert( "The event was not consumed.", event.isConsumed() );
}
}
