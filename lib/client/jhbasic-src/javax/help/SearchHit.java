/*
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
/*
 * @(#) SearchHit.java 1.3 - last change made 03/19/99
 */

package javax.help;

/**
 * Stores search information for individual Search hits.
 *
 * @author Roger D. Brinkley
 * @version   1.3     03/19/99
 */

public class SearchHit {

    private double confidence;
    private int begin;
    private int end;

    public SearchHit(double confidence, int begin, int end) {
	this.confidence = confidence;
	this.begin = begin;
	this.end = end;
    }

    public double getConfidence() {
	return confidence;
    }

    public int getBegin() {
	return begin;
    }

    public int getEnd() {
	return end;
    }
}

