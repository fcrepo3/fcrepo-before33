package fedora.client.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import fedora.client.Administrator;
import fedora.client.purge.AutoPurger;

public class PurgeObject
        extends AbstractAction {

    private Set m_pids;
    private boolean m_prompt;
    
    public PurgeObject() {
        super("Purge Object...");
        m_prompt=true;
    }

    public PurgeObject(String pid) {
        super("Purge Object");
        m_pids=new HashSet();
        m_pids.add(pid);
    }

    public PurgeObject(Set pids) {
        super("Purge Objects");
        m_pids=pids;
    }
    
    public void actionPerformed(ActionEvent ae) {
        boolean failed=false;
        if (m_prompt) {
            String pid=JOptionPane.showInputDialog("Enter the PID.");
            if (pid==null) {
                return;
            }
            m_pids=new HashSet();
            m_pids.add(pid);
        }
        AutoPurger purger=null;
        try {
            purger=new AutoPurger(Administrator.getHost(), Administrator.getPort());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Purge Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
        if (purger!=null) {
            Iterator pidIter=m_pids.iterator();
            if (m_pids.size()==1) {
                String pid=(String) pidIter.next();
                // just purge one
                String reason=JOptionPane.showInputDialog("Why are you permanently removing " + pid + "?"); 
                if (reason!=null) {
                    try {
                        purger.purge(pid, reason);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                e.getClass().getName() + ": " + e.getMessage(),
                                "Purge Failure",
                                JOptionPane.ERROR_MESSAGE);
                        failed=true;
                    }
                    if (!failed) {
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                "Purge succeeded.");
                    }
                }
            } else {
                // purge multiple
                while (pidIter.hasNext()) {
                    try {
                        String pid=(String) pidIter.next();
                        String reason=JOptionPane.showInputDialog("Why are you permanently removing " + pid + "?"); 
                        if (reason!=null) {
                                purger.purge(pid, reason);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                e.getClass().getName() + ": " + e.getMessage(),
                                "Purge Failure",
                                JOptionPane.ERROR_MESSAGE);
                        failed=true;
                    }
                }
                if (!failed) {
                    JOptionPane.showMessageDialog(Administrator.getDesktop(),
                            "Purge succeeded.");
                }
            }
        }
    }

}