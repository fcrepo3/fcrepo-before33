// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPDeserializationState.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.util.IntegerArrayList;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataHandler;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            DeserializationException, SOAPInstanceBuilder, JAXRPCDeserializer, SOAPDeserializationContext

public class SOAPDeserializationState {

    private static final boolean writeDebug = false;
    private static final int CREATION_GATES_CONSTRUCTION = 5;
    private static final int INITIALIZATION_GATES_CONSTRUCTION = 17;
    private static final int COMPLETION_GATES_CONSTRUCTION = 9;
    private static final int CREATION_GATES_INITIALIZATION = 6;
    private static final int INITIALIZATION_GATES_INITIALIZATION = 10;
    private static final int COMPLETION_GATES_INITIALIZATION = 18;
    private static final int NO_STATE = 0;
    private static final int CREATED_STATE = 4;
    private static final int INITIALIZED_STATE = 8;
    private static final int COMPLETE_STATE = 16;
    private static final int CREATION_EVENT = 4;
    private static final int INITIALIZATION_EVENT = 8;
    private static final int COMPLETION_EVENT = 16;
    private static final int EVENT_BIT_MASK = 28;
    private static final int CREATION_GATE = 1;
    private static final int INITIALIZATION_GATE = 2;
    private static final int GATE_BIT_MASK = 3;
    private JAXRPCDeserializer deserializer;
    private SOAPInstanceBuilder builder;
    private List listeners;
    private IntegerArrayList listenerMembers;
    private int constructionGates;
    private int initializationGates;
    private int completionGates;
    private boolean hasBeenRead;
    private int state;
    private Object instance;
    private XMLReader recordedElement;
    private QName recordedElementExpectedName;
    private SOAPDeserializationContext recordedElementDeserialzationContext;

    public boolean isCompleteForKnownMembers() {
        return completionGates == 0;
    }

    public boolean isComplete() {
        return state == 16;
    }

    public void promoteToCompleteOrFail() {
        switch(state) {
        case 16: // '\020'
            return;

        case 8: // '\b'
            state = 16;
            return;
        }
        throw new DeserializationException("soap.incompleteObject");
    }

    public SOAPDeserializationState() {
        deserializer = null;
        builder = null;
        listeners = new ArrayList();
        listenerMembers = new IntegerArrayList();
        constructionGates = 0;
        initializationGates = 0;
        completionGates = 0;
        hasBeenRead = false;
        state = 0;
        instance = null;
        recordedElement = null;
        recordedElementExpectedName = null;
    }

    public void registerListener(SOAPDeserializationState parentState, int memberIndex) {
        if(deserializer == null) {
            throw new DeserializationException("soap.state.wont.notify.without.deserializer");
        } else {
            listeners.add(parentState);
            listenerMembers.add(memberIndex);
            parentState.waitFor(memberIndex);
            sendPastEventsTo(parentState, memberIndex);
            return;
        }
    }

    public void sendPastEventsTo(SOAPDeserializationState listener, int memberIndex) {
        for(int pastState = 0; pastState != state;) {
            switch(pastState) {
            case 0: // '\0'
                listener.setMember(memberIndex, getInstance());
                pastState = 4;
                break;

            case 4: // '\004'
                pastState = 8;
                break;

            case 8: // '\b'
                pastState = 16;
                break;
            }
            listener.beNotified(memberIndex, pastState);
        }

    }

    private void waitFor(int memberIndex) {
        switch(memberGateType(memberIndex)) {
        case 5: // '\005'
        case 9: // '\t'
        case 17: // '\021'
            constructionGates++;
            break;

        case 6: // '\006'
        case 10: // '\n'
        case 18: // '\022'
            initializationGates++;
            break;
        }
        completionGates++;
    }

    public void beNotified(int memberIndex, int event) {
        int gateType = memberGateType(memberIndex);
        int watchedEvent = gateType & 0x1c;
        if(event == watchedEvent) {
            int gatedState = gateType & 3;
            switch(gatedState) {
            case 1: // '\001'
                constructionGates--;
                break;

            case 2: // '\002'
                initializationGates--;
                break;
            }
        }
        if(event == 16)
            completionGates--;
        updateState();
    }

    private void updateState() {
        switch(state) {
        case 0: // '\0'
            if(constructionGates > 0)
                return;
            if(instance == null && builder != null)
                builder.construct();
            changeStateTo(4);
            // fall through

        case 4: // '\004'
            if(initializationGates > 0 || !hasBeenRead)
                return;
            if(builder != null)
                builder.initialize();
            changeStateTo(8);
            // fall through

        case 8: // '\b'
            if(completionGates > 0)
                return;
            changeStateTo(16);
            // fall through

        default:
            return;
        }
    }

    private void changeStateTo(int newState) {
        state = newState;
        notifyListeners();
    }

    private void notifyListeners() {
        for(int i = 0; i < listeners.size(); i++) {
            SOAPDeserializationState eachListener = (SOAPDeserializationState)listeners.get(i);
            int listenerMember = listenerMembers.get(i);
            if(state == 4)
                eachListener.setMember(listenerMember, getInstance());
            eachListener.beNotified(listenerMember, state);
        }

    }

    public int memberGateType(int memberIndex) {
        if(builder == null)
            throw new IllegalStateException();
        else
            return builder.memberGateType(memberIndex);
    }

    public void setInstance(Object instance) {
        this.instance = instance;
        if(builder != null)
            builder.setInstance(instance);
    }

    protected void setMember(int memberIndex, Object value) {
        if(builder == null) {
            throw new IllegalStateException();
        } else {
            builder.setMember(memberIndex, value);
            return;
        }
    }

    public void setBuilder(SOAPInstanceBuilder newBuilder) {
        if(newBuilder == null)
            throw new IllegalArgumentException();
        if(builder != null && builder != newBuilder) {
            throw new IllegalStateException();
        } else {
            builder = newBuilder;
            builder.setInstance(instance);
            return;
        }
    }

    public SOAPInstanceBuilder getBuilder() {
        return builder;
    }

    public void setDeserializer(JAXRPCDeserializer deserializer) {
        try {
            if(deserializer == null)
                return;
            if(this.deserializer == deserializer)
                return;
            this.deserializer = deserializer;
            if(recordedElement != null)
                deserialize(recordedElementExpectedName, recordedElement, recordedElementDeserialzationContext);
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public void doneReading() {
        hasBeenRead = true;
        updateState();
    }

    public Object getInstance() {
        if(builder == null)
            return instance;
        else
            return builder.getInstance();
    }

    public void deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        try {
            if(deserializer == null) {
                recordedElementExpectedName = name;
                recordedElement = reader.recordElement();
                recordedElementDeserialzationContext = context;
                return;
            }
            deserializer.deserialize(name, reader, context);
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    public void deserialize(DataHandler dataHandler, SOAPDeserializationContext context) throws DeserializationException {
        try {
            if(deserializer == null)
                return;
            deserializer.deserialize(dataHandler, context);
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
    }

    private String stringRep() {
        StringBuffer rep = new StringBuffer("" + hashCode() + ":");
        if(getInstance() != null) {
            String instanceClassName = getInstance().getClass().getName();
            int lastDotLoc = instanceClassName.lastIndexOf('.');
            rep.append(instanceClassName.substring(lastDotLoc));
        }
        rep.append(":");
        if(deserializer != null) {
            String deserializerClassName = deserializer.getClass().getName();
            int lastDotLoc = deserializerClassName.lastIndexOf('.');
            rep.append(deserializerClassName.substring(lastDotLoc));
        }
        return rep.toString();
    }
}
