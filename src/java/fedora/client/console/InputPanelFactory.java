package fedora.client.console;

public abstract class InputPanelFactory {

    public static InputPanel getPanel(Class cl) {
        if (cl.getName().equals("java.lang.String")) {
            return new StringInputPanel();
        }
        return NullInputPanel.getInstance();
    }

}