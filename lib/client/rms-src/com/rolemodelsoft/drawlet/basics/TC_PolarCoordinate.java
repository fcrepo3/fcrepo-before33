package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TC_PolarCoordinate.java
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
public class TC_PolarCoordinate extends TestCase {
	PolarCoordinate loc1, loc2, loc3;

	public TC_PolarCoordinate(String name) {
		super(name);
	}

	public void setUp() {
		loc1 = new PolarCoordinate( 0, 0.0 );
		loc2 = new PolarCoordinate( 10, 0.5 );
		loc3 = new PolarCoordinate( 40, -0.5 );
	}

	public void testDuplicate() {
		Object obj = loc1.duplicate();
		assert( "The object wasn't a drawingpoint", obj instanceof PolarCoordinate );
		PolarCoordinate loc = (PolarCoordinate)obj;
		assertEquals( loc1.x(), loc.x() );
		assertEquals( loc1.y(), loc.y() );
	}

	public void testDuplicateIn() {
		Hashtable hash = new Hashtable();
		loc1.duplicateIn( hash );
		Object obj = hash.get( loc1 );
		assert( "The object wasn't a drawingpoint", obj instanceof PolarCoordinate );
		PolarCoordinate loc = (PolarCoordinate)obj;
		assertEquals( loc1.x(), loc.x() );
		assertEquals( loc1.y(), loc.y() );
	}

	public void testPostDuplicate() {
	}

	public void testR() {
		assertEquals( 0, loc1.r() );
		assertEquals( 10, loc2.r() );
		assertEquals( 40, loc3.r() );
		
	}

	public void testTheta() {
		assertEquals( new Double( 0.0 ), new Double( loc1.theta() ) );
		assertEquals( new Double( 0.5 ), new Double( loc2.theta() ) );
		assertEquals( new Double( -0.5 ), new Double( loc3.theta() ) );
		
	}

	public void testToString() {
		assertEquals( "com.rolemodelsoft.drawlet.basics.PolarCoordinate [x=0,y=0;r=0,theta=0.0]", loc1.toString() );
		assertEquals( "com.rolemodelsoft.drawlet.basics.PolarCoordinate [x=8,y=4;r=10,theta=0.5]", loc2.toString() );
	}

	public void testX() {
		assertEquals( 0, loc1.x() );
		assertEquals( 8, loc2.x() );
		assertEquals( 35, loc3.x() );
	}

	public void testY() {
		assertEquals( 0, loc1.y() );
		assertEquals( 4, loc2.y() );
		assertEquals( -19, loc3.y() );
		
	}
}
