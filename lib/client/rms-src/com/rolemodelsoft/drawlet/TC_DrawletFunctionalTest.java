package com.rolemodelsoft.drawlet;

/**
 * @(#)TC_DrawletFunctionalTest.java
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

import java.awt.*;
import java.beans.*;
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.shapes.polygons.*;
import com.rolemodelsoft.drawlet.shapes.lines.*;
import com.rolemodelsoft.drawlet.text.*;

public class TC_DrawletFunctionalTest extends junit.framework.TestCase {
	//DrawingCanvas canvas;
	Figure rectangle;
	final Rectangle rectangleStart = new Rectangle(80, 200, 87, 34);

	Figure triangle;
	final int triangleX[] = {5, 5, 20};
	final int triangleY[] = {5, 20, 13};
	final Point triangleLocation = new Point(5, 5);

	Figure pentagon;
	final int pentagonX[] = {100, 150, 220, 300, 160};
	final int pentagonY[] = {50, 75, 68, 49, 25};
	final Point pentagonLocation = new Point(100, 25);

	LineFigure line1;
	LineFigure line2;

	TextLabel text;


public TC_DrawletFunctionalTest(String name) {
	super(name);
}
	/**
	 * Sets up the tests.
	 */
	public void setUp() {
		// Create the triangle
		triangle = new PolygonShape( new Polygon( triangleX, triangleY, triangleX.length ) );

		// Create the rectangle
		rectangle = new RectangleShape();
		rectangle.setBounds( rectangleStart.x, rectangleStart.y, rectangleStart.width, rectangleStart.height );
		
		// Create the polygon
		pentagon = new PolygonShape( new Polygon( pentagonX, pentagonY, pentagonX.length ) );

		// Get the shape locators, for attaching the lines
		Locator triangleLoc = triangle.requestConnection( null, 0, 0 );
		Locator rectangleLoc = rectangle.requestConnection( null, 0, 0 );
		Locator pentagonLoc = pentagon.requestConnection( null, 0, 0 );

		// Create the first line, from the triangle to the rectangle
		line1 = new ConnectingLine( triangleLoc, rectangleLoc );

		// Get the locators for the new line, to attach the second line
		Locator lineLoc1 = line1.getLocator(0);
		Locator lineLoc2 = line1.getLocator(1);

		// Get the midpoint of the first line
		Point linePoint = new Point( ( Math.abs( lineLoc1.x() - lineLoc2.x() ) / 2 ) + Math.min( lineLoc1.x(), lineLoc2.x() ),
						( Math.abs( lineLoc1.y() - lineLoc2.y() ) / 2 ) + Math.min( lineLoc1.y(), lineLoc2.y() ) );

		// Create the second line, b/t the pentagon and the midpoint of the first line
		line2 = new ConnectingLine( 0, 0, 1, 1 );
		line2.setLocator( 0, line1.requestConnection( line2, linePoint.x, linePoint.y ) );
		line2.setLocator( 1, pentagonLoc );

		// Get the locators for the second line, to attach the text label
		lineLoc1 = line2.getLocator(0);
		lineLoc2 = line2.getLocator(1);

		// Get the midpoint of the second line
		linePoint.x = ( Math.abs( lineLoc1.x() - lineLoc2.x() ) / 2 ) + Math.min( lineLoc1.x(), lineLoc2.x() );
		linePoint.y = ( Math.abs( lineLoc1.y() - lineLoc2.y() ) / 2 ) + Math.min( lineLoc1.y(), lineLoc2.y() );

		// Attach a text label to the second line
		text = new TextLabel( "Test Label" );
		text.move( line2.requestConnection( text, linePoint.x, linePoint.y ) );
	}
	/**
	 * Tests to make sure line creation is working properly.
	 */
	public void testLineCreation() {
		line2 = null;

		Locator Loc1 = line1.getLocator(0);
		Locator Loc2 = line1.getLocator(1);
		Locator pentagonLoc = pentagon.getLocator();

		line2 = new ConnectingLine( 0, 0, 1, 1 );
		assert( "line2 is null", line2 != null );
		assert( "line2 is not a ConnectingLine", line2 instanceof ConnectingLine );

		assert( "line1 is null", line1 != null );

		Point linePoint = new Point( ( Math.abs( Loc1.x() - Loc2.x() ) / 2) + Math.min( Loc1.x(), Loc2.x() ),
						( Math.abs( Loc1.y() - Loc2.y() ) / 2 ) + Math.min( Loc1.y(), Loc2.y() ) );
		assert( "linePoint is null", linePoint != null );
		assert( "linePoint is not within line1;\n\tlinePoint: " + linePoint.toString() +
			"\n\tLoc1: [x=" + Integer.toString( Loc1.x() ) + ", y=" + Integer.toString( Loc1.y() ) + "]" +
			"\n\tLoc2: [x=" + Integer.toString( Loc2.x() ) + ", y=" + Integer.toString( Loc2.y() ) + "]",
				line1.contains( linePoint.x, linePoint.y ) );

		Locator lineLoc = line1.requestConnection( line2, linePoint.x, linePoint.y );
		assert( "lineLoc is null; " + linePoint.toString(), lineLoc != null );

		try {
			line2.addLocator( 0, pentagonLoc );
		}
		catch ( Exception e ) {
			assert( "Adding locator 0 to line2 failed; Exception: " + e.toString(), false );
		}

		try {
			line2.addLocator( 1, lineLoc );
		}
		catch ( Exception e ) {
			assert( "Adding locator 1 to line2 failed; Exception: " + e.toString(), false );
		}
	}
	/**
	 * Tests to see if the proper events are propagated when the triangle is
	 * moved.
	 */
	public void testMoveTriangle() {
	PropertyChangeRevealer triangleListener = new PropertyChangeRevealer();

	triangle.addPropertyChangeListener( triangleListener );

	triangle.translate( 5, 5 );

	assert( "There was no PropertyChangeEvent propagated", triangleListener.getPropertyChangeEvent() != null );
	}
	/**
	 * Tests to see if the pentagon is in the proper location.
	 */
	public void testPentagonLocation() {
	Point actualPentagonLocation = new Point( pentagon.getLocator().x(), pentagon.getLocator().y() );
	assertEquals( actualPentagonLocation, pentagonLocation );
	}
	/**
	 * Tests to see if the rectangle is in the proper location.
	 */
	public void testRectangleLocation() {
	Point actualRectangleLocation = new Point( rectangle.getLocator().x(), rectangle.getLocator().y() );
	Point assumedRectangleLocation = new Point( rectangleStart.x, rectangleStart.y );
	assertEquals( actualRectangleLocation, assumedRectangleLocation );
	}
	/**
	 * Tests to see if the triangle is in the proper location.
	 */
	public void testTriangleLocation() {
	Point actualTriangleLocation = new Point( triangle.getLocator().x(), triangle.getLocator().y() );
	assertEquals( actualTriangleLocation, triangleLocation );
	}
}
