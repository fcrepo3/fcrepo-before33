package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)TC_ArrowStyle.java
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
import junit.framework.*;
import junit.ui.*;

public class TC_ArrowStyle extends TestCase {
	Locator[] locs;
/**
 * ArrowStyleTest constructor comment.
 * @param name java.lang.String
 */
public TC_ArrowStyle(String name) {
	super(name);
}
	/**
	 */
	public void setUp() {
		locs = new Locator[] {
			new PolarCoordinate( 10, 0.0 + Math.PI ),
			new PolarCoordinate( 20, 0.5 + Math.PI ),
			new PolarCoordinate( 0, 0.0 ),
			new PolarCoordinate( 20, -0.5 + Math.PI )
		};

		ArrowStyle.setDefaultLocators( locs );
	}
	/**
	 */
	public void testGetLocators() {
		ArrowStyle style = new ArrowStyle();
		for ( int i = 0; i < locs.length; i++ ) {
			assertEquals( "The locators returned were incorrect", (int)locs[i].theta(), (int)style.getLocators()[i].theta() );
			assertEquals( "The locators returned were incorrect", locs[i].r(), style.getLocators()[i].r() );
		}
	}
	/**
	 */
	public void testIsOpaque() {
		assert( "The opacity was incorrect", (new ArrowStyle()).isOpaque() );
	}
	/**
	 */
	public void testSetDefaultLocators() {
		Locator[] locs = new Locator[] {
			new PolarCoordinate( 10, 0.5 ),
			new PolarCoordinate( 0, 0 ),
			new PolarCoordinate( 10, -0.5 )
		};

		ArrowStyle style = new ArrowStyle();
		
		assert( "The right locators were already in place", (int)locs[0].theta() != (int)style.getLocators()[0].theta() );

		ArrowStyle.setDefaultLocators( locs );
		assert( "The right locators were propagated.",  (int)locs[0].theta() != (int)style.getLocators()[0].theta() );
		style = new ArrowStyle();
		assertEquals( "The wrong locators were returned", locs, style.getLocators() );
	}
	/**
	 */
	public void testSetOpaque() {
		ArrowStyle style = new ArrowStyle();
		style.setOpaque( false );
		assert( "The opacity was not set.", ! style.isOpaque() );
	}
}
