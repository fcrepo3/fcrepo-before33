package com.rolemodelsoft.drawlet.shapes.polygons;

/**
 * @(#)TS_PolygonTests.java
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

import junit.framework.*;
import com.rolemodelsoft.test.*;

public class TS_PolygonTests extends junit.framework.TestCase {

public TS_PolygonTests(String name) {
	super(name);
}

public static void main(java.lang.String[] args) {
	TestRunnerHelper.run();
}

public static TestSuite suite() {
	TestSuite suite = new TestSuite();
	
	suite.addTest(new TestSuite(TC_Polygon.class));
	
	return suite;
}
}
