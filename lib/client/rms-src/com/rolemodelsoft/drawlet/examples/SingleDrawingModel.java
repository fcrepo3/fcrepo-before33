package com.rolemodelsoft.drawlet.examples;

/**
 * @(#)SingleDrawingModel.java
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
import com.rolemodelsoft.drawlet.util.*;
import com.rolemodelsoft.drawlet.basics.*;
import java.io.*;
import java.awt.*;

/**
 * @version 	1.1.6, 12/29/98
 */
 
public class SingleDrawingModel extends BasicObservable {
	/**
	 * The drawing
	 */
	Drawing drawing = new SimpleDrawing();
/**
 * Default constructor.
 */
public SingleDrawingModel() {
}
/**
 * Clears the drawing.
 */
public void clearDrawing() {
	Drawing newDrawing = new SimpleDrawing();
	newDrawing.setStyle( drawing.getStyle() );
	setDrawing(newDrawing);
}
/**
 * Gets the drawing.
 *
 * @return the Drawing currently associated with the receiver
 */
public Drawing getDrawing() {
	return drawing;
}
	/**
	 * Print the drawing.
	 */
	public void printDrawing( Frame f ) {
		Toolkit tk = Toolkit.getDefaultToolkit();
		java.util.Properties props = new java.util.Properties();

		if( tk != null ) {
			PrintJob pj = tk.getPrintJob( f, "Drawlets print job", props );

			if ( pj != null ) {
				Graphics pg = pj.getGraphics();

				if( pg != null ) {
					try {
						drawing.paint( pg );
					}
					finally {
						pg.dispose();
					}
				}
				pj.end();
			}
			System.out.println( props );
		}
	}
/**
 * Restore the drawing from the given file name
 * 
 * @param fileName the file name
 */
public void restoreDrawing(String fileName) {
	try {
		FileInputStream fileIn = new FileInputStream(fileName);
		ObjectInputStream in = new ObjectInputStream(fileIn);
		setDrawing((Drawing)in.readObject());
		in.close();
	} catch (Throwable e) {System.out.println("Cannot read drawing from file " + fileName);}
}
/**
 * Save the drawing to the given file name.
 *
 * @param fileName the file name
 */
public void saveDrawing(String fileName) {
	try {
		FileOutputStream fileOut = new FileOutputStream(fileName);
		ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		objectOut.writeObject(getDrawing());
		objectOut.close();
		fileOut.close();			
	} catch (Throwable e) {
		System.out.println("Cannot write drawing to file " + fileName + "\nException string: " + e.toString() + "\nException message: " + e.getMessage());
		e.printStackTrace();
	}
}
/**
 * Set the drawing associated with the receiver to the given drawing,
 * and tell observers about it.
 *
 * @param newDrawing the drawing
 */
public void setDrawing(Drawing newDrawing) {
//	newDrawing.setStyle( drawing.getStyle() );
	drawing = newDrawing;
	notifyObservers("getDrawing");
}
}
