// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   IndentingWriter.java

package com.sun.xml.rpc.processor.util;

import java.io.*;
import java.text.MessageFormat;

public class IndentingWriter extends BufferedWriter {

    private boolean beginningOfLine;
    private int currentIndent;
    private int indentStep;

    public IndentingWriter(Writer out) {
        super(out);
        beginningOfLine = true;
        currentIndent = 0;
        indentStep = 4;
    }

    public IndentingWriter(Writer out, int step) {
        this(out);
        if(indentStep < 0) {
            throw new IllegalArgumentException("negative indent step");
        } else {
            indentStep = step;
            return;
        }
    }

    public void write(int c) throws IOException {
        checkWrite();
        super.write(c);
    }

    public void write(char cbuf[], int off, int len) throws IOException {
        if(len > 0)
            checkWrite();
        super.write(cbuf, off, len);
    }

    public void write(String s, int off, int len) throws IOException {
        if(len > 0)
            checkWrite();
        super.write(s, off, len);
    }

    public void newLine() throws IOException {
        super.newLine();
        beginningOfLine = true;
    }

    protected void checkWrite() throws IOException {
        if(beginningOfLine) {
            beginningOfLine = false;
            for(int i = currentIndent; i > 0; i--)
                super.write(32);

        }
    }

    protected void indentIn() {
        currentIndent += indentStep;
    }

    protected void indentOut() {
        currentIndent -= indentStep;
        if(currentIndent < 0)
            currentIndent = 0;
    }

    public void pI() {
        indentIn();
    }

    public void pO() {
        indentOut();
    }

    public void pI(int levels) {
        for(int i = 0; i < levels; i++)
            indentIn();

    }

    public void pO(int levels) {
        for(int i = 0; i < levels; i++)
            indentOut();

    }

    public void p(String s) throws IOException {
        int tabCount = 0;
        for(int i = 0; i < s.length(); i++)
            if(s.charAt(i) == '\t') {
                tabCount++;
                indentIn();
            }

        write(s.substring(tabCount));
        while(tabCount-- > 0) 
            indentOut();
    }

    public void p(String s1, String s2) throws IOException {
        p(s1);
        p(s2);
    }

    public void p(String s1, String s2, String s3) throws IOException {
        p(s1);
        p(s2);
        p(s3);
    }

    public void p(String s1, String s2, String s3, String s4) throws IOException {
        p(s1);
        p(s2);
        p(s3);
        p(s4);
    }

    public void p(String s1, String s2, String s3, String s4, String s5) throws IOException {
        p(s1);
        p(s2);
        p(s3);
        p(s4);
        p(s5);
    }

    public void pln() throws IOException {
        newLine();
    }

    public void pln(String s) throws IOException {
        p(s);
        pln();
    }

    public void pln(String s1, String s2) throws IOException {
        p(s1, s2);
        pln();
    }

    public void pln(String s1, String s2, String s3) throws IOException {
        p(s1, s2, s3);
        pln();
    }

    public void pln(String s1, String s2, String s3, String s4) throws IOException {
        p(s1, s2, s3, s4);
        pln();
    }

    public void pln(String s1, String s2, String s3, String s4, String s5) throws IOException {
        p(s1, s2, s3, s4, s5);
        pln();
    }

    public void plnI(String s) throws IOException {
        p(s);
        pln();
        pI();
    }

    public void pO(String s) throws IOException {
        pO();
        p(s);
    }

    public void pOln(String s) throws IOException {
        pO(s);
        pln();
    }

    public void pOlnI(String s) throws IOException {
        pO(s);
        pln();
        pI();
    }

    public void p(Object o) throws IOException {
        write(o.toString());
    }

    public void pln(Object o) throws IOException {
        p(o.toString());
        pln();
    }

    public void plnI(Object o) throws IOException {
        p(o.toString());
        pln();
        pI();
    }

    public void pO(Object o) throws IOException {
        pO();
        p(o.toString());
    }

    public void pOln(Object o) throws IOException {
        pO(o.toString());
        pln();
    }

    public void pOlnI(Object o) throws IOException {
        pO(o.toString());
        pln();
        pI();
    }

    public void pM(String s) throws IOException {
        int j;
        for(int i = 0; i < s.length(); i = j + 1) {
            j = s.indexOf('\n', i);
            if(j == -1) {
                p(s.substring(i));
                break;
            }
            pln(s.substring(i, j));
        }

    }

    public void pMln(String s) throws IOException {
        pM(s);
        pln();
    }

    public void pMlnI(String s) throws IOException {
        pM(s);
        pln();
        pI();
    }

    public void pMO(String s) throws IOException {
        pO();
        pM(s);
    }

    public void pMOln(String s) throws IOException {
        pMO(s);
        pln();
    }

    public void pF(String pattern, Object arguments[]) throws IOException {
        pM(MessageFormat.format(pattern, arguments));
    }

    public void pFln(String pattern, Object arguments[]) throws IOException {
        pF(pattern, arguments);
        pln();
    }
}
