// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Event.java

package com.sun.xml.rpc.streaming;


public final class Event {

    public int state;
    public String name;
    public String value;
    public String uri;
    public int line;

    public Event() {
        state = 6;
    }

    public Event(int s, String n, String v, String u) {
        this(s, n, v, u, -1);
    }

    public Event(int s, String n, String v, String u, int i) {
        state = 6;
        state = s;
        name = n;
        value = v;
        uri = u;
        line = i;
    }

    public Event(Event e) {
        state = 6;
        from(e);
    }

    public void from(Event e) {
        state = e.state;
        name = e.name;
        value = e.value;
        uri = e.uri;
        line = e.line;
    }

    public String toString() {
        return "Event(" + getStateName() + ", " + name + ", " + value + ", " + uri + ", " + line + ")";
    }

    protected String getStateName() {
        switch(state) {
        case 0: // '\0'
            return "start";

        case 1: // '\001'
            return "end";

        case 2: // '\002'
            return "attr";

        case 3: // '\003'
            return "chars";

        case 4: // '\004'
            return "iws";

        case 5: // '\005'
            return "pi";

        case 6: // '\006'
            return "at_end";
        }
        return "unknown";
    }
}
