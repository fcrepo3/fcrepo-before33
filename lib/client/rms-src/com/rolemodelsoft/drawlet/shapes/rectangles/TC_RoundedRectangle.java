package com.rolemodelsoft.drawlet.shapes.rectangles;

/**
 * @(#)TC_RoundedRectangle.java
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
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import junit.framework.*;
import java.awt.Rectangle;

public class TC_RoundedRectangle extends TC_AbstractFigure {
/**
 * RoundedRectangleTest constructor comment.
 * @param name java.lang.String
 */
public TC_RoundedRectangle(String name) {
	super(name);
}
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
	protected void setUp() 
	{
		figure = new RoundedRectangleShape(5,5,10,10,4,6);
		propertyRevealer = new PropertyChangeRevealer();
		locationRevealer = new RelatedLocationRevealer();
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
	testFigure.setBounds( 5, 5, 9, 9 );
	assert( "Figure contains the figure", ! figure.contains( testFigure ) );
	testFigure.setBounds( 5, 10, 5, 4 );
	assert( "Figure contains the figure", ! figure.contains( testFigure ) );
}
/**
 * Test to make sure the contains( int, int ) method is
 * functioning properly.
 */
public void testContainsIntInt() 
{
	assert( "Figure does not contain the point 10, 10", figure.contains( 10, 10 ) );
	assert( "Figure contains the point 20, 20", ! figure.contains( 20, 20 ) );

	// check the corners
	assert( "Figure contains the point 5, 6", ! figure.contains( 5, 6 ) );
	assert( "Figure does not contain the point 6, 6", figure.contains( 6, 6 ) );
	
	assert( "Figure contains the point 15, 6", ! figure.contains( 15, 6 ) );
	assert( "Figure does not contain the point 14, 6", figure.contains( 14, 6 ) );
	
	assert( "Figure contains the point 15, 14", ! figure.contains( 15, 14 ) );
	assert( "Figure does not contain the point 14, 14", figure.contains( 14, 14 ) );
	
	assert( "Figure contains the point 5, 14", ! figure.contains( 5, 14 ) );
	assert( "Figure does not contain the point 6, 14", figure.contains( 6, 14 ) );

	assert( "Figure contains the point 10, 5", figure.contains( 10, 5 ) );
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
	testRect.setBounds( 5, 5, 9, 9 );
	assert( "Figure contains the figure", ! figure.contains( testRect ) );
	testRect.setBounds( 5, 10, 5, 4 );
	assert( "Figure contains the figure", ! figure.contains( testRect ) );
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
	testRect.setBounds( 4, 4, 1, 2 );
	assert( "RR does not intersect the rectangle", ! figure.intersects( testRect ) );
	testRect.setBounds( 4, 4, 2, 2 );
	assert( "RR does intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 5, 5, 10, 10 );
	assert( "RR does intersect its bounds", figure.intersects( testRect ) );
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
}
