package com.rolemodelsoft.drawlet.text;

/**
 * @(#)TS_TextLabel.java
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
import com.rolemodelsoft.drawlet.text.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.util.*;
import junit.ui.*;
import junit.framework.*;
import java.awt.*;

public class TC_TextLabel extends TC_AbstractFigure {
	protected TextLabel label;
	protected BasicStringRenderer renderer;
/**
 * TextLabelTest constructor comment.
 * @param name java.lang.String
 */
public TC_TextLabel(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
public void setUp() 
{
	figure = new TextLabel();
	label = (TextLabel) figure;
	figure.setBounds( 10, 10, 10, 10 );
	propertyRevealer = new PropertyChangeRevealer();
	locationRevealer = new RelatedLocationRevealer();
	renderer = new BasicStringRenderer( label.getString(), label.getFont() );
}
/**
 * Test to make sure the contains( Figure ) method is
 * functioning properly.
 */
public void testContainsFigure() 
{
	Figure testFigure = new RectangleShape( 11, 11, 8, 8 );
	assert( "Figure does not contain the figure", figure.contains( testFigure ) );
	testFigure.setBounds( 4, 4, 12, 12 );
	assert( "Figure contains the figure", ! figure.contains( testFigure ) );
}
/**
 * Test to make sure the contains( int, int ) method is
 * functioning properly.
 */
public void testContainsIntInt() 
{
	assert( "Figure does not contain the point", figure.contains( 11, 11 ) );
	assert( "Figure contains the point", ! figure.contains( 30, 30 ) );
}
/**
 * Test to make sure the contains( Rectangle ) method is
 * functioning properly.
 */
public void testContainsRectangle() 
{
	Rectangle testRect = new Rectangle( 11, 11, 8, 8 );
	assert( "Figure does not contain the rectangle", figure.contains( testRect ) );
	testRect.setBounds( 4, 4, 12, 12 );
	assert( "Figure contains the rectangle", ! figure.contains( testRect ) );
}
/**
 * Test to make sure that the edit tool returned is null.
 * Subclasses should override if they return expect a
 * different result.
 */
public void testEditTool() 
{
	assert( "The edit tool returned was null.", figure.editTool( 0, 0 ) != null );
	assert( "The edit tool was not a LabelEditHandle.", figure.editTool( 0, 0 ) instanceof LabelEditHandle );
}
/**
 * Test to make sure the bottom is properly returned.
 */
public void testGetBottom() 
{
	assertEquals( "The int returned does not correspond to the expected bottommost coordinate of the figure", renderer.getStringHeight() + 10, figure.getBottom() );
}
/**
 * Test to make sure the height of the figure is properly
 * returned.
 */
public void testGetHeight() 
{
	assertEquals( "The height was not what was expected.", renderer.getStringHeight(), figure.getHeight() );
}
/**
 * Test to make sure the leftmost coordinate of the figure
 * is properly returned.
 */
public void testGetLeft() 
{
	assertEquals( "The int returned does not correspond to the expected leftmost coordinate of the figure", 10, figure.getLeft() );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testGetLocator() 
{
	assert( "There was no locator returned.", figure.getLocator() != null );
	assertEquals( "The x of the locator returned does not correspond to the left side of this label.", 13, figure.getLocator().x() );
	assertEquals( "The y of the locator returned does not correspond to the top of this label.", 10, figure.getLocator().y() );
}
/**
 * Test to make sure the rightmost coordinate of the figure
 * is properly returned.
 */
public void testGetRight() 
{
	assertEquals( "The int returned does not correspond to the expected rightmost coordinate of the figure", renderer.getStringWidth() + 6 + 10, figure.getRight() );
}
/**
 * Test to make sure the size of the figure is properly
 * returned.
 */
public void testGetSize() 
{
	assertEquals( "The size was not what was expected.", new Dimension( renderer.getStringWidth() + 6, renderer.getStringHeight() ), figure.getSize() );
}
/**
 * Test to make sure the top is properly returned.
 */
public void testGetTop() 
{
	assertEquals( "The int returned does not correspond to the expected topmost coordinate of the figure", 10, figure.getTop() );
}
/**
 * Test to make sure the width of the figure is properly
 * returned.
 */
public void testGetWidth() 
{
	assertEquals( "The width was not what was expected.", renderer.getStringWidth() + 6, figure.getWidth() );
}
/**
 * Test to make sure the intersects( Figure ) method is
 * functioning properly.
 */
public void testIntersectsFigure() 
{
	Figure testFigure = new RectangleShape( 11, 11, 2, 2 );
	assert( "Figure does not intersect the figure", figure.intersects( testFigure ) );
	testFigure.setBounds( 7, 7, 5, 5 );
	assert( "Figure does not intersect the figure", figure.intersects( testFigure ) );
	testFigure.setBounds( 40, 40, 6, 6 );
	assert( "Figure intersects the figure", ! figure.intersects( testFigure ) );
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectsRectangle() 
{
	Rectangle testRect = new Rectangle( 11, 11, 2, 2 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 7, 7, 5, 5 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 40, 40, 6, 6 );
	assert( "Figure intersects the rectangle", ! figure.intersects( testRect ) );
}
/**
 * Test to make sure the isWithin( Figure ) method is
 * functioning properly.
 */
public void testIsWithinFigure() 
{
	Figure testFigure = new RectangleShape( 9, 9, 30, 30 );
	assert( "Figure isn't within the figure", figure.isWithin( testFigure ) );
	testFigure.setBounds( 5, 5, 2, 2 );
	assert( "Figure is within the figure", ! figure.isWithin( testFigure ) );
}
/**
 * Test to make sure the isWithin( Rectangle ) method is
 * functioning properly.
 */
public void testIsWithinRectangle() 
{
	Rectangle testRect = new Rectangle( 9, 9, 30, 30 );
	assert( "Figure isn't within the rectangle", figure.isWithin( testRect ) );
	testRect.setBounds( 5, 5, 2, 2 );
	assert( "Figure is within the rectangle", ! figure.isWithin( testRect ) );
}
/**
 * Test to make sure move( int, int ) works correctly.
 */
public void testMoveIntInt() 
{
	figure.move( 20, 20 );
	assert( "The figure did not move properly", figure.getLeft() == 20 && figure.getTop() == 20 );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testRequestConnection() 
{
	Locator locator = figure.requestConnection( new RectangleShape(), 0, 0 );
	assert( "There was no locator returned.", locator != null );
	assert( "The locator is incorrect.", locator.x() == 0 && locator.y() == 0 );
	if ( locator instanceof FigureRelativePoint ) {
		FigureRelativePoint relativeLocator = (FigureRelativePoint) locator;
		assert( "The locator returned does not have the figure as its owner.", relativeLocator.getFigure() == figure );
		figure.translate( 1, 1 );
		assert( "The locator did not update when the figure moved.", relativeLocator.x() == 1 && relativeLocator.y() == 1 );
	} else {
		assert( "The locator returned is not a FigureRelativePoint. If that is OK, you need to override this test method.", false );
	}
}
/**
 * Test to make sure the bounds are properly set.
 */
public void testSetBounds()
{
	label.setBounds( 20, 20, 100, 100 );
	assertEquals( "The figure's left side is incorrect.", 20, figure.getLeft() );
	assertEquals( "The figure's right side is incorrect.", 20 + renderer.getStringWidth() + 6, figure.getRight() );
	assertEquals( "The figure's top side is incorrect.", 20, figure.getTop() );
	assertEquals( "The figure's bottom side is incorrect.", 20 + renderer.getStringHeight(), figure.getBottom() );
	
}
/**
 * Test to make sure the setSize( Dimension ) method is
 * working properly.
 */
public void testSetSizeDimension()
{
	figure.setSize( new Dimension( 100, 100 ) );
	assertEquals( "The figure's width is incorrect.", renderer.getStringWidth() + 6, figure.getWidth() );
	assertEquals( "The figure's height is incorrect.", renderer.getStringHeight(), figure.getHeight() );
}
/**
 * Test to make sure the setSize( int, int ) method is
 * working properly.
 */
public void testSetSizeIntInt()
{
	figure.setSize( 100, 100 );
	assertEquals( "The figure's width is incorrect.", renderer.getStringWidth() + 6, figure.getWidth() );
	assertEquals( "The figure's height is incorrect.", renderer.getStringHeight(), figure.getHeight() );
}
/**
 * Test to make sure translate works correctly.
 */
public void testTranslate() 
{
	translateByExpecting( 5, 5 , 15, 15 );
	translateByExpecting( -5, -5, 10, 10 );
}
}
