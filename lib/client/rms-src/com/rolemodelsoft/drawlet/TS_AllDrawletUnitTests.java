package com.rolemodelsoft.drawlet;

/**
 * @(#)TS_AllDrawletUnitTests.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import com.rolemodelsoft.drawlet.util.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.shapes.rectangles.*;
import com.rolemodelsoft.drawlet.shapes.polygons.*;
import com.rolemodelsoft.drawlet.shapes.lines.*;
import com.rolemodelsoft.drawlet.text.*;
import com.rolemodelsoft.drawlet.shapes.ellipses.*;
import junit.framework.*;
import com.rolemodelsoft.test.*;

/**
 * @version 	1.1.6, 12/29/98
 */
 
 public class TS_AllDrawletUnitTests extends TestCase {
	/**
	 * @param test the test.
	 */
	public TS_AllDrawletUnitTests( String test ) {
		super( test );
	}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	TestRunnerHelper.run();
}
	/**
	 * @return a Test representation of a suite of tests.
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(TS_InputEventHandlerTests.suite());
		suite.addTest(TS_EllipseTests.suite());
		suite.addTest(TS_LineTests.suite());
		suite.addTest(TS_PolygonTests.suite());
		suite.addTest(TS_RectangleTests.suite());
		suite.addTest(TS_TextTests.suite());
		suite.addTest(TS_UtilityTests.suite());
		
		return suite;
	}
}
