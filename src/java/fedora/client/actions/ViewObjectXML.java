package fedora.client.actions;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

import fedora.client.Administrator;
import fedora.client.export.AutoExporter;

/**
 *
 * <p><b>Title:</b> ViewObjectXML.java</p>
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
public class ViewObjectXML
        extends AbstractAction {

    private Set m_pids;
    private boolean m_prompt;
    private JPanel m_viewPane;

    public ViewObjectXML() {
        super("View Object XML...");
        m_prompt=true;
    }

    public ViewObjectXML(String pid) {
        super("View Object XML");
        m_pids=new HashSet();
        m_pids.add(pid);
    }

    public ViewObjectXML(String pid, JPanel viewPane) {
        super("View Object XML");
        m_pids=new HashSet();
        m_pids.add(pid);
        m_viewPane=viewPane;
    }

    public ViewObjectXML(Set pids) {
        super("View Objects XML");
        m_pids=pids;
    }

    public void actionPerformed(ActionEvent ae) {
        if (m_prompt) {
            String pid=JOptionPane.showInputDialog("Enter the PID.");
            if (pid==null) {
                return;
            }
            m_pids=new HashSet();
            m_pids.add(pid);
        }
        AutoExporter exporter=null;
        try {
            exporter=new AutoExporter(Administrator.getHost(), Administrator.getPort(), Administrator.getUser(), Administrator.getPass());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "View Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (exporter!=null) {
            Iterator pidIter=m_pids.iterator();
            while (pidIter.hasNext()) {
                try {
                    String pid=(String) pidIter.next();
                    ByteArrayOutputStream out=new ByteArrayOutputStream();
                    exporter.export(pid, out, true);
                    JTextComponent textEditor=new JTextArea();
                    textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                    textEditor.setText(new String(out.toByteArray()));
                    textEditor.setCaretPosition(0);
                    textEditor.setEditable(false);
                    if (m_viewPane==null) {
                        JInternalFrame viewFrame=new JInternalFrame("Viewing " + pid, true, true, true, true);
                        viewFrame.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Edit16.gif")));
                        viewFrame.getContentPane().add(new JScrollPane(textEditor));
                        viewFrame.setSize(720,520);
                        viewFrame.setVisible(true);
                        Administrator.getDesktop().add(viewFrame);
                        try {
                            viewFrame.setSelected(true);
                        } catch (java.beans.PropertyVetoException pve) {}
                    } else {
                        m_viewPane.removeAll();
                        m_viewPane.setLayout(new BorderLayout());
                        m_viewPane.add(new JScrollPane(textEditor), BorderLayout.CENTER);
                        m_viewPane.validate();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            e.getClass().getName() + ": " + e.getMessage(),
                            "View Failure",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}