package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)TS_InputEventHandlerTests.java
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
import com.rolemodelsoft.test.*;
/**
 * Runs all tests in com.rolemodelsoft.drawlet.basics
 */
public class TS_InputEventHandlerTests extends junit.framework.TestCase {

public TS_InputEventHandlerTests(String name) {
	super(name);
}

public static void main(java.lang.String[] args) {
	TestRunnerHelper.run();
}

public static TestSuite suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new TestSuite(TC_AbstractFigure.class));
	suite.addTest(new TestSuite(TC_CanvasTool.class));
	suite.addTest(new TestSuite(TC_DrawingPoint.class));
	suite.addTest(new TestSuite(TC_EdgeLocator.class));
	suite.addTest(new TestSuite(TC_FigureRelativePoint.class));
	suite.addTest(new TestSuite(TC_PolarCoordinate.class));
	suite.addTest(new TestSuite(TC_SimpleDrawing.class));
	suite.addTest(new TestSuite(TC_SimpleDrawingCanvas.class));
	
	return suite;
}
}
