package fedora.client.console;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 *
 * <p><b>Title:</b> ConsoleCommandInvoker.java</p>
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
public class ConsoleCommandInvoker
        extends JPanel {

    private ConsoleCommand m_command;
    private Console m_console;
    private InputPanel[] m_inputPanels;

    public ConsoleCommandInvoker(ConsoleCommand command, Console console) {
        m_command=command;
        m_console=console;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel commandNameLabel=new JLabel("Command: " + m_command.getName());
        JPanel jeez=new JPanel();
        jeez.setLayout(new BorderLayout());
        jeez.add(commandNameLabel, BorderLayout.WEST);
        add(jeez);
        Class types[]=command.getParameterTypes();
        String names[]=command.getParameterNames();
        m_inputPanels=new InputPanel[types.length];
        for (int i=0; i<types.length; i++) {
            JPanel typeNameInputPanel=new JPanel();
            typeNameInputPanel.setLayout(new BorderLayout());
            typeNameInputPanel.add(new JLabel(names[i] + " ("
                    + command.getUnqualifiedName(types[i]) + ") : "),
                    BorderLayout.WEST);
            m_inputPanels[i]=InputPanelFactory.getPanel(types[i]);
            typeNameInputPanel.add(m_inputPanels[i]);
            add(typeNameInputPanel);
        }
        JLabel returnTypeLabel=new JLabel("Returns: " +
                m_command.getUnqualifiedName(m_command.getReturnType()));
        JPanel jeez2=new JPanel();
        jeez2.setLayout(new BorderLayout());
        jeez2.add(returnTypeLabel, BorderLayout.WEST);
        add(jeez2);
    }

    /**
     * Invokes the console command with whatever parameters have been
     * set thus far, sending any errors to the console.
     */
    public void invoke() {
        try {
            m_console.setBusy(true);
            m_console.print("Invoking " + m_command.toString() + "\n");
            Object[] parameters=new Object[m_command.getParameterTypes().length];
            if (m_command.getParameterTypes().length>0) {
                for (int i=0; i<m_command.getParameterTypes().length; i++) {
                    m_console.print(m_command.getParameterNames()[i]);
                    m_console.print("=");
                    Object paramValue=m_inputPanels[i].getValue();
                    parameters[i]=paramValue;
                    if (paramValue==null) {
                        m_console.print("<null>");
                    } else {
                        m_console.print(stringify(paramValue));
                    }
                    m_console.print("\n");
                }
            }
            long startms=new Date().getTime();
            Object returned=m_command.invoke(
                    m_console.getInvocationTarget(m_command), parameters);
            long endms=new Date().getTime();
            long totalms=endms-startms;
            if (returned!=null) {
                m_console.print("Returned: " + stringify(returned) + "\n");
            } else {
                if (m_command.getReturnType()==null) {
                    m_console.print("Returned.\n");
                } else {
                    m_console.print("Returned: <null>\n");
                }
            }
            String duration;
            if (totalms==0) {
                duration="< 0.001 seconds.";
            } else {
                double secs=totalms/1000.0;
                duration=secs+" seconds.";
            }
            m_console.print("Roundtrip time: ");
            m_console.print(duration);
            m_console.print("\n");
        } catch (InvocationTargetException ite) {
            m_console.print("ERROR (" + ite.getTargetException().getClass().getName() + ") : "
                    + ite.getTargetException().getMessage() + "\n");
        } catch (Throwable th) {
            m_console.print("ERROR (" + th.getClass().getName() + ") : "
                    + th.getMessage() + "\n");
            StringWriter sw=new StringWriter();
            th.printStackTrace(new PrintWriter(sw));
            m_console.print(sw.toString());
        } finally {
            m_console.setBusy(false);
        }
    }

    private String stringify(Object obj) {
        if (obj==null) { return "<null>"; }
        String nm=obj.getClass().getName();
        if (nm.startsWith("[L")) {
            // print array
            StringBuffer buf=new StringBuffer();
            buf.append("{");
            for (int i=0; i<Array.getLength(obj); i++) {
                if (i>0) {
                    buf.append(",");
                }
                buf.append(Array.get(obj, i).toString());
            }
            buf.append("}");
            return buf.toString();
        }
        return obj.toString();
    }

}