package com.rolemodelsoft.drawlet.shapes.polygons;

/**
 * @(#)TC_Polygon.java
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
import com.rolemodelsoft.drawlet.shapes.polygons.*;
import java.awt.*;
import junit.framework.*;
import junit.ui.*;

public class TC_Polygon extends TC_AbstractFigure {
	protected static boolean run;
/**
 * PolygonTest constructor comment.
 */
public TC_Polygon(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
protected void setUp() 
{
	int[] intx = {5, 15, 15, 5};
	int[] inty = {5, 5, 15, 15};
	Polygon p = new Polygon(intx, inty, 4);
	figure = new PolygonShape(p);
	propertyRevealer = new PropertyChangeRevealer();
	locationRevealer = new RelatedLocationRevealer();
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectRectPolygon1() 
{
	Polygon p1 = new Polygon(new int[] {50, 100, 100, 50}, new int[] {100, 100, 200, 200}, 4);
	PolygonShape poly1 = new PolygonShape(p1);
	Rectangle rects1[] = new Rectangle[] {
		// sides and corners
		new Rectangle(40,90,10,10),
		new Rectangle(100,90,10,10),
		new Rectangle(100,200,10,10),
		new Rectangle(40,200,10,10),
		new Rectangle(70,90,10,10),
		new Rectangle(40,145,10,10),
		new Rectangle(100,145,10,10),
		new Rectangle(70,200,10,10),

		// other stuff
		new Rectangle(60,95,30,10),
		new Rectangle(95,110,10,80),
		new Rectangle(60,195,30,10),
		new Rectangle(45,110,10,80),
		new Rectangle(70,120,10,60),
		new Rectangle(30,80,90,140),
		new Rectangle(45,95,10,10),
		new Rectangle(95,95,10,10),
		new Rectangle(95,195,10,10),
		new Rectangle(45,195,10,10),
		new Rectangle(43,93,64,14),
		new Rectangle(43,193,64,14),
		new Rectangle(41,91,16,118),
		new Rectangle(93,91,16,118),
		new Rectangle(50,100,50,100)
	};
	visualize(p1,rects1,"Polygon1 Intersection");
	for(int i = 0; i < rects1.length; i++) {
		if (!poly1.intersects( rects1[i] ) )
			assert( "Perpendicular polygon should intersect the rectangle", poly1.intersects( rects1[i] ) );
	}
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectRectPolygon2() 
{
	int[] intx2 = {150, 200, 250, 300, 350, 175};
	int[] inty2 = {250, 200, 150, 110, 300, 120};
	Polygon p2 = new Polygon(intx2, inty2, 6);	
	PolygonShape poly2 = new PolygonShape(p2);
	Rectangle rects2[] = new Rectangle[] {
		 //corner and sides
		new Rectangle(300,100,10,10),
		new Rectangle(290,100,10,10),
		new Rectangle(350,300,10,10),
		new Rectangle(345,300,10,10),
		new Rectangle(140,245,10,10),
		new Rectangle(140,250,10,10),
		new Rectangle(170,110,10,10),
		
		new Rectangle(145,245,15,10),
		new Rectangle(170,115,15,10),
		new Rectangle(340,200,25,115),
		new Rectangle(140,180,15,90),
		new Rectangle(185,200,25,25),
		new Rectangle(235,200,25,25),
		new Rectangle(195,130,20,20),
		new Rectangle(270,105,20,20),
		new Rectangle(270,145,20,20),
		new Rectangle(130,100,240,225)
	};
	visualize(p2,rects2,"Polygon2 Intersection");
	for(int i = 0; i < rects2.length; i++) {
		assert( "Skewed polygon should intersect the rectangle", poly2.intersects( rects2[i] ) );
	}
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectRectPolygon3() 
{
	int[] intx3 = {100, 300, 300, 100, 200, 200};
	int[] inty3 = {100, 100, 200, 200, 50, 250};
	Polygon p3 = new Polygon(intx3, inty3, 6);
	PolygonShape poly3 = new PolygonShape(p3);
	Rectangle rects3[] = new Rectangle[] {
		new Rectangle(145,155,20,20),
		new Rectangle(145,125,20,20),
		new Rectangle(173,95,9,20),
		new Rectangle(173,185,9,20),
		new Rectangle(185,125,20,9),
		new Rectangle(185,100,9,20),
		new Rectangle(185,180,9,20),
		new Rectangle(180,137,20,9),
	};
	visualize(p3,rects3,"Polygon3 Intersection");
	for(int i = 0; i < rects3.length; i++) {
		assert( "Hollow polygon should intersect the rectangle", poly3.intersects( rects3[i] ) );
	}
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testIntersectsRectangle() 
{
	testIntersectRectPolygon1();
	testIntersectRectPolygon2();
	testIntersectRectPolygon3();
	testNonIntersectRectPolygon1();
	testNonIntersectRectPolygon2();
	testNonIntersectRectPolygon3();
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testNonIntersectRectPolygon1() 
{
	Polygon p1 = new Polygon(new int[] {50, 100, 100, 50}, new int[] {100, 100, 200, 200}, 4);
	PolygonShape poly1 = new PolygonShape(p1);
	Rectangle rects1[] = new Rectangle[] {
		new Rectangle(39,90,10,10),
		new Rectangle(100,89,10,10),
		new Rectangle(100,201,10,10),
		new Rectangle(39,200,10,10),
		new Rectangle(70,89,10,10),
		new Rectangle(39,145,10,10),
		new Rectangle(101,145,10,10),
		new Rectangle(70,201,10,10),
	};
	visualize(p1,rects1,"Polygon1 Non-Intersection");
	for(int i = 0; i < rects1.length; i++) {
		assert( "Perpendicular polygon should not intersect the rectangle", !poly1.intersects( rects1[i] ) );
	}
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testNonIntersectRectPolygon2() 
{
	int[] intx2 = {150 , 200 , 250 , 300, 350, 175};
	int[] inty2 = {250 , 200 , 150 , 110, 300, 120};
	Polygon p2 = new Polygon(intx2, inty2, 6);
	PolygonShape poly2 = new PolygonShape(p2);
	Rectangle rects2[] = new Rectangle[] {
		new Rectangle(225,150,10,10),
		new Rectangle(225,185,10,10),
		new Rectangle(158,130,10,10),
		new Rectangle(310,130,10,10),
		new Rectangle(180,222,10,10),
		new Rectangle(260,222,10,10),
		new Rectangle(170,109,10,10),
		new Rectangle(139,245,10,10),
		new Rectangle(351,300,10,10),
	};
	visualize(p2,rects2,"Polygon2 Non-Intersection");
	for(int i = 0; i < rects2.length; i++) {
		assert( "Skewed polygon should not intersect the rectangle", ! poly2.intersects( rects2[i] ) );
	}
}
/**
 * Test to make sure the intersects( Rectangle ) method is
 * functioning properly.
 */
public void testNonIntersectRectPolygon3() 
{
	int[] intx3 = {100, 300, 300, 100, 200, 200};
	int[] inty3 = {100, 100, 200, 200, 50, 250};
	Polygon p3 = new Polygon(intx3, inty3, 6);
	PolygonShape poly3 = new PolygonShape(p3);
	Rectangle rects3[] = new Rectangle[] {
		new Rectangle(155,140,20,20),
		new Rectangle(173,101,20,20),
		new Rectangle(173,179,20,20),
		new Rectangle(179,125,20,20)
	};
	visualize(p3,rects3,"Polygon3 Non-Intersection");
	for(int i = 0; i < rects3.length; i++) {
		assert( "Hollow polygon should not intersect the rectangle", !poly3.intersects( rects3[i] ) );
	}
}
/**
 * Paint the polygon and rectangles to see what we are trying to test... visually verifying
 * This method is basically used to see why the unit tests may be failing.
 * To run them modify the run field to equal true.
 */
protected static void visualize(Polygon p1, Rectangle[] rects, String title) {
	if (!run)
		return;
	PolygonTestVisualizer visualizer = new PolygonTestVisualizer(p1,rects);
	visualizer.setBounds(0,75,400,350);
	visualizer.setTitle(title);
	visualizer.show();
	visualizer.toFront();
}
}
