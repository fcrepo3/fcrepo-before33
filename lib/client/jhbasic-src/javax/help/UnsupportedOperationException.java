/*
 * @(#) UnsupportedOperationException.java 1.3 - last change made 03/18/99
 *
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

package javax.help;

/**
 * This is a platform-independent stand-in for the java.lang class
 */
public class UnsupportedOperationException extends RuntimeException {
    
    /**
     * Constructs an UnsupportedOperationException with no detail message.
     */
    public UnsupportedOperationException() {
    }
    
    /**
     * Constructs an UnsupportedOperationException with the specified
     * detail message. A null message is valid.
     */
    public UnsupportedOperationException(String message) {
	super(message);
    }
    
}
