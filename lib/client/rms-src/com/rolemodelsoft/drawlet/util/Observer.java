package com.rolemodelsoft.drawlet.util;

/**
 * @(#)Observer.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS AND KSC MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. NEITHER RMS NOR KSC SHALL BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
 
/**
 * This interface defines a generic Observer interface
 * This is desirable as the java.util.Observable is a class which
 * could force unnatural hierarchies.  This looks for the Observable
 * interface instead of the class to allow more flexibility in implementing
 * concrete classes.
 *
 * @version 	1.1.6, 12/30/98
 */

public interface Observer {
	/**
	 * The subject has notified the receiver of a change.
	 *
	 * @param subject the observed subject.
	 * @param arg the argument being notified.
	 */
	void update(Observable subject, Object arg);
}
