package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)FigureTransfer.java
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

import java.awt.datatransfer.*;
import com.rolemodelsoft.drawlet.*;
import java.util.Vector;
import java.io.*;

/**
 * Used to transfer objects from the <code>DrawingCanvas</code> to the clipboard.
 * @version 	1.1.6, 12/28/98
 */
 
public class FigureTransfer implements ClipboardOwner, Transferable {
	/**
	 * The figures that this <code>FigureTransfer</code> represents.
	 */
	protected Vector figures;

	/**
	 * The <code>DataFlavor</code> that <code>FigureTransfers</code> represent.
	 */
	public static DataFlavor figuresFlavor = new DataFlavor(Vector.class,"figures");
	/**
	 * Create a new <code>FigureTransfer</code>, and initialize it with
	 * the given <code>Vector</code>, which is assumed to contain
	 * <code>Figures</code>.
	 *
	 * @param figures a vector of the figures to be
	 * associated with this FigureTransfer
	 */
	public FigureTransfer(Vector figures) {
		this.figures = figures;
	}
	/**
	 * Return the data that the receiver holds, in the given flavor.
	 * 
	 * @param flavor the <code>DataFlavor</code> that the data should be returned as.
	 * @return	an Object that is the data.
	 */
	public Object getTransferData(DataFlavor flavor) throws IOException, UnsupportedFlavorException {
		if (flavor.equals(figuresFlavor)) return figures;
		else if (isSingleItemFlavorSupported(flavor)) {
			Figure figure = (Figure)figures.firstElement();
			String figureString;
			if (figures.firstElement() instanceof StringHolder) figureString = ((StringHolder)figure).getString();
			else figureString = figure.toString();
			
			if (flavor.equals(DataFlavor.stringFlavor)) return figureString;
			else if (flavor.equals(DataFlavor.plainTextFlavor)) return new StringReader(figureString);
			else throw new UnsupportedFlavorException(flavor);
		}
		else throw new UnsupportedFlavorException(flavor);
	}
	/**
	 * Answer the <code>DataFlavors</code> currently held.
	 * @return	an array of the <code>DataFlavors</code> currently
	 * contained in this <code>FigureTransfer</code>.
	 */
	public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
		if (figures.size() == 1) return new DataFlavor[] { figuresFlavor, DataFlavor.stringFlavor, DataFlavor.plainTextFlavor };
		else return new DataFlavor[] { figuresFlavor };
	}
	/**
	 * Answer whether or not the given flavor is supported.
	 * 
	 * @param flavor the flavor to check
	 * @return	boolean value of <code>true</code> if the flavor is supported;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavor.equals(figuresFlavor) || isSingleItemFlavorSupported(flavor));
	}
	/**
	 * @param flavor the flavor to check
	 * @return	boolean value of <code>true</code> if the flavor is supported;
	 * 			<code>false</code> otherwise.
	 */
	boolean isSingleItemFlavorSupported(DataFlavor flavor) {
		return 
			(figures.size() == 1 &&
						(flavor.equals(DataFlavor.stringFlavor) ||
						flavor.equals(DataFlavor.plainTextFlavor)));
	}
	/**
	 * Called when the receiver no longer hold ownership of the clipboard.
	 *
	 * @param clipboard the <code>Clipboard</code> whose ownership was lost.
	 * @param contents the contents of the <code>Clipboard</code> when
	 * ownership was lost.
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
