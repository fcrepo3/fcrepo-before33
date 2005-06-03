package fedora.utilities.policyEditor;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/*
 * Created on May 24, 2005
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
public class NewUserClassDialog extends JDialog implements ActionListener, ListSelectionListener, KeyListener
{
    private CardLayout card = null;
    private JPanel mainPanel = null;
    private JPanel paramPanel = null;
    private JTable compositeTable = null;
    private JTable templateTable = null;
    private JButton createClass = null;
    private JRadioButton andButton = null;
    public final static int PERMIT = 0;
    public final static int DENY = 1;
    public final static int TEMPLATE = 0;
    public final static int COMPOSITE = 1;
    private int permitOrDeny = PERMIT;
    private int templateOrComposite = TEMPLATE;
    /**
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public NewUserClassDialog(Frame owner, String title)
            throws HeadlessException
    {
        super(owner, title, true);

        makeNewUserPanel();
        pack();
        show();
        
    }
        
    private void makeNewUserPanel()
    {
        JPanel topPanel = new JPanel();
        JPanel topPanel1 = new JPanel();
        JPanel topPanel2 = new JPanel();
        mainPanel = new JPanel()
        {
            public Dimension getPreferredSize()
            {
                return (new Dimension(500, 250));
            }
        };
        JPanel botPanel = new JPanel();
        
        ButtonGroup group1 = new ButtonGroup();
        topPanel1.setLayout(new GridLayout(0,1,0,5));
        topPanel1.add(makeRadioButton(group1, "Permit", true));
        topPanel1.add(makeRadioButton(group1, "Deny", false));
        topPanel1.setBorder(new CompoundBorder(new TitledBorder("Rule Type"), new EmptyBorder(10,10,10,10)));
        ButtonGroup group2 = new ButtonGroup();
        topPanel2.setLayout(new GridLayout(0,1,0,5));
        topPanel2.add(makeRadioButton(group2, "Create New User Class from Template", true));
        topPanel2.add(makeRadioButton(group2, "Create Composite User Class by Combining 2 (or more) existing classes", false));
        topPanel2.setBorder(new CompoundBorder(new EmptyBorder(0,10,0,0), new CompoundBorder(new TitledBorder("Type of User Class to Create"), new EmptyBorder(10,10,10,10))));
        topPanel.setBorder(new EmptyBorder(10,10,10,10));
        topPanel.setLayout(new BorderLayout());
        topPanel.add(topPanel1, BorderLayout.WEST);
        topPanel.add(topPanel2, BorderLayout.CENTER);
        
        JPanel fromTemplate = new JPanel();
        JPanel combineExisting = new JPanel();
        GroupRuleTableModel tModel = new GroupRuleTableModel(false, PERMIT);
        compositeTable = new JTable(tModel);
        compositeTable.getSelectionModel().addListSelectionListener(this); 
        
        combineExisting.setLayout(new BorderLayout());
        combineExisting.add(new JScrollPane(compositeTable), BorderLayout.CENTER);
        JPanel andOrPanel = new JPanel();
        ButtonGroup group3 = new ButtonGroup();
        andOrPanel.setBorder(new CompoundBorder(new EmptyBorder(10,10,10,10), new TitledBorder("Combine Classes Using:")));
        andOrPanel.add(andButton = makeRadioButton(group3, "AND", true));
        andOrPanel.add(makeRadioButton(group3, "OR", true));
        
        combineExisting.add(andOrPanel, BorderLayout.SOUTH);
        
        GroupRuleTableModel templateModel = new GroupRuleTableModel(GroupRuleTableModel.TEMPLATES, false, PERMIT);
        templateTable = new JTable(templateModel);
        templateTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        templateTable.getSelectionModel().addListSelectionListener(this); 
        fromTemplate.setLayout(new BorderLayout());
        fromTemplate.add(new JScrollPane(templateTable), BorderLayout.CENTER);
        paramPanel = new JPanel()
        {
            public Dimension getPreferredSize()
            {
                return (new Dimension(400, 150));
            }
        };
        paramPanel.setLayout(new ParagraphLayout());
        paramPanel.setBorder(new CompoundBorder(new EmptyBorder(10,10,10,10), new TitledBorder("Enter Values For Parameters:")));
        fromTemplate.add(paramPanel, BorderLayout.SOUTH);
        
        mainPanel.setLayout(card = new CardLayout());
        mainPanel.add(fromTemplate, "fromTemplate");
        mainPanel.add(combineExisting, "combineExisting");
        
        botPanel.setLayout(new FlowLayout(FlowLayout.CENTER,10, 10));
        botPanel.add(createClass = makeButton("Create User Class", false));
        botPanel.add(makeButton("Cancel", true));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(botPanel, BorderLayout.SOUTH);
        
        updatePanel(permitOrDeny, templateOrComposite);
    }
        
    private JRadioButton makeRadioButton(ButtonGroup group, String label, boolean active)
    {
        JRadioButton button = new JRadioButton(label);
        group.add(button);
        button.addActionListener(this);
        button.setSelected(active);
        return(button);
    }
    
    private JButton makeButton(String label, boolean enabled)
    {
        JButton button = new JButton(label);
        button.addActionListener(this);
        button.setEnabled(enabled);
        return(button);
    }
    
    private void updatePanel(int permitOrDeny, int templateOrComposite)   
    {
        if (templateOrComposite == COMPOSITE)
        {
            ((GroupRuleTableModel)compositeTable.getModel()).setPermitDenyOrBoth(permitOrDeny);
            // now enable/disable the createClass button.
            createClass.setEnabled(compositeTable.getSelectedRowCount() > 1);
        }
        
        if (templateOrComposite == TEMPLATE)
        {
            ((GroupRuleTableModel)templateTable.getModel()).setPermitDenyOrBoth(permitOrDeny);
            boolean enabled = (templateTable.getSelectedRowCount() == 1);
            for (int i = 0; enabled && i < paramPanel.getComponentCount(); i++)
            {
                if (paramPanel.getComponent(i) instanceof JTextField)
                {
                    if (((JTextField)paramPanel.getComponent(i)).getText().length() == 0)
                    {
                        enabled = false;
                    }
                }
            }
            createClass.setEnabled(enabled);
        }
    }
    
    private void createCompositeClass()
    {
        if (templateOrComposite == COMPOSITE)
        {
            int totalNum = compositeTable.getSelectedRowCount();
            int index[] = compositeTable.getSelectedRows();
            GroupRuleTableModel model = ((GroupRuleTableModel)compositeTable.getModel());
            int andOrOr = andButton.isSelected() ? GroupRuleInfo.AND : GroupRuleInfo.OR;
            GroupRuleInfo.buildFromRules(model.isPermit(), index, andOrOr);
        }
    }
    
    private void createTemplateClass()
    {
        if (templateOrComposite == TEMPLATE)
        {
            String parmString = "";
            for (int i = 0; i < paramPanel.getComponentCount(); i++)
            {
                if (paramPanel.getComponent(i) instanceof JTextField)
                {
                    JTextField field = (JTextField)paramPanel.getComponent(i);
                    parmString = parmString + (parmString.length() > 0 ? ";" : "") + 
                                 field.getName() + "=" + field.getText();
                }
            }

            int index = templateTable.getSelectedRow();
            GroupRuleTableModel model = ((GroupRuleTableModel)templateTable.getModel());
            GroupRuleInfo.buildFromTemplate(model.isPermit(), model.getRowNum(index), parmString);
//            GroupRuleTableModel model2 = ((GroupRuleTableModel)compositeTable.getModel());
//            model2.addRow(permitOrDeny, newgroup);
        }
    }

    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof JTextField)
        {
            updatePanel(permitOrDeny, templateOrComposite);            
        }
        else if (e.getActionCommand().equals("Cancel"))
        {
            this.hide();
            this.dispose();
        }
        else if (e.getActionCommand().startsWith("Create New"))
        {
            card.show(mainPanel, "fromTemplate");
            if (templateOrComposite != TEMPLATE)
            {
                templateOrComposite = TEMPLATE;
                updatePanel(permitOrDeny, templateOrComposite);
            }
        }
        else if (e.getActionCommand().startsWith("Create Composite"))
        {
            card.show(mainPanel, "combineExisting");
            if (templateOrComposite != COMPOSITE)
            {
                templateOrComposite = COMPOSITE;
                updatePanel(permitOrDeny, templateOrComposite);
            }
        }
        else if (e.getActionCommand().startsWith("Permit"))
        {
            permitOrDeny = PERMIT;
            updatePanel(permitOrDeny, templateOrComposite);
        }
        else if (e.getActionCommand().startsWith("Deny"))
        {
            permitOrDeny = DENY;
            updatePanel(permitOrDeny, templateOrComposite);
        }
        else if (e.getActionCommand().equals("Create User Class"))
        {
            if (templateOrComposite == COMPOSITE)
            {
                createCompositeClass();
            }
            if (templateOrComposite == TEMPLATE)
            {
                createTemplateClass();
            }
            this.hide();
            this.dispose();

        }
    }


    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        // now enable/disable the createClass button.
        if (templateOrComposite == COMPOSITE)
        {
            createClass.setEnabled(compositeTable.getSelectedRowCount() > 1);
        }
        if (templateOrComposite == TEMPLATE)
        {
            int row = templateTable.getSelectedRow();
            GroupRuleTableModel model = ((GroupRuleTableModel)templateTable.getModel());
            paramPanel.removeAll();
            paramPanel.invalidate();
            if (row != -1)
            {
                GroupRuleInfo entry = model.getRow(row);
                for (int i = 0; i < entry.getNumParms(); i++)
                {
                    JLabel label = new JLabel(entry.getParmValue(i));
                    label.setName(entry.getParmName(i));
                    paramPanel.add(label, ParagraphLayout.NEW_PARAGRAPH);
                    JTextField field = new JTextField(35);
                    field.setName(entry.getParmName(i));
                    field.addActionListener(this);
                    field.addKeyListener(this);
                    paramPanel.add(field);
                } 
            }
            else 
            {
                paramPanel.add(new JLabel("no template selected", SwingConstants.CENTER));
            }
            paramPanel.validate();
            paramPanel.repaint();
        }
    }


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e)
    {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e)
    {
        if (e.getSource() instanceof JTextField)
        {
            updatePanel(permitOrDeny, templateOrComposite);            
        }
    }


}
