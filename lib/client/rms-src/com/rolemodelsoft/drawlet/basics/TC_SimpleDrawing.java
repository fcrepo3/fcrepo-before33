package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_SimpleDrawing.java
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
import junit.framework.*;
import junit.ui.*;
import java.awt.*;
/**
 * 
 */
public class TC_SimpleDrawing extends TestCase {
	protected SimpleDrawing drawing;
	protected Figure figure1, figure2, figure3;
	protected PropertyChangeRevealer propertyRevealer;
/**
 * SimpleDrawingTest constructor comment.
 * @param name java.lang.String
 */
public TC_SimpleDrawing(String name) {
	super(name);
}
/**
 * Sets up this test.
 */
public void setUp() {
	drawing = new SimpleDrawing( 200, 200 );
	figure1 = new RectangleShape( 0, 0, 10, 10 );
	figure2 = new RectangleShape( 0, 0, 10, 10 );
	figure3 = new RectangleShape( 0, 0, 10, 10 );

	drawing.addFigure( figure1 );

	propertyRevealer = new PropertyChangeRevealer();
}

public void testAddFigure() {
	FigureEnumeration enum = drawing.figures();
	assert( "The figure was not added.", enum.hasMoreElements() );
	enum.nextElement();
	assert( "More than one figure was added.", ! enum.hasMoreElements() );
}

public void testAddFigureBehind() {
	drawing.addFigureBehind( figure2, figure1 );
	FigureEnumeration enum = drawing.figures();
	assertEquals( "The figure was not added behind.", figure2, enum.nextElement() );
	assert( "The second figure was not added.", enum.hasMoreElements() );
}
/**
 * Test to make sure PropertyChangeListeners are being added
 * correctly.
 */
public void testAddPropertyChangeListener() 
{
	drawing.addPropertyChangeListener( propertyRevealer );
	drawing.setSize( 100, 100 );
	assert( "No PropertyChangeEvent propagated", propertyRevealer.getEventCount() == 1 );

	propertyRevealer.clearEventCount();
	drawing.addPropertyChangeListener( propertyRevealer );
	drawing.setSize( 200, 200 );
	assert( "Two PropertyChangeEvents propagated", propertyRevealer.getEventCount() == 1 );
}

public void testFigureAt() {
	drawing.addFigureBehind( figure2, figure1 );
	FigureEnumeration enum = drawing.figures();
	assertEquals( "The correct figure was not returned.", figure1, drawing.figureAt( 5, 5 ) );
}

public void testFigures() {
	FigureEnumeration enum = drawing.figures();
	assert( "The enumeration is null.", enum != null );
	assert( "The enumeration is empty.", enum.hasMoreElements() );
}

public void testGetBounds() {
	drawing.setSize( 10, 10 );
	assertEquals( "The bounds returned were incorrect.", new Rectangle( 0, 0, 10, 10 ), drawing.getBounds() );
	drawing.setDynamicSize( true );
	Rectangle rec = new Rectangle( 0, 0, 0, 0 );
	rec.add( figure1.getBounds() );
	assertEquals( "The bounds returned were incorrect.", rec, drawing.getBounds() );
	drawing.removeFigure( figure1 );
	assertEquals( "The bounds were not empty.", new Rectangle( 0, 0, 0, 0 ), drawing.getBounds() );
}
/**
 * Tests getSize().
 */
public void testGetSize() {
	drawing.setSize( 10, 10 );
	assertEquals( "The size returned were incorrect.", new Dimension( 10, 10 ), drawing.getSize() );
	drawing.setDynamicSize( true );
	Rectangle rec = new Rectangle( 0, 0, 0, 0 );
	rec.add( figure1.getBounds() );
	assertEquals( "The size was incorrect.", rec.getSize(), drawing.getSize() );
	drawing.removeFigure( figure1 );
	assertEquals( "The size was incorrect.", (new Rectangle( 0, 0, 0, 0 )).getSize(), drawing.getSize() );
}
/**
 * Tests getStyle().
 */
public void testGetStyle() {
	assertEquals( "The style returned was incorrect.", SystemColor.window, drawing.getStyle().getBackgroundColor() );
}

public void testIsDynamicSize() {
	assert( "The size was dynamic.", ! drawing.isDynamicSize() );
}

public void testMoveFigureBehind() {
	drawing.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, drawing.figureAt( 5, 5 ) );
	drawing.moveFigureBehind( figure2, figure1 );
	assertEquals( "Figure 1 was not in the front", figure1, drawing.figureAt( 5, 5 ) );
}

public void testMoveFigureInFront() {
	drawing.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, drawing.figureAt( 5, 5 ) );
	drawing.moveFigureInFront( figure1, figure2 );
	assertEquals( "Figure 1 was not in the front", figure1, drawing.figureAt( 5, 5 ) );
}

public void testMoveFigureToBack() {
	drawing.addFigure( figure2 );
	drawing.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, drawing.figureAt( 5, 5 ) );
	drawing.moveFigureToBack( figure3 );
	assertEquals( "Figure 3 was not in the back", figure3, drawing.figures().nextElement() );
}

public void testMoveFigureToFront() {
	drawing.addFigure( figure2 );
	drawing.addFigure( figure3 );
	assertEquals( "Figure 3 was not in the front", figure3, drawing.figureAt( 5, 5 ) );
	drawing.moveFigureToFront( figure1 );
	assertEquals( "Figure 1 was not in the front", figure1, drawing.figureAt( 5, 5 ) );
}

public void testOtherFigureAt() {
	drawing.addFigure( figure2 );
	assertEquals( "Figure 2 was not in the front", figure2, drawing.figureAt( 5, 5 ) );
	assertEquals( "Figure 1 was not returned", figure1, drawing.otherFigureAt( figure2, 5, 5 ) );
}

public void testRemoveFigure() {
	drawing.removeFigure( figure1 );
	assert( "Figure 1 was not removed", ! drawing.figures().hasMoreElements() );
}
/**
 * Test to make sure PropertyChangeListeners are being
 * removed correctly.
 */
public void testRemovePropertyChangeListener() 
{
	drawing.addPropertyChangeListener( propertyRevealer );
	drawing.removePropertyChangeListener( propertyRevealer );
	drawing.setSize( 100, 100 );
	assert( "A PropertyChangeEvent propagated", propertyRevealer.getEventCount() == 0 );
}
/**
 * Tests setDynamicSize().
 */
public void testSetDynamicSize() {
	drawing.setDynamicSize( true );
	assert( "The size wasn't dynamic.", drawing.isDynamicSize() );
}

public void testSetSizeDimension() {
	drawing.setSize( new Dimension( 10, 10 ) );
	assertEquals( "The size returned was incorrect.", new Dimension( 10, 10 ), drawing.getSize() );
	drawing.setDynamicSize( true );
	drawing.setSize( 500, 500 );
	Rectangle rec = new Rectangle( 0, 0, 0, 0 );
	rec.add( figure1.getBounds() );
	assertEquals( "The size returned was incorrect.", rec.getSize(), drawing.getSize() );
}

public void testSetSizeIntInt() {
	drawing.setSize( 10, 10 );
	assertEquals( "The size returned was incorrect.", new Dimension( 10, 10 ), drawing.getSize() );
	drawing.setDynamicSize( true );
	drawing.setSize( 500, 500 );
	Rectangle rec = new Rectangle( 0, 0, 0, 0 );
	rec.add( figure1.getBounds() );
	assertEquals( "The size returned was incorrect.", rec.getSize(), drawing.getSize() );
}
/**
 * Tests setStyle().
 */
public void testSetStyle() {
	DrawingStyle style = new SimpleDrawingStyle();
	style.setBackgroundColor( Color.red );
	drawing.setStyle( style );
	assertEquals( "The style returned was incorrect.", Color.red, drawing.getStyle().getBackgroundColor() );
}
}
