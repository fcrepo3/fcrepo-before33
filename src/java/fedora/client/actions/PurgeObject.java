package fedora.client.actions;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import fedora.client.Administrator;
import fedora.client.export.AutoExporter;

public class PurgeObject
        extends AbstractAction {

    private Set m_pids;
    private boolean m_prompt;
    
    public PurgeObject() {
        super("Purge Object...");
        m_pids=new HashSet();
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
        if (m_prompt) {
            m_pids.add(JOptionPane.showInputDialog("Enter the PID."));
        }
        AutoExporter exporter=null;
        try {
            exporter=new AutoExporter(Administrator.getHost(), Administrator.getPort());
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
                    exporter.export(pid, out);
                    JInternalFrame viewFrame=new JInternalFrame("Viewing " + pid, true, true, true, true);
                    viewFrame.setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Edit16.gif")));
                    JTextComponent textEditor=new JTextArea();
                    textEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                    textEditor.setText(new String(out.toByteArray()));
                    viewFrame.getContentPane().add(new JScrollPane(textEditor));
                    viewFrame.setSize(300,300);
                    viewFrame.setVisible(true);
                    Administrator.getDesktop().add(viewFrame);
                    try {
                        viewFrame.setSelected(true);
                    } catch (java.beans.PropertyVetoException pve) {}
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