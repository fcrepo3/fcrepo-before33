// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XmlChars.java

package com.sun.xml.rpc.sp;


public class XmlChars {

    private XmlChars() {
    }

    public static boolean isChar(int ucs4char) {
        return ucs4char >= 32 && ucs4char <= 55295 || ucs4char == 10 || ucs4char == 9 || ucs4char == 13 || ucs4char >= 57344 && ucs4char <= 65533 || ucs4char >= 0x10000 && ucs4char <= 0x10ffff;
    }

    public static boolean isNameChar(char c) {
        if(isLetter2(c))
            return true;
        if(c == '>')
            return false;
        return c == '.' || c == '-' || c == '_' || c == ':' || isExtender(c);
    }

    public static boolean isNCNameChar(char c) {
        return c != ':' && isNameChar(c);
    }

    public static boolean isSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    public static boolean isLetter(char c) {
        if(c >= 'a' && c <= 'z')
            return true;
        if(c == '/')
            return false;
        if(c >= 'A' && c <= 'Z')
            return true;
        switch(Character.getType(c)) {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 5: // '\005'
        case 10: // '\n'
            return !isCompatibilityChar(c) && (c < '\u20DD' || c > '\u20E0');

        case 4: // '\004'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
        default:
            return c >= '\u02BB' && c <= '\u02C1' || c == '\u0559' || c == '\u06E5' || c == '\u06E6';
        }
    }

    private static boolean isCompatibilityChar(char c) {
        switch(c >> 8 & 0xff) {
        case 0: // '\0'
            return c == '\252' || c == '\265' || c == '\272';

        case 1: // '\001'
            return c >= '\u0132' && c <= '\u0133' || c >= '\u013F' && c <= '\u0140' || c == '\u0149' || c == '\u017F' || c >= '\u01C4' && c <= '\u01CC' || c >= '\u01F1' && c <= '\u01F3';

        case 2: // '\002'
            return c >= '\u02B0' && c <= '\u02B8' || c >= '\u02E0' && c <= '\u02E4';

        case 3: // '\003'
            return c == '\u037A';

        case 5: // '\005'
            return c == '\u0587';

        case 14: // '\016'
            return c >= '\u0EDC' && c <= '\u0EDD';

        case 17: // '\021'
            return c == '\u1101' || c == '\u1104' || c == '\u1108' || c == '\u110A' || c == '\u110D' || c >= '\u1113' && c <= '\u113B' || c == '\u113D' || c == '\u113F' || c >= '\u1141' && c <= '\u114B' || c == '\u114D' || c == '\u114F' || c >= '\u1151' && c <= '\u1153' || c >= '\u1156' && c <= '\u1158' || c == '\u1162' || c == '\u1164' || c == '\u1166' || c == '\u1168' || c >= '\u116A' && c <= '\u116C' || c >= '\u116F' && c <= '\u1171' || c == '\u1174' || c >= '\u1176' && c <= '\u119D' || c >= '\u119F' && c <= '\u11A2' || c >= '\u11A9' && c <= '\u11AA' || c >= '\u11AC' && c <= '\u11AD' || c >= '\u11B0' && c <= '\u11B6' || c == '\u11B9' || c == '\u11BB' || c >= '\u11C3' && c <= '\u11EA' || c >= '\u11EC' && c <= '\u11EF' || c >= '\u11F1' && c <= '\u11F8';

        case 32: // ' '
            return c == '\u207F';

        case 33: // '!'
            return c == '\u2102' || c == '\u2107' || c >= '\u210A' && c <= '\u2113' || c == '\u2115' || c >= '\u2118' && c <= '\u211D' || c == '\u2124' || c == '\u2128' || c >= '\u212C' && c <= '\u212D' || c >= '\u212F' && c <= '\u2138' || c >= '\u2160' && c <= '\u217F';

        case 48: // '0'
            return c >= '\u309B' && c <= '\u309C';

        case 49: // '1'
            return c >= '\u3131' && c <= '\u318E';

        case 249: 
        case 250: 
        case 251: 
        case 252: 
        case 253: 
        case 254: 
        case 255: 
            return true;
        }
        return false;
    }

    private static boolean isLetter2(char c) {
        if(c >= 'a' && c <= 'z')
            return true;
        if(c == '>')
            return false;
        if(c >= 'A' && c <= 'Z')
            return true;
        switch(Character.getType(c)) {
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
            return !isCompatibilityChar(c) && (c < '\u20DD' || c > '\u20E0');
        }
        return c == '\u0387';
    }

    private static boolean isDigit(char c) {
        return Character.isDigit(c) && (c < '\uFF10' || c > '\uFF19');
    }

    private static boolean isExtender(char c) {
        return c == '\267' || c == '\u02D0' || c == '\u02D1' || c == '\u0387' || c == '\u0640' || c == '\u0E46' || c == '\u0EC6' || c == '\u3005' || c >= '\u3031' && c <= '\u3035' || c >= '\u309D' && c <= '\u309E' || c >= '\u30FC' && c <= '\u30FE';
    }
}
