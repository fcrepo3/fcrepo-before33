package fedora.server.config;

import java.awt.*;
import java.io.*;
import javax.swing.*;

public class ConfigApp {

    private File m_configFile;

    public ConfigApp(File configFile) {
        m_configFile = configFile;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Fedora Server Configuration - " + m_configFile.getPath());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel serverPanel = new JPanel();
        serverPanel.add(new JLabel("This is the panel where you configure the server."));
        JPanel modulesPanel = new JPanel();
        modulesPanel.add(new JLabel("This is the panel where you configure modules."));
        JPanel datastoresPanel = new JPanel();
        datastoresPanel.add(new JLabel("This is the panel where you configure datastores."));

        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.addTab("Server", serverPanel);
        tabPanel.addTab("Modules", modulesPanel);
        tabPanel.addTab("Datastores", datastoresPanel);

        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.add(new JButton("Save"));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabPanel, BorderLayout.CENTER);
        mainPanel.add(mainButtonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        // Display the window, centered.
        frame.pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width-frame.getWidth())/2, 
                          (d.height-frame.getHeight())/2);
        frame.setVisible(true);
    }

    public static void showUsage(String errorMessage) {
        if (errorMessage != null) {
            System.err.println("Error: " + errorMessage);
        }
        System.err.println("Usage: fedora-config [path/to/fedora.fcfg]");
        System.err.println("If the path is unspecified, FEDORA_HOME/server/config/fedora.fcfg will be used.");
    }

    public static void main(String[] args) {
        String configFilePath = null;
        if (args.length == 0) {
            String fedoraHome = System.getProperties().getProperty("fedora.home");
            if (fedoraHome == null || fedoraHome.equals("")) {
                showUsage("No path provided, and fedora.home is undefined.");
            } else {
                configFilePath = fedoraHome + File.separator
                               + "server"   + File.separator
                               + "config"   + File.separator
                               + "fedora.fcfg";
            }
        } else if (args.length == 1) {
            configFilePath = args[0];
        } else {
            showUsage("Too many arguments.");
        }
        if (configFilePath != null) {
            try {
                new ConfigApp(new File(configFilePath));
            } catch (Exception e) {
                String message = e.getMessage();
                if (message == null) message = e.getClass().getName();
                showUsage(message);
            }
        }
    }

}