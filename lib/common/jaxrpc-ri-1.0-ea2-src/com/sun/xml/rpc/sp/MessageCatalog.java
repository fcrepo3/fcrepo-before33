// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MessageCatalog.java

package com.sun.xml.rpc.sp;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.*;

public abstract class MessageCatalog {

    private String bundleName;
    private Map cache;

    protected MessageCatalog(Class packageMember) {
        this(packageMember, "Messages");
    }

    private MessageCatalog(Class packageMember, String bundle) {
        cache = new HashMap(5);
        bundleName = packageMember.getName();
        int index = bundleName.lastIndexOf('.');
        if(index == -1)
            bundleName = "";
        else
            bundleName = bundleName.substring(0, index) + ".";
        bundleName = bundleName + "resources." + bundle;
    }

    public String getMessage(Locale locale, String messageId) {
        if(locale == null)
            locale = Locale.getDefault();
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            return bundle.getString(messageId);
        }
        catch(MissingResourceException missingresourceexception) {
            return packagePrefix(messageId);
        }
    }

    private String packagePrefix(String messageId) {
        String temp = getClass().getName();
        int index = temp.lastIndexOf('.');
        if(index == -1)
            temp = "";
        else
            temp = temp.substring(0, index);
        return temp + '/' + messageId;
    }

    public String getMessage(Locale locale, String messageId, Object parameters[]) {
        if(parameters == null)
            return getMessage(locale, messageId);
        for(int i = 0; i < parameters.length; i++)
            if(!(parameters[i] instanceof String) && !(parameters[i] instanceof Number) && !(parameters[i] instanceof Date))
                if(parameters[i] == null)
                    parameters[i] = "(null)";
                else
                    parameters[i] = parameters[i].toString();

        if(locale == null)
            locale = Locale.getDefault();
        MessageFormat format;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
            format = new MessageFormat(bundle.getString(messageId));
        }
        catch(MissingResourceException missingresourceexception) {
            String retval = packagePrefix(messageId);
            for(int i = 0; i < parameters.length; i++) {
                retval = retval + ' ';
                retval = retval + parameters[i];
            }

            return retval;
        }
        format.setLocale(locale);
        StringBuffer result = new StringBuffer();
        result = format.format(parameters, result, new FieldPosition(0));
        return result.toString();
    }

    public Locale chooseLocale(String languages[]) {
        if((languages = canonicalize(languages)) != null) {
            for(int i = 0; i < languages.length; i++)
                if(isLocaleSupported(languages[i]))
                    return getLocale(languages[i]);

        }
        return null;
    }

    private String[] canonicalize(String languages[]) {
        boolean didClone = false;
        int trimCount = 0;
        if(languages == null)
            return languages;
        for(int i = 0; i < languages.length; i++) {
            String lang = languages[i];
            int len = lang.length();
            if(len != 2 && len != 5) {
                if(!didClone) {
                    languages = (String[])languages.clone();
                    didClone = true;
                }
                languages[i] = null;
                trimCount++;
            } else
            if(len == 2) {
                lang = lang.toLowerCase();
                if(lang != languages[i]) {
                    if(!didClone) {
                        languages = (String[])languages.clone();
                        didClone = true;
                    }
                    languages[i] = lang;
                }
            } else {
                char buf[] = new char[5];
                buf[0] = Character.toLowerCase(lang.charAt(0));
                buf[1] = Character.toLowerCase(lang.charAt(1));
                buf[2] = '_';
                buf[3] = Character.toUpperCase(lang.charAt(3));
                buf[4] = Character.toUpperCase(lang.charAt(4));
                if(!didClone) {
                    languages = (String[])languages.clone();
                    didClone = true;
                }
                languages[i] = new String(buf);
            }
        }

        if(trimCount != 0) {
            String temp[] = new String[languages.length - trimCount];
            int i = 0;
            trimCount = 0;
            for(; i < temp.length; i++) {
                while(languages[i + trimCount] == null) 
                    trimCount++;
                temp[i] = languages[i + trimCount];
            }

            languages = temp;
        }
        return languages;
    }

    private Locale getLocale(String localeName) {
        int index = localeName.indexOf('_');
        String language;
        String country;
        if(index == -1) {
            if(localeName.equals("de"))
                return Locale.GERMAN;
            if(localeName.equals("en"))
                return Locale.ENGLISH;
            if(localeName.equals("fr"))
                return Locale.FRENCH;
            if(localeName.equals("it"))
                return Locale.ITALIAN;
            if(localeName.equals("ja"))
                return Locale.JAPANESE;
            if(localeName.equals("ko"))
                return Locale.KOREAN;
            if(localeName.equals("zh"))
                return Locale.CHINESE;
            language = localeName;
            country = "";
        } else {
            if(localeName.equals("zh_CN"))
                return Locale.SIMPLIFIED_CHINESE;
            if(localeName.equals("zh_TW"))
                return Locale.TRADITIONAL_CHINESE;
            language = localeName.substring(0, index);
            country = localeName.substring(index + 1);
        }
        return new Locale(language, country);
    }

    public boolean isLocaleSupported(String localeName) {
        Boolean value = (Boolean)cache.get(localeName);
        if(value != null)
            return value.booleanValue();
        ClassLoader loader = null;
        do {
            String name = bundleName + "_" + localeName;
            try {
                Class.forName(name);
                cache.put(localeName, Boolean.TRUE);
                return true;
            }
            catch(Exception exception) { }
            if(loader == null)
                loader = getClass().getClassLoader();
            name = name.replace('.', '/');
            name = name + ".properties";
            java.io.InputStream in;
            if(loader == null)
                in = ClassLoader.getSystemResourceAsStream(name);
            else
                in = loader.getResourceAsStream(name);
            if(in != null) {
                cache.put(localeName, Boolean.TRUE);
                return true;
            }
            int index = localeName.indexOf('_');
            if(index > 0) {
                localeName = localeName.substring(0, index);
            } else {
                cache.put(localeName, Boolean.FALSE);
                return false;
            }
        } while(true);
    }
}
