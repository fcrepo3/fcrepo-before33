package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)TC_AdornedLine.java
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
import com.rolemodelsoft.drawlet.shapes.lines.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import java.awt.*;
import junit.framework.*;
import junit.ui.*;

public class TC_AdornedLine extends TC_AbstractFigure {
	AdornedLine adornedLine;
	Arrow adornment1;
/**
 * AdornedLineTest constructor comment.
 * @param name java.lang.String
 */
public TC_AdornedLine(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
public void setUp() 
{
	adornedLine = new AdornedLine( 0, 0, 50, 50 );
	figure = (AbstractFigure) adornedLine;
	adornment1 = new Arrow( adornedLine );
	adornedLine.addAdornment( adornment1 );
	propertyRevealer = new PropertyChangeRevealer();
	locationRevealer = new RelatedLocationRevealer();
}
/**
 * Test to make sure ardornments are being added properly.
 */
public void testAddAdornment() 
{
	assert( "The adornment was not properly added.", figure.contains( 43, 48 ) );
}
/**
 * Test to make sure the contains( Figure ) method is
 * functioning properly.
 */
public void testContainsFigure() 
{
	Figure testFigure = new RectangleShape( 9, 9, 2, 2 );
	assert( "Figure does not contain the figure", figure.contains( testFigure ) );
	testFigure.setBounds( 51, 51, 2, 2 );
	assert( "Figure contains the figure", ! figure.contains( testFigure ) );
}
/**
 * Test to make sure the contains( int, int ) method is
 * functioning properly.
 */
public void testContainsIntInt() 
{
	assert( "Figure does not contain the point", figure.contains( 6, 6 ) );
	assert( "Figure contains the point", ! figure.contains( 5, 2 ) );
}
/**
 * Test to make sure the contains( Rectangle ) method is
 * functioning properly.
 */
public void testContainsRectangle() 
{
	Rectangle testRect = new Rectangle( 9, 9, 2, 2 );
	assert( "Figure does not contain the rectangle", figure.contains( testRect ) );
	testRect.setBounds( 51, 51, 2, 2 );
	assert( "Figure contains the rectangle", ! figure.contains( testRect ) );
}
/**
 * Test to make sure the bottom is properly returned.
 */
public void testGetBottom() 
{
	assertEquals( "The int returned does not correspond to the expected bottommost coordinate of the figure", 50, figure.getBottom() );
}
/**
 * Test to make sure the bottom is properly returned.
 */
public void testGetBounds() 
{
	assertEquals( "The bounds were incorrect", new Rectangle( 0, 0, 50, 50 ), figure.getBounds() );
}
/**
 * Test to make sure the height of the figure is properly
 * returned.
 */
public void testGetHeight() 
{
	assertEquals( "The height was not what was expected", 50, figure.getHeight() );
}
/**
 * Test to make sure the leftmost coordinate of the figure
 * is properly returned.
 */
public void testGetLeft() 
{
	assertEquals( "The int returned does not correspond to the expected leftmost coordinate of the figure", 0, figure.getLeft() );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testGetLocator() 
{
	assert( "There was no locator returned.", figure.getLocator() != null );
	assert( "The locator returned does not correspond to the top left corner of this figure. If that is OK, you need to override this test method.", figure.getLocator().x() == 0 && figure.getLocator().y() == 0 );
}
/**
 * Test to make sure the rightmost coordinate of the figure
 * is properly returned.
 */
public void testGetRight() 
{
	assertEquals( "The int returned does not correspond to the expected rightmost coordinate of the figure", 50, figure.getRight() );
}
/**
 * Test to make sure the size of the figure is properly
 * returned.
 */
public void testGetSize() 
{
	assertEquals( "The size was not what was expected", new Dimension( 50, 50 ), figure.getSize() );
}
/**
 * Test to make sure the top is properly returned.
 */
public void testGetTop() 
{
	assert( "The int returned does not correspond to the expected topmost coordinate of the figure", figure.getTop() == 0 );
}
/**
 * Test to make sure the width of the figure is properly
 * returned.
 */
public void testGetWidth() 
{
	assertEquals( "The width was not what was expected", 50, figure.getWidth() );
}
/**
 * Test to make sure the intersects( Figure ) method is
 * functioning properly.
 */
public void testIntersectsFigure() 
{
	Figure testFigure = new RectangleShape( 6, 6, 2, 2 );
	assert( "Figure does not intersect the figure", figure.intersects( testFigure ) );
	testFigure.setBounds( 2, 2, 5, 5 );
	assert( "Figure does not intersect the figure", figure.intersects( testFigure ) );
	testFigure.setBounds( 52, 52, 6, 6 );
	assert( "Figure intersects the figure", ! figure.intersects( testFigure ) );
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectsRectangle() 
{
	Rectangle testRect = new Rectangle( 6, 6, 2, 2 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 2, 2, 5, 5 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 52, 52, 6, 6 );
	assert( "Figure intersects the rectangle", ! figure.intersects( testRect ) );
}
/**
 * Test to make sure the isWithin( Figure ) method is
 * functioning properly.
 */
public void testIsWithinFigure() 
{
	Figure testFigure = new RectangleShape( -1, -1, 52, 52 );
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
	Rectangle testRect = new Rectangle( -1, -1, 52, 52 );
	assert( "Figure isn't within the rectangle", figure.isWithin( testRect ) );
	testRect.setBounds( 5, 5, 2, 2 );
	assert( "Figure is within the rectangle", ! figure.isWithin( testRect ) );
}
/**
 * Test to make sure relatedLocationListeners
 * works correctly.
 */
public void testRelatedLocationListeners()
{
	assert( "The figure did not already have a location listener", figure.relatedLocationListeners().hasMoreElements() );
	figure.addRelatedLocationListener( locationRevealer );
	assert( "The figure did not have any location listeners", figure.relatedLocationListeners().hasMoreElements() );
}
/**
 * Test to make sure ardornments are being removed properly.
 */
public void testRemoveAdornment() 
{
	adornedLine.removeAdornment( adornment1 );
	assert( "The adornment was not properly removed.", ! figure.contains( 1, 5 ) );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testRequestConnection() 
{
	Locator locator = figure.requestConnection( new RectangleShape(), 0, 0 );
	if( locator == null ) {
		assert( "There was no locator returned.", false );
		return;
	}
	assert( "The locator returned does not correspond to the center of this figure. It was " + String.valueOf( locator.x() ) + ", " + String.valueOf( locator.y() ) + ". If that is OK, you need to override this test method.", locator.x() == 0 && locator.y() == 0 );
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
	figure.setBounds( 5, 5, 100, 100 );
	assertEquals( "The figure's left side is incorrect.", 5, figure.getLeft() );
	assertEquals( "The figure's right side is incorrect", 105, figure.getRight() );
	assertEquals( "The figure's top side is incorrect.", 5, figure.getTop() );
	assertEquals( "The figure's bottom side is incorrect.", 105, figure.getBottom() );
	
}
/**
 * Test to make sure the setSize( Dimension ) method is
 * working properly.
 */
public void testSetSizeDimension()
{
	figure.setSize( new Dimension( 100, 100 ) );
	assertEquals( "The figure's width is incorrect.", 100, figure.getWidth() );
	assertEquals( "The figure's height is incorrect.", 100, figure.getHeight() );
}
/**
 * Test to make sure the setSize( int, int ) method is
 * working properly.
 */
public void testSetSizeIntInt()
{
	figure.setSize( 100, 100 );
	assertEquals( "The figure's width is incorrect", 100, figure.getWidth() );
	assertEquals( "The figure's height is incorrect", 100, figure.getHeight() );
}
/**
 * Test to make sure translate works correctly.
 */
public void testTranslate() 
{
	translateByExpecting( 5, 5 , 5, 5 );
	translateByExpecting( -5, -5, 0, 0);
}
}
