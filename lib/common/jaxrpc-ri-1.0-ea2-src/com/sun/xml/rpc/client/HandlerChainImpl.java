// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   HandlerChainImpl.java

package com.sun.xml.rpc.client;

import java.util.*;
import javax.xml.rpc.handler.*;
import javax.xml.rpc.soap.SOAPFaultException;

// Referenced classes of package com.sun.xml.rpc.client:
//            HandlerException

public class HandlerChainImpl
    implements HandlerChain {

    protected List handlerInfos;
    protected List handlerInstances;
    protected int nextRequest;
    protected int nextResponse;
    protected HandlerChainImpl requestContinuation;
    protected HandlerChainImpl responseContinuation;
    protected boolean isImmutable;

    protected HandlerChainImpl(List handlerInfos, List handlerInstances, int nextRequest, int nextResponse) {
        this.handlerInfos = new ArrayList();
        requestContinuation = null;
        responseContinuation = null;
        isImmutable = false;
        this.handlerInfos = handlerInfos;
        this.handlerInstances = handlerInstances;
        this.nextRequest = nextRequest;
        this.nextResponse = nextResponse;
    }

    public HandlerChainImpl() {
        this(((List) (new ArrayList())), null, 0, -1);
    }

    protected HandlerChainImpl(List handlerInfos) {
        this(handlerInfos, null, 0, -1);
    }

    protected HandlerChainImpl createContinuation(int nextRequest, int nextResponse) {
        beImmutable();
        HandlerChainImpl continuation = new HandlerChainImpl(handlerInfos, handlerInstances, nextRequest, nextResponse);
        continuation.isImmutable = true;
        return continuation;
    }

    public void beImmutable() {
        if(!isImmutable) {
            handlerInfos = Collections.unmodifiableList(handlerInfos);
            isImmutable = true;
        }
    }

    public void handleRequest(MessageContext context, HandlerChain chain) throws SOAPFaultException {
        createInstancesIfNeeded();
        if(nextRequest + 1 > size())
            return;
        if(requestContinuation == null)
            requestContinuation = createContinuation(nextRequest + 1, nextResponse);
        getHandlerInstance(nextRequest).handleRequest(context, requestContinuation);
    }

    public void handleResponse(MessageContext context, HandlerChain chain) throws SOAPFaultException {
        createInstancesIfNeeded();
        if(nextResponse < 0)
            return;
        if(responseContinuation == null)
            responseContinuation = createContinuation(handlerInfos.size() - 1, nextResponse - 1);
        getHandlerInstance(nextResponse).handleResponse(context, responseContinuation);
    }

    public void init(Map config) {
        for(int i = 0; i < size(); i++)
            getHandlerInstance(i).init(getHandlerInfo(i).getHandlerConfig());

    }

    public void destroy() {
        for(int i = 0; i < size(); i++)
            getHandlerInstance(i).destroy();

    }

    protected void createInstancesIfNeeded() {
        if(handlerInstances == null) {
            handlerInstances = new ArrayList();
            for(int i = 0; i < handlerInfos.size(); i++) {
                Handler h = newHandler(getHandlerInfo(i));
                handlerInstances.add(h);
            }

        }
    }

    protected void destroyInstancesIfPresent() {
        if(!isImmutable && handlerInstances != null) {
            for(int i = 0; i < handlerInstances.size(); i++) {
                Handler h = (Handler)handlerInstances.get(i);
                h.destroy();
            }

            handlerInstances = null;
        }
    }

    Handler getHandlerInstance(int index) {
        return (Handler)handlerInstances.get(index);
    }

    HandlerInfo getHandlerInfo(int index) {
        return (HandlerInfo)get(index);
    }

    Handler newHandler(HandlerInfo handlerInfo) {
        try {
            Handler handler = (Handler)handlerInfo.getHandlerClass().newInstance();
            handler.init(handlerInfo.getHandlerConfig());
            return handler;
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Unable to instantiate handler: " + handlerInfo.getHandlerClass());
    }

    public int size() {
        return handlerInfos.size();
    }

    public boolean isEmpty() {
        return handlerInfos.isEmpty();
    }

    public boolean contains(Object o) {
        return handlerInfos.contains(o);
    }

    public Iterator iterator() {
        return handlerInfos.iterator();
    }

    public Object[] toArray() {
        return handlerInfos.toArray();
    }

    public Object[] toArray(Object a[]) {
        return handlerInfos.toArray(a);
    }

    public boolean add(Object o) {
        checkIsHandlerInfo(o);
        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.add(o);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public boolean remove(Object o) {
        checkIsHandlerInfo(o);
        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.remove(o);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public boolean containsAll(Collection c) {
        return handlerInfos.containsAll(c);
    }

    public boolean addAll(Collection c) {
        Object currentHandler;
        for(Iterator eachHandlerInfo = c.iterator(); eachHandlerInfo.hasNext(); checkIsHandlerInfo(currentHandler))
            currentHandler = eachHandlerInfo.next();

        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.addAll(c);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public boolean addAll(int index, Collection c) {
        Object currentHandler;
        for(Iterator eachHandlerInfo = c.iterator(); eachHandlerInfo.hasNext(); checkIsHandlerInfo(currentHandler))
            currentHandler = eachHandlerInfo.next();

        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.addAll(index, c);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public boolean removeAll(Collection c) {
        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.removeAll(c);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public boolean retainAll(Collection c) {
        destroyInstancesIfPresent();
        boolean returnValue = handlerInfos.retainAll(c);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public void clear() {
        destroyInstancesIfPresent();
        handlerInfos.clear();
        nextResponse = handlerInfos.size() - 1;
    }

    public boolean equals(Object o) {
        return handlerInfos.equals(o);
    }

    public int hashCode() {
        return handlerInfos.hashCode();
    }

    public Object get(int index) {
        return handlerInfos.get(index);
    }

    public Object set(int index, Object element) {
        checkIsHandlerInfo(element);
        destroyInstancesIfPresent();
        Object returnValue = handlerInfos.set(index, element);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public void add(int index, Object element) {
        checkIsHandlerInfo(element);
        destroyInstancesIfPresent();
        handlerInfos.add(index, element);
        nextResponse = handlerInfos.size() - 1;
    }

    public Object remove(int index) {
        destroyInstancesIfPresent();
        Object returnValue = handlerInfos.remove(index);
        nextResponse = handlerInfos.size() - 1;
        return returnValue;
    }

    public int indexOf(Object o) {
        checkIsHandlerInfo(o);
        return handlerInfos.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        checkIsHandlerInfo(o);
        return handlerInfos.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        throw new UnsupportedOperationException();
    }

    public ListIterator listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    public List subList(int fromIndex, int toIndex) {
        return new HandlerChainImpl(handlerInfos.subList(fromIndex, toIndex));
    }

    protected void checkIsHandlerInfo(Object o) {
        if(!(o instanceof HandlerInfo))
            throw new HandlerException("handler.chain.contains.handlerinfo.only", new Object[] {
                o.getClass().getName()
            });
        else
            return;
    }
}
