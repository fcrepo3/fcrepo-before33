// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ContentModel.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            SimpleHashtable

final class ContentModel {

    public char type;
    public Object content;
    public ContentModel next;
    private SimpleHashtable cache;

    public ContentModel(String element) {
        cache = new SimpleHashtable();
        type = '\0';
        content = element;
    }

    public ContentModel(char type, ContentModel content) {
        cache = new SimpleHashtable();
        this.type = type;
        this.content = content;
    }

    public boolean empty() {
        switch(type) {
        case 42: // '*'
        case 63: // '?'
            return true;

        case 0: // '\0'
        case 43: // '+'
            return false;

        case 124: // '|'
            if((content instanceof ContentModel) && ((ContentModel)content).empty())
                return true;
            for(ContentModel m = next; m != null; m = m.next)
                if(m.empty())
                    return true;

            return false;

        case 44: // ','
            if(content instanceof ContentModel) {
                if(!((ContentModel)content).empty())
                    return false;
            } else {
                return false;
            }
            for(ContentModel m = next; m != null; m = m.next)
                if(!m.empty())
                    return false;

            return true;
        }
        throw new InternalError();
    }

    public boolean first(String token) {
        Boolean b = (Boolean)cache.get(token);
        if(b != null)
            return b.booleanValue();
        boolean retval;
        switch(type) {
        case 0: // '\0'
        case 42: // '*'
        case 43: // '+'
        case 63: // '?'
            if(content instanceof String)
                retval = content == token;
            else
                retval = ((ContentModel)content).first(token);
            break;

        case 44: // ','
            if(content instanceof String)
                retval = content == token;
            else
            if(((ContentModel)content).first(token))
                retval = true;
            else
            if(!((ContentModel)content).empty())
                retval = false;
            else
            if(next != null)
                retval = next.first(token);
            else
                retval = false;
            break;

        case 124: // '|'
            if((content instanceof String) && content == token)
                retval = true;
            else
            if(((ContentModel)content).first(token))
                retval = true;
            else
            if(next != null)
                retval = next.first(token);
            else
                retval = false;
            break;

        default:
            throw new InternalError();
        }
        if(retval)
            cache.put(token, Boolean.TRUE);
        else
            cache.put(token, Boolean.FALSE);
        return retval;
    }
}
