package com.rolemodelsoft.drawlet.util;

/**
 * @(#)TS_SegmentIntersection.java
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

import com.rolemodelsoft.drawlet.util.GraphicsGeometry;
import java.awt.Toolkit;
import junit.framework.*;
import junit.ui.*;
import java.awt.*;
import junit.framework.*;



public class TC_SegmentIntersection extends TestCase {
	protected static boolean run = false;
/**
 * BasicStringRendererTest constructor comment.
 * @param name java.lang.String
 */
public TC_SegmentIntersection(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
protected void setUp() 
{

}
/**
 * 
 */
public void testCollinearIntersection() {
int segment1x1 = 50;
int segment1y1 = 50;
int segment1x2 = 50;
int segment1y2 = 100;
int segment2x1 = 50;
int segment2y1 = 30;
int segment2x2 = 50;
int segment2y2 = 70;
	
	assert( "Collinear lines should intersect", GraphicsGeometry.segmentIntersectsSegment(segment1x1, segment1y1, segment1x2, segment1y2, segment2x1, segment2y1, segment2x2, segment2y2) );
	
	}
/**
 * 
 */
public void testIntersectsLine1() {
	int[] line1 = {50, 50, 50, 100};
	int[][] cros1 = {	{50, 30, 50, 50},
						{50, 30, 50, 51},				
						{50, 100, 50, 130},
						{50, 99, 50, 130},
						{25, 30, 50, 50},
						{25, 30, 50, 51},
						{75, 30, 50, 50},
						{75, 30, 50, 51},
						{25, 130, 50, 100},
						{25, 130, 50, 99},
						{75, 130, 50, 100},
						{75, 130, 50, 99},
						{25, 50, 50, 50},
						{25, 50, 51, 50},
						{25, 50, 50, 51},
						{25, 50, 51, 51},
						{75, 50, 50, 50},
						{75, 50, 49, 50},
						{75, 50, 50, 51},
						{75, 50, 49, 51},
						{25, 100, 50, 100},
						{25, 100, 51, 100},
						{25, 100, 50, 99},
						{25, 100, 51, 99},
						{75, 100, 50, 100},
						{75, 100, 49, 100},
						{75, 100, 50, 99},
						{75, 100, 49, 99},
						{49, 50, 51, 100},
						{51, 50, 49, 100},
						{25, 50, 75, 100},
						{75, 50, 25, 100},
						{25, 60, 75, 90},
						{75, 60, 25, 90},
						{25, 40, 75, 110},
						{75, 40, 25, 110},
						{25, 50, 50, 75},
						{75, 50, 50, 75},
						{25, 100, 50, 75},
						{75, 100, 50, 75},
						{50, 50, 50, 100},
						{50, 30, 50, 130},
						{50, 60, 50, 90},
						{25, 75, 50, 75},
						{25, 75, 51, 75},
						{75, 75, 50, 75},
						{75, 75, 49, 75}
						};
	visualize(line1, cros1, "Line 1 Intersection");
	for(int i = 0; i < cros1.length; i++){
		assert( "Lines should intersect vertical", GraphicsGeometry.segmentIntersectsSegment(line1[0], line1[1], line1[2], line1[3], cros1[i][0], cros1[i][1], cros1[i][2], cros1[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine2() {
	int[] line2 = {100, 100, 200, 100};
	int[][] cros2 = {	{80, 100, 100, 100},
						{80, 100, 101, 100},
						{220, 100, 200, 100},
						{220, 100, 199, 100},
						{80, 80, 100, 100},
						{80, 80, 101, 100},
						{80, 120, 100, 100},
						{80, 120, 101, 100},
						{100, 80, 100, 100},
						{100, 80, 100, 101},
						{100, 80, 101, 100},
						{100, 80, 101, 101},						
						{100, 120, 100, 100},
						{100, 120, 100, 99},
						{100, 120, 101, 100},
						{100, 120, 101, 99},
						{200, 80, 200, 100},
						{200, 80, 200, 101},
						{200, 80, 199, 100},
						{200, 80, 199, 101},						
						{200, 120, 200, 100},
						{200, 120, 200, 99},
						{200, 120, 199, 100},
						{200, 120, 199, 99},
						{220, 80, 200, 100},
						{220, 80, 199, 100},
						{220, 120, 200, 100},
						{220, 120, 199, 100},
						{100, 100, 200, 100},
						{80, 100, 220, 100},
						{120, 100, 180, 100},
						{150, 80, 150, 100},
						{150, 80, 150, 101},
						{150, 80, 151, 100},
						{150, 80, 151, 101},
						{150, 120, 150, 100},
						{150, 120, 150, 99},
						{150, 120, 151, 100},
						{150, 120, 151, 99},
						{100, 99, 200, 101},
						{100, 101, 200, 99},
						{100, 80, 150, 100},
						{100, 80, 149, 101},
						{100, 120, 150, 100},
						{100, 120, 151, 99},
						{200, 80, 150, 100},
						{200, 80, 149, 101},
						{200, 120, 150, 100},
						{200, 120, 151, 99},
	};
	visualize(line2, cros2, "Line 2 Intersection");
	for(int i = 0; i < cros2.length; i++){
		assert( "Lines should intersect horizontal", GraphicsGeometry.segmentIntersectsSegment(line2[0], line2[1], line2[2], line2[3], cros2[i][0], cros2[i][1], cros2[i][2], cros2[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine3() {
	int[] line3 = {100, 50, 200, 45};
	int[][] cros3 = {	{80, 50, 100, 50},
						{80, 50, 101, 50},
						{80, 40, 100, 50},
						{80, 40, 101, 50},
						{80, 60, 100, 50},
						{80, 60, 110, 45},
						{100, 51, 200, 44},
						{100, 49, 200, 46},
						{100, 50, 200, 45},
						{120, 49, 140, 48},
						{80, 51, 100, 50},
						{80, 51, 120, 49},
						{80, 51, 220, 44},
						{220, 44, 200, 45},
						{220, 44, 180, 46},
						{220, 24, 200, 45},
						{220, 64, 200, 45},
						{220, 24, 199, 46},
						{220, 64, 199, 45},
						{100, 30, 100, 50},
						{100, 30, 100, 51},
						{100, 70, 100, 50},
						{100, 70, 100, 49},
						{100, 30, 101, 50},
						{100, 70, 101, 49},
						{200, 25, 200, 45},
						{200, 25, 200, 46},
						{200, 25, 199, 46},
						{200, 65, 200, 45},
						{200, 65, 199, 45},
						{140, 25, 140, 48},
						{140, 25, 140, 50},
						{140, 25, 120, 49},
						{140, 25, 160, 47},
						{140, 75, 140, 48},
						{140, 75, 140, 45},
						{140, 75, 120, 49},
						{140, 75, 160, 47},
						{139, 25, 141, 75},
						{141, 25, 139, 75}
	};
	visualize(line3, cros3, "Line 3 Intersection");
	for(int i = 0; i < cros3.length; i++){
		assert( "Lines should intersect small positive slope", GraphicsGeometry.segmentIntersectsSegment(line3[0], line3[1], line3[2], line3[3], cros3[i][0], cros3[i][1], cros3[i][2], cros3[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine4() {
	int[] line4 = {250, 45, 350, 50};
	int[][] cros4 = {	{230, 45, 250, 45},
						{230, 45, 251, 45},
						{230, 25, 250, 45},
						{230, 65, 250, 45},
						{230, 25, 251, 46},
						{230, 65, 251, 45},
						{250, 25, 250, 45},
						{250, 65, 250, 45},
						{250, 25, 250, 46},
						{250, 65, 250, 44},
						{250, 45, 350, 50},
						{230, 44, 370, 51},
						{270, 46, 290, 47},
						{380, 50, 350, 50},
						{380, 50, 349, 50},
						{380, 30, 350, 50},
						{380, 70, 350, 50},
						{380, 30, 348, 51},
						{380, 70, 348, 49},
						{250, 44, 350, 51},
						{250, 46, 350, 49},
						{290, 25, 290, 47},
						{290, 25, 290, 48},
						{289, 25, 290, 47},
						{290, 25, 270, 46},
						{290, 25, 310, 48},
						{310, 35, 309, 55}
	};
	visualize(line4, cros4, "Line 4 Intersection");
	for(int i = 0; i < cros4.length; i++){
		assert( "Lines should intersect negative small slope", GraphicsGeometry.segmentIntersectsSegment(line4[0], line4[1], line4[2], line4[3], cros4[i][0], cros4[i][1], cros4[i][2], cros4[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine5() {
	int[] line5 = {50, 200, 55, 300};
	int[][] cros5 = {	{50, 175, 50, 200},
						{50, 175, 50, 201},
						{45, 175, 50, 200},
						{30, 175, 51, 202},
						{50, 200, 55, 300},
						{49, 180, 56, 320},
						{51, 220, 52, 240},
						{49, 200, 56, 300},
						{51, 200, 55, 300},
						{30, 240, 52, 240},
						{30, 220, 52, 240},
						{30, 260, 52, 240},
						{30, 240, 51, 220},
						{30, 240, 53, 260},
						{30, 200, 70, 200},
						{30, 300, 70, 300}	
	};		
	visualize(line5, cros5, "Line 5 Intersection");
	for(int i = 0; i < cros5.length; i++){
		assert( "Lines should intersect large negative slope", GraphicsGeometry.segmentIntersectsSegment(line5[0], line5[1], line5[2], line5[3], cros5[i][0], cros5[i][1], cros5[i][2], cros5[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine6() {
	int[] line6 = {105, 200, 100, 300};
	int[][] cros6 = {	{105, 180, 105, 200},
						{105, 180, 105, 201},
						{105, 200, 100, 300},
						{106, 180, 99, 320},
						{104, 220, 103, 240},
						{106, 200, 99, 300},
						{104, 200, 101, 300},
						{80, 250, 120, 250},
						{90, 250, 110, 251},
						{90, 250, 110, 249},
						{80, 240, 103, 240},
						{80, 240, 104, 220},
						{80, 240, 102, 260},
						{80, 200, 120, 200},
						{80, 300, 120, 300},
						{120, 240, 103, 240},
						{120, 240, 104, 220},
						{120, 240, 102, 260},
						{80, 180, 105, 200},
						{120, 180, 105, 200}			
	};	
	visualize(line6, cros6, "Line 6 Intersection");
	for(int i = 0; i < cros6.length; i++){
		assert( "Lines should intersect large positive slope", GraphicsGeometry.segmentIntersectsSegment(line6[0], line6[1], line6[2], line6[3], cros6[i][0], cros6[i][1], cros6[i][2], cros6[i][3]) );
	}
}
/**
 * 
 */
public void testIntersectsLine7() {
	int[] line7 = {200, 200, 300, 300};
	int[][] cros7 = {	{200, 200, 300, 300},
						{180, 180, 320, 320},
						{220, 220, 280, 280},
						{280, 300, 320, 300},
						{180, 200, 220, 200},
						{200, 300, 300, 200},
						{250, 220, 250, 270},
						{250, 220, 249, 249},
						{250, 220, 251, 251},
						{199, 200, 301, 300},
						{201, 200, 299, 300},
						{230, 250, 251, 251},
						{270, 250, 249, 249}	
	};
	visualize(line7, cros7, "Line 7 Intersection");
	for(int i = 0; i < cros7.length; i++){
		assert( "Lines should intersect regular positive slope", GraphicsGeometry.segmentIntersectsSegment(line7[0], line7[1], line7[2], line7[3], cros7[i][0], cros7[i][1], cros7[i][2], cros7[i][3]) );
	}
}
/**
 * 
 */
public void testNoIntersectsLine1() {
	int[] line1 = {50, 50, 50, 100};
	int[][] nocros1 = {	{50, 30, 50, 49},
						{50, 30, 49, 50},
						{50, 30, 51, 50},
						{30, 30, 49, 50},
						{70, 30, 51, 50},
						{49, 50, 49, 100},
						{51, 50, 51, 100},
						{50, 120, 50, 101},
						{30, 100, 49, 100},
						{70, 100, 51, 100},
						{30, 100, 49, 50},
						{70, 100, 51, 50},
						{48, 47, 52, 51}
	};
	visualize(line1, nocros1, "Line 1 Non-Intersection");
	for(int i = 0; i < nocros1.length; i++){
		assert( "Lines should not intersect vertical", !GraphicsGeometry.segmentIntersectsSegment(line1[0], line1[1], line1[2], line1[3], nocros1[i][0], nocros1[i][1], nocros1[i][2], nocros1[i][3]) );
	}
}
/**
 * 
 */
public void testNoIntersectsLine2() {
	int[] line2 = {100, 100, 200, 100};
	int[][] nocros2 = {	{100, 99, 200, 99},
						{100, 101, 200, 101},
						{80, 100, 99, 100},
						{201, 100, 220, 100},
						{99, 80, 99, 120},
						{201, 80, 201, 120},
						{150, 80, 150, 99},
						{150, 120, 150, 101},
						{150, 80, 149, 99},
						{150, 80, 151, 99},
						{150, 80, 130, 99},
						{150, 80, 170, 99},
						{150, 120, 130, 101},
						{150, 120, 170, 101},
						{100, 98, 200, 99},
						{100, 99, 200, 98},
						{100, 101, 200, 102},
						{100, 102, 200, 101},
						{89, 90, 100, 101},
						{89, 110, 100, 99},
	};
	visualize(line2, nocros2, "Line 2 Non-Intersection");
	for(int i = 0; i < nocros2.length; i++){
		assert( "Lines should not intersect horizontal", !GraphicsGeometry.segmentIntersectsSegment(line2[0], line2[1], line2[2], line2[3], nocros2[i][0], nocros2[i][1], nocros2[i][2], nocros2[i][3]) );
	}
}
/**
 * 
 */
public void testNoIntersectsLine3() {
	int[] line3 = {100, 50, 200, 45};
	int[][] nocros3 = {	{100, 49, 200, 44},
						{100, 51, 200, 46},
						{80, 50, 100, 49},
						{80, 50, 100, 51},
						{80, 50, 99, 50},
						{140, 30, 139, 48},
						{140, 70, 141, 48},
						{201, 30, 201, 70},
						{220, 30, 199, 45},
						{220, 70, 200, 46} 
	};	
	visualize(line3, nocros3, "Line 3 Non-Intersection");
	for(int i = 0; i < nocros3.length; i++){
		assert( "Lines should not intersect the small positive slope", !GraphicsGeometry.segmentIntersectsSegment(line3[0], line3[1], line3[2], line3[3], nocros3[i][0], nocros3[i][1], nocros3[i][2], nocros3[i][3]) );
	}
}
/**
 * Paint the polygon and rectangles to see what we are trying to test... visually verifying
 * This method is basically used to see why the unit tests may be failing.
 * To run them modify the run field to equal true.
 */
protected static void visualize(int[] Line, int[][] TestLine, String title) {
	if (!run)
		return;
	SegmentIntersectionTestVisualizer visualizer = new SegmentIntersectionTestVisualizer(Line, TestLine);
	visualizer.setBounds(0,75,400,350);
	visualizer.setTitle(title);
	visualizer.show();
	visualizer.toFront();
}
}
