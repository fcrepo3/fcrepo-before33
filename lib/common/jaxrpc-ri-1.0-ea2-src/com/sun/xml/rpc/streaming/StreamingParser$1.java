// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StreamingParser.java

package com.sun.xml.rpc.streaming;


// Referenced classes of package com.sun.xml.rpc.streaming:
//            Stream, StreamingParser, Event

class StreamingParser$1
    implements Stream {

    private final StreamingParser this$0; /* synthetic field */

    StreamingParser$1(StreamingParser this$0) {
        this.this$0 = this$0;
    }

    public int next(Event event) {
        int state = this$0.next();
        event.state = StreamingParser.access$000(this$0);
        event.name = StreamingParser.access$100(this$0);
        event.value = StreamingParser.access$200(this$0);
        event.uri = StreamingParser.access$300(this$0);
        event.line = StreamingParser.access$400(this$0);
        return state;
    }
}
