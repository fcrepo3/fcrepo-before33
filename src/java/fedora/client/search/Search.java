package fedora.client.search;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import fedora.client.Administrator;

public class Search
        extends JInternalFrame {
        
    private JTextField m_pidField=new JTextField("*", 8);
    private JTextField m_labelField=new JTextField("*", 8);
    private JTextField m_typeField=new JTextField("*", 8);
    private JTextField m_contentModelField=new JTextField("*", 8);
    private JTextField m_stateField=new JTextField("*", 8);
    private JTextField m_lockedByField=new JTextField("*", 8);
    
    public Search() {
        super("Search Repository",
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

/*
        String[] columnNames = {"PID", 
                                "Label",
                                "Type",
                                "Content Model",
                                "State",
                                "Locked By",
                                "Created",
                                "Last Modified"};
*/
        JButton btn=new JButton("Search");
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseOnResults();
            }
        });
        JPanel entryPanel=new JPanel();
        entryPanel.setLayout(new BorderLayout());
        entryPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        entryPanel.add(new JLabel("Search Criteria"), BorderLayout.NORTH);
        JPanel labelPanel=new JPanel();
        labelPanel.setLayout(new GridLayout(0, 2));
        labelPanel.add(new JLabel("PID"));
        labelPanel.add(m_pidField);
        labelPanel.add(new JLabel("Label"));
        labelPanel.add(m_labelField);
        labelPanel.add(new JLabel("Type"));
        labelPanel.add(m_typeField);
        labelPanel.add(new JLabel("Content Model"));
        labelPanel.add(m_contentModelField);
        labelPanel.add(new JLabel("State"));
        labelPanel.add(m_stateField);
        labelPanel.add(new JLabel("Locked By"));
        labelPanel.add(m_lockedByField);
        labelPanel.add(new JLabel("Earliest Create Date"));
        labelPanel.add(new JLabel("NOT IMPLEMENTED"));
        labelPanel.add(new JLabel("Latest Create Date"));
        labelPanel.add(new JLabel("NOT IMPLEMENTED"));
        labelPanel.add(new JLabel("Earliest Modified Date"));
        labelPanel.add(new JLabel("NOT IMPLEMENTED"));
        labelPanel.add(new JLabel("Latest Modified Date"));
        labelPanel.add(new JLabel("NOT IMPLEMENTED"));
        entryPanel.add(labelPanel, BorderLayout.WEST);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(entryPanel, BorderLayout.CENTER);
        getContentPane().add(btn, BorderLayout.SOUTH);

        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Search16.gif")));

        setSize(400,400);
    }
    
    public void browseOnResults() {
        String pidPattern=null;
        if (!m_pidField.getText().equals("*")) {
            pidPattern=m_pidField.getText();
        }
        String foType=null;
        if (!m_typeField.getText().equals("*")) {
            foType=m_typeField.getText();
        }
        String lockedByPattern=null;
        if (!m_lockedByField.getText().equals("*")) {
            lockedByPattern=m_lockedByField.getText();
        }
        String state=null;
        if (!m_stateField.getText().equals("*")) {
            state=m_stateField.getText();
        }
        String labelPattern=null;
        if (!m_labelField.getText().equals("*")) {
            labelPattern=m_labelField.getText();
        }
        String contentModelIdPattern=null;
        if (!m_contentModelField.getText().equals("*")) {
            contentModelIdPattern=m_contentModelField.getText();
        }
        Calendar createDateMin=null;
        Calendar createDateMax=null;
        Calendar lastModDateMin=null;
        Calendar lastModDateMax=null;
        RepositoryBrowser frame=new RepositoryBrowser(pidPattern, foType, 
                lockedByPattern, state, labelPattern, contentModelIdPattern, 
                createDateMin, createDateMax, lastModDateMin, lastModDateMax);
        frame.setVisible(true);
        Administrator.getDesktop().add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

}
