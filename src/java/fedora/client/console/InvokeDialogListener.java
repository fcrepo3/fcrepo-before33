package fedora.client.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

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
        if (event.getActionCommand().equals("Ok")) {
            m_invoker.invoke();
        }
    }
}