package com.rolemodelsoft.drawlet;

/**
 * @(#)EventHandler.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.awt.*;
import java.awt.event.*;

/**
 * This interface defines the basic protocol to handle Events.
 * Implementing classes can be used as tools for DrawingCanvas, etc.
 * It is expected that most implementers will use the various event methods (i.e.
 * actionPerformed, keyPressed, MouseReleased, etc.) for handling and dispatching
 * events, but it should not be assumed that this will be the only InputEventHandler
 * implementation.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface InputEventHandler extends KeyListener, MouseListener, MouseMotionListener {

}
