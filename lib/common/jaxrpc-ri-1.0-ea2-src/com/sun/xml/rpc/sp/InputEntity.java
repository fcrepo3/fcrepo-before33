// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   InputEntity.java

package com.sun.xml.rpc.sp;

import java.io.*;
import java.net.URL;
import java.util.Locale;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

// Referenced classes of package com.sun.xml.rpc.sp:
//            XmlReader, EndOfInputException, ParseException, XmlChars,
//            Parser, MessageCatalog

final class InputEntity
    implements Locator {

    private int start;
    private int finish;
    private char buf[];
    private int lineNumber;
    private boolean returnedFirstHalf;
    private boolean maybeInCRLF;
    private String name;
    private InputEntity next;
    private InputSource input;
    private Reader reader;
    private boolean isClosed;
    private Locale locale;
    private StringBuffer rememberedText;
    private int startRemember;
    private boolean isPE;
    private static final int BUFSIZ = 2049;
    private static final char newline[] = {
        '\n'
    };
    private int end;

    public static InputEntity getInputEntity(Locale l) {
        InputEntity retval = new InputEntity();
        retval.locale = l;
        return retval;
    }

    private InputEntity() {
        lineNumber = 1;
        returnedFirstHalf = false;
        maybeInCRLF = false;
        end = -1;
    }

    public boolean isInternal() {
        return reader == null;
    }

    public boolean isDocument() {
        return next == null;
    }

    public boolean isParameterEntity() {
        return isPE;
    }

    public String getName() {
        return name;
    }

    public void init(InputSource in, String name, InputEntity stack, boolean isPE) throws ParseException, IOException {
        input = in;
        this.isPE = isPE;
        reader = in.getCharacterStream();
        if(reader == null) {
            java.io.InputStream bytes = in.getByteStream();
            if(bytes == null)
                reader = XmlReader.createReader((new URL(in.getSystemId())).openStream());
            else
            if(in.getEncoding() != null)
                reader = XmlReader.createReader(in.getByteStream(), in.getEncoding());
            else
                reader = XmlReader.createReader(in.getByteStream());
        }
        next = stack;
        buf = new char[2049];
        this.name = name;
        checkRecursion(stack);
    }

    public void init(char b[], String name, InputEntity stack, boolean isPE) throws ParseException {
        next = stack;
        buf = b;
        finish = b.length;
        this.name = name;
        this.isPE = isPE;
        checkRecursion(stack);
    }

    private void checkRecursion(InputEntity stack) throws ParseException {
        if(stack == null)
            return;
        for(stack = stack.next; stack != null; stack = stack.next)
            if(stack.name != null && stack.name.equals(name))
                fatal("P-069", new Object[] {
                    name
                });

    }

    public InputEntity pop() throws ParseException, IOException {
        close();
        return next;
    }

    public boolean isEOF() throws ParseException, IOException {
        if(start >= finish) {
            fillbuf();
            return start >= finish;
        } else {
            return false;
        }
    }

    public String getEncoding() {
        if(reader == null)
            return null;
        if(reader instanceof XmlReader)
            return ((XmlReader)reader).getEncoding();
        if(reader instanceof InputStreamReader)
            return ((InputStreamReader)reader).getEncoding();
        else
            return null;
    }

    public char getNameChar() throws ParseException, IOException {
        if(finish <= start)
            fillbuf();
        if(finish > start) {
            char c = buf[start++];
            if(XmlChars.isNameChar(c))
                return c;
            start--;
        }
        return '\0';
    }

    public char getc() throws ParseException, IOException {
        if(finish <= start)
            fillbuf();
        if(finish > start) {
            char c = buf[start++];
            if(returnedFirstHalf) {
                if(c >= '\uDC00' && c <= '\uDFFF') {
                    returnedFirstHalf = false;
                    return c;
                }
                fatal("P-070", new Object[] {
                    Integer.toHexString(c)
                });
            }
            if(c >= ' ' && c <= '\uD7FF' || c == '\t' || c >= '\uE000' && c <= '\uFFFD')
                return c;
            if(c == '\r' && !isInternal()) {
                maybeInCRLF = true;
                c = getc();
                if(c != '\n')
                    ungetc();
                maybeInCRLF = false;
                lineNumber++;
                return '\n';
            }
            if(c == '\n' || c == '\r') {
                if(!isInternal() && !maybeInCRLF)
                    lineNumber++;
                return c;
            }
            if(c >= '\uD800' && c < '\uDC00') {
                returnedFirstHalf = true;
                return c;
            }
            fatal("P-071", new Object[] {
                Integer.toHexString(c)
            });
        }
        throw new EndOfInputException();
    }

    public boolean peekc(char c) throws ParseException, IOException {
        if(finish <= start)
            fillbuf();
        if(finish > start) {
            if(buf[start] == c) {
                start++;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void ungetc() {
        if(start == 0)
            throw new InternalError("ungetc");
        start--;
        if(buf[start] == '\n' || buf[start] == '\r') {
            if(!isInternal())
                lineNumber--;
        } else
        if(returnedFirstHalf)
            returnedFirstHalf = false;
    }

    public boolean maybeWhitespace() throws ParseException, IOException {
        boolean isSpace = false;
        boolean sawCR = false;
        do {
            if(finish <= start)
                fillbuf();
            if(finish <= start)
                return isSpace;
            char c = buf[start++];
            if(c != ' ' && c != '\t' && c != '\n' && c != '\r')
                break;
            isSpace = true;
            if((c == '\n' || c == '\r') && !isInternal()) {
                if(c != '\n' || !sawCR) {
                    lineNumber++;
                    sawCR = false;
                }
                if(c == '\r')
                    sawCR = true;
            }
        } while(true);
        start--;
        return isSpace;
    }

    String getParsedContent(boolean coalescing) throws ParseException, IOException {
        int s;
        if(!coalescing) {
            s = start;
            if(parsedContent()) {
                if(end == -1)
                    end = start;
                return new String(buf, s, start - s);
            } else {
                return null;
            }
        }
        s = start;
        StringBuffer content = null;
        while(parsedContent())  {
            if(content == null)
                content = new StringBuffer();
            if(end == -1)
                end = start;
            content.append(buf, s, end - s);
            end = -1;
            if(!coalescing || isEOF())
                break;
            s = start;
        }
        return content != null ? content.toString() : null;
    }

    public boolean parsedContent() throws ParseException, IOException {
        int last;
        int first = last = start;
        boolean sawContent = false;
        do {
            if(last >= finish) {
                if(last > first) {
                    sawContent = true;
                    start = last;
                    return sawContent;
                }
                if(isEOF())
                    return sawContent;
                first = start;
                last = first - 1;
            } else {
                char c = buf[last];
                if((c <= ']' || c > '\uD7FF') && (c >= '&' || c < ' ') && (c <= '<' || c >= ']') && (c <= '&' || c >= '<') && c != '\t' && (c < '\uE000' || c > '\uFFFD')) {
                    if(c == '<' || c == '&')
                        break;
                    if(c == '\n') {
                        if(!isInternal())
                            lineNumber++;
                    } else
                    if(c == '\r') {
                        if(!isInternal()) {
                            sawContent = true;
                            lineNumber++;
                            if(finish > last + 1) {
                                if(buf[last + 1] == '\n') {
                                    last++;
                                    buf[last - 1] = '\n';
                                    end = last;
                                } else {
                                    buf[last] = '\n';
                                }
                            } else {
                                buf[last] = '\n';
                            }
                            first = start = last + 1;
                            return sawContent;
                        }
                    } else
                    if(c == ']')
                        switch(finish - last) {
                        case 2: // '\002'
                            if(buf[last + 1] != ']')
                                break;
                            // fall through

                        case 1: // '\001'
                            if(reader != null && !isClosed) {
                                if(last == first)
                                    throw new InternalError("fillbuf");
                                if(--last > first) {
                                    sawContent = true;
                                    start = last;
                                    return sawContent;
                                }
                                fillbuf();
                                first = last = start;
                            }
                            break;

                        default:
                            if(buf[last + 1] == ']' && buf[last + 2] == '>')
                                fatal("P-072", null);
                            break;
                        }
                    else
                    if(c >= '\uD800' && c <= '\uDFFF') {
                        if(last + 1 >= finish) {
                            if(last > first) {
                                sawContent = true;
                                end = last;
                                start = last + 1;
                                return sawContent;
                            }
                            if(isEOF())
                                fatal("P-081", new Object[] {
                                    Integer.toHexString(c)
                                });
                            first = start;
                            last = first;
                        } else
                        if(checkSurrogatePair(last)) {
                            last++;
                        } else {
                            last--;
                            break;
                        }
                    } else {
                        fatal("P-071", new Object[] {
                            Integer.toHexString(c)
                        });
                    }
                }
            }
            last++;
        } while(true);
        if(last == first) {
            return sawContent;
        } else {
            start = last;
            return true;
        }
    }

    String getUnparsedContent(boolean ignorableWhitespace, String whitespaceInvalidMessage) throws ParseException, IOException {
        int s = start;
        String ret = null;
        if(!unparsedContent(ignorableWhitespace, whitespaceInvalidMessage))
            return null;
        else
            return new String(buf, s + 8, start - 11 - s);
    }

    public boolean unparsedContent(boolean ignorableWhitespace, String whitespaceInvalidMessage) throws ParseException, IOException {
        if(!peek("![CDATA[", null))
            return false;
        do {
            boolean done = false;
            boolean white = ignorableWhitespace;
            int last;
            for(last = start; last < finish; last++) {
                char c = buf[last];
                if(!XmlChars.isChar(c)) {
                    white = false;
                    if(c >= '\uD800' && c <= '\uDFFF') {
                        if(checkSurrogatePair(last)) {
                            last++;
                            continue;
                        }
                        last--;
                        break;
                    }
                    fatal("P-071", new Object[] {
                        Integer.toHexString(buf[last])
                    });
                }
                if(c == '\n') {
                    if(!isInternal())
                        lineNumber++;
                    continue;
                }
                if(c == '\r') {
                    if(!isInternal()) {
                        if(white && whitespaceInvalidMessage != null)
                            fatal(Parser.messages.getMessage(locale, whitespaceInvalidMessage));
                        lineNumber++;
                        if(finish > last + 1 && buf[last + 1] == '\n')
                            last++;
                        start = last + 1;
                    }
                    continue;
                }
                if(c != ']') {
                    if(c != ' ' && c != '\t')
                        white = false;
                    continue;
                }
                if(last + 2 >= finish)
                    break;
                if(buf[last + 1] == ']' && buf[last + 2] == '>') {
                    done = true;
                    break;
                }
                white = false;
            }

            if(white && whitespaceInvalidMessage != null)
                fatal(Parser.messages.getMessage(locale, whitespaceInvalidMessage));
            if(done) {
                start = last + 3;
                break;
            }
            start = last;
            if(isEOF())
                fatal("P-073", null);
        } while(true);
        return true;
    }

    private boolean checkSurrogatePair(int offset) throws ParseException {
        if(offset + 1 >= finish)
            return false;
        char c1 = buf[offset++];
        char c2 = buf[offset];
        if(c1 >= '\uD800' && c1 < '\uDC00' && c2 >= '\uDC00' && c2 <= '\uDFFF') {
            return true;
        } else {
            fatal("P-074", new Object[] {
                Integer.toHexString(c1 & 0xffff), Integer.toHexString(c2 & 0xffff)
            });
            return false;
        }
    }

    public boolean ignorableWhitespace() throws ParseException, IOException {
        boolean isSpace = false;
        int first = start;
        do {
            if(finish <= start) {
                fillbuf();
                first = start;
            }
            if(finish <= start)
                return isSpace;
            char c = buf[start++];
            switch(c) {
            case 10: // '\n'
                if(!isInternal())
                    lineNumber++;
                // fall through

            case 9: // '\t'
            case 32: // ' '
                isSpace = true;
                break;

            case 13: // '\r'
                isSpace = true;
                if(!isInternal())
                    lineNumber++;
                if(start < finish && buf[start] == '\n')
                    start++;
                first = start;
                break;

            default:
                ungetc();
                return isSpace;
            }
        } while(true);
    }

    public boolean peek(String next, char chars[]) throws ParseException, IOException {
        int len;
        if(chars != null)
            len = chars.length;
        else
            len = next.length();
        if(finish <= start || finish - start < len)
            fillbuf();
        if(finish <= start)
            return false;
        int i=0;
        if(chars != null)
            for(i = 0; i < len && start + i < finish; i++)
                if(buf[start + i] != chars[i])
                    return false;

        else
            for(i = 0; i < len && start + i < finish; i++)
                if(buf[start + i] != next.charAt(i))
                    return false;

        if(i < len) {
            if(reader == null || isClosed)
                return false;
            if(len > buf.length)
                fatal("P-077", new Object[] {
                    new Integer(buf.length)
                });
            fillbuf();
            return peek(next, chars);
        } else {
            start += len;
            return true;
        }
    }

    public void startRemembering() {
        if(startRemember != 0) {
            throw new InternalError();
        } else {
            startRemember = start;
            return;
        }
    }

    public String rememberText() {
        String retval;
        if(rememberedText != null) {
            rememberedText.append(buf, startRemember, start - startRemember);
            retval = rememberedText.toString();
        } else {
            retval = new String(buf, startRemember, start - startRemember);
        }
        startRemember = 0;
        rememberedText = null;
        return retval;
    }

    private Locator getLocator() {
        InputEntity current;
        for(current = this; current != null && current.input == null; current = current.next);
        return current != null ? current : this;
    }

    public String getPublicId() {
        Locator where = getLocator();
        if(where == this)
            return input.getPublicId();
        else
            return where.getPublicId();
    }

    public String getSystemId() {
        Locator where = getLocator();
        if(where == this)
            return input.getSystemId();
        else
            return where.getSystemId();
    }

    public int getLineNumber() {
        Locator where = getLocator();
        if(where == this)
            return lineNumber;
        else
            return where.getLineNumber();
    }

    public int getColumnNumber() {
        return -1;
    }

    private void fillbuf() throws ParseException, IOException {
        if(reader == null || isClosed)
            return;
        if(startRemember != 0) {
            if(rememberedText == null)
                rememberedText = new StringBuffer(buf.length);
            rememberedText.append(buf, startRemember, start - startRemember);
        }
        boolean extra = finish > 0 && start > 0;
        if(extra)
            start--;
        int len = finish - start;
        System.arraycopy(buf, start, buf, 0, len);
        start = 0;
        finish = len;
        try {
            len = buf.length - len;
            len = reader.read(buf, finish, len);
        }
        catch(UnsupportedEncodingException e) {
            fatal("P-075", new Object[] {
                e.getMessage()
            });
        }
        catch(CharConversionException e) {
            fatal("P-076", new Object[] {
                e.getMessage()
            });
        }
        if(len >= 0)
            finish += len;
        else
            close();
        if(extra)
            start++;
        if(startRemember != 0)
            startRemember = 1;
    }

    public void close() {
        try {
            if(reader != null && !isClosed)
                reader.close();
            isClosed = true;
        }
        catch(IOException ioexception) { }
    }

    private void fatal(String message) throws ParseException {
        ParseException x = new ParseException(message, getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        close();
        throw x;
    }

    private void fatal(String messageId, Object params[]) throws ParseException {
        fatal(Parser.messages.getMessage(locale, messageId, params));
    }

}
