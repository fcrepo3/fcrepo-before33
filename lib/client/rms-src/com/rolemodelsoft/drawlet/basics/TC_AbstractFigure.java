package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_AbstractFigure.java
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
import java.awt.*;
import junit.framework.*;


public class TC_AbstractFigure extends TestCase {
	protected AbstractFigure figure;
	protected PropertyChangeRevealer propertyRevealer;
	protected RelatedLocationRevealer locationRevealer;
/**
 * BasicStringRendererTest constructor comment.
 * @param name java.lang.String
 */
public TC_AbstractFigure(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
protected void setUp() 
{
	figure = new RectangleShape();
	figure.setBounds( 5, 5, 10, 10 );
	propertyRevealer = new PropertyChangeRevealer();
	locationRevealer = new RelatedLocationRevealer();
}
/**
 * Test to make sure PropertyChangeListeners are being added
 * correctly.
 */
public void testAddPropertyChangeListener() 
{
	figure.addPropertyChangeListener( propertyRevealer );
	figure.translate( 5, 5 );
	assert( "No PropertyChangeEvent propagated", propertyRevealer.getEventCount() == 1 );

	propertyRevealer.clearEventCount();
	figure.addPropertyChangeListener( propertyRevealer );
	figure.translate( 5, 5 );
	assert( "Two PropertyChangeEvents propagated", propertyRevealer.getEventCount() == 1 );
}
/**
 * Test to make sure RelatedLocationListeners are being added
 * correctly.
 */
public void testAddRelatedLocationListener() 
{
	figure.addRelatedLocationListener( locationRevealer );
	figure.translate( 5, 5 );
	assert( "No RelatedLocatorEvent propagated", locationRevealer.getEventCount() == 1 );

	locationRevealer.clearEventCount();
	figure.addRelatedLocationListener( locationRevealer );
	figure.translate( 5, 5 );
	assert( "Two RelatedLocatorEvents propagated", locationRevealer.getEventCount() == 1 );
}
/**
 * Test to make sure the contains( Figure ) method is
 * functioning properly.
 */
public void testContainsFigure() 
{
	Figure testFigure = new RectangleShape( 6, 6, 8, 8 );
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
	assert( "Figure does not contain the point", figure.contains( 6, 6 ) );
	assert( "Figure contains the point", ! figure.contains( 20, 20 ) );
}
/**
 * Test to make sure the contains( Rectangle ) method is
 * functioning properly.
 */
public void testContainsRectangle() 
{
	Rectangle testRect = new Rectangle( 6, 6, 8, 8 );
	assert( "Figure does not contain the rectangle", figure.contains( testRect ) );
	testRect.setBounds( 4, 4, 12, 12 );
	assert( "Figure contains the rectangle", ! figure.contains( testRect ) );
}
/**
 * Test to make sure that (a) the figure is firing the
 * disconnect event, and (b) that it then doesn't fire
 * anything else ( at least until someone else registers).
 */
public void testDisconnect() 
{
	figure.addRelatedLocationListener( locationRevealer );
	figure.disconnect();
	assert( "No disconnect event was fired", locationRevealer.getEventCount() == 1 );
	locationRevealer.clearEventCount();
	figure.translate( 5, 5 );
	assert( "A RelatedLocation event was fired", locationRevealer.getEventCount() == 0 );
}
/**
 * Test to make sure that the edit tool returned is null.
 * Subclasses should override if they return expect a
 * different result.
 */
public void testEditTool() 
{
	assert( "The edit tool returned was not null. You probably need to override testEditTool.", figure.editTool( 0, 0 ) == null );
}
/**
 * Test to make sure the bottom is properly returned.
 */
public void testGetBottom() 
{
	assertEquals( "The int returned does not correspond to the expected bottommost coordinate of the figure", 15, figure.getBottom() );
}
/**
 * Test to make sure the handles are properly returned.
 */
public void testGetHandles() 
{
	assert( "There were no handles returned.", figure.getHandles().length > 0 );
}
/**
 * Test to make sure the height of the figure is properly
 * returned.
 */
public void testGetHeight() 
{
	assertEquals( "The height was not what was expected", 10, figure.getHeight() );
}
/**
 * Test to make sure the leftmost coordinate of the figure
 * is properly returned.
 */
public void testGetLeft() 
{
	assertEquals( "The int returned does not correspond to the expected leftmost coordinate of the figure", 5, figure.getLeft() );
}
/**
 * Test to make sure the locator is properly returned.
 */
public void testGetLocator() 
{
	assert( "There was no locator returned.", figure.getLocator() != null );
	assert( "The locator returned does not correspond to the top left corner of this figure. If that is OK, you need to override this test method.", figure.getLocator().x() == 5 && figure.getLocator().y() == 5 );
}
/**
 * Test to make sure the rightmost coordinate of the figure
 * is properly returned.
 */
public void testGetRight() 
{
	assertEquals( "The int returned does not correspond to the expected rightmost coordinate of the figure", 15, figure.getRight() );
}
/**
 * Test to make sure the size of the figure is properly
 * returned.
 */
public void testGetSize() 
{
	assert( "The size was not what was expected; it was " + figure.getSize().toString() + "instead of 10 by 10.", figure.getSize().width == 10 && figure.getSize().height == 10 );
}
/**
 * Test to make sure the style returned is correct.
 */
public void testGetStyle() 
{
	assert( "The style returned was null. If this is allowable, you need to override this test method.", figure.getStyle() != null );
	assert( "The style returned was not a SimpleDrawingStyle. If this is allowable, you need to override this test method.", figure.getStyle() instanceof SimpleDrawingStyle );
}
/**
 * Test to make sure the top is properly returned.
 */
public void testGetTop() 
{
	assertEquals( "The int returned does not correspond to the expected topmost coordinate of the figure", 5, figure.getTop() );
}
/**
 * Test to make sure the width of the figure is properly
 * returned.
 */
public void testGetWidth() 
{
	assertEquals( "The width was not what was expected.", 10, figure.getWidth() );
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
	testFigure.setBounds( 16, 16, 6, 6 );
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
	testRect.setBounds( 16, 16, 6, 6 );
	assert( "Figure intersects the rectangle", ! figure.intersects( testRect ) );
}
/**
 * Test to make sure the isObsolete() is functioning
 * properly.
 */
public void testIsObsolete() 
{
	assert( "The figure was obselete; if that is a valid state, you should override this test method.", ! figure.isObsolete() );
}
/**
 * Test to make sure the isWithin( Figure ) method is
 * functioning properly.
 */
public void testIsWithinFigure() 
{
	Figure testFigure = new RectangleShape( 4, 4, 12, 12 );
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
	Rectangle testRect = new Rectangle( 4, 4, 12, 12 );
	assert( "Figure isn't within the rectangle", figure.isWithin( testRect ) );
	testRect.setBounds( 5, 5, 2, 2 );
	assert( "Figure is within the rectangle", ! figure.isWithin( testRect ) );
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
	figure.translate( 1, 1 );
	assert( "The relativeLocator improperly adjusted its x", relativeLocator.x() == 1 );
	assert( "The relativeLocator improperly adjusted its y", relativeLocator.y() == 1 );
}
/**
 * Test to make sure move( int, int ) works correctly.
 */
public void testMoveIntInt() 
{
	figure.move( 10, 10 );
	Locator loc = figure.getLocator();
	assertEquals( "The figure did not move properly", 10, loc.x() );
	assertEquals( "The figure did not move properly", 10, loc.y() );
}
/**
 * Test to make sure move( Locator ) works correctly.
 */
public void testMoveLocator()
{
	figure.move( new DrawingPoint( 10, 10 ) );
	Locator loc = figure.getLocator();
	assertEquals( "The figure did not move properly", 10, loc.x() );
	assertEquals( "The figure did not move properly", 10, loc.y() );
}
/**
 * Test to make sure relatedLocationListeners
 * works correctly.
 */
public void testRelatedLocationListeners()
{
	assert( "The figure already had location listeners", ! figure.relatedLocationListeners().hasMoreElements() );
	figure.addRelatedLocationListener( locationRevealer );
	assert( "The figure did not have any location listeners", figure.relatedLocationListeners().hasMoreElements() );
	assert( "The figure did not properly add the location listener", ( (RelatedLocationRevealer) figure.relatedLocationListeners().nextElement() ) == locationRevealer );
}
/**
 * Test to make sure PropertyChangeListeners are being
 * removed correctly.
 */
public void testRemovePropertyChangeListener() 
{
	figure.addPropertyChangeListener( propertyRevealer );
	figure.removePropertyChangeListener( propertyRevealer );
	figure.translate( 5, 5 );
	assert( "A PropertyChangeEvent propagated", propertyRevealer.getEventCount() == 0 );
}
/**
 * Test to make sure RelatedLocationListeners are being
 * removed correctly.
 */
public void testRemoveRelatedLocationListener() 
{
	figure.addRelatedLocationListener( locationRevealer );
	figure.removeRelatedLocationListener( locationRevealer );
	figure.translate( 5, 5 );
	assert( "RelatedLocatorEvent propagated", locationRevealer.getEventCount() == 0 );
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
	assert( "The locator returned does not correspond to the center of this figure. If that is OK, you need to override this test method.", locator.x() == 10 && locator.y() == 10 );
	if ( locator instanceof FigureRelativePoint ) {
		FigureRelativePoint relativeLocator = (FigureRelativePoint) locator;
		assert( "The locator returned does not have the figure as its owner.", relativeLocator.getFigure() == figure );
		figure.translate( 1, 1 );
		assert( "The locator did not update when the figure moved.", relativeLocator.x() == 11 && relativeLocator.y() == 11 );
	} else {
		assert( "The locator returned is not a FigureRelativePoint. If that is OK, you need to override this test method.", false );
	}
}
/**
 * Test to make sure the bounds are properly set.
 */
public void testSetBounds()
{
	assert( "The figure's left side is incorrect.", figure.getLeft() == 5 );
	assert( "The figure's right side is incorrect.", figure.getRight() == 15 );
	assert( "The figure's top side is incorrect.", figure.getTop() == 5 );
	assert( "The figure's bottom side is incorrect.", figure.getBottom() == 15 );
	
}
/**
 * Test to make sure the setSize( Dimension ) method is
 * working properly.
 */
public void testSetSizeDimension()
{
	figure.setSize( new Dimension( 100, 100 ) );
	assert( "The figure's width is incorrect.", figure.getWidth() == 100 );
	assert( "The figure's height is incorrect.", figure.getHeight() == 100 );
}
/**
 * Test to make sure the setSize( int, int ) method is
 * working properly.
 */
public void testSetSizeIntInt()
{
	figure.setSize( 100, 100 );
	assert( "The figure's width is incorrect.", figure.getWidth() == 100 );
	assert( "The figure's height is incorrect.", figure.getHeight() == 100 );
}
/**
 * Test to make sure the setStyle method is working properly.
 */
public void testSetStyle()
{
	figure.addPropertyChangeListener( propertyRevealer );
	DrawingStyle oldStyle = figure.getStyle();
	DrawingStyle newStyle = new SimpleDrawingStyle();
	figure.setStyle( newStyle );
	assert( "The figure did not properly fire a property change event.", propertyRevealer.getEventCount() > 0 );
	assert( "The event's type is incorrect; it should be " + Figure.STYLE_PROPERTY + ", but it is " + propertyRevealer.getPropertyChangeEvent().getPropertyName() + " instead.", propertyRevealer.getPropertyChangeEvent().getPropertyName() == Figure.STYLE_PROPERTY );
	assert( "the event's newValue is incorrect", propertyRevealer.getPropertyChangeEvent().getNewValue() == newStyle );
}
/**
 * Test to make sure translate works correctly.
 */
public void testTranslate() 
{
	translateByExpecting( 5, 5 , 10, 10 );
	translateByExpecting( -5, -5, 5, 5);
}
/**
 * Test to make sure translate works correctly.
 */
protected void translateByExpecting(int startX, int startY, int expectedX, int expectedY) 
{
	figure.translate( startX, startY );
	assertEquals( "The figure did not translate properly", new Point( expectedX, expectedY ), new Point( figure.getLeft(), figure.getTop() ) );
}
}
