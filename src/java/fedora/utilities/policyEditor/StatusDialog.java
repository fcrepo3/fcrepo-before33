package fedora.utilities.policyEditor;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
/*
 * Created on May 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author diglib
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class StatusDialog extends JDialog implements ActionListener
{
    private JButton button = null;
    private JPanel panel = null;

    /**
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public StatusDialog(Frame owner, String title) throws HeadlessException
    {
        super(owner, title, true);
        getContentPane().setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridLayout(0,1,50,5));
        panel.add(new JLabel("Writing Policy File:"));
        getContentPane().add(panel, BorderLayout.CENTER);
        button = new JButton("Close");
        getContentPane().add(button, BorderLayout.SOUTH);
        button.setEnabled(false);
        button.addActionListener(this);
        ((JComponent)(getContentPane())).setBorder(new EmptyBorder(20,20,20,20 ));
    }
    
    public void addText(String text)
    {
        panel.add(new JLabel(text));
        this.pack();
        this.validate();
    }
    
    public void finish()
    {
        button.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getActionCommand().equals("Close"))
        { 
            this.hide();
            this.dispose();
        }                       
    }
}
