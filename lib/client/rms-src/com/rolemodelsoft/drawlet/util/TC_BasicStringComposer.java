package com.rolemodelsoft.drawlet.util;

/**
 * @(#)TC_BasicStringComposer.java
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

import com.rolemodelsoft.drawlet.util.BasicStringComposer;
import com.rolemodelsoft.drawlet.util.StringRenderer;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import junit.framework.*;

public class TC_BasicStringComposer extends TestCase {
	protected StringRenderer composer;
	protected StringRenderer bigComposer;
	protected StringRenderer multilineComposer;
	protected StringRenderer narrowComposer;
	protected StringRenderer shortNarrowComposer;
	protected StringRenderer nothingComposer;
	protected StringRenderer emptyLineComposer;
	protected StringRenderer noWidthComposer;
	protected StringRenderer breakBeforeComposer;
	protected StringRenderer breakAfterComposer;
/**
 * BasicStringComposerTest constructor comment.
 * @param name java.lang.String
 */
public TC_BasicStringComposer(String name) {
	super(name);
}
/**
 * Sets up the fixture, for example, open a network connection.
 * This method is called before a test is executed.
 */
protected void setUp() 
{
	composer = new BasicStringComposer("this ");
	bigComposer = new BasicStringComposer("this is a test of the emergency HotDraw system",new Font("SansSerif",Font.BOLD,18));
	multilineComposer = new BasicStringComposer("this is a\n\tindented and more \ncomplex test of the emergency HotDraw system",new Font("Serif",Font.PLAIN,12));
	narrowComposer = new BasicStringComposer("this is a\n\tindented and more \ncomplex test of the emergency HotDraw system",new Font("Serif",Font.PLAIN,12), 100);
	shortNarrowComposer = new BasicStringComposer("this is a\n\tindented and more \ncomplex test of the emergency HotDraw system",new Font("Serif",Font.PLAIN,12), 100, 36);
	nothingComposer = new BasicStringComposer("");
	emptyLineComposer = new BasicStringComposer("\n");
	noWidthComposer = new BasicStringComposer("thw",0);
	breakBeforeComposer = new BasicStringComposer("splitthis(here)<\n>",70);
	breakAfterComposer = new BasicStringComposer("split?here?",50); //this is breaking
}
/**
 * Test for accurate String lines.
 */
public void testRawStringLines() 
{
	String[] lines1 = composer.getRawStringLines();
	String[] lines2 = bigComposer.getRawStringLines();
	String[] lines3 = multilineComposer.getRawStringLines();
	String[] lines4 = narrowComposer.getRawStringLines();
	String[] lines5 = shortNarrowComposer.getRawStringLines();
	String[] lines6 = nothingComposer.getRawStringLines();
	String[] lines7 = emptyLineComposer.getRawStringLines();
	String[] lines8 = noWidthComposer.getRawStringLines();
	String[] lines9 = breakBeforeComposer.getRawStringLines();
	String[] lines10 = breakAfterComposer.getRawStringLines();
	assertEquals(1,lines1.length);
	assertEquals(1,lines2.length);
	assertEquals(3,lines3.length);
	assertEquals(6,lines4.length);
	assertEquals(3,lines5.length);
	assertEquals(1,lines6.length);
	assertEquals(2,lines7.length);
	assertEquals(3,lines8.length);
	assertEquals(3,lines9.length);
	assertEquals(2,lines10.length);
	assertEquals(composer.getString(),lines1[0]);
	assertEquals(bigComposer.getString(),lines2[0]);
	assertEquals("this is a\n",lines3[0]);
	assertEquals("\tindented and more \n",lines3[1]);
	assertEquals("complex test of the emergency HotDraw system",lines3[2]);
	assertEquals("this is a\n",lines4[0]);
	assertEquals("\tindented ",lines4[1]);
	assertEquals("and more \n",lines4[2]);
	assertEquals("complex test of the ",lines4[3]);
	assertEquals("emergency HotDraw ",lines4[4]);
	assertEquals("system",lines4[5]);
	assertEquals("this is a\n",lines5[0]);
	assertEquals("\tindented ",lines5[1]);
	assertEquals("and more \n",lines5[2]);
	assertEquals("",lines6[0]);
	assertEquals("\n",lines7[0]);
	assertEquals("",lines7[1]);
	assertEquals("t",lines8[0]);
	assertEquals("h",lines8[1]);
	assertEquals("w",lines8[2]);
	assertEquals("splitthis",lines9[0]);
	assertEquals("(here)<\n",lines9[1]);
	assertEquals(">",lines9[2]);
	assertEquals("split?",lines10[0]);
	assertEquals("here?",lines10[1]);
}
/**
 * Test to make sure we properly recompose after changing the fonts.
 */
public void testSetFont() 
{
	testStringWidth();
	testStringHeight();
	composer.setFont(new Font("SansSerif",Font.BOLD,18));
	bigComposer.setFont(new Font("Serif",Font.BOLD,14));
	multilineComposer.setFont(new Font("Monospace",Font.BOLD,18));
	narrowComposer.setFont(new Font("SansSerif",Font.BOLD,10));
	shortNarrowComposer.setFont(new Font("SansSerif",Font.BOLD,4));  //this is an issue that needs to be fixed
	nothingComposer.setFont(new Font("Monospace",Font.BOLD,18));
	emptyLineComposer.setFont(new Font("SansSerif",Font.BOLD,10));
	noWidthComposer.setFont(new Font("Monospace",Font.BOLD,18));
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
	composer.setString("Changed text");
	bigComposer.setString("Changed text");
	testStringWidth();
	testStringHeight();
}
/**
 * Test for accurate String height.
 */
public void testStringHeight() 
{
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(composer.getFont()).getHeight(),composer.getStringHeight());
	assertEquals(Toolkit.getDefaultToolkit().getFontMetrics(bigComposer.getFont()).getHeight(),bigComposer.getStringHeight());
	assertEquals(3 * Toolkit.getDefaultToolkit().getFontMetrics(multilineComposer.getFont()).getHeight(),multilineComposer.getStringHeight());
	assertEquals(6 * Toolkit.getDefaultToolkit().getFontMetrics(narrowComposer.getFont()).getHeight(),narrowComposer.getStringHeight());
	assertEquals(3 * Toolkit.getDefaultToolkit().getFontMetrics(shortNarrowComposer.getFont()).getHeight(),shortNarrowComposer.getStringHeight());
	assertEquals(1 * Toolkit.getDefaultToolkit().getFontMetrics(nothingComposer.getFont()).getHeight(),nothingComposer.getStringHeight());
	assertEquals(2 * Toolkit.getDefaultToolkit().getFontMetrics(emptyLineComposer.getFont()).getHeight(),emptyLineComposer.getStringHeight());
	assertEquals(3 * Toolkit.getDefaultToolkit().getFontMetrics(noWidthComposer.getFont()).getHeight(),noWidthComposer.getStringHeight());
}
/**
 * Test for accurate String lines.
 */
public void testStringLines() 
{
	String[] lines1 = composer.getStringLines();
	String[] lines2 = bigComposer.getStringLines();
	String[] lines3 = multilineComposer.getStringLines();
	String[] lines4 = narrowComposer.getStringLines();
	String[] lines5 = shortNarrowComposer.getStringLines();
	String[] lines6 = nothingComposer.getStringLines();
	String[] lines7 = emptyLineComposer.getStringLines();
	String[] lines8 = noWidthComposer.getStringLines();
	String[] lines9 = breakBeforeComposer.getStringLines();
	String[] lines10 = breakAfterComposer.getStringLines();
	assertEquals(1,lines1.length);
	assertEquals(1,lines2.length);
	assertEquals(3,lines3.length);
	assertEquals(6,lines4.length);
	assertEquals(3,lines5.length);
	assertEquals(1,lines6.length);
	assertEquals(2,lines7.length);
	assertEquals(3,lines8.length);
	assertEquals(3,lines9.length);
	assertEquals(2,lines10.length);
	assertEquals(1,lines1.length);
	assertEquals(1,lines2.length);
	assertEquals(3,lines3.length);
	assertEquals(6,lines4.length);
	assertEquals(3,lines5.length);
	assertEquals(1,lines6.length);
	assertEquals(2,lines7.length);
	assertEquals(3,lines8.length);
	assertEquals(composer.getString().trim(),lines1[0]);
	assertEquals(bigComposer.getString(),lines2[0]);
	assertEquals("this is a",lines3[0]);
	assertEquals("\tindented and more",lines3[1]);
	assertEquals("complex test of the emergency HotDraw system",lines3[2]);
	assertEquals("this is a",lines4[0]);
	assertEquals("\tindented",lines4[1]);
	assertEquals("and more",lines4[2]);
	assertEquals("complex test of the",lines4[3]);
	assertEquals("emergency HotDraw",lines4[4]);
	assertEquals("system",lines4[5]);
	assertEquals("this is a",lines5[0]);
	assertEquals("\tindented",lines5[1]);
	assertEquals("and more",lines5[2]);
	assertEquals("",lines6[0]);
	assertEquals("",lines7[0]);
	assertEquals("",lines7[1]);
	assertEquals("t",lines8[0]);
	assertEquals("h",lines8[1]);
	assertEquals("w",lines8[2]);
	assertEquals("splitthis",lines9[0]);
	assertEquals("(here)<",lines9[1]);
	assertEquals(">",lines9[2]);
	assertEquals("split?",lines10[0]);
	assertEquals("here?",lines10[1]);
}
/**
 * Test for accurate String widths.
 */
public void testStringWidth() 
{
	FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(composer.getFont());
	assertEquals("composer" + composer.getFont() + " String size different than sum of words",metrics.stringWidth("A simple test"),metrics.stringWidth("A ") + metrics.stringWidth("simple ") + metrics.stringWidth("test"));
	assertEquals(metrics.stringWidth(composer.getString()),composer.getStringWidth());
	metrics = Toolkit.getDefaultToolkit().getFontMetrics(bigComposer.getFont());
	assertEquals("bigComposer" + bigComposer.getFont().getPeer() + " String size different than sum of words",metrics.stringWidth("A simple test"),metrics.stringWidth("A ") + metrics.stringWidth("simple ") + metrics.stringWidth("test"));
	assertEquals(metrics.stringWidth(bigComposer.getString()),bigComposer.getStringWidth());
	metrics = Toolkit.getDefaultToolkit().getFontMetrics(multilineComposer.getFont());
	String[] lines = multilineComposer.getStringLines();
	assertEquals(metrics.stringWidth(lines[2]),multilineComposer.getStringWidth());
	assert("Expected wider than max width - 20",80 < narrowComposer.getStringWidth());
	assert("Expected narrower than max width + 10 due to whitespace",110 > narrowComposer.getStringWidth());
	assert("Expected wider than max width - 20",80 < shortNarrowComposer.getStringWidth());
	assert("Expected narrower than max width + 10 due to whitespace",110 > shortNarrowComposer.getStringWidth());
	assertEquals(0,nothingComposer.getStringWidth());
	assertEquals(0,emptyLineComposer.getStringWidth());
	metrics = Toolkit.getDefaultToolkit().getFontMetrics(noWidthComposer.getFont());
	assertEquals("noWidthComposer" + noWidthComposer.getFont() + " String size different than sum of words",metrics.stringWidth("A simple test"),metrics.stringWidth("A ") + metrics.stringWidth("simple ") + metrics.stringWidth("test"));
	assertEquals(metrics.stringWidth("w"),noWidthComposer.getStringWidth());
	metrics = Toolkit.getDefaultToolkit().getFontMetrics(breakBeforeComposer.getFont());
	assertEquals("breakBeforeComposer" + breakBeforeComposer.getFont() + " String size different than sum of words",metrics.stringWidth("A simple test"),metrics.stringWidth("A ") + metrics.stringWidth("simple ") + metrics.stringWidth("test"));
	assertEquals(metrics.stringWidth("splitthis"),breakBeforeComposer.getStringWidth());
	metrics = Toolkit.getDefaultToolkit().getFontMetrics(breakAfterComposer.getFont());
	assertEquals("breakAfterComposer" + breakAfterComposer.getFont() + " String size different than sum of words",metrics.stringWidth("A simple test"),metrics.stringWidth("A ") + metrics.stringWidth("simple ") + metrics.stringWidth("test"));
	assertEquals(Math.max(metrics.stringWidth("split?"),metrics.stringWidth("here?")),breakAfterComposer.getStringWidth());
}
}
