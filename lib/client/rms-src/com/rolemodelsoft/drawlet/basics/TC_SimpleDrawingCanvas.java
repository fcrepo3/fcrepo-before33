package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_SimpleDrawingCanvas.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.text.*;
import junit.framework.*;
import junit.ui.*;
import java.awt.*;
import java.awt.datatransfer.*;
/**
 * 
 */
public class TC_SimpleDrawingCanvas extends TestCase {
	DrawingCanvas canvas, staticCanvas;
	Drawing drawing;
	SimpleDrawingCanvas simpleCanvas;
	DrawingCanvasComponent component, staticComponent;
	Figure figure1, figure2, figure3;
	Handle handle;
/**
 * SimpleDrawingCanvasTest constructor comment.
 * @param name java.lang.String
 */
public TC_SimpleDrawingCanvas(String name) {
	super(name);
}
/**
 * Sets up this test
 */
public void setUp() {
	canvas = new SimpleDrawingCanvas( drawing = new SimpleDrawing() );
	simpleCanvas = (SimpleDrawingCanvas) canvas;
	component = new DrawingCanvasComponent(canvas);
	figure1 = new RectangleShape( 0, 0, 10, 10 );
	figure2 = new RectangleShape( 0, 0, 10, 10 );
	figure3 = new RectangleShape( 0, 0, 10, 10 );
	handle = new BoxSelectionHandle( 5, 5, 10, 10 );
	canvas.addFigure( figure1 );

	staticCanvas = new SimpleDrawingCanvas( new SimpleDrawing( 50, 50 ) );
	staticComponent = new DrawingCanvasComponent( staticCanvas );
	staticComponent.setSize( 10, 10 );
}
/**
 * Test addFigure.
 */
public void testAddFigure() {
	assert( "The figure was not added.", canvas.figures().hasMoreElements() );
	assertEquals( "The figure was not added properly.", figure1, canvas.figures().nextElement() );
}
/**
 * Test addFigureBehind. Most of the difference is actually
 * drawing, so not much is different.
 */
public void testAddFigureBehind() {
	canvas.addFigureBehind( figure2, figure1 );
	assert( "The figures were not added.", canvas.figures().hasMoreElements() );
	assertEquals( "The second figure was not added properly.", figure2, canvas.figures().nextElement() );
}
/**
 * Test addFigureBehind. Most of the difference is actually
 * drawing, so not much is different.
 */
public void testAddHandle() {
	assert( "There were already handles on the canvas.", canvas.getHandles().length == 0 );
	canvas.addHandle( handle );
	assert( "There was not a handle added.", canvas.getHandles().length == 1 );
	assertEquals( "The handle was not added properly.", handle, canvas.getHandles()[0] );
}
/**
 * Test addFigureBehind. Most of the difference is actually
 * drawing, so not much is different.
 */
public void testAddHandlesFigure() {
	assert( "There were already handles on the canvas.", canvas.getHandles().length == 0 );
	canvas.addHandles( figure1 );
	assert( "The wrong number of handles were added; was " + String.valueOf( canvas.getHandles().length ) + " instead of 8.", canvas.getHandles().length == 8 );
	assert( "The handles were not added properly.", figure1.getHandles()[0] instanceof TopLeftHandle );
}

public void testAddHandlesFigureHandle() {
	assert( "There were already handles on the canvas.", canvas.getHandles().length == 0 );
	Handle handles[] = { handle };
	canvas.addHandles( figure1, handles );
	assert( "The wrong number of handles were added; was " + String.valueOf( canvas.getHandles().length ) + " instead of 1.", canvas.getHandles().length == 1 );
	assertEquals( "The handles were not added properly.", handle, canvas.getHandles()[0] );
}
/**
 * Test addSelection.
 */
public void testAddSelection() {
	assert( "There was already a selection.", canvas.getSelections().length == 0 );
	canvas.addSelection( figure1 );
	assertEquals( "The figure was not added correctly.", figure1, canvas.getSelections()[0] );
}

public void testClearSelections() {
	canvas.addSelection( figure1 );
	canvas.addSelection( figure2 );
	assert( "The figures were not added.", canvas.getSelections().length == 2 );
	canvas.clearSelections();
	assert( "The figures were not removed.", canvas.getSelections().length == 0 );
}

public void testCopySelections() {
	canvas.addSelection( new TextLabel( "This is test text" ) );
	assertEquals( "The text label was not added.", 1, canvas.getSelections().length );
	((SimpleDrawingCanvas)canvas).copySelections();
	assert( "The text flavor was not on the clipboard", Toolkit.getDefaultToolkit().getSystemClipboard().getContents( this ).isDataFlavorSupported( DataFlavor.stringFlavor ) );
	try {
		assertEquals( "The text was not put on the system clipboard.", "This is test text", (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents( this ).getTransferData( DataFlavor.stringFlavor ) );
	} catch ( Exception e ) {
		assert( "An exception was thrown", false );
	}
}
/**
 * Test cutSelections.
 */
public void testCutSelections() {
	canvas.removeFigure( figure1 );
	TextLabel label = new TextLabel( "This is test text" );
	canvas.addFigure( label );
	canvas.addSelection( label );
	assertEquals( "The text label was not added.", 1, canvas.getSelections().length );
	((SimpleDrawingCanvas)canvas).cutSelections();
	assert( "The text flavor was not on the clipboard", Toolkit.getDefaultToolkit().getSystemClipboard().getContents( this ).isDataFlavorSupported( DataFlavor.stringFlavor ) );
	try {
		assertEquals( "The text was not put on the system clipboard.", "This is test text", (String) Toolkit.getDefaultToolkit().getSystemClipboard().getContents( this ).getTransferData( DataFlavor.stringFlavor ) );
	} catch ( Exception e ) {
		assert( "An exception was thrown", false );
	}
	assert( "There were still figures left", ! canvas.figures().hasMoreElements() );
}

public void testDeleteSelections() {
	canvas.addSelection( figure1 );
	((SimpleDrawingCanvas)canvas).deleteSelections();
	assert( "There were still some figures left", ! canvas.figures().hasMoreElements() );
}

public void testFigureAt() {
	canvas.addFigureBehind( figure2, figure1 );
	FigureEnumeration enum = canvas.figures();
	assertEquals( "The correct figure was not returned.", figure1, canvas.figureAt( 5, 5 ) );
}

public void testFigures() {
	assert( "The FigureEnumeration was null.", canvas.figures() != null );
	assert( "The FigureEnumeration was empty.", canvas.figures().hasMoreElements() );
}
/**
 * Test getBounds.
 */
public void testGetBounds() {
	component.setSize( 300, 300 );
	assertEquals( "The bounds were incorrect", new Rectangle( 0, 0, 300, 300 ), canvas.getBounds() );
	drawing.setDynamicSize( false );
	component.setSize( 400, 400 );
	assertEquals( "The bounds were incorrect", new Rectangle( 0, 0, 300, 300 ), canvas.getBounds() );
}
/**
 * Test getComponent.
 */
public void testGetComponent() {
	assert( "The component was null.", simpleCanvas.getComponent() != null );
}

public void testGetHandles() {
	assert( "The handle array returned was the wrong length.", canvas.getHandles().length == 0 );
	canvas.addHandle( handle );
	assert( "The handle array returned was the wrong length.", canvas.getHandles().length == 1 );
}
/**
 * Test getLocator.
 */
public void testGetLocator() {
	assert( "The locator returned was incorrect (dynamic).", canvas.getLocator( 60, 60 ).x() == 60 &&  canvas.getLocator( 60, 60 ).y() == 60 );
	assert( "The locator returned was incorrect (static); x was " + String.valueOf( staticCanvas.getLocator( 60, 60 ).x() ) + ".", staticCanvas.getLocator( 60, 60 ).x() == 49 &&  staticCanvas.getLocator( 60, 60 ).y() == 49 );
}

public void testGetSelections() {
	assert( "There were already selections.", canvas.getSelections().length == 0 );
	canvas.addSelection( figure1 );
	assert( "The selection array returned was the wrong length.", canvas.getSelections().length == 1 );
}
/**
 * Test getSize.
 */
public void testGetSize() {
	assertEquals( "The size was incorrect", new Dimension( 200, 200 ), canvas.getSize() );
}

public void testGetStyle() {
	assert( "The style was null.", canvas.getStyle() != null );
}

public void testGetTool() {
	assert( "The tool was null.", canvas.getTool() != null );
	assert( "The tool wasn't a selection tool", canvas.getTool() instanceof SelectionTool );
}

public void testHandleAt() {
	canvas.addHandle( handle );
	assertEquals( "The correct handle was not returned.", handle, canvas.handleAt( 6, 6 ) );
}

public void testMoveFigureBehind() {
	canvas.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, canvas.figureAt( 5, 5 ) );
	canvas.moveFigureBehind( figure2, figure1 );
	assertEquals( "Figure 1 was not in the front", figure1, canvas.figureAt( 5, 5 ) );
}

public void testMoveFigureInFront() {
	canvas.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, canvas.figureAt( 5, 5 ) );
	canvas.moveFigureInFront( figure1, figure2 );
	assertEquals( "Figure 1 was not in the front", figure1, canvas.figureAt( 5, 5 ) );
}

public void testMoveFigureToBack() {
	canvas.addFigure( figure2 );
	canvas.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, canvas.figureAt( 5, 5 ) );
	canvas.moveFigureToBack( figure3 );
	assertEquals( "Figure 3 was not in the back", figure3, canvas.figures().nextElement() );
}

public void testMoveFigureToFront() {
	canvas.addFigure( figure2 );
	canvas.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, canvas.figureAt( 5, 5 ) );
	canvas.moveFigureToFront( figure1 );
	assertEquals( "Figure 1 was not in the front", figure1, canvas.figureAt( 5, 5 ) );
}

public void testMoveSelectionsToBack() {
	canvas.addFigure( figure2 );
	canvas.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, canvas.figureAt( 5, 5 ) );
	canvas.addSelection( figure3 );
	simpleCanvas.moveSelectionsToBack();
	assertEquals( "Figure 3 was not in the back", figure3, canvas.figures().nextElement() );
}

public void testMoveSelectionsToFront() {
	canvas.addFigure( figure2 );
	canvas.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, canvas.figureAt( 5, 5 ) );
	canvas.addSelection( figure1 );
	simpleCanvas.moveSelectionsToFront();
	assertEquals( "Figure 1 was not in the front", figure1, canvas.figureAt( 5, 5 ) );
}

public void testOtherFigureAt() {
	canvas.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, canvas.figureAt( 5, 5 ) );
	assertEquals( "Figure 1 was not returned", figure1, canvas.otherFigureAt( figure2, 5, 5 ) );
}

public void testPaste() {
	canvas.addSelection( figure1 );
	canvas.addFigure( figure2 );
	canvas.addSelection( figure2 );
	assertEquals( "The figures were not added.", 2, canvas.getSelections().length );
	((SimpleDrawingCanvas)canvas).copySelections();
	((SimpleDrawingCanvas)canvas).paste();
	FigureEnumeration enum = canvas.figures();
	for ( int i = 0; i < 4; i++, enum.nextElement() ) {
		assert( "There were not enough elements on the canvas", enum.hasMoreElements() );
	}
	assert( "There were too many elements on the canvas", ! enum.hasMoreElements() );
}

public void testPasteFromSystem() {
	canvas.removeFigure( figure1 );
	canvas.addSelection( new TextLabel( "This is test text" ) );
	((SimpleDrawingCanvas)canvas).copySelections();
	canvas.clearSelections();
	((SimpleDrawingCanvas)canvas).pasteFromSystem();
	assert( "The canvas was empty", canvas.figures().hasMoreElements() );
	assert( "A text label was not added to the canvas", canvas.figures().nextElement() instanceof TextLabel );
	assertEquals( "The TextLabel has the wrong string", "This is test text", ((StringHolder)canvas.figures().nextElement()).getString() );
}

public void testRemoveFigure() {
	canvas.removeFigure( figure1 );
	assert( "Figure 1 was not removed", ! canvas.figures().hasMoreElements() );
}

public void testRemoveHandle() {
	canvas.addHandle( handle );
	assert( "There was not a handle added.", canvas.getHandles().length == 1 );
	canvas.removeHandle( handle );
	assert( "The handle was not removed from the canvas.", canvas.getHandles().length == 0 );
}

public void testRemoveHandlesFigure() {
	canvas.addHandles( figure1 );
	assertEquals( "There were not handles added.", 8, canvas.getHandles().length );
	canvas.removeHandles( figure1 );
	assertEquals( "The handles were not removed from the canvas.", 0, canvas.getHandles().length );
}

public void testRemoveSelection() {
	canvas.addSelection( figure1 );
	assertEquals( "The figure was not added correctly.", figure1, canvas.getSelections()[0] );
	canvas.removeSelection( figure1 );
	assert( "The figure was not removed properly.", canvas.getSelections().length == 0 );
}

public void testSelect() {
	canvas.addSelection( figure1 );
	canvas.addSelection( figure2 );
	assert( "The figures were not added correctly.", canvas.getSelections().length == 2 );
	canvas.select( figure3 );
	assert( "The figure was not selected correctly.", canvas.getSelections().length == 1 );
	assertEquals( "The figure was not selected correctly.", figure3, canvas.getSelections()[0] );
}

public void testSetComponent() {
	Component comp = new Canvas();
	simpleCanvas.setComponent( comp );
	assertEquals( "The component was not set correctly.", comp, simpleCanvas.getComponent() );
}
/**
 * Test setDrawing.
 */
public void testSetDrawing() {
	canvas.select( figure1 );
	SimpleDrawing drawing2 = new SimpleDrawing();
	((SimpleDrawingCanvas)canvas).setDrawing( drawing2 );
	assertEquals( "The figure is still selected", 0, canvas.getSelections().length );
	drawing2.setDynamicSize( false );
	drawing2.setSize( 10, 10 );
	assertEquals( "The canvas is not receiving update events from the new drawing", new Dimension( 10, 10 ), canvas.getSize() );
}
/**
 * Test setSize( <code>Dimension</code> ).
 */
public void testSetSizeDimension() {
	canvas.setSize( new Dimension( 10, 10 ) );
	assertEquals( "The size was not set properly.", new Dimension( 10, 10 ), canvas.getSize() );
}
/**
 * Test setSize( int, int ).
 */
public void testSetSizeIntInt() {
	canvas.setSize( 10, 10 );
	assertEquals( "The size was not set properly.", new Dimension( 10, 10 ), canvas.getSize() );
}

public void testSetStyle() {
	DrawingStyle style = new SimpleDrawingStyle();
	canvas.setStyle( style );
	assertEquals( "The style was not set properly.", style, canvas.getStyle() );
}

public void testSetTool() {
	InputEventHandler tool = new RectangleTool( canvas );
	canvas.setTool( tool );
	assertEquals( "The tool wasn't set properly", tool, canvas.getTool() );
}

public void testToggleSelection() {
	canvas.toggleSelection( figure1 );
	assertEquals( "The figure was not selected correctly.", figure1, canvas.getSelections()[0] );
	canvas.toggleSelection( figure1 );
	assert( "The figure was not deselected correctly.", canvas.getSelections().length == 0 );
}

public void testToolTaskCompleted() {
	// Method is a no-op
}
}
