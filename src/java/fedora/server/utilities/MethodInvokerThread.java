package fedora.server.utilities;

import java.lang.reflect.Method;

/**
 *
 * <p><b>Title:</b> MethodInvokerThread.java</p>
 * <p><b>Description:</b> A <code>Thread</code> that invokes a single method,
 * then exits.</p>
 *
 * <p>This is convenient in situations where some method should run in a separate
 * <code>Thread</code>, but it is either inconvenient or inappropriate to
 * write a <code>Runnable</code> to do the work.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class MethodInvokerThread
        extends Thread {

    /** The object in which the method should be invoked. */
    private Object m_object;

    /** The method. */
    private Method m_method;

    /** The arguments to the method. */
    private Object[] m_args;

    /** The <code>Object</code> returned by the method call, if any. */
    private Object m_returned;

    /** The <code>Throwable</code> the method call resulted in, if any. */
    private Throwable m_thrown;

    /**
     * Constructs a <code>MethodInvokerThread</code>.
     *
     * @param targetObject The object in which the method resides.
     * @param method The <code>Method</code> to invoke.
     * @param args The arguments to the method.
     */
    public MethodInvokerThread(Object targetObject, Method method,
            Object[] args) {
        m_object=targetObject;
        m_method=method;
        m_args=args;
    }

    /**
     * Constructs a <code>MethodInvokerThread</code> with a name.
     *
     * @param targetObject The object in which the method resides.
     * @param method The <code>Method</code> to invoke.
     * @param args The arguments to the method.
     * @param name The thread's name.
     */
    public MethodInvokerThread(Object targetObject, Method method,
            Object[] args, String name) {
        super(name);
        m_object=targetObject;
        m_method=method;
        m_args=args;
    }

    /**
     * Constructs a <code>MethodInvokerThread</code> with a
     * <code>ThreadGroup</code> and a name.
     *
     * @param targetObject The object in which the method resides.
     * @param method The <code>Method</code> to invoke.
     * @param args The arguments to the method.
     * @param threadGroup The <code>ThreadGroup</code> to which the thread
     *        should belong.
     * @param name The thread's name.
     */
    public MethodInvokerThread(Object targetObject, Method method,
            Object[] args, ThreadGroup threadGroup, String name) {
        super(threadGroup, name);
        m_object=targetObject;
        m_method=method;
        m_args=args;
    }

    /**
     * Invokes the <code>Method</code>, then exits.
     */
    public void run() {
        try {
            m_returned=m_method.invoke(m_object, m_args);
        } catch (Throwable thrown) {
            m_thrown=thrown;
        }
    }

    /**
     * Gets the <code>Object</code> returned by the invoked <code>Method</code>.
     *
     * @return The Object, or null if the method has no return type or the
     *         method hasn't been invoked yet.
     */
    public Object getReturned() {
        return m_returned;
    }

    /**
     * Gets the <code>Throwable</code> that resulted if an error occurred while
     * trying to invoke the <code>Method</code>.
     *
     * @return The Throwable, or null if the method's invocation did not
     *         produce a Throwable or the method hasn't been invoked yet.
     */
    public Throwable getThrown() {
        return m_thrown;
    }

}