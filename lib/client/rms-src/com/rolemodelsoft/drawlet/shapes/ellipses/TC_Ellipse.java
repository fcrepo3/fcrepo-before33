package com.rolemodelsoft.drawlet.shapes.ellipses;

/**
 * @(#)TC_Ellipse.java
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
import com.rolemodelsoft.drawlet.shapes.ellipses.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import java.awt.*;
import junit.framework.*;
import junit.ui.*;

public class TC_Ellipse extends TC_AbstractFigure {
	/**
	 */
	public TC_Ellipse(String name) {
		super(name);
	}
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
	protected void setUp() 
	{
		figure = new Ellipse(5,5,10,10);
		propertyRevealer = new PropertyChangeRevealer();
		locationRevealer = new RelatedLocationRevealer();
	}
/**
 * Test to make sure the contains( Figure ) method is
 * functioning properly.
 */
public void testContainsFigure() 
{
	Figure testFigure = new RectangleShape( 9, 9, 2, 2 );
	assert( "Figure does not contain the figure", figure.contains( testFigure ) );
	testFigure.setBounds( 6, 6, 13, 13 );
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
	assert( "Figure contains the point 7, 7", ! figure.contains( 5, 6 ) );
	
	assert( "Figure contains the point 13, 7", ! figure.contains( 15, 6 ) );
	
	assert( "Figure contains the point 13, 13", ! figure.contains( 15, 14 ) );
	
	assert( "Figure contains the point 7, 13", ! figure.contains( 5, 14 ) );

	assert( "Figure does not contain the point 6, 6", ! figure.contains( 6, 6 ) );

	assert( "Figure contains the point 8, 8", figure.contains( 8, 8) );

	assert( "Figure does contain the point 7, 7", figure.contains( 7, 7) );
}
	/**
	 * Test to make sure the contains( Rectangle ) method is
	 * functioning properly.
	 */
	public void testContainsRectangle() 
	{
		Rectangle testRect = new Rectangle( 9, 9, 2, 2 );
		assert( "Figure does not contain the rectangle", figure.contains( testRect ) );
		testRect.setBounds( 7, 7, 13, 13 );
		assert( "Figure contains the rectangle", ! figure.contains( testRect ) );
		testRect.setBounds( 6, 10, 5, 4 );
		assert( "Figure contains the rectangle", ! figure.contains( testRect ) );
	}
/**
 * Test to make sure the intersects( Figure ) method is
 * functioning properly.
 */
public void testIntersectsFigure() 
{
	Figure testFigure = new RectangleShape( 6, 6, 2, 2 );
	assert( "Ellipse does not intersect the figure", figure.intersects( testFigure ) );
	testFigure.setBounds( 2, 2, 4, 4 );
	assert( "Ellipse does intersect the figure", ! figure.intersects( testFigure ) );
	testFigure.setBounds( 16, 16, 6, 6 );
	assert( "Ellipse intersects the figure", ! figure.intersects( testFigure ) );
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectsRectangle() 
{
	Rectangle testRect = new Rectangle( 8, 8, 2, 2 );
	assert( "Figure should intersect the rectangle", figure.intersects( testRect ) );

	// check rectangles near each of the four corners that would intersect if it weren't for rounded corners
	testRect.setBounds( 4, 4, 2, 2 );
	assert( "Figure should not intersect the rectangle (though bounds overlaps)", !figure.intersects( testRect ) );
	testRect.setBounds( 14, 14, 6, 6 );
	assert( "Figure should not intersect the rectangle", ! figure.intersects( testRect ) );
	testRect.setBounds( 14, 0, 6, 6 );
	assert( "Figure should not intersect the rectangle", ! figure.intersects( testRect ) );
	testRect.setBounds( 0, 14, 6, 6 );
	assert( "Figure should not intersect the rectangle", ! figure.intersects( testRect ) );

	testRect.setBounds( 2, 2, 10, 10 );
	assert( "Figure should intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 16, 16, 6, 6 );
	assert( "Figure should not intersect the rectangle", ! figure.intersects( testRect ) );
	testRect.setBounds( 6, 6, 3, 3);
	assert( "Figure should intersect the rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 5, 5, 10, 10);
	assert( "Ellipse should intersect its bounding rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 5, 5, 11, 11);
	assert( "Ellipse should intersect its bounding rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 4, 4, 12, 12);
	assert( "Ellipse should intersect its bounding rectangle", figure.intersects( testRect ) );
	testRect.setBounds( 5, 5, 9, 9);
	assert( "Ellipse should intersect its bounding rectangle", figure.intersects( testRect ) );
}
}
