package fedora.client.console;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
            Object returned=m_command.invoke(
                    m_console.getInvocationTarget(m_command), parameters);
            m_console.print("Returned: " + stringify(returned) + "\n");
        } catch (InvocationTargetException ite) {
            m_console.print("ERROR (" + ite.getTargetException().getClass().getName() + ") : " 
                    + ite.getTargetException().getMessage() + "\n");
        } catch (Throwable th) {
            m_console.print("ERROR (" + th.getClass().getName() + ") : " 
                    + th.getMessage() + "\n");
        }
    }
    
    private String stringify(Object obj) {
        return obj.toString();
    }
    
}