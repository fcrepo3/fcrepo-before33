// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ContentModelState.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            ContentModel, EndOfInputException

class ContentModelState {

    private ContentModel model;
    private boolean sawOne;
    private ContentModelState next;

    ContentModelState(ContentModel model) {
        this(model, null);
    }

    private ContentModelState(Object content, ContentModelState next) {
        model = (ContentModel)content;
        this.next = next;
        sawOne = false;
    }

    boolean terminate() {
        switch(model.type) {
        case 43: // '+'
            if(!sawOne && !model.empty())
                return false;
            // fall through

        case 42: // '*'
        case 63: // '?'
            return next == null || next.terminate();

        case 124: // '|'
            return model.empty() && (next == null || next.terminate());

        case 44: // ','
            ContentModel m;
            for(m = model; m != null && m.empty(); m = m.next);
            if(m != null)
                return false;
            else
                return next == null || next.terminate();

        case 0: // '\0'
            return false;

        default:
            throw new InternalError();
        }
    }

    ContentModelState advance(String token) throws EndOfInputException {
        switch(model.type) {
        default:
            break;

        case 42: // '*'
        case 43: // '+'
            if(model.first(token)) {
                sawOne = true;
                if(model.content instanceof String)
                    return this;
                else
                    return (new ContentModelState(model.content, this)).advance(token);
            }
            if((model.type == '*' || sawOne) && next != null)
                return next.advance(token);
            break;

        case 63: // '?'
            if(model.first(token))
                if(model.content instanceof String)
                    return next;
                else
                    return (new ContentModelState(model.content, next)).advance(token);
            if(next != null)
                return next.advance(token);
            break;

        case 124: // '|'
            for(ContentModel m = model; m != null; m = m.next)
                if(m.content instanceof String) {
                    if(token == m.content)
                        return next;
                } else
                if(((ContentModel)m.content).first(token))
                    return (new ContentModelState(m.content, next)).advance(token);

            if(model.empty() && next != null)
                return next.advance(token);
            break;

        case 44: // ','
            if(model.first(token)) {
                if(model.type == 0)
                    return next;
                ContentModelState nextState;
                if(model.next == null) {
                    nextState = new ContentModelState(model.content, next);
                } else {
                    nextState = new ContentModelState(model.content, this);
                    model = model.next;
                }
                return nextState.advance(token);
            }
            if(model.empty() && next != null)
                return next.advance(token);
            break;

        case 0: // '\0'
            if(model.content == token)
                return next;
            break;
        }
        throw new EndOfInputException();
    }
}
