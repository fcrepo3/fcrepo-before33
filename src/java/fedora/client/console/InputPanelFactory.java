package fedora.client.console;

public abstract class InputPanelFactory {

    public static InputPanel getPanel(Class cl) {
        if (cl.getName().equals("java.lang.String")) {
            return new StringInputPanel();
        }
        if (cl.getName().equals("[B")) {
            return new ByteArrayInputPanel(true);
        }
        if (cl.getName().equals("boolean")) {
            return new BooleanInputPanel(true);
        }
        if (cl.getName().equals("java.lang.Boolean")) {
            return new BooleanInputPanel(false);
        }
        System.out.println("Unrecognized type: " + cl.getName());
        return NullInputPanel.getInstance();
    }

}