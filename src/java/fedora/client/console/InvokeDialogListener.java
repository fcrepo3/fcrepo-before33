package fedora.client.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

import fedora.server.utilities.MethodInvokerThread;

/**
 *
 * <p><b>Title:</b> InvokeDialogListener.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InvokeDialogListener
        implements ActionListener {

    private JDialog m_dialog;
    private ConsoleCommandInvoker m_invoker;

    public InvokeDialogListener(JDialog dialog, ConsoleCommandInvoker invoker) {
        m_dialog=dialog;
        m_invoker=invoker;
    }

    public void actionPerformed(ActionEvent event) {
        m_dialog.hide();
        if (event.getActionCommand().equals("OK")) {
            try {
                MethodInvokerThread th=new MethodInvokerThread(m_invoker,
                        m_invoker.getClass().getMethod("invoke", new Class[0]),
                        new Object[0]);
                th.start();
/*            } catch (InvocationTargetException ite) {
                System.out.println("Invocation target exception: " + ite.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Interrupted while doing request: " + ie.getMessage());
*/
            } catch (NoSuchMethodException nsme) {
                System.out.println("No such method as invoke()? This Shouldnt happen!");
            }
            // m_invoker.invoke();
        }
    }
}