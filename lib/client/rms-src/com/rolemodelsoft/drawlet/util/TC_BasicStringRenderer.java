package com.rolemodelsoft.drawlet.util;

/**
 * @(#)TC_BasicStringRenderer.java
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

import com.rolemodelsoft.drawlet.util.BasicStringRenderer;
import com.rolemodelsoft.drawlet.util.StringRenderer;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import junit.framework.*;

public class TC_BasicStringRenderer extends TestCase {
	protected StringRenderer renderer;
	protected StringRenderer bigRenderer;
	protected StringRenderer multilineRenderer;
	protected StringRenderer bigIndentRenderer;
	protected StringRenderer nothingRenderer;
	protected StringRenderer emptyLineRenderer;
/**
 * BasicStringRendererTest constructor comment.
 * @param name java.lang.String
 */
public TC_BasicStringRenderer(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
protected void setUp() 
{
	renderer = new BasicStringRenderer("this is a test of the emergency HotDraw system ");
	bigRenderer = new BasicStringRenderer("this is a test of the emergency HotDraw system",new Font("SansSerif",Font.BOLD,18));
	multilineRenderer = new BasicStringRenderer("this is a\n\tindented and more \ncomplex test of the emergency HotDraw system",new Font("Serif",Font.PLAIN,12));
	bigIndentRenderer = new BasicStringRenderer("this is a\n\t\t\t\t\t\treally indented and more \ncomplex test of the emergency HotDraw system",new Font("Serif",Font.PLAIN,12));
	nothingRenderer = new BasicStringRenderer("");
	emptyLineRenderer = new BasicStringRenderer("\n");
}
/**
 * Test for accurate String lines.
 */
public void testRawStringLines() 
{
	String[] lines1 = renderer.getRawStringLines();
	String[] lines2 = bigRenderer.getRawStringLines();
	String[] lines3 = multilineRenderer.getRawStringLines();
	String[] lines4 = nothingRenderer.getRawStringLines();
	String[] lines5 = emptyLineRenderer.getRawStringLines();
	assertEquals(1,lines1.length);
	assertEquals(1,lines2.length);
	assertEquals(3,lines3.length);
	assertEquals(1,lines4.length);
	assertEquals(2,lines5.length);
	assertEquals(renderer.getString(),lines1[0]);
	assertEquals(bigRenderer.getString(),lines2[0]);
	assertEquals("this is a\n",lines3[0]);
	assertEquals("\tindented and more \n",lines3[1]);
	assertEquals("complex test of the emergency HotDraw system",lines3[2]);
	assertEquals("",lines4[0]);
	assertEquals("\n",lines5[0]);
	assertEquals("",lines5[1]);
}
/**
 * Test to make sure we properly recompose after changing the fonts.
 */
public void testSetFont() 
{
	testStringWidth();
	testStringHeight();
	renderer.setFont(new Font("SansSerif",Font.BOLD,18));
	bigRenderer.setFont(new Font("Serif",Font.BOLD,14));
	multilineRenderer.setFont(new Font("Monospace",Font.BOLD,18));
	bigIndentRenderer.setFont(new Font("SansSerif",Font.BOLD,10));
	nothingRenderer.setFont(new Font("Monospace",Font.BOLD,18));
	emptyLineRenderer.setFont(new Font("SansSerif",Font.BOLD,10));
	testStringWidth();
	testStringHeight();
}
/**
 * Test to make sure we properly recompose after changing the strings.
 */
public void testSetString() 
{
	testStringWidth();
	testStringHeight();
	renderer.setString("Changed text");
	bigRenderer.setString("Changed text");
	testStringWidth();
	testStringHeight();
}
/**
 * Test for accurate String height.
 */
public void testStringHeight() 
{
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(renderer.getFont()).getHeight(),renderer.getStringHeight());
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(bigRenderer.getFont()).getHeight(),bigRenderer.getStringHeight());
	assertEquals(3 * Toolkit.getDefaultToolkit().getFontMetrics(multilineRenderer.getFont()).getHeight(),multilineRenderer.getStringHeight());
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(nothingRenderer.getFont()).getHeight(),nothingRenderer.getStringHeight());
	assertEquals(2 * Toolkit.getDefaultToolkit().getFontMetrics(emptyLineRenderer.getFont()).getHeight(),emptyLineRenderer.getStringHeight());
}
/**
 * Test for accurate String lines.
 */
public void testStringLines() 
{
	String[] lines1 = renderer.getStringLines();
	String[] lines2 = bigRenderer.getStringLines();
	String[] lines3 = multilineRenderer.getStringLines();
	String[] lines4 = nothingRenderer.getStringLines();
	String[] lines5 = emptyLineRenderer.getStringLines();
	assertEquals(1,lines1.length);
	assertEquals(1,lines2.length);
	assertEquals(3,lines3.length);
	assertEquals(1,lines4.length);
	assertEquals(2,lines5.length);
	assertEquals(renderer.getString().trim(),lines1[0]);
	assertEquals(bigRenderer.getString(),lines2[0]);
	assertEquals("this is a",lines3[0]);
	assertEquals("\tindented and more",lines3[1]);
	assertEquals("complex test of the emergency HotDraw system",lines3[2]);
	assertEquals("",lines4[0]);
	assertEquals("",lines5[0]);
	assertEquals("",lines5[1]);
}
/**
 * Test for accurate String widths.
 */
public void testStringWidth() 
{
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(renderer.getFont()).stringWidth(renderer.getString()),renderer.getStringWidth());
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(bigRenderer.getFont()).stringWidth(bigRenderer.getString()),bigRenderer.getStringWidth());
	FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(multilineRenderer.getFont());
	String[] lines = multilineRenderer.getStringLines();
	assertEquals(metrics.stringWidth(lines[2]),multilineRenderer.getStringWidth());
	FontMetrics indentMetrics = Toolkit.getDefaultToolkit().getFontMetrics(bigIndentRenderer.getFont());
	String[] indentLines = bigIndentRenderer.getStringLines();
	assert("Expected wider than last line",indentMetrics.stringWidth(indentLines[2]) < bigIndentRenderer.getStringWidth());
	assert("Expected wider than raw 2nd line",indentMetrics.stringWidth(indentLines[1]) < bigIndentRenderer.getStringWidth());
	assertEquals(0,nothingRenderer.getStringWidth());
	assertEquals(0,emptyLineRenderer.getStringWidth());
}
}
