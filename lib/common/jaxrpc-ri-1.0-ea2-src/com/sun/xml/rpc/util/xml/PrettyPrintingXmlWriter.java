// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   PrettyPrintingXmlWriter.java

package com.sun.xml.rpc.util.xml;

import java.io.*;

public class PrettyPrintingXmlWriter {

    private static final boolean shouldPrettyprint = true;
    private BufferedWriter out;
    private char quoteChar;
    private int depth;
    private boolean inStart;
    private boolean needNewline;
    private boolean writtenChars;
    private boolean inAttribute;
    private boolean inAttributeValue;

    private PrettyPrintingXmlWriter(OutputStreamWriter w, boolean declare) throws IOException {
        quoteChar = '"';
        depth = 0;
        inStart = false;
        needNewline = false;
        writtenChars = false;
        inAttribute = false;
        inAttributeValue = false;
        out = new BufferedWriter(w, 1024);
        String enc = w.getEncoding();
        if(enc.equals("UTF8"))
            enc = "UTF-8";
        else
        if(enc.equals("ASCII"))
            enc = "US-ASCII";
        if(declare) {
            out.write("<?xml version=\"1.0\" encoding=\"" + enc + "\"?>");
            out.newLine();
            needNewline = true;
        }
    }

    public PrettyPrintingXmlWriter(OutputStream out, String enc, boolean declare) throws UnsupportedEncodingException, IOException {
        this(new OutputStreamWriter(out, enc), declare);
    }

    public PrettyPrintingXmlWriter(OutputStream out, String enc) throws UnsupportedEncodingException, IOException {
        this(new OutputStreamWriter(out, enc), true);
    }

    public PrettyPrintingXmlWriter(OutputStream out) throws IOException {
        this(new OutputStreamWriter(out, "UTF-8"), true);
    }

    public void setQuote(char quote) {
        if(quote != '"' && quote != '\'') {
            throw new IllegalArgumentException("Illegal quote character: " + quote);
        } else {
            quoteChar = quote;
            return;
        }
    }

    private void quote(char c) throws IOException {
        switch(c) {
        case 38: // '&'
            out.write("&amp;");
            break;

        case 60: // '<'
            out.write("&lt;");
            break;

        case 62: // '>'
            out.write("&gt;");
            break;

        default:
            out.write(c);
            break;
        }
    }

    private void aquote(char c) throws IOException {
        switch(c) {
        case 39: // '\''
            if(quoteChar == c)
                out.write("&apos;");
            else
                out.write(c);
            break;

        case 34: // '"'
            if(quoteChar == c)
                out.write("&quot;");
            else
                out.write(c);
            break;

        default:
            quote(c);
            break;
        }
    }

    private void quote(String s) throws IOException {
        for(int i = 0; i < s.length(); i++)
            quote(s.charAt(i));

    }

    private void aquote(String s) throws IOException {
        for(int i = 0; i < s.length(); i++)
            aquote(s.charAt(i));

    }

    private void indent(int depth) throws IOException {
        for(int i = 0; i < depth; i++)
            out.write("  ");

    }

    public void doctype(String root, String dtd) throws IOException {
        if(needNewline)
            out.newLine();
        needNewline = true;
        out.write("<!DOCTYPE " + root + " SYSTEM " + quoteChar);
        quote(dtd);
        out.write(quoteChar + ">");
        out.newLine();
    }

    private void start0(String name) throws IOException {
        finishStart();
        if(!writtenChars) {
            needNewline = true;
            indent(depth);
        }
        out.write(60);
        out.write(name);
        inStart = true;
        writtenChars = false;
        depth++;
    }

    private void start1(String name) throws IOException {
        finishStart();
        if(!writtenChars) {
            if(needNewline)
                out.newLine();
            needNewline = true;
            indent(depth);
        }
        out.write(60);
        out.write(name);
        inStart = true;
        writtenChars = false;
        depth++;
    }

    private void finishStart() throws IOException {
        if(inStart) {
            if(inAttribute)
                out.write(quoteChar);
            out.write(62);
            inStart = false;
            inAttribute = false;
            inAttributeValue = false;
        }
    }

    public void start(String name) throws IOException {
        start1(name);
    }

    public void attribute(String name, String value) throws IOException {
        attributeName(name);
        attributeValue(value);
    }

    public void attributeName(String name) throws IOException {
        if(!inStart)
            throw new IllegalStateException();
        if(inAttribute) {
            out.write(quoteChar);
            inAttribute = false;
            inAttributeValue = false;
        }
        out.write(32);
        out.write(name);
        out.write(61);
        out.write(quoteChar);
        inAttribute = true;
    }

    public void attributeValue(String value) throws IOException {
        if(!inAttribute || inAttributeValue) {
            throw new IllegalStateException();
        } else {
            aquote(value);
            out.write(quoteChar);
            inAttribute = false;
            return;
        }
    }

    public void attributeValueToken(String token) throws IOException {
        if(!inAttribute)
            throw new IllegalStateException();
        if(inAttributeValue)
            out.write(32);
        aquote(token);
        inAttributeValue = true;
    }

    public void end(String name) throws IOException {
        if(inStart) {
            if(inAttribute)
                out.write(quoteChar);
            out.write("/>");
            inStart = false;
            inAttribute = false;
            inAttributeValue = false;
        } else {
            out.write("</");
            out.write(name);
            out.write(62);
        }
        depth--;
        writtenChars = false;
    }

    public void chars(String chars) throws IOException {
        finishStart();
        quote(chars);
        writtenChars = true;
    }

    public void leaf(String name, String chars) throws IOException {
        start1(name);
        if(chars != null && chars.length() != 0)
            chars(chars);
        end(name);
    }

    public void inlineLeaf(String name, String chars) throws IOException {
        start0(name);
        if(chars != null && chars.length() != 0)
            chars(chars);
        end(name);
    }

    public void leaf(String name) throws IOException {
        leaf(name, null);
    }

    public void inlineLeaf(String name) throws IOException {
        inlineLeaf(name, null);
    }

    public void flush() throws IOException {
        if(depth != 0) {
            throw new IllegalStateException("Nonzero depth");
        } else {
            out.newLine();
            out.flush();
            return;
        }
    }

    public void close() throws IOException {
        flush();
        out.close();
    }
}
