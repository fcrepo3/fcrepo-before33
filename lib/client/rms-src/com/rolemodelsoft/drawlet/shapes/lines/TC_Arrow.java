package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)TC_Arrow.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.shapes.lines.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import java.awt.*;
import junit.framework.*;

public class TC_Arrow extends TC_AbstractFigure {
	AbstractFigure forwardFigure, reverseFigure;
	AdornedLine lineFigure;
/**
 * ArrowTest constructor comment.
 * @param name java.lang.String
 */
public TC_Arrow(String name) {
	super(name);
}
/**
 * ArrowTest constructor comment.
 * @param name java.lang.String
 */
public void setUp() {
	lineFigure = new AdornedLine( 0, 0, 50, 50 );
	forwardFigure = new Arrow( lineFigure, Arrow.FORWARD );
	reverseFigure = new Arrow( lineFigure, Arrow.REVERSE );
	figure = forwardFigure;
	lineFigure.addAdornment( (Arrow)figure );
	propertyRevealer = new PropertyChangeRevealer();
	locationRevealer = new RelatedLocationRevealer();
	ArrowStyle.setDefaultLocators( new Locator[] {
										new PolarCoordinate( 10, 0.0 + Math.PI ),
										new PolarCoordinate( 20, 0.5 + Math.PI ),
										new PolarCoordinate( 0, 0.0 ),
										new PolarCoordinate( 20, -0.5 + Math.PI )
									}
	);
}
/**
 * Test to make sure PropertyChangeListeners are being added
 * correctly.
 */
public void testAddPropertyChangeListener() 
{
	figure.addPropertyChangeListener( propertyRevealer );
	figure.setStyle( new SimpleDrawingStyle() );
	assert( "No PropertyChangeEvent propagated", propertyRevealer.getEventCount() == 1 );

	propertyRevealer.clearEventCount();
	figure.addPropertyChangeListener( propertyRevealer );
	figure.setStyle( new SimpleDrawingStyle() );
	assert( "Two PropertyChangeEvents propagated", propertyRevealer.getEventCount() == 1 );
}
/**
 * Test to make sure RelatedLocationListeners are being added
 * correctly.
 */
public void testAddRelatedLocationListener() 
{
	figure.addRelatedLocationListener( locationRevealer );
	lineFigure.translate( 5, 5 );
	assertEquals( "RelatedLocatorEvent not propagated.", 1, locationRevealer.getEventCount() );
}
/**
 * Test to make sure the contains( Figure ) method is
 * functioning properly.
 */
public void testContainsFigure() 
{
	Figure testFigure = new RectangleShape( 43, 43, 5, 5);
	assert( "Figure does not contain the figure", forwardFigure.contains( testFigure ) );
	testFigure.setBounds( 9, 9, 12, 12 );
	assert( "Figure contains the figure", ! forwardFigure.contains( testFigure ) );
}
/**
 * Test to make sure the contains( int, int ) method is
 * functioning properly.
 */
public void testContainsIntInt() 
{
	assert( "Figure does not contain the point", forwardFigure.contains( 49, 49 ) );
	assert( "Figure contains the point", ! forwardFigure.contains( 20, 20 ) );
}
/**
 * Test to make sure the contains( Rectangle ) method is
 * functioning properly.
 */
public void testContainsRectangle() 
{
	Rectangle testRect = new Rectangle( 43, 43, 5, 5);
	assert( "Figure does not contain the rectangle", forwardFigure.contains( testRect ) );
	testRect.setBounds( 9, 9, 12, 12 );
	assert( "Figure contains the rectangle", ! forwardFigure.contains( testRect ) );
}
/**
 * Test to make sure the ArrowStyle is properly returned.
 */
public void testGetArrowStyle() 
{
	ArrowStyle style = ((Arrow)figure).getArrowStyle();
	Locator[] styleLocs, locs;
	styleLocs = style.getLocators();
	locs = new Locator[] { new PolarCoordinate( 10, 0.0 + Math.PI ), new PolarCoordinate( 20, 0.5 + Math.PI ), new PolarCoordinate( 0, 0.0 ), new PolarCoordinate( 20, -0.5 + Math.PI ) };
	for ( int i = 0; i < styleLocs.length; i++ ) {
		assertEquals( "The ArrowStyle's locators were incorrect", locs[i].x(), styleLocs[i].x() );
		assertEquals( "The ArrowStyle's locators were incorrect", locs[i].y(), styleLocs[i].y() );
	}
	assert( "The style wasn't opaque", style.isOpaque() );
}
/**
 * Test to make sure the bottom is properly returned.
 */
public void testGetBottom() 
{
	assertEquals( "The int returned does not correspond to the expected bottommost coordinate of the figure", 50, figure.getBottom() );
}
/**
 * Test to make sure the bounds are returned properly.
 */
public void testGetBounds() 
{
	assertEquals( "The bounds were incorrect.", new Rectangle( 31, 31, 19, 19 ), forwardFigure.getBounds() );
}
/**
 * Test to make sure the handles are properly returned.
 */
public void testGetHandles() 
{
	assert( "There were handles returned.", forwardFigure.getHandles().length == 0 );
}
/**
 * Test to make sure the height of the figure is properly
 * returned.
 */
public void testGetHeight() 
{
	assertEquals( "The height was not what was expected", 19, forwardFigure.getHeight() );
}
/**
 * Test to make sure the leftmost coordinate of the figure
 * is properly returned.
 */
public void testGetLeft() 
{
	assertEquals( "The int returned does not correspond to the expected leftmost coordinate of the figure", 31, forwardFigure.getLeft() );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testGetLocator() 
{
	Locator loc = forwardFigure.getLocator();
	assert( "There was no locator returned.", loc != null );
	assertEquals( "The locator returned does not correspond to the top left corner of this figure. If that is OK, you need to override this test method.", 31, loc.x() );
	assertEquals( "The locator returned does not correspond to the top left corner of this figure. If that is OK, you need to override this test method.", 31, loc.y() );
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
	assertEquals( "The size was not what was expected", new Dimension( 19, 19 ), forwardFigure.getSize() );
}
/**
 * Test to make sure the top is properly returned.
 */
public void testGetTop() 
{
	assertEquals( "The int returned does not correspond to the expected topmost coordinate of the figure", 31, forwardFigure.getTop() );
}
/**
 * Test to make sure the width of the figure is properly
 * returned.
 */
public void testGetWidth() 
{
	assertEquals( "The width was not what was expected", 19, forwardFigure.getWidth() );
}
/**
 * Test to make sure the intersects( Figure ) method is
 * functioning properly.
 */
public void testIntersectsFigure() 
{
	Figure testFigure = new RectangleShape( 43, 43, 2, 2 );
	assert( "Figure does not intersect the figure", forwardFigure.intersects( testFigure ) );
	testFigure.setBounds( 29, 29, 11, 11 );
	assert( "Figure does not intersect the figure", forwardFigure.intersects( testFigure ) );
	testFigure.setBounds( 16, 16, 6, 6 );
	assert( "Figure intersects the figure", ! forwardFigure.intersects( testFigure ) );
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectsRectangle() 
{
	Rectangle testRect = new Rectangle( 43, 43, 2, 2 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 29, 29, 11, 11 );
	assert( "Figure does not intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 16, 16, 6, 6 );
	assert( "Figure intersects the rectangle", ! figure.intersects( testRect ) );
}
/**
 * Test to make sure the isWithin( Figure ) method is
 * functioning properly.
 */
public void testIsWithinFigure() 
{
	Figure testFigure = new RectangleShape( 30, 30, 21, 21 );
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
	Rectangle testRect = new Rectangle( 30, 30, 21, 21 );
	assert( "Figure isn't within the rectangle", figure.isWithin( testRect ) );
	testRect.setBounds( 5, 5, 2, 2 );
	assert( "Figure is within the rectangle", ! figure.isWithin( testRect ) );
}
/**
 * ArrowTest constructor comment.
 * @param name java.lang.String
 */
public void testLineMoved() {
	assertEquals( "Arrows original bounds are not correct", new Rectangle( 31, 31, 19, 19 ), figure.getBounds() );
	lineFigure.translate( 5, 5 );
	assertEquals( "Arrows bounds are not correct", new Rectangle( 36, 36, 19, 19 ), figure.getBounds() );
}
/**
 * Test to make sure locatorAt returns the proper value.
 */
public void testLocatorAt() 
{
	Locator locator = figure.locatorAt( 0, 0 );
	if( ! ( locator instanceof FigureRelativePoint ) ) {
		assert( "The locator returned isn't a FigureRelativePoint", false );
		return;
	}
	FigureRelativePoint relativeLocator = (FigureRelativePoint) locator;
	assert( "The relativeLocator returned doesn't have the figure as its owner", relativeLocator.getFigure() == figure );
	assert( "The relativeLocator has an improper x", relativeLocator.x() == 0 );
	assert( "The relativeLocator has an improper y", relativeLocator.y() == 0 );
	lineFigure.translate( 1, 1 );
	assert( "The relativeLocator improperly adjusted its x", relativeLocator.x() == 1 );
	assert( "The relativeLocator improperly adjusted its y", relativeLocator.y() == 1 );
}
/**
 * Test to make sure move( int, int ) works correctly.
 */
public void testMoveIntInt() 
{
	figure.move( 20, 20 );
	Locator loc = figure.getLocator();
	assertEquals( "The figure did not move properly", 31, loc.x() );
	assertEquals( "The figure did not move properly", 31, loc.y() );
}
/**
 * Test to make sure move( Locator ) works correctly.
 */
public void testMoveLocator()
{
	figure.move( new DrawingPoint( 20, 20 ) );
	Locator loc = figure.getLocator();
	assertEquals( "The figure did not move properly", 31, loc.x() );
	assertEquals( "The figure did not move properly", 31, loc.y() );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testRequestConnection() 
{
	Locator locator = figure.requestConnection( new RectangleShape(), 0, 0 );
	assert( "There was no locator returned.", locator != null );
	assertEquals( "The locator returned does not correspond to the center of this figure. If that is OK, you need to override this test method.", 40, locator.x() );
	assertEquals( "The locator returned does not correspond to the center of this figure. If that is OK, you need to override this test method.", 40, locator.y() );
	assert( "The locator returned is not a FigureRelativePoint. If that is OK, you need to override this test method.", locator instanceof FigureRelativePoint );
	FigureRelativePoint relativeLocator = (FigureRelativePoint) locator;
	assertEquals( "The locator returned does not have the figure as its owner.", figure, relativeLocator.getFigure() );
	lineFigure.translate( 1, 1 );
	assertEquals( "The locator did not update when the figure moved.", 41, relativeLocator.x() );
	assertEquals( "The locator did not update when the figure moved.", 41, relativeLocator.y() );
}
/**
 * Test to make sure the bounds are properly set.
 */
public void testSetBounds()
{
	figure.setBounds( 50, 50, 50, 50 );
	assertEquals( "The figure's bounds changed", new Rectangle( 31, 31, 19, 19 ), figure.getBounds() );
	
}
/**
 * Test to make sure the setSize( Dimension ) method is
 * working properly.
 */
public void testSetSizeDimension()
{
	figure.setSize( new Dimension( 100, 100 ) );
	assertEquals( "The figure's size changed", new Dimension( 19, 19 ), figure.getSize() );
}
/**
 * Test to make sure the setSize( int, int ) method is
 * working properly.
 */
public void testSetSizeIntInt()
{
	figure.setSize( 100, 100 );
	assertEquals( "The figure's size changed", new Dimension( 19, 19 ), figure.getSize() );
}
/**
 * Test to make sure translate works correctly.
 */
public void testTranslate() 
{
	translateByExpecting( 5, 5 , 31, 31 );
	translateByExpecting( -5, -5, 31, 31);
}
}
