package com.rolemodelsoft.drawlet.util;

/**
 * @(#)GraphicsGeometry.java
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

/**
 * A collection of static geometric utility methods.
 */
public class GraphicsGeometry {
/**
 * Answer whether the two intervals overlap.
 * @return boolean
 * @param start1 int
 * @param stop1 int
 * @param start2 int
 * @param stop2 int
 */
public static boolean intervalsOverlap(int start1, int stop1, int start2, int stop2) {
	return (
		isBetween(start1,start2,stop2) ||
		isBetween(stop1,start2,stop2) ||
		isBetween(start2,start1,stop1)
	);
}
/**
 * 
 * @return boolean
 * @param seg1Constant int
 * @param seg1Boundary1 int
 * @param seg1Boundary2 int
 * @param seg2Constant int
 * @param seg2Boundary1 int
 * @param seg2Boundary2 int
 */

//ONLY for pairs of VERTICAL or HORIZONTAL lines, not diagonals 
protected static boolean isBetween(double potential, double first, double last) {
	if (first <= last)
		return (first <= potential) && (potential <= last);
	else
		return (last <= potential) && (potential <= first);
}
/**
 * Answer whether the first segment on a line overlaps the second segment on the same line.
 */
protected static boolean sameLineSegmentsOverlap(int segment1x1, int segment1y1, int segment1x2, int segment1y2, int segment2x1, int segment2y1, int segment2x2, int segment2y2){
	if (segment1x1 == segment1x2)
		return intervalsOverlap(segment1y1, segment1y2, segment2y1, segment2y2);
	return intervalsOverlap(segment1x1, segment1x2, segment2x1, segment2x2);
}
/**
 * Answer whether the first segment intersects the line through the second segment.
 */
public static boolean segmentIntersectsLine(int segment1x1, int segment1y1, int segment1x2, int segment1y2, int segment2x1, int segment2y1, int segment2x2, int segment2y2){
	if (segment2x2 == segment2x1)
		return (isBetween(segment2x1,segment1x1,segment1x2));
	double slope = (double)(segment2y2 - segment2y1) / (double)(segment2x2 - segment2x1);
	double yIntercept = (double)segment2y1 - (double)(slope * segment2x1);
	return segmentIntersectsNonVerticalLine(segment1x1, segment1y1, segment1x2, segment1y2, slope, yIntercept);
}
/**
 * Answer whether a segment intersects a non-vertical line.
 * @return boolean
 * @param segmentX1 int
 * @param segmentY1 int
 * @param segmentX2 int
 * @param segmentY2 int
 * @param slope double
 * @param yIntercept double
 */
public static boolean segmentIntersectsNonVerticalLine(int segmentX1, int segmentY1, int segmentX2, int segmentY2, double slope, double yIntercept) {
	double y1 = slope * segmentX1 + yIntercept;
	double y2 = slope * segmentX2 + yIntercept;
	/*
	 * if the two endpoints of the segment are on different sides
	 * of the line or at least one is on it, the segment intersects the line
	 */
	return ( ((segmentY1 >= y1) && (segmentY2 <= y2)) || ((segmentY1 <= y1) && (segmentY2 >= y2)) );
}
/**
 * Answer whether the two segments intersect.
 * based on Paul Bourke's algorithm at http://www.mhri.edu.au/~pdb/geometry/lineline2d
 */
public static boolean segmentIntersectsSegment(int segment1x1, int segment1y1, int segment1x2, int segment1y2, int segment2x1, int segment2y1, int segment2x2, int segment2y2){
	double ud, uan, ubn, ua, ub, crossx, crossy;
	ud = (segment2y2 - segment2y1) * (segment1x2 - segment1x1) - (segment2x2 - segment2x1) * (segment1y2 - segment1y1);
	uan = (segment2x2 - segment2x1) * (segment1y1 - segment2y1) - (segment2y2 - segment2y1) * (segment1x1 - segment2x1);
	ubn = (segment1x2 - segment1x1) * (segment1y1 - segment2y1) - (segment1y2 - segment1y1) * (segment1x1 - segment2x1);
	if (ud == 0){
		if (uan == 0)
			return sameLineSegmentsOverlap(segment1x1, segment1y1, segment1x2, segment1y2, segment2x1, segment2y1, segment2x2, segment2y2);
		return false;
	}
	ua = uan / ud;
	ub = ubn / ud;
	return isBetween(ua, 0, 1) && isBetween(ub, 0, 1);
}
/**
 * Answer whether the first segment overlaps the second segment.
 */
public static boolean segmentOverlapsSegment(int segment1x1, int segment1y1, int segment1x2, int segment1y2, int segment2x1, int segment2y1, int segment2x2, int segment2y2){
	return (segmentsOnSameLine(segment1x1, segment1y1, segment1x2, segment1y2, segment2x1, segment2y1, segment2x2, segment2y2))
		&& sameLineSegmentsOverlap(segment1x1, segment1y1, segment1x2, segment1y2, segment2x1, segment2y1, segment2x2, segment2y2);
}
/**
 * Answer whether the first segment and second segment are on the same line.
 */
protected static boolean segmentsOnSameLine(int segment1x1, int segment1y1, int segment1x2, int segment1y2, int segment2x1, int segment2y1, int segment2x2, int segment2y2){
	if (segment1x1 == segment1x2)
		return (segment2x1 == segment2x2) && (segment2x1 == segment1x1);
	if (segment2x1 == segment2x2)
		return false;
	double slope1 = (double)(segment1y2 - segment1y1) / (double)(segment1x2 - segment1x1);
	double slope2 = (double)(segment2y2 - segment2y1) / (double)(segment2x2 - segment2x1);
	double yIntercept1 = (double)segment1y1 - (double)(slope1 * segment1x1);
	double yIntercept2 = (double)segment2y1 - (double)(slope2 * segment2x1);
	return ((slope1 == slope2) && (yIntercept1 == yIntercept2));
}
}
