package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_DrawingPoint.java
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
import junit.framework.*;
import junit.ui.*;
import java.util.*;
/**
 * 
 */
public class TC_DrawingPoint extends TestCase {
	DrawingPoint loc1, loc2, loc3;
	/**
	 * @param name java.lang.String
	 */
	public TC_DrawingPoint(String name) {
		super(name);
	}

	public void setUp() {
		loc1 = new DrawingPoint( 0, 0 );
		loc2 = new DrawingPoint( 10, 10 );
		loc3 = new DrawingPoint( -10, -10 );
	}
	/**
	 *
	 */
	public void testDuplicate() {
		Object obj = loc1.duplicate();
		assert( "The object wasn't a drawingpoint", obj instanceof DrawingPoint );
		DrawingPoint loc = (DrawingPoint)obj;
		assertEquals( loc1.x(), loc.x() );
		assertEquals( loc1.y(), loc.y() );
	}
	/**
	 *
	 */
	public void testDuplicateIn() {
		Hashtable hash = new Hashtable();
		loc1.duplicateIn( hash );
		Object obj = hash.get( loc1 );
		assert( "The object wasn't a drawingpoint", obj instanceof DrawingPoint );
		DrawingPoint loc = (DrawingPoint)obj;
		assertEquals( loc1.x(), loc.x() );
		assertEquals( loc1.y(), loc.y() );
	}
	/**
	 *
	 */
	public void testMoveIntInt() {
		loc1.move( 5, 5 );
		assertEquals( 5, loc1.x() );
		assertEquals( 5, loc1.y() );
		
	}
	/**
	 *
	 */
	public void testPostDuplicate() {
	}
	/**
	 *
	 */
	public void testR() {
		assertEquals( 0, loc1.r() );
		assertEquals( 14, loc2.r() );
		assertEquals( 14, loc3.r() );
		
	}
	/**
	 *
	 */
	public void testTheta() {
		assertEquals( new Double( Double.NaN ), new Double( loc1.theta() ) );
		assertEquals( new Double( 0.7853981633974483 ), new Double( loc2.theta() ) );
		assertEquals( new Double( 3.9269908169872414 ), new Double( loc3.theta() ) );
		
	}
	/**
	 *
	 */
	public void testToString() {
		assertEquals( "com.rolemodelsoft.drawlet.basics.DrawingPoint [x=0,y=0;r=0,theta=NaN]", loc1.toString() );
		assertEquals( "com.rolemodelsoft.drawlet.basics.DrawingPoint [x=10,y=10;r=14,theta=0.7853981633974483]", loc2.toString() );
	}
	/**
	 *
	 */
	public void testTranslateIntInt() {
		loc2.translate( 5, 5 );
		assertEquals( 15, loc2.x() );
		assertEquals( 15, loc2.y() );
		
	}
	/**
	 *
	 */
	public void testX() {
		assertEquals( 0, loc1.x() );
		assertEquals( 10, loc2.x() );
		assertEquals( -10, loc3.x() );
	}
	/**
	 *
	 */
	public void testXInt() {
		loc1.x( 5 );
		assertEquals( 5, loc1.x() );
	}
	/**
	 *
	 */
	public void testY() {
		assertEquals( 0, loc1.y() );
		assertEquals( 10, loc2.y() );
		assertEquals( -10, loc3.y() );
		
	}
	/**
	 *
	 */
	public void testYInt() {
		loc1.y( 5 );
		assertEquals( 5, loc1.y() );
	}
}
