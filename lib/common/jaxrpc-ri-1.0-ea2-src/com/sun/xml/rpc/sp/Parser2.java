// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser2.java

package com.sun.xml.rpc.sp;

import java.io.*;
import java.net.URL;
import java.util.*;
import org.xml.sax.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            AttributesExImpl, NamespaceSupport, Resolver, InternalEntity, 
//            ExternalEntity, EndOfInputException, AttributeDecl, ElementDecl, 
//            ParseException, SimpleHashtable, MessageCatalog, InputEntity, 
//            XmlChars, EntityDecl, ContentModel, AttributesEx

public final class Parser2 {

    private String curName;
    private String curValue;
    private String curURI;
    private InputEntity in;
    private AttributesExImpl attTmp;
    private String parts[];
    private StringBuffer strTmp;
    private char nameTmp[];
    private Parser2$NameCache nameCache;
    private char charTmp[];
    private boolean namespace;
    private NamespaceSupport ns;
    private boolean isInAttribute;
    private boolean inExternalPE;
    private boolean doLexicalPE;
    private boolean donePrologue;
    private boolean doneEpilogue;
    private boolean doneContent;
    private AttributesExImpl attr;
    private int attrIndex;
    private boolean startEmptyStack;
    private boolean isStandalone;
    private String rootElementName;
    private boolean ignoreDeclarations;
    private SimpleHashtable elements;
    private SimpleHashtable params;
    Map notations;
    SimpleHashtable entities;
    static final String strANY = "ANY";
    static final String strEMPTY = "EMPTY";
    private Locale locale;
    private EntityResolver resolver;
    Locator locator;
    private boolean fastStandalone;
    private static final String XMLNS_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
    private static final String XmlLang = "xml:lang";
    static final Parser2$Catalog messages = new Parser2$Catalog();
    private InputSource input;
    private boolean coalescing;
    private StringBuffer charsBuffer;
    private int cacheRet;
    private String cacheName;
    private String cacheValue;
    private String simpleCharsBuffer;
    private boolean lastRetWasEnd;
    private Parser2$FastStack stack;
    private Parser2$PIQueue piQueue;
    private static final int ELEMENT_IN_CONTENT = 1;
    private static final int ELEMENT_ROOT = 2;
    private static final int CONTENT_IN_ELEMENT = 4;
    private static final int CONTENT_IN_INTREF = 8;
    private static final int CONTENT_IN_EXTREF = 16;
    private static final int ELEMENT = 256;
    private static final int CONTENT = 1024;
    private static final int START = 1;
    private static final int END = 2;
    private static final int CHARS = 3;
    private static final int PI = 4;
    private static final int EMPTY = 10;
    private static final int ATTR = 11;
    private boolean haveAttributes;
    private int startLine;
    private boolean hasContent;

    public void setLocale(Locale l) throws ParseException {
        if(l != null && !messages.isLocaleSupported(l.toString()))
            fatal(messages.getMessage(locale, "P-078", new Object[] {
                l
            }));
        locale = l;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getCurName() {
        return curName;
    }

    public String getCurURI() {
        return curURI;
    }

    public String getCurValue() {
        return curValue;
    }

    public NamespaceSupport getNamespaceSupport() {
        return ns;
    }

    public AttributesEx getAttributes() {
        return attr;
    }

    public int getLineNumber() {
        return locator.getLineNumber();
    }

    public int getColumnNumber() {
        return locator.getColumnNumber();
    }

    public String getPublicId() {
        return locator.getPublicId();
    }

    public String getSystemId() {
        return locator.getSystemId();
    }

    public Locale chooseLocale(String languages[]) throws ParseException {
        Locale l = messages.chooseLocale(languages);
        if(l != null)
            setLocale(l);
        return l;
    }

    public void setEntityResolver(EntityResolver r) {
        resolver = r;
    }

    public EntityResolver getEntityResolver() {
        return resolver;
    }

    public void setFastStandalone(boolean value) {
        fastStandalone = value;
    }

    public boolean isFastStandalone() {
        return fastStandalone;
    }

    private void init() {
        in = null;
        attTmp = new AttributesExImpl();
        strTmp = new StringBuffer();
        nameTmp = new char[20];
        nameCache = new Parser2$NameCache();
        if(namespace)
            if(ns == null)
                ns = new NamespaceSupport();
            else
                ns.reset();
        isStandalone = false;
        rootElementName = null;
        isInAttribute = false;
        inExternalPE = false;
        doLexicalPE = false;
        donePrologue = false;
        doneEpilogue = false;
        doneContent = false;
        attr = null;
        attrIndex = 0;
        startEmptyStack = true;
        entities.clear();
        notations.clear();
        params.clear();
        elements.clear();
        ignoreDeclarations = false;
        stack.clear();
        piQueue.clear();
        builtin("amp", "&#38;");
        builtin("lt", "&#60;");
        builtin("gt", ">");
        builtin("quot", "\"");
        builtin("apos", "'");
        if(locale == null)
            locale = Locale.getDefault();
        if(resolver == null)
            resolver = new Resolver();
    }

    private void builtin(String entityName, String entityValue) {
        InternalEntity entity = new InternalEntity(entityName, entityValue.toCharArray());
        entities.put(entityName, entity);
    }

    void afterRoot() throws ParseException {
    }

    void afterDocument() {
    }

    private void whitespace(String roleId) throws IOException, ParseException {
        if(!maybeWhitespace())
            fatal("P-004", new Object[] {
                messages.getMessage(locale, roleId)
            });
    }

    private boolean maybeWhitespace() throws IOException, ParseException {
        if(!inExternalPE || !doLexicalPE)
            return in.maybeWhitespace();
        char c = getc();
        boolean saw = false;
        for(; c == ' ' || c == '\t' || c == '\n' || c == '\r'; c = getc()) {
            saw = true;
            if(in.isEOF() && !in.isInternal())
                return saw;
        }

        ungetc();
        return saw;
    }

    private String maybeGetName() throws IOException, ParseException {
        Parser2$NameCacheEntry entry = maybeGetNameCacheEntry();
        return entry != null ? entry.name : null;
    }

    private Parser2$NameCacheEntry maybeGetNameCacheEntry() throws IOException, ParseException {
        char c = getc();
        if(!XmlChars.isLetter(c) && c != ':' && c != '_') {
            ungetc();
            return null;
        } else {
            return nameCharString(c);
        }
    }

    private String getNmtoken() throws ParseException, IOException {
        char c = getc();
        if(!XmlChars.isNameChar(c))
            fatal("P-006", new Object[] {
                new Character(c)
            });
        return nameCharString(c).name;
    }

    private Parser2$NameCacheEntry nameCharString(char c) throws IOException, ParseException {
        int i = 1;
        for(nameTmp[0] = c; (c = in.getNameChar()) != 0; nameTmp[i++] = c)
            if(i >= nameTmp.length) {
                char tmp[] = new char[nameTmp.length + 10];
                System.arraycopy(nameTmp, 0, tmp, 0, nameTmp.length);
                nameTmp = tmp;
            }

        return nameCache.lookupEntry(nameTmp, i);
    }

    private void parseLiteral(boolean isEntityValue) throws IOException, ParseException {
        boolean savedLexicalPE = doLexicalPE;
        doLexicalPE = isEntityValue;
        char quote = getc();
        InputEntity source = in;
        if(quote != '\'' && quote != '"')
            fatal("P-007");
        isInAttribute = !isEntityValue;
        strTmp = new StringBuffer();
        do {
            for(; in != source && in.isEOF(); in = in.pop());
            char c;
            if((c = getc()) == quote && in == source)
                break;
            if(c == '&') {
                String entityName = maybeGetName();
                if(entityName != null) {
                    nextChar(';', "F-020", entityName);
                    if(isEntityValue) {
                        strTmp.append('&');
                        strTmp.append(entityName);
                        strTmp.append(';');
                    } else {
                        expandEntityInLiteral(entityName, entities, isEntityValue);
                    }
                } else
                if((c = getc()) == '#') {
                    int tmp = parseCharNumber();
                    if(tmp > 65535) {
                        tmp = surrogatesToCharTmp(tmp);
                        strTmp.append(charTmp[0]);
                        if(tmp == 2)
                            strTmp.append(charTmp[1]);
                    } else {
                        strTmp.append((char)tmp);
                    }
                } else {
                    fatal("P-009");
                }
                continue;
            }
            if(c == '%' && isEntityValue) {
                String entityName = maybeGetName();
                if(entityName != null) {
                    nextChar(';', "F-021", entityName);
                    if(inExternalPE)
                        expandEntityInLiteral(entityName, params, isEntityValue);
                    else
                        fatal("P-010", new Object[] {
                            entityName
                        });
                    continue;
                }
                fatal("P-011");
            }
            if(!isEntityValue) {
                if(c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    strTmp.append(' ');
                    continue;
                }
                if(c == '<')
                    fatal("P-012");
            }
            strTmp.append(c);
        } while(true);
        isInAttribute = false;
        doLexicalPE = savedLexicalPE;
    }

    private void expandEntityInLiteral(String name, SimpleHashtable table, boolean isEntityValue) throws ParseException, IOException {
        Object entity = table.get(name);
        if(entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity)entity;
            pushReader(value.buf, name, !((EntityDecl) (value)).isPE);
        } else
        if(entity instanceof ExternalEntity) {
            if(!isEntityValue)
                fatal("P-013", new Object[] {
                    name
                });
            pushReader((ExternalEntity)entity);
        } else
        if(entity == null)
            fatal(table != params ? "P-014" : "V-022", new Object[] {
                name
            });
    }

    private String getQuotedString(String type, String extra) throws IOException, ParseException {
        char quote = in.getc();
        if(quote != '\'' && quote != '"')
            fatal("P-015", new Object[] {
                messages.getMessage(locale, type, new Object[] {
                    extra
                })
            });
        strTmp = new StringBuffer();
        char c1;
        while((c1 = in.getc()) != quote) 
            strTmp.append(c1);
        return strTmp.toString();
    }

    private String parsePublicId() throws IOException, ParseException {
        String retval = getQuotedString("F-033", null);
        for(int i = 0; i < retval.length(); i++) {
            char c = retval.charAt(i);
            if(" \r\n-'()+,./:=?;!*#@$_%0123456789".indexOf(c) == -1 && (c < 'A' || c > 'Z') && (c < 'a' || c > 'z'))
                fatal("P-016", new Object[] {
                    new Character(c)
                });
        }

        strTmp = new StringBuffer();
        strTmp.append(retval);
        return normalize(false);
    }

    private boolean maybeComment(boolean skipStart) throws IOException, ParseException {
        boolean savedLexicalPE;
label0:
        {
            if(!in.peek(skipStart ? "!--" : "<!--", null))
                return false;
            savedLexicalPE = doLexicalPE;
            doLexicalPE = false;
            boolean saveCommentText = false;
            if(saveCommentText)
                strTmp = new StringBuffer();
            do
                try {
                    int c = getc();
                    if(c == 45) {
                        c = getc();
                        if(c != 45) {
                            if(saveCommentText)
                                strTmp.append('-');
                            ungetc();
                        } else {
                            nextChar('>', "F-022", null);
                            break label0;
                        }
                    } else
                    if(saveCommentText)
                        strTmp.append((char)c);
                }
                catch(EndOfInputException endofinputexception) {
                    if(inExternalPE || !donePrologue && in.isInternal())
                        in = in.pop();
                    else
                        fatal("P-017");
                }
            while(true);
        }
        doLexicalPE = savedLexicalPE;
        return true;
    }

    private void maybeXmlDecl() throws IOException, ParseException {
        if(!peek("<?xml"))
            return;
        readVersion(true, "1.0");
        readEncoding(false);
        readStandalone();
        maybeWhitespace();
        if(!peek("?>")) {
            char c = getc();
            fatal("P-023", new Object[] {
                Integer.toHexString(c), new Character(c)
            });
        }
    }

    private String maybeReadAttribute(String name, boolean must) throws IOException, ParseException {
        if(!maybeWhitespace()) {
            if(!must)
                return null;
            fatal("P-024", new Object[] {
                name
            });
        }
        if(!peek(name))
            if(must) {
                fatal("P-024", new Object[] {
                    name
                });
            } else {
                ungetc();
                return null;
            }
        maybeWhitespace();
        nextChar('=', "F-023", null);
        maybeWhitespace();
        return getQuotedString("F-035", name);
    }

    private void readVersion(boolean must, String versionNum) throws IOException, ParseException {
        String value = maybeReadAttribute("version", must);
        if(must && value == null)
            fatal("P-025", new Object[] {
                versionNum
            });
        if(value != null) {
            int length = value.length();
            for(int i = 0; i < length; i++) {
                char c = value.charAt(i);
                if((c < '0' || c > '9') && c != '_' && c != '.' && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != ':' && c != '-')
                    fatal("P-026", new Object[] {
                        value
                    });
            }

        }
        if(value != null && !value.equals(versionNum))
            error("P-027", new Object[] {
                versionNum, value
            });
    }

    private void maybeMisc(boolean eofOK) throws IOException, ParseException {
        while(!eofOK || !in.isEOF()) 
            if(!maybeComment(false) && !maybePI(false) && !maybeWhitespace())
                break;
    }

    private String getMarkupDeclname(String roleId, boolean qname) throws IOException, ParseException {
        whitespace(roleId);
        String name = maybeGetName();
        if(name == null)
            fatal("P-005", new Object[] {
                messages.getMessage(locale, roleId)
            });
        return name;
    }

    private boolean maybeDoctypeDecl() throws IOException, ParseException {
        if(!peek("<!DOCTYPE"))
            return false;
        ExternalEntity externalSubset = null;
        rootElementName = getMarkupDeclname("F-014", true);
        if(maybeWhitespace() && (externalSubset = maybeExternalID()) != null)
            maybeWhitespace();
        if(in.peekc('[')) {
            in.startRemembering();
            do {
                do
                    for(; in.isEOF() && !in.isDocument(); in = in.pop());
                while(maybeMarkupDecl() || maybePEReference() || maybeWhitespace());
                if(!peek("<!["))
                    break;
                fatal("P-028");
            } while(true);
            nextChar(']', "F-024", null);
            maybeWhitespace();
        }
        nextChar('>', "F-025", null);
        if(externalSubset == null);
        params.clear();
        List v = new ArrayList();
        for(Iterator e = notations.keySet().iterator(); e.hasNext();) {
            String name = (String)e.next();
            Object value = notations.get(name);
            if(value == Boolean.TRUE)
                v.add(name);
            else
            if(value instanceof String)
                v.add(name);
        }

        Object name;
        for(; !v.isEmpty(); notations.remove(name)) {
            name = v.get(0);
            v.remove(name);
        }

        return true;
    }

    private boolean maybeMarkupDecl() throws IOException, ParseException {
        return maybeElementDecl() || maybeAttlistDecl() || maybeEntityDecl() || maybeNotationDecl() || maybePI(false) || maybeComment(false);
    }

    private void readStandalone() throws IOException, ParseException {
        String value = maybeReadAttribute("standalone", false);
        if(value == null || "no".equals(value))
            return;
        if("yes".equals(value)) {
            isStandalone = true;
            return;
        } else {
            fatal("P-029", new Object[] {
                value
            });
            return;
        }
    }

    private boolean isXmlLang(String value) {
        if(value.length() < 2)
            return false;
        char c = value.charAt(1);
        int nextSuffix;
        if(c == '-') {
            c = value.charAt(0);
            if(c != 'i' && c != 'I' && c != 'x' && c != 'X')
                return false;
            nextSuffix = 1;
        } else
        if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            c = value.charAt(0);
            if((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                return false;
            nextSuffix = 2;
        } else {
            return false;
        }
        while(nextSuffix < value.length())  {
            c = value.charAt(nextSuffix);
            if(c != '-')
                break;
            while(++nextSuffix < value.length())  {
                c = value.charAt(nextSuffix);
                if((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
                    break;
            }
        }
        return value.length() == nextSuffix && c != '-';
    }

    private boolean defaultAttributes(AttributesExImpl attributes, ElementDecl element) throws ParseException {
        boolean didDefault = false;
        for(Iterator e = element.attributes.keys(); e.hasNext();) {
            String key = (String)e.next();
            String value = attributes.getValue(key);
            if(value == null) {
                AttributeDecl info = (AttributeDecl)element.attributes.get(key);
                if(info.defaultValue != null) {
                    attributes.addAttribute("", key, key, info.type, info.defaultValue, info.defaultValue, false);
                    didDefault = true;
                }
            }
        }

        return didDefault;
    }

    private boolean maybeElementDecl() throws IOException, ParseException {
        InputEntity start = peekDeclaration("!ELEMENT");
        if(start == null)
            return false;
        String name = getMarkupDeclname("F-015", true);
        ElementDecl element = (ElementDecl)elements.get(name);
        boolean declEffective = false;
        if(element != null) {
            if(element.contentType != null)
                element = new ElementDecl(name);
        } else {
            element = new ElementDecl(name);
            if(!ignoreDeclarations) {
                elements.put(element.name, element);
                declEffective = true;
            }
        }
        element.isFromInternalSubset = !inExternalPE;
        whitespace("F-000");
        if(peek("EMPTY")) {
            element.contentType = "EMPTY";
            element.ignoreWhitespace = true;
        } else
        if(peek("ANY")) {
            element.contentType = "ANY";
            element.ignoreWhitespace = false;
        } else {
            element.contentType = getMixedOrChildren(element);
        }
        maybeWhitespace();
        char c = getc();
        if(c != '>')
            fatal("P-036", new Object[] {
                name, new Character(c)
            });
        return true;
    }

    private String getMixedOrChildren(ElementDecl element) throws IOException, ParseException {
        strTmp = new StringBuffer();
        nextChar('(', "F-028", element.name);
        InputEntity start = in;
        maybeWhitespace();
        strTmp.append('(');
        if(peek("#PCDATA")) {
            strTmp.append("#PCDATA");
            getMixed(element.name, start);
            element.ignoreWhitespace = false;
        } else {
            element.model = getcps(element.name, start);
            element.ignoreWhitespace = true;
        }
        return strTmp.toString();
    }

    private ContentModel getcps(String element, InputEntity start) throws IOException, ParseException {
        boolean decided = false;
        char type = '\0';
        ContentModel current;
        ContentModel temp;
        ContentModel retval = current = temp = null;
        do {
            String tag = maybeGetName();
            if(tag != null) {
                strTmp.append(tag);
                temp = getFrequency(null);
            } else
            if(peek("(")) {
                InputEntity next = in;
                strTmp.append('(');
                maybeWhitespace();
                temp = getFrequency(getcps(element, next));
            } else {
                fatal(type != 0 ? type != ',' ? "P-038" : "P-037" : "P-039", new Object[] {
                    new Character(getc())
                });
            }
            maybeWhitespace();
            if(decided) {
                char c = getc();
                if(current != null) {
                    current.next = null;
                    current = current.next;
                }
                if(c == type) {
                    strTmp.append(type);
                    maybeWhitespace();
                    continue;
                }
                if(c == ')') {
                    ungetc();
                    continue;
                }
                fatal(type != 0 ? "P-040" : "P-041", new Object[] {
                    new Character(c), new Character(type)
                });
            } else {
                type = getc();
                if(type == '|' || type == ',') {
                    decided = true;
                    retval = current = null;
                } else {
                    retval = current = temp;
                    ungetc();
                    continue;
                }
                strTmp.append(type);
            }
            maybeWhitespace();
        } while(!peek(")"));
        strTmp.append(')');
        return getFrequency(retval);
    }

    private ContentModel getFrequency(ContentModel original) throws IOException, ParseException {
        char c = getc();
        if(c == '?' || c == '+' || c == '*') {
            strTmp.append(c);
            if(original == null)
                return null;
            if(original.type == 0) {
                original.type = c;
                return original;
            } else {
                return null;
            }
        } else {
            ungetc();
            return original;
        }
    }

    private void getMixed(String element, InputEntity start) throws IOException, ParseException {
        maybeWhitespace();
        if(peek(")*") || peek(")")) {
            strTmp.append(')');
            return;
        }
        for(; peek("|"); maybeWhitespace()) {
            strTmp.append('|');
            maybeWhitespace();
            String name = maybeGetName();
            if(name == null)
                fatal("P-042", new Object[] {
                    element, Integer.toHexString(getc())
                });
            strTmp.append(name);
        }

        if(!peek(")*"))
            fatal("P-043", new Object[] {
                element, new Character(getc())
            });
        strTmp.append(')');
    }

    private boolean maybeAttlistDecl() throws IOException, ParseException {
        InputEntity start = peekDeclaration("!ATTLIST");
        if(start == null)
            return false;
        String name = getMarkupDeclname("F-016", true);
        ElementDecl element = (ElementDecl)elements.get(name);
        if(element == null) {
            element = new ElementDecl(name);
            if(!ignoreDeclarations)
                elements.put(name, element);
        }
        maybeWhitespace();
        for(; !peek(">"); maybeWhitespace()) {
            name = maybeGetName();
            if(name == null)
                fatal("P-044", new Object[] {
                    new Character(getc())
                });
            whitespace("F-001");
            AttributeDecl a = new AttributeDecl(name);
            a.isFromInternalSubset = !inExternalPE;
            if(peek("CDATA"))
                a.type = "CDATA";
            else
            if(peek("IDREFS"))
                a.type = "IDREFS";
            else
            if(peek("IDREF"))
                a.type = "IDREF";
            else
            if(peek("ID")) {
                a.type = "ID";
                if(element.id == null)
                    element.id = name;
            } else
            if(peek("ENTITY"))
                a.type = "ENTITY";
            else
            if(peek("ENTITIES"))
                a.type = "ENTITIES";
            else
            if(peek("NMTOKENS"))
                a.type = "NMTOKENS";
            else
            if(peek("NMTOKEN"))
                a.type = "NMTOKEN";
            else
            if(peek("NOTATION")) {
                a.type = "NOTATION";
                whitespace("F-002");
                nextChar('(', "F-029", null);
                maybeWhitespace();
                List v = new ArrayList();
                do {
                    if((name = maybeGetName()) == null)
                        fatal("P-068");
                    v.add(name);
                    maybeWhitespace();
                    if(peek("|"))
                        maybeWhitespace();
                } while(!peek(")"));
                a.values = new String[v.size()];
                for(int i = 0; i < v.size(); i++)
                    a.values[i] = (String)v.get(i);

            } else
            if(peek("(")) {
                a.type = "ENUMERATION";
                maybeWhitespace();
                List v = new ArrayList();
                do {
                    name = getNmtoken();
                    v.add(name);
                    maybeWhitespace();
                    if(peek("|"))
                        maybeWhitespace();
                } while(!peek(")"));
                a.values = new String[v.size()];
                for(int i = 0; i < v.size(); i++)
                    a.values[i] = (String)v.get(i);

            } else {
                fatal("P-045", new Object[] {
                    name, new Character(getc())
                });
            }
            whitespace("F-003");
            if(peek("#REQUIRED"))
                a.isRequired = true;
            else
            if(peek("#FIXED")) {
                a.isFixed = true;
                whitespace("F-004");
                parseLiteral(false);
                if(a.type != "CDATA")
                    a.defaultValue = normalize(false);
                else
                    a.defaultValue = strTmp.toString();
            } else
            if(!peek("#IMPLIED")) {
                parseLiteral(false);
                if(a.type != "CDATA")
                    a.defaultValue = normalize(false);
                else
                    a.defaultValue = strTmp.toString();
            }
            if("xml:lang".equals(a.name) && a.defaultValue != null && !isXmlLang(a.defaultValue))
                error("P-033", new Object[] {
                    a.defaultValue
                });
            if(!ignoreDeclarations && element.attributes.get(a.name) == null)
                element.attributes.put(a.name, a);
        }

        return true;
    }

    private String normalize(boolean invalidIfNeeded) throws ParseException {
        String s = strTmp.toString();
        String s2 = s.trim();
        boolean didStrip = false;
        if(s != s2) {
            s = s2;
            s2 = null;
            didStrip = true;
        }
        strTmp = new StringBuffer();
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(!XmlChars.isSpace(c)) {
                strTmp.append(c);
            } else {
                strTmp.append(' ');
                while(++i < s.length() && XmlChars.isSpace(s.charAt(i))) 
                    didStrip = true;
                i--;
            }
        }

        if(didStrip)
            return strTmp.toString();
        else
            return s;
    }

    private boolean maybeConditionalSect() throws IOException, ParseException {
        if(!peek("<!["))
            return false;
        InputEntity start = in;
        maybeWhitespace();
        String keyword;
        if((keyword = maybeGetName()) == null)
            fatal("P-046");
        maybeWhitespace();
        nextChar('[', "F-030", null);
        if("INCLUDE".equals(keyword))
            do {
                while(in.isEOF() && in != start) 
                    in = in.pop();
                if(in.isEOF())
                    in = in.pop();
                if(peek("]]>"))
                    break;
                doLexicalPE = false;
                if(!maybeWhitespace() && !maybePEReference()) {
                    doLexicalPE = true;
                    if(!maybeMarkupDecl() && !maybeConditionalSect())
                        fatal("P-047");
                }
            } while(true);
        else
        if("IGNORE".equals(keyword)) {
            int nestlevel = 1;
            doLexicalPE = false;
            while(nestlevel > 0)  {
                char c = getc();
                if(c == '<') {
                    if(peek("!["))
                        nestlevel++;
                } else
                if(c == ']' && peek("]>"))
                    nestlevel--;
            }
        } else {
            fatal("P-048", new Object[] {
                keyword
            });
        }
        return true;
    }

    private int parseCharNumber() throws ParseException, IOException {
        int retval = 0;
        if(getc() != 'x') {
            ungetc();
            do {
                char c = getc();
                if(c >= '0' && c <= '9') {
                    retval *= 10;
                    retval += c - 48;
                } else {
                    if(c == ';')
                        return retval;
                    fatal("P-049");
                }
            } while(true);
        }
        do {
            char c = getc();
            if(c >= '0' && c <= '9') {
                retval <<= 4;
                retval += c - 48;
            } else
            if(c >= 'a' && c <= 'f') {
                retval <<= 4;
                retval += 10 + (c - 97);
            } else
            if(c >= 'A' && c <= 'F') {
                retval <<= 4;
                retval += 10 + (c - 65);
            } else {
                if(c == ';')
                    return retval;
                fatal("P-050");
            }
        } while(true);
    }

    private int surrogatesToCharTmp(int ucs4) throws ParseException {
        if(ucs4 <= 65535) {
            if(XmlChars.isChar(ucs4)) {
                charTmp[0] = (char)ucs4;
                return 1;
            }
        } else
        if(ucs4 <= 0x10ffff) {
            ucs4 -= 0x10000;
            charTmp[0] = (char)(0xd800 | ucs4 >> 10 & 0x3ff);
            charTmp[1] = (char)(0xdc00 | ucs4 & 0x3ff);
            return 2;
        }
        fatal("P-051", new Object[] {
            Integer.toHexString(ucs4)
        });
        return -1;
    }

    private boolean maybePEReference() throws IOException, ParseException {
        if(!in.peekc('%'))
            return false;
        String name = maybeGetName();
        if(name == null)
            fatal("P-011");
        nextChar(';', "F-021", name);
        Object entity = params.get(name);
        if(entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity)entity;
            pushReader(value.buf, name, false);
        } else
        if(entity instanceof ExternalEntity)
            externalParameterEntity((ExternalEntity)entity);
        else
        if(entity == null) {
            ignoreDeclarations = true;
            warning("V-022", new Object[] {
                name
            });
        }
        return true;
    }

    private boolean maybeEntityDecl() throws IOException, ParseException {
        InputEntity start = peekDeclaration("!ENTITY");
        if(start == null)
            return false;
        doLexicalPE = false;
        whitespace("F-005");
        SimpleHashtable defns;
        if(in.peekc('%')) {
            whitespace("F-006");
            defns = params;
        } else {
            defns = entities;
        }
        ungetc();
        doLexicalPE = true;
        String entityName = getMarkupDeclname("F-017", false);
        whitespace("F-007");
        ExternalEntity externalId = maybeExternalID();
        boolean doStore = defns.get(entityName) == null;
        if(!doStore && defns == entities)
            warning("P-054", new Object[] {
                entityName
            });
        doStore &= !ignoreDeclarations;
        if(externalId == null) {
            doLexicalPE = false;
            parseLiteral(true);
            doLexicalPE = true;
            if(doStore) {
                char value[] = new char[strTmp.length()];
                if(value.length != 0)
                    strTmp.getChars(0, value.length, value, 0);
                InternalEntity entity = new InternalEntity(entityName, value);
                entity.isPE = defns == params;
                entity.isFromInternalSubset = !inExternalPE;
                defns.put(entityName, entity);
            }
        } else {
            if(defns == entities && maybeWhitespace() && peek("NDATA"))
                externalId.notation = getMarkupDeclname("F-018", false);
            externalId.name = entityName;
            externalId.isPE = defns == params;
            externalId.isFromInternalSubset = !inExternalPE;
            if(doStore)
                defns.put(entityName, externalId);
        }
        maybeWhitespace();
        nextChar('>', "F-031", entityName);
        return true;
    }

    private ExternalEntity maybeExternalID() throws IOException, ParseException {
        String temp = null;
        if(peek("PUBLIC")) {
            whitespace("F-009");
            temp = parsePublicId();
        } else
        if(!peek("SYSTEM"))
            return null;
        ExternalEntity retval = new ExternalEntity(in);
        retval.publicId = temp;
        whitespace("F-008");
        retval.systemId = parseSystemId();
        return retval;
    }

    private String parseSystemId() throws IOException, ParseException {
        String uri = getQuotedString("F-034", null);
        int temp = uri.indexOf(':');
        if(temp == -1 || uri.indexOf('/') < temp) {
            String baseURI = in.getSystemId();
            if(baseURI == null)
                baseURI = "NODOCTYPE:///tmp/";
            if(uri.length() == 0)
                uri = ".";
            baseURI = baseURI.substring(0, baseURI.lastIndexOf('/') + 1);
            if(uri.charAt(0) != '/')
                uri = baseURI + uri;
            else
                throw new InternalError();
        }
        if(uri.indexOf('#') != -1)
            error("P-056", new Object[] {
                uri
            });
        return uri;
    }

    private void maybeTextDecl() throws IOException, ParseException {
        if(peek("<?xml")) {
            readVersion(false, "1.0");
            readEncoding(true);
            maybeWhitespace();
            if(!peek("?>"))
                fatal("P-057");
        }
    }

    private void externalParameterEntity(ExternalEntity next) throws IOException, ParseException {
        if(isStandalone && fastStandalone)
            return;
        inExternalPE = true;
        pushReader(next);
        InputEntity pe = in;
        maybeTextDecl();
        while(!pe.isEOF())  {
            if(in.isEOF()) {
                in = in.pop();
                continue;
            }
            doLexicalPE = false;
            if(maybeWhitespace() || maybePEReference())
                continue;
            doLexicalPE = true;
            if(!maybeMarkupDecl() && !maybeConditionalSect())
                break;
        }
        if(!pe.isEOF())
            fatal("P-059", new Object[] {
                in.getName()
            });
        in = in.pop();
        inExternalPE = !in.isDocument();
        doLexicalPE = false;
    }

    private void readEncoding(boolean must) throws IOException, ParseException {
        String name = maybeReadAttribute("encoding", must);
        if(name == null)
            return;
        for(int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (i == 0 || (c < '0' || c > '9') && c != '-' && c != '_' && c != '.'))
                fatal("P-060", new Object[] {
                    new Character(c)
                });
        }

        String currentEncoding = in.getEncoding();
        if(currentEncoding != null && !name.equalsIgnoreCase(currentEncoding))
            warning("P-061", new Object[] {
                name, currentEncoding
            });
    }

    private boolean maybeNotationDecl() throws IOException, ParseException {
        InputEntity start = peekDeclaration("!NOTATION");
        if(start == null)
            return false;
        String name = getMarkupDeclname("F-019", false);
        ExternalEntity entity = new ExternalEntity(in);
        whitespace("F-011");
        if(peek("PUBLIC")) {
            whitespace("F-009");
            entity.publicId = parsePublicId();
            if(maybeWhitespace() && !peek(">"))
                entity.systemId = parseSystemId();
        } else
        if(peek("SYSTEM")) {
            whitespace("F-008");
            entity.systemId = parseSystemId();
        } else {
            fatal("P-062");
        }
        maybeWhitespace();
        nextChar('>', "F-032", name);
        if(entity.systemId != null && entity.systemId.indexOf('#') != -1)
            error("P-056", new Object[] {
                entity.systemId
            });
        Object value = notations.get(name);
        if(value != null && (value instanceof ExternalEntity))
            warning("P-063", new Object[] {
                name
            });
        else
        if(!ignoreDeclarations)
            notations.put(name, entity);
        return true;
    }

    private char getc() throws IOException, ParseException {
        char c;
        if(!inExternalPE || !doLexicalPE) {
            c = in.getc();
            if(c == '%' && doLexicalPE)
                fatal("P-080");
            return c;
        }
        while(in.isEOF()) 
            if(in.isInternal() || doLexicalPE && !in.isDocument())
                in = in.pop();
            else
                fatal("P-064", new Object[] {
                    in.getName()
                });
        if((c = in.getc()) == '%' && doLexicalPE) {
            String name = maybeGetName();
            if(name == null)
                fatal("P-011");
            nextChar(';', "F-021", name);
            Object entity = params.get(name);
            pushReader(" ".toCharArray(), null, false);
            if(entity instanceof InternalEntity)
                pushReader(((InternalEntity)entity).buf, name, false);
            else
            if(entity instanceof ExternalEntity)
                pushReader((ExternalEntity)entity);
            else
            if(entity == null)
                fatal("V-022");
            else
                throw new InternalError();
            pushReader(" ".toCharArray(), null, false);
            return in.getc();
        } else {
            return c;
        }
    }

    private void ungetc() {
        in.ungetc();
    }

    private boolean peek(String s) throws IOException, ParseException {
        return in.peek(s, null);
    }

    private InputEntity peekDeclaration(String s) throws IOException, ParseException {
        if(!in.peekc('<'))
            return null;
        InputEntity start = in;
        if(in.peek(s, null)) {
            return start;
        } else {
            in.ungetc();
            return null;
        }
    }

    private void nextChar(char c, String location, String near) throws IOException, ParseException {
        for(; in.isEOF() && !in.isDocument(); in = in.pop());
        if(!in.peekc(c))
            fatal("P-008", new Object[] {
                new Character(c), messages.getMessage(locale, location), near != null ? '"' + near + '"' : ""
            });
    }

    private void pushReader(char buf[], String name, boolean isGeneral) throws ParseException {
        InputEntity r = InputEntity.getInputEntity(locale);
        r.init(buf, name, in, !isGeneral);
        in = r;
    }

    private boolean pushReader(ExternalEntity next) throws ParseException, IOException {
        try {
            InputEntity r = InputEntity.getInputEntity(locale);
            InputSource s = next.getInputSource(resolver);
            r.init(s, ((EntityDecl) (next)).name, in, ((EntityDecl) (next)).isPE);
            in = r;
        }
        catch(SAXException e) {
            throw translate(e);
        }
        return true;
    }

    private void warning(String messageId, Object parameters[]) throws ParseException {
        fatal(messages.getMessage(locale, messageId, parameters));
    }

    void error(String messageId, Object parameters[]) throws ParseException {
        fatal(messages.getMessage(locale, messageId, parameters));
    }

    private void fatal(String message) throws ParseException {
        fatal(message, null, null);
    }

    private void fatal(String message, Object parameters[]) throws ParseException {
        fatal(message, parameters, null);
    }

    private void fatal(String messageId, Object parameters[], Exception e) throws ParseException {
        String m = messages.getMessage(locale, messageId, parameters);
        String m2 = e != null ? e.toString() : null;
        if(m2 != null)
            m = m + ": " + m2;
        ParseException x = new ParseException(m, getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        throw x;
    }

    private ParseException translate(SAXException x) {
        String m = x.getMessage();
        if(x.getException() != null) {
            String n = x.getException().toString();
            if(m != null)
                m = m + ": " + n;
            else
                m = n;
        }
        return new ParseException(m);
    }

    public Parser2(InputStream in, boolean coalescing, boolean namespaceAware) {
        this(new InputSource(in), coalescing, namespaceAware);
    }

    public Parser2(InputStream in) {
        this(new InputSource(in), false, false);
    }

    public Parser2(File file, boolean coalescing, boolean namespaceAware) throws IOException {
        curName = null;
        curValue = null;
        curURI = null;
        parts = new String[3];
        charTmp = new char[2];
        namespace = false;
        ns = null;
        isInAttribute = false;
        attr = null;
        attrIndex = 0;
        startEmptyStack = true;
        elements = new SimpleHashtable(47);
        params = new SimpleHashtable(7);
        notations = new HashMap(7);
        entities = new SimpleHashtable(17);
        fastStandalone = false;
        input = null;
        this.coalescing = false;
        charsBuffer = null;
        cacheRet = -1;
        cacheName = null;
        cacheValue = null;
        simpleCharsBuffer = null;
        lastRetWasEnd = false;
        stack = new Parser2$FastStack(this, 100);
        piQueue = new Parser2$PIQueue(this, 10);
        haveAttributes = false;
        hasContent = true;
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        InputSource is = new InputSource(in);
        is.setSystemId(file.toURL().toString());
        locator = new Parser2$DocLocator(this);
        input = is;
        this.coalescing = coalescing;
        namespace = namespaceAware;
    }

    public Parser2(File file) throws IOException {
        curName = null;
        curValue = null;
        curURI = null;
        parts = new String[3];
        charTmp = new char[2];
        namespace = false;
        ns = null;
        isInAttribute = false;
        attr = null;
        attrIndex = 0;
        startEmptyStack = true;
        elements = new SimpleHashtable(47);
        params = new SimpleHashtable(7);
        notations = new HashMap(7);
        entities = new SimpleHashtable(17);
        fastStandalone = false;
        input = null;
        coalescing = false;
        charsBuffer = null;
        cacheRet = -1;
        cacheName = null;
        cacheValue = null;
        simpleCharsBuffer = null;
        lastRetWasEnd = false;
        stack = new Parser2$FastStack(this, 100);
        piQueue = new Parser2$PIQueue(this, 10);
        haveAttributes = false;
        hasContent = true;
        InputStream in = new BufferedInputStream(new FileInputStream(file));
        InputSource is = new InputSource(in);
        is.setSystemId(file.toURL().toString());
        locator = new Parser2$DocLocator(this);
        input = is;
    }

    private Parser2(InputSource input, boolean coalescing, boolean namespaceAware) {
        curName = null;
        curValue = null;
        curURI = null;
        parts = new String[3];
        charTmp = new char[2];
        namespace = false;
        ns = null;
        isInAttribute = false;
        attr = null;
        attrIndex = 0;
        startEmptyStack = true;
        elements = new SimpleHashtable(47);
        params = new SimpleHashtable(7);
        notations = new HashMap(7);
        entities = new SimpleHashtable(17);
        fastStandalone = false;
        this.input = null;
        this.coalescing = false;
        charsBuffer = null;
        cacheRet = -1;
        cacheName = null;
        cacheValue = null;
        simpleCharsBuffer = null;
        lastRetWasEnd = false;
        stack = new Parser2$FastStack(this, 100);
        piQueue = new Parser2$PIQueue(this, 10);
        haveAttributes = false;
        hasContent = true;
        locator = new Parser2$DocLocator(this);
        this.input = input;
        this.coalescing = coalescing;
        namespace = namespaceAware;
    }

    private void prologue() throws IOException, ParseException {
        init();
        if(input == null)
            fatal("P-000");
        in = InputEntity.getInputEntity(locale);
        in.init(input, null, null, false);
        maybeXmlDecl();
        maybeMisc(false);
        maybeDoctypeDecl();
        maybeMisc(false);
    }

    public int parse() throws ParseException, IOException {
        int ret = 0;
        try {
            if(!donePrologue) {
                prologue();
                donePrologue = true;
            }
            if((ret = retrievePIs()) != -1)
                return ret;
            if(!doneContent)
                if(!coalescing) {
                    if((ret = parseContent()) != 10)
                        return ret;
                    doneContent = true;
                } else {
                    if(lastRetWasEnd) {
                        ns.slideContextUp();
                        lastRetWasEnd = false;
                    }
                    if(cacheRet != -1) {
                        ret = cacheRet;
                        curName = cacheName;
                        curValue = cacheValue;
                        cacheRet = -1;
                        cacheName = null;
                        cacheValue = null;
                        if(namespace)
                            if(ret == 1)
                                ns.slideContextDown();
                            else
                            if(ret == 2)
                                lastRetWasEnd = true;
                        return ret;
                    }
                    while((ret = parseContent()) != 10) 
                        if(ret == 3) {
                            if(simpleCharsBuffer == null) {
                                simpleCharsBuffer = curValue;
                            } else {
                                if(charsBuffer == null) {
                                    charsBuffer = new StringBuffer();
                                    charsBuffer.append(simpleCharsBuffer);
                                }
                                charsBuffer.append(curValue);
                            }
                        } else
                        if(ret != 3) {
                            if(simpleCharsBuffer != null) {
                                cacheRet = ret;
                                cacheName = curName;
                                cacheValue = curValue;
                                if(charsBuffer == null) {
                                    curName = null;
                                    curValue = simpleCharsBuffer;
                                } else {
                                    curName = null;
                                    curValue = charsBuffer.toString();
                                    charsBuffer = null;
                                }
                                simpleCharsBuffer = null;
                                if(namespace)
                                    if(cacheRet == 1)
                                        ns.slideContextUp();
                                    else
                                    if(cacheRet == 2)
                                        ns.slideContextDown();
                                return 3;
                            }
                            if(ret == 2) {
                                lastRetWasEnd = true;
                                ns.slideContextDown();
                            }
                            return ret;
                        }
                    doneContent = true;
                }
            if(!doneEpilogue) {
                epilogue();
                doneEpilogue = true;
            }
            return retrievePIs();
        }
        catch(EndOfInputException e) {
            if(!in.isDocument()) {
                String name = in.getName();
                try {
                    do
                        in = in.pop();
                    while(in.isInternal());
                    fatal("P-002", new Object[] {
                        name
                    }, e);
                }
                catch(IOException ioexception) {
                    fatal("P-002", new Object[] {
                        name
                    }, e);
                }
            } else {
                fatal("P-003", null, e);
            }
        }
        catch(RuntimeException e) {
            throw new ParseException(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        }
        return ret;
    }

    private int retrievePIs() {
        if(!piQueue.empty()) {
            curName = piQueue.getNextTarget();
            curValue = piQueue.getNextContent();
            return 4;
        } else {
            return -1;
        }
    }

    private void epilogue() throws IOException, ParseException {
        try {
            afterRoot();
            maybeMisc(true);
            if(!in.isEOF())
                fatal("P-001", new Object[] {
                    Integer.toHexString(getc())
                });
        }
        catch(EndOfInputException e) {
            if(!in.isDocument()) {
                String name = in.getName();
                do
                    in = in.pop();
                while(in.isInternal());
                fatal("P-002", new Object[] {
                    name
                }, e);
            } else {
                fatal("P-003", null, e);
            }
        }
        catch(RuntimeException e) {
            throw new ParseException(e.getMessage() == null ? e.getClass().getName() : e.getMessage(), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        }
        finally {
            strTmp = null;
            attTmp = null;
            nameTmp = null;
            nameCache = null;
            if(in != null) {
                in.close();
                in = null;
            }
            params.clear();
            entities.clear();
            notations.clear();
            elements.clear();
            afterDocument();
        }
    }

    private ElementDecl getElement() throws IOException, ParseException {
        Parser2$NameCacheEntry name = maybeGetNameCacheEntry();
        if(name == null)
            return null;
        ElementDecl element = (ElementDecl)elements.get(name.name);
        if(element == null || element.contentType == null) {
            element = new ElementDecl(name.name);
            element.contentType = "ANY";
            elements.put(name.name, element);
        }
        startLine = in.getLineNumber();
        boolean sawWhite = in.maybeWhitespace();
        while(!in.peekc('>'))  {
            if(in.peekc('/')) {
                hasContent = false;
                break;
            }
            if(!sawWhite)
                fatal("P-030");
            String attName = maybeGetName();
            if(attName == null)
                fatal("P-031", new Object[] {
                    new Character(getc())
                });
            if(attTmp.getValue(attName) != null)
                fatal("P-032", new Object[] {
                    attName
                });
            in.maybeWhitespace();
            nextChar('=', "F-026", attName);
            in.maybeWhitespace();
            parseLiteral(false);
            sawWhite = in.maybeWhitespace();
            AttributeDecl info = element != null ? (AttributeDecl)element.attributes.get(attName) : null;
            String value;
            if(info == null)
                value = strTmp.toString();
            else
            if(!"CDATA".equals(info.type))
                value = normalize(!info.isFromInternalSubset);
            else
                value = strTmp.toString();
            if("xml:lang".equals(attName) && !isXmlLang(value))
                error("P-033", new Object[] {
                    value
                });
            attTmp.addAttribute("", attName, attName, info != null ? info.type : "CDATA", value, info != null ? info.defaultValue : null, true);
            haveAttributes = true;
        }
        if(element != null)
            attTmp.setIdAttributeName(element.id);
        if(element != null && element.attributes.size() != 0)
            haveAttributes = defaultAttributes(attTmp, element) || haveAttributes;
        attr = attTmp;
        return element;
    }

    private boolean maybeReferenceInContent() throws IOException, ParseException {
        return in.peekc('&');
    }

    private boolean maybeEntityReference() throws IOException, ParseException {
        return !in.peekc('#');
    }

    private Object getEntityReference() throws IOException, ParseException {
        String name = maybeGetName();
        if(name == null)
            fatal("P-009");
        nextChar(';', "F-020", name);
        Object entity = entities.get(name);
        err(" after in = " + in);
        if(entity == null)
            fatal("P-014", new Object[] {
                name
            });
        return entity;
    }

    private void elementEpilogue(ElementDecl element) throws IOException, ParseException {
        if(!in.peek(element.name, null))
            fatal("P-034", new Object[] {
                element.name, new Integer(startLine)
            });
        in.maybeWhitespace();
        nextChar('>', "F-027", element.name);
    }

    private void intRefEpilogue(Parser2$StackElement elt) throws IOException, ParseException {
        InternalEntity entity = (InternalEntity)elt.entity;
        InputEntity last = elt.in;
        if(in != last && !in.isEOF()) {
            for(; in.isInternal(); in = in.pop());
            fatal("P-052", new Object[] {
                ((EntityDecl) (entity)).name
            });
        }
        in = in.pop();
    }

    private void extRefEpilogue(Parser2$StackElement elt) throws IOException, ParseException {
        ExternalEntity entity = (ExternalEntity)elt.entity;
        if(!in.isEOF())
            fatal("P-058", new Object[] {
                ((EntityDecl) (entity)).name
            });
        in = in.pop();
    }

    private boolean maybePI(boolean skipStart) throws IOException, ParseException {
        boolean savedLexicalPE = doLexicalPE;
        if(!in.peek(skipStart ? "?" : "<?", null))
            return false;
        doLexicalPE = false;
        String target = maybeGetName();
        String piContent = null;
        if(target == null)
            fatal("P-018");
        if("xml".equals(target))
            fatal("P-019");
        if("xml".equalsIgnoreCase(target))
            fatal("P-020", new Object[] {
                target
            });
        if(maybeWhitespace()) {
            strTmp = new StringBuffer();
            try {
                do {
                    char c = in.getc();
                    if(c == '?' && in.peekc('>'))
                        break;
                    strTmp.append(c);
                } while(true);
            }
            catch(EndOfInputException endofinputexception) {
                fatal("P-021");
            }
            piContent = strTmp.toString();
        } else {
            if(!in.peek("?>", null))
                fatal("P-022");
            piContent = "";
        }
        doLexicalPE = savedLexicalPE;
        piQueue.in(target, piContent);
        return true;
    }

    private void processStartElement(ElementDecl elt) throws IOException, ParseException {
        ns.pushContext();
        boolean seenDecl = false;
        int length = attr.getLength();
        for(int i = 0; i < length; i++) {
            String attRawName = attr.getQName(i);
            String value = attr.getValue(i);
            boolean isNamespaceDecl = false;
            String prefix = "";
            if(attRawName.startsWith("xmlns")) {
                isNamespaceDecl = true;
                if(attRawName.length() != 5)
                    if(attRawName.charAt(5) == ':')
                        prefix = attRawName.substring(6);
                    else
                        isNamespaceDecl = false;
            }
            if(isNamespaceDecl) {
                if(!ns.declarePrefix(prefix, value))
                    fatal("Illegal Namespace prefix: " + prefix);
                seenDecl = true;
                attr.setURI(i, "http://www.w3.org/2000/xmlns/");
            } else {
                String attName[] = ns.processName(attRawName, parts, true);
                if(attName != null) {
                    attr.setURI(i, attName[0]);
                    attr.setLocalName(i, attName[1]);
                }
            }
        }

        if(seenDecl) {
            length = attr.getLength();
            for(int i = 0; i < length; i++) {
                String attRawName = attr.getQName(i);
                if(!attRawName.startsWith("xmlns") || attRawName.length() != 5 && attRawName.charAt(5) != ':') {
                    String attName[] = ns.processName(attRawName, parts, true);
                    if(attName != null) {
                        attr.setURI(i, attName[0]);
                        attr.setLocalName(i, attName[1]);
                    }
                }
            }

        }
        getSetCurName(elt.name, false);
        curValue = null;
    }

    private void processEndElement(ElementDecl elt) throws IOException, ParseException {
        getSetCurName(elt.name, false);
        ns.popContext();
    }

    private void getSetCurName(String rawName, boolean isAttribute) throws ParseException {
        String names[] = ns.processName(rawName, parts, isAttribute);
        if(names == null)
            fatal("P-084", new Object[] {
                rawName
            });
        curURI = names[0];
        curName = names[1];
        curValue = null;
    }

    private int parseContent() throws IOException, ParseException {
        ElementDecl elt = null;
        do {
            while(stack.empty())  {
                if(!startEmptyStack)
                    return 10;
                if(startEmptyStack && (!in.peekc('<') || (elt = getElement()) == null)) {
                    fatal("P-067");
                } else {
                    startEmptyStack = false;
                    stack.push(newStackElement(2, 256, elt, null, null));
                    if(!haveAttributes && hasContent)
                        stack.push(newStackElement(4, 1024, elt, null, null));
                    if(!namespace) {
                        curName = elt.name;
                        curValue = null;
                    } else {
                        processStartElement(elt);
                    }
                    return 1;
                }
            }
            Parser2$StackElement se = stack.pop();
            elt = se.elt;
            switch(se.curState) {
            case 256: 
                if(attr == null)
                    fatal("P-082");
                if(haveAttributes) {
                    attr = null;
                    attrIndex = 0;
                    attTmp.clear();
                    haveAttributes = false;
                }
                if(hasContent) {
                    stack.push(se);
                    stack.push(newStackElement(4, 1024, elt, null, null));
                } else {
                    hasContent = true;
                    nextChar('>', "F-027", elt.name);
                    freeStackElement(se);
                    curName = elt.name;
                    if(!namespace)
                        curValue = null;
                    else
                        processEndElement(elt);
                    return 2;
                }
                continue;

            case 1024: 
                ElementDecl e2 = null;
                Parser2$StackElement se2 = null;
                String chars = null;
                if(in.peekc('<')) {
                    if((e2 = getElement()) != null) {
                        stack.push(se);
                        stack.push(newStackElement(1, 256, e2, null, null));
                        if(!haveAttributes && hasContent)
                            stack.push(newStackElement(4, 1024, e2, null, null));
                        if(!namespace) {
                            curName = e2.name;
                            curValue = null;
                        } else {
                            processStartElement(e2);
                        }
                        return 1;
                    }
                    if(!in.peekc('/')) {
                        if(maybeComment(true)) {
                            stack.push(se);
                            continue;
                        }
                        if(maybePI(true)) {
                            stack.push(se);
                            curName = piQueue.getNextTarget();
                            curValue = piQueue.getNextContent();
                            return 4;
                        }
                        if((chars = in.getUnparsedContent(elt != null && elt.ignoreWhitespace, null)) != null) {
                            stack.push(se);
                            if(chars.length() != 0) {
                                curName = null;
                                curValue = chars;
                                return 3;
                            }
                            continue;
                        }
                        char c = getc();
                        fatal("P-079", new Object[] {
                            Integer.toHexString(c), new Character(c)
                        });
                    }
                } else {
                    if(elt != null && elt.ignoreWhitespace && in.ignorableWhitespace()) {
                        stack.push(se);
                        continue;
                    }
                    if((chars = in.getParsedContent(coalescing)) != null) {
                        stack.push(se);
                        if(chars.length() != 0) {
                            curName = null;
                            curValue = chars;
                            return 3;
                        }
                        continue;
                    }
                    if(in.isEOF()) {
                        if(se.origState == 4)
                            fatal("P-035");
                    } else
                    if(maybeReferenceInContent()) {
                        if(maybeEntityReference()) {
                            stack.push(se);
                            Object entity = getEntityReference();
                            InputEntity last = in;
                            if(entity instanceof InternalEntity) {
                                InternalEntity e = (InternalEntity)entity;
                                stack.push(newStackElement(8, 1024, elt, e, last));
                                pushReader(e.buf, ((EntityDecl) (e)).name, true);
                            } else
                            if(entity instanceof ExternalEntity) {
                                ExternalEntity e = (ExternalEntity)entity;
                                if(e.notation != null)
                                    fatal("P-053", new Object[] {
                                        ((EntityDecl) (e)).name
                                    });
                                if(pushReader(e)) {
                                    maybeTextDecl();
                                    stack.push(newStackElement(16, 1024, elt, e, null));
                                }
                            } else {
                                throw new InternalError();
                            }
                        } else {
                            stack.push(se);
                            int ret = surrogatesToCharTmp(parseCharNumber());
                            curName = null;
                            curValue = new String(charTmp, 0, ret);
                            return 3;
                        }
                        continue;
                    }
                }
                if(se.origState == 4) {
                    se2 = stack.pop();
                    if(se2.curState != 256)
                        fatal("P-083");
                    elementEpilogue(elt);
                    curName = elt.name;
                    if(!namespace)
                        curValue = null;
                    else
                        processEndElement(elt);
                    freeStackElement(se);
                    freeStackElement(se2);
                    return 2;
                }
                if(se.origState == 8) {
                    intRefEpilogue(se);
                    freeStackElement(se);
                } else
                if(se.origState == 16) {
                    extRefEpilogue(se);
                    freeStackElement(se);
                }
                break;

            default:
                fatal("P-083");
                break;
            }
        } while(true);
    }

    private Parser2$StackElement newStackElement(int origState, int curState, ElementDecl elt, EntityDecl entity, InputEntity in) {
        return new Parser2$StackElement(this, origState, curState, elt, entity, in);
    }

    private void freeStackElement(Parser2$StackElement parser2$stackelement) {
    }

    private void err(String s) {
    }

    private void debug() {
    }

    static InputEntity access$000(Parser2 x0) {
        return x0.in;
    }

}
