package com.rolemodelsoft.drawlet.util;

/**
 * @(#)SegmentIntersectionTestVisualizer.java
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

import com.rolemodelsoft.drawlet.examples.awt.*;
import java.awt.*;

public class SegmentIntersectionTestVisualizer extends ExitingFrame {
	protected int[] Line;
	protected int[][] TestLine;
/**
 * PolygonTestVisualizer constructor comment.
 */
public SegmentIntersectionTestVisualizer() {
	super();
}
/**
 * PolygonTestVisualizer constructor comment.
 * @param title java.lang.String
 */
public SegmentIntersectionTestVisualizer(int[] Line, int[][] TestLine) {
	super();
	this.Line = Line;
	this.TestLine = TestLine;
}
/**
 * PolygonTestVisualizer constructor comment.
 * @param title java.lang.String
 */
public SegmentIntersectionTestVisualizer(String title) {
	super(title);
}
/**
 * 
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	g.setColor(Color.black);
	for(int i = 0; i < TestLine.length; i++)
		g.drawLine(TestLine[i][0], TestLine[i][1], TestLine[i][2], TestLine[i][3]);
	g.setColor(Color.red);
	g.drawLine(Line[0], Line[1], Line[2], Line[3]);
}
}
