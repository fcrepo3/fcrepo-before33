package fedora.client.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fedora.client.Administrator;

public class ConsoleSendButtonListener
        implements ActionListener {
        
    private Administrator m_mainFrame;
    private ComboBoxModel m_model;
        
    public ConsoleSendButtonListener(ComboBoxModel model, Administrator mainFrame) {
        m_model=model;
        m_mainFrame=mainFrame;
    }
    
    public void actionPerformed(ActionEvent event) {
        JFrame dummy=new JFrame();
        dummy.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("images/standard/general/SendMail16.gif")).getImage());
        JDialog jd=new JDialog(dummy, "Input", true); 
        jd.getContentPane().add(
                ((ConsoleCommand) (m_model.getSelectedItem())).getInvokerPanel(), 
                BorderLayout.CENTER);
        jd.setLocation(m_mainFrame.getCenteredPos(jd.getWidth(), 
        jd.getHeight()));
        jd.pack();
        jd.show();
    }
}